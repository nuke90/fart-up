package lpad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import lpad.features.Factor;
import lpad.features.FactorSpaceBuilder;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;

import sample.feature.FeatureSpace;
import util.stringFunction.NoWhiteLowerCase;
import de.derivo.sparqldlapi.Query;
import de.derivo.sparqldlapi.QueryArgument;
import de.derivo.sparqldlapi.QueryBinding;
import de.derivo.sparqldlapi.QueryEngine;
import de.derivo.sparqldlapi.QueryResult;
import de.derivo.sparqldlapi.exceptions.QueryEngineException;
import de.derivo.sparqldlapi.exceptions.QueryParserException;
import de.derivo.sparqldlapi.impl.QueryEngineImpl;
import de.derivo.sparqldlapi.types.QueryArgumentType;


/**
 * 
 * @author Flavio Zuffa
 *
 */
public class MyReader implements OWLReader{
	/**
	 * Returns null when there's some kind of error
	 */
	public RiskFactorsData read(Reader reader) {
		
		OWLOntology ontology=null;
//		Set<OWLClass> classes=null;
//		Set<OWLAnonymousIndividual> individuals=null;
		OWLReasoner reasoner=null;		
		String ontologyURI="http://www.unibo.it/fallrisk";

		
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
//		if(reasoner==null) return null;
		
		
		
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
		Map<String,List<RiskFactorI>> RiskSettingORRiskFactors=new HashMap<>();
		
		@SuppressWarnings("unused")
		String reference="Deandrea2010";
		
		/*
		 * 
		 * RiskSettingORRef è collegato da un'annotazione alla classe vera e propria che ha un nome diverso
		 * QUI LI ESTRAIAMO
		 */
		
		
		//versione che considera la reference
//		res=sdQuery(manager, reasoner, "PREFIX fa: <"+iri+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> SELECT ?k WHERE { Annotation(?p,rdf:label,\"RiskSettingORRef\"),Type(?k,?p),PropertyValue(?k,fa:hasReference,fa:"+reference+")}");
		
		//questa versione non considera la reference
		res=sdQuery(manager, reasoner, "PREFIX fa: <"+ontologyURI+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> "
				+ "SELECT ?k ?d WHERE { "
				+ "Annotation(?p,rdf:label,\"RiskSettingORRef\")"
				+ ",Type(?k,?p)"
				+ ",PropertyValue(?k,fa:OddsRatio,?d)"
				+ ",PropertyValue(?k,fa:hasReference,fa:Deandrea2010)"
				+ ",PropertyValue(?k,fa:hasRisk,fa:FallIn1Year)"
				+ "}");
		
		System.out.println(res);
		for (QueryBinding queryBinding : res) {
			
			String riskSettingORURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).toString();
			Double value=null;
			
			try{
				
				String str=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "d")).toString().replaceAll("\"", "");
				value=Double.valueOf(str);
			
			}
			catch(NumberFormatException e)
			{
				
				value=0d;
				
			}
			
			RiskSettingORRef.put(riskSettingORURI, value);
			RiskSettingORRiskFactors.put(riskSettingORURI,new ArrayList<>());

		}
		
		
		//adesso abbiamo una lista di ORRef collegati a dei RiskFactor, troviamo quei RiskFactor
		
		//dobbiamo determinare che tipo di riskFactor è, se Ternary, Scalar o Synergy
		String query=null;
		String prefissi="PREFIX fa: <"+ontologyURI+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> ";
		
		for (String string : RiskSettingORRef.keySet()) {
					
			query=prefissi+"SELECT ?k WHERE {PropertyValue(<"+string+">,fa:hasRiskFactor,?k)}";
			res=sdQuery(manager, reasoner, query);
			System.out.println(res);
			
			for (QueryBinding queryBinding : res) {
			
				String value=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).getValue();
				RiskFactors.add(value);
				System.out.println(string+"   "+value);
				
			}
			
		}
		
