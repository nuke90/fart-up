package lpad;

import java.util.*;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
































import lpad.features.FactorNames;
import lpad.features.FactorTypes;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.expression.OWLEntityChecker;
import org.semanticweb.owlapi.expression.ParserException;
import org.semanticweb.owlapi.expression.ShortFormEntityChecker;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLNamedObjectVisitor;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.BidirectionalShortFormProvider;
import org.semanticweb.owlapi.util.BidirectionalShortFormProviderAdapter;
import org.semanticweb.owlapi.util.OWLClassExpressionCollector;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleIRIMapper;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.QueryBinding;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import de.derivo.sparqldlapi.impl.QueryEngineImpl;
import de.derivo.sparqldlapi.types.QueryArgumentType;
import uk.ac.manchester.cs.owl.owlapi.OWLClassExpressionImpl;


/**
 * 
 * @author Flavio
 *
 */
public class MyReader implements OWLReader{
	/**
	 * Returns null when there's some kind of error
	 */
	public RiskFactorsData read(Reader reader) {
		
		OWLOntology ontology=null;
		Set<OWLClass> classes=null;
		Set<OWLAnonymousIndividual> individuals=null;
		OWLReasoner reasoner=null;		
		String iri="http://www.unibo.it/fallrisk";

		
		OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
		OWLOntologyDocumentSource docSource=new ReaderDocumentSource(reader);
		
		try {
			ontology=manager.loadOntologyFromOntologyDocument(docSource);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			System.err.println("Error when trying to load the document from the source");
			return null;
		}
		
		
		//creazione Reasoner
		//configurazione del reasoner
		Configuration conf=new Configuration();
		//questo server perché il datatype reversible non è supportato da OWL 2, per farlo funzionare ci vuole questa configurazione
		conf.ignoreUnsupportedDatatypes=true;
		
		reasoner=new Reasoner(conf, ontology);
//		reasoner = new Reasoner(ontology);
		if(reasoner==null) return null;
		
		
		
		QueryResult res;
		
//		res=sdQuery(manager, reasoner, "PREFIX fa: <"+iri+"#> SELECT ?p ?x ?r WHERE { ?etType(?x,fa:OWLClass_a924e8db_a89c_4098_9259_0bbb77acfc04), PropertyValue(?p, fa:isRiskFactorIn, ?x), PropertyValue(?x,fa:OddsRatio,?r)}");
		
		
//		res=sdQuery(manager, reasoner, "PREFIX fa: <"+iri+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?x WHERE { Annotation(?p,rdf:label,\"RiskSettingORRef\"),Type(?k,?p),PropertyValue(?x,fa:isRiskFactorIn,?k)}");
		
		
		
//		System.out.println(res);
//		System.out.println("FINE QUERY SPARQL - DL");
		//STAMPIAMO LE CLASSI
		
//		printClasse(ontology);
		
		
		//parte in cui controlliamo i dati veri e propri
		//usiamo RiskFactorDataBuilder
		
		RiskFactorsDataBuilder b=new RiskFactorsDataBuilder();
        
		
		//per prima cosa tiriamo fuori quali sono gli oddsratio in relazione al reference
		
		/*
		 * 			for (QueryArgument arg : queryBinding.getBoundArgs()) {
						System.out.println(arg.getValue()+arg.getType());
					}
		 * 
		 */
		
		//contiene gli URI e i rispettivi OddsRatio collegati
		Map<String,Double> RiskSettingORRef=new HashMap<>();
		List<String> RiskFactors=new ArrayList<>();
		Map<String,List<String>> RiskSettingORRiskFactor=new HashMap<>();
		Map<String,List<String>> RiskSettingORRiskFactorScalar=new HashMap<>();
		Map<String,List<String>> RiskSettingORRiskFactorTernary=new HashMap<>();
		Map<String,List<String>> RiskSettingORRiskFactorFunctional=new HashMap<>();
		Map<String,List<String>> RiskSettingORRiskFactorComorbidity=new HashMap<>();
		Map<String,List<String>> ComorbidityToSinergicFTF=new HashMap<>();
		
		String reference="Deandrea2010";
		
		/*
		 * 
		 * RiskSettingORRef è collegato da un'annotazione alla classe vera e propria che ha un nome diverso
		 * QUI LI ESTRAIAMO
		 */
		
		
		//versione che considera la reference
//		res=sdQuery(manager, reasoner, "PREFIX fa: <"+iri+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?k WHERE { Annotation(?p,rdf:label,\"RiskSettingORRef\"),Type(?k,?p),PropertyValue(?k,fa:hasReference,fa:"+reference+")}");
		
		//questa versione non considera la reference
		res=sdQuery(manager, reasoner, "PREFIX fa: <"+iri+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?k ?d WHERE { Annotation(?p,rdf:label,\"RiskSettingORRef\"),Type(?k,?p),PropertyValue(?k,fa:OddsRatio,?d)}");
		
		System.out.println(res);
		for (QueryBinding queryBinding : res) {
			
			String rf=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).toString();
			Double value=null;
			try{
				value=Double.parseDouble(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "d")).toString());
			}
			catch(NumberFormatException e)
			{
				value=null;
			}
			RiskSettingORRef.put(rf, value);
			RiskSettingORRiskFactor.put(rf,new ArrayList<>());
			RiskSettingORRiskFactorScalar.put(rf,new ArrayList<>());
			RiskSettingORRiskFactorTernary.put(rf, new ArrayList<>());
			RiskSettingORRiskFactorFunctional.put(rf, new ArrayList<>());
			RiskSettingORRiskFactorComorbidity.put(rf, new ArrayList<>());
		}
		
		
		//adesso abbiamo una lista di ORRef collegati a dei RiskFactor, troviamo quei RiskFactor
		
