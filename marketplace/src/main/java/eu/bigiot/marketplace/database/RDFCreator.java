package eu.bigiot.marketplace.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RDFCreator {
	
	private final Logger logger = LoggerFactory.getLogger(RDFCreator.class);
	
	/**
	 * method is for testing.
	 * 
	 * @param args
	 */
	public static void main(String[] args){
		
		//Model of the RDF store
		Model model = Namespace.createBasicRDFModel();
		
		System.out.println("RDF generation started...");
		
		System.getProperties().put("http.proxyHost", "194.145.60.1");
		System.getProperties().put("http.proxyPort", "9400");
		
		try{			
			RDFCreator rdfCreator = new RDFCreator();
			rdfCreator.createRDFStore(model);

			File dbFile = new File(BIGIoT_DAO.rdfFilePath);
			System.out.println("Writing the database back to the file " + dbFile.getAbsolutePath());
			model.write(new FileOutputStream(dbFile), BIGIoT_DAO.rdfFormat);
			
		}catch(Exception e){
			System.out.println("RDF generation interrupted due to following error :");
			e.printStackTrace();
			return;
		}
		System.out.println("RDF generation successful...");
	}
	
	
	/**
	 * 
	 * @param model
	 * @throws FileNotFoundException
	 */
	public void createRDFStore(Model model) throws FileNotFoundException{	
				
		//Creating properties
		Property name = model.createProperty(Namespace.NAME);
		Property description = model.createProperty(Namespace.DESCRIPTION);
		Property icon = model.createProperty(Namespace.ICON);
		Property type = model.createProperty(Namespace.TYPE);
		Property category = model.createProperty(Namespace.CATEGORY);
		
		Property hasIngredients = model.createProperty(Namespace.HAS_INGREDIENT);
		Property inputData = model.createProperty(Namespace.HAS_INPUT);
		Property outputData = model.createProperty(Namespace.HAS_OUTPUT);
		Property configAttributes = model.createProperty(Namespace.HAS_CONFIG_ATTRIBUTE);
		
		Property priceAmount = model.getProperty(Namespace.PRICE_AMOUNT);
		Property priceCurrency = model.getProperty(Namespace.PRICE_CURRENCY);
		Property priceSpecification = model.getProperty(Namespace.PRICE_SPECIFICATION);
		Property accountingModel = model.getProperty(Namespace.ACCOUNTING_MODEL);
		Property license = model.getProperty(Namespace.LICENSE);
		Property location = model.getProperty(Namespace.REGION);
		
		//Resource types
		Resource city = model.createResource(Namespace.TYPE_CITY);
		Resource service = model.createResource(Namespace.TYPE_SERVICE);
		Resource recipe = model.createResource(Namespace.TYPE_RECIPE);
		Resource offering = model.createResource(Namespace.TYPE_OFFERING);
		Resource data = model.createResource(Namespace.TYPE_DATA);
		
		//Cities
		Resource barcelona = model.createResource(Namespace.MARKETPLACE + "barcelona", city);
		Resource berlin = model.createResource(Namespace.MARKETPLACE + "berlin", city);
		Resource munich = model.createResource(Namespace.MARKETPLACE + "munich", city);
		Resource wolfsburg = model.createResource(Namespace.MARKETPLACE + "wolfsburg", city);
		Resource piedmont = model.createResource(Namespace.MARKETPLACE + "piedmont", city);
		Resource dublin = model.createResource(Namespace.MARKETPLACE + "dublin", city);
		
		//Assigning names to cities
		barcelona.addProperty(name, "Barcelona");
		berlin.addProperty(name, "Berlin");
		munich.addProperty(name, "Munich");
		wolfsburg.addProperty(name, "Wolfsburg");
		piedmont.addProperty(name, "Piedmont");
		dublin.addProperty(name, "Dublin");
		
		//Assigning icons to cities
		barcelona.addProperty(icon, "app/images/cities/barcelona.jpg");
		berlin.addProperty(icon, "app/images/cities/berlin.jpg");
		munich.addProperty(icon, "app/images/cities/munich.jpg");
		wolfsburg.addProperty(icon, "app/images/cities/wolfsburg.jpg");
		piedmont.addProperty(icon, "app/images/cities/piedmont.jpg");
		dublin.addProperty(icon, "app/images/cities/dublin.jpg");
		
		//Services
		Resource bikeSharing = model.createResource(Namespace.MARKETPLACE + "bike_sharing", service);
		Resource buses = model.createResource(Namespace.MARKETPLACE + "buses", service);
		Resource parking = model.createResource(Namespace.MARKETPLACE + "parking", service);
		Resource traffic = model.createResource(Namespace.MARKETPLACE + "traffic", service);
		Resource household = model.createResource(Namespace.MARKETPLACE + "building", service);
		
		//Assigning names to services
		bikeSharing.addProperty(name, "Bike Sharing");
		buses.addProperty(name, "Buses");
		parking.addProperty(name, "Parking");
		traffic.addProperty(name, "Traffic");
		household.addProperty(name, "Smart Building");
		
		//Assigning icons to services
		bikeSharing.addProperty(icon, "app/images/services/bike.png");
		buses.addProperty(icon, "app/images/services/bus.png");
		parking.addProperty(icon, "app/images/services/park.png");
		traffic.addProperty(icon, "app/images/services/traffic.png");
		household.addProperty(icon, "app/images/services/house.png");
		
		//Recipe categories
		Resource category_bike_sharing = model.createResource(Namespace.BIKE_SHARING);
		Resource category_buses = model.createResource(Namespace.BUSES);
		Resource category_parking = model.createResource(Namespace.PARKING);
		Resource category_traffic = model.createResource(Namespace.TRAFFIC);
		Resource category_building = model.createResource(Namespace.SMART_BUILDING);
				
		//Assigning recipe categories to services
		bikeSharing.addProperty(category, category_bike_sharing);
		buses.addProperty(category, category_buses);
		parking.addProperty(category, category_parking);
		traffic.addProperty(category, category_traffic);
		household.addProperty(category, category_building);
		
		logger.debug("RDF model created");
	}
	
}
