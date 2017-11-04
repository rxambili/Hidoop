package ordo;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import map.Mapper;
import formats.Format;

public class DaemonImpl extends UnicastRemoteObject implements Daemon {

	protected DaemonImpl() throws RemoteException {
		super();
	}


	public void runMap(Mapper m, Format reader, Format writer, CallBack cb)
			throws RemoteException {
		reader.open(Format.OpenMode.R);
		writer.open(Format.OpenMode.W);
		m.map(reader, writer);
		reader.close();
		writer.close();
		
	}

	public static void main(String args[]) {
		try {
			Daemon daemon = new DaemonImpl();
			Naming.rebind("//localhost:4000/Daemon" + args[0], daemon);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
			
	}
}
