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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import lpad.features.Factor;
import lpad.features.FactorSpaceBuilder;
import lpad.synergy.SynergyOR;
import lpad.synergy.SynergyTypeBuilder;

import org.semanticweb.HermiT.Configuration;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.HermiT.Reasoner.ReasonerFactory;
import org.semanticweb.HermiT.examples.Explanations;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.OWLAxiom;
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
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.clarkparsia.owlapi.explanation.BlackBoxExplanation;
import com.clarkparsia.owlapi.explanation.HSTExplanationGenerator;
import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

import sample.feature.FeatureSpace;
import stat.odds.OddsRatio;
import stat.odds.OddsRatioImpl;
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
	private String ontologyURI="http://ai.unibo.it/on2risk";
	private String prefissi="PREFIX fa: <"+ontologyURI+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> ";
	OWLReasoner reasonerHermitInstance,reasonerPelletInstance;
	
	public RiskFactorsData read(Reader reader) {
		
		OWLOntology ontology=null;
//		Set<OWLClass> classes=null;
//		Set<OWLAnonymousIndividual> individuals=null;
		OWLReasoner reasoner=null;		
		
	    
		OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
		OWLOntologyDocumentSource docSource=new ReaderDocumentSource(reader);
		
		try {
			ontology=manager.loadOntologyFromOntologyDocument(docSource);
		} catch (OWLOntologyCreationException e) {
			e.printStackTrace();
			System.err.println("Error when trying to load the document from the source");
			return null;
		}
		
		
//		reasoner=getReasonerHermit(ontology);


//		System.out.println("Is the ontology consistent? "+reasoner.isConsistent());
		
		QueryResult res;
		
//		String q;
//		BufferedReader buffR=new BufferedReader(new InputStreamReader(System.in));
//		
//		try {
//			System.out.println("INSERIMENTO QUERY, per uscire CTRL+Z");
//			while((q=buffR.readLine())!=null){
//				q=prefissi+q;
//				res=sdQuery(manager, reasoner, q);
//				System.out.println(res);
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
		
		
		System.out.println("ok");
		
		
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
		List<String> RiskFactorsURIs=new ArrayList<>();
		Map<String,List<RiskFactorI>> RiskSettingORRiskFactors=new HashMap<>();
		List<RiskFactorI> riskFactors=new ArrayList<>();
		Map<String, EstimatorI> estimators=new HashMap<>();
		
		@SuppressWarnings("unused")
		String reference="Deandrea2010";
		String setting="Community";
		String risk="FallIn1Year";
		String query=null;

		//per questa usiamo hermit che mi sembra più veloce
		
		reasoner=getReasonerPellet(ontology);
		
//		query=prefissi+"select distinct ?torfsr ?riskfactor where{"
//				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
//				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
//				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
//				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?torf)"
//				+ ",PropertyValue(?torf,fa:isAboutRiskFactor,?riskfactor)"
////				+ ",PropertyValue(?riskfactor,fa:hasFeatureType,?featureType)"
////				+ ",PropertyValue(?riskfactor,fa:hasReversibility,?reversibility)"//da qui in giù parte per la determinazione dell'or
//				+ "}";
		
		//possiamo anche ottenere i rf semplicemente basandoci sull'odds ratio e poi partire di lì.

		
		
		
		
		query=prefissi+"select distinct ?riskfactor ?oddsRatio where{"
				+ "Type(?torfsr,fa:RiskSettingOddsRatioRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:OddsRatio,?oddsRatio)"
				+ ",PropertyValue(?torfsr,fa:isAboutRiskFactor,?riskfactor)"
				+ ",PropertyValue(?torfsr,fa:isAboutRisk,fa:"+risk+")"
//				+ ",PropertyValue(?riskfactor,fa:hasFeatureType,?featureType)"
//				+ ",PropertyValue(?riskfactor,fa:hasReversibility,?reversibility)"//da qui in giù parte per la determinazione dell'or
				+ "}";
		
		//FIXME dobbiamo sistemare la reversibility all'interno del nostro programma
		
		res=sdQuery(manager, reasoner, query);
		
		//FIXME bisogna aggiungere anche le prevalence
		
		System.out.println(res);
		
		//QUI ESTRAIAMO TUTTI GLI ESTIMATOR E LI COLLEGHIAMO AI RISKFACTOR


		for (QueryBinding queryBinding : res) {
			
			String URI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "riskfactor")).getValue();
