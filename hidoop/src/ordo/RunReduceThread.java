package ordo;

import java.rmi.RemoteException;

import map.MapReduce;
import formats.Format;

public class RunReduceThread extends Thread {
	
	private Daemon daemon;
	private MapReduce mr;
	private Format readerCourant;
	private Format writerCourant;
	private CallBack cb;

	public RunReduceThread(Daemon d, MapReduce mapreduce, Format reader, Format writer, CallBack callback) {
		this.daemon = d;
		this.mr = mapreduce;
		this.readerCourant = reader;
		this.writerCourant = writer;
		this.cb = callback;
	}

	public void run() {
		try {
			daemon.runReduce(mr, readerCourant, writerCourant, cb);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
