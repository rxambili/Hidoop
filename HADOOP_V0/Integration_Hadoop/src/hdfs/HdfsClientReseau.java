package hdfs;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

import formats.Format;
import formats.FormatImpl;
import formats.KV;

/**
 * Le client.
 *
 * @author William Attache and Tom Ragonneau
 * @version 1.0
 */
public class HdfsClientReseau {

	/**
	 * Le tableau des noms des fichiers qui seront enregistrés sur le cluster
	 * par HdfsWrite.
	 */
	private static String[] fileNames = { "aFragmenter_part0", "aFragmenter_part1", "aFragmenter_part2", "aFragmenter_part3", "aFragmenter_part4",
			"aFragmenter_part5", "aFragmenter_part6", "aFragmenter_part7", "aFragmenter_part8", "aFragmenter_part9", "aFragmenter_part10"};

	/**
	 * Le tableau des machines du cluster.
	 */
	private static String[] clusterNames = { "fictif", "mystique", "magneto", "diablo", "jeangrey", "cyclope",
			"malicia", "angel", "colossus", "tornade", "serval" };

	/**
	 * Le tableau des numéro de port des serveurs sur les machines distantes
	 */
	private static int[] numPort = { 0, 8081, 8082, 8083, 8084, 8085, 8086, 8087, 8088, 8089, 8090 };

	/**
	 * Le client a un socket pour envoyer ses fichiers
	 */
	private static Socket s;

	/**
	 * Nombre d'enregistrements distants restant
	 */
	private static int nbRecordLeft = 20;

	/**
	 * Affichage du message d'utilisation.
	 */
	private static void usage() {
		System.out.println("Usage: java HdfsClientReseau read <file>");
		System.out.println("Usage: java HdfsClientReseau write  <file> <line|kv>");
		System.out.println("Usage: java HdfsClientReseau delete <file>");
		System.out.println();
	}