//			String type=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "featureType")).getValue();
//			String reversibility=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "reversibility")).getValue();
			
			double oddsRatio=0;
			try{
				oddsRatio=Double.parseDouble(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "oddsRatio")).getValue());
			}
			catch(NumberFormatException e){
				oddsRatio=0;
			}
			
			RiskFactorI rf=new RiskFactorImpl(URI,oddsRatio);
			
			riskFactors.add(rf);
			System.out.println("aggiunto:"+rf);
		}
		
		//QUI ESTRAIAMO TUTTI GLI ESTIMATOR E LI COLLEGHIAMO AI RISKFACTOR
		/*soluzione perfetta per estrarre tutto
		query=prefissi+"select distinct ?riskfactor ?estimator where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",PropertyValue(?estToF,fa:isAboutEstimator,?estimator)"
				+ "} "
				+ "or where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",PropertyValue(?estToF,fa:hasEstimatorInequality,?estimatorIn)"
				+ ",PropertyValue(?estimatorIn,fa:isAboutEstimator,?estimator)"
				+ "}"
				+ "or where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",PropertyValue(?estToF,fa:hasEstimatorInterpretation,?estimatorIn)"
				+ ",PropertyValue(?estimatorIn,fa:isAboutEstimator,?estimator)"
				+ "}";
		*/
		
		//estraiamo un tipo di estimator alla volta
		
		//prima i constantTernary
		
		query=prefissi+"select distinct ?riskfactor ?estimator ?question where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",PropertyValue(?estToF,fa:isAboutEstimator,?estimator)"
				+ ",PropertyValue(?estimator,fa:Question,?question)"
				+ "}";
		
		
		res=sdQuery(manager, reasoner, query);
		
		System.out.println(res);
		
		for(QueryBinding queryBinding:res){
			
			String rfURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "riskfactor")).getValue();
			String estURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimator")).getValue();
			String question=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "question")).getValue();
			
			for(RiskFactorI riskFactor:riskFactors){
				
				if(riskFactor.getURI().compareTo(rfURI)==0){
					
					EstimatorI estimator=new EstimatorImpl(estURI, EstimatorType.TERNARY,question);
					riskFactor.addEstimator(estimator);
					
				}
				
			}
			
		}
		
		//adesso gli scalar inequality
		
		//FIXME sistemare la question all'interno dell'ontologia
		
		query=prefissi+"select distinct ?riskfactor ?estimator ?scalarValue ?inequality ?estimatorMin ?estimatorMax where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",PropertyValue(?estToF,fa:hasEstimatorInequality,?estimatorIn)"
				+ ",PropertyValue(?estimatorIn,fa:ScalarEstimatorValue,?scalarValue)"
				+ ",PropertyValue(?estimatorIn,fa:Inequality,?inequality)"
				+ ",PropertyValue(?estimatorIn,fa:isAboutEstimator,?estimator)"
				+ ",PropertyValue(?estimator,fa:EstimatorMinValue,?estimatorMin)"
				+ ",PropertyValue(?estimator,fa:EstimatorMaxValue,?estimatorMax)"
				+ "}";
		
		res=sdQuery(manager, reasoner, query);
		
		System.out.println(res);
		
		for(QueryBinding queryBinding:res){
			
			int max,min,scalarValue;

			String rfURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "riskfactor")).getValue();
			String estURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimator")).getValue();
			String inequality=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "inequality")).getValue();
			
			try{
				max=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimatorMax")).getValue());
				min=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimatorMin")).getValue());
				scalarValue=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "scalarValue")).getValue());
			}
			catch(NumberFormatException e){
				System.err.println("Impossible to parse int value of: "+estURI);
				max=-1;
				min=-1;
				scalarValue=-1;
			}
			

