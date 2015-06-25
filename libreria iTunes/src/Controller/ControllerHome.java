package Controller;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import ParserLibreria.ParserLibreriaITunes;
import View.mioForm;

public class ControllerHome implements ActionListener,WindowListener,WindowStateListener{

	public final class FileFilterSoloDir extends
			javax.swing.filechooser.FileFilter {
		public String getDescription() {return null;}

		public boolean accept(File arg0) {
			if(arg0.isDirectory()) return true;
			String estensione=arg0.getName();
			if(arg0.getName().length()>3){
				estensione=estensione.substring(estensione.length()-4,estensione.length());
			}
			if(estensione.compareTo(".xml")==0)return true;
			else return false;
		}
	}
	public List<String> listaNomiProcessi;
	public List<ProcessoCopia> listaProcessi;
	protected ParserLibreriaITunes parser;
	protected mioForm home;
	
	public ControllerHome(ParserLibreriaITunes a, mioForm form){
		listaNomiProcessi=new ArrayList<String>();
		listaProcessi=new ArrayList<>();
		this.parser=a;
		this.home=form;
	}
	
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent arg0) {
		
		//per aprire la libreria
		//qui le cose di quando premiamo i pulsanti
		
		String nome=(((JComponent)arg0.getSource()).getName());
		if(nome.compareTo("Copia")==0){
			copiaPlaylist();
		}
		
		if(nome.compareTo("CopiaChiavetta")==0){
			copiaPlaylistMultiCartella();
		}
		
		if(nome.compareTo("About")==0){
			JOptionPane.showMessageDialog(null,"Programma creato da\nNuke90");
		}
		if(nome.compareTo("Exit")==0){
			windowClosing(null);
		}
		
		for(int i=0;i<listaProcessi.size();i++){
			ProcessoCopia p=listaProcessi.get(i);
			if(p.nomeProcesso.compareTo(nome)==0) {
				p.stop();
				listaProcessi.remove(p);
				listaNomiProcessi.remove(p.nomeProcesso);
				aggiornaListaProcessi();
				break;
			
			}
		}
		
	}
	
	/*
	 * Copia la playlist in un punto deciso dall'utente, controlliamo però che lo spazio disponibile sia presente!
	 * Come?
	 */
	public void copiaPlaylist(){
//		home.setEnabled(false);
		File currDir=new File("D:\\playlist");
		File f=null;
//		int ritorno=0;
		JFileChooser chooser=new JFileChooser();
		chooser.setVisible(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		while(f==null || !f.exists() || parser==null || !f.isDirectory()){
			chooser.setCurrentDirectory(currDir);
			chooser.setDialogTitle("Scegli la cartella in cui copiare");
			if(chooser.showOpenDialog(home)!=0)return;
			f=chooser.getSelectedFile();
		}
		//qui mandiamo direttamente la cosa, ci vuole un ritorno tipo -1 per mancanza di spazio
		//usiamo un processo a parte
		
		String playlist=home.getComboChoice().replaceFirst(" \\(n:.+\\)$", "");
		ProcessoCopia p=new ProcessoCopia(parser, f.getAbsolutePath()+"\\", playlist,this,"Copia playlist:"+playlist+" Cartella:"+f.getAbsolutePath()+"\\",0);
		p.start();
		//aggiungiamo il processo a una lista.
		listaProcessi.add(p);

	}//fine metodo
	
	public void copiaPlaylistMultiCartella(){
//		home.setEnabled(false);
		File f=null;
		JFileChooser chooser=new JFileChooser();
		chooser.setVisible(true);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		while(f==null || !f.exists() || parser==null || !f.isDirectory()){
			chooser.setDialogTitle("Scegli il drive su cui copiare");
			if(chooser.showOpenDialog(home)!=0)return;
			f=chooser.getSelectedFile();
		}
		//qui mandiamo direttamente la cosa, ci vuole un ritorno tipo -1 per mancanza di spazio
		//copia tutto nella cartella principale con il nome della playlist
		String cartella=home.getComboChoice().replaceFirst(" \\(n:.+\\)$", "");
		
		//lettera unità+cartella
		String path=f.getAbsolutePath().split(":")[0]+":\\"+cartella+"\\";
		int numeroFile=-1;
		
		
		while(numeroFile<=0){
			try{
				String ris=JOptionPane.showInputDialog("Inserisci il numero di brani massimo");
				if(ris==null) return;
				numeroFile=Integer.parseInt(ris);
			}catch(NumberFormatException e){
				continue;
			}
		}
		//usciamo
		
		String playlist=home.getComboChoice().replaceFirst(" \\(n:.+\\)$", "");
		
		ProcessoCopia p=new ProcessoCopia(parser, path, playlist,this,"Copia playlist:"+playlist+" Cartella:"+path,numeroFile);
		p.start();

	}//fine metodo

	public void aggiornaListaProcessi(){
		home.clearButtonMediano();
		home.etichetta.setText("<html>Processi in corso:<br>");
		for(String s : listaNomiProcessi){
			home.etichetta.setText(home.etichetta.getText()+s+"<br>");
			home.addButtonMediano(s,this);
		}
		home.etichetta.setText(home.etichetta.getText()+"</html>");
	}
	
	public void windowActivated(WindowEvent arg0) {
		
		
		
	}

	public void windowClosed(WindowEvent arg0) {
		
		
	}

	public void windowClosing(WindowEvent arg0) {
		
		if(listaNomiProcessi.size()>0){
			int ritorno=JOptionPane.showConfirmDialog(null, "Ci sono dei processi in esecuzione, vuoi chiudere lo stesso? Questo terminerà i processi");
			if(ritorno==0)System.exit(0);
		}
		else{
			System.exit(0);
		}
	}

	public void windowDeactivated(WindowEvent arg0) {
		
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		
		
	}

	public void windowIconified(WindowEvent arg0) {
		
		
	}

	public void windowOpened(WindowEvent arg0) {
		
		home.setEnabled(false);
		JFileChooser chooser=new JFileChooser();
		javax.swing.filechooser.FileFilter fileF;
		fileF=new FileFilterSoloDir();
		chooser.setFileFilter(fileF);
		//mette come cartella di default quella che dovrebbe essere di iTunes
		File currDir=new File(System.getenv("USERPROFILE")+"\\Music\\iTunes");
		File f=new File(System.getenv("USERPROFILE")+"\\Music\\iTunes\\iTunes Music Library.xml");
		
		//manda dei messaggi JOptionPane.showMessageDialog(home, "Eggs are not supposed to be green.");
		
		while(f==null || !f.exists() || parser==null){
			
			while(f==null){
				chooser.setCurrentDirectory(currDir);
				chooser.setDialogTitle("Scegli il file della libreria di ITunes");
				chooser.showOpenDialog(((Window)arg0.getSource()).getParent());
				f=chooser.getSelectedFile();
			}
			
			try{
				if(parser==null){
					parser=new ParserLibreriaITunes(f.getPath());
				}
			} catch (ParserConfigurationException e) {
				
				JOptionPane.showMessageDialog(home, "Problemi con la configurazione del parser! Esco");
				e.printStackTrace();
				System.exit(1);
			} catch (SAXException e) {
				
				System.out.println("Problemi con sax");
				JOptionPane.showMessageDialog(home, "Problemi del parser SAX! Esco");
				e.printStackTrace();
				System.exit(2);
			} catch (IOException e) {
				
				System.out.println("problemi con il file di iTunes");
				JOptionPane.showMessageDialog(home, "Problemi con l'apertura del file! Esco");
				e.printStackTrace();
				System.exit(3);
			}
		}
		//popoliamo le playlist
		
		home.setPlaylist(parser.getPlaylistNamesTrackNumberAndSize());
		//rendiamo la home visibile solo quando possiamo usarla
		home.setEnabled(true);
	}

	public void windowStateChanged(WindowEvent arg0) {
		
		
	}

}