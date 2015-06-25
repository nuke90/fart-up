import Controller.ControllerHome;
import ParserLibreria.ParserLibreriaITunes;
import View.mioForm;

public class Inizio{
	static ParserLibreriaITunes parser;
	public static void main(String[] args) {
		

		//iniziamo con l'handler di default
		
		parser=null;
		
			//al parser mandiamo l'handler che abbiamo creato noi, possiamo fare una classe wrapper
			
		mioForm formIniziale=new mioForm();
		ControllerHome cHome=new ControllerHome(parser,formIniziale);
		formIniziale.addWindowListener(cHome);
		formIniziale.setEnabled(false);
		formIniziale.setOkButtonListener(cHome);
		formIniziale.setCopiaChiavettaButtonListener(cHome);
		formIniziale.setAboutButtonListener(cHome);
		formIniziale.setExitButtonListener(cHome);
		formIniziale.setVisible(true);

	}//fine main
	
}