//		provaQuery(manager, reasoner);
		
		
		for (String string : RiskFactors) {
			
			//facciamo un controllo unico per ogni RiskFactor
			query=prefissi+"SELECT DISTINCT ?k ?o ?f WHERE {"
					+ "PropertyValue(<"+string+">,fa:isRiskFactorIn,?k)"
					+ ",PropertyValue(<"+string+">,fa:hasReversibility,?f)"
					+ ",PropertyValue(<"+string+">,fa:hasRiskFactorType,?j)"
					+ ",DirectSubClassOf(?o,fa:RiskFactorType)"
					+ ",Type(?j,?o)"
					+ "}";

			res=sdQuery(manager, reasoner, query);

			for (QueryBinding queryBinding : res) {
				
				String riskSettingRefOR=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "k")).getValue();
				String type=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "o")).getValue();
				String reversibility=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "f")).getValue();
			
				if(RiskSettingORRef.containsKey(new String(riskSettingRefOR))){
				
					RiskSettingORRiskFactors.get(riskSettingRefOR).add(new RiskFactorImpl(string, parseRiskFactorType(type),parseReversibility(reversibility)));
					System.out.println("scala:"+riskSettingRefOR+"   "+string);
				
				}
			
			}
			
			
		}

		
		
		/*
		 * Associamo le parti sinergiche e associamole al ComorbidityRiskFactor
		 * 
		 */
		
		for (List<RiskFactorI> lista : RiskSettingORRiskFactors.values()){
			for(RiskFactorI riskFactor : lista){
				if(riskFactor.getType()==RiskFactorType.SINERGY){
					query=prefissi+"SELECT DISTINCT ?r ?o ?f "
							+ "WHERE {Type(?k,fa:SinergicFactorsToFactor),"
							+ "PropertyValue(?k,fa:isRiskFactorTypeOf,<"+riskFactor.getURI()+">)"
							+ ",PropertyValue(?k,fa:hasSinergicRiskFactor,?r)"
							+ ",PropertyValue(?r,fa:hasReversibility,?f)"
							+ ",PropertyValue(?r,fa:hasRiskFactorType,?j)"
							+ ",DirectSubClassOf(?o,fa:RiskFactorType),Type(?j,?o)"
							+ "}";
					res=sdQuery(manager, reasoner, query);
					System.out.println(res);
					for (QueryBinding queryBinding : res) {
						String uri=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "r")).getValue();
						String type=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "o")).getValue();
						String reversibility=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "f")).getValue();
						
						riskFactor.addSinergyRiskFactor(new RiskFactorImpl(uri, parseRiskFactorType(type),parseReversibility(reversibility)));
					}
				}//fine if
			}
		}
		
		/*
		 * IN QUESTA PARTE ASSOCIAMO GLI ESTIMATOR AI RISK FACTOR(il collegamento viene dedotto usando le istanze di EstimatorsToFactor che hanno come requisiti
		 * quello di avere un RiskFactor e un Estimator(possono avere tipi diversi, sia di ternary che di scalar)
		 * 
		 */
		
		System.out.println("///////////////////////////////RICERCA ESTIMATOR");
		
		for (List<RiskFactorI> lista : RiskSettingORRiskFactors.values()){
		
			for(RiskFactorI riskFactor : lista){
				
				if(riskFactor.getType()==RiskFactorType.SCALAR || riskFactor.getType()==RiskFactorType.TERNARY){

					//qui abbiamo tutti i riskfactor scalar e anche i ternary, a cui associare gli estimator
					
					query=prefissi+"SELECT DISTINCT ?r ?f WHERE{"
							+ "Type(?k,fa:EstimatorsToFactor)"
							+ ",PropertyValue(?k,fa:hasRiskFactor,<"+riskFactor.getURI()+">)"
							+ ",PropertyValue(?k,fa:hasEstimator,?r)"
							+ ",Type(?r,?f)"
							+ ",DirectSubClassOf(?f,fa:Estimator)"
							+ "}";
					
					res=sdQuery(manager, reasoner, query);
					
					for (QueryBinding queryBinding : res) {
						
						String URI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "r")).getValue();
						String type=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "f")).getValue();
						
						EstimatorI estimator=new EstimatorImpl(URI,parseEstimatorType(type));
						QueryResult res1;
						//adesso che abbiamo l'estimator dobbiamo collegare a sua volta un EstimatorToFactor
						
						query=prefissi+"SELECT DISTINCT ?r ?f WHERE{"
								+ ""
								+ "PropertyValue(?r,fa:hasEstimator,<"+URI+">)"
								+ ",PropertyValue(?r,fa:hasRiskFactor,<"+riskFactor.getURI()+">)"
								+ ",Type(?r,?f)"
								+ ",DirectSubClassOf(?f,fa:EstimatorsToFactor)"
								+ "}";
						res1=sdQuery(manager, reasoner, query);
