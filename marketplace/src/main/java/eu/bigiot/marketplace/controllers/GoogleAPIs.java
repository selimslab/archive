package eu.bigiot.marketplace.controllers;

import com.github.jsonldjava.utils.JsonUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class GoogleAPIs{
	
	//Generate Google API keys to enable these services
	private static final String directionsAPIKey = "AIzaSyAvC-rgFZmE8_Jea9oMqKBLov1zNk2TVgM";
	private static final String cordinatesAPIKey = "AIzaSyAEqycV88f2FvtEHmhmpRyDx3wSC84auHE";
	
	public static Map<String, Double> getGeoCordinates(String address) {
		Map<String,Double>  cordinates = new HashMap<String,Double>();
		try {
			
			HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
			String url = "https://maps.googleapis.com/maps/api/geocode/json";
			url = url + "?address=" + address.replaceAll("\\s+", "+");
			url = url + "&key=" + cordinatesAPIKey;
			System.out.println("API call :" + url);
			
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
						
			Map<String,Object> response = (Map<String,Object>) JsonUtils.fromInputStream(is, "iso-8859-1");
			
			List results = (List)response.get("results");
			if(results!=null && !results.isEmpty()){
				Map<String,Object> geometry = (Map<String, Object>) ((Map<String,Object>) results.get(0)).get("geometry");
				if(geometry!=null){
					Map<String,Object> location = (Map<String, Object>) geometry.get("location");
					if(location!=null){
						cordinates.put("latitude", (Double) location.get("lat"));
						cordinates.put("longitude", (Double) location.get("lng"));
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cordinates;
	}
	
	
	public static String getDirections(double srcLat, double srcLng, double desLat, double desLng) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html>");
		try {
			HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
			String url = "https://maps.googleapis.com/maps/api/directions/json";
			url = url + "?language=" + "EN-US";
			url = url + "&origin=" + srcLat + "," + srcLng;
			url = url + "&destination=" + desLat + "," + desLng;
			url = url + "&key=" + directionsAPIKey;
			System.out.println("API call :" + url);
			
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			InputStream is = httpEntity.getContent();
			Map<String,Object> response = (Map<String,Object>) JsonUtils.fromInputStream(is, "UTF-8");
			
			List routes = (List)response.get("routes");
			if(routes!=null && !routes.isEmpty()){
				List legs = (List) ((Map<String,Object>) routes.get(0)).get("legs");
				if(legs!=null && !legs.isEmpty()){
					List steps = (List) ((Map<String,Object>) legs.get(0)).get("steps");
					if(steps!=null){
						for(int i=0;i<steps.size();i++) {
							String instruction = ((Map<String, Object>)steps.get(i)).get("html_instructions").toString();
							sb.append(instruction+"<br>");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sb.append("</html>");
		return sb.toString();
	}
	
	
	public static void sendEmail(String receiverAddress, String subject, String body) {
		try {
			
			//Enter a valid email address for 'senderAddress' and generate the APIKey and SecretKey for that email address with MailJet
			final String APIKey = "e2fec636e39aa4d1cb00d2e805325d39";
			final String SecretKey = "00da2be3923347ea76efb35882b2ce00";
			String senderAddress = "arne.broering@siemens.com";
	 
			Properties props = new Properties ();
	 
			props.put ("mail.smtp.host", "in.mailjet.com");
			props.put ("mail.smtp.socketFactory.port", "465");
			props.put ("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put ("mail.smtp.auth", "true");
			props.put ("mail.smtp.port", "465");
	 
			Session session = Session.getDefaultInstance (props,
				new javax.mail.Authenticator ()
				{
					protected PasswordAuthentication getPasswordAuthentication ()
					{
						return new PasswordAuthentication (APIKey, SecretKey);
					}
				});
	 
			Message message = new MimeMessage (session);
			message.setFrom (new InternetAddress (senderAddress));
			message.setRecipients (Message.RecipientType.TO, InternetAddress.parse(receiverAddress));
			message.setSubject (subject);
			message.setContent(body, "text/html");
 
			Transport.send (message);
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	
}
