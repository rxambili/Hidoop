package hdfs;

import java.io.*;
import java.net.*;

import formats.Format;
import formats.FormatLocal;

public class HdfsServeur {

	/** On se connectera par defaut sur le port 8080 de la machine */
	static final int port = 8080;

	/**
	 * Programme main qui récupère le fragments depuis le hdfsClient distant et
	 * les enregistre sur la machine local sur lequel il est lancé
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		/*
		 * Fichier de sortie "fragment*" sur la machine pour enregistrer le
		 * fragment arrivant
		 */
		FileInputStream fis = null;

		try {

			/* Ouverture d'un socket d'écoute sur le pour port */
			ServerSocket s = new ServerSocket(port);

			/* Compteur du nombre de connexions effectuée */
			int compteurCo = 1;

			/* Tant qu'il y a des connexions, on les traite */
			while (true) {
				/* On accepte la connexion dans condition */
				Socket socket = s.accept();

				/* On lit le message qui arrive */
				BufferedReader plec = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String texte = plec.readLine();

				System.out.println(" On a lu " + texte);

				/* On récupère le contenu à enregistrer dans le fichier créé */
				String[] contenu = texte.split("-");

				System.out.println("L'ordre reçu est " + contenu[0]);

				if (contenu[0].equals("w")) {
					/*
					 * Création d'un fichier de type format dans lequel écrire
					 * ce qui est reçu
					 */
					File f = new File(contenu[1]);
					BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
					// PrintWriter pred = new PrintWriter (new
					// BufferedWriter(new OutputStreamWriter(fo)));

					/* Ecriture du fragment dans le fichier de sortie créé */
					bw.write(contenu[2]);
					bw.flush();
					bw.close();

				}

				if (contenu[0].equals("r")) {

					/* Récupération des données dans le fichier fname */
					Reader reader = new InputStreamReader(new FileInputStream(new File(contenu[1])), "ascii");
					String contenuFichier = null;
					try {
						StringBuilder builder = new StringBuilder();
						char[] buffer = new char[512];
						int nbRead = reader.read(buffer);
						while (nbRead > 0) {
							builder.append(buffer, 0, nbRead);
							nbRead = reader.read(buffer);
						}
						contenuFichier = builder.toString();

						System.out.println("On a récupéré " + contenuFichier);

					} finally {
						reader.close();
					}

					/*
					 * Envoie des données du fichier fname sur le socket du
					 * client
					 */
					PrintWriter pred = new PrintWriter(
							new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
					pred.println(contenuFichier);

				}

				if (contenu[0].equals("d")) {
					/* On supprime le fichier sur la machine */
					File f = new File(contenu[1]);
					if (f.exists()) {
						f.delete();
						System.out.println("Le fichier " + contenu[1] + " a bien été supprimé");
					} else {
						System.out.println("Vous tentez de supprimer un fichier qui n'existe pas.");
					}
				}
			}

			/* Fermeture des canaux réseau */
			// ... .close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
