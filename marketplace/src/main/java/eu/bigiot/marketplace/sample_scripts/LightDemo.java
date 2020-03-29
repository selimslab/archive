package eu.bigiot.marketplace.sample_scripts;

import org.eclipse.californium.core.CoapResponse;
import org.apache.http.client.methods.HttpPost;
import org.eclipse.californium.core.CoapClient;
import com.github.jsonldjava.utils.JsonUtils;
import org.apache.http.client.HttpClient;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import java.util.HashMap;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.InputStream;

public class LightDemo{

	public static void main(String[] args){
		Object UntilTimeLimit_output_trigger = null;

		for (Integer timeStep = 0 ; timeStep < 100 ; timeStep += 1) {

			//Waiting for 1000 milliseconds before the execution
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//Collecting input parameters for the ingredient CheckSwitch
			Map<String,Object> CheckSwitch_inputs = new HashMap<String,Object>();
			CheckSwitch_inputs.put("trigger", UntilTimeLimit_output_trigger);

			//Executing the ingredient CheckSwitch
			Map<String,Object> CheckSwitch_outputs = CheckSwitch(CheckSwitch_inputs);

			//Collecting input parameters for the ingredient Light
			Map<String,Object> Light_inputs = new HashMap<String,Object>();
			Light_inputs.put("status", CheckSwitch_outputs.get("on_off"));

			//Executing the ingredient Light
			Light(Light_inputs);

		}

	}

	//The method to access the ingredient Light
	private static Map<String,Object> Light(Map<String,Object> inputParams) {
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			String url = "http://localhost:8080/marketplace/gateway/controlAllLights";
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

	//The method to access the ingredient CheckSwitch
	private static Map<String,Object> CheckSwitch(Map<String,Object> inputParams) {
		try {
			CoapClient client = new CoapClient("coap://127.0.0.1:5687/enoceanSwitch/switch");
			CoapResponse response = client.get();
			String payload = new String(response.getPayload());
			Map<String,Object> outputParams = new HashMap<String,Object>();
			outputParams.put("on_off", payload);
			return outputParams;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}

