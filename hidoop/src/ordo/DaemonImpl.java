package ordo;

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
		
	}

}
