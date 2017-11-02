package ordo;

import map.MapReduce;
import formats.Format;

public class Job implements JobInterface {

	private int numberOfReduces;
	private int numberOfMaps;
	private Format.Type inputFormat;
	private Format.Type outputFormat;
	private String inputFname;
	private String outputFname;
	private SortComparator sortComparator;
	
	
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

	
	public void startJob(MapReduce mr) {
		// TODO Auto-generated method stub

	}

}
