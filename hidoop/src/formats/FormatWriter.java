package formats;

/**
 * Interface FormatWriter.
 * Permet d'�crire diff�rents formats.
 *
 */
public interface FormatWriter {
	/**
	 * Ecrit l'enregistrement du format correspondant � la key-value dans le fichier.
	 * @param record key-value � �crire
	 */
	public void write(KV record);
}
