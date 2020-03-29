package eu.bigiot.marketplace.database;

import com.github.jsonldjava.utils.JsonUtils;
import eu.bigiot.marketplace.model.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BIGIoT_DAO {
		
	private final Logger logger = LoggerFactory.getLogger(BIGIoT_DAO.class);
	
	public static final String rdfFilePath = "src/main/resources/rdf_store.ttl";
	public static final String rdfFormat = "TTL";
	
	private Model rdfModel = null;
	private InfModel infModel = null;
	
	private Property name,description,icon,type,category;
	private Property hasIngredient,hasInteraction,hasInput,hasOutput,rdfType,hasConfigAttributes;
	private Property dataType;
	private Property hasIngredientFrom, hasIngredientTo, hasIngredientInput, hasIngredientOutput;
	private Property endpoint, endpointUrl, endpointType, priceSpecification, priceAmount, priceCurrency, accountingModel, license, region;
	private Property hasOperator, hasVariable, hasValue;
	private Property initType, initVariable, initValue, incrementVariable, incrementValue;
	private Property label, value, OSRCategory, OSRValueType, operator, rule, property, offeringURI;
	private Property minCardinality, maxCardinality, operatorCP;
	
	
	public BIGIoT_DAO(Model rdfModel, InfModel infModel) {
		this.rdfModel = rdfModel;
		this.infModel = infModel;
	}
	
	
	public void initDB(){
		logger.debug("Database initialization started!");
		try{
			
			//Fetch the properties of the model
			name = rdfModel.getProperty(Namespace.NAME);
			description = rdfModel.getProperty(Namespace.DESCRIPTION);
			icon = rdfModel.getProperty(Namespace.ICON);
			type = rdfModel.getProperty(Namespace.TYPE);
			category = rdfModel.getProperty(Namespace.CATEGORY);
			
			hasIngredient = rdfModel.getProperty(Namespace.HAS_INGREDIENT);
			hasInteraction = rdfModel.getProperty(Namespace.HAS_INTERACTION);
			hasInput = rdfModel.getProperty(Namespace.HAS_INPUT);
			hasOutput = rdfModel.getProperty(Namespace.HAS_OUTPUT);
			rdfType = rdfModel.getProperty(Namespace.RDF_TYPE);
			hasConfigAttributes = rdfModel.getProperty(Namespace.HAS_CONFIG_ATTRIBUTE);
			
			dataType = rdfModel.getProperty(Namespace.DATA_TYPE);
			
			hasIngredientFrom = rdfModel.getProperty(Namespace.HAS_INGREDIENT_FROM);
			hasIngredientTo = rdfModel.getProperty(Namespace.HAS_INGREDIENT_TO);
			hasIngredientInput = rdfModel.getProperty(Namespace.HAS_INGREDIENT_INPUT);
			hasIngredientOutput = rdfModel.getProperty(Namespace.HAS_INGREDIENT_OUTPUT);
			
			endpoint = rdfModel.getProperty(Namespace.ENDPOINT);
			endpointUrl = rdfModel.getProperty(Namespace.ENDPOINT_URL);
			endpointType = rdfModel.getProperty(Namespace.ENDPOINT_TYPE);
			priceSpecification = rdfModel.getProperty(Namespace.PRICE_SPECIFICATION);
			priceAmount = rdfModel.getProperty(Namespace.PRICE_AMOUNT);
			priceCurrency = rdfModel.getProperty(Namespace.PRICE_CURRENCY);
			accountingModel = rdfModel.getProperty(Namespace.ACCOUNTING_MODEL);
			license = rdfModel.getProperty(Namespace.LICENSE);
			region = rdfModel.getProperty(Namespace.REGION);
			
			hasOperator = rdfModel.getProperty(Namespace.HAS_OPERATOR);
			hasVariable = rdfModel.getProperty(Namespace.HAS_VARIABLE);
			hasValue = rdfModel.getProperty(Namespace.HAS_VALUE);
			
			initType = rdfModel.getProperty(Namespace.INIT_TYPE);
			initVariable = rdfModel.getProperty(Namespace.INIT_VARIABLE);
			initValue = rdfModel.getProperty(Namespace.INIT_VALUE);
			incrementVariable = rdfModel.getProperty(Namespace.INCREMENT_VARIABLE);
			incrementValue = rdfModel.getProperty(Namespace.INCREMENT_VALUE);
			
			label = rdfModel.getProperty(Namespace.LABEL);
			value = rdfModel.getProperty(Namespace.VALUE);
			OSRCategory = rdfModel.getProperty(Namespace.OSR_CATEGORY);
			OSRValueType = rdfModel.getProperty(Namespace.OSR_VALUE_TYPE);
			operator = rdfModel.getProperty(Namespace.OSR_OPERATOR);
			property = rdfModel.getProperty(Namespace.OSR_PROPERTY);
			offeringURI = rdfModel.getProperty(Namespace.OSR_OFFERINGURI);
			rule = rdfModel.getProperty(Namespace.OSR_RULE);
			minCardinality = rdfModel.getProperty(Namespace.OSR_MIN_CARDINALITY);
			maxCardinality = rdfModel.getProperty(Namespace.OSR_MAX_CARDINALITY);
			operatorCP = rdfModel.getProperty(Namespace.OSR_OPERATOR_CP);

		}catch(Exception e){
			logger.error("Database initialization failed!", e);
			rdfModel = null;
			return;
		}
		logger.debug("Database initialization successful!");
	}
	

	public List<City> getCities(){
		List<City> list_of_cities = new ArrayList<City>();
		if(rdfModel==null) return list_of_cities;
		try{
			String locationsQueryString = 
					"PREFIX marketplace: <" + Namespace.MARKETPLACE + "> " +
					"SELECT ?city " +
					"WHERE {" +
					"      ?city a marketplace:City . " +
					"      }";
			
			Query locationsQuery = QueryFactory.create(locationsQueryString);
			QueryExecution locationsQueryEx = QueryExecutionFactory.create(locationsQuery, rdfModel);
			logger.debug("getCities Query: \n" +locationsQueryEx.getQuery().toString());
			ResultSet locationsQueryResults = locationsQueryEx.execSelect();
			
		    while (locationsQueryResults.hasNext()) {
		    	Resource city = locationsQueryResults.next().getResource("city");
		    	String nm = city.hasProperty(name) ? city.getProperty(name).getString() : null;
		    	String img = city.hasProperty(icon) ? city.getProperty(icon).getString() : null;
		    	list_of_cities.add(new City(nm, img));
		    }
		    locationsQueryEx.close();
			return list_of_cities;
		}catch(Exception e){
			logger.error("Error occurred while fetching cities!", e);
			return list_of_cities;
		}
	}
	
	
	public List<Service> getServices(String city_name){
		List<Service> list_of_services = new ArrayList<Service>();
		if(rdfModel==null) return list_of_services;
		try{
			String servicesQueryString = 
					"PREFIX marketplace: <" + Namespace.MARKETPLACE + "> " +
					"SELECT ?service " +
					"WHERE {" +
					"      ?service a marketplace:Service . " +
					"      }";
			
			Query servicesQuery = QueryFactory.create(servicesQueryString);
			QueryExecution servicesQueryEx = QueryExecutionFactory.create(servicesQuery, rdfModel);
			logger.debug("getServices Query: \n" +servicesQueryEx.getQuery().toString());
			ResultSet servicesQueryResults = servicesQueryEx.execSelect();
			
		    while (servicesQueryResults.hasNext()) {
		    	Resource service = servicesQueryResults.next().getResource("service");
		    	String nm = service.hasProperty(name) ? service.getProperty(name).getString() : null;
		    	String img = service.hasProperty(icon) ? service.getProperty(icon).getString() : null;
		    	list_of_services.add(new Service(nm, img));
		    }
		    servicesQueryEx.close();
			return list_of_services;
		}catch(Exception e){
			logger.error("Error occurred while fetching services!", e);
			return list_of_services;
		}
	}
	
	
	public List<Recipe> getRecipes(String service_name){
		List<Recipe> list_of_recipes = new ArrayList<Recipe>();
		if(rdfModel==null) return list_of_recipes;
		try{
			String recipesQueryString = 
					"PREFIX schema: <" + Namespace.SCHEMA + "> " +
					"PREFIX offeringRecipe: <" + Namespace.OFFERING_RECIPE + "> " +
					"PREFIX marketplace: <" + Namespace.MARKETPLACE + "> " +
					"PREFIX skos: <" + Namespace.SKOS + "> " +
					"SELECT DISTINCT ?recipe " +
					"WHERE {" +
					"      ?service a marketplace:Service . " +
					"      ?service schema:name \""+ service_name +"\" . " +
					"      ?service schema:category ?category . " +
					"      ?recipe a offeringRecipe:Recipe . " +
					"      {{ ?recipe schema:category ?category } UNION { " +
					"         ?recipe schema:category ?subCategory . " +
					"         ?category skos:narrower* ?subCategory }} " +
					"      }";
			
			Query recipesQuery = QueryFactory.create(recipesQueryString);
			QueryExecution recipesQueryEx = QueryExecutionFactory.create(recipesQuery, rdfModel);
			logger.debug("getRecipes Query: \n" +recipesQueryEx.getQuery().toString());
			ResultSet recipesQueryResults = recipesQueryEx.execSelect();
			
		    while (recipesQueryResults.hasNext()) {
		    	Resource recipe = recipesQueryResults.next().getResource("recipe");
		    	String id = recipe.getURI();
		    	String name = id.substring(id.indexOf('#') + 1);
		    	String des = recipe.hasProperty(description) ? recipe.getProperty(description).getString() : "No description available";
		    	list_of_recipes.add(new Recipe(id, name, des));
		    }
		    recipesQueryEx.close();
			return list_of_recipes;
		}catch(Exception e){
			logger.error("Error occurred while fetching recipes!", e);
			return list_of_recipes;
		}
	}
	
	
	public Map<String, Object> getRecipeModel(String recipeID){
	
		try{
			if(rdfModel==null) return null;
			
			String recipeQueryString = "DESCRIBE <" + recipeID + "> ";
			
			Query recipeQuery = QueryFactory.create(recipeQueryString);
			QueryExecution recipeQueryEx = QueryExecutionFactory.create(recipeQuery, rdfModel);
			Model resultModel = recipeQueryEx.execDescribe();

			if(resultModel!=null){
				StringWriter out = new StringWriter();
				resultModel.write(out, "JSON-LD");
				recipeQueryEx.close();
				System.out.println("recipe model : "+out.toString());
				return (Map<String, Object>) JsonUtils.fromString(out.toString());
			}
			else{
				recipeQueryEx.close();
				return null;
			}
			
		}catch(Exception e){
			logger.error("Error occurred while fetching recipes!", e);
			return null;
		}
	}
	
	public List<OSR> getOSRList(){
		return getOSRList(rdfModel);
	}
	
	private List<OSR> getOSRList(Model rdfModel) {
		List<OSR> OSRList = new ArrayList<OSR>(); // OSR: Offering Selection Rule
		
		try{
			if(rdfModel==null) return OSRList;
   				
    		String OSRQueryString = 
					"PREFIX rdfs: <" + Namespace.RDFS + "> " +
					"PREFIX rdf: <" + Namespace.RDF + "> " +
					"PREFIX rrc: <" + Namespace.OFFERING_RECIPE_RUNTIME_CONFIG + "> " +
					"SELECT ?OSR " +
					"WHERE {" +
					"      		?OSR rdf:type ?o ."	+
					"			FILTER (?o IN (rrc:OSR, rrc:DirectOSR, rrc:OperatorOSR, rrc:ComplexOSR)) . " +
					"      }";
			
    		
			Query OSRQuery = QueryFactory.create(OSRQueryString);
			QueryExecution OSRQueryEx = QueryExecutionFactory.create(OSRQuery, rdfModel);
			logger.debug("Global OSR Query: \n" +OSRQueryEx.getQuery().toString());
			ResultSet OSRQueryResults = OSRQueryEx.execSelect();
    		
			while (OSRQueryResults.hasNext()) {
				Resource thisOSR = OSRQueryResults.next().getResource("OSR");
				String lbl = thisOSR.hasProperty(label) ? thisOSR.getProperty(label).getString() : null;
				String nm = thisOSR.hasProperty(name) ? thisOSR.getProperty(name).getString() : null;
				String val = thisOSR.hasProperty(value) ? thisOSR.getProperty(value).getString() : null;
				String cat = thisOSR.hasProperty(OSRCategory) ? thisOSR.getProperty(OSRCategory).getString() : null;
				String valType = thisOSR.hasProperty(OSRValueType) ? thisOSR.getProperty(OSRValueType).getString() : null;
				if(OSR.OSRCATEGORY_DIRECTOSR.equals(cat)){
					OSRList.add(new DirectOSR(nm, lbl, val, valType, cat));
				}else if(OSR.OSRCATEGORY_OPERATOROSR.equals(cat)){
					String op = thisOSR.hasProperty(operator) ? thisOSR.getProperty(operator).getString() : null;
					OSRList.add(new OperatorOSR(nm, lbl, val, valType, cat, op));
				}else if(OSR.OSRCATEGORY_COMPLEXOSR.equals(cat)){
					String rl = thisOSR.hasProperty(rule) ? thisOSR.getProperty(rule).getString() : null;
					switch (rl) {
					case "sameAs":
						String prop = thisOSR.hasProperty(property) ? thisOSR.getProperty(property).getString() : null;
						String offURI = thisOSR.hasProperty(offeringURI) ? thisOSR.getProperty(offeringURI).getString() : null;
						OSRList.add(new SameAsCOSR(nm, lbl, val, valType, cat, rl, prop, offURI));
						break;
					case "cardinality":
						int minCard = thisOSR.hasProperty(minCardinality) ? thisOSR.getProperty(minCardinality).getInt() : 1;
						int maxCard = thisOSR.hasProperty(maxCardinality) ? thisOSR.getProperty(maxCardinality).getInt() : 1;
						OSRList.add(new CardinalityCOSR(nm, lbl, val, valType, cat, rl, minCard, maxCard));
						break; 
					case "combinedPrice":
						String op = thisOSR.hasProperty(operatorCP) ? thisOSR.getProperty(operatorCP).getString() : null;
						OSRList.add(new CombinedPriceCOSR(nm, lbl, val, valType, cat, rl, op));
						break;
					default: 
						break;
					}
				}
			}
    		logger.debug("OSRs ResultSet:" +OSRList);
    		OSRQueryEx.close();

			return OSRList;
			
		}catch(Exception e){
			logger.error("Error occurred while fetching global OSRList!", e);
			return null;
		}
	}
	
	public List<OSR> getGlobalOSRList(){
		return getGlobalOSRList(rdfModel);
	}
	
	// getGlobalOSRList - Fetch general OSRs to facilitate the user in defining custom OSRs for offerings
	private List<OSR> getGlobalOSRList(Model rdfModel) {
		List<OSR> OSRList = new ArrayList<OSR>(); // OSR: Offering Selection Rule
		
		try{
			if(rdfModel==null) return OSRList;
   				
			String OSRQueryString = 
					"PREFIX rdfs: <" + Namespace.RDFS + "> " +
					"PREFIX rrc: <" + Namespace.OFFERING_RECIPE_RUNTIME_CONFIG + "> " +
					"SELECT ?OSR " +
					"WHERE {" +
					"      		?OSR rdfs:subClassOf* rrc:OSR . " +
					"      }";
    		
			Query OSRQuery = QueryFactory.create(OSRQueryString);
			QueryExecution OSRQueryEx = QueryExecutionFactory.create(OSRQuery, rdfModel);
			logger.debug("Global OSR Query: \n" +OSRQueryEx.getQuery().toString());
			ResultSet OSRQueryResults = OSRQueryEx.execSelect();
    		
			while (OSRQueryResults.hasNext()) {
				Resource thisOSR = OSRQueryResults.next().getResource("OSR");
				String lbl = thisOSR.hasProperty(label) ? thisOSR.getProperty(label).getString() : null;
				// filter out the OSR classes
				if(!OSR.OSRCATEGORY_OSR.equals(lbl) && !OSR.OSRCATEGORY_DIRECTOSR.equals(lbl) 
						&& !OSR.OSRCATEGORY_OPERATOROSR.equals(lbl) && !OSR.OSRCATEGORY_COMPLEXOSR.equals(lbl)
						&& !ComplexOSR.COMPLEXOSR_CATEGORY_SAMEAS.equals(lbl)){
					String nm = thisOSR.hasProperty(name) ? thisOSR.getProperty(name).getString() : null;
					String val = thisOSR.hasProperty(value) ? thisOSR.getProperty(value).getString() : null;
					String cat = thisOSR.hasProperty(OSRCategory) ? thisOSR.getProperty(OSRCategory).getString() : null;
					String valType = thisOSR.hasProperty(OSRValueType) ? thisOSR.getProperty(OSRValueType).getString() : null;
					if(OSR.OSRCATEGORY_DIRECTOSR.equals(cat)){
						OSRList.add(new DirectOSR(nm, lbl, val, valType, cat));
					}else if(OSR.OSRCATEGORY_OPERATOROSR.equals(cat)){
						String op = thisOSR.hasProperty(operator) ? thisOSR.getProperty(operator).getString() : null;
						OSRList.add(new OperatorOSR(nm, lbl, val, valType, cat, op));
					}else if(OSR.OSRCATEGORY_COMPLEXOSR.equals(cat)){
						String rl = thisOSR.hasProperty(rule) ? thisOSR.getProperty(rule).getString() : null;
						switch (rl) {
						case "sameAs":
							String prop = thisOSR.hasProperty(property) ? thisOSR.getProperty(property).getString() : null;
							String offURI = thisOSR.hasProperty(offeringURI) ? thisOSR.getProperty(offeringURI).getString() : null;
							OSRList.add(new SameAsCOSR(nm, lbl, val, valType, cat, rl, prop, offURI));
							break;
						case "cardinality":
							int minCard = thisOSR.hasProperty(minCardinality) ? thisOSR.getProperty(minCardinality).getInt() : 1;
							int maxCard = thisOSR.hasProperty(maxCardinality) ? thisOSR.getProperty(maxCardinality).getInt() : 1;
							OSRList.add(new CardinalityCOSR(nm, lbl, val, valType, cat, rl, minCard, maxCard));
							break; 
						case "combinedPrice":
							String op = thisOSR.hasProperty(operatorCP) ? thisOSR.getProperty(operatorCP).getString() : null;
							OSRList.add(new CombinedPriceCOSR(nm, lbl, val, valType, cat, rl, op));
							break;
						default: 
							break;
						}
					}
				}
			}
    		logger.debug("OSRs ResultSet:" +OSRList);
    		OSRQueryEx.close();

			return OSRList;
			
		}catch(Exception e){
			logger.error("Error occurred while fetching global OSRList!", e);
			return null;
		}
	}
	
	public Map<String,Object> getRecipePattern(String recipeID){
		return getRecipePattern(rdfModel, recipeID);
	}
	
	/**
	 * 
	 * @param rdfModel
	 * @param recipeID
	 * @return
	 */
	public Map<String,Object> getRecipePattern(Model rdfModel, String recipeID){
		Map<String,Object> recipePattern = new HashMap<String,Object>();
		
		try{
			if(rdfModel==null) return recipePattern;
			
			Map<String,List<String>> adjacencyList = new HashMap<String, List<String>>();
			Map<String,String> elementLabels = new HashMap<String,String>();
			Map<String,String> elementTypes = new HashMap<String,String>();
			Map<String,String> dataTypes = new HashMap<String,String>();
			Map<String,Map<String,String>> configAttributes = new HashMap<String,Map<String,String>>();
			
			String recipeName = null, recipeDescription = null, recipeCategory = null;
			Resource recipe = rdfModel.getResource(recipeID);
			if(recipe != null) {
				recipeName = recipeID.substring(recipeID.indexOf('#')+1);
				if(recipe.hasProperty(description)) recipeDescription = recipe.getProperty(description).getString();
				if(recipe.hasProperty(category)) recipeCategory = rdfModel.shortForm(recipe.getProperty(category).getResource().getURI());
			}
			
			
		    String ingredientsQueryString = 
					"PREFIX offeringRecipe: <" + Namespace.OFFERING_RECIPE + "> \n" +
					"PREFIX bigiot: <" + Namespace.BIGIOT + "> \n" +
					"PREFIX rdfs: <" + Namespace.RDFS + "> \n" +
					"SELECT ?ingredient \n" +
					"WHERE {\n" +
					"      {{ ?ingredient a bigiot:Offering } UNION { \n" +
					"         ?ingredient a ?class . \n" +
					"         ?class rdfs:subClassOf bigiot:Offering }} . \n" +
					"      <" + recipeID + "> offeringRecipe:hasIngredient ?ingredient \n" +
					"      }";
		    
		    logger.debug("ingredientsQueryString Query: \n" + ingredientsQueryString + "\n");
			
			Query ingredientsQuery = QueryFactory.create(ingredientsQueryString);
			QueryExecution ingredientsQueryEx = QueryExecutionFactory.create(ingredientsQuery, rdfModel);
			ResultSet ingredientsQueryResults = ingredientsQueryEx.execSelect();

		    while (ingredientsQueryResults.hasNext()) {
		    	Resource ingredient = ingredientsQueryResults.next().getResource("ingredient");
		    	String ingredientURI = ingredient.getURI();
		    	String ingredientType = ingredient.hasProperty(type) ? ingredient.getProperty(type).getResource().getURI(): "";
		    	
		    	adjacencyList.put(ingredientURI, new ArrayList<>());
		    	elementLabels.put(ingredientURI, ingredientURI.substring(ingredientURI.indexOf('#') + 1));
		    	if(ingredientType.equals(Namespace.IF_CONDITION)) elementTypes.put(ingredientURI, "condition");
		    	else if(ingredientType.equals(Namespace.FOR_LOOP)) elementTypes.put(ingredientURI, "loop");
		    	else elementTypes.put(ingredientURI, "offering");
		    	
		    	String configQueryString = 
						"PREFIX td: <" + Namespace.TD + "> \n" +
						"PREFIX offeringRecipe: <" + Namespace.OFFERING_RECIPE + "> \n" +
						"SELECT ?param ?value \n" +
						"WHERE {\n" +
						"      <" + ingredientURI + "> offeringRecipe:hasConfigAttribute ?conf . \n" +
						"      ?conf ?param ?value \n" +
						"      }";

			    logger.debug("configQueryString Query: \n" + configQueryString + "\n");
				
				Query configQuery = QueryFactory.create(configQueryString);
				QueryExecution configQueryEx = QueryExecutionFactory.create(configQuery, rdfModel);
				ResultSet configQueryResults = configQueryEx.execSelect();
				
	    		Map<String,String> configMap = new HashMap<String,String>();
				
	    		while (configQueryResults.hasNext()) {
	    			QuerySolution solution = configQueryResults.next();
	    			String param = solution.get("param").toString();
	    			param = param.substring(param.indexOf('#') + 1);
	    			RDFNode value = solution.get("value");
	    			String valueString = value.isResource() ? value.asResource().getURI() : value.asLiteral().getString();
	    			configMap.put(param, rdfModel.shortForm(valueString));
	    		}
	    		configQueryEx.close();
	    			    				
	    		if(ingredientType.equals(Namespace.IF_CONDITION) || ingredientType.equals(Namespace.FOR_LOOP)) {
	    			String operator = ingredient.hasProperty(hasOperator) ? ingredient.getProperty(hasOperator).getResource().getURI() : "";
	    			String variable = null;
	    			if(ingredient.hasProperty(hasVariable)){
	    				RDFNode node = ingredient.getProperty(hasVariable).getObject();
	    				if(node.isResource()) variable = rdfModel.shortForm(node.asResource().getURI());
	    				else variable = node.asLiteral().getString();
	    			}
	    			String value = null;
	    			if(ingredient.hasProperty(hasValue)){
	    				RDFNode node = ingredient.getProperty(hasValue).getObject();
	    				if(node.isResource()) value = rdfModel.shortForm(node.asResource().getURI());
	    				else value = node.asLiteral().getString();
	    			}
	    			configMap.put("operator", rdfModel.shortForm(operator));
	    			configMap.put("variable", variable);
	    			configMap.put("value", value);
	    			
	    			if(ingredientType.equals(Namespace.FOR_LOOP)) {
	    				configMap.put("initType", ingredient.hasProperty(initType) ? rdfModel.shortForm(ingredient.getProperty(initType).getResource().getURI()) : null);
	    				configMap.put("initVariable", ingredient.hasProperty(initVariable) ? ingredient.getProperty(initVariable).getString() : null);
	    				configMap.put("initValue", ingredient.hasProperty(initValue) ? ingredient.getProperty(initValue).getString() : null);
	    				configMap.put("incrementVariable", ingredient.hasProperty(incrementVariable) ? ingredient.getProperty(incrementVariable).getString() : null);
	    				configMap.put("incrementValue", ingredient.hasProperty(incrementValue) ? ingredient.getProperty(incrementValue).getString() : null);
		    		}
	    		} else {
	    			if(ingredient.hasProperty(category)){
	    				configMap.put("category", rdfModel.shortForm(ingredient.getProperty(category).getResource().getURI()));
	    			}
	    		}
	    		
	    		Property waitTime = rdfModel.getProperty(Namespace.BIGIOT + "waitTime");
				if(ingredient.hasProperty(waitTime)){
					configMap.put("waitTime", ingredient.getProperty(waitTime).getString());
				}
	    		
	    		configAttributes.put(ingredientURI, configMap);
		    	
		    	String inputsQueryString = 
						"PREFIX td: <" + Namespace.TD + "> \n" +
						"SELECT ?input \n" +
						"WHERE {\n" +
						"      <" + ingredientURI + "> td:hasInput ?input . \n" +
						"      }";

			    logger.debug("inputsQueryString Query: \n" + inputsQueryString + "\n");
			    
				Query inputsQuery = QueryFactory.create(inputsQueryString);
				QueryExecution inputsQueryEx = QueryExecutionFactory.create(inputsQuery, rdfModel);
				ResultSet inputsQueryResults = inputsQueryEx.execSelect();
	    		
	    		while (inputsQueryResults.hasNext()) { 
				    Resource input = inputsQueryResults.next().getResource("input");
				    String inputURI = input.getURI();
				    List<String> current = adjacencyList.getOrDefault(inputURI, new ArrayList<>());
				    current.add(ingredientURI);
				    adjacencyList.put(inputURI, current);
			    	elementLabels.put(inputURI, inputURI.substring(inputURI.indexOf('#') + 1));
			    	if(input.hasProperty(dataType)){
			    		dataTypes.put(inputURI, rdfModel.shortForm(input.getProperty(dataType).getResource().getURI()));
	    			}
			    	elementTypes.put(inputURI, "input");
	    		}
	    		inputsQueryEx.close();
	    		
	    		String outputsQueryString = 
							"PREFIX td: <" + Namespace.TD + "> \n" +
							"SELECT ?output \n" +
							"WHERE {\n" +
							"      <" + ingredientURI + "> td:hasOutput ?output . \n" +
							"      }";

			    logger.debug("outputsQueryString Query: \n" + outputsQueryString + "\n");
			    
				Query outputsQuery = QueryFactory.create(outputsQueryString);
				QueryExecution outputsQueryEx = QueryExecutionFactory.create(outputsQuery, rdfModel);
				ResultSet outputsQueryResults = outputsQueryEx.execSelect();
		    	
		    	while (outputsQueryResults.hasNext()) { 
				    Resource output = outputsQueryResults.next().getResource("output");
				    String outputURI = output.getURI();
				    List<String> current = adjacencyList.getOrDefault(ingredientURI, new ArrayList<>());
				    current.add(outputURI);
				    adjacencyList.put(ingredientURI, current);
					if (!adjacencyList.containsKey(outputURI)) {
						adjacencyList.put(outputURI, new ArrayList<>());
					}
				   	elementLabels.put(outputURI, outputURI.substring(outputURI.indexOf('#') + 1));
				   	if(output.hasProperty(dataType)){
			    		dataTypes.put(outputURI, rdfModel.shortForm(output.getProperty(dataType).getResource().getURI()));
	    			}
				   	elementTypes.put(outputURI, "output");
		    	}
		    	outputsQueryEx.close();
	    	
		    }
		    ingredientsQueryEx.close();
		    
		    String interactionsQueryString = 
					"PREFIX td: <" + Namespace.TD + "> \n" +
					"PREFIX offeringRecipe: <" + Namespace.OFFERING_RECIPE + "> \n" +
					"SELECT ?from ?to ?input ?output \n" +
					"WHERE {\n" +
					"      ?interaction a offeringRecipe:Interaction . \n" +
					"      <" + recipeID + "> offeringRecipe:hasInteraction ?interaction . \n" +
					"      ?interaction offeringRecipe:hasIngredientFrom ?from . \n" +
					"      ?interaction offeringRecipe:hasIngredientTo ?to . \n" +
					"      ?interaction offeringRecipe:hasIngredientInput ?input . \n" +
					"      ?interaction offeringRecipe:hasIngredientOutput ?output . \n" +
					"      }";

		    logger.debug("interactionsQueryString Query: \n" + interactionsQueryString + "\n");
		    
			Query interactionsQuery = QueryFactory.create(interactionsQueryString);
			QueryExecution interactionsQueryEx = QueryExecutionFactory.create(interactionsQuery, rdfModel);
			ResultSet interactionsQueryResults = interactionsQueryEx.execSelect();
    		
    		while (interactionsQueryResults.hasNext()) {
    			QuerySolution solution = interactionsQueryResults.next();
			    Resource from = solution.getResource("from");
			    Resource to = solution.getResource("to");
			    Resource input = solution.getResource("input");
			    Resource output = solution.getResource("output");

			    logger.debug("Received input: {}, output: {}", input, output);

				String outputURI = output.getURI();
				String inputURI  = input.getURI();
                List<String> outList = adjacencyList.getOrDefault(outputURI, new ArrayList<>());
				outList.add(inputURI);
				adjacencyList.put(outputURI, outList);
    		}
    		interactionsQueryEx.close();
    		recipePattern.put("recipeName", recipeName);
    		recipePattern.put("recipeDescription", recipeDescription);
    		recipePattern.put("recipeCategory", recipeCategory);
			recipePattern.put("adjacencyList", adjacencyList);
			recipePattern.put("elementLabels", elementLabels);
			recipePattern.put("elementTypes", elementTypes);
			recipePattern.put("dataTypes", dataTypes);
			recipePattern.put("configAttributes", configAttributes);

			logger.debug("SUCCESS");
			for (String infoElement : recipePattern.keySet()) {
				logger.debug("Recipe pattern element '" + infoElement + "': " + recipePattern.get(infoElement));
			}
			
			return recipePattern;
			
		}catch(Exception e){
			logger.error("Error occurred while fetching recipe patterns!", e);
			return null;
		}
	}
	
	
	public List<String> getRecipeCategories(){
		List<String> categories = new ArrayList<String>();
		if(rdfModel==null) return categories;
		try{
			
			String categoriesQueryString = 
					"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
					"PREFIX skos: <" + Namespace.SKOS + "> " +
					"SELECT ?category " +
					"WHERE {" +
					"      ?allCat a bigiot:OfferingCategory; " +
					"      		skos:narrower ?category." +
					"} order by ?category";
			
			
			Query categoriesQuery = QueryFactory.create(categoriesQueryString);
			QueryExecution categoriesQueryEx = QueryExecutionFactory.create(categoriesQuery, rdfModel);
			ResultSet categoriesQueryResults = categoriesQueryEx.execSelect();
			
		    while (categoriesQueryResults.hasNext()) {
		    	QuerySolution solution = categoriesQueryResults.next();
		    	String category = solution.getResource("category").getURI();
		    	categories.add(rdfModel.shortForm(category));
		    }
		    categoriesQueryEx.close();
		    
			return categories;
		}catch(Exception e){
			logger.error("Error occurred while fetching recipe categories!", e);
			return categories;
		}
	}
	
	
	public List<String> getOfferingCategories(){
		List<String> categories = new ArrayList<String>();
		if(rdfModel==null) return categories;
		try{
			
			String categoriesQueryString = 
					"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
					"PREFIX skos: <" + Namespace.SKOS + "> " +
					"PREFIX rdfs: <" + Namespace.RDFS + "> " +
					"SELECT DISTINCT ?category ?sub " +
					"WHERE {" +
					"      ?allCat a bigiot:OfferingCategory; " +
					"      		skos:narrower ?category." +
					"	   ?category skos:narrower* ?sub; " +
					"      		rdfs:label ?label. " +
					"      ?sub rdfs:label ?subLabel." +
					"} order by ?category";
			
			Query categoriesQuery = QueryFactory.create(categoriesQueryString);
			QueryExecution categoriesQueryEx = QueryExecutionFactory.create(categoriesQuery, rdfModel);
			logger.info("getOfferingCategories Query: \n" +categoriesQueryEx.getQuery().toString());
			ResultSet categoriesQueryResults = categoriesQueryEx.execSelect();
			
		    while (categoriesQueryResults.hasNext()) {
		    	QuerySolution solution = categoriesQueryResults.next();
		    	//String label = solution.getResource("category").getURI();
		    	String subCategory = solution.getResource("sub").getURI();
		    	categories.add(rdfModel.shortForm(subCategory));
		    }
		    categoriesQueryEx.close();
		    
			return categories;
		}catch(Exception e){
			logger.error("Error occurred while fetching offering categories!", e);
			return categories;
		}
	}
	
	
//	public List<String> getAllDataTypes(){
//		List<String> dataTypes = new ArrayList<String>();
//		if(rdfModel==null) return dataTypes;
//		try{
//			
//			String dataTypesQueryString = 
//					"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
//					"PREFIX skos: <" + Namespace.SKOS + "> " +
//					"PREFIX rdf: <" + Namespace.RDF + "> " +
//					"PREFIX rdfs: <" + Namespace.RDFS + "> " +
//					"PREFIX schema: <" + Namespace.SCHEMA + "> " +
//					"SELECT DISTINCT ?dataType " +
//					"WHERE {" +
//					"      ?allCat a bigiot:OfferingCategory; " +
//					"      		skos:narrower ?category." +
//					"	   ?category skos:narrower* ?sub. " +
//					"      ?sub bigiot:refersTo ?class. " +
//					"      ?prop a rdf:Property ." +
//					"      ?prop (schema:domainIncludes|(schema:domainIncludes/^schema:domainIncludes)+)  ?class ." +
//					"      ?prop schema:rangeIncludes ?dataType" +
//					"}";
//			
//			Query dataTypesQuery = QueryFactory.create(dataTypesQueryString);
//			QueryExecution dataTypesQueryEx = QueryExecutionFactory.create(dataTypesQuery, rdfModel);
//			logger.debug("getAllDataTypes Query: \n" +dataTypesQueryEx.getQuery().toString());
//			ResultSet dataTypesQueryResults = dataTypesQueryEx.execSelect();
//			
//		    while (dataTypesQueryResults.hasNext()) {
//		    	QuerySolution solution = dataTypesQueryResults.next();
//		    	String dataType = solution.getResource("dataType").getURI();
//		    	dataTypes.add(rdfModel.shortForm(dataType));
//		    }
//		    dataTypesQueryEx.close();
//			
//			return dataTypes;
//		}catch(Exception e){
//			logger.error("Error occurred while fetching offering categories!", e);
//			return dataTypes;
//		}
//	}
	
	
	public List<String> getDataTypes(String category){
		List<String> dataTypes = new ArrayList<String>();
		if(infModel ==null) return dataTypes;

		try{
			// new query (runs on inferred (infModel) rdf store)
			String dataTypesQueryString = 
					"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
							"PREFIX skos: <" + Namespace.SKOS + "> " +
							"PREFIX schema: <" + Namespace.SCHEMA + "> " +
							"SELECT DISTINCT ?dataType " +
							"WHERE {" +
					"      ?allCat a bigiot:OfferingCategory; " +
					"      		skos:narrower* ?category." +
					"	   ?category bigiot:expectedAnnotation ?dataType . " +
					"      FILTER(?category = " + category + " || ?sub = " + category + ") " +
					"}";
			
			Query dataTypesQuery = QueryFactory.create(dataTypesQueryString);
			QueryExecution dataTypesQueryEx = QueryExecutionFactory.create(dataTypesQuery, infModel);
			logger.debug("getDataTypes Query: \n" +dataTypesQueryEx.getQuery().toString());
			ResultSet dataTypesQueryResults = dataTypesQueryEx.execSelect();
			
		    while (dataTypesQueryResults.hasNext()) {
		    	QuerySolution solution = dataTypesQueryResults.next();
		    	String dataType = solution.getResource("dataType").getURI();
		    	dataTypes.add(infModel.shortForm(dataType));
		    }
		    dataTypesQueryEx.close();
		    
		    logger.debug("getDataTypes Success!");
		    logger.debug("getDataTypes ResultSet:" +dataTypes);
			
			return dataTypes;
		}catch(Exception e){
			logger.error("Error occurred while fetching offering categories!", e);
			return dataTypes;
		}
	}
	
	public List<Offering> getAllOfferings(){
		List<Offering> allOfferings = new ArrayList<Offering>();
		
		String offeringsQueryString = 
				"PREFIX td: <" + Namespace.TD + "> \n" +
				"PREFIX bigiot: <" + Namespace.BIGIOT + "> \n" +
				"PREFIX schema: <" + Namespace.SCHEMA + "> \n" +
				"PREFIX ssn: <" + Namespace.SSN + "> " +				   
				"SELECT ?offering ?name ?category ?provider ?url ?endpointType " +
				"WHERE {" +
				"      ?offering a bigiot:Offering ." +
				"      ?offering schema:name ?name ." +
				"      ?offering schema:category ?category ;" +
				"      bigiot:providerId     ?provider ;" +
				"      bigiot:endpoint       ?endpoint ." +
				"      ?endpoint schema:url            ?url ." +
				"      OPTIONAL {" +
				"      ?endpoint bigiot:endpointType   ?endpointType" +
				"      }" +
//				"      OPTIONAL {" +
//				"      ?offering td:hasInput           ?input ." +
//				"      ?input    schema:name		   ?inputName ." +
//				"      }" +
//				"      OPTIONAL {" +
//				"      ?offering td:hasOutput          ?output ." +
//				"      ?output   schema:name		   ?outputName ." +
//				"      }" +
				"}";
		
		Query offeringQuery = QueryFactory.create(offeringsQueryString);
		QueryExecution offeringQueryEx = QueryExecutionFactory.create(offeringQuery, rdfModel);
		
		logger.debug("All offering Query: \n" +offeringQueryEx.getQuery().toString());
		
		ResultSet offeringQueryResults = offeringQueryEx.execSelect();
		
		while (offeringQueryResults.hasNext()) {
			QuerySolution solution = offeringQueryResults.next();
			
//			// list all available variables in the solution:
//			logger.debug("All vars: " );
//			Iterator<String> varNames = solution.varNames();
//			while (varNames.hasNext()) {
//				String variable = varNames.next();
//				logger.debug(variable);
//			}
			
	    	String offeringURI      = solution.getResource("offering").getURI();
	    	String offeringName     = solution.getLiteral("name").getString();
	    	String offeringCategory = solution.getResource("category").getURI();
	    	String providerId       = solution.getLiteral("provider").getString();
	    	String endpointUrl      = solution.getLiteral("url").getString();
	    	
	    	String endpointType = null;
	    	if (solution.getLiteral("endpointType") != null) {
	    		endpointType = solution.getLiteral("endpointType").getString();
	    	}
	    	
//	    	String inputName = null;
//	    	if (solution.getLiteral("inputName") != null) {
//	    		inputName    = solution.getLiteral("inputName").getString();
//	    	}
//	    	
//	    	String outputName = null;
//	    	if (solution.getLiteral("outputName") != null) {
//	    		outputName   = solution.getLiteral("outputName").getString();
//	    	}
	    	
	    	//Map<String, String> nfpMap = getNonFunctionalPropertiesForOffering(offeringURI);
	    	
	    	Offering o = new Offering(offeringURI, offeringName, offeringCategory, providerId, endpointUrl, endpointType, null);
	    	
//	    	addOutputsToOffering(o);
//	    	addInputsToOffering(o);
	    	
	    	allOfferings.add(o);
	    	
		}
		offeringQueryEx.close();
		
		logger.debug("All offerings - number of offerings in list: " + allOfferings.size());
		
		return allOfferings;
	}
	
	public void addInputsToOffering(Offering offering) {
		
		String queryString= 
				"PREFIX schema: <" + Namespace.SCHEMA + "> " +
				"PREFIX rdf: <" + Namespace.RDF + "> " +
				"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
				"PREFIX td: <" + Namespace.TD + "> " +
				"SELECT ?inputName " +
				"WHERE {" +
				"      ?offering a bigiot:Offering ;" +
				"      ?offering schema:name <" + offering.getId() + "> ;" +
				"      OPTIONAL {" +
				"          ?offering td:hasInput           ?input ." +
				"          ?input    schema:name		   ?inputName ." +
				"      }" +
				"}";
				
		Query query = QueryFactory.create(queryString);
		QueryExecution queryEx = QueryExecutionFactory.create(query, rdfModel);
		ResultSet queryResults = queryEx.execSelect();
		while (queryResults.hasNext()) {
			QuerySolution solution = queryResults.next();
			
			if (solution.getLiteral("inputName") != null) {
	    		String inputName = solution.getLiteral("inputName").getString();
	    		offering.addInput(inputName);
	    	}
	    	
		}
		queryEx.close();
	}
	
	public void addOutputsToOffering(Offering offering) {
		
		String queryString= 
				"PREFIX schema: <" + Namespace.SCHEMA + "> " +
				"PREFIX rdf: <" + Namespace.RDF + "> " +
				"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
				"PREFIX td: <" + Namespace.TD + "> " +
				"SELECT ?outputName " +
				"WHERE {" +
				"      ?offering a bigiot:Offering ;" +
				"      ?offering schema:name <" + offering.getId() + "> ;" +
				"      OPTIONAL {" +
				"          ?offering td:hasOutput          ?output ." +
				"          ?output    schema:name		   ?outputName ." +
				"      }" +
				"}";
				
		Query query = QueryFactory.create(queryString);
		QueryExecution queryEx = QueryExecutionFactory.create(query, rdfModel);
		ResultSet queryResults = queryEx.execSelect();
		while (queryResults.hasNext()) {
			QuerySolution solution = queryResults.next();
			
			if (solution.getLiteral("outputName") != null) {
	    		String inputName = solution.getLiteral("outputName").getString();
	    		offering.addInput(inputName);
	    	}
	    	
		}
		queryEx.close();
	}
	
	public List<Ingredient> getIngredients(String recipeID, Map<String, List<Map<String, String>>> OSRMap){
		List<Ingredient> list_of_ingredients = new ArrayList<Ingredient>();
		if(rdfModel==null) return list_of_ingredients;
		try{
			String ingredientsQueryString = 
					"PREFIX bigiot: <" + Namespace.BIGIOT + "> \n" +
					"PREFIX offeringRecipe: <" + Namespace.OFFERING_RECIPE + "> \n" +
					"SELECT ?ingredient \n" +
					"WHERE {\n" +
					"      ?ingredient a bigiot:Offering . \n" +
					"      <" + recipeID + "> offeringRecipe:hasIngredient ?ingredient \n" +
					"      }\n";
			
			logger.debug("ingredients Query: \n" +ingredientsQueryString);
			
			Query ingredientsQuery = QueryFactory.create(ingredientsQueryString);
			QueryExecution ingredientsQueryEx = QueryExecutionFactory.create(ingredientsQuery, rdfModel);
			ResultSet ingredientsQueryResults = ingredientsQueryEx.execSelect();
			
		    while (ingredientsQueryResults.hasNext()) {
		    	
		    	Resource ingredient = ingredientsQueryResults.next().getResource("ingredient");
		    	String ingredientURI = ingredient.getURI();

		    	logger.debug("Getting offerings for ingredient '" + ingredientURI + "'");
		    	
		    	if(ingredient.hasProperty(category)){
		    		String ingredientCategory = ingredient.getProperty(category).getResource().getURI();
		    		
		    		//Initial offerings query
			    	String offeringsQueryString = 
							"PREFIX td: <" + Namespace.TD + "> \n" +
							"PREFIX bigiot: <" + Namespace.BIGIOT + "> \n" +
							"PREFIX schema: <" + Namespace.SCHEMA + "> \n" +
							"PREFIX ssn: <" + Namespace.SSN + "> \n" +
							"PREFIX rrc: <" + Namespace.OFFERING_RECIPE_RUNTIME_CONFIG + "> \n" +		   
							"SELECT DISTINCT ?offering \n" +
							"WHERE {\n" +
							"      ?offering a bigiot:Offering . \n" +
							"      ?offering schema:category <" + ingredientCategory + "> . \n" +
							"      ?offering bigiot:providerId ?provider . \n";
			    	
			    	//Checking ingredient inputs and extending the offerings query
			    	String inputsQueryString = 
							"PREFIX td: <" + Namespace.TD + "> " +
							"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
							"SELECT ?type " +
							"WHERE {" +
							"      <" + ingredientURI + "> td:hasInput ?input . " +
					    	"      ?input bigiot:rdfType ?type . " +
							"      }";
			    	
			    	Query inputsQuery = QueryFactory.create(inputsQueryString);
					QueryExecution inputsQueryEx = QueryExecutionFactory.create(inputsQuery, rdfModel);
					ResultSet inputsQueryResults = inputsQueryEx.execSelect();
					int inputIndex = 1;
					while (inputsQueryResults.hasNext()) {
				    	String type = inputsQueryResults.next().getResource("type").getURI();
				    	offeringsQueryString += "      ?offering td:hasInput ?input" + inputIndex + " . \n";
				    	offeringsQueryString += "      ?input" + inputIndex++ + " bigiot:rdfType <" + type + "> . \n";
					}
					inputsQueryEx.close();
					
					//Checking ingredient outputs and extending the offerings query
					String outputsQueryString = 
							"PREFIX td: <" + Namespace.TD + "> " +
							"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
							"SELECT ?type " +
							"WHERE {" +
							"      <" + ingredientURI + "> td:hasOutput ?output . " +
					    	"      ?output bigiot:rdfType ?type . " +
							"      }";
			    	
			    	Query outputsQuery = QueryFactory.create(outputsQueryString);
					QueryExecution outputsQueryEx = QueryExecutionFactory.create(outputsQuery, rdfModel);
					ResultSet outputsQueryResults = outputsQueryEx.execSelect();
					int outputIndex = 1;
					while (outputsQueryResults.hasNext()) {
				    	String type = outputsQueryResults.next().getResource("type").getURI();
				    	offeringsQueryString += "      ?offering td:hasOutput ?output" + outputIndex + " . \n";
				    	offeringsQueryString += "      ?output" + outputIndex++ + " bigiot:rdfType <" + type + "> . \n";
					}
					outputsQueryEx.close();
					
					// Add OSR check to the query
					List<Map<String, String>> OSRList = OSRMap.get(ingredientURI);
					
					if(OSRList != null){
						offeringsQueryString += createOSRQueryString(OSRList);
					}
					
					//End of the offerings query
					offeringsQueryString += "}\n";
					
					logger.debug("offeringsQuery: \n" + offeringsQueryString);
										
					Query offeringsQuery = QueryFactory.create(offeringsQueryString);
					QueryExecution offeringsQueryEx = QueryExecutionFactory.create(offeringsQuery, rdfModel);
					ResultSet offeringsQueryResults = offeringsQueryEx.execSelect();
					
			    	if (offeringsQueryResults.hasNext()) {
				    	String nm1 = ingredientURI.substring(ingredientURI.indexOf('#') + 1);
				    	Ingredient ingredientObject = new Ingredient(ingredientURI, nm1, ingredientCategory);
				    	list_of_ingredients.add(ingredientObject);
				    	
					    while (offeringsQueryResults.hasNext()) {
					    	Resource offeringResource = offeringsQueryResults.next().getResource("offering");
					    	String offeringURI = offeringResource.getURI();
					    	
					    	//Checking for additional inputs
					    	String extraInputsQueryString = 
									"PREFIX td: <" + Namespace.TD + "> " +
									"SELECT (COUNT(DISTINCT ?input) AS ?count) " +
									"WHERE {" +
									"      <" + offeringURI + "> td:hasInput ?input " +
									"      }";
							
							Query extraInputsQuery = QueryFactory.create(extraInputsQueryString);
							QueryExecution extraInputsQueryEx = QueryExecutionFactory.create(extraInputsQuery, rdfModel);
							ResultSet extraInputsQueryResults = extraInputsQueryEx.execSelect();
							int count = 0;
							if (extraInputsQueryResults.hasNext()) {
								count = extraInputsQueryResults.next().getLiteral("count").getInt();
							}
							extraInputsQueryEx.close();
							if(count != inputIndex-1) {
								continue;
							}
							//TODO: what is the above? why the above if statement? 
							
							Map<String, String> nfpMap = getNonFunctionalPropertiesForOffering(offeringURI);
					    	
					    	String offeringName = offeringResource.hasProperty(name) ? offeringResource.getProperty(name).getString() : null;
					    	
					    	ingredientObject.list_of_offerings.add(new Offering(offeringURI, offeringName, ingredientCategory, null, null, null, nfpMap));
					    }
			    	}
			    	offeringsQueryEx.close();
		    	}
		    }
		    ingredientsQueryEx.close();
			return list_of_ingredients;
		} catch(Exception e){
			logger.error("Error occurred while fetching ingredients!", e);
			return list_of_ingredients;
		}
	}
	
	public List<Offering> getOfferingsForIngredient(Map<String, Object> ingredient, List<Map<String, String>> OSRList) {
		List<Offering> listOfOfferings = new ArrayList<>();
		if(rdfModel==null) return listOfOfferings;
		try{
			String ingredientURI = (String) ingredient.get("id");
			String ingredientCategory = (String) ingredient.get("category");

			logger.debug("Getting offerings for ingredient '" + ingredientURI + "'");

			//Initial offerings query
			String offeringsQueryString = 
					"PREFIX td: <" + Namespace.TD + "> \n" +
							"PREFIX bigiot: <" + Namespace.BIGIOT + "> \n" +
							"PREFIX schema: <" + Namespace.SCHEMA + "> \n" +
							"PREFIX ssn: <" + Namespace.SSN + "> \n" +
							"PREFIX rrc: <" + Namespace.OFFERING_RECIPE_RUNTIME_CONFIG + "> \n" +		   
							"SELECT DISTINCT ?offering \n" +
							"WHERE {\n" +
							"      ?offering a bigiot:Offering . \n" +
							"      ?offering schema:category <" + ingredientCategory + "> . \n" +
							"      ?offering bigiot:providerId ?provider . \n";

			//Checking ingredient inputs and extending the offerings query
			String inputsQueryString = 
					"PREFIX td: <" + Namespace.TD + "> " +
							"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
							"SELECT ?type " +
							"WHERE {" +
							"      <" + ingredientURI + "> td:hasInput ?input . " +
							"      ?input bigiot:rdfType ?type . " +
							"      }";

			Query inputsQuery = QueryFactory.create(inputsQueryString);
			QueryExecution inputsQueryEx = QueryExecutionFactory.create(inputsQuery, rdfModel);
			ResultSet inputsQueryResults = inputsQueryEx.execSelect();
			int inputIndex = 1;
			while (inputsQueryResults.hasNext()) {
				String type = inputsQueryResults.next().getResource("type").getURI();
				offeringsQueryString += "      ?offering td:hasInput ?input" + inputIndex + " . \n";
				offeringsQueryString += "      ?input" + inputIndex++ + " bigiot:rdfType <" + type + "> . \n";
			}
			inputsQueryEx.close();

			//Checking ingredient outputs and extending the offerings query
			String outputsQueryString = 
					"PREFIX td: <" + Namespace.TD + "> " +
							"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
							"SELECT ?type " +
							"WHERE {" +
							"      <" + ingredientURI + "> td:hasOutput ?output . " +
							"      ?output bigiot:rdfType ?type . " +
							"      }";

			Query outputsQuery = QueryFactory.create(outputsQueryString);
			QueryExecution outputsQueryEx = QueryExecutionFactory.create(outputsQuery, rdfModel);
			ResultSet outputsQueryResults = outputsQueryEx.execSelect();
			int outputIndex = 1;
			while (outputsQueryResults.hasNext()) {
				String type = outputsQueryResults.next().getResource("type").getURI();
				offeringsQueryString += "      ?offering td:hasOutput ?output" + outputIndex + " . \n";
				offeringsQueryString += "      ?output" + outputIndex++ + " bigiot:rdfType <" + type + "> . \n";
			}
			outputsQueryEx.close();

			// Add OSR check to the query
			if(OSRList != null){
				offeringsQueryString += createOSRQueryString(OSRList);
			}

			//End of the offerings query
			offeringsQueryString += "}\n";

			logger.debug("offeringsQuery: \n" + offeringsQueryString);

			Query offeringsQuery = QueryFactory.create(offeringsQueryString);
			QueryExecution offeringsQueryEx = QueryExecutionFactory.create(offeringsQuery, rdfModel);
			ResultSet offeringsQueryResults = offeringsQueryEx.execSelect();

			if (offeringsQueryResults.hasNext()) {

				while (offeringsQueryResults.hasNext()) {
					Resource offeringResource = offeringsQueryResults.next().getResource("offering");
					String offeringURI = offeringResource.getURI();

					//Checking for additional inputs
					String extraInputsQueryString = 
							"PREFIX td: <" + Namespace.TD + "> " +
									"SELECT (COUNT(DISTINCT ?input) AS ?count) " +
									"WHERE {" +
									"      <" + offeringURI + "> td:hasInput ?input " +
									"      }";

					Query extraInputsQuery = QueryFactory.create(extraInputsQueryString);
					QueryExecution extraInputsQueryEx = QueryExecutionFactory.create(extraInputsQuery, rdfModel);
					ResultSet extraInputsQueryResults = extraInputsQueryEx.execSelect();
					int count = 0;
					if (extraInputsQueryResults.hasNext()) {
						count = extraInputsQueryResults.next().getLiteral("count").getInt();
					}
					extraInputsQueryEx.close();
					if(count != inputIndex-1) {
						continue;
					}
					//TODO: what is the above? why the above if statement? 

					Map<String, String> nfpMap = getNonFunctionalPropertiesForOffering(offeringURI);

					String offeringName = offeringResource.hasProperty(name) ? offeringResource.getProperty(name).getString() : null;

					listOfOfferings.add(new Offering(offeringURI, offeringName, ingredientCategory, null, null, null, nfpMap));
				}
			}
			offeringsQueryEx.close();

			return listOfOfferings;
		} catch(Exception e){
			logger.error("Error occurred while fetching ingredients!", e);
			return listOfOfferings;
		}
	}
	
	
	public String createOSRQueryString(List<Map<String, String>> OSRList){
		String OSRQueryString ="";
		for (int i = 0; i < OSRList.size(); i++){
			
			Map<String, String> thisOSR = (Map<String, String>) OSRList.get(i);

			String OSRCategory = thisOSR.get("OSRCategory");
			String value = thisOSR.get("value");
			String OSRValueType = thisOSR.get("OSRValueType");
			String name = thisOSR.get("name");
			String label = thisOSR.get("label");
			String operation = thisOSR.get("operation");
			
			if(i == 0){
				OSRQueryString += " { ";
			}else if(i-1 >= 0){
				Map<String, String> prevOSR = (Map<String, String>) OSRList.get(i-1);

				if(!"AND".equals(prevOSR.get("operation"))){
					OSRQueryString += " { ";
				}
			}

			if(OSR.OSRCATEGORY_DIRECTOSR.equals(OSRCategory)){
				OSRQueryString += "?offering " + OSRValueType + " \"" + value + "\" . ";
			}else if(OSR.OSRCATEGORY_OPERATOROSR.equals(OSRCategory)){
				String operator = thisOSR.get("operator");
				OSRQueryString += "?offering schema:priceSpecification [ " + OSRValueType + " ?price ] . ";
				// TODO: In the rdf model, add fields OSRValueTypeBefore = "schema:priceSpecification  [ schema:price" & OSRValueTypeAfter = "]" 
				// OR add a boolean field OSRValueTypeAfter, if true, add a ']' here
				// to accommodate such predicates with blank nodes

				OSRQueryString += "FILTER( ?price " + operator + " " + value + ") . ";
			}else if(OSR.OSRCATEGORY_COMPLEXOSR.equals(OSRCategory)){

				String rule = thisOSR.get("rule");
				ComplexOSR cOSR = new ComplexOSR(name, label, value, OSRValueType, OSRCategory, operation, rule);

				switch (rule) {
				case ComplexOSR.RULE_SAME_AS:
					String property = thisOSR.get("property");
					String offeringURI = thisOSR.get("offeringURI");

					SameAsCOSR sacOSR = new SameAsCOSR(name, label, value, OSRValueType, OSRCategory, operation, rule, property, offeringURI);
					cOSR = sacOSR;
					break;
				/*case ComplexOSR.RULE_CARDINALITY:
					// Cardinality rule can only be applied at runtime at the repository side
					// At recipe instantiation time, cardinality rules are to be applied at the UI/Client side 
					int minCard = Integer.parseInt(thisOSR.get("minCardinality"));
					int maxCard = Integer.parseInt(thisOSR.get("maxCardinality"));

					CardinalityCOSR ccOSR = new CardinalityCOSR(name, label, value, OSRValueType, OSRCategory, operation, rule, minCard, maxCard);
					cOSR = ccOSR;
					break;*/
				/*case ComplexOSR.RULE_COMBINED_PRICE:	// misplaced? CombinedPrice would be applicable to RRC and not to IRC
					String operator = thisOSR.get("operator");

					CombinedPriceCOSR cpcOSR = new CombinedPriceCOSR(name, label, value, OSRValueType, OSRCategory, operation, rule, operator);
					cOSR = cpcOSR;
					break;*/
				}

				OSRQueryString += cOSR.interpretRuleToQuery();
			}
			
			if(i+1 < OSRList.size()){
				if("OR".equals(operation)){
					OSRQueryString += " } ";
					OSRQueryString += " UNION ";
				}
			}
			else{
				OSRQueryString += " } ";
			}
		}
		return OSRQueryString;
	}
	
	public String createOSRListQueryString(List<OSR> OSRList){
		String OSRQueryString ="";
		for (int i = 0; i < OSRList.size(); i++){
			
			OSR thisOSR = OSRList.get(i);
			
			if(i == 0){
				OSRQueryString += " { ";
			}else if(i-1 >= 0){
				OSR prevOSR = OSRList.get(i-1);

				if(!"AND".equals(prevOSR.operation)){
					OSRQueryString += " { ";
				}
			}

			if(OSR.OSRCATEGORY_DIRECTOSR.equals(thisOSR.OSRCategory)){
				OSRQueryString += "?offering " + thisOSR.OSRValueType + " \"" + thisOSR.value + "\" . ";
			}else if(OSR.OSRCATEGORY_OPERATOROSR.equals(thisOSR.OSRCategory)){
				OperatorOSR thisOpOSR = (OperatorOSR) OSRList.get(i);
				OSRQueryString += "?offering schema:priceSpecification [ " + OSRValueType + " ?price ] . ";
				// TODO: In the rdf model, add fields OSRValueTypeBefore = "schema:priceSpecification  [ schema:price" & OSRValueTypeAfter = "]" 
				// OR add a boolean field OSRValueTypeAfter, if true, add a ']' here
				// to accommodate such predicates with blank nodes

				OSRQueryString += "FILTER( ?price " + thisOpOSR.operator + " " + thisOSR.value + ") . ";
			}else if(OSR.OSRCATEGORY_COMPLEXOSR.equals(OSRCategory)){
				
				ComplexOSR cOSR = (ComplexOSR) OSRList.get(i);
				//String rule = thisOSR.get("rule");
				//ComplexOSR cOSR = new ComplexOSR(name, label, value, OSRValueType, OSRCategory, operation, rule);

				switch (cOSR.rule) {
				case ComplexOSR.RULE_SAME_AS:
					SameAsCOSR sacOSR = (SameAsCOSR) OSRList.get(i);
					cOSR = sacOSR;
					break;
				/*case ComplexOSR.RULE_CARDINALITY:
					// Cardinality rule can only be applied at runtime at the repository side
					// At recipe instantiation time, cardinality rules are to be applied at the UI/Client side 
					int minCard = Integer.parseInt(thisOSR.get("minCardinality"));
					int maxCard = Integer.parseInt(thisOSR.get("maxCardinality"));

					CardinalityCOSR ccOSR = new CardinalityCOSR(name, label, value, OSRValueType, OSRCategory, operation, rule, minCard, maxCard);
					cOSR = ccOSR;
					break;*/
				/*case ComplexOSR.RULE_COMBINED_PRICE:	// misplaced? CombinedPrice would be applicable to RRC and not to IRC
					String operator = thisOSR.get("operator");

					CombinedPriceCOSR cpcOSR = new CombinedPriceCOSR(name, label, value, OSRValueType, OSRCategory, operation, rule, operator);
					cOSR = cpcOSR;
					break;*/
				}

				OSRQueryString += cOSR.interpretRuleToQuery();
			}
			
			if(i+1 < OSRList.size()){
				if("OR".equals(thisOSR.operation)){
					OSRQueryString += " } ";
					OSRQueryString += " UNION ";
				}
			}
			else{
				OSRQueryString += " } ";
			}
		}
		return OSRQueryString;
	}
	
	/**
	 * retrieves all the non functional properties (NFPs) of a specified offering.
	 * 
	 * @param offeringIRI
	 * @return
	 */
	public Map<String, String> getNonFunctionalPropertiesForOffering(String offeringIRI) {
		//Fetching NFPs
		String nfpQueryString = 
				"PREFIX schema: <" + Namespace.SCHEMA + "> " +
				"PREFIX rdf: <" + Namespace.RDF + "> " +
				"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
				"PREFIX td: <" + Namespace.TD + "> " +
				"SELECT ?prop ?val " +
				"WHERE {" +
				"      <" + offeringIRI + "> ?prop ?val " +
				"      FILTER (?prop != schema:name) ." +
				"      FILTER (?prop != rdf:type) ." +
				"      FILTER (?prop != bigiot:providerId) ." +
				"      FILTER (?prop != schema:category) ." +
				"      FILTER (?prop != bigiot:endpoint) ." +
				"      FILTER (?prop != td:hasInput) ." +
				"      FILTER (?prop != td:hasOutput) ." +
				"      }";
		
		Map<String, String> nfpMap = new HashMap<String, String>();
		
		Query nfpQuery = QueryFactory.create(nfpQueryString);
		QueryExecution nfpQueryEx = QueryExecutionFactory.create(nfpQuery, rdfModel);
		ResultSet nfpQueryResults = nfpQueryEx.execSelect();
		while (nfpQueryResults.hasNext()) {
			QuerySolution solution = nfpQueryResults.next();
			String prop = solution.get("prop").toString();
			RDFNode val = solution.get("val");
			fillNestedNFP(nfpMap, "", prop, val);
		}
		nfpQueryEx.close();
		
		return nfpMap;
	}
	
	//Fetch the nested NFPs recursively, and put them into a map
	private void fillNestedNFP(Map<String,String> nfpMap, String prefix, String predicate, RDFNode value){
		predicate = predicate.substring(predicate.lastIndexOf('#')+1);
		predicate = predicate.substring(predicate.lastIndexOf('/')+1);
		if(value.isResource()){
			StmtIterator iter = value.asResource().listProperties();
			while (iter.hasNext()) {
				Statement stmt = iter.next();
				String pred = stmt.getPredicate().toString();
				RDFNode obj = stmt.getObject();
				fillNestedNFP(nfpMap, prefix+predicate+".", pred, obj);
			}
		}
		else nfpMap.put(prefix+predicate, value.asLiteral().getString());
	}

	
	public Map<String,String> getOfferingParams(String ingredientID, String offeringID){
		Map<String,String> offeringParams = new HashMap<String,String>();
		try{
			if(rdfModel==null) return offeringParams;
			
			Resource offering = rdfModel.getResource(offeringID);
			if(offering == null) return offeringParams;
			
			if(offering.hasProperty(endpoint)){
				Resource endPoints = offering.getProperty(endpoint).getResource();
				if(endPoints.hasProperty(endpointUrl)) offeringParams.put("url", endPoints.getProperty(endpointUrl).getString());
				if(endPoints.hasProperty(endpointType)){
					String endpointTypeStr = endPoints.getProperty(endpointType).getString();
					int separator = endpointTypeStr.indexOf('_');
					offeringParams.put("protocol", endpointTypeStr.substring(0, separator));
					offeringParams.put("method", endpointTypeStr.substring(separator + 1));
				} else {
				    logger.warn("Offering {} has no endpoint. Code generation will not work!", offeringID);
                }
			}

			String inputsQueryString = 
					"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
					"PREFIX td: <" + Namespace.TD + "> " +
					"PREFIX schema: <" + Namespace.SCHEMA + "> " +
					"SELECT ?ingInputID ?offInputName " +
					"WHERE {" +
					"      <" + ingredientID + "> td:hasInput ?ingInputID . " +
					"      <" + offeringID + "> td:hasInput ?offInputID . " +
					"      ?ingInputID bigiot:rdfType ?rdfType . " +
					"      ?offInputID bigiot:rdfType ?rdfType . " +
					"      ?offInputID schema:name ?offInputName . " +
					"      }";
			
			Query inputsQuery = QueryFactory.create(inputsQueryString);
			QueryExecution inputsQueryEx = QueryExecutionFactory.create(inputsQuery, rdfModel);
			ResultSet inputsQueryResults = inputsQueryEx.execSelect();
			
			while (inputsQueryResults.hasNext()) {
				QuerySolution result = inputsQueryResults.next();
		    	String ingInputID = result.getResource("ingInputID").getURI();
		    	String offInputName = result.getLiteral("offInputName").getString();
		    	offeringParams.put(ingInputID, offInputName);
			}
			inputsQueryEx.close();
			
			String outputsQueryString = 
					"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
					"PREFIX td: <" + Namespace.TD + "> " +
					"PREFIX schema: <" + Namespace.SCHEMA + "> " +
					"SELECT ?ingOutputID ?offOutputName " +
					"WHERE {" +
					"      <" + ingredientID + "> td:hasOutput ?ingOutputID . " +
					"      <" + offeringID + "> td:hasOutput ?offOutputID . " +
					"      ?ingOutputID bigiot:rdfType ?rdfType . " +
					"      ?offOutputID bigiot:rdfType ?rdfType . " +
					"      ?offOutputID schema:name ?offOutputName . " +
					"      }";
			
			Query outputsQuery = QueryFactory.create(outputsQueryString);
			QueryExecution outputsQueryEx = QueryExecutionFactory.create(outputsQuery, rdfModel);
			ResultSet outputsQueryResults = outputsQueryEx.execSelect();
			
			while (outputsQueryResults.hasNext()) {
				QuerySolution result = outputsQueryResults.next();
		    	String ingOutputID = result.getResource("ingOutputID").getURI();
		    	String offOutputName = result.getLiteral("offOutputName").getString();
		    	offeringParams.put(ingOutputID, offOutputName);
			}
			outputsQueryEx.close();
			
			String offeringCategory = offering.getProperty(category).getResource().getURI();
			if(offeringCategory.equals(Namespace.IF_CONDITION) || offeringCategory.equals(Namespace.FOR_LOOP)){
				String bypassQueryString = 
						"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
						"PREFIX td: <" + Namespace.TD + "> " +
						"SELECT ?input ?output " +
						"WHERE {" +
						"      <" + offeringID + "> td:hasInput ?input . " +
						"      <" + offeringID + "> td:hasOutput ?output . " +
						"      ?input bigiot:rdfType ?rdfType . " +
						"      ?output bigiot:rdfType ?rdfType . " +
						"      }";
				
				Query bypassQuery = QueryFactory.create(bypassQueryString);
				QueryExecution bypassQueryEx = QueryExecutionFactory.create(bypassQuery, rdfModel);
				ResultSet bypassQueryResults = bypassQueryEx.execSelect();
				
				while (bypassQueryResults.hasNext()) {
					QuerySolution result = bypassQueryResults.next();
			    	String inputID = result.getResource("input").getURI();
			    	String outputID = result.getResource("output").getURI();
			    	offeringParams.put(outputID, inputID);
				}
				bypassQueryEx.close();
			}
			
			return offeringParams;
			
		} catch(Exception e){
			logger.error("Error occurred while fetching the offering " + offeringID + " : ", e);
			return offeringParams;
		}
	}

	public String getFacts(String uri) {
		// Get all direct facts about a specific subject
		String queryString = "CONSTRUCT {\n" +
				"?uri ?rel ?ob\n" +
				"}\n" +
				" where {\n" +
				"?uri ?rel ?ob.\n" +
				"}";
		ParameterizedSparqlString pss = new ParameterizedSparqlString(queryString);
		pss.setIri("uri", uri);

		QueryExecution qe = QueryExecutionFactory.create(pss.toString(), rdfModel);
		Model m = qe.execConstruct();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		RDFDataMgr.write(bos, m, Lang.JSONLD);
		return new String(bos.toByteArray(), StandardCharsets.UTF_8);
	}

	public Set<String> getRDFType(String uri) {
		String queryString =
				"PREFIX bigiot: <" + Namespace.BIGIOT + "> " +
						"PREFIX td: <" + Namespace.TD + "> " +
						"SELECT ?type " +
						"WHERE {" +
						"      <" + uri + "> bigiot:rdfType ?type . " +
						"}";

		Query q = QueryFactory.create(queryString);
		QueryExecution qe = QueryExecutionFactory.create(q, rdfModel);
		ResultSet rs = qe.execSelect();
		Set<String> result = new HashSet<String>();
		while (rs.hasNext()) {
			QuerySolution s = rs.next();
			result.add(s.getResource("type").getURI());
		}
		return result;
	}
	
	public String createRecipe(String recipeName, String recipeID, String data, String format){
		try{
			if(rdfModel==null) return "Unable to connect to database successfully!";
			
			Resource recipe = ResourceFactory.createResource(recipeID);
			logger.debug("Recipe URI : " + recipe.getURI() + " : " + rdfModel.containsResource(recipe));
			if(rdfModel.containsResource(recipe)) return "A recipe with the name " + recipeName + " already exists!";
			else{
				rdfModel.read(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), null, format);
				//rdfModel.write(new FileOutputStream(rdfFilePath), rdfFormat);
				
				return "Recipe added to in-memory triple store!";
			}
		} catch(Exception e){
			logger.error("Error occurred while writing the recipe : ", e);
			return "Error occurred while writing the recipe!";
		}
	}
	
	public String createRecipeRuntimeConfiguration(String recipeInstanceName, String recipeInstanceID, String data, String format){
		try{
			if(rdfModel==null) return "Unable to connect to database successfully!";
			
			Resource recipeInstance = ResourceFactory.createResource(recipeInstanceID);
			logger.debug("RecipeRuntimeConfiguration URI : " + recipeInstance.getURI() + " : " + rdfModel.containsResource(recipeInstance));
			if(rdfModel.containsResource(recipeInstance)) return "A RecipeRuntimeConfiguration with the name " + recipeInstanceName + " already exists! Please try again.";
			else{
				rdfModel.read(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), null, format);
				rdfModel.write(new FileOutputStream(rdfFilePath), rdfFormat);
				return "RecipeRuntimeConfiguration written to the database!";
			}
		} catch(Exception e){
			logger.error("Error occurred while writing the RecipeRuntimeConfiguration : ", e);
			return "Error occurred while writing the RecipeRuntimeConfiguration!";
		}
	}
	
	public String createIngredientRC(String IRCName, String IRCID, String data, String format){
		try{
			if(rdfModel==null) return "Unable to connect to database successfully!";
			
			Resource IRC = ResourceFactory.createResource(IRCID);
			logger.debug("IngredientRuntimeConfiguration URI : " + IRC.getURI() + " : " + rdfModel.containsResource(IRC));
			if(rdfModel.containsResource(IRC)) return "An IngredientRuntimeConfiguration with the name " + IRCName + " already exists! Please try again.";
			else{
				rdfModel.read(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), null, format);
				rdfModel.write(new FileOutputStream(rdfFilePath), rdfFormat);
				return "IngredientRuntimeConfiguration written to the database!";
			}
		} catch(Exception e){
			logger.error("Error occurred while writing the IngredientRuntimeConfiguration : ", e);
			return "Error occurred while writing the IngredientRuntimeConfiguration!";
		}
	}

	public String createOSR(String OSRName, String OSRID, String data, String format){
		try{
			if(rdfModel==null) return "Unable to connect to database successfully!";
			
			Resource OSR = ResourceFactory.createResource(OSRID);
			logger.debug("OSR URI : " + OSR.getURI() + " : " + rdfModel.containsResource(OSR));
			if(rdfModel.containsResource(OSR)) return "An OSR with the name " + OSRName + " already exists! Please try again.";
			else{
				rdfModel.read(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), null, format);
				rdfModel.write(new FileOutputStream(rdfFilePath), rdfFormat);
				return "OSR written to the database!";
			}
		} catch(Exception e){
			logger.error("Error occurred while writing the OSR : ", e);
			return "Error occurred while writing the OSR!";
		}
	}

	public Map<String,Object> loadRecipeFile(String data, String format){
		try{
			Model tempModel = Namespace.createBasicRDFModel();
			tempModel.read(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)), null, format);
			
			String recipeQueryString = 
					"PREFIX offeringRecipe: <" + Namespace.OFFERING_RECIPE + "> " +
					"SELECT ?recipe " +
					"WHERE {" +
					"      ?recipe a offeringRecipe:Recipe " +
					"      }";
			
			Query recipeQuery = QueryFactory.create(recipeQueryString);
			QueryExecution recipeQueryEx = QueryExecutionFactory.create(recipeQuery, tempModel);
			ResultSet recipeQueryResults = recipeQueryEx.execSelect();
			
			Map<String,Object> result = null;
		    if (recipeQueryResults.hasNext()) {
		    	Resource recipe = recipeQueryResults.next().getResource("recipe");
		    	result = getRecipePattern(tempModel, recipe.getURI());
		    }
		    recipeQueryEx.close();
		    return result;
			
		} catch(Exception e){
			logger.error("Error occurred while loading the recipe file: ", e);
			return null;
		}
	}
	
}

