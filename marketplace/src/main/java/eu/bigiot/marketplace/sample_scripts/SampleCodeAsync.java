package eu.bigiot.marketplace.sample_scripts;

import org.apache.http.client.methods.HttpPost;

import com.github.jsonldjava.utils.JsonUtils;

import org.apache.http.client.HttpClient;

import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

import java.util.HashMap;

import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;


//This is how the code generated for the parallel asynchronous execution should look like.

abstract class Process implements Runnable{
	
	String id;
	Map<String, Map<String,Object>> messageContainer;
	String[] requiredInputs;
	Map<String, Map<String,String>> listenerMapping;
	
	@Override
	public void run(){
		
		//Receiver component
		Map<String,Object> inputsMap = messageContainer.get(id);
		boolean inputsAvailable = false;
		while(!inputsAvailable) {
			inputsAvailable = true;
			for(String input : requiredInputs){
				if(!inputsMap.containsKey(input)) inputsAvailable = false;
			}
		}
		
		//Task component
		Map<String,Object> outputsMap = accessOffering(inputsMap);
		
		//Sender component
		for(String outputID : outputsMap.keySet()){
			Object data = outputsMap.get(outputID);
			Map<String,String> listeners = listenerMapping.get(outputID);
			for(String listenerID : listeners.keySet()){
				Map<String,Object> listenerInbox = messageContainer.get(listenerID);
				String listenerInputID = listeners.get(listenerID);
				listenerInbox.put(listenerInputID, data);
			}
		}
		
	}
	
	//The method which accesses the offering
	public abstract Map<String,Object> accessOffering(Map<String,Object> inputs);
	
}


public class SampleCodeAsync{

	public static void main(String[] args){
		
		Map<String, Map<String,Object>> messageContainer = new HashMap<String, Map<String,Object>>();
		messageContainer.put("getGeoCordinates", new HashMap<String,Object>());
		messageContainer.put("getNearestParkSite", new HashMap<String,Object>());
		messageContainer.put("getDirections", new HashMap<String,Object>());
		
		Process getNearestParkSite = new Process() {
			@Override
			public Map<String, Object> accessOffering(Map<String, Object> inputParams) {
				try {
					HttpClient httpClient = HttpClientBuilder.create().build();
					String url = "http://localhost:8080/marketplace/gateway/getNearestParkSite";
					StringEntity entity = new StringEntity(JsonUtils.toPrettyString(inputParams));
					HttpPost httpPost = new HttpPost(url);
					httpPost.setHeader("Content-type", "application/json");
					httpPost.setEntity(entity);
					HttpResponse httpResponse = httpClient.execute(httpPost);
					HttpEntity httpEntity = httpResponse.getEntity();
					InputStream is = httpEntity.getContent();
					Map<String,Object> outputParams = (Map<String,Object>) JsonUtils.fromInputStream(is, "iso-8859-1");
					return outputParams;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		};
		
		getNearestParkSite.id = "getNearestParkSite";
		getNearestParkSite.requiredInputs = new String[]{"userLocation"};
		getNearestParkSite.listenerMapping = new HashMap<String, Map<String,String>>();
		Map<String,String> parkingLocationListeners = new HashMap<String,String>();
		parkingLocationListeners.put("getDirections", "destination");
		getNearestParkSite.listenerMapping.put("parkingLocation", parkingLocationListeners);
		
		new Thread(getNearestParkSite).start();
		
		messageContainer.get("getGeoCordinates").put("userAddress", args[0]);

	}
}

