package eu.bigiot.marketplace.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFRuleApplier {

	private final Logger logger = LoggerFactory.getLogger(RDFRuleApplier.class);
	
	/**
	 * method is for testing.
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		
		try{
			System.out.println("Reading the existing database...");
			Model rdfModel = Namespace.createBasicRDFModel();

			File dbFile = new File(BIGIoT_DAO.rdfFilePath);
			File dbFileOut = new File("src/main/resources/rdf_store_rulesApplied.ttl");
			
			rdfModel.read(new FileInputStream(dbFile), null, BIGIoT_DAO.rdfFormat);
			
			InfModel infModel = new RDFRuleApplier().applyInferenceRules(rdfModel);
			
			System.out.println("Writing the database back to the file " + dbFile.getAbsolutePath());
			infModel.write(new FileOutputStream(dbFileOut), BIGIoT_DAO.rdfFormat);
						
		}catch(Exception e){
			System.out.println("Rule application interrupted due to following error :");
			e.printStackTrace();
			return;
		}
		System.out.println("Rule application successful...");
	}
	
	/**
	 * applies rules to the specified model.
	 * 
	 * @param rdfModel
	 * @return
	 */
	public InfModel applyInferenceRules(Model rdfModel){
		logger.debug("Adding inference rules...");
		
		final String inferenceRules =
				"[r1: (?a " + Namespace.BIGIOT + "refersTo ?b) (?p " + Namespace.SCHEMA + "domainIncludes ?b) (?p " + Namespace.SCHEMA + "rangeIncludes ?c) "
						+ "-> (?a " + Namespace.BIGIOT + "expectedAnnotation ?c)] 	" +
				"[r1b: (?a " + Namespace.BIGIOT + "expectedAnnotation ?b) (?c " + Namespace.SKOS + "narrower ?a) "
						+ "-> (?c " + Namespace.BIGIOT + "expectedAnnotation ?b) ]" +
				"[r1c: (?a " + Namespace.BIGIOT + "expectedAnnotation ?b) (?b " + Namespace.SCHEMA + "rangeIncludes ?c) "
						+ "-> (?a " + Namespace.BIGIOT + "expectedAnnotation ?c) ]" ;
		
		logger.debug("Running inference rules: " + inferenceRules);
		
		Reasoner reasoner = new GenericRuleReasoner(Rule.parseRules(inferenceRules));
		InfModel infModel = ModelFactory.createInfModel(reasoner, rdfModel); //rdfModel = ModelFactory.createInfModel(reasoner, rdfModel); 
		reasoner.setDerivationLogging(true);
			
		logger.debug("Inference rules applied!");
		
		return infModel;
	}
	
}
