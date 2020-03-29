package eu.bigiot.marketplace.sample_scripts;

import org.apache.http.client.methods.HttpPost;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import java.util.Scanner;
import java.util.HashMap;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.InputStream;

public class EmailDirectionsToParking{

	public static void main(String[] args){

		Map<String,Object> userInputs = new HashMap<String,Object>();
		Scanner scanner = new Scanner(System.in);

		//Fetching the value for getGeoCordinates_userAddress
		try {
			System.out.print("\nEnter the value for getGeoCordinates_userAddress :");
			userInputs.put("getGeoCordinates_userAddress", JsonUtils.fromString(scanner.nextLine()));
		} catch (Exception e) {
			System.out.println("Input Error : Invalid value provided for getGeoCordinates_userAddress!");
			scanner.close();
			return;
		}

		//Fetching the value for sendEmail_userEmail
		try {
			System.out.print("\nEnter the value for sendEmail_userEmail :");
			userInputs.put("sendEmail_userEmail", JsonUtils.fromString(scanner.nextLine()));
		} catch (Exception e) {
			System.out.println("Input Error : Invalid value provided for sendEmail_userEmail!");
			scanner.close();
			return;
		}

		scanner.close();

		//Collecting input parameters for the ingredient getAllParkSites
		Map<String,Object> getAllParkSites_inputs = new HashMap<String,Object>();

		//Executing the ingredient getAllParkSites
		Map<String,Object> getAllParkSites_outputs = getAllParkSites(getAllParkSites_inputs);

		//Collecting input parameters for the ingredient getGeoCordinates
		Map<String,Object> getGeoCordinates_inputs = new HashMap<String,Object>();
		getGeoCordinates_inputs.put("userAddress", userInputs.get("getGeoCordinates_userAddress"));

		//Executing the ingredient getGeoCordinates
		Map<String,Object> getGeoCordinates_outputs = getGeoCordinates(getGeoCordinates_inputs);

		//Collecting input parameters for the ingredient getNearestParkSite
		Map<String,Object> getNearestParkSite_inputs = new HashMap<String,Object>();
		getNearestParkSite_inputs.put("allParkSites", getAllParkSites_outputs.get("parkingLocations"));
		getNearestParkSite_inputs.put("userCoordinates", getGeoCordinates_outputs.get("GeoCoordinates"));

		//Executing the ingredient getNearestParkSite
		Map<String,Object> getNearestParkSite_outputs = getNearestParkSite(getNearestParkSite_inputs);

		//Collecting input parameters for the ingredient getDirections
		Map<String,Object> getDirections_inputs = new HashMap<String,Object>();
		getDirections_inputs.put("destination", getNearestParkSite_outputs.get("nearesrParkCoordinates"));
		getDirections_inputs.put("source", getGeoCordinates_outputs.get("GeoCoordinates"));

		//Executing the ingredient getDirections
		Map<String,Object> getDirections_outputs = getDirections(getDirections_inputs);

		//Collecting input parameters for the ingredient sendEmail
		Map<String,Object> sendEmail_inputs = new HashMap<String,Object>();
		sendEmail_inputs.put("body", getDirections_outputs.get("directions"));
		sendEmail_inputs.put("subject", getNearestParkSite_outputs.get("nearesrParkName"));
		sendEmail_inputs.put("receiverAddress", userInputs.get("sendEmail_userEmail"));

		//Executing the ingredient sendEmail
		sendEmail(sendEmail_inputs);

	}

	//The method to access the ingredient getDirections
	private static Map<String,Object> getDirections(Map<String,Object> inputParams) {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String url = "http://localhost:8080/marketplace/gateway/getDirections";
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

	//The method to access the ingredient sendEmail
	private static Map<String,Object> sendEmail(Map<String,Object> inputParams) {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String url = "http://localhost:8080/marketplace/gateway/sendEmail";
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

	//The method to access the ingredient getAllParkSites
	private static Map<String,Object> getAllParkSites(Map<String,Object> inputParams) {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String url = "http://localhost:8080/marketplace/gateway/getAllParkSites";
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			Map<String,Object> outputParams = (Map<String,Object>) JsonUtils.fromInputStream(is, "iso-8859-1");
			return outputParams;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//The method to access the ingredient getGeoCordinates
	private static Map<String,Object> getGeoCordinates(Map<String,Object> inputParams) {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String url = "http://localhost:8080/marketplace/gateway/getGeoCordinates";
			url = url + "?userAddress=" + inputParams.get("userAddress").toString().replaceAll("\\s+", "+");
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			Map<String,Object> outputParams = (Map<String,Object>) JsonUtils.fromInputStream(is, "iso-8859-1");
			return outputParams;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	//The method to access the ingredient getNearestParkSite
	private static Map<String,Object> getNearestParkSite(Map<String,Object> inputParams) {
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

}

