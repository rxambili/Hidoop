package formats;

import java.io.Serializable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;

/**
 * Classe FormatImpl implemente l'interface Format.
 * Permet d'ouvrir, lire, ecrire ou fermer des fichiers dans les differents formats.
 * (Le type de format est donne en attribut, une classe par type est envisagee dans le futur).
 * @author Bonnet, Steux, Xambili
 *
 */
public class FormatLocal implements Format {

	/** Type de format; */
	private Format.Type type;
	/** Index. */
  	private long index;
  	/** Nom du fichier. */
  	private String fname;
	
	private File fichier;      // fichier de nom fname
	private BufferedReader br; // pour la lecture de fichier
	private BufferedWriter bw; // pour l'ecriture de fichier

	/**
	 * Constructeur de FormatImpl.
	 * @param type type de format
	 * @param index index initial
	 * @param fname nom du fichier
	 */
  	public FormatLocal (Format.Type type, long index, String fname) {
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
			try {
				this.br = new BufferedReader(new FileReader(fichier));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			// mode ecriture
			try {
				this.fichier.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.fichier.setReadable(true);
			this.fichier.setWritable(true);
			try {
				this.bw = new BufferedWriter(new FileWriter(fichier));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
  	}
	
	public KV read() {
		KV kv = null;
		try {
			if (this.type == Format.Type.LINE) {
				// format ligne
				String ligne = br.readLine();
				if (ligne != null) {
					kv = new KV(String.valueOf(index), ligne);
				}
			} else {
				// format key-value
				String ligne = br.readLine();
				if (ligne != null) {
					String[] parties = ligne.split(KV.SEPARATOR); // separation de la ligne en cle + valeur
					kv = new KV(parties[0], parties[1]); // parties[0] : cle, parties[1] : valeur
				}
			}
				this.index++; // incrementation de index (ligne courante dans le cas de la lecture)
		} catch (IOException e) {
			e.printStackTrace();
		}
		return kv;
	}
	
	/**
	 * Ecrit l'enregistrement du format correspondant e la key-value dans le fichier.
	 */
	public void write(KV record) {
		try {
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
	
	public void close() {
  		try {
  			if (br != null){
				br.close();
			}
			if (bw != null){
				bw.close();
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