package hdfs;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import formats.Format;

class Envoyer_fragment extends Thread{
		     
		    private Socket socket;
		    private String extrait;
		    private String fileName;
		    private String commande;
		    private int type;

		 
		    public Envoyer_fragment(Socket s, String fragment, String fileName, String commande, String tp) {
				this.socket = s;
				this.fileName = fileName;
				this.commande = commande;
				this.type = Integer.parseInt(tp);
				this.extrait = this.commande + "-" + this.fileName + "-" + fragment + "-" + this.type;
			}

			public void run(){
		        try{
		        	int tache = 0;
		        	
		            while(tache != 1){
		                 
		            	PrintWriter pred = new PrintWriter (new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		                pred.println(this.extrait);               
		               		                
		                tache = 1;
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		    }
		}
		