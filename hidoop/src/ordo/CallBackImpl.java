package ordo;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class CallBackImpl extends UnicastRemoteObject implements CallBack {


    //private String machine;
    boolean fini;

    public CallBackImpl() throws  RemoteException{
       // this.machine = m;
        this.fini = false;
    }

    public void addMachineFinished() throws RemoteException {
        System.out.println("calcul fini  ");
        this.fini = true;
    }

    public boolean estFini() throws RemoteException{
        return this.fini;
    }

}