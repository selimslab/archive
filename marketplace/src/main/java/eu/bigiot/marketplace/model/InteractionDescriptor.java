package eu.bigiot.marketplace.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * InteractionDescriptor enables the choreography of offerings at runtime
 */
public class InteractionDescriptor {
	@JsonProperty("offering")
	public String offeringURI;		// id of the offering that represents this device in recipes
	
	@JsonProperty("recipeRuntimeConfiguration")
	public String rrcURI;			// id of the RRC (Recipe Runtime Configuration) from which this InDe was created

	@JsonProperty("inputWaitWindow")
	public int inputWaitWindow;	// time (in milliseconds) until partial inputs are discarded

	@JsonProperty("outputs")
	public Map<String, Interaction> outputs;	// Map(<interactionURI> -> interation_object)

	@JsonProperty("inputs")
	public List<String> inputs;		// List of inputURIs

	// Literals
	public static final String OFFERING_URI = "offering";
	public static final String RRC_URI = "recipeRuntimeConfiguration";
	public static final String INPUT_WAIT_WINDOW = "inputWaitWindow";
	public static final String OUTPUTS = "outputs";
	public static final String INPUTS = "inputs";

	public static final int INPUT_WAIT_WINDOW_DEFAULT = 10000;		// milliseconds

	public InteractionDescriptor(){
		inputWaitWindow = INPUT_WAIT_WINDOW_DEFAULT;
		outputs = new HashMap<String, Interaction>();
		inputs = new ArrayList<String>();
	}
	
	// JSON to InteractionDescriptor object
	public InteractionDescriptor(Map<String, Object> inDe){
		inputWaitWindow = INPUT_WAIT_WINDOW_DEFAULT;
		inputs = new ArrayList<String>();
		
		offeringURI = (String) inDe.get("offering");
		rrcURI = (String) inDe.get("rrcURI");
		inputWaitWindow = (int) inDe.get("inputWaitWindow");
		
		outputs = new HashMap<String, Interaction>();
		Map<String, Interaction> outputsMap = (Map<String, Interaction>) inDe.get("outputs");
		if(outputsMap != null){
			for(Map.Entry<String, Interaction> entry : outputsMap.entrySet()){
	        	String interactionURI = entry.getKey();
	        	Map<String, Object> interactionsMap = (Map<String, Object>) entry.getValue();
	        	Interaction interaction = new Interaction(interactionsMap);
	        	outputs.put(interactionURI, interaction);
			}
		}
		inputs = (List<String>) inDe.get("inputs");
	}

}