		//dobbiamo determinare che tipo di riskFactor è, se Ternary, Scalar o Synergy
		String query;
		String prefissi="PREFIX fa: <"+iri+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> ";
		
		for (String string : RiskSettingORRef.keySet()) {
					
			query=prefissi+"SELECT ?k WHERE {PropertyValue(<"+string+">,fa:hasRiskFactor,?k)}";
			res=sdQuery(manager, reasoner, query);
			System.out.println(res);
			for (QueryBinding queryBinding : res) {
				String value=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).getValue();
				RiskSettingORRiskFactor.get(string).add(value);
				RiskFactors.add(value);
				System.out.println(string+"   "+value);
			}
			
		}
		
		/*
		 * 
		 * 
		 * CONTROLLO SCALAR
		 * 
		 * 
		 * 
		 */
		
		for (String string : RiskFactors) {
			
			query=prefissi+"SELECT ?k WHERE {PropertyValue(<"+string+">,fa:IsScalarRiskFactorIn,?k)}";
//			query=prefissi+"SELECT ?x ?k WHERE {PropertyValue(<"+string+">,?x,?k)}";
			res=sdQuery(manager, reasoner, query);
			System.out.println(res);
			for (QueryBinding queryBinding : res) {
				String value=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).getValue();
				
				if(RiskSettingORRef.containsKey(new String(value))){
					RiskSettingORRiskFactorScalar.get(value).add(string);
					System.out.println("scala:"+value+"   "+string);
				}
				
				
			}
			
		}
		
		
		/*
		 * 
		 * 
		 * CONTROLLO TERNARY
		 * 
		 * 
		 * 
		 */
		
		for (String string : RiskFactors) {
			
			query=prefissi+"SELECT ?k WHERE {PropertyValue(<"+string+">,fa:IsTernaryRiskFactorIn,?k)}";
//			query=prefissi+"SELECT ?x ?k WHERE {PropertyValue(<"+string+">,?x,?k)}";
			res=sdQuery(manager, reasoner, query);
			System.out.println(res);
			for (QueryBinding queryBinding : res) {
				String value=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).getValue();
				
				if(RiskSettingORRef.containsKey(new String(value))){
					RiskSettingORRiskFactorTernary.get(value).add(string);
					System.out.println("scala:"+value+"   "+string);
				}
				
				
			}
			
		}
		
		/*
		 * 
		 * 
		 * CONTROLLO FUNCTIONAL
		 * 
		 * 
		 * 
		 */
		
		for (String string : RiskFactors) {
			
			query=prefissi+"SELECT ?k WHERE {PropertyValue(<"+string+">,fa:isRiskFactorFunctionalIn,?k)}";
//			query=prefissi+"SELECT ?x ?k WHERE {PropertyValue(<"+string+">,?x,?k)}";
			res=sdQuery(manager, reasoner, query);
			System.out.println(res);
			for (QueryBinding queryBinding : res) {
				String value=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).getValue();
				
				if(RiskSettingORRef.containsKey(new String(value))){
					RiskSettingORRiskFactorFunctional.get(value).add(string);
					System.out.println("scala:"+value+"   "+string);
				}
				
				
			}
			
		}
		
		
		/*
		 * 
		 * 
		 * CONTROLLO COMORBIDITY
		 * 
		 * 
		 * 
		 */
		
		for (String string : RiskFactors) {
			
			query=prefissi+"SELECT ?k WHERE {PropertyValue(<"+string+">,fa:isRiskFactorIn,?k),PropertyValue(<"+string+">,fa:hasRiskFactorType,fa:SinergyRiskFactorType)}";
//			query=prefissi+"SELECT ?x ?k WHERE {PropertyValue(<"+string+">,?x,?k)}";
			res=sdQuery(manager, reasoner, query);
			System.out.println(res);
			for (QueryBinding queryBinding : res) {
				String value=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).getValue();
				
				if(RiskSettingORRef.containsKey(new String(value))){
					RiskSettingORRiskFactorComorbidity.get(value).add(string);
					ComorbidityToSinergicFTF.put(string, new ArrayList<>());
					System.out.println("scala:"+value+"   "+string);
				}
				
				
			}
			
		}
		
		
		/*
		 * Associamo le parti sinergiche e associamole al ComorbidityRiskFactor
		 * 
		 */
		
		for (String string : ComorbidityToSinergicFTF.keySet()){
			query=prefissi+"SELECT ?r "
					+ "WHERE {Type(?k,fa:SinergicFactorsToFactor),"
					+ "PropertyValue(?k,fa:isRiskFactorTypeOf,<"+string+">),"
					+ "PropertyValue(?k,fa:hasSinergicRiskFactor,?r)}";
			res=sdQuery(manager, reasoner, query);
			System.out.println(res);
		}
		
		/*
		 * 
		 * Adesso dobbiamo collegare a ogni RiskFactor di tipo SinergyRiskFactorType i RiskFactor che concorrono nella creazione
		 * 
		 */
		
		
		/*
		 * 
		 * A questo punto abbiamo delle liste che contengono i RiskFactor e li collegano agli odds ratio, anche sapendo di che tipo sono
		 * 
		 */
				
		System.out.println("ABBIAMO DELLE LISTE POPOLATE DA VARIE COSE, TERNARY E SCALAR SOPRATUTTO");
		
		System.out.println("SCALAR");
		
		for (String string : RiskSettingORRef.keySet()) {
			System.out.println(string);
			for(String stringa:RiskSettingORRiskFactorScalar.get(string)){
				System.out.println("---"+stringa);
			}
		}
		
		System.out.println("TERNARY");
		
		for (String string : RiskSettingORRef.keySet()) {
			System.out.println(string);
			for(String stringa:RiskSettingORRiskFactorTernary.get(string)){
				System.out.println("---"+stringa);
			}
		}
		
		System.out.println("FUNCTIONAL");
		
		for (String string : RiskSettingORRef.keySet()) {
			System.out.println(string);
			for(String stringa:RiskSettingORRiskFactorFunctional.get(string)){
				System.out.println("---"+stringa);
			}
		}
		
		System.out.println("COMORBIDITY");
		
		for (String string : RiskSettingORRef.keySet()) {
			System.out.println(string);
			for(String stringa:RiskSettingORRiskFactorComorbidity.get(string)){
				System.out.println("---"+stringa);
			}
		}
		
		
		
	
		
		
		//otteniamo un set di RiskFactor collegati ai RiskFactorCommunityORRef
		
		
		
		
		/*
        NodeSet<OWLNamedIndividual> riskFactors=getInstancesFromQuery(ontology, reasoner, "hasReference some Reference");
        
        if(riskFactors==null) return null;
        
        for(Node<OWLNamedIndividual> node:riskFactors){
        	for(OWLNamedIndividual ind:node){
        		System.out.println(ind);
        		getIndividualDataProperties(ontology, ind);
        		Set<OWLLiteral> lit=reasoner.getDataPropertyValues(ind, getSpecifiedDataProperty(ontology, "Odds"));
        		System.out.println(getSpecifiedDataPropertyString(ontology, reasoner, ind, "Odds"));
        		System.out.println("ciao");
        	}
        }
        
        System.out.println(getSpecifiedDataProperty(ontology, "Odds"));
        
        BufferedInputStream bin=new BufferedInputStream(System.in);
        BufferedReader bread=new BufferedReader(new InputStreamReader(System.in));
        
        for(;;){
        	System.out.println("Inserisci la tua query:");
        	String query;
        	try {
				if((query=bread.readLine())!=null){
					printInstancesQuery(ontology, reasoner, query);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
        	
        }
        */
        
        return null;
	}//fine read
	
	/**
	 * This function prints the instances that the query on the ontology retrieves.
	 * 
	 * @param ontology Ontology on which we want to search
	 * @param reasoner 
	 * @param query The query in Manchester DL Syntax
	 * @throws InterruptedException 
	 */
	public void printInstancesQuery(OWLOntology ontology,OWLReasoner reasoner,String query){
		
	    System.out.println("----------------------------------------------");
	    System.out.println("QUERY:"+query);
	    System.out.println("-----------------RESULTS----------------------");
	    
	    NodeSet<OWLNamedIndividual> instanze;
	    
	    instanze=getInstancesFromQuery(ontology, reasoner, query);
	    
	    if(!(instanze==null)){	    
		    for(Node<OWLNamedIndividual> n:instanze){
		    	for (OWLNamedIndividual owlNamedIndividual : n) {
					System.out.println(owlNamedIndividual.toStringID());
				}
		    }
	    }
	    else{
	    	System.out.println("!!!!!NO RESULT!!!!!");
	    }
	    System.out.println("---------------END RESULTS--------------------");
	    
	}
	
	/**
	 * This function gives back the instances of the specified DL Query, the instances don't
	 * have to be necessarily direct relatives of the class
	 * @param ontology
	 * @param reasoner
	 * @param query
	 * @return
	 */
	public NodeSet<OWLNamedIndividual> getInstancesFromQuery(OWLOntology ontology,OWLReasoner reasoner,String query){
		
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		
		DLQueryParser parser=new DLQueryParser(ontology, shortFormProvider);
	
	    OWLClassExpression clex=parser.parseClassExpression(query);
	    
	    if(clex==null) return null;
	    
	    return reasoner.getInstances(clex, false);
    }
    
	private String esempio(Reader reader){
		OWLOntology ontology=null;
		Set<OWLClass> classes=null;
		Set<OWLAnonymousIndividual> individuals=null;
		OWLReasoner reasoner=null;		
		String iri="http://www.unibo.it/fallrisk";
	
		
		OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
		OWLOntologyDocumentSource docSource=new ReaderDocumentSource(reader);
		
		try {
			ontology=manager.loadOntologyFromOntologyDocument(docSource);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			System.err.println("Error when trying to load the document from the source");
			return null;
		}
		
		
		
		
		
		reasoner = new Reasoner(ontology);
		if(reasoner==null) return null;
		
		//TODO adesso dobbiamo leggere i riskfactor e creare usando il databuilder?
		
		classes=ontology.getClassesInSignature();
		
		OWLClass riskFactor=null;
		NodeSet<OWLNamedIndividual> riskFactorInstancesNodes=null;
		Set<OWLNamedIndividual> riskFactorInstances=new HashSet<OWLNamedIndividual>();
		
		for (OWLClass clas : classes) {
			if(clas.toStringID().compareTo(iri+"#RiskFactor")==0) riskFactor=clas;
		}
		
		if(riskFactor==null){
			System.err.println("Impossible to find RiskFactor class");
			return null;
		}
		
		riskFactorInstancesNodes=reasoner.getInstances(riskFactor, false);
		
		
		//TODO ELIMINARE I PRINT DI CONTROLLO
		for (Node<OWLNamedIndividual> node : riskFactorInstancesNodes) {
			System.out.println("nodo:");
			for (OWLNamedIndividual owlNI : node) {
				System.out.println("----"+owlNI.toStringID());
				riskFactorInstances.add(owlNI);
			}
		}
		
		/*
		 * adesso abbiamo tutte le istanze della classe riskFactor, anche quelle che non sono derivate direttamente da riskfactor
		 * 
		 */
		
		
		
		for (OWLNamedIndividual owlNamedIndividual : riskFactorInstances) {
			System.out.println("istanza:"+owlNamedIndividual.toStringID());
			for(OWLObjectPropertyAssertionAxiom prop: ontology.getObjectPropertyAssertionAxioms(owlNamedIndividual)){
				System.out.println(prop.getProperty());
				
				//TODO ELIMINARE LA STAMPA
			}
		}
		
		/*
		 * a questo punto abbiamo tutte le classi in classes e tutte le instanze di riskFactor in riskFactorInstances
		 * 
		 */
		
		
		//TODO come procediamo?
	
		
		
		//SparqlOwlParser par= SparqlOwlParser.
		
		//proviamo la query con hermit
		
		Reasoner r=(Reasoner) reasoner;
		
		ShortFormProvider shortFormProvider = new SimpleShortFormProvider();
		
		//parte in cui proviamo con le dl query
		String classExpressionString="RiskFactor and hasReversibility some Reversible";
		OWLDataFactory dataFactory = ontology.getOWLOntologyManager()
	            .getOWLDataFactory();
	    // Set up the real parser
	    DLQueryParser parser=new DLQueryParser(ontology, shortFormProvider);
	    
	    OWLClassExpression clex=parser.parseClassExpression(classExpressionString);
	    
	    for(Node<OWLNamedIndividual> n:reasoner.getInstances(clex, false)){
	    	for (OWLNamedIndividual owlNamedIndividual : n) {
				System.out.println(":"+owlNamedIndividual.toStringID());
			}
	    }
	
	    reasoner = new Reasoner(ontology);
		if(reasoner==null) return null;
	    
	    printInstancesQuery(ontology, reasoner, "RiskFactor and Reversibility value \"SubjectSpecific\"");
	    
	    printInstancesQuery(ontology, reasoner, "hasReference value Deandrea2010");
	    
	    return null;
	}//fine esempio()
	
	/**
	 * This function gives the dataproperty assertions of an individual
	 * 
	 * @TODO bisogna fare una funzione che data la assertion dia il valore, e che magari verifichi di che tipo è il valore
	 * 
	 * @param ontology
	 * @param ind
	 * @return
	 */
	public Set<OWLDataPropertyAssertionAxiom> getIndividualDataProperties(OWLOntology ontology,OWLNamedIndividual ind){
		Set<OWLDataPropertyAssertionAxiom> dpaxs;
		dpaxs=ontology.getDataPropertyAssertionAxioms(ind);
		ontology.getDataPropertiesInSignature(true);
		for(OWLDataPropertyAssertionAxiom dpax : dpaxs){
			OWLDataPropertyExpression ex=dpax.getProperty();
			Set<OWLLiteral> values=ind.getDataPropertyValues(ex,ontology);
			System.out.println("---"+ex);
			for(OWLLiteral value:values){
				System.out.println("---"+value.getLiteral());
			}
		}
		return null;
	}
	/**
	 * Returns the OWLDataProperty that satisfy the name given as a parameter
	 * @param ontology
	 * @param name the name of the OWLDataProperty we want to search for
	 * @return the OWLDataProperty or null
	 */
	public OWLDataProperty getSpecifiedDataProperty(OWLOntology ontology,String name){
		Set<OWLDataProperty> properties;
		OWLDataProperty prop=null;
		properties=ontology.getDataPropertiesInSignature();
		for(OWLDataProperty p: properties){
			if(p.toStringID().matches(".+"+name+".*")){
				prop=p;
			}
		}
		return prop;
	}
	
	/**
	 * Ritorna il valore in stringa della Data property che si specifica in name e sull'individuo, altrimenti null
	 * 
	 * @param ontology L'ontologia in cui si cerca la proprietà name
	 * @param reasoner Il reasoner su quella ontologia
	 * @param ind L'individual
	 * @param name Il nome della Data Property(meglio metterlo univoco)
	 * @return valore in stringa della Data property che si specifica in name e sull'individuo, altrimenti null
	 */
	public String getSpecifiedDataPropertyString(OWLOntology ontology,OWLReasoner reasoner,OWLNamedIndividual ind,String name){
		String value=null;
		Set<OWLLiteral> values=reasoner.getDataPropertyValues(ind,getSpecifiedDataProperty(ontology, name));
		
		for(OWLLiteral lit:values){
			value=lit.getLiteral();
		}
		
		return value;
	}

	public void printClasse(OWLOntology ontology){
		Set<OWLClass> classi;
		
		classi=ontology.getClassesInSignature();
		
		for(OWLClass cl:classi){
			System.out.println(cl.toStringID());
		}
	}
	
	
	public QueryResult sdQuery(OWLOntologyManager manager,OWLReasoner reasoner,String query){
		Query q=null;
		
		QueryEngine qEn=new QueryEngineImpl(manager, reasoner);
		
		QueryResult res=null;
//		query="PREFIX fa: <"+iri+"#> SELECT ?p ?x ?r WHERE { Type(?x,fa:OWLClass_a924e8db_a89c_4098_9259_0bbb77acfc04), PropertyValue(?p, fa:isRiskFactorIn, ?x), PropertyValue(?x,fa:OddsRatio,?r)}";
		System.out.println(query);
		try {
			q=Query.create(query);
			res=qEn.execute(q);
			return res;
		} catch (QueryParserException e1) {
			// TODO Auto-generated catch block
			System.err.println("PROBLEMA NELLA CREAZIONE DELLA QUERY");
			e1.printStackTrace();
		} catch (QueryEngineException e) {
			// TODO Auto-generated catch block
			System.err.println("PROBLEMA NELL'ESECUZIONE DELLA QUERY");
			e.printStackTrace();
		}
		return null;
	}//fine sdQuery
	
}//fine class


