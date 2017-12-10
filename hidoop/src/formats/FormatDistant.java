package formats;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.HashMap;
import java.util.ArrayList;

import ordo.Daemon;
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
  	private ArrayList<Daemon> nodes;
  	private SortComparator sc;

	/**
	 * Constructeur de FormatImpl.
	 * @param type type de format
	 * @param index index initial
	 * @param fname nom du fichier
	 */
  	public FormatDistant (Format.Type type, long index, String fname, ArrayList<Daemon> daemons, SortComparator sc) {
    		this.type = type;
    		this.index = index;
    		this.nodes = daemons;
    		for (int i=0; i<nodes.size(); i++) {
    			FormatLocal f = new FormatLocal(type,index,fname+(i+1));
    			try {
    				nodes.get(i).initDistantFormat(f);
    			} catch (RemoteException e) {
    				e.printStackTrace();
    			}
    		}
    		this.sc = sc;
  	}

  	public void open(OpenMode mode) {
  		for (Daemon node : nodes) {
			try {
				node.distantOpen(mode);
			} catch (RemoteException e) {
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
		Daemon node = this.shuffle(record);
		try {
			node.distantWrite(record);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public Daemon shuffle(KV record) {
		if (sc.compare(record.k, "m") < 0 ) {
			return nodes.get(0);
		} else {
			return nodes.get(1);
		}
	}
	
	public void close() {
		for (Daemon node : nodes) {
			try {
				node.distantClose();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
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