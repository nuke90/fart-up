package lpad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


public class Main {
	
	public static void main(String[] args) throws FileNotFoundException{
//		String fileName="e:\\DATI\\COSE\\Uni\\java workspace\\OWLParser\\bin\\fallRisk.owl";
//		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\fallRisk.owl";
//		String fileName="e:\\DATI\\COSE\\Uni\\Tesi\\fallRisk10 0.8.owl";
		String fileName="e:\\Documents and Settings\\Flavio\\Desktop\\fallRisk10 0.8a.owl";
		
		OWLReader reader;
		
		File docFile=new File(fileName);
		reader=new MyReader();
		RiskFactorsData data;
		
//		//leggiamo il documento per il SAX
//		SAXParserFactory spf = SAXParserFactory.newInstance();
//	    
//		spf.setNamespaceAware(true);
//	    try{
//		    SAXParser saxParser = spf.newSAXParser();
//			
//		    XMLReader xmlReader = saxParser.getXMLReader();
//			xmlReader.setContentHandler(new DataPropertyParser());
//			xmlReader.parse(new InputSource(new FileInputStream(new File(fileName))));
//			
//	    }
//	    catch(SAXException e){
//	    	e.printStackTrace();
//	    }
//	    catch(IOException e){
//	    	
//	    } catch (ParserConfigurationException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
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