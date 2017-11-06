/* une PROPOSITION, SAUF startJob(), setInputFormat(Format.Type ft) et setInputFname(String fname),  qui sont EXIGÃ‰ES.
 * tout le reste peut Ãªtre complÃ©tÃ© ou adaptÃ©
 */

package ordo;

import map.MapReduce;
import formats.Format;

/**
 * Interface JobInterface permet de lancer les map/reduce sur les machines distantes
 * (actuellement seulement en local sur la même machine).
 * @author Bonnet, Steux, Xambili
 *
 */
public interface JobInterface {
	/**
	 * Modifie le nombre de reduces.
	 * @param tasks nouveau nombre de reduces
	 */
    public void setNumberOfReduces(int tasks);
    
    /**
	 * Modifie le nombre de maps.
	 * @param tasks nouveau nombre de maps
	 */
    public void setNumberOfMaps(int tasks);
    
    /**
     * Modifie le format d'entrée.
     * @param ft nouveau format
     */
    public void setInputFormat(Format.Type ft);
    
    /**
     * Modifie le format de sortie.
     * @param ft nouveau format
     */
    public void setOutputFormat(Format.Type ft);
    
    /**
     * Modifie le nom du fichier source
     * @param fname nom du fichier source
     */
    public void setInputFname(String fname);
//    public void setOutputFname(String fname);
//    public void setSortComparator(SortComparator sc);
    
    /**
     * Retourne le nombre de reduces.
     * @return nombre de reduces
     */
    public int getNumberOfReduces();
    
    /**
     * Retourne le nombre de maps.
     * @return nombre de maps
     */
    public int getNumberOfMaps();
    
    /**
     * Retourne le format d'entrée.
     * @return format d'entrée
     */
    public Format.Type getInputFormat();
    
    /**
     * Retourne le format de sortie.
     * @return format de sortie
     */
    public Format.Type getOutputFormat();
    
    /**
     * Retourne le nom du fichier source.
     * @return nom du fichier source
     */
    public String getInputFname();
    
    /**
     * Retourne le nom du fichier résultat
     * @return nom du fichier résultat
     */
    public String getOutputFname();
//    public SortComparator getSortComparator();
    
    /**
     * Lance le map/reduce.
     * @param mr Map/Reduce de l'application
     */
    public void startJob (MapReduce mr);
}