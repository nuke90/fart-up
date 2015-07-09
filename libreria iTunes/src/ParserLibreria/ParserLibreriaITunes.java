package ParserLibreria;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;



public class ParserLibreriaITunes {


public final class FileFilterSoloFile implements FileFilter {
	public boolean accept(File arg0) {
			if(arg0.isDirectory()) return false;
			else return true;
	}
}

protected HandlerITunesLibrary handler;
public int copiati=0,nonCopiati=0,rinominati=0;
@SuppressWarnings("unused")


private ParserLibreriaITunes(){
	
}



public ParserLibreriaITunes(String path) throws ParserConfigurationException, SAXException, IOException{
	File f=new File(path);
	if(!f.exists()) throw new FileNotFoundException();
	//abbiamo controllato che esista
	
	SAXParserFactory fact=SAXParserFactory.newInstance();
	handler=new ConcreteHandlerITunes();
	
	//al parser mandiamo l'handler che abbiamo creato noi, possiamo fare una classe wrapper
	SAXParser parser=fact.newSAXParser();
	parser.parse(f, handler);
	
}

//adesso la parte con tutti i magheggi

//restituisce una mappa in cui la chiave  è il nome del file da copiare, e il valore è brano.
public Map<String,Brano> mappaNuovoNomeFilePlaylist(String nomePlaylist) throws Exception{
	Map<String,Brano> uscita;
	uscita=new TreeMap<String, Brano>();
	NavigableMap<String, List<Integer>> playlist;
	NavigableMap<Integer, Brano> brani;
	brani=handler.getBrani();
	playlist=handler.getPlaylist();
	if(playlist.get(nomePlaylist)!=null){
		List<Integer> indiciPlaylist=playlist.get(nomePlaylist);
		//abbiamo ottenuto l'elenco dei brani
		int i=0;
		int numeroCifre=0; //qui diciamo quante cifre ci vogliono in totale
		numeroCifre=((Double)Math.log10(indiciPlaylist.size())).intValue()+1;
		for (Integer indice : indiciPlaylist){
			i++;	
			String nomeFile=new String();
			int cifreDaAgg=numeroCifre-((Double)Math.log10(i)).intValue()-1;
			for(int k=0;k<cifreDaAgg;k++){
				nomeFile=nomeFile+"0";
			}
			nomeFile=nomeFile+i+"-";
			//adesso abbiamo messo bene l'indice
			//System.out.println(brani.get(indice).location);
			if(brani.get(indice).location.matches(".+\\..+$")){
				int indiceEstensione;
				indiceEstensione=brani.get(indice).location.split("\\.").length-1;
				
				//dobbiamo preparare Nome, artista e quant'altro
				String nome;
				String artista;
				
				//vanno resi inoffensivi
				nome=sistemaPath(brani.get(indice).name);
				
				artista=sistemaPath(brani.get(indice).artist);
				
				nomeFile=nomeFile+artista+"-"+nome+"."+brani.get(indice).location.split("\\.")[indiceEstensione];
			}
			
			//qui abbiamo il nome file completo per la copia sul supporto, o per ins in lista
			/*
			 * associamo questo e il brani, poi possiamo confrontare.
			 * penso che ci voglia un troncamento per via del filesystem
			 *
			 *Lunghezza massima FAT 32 percorso+nome=255 caratteri 
			 */
			//System.out.println(nomeFile);
			uscita.put(nomeFile,brani.get(indice));
		}
	}
	if(!uscita.isEmpty()){
		return uscita;
	}
	else{
		throw new Exception("il nome della playlist è sbagliato o la playlist è vuota");
	}
}//fine della funzione


//stessa cosa di rinomina solo che elimina le cose che non c'entrano assolutamente niente
public void rinominaElimina(Map<String,String> fileDaRin){
	rinominati=rinominati<0 ? rinominati : 0;
	String pathControllo=null;
	for(String s : fileDaRin.keySet()){
		File f=new File(s);
		//String path=f.getParent();
		//System.out.println(path);
		f.renameTo(new File(f.getParent()+"\\"+fileDaRin.get(s)));
		pathControllo=f.getParent()+"\\";
		//usiamo un solo path alla fine
		rinominati=rinominati<0 ? rinominati-1 : rinominati+1;
	}
	//aggiunta parte per eliminazione
	for(String s: fileDaRin.values()){
		File listaFile[];
		listaFile=new File(pathControllo).listFiles(new FileFilterSoloFile());
		String numero;
		numero=s.split("-")[0];
		for(File f : listaFile){
			String numeroFile;
			numeroFile=f.getName().split("-")[0];
			if(f.getName().compareTo(s)!=0 && (numeroFile.compareTo(numero)==0 || numeroFile.length()!=numero.length())){
				f.delete();
			}
		}//fine for su listaFile
	}//fine for su keySet
}//fine metodo


//copia una lista di brani formata da mappaNuovoNomeFilePlaylist in una cartella che forniamo.
//elimina quei file che hanno lo stesso indice e non sono quelli creati da noi
int copiaElimina(Map<String,Brano> mappa, String percorso) throws FileSystemException{
	copiati= copiati<0 ? copiati : 0;
	nonCopiati= nonCopiati<0 ? nonCopiati : 0;
	if(percorso.matches(".+\\\\$")){
		
		//facciamo un controllo sullo spazio disponibile e ritorniamo -1 se lo spazio disponibile non è sufficiente
		long spazioDisp=0;
		long spazioRichiesto=0;
		
		//qui determiniamo lo spazio richiesto
		for(Brano b : mappa.values()){
			spazioRichiesto+=b.size;
		}
		
		//qui determiniamo lo spazio disponibile
		File cartellaDestinazione=new File(percorso);
		
		//controlliamo che la cartella esista
		if(!cartellaDestinazione.exists() || !cartellaDestinazione.isDirectory()) return -1;
		
		
		//otteniamo la lista dei file
		
		File[] listaFile;
		listaFile=cartellaDestinazione.listFiles(new FileFilterSoloFile());
		
		
		
		for(String s: mappa.keySet()){
			String numero;
			numero=s.split("-")[0];
			
			for(File f : listaFile){
				String numeroFile;
				numeroFile=f.getName().split("-")[0];
				if(numeroFile.compareTo(numero)==0 || numeroFile.length()!=numero.length()){
					f.delete();
				}
			}
		}
		

		//adesso che abbiamo fatto le eliminazioni controlliamo se c'è lo spazio
		spazioDisp=cartellaDestinazione.getFreeSpace();
		

		
		if(spazioRichiesto>spazioDisp){
			return -1;
		}
		
		for(String s : mappa.keySet()){
			File source,dest;
			source=new File(mappa.get(s).location);
			dest=new File(percorso+s);
			//copia vera e propria
			try {
			//	if(!source.exists()) throw new FileNotFoundException();
				Files.copy(source.toPath(), dest.toPath(),StandardCopyOption.COPY_ATTRIBUTES);
				copiati=copiati<0 ? copiati-1 : copiati+1;
				
			} catch (IOException e) {
				
				System.out.println("Problema di IO");
				nonCopiati=nonCopiati<0 ? nonCopiati-1 : nonCopiati+1;
				e.printStackTrace();
			} catch (InvalidPathException e){
				System.out.println("Path sbagliato di:"+mappa.get(s).location);
				System.out.println("si voleva copiare in:"+dest.toPath());
				nonCopiati=nonCopiati<0 ? nonCopiati-1 : nonCopiati+1;
				e.printStackTrace();
			}
			
		}
		//se va tutto bene
		return 1;
	}
	else{
		
		throw new FileSystemException("la cartella non è nel formato giusto(c:\\nome\\cartella\\)");
		
	}//if del matches
}//fine funzione

//ci da una mappa con i file da rinominare, la chiave è il nome del file vecchio, il valore il nome del file nuovo,
//percorso è il percorso della cartella in cui controllare
//mappa è la mappa di brani "nuova" generata dalla playlist
public Map<String,String> ottieniFileDaRinominare(String percorso, Map<String,Brano> mappa) throws FileSystemException{
	File cartellaDaControllare;
	if(percorso.matches(".+\\\\$")){
		cartellaDaControllare=new File(percorso);
		if(cartellaDaControllare.isDirectory()){//il percorso è valido
			File listaFile[];
			Set<String> chiaviMappa;
			Map<String,String> fileDaRinominare; //la chiave è il file vecchio, il valore il file nuovo
			
			fileDaRinominare=new TreeMap<String, String>();
			chiaviMappa=mappa.keySet(); //tutte le chiavi, che corrispondono al nome del file nuovo che vogliamo mettere
			//filtro per i file
			
			listaFile=cartellaDaControllare.listFiles(new FileFilterSoloFile());
			
			
			//qui vediamo se coincidono
			
			for(File f : listaFile){
				//controllo per ogni file
				String provv,name,artist,numero;
				int size=0;
				try{
					provv=f.getName();
					numero=provv.split("-")[0];
					artist=provv.split("-")[1];
					name=provv.split("-")[2];
					name=name.split("\\..+$")[0];
					size=(int)f.length();
				}catch(Exception e){
					//catturiamo l'eccezione nel caso in cui lo split non vada a buon fine, semplicemente sarà uno dei file
					//da eliminare.
					numero="";
					artist="";
					name="";
					size=0;
				}
				//facciamo il replace per le regexp
				
				artist=sistemaPath(artist);
				name=sistemaPath(name);
				
				Pattern.quote(name);
				Pattern.quote(artist);
				

				for(String str : chiaviMappa){
					if(str.matches("^"+numero+"-"+artist+"-"+name+"\\..+$")) break; //nel caso in cui sia uguale in tutto e per tutto così non rinomina
					if(str.matches("^.+-"+artist+"-"+name+"\\..+$")){
						//adesso abbiamo controllato il nome, controlliamo la size
//							System.out.print("match nome & ");
						//controllo size
						if(mappa.get(str).size==size){

							//quando siamo qui vuol dire che la corrispondenza è totale.
							
							//cosa facciamo? facciamo 2 liste separate
							fileDaRinominare.put(f.getPath(),str);
							
							break; //per uscire dal ciclo siccome non ci serve
						}//fine if size
					}//fine if match nome
				}//fine for sulle stringhe delle chiavi
			}//fine for sui file
			
			//nella map fileDaRinominare ci sono tutti quei file che hanno il matching corretto anche in size e vanno solo rinominati
			
			return fileDaRinominare;
			
		}else{//caso con percorso non valido
			return null;
		}//fine if percorso
	}else{
		throw new FileSystemException("la cartella non è nel formato giusto(c:\\nome\\cartella\\)");
	}
}//fine metodo

//dato un percorso fornisce la mappa di file che sono da copiare, in quanto nuovi
//nel ritorno la chiave è il nome del file da copiare, il valore è il brano.
public Map<String,Brano> ottieniFileDaCopiare(String percorso, Map<String,Brano> mappa) throws FileSystemException{
	File cartella;
	if(percorso.matches(".+\\\\$")){
		cartella=new File(percorso);
		if(cartella.isDirectory()){//il percorso è valido
			
			List<String> listaDaCanc=new ArrayList<String>();
			
			File listaFile[]=cartella.listFiles(new FileFilterSoloFile());
			
			for(String s:mappa.keySet()){
				//s è il nome che dovremmo copiare dentro alla cartella se non ci fosse, noi dobbiamo controllare se c'è o meno
				for(File f : listaFile){
					//controlliamo se va bene più o meno
					String nome=f.getName();
					int size=(int)f.length();
					if(s.compareTo(nome)==0 && size==mappa.get(s).size){
						listaDaCanc.add(s);
						break;
					}
				}
			}//fine ciclo totale
			
			Map<String,Brano> mappaProv=new TreeMap<String, Brano>();
			for(java.util.Map.Entry<String, Brano> e : mappa.entrySet()){
				mappaProv.put(e.getKey(), e.getValue());
			}
			
			for(String s:listaDaCanc) {
				mappaProv.remove(s);
			}
			
			return mappaProv;
			
		}//fine if cartella
		else
		{//caso con percorso non valido
			return null;
		}//fine if isDirectory
	}else{
		throw new FileSystemException("la cartella non è nel formato giusto(c:\\nome\\cartella\\)");
	}//fine if percorso matches
}//fine metodo

//dato un percorso e il nome di una playlist controlla quali sono da rinominare e quali sono invece da copiare nuovamente
//poi esegue tutte e due le cose.
public int azioneCompletaCartella(String percorso, String nomePlaylist){
	
	try {
		Map<String,String> fileDaRin=ottieniFileDaRinominare(percorso,mappaNuovoNomeFilePlaylist(nomePlaylist));
		rinominaElimina(fileDaRin);
		Map<String,Brano> fileDaCop=ottieniFileDaCopiare(percorso, mappaNuovoNomeFilePlaylist(nomePlaylist));
		pulizia(percorso, nomePlaylist);
		//qui possiamo mettere anche copiaElimina se vogliamo che elimini i file che non c'entrano
		return copiaElimina(fileDaCop, percorso);
		
	} catch (FileSystemException e) {
		
		e.printStackTrace();
	} catch (Exception e) {
		
		e.printStackTrace();
	}
	return -1;
}

//fa la stessa cosa di azione completa ma può dividere in due o più parti le cartelle a seconda del limiteDiBrani
public int azioneCompletaCartellaLimiteBrani(String percorso, String nomePlaylist,int limiteBrani){
	rinominati=-1;
	copiati=-1;
	nonCopiati=-1;
	//dobbiamo controllare il numero di cartelle disponibile
	File cartellaMadre=new File(percorso);
	
	//if(!(cartellaMadre.exists() && cartellaMadre.isDirectory())) return -1;
	//adesso vediamo qual'è il numero dei file
	percorso=cartellaMadre.getPath()+"\\";
	
	try {
		
		
		
		
		int conta=0;
		int i=0;
		
		String percorsoProvvisorio; //percorso che usiamo per le cartelle
		
		percorsoProvvisorio=percorso;
		percorsoProvvisorio=percorsoProvvisorio.replaceAll("\\\\$", " "+conta+"\\\\");
		
		//DA QUI IN POI USIAMO SOLO PERCORSO PROVVISORIO!!!!!!!!!!!!!!
		
		//la rinomina non la modifichiamo
//		rinomina(fileDaRin);
		
		//caso in cui usiamo una sola cartella

//		return copia(fileDaCop, percorso);
		
		
		//caso in cui abbiamo più cartelle
		
		//lo inizializziamo
		
		
		
		//usiamo la mappa totale all'inizo
		Object valori[]=mappaNuovoNomeFilePlaylist(nomePlaylist).values().toArray();
		Object chiavi[]=mappaNuovoNomeFilePlaylist(nomePlaylist).keySet().toArray();
		
		float numeroCartelle=(float)mappaNuovoNomeFilePlaylist(nomePlaylist).size()/limiteBrani;
		
		if((numeroCartelle%1)>0) numeroCartelle=numeroCartelle-(numeroCartelle%1)+1;

		
		for(;conta<numeroCartelle;conta++){
			percorsoProvvisorio=percorso;
			percorsoProvvisorio=percorsoProvvisorio.replaceAll("\\\\$", " "+conta+"\\\\");
			//creiamo la cartella
			File nuovaCartella=new File(percorsoProvvisorio);
			if(!nuovaCartella.exists())nuovaCartella.mkdir();
			

			Map<String, Brano> mappaProv=new TreeMap<String, Brano>();
			
			for(;i<(limiteBrani*(conta+1)) && i<chiavi.length;i++){
				//la mappa provvisoria contiene tutti i brani che dobbiamo controllare
				mappaProv.put(((String)chiavi[i]),((Brano)valori[i]));
				
				//adesso abbiamo le cose specifiche per la singola cartella
			}
			
			Map<String,String> fileDaRin=ottieniFileDaRinominare(percorsoProvvisorio,mappaProv);
			rinominaElimina(fileDaRin);
			Map<String,Brano> fileDaCop=ottieniFileDaCopiare(percorsoProvvisorio, mappaProv);
			pulizia(percorsoProvvisorio,mappaProv); //da modificare perché lla chiamata sopra modifica la lista!
			if(copiaElimina(fileDaCop, percorsoProvvisorio)<0){
				return -1;
			}
		}
		copiati=(copiati+1)*-1;
		nonCopiati=(nonCopiati+1)*-1;
		rinominati=(rinominati+1)*-1;
		//alla fine se va tutto bene ritorna 1
		return 1;
	} catch (FileSystemException e) {
		
		e.printStackTrace();
	} catch (Exception e) {
		
		e.printStackTrace();
	}
	return -1;
}


/*sistema i path per esseri sicuri che non ci siano problemi con caratteri speciali o quant'altro, tutte le cose 
 * tutte le cose prima di essere salvate o controllate per l'esistenza passano di qui.
*/
public String sistemaPath(String str){
	if(str==null) return str;
	str=str.replaceAll("/", "_");
	str=str.replaceAll("-", "_");
	str=str.replaceAll("\\?", "_");
	str=str.replaceAll(":", "_");
	str=str.replaceAll("\\\\", "_");
	str=str.replaceAll("\\*", "_");
	str=str.replaceAll("<", "_");
	str=str.replaceAll(">", "_");
	str=str.replaceAll("\\(", "_");
	str=str.replaceAll("\\)", "_");
	str=str.replaceAll("\\[", "_");
	str=str.replaceAll("\\]", "_");
	str=str.replaceAll("\\\"", "_");
	return str;
}

public List<String> getPlaylistNamesTrackNumberAndSize(){
	
	List<String> ret=new ArrayList<String>();
	long dimensione=0;
	for(String s : handler.getPlaylist().keySet()){
		dimensione=0;
		for(Integer indice : handler.getPlaylist().get(s)){
			if(handler.getBrani()!=null && handler.getBrani().containsKey(indice)){
				dimensione+=handler.getBrani().get(indice).size; //otteniamo la dimensione in bite, noi la esprimiamo in MB
			}
		}
		dimensione=dimensione/1048576;
		ret.add(s+" (n:"+handler.getPlaylist().get(s).size()+" brani, "+dimensione+"MB)");
	}
	return ret;
	
}

//include tutti i file presenti nella cartella, per questo va eseguito sempre dopo le altre cose
public void creaPlaylistM3U(String path){
	creaPlaylistM3U(path, "playlist.m3u");
}

public void creaPlaylistM3U(String path,String nomeFile){
	File f=new File(path);
	if(!f.isDirectory()) return;
	
	File[] listaFile=f.listFiles(new FileFilterSoloFile());
	
	//dobbiamo ordinare i file nel modo giusto.
	
	SortedMap<String, File> mappaOrd= new TreeMap<String, File>();

	for(File file : listaFile) mappaOrd.put(file.getName(), file);
	
	
	File playlist=new File(f.getAbsolutePath()+"\\"+nomeFile);
	try {
		BufferedWriter out=new BufferedWriter(new FileWriter(playlist));
		/*
		 * Ho aggiunto gli \r visto che lo standard dos prevede CARRIAGE RETURN E NEW LINE
		 */
		//intestazione
		out.write("#EXTM3U\r\n");
		for(File file : mappaOrd.values()){
			out.write("#EXTINF:0,"+file.getName()+"\r\n");
			out.write(file.getName()+"\r\n");
			out.write("\r\n");
			out.flush();
		}
		out.close();
	} catch (FileNotFoundException e) {
		
		e.printStackTrace();
	} catch (IOException e) {
		
		e.printStackTrace();
	}
	
}

protected void pulizia(String path,String playlist){
	File cartellaDestinazione=new File(path);
	Map<String,Brano> temporanea;
	
	
	if(!(cartellaDestinazione.isDirectory() && cartellaDestinazione.exists())) return;
	
	try {
		temporanea=mappaNuovoNomeFilePlaylist(playlist);
	} catch (Exception e1) {
		
		return;
	}
	
	File listaFile[];
	
	listaFile=cartellaDestinazione.listFiles(new FileFilterSoloFile());
	
	int numeroMax=0; //massimo dei file che dobbiamo copiare
	
	for(String s : temporanea.keySet()){
		String numero=s.split("-")[0];
		numeroMax= Integer.parseInt(numero)>numeroMax ? Integer.parseInt(numero) : numeroMax;
	}
	
	for(File f : listaFile){
		String numeroFileStringa=null;
		int numeroFile;
		try{
			numeroFileStringa=f.getName().split("-")[0];
			numeroFile=Integer.parseInt(numeroFileStringa);
		}catch(Exception e){
			numeroFile=-1;
		}
		if(numeroFile>numeroMax || numeroFile==-1){
			f.delete();
		}
	}
	
	
}//fine metodo

//pulizia elimina quegli indici che sono maggiori del massimo che abbiamo o quei file che non servono a niente e che hanno delle estensioni diverse

protected void pulizia(String path,Map<String,Brano> playlist){
	
	if(playlist==null || playlist.size()==0) return;
	
	File cartellaDestinazione=new File(path);
	
	Map<String,Brano> temporanea;
	
	
	if(!(cartellaDestinazione.isDirectory() && cartellaDestinazione.exists())) return;
	
	temporanea=playlist;
	
	File listaFile[];
	
	listaFile=cartellaDestinazione.listFiles(new FileFilterSoloFile());
	
	int numeroMax=0; //massimo dei file che dobbiamo copiare
	
	for(String s : temporanea.keySet()){
		String numero=s.split("-")[0];
		numeroMax= Integer.parseInt(numero)>numeroMax ? Integer.parseInt(numero) : numeroMax;
	}
	
	for(File f : listaFile){
		String numeroFileStringa=null;
		int numeroFile;
		try{
			numeroFileStringa=f.getName().split("-")[0];
			numeroFile=Integer.parseInt(numeroFileStringa);
		}catch(Exception e){
			numeroFile=-1;
		}
		if(numeroFile>numeroMax || numeroFile==-1){
			f.delete();
		}
	}
	
	
}//fine metodo

}
