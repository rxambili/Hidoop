package hdfs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import formats.FormatImpl;
import formats.KV;

public class Recuperer_fragment extends Thread {
	
	public Socket socket;
	public BufferedWriter outFile;
	public FormatImpl format;
	
	public  Recuperer_fragment(Socket s, FormatImpl form) {
		this.socket = s;
		this.format = form;
	}

	public void run() {
		
		/* tache vaut 1 si l'écriture a été faite, 0 sinon */
		int tache = 0;
		
        try{
            while(tache != 1){
            		
            		/* Récupération du contenu de fname */
            		BufferedReader plec = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            		String texte = plec.readLine();
            		KV kv = new KV("1",texte);
            		
            		System.out.println("Le client a reçu " + kv.toString());
            		
            		/* Ecriture du contenu dans le format */
            		this.format.write(kv);           	   
            	   
            		tache = 1; 
            }
	
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}

}