//			String question=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "question")).getValue();
			
			for(RiskFactorI riskFactor:riskFactors){
				
				if(riskFactor.getURI().compareTo(rfURI)==0){
					
					EstimatorI estimator=new EstimatorImpl(estURI, EstimatorType.SCALAR_INEQUALITY,max,min,scalarValue,parseInequalityType(inequality));
					riskFactor.addEstimator(estimator);
					
				}
				
			}
			
		}
		
		
		
		//adesso invece estraiamo gli scalar a livelli
		
		
		query=prefissi+"select distinct ?riskfactor ?estimator ?estimatorLevels ?lastStep ?stepSize ?firstStep ?estimatorMin ?estimatorMax where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",Type(?estToF,fa:MaxOfInterpretations)"
				+ ",PropertyValue(?estToF,fa:hasEstimatorInterpretation,?estimatorLevels)"
				+ ",PropertyValue(?estimatorLevels,fa:isAboutEstimator,?estimator)"
				+ ",PropertyValue(?estimatorLevels,fa:LastStep,?lastStep)"
				+ ",PropertyValue(?estimatorLevels,fa:Step1Start,?firstStep)"
				+ ",PropertyValue(?estimatorLevels,fa:StepSize,?stepSize)"
				+ ",PropertyValue(?estimator,fa:EstimatorMinValue,?estimatorMin)"
				+ ",PropertyValue(?estimator,fa:EstimatorMaxValue,?estimatorMax)"
				+ "}";
		
		res=sdQuery(manager, reasoner, query);
		
		System.out.println(res);
		
		
		for(QueryBinding queryBinding:res){
			
			int max,min,firstStep,stepSize,lastStep;

			String rfURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "riskfactor")).getValue();
			String estURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimator")).getValue();
			String estDiscreteLevelsURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimatorLevels")).getValue();
			
			try{
				max=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimatorMax")).getValue());
				min=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "estimatorMin")).getValue());
				firstStep=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "firstStep")).getValue());
				lastStep=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "lastStep")).getValue());
				stepSize=Integer.parseInt(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "stepSize")).getValue());
				
			}
			catch(NumberFormatException e){
				System.err.println("Impossible to parse int value of: "+estURI);
				max=-1;
				min=-1;
				stepSize=-1;
				lastStep=-1;
				firstStep=-1;
			}
			

			//otteniamo anche le prevalence dei livelli
			Map<Integer,Double> levelPrevalence=new HashMap<>();
			
			query=prefissi+"select distinct ?riskFactorLevel ?riskFactorLevelProbability where{"
					+ "Type(?rfdl,fa:RiskFactorLevelPrevalence)"
					+ ",PropertyValue(?rfdl,fa:isRiskFactorLevelPrevalenceOf,?rfp)"
					+ ",PropertyValue(?rfp,fa:isAboutRiskFactor,<"+rfURI+">)"
					+ ",PropertyValue(?rfp,fa:isAboutSetting,fa:"+setting+")"
					+ ",PropertyValue(?rfdl,fa:RiskFactorLevel,?riskFactorLevel)"
					+ ",PropertyValue(?rfdl,fa:Probability,?riskFactorLevelProbability)"
					+ "}";
			
			QueryResult res2=sdQuery(manager, reasoner, query);
			
			System.out.println(res2);
			
			for(QueryBinding qb:res2){
				
				int level;
				double probability;
				
				try{
					
					level=Integer.parseInt(qb.get(new QueryArgument(QueryArgumentType.VAR, "riskFactorLevel")).getValue());
					probability=Double.parseDouble(qb.get(new QueryArgument(QueryArgumentType.VAR, "riskFactorLevelProbability")).getValue());
					
				}
				catch(NumberFormatException e){
					
					System.err.println("Impossible to parse int value of: "+estURI);
					level=-1;
					probability=-1;
					
				}
				
				levelPrevalence.put(level, probability);
				
			}
			//abbiamo finito con i livelli e le sue prevalenze
			
