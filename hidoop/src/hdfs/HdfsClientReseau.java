package hdfs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ResourceBundle;

import formats.Format;
import formats.FormatLocal;
import formats.KV;

public class HdfsClientReseau {

	/** Fichier de configuration */
	// static ResourceBundle bundle =
	// ResourceBundle.getBundle("domaine.properties.config");

	/********************** ATTRIBUTS ***********************/

	/** Tableau d'hôtes (PC sur lesquels on va enregistrer) */
	// private static String[] host = bundle.getStringArray("hosts");

	/** Tableau de ports (pour effectuer les connexions à ces hôtes) */
	// on se branche toujours sur le port 8080
	// private static int[] port = {8080,8080};

	/**
	 * Tableau des noms des fichiers qui seront enregistrés sur le cluster par
	 * HdfsWrite
	 **/
	/*private static String[] fileNames = { "fragment0", "fragment1", "fragment2", "fragment3", "fragment4", "fragment5",
			"fragment6", "fragment7", "fragment8", "fragment9", "fragment10" };
	*/
	/** Tableau des machines du cluster */
	private static String[] clusterNames = { "fictif", "mystique", "magneto", "diablo", "jeangrey", "cyclope",
			"malicia", "angel", "colossus", "tornade", "serval" };

	/********************************************************/

	/** Le client a un socket pour envoyer ses fichiers */
	private static Socket s;

