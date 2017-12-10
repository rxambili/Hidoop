package ordo;

import map.MapReduce;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;

import formats.Format;
import formats.FormatDistant;
import formats.FormatLocal;
import hdfs.HdfsClient;

import java.util.HashMap;
import java.util.ArrayList;

/**
 * Classe Job implemente JobInterface.
 * Permet de lancer les map/reduce sur les machines distantes
 * (actuellement seulement en local sur la meme machine et un seul reduce et 4 maps).
 * @author Bonnet, Steux, Xambili
 *
 */
public class Job implements JobInterface {


	//private static final String ArrayListeMachine[] = {"yoda.enseeiht.fr", "vador.enseeiht.fr", "aragorn.enseeiht.fr", "gandalf.enseeiht.fr"};
	private ArrayList<String> daemonsString;
	private ArrayList<Daemon> daemons;
	private final static String configDaemons = "../config/daemons.txt";

	/** Nombre de reduces. */
	private int numberOfReduces;
	/** Nombre de maps. */
	private int numberOfMaps;
	/** Format d'entree. */
	private Format.Type inputFormat;
	/** Format de sortie/ */
	private Format.Type outputFormat;
	/** Nom du fichier source. */
	private String inputFname;
	/** Nom du fichier resultat. */
	private String outputFname;
	/** Comparateur pour trier. */
	private SortComparator sortComparator;

	private HashMap<Integer, CallBack> ArrayListeCallBack;
	
	/**
	 * Constructeur de Job.
	 */
	public Job() {
		this.numberOfReduces = 2;
		this.inputFormat = Format.Type.KV;
		this.outputFormat = Format.Type.KV;
		this.ArrayListeCallBack = new HashMap<Integer, CallBack>();
		this.sortComparator = new SortComparatorImpl();
		try {
			initDaemons();
			this.numberOfMaps = daemons.size();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
		

	}
	
	
	public void setNumberOfReduces(int tasks) {
		this.numberOfReduces = tasks;
	}

	
	public void setNumberOfMaps(int tasks) {
		this.numberOfMaps = tasks;
	}

	
	public void setInputFormat(Format.Type ft) {
		this.inputFormat = ft;
	}

	
	public void setOutputFormat(Format.Type ft) {
		this.outputFormat = ft;
	}
	
	
	public void setInputFname(String fname) {
		this.inputFname = fname;
		this.outputFname = fname + Format.SUFFIXE_res;
	}

	
	public void setOutputFname(String fname) {
		this.outputFname = fname;
	}

	
	public void setSortComparator(SortComparator sc) {
		this.sortComparator = sc;
	}

	
	public int getNumberOfReduces() {
		return this.numberOfReduces;
	}

	
	public int getNumberOfMaps() {
		return this.numberOfMaps;
	}

	
	public Format.Type getInputFormat() {
		return this.inputFormat;
	}

	
	public Format.Type getOutputFormat() {
		return this.outputFormat;
	}
	
	
	public String getInputFname() {
		return this.inputFname;
	}

	
	public String getOutputFname() {
		return this.outputFname;
	}

	
	public SortComparator getSortComparator() {
		return this.sortComparator;
	}
	
	/**
	 * Permet de lancer les maps sur les machines reparties
	 * (seulement en locale pour le moment) ainsi que d'executer le reduce.
	 * Le fichier source doit prealablement etre decouper en 4 parties.
	 */
	public void startJob(MapReduce mr) {
		//HdfsClient.HdfsWrite(inputFormat, inputFname, numberOfMaps);
		ArrayList<String> reduceNodes = new ArrayList<String>();
		reduceNodes.add(daemonsString.get(0));
		reduceNodes.add(daemonsString.get(1));
		RunMapThread tm[] = new RunMapThread[numberOfMaps];
		for (int i=0 ; i<numberOfMaps; i++) {
			try {
				Format readerCourant = new FormatLocal(inputFormat, 0, inputFname + Format.SUFFIXE_part + (i+1));
				Format writerCourant = new FormatDistant(outputFormat,  0, reduceNodes, sortComparator);

				CallBack cb = new CallBackImpl(i);
				ArrayListeCallBack.put(i, cb);


				//recuperation de l'objet
				//Daemon daemon = (Daemon) Naming.lookup("//localhost:4000/Daemon"+i);
				//Daemon daemon = (Daemon) Naming.lookup("//" + daemons.get(i-1) + ":4000/Daemon"+i);

				// appel de RunMap
				tm[i] = new RunMapThread(daemons.get(i), mr, readerCourant, writerCourant, cb);
				tm[i].start();

			} catch (Exception ex) {
					ex.printStackTrace();
			}
		}


		for (int i=0 ; i<numberOfMaps; i++) {
			try {
				tm[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		RunReduceThread tr[] = new RunReduceThread[numberOfReduces];
		for (int i=0 ; i<numberOfReduces; i++) {
			try {
				Format readerCourant = new FormatLocal(inputFormat, 0, "../data/Daemon" + (i+1) + "_input_KV.txt");
				Format writerCourant = new FormatLocal(outputFormat,  0, outputFname + Format.SUFFIXE_tmp + Format.SUFFIXE_part + (i+1));

				CallBack cb = new CallBackImpl(i);
				ArrayListeCallBack.put(i, cb);


				//recuperation de l'objet
				//Daemon daemon = (Daemon) Naming.lookup("//localhost:4000/Daemon"+i);
				//Daemon daemon = (Daemon) Naming.lookup("//" + daemons.get(i-1) + ":4000/Daemon"+i);

				// appel de RunMap
				tr[i] = new RunReduceThread(daemons.get(i), mr, readerCourant, writerCourant, cb);
				tr[i].start();

			} catch (Exception ex) {
					ex.printStackTrace();
			}
		}
		
		HdfsClient.HdfsRead(outputFname + Format.SUFFIXE_tmp, outputFname, this.numberOfReduces);
		
		/*Format readerRes = new FormatImpl(outputFormat, 0, outputFname + Format.SUFFIXE_tmp);
		Format writerRes = new FormatImpl(outputFormat, 0, outputFname);
		readerRes.open(Format.OpenMode.R);
		writerRes.open(Format.OpenMode.W);
		mr.reduce(readerRes, writerRes);
		readerRes.close();
		writerRes.close();*/
	}
	
	private void initDaemons() throws IOException, NotBoundException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(configDaemons)));
		this.daemonsString = new ArrayList<String>();
		this.daemons = new ArrayList<Daemon>();
	    String line;
	    int i = 1;
	    while ((line = reader.readLine()) != null) {
	    	  daemonsString.add(line);
	    	  Daemon daemon = (Daemon) Naming.lookup("//" + line + ":4000/Daemon"+i);
	    	  daemons.add(daemon);
	    	  i++;
	    }
	    reader.close();
	}
}
