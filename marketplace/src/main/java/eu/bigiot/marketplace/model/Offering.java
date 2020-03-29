package eu.bigiot.marketplace.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Offering {
	
	public String id;
	public String name;
	public String category;
	public String providerId;
	public String endpointUrl;
	public String endpointType;
	public List<String> inputs;
	public List<String> outputs;
	
	/**
	 * all non-functional properties
	 */
	public Map<String, String> nfpMap;
	
	public Offering(
			String id, 
			String name, 
			String category, 
			String providerId, 
			String endpointUrl, 
			String endpointType,
			Map<String, String> nfpMap) {
		this.id = id;
		this.name = name;
		this.category = category;
		this.providerId = providerId;
		this.endpointUrl = endpointUrl;
		this.endpointType = endpointType;
		this.inputs = new ArrayList<String>();
		this.outputs = new ArrayList<String>();
		this.nfpMap = nfpMap;
	}
	
	public Offering(Map<String, Object> selectedOffering) {
		id = (String) selectedOffering.get("id");
		name = (String) selectedOffering.get("name");
	}

	@Override
	public String toString() {
		String oString = "[IRI=" + id + ", name=" + name + ", catgory="+ category + ", NFPs: ";
		
		if (nfpMap != null) {
			for (String nfpItem : nfpMap.keySet()) {
				oString += nfpItem + ": " + nfpMap.get(nfpItem) + " "; 
			}
		}
		
		oString += "]";
		return oString;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}
	
	public String getProviderId() {
		return providerId;
	}

	public String getEndpointUrl() {
		return endpointUrl;
	}

	public String getEndpointType() {
		return endpointType;
	}
	
	public void addInput(String inputName) {
		inputs.add(inputName);
	}
	
	public void addOutput(String outputName) {
		inputs.add(outputName);
	}
	
	public List<String> getInputs() {
		return inputs;
	}

	public List<String> getOutputs() {
		return outputs;
	}

	public Map<String, String> getNfpMap() {
		return nfpMap;
	}
	
	
}
