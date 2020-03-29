package eu.bigiot.marketplace.controllers;

import com.github.jsonldjava.utils.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MindboxAPIs{
	
	public static List<String> getAllStations() {
		List<String> stations = new ArrayList<String>();
		try {
			HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
			String url = "http://opendata.dbbahnpark.info/api/beta/stations";
			
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			Map<String,Object> response = (Map<String,Object>) JsonUtils.fromInputStream(is, "UTF-8");
			is.close();
			
			List results = (List)response.get("results");
			if(results!=null){
				for(int i=0;i<results.size();i++) {
					String station = ((Map<String, Object>)results.get(i)).get("station").toString();
					stations.add(station);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stations;
	}
	
	
	public static Map<String, Map<String, Double>> getAllParkSites() {
		Map<String,Map<String,Double>> parkSites = new HashMap<String,Map<String,Double>>();
		try {
			HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
			String url = "http://opendata.dbbahnpark.info/api/beta/sites";
			
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			Map<String,Object> response = (Map<String,Object>) JsonUtils.fromInputStream(is, "UTF-8");
			is.close();
			
			List results = (List)response.get("results");
			if(results!=null){
				for(int i=0;i<results.size();i++) {
					try {
						String name = ((Map<String, Object>)results.get(i)).get("parkraumDisplayName").toString();
						Map<String,Double> cordinates = new HashMap<String,Double>();
						cordinates.put("latitude", Double.parseDouble(((Map<String, Object>)results.get(i)).get("parkraumGeoLatitude").toString().replace(',', '.')));
						cordinates.put("longitude", Double.parseDouble(((Map<String, Object>)results.get(i)).get("parkraumGeoLongitude").toString().replace(',', '.')));
						parkSites.put(name, cordinates);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parkSites;
	}
	
}
