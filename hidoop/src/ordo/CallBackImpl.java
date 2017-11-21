package ordo;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class CallBackImpl implements CallBack {


    //private String machine;
    boolean fini;

    public void CallBackImpl() {
       // this.machine = m;
        this.fini = false;
    }

    public void addMachineFinished() throws RemoteException {
        System.out.println("calcul fini  ");
        this.fini = true;
    }

    public boolean estFini() {
        return this.fini;
    }

}