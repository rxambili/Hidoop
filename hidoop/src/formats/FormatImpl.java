package formats;

import java.io.Serializable;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class FormatImpl implements Format {

	private Format.Type type;
  	private long index;
  	private String fname;
	
	private File fichier;      // fichier de nom fname
	private BufferedReader br; // pour la lecture de fichier
	private BufferedWriter bw; // pour l'ecriture de fichier

  	public FormatImpl (Format.Type type, long index, String fname) {
    		this.type = type;
    		this.index = index;
    		this.fname = fname;
  	}

 	public void open(OpenMode mode) {
		this.fichier = new File(fname);
		this.fichier.setExecutable(false);
		if (mode == Format.OpenMode.R) {
			// mode lecture
			this.fichier.setReadable(true);
			this.fichier.setWritable(false);
		} else {
			// mode ecriture
			this.fichier.setReadable(false);
			this.fichier.setWritable(true);
		}
		this.br = new BufferedReader(new FileReader(fichier));
		this.bw = new BufferedWriter(new FileWriter(fichier));
  	}
	
	public KV read() {
		KV kv = new KV(br.readLine(), index.toString());
		this.index++; // incrementation de index (ligne courante dans le cas de la lecture)
		return kv;
	}
	
	public void write(KV record) {
		bw.newLine();
		bw.write(record.k, 0, record.k.length());
		this.index++; // incrementation de index (nombre de lignes dans le cas de l'ecriture)
	}
	
	public void close() {
  		br.close();
		bw.close();
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