	/**
	 * Permet de supprimer les fragments d'un fichier stocké dans HDFS
	 *
	 * @param fname
	 *            nom du fichier à supprimer
	 */
	public static void HdfsDelete(String fname) {
		for (int indHote = 1; indHote <= 10; indHote++) {

			try {
				/* Ouverture de la connexion sur un socket */
				s = new Socket(InetAddress.getByName(clusterNames[indHote]), numPort[indHote]);

				/* Demander au serveur la suppresion du fichier de nom fname */
				Envoyer_fragment t = new Envoyer_fragment(s, "rien", "frag"+indHote+"/"+fname, "d", "0");
				t.start();

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * Permet d'obtenir une machine sur laquelle enregistrer un fragment (cas de
	 * la duplication)
	 * 
	 * @param duplication
	 *            tableau contenant les machines encore disponibles pour
	 *            l'enregistrement
	 */
	public static int availableAdvice(int[] dupli) {
		Random r = new Random();
		int i = 1 + r.nextInt(10);
		if (nbRecordLeft != 0) {
			while (dupli[i] == 0) {
				i = 1 + r.nextInt(10);
			}
			nbRecordLeft--;
			return i;
		} else {
			return 0;
		}

	}

	/**
	 * Permet d'écrire un fichier dans HDFS. Le fichier localFSSourceFname est
	 * lu sur le système de fichier local, découpé en fragments (de taille fixe)
	 * et les fragments sont envoyés pour stockage sur les différentes machines
	 *
	 * @param fmt
	 *            format du fichier en entree
	 * @param fname
	 *            fichier sur le système de fichiers local
	 */
	public static void HdfsWrite(int fmt, String fname) {

		/*
		 * Création d'un format sur lequel on va effetuer la segmentation
		 ********/
		Format.Type ft = null;
		if (fmt == 0) {
			ft = Format.Type.LINE;
		} else {
			ft = Format.Type.KV;
		}

		FormatImpl fi = new FormatImpl(ft, 1, fname);

		/*
		 * Ouverture du fichier en lecture
		 ***************************************/
		fi.open(Format.OpenMode.R);

		/*
		 * On définit un tableau de 10 Threads, qui feront les 10 écritures
		 ******/
		Envoyer_fragment[] t = new Envoyer_fragment[11];

		/*
		 * On ecrit dans le fichier
		 **********************************************/
		try {
			/* lu sert à lire dans le format ouvert */
			KV lu;

			/*
			 * indHote permet de parcourir la liste des différentes machines du
			 * cluster
			 */
			int indHote = 1;

			while ((lu = fi.read()) != null && (indHote <= 10)) {

				try {

					System.out.println("  ");
					System.out.println("------------------------------------------------");

					System.out.println("Connection : " + clusterNames[indHote] + " sur le port" + numPort[indHote]);

					/*
					 * Extraction de ce qui nous intéresse : la valeur du KV lu
					 */
					String texte = lu.v;

					/* Gestion du fragment par un thread pour paralléliser */

					s = new Socket(InetAddress.getByName(clusterNames[indHote]), numPort[indHote]);
					t[indHote] = new Envoyer_fragment(s, texte,"frag" + indHote, "w", "0");

					System.out.println("Enregistrement de : << " + texte + " >> dans le fichier " + fileNames[indHote]);
					System.out.println("------------------------------------------------");
					System.out.println(" ");

					t[indHote].start();
			
					/* On passe à l'hôte, et donc au fragment, suivant
					 */
					indHote++;

				} catch (Exception e) {
					e.printStackTrace();
					//System.err.println("L'écriture du fichier a échouée...");
				}
			}

		} catch (Exception e) {
			System.err.println("Aucun fichier n'a été trouvé...");
		}
	}

	/**
	 * Permet de lire un fichier de nom fname à partir de HDFS. Les fragments du
	 * fichier sont lus à partir des différentes machines, concaténés et stockés
	 * localement dans un fichier de nom localFSDestFname.
	 *
	 * @param fname
	 *            fichier dont on lit les fragments
	 */
	public static void HdfsRead(String fname) {

		/*
		 * 
		 * Création du fichier de concaténation sur le disque local
		 **************/
		FormatImpl fi = new FormatImpl(Format.Type.LINE, 1, "fragment-res-tmp");

		/*
		 * Ouverture du format en écriture
		 ***************************************/
		fi.open(Format.OpenMode.W);

		/*
		 * On regarde sur chacune des machines du cluster s'il y a un fichier de
		 * nom fname
		 */
		for (int indHote = 1; indHote <= 10; indHote++) {

			try {

				/* Ouverture de la connexionsur un socket */
				s = new Socket(InetAddress.getByName(clusterNames[indHote]), numPort[indHote]);
				
				System.out.println("  ");
				System.out.println("-------------------------------------------");
				System.out.println(" On lit sur la machine " + clusterNames[indHote]);
				
				Envoyer_fragment t = new Envoyer_fragment(s, "rien", "frag"+indHote+"/" + fname, "r", "0");
				t.start();

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
					System.err.print("La connexion a été interrompue...");
				}

			} catch (FileNotFoundException e) {
				System.err.print("Aucun fichier n'a été trouvé...");
			} catch (IOException e) {
				System.err.print("La commande a échouée...");
			}

		}

	}

	/**
	 * Programme qui effectue le decoupage, se connecte a des entites distantes
	 * et leur envoie les fichiers, avec toutes les informations necessaire a la
	 * recuperation et au traitement des fichiers par le Map-Reduce.
	 *
	 * @param args
	 *            Les arguments de la ligne de commande. args[0] correspond a
	 *            l'instruction. args[1] correspond au nom du fichier. args[2]
	 *            correspond au type du fichier.
	 */
	public static void main(String[] args) {

		if (args.length < 2) {
			usage();
			return;
		}

		switch (args[0]) {
		case "write":
			if (args.length != 3) {
				usage();
				return;
			}

			HdfsWrite(Integer.parseInt(args[2]), args[1]);
			break;

		case "read":
			HdfsRead(args[1]);
			break;

		case "delete":
			HdfsDelete(args[1]);
			break;

		default:
			usage();
			break;
		}
	}

}
