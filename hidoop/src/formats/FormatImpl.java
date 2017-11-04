package formats;

public class FormatImpl implements Format {

  private Format.Type type;
  private Format.OpenMode openMode;
  private long index;
  private String fname;

  public FormatImpl (Format.Type type, Format.OpenMode openMode, long index, String fname) {
    this.type = type;
    this.openMode = openMode;
    this.index = index;
    this.fname = fname;
  }

  public void open(OpenMode mode) {
  
  }
  
	public void close() {
  
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
