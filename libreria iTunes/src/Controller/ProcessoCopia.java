package Controller;
import javax.swing.JOptionPane;

import ParserLibreria.ParserLibreriaITunes;
public class ProcessoCopia extends Thread{

	//protected HandlerITunesLibrary handler;
	protected ParserLibreriaITunes parser;
	protected String path;
	protected String playlist;
	protected ControllerHome padre;
	protected int limiteBrani;
	protected String nomeProcesso;
	
	public ProcessoCopia(ParserLibreriaITunes parser, String path,String playlist, ControllerHome padre, String nomeProcesso,int limiteBrani) {
		super();
		this.parser = parser;
		this.path = path;
		this.playlist = playlist;
		this.padre=padre;
		this.limiteBrani=limiteBrani;
		this.nomeProcesso=nomeProcesso;
	}

	public void run() {
		
		int ritorno=0;
		padre.listaNomiProcessi.add(nomeProcesso);
		padre.aggiornaListaProcessi();
		
		if(limiteBrani==0){
			ritorno=parser.azioneCompletaCartella(path, playlist);
			try {
				sleep(2000); //per lasciare il tempo che la copia finisca
			} catch (InterruptedException e) {
				
			}
			parser.creaPlaylistM3U(path,parser.sistemaPath(playlist)+".m3u");
		}else{
			ritorno=parser.azioneCompletaCartellaLimiteBrani(path, playlist,limiteBrani);
		}
		if(ritorno<0) JOptionPane.showMessageDialog(null, "La copia non è stata fatta siccome lo spazio non è sufficiente, path:"+path);
		else {
			if(parser.nonCopiati==0){
				JOptionPane.showMessageDialog(null, "Copia avvenuta con successo, file copiati:"+parser.copiati+",file rinominati:"+parser.rinominati+", playlist:"+playlist+" path:"+path);
			}else{
				JOptionPane.showMessageDialog(null, "Copia avvenuta con successo, file copiati:"+parser.copiati+",file rinominati:"+parser.rinominati+", non copiati: "+parser.nonCopiati+",playlist:"+playlist+" path:"+path);
			}
		}
		padre.listaNomiProcessi.remove(nomeProcesso);
		padre.listaProcessi.remove(this);
		padre.aggiornaListaProcessi();
	}

}
