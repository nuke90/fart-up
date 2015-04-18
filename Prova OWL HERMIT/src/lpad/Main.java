package lpad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {
	
	
	public static void main(String[] args) throws FileNotFoundException{
//		String fileName="e:\\DATI\\COSE\\Uni\\java workspace\\OWLParser\\bin\\fallRisk.owl";
		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\fallRisk.owl";
		OWLReader reader;
		
		File docFile=new File(fileName);
		reader=new MyReader();
		
		reader.read(new FileReader(docFile));
		
	}
}
