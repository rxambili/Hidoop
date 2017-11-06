package formats;

/**
 * Interface FormatReader.
 * Permet de lire différents formats.
 *
 */
public interface FormatReader {
	/**
	 * Lit un enregistrement du format dans le fichier.
	 * @return key-value correspondante
	 */
	public KV read();
}
