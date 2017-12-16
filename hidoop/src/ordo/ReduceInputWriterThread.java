package ordo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
//import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
//import java.util.ArrayList;

//import java.rmi.RemoteException;

public class ReduceInputWriterThread extends Thread {
	
	private int port;
	private boolean finir;
	private String nom;
	private BufferedWriter bw;
	
	public ReduceInputWriterThread(int p, String n) {
		this.port = p;
		this.finir = false;
		this.nom = n;
		this.bw = null;
	}
	
	public void run() {

		BufferedReader br = null;
		this.init();
							
		try {

			/* Ouverture d'un socket d'écoute sur le pour port */
			ServerSocket s = new ServerSocket(this.port);
			Socket socket = null;
			/* Tant qu'il y a des connexions, on les traite */
			while (!finir) {
				/* On accepte la connexion dans condition */
				socket = s.accept();
				System.out.println("connection accepte");
				/* On lit le message qui arrive */
			   	br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			    
			   	/* ecrire texte dans fichier */
				if (br != null && bw != null) {
    				try {
    					String texte = br.readLine();
    					if (texte != null) { 
    						System.out.println(texte);
    						bw.write(texte, 0, texte.length());
    						bw.newLine();
						bw.flush();
    					} else {
    						System.out.println("contenu vide");
    					}
    					br.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    				}
			}

			/* Fermeture des canaux réseau */

			bw.close();
			if (socket != null) {
				socket.close();
			}
			s.close();				

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fermer() {
		finir = true;
	}

	public void init() {
		try {
			if (this.bw != null) {
				this.bw.close();
			}

			File fichier = new File("../data/" + nom + "_input_KV.txt");
			fichier.createNewFile();
			fichier.setReadable(true);
			fichier.setWritable(true);
	    		bw = new BufferedWriter(new FileWriter(fichier));
	   	} catch (IOException e) {
			e.printStackTrace();
	   	}

	}


}
