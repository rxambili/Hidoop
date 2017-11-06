package ordo;

import java.rmi.Remote;
import java.rmi.RemoteException;

import map.Mapper;
import formats.Format;

/**
 * Interface Daemon permettant l'�x�cution r�partie des maps.
 * Un Daemon doit �tre lanc� sur chaque machine distante (noeud).
 * @author Bonnet, Steux, Xambili
 *
 */
public interface Daemon extends Remote {
	/**
	 * Ex�cute le map fourni par l'application.
	 * @param m le map fourni par l'application
	 * @param reader fragment locale du fichier source
	 * @param writer fragment locale du r�sultat
	 * @param cb callback
	 * @throws RemoteException
	 */
	public void runMap (Mapper m, Format reader, Format writer, CallBack cb) throws RemoteException;
}
