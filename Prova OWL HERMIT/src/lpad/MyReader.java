package lpad;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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
import lpad.synergy.SynergyOR;
import lpad.synergy.SynergyTypeBuilder;

//import org.semanticweb.HermiT.Configuration;
//import org.semanticweb.HermiT.Reasoner;
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
import stat.odds.OddsRatioImpl;
import util.stringFunction.NoWhiteLowerCase;

import com.clarkparsia.pellet.owlapiv3.PelletReasonerFactory;

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
	
	String reference="Deandrea2010";
	String setting="Community";
	String risk="FallIn1Year";
	String query=null;
	private String ontologyURI="http://ai.unibo.it/on2risk";
	private String prefissi="PREFIX fa: <"+ontologyURI+"#> PREFIX rdf: <http://www.w3.org/2000/01/rdf-schema#> ";
	OWLReasoner reasonerHermitInstance,reasonerPelletInstance;
	
	
	public MyReader(String reference, String setting, String risk){
		
		this();
		this.reference=reference;
		this.risk=risk;
		this.setting=setting;
		
	}
	
	public MyReader() {
		
	}

	public RiskFactorsData read(Reader reader) {
		
		//ESTRAIAMO TUTTI GLI ESTIMATOR.
		
		List<RiskFactorI> riskFactors;
		
		riskFactors=getRiskFactors(reader);
		
		List<EstimatorI> estimators=new ArrayList<EstimatorI>();
		
		for(RiskFactorI riskFactor:riskFactors){
			
			for(EstimatorI estimator:riskFactor.getEstimators()){
				
				estimators.add(estimator);
				
			}
			
		}
		
		
		createHtmlTableEstimators(estimators);
		
		
		
        return fromRFToRiskFactorsData(riskFactors);
	}//fine read
	
	
	/**
	 * 
	 * This function extract a list of RiskFactorI from an ontology
	 * 
	 * @param reader
	 * @return
	 */
	public List<RiskFactorI> getRiskFactors(Reader reader){
		
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
		
		
        
		List<RiskFactorI> riskFactors=new ArrayList<>();
		

		//per questa usiamo hermit che mi sembra più veloce
		
		reasoner=getReasonerPellet(ontology);
				
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
		
		
		res=sdQuery(manager, reasoner, query);
		
		//TODO bisogna aggiungere anche le prevalence
		
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
		
		//aggiungiamo tutte le prevalence ai riskFactor
		
		for(RiskFactorI riskfactor:riskFactors){
			
			query=prefissi+"select ?prevalence where{"
					+ "Type(?insP,fa:RiskSettingPrevalenceRef)"
					+ ",PropertyValue(?insP,fa:isAboutSetting,fa:"+setting+")"
					+ ",PropertyValue(?insP,fa:isAboutRiskFactor,<"+riskfactor.getURI()+">)"
					+ ",PropertyValue(?insP,fa:Probability,?prevalence)"
					+ "}";
			
			res=sdQuery(manager, reasoner, query);
			
			for (QueryBinding queryBinding : res) {
				
				double prevalence=0;
				
				try{
					prevalence=Double.parseDouble(queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "prevalence")).getValue());
				}
				catch(NumberFormatException e){
					prevalence=0;
				}
				
				riskfactor.setPrevalence(prevalence);
			
			}
			
			
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
		
		System.out.println("--------------------------DETERMINAZIONE TERNARY CONSTANT---------------------------");
		
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
		
		System.out.println("--------------------------DETERMINAZIONE SCALAR INEQUALITY---------------------------");
		
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
			
			
			//------------------------------AGGIUNTA DESCRIZIONE LIVELLI(ESTIMATOR)---------------------------------
			
			query=prefissi+"select ?desc ?lev where{"
					+ "Type(?inst,fa:ScalarEstimatorValueDescription)"
					+ ",PropertyValue(?inst,fa:isValueDescriptionOf,<"+estURI+">)"
					+ ",PropertyValue(?inst,fa:ScalarEstimatorValue,?lev)"
					+ ",PropertyValue(?inst,fa:BriefDescription,?desc)"
					+ "}";
			
			QueryResult res2=sdQuery(manager, reasoner, query);
			
			Map<Integer,String> levelDescriptions=new HashMap<>();
			
			for(QueryBinding qb:res2){
				int level;
				String desc=qb.get(new QueryArgument(QueryArgumentType.VAR, "desc")).getValue();
				try{
					level=Integer.parseInt(qb.get(new QueryArgument(QueryArgumentType.VAR, "lev")).getValue());
				}
				catch(NumberFormatException e){
					System.err.println("Impossible to parse int value of: "+estURI);
					level=-1;
				}
				
				levelDescriptions.put(level, desc);
				
			}
			
			//------------------------------------------------------------------------------------------------------

