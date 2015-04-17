package lpad;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;



import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.ReaderDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
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
import org.semanticweb.owlapi.util.SimpleIRIMapper;



public class MyReader implements OWLReader{

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
				System.out.println(prop.toString());
				
				//TODO ELIMINARE LA STAMPA
			}
		}
		
		/*
		 * a questo punto abbiamo tutte le classi in classes e tutte le instanze di riskFactor in riskFactorInstances
		 * 
		 */
		
		
		//TODO come procediamo?
//		
//		KnowledgeBase kb=reasoner.getKB();
//		
//		//FIXME qui ci vuole anche jena per fare il passaggio delle query!!!!!
//		PelletInfGraph graph=new org.mindswap.pellet.jena.PelletReasoner().bind( kb );
//		
//		// Wrap the graph in a model
//		InfModel model = ModelFactory.createInfModel( graph );
//		// Use the model to answer SPARQL queries
//		String queryString=null;
//		
//		queryString="PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
//				+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
//				+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
//				+ "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
//				+ "SELECT ?subject ?object\n"
//				+ "WHERE { ?subject rdfs:subClassOf ?object }";
//		
//		System.out.println(queryString);
//		
//		Query q=QueryFactory.create(queryString);
//		
//		QueryExecution qex=QueryExecutionFactory.create(q,model);
//		
//		ResultSet results = qex.execSelect();
//	    ResultSetFormatter.out(System.out, results, q);       
//
//	     qex.close() ;
//		
		
//		Selector s;
//		Statement stat;
//		
//		s=new SelectorImpl();
		
		
		
		//SparqlOwlParser par= SparqlOwlParser.
		
		return null;
		
	}//fine read

}
