package formats;

import java.io.Serializable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
		try {
			this.br = new BufferedReader(new FileReader(fichier));
			this.bw = new BufferedWriter(new FileWriter(fichier));
		} catch (IOException e) {
			e.printStackTrace();
		}
  	}
	
	public KV read() {
		KV kv = null;
		try {
			if (this.type == Format.Type.LINE) {
				// format ligne
				kv = new KV(String.valueOf(index), br.readLine());
			} else {
				// format key-value
				String ligne = br.readLine();
				String[] parties = ligne.split(kv.SEPARATOR); // separation de la ligne en cle + valeur
				kv = new KV(parties[0], parties[1]); // parties[0] : cle, parties[1] : valeur
			}
				this.index++; // incrementation de index (ligne courante dans le cas de la lecture)
		} catch (IOException e) {
			e.printStackTrace();
		}
		return kv;
	}
	
	public void write(KV record) {
		try {
			bw.newLine();
			bw.write(record.k + record.SEPARATOR + record.v, 0, record.k.length() + record.SEPARATOR.length() + record.v.length()); // cle<->valeur
			this.index++; // incrementation de index (nombre de lignes dans le cas de l'ecriture)
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
  		try {
			br.close();
			bw.close();
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
