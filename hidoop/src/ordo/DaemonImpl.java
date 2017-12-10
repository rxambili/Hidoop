package ordo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import map.MapReduce;
import map.Mapper;
import map.Reducer;
import formats.Format;
import formats.FormatLocal;
import formats.KV;

/**
 * Classe DaemonImpl implemente l'interface Daemon.
 * Cet classe doit etre lancee sur chaque machine distante.
 * @author Bonnet, Steux, Xambili
 *
 */
public class DaemonImpl extends UnicastRemoteObject implements Daemon {
	
	private FormatLocal distantFormat;
	private int nbClients;
	
	/**
	 * Constructeur de DaemonImpl.
	 * @throws RemoteException
	 */
	protected DaemonImpl() throws RemoteException {
		super();
		nbClients = 0;
	}

	/**
	 * Ouvre/cree les fichiers puis execute le map localement avant de fermer les fichiers.
	 */
	public void runMap(Mapper m, Format reader, Format writer, CallBack cb)
			throws RemoteException {

		reader.open(Format.OpenMode.R);
		writer.open(Format.OpenMode.W);
		m.map(reader, writer);
		reader.close();
		writer.close();

		// dire au client que c'est fini
		cb.notifyMapFinished();
	}

	public void runReduce(Reducer r, Format reader, Format writer, CallBack cb) throws RemoteException {
		reader.open(Format.OpenMode.R);
		writer.open(Format.OpenMode.W);
		r.reduce(reader, writer);
		reader.close();
		writer.close();
		cb.notifyReduceFinished();
	}
	
	public void distantOpen(Format.OpenMode mode) throws RemoteException {
		this.distantFormat.open(mode);
		this.nbClients++;
		
	}
	
	public void distantWrite(KV record) throws RemoteException {
		this.distantFormat.write(record);		
	}
	
	public KV distantRead() throws RemoteException {
		return this.distantFormat.read();
	}
	
	public void distantClose() throws RemoteException {
		if (nbClients == 1) {
			this.distantFormat.close();
		}
		this.nbClients--;
	}
	
	public void initDistantFormat(FormatLocal f) throws RemoteException {
		this.distantFormat = f;		
	}


	/**
	 * Methode principale de la classe DaemonImpl.
	 * Elle cree le RMI registry sur la machine s'il n'existe pas deje 
	 * puis enregistre une instance de DaemonImpl au registry.
	 * @param args args[0] correspond au numero du Daemon.
	 */
	public static void main(String args[]) {
		try {
			Registry registry = LocateRegistry.createRegistry(4000);
			System.out.println("Creation du registry sur le port 4000");
		} catch (RemoteException e1) {
			System.out.println("registry deja existant");
		}
		try {
			
			Daemon daemon = new DaemonImpl();
			//ReduceInputWriterThread t = new ReduceInputWriterThread(4444, "Daemon" + args[0] );
			//t.start();
			Naming.rebind("//localhost:4000/Daemon" + args[0], daemon);
			boolean isClosed = false;
			Scanner sc = new Scanner(System.in);
			while (!isClosed) {				
				System.out.println("Saisir Commande :");
				String str = sc.nextLine();
				if (str.equals("exit")) {
					//t.fermer();
					isClosed = true;
					sc.close();
					System.exit(0);
				} else {
					usage();
				}
			}
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
			
	}

	private static void usage() {
		System.out.println("usage : exit | ...");		
	}
}

