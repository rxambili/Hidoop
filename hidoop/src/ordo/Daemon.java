package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;

import map.Mapper;
import map.Reducer;
import formats.Format;
import formats.FormatLocal;
import formats.KV;

/**
 * Interface Daemon permettant l'execution repartie des maps.
 * Un Daemon doit etre lance sur chaque machine distante (noeud).
 * @author Bonnet, Steux, Xambili
 *
 */
public interface Daemon extends Remote {
	/**
	 * Execute le map fourni par l'application.
	 * @param m le map fourni par l'application
	 * @param reader fragment locale du fichier source
	 * @param writer fragment locale du resultat
	 * @param cb callback
	 * @throws RemoteException
	 */
	public void runMap (Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException;
	
	/**
	 * Execute le reduce fourni par l'application.
	 * @param m le reduce fourni par l'application
	 * @param reader fragment locale du fichier source
	 * @param writer fragment locale du resultat
	 * @param cb callback
	 * @throws RemoteException
	 */
	public void runReduce(Reducer r, Format reader, Format writer, CallBack cb) throws RemoteException;
	
	public void distantOpen(Format.OpenMode mode) throws RemoteException;
	
	public void distantWrite(KV record) throws RemoteException;
	
	public KV distantRead() throws RemoteException;
	
	public void distantClose() throws RemoteException;
	
	public void initDistantFormat(FormatLocal f) throws RemoteException;
}