	/** On définit arbitrairement le nombre de lignes par fichier */
	/* !!!!!!!!!!!!!!!!!! A CALCULER !!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
	private static int nbLignesparFichier = 3;

	private static void usage() {
		System.out.println("Usage: java HdfsClient read <file>");
		System.out.println("Usage: java HdfsClient write <line|kv> <file>");
		System.out.println("Usage: java HdfsClient delete <file>");
	}

	/**
	 * Permet de supprimer les fragments d'un fichier stocké dans HDFS
	 * 
	 * @param fname
	 *            : nom du fichier à supprimer
	 */
	public static void HdfsDelete(String fname) {

		for (int indHote = 1; indHote <= 11; indHote++) {

			try {
				/* Ouverture de la connexion sur un socket */
				s = new Socket(InetAddress.getByName(clusterNames[indHote]), 8080);

				/* Demander au serveur la suppresion du fichier de nom fname */
				Envoyer_fragment t = new Envoyer_fragment(s, "rien", fname, "d", "0");
				t.start();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Permet d'écrire un fichier dans HDFS. Le fichier localFSSourceFname est
	 * lu sur le système de fichier local, découpé en fragments (de taille fixe)
	 * et les fragments sont envoyés pour stockage sur les différentes machines
	 * 
	 * @param fmt
	 *            : format du fichier en entree
	 * @param fname
	 *            : fichier sur le système de fichiers local
	 */
	public static void HdfsWrite(int fmt, String fname) {

		/* Création d'un format sur lequel on va effetuer la segmentation */
		Format.Type ft = null;
		if (fmt == 0) {
			ft = Format.Type.LINE;
		} else {
			ft = Format.Type.KV;
		}

		FormatLocal fi = new FormatLocal(ft, 1, fname);

		/* Ouverture du fichier en lecture */
		fi.open(Format.OpenMode.R);

		/* On définit un tableau de 10 Threads, qui feront les 10 écritures */
		Envoyer_fragment[] t = new Envoyer_fragment[11];

		try {
			/* lu sert à lire dans le format ouvert */
			KV lu;

			/*
			 * indHote permet de parcourir la liste des différentes machines du
			 * cluster
			 */
			int indHote = 1;

			/* On prend garde à bien enregistrer nbLignesparFichier lignes */
			int dejaEnregistrees = 0;

			while ((lu = fi.read()) != null && (indHote <= 11)) {

				try {

					/* Ouverture de la connexion vers le bon hote */
					s = new Socket(InetAddress.getByName(clusterNames[indHote]), 8080);

					System.out.println("!!!!!!!!!!! On a lu " + lu.v + "!!!!!!!!!!!!");

					System.out.println("On a connecté un socket à " + clusterNames[indHote]);

					/*
					 * Extraction de ce qui nous intéresse : la valeur du KV lu
					 */
					String texte = lu.v;

					/*
					 * Gestion du fragment par un thread pour paralléliser
					 */
					if (dejaEnregistrees == nbLignesparFichier) {
						/* On change d'hote pour cette ligne */
						indHote++;
						t[indHote] = new Envoyer_fragment(s, texte, fname+"_part"+indHote, "w", "0");
						dejaEnregistrees = 1;
					} else {
						t[indHote] = new Envoyer_fragment(s, texte, fname+"_part"+indHote, "w", "0");
						dejaEnregistrees++;
					}

					System.out.println("On enregiste " + texte + " dans " + fname+"_part"+indHote);

					t[indHote].start();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/*
			 * On attend la terminaison de tous les thread avant d'aller fermer
			 * le fichier
			 */
			for (int i = 1; i <= indHote; i++) {
				t[i].join();
			}

		} catch (Exception e) {
			// Cette exception est levée si l'objet FileInputStream ne trouve
			// aucun fichier
			e.printStackTrace();
		}
	}

	/**
	 * Permet de lire un fichier de nom fname à partir de HDFS. Les fragments du
	 * fichier sont lus à partir des différentes machines, concaténés et stockés
	 * localement dans un fichier de nom localFSDestFname.
	 * 
	 * @param fname
	 *            : fichier dont on lit les fragments
	 */
	public static void HdfsRead(String fname) {

		/* Création du fichier de concaténation sur le disque local */
		FormatLocal fi = new FormatLocal(Format.Type.LINE, 1, "localFSDestFname");

		/* Ouverture du format en écriture */
		fi.open(Format.OpenMode.W);

		/* indHote permet de parcourir la liste des machines du cluster */
		/*
		 * On regarde sur chacune des machines du cluster s'il y a un fichier de
		 * nom fname
		 */

		for (int indHote = 1; indHote <= 11; indHote++) {

			try {

				/* Ouverture de la connexionsur un socket */
				s = new Socket(InetAddress.getByName(clusterNames[indHote]), 8080);

				Envoyer_fragment t = new Envoyer_fragment(s, "rien", fname, "r", "0");
				t.start();

				System.out.println("L'ordre de lecture a été envoyé au serveur.");

				/*
				 * Récupération des fragments et mise dans le fichier local de
				 * sortie
				 */
				Recuperer_fragment t1 = new Recuperer_fragment(s, fi);
				t1.start();

				/* Attendre que t1 termine avant de faire le finally */
				try {
					t1.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				System.out.println("Le fichier a été récupéré et écrit localement");

			} catch (FileNotFoundException e) {
				// Cette exception est levée si l'objet FileInputStream ne
				// trouve
				// aucun fichier
				e.printStackTrace();
			} catch (IOException e) {
				// Celle-ci se produit lors d'une erreur d'écriture ou de
				// lecture
				e.printStackTrace();
			} finally {
				System.out.println("Tout est bon.");
			}

		}

	}

	/**
	 * Programme qui effectue le découpage, se connecte à des entités distantes
	 * et leur envoie les fichiers, avec toutes les informations nécessaire à la
	 * récupération et au traitement des fichiers par le Map-Reduce
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// args[0] = instruction ; args[1] = nom du fichier ; args[2] = type du
		// fichier si nécessaire

		System.out.println(args[0]);

		if (args[0].equals("write")) {
			HdfsWrite(Integer.parseInt(args[2]), args[1]);
		} else {

			if (args[0].equals("read")) {
				HdfsRead(args[1]);
			} else {

				if (args[0].equals("delete")) {
					HdfsDelete(args[1]);
				}
			}
		}
	}

}
