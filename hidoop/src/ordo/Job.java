package ordo;

import map.MapReduce;
import java.rmi.Naming;

import formats.Format;
import formats.FormatImpl;
import hdfs.HdfsClient;

public class Job implements JobInterface {

	private int numberOfReduces;
	private int numberOfMaps;
	private Format.Type inputFormat;
	private Format.Type outputFormat;
	private String inputFname;
	private String outputFname;
	//private SortComparator sortComparator;
	
	public Job() {
		this.numberOfReduces = 1;
		this.numberOfMaps = 4;
		this.inputFormat = Format.Type.KV;
		this.outputFormat = Format.Type.KV;
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
		this.outputFname = fname + "-res";
	}

	
	/*public void setOutputFname(String fname) {
		this.outputFname = fname;
	}*/

	
	/*public void setSortComparator(SortComparator sc) {
		this.sortComparator = sc;
	}*/

	
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

	
	/*public SortComparator getSortComparator() {
		return this.sortComparator;
	}*/
	
	public void startJob(MapReduce mr) {
		//HdfsClient.HdfsWrite(inputFormat, inputFname, numberOfMaps);
		for (int i=1 ; i<=numberOfMaps; i++) {
			try {
				Format readerCourant = new FormatImpl(inputFormat, 0, inputFname + "_part" + i);
				Format writerCourant = new FormatImpl(outputFormat,  0, outputFname + "-tmp_part" + i);
				CallBack cb = new CallBack();
				// récupération de l'objet
				Daemon daemon = (Daemon) Naming.lookup("//localhost:4000/Daemon"+i);
				// appel de RunMap
				daemon.runMap(mr, readerCourant, writerCourant, cb);
			} catch (Exception ex) {
					ex.printStackTrace();
			}
		}
		//quand execution des map sur les machines distantes finies (callbacks)
		HdfsClient.HdfsRead(outputFname + "-tmp", outputFname + "-tmp");
		Format readerRes = new FormatImpl(outputFormat, 0, outputFname + "-tmp");
		Format writerRes = new FormatImpl(outputFormat, 0, outputFname);
		mr.reduce(readerRes, writerRes);
	}

}
