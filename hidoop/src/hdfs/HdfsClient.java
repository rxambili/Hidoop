/* une PROPOSITION de squelette, incomplète et adaptable... */

package hdfs;
import formats.Format;
import formats.KV;
import formats.KVFormat;
import formats.LineFormat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;

public class HdfsClient {

    private static void usage() {
        System.out.println("Usage: java HdfsClient read <file>");
        System.out.println("Usage: java HdfsClient write <line|kv> <file>");
        System.out.println("Usage: java HdfsClient delete <file>");
    }
	
    public static void HdfsDelete(String hdfsFname) {}
	
    public static void HdfsWrite(Format.Type fmt, String localFSSourceFname, 
     int repFactor) { }

    public static void HdfsRead(String hdfsFname, String localFSDestFname) {
	try {
		BufferedWriter bw = new BufferedWriter(new FileWriter(localFSDestFname));
	} catch (IOException e) {
			e.printStackTrace();
	}
    	for (int i = 1; i < 5; i++) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(hdfsFname + "_part" + String.valueOf(i)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		String line;
		while ((line = br.readLine()) != null) {
   			bw.write(line, 0, line.length());
			bw.newLine();
		}
		try {
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	    try {
		    bw.close();
	    } catch (IOException e) {
			e.printStackTrace();
	    }
    }

	
    public static void main(String[] args) {
        // java HdfsClient <read|write> <line|kv> <file>

        try {
            if (args.length<2) {usage(); return;}

            switch (args[0]) {
              case "read": HdfsRead(args[1],null); break;
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