//						System.out.println(res1);
						
						for(QueryBinding queryBinding1 : res1){
							
							String URI1=queryBinding1.get(new QueryArgument(QueryArgumentType.VAR, "r")).getValue();
							String type1=queryBinding1.get(new QueryArgument(QueryArgumentType.VAR, "f")).getValue();
							EstimatorToFactorType typeParsed;
							typeParsed=parseEstimatorToFactorType(type1);
							
							EstimatorToFactorI etf=new EstimatorToFactorImpl(URI1, typeParsed);
							estimator.addEstimatorToFactor(etf);
							
							
						}
						
						riskFactor.addEstimator(estimator);
					
					}
					
				}
			
			}
		
		}
		
		
		//conviene creare prima un mondo di istanze
		
		FactorSpaceBuilder fsb=new FactorSpaceBuilder(new NoWhiteLowerCase());
		@SuppressWarnings("rawtypes")
		FeatureSpace<Factor> featureSpace;
		
		
		b.setFallProb(FRATupConsts.instance().defaultFallProb());
        b.setHealthyFall(FRATupConsts.instance().defaultHealthyFall());
		
		
		for (List<RiskFactorI> lista : RiskSettingORRiskFactors.values()){
			
			for(RiskFactorI riskFactor : lista){
				
				boolean isReversible;
				
				if(riskFactor.getReversibility()!=Reversibility.IRREVERSIBLE) isReversible=true;
				else isReversible=false;
				
				
				switch (riskFactor.getType()) {
				
				case TERNARY:
					fsb.addTernaryType(riskFactor.getURI(), isReversible);
					break;

				case SCALAR:
					//FIXME da vedere come implementare il massimo e il minimo, non sono chiaramente indicati nell'ontologia
					fsb.addScalarType(riskFactor.getURI(), 10, isReversible);
					break;
				default:
					break;
				
				}
				
				
				//FIXME Vanno aggiunti tutti i RiskFactor prima di andare sui comorbidity!!!
			}	
				
		}
		

		
		//creiamo il FeatureSpace
		
		try{
			
			featureSpace=fsb.build();
		
		}
		catch(IllegalStateException e){
		
			e.printStackTrace();
			return null;
			
		}
		
		/*
		 * adesso che abbiamo il fsb dobbiamo fare un'altra cosa
		 * 
		 */
		
		try{
		
			for(String riskSettingURI:RiskSettingORRiskFactors.keySet()){
				
				for (RiskFactorI riskFactor : RiskSettingORRiskFactors.get(riskSettingURI)){
						
					switch (riskFactor.getType()) {
					case TERNARY:
						b.setTernaryRiskFactor(featureSpace.getType(riskFactor.getURI()), RiskSettingORRef.get(riskSettingURI), true);
						break;
	
					case SCALAR:
						//FIXME da vedere come implementare il massimo e il minimo, non sono chiaramente indicati nell'ontologia
						b.addScalarFactor(featureSpace.getType(riskFactor.getURI()), RiskSettingORRef.get(riskSettingURI), true);
						break;
						
					case SINERGY:
						//non possiamo fare niente siccome in questa parte preliminare mancano molti RiskFactor e ci servono tutti nelle istanze per poterli aggiungere
						//FIXME BISOGNA SISTEMARE LA COMORBIDITY!!!!!!!!
						break;
					default:
						break;
					}
					//per i comorbidity come facciamo? Vanno aggiunti gli altri RiskFactor prima di poter aggiungere il comorbidity!!!!!		
				}
				
			}
			
		}
		catch(NoSuchElementException e){
			
			e.printStackTrace();
			System.err.println("RiskFactor not found in the feature space");
			return null;
		
		}
		catch(IllegalArgumentException e){
			
			e.printStackTrace();
			return null;
		
		}
		catch(NullPointerException e){
		
			e.printStackTrace();
			return null;
		
		}
		
		
		
		
		/*
		 * 
		 * PARTE COMORBIDITY NON ANCORA UTILIZZABILE
		 * 
		 
		for(String riskSettingORURI : RiskSettingORRiskFactors.keySet()){
				
			for(RiskFactorI riskFactor : RiskSettingORRiskFactors.get(riskSettingORURI)){
				
				if(riskFactor.getType()==RiskFactorType.SINERGY) {
					
					SynergyTypeBuilder comorbidityB = new SynergyTypeBuilder(riskFactor.getURI());
					
					for(RiskFactorI rf:riskFactor.getSinergyRiskFactors()){
						comorbidityB.addFactor(featureSpace.getType(rf.getURI()));
					}
					
					try{
						SynergyOR comorbidity=SynergyOR.createFixedOR(comorbidityB.build(), OddsRatioImpl.create(RiskSettingORRef.get(riskSettingORURI)));
						b.addSynergy(comorbidity,true);
					}
					catch(Exception e){
						e.printStackTrace();
						System.err.println("Impossible to create this comorbidity factor:"+riskFactor.getURI());
						return null;
					}
					
				}				
				//FIXME Vanno aggiunti tutti i RiskFactor prima di andare sui comorbidity!!!
			}	
			
		}
		*/
		
		
		
		/*
		 * 
		 * PARTE IN CUI AGGIUNGIAMO TUTTO COME NEL DEANDREA, CI SERVIAMO DEI BUILDER FORNITI DAL PROF?
		 * 
		 */
		
		
		/*
		 * STAMPA TUTTI I RISKFACTOR COLLEGATI AD UN SINERGY----------------------------------------------------------------------------------------------------
		 */
		for (List<RiskFactorI> lista : RiskSettingORRiskFactors.values()){
			for(RiskFactorI riskFactor : lista){
				
				if(riskFactor.getType()==RiskFactorType.SINERGY){
					Set<RiskFactorI> rf=riskFactor.getSinergyRiskFactors();
					for(RiskFactorI rfa:rf) System.out.println(rfa);
				}
			}
		}
		
		/*
		 * Stampa estimator collegati
		 */
		
		for (List<RiskFactorI> lista : RiskSettingORRiskFactors.values()){
			for(RiskFactorI riskFactor : lista){
				System.out.println(riskFactor);
				for(EstimatorI estimator:riskFactor.getEstimators()){
					System.out.println("-<<--"+estimator);
				}
			}
		}
		
		
		
		
		System.out.println("-------------------------------------STAMPA RISKSETTINGORREF E RISKFACTOR A LORO COLLEGATI--------------------------------");
		
		
		for (String string : RiskSettingORRef.keySet()) {
			System.out.println(string);
			for(RiskFactorI riskFactor:RiskSettingORRiskFactors.get(string)){
				System.out.println("---"+riskFactor);
			}
		}
		
		/*
		 * FINE STAMPA DI CONTROLLO--------------------------------------------------------------------------------------------------------------------------------
		 */
	
        
		RiskFactorsData output=null;
		
		try{
			
			output=b.build();
		
		}
		catch(IllegalStateException e){
		
			e.printStackTrace();
			System.err.println("Impossible to build the RiskFactorData from the RiskFactorDataBuilder");
			
		}
		
		
        return output;
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
	
	/**
	 * This function runs a query using the standard SPARQL DL and returns the result
	 * @param manager The manager of the ontology on which we want to query
	 * @param reasoner A reasoner on that ontology
	 * @param query as the name says
	 * @return the result or null
	 */
	public QueryResult sdQuery(OWLOntologyManager manager,OWLReasoner reasoner,String query){
		Query q=null;
		
		QueryEngine qEn=new QueryEngineImpl(manager, reasoner);
		
		QueryResult res=null;
//		query="PREFIX fa: <"+iri+"#> SELECT ?p ?x ?r WHERE { Type(?x,fa:OWLClass_a924e8db_a89c_4098_9259_0bbb77acfc04), PropertyValue(?p, fa:isRiskFactorIn, ?x), PropertyValue(?x,fa:OddsRatio,?r)}";
//		System.out.println(query);
		try {
			q=Query.create(query);
			res=qEn.execute(q);
			return res;
		} catch (QueryParserException e1) {
			
			System.err.println("PROBLEMA NELLA CREAZIONE DELLA QUERY");
			e1.printStackTrace();
		} catch (QueryEngineException e) {
			
			System.err.println("PROBLEMA NELL'ESECUZIONE DELLA QUERY");
			e.printStackTrace();
		}
		return null;
	}//fine sdQuery
	
	/**
	 * Returns the RiskFactorType given an URI that points to the RiskFactorType
	 * @param type
	 * @return the RiskFactorType or null if not found
	 */
	public RiskFactorType parseRiskFactorType (String type){
		
		Map<String,RiskFactorType> mappaType;
		mappaType=new HashMap<>();
		
		mappaType.put("http://www.unibo.it/fallrisk#ScalarRiskFactorType",RiskFactorType.SCALAR );
		mappaType.put("http://www.unibo.it/fallrisk#TernaryFeatureType",RiskFactorType.TERNARY);
		mappaType.put("http://www.unibo.it/fallrisk#SinergyRiskFactorType",RiskFactorType.SINERGY);
		
		return mappaType.get(type);
		
		
	}
	
	/**
	 * Returns the EstimatorType given an URI that points to the EstimatorType
	 * @param type
	 * @return the EstimatorType or null if not found
	 */
	public EstimatorType parseEstimatorType (String type){
		
		Map<String,EstimatorType> mappaType;
		mappaType=new HashMap<>();
		
		mappaType.put("http://www.unibo.it/fallrisk#ScalarEstimator",EstimatorType.SCALAR );
		mappaType.put("http://www.unibo.it/fallrisk#TernaryEstimator",EstimatorType.TERNARY);
		
		return mappaType.get(type);
		
		
	}
	
	
	
	/**
	 * Returns the Reversibility given an URI that points to it
	 * @param reversibility
	 * @return the Reversibility or null if not found
	 */
	public Reversibility parseReversibility (String reversibility){
		
		Map<String,Reversibility> mappaType;
		mappaType=new HashMap<>();
		
		mappaType.put("http://www.unibo.it/fallrisk#Irreversible", Reversibility.IRREVERSIBLE);
		mappaType.put("http://www.unibo.it/fallrisk#SurelyReversible",Reversibility.REVERSIBLE);
		mappaType.put("http://www.unibo.it/fallrisk#SubjectSpecificReversible",Reversibility.SUBJECT_SPECIFIC);
		
		return mappaType.get(reversibility);
		
		
	}
	
	/**
	 * Returns the EstimatorToFactorType given an URI that points to the EstimatorToFactorType
	 * @param type
	 * @return the EstimatorToFactorType or null if not found
	 */
	public EstimatorToFactorType parseEstimatorToFactorType (String type){
		
		Map<String,EstimatorToFactorType> mappaType;
		mappaType=new HashMap<>();
		
		mappaType.put("http://www.unibo.it/fallrisk#ConstantEstimatorToFactor", EstimatorToFactorType.CONSTANT);
		mappaType.put("http://www.unibo.it/fallrisk#DiscreteLevelsEstimatorToFactor",EstimatorToFactorType.DISCRETE_LEVELS);
		mappaType.put("http://www.unibo.it/fallrisk#InequalityOREstimatorsToFactor",EstimatorToFactorType.INEQUALITY);
		
		return mappaType.get(type);
		
		
	}
	
	public void provaQuery(OWLOntologyManager manager, OWLReasoner reasoner){
		String query=null;
		
		BufferedReader read=new BufferedReader(new InputStreamReader(System.in));
		
		try {
			while((query=read.readLine())!=null){
				QueryResult res=null;
				res=sdQuery(manager, reasoner, query);
				System.out.println(res);
			}
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
}//fine class


