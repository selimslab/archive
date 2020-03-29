package eu.bigiot.marketplace.database;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

//BIGIoT namespace
public class Namespace {

	//Namespaces used by the the marketplace
	public static final String OWL 				= "http://www.w3.org/2002/07/owl#";
	public static final String SCHEMA 			= "http://schema.org/";
	public static final String XSD 				= "http://www.w3.org/2001/XMLSchema#";
	public static final String DC 				= "http://purl.org/dc/elements/1.1/";
	public static final String RDF 				= "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static final String RDFS 			= "http://www.w3.org/2000/01/rdf-schema#";
	public static final String DATEX 			= "http://vocab.datex.org/terms#";
	public static final String TD 				= "http://w3c.github.io/wot/w3c-wot-td-ontology.owl#";
	public static final String OFFERING_RECIPE 	= "http://w3c.github.io/bigiot/offeringRecipeModel#";
	public static final String OFFERING_RECIPE_RUNTIME_CONFIG = "http://w3c.github.io/bigiot/offeringRecipeRuntimeConfigurationModel#";
	public static final String BIGIOT 			= "http://big-iot.eu/core#";
	public static final String SKOS 			= "http://www.w3.org/2004/02/skos/core#";
	public static final String PATTERN 			= "http://w3c.github.io/bigiot/RecipePatternModel#";
	public static final String GEO 				= "http://www.w3.org/2003/01/geo/wgs84_pos#";
	public static final String SSN 				= "https://www.w3.org/2005/Incubator/ssn/ssnx/ssn#";
	public static final String MARKETPLACE 		= "http://localhost:8080/marketplace/namespace/marketplace.ttl#";
	public static final String SAREF 			= "https://w3id.org/saref#";
	//public static final String BUILDING = "http://w3c.github.io/bigiot/smartBuilding#";
	
	//Types of resources
	public static final String TYPE_CITY = MARKETPLACE + "City";
	public static final String TYPE_SERVICE = MARKETPLACE + "Service";
	public static final String TYPE_RECIPE = OFFERING_RECIPE + "Recipe";
	public static final String TYPE_OFFERING = BIGIOT + "Offering";
	public static final String TYPE_DATA = BIGIOT + "Data";
	
	//General attributes
	public static final String NAME = SCHEMA + "name";
	public static final String DESCRIPTION = OFFERING_RECIPE + "description";
	public static final String ICON = MARKETPLACE + "icon";
	public static final String TYPE = RDF + "type";
	public static final String CATEGORY = SCHEMA + "category";

	//Attributes for recipes
	public static final String HAS_INGREDIENT = OFFERING_RECIPE + "hasIngredient";
	public static final String HAS_INTERACTION = OFFERING_RECIPE + "hasInteraction";
	public static final String HAS_INPUT = TD + "hasInputData";
	public static final String HAS_OUTPUT = TD + "hasOutputData";
	public static final String RDF_TYPE = BIGIOT + "rdfType";
	public static final String HAS_CONFIG_ATTRIBUTE = OFFERING_RECIPE + "hasConfigAttribute";
	
	//Attributes for interactions
	public static final String HAS_INGREDIENT_FROM = OFFERING_RECIPE + "hasIngredientFrom";
	public static final String HAS_INGREDIENT_TO = OFFERING_RECIPE + "hasIngredientTo";
	public static final String HAS_INGREDIENT_INPUT = OFFERING_RECIPE + "hasIngredientInput";
	public static final String HAS_INGREDIENT_OUTPUT = OFFERING_RECIPE + "hasIngredientOutput";
	
	//Attributes of offerings
	public static final String PRICE_SPECIFICATION = SCHEMA + "priceSpecification";
	public static final String PRICE_AMOUNT = SCHEMA + "price";	
	public static final String PRICE_CURRENCY = SCHEMA + "priceCurrency";
	public static final String ACCOUNTING_MODEL = BIGIOT + "accountingModel";
	public static final String LICENSE = SCHEMA + "license";
	public static final String LICENSE_TYPE = BIGIOT + "licenseType";
	public static final String REGION = SCHEMA + "spatialCoverage";
	public static final String ENDPOINT = BIGIOT + "endpoint";
	public static final String ENDPOINT_URL = SCHEMA + "url";
	public static final String ENDPOINT_TYPE = BIGIOT + "endpointType";
	
	//Attributes of data
	public static final String DATA_TYPE = BIGIOT + "rdfType";

	//Recipe categories
	public static final String BIKE_SHARING = SCHEMA + "bike_sharing";
	public static final String BUSES = SCHEMA + "buses";
	public static final String PARKING = SCHEMA + "parking";
	public static final String TRAFFIC = SCHEMA + "transportation";
	public static final String SMART_BUILDING = SCHEMA + "smartBuilding";

	//Ingredient categories
	public static final String IF_CONDITION = PATTERN + "If_Condition";
	public static final String FOR_LOOP = PATTERN + "For_Loop";
	
	//Condition attributes
	public static final String HAS_OPERATOR = PATTERN + "hasRelationalOperator";
	public static final String HAS_VARIABLE = PATTERN + "hasVariable";
	public static final String HAS_VALUE = PATTERN + "hasValue";
	
	//Loop attributes
	public static final String INIT_TYPE = PATTERN + "initType";
	public static final String INIT_VARIABLE = PATTERN + "initVariable";
	public static final String INIT_VALUE = PATTERN + "initValue";
	public static final String INCREMENT_VARIABLE = PATTERN + "incrementVariable";
	public static final String INCREMENT_VALUE = PATTERN + "incrementValue";
	
	//Operator types
	public static final String EQUAL_TO = PATTERN + "equalTo";
	public static final String LESS_THAN = PATTERN + "lessThan";
	public static final String GREATER_THAN = PATTERN + "greaterThan";
	
	//OSR
	public static final String LABEL = RDFS + "label";
	public static final String VALUE = SCHEMA + "value";
	public static final String OSR_CATEGORY = OFFERING_RECIPE_RUNTIME_CONFIG + "OSRCategory";
	public static final String OSR_VALUE_TYPE = OFFERING_RECIPE_RUNTIME_CONFIG + "OSRValueType";
	public static final String OSR_OPERATOR = OFFERING_RECIPE_RUNTIME_CONFIG + "operator";
	public static final String OSR_RULE = OFFERING_RECIPE_RUNTIME_CONFIG + "rule";
	public static final String OSR_PROPERTY = OFFERING_RECIPE_RUNTIME_CONFIG + "property";
	public static final String OSR_OFFERINGURI = OFFERING_RECIPE_RUNTIME_CONFIG + "offeringURI";
	public static final String OSR_MIN_CARDINALITY = OFFERING_RECIPE_RUNTIME_CONFIG + "minCardinality";
	public static final String OSR_MAX_CARDINALITY = OFFERING_RECIPE_RUNTIME_CONFIG + "maxCardinality";
	public static final String OSR_OPERATOR_CP = OFFERING_RECIPE_RUNTIME_CONFIG + "operator";
	
	public static Model createBasicRDFModel(){
		Model model = ModelFactory.createDefaultModel();
//		model.setNsPrefix( "schema", SCHEMA );
//		model.setNsPrefix( "xsd", XSD );
//		model.setNsPrefix( "dc", DC );
//		model.setNsPrefix( "rdf", RDF );
//		model.setNsPrefix( "rdfs", RDFS );
//		model.setNsPrefix( "datex", DATEX );
//		model.setNsPrefix( "td", TD );
//		model.setNsPrefix( "offeringRecipe", OFFERING_RECIPE );
//		model.setNsPrefix( "rrc", OFFERING_RECIPE_RUNTIME_CONFIG );
//		model.setNsPrefix( "bigiot", BIGIOT );
//		model.setNsPrefix( "pattern", PATTERN );
//		model.setNsPrefix( "geo", GEO );
//		model.setNsPrefix( "marketplace", MARKETPLACE );
//		model.setNsPrefix( "saref", SAREF );
//		model.setNsPrefix( "smartBuilding", BUILDING );
		return model;
	}
	
}
