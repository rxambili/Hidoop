/* une PROPOSITION de squelette, incomplete et adaptable... */

package hdfs;
import formats.Format;
import formats.KV;
// import formats.KVFormat;
// import formats.LineFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * Classe HdfsClient.
 * Implementation de HdfsRead pour une utilisation en locale afin de tester.
 * @author Bonnet, Steux, Xambili
 *
 */
public class HdfsClient {

    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public static void HdfsDelete(String hdfsFname) {}
	
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, 
     int repFactor) { }

    /**
     * Permet de concatener les differentes parties (4 parties) d'un fichier.
     * Implementer afin de pouvoir tester plus facilement en local.
     * @param hdfsFname nom du fichier source
     * @param localFSDestFname nom du fichier destinataire
     */
    public static void HdfsRead(String hdfsFname, String localFSDestFname, int nbParts) {
    	BufferedWriter bw = null;
    	try {
		File fichier = new File(localFSDestFname);
		fichier.createNewFile();
		fichier.setReadable(true);
		fichier.setWritable(true);
    	bw = new BufferedWriter(new FileWriter(fichier));
    	} catch (IOException e) {
			e.printStackTrace();
    	}
    	if ( bw != null) {
    		for (int i = 1; i <= nbParts; i++) {
    			BufferedReader br = null;
    			try {
    				br = new BufferedReader(new FileReader(hdfsFname + "_part" + String.valueOf(i)));
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			String line;
    			if (br != null) {
    				try {
    					while ((line = br.readLine()) != null) {
    						bw.write(line, 0, line.length());
    						bw.newLine();
    					}
    					br.close();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}
    		}
    		try {
    			bw.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    }

	
    public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>

        try {
            if (args.length<2) {usage(); return;}

            switch (args[0]) {
              case "read": HdfsRead(args[1],null,1); break;
              case "delete": HdfsDelete(args[1]); break;
              case "write": 
                Format.Type fmt;
                if (args.length<3) {usage(); return;}
                if (args[1].equals("line")) fmt = Format.Type.LINE;
                else if(args[1].equals("kv")) fmt = Format.Type.KV;
                else {usage(); return;}
                HdfsWrite(fmt,args[2],1);
            }	
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
