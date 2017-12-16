package formats;

/**
 * Interface FormatWriter.
 * Permet d'ecrire differents formats.
 *
 */
public interface FormatWriter {
	/**
	 * Ecrit l'enregistrement du format correspondant e la key-value dans le fichier.
	 * @param record key-value e ecrire
	 */
	public void write(KV record);
}
