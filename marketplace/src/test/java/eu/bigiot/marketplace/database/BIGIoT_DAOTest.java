package eu.bigiot.marketplace.database;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.bigiot.marketplace.model.Ingredient;
import eu.bigiot.marketplace.model.Offering;
import eu.bigiot.marketplace.model.Recipe;

public class BIGIoT_DAOTest {

	private final Logger logger = LoggerFactory.getLogger(BIGIoT_DAOTest.class);
	
	private BIGIoT_DAO dao;
	
	private static final String rdfFilePath = "src/main/resources/rdf_store_test.ttl";
	
	@Before
	public void init() {
		
		System.getProperties().put("http.proxyHost", "194.145.60.1");
		System.getProperties().put("http.proxyPort", "9400");

		// Create the initial Model of the RDF store:
		Model model = Namespace.createBasicRDFModel();
		InfModel infModel = null;
		
		try {
			//
			// 1. create RDF store:
			//
			logger.info("RDF generation started...");

			RDFCreator rdfCreator = new RDFCreator();
			rdfCreator.createRDFStore(model);

			logger.info("RDF generation successful...");

			//
			// 2. import vocabularies to RDF store:
			//
			logger.info("RDF import started...");
			
			RDFImporter rdfImporter = new RDFImporter();
			rdfImporter.importFromFileToModel(model, "vocabularies");
			rdfImporter.importFromFileToModel(model, "samples");

			logger.info("Import successful...");

			//
			// 3. infer rules on RDF store:
			//
			RDFRuleApplier rdfRuleApplier = new RDFRuleApplier();
			infModel = rdfRuleApplier.applyInferenceRules(model);

			//
			// store rdf file for testing purposes:
			//
			infModel.write(new FileOutputStream(rdfFilePath), BIGIoT_DAO.rdfFormat);
			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		//
		// init DAO:
		//
		dao = new BIGIoT_DAO(model, infModel);
		dao.initDB();
	}
	
	@Test
	public void testGetRecipes() {
		
		List<Recipe> recipeList = dao.getRecipes("Parking"); //"Smart Building"
		for (Recipe recipe : recipeList) {
			logger.info(recipe.id);
		}
	}
	
	@Test
	public void testGetRecipePattern() {
		Map<String, Object> recipePatternMap = dao.getRecipePattern("http://w3c.github.io/bigiot/PerfectSwitchLightRecipe#PerfectSwitchLightRecipe");//"http://w3c.github.io/bigiot/RouteToParking#EmailDirectionsToParking");//"http://w3c.github.io/bigiot/RouteToParkingSpot#RouteToParkingSpot");
		for (String recipeInfoElement : recipePatternMap.keySet()) {
			logger.info(recipeInfoElement + ": " + recipePatternMap.get(recipeInfoElement));
		}
	}

	@Test
	public void testGetOfferingCategories() {
		List<String> offeringCategoryList = dao.getOfferingCategories();
		for (String category : offeringCategoryList) {
			logger.info(category);
		}
	}

	@Test
	public void testGetIngredients() {
		List<Ingredient> ingredientsList = dao.getIngredients("http://w3c.github.io/bigiot/RouteToParking#EmailDirectionsToParking", null);
		for (Ingredient ingredient : ingredientsList) {
			logger.info(ingredient.id);
		}
	}
	
	@Test
	public void testGetAllOfferings() {
		List<Offering> offeringsList = dao.getAllOfferings();
		for (Offering o : offeringsList) {
			logger.info("offering: " + o);
		}
	}
	
//	@Test
//	public void testGetAllDataTypes() {
//		List<String> dataTypeList = dao.getAllDataTypes();
//		for (String dataType : dataTypeList) {
//			logger.info(dataType);
//		}
//	}

	@Test
	public void testGetDataTypes() {
		List<String> dataTypeList = dao.getDataTypes("bigiot:allOfferings");//schema:charging");
		for (String dataType : dataTypeList) {
			logger.info(dataType);
		}
	}
}
