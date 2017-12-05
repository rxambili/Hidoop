package formats;

/**
 * Interface FormatWriter.
 * Permet d'écrire différents formats.
 *
 */
public interface FormatWriter {
	/**
	 * Ecrit l'enregistrement du format correspondant à la key-value dans le fichier.
	 * @param record key-value à écrire
	 */
	public void write(KV record);
}
