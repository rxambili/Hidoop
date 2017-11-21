package formats;

import java.io.Serializable;
/**
 * Interface Format.
 * Permet d'ouvrir, lire, ecrire ou fermer des fichiers dans les differents formats.
 *
 */
public interface Format extends FormatReader, FormatWriter, Serializable {
	/** Les differents types de format.	 */
    public enum Type { LINE, KV };
    
    /** Les modes d'ouverture de fichier. */
    public enum OpenMode { R, W };

    /**
     * Ouvre le fichier. Le cree s'il n'existe pas.
     * @param mode mode d'ouverture
     */
	public void open(OpenMode mode);
	
	/**
	 * Ferme le fichier.
	 */
	public void close();
	
	/**
	 * Retourne l'index.
	 * @return index
	 */
	public long getIndex();
	
	/**
	 * Retourne le nom du fichier.
	 * @return nom du fichier
	 */
	public String getFname();
	
	/**
	 * Modifie le nom du fichier.
	 * @param fname nom du fichier
	 */
	public void setFname(String fname);

}
