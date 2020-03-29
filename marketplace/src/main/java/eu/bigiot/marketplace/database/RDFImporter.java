package eu.bigiot.marketplace.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URL;

import org.apache.jena.rdf.model.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFImporter {

	private final Logger logger = LoggerFactory.getLogger(RDFImporter.class);
	
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
			
			rdfModel.read(new FileInputStream(dbFile), null, BIGIoT_DAO.rdfFormat);
			
			new RDFImporter().importFromFileToModel(rdfModel, args[0]);
			
			System.out.println("Writing the database back to the file " + dbFile.getAbsolutePath());
			rdfModel.write(new FileOutputStream(dbFile), BIGIoT_DAO.rdfFormat);
			
		} catch(Exception e) {
			System.out.println("Import interrupted due to following error :");
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 
	 * @param model
	 * @param file
	 * @throws FileNotFoundException 
	 */
	public void importFromFileToModel(Model model, String fileName) throws FileNotFoundException {

		logger.debug("Starting RDF import from file: " + fileName);
		
		URL fileAsUrl = RDFImporter.class.getResource("/" + fileName);
		
		File file = new File(fileAsUrl.getFile());
		
		if(!file.exists()) {
			throw new FileNotFoundException("File '" + fileName + "' does not exist.");
		}
		
		if(file.isDirectory()){
			File[] listOfFiles = file.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
			      if (listOfFiles[i].isFile()) {
			    	  readFileToModel(model, listOfFiles[i]);
			      }
			}
		}
		else if(file.isFile()){
			readFileToModel(model, file);
		}
	}
	
	private void readFileToModel(Model rdfModel, File file) throws FileNotFoundException{
		if(file.getName().endsWith("jsonld")){
			logger.debug("Reading file: " + file.getAbsolutePath());
			rdfModel.read(new FileInputStream(file), null, "JSON-LD");
		}
		else if(file.getName().endsWith("ttl")){
			logger.debug("Reading file: " + file.getAbsolutePath());
			rdfModel.read(new FileInputStream(file), null, "TTL");
		}
		else if(file.getName().endsWith("rdf")){
			logger.debug("Reading file: " + file.getAbsolutePath());
			rdfModel.read(new FileInputStream(file), null, "RDF/XML");
		}
		else {
			logger.debug("Skipping file " + file.getName() + "!");
		}
	}

}