//			String question=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "question")).getValue();
			
			for(RiskFactorI riskFactor:riskFactors){
				
				if(riskFactor.getURI().compareTo(rfURI)==0){
					
					EstimatorI estimator=new EstimatorImpl(estURI, EstimatorType.SCALAR_LEVELS,max,min,firstStep,stepSize,lastStep);
					riskFactor.addEstimator(estimator);
					riskFactor.setLevelPrevalences(levelPrevalence);
					
				}
				
			}
			
		}
		
		
		//abbiamo aggiunto inequality, scalar e ternary, ci mancano i sinergic
		
		
		//--------------------------------------------QUI AGGIUNGIAMO GLI ESTIMATOR DI TIPO SINERGIC------------------------------------
		
		
		query=prefissi+"select distinct ?riskfactor ?sinergicRF where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",Type(?estToF,fa:SinergicFactorsToFactor)"
				+ ",PropertyValue(?estToF,fa:hasSinergicRiskFactor,?sinergicRF)"
				+ "}";
		
		
		res=sdQuery(manager, reasoner, query);
		
		System.out.println(res);
		
		for(QueryBinding queryBinding:res){
			
			String rfURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "riskfactor")).getValue();
			String sinergicRFURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "sinergicRF")).getValue();
			
			for(RiskFactorI riskFactor:riskFactors){
				
				if(riskFactor.getURI().compareTo(rfURI)==0){
					
					riskFactor.setType(RiskFactorType.SINERGY);
					
					for(RiskFactorI riskFactorSin:riskFactors){
						
						if(riskFactorSin.getURI().compareTo(sinergicRFURI)==0){
							
							riskFactor.addSinergyRiskFactor(riskFactorSin);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		
		//qui usiamo pellet
		
		reasoner=getReasonerPellet(ontology);
		
		//parte per la determinazione dell'oddsratio
//		query=prefissi+"select ?orRef ?riskFactor ?oddsRatio where{"
//		+ "Type(?orRef,fa:RiskSettingOddsRatioRef)"
//		+ ",PropertyValue(?orRef,fa:isAboutSetting,fa:"+setting+")"
//		+ ",PropertyValue(?orRef,fa:supportedByRef,fa:"+reference+")"
//		+ ",PropertyValue(?orRef,fa:isAboutRisk,fa:"+risk+")"
//		+ ",PropertyValue(?orRef,fa:isAboutRiskFactor,?riskFactor)"
//		+ ",PropertyValue(?orRef,fa:OddsRatio,?oddsRatio)"
//		+ "}";
//		
//		
//		res=sdQuery(manager, reasoner, query);
//		System.out.println(res);
		

//		for(QueryBinding queryBinding:res){
//			double oddsRatio=0;
//			try{
//				oddsRatio=Double.parseDouble(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "oddsRatio")).getValue());
//			}
//			catch(NumberFormatException e){
//				oddsRatio=0;
//			}
//			
//			String URI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "riskFactor")).getValue();
//			String orRefUri=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "orRef")).getValue();
//			
//			
//			for(RiskFactorI riskFactor:riskFactors){
//				
//				
//				if(riskFactor.getURI().compareTo(URI)==0){
//					
//					riskFactor.setOddsRatio(oddsRatio);
//					
//				}
//				
//			}
//			
//		}
		
		System.out.println("fermino");
		
		//determiniamo il type

		query=prefissi+"select distinct ?rf ?type where{"
		+ "Type(?rf,fa:RiskFactor)"
		+ ",Type(?rf,?type)"
		+ ",DirectSubClassOf(?type,fa:ScalarRiskFactor)"
		+ "}"
		+ "or where{"
		+ "Type(?rf,fa:RiskFactor)"
		+ ",Type(?rf,?type)"
		+ ",DirectSubClassOf(?type,fa:TernaryRiskFactor)"
		+ "}";

		res=sdQuery(manager, reasoner, query);
		
		System.out.println(res);
		
		for(QueryBinding queryBinding : res){
			
			String URI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "rf")).getValue();
			String type=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "type")).getValue();
			
			for(RiskFactorI rf: riskFactors){
				
				if(rf.getURI().compareTo(URI)==0){
					
					rf.setType(parseRiskFactorType(type));
					
				}
				
			}
			
		}
		
		//abbiamo finito di definire se è ind/dir scalar/tern
		
		//qui controlliamo se qualcosa è synergic o meno
		
		for(RiskFactorI riskFactor:riskFactors){
		
			//dobbiamo discriminare i direct e gli indirect
			
			query=prefissi+"select ?toRFtype where{"
					+ "Type(?trfsr,fa:ToRiskFactorSettingRef)"
					+ ",PropertyValue(?trfsr,fa:isAboutSetting,fa:"+setting+")"
					+ ",PropertyValue(?trfsr,fa:supportedByRef,fa:"+reference+")"
					+ ",PropertyValue(?trfsr,fa:hasToRiskFactor,?toRF)"
					+ ",PropertyValue(?toRF,fa:isAboutRiskFactor,<"+riskFactor.getURI()+">)"
					+ ",Type(?toRF,?toRFtype)"
					+ ",SubClassOf(?toRFtype,fa:AggregationOfInterpretations)"
					+ "}";
			System.out.println(query);
			System.out.println(sdQuery(manager, reasoner, query));
		
		}//fine for
		
	
		//fino a qui sembra funzionare ma molto lento
		System.out.println("fermino");
		
		
		
		//conviene creare prima un mondo di istanze
		
		FactorSpaceBuilder fsb=new FactorSpaceBuilder(new NoWhiteLowerCase());
		@SuppressWarnings("rawtypes")
		FeatureSpace<Factor> featureSpace;
		
		
		b.setFallProb(FRATupConsts.instance().defaultFallProb());
        b.setHealthyFall(FRATupConsts.instance().defaultHealthyFall());
		
		
		
			
			for(RiskFactorI riskFactor : riskFactors){
				
				boolean isReversible;
				
				if(riskFactor.getReversibility()!=Reversibility.IRREVERSIBLE) isReversible=true;
				else isReversible=false;
				
				
				switch (riskFactor.getType()) {
				
				case TERNARY:
					fsb.addTernaryType(riskFactor.getURI(), isReversible);
					break;

				case SCALAR:
					
//FIXME da vedere come implementare il massimo e il minimo, non sono chiaramente indicati nell'ontologia
					
					query=prefissi+"select ?maxLevel where{"
							+ "PropertyValue(<"+riskFactor.getURI()+">,fa:hasRiskFactorType,?riskFactorType)"
							+ ",PropertyValue(?riskFactorType,fa:RiskFactorMaxLevel,?maxLevel)"
							+ "}";
					res=sdQuery(manager, reasoner, query);
					
					int maxLevel;
					
					try{
						maxLevel=Integer.parseInt(res.get(0).get(new QueryArgument(QueryArgumentType.VAR, "maxLevel")).getValue());
					}
					catch(NumberFormatException e){
						maxLevel=0;
					}
					
					//qui dobbiamo anche controllare se è synergic o meno
					
					
					fsb.addScalarType(riskFactor.getURI(), maxLevel, isReversible);
					
					break;
//				case SINERGY:
//					//FIXME da vedere come implementare il massimo e il minimo, non sono chiaramente indicati nell'ontologia
//					
//					query=prefissi+"select ?maxLevel where{"
//							+ "PropertyValue(<"+riskFactor.getURI()+">,fa:hasRiskFactorType,?riskFactorType)"
//							+ ",PropertyValue(?riskFactorType,fa:RiskFactorMaxLevel,?maxLevel)"
//							+ "}";
//					res=sdQuery(manager, reasoner, query);
//					
//					
//					
//					try{
//						maxLevel=Integer.parseInt(res.get(0).get(new QueryArgument(QueryArgumentType.VAR, "maxLevel")).getValue());
//					}
//					catch(NumberFormatException e){
//						maxLevel=0;
//					}
//					
//					//qui dobbiamo anche controllare se è synergic o meno
//					
//					
//					fsb.addScalarType(riskFactor.getURI(), maxLevel, isReversible);
//					
//					break;
				default:
					break;
				
				}
				
				
				//FIXME Vanno aggiunti tutti i RiskFactor prima di andare sui comorbidity!!!
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
						
			for (RiskFactorI riskFactor : riskFactors){
					
				switch (riskFactor.getType()) {
				case TERNARY:
					b.setTernaryRiskFactor(featureSpace.getType(riskFactor.getURI()), riskFactor.getOddsRatio(), true);
					break;

				case SCALAR:
					//FIXME da vedere come implementare il massimo e il minimo, non sono chiaramente indicati nell'ontologia
					b.addScalarFactor(featureSpace.getType(riskFactor.getURI()), riskFactor.getOddsRatio(), true);
					break;
					
				case INDIRECT:
					//non possiamo fare niente siccome in questa parte preliminare mancano molti RiskFactor e ci servono tutti nelle istanze per poterli aggiungere
					//FIXME BISOGNA SISTEMARE LA COMORBIDITY!!!!!!!!

					break;
				default:
					break;
				}
				//per i comorbidity come facciamo? Vanno aggiunti gli altri RiskFactor prima di poter aggiungere il comorbidity!!!!!		
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
		
		
		
		
		for(RiskFactorI riskFactor:riskFactors){
			
			if(riskFactor.getType()==RiskFactorType.INDIRECT){
				
				final SynergyTypeBuilder comorbidityB = new SynergyTypeBuilder(riskFactor.getURI());
				
				for(RiskFactorI rf:riskFactor.getSinergyRiskFactors()){
					
					comorbidityB.addFactor(featureSpace.getType(rf.getURI()));
					
				}
				
				final SynergyOR comorbidity =
		                SynergyOR.createFixedOR(
		                comorbidityB.build(),
		                OddsRatioImpl.create(riskFactor.getOddsRatio()));
				b.addSynergy(comorbidity,true);
				
			}
			
		}
		
		
		
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
				
				if(riskFactor.getType()==RiskFactorType.INDIRECT){
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
			if(res!=null)System.out.println("NUMERO RECORD:"+res.size());
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
		
		mappaType.put(ontologyURI+"#DirectScalarRiskFactor",RiskFactorType.DIRECT_SCALAR );
		mappaType.put(ontologyURI+"#DirectTernaryRiskFactor",RiskFactorType.DIRECT_TERNARY);
		mappaType.put(ontologyURI+"#IndirectScalarRiskFactor",RiskFactorType.INDIRECT_SCALAR );
		mappaType.put(ontologyURI+"#IndirectTernaryRiskFactor",RiskFactorType.INDIRECT_TERNARY);
		
		if(mappaType.get(type)!=null){
			return mappaType.get(type);
		}
		else{
			return RiskFactorType.UNKNOWN;
		}
	
	}

//public RiskFactorType findUnknownRiskFactorType (String type,OWLOntologyManager manager,OWLReasoner reasoner){
//	
//	Map<String,RiskFactorType> mappaType;
//	mappaType=new HashMap<>();
//	
//	mappaType.put("http://www.unibo.it/fallrisk#ScalarRiskFactorType",RiskFactorType.SCALAR );
//	mappaType.put("http://www.unibo.it/fallrisk#TernaryFeatureType",RiskFactorType.TERNARY);
//	mappaType.put("http://www.unibo.it/fallrisk#SinergyRiskFactorType",RiskFactorType.SINERGY);
//	
//	QueryResult res;
//	
//	String query=prefissi+" select ?tipo where{"
//			+ "Type(<"+"type"+">,?tipo"
//			+ ",DirectSubClassOf(?tipo,fa:RiskFactorType)"
//			+ "}";
//	
//	res=sdQuery(manager, reasoner, query);
//	
//	for(QueryBinding queryBinding:res){
//		String tipo;
//		tipo=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "maxLevel")).getValue();
//		
//		if(tipo.compareTo("http://www.unibo.it/fallrisk#ScalarRiskFactorType")==0){
//			return RiskFactorType.SCALAR;
//		}
//		else if(tipo.compareTo("http://www.unibo.it/fallrisk#SinergyRiskFactorType")==0)
//		
//		
//	}
//	
//	return RiskFactorType.UNKNOWN;
//
//}
	
	
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
	
	
	private OWLReasoner getReasonerHermit(OWLOntology ontology){
		
		OWLReasoner reasoner;
		
		if(reasonerHermitInstance==null){
			Configuration conf=new Configuration();
			
			//questo server perché il datatype reversible non è supportato da OWL 2, per farlo funzionare ci vuole questa configurazione
			conf.ignoreUnsupportedDatatypes=true;
			conf.throwInconsistentOntologyException=false;
			
			reasoner=new Reasoner(conf, ontology);
			reasonerHermitInstance=reasoner;
			
		}
		
		return reasonerHermitInstance;
		
	}
	
	private OWLReasoner getReasonerPellet(OWLOntology ontology){
		
		OWLReasoner reasoner;
		
		if(reasonerPelletInstance==null){
		
			reasoner=PelletReasonerFactory.getInstance().createReasoner(ontology);
			reasonerPelletInstance=reasoner;
		
		}
		
		return reasonerPelletInstance;
		
	}
	
	public InequalityType parseInequalityType(String inequality){
		Map<String,InequalityType> mappaInequality;
		mappaInequality=new HashMap<>();
		
		mappaInequality.put("Equal",InequalityType.EQUAL);
		mappaInequality.put("Less",InequalityType.LESS);
		mappaInequality.put("LessOrEqual",InequalityType.LESS_OR_EQUAL);
		mappaInequality.put("Greater",InequalityType.GREATER);
		mappaInequality.put("GreaterOrEqual",InequalityType.GREATER_OR_EQUAL);
		
		if(mappaInequality.get(inequality)!=null){
			return mappaInequality.get(inequality);
		}
		else{
			return null;
		}
	}
	
}//fine class


