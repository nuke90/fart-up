package ParserLibreria;
import java.util.List;
import java.util.NavigableMap;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


public abstract class HandlerITunesLibrary extends org.xml.sax.helpers.DefaultHandler {


	@Override
	public abstract void error(SAXParseException arg0) throws SAXException;

	@Override
	public abstract void unparsedEntityDecl(String arg0, String arg1, String arg2,String arg3) throws SAXException;

	@Override
	public abstract void warning(SAXParseException arg0) throws SAXException;

	public abstract void characters(char[] arg0, int arg1, int arg2) throws SAXException;

	@Override
	public abstract void endDocument() throws SAXException;

	@Override
	public abstract void endElement(String arg0, String arg1, String arg2);

	@Override
	public abstract void startElement(String arg0, String arg1, String arg2,Attributes arg3) throws SAXException;

	public abstract NavigableMap<Integer, Brano> getBrani();
	
	public abstract NavigableMap<String, List<Integer>> getPlaylist();
}
