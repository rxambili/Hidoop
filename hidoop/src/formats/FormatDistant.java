package formats;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ordo.SortComparator;

/**
 * Classe FormatImpl implemente l'interface Format.
 * Permet d'ouvrir, lire, ecrire ou fermer des fichiers dans les differents formats.
 * (Le type de format est donne en attribut, une classe par type est envisagee dans le futur).
 * @author Bonnet, Steux, Xambili
 *
 */
public class FormatDistant implements Format {

	/** Type de format; */
	private Format.Type type;
	/** Index. */
  	private long index;
  	/** Nom du fichier. */
  	private String fname;
  	/** Stockage de node de reduce */
  	//private HashMap<String, String>  nodeReduce;
  	private List<String> nodes;
  	private List<BufferedWriter> bws;
  	private List<Socket> sockets;
  	private SortComparator sc;

	/**
	 * Constructeur de FormatImpl.
	 * @param type type de format
	 * @param index index initial
	 * @param fname nom du fichier
	 */
  	public FormatDistant (Format.Type type, long index, List<String> daemonsString, SortComparator sc) {
    		this.type = type;
    		this.index = index;
    		this.nodes = daemonsString;
    		this.sc = sc;
    		//this.nodeReduce.put("am", "//yoda:4000/Daemon");
		    //this.nodeReduce.put("am", "//vador:4000/Daemon");
  	}

  	public void open(OpenMode mode) {
  		/* Ouverture de la connexion sur un socket */
  		for (String node : nodes) {
  			try {
				Socket s = new Socket(InetAddress.getByName(node), 4444);
				sockets.add(s);
				bws.add(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  		}
  	}
	
	public KV read() {
		return null;
	}
	
	/**
	 * Ecrit l'enregistrement du format correspondant e la key-value dans le fichier.
	 */
	public void write(KV record) {
		try {
			BufferedWriter bw = this.shuffle(record);
			if (this.type == Format.Type.KV) {
				bw.write(record.k + KV.SEPARATOR + record.v, 0, record.k.length() + KV.SEPARATOR.length() + record.v.length()); // cle<->valeur
				bw.newLine();
			} else if (this.type == Format.Type.LINE) {
				bw.write(record.v);
			}
			this.index++; // incrementation de index (nombre de lignes dans le cas de l'ecriture)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedWriter shuffle(KV record) {
		if (sc.compare(record.v, "m") < 0 ) {
			return bws.get(0);
		} else {
			return bws.get(1);
		}
	}
	
	public void close() {
  		try {
  			for (BufferedWriter bw : bws) {
  				if (bw != null) {
  					bw.close();
  				}
  			}
  			for (Socket s : sockets) {
  				if (s != null) {
  					s.close();
  				}
  			}

  		} catch (IOException e) {
			e.printStackTrace();
		}
  	}
  
	public long getIndex() {
    		return this.index;
  	}
  
	public String getFname() {
   		return this.fname;
 	}
  
  	public void setFname(String fname) {
    		this.fname = fname;
  	}
}