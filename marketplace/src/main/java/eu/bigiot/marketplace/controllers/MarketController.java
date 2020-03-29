package eu.bigiot.marketplace.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.github.jsonldjava.utils.JsonUtils;

import eu.bigiot.marketplace.appgen.AkkaAppGenerator;
import eu.bigiot.marketplace.appgen.GeneratorIf;
import eu.bigiot.marketplace.appgen.SynchronousAppGenerator;
import eu.bigiot.marketplace.database.BIGIoT_DAO;
import eu.bigiot.marketplace.database.Namespace;
import eu.bigiot.marketplace.database.RDFCreator;
import eu.bigiot.marketplace.database.RDFImporter;
import eu.bigiot.marketplace.database.RDFRuleApplier;
import eu.bigiot.marketplace.model.CardinalityCOSR;
import eu.bigiot.marketplace.model.City;
import eu.bigiot.marketplace.model.CombinedPriceCOSR;
import eu.bigiot.marketplace.model.ComplexOSR;
import eu.bigiot.marketplace.model.DirectOSR;
import eu.bigiot.marketplace.model.Ingredient;
import eu.bigiot.marketplace.model.IngredientRuntimeConfiguration;
import eu.bigiot.marketplace.model.Interaction;
import eu.bigiot.marketplace.model.InteractionDescriptor;
import eu.bigiot.marketplace.model.OSR;
import eu.bigiot.marketplace.model.Offering;
import eu.bigiot.marketplace.model.OperatorOSR;
import eu.bigiot.marketplace.model.Recipe;
import eu.bigiot.marketplace.model.RecipeRuntimeConfiguration;
import eu.bigiot.marketplace.model.SameAsCOSR;
import eu.bigiot.marketplace.model.Service;
import eu.bigiot.marketplace.services.BackendService;

@Controller
public class MarketController {

	private final Logger logger = LoggerFactory.getLogger(MarketController.class);
	private BackendService backendService;
	private BIGIoT_DAO dao;

	@Autowired
	public MarketController(BackendService backendService) {
		this.backendService = backendService;
	}

	/**
	 * this method sets up the marketplace recipe cooker environment.
	 */
	@PostConstruct
	public void init() {

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

	@RequestMapping(value = "/", method = RequestMethod.GET, produces = "text/plain; charset=UTF-8")
	public String getIndex(Map<String, Object> model) {
		logger.debug("Root : index.html is requested!");
		model.put("title", backendService.getTitle());
		model.put("msg", backendService.getDesc());
		return "index.html";
	}

	@RequestMapping(value = "/locations", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<City> getLocations() {
		logger.debug("Locations requested!");
		return dao.getCities();
	}

	@RequestMapping(value = "/services", params = "city", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Service> getServices(@RequestParam(value = "city", required = true) String city) {
		logger.debug("Services requested for " + city);
		return dao.getServices(city);
	}

	@RequestMapping(value = "/recipes", params = "service", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Recipe> getRecipes(@RequestParam(value = "service", required = true) String service) {
		logger.debug("Recipes requested for " + service);
		return dao.getRecipes(service);
	}

	@RequestMapping(value = "/recipe_pattern", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String getRecipePattern(@RequestBody Map<String, Object> params) {
		String recipeID = (String) params.get("recipe");
		logger.debug("Recipe pattern requested for " + recipeID);
		try {
			return JsonUtils.toPrettyString(dao.getRecipePattern(recipeID));
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
	
	@RequestMapping(value = "/osr_list", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<OSR> getOSRList() {
		logger.debug("OSR List requested.");
		//return dao.getOSRList();
		return dao.getGlobalOSRList();
	}

	@RequestMapping(value = "/allOfferings", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Offering> getAllOfferings() {
		logger.debug("All Offerings requested");
		return dao.getAllOfferings();
	}
	
	@RequestMapping(value = "/offerings", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Ingredient> getIngredients(@RequestBody Map<String, Object> params) {
		String recipeID = (String) params.get("recipe");
		//List<OSR> OSRList = (List<OSR>) params.get("OSRList");
		Map<String, List<Map<String, String>>> OSRMap = (Map<String, List<Map<String, String>>>) params.get("OSRMap");
		logger.debug("Offerings requested for " + recipeID);
		return dao.getIngredients(recipeID, OSRMap);
	}
	
	@RequestMapping(value = "/offerings_for_ingredient", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Offering> getOfferingsForIngredient(@RequestBody Map<String, Object> params) {
		Map<String, Object> ingredient = (Map<String, Object>) params.get("ingredient");
		List<Map<String, String>> OSRList = (List<Map<String, String>>) params.get("OSRList");
		//List<OSR> OSRList = (List<OSR>) params.get("OSRList");
		Map<String, List<OSR>> OSRMap = (Map<String, List<OSR>>) params.get("OSRMap");
		logger.debug("Offerings requested for " + (String)ingredient.get("id"));
		return dao.getOfferingsForIngredient(ingredient, OSRList);
	}

	@RequestMapping(value = "/create_recipe", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public String createRecipe(@RequestBody Map<String, Object> params) {
		String recipeName = (String) params.get("recipeName");
		String recipeID = (String) params.get("recipeID");
		String data = (String) params.get("data");
		String format = (String) params.get("format");
		logger.debug("Create new recipe : " + recipeID);
		String result = dao.createRecipe(recipeName, recipeID, data, format);
		logger.debug("Result : " + result);
		return result;
	}

	@RequestMapping(value = "/load_recipe_file", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, Object> loadRecipeFile(@RequestBody Map<String, Object> params) {
		String data = (String) params.get("data");
		String format = (String) params.get("format");
		logger.debug("Load recipe from file!");
		Map<String, Object> result = dao.loadRecipeFile(data, format);
		return result;
	}

	@RequestMapping(value = "/recipe_categories", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getRecipeCategories() {
		logger.debug("Recipe categories requested!");
		return dao.getRecipeCategories();
	}

	@RequestMapping(value = "/offering_categories", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getOfferingCategories() {
		logger.debug("Offering categories requested!");
		return dao.getOfferingCategories();
	}

	@RequestMapping(value = "/data_types", params = "category", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<String> getDataTypes(@RequestParam(value = "category", required = true) String category) {
		logger.debug("Data types requested for category " + category);
		return dao.getDataTypes(category);
	}

//	@RequestMapping(value = "/all_data_types", method = RequestMethod.GET, produces = "application/json")
//	@ResponseBody
//	public List<String> getAllDataTypes() {
//		logger.debug("All data types requested");
//		return dao.getAllDataTypes();
//	}

	@RequestMapping(value = "/application", method = RequestMethod.POST, consumes = "application/json", produces = "text/plain; charset=UTF-8")
	@ResponseBody
	public String getApplicationSctipt(@RequestBody Map<String, Object> offeringSelection) {
		String recipeID = (String) offeringSelection.get("recipe");
		logger.debug("Application script requested for " + recipeID);
		offeringSelection.remove("recipe");
		for (String ingredientID : offeringSelection.keySet())
			logger.debug("User selection : " + ingredientID + " = " + offeringSelection.get(ingredientID));

		Map<String, Object> recipePattern = dao.getRecipePattern(recipeID);
		recipePattern.put("recipeName", recipeID.substring(recipeID.indexOf('#') + 1));

		Map<String, String> elementTypes = (Map<String, String>) recipePattern.get("elementTypes");
		Map<String, Object> offerings = new HashMap<String, Object>();

		for (String ingredientID : elementTypes.keySet()) {
			String elementType = elementTypes.get(ingredientID);
			if (elementType.equals("condition") || elementType.equals("loop")) {
				offerings.put(ingredientID, new HashMap<String, String>());
			} else if (elementType.equals("offering")) {
				if (offeringSelection.containsKey(ingredientID)) {
					String offeringID = offeringSelection.get(ingredientID).toString();
					offerings.put(ingredientID, dao.getOfferingParams(ingredientID, offeringID));
					logger.debug("Fetched offering : " + offeringID);
				} else {
					logger.error("No offering chosen for the ingredient " + ingredientID);
					return null;
				}
			}
		}
		String generatorImplementation = System.getProperty("eu.bigiot.generatorImpl", "sync");
		GeneratorIf appGenerator;
		if (generatorImplementation.equals("sync")) {
			// Synchronous generator
			appGenerator = new SynchronousAppGenerator();
		} else if (generatorImplementation.equals("async")) {
			// Asynchronous generator
			appGenerator = new AkkaAppGenerator(dao);
		} else {
			throw new RuntimeException("Unknown generator implementation " + generatorImplementation);
		}
		return appGenerator.generateScript(recipePattern, offerings);
	}
	
	// Recipe Runtime Configuration (RRC) contains the Ingredient Runtime Configurations (IRCs), which in turn contains 
	// the selected offerings and offering selection rules (OSRs) defined by the user for IRCs
	@RequestMapping(value = "/create_recipe_runtime_configuration", method = RequestMethod.POST, consumes = "application/json", produces = "text/plain; charset=UTF-8")
	@ResponseBody
    public String createRecipeRuntimeConfiguration(@RequestBody Map<String, Object> rrcData) {
		
		// 1. Store the Recipe Runtime Configuration (RRC), Ingredient Runtime Configurations (IRCs) and the OSRs
		
		String recipe = (String) rrcData.get("recipe");
		String recipeID = recipe.substring(0, recipe.indexOf('#'));
		// All RRCs for a particular recipe belong to the same namespace as the recipe
		String recipeName = recipe.substring(recipe.indexOf('#')+1);		// prefix
		
		RecipeRuntimeConfiguration rrc = new RecipeRuntimeConfiguration(rrcData, recipeName, recipeID, null);	// recipeDescription isn't needed here, so passing null
		
		Map<String, List<Map<String, String>>> mapOfIngredients = new HashMap<String, List<Map<String, String>>>();
		// maps ingredientId to list of 'map of offering parameters'
		
		logger.debug("Create Recipe Runtime Configuration requested for recipe: " + recipeID);
    	
    	String rrcString = "";
    	rrcString += "@prefix bigiot: <http://big-iot.eu/core#> .\n";
    	rrcString += "@prefix offeringRecipe: <http://w3c.github.io/bigiot/offeringRecipeModel#> .\n";
    	rrcString += "@prefix rrc: <http://w3c.github.io/bigiot/offeringRecipeRuntimeConfigurationModel#> .\n";
    	rrcString += "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n";
    	rrcString += "@prefix pattern: <http://w3c.github.io/bigiot/RecipePatternModel#> .\n";
    	rrcString += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
    	rrcString += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
    	rrcString += "@prefix schema: <http://schema.org/> .\n";
    	rrcString += "@prefix td: <http://w3c.github.io/wot/w3c-wot-td-ontology.owl#> .\n";
    	rrcString += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";
    	rrcString += "@prefix smartBuilding: <http://w3c.github.io/bigiot/smartBuilding#> .\n";
    	rrcString += "@prefix " + recipeName + ": <" + recipeID + "#> .\n";
    	rrcString += "\n";
    	
    	rrcString += "rrc:RecipeRuntimeConfiguration\n";
    	rrcString += "\trdf:type owl:Class ;\n";
    	rrcString += "\trdfs:subClassOf owl:Thing ;\n";
    	rrcString += ".\n";
    	
    	rrcString += recipeName + ":" + rrc.rrcName + "\n";
    	rrcString += "\trdf:type rrc:RecipeRuntimeConfiguration;\n";
    	rrcString += "\trrc:runtimeConfigurationOfRecipe " + recipeName + ":" + recipeName + " ;\n";
    	
    	// IngredientRuntimeConfiguration
    	// need to create the IngredientRuntimeConfigurations before creating the Recipe Runtime Configuration
    	List<String> createdIRCs =  createIngredientRCs(rrc.ircList, rrc.uniqID, recipeName, recipeID);
        logger.debug("IngredientRuntimeConfigurations created for Recipe Runtime Configuration : " + rrc.rrcName);
    	
    	for(int i = 0; i < createdIRCs.size(); i++){
        	rrcString += "\trrc:hasIngredientRuntimeConfiguration " + createdIRCs.get(i) + " ; \n";
    	}
        
        rrcString += ".\n";
        
    	logger.debug("RecipeRuntimeConfiguration :\n");
    	logger.debug(rrcString);
		
    	logger.debug("Create new RecipeRuntimeConfiguration : " + rrc.rrcName);
        String result =  dao.createRecipeRuntimeConfiguration(rrc.rrcName, rrc.rrcID, rrcString, RecipeRuntimeConfiguration.FORMAT);
        logger.debug("Result : " + result);

        // RRC, IRCs and OSRs stored
        // 2. Generate Interaction Descriptors (InDe's) for all the offerings now and upload them to the respective offering devices

        // Prepare the mapOfIngredients here
        // mapOfIngredients will be useful while generating the Interaction Descriptors
        for(IngredientRuntimeConfiguration irc : rrc.ircList){
        	String ingredientId = irc.id;		// id of the parent ingredient
        	List<Map<String, String>> listOfOfferingsWithParams = new ArrayList<Map<String, String>>();

        	for(Offering selectedOffering : irc.listOfSelectedOfferings){
        		Map<String, String> offeringParams = dao.getOfferingParams(ingredientId, selectedOffering.id);
        		listOfOfferingsWithParams.add(offeringParams);
        		selectedOffering.nfpMap = offeringParams;
        	}
        	mapOfIngredients.put(ingredientId, listOfOfferingsWithParams);
        }

        // get the Recipe pattern
        Map<String, Object> recipePattern = dao.getRecipePattern(recipe);
        recipePattern.put("recipeName", recipeID.substring(recipeID.indexOf('#') + 1));

        Map<String,List<String>> adjacencyList = (Map<String, List<String>>) recipePattern.get("adjacencyList");

        Map<String,List<String>> reverseAdjacencyList = createReverseAdjacencyList(adjacencyList);
        
        List<InteractionDescriptor> interactionDescriptorList = new ArrayList<InteractionDescriptor>();
        
        for(IngredientRuntimeConfiguration irc : rrc.ircList){
    		// Create InDe for this 'Ingredient' without offeringURI
			InteractionDescriptor inDe = new InteractionDescriptor();
			inDe = generateInDe(irc.id, mapOfIngredients, adjacencyList, reverseAdjacencyList);
			
			// Add rrcURI
			inDe.rrcURI = rrc.rrcID;
			
			// Add offeringURI's to InDe's
	    	for(Offering selectedOffering : irc.listOfSelectedOfferings){
	    		inDe.offeringURI = selectedOffering.id;
	    		// inDe is complete now, can upload to offering-device directly here, or just add to list of InDe's and upload all later
	    		interactionDescriptorList.add(inDe);
	    		
	    		// Log
	    		try {
	    			logger.debug("Generated Interaction Descriptor : \n" +JsonUtils.toPrettyString(inDe));
	    		} catch (JsonGenerationException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}

	    		// Upload
	    		// InteractionDescriptor interface for any offering can be obtained by appending "/inde" to its URL
	    		String destinationURL = selectedOffering.nfpMap.get("url") + "/inde";
	    		uploadInteractionDescriptor(inDe, destinationURL);
	    	}
    	}
        return result;
    }
	
	private Map<String, List<String>> createReverseAdjacencyList(Map<String, List<String>> adjacencyList) {
		//Create the reverse version of the adjacency list
        Map<String,List<String>> reverseAdjacencyList = new HashMap<String,List<String>>();
        for(Map.Entry<String,List<String>> entry : adjacencyList.entrySet()){
        	String source = entry.getKey();
        	List<String> destList = entry.getValue();
        	if(!reverseAdjacencyList.containsKey(source)) reverseAdjacencyList.put(source, new ArrayList<String>());
        	for(String dest : destList){
        		if(!reverseAdjacencyList.containsKey(dest)){
        			List<String> srcList = new ArrayList<String>();
        			srcList.add(source);
        			reverseAdjacencyList.put(dest, srcList);
        		}
        		else{
        			List<String> srcList = reverseAdjacencyList.get(dest);
        			srcList.add(source);
        		}
        	}
        }
        return reverseAdjacencyList;
	}

	// returns a single InteractioDescriptor (InDe) for the given IngredientId
	public InteractionDescriptor generateInDe(String ingredientId, Map<String, List<Map<String, String>>> mapOfIngredients, Map<String, List<String>> adjacencyList, 
			Map<String, List<String>> reverseAdjacencyList){
		InteractionDescriptor inDe = new InteractionDescriptor();

		// set "outputs"
		List<String> outputsList = adjacencyList.get(ingredientId);
		// for each output of this ingredient, find the mapping to input and then the ingredient that consumes this input
		// the number of elements in outputsList, inputsList and destIngredientsList will always be the same
		for(String output : outputsList){

			List<String> inputsList = adjacencyList.get(output);
			Interaction interaction = new Interaction();

			if(inputsList != null && !inputsList.isEmpty()){					// inputsList will be null or empty for recipe output
				String input = inputsList.get(0);								// only 1 i/p per o/p
				// find the ingredient that consumes this input
				List<String> destIngredientsList = adjacencyList.get(input);
				String destIngredient = destIngredientsList.get(0);				// only 1 destination Ingredient per i/p
				// get the selected offerings from mapOfIngredients and set the "outputs"
				List<Map<String, String>> listOfSelectedOfferings = mapOfIngredients.get(destIngredient);
				// for each selectedOffering in listOfSelectedOfferings, set the "destination" and "outInMapping"
				for(Map<String, String> selectedOffering : listOfSelectedOfferings){
					String destinationURL = selectedOffering.get("url");
					interaction.destination.add(destinationURL);
				}
				interaction.outInMapping.put(output, input);
			}
			else{	
				// recipe output
				interaction.destination.add("dummy");
				interaction.outInMapping.put(output, "dummy");
			}
			
			inDe.outputs.put(output, interaction);
		}

		// set "inputs"
		List<String> inputsList = reverseAdjacencyList.get(ingredientId);
		for(String input : inputsList){
			inDe.inputs.add(input);
		}

		return inDe;
	}
	
	// create Ingredient Runtime Configurations
	public List<String> createIngredientRCs(List<IngredientRuntimeConfiguration> IRCList, String rrcUniqId, String recipeName, String recipeID){
		List<String> createdIRCs = new ArrayList<String>();
		String createdIRCId;
		
		for(IngredientRuntimeConfiguration irc : IRCList){
			
			//int presentCardinality = 0;		// represents presentCardinality in IngredientRuntimeConfiguration
			
			String IRCString = "";
			IRCString += "@prefix bigiot: <http://big-iot.eu/core#> .\n";
			IRCString += "@prefix rrc: <http://w3c.github.io/bigiot/offeringRecipeRuntimeConfigurationModel#> .\n";
	    	IRCString += "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n";
	    	IRCString += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
	    	IRCString += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
	    	IRCString += "@prefix schema: <http://schema.org/> .\n";
	    	IRCString += "@prefix td: <http://w3c.github.io/wot/w3c-wot-td-ontology.owl#> .\n";
	    	IRCString += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";
	    	IRCString += "@prefix smartBuilding: <http://w3c.github.io/bigiot/smartBuilding#> .\n";
	    	IRCString += "@prefix " + recipeName + ": <" + recipeID + "#> .\n";
	    	IRCString += "\n";
	    	
	    	createdIRCId = recipeName + ":" + irc.ircName;
	    	IRCString += createdIRCId;
	    	
	    	IRCString += "\n";
	    	IRCString += "\trdf:type rrc:IngredientRuntimeConfiguration ;\n";
	    	IRCString += "\trrc:hasOfferingCategory <" + irc.category + "> ;\n";
	    	
	    	for(Offering selectedOffering : irc.listOfSelectedOfferings){
	    		IRCString += "\trrc:hasOfferingInstance <" + selectedOffering.id + ">;\n";
	    		//presentCardinality++;
	    	}
	    	IRCString += "\trrc:presentCardinality \"" + irc.presentCardinality + "\"^^xsd:nonNegativeInteger ;\n";
	    	
	    	// OSR
	    	// need to create the OSRs before creating the IngredientRuntimeConfiguration
	    	if(irc.OSRList != null){
	    		List<String> createdOSRs =  createOSRs(irc.OSRList, irc.name, rrcUniqId, recipeName, recipeID);
	    		logger.debug("OSRs created for recipe runtime configuration : " + recipeName + "RRC" + rrcUniqId);

	    		for(int k = 0; k < createdOSRs.size(); k++){
	    			IRCString += "\trrc:hasOSR " + createdOSRs.get(k) + "; \n";
	    		}
	    	}
	    	
	    	// escape double quotes (")
	    	String OSRQueryString = dao.createOSRListQueryString(irc.OSRList);
	    	String OSRQueryStringEscaped = OSRQueryString.replaceAll("\"", "&quot;");
	    	IRCString += "\trrc:OSRQueryString \"" + OSRQueryStringEscaped + "\" ; \n";
	    	IRCString += ".\n";

	    	String IRCResult =  dao.createIngredientRC(irc.ircName, createdIRCId, IRCString, IngredientRuntimeConfiguration.FORMAT);
	        logger.debug("IRCResult : " + IRCResult);
	    	
	    	createdIRCs.add(createdIRCId);
		}
		return createdIRCs;
	}
	
	public List<String> createOSRs(List<OSR> OSRList, String ingredientName, String rrcUniqId, String recipeName, String recipeID){
		List<String> createdOSRs = new ArrayList<String>();
		String createdOSRId;
		
		for(OSR thisOSR : OSRList){

			String uniqName = ingredientName + "_" + thisOSR.name + rrcUniqId;
			
			String OSRString = "";
			OSRString += "@prefix bigiot: <http://big-iot.eu/core#> .\n";
			OSRString += "@prefix rrc: <http://w3c.github.io/bigiot/offeringRecipeRuntimeConfigurationModel#> .\n";
	    	OSRString += "@prefix owl: <http://www.w3.org/2002/07/owl#> .\n";
	    	OSRString += "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
	    	OSRString += "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
	    	OSRString += "@prefix schema: <http://schema.org/> .\n";
	    	OSRString += "@prefix td: <http://w3c.github.io/wot/w3c-wot-td-ontology.owl#> .\n";
	    	OSRString += "@prefix xsd: <http://www.w3.org/2001/XMLSchema#> .\n";
	    	OSRString += "@prefix smartBuilding: <http://w3c.github.io/bigiot/smartBuilding#> .\n";
	    	OSRString += "@prefix " + recipeName + ": <" + recipeID + "#> .\n";
	    	OSRString += "\n";
	    	
	    	createdOSRId = recipeName + ":" + uniqName;
	    	OSRString += createdOSRId;
	    	
	    	OSRString += "\n";
	    	OSRString += "\trdf:type rrc:" + thisOSR.OSRCategory + " ;\n";
	    	OSRString += "\tschema:name \"" + thisOSR.name + "\" ;\n";
	    	OSRString += "\trdfs:label \"" + thisOSR.label + "\" ;\n";
	    	OSRString += "\trrc:OSRCategory \"" + thisOSR.OSRCategory + "\" ;\n";
	    	
	    	if(thisOSR instanceof DirectOSR || thisOSR instanceof OperatorOSR){
	    		
	    		OSRString += "\tschema:value \"" + thisOSR.value + "\" ;\n";
	    		OSRString += "\trrc:OSRValueType \"" + thisOSR.OSRValueType + "\" ;\n";
	    	}
	    	if(thisOSR instanceof OperatorOSR){
	    		OperatorOSR opOSR = (OperatorOSR) thisOSR;
				//Operator
	    		OSRString += "\trrc:operator \"" + opOSR.operator + "\" ;\n";
			}else if(thisOSR instanceof ComplexOSR){
				ComplexOSR cOSR = (ComplexOSR) thisOSR;
				//Rule
				OSRString += "\trrc:rule \"" + cOSR.rule + "\" ;\n";
				
				switch (cOSR.rule) {
		            case "sameAs":
		            	SameAsCOSR saCOSR = (SameAsCOSR) cOSR;
		            	//Property, OfferingURI
			    		OSRString += "\trrc:property \"" + saCOSR.property + "\" ;\n";
			    		OSRString += "\trrc:offering <" + saCOSR.offeringURI + "> ;\n";
		                break;
			        case "cardinality":
			        	CardinalityCOSR cCOSR = (CardinalityCOSR) cOSR;
			         	// min,max Cardinality
				    	OSRString += "\trrc:minCardinality \"" + cCOSR.minCardinality + "\"^^xsd:nonNegativeInteger ;\n";
				    	OSRString += "\trrc:maxCardinality \"" + cCOSR.maxCardinality + "\"^^xsd:nonNegativeInteger ;\n";
			        	break; 
			        case "combinedPrice":
			        	CombinedPriceCOSR cpCOSR = (CombinedPriceCOSR) cOSR;
			         	// operator
				    	OSRString += "\trrc:operator \"" + cpCOSR.operator + "\" ;\n";
			        	break;
		            default: 
		                break;
				}
			}
	    	
	    	OSRString += ".\n";

	    	String OSRResult =  dao.createOSR(uniqName, createdOSRId, OSRString, OSR.FORMAT);
	        logger.debug("OSRResult : " + OSRResult);
	    	
	    	createdOSRs.add(createdOSRId);
		}
		return createdOSRs;
	}
	
	// Part of the Repository
	@RequestMapping(value = "/register_offering", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> registerOffering(@RequestBody Map<String, Object> offeringDescription) {
		
		Map<String, String> feedback = new HashMap<>();		// success or error
		
		// store OD in triple store
		// Dynamic Discovery: Go thorough the Recipe Runtime Configurations (RRCs) and their OSRs to find the RRC where this offering could be deployed
		// Generate Interaction Descriptors (InDe's) for this offering and for all the offerings this offering will directly be communicating with.
		// Upload the InDe's to all of these offering devices and get feedbacks from all
		// return the feedback map
		
		return feedback;
	}
	
	// Part of the Repository
	@RequestMapping(value = "/update_offering", method = RequestMethod.PUT, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateOffering(@RequestBody Map<String, Object> offeringDescription) {
		
		Map<String, String> feedback = new HashMap<>();		// success or error

		return feedback;
	}

	@RequestMapping(value = "/namespace/{file:.+}", method = RequestMethod.GET, produces = "application/json")
	public ModelAndView getNameSpace(@PathVariable("file") String file) {
		logger.debug("Namespace file : " + file + " is requested!");

		ModelAndView model = new ModelAndView();
		model.setViewName("namespace/" + file);
		model.addObject("title", backendService.getTitle());
		model.addObject("msg", backendService.getDesc());

		return model;
	}

	@RequestMapping(value = "/{path:.+}", method = RequestMethod.GET)
	public ModelAndView anyPage(@PathVariable("path") String path) {
		logger.debug("Page : " + path + " is requested!");

		ModelAndView model = new ModelAndView();
		model.setViewName(path);
		model.addObject("title", backendService.getTitle());
		model.addObject("msg", backendService.getDesc());

		return model;
	}

	// Gateway services

	@RequestMapping(value = "/gateway/getGeoCordinates", params = "userAddress", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getGeoCordinates(
			@RequestParam(value = "userAddress", required = true) String userAddress) {
		logger.debug("Accessed gateway service getGeoCordinates : userAddress=" + userAddress);
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Double> cordinates = GoogleAPIs.getGeoCordinates(userAddress);
		result.put("GeoCoordinates", cordinates);
		return result;
	}

	@RequestMapping(value = "/gateway/getDirections", consumes = "application/json", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getDirections(@RequestBody Map<String, Object> requestParams) {
		logger.debug("Accessed offering getDirections :");
		Map<String, Double> source = (Map<String, Double>) requestParams.get("source");
		Map<String, Double> destination = (Map<String, Double>) requestParams.get("destination");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("directions", GoogleAPIs.getDirections(source.get("latitude"), source.get("longitude"),
				destination.get("latitude"), destination.get("longitude")));
		return result;
	}

	@RequestMapping(value = "/gateway/getAllStations", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getGeoCordinates() {
		logger.debug("Accessed gateway service getAllStations");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("allStations", MindboxAPIs.getAllStations());
		return result;
	}

	@RequestMapping(value = "/gateway/getAllParkSites", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getAllParkSites() {
		logger.debug("Accessed gateway service getAllParkSites");
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("parkingLocations", MindboxAPIs.getAllParkSites());
		return result;
	}

	@RequestMapping(value = "/gateway/getNearestParkSite", consumes = "application/json", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getNearestParkSite(@RequestBody Map<String, Object> requestParams) {
		logger.debug("Accessed offering getNearestParkSite :");
		Map<String, Double> userCoordinates = (Map<String, Double>) requestParams.get("userCoordinates");
		Double userLatitude = userCoordinates.get("latitude");
		Double userLongitude = userCoordinates.get("longitude");
		Map<String, Object> allParkSites = (Map<String, Object>) requestParams.get("allParkSites");
		logger.debug("userLatitude = " + userLatitude);
		logger.debug("userLongitude = " + userLongitude);

		String nearesrParkName = null;
		Double nearesrParkLatitude = null;
		Double nearesrParkLongitude = null;
		double minDist = Double.MAX_VALUE;
		for (Map.Entry<String, Object> entry : allParkSites.entrySet()) {
			Map<String, Double> cordinates = (Map<String, Double>) entry.getValue();
			Double parkLatitude = cordinates.get("latitude");
			Double parkLongitude = cordinates.get("longitude");
			double dist = Math.pow(userLatitude - parkLatitude, 2) + Math.pow(userLongitude - parkLongitude, 2);
			if (dist < minDist) {
				minDist = dist;
				nearesrParkName = entry.getKey();
				nearesrParkLatitude = parkLatitude;
				nearesrParkLongitude = parkLongitude;
			}
		}

		Map<String, Double> nearesrParkCoordinates = new HashMap<String, Double>();
		nearesrParkCoordinates.put("latitude", nearesrParkLatitude);
		nearesrParkCoordinates.put("longitude", nearesrParkLongitude);

		Map<String, Object> result = new HashMap<String, Object>();
		result.put("nearesrParkName", nearesrParkName);
		result.put("nearesrParkCoordinates", nearesrParkCoordinates);

		return result;
	}

	@RequestMapping(value = "/gateway/sendEmail", consumes = "application/json", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> sendEmail(@RequestBody Map<String, Object> requestParams) {
		logger.debug("Accessed offering sendEmail :");
		String receiverAddress = (String) requestParams.get("receiverAddress");
		String subject = (String) requestParams.get("subject");
		String body = (String) requestParams.get("body");
		// Map<String,Object> allParkSites = (Map<String, Object>)
		// requestParams.get("allParkSites");
		logger.debug("receiverAddress = " + receiverAddress);
		logger.debug("subject = " + subject);
		logger.debug("body = " + body);
		GoogleAPIs.sendEmail(receiverAddress, "Route to Parking Spot : " + subject, body);
		return new HashMap<String, Object>();
	}

	@RequestMapping(value = "/gateway/lightSensor", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> lightSensor() {
		logger.debug("Accessed offering lightSensor :");
		Double illumination = 2000d;

		try {
			CoapClient client = new CoapClient("coap://127.0.0.1:5685/lightSensor/illumination");
			CoapResponse response = client.get();
			String payload = new String(response.getPayload());
			illumination = Double.parseDouble(payload);
			logger.debug("illumination = " + illumination);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String switchValue = illumination < 100 ? "1" : "0";
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("switch_value", switchValue);
		return result;
	}

	@RequestMapping(value = "/gateway/controlLight/{light:.+}", consumes = "application/json", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> controlLight(@PathVariable("light") String light,
			@RequestBody Map<String, Object> requestParams) {
		logger.debug("Called controlLight : " + light);
		String status = (String) requestParams.get("status");
		logger.debug("status = " + status);

		String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:switchableRecordType xmlns:ns2=\"http://www.baas-itea3.eu/temperature\"><switchableValue>";
		String postfix = "</switchableValue></ns2:switchableRecordType>";
		boolean switchOn = status.equals("1");
		String lightControlString = prefix + String.valueOf(switchOn) + postfix;

		try {
			int index = Integer.parseInt(light.substring(6));
			String url = lightURLs[index];
			CoapClient client = new CoapClient(url);
			CoapResponse response = client.put(lightControlString, MediaTypeRegistry.TEXT_PLAIN);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HashMap<String, Object>();
	}

	@RequestMapping(value = "/gateway/controlAllLights", consumes = "application/json", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> controlAllLights(@RequestBody Map<String, Object> requestParams) {
		logger.debug("Called controlAllLights");
		String status = (String) requestParams.get("status");
		logger.debug("status = " + status);

		String prefix = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ns2:switchableRecordType xmlns:ns2=\"http://www.baas-itea3.eu/temperature\"><switchableValue>";
		String postfix = "</switchableValue></ns2:switchableRecordType>";
		boolean switchOn = status.equals("1");
		String lightControlString = prefix + String.valueOf(switchOn) + postfix;

		for (String url : lightURLs) {
			try {
				CoapClient client = new CoapClient(url);
				CoapResponse response = client.put(lightControlString, MediaTypeRegistry.TEXT_PLAIN);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return new HashMap<String, Object>();
	}

	private String[] lightURLs = new String[] {
			"coap://172.31.0.130:5683/JS_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource",
			"coap://172.31.0.130:5683/AZ_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource",
			"coap://172.31.0.130:5683/NV_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource",
			"coap://172.31.0.130:5683/D1_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource",
			"coap://172.31.0.131:5683/MB_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource",
			"coap://172.31.0.131:5683/TT_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource",
			"coap://172.31.0.131:5683/AR_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource",
			"coap://172.31.0.131:5683/ST_Gen_BaasSDCLuminaireControllerService/SDCLuminaireControlerLightSwitchableswitchableEVRootResource" };

	// Mock services

	@RequestMapping(value = "/test/GetParkingSpaceInfo", params = "destinationArea", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getParkingSpaceInfo(
			@RequestParam(value = "destinationArea", required = true) String destinationArea) {
		logger.debug("Accessed offering GetParkingSpaceInfo : destinationArea=" + destinationArea);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("parkingStatus", true);
		result.put("parkingSpaceId", "85672217");
		return result;
	}

	@RequestMapping(value = "/test/ParkingSpaceReservation", consumes = "application/json", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> parkingSpaceReservation(@RequestBody Map<String, Object> requestParams) {
		logger.debug("Accessed offering ParkingSpaceReservation :");
		String parkingSpaceId = (String) requestParams.get("parkingSpaceId");
		Object paymentData = requestParams.get("paymentData");
		logger.debug("parkingSpaceId = " + parkingSpaceId);
		logger.debug("paymentData = " + paymentData.toString());
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("parkingReservationStatus", "Successfully reserved the parking spot " + parkingSpaceId);
		return result;
	}

	@RequestMapping(value = "/test/GetTrafficInformation", params = "areaSpecification", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map<String, Object> getTrafficInformationOffering(
			@RequestParam(value = "areaSpecification", required = true) String areaSpecification) {
		logger.debug("Accessed offering GetTrafficInformationOffering : areaSpecification=" + areaSpecification);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("trafficSpeed", Math.random() * 100);
		result.put("travelTime", Math.random() * 20);
		return result;
	}

	@RequestMapping(value = "/test/MonitorTrafficAlarms", consumes = "application/json", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Map<String, Object> monitorTrafficAlarms(@RequestBody Map<String, Object> requestParams) {
		logger.debug("Accessed offering MonitorTrafficAlarms :");
		double trafficSpeed = (double) requestParams.get("trafficSpeed");
		double travelTime = (double) requestParams.get("travelTime");
		double speedThreshold = (double) requestParams.get("speedThreshold");
		double travelTimeThreshold = (double) requestParams.get("travelTimeThreshold");
		logger.debug("trafficSpeed = " + trafficSpeed);
		logger.debug("travelTime = " + travelTime);
		logger.debug("speedThreshold = " + speedThreshold);
		logger.debug("travelTimeThreshold = " + travelTimeThreshold);
		String notification = "";
		if (trafficSpeed < speedThreshold)
			notification = "Traffic speed is lower than the threshold!!!";
		else if (travelTime > travelTimeThreshold)
			notification = "Travel time is higher than the threshold!!!";
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("trafficNotification", notification);
		return result;
	}
	
	/*
	 *  Offering device: <http://w3c.github.io/bigiot/DummySwitch2Offering>
	 *  URL: "http://localhost:8080/marketplace/gateway/dummySwitch2"
	 *  InDe i/f: "http://localhost:8080/marketplace/gateway/dummySwitch2/inde"
	 */
	@RequestMapping(value = "/gateway/dummySwitch2/inde", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> receiveInDeDummySwitch2(@RequestBody Map<String, Object> interactionDescriptor) {
		Map<String,String> result = new HashMap<String,String>();
		logger.debug("Accessed dummySwitch2 offering device");
		logger.debug("Received InteractionDescriptor : \n" + interactionDescriptor);
		InteractionDescriptor inDe = new InteractionDescriptor(interactionDescriptor);
		// do something with inDe
		result.put("status", "success");
		result.put("message", "Interaction Descriptor upload onto DummySwitch2 successful!");
		return result;
	}
	
	/*
	 *  Offering device: <http://w3c.github.io/bigiot/DummyLight2Offering>
	 *  URL: "http://localhost:8080/marketplace/gateway/dummyLight2"
	 *  InDe i/f: "http://localhost:8080/marketplace/gateway/dummyLight2/inde"
	 */
	@RequestMapping(value = "/gateway/dummyLight2/inde", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> receiveInDeDummyLight2(@RequestBody Map<String, Object> interactionDescriptor) {
		Map<String,String> result = new HashMap<String,String>();
		logger.debug("Accessed DummyLight2 offering device");
		logger.debug("Received InteractionDescriptor : \n" + interactionDescriptor);
		InteractionDescriptor inDe = new InteractionDescriptor(interactionDescriptor);
		// do something with inDe
		result.put("status", "success");
		result.put("message", "Interaction Descriptor upload onto DummyLight2 successful!");
		return result;
	}
	
	public Map<String,Object> uploadInteractionDescriptor(InteractionDescriptor inDe, String destURL) {
		try {
			Map<String,Object> outputParams = new HashMap<>();
			HttpClient httpClient = HttpClientBuilder.create().build();
			StringEntity entity = new StringEntity(JsonUtils.toPrettyString(inDe));
			HttpPost httpPost = new HttpPost(destURL);
			httpPost.setHeader("Content-type", "application/json");
			httpPost.setEntity(entity);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if(200 == httpResponse.getStatusLine().getStatusCode()){
				HttpEntity httpEntity = httpResponse.getEntity();
				InputStream is = httpEntity.getContent();
				outputParams = (Map<String,Object>) JsonUtils.fromInputStream(is, "iso-8859-1");
				logger.debug("Interaction Descriptor upload onto [" + destURL + "] successful!");
			}
			else{
				logger.debug("Error uploading Interaction Descriptor onto [" + destURL + "]!");
				logger.debug("HTTP status code : " + httpResponse.getStatusLine().getStatusCode());
			}
			return outputParams;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
