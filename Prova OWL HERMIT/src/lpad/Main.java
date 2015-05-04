package lpad;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;


public class Main {
	
	public static void main(String[] args) throws FileNotFoundException{
//		String fileName="e:\\DATI\\COSE\\Uni\\java workspace\\OWLParser\\bin\\fallRisk.owl";
//		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\fallRisk.owl";
		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\fallRisk9 0.4.owl";
		OWLReader reader;
		
		File docFile=new File(fileName);
		reader=new MyReader();
		RiskFactorsData data;
		data=reader.read(new FileReader(docFile));
		System.out.println("FINE PROGRAMMA!!!!!");
		
	      //XXX parte in cui proviamo deAndrea
//        DeAndrea dea;
//        
//        dea=new DeAndrea();
        
//        for(Factor f: dea.riskFactorsData().getActiveFactors()){
//        	System.out.println(f.getName());
//        }
//        
		
		
		
	
}
}