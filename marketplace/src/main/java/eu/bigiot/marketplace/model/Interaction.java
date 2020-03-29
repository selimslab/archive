package eu.bigiot.marketplace.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Interaction from InteractionDescriptor
 */
public class Interaction {
	@JsonProperty("destination")
	public List<String> destination;	// endpoint url of the offering that receives the output of this offering
	
	@JsonProperty("outInMapping")
	public Map<String, String> outInMapping;	// how to map output keys to input keys for this interaction
	
	// Literals
	public static final String DESTINATION = "destination";
	public static final String OUT_IN_MAPPING = "outInMapping";

	public Interaction(){
		destination = new ArrayList<String>();
		outInMapping = new HashMap<String, String>();
	}

	// JSON to Interaction object
	public Interaction(Map<String, Object> interactionsMap) {
		destination = (List<String>) interactionsMap.get("destination");
		outInMapping = (Map<String, String>) interactionsMap.get("outInMapping");
	}
}