//			String question=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "question")).getValue();
			
			for(RiskFactorI riskFactor:riskFactors){
				
				if(riskFactor.getURI().compareTo(rfURI)==0){
					
					EstimatorI estimator=new EstimatorImpl(estURI, EstimatorType.SCALAR_INEQUALITY,max,min,scalarValue,parseInequalityType(inequality));
					estimator.setLevelDescriptions(levelDescriptions);
					riskFactor.addEstimator(estimator);
					
				}
				
			}
			
		}
		
		
		
		//adesso invece estraiamo gli scalar a livelli
		
		System.out.println("--------------------------DETERMINAZIONE SCALAR LEVELS---------------------------");
		
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
			
			System.out.println("--------------------------PREVALENZE LIVELLI RISK FACTOR---------------------------");
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
		
		
		//abbiamo aggiunto inequality, scalar e ternary, ci mancano i Synergic
		
		
		//--------------------------------------------QUI AGGIUNGIAMO I RISKFACTOR NORMALI AL TIPO Synergic------------------------------------
		
		System.out.println("--------------------------AGGIUNTA RF DI TIPO Synergic---------------------------");
		
		query=prefissi+"select distinct ?riskfactor ?SynergicRF where{"
				+ "Type(?torfsr,fa:ToRiskFactorSettingRef)"
				+ ",PropertyValue(?torfsr,fa:isAboutSetting,fa:"+setting+")"
				+ ",PropertyValue(?torfsr,fa:supportedByRef,fa:"+reference+")"
				+ ",PropertyValue(?torfsr,fa:hasToRiskFactor,?estToF)"
				+ ",PropertyValue(?estToF,fa:isAboutRiskFactor,?riskfactor)"
				+ ",Type(?estToF,fa:SynergicFactorsToFactor)"
				+ ",PropertyValue(?estToF,fa:hasSynergicRiskFactor,?SynergicRF)"
				+ "}";
		
		
		res=sdQuery(manager, reasoner, query);
		
		System.out.println(res);
		
		for(QueryBinding queryBinding:res){
			
			String rfURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "riskfactor")).getValue();
			String SynergicRFURI=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "SynergicRF")).getValue();
			
			for(RiskFactorI riskFactor:riskFactors){
				
				if(riskFactor.getURI().compareTo(rfURI)==0){
					
					riskFactor.setType(RiskFactorType.SYNERGY);
					
					for(RiskFactorI riskFactorSin:riskFactors){
						
						if(riskFactorSin.getURI().compareTo(SynergicRFURI)==0){
							
							riskFactor.addSynergyRiskFactor(riskFactorSin);
							
						}
						
					}
					
				}
				
			}
			
		}
		
		
		//multipli estimator per riskfactor
		
		
		//qui usiamo pellet
		
		reasoner=getReasonerPellet(ontology);
		
		
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
				
				if(rf.getURI().compareTo(URI)==0 && rf.getType()==null){
					
					rf.setType(parseRiskFactorType(type));
					
				}
				
			}
			
		}
		
		
		/*
		 * 
		 * AGGIUNGIAMO QUESTION A TUTTI GLI ESTIMATOR CHE CE L'HANNO
		 * 
		 */
				
		for(RiskFactorI riskFactor:riskFactors){
			
			Set<EstimatorI> estimators;		
			estimators=riskFactor.getEstimators();
			
			for(EstimatorI estimator:estimators){
				
				query=prefissi+"select ?question where{"
						+ "PropertyValue(<"+estimator.getURI()+">,fa:Question,?question)"
						+ "}";
				
				res=sdQuery(manager, reasoner, query);
				
				for(QueryBinding queryBinding:res){
					
					String question=queryBinding.get(new QueryArgument(QueryArgumentType.VAR, "question")).getValue();
					estimator.setQuestion(question);
				
				}
				
				
			}
			
		}//fine for
		
		
		/*
		 * 
		 * STAMPA DEI RISKFACTOR E DI QUELLO DA CUI SONO POPOLATI
		 * 
		 */
		
		
		//abbiamo finito di definire se è ind/dir scalar/tern
				
		for(RiskFactorI riskFactor:riskFactors){
			
			
			System.out.println(riskFactor);
		
		
		}//fine for
		
	
		
		for(RiskFactorI riskFactor : riskFactors){
			
			
			if(riskFactor.getType()==RiskFactorType.DIRECT_SCALAR){
				
				
				query=prefissi+"select ?maxLevel where{"
						+ "PropertyValue(<"+riskFactor.getURI()+">,fa:RiskFactorMaxLevel,?maxLevel)"
						+ "}";
				res=sdQuery(manager, reasoner, query);
				
				int maxLevel;
				
				try{
					maxLevel=Integer.parseInt(res.get(0).get(new QueryArgument(QueryArgumentType.VAR, "maxLevel")).getValue());
				}
				catch(NumberFormatException e){
					maxLevel=0;
				}
				
				riskFactor.setMax(maxLevel);
				
				
			}
			
			
		}
		
		
		
		
		/*
		 * 
		 * prima di aggiungere a Deandrea, puliamo l'URI
		 * RENDIAMO L'URI CON LA PRIMA LETTERA MINUSCOLA E SENZA L'INDIRIZZO DAVANTI
		 * 
		 * 
		 */
		
		
		for(RiskFactorI riskFactor: riskFactors){
			
			String provv;
			provv=riskFactor.getURI().replaceAll(".+#", "");
			provv=provv.replaceFirst("^.", provv.substring(0, 1).toLowerCase());
			System.out.println(provv);
			riskFactor.setURI(provv);
			
			for(EstimatorI estimator:riskFactor.getEstimators()){
				provv=estimator.getURI().replaceAll(".+#", "");
				provv=provv.replaceFirst("^.", provv.substring(0, 1).toLowerCase());
				System.out.println(provv);
				estimator.setURI(provv);
			}
			
		}
		
		return riskFactors;
		
	}
	
	
	/**
	 * 
	 * This function extract a list of EstimatorI from an ontology
	 * 
	 * @param reader
	 * @return
	 */
	public List<EstimatorI> getEstimators(Reader reader){
		
		List<RiskFactorI> riskFactors;
		
		riskFactors=getRiskFactors(reader);
		
		List<EstimatorI> estimators=new ArrayList<EstimatorI>();
		
		for(RiskFactorI riskFactor:riskFactors){
			
			for(EstimatorI estimator:riskFactor.getEstimators()){
				
				estimators.add(estimator);
				
			}
			
		}
		
		return estimators;
		
	}
	
	/**
	 * 
	 * This function creates a RiskFactorsData class from a list of RiskFactorI
	 *(used in the function Read)
	 * 
	 * @param riskFactors
	 * @return RiskFactorsData class
	 * @author Flavio Zuffa
	 */
	public RiskFactorsData fromRFToRiskFactorsData(List<RiskFactorI> riskFactors){
		
		
	//conviene creare prima un mondo di istanze
		
		RiskFactorsDataBuilder b=new RiskFactorsDataBuilder();
		
		
		FactorSpaceBuilder fsb=new FactorSpaceBuilder(new NoWhiteLowerCase());
		@SuppressWarnings("rawtypes")
		FeatureSpace<Factor> featureSpace;
		
		
		b.setFallProb(FRATupConsts.instance().defaultFallProb());
        b.setHealthyFall(FRATupConsts.instance().defaultHealthyFall());
		
		
		
			
			for(RiskFactorI riskFactor : riskFactors){
				
				boolean isReversible=true;
				
				
				switch (riskFactor.getType()) {
				
				case TERNARY:
					
				case DIRECT_TERNARY:
					fsb.addTernaryType(riskFactor.getURI(), isReversible);
					break;
				case SCALAR:
					
				case DIRECT_SCALAR:
		
					
					//qui dobbiamo anche controllare se è synergic o meno
					
					fsb.addScalarType(riskFactor.getURI(), riskFactor.getMax(), isReversible);
					
					break;
//				case SINERGY:
//					//FIXME da vedere come implementare il massimo e il minimo, non sono chiaramente indicati nell'ontologia
//					
//					query=prefissi+"select ?maxLevel where{"
//							+ "PropertyValue(<"+riskFactor.getURI()+">,fa:RiskFactorMaxLevel,?maxLevel)"
//							+ "}";
//					res=sdQuery(manager, reasoner, query);
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
				case DIRECT_TERNARY:
				case TERNARY:
					b.setTernaryRiskFactor(featureSpace.getType(riskFactor.getURI()), riskFactor.getOddsRatio(), true);
					break;
				case DIRECT_SCALAR:
				case SCALAR:
					b.addScalarFactor(featureSpace.getType(riskFactor.getURI()), riskFactor.getOddsRatio(), true);
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
			
			if(riskFactor.getType()==RiskFactorType.SYNERGY){
				
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
		
	
        
		RiskFactorsData output=null;
		
		try{
			
			output=b.build();
		
		}
		catch(IllegalStateException e){
		
			e.printStackTrace();
			System.err.println("Impossible to build the RiskFactorData from the RiskFactorDataBuilder");
			
		}
		
		
		return output;
		
		
	}//fine funzione
	
	/**
	 * This function creates an html table that contains radio button for ternary estimators and an input text for Scalar estimators.
	 * @param estimators
	 * @return
	 * @author Flavio Zuffa
	 */
	public String createHtmlTableEstimators(List<EstimatorI> estimators){
		StringBuilder builder=new StringBuilder();
		StringBuilder buildSc=new StringBuilder();
		StringBuilder buildT=new StringBuilder();
		String tabEstimators;
		
		
		
		for(EstimatorI estimator:estimators){
			
			if(estimator.getType()==EstimatorType.SCALAR_INEQUALITY || estimator.getType()==EstimatorType.SCALAR_LEVELS){
				
				buildSc.append("<tr>");
				buildSc.append("<td width=\"40%\">");
				buildSc.append("<span id=\""+estimator.getURI()+"\">");
				buildSc.append(estimator.getQuestion());
				buildSc.append("</span></td>");
				buildSc.append("<td><div class=\"row\"");
				buildSc.append("::before");
				buildSc.append("<div class=\"form-group scalarEstimatorInputGroup\">");
				buildSc.append("<div class=\"col-xs-4\">");
				buildSc.append("<input class=\"input-mini inputParam form-control scalarEstimatorInputText\" id=\""+estimator.getURI()+"text\" name=\""+estimator.getURI()+"\" type=\"text\" prolog=\""+estimator.getURI()+"\" minallowedvalue=\""+estimator.getMin()+"\" maxallowedvalue=\""+estimator.getMax()+"\" disabled>");
				buildSc.append("</div>");
				buildSc.append("<div class=\"col-xs-8\">");
				buildSc.append("<label class=\"checkbox-inline\"><input type=\"checkbox\" class=\"inputParam\" id=\""+estimator.getURI()+"\"  name=\""+estimator.getURI()+"\" prolog=\""+estimator.getURI()+"\" value=\"u\" checked");
				buildSc.append("\"Use prevalence\"</label></div></div> ::after </div></td></tr>");
			}
			else if(estimator.getType()==EstimatorType.TERNARY){
				
				buildT.append("<tr>");
				buildT.append("<td width=\"40%\">");
				buildT.append("<span id=\""+estimator.getURI()+"\">");
				buildT.append(estimator.getQuestion());
				buildT.append("</span></td>");
				buildT.append("<td><div class=\"radio-inline\"");
				buildT.append("<label class=\"radio-inline\"> <input type=\"radio\" class=\"inputParam\" prolog=\""+estimator.getURI()+"\" name=\""+estimator.getURI()+"\" value=\"t\">Yes</label>");
				buildT.append("<label class=\"radio-inline\"> <input type=\"radio\" class=\"inputParam\" prolog=\""+estimator.getURI()+"\" name=\""+estimator.getURI()+"\" value=\"f\">No</label>");
				buildT.append("<label class=\"radio-inline\"> <input type=\"radio\" class=\"inputParam\" prolog=\""+estimator.getURI()+"\" name=\""+estimator.getURI()+"\" value=\"u\" checked>Use prevalence</label>");
				buildT.append("</div></td></tr>");
				
			}
			
		}
		
		
		tabEstimators="<table><tbody>"+buildSc.toString()+buildT.toString()+"</tbody></table>";
		
		tabEstimators=tabEstimators.replaceAll(">", ">\n");
		
		/*
		 * 
		 */
		
		File f;
		
		f=new File("C:\\Users\\Flavio\\Desktop\\ris.html");
		
		try {
			FileWriter fw=new FileWriter(f);
			fw.write(tabEstimators);
			fw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
//		System.out.println(tabEstimators);
		
		return tabEstimators;
		
	}
	
	

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

	
//	/**
//	 * Returns the EstimatorType given an URI that points to the EstimatorType
//	 * @param type
//	 * @return the EstimatorType or null if not found
//	 */
//	public EstimatorType parseEstimatorType (String type){
//		
//		Map<String,EstimatorType> mappaType;
//		mappaType=new HashMap<>();
//		
//		mappaType.put(ontologyURI+"#ScalarEstimator",EstimatorType.SCALAR );
//		mappaType.put(ontologyURI+"#TernaryEstimator",EstimatorType.TERNARY);
//		
//		return mappaType.get(type);
//		
//		
//	}
	
	
	
	/**
	 * Returns the Reversibility given an URI that points to it
	 * @param reversibility
	 * @return the Reversibility or null if not found
	 * @deprecated
	 */
	public Reversibility parseReversibility (String reversibility){
		
		Map<String,Reversibility> mappaType;
		mappaType=new HashMap<>();
		
		mappaType.put(ontologyURI+"#Irreversible", Reversibility.IRREVERSIBLE);
		mappaType.put(ontologyURI+"#SurelyReversible",Reversibility.REVERSIBLE);
		mappaType.put(ontologyURI+"#SubjectSpecificReversible",Reversibility.SUBJECT_SPECIFIC);
		
		return mappaType.get(reversibility);
		
		
	}
	
//	/**
//	 * Returns the EstimatorToFactorType given an URI that points to the EstimatorToFactorType
//	 * @param type
//	 * @return the EstimatorToFactorType or null if not found
//	 */
//	public EstimatorToFactorType parseEstimatorToFactorType (String type){
//		
//		Map<String,EstimatorToFactorType> mappaType;
//		mappaType=new HashMap<>();
//		
//		mappaType.put(ontologyURI+"#ConstantEstimatorToFactor", EstimatorToFactorType.CONSTANT);
//		mappaType.put(ontologyURI+"#DiscreteLevelsEstimatorToFactor",EstimatorToFactorType.DISCRETE_LEVELS);
//		mappaType.put(ontologyURI+"#InequalityOREstimatorsToFactor",EstimatorToFactorType.INEQUALITY);
//		
//		return mappaType.get(type);
//		
//		
//	}
	
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
	
	
	@SuppressWarnings("unused")
//	private OWLReasoner getReasonerHermit(OWLOntology ontology){
//		
//		OWLReasoner reasoner;
//		
//		if(reasonerHermitInstance==null){
//			Configuration conf=new Configuration();
//			
//			//questo server perché il datatype reversible non è supportato da OWL 2, per farlo funzionare ci vuole questa configurazione
//			conf.ignoreUnsupportedDatatypes=true;
//			conf.throwInconsistentOntologyException=false;
//			
//			reasoner=new Reasoner(conf, ontology);
//			reasonerHermitInstance=reasoner;
//			
//		}
//		
//		return reasonerHermitInstance;
//		
//	}
	
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


