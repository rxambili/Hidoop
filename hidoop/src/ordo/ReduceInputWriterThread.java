package ordo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

//import java.rmi.RemoteException;

public class ReduceInputWriterThread extends Thread {
	
	int port;
	boolean finir;
	
	public ReduceInputWriterThread(int p) {
		this.port = p;
		this.finir = false;
	}
	
	public void run() {
		try {
			
			FileInputStream fis = null;
			BufferedWriter bw = null;
			BufferedReader br = null;
			try {
				File fichier = new File("input_KV.txt");
				fichier.createNewFile();
				fichier.setReadable(true);
				fichier.setWritable(true);
		    	bw = new BufferedWriter(new FileWriter(fichier));
		    	} catch (IOException e) {
					e.printStackTrace();
		    	}

			try {

				/* Ouverture d'un socket d'écoute sur le pour port */
				ServerSocket s = new ServerSocket(this.port);

				/* Compteur du nombre de connexions effectuée */
				int compteurCo = 1;

				/* Tant qu'il y a des connexions, on les traite */
				while (true) {
					/* On accepte la connexion dans condition */
					Socket socket = s.accept();

					/* On lit le message qui arrive */
				    br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					


					/* ecrire texte dans fichier */
					if (br != null && bw!= null) {
	    				try {
	    					String texte = br.readLine();
	    					bw.write(texte, 0, texte.length());
	    					bw.newLine();
	    					br.close();
	    				} catch (IOException e) {
	    					e.printStackTrace();
	    				}
	    			}
					
					// sortie
					if (this.finir) {
						break;
					}
					
				}

				/* Fermeture des canaux réseau */
				// ... .close();
				s.close();

			} catch (Exception e) {
				System.out.println(e);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
