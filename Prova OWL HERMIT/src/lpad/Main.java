package lpad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Main {
	
	public static void main(String[] args) {
		
//		String fileName="e:\\DATI\\COSE\\Uni\\java workspace\\OWLParser\\bin\\fallRisk.owl";
//		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\fallRisk.owl";
//		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\fallRisk10 0.8.owl";
		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\in uso\\fallRisk10 0.93.owl";
		
		OWLReader reader;
		
		File docFile=new File(fileName);
		reader=new MyReader();
		RiskFactorsData data;
		
		try {
			data=reader.read(new FileReader(docFile));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("FINE PROGRAMMA!!!!!");
			
	}
	
}