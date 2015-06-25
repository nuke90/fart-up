package ParserLibreria;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringEscapeUtils; //importazione apache commons
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ConcreteHandlerITunes extends HandlerITunesLibrary{

private StringBuilder buffer;
private boolean name,chiave,location,stringa,trackID,integ,playlists,artist,size;
private int contaArray=0;
private int contaBrani=0;
private Brano provv=null;
private List<Integer> listaProvv=null;
private String nomeListaProvv=null;

NavigableMap<Integer,Brano> brani; //lista completa dei brani, la key è il numero di key del brano.

NavigableMap<String,List<Integer>> playlist; //lista che associa il nome della playlist a una lista di chiavi del brano.

	public ConcreteHandlerITunes(){
		super();
		buffer=new StringBuilder(); //dove mettiamo i caratteri in modo provvisorio
		name=false; //vero quando incontriamo <key>Name</key>
		chiave=false; //vero quando incontriamo <key>
		stringa=false; //vero quando incontriamo <String>
		location=false;  //vero quando <key>Location</key>
		trackID=false; //vero quando <key>Track ID</key>
		artist=false;
		size=false;
		integ=false; //vero quando incontriamo <integer>
		playlists=false; //vero quando <key>Playlists</key>, la zona in cui vi sono i dati delle playlist
		brani=new TreeMap<Integer, Brano>();
		playlist=new TreeMap<String, List<Integer>>();
	}
	
	public void startElement(String uri, String localName,String qName, Attributes attributes) throws SAXException {
		//System.out.println(qName);
		if(qName.compareToIgnoreCase("key")==0) chiave=true;
		if(qName.compareToIgnoreCase("string")==0) stringa=true;
		if(qName.compareToIgnoreCase("integer")==0) integ=true;
		if(qName.compareToIgnoreCase("array")==0) contaArray++;
	}
		
	public void endElement(java.lang.String uri, java.lang.String localName, java.lang.String qName){
		if(qName.compareToIgnoreCase("key")==0) chiave=false;
		if(qName.compareToIgnoreCase("string")==0) {
			//qui dobbiamo anche chiudere il buffer di character
			
			
			if(stringa==true && name==true && !playlists ){
				//System.out.println("Titolo: "+contenuto);
				name=false;
				provv.name=buffer.toString();
				contaBrani++; //va spostato nella chiusura
			}
			
			if(stringa==true && artist==true && !playlists ){
				artist=false;
				provv.artist=buffer.toString();
			}
			
			if(stringa==true && location==true && !playlists ){
				//System.out.println("Location: "+parseLocation(contenuto));
				//System.out.println(contenuto);
				String provvisoria;
				provvisoria=buffer.toString();
				provv.location=parseLocation(provvisoria);
				brani.put(provv.trackID, provv);
				location=false;
			}
			
			//così elenchiamo tutta la musica, adesso dobbiamo dividere le playlist

			//parte per il parsing delle playlist
			
			if(stringa==true && name==true && playlists ){
				//System.out.println("Titolo Playlist: "+contenuto);

				nomeListaProvv=buffer.toString();
				listaProvv=new ArrayList<Integer>();
				name=false;
				//siccome passiamo di qui tutte le volte che apriamo una playlist, ci vuole un 
				
			}
			stringa=false;
			buffer=new StringBuilder();
		}
		if(qName.compareToIgnoreCase("integer")==0) integ=false;
		if(qName.compareToIgnoreCase("plist")==0) playlists=false; // a quanto pare finisce così....
		if(qName.compareToIgnoreCase("array")==0) contaArray--;
		
		//quando contaArray rimane con una sola istanza e siamo dentro playlist, vuol dire che abbiamo chiuso 1 item
		
		if(contaArray==1 && qName.compareToIgnoreCase("array")==0){
			playlist.put(nomeListaProvv,listaProvv);
		}
		
	}
	
	public void characters(char[] ch, int start, int length){
		String contenuto=new String(ch,start,length);
		if(chiave){
			if(contenuto.compareTo("Track ID")==0){
				//dopo questo name c'è uno string, dobbiamo catturare quello
				//System.out.println(cont);
				trackID=true;
			}
			if(contenuto.compareTo("Name")==0){
				//dopo questo name c'è uno string, dobbiamo catturare quello
				//System.out.println(cont);
				name=true;
			}
			if(contenuto.compareTo("Artist")==0){
				//dopo questo name c'è uno string, dobbiamo catturare quello
				//System.out.println(cont);
				artist=true;
			}
			if(contenuto.compareTo("Size")==0){
				size=true;
			}
			if(contenuto.compareTo("Location")==0){
				//dopo questo name c'è uno string, dobbiamo catturare quello
				//System.out.println(cont);
				location=true;
			}
			if(contenuto.compareTo("Playlists")==0){
				//dopo questo name c'è uno string, dobbiamo catturare quello
				//System.out.println(cont);
				playlists=true;
			}
		}
		
		if(integ==true && trackID==true && !playlists ){
			//System.out.println("Track ID: "+contenuto);
			provv=new Brano();
			provv.trackID=Integer.parseInt(contenuto);
			trackID=false;
		}
		
		if(integ==true && size==true && !playlists ){
			size=false;
			try{
				provv.size=Integer.parseInt(contenuto);
			}
			catch(NumberFormatException e){
				provv.size=0;
			}
		}
		
		//dobbiamo trovare il modo di fare una lista di Track ID per ogni playlist
		
		if(integ==true && trackID==true && playlists ){
			//System.out.println("Track ID: "+contenuto);
			listaProvv.add(Integer.parseInt(contenuto));
			trackID=false;
		}
		
		//-------------PARTE DELLE STRINGHE
		if(stringa==true && name==true && !playlists ){
			//System.out.println("Titolo: "+contenuto);
			buffer.append(contenuto);

		}
		
		if(stringa==true && artist==true && !playlists ){
			buffer.append(contenuto);
		}
		
		if(stringa==true && location==true && !playlists ){
			//System.out.println("Location: "+parseLocation(contenuto));
			//System.out.println(contenuto);
			//contenuto=formattaTesto(contenuto);
			//String s = new String(ch);
			//System.out.println(s);
			buffer.append(contenuto);
			//parte che va nella chiusura

		}
		
		//così elenchiamo tutta la musica, adesso dobbiamo dividere le playlist

		//parte per il parsing delle playlist
		
		if(stringa==true && name==true && playlists ){
			//System.out.println("Titolo Playlist: "+contenuto);
			buffer.append(contenuto);
			
			//siccome passiamo di qui tutte le volte che apriamo una playlist, ci vuole un 
			
		}
		//-----------FINE PARTE DELLE STRINGHE
		
	}
	@Override
	public void error(SAXParseException arg0) throws SAXException {
		
		
	}

	@Override
	public void unparsedEntityDecl(String arg0, String arg1, String arg2,
			String arg3) throws SAXException {
		
		
	}

	@Override
	public void warning(SAXParseException arg0) throws SAXException {
		
		
	}

	public void endDocument(){
		System.out.println("numero di brani:"+contaBrani);


	}
	
	public String parseLocation(String loc){
		String finita=null;
		if(loc.matches("^file://localhost/.+")){
			finita=loc.split("^file://localhost/")[1];
//			finita=finita.replaceAll("%20", " ");
		}
		
		if(loc.matches("^http://.+")){
			finita=loc.replaceAll("%20", " ");
		}
		finita=formattaTesto(finita);
		
		
		return finita;
	}
	
	public String formattaTesto(String testo){
		//return 
		String unescapedXML=StringEscapeUtils.unescapeXml(testo);
		String out=unescapedXML;
		try {
			out=URLDecoder.decode(out,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}

		return out;
	}
	
	public NavigableMap<Integer, Brano> getBrani(){
		//System.out.println("ok");
		return this.brani;
	}
	
	public NavigableMap<String, List<Integer>> getPlaylist(){
		return playlist;
	}
	
}
