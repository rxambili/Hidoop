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
		// TODO Auto-generated constructor stub
	}


	public void runMap(Mapper m, Format reader, Format writer, CallBack cb)
			throws RemoteException {
		// TODO Auto-generated method stub

        m.map(reader, writer);
		
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
