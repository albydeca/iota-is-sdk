import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpHeaders;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {
    public static String channelAddress;
//    public static final String api_key =
//    		"api-key=94F5BA49-12B6-4E45-A487-BF91C442276D";
    public static final String apiKey = "api-key="+System.getProperty("api-key");
    public static final String iotastreamsApiBaseUrl =
    		"https://ensuresec.solutions.iota.org/api/v0.1/";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public void setChannelAddress(String channel_address) {
        Utils.channelAddress = channel_address;
    }
    public String getChannel_address() {
        return channelAddress;
    }
    
    public static JSONObject sendIotaPostRequest
    (String endpoint, JSONObject body, String jwt) 
    		throws ClientProtocolException, IOException {
    	CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(endpoint);

        StringEntity entity = new StringEntity(body.toString());
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        if(jwt != null) {
        	httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);
        }
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject result = new JSONObject(EntityUtils.toString
        		(response.getEntity(), StandardCharsets.UTF_8));
        client.close();
        return result;
    }
    
    public static JSONObject sendIOTAGetRequest(String endpoint)
    		throws ClientProtocolException, IOException {
    	CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(endpoint);
        CloseableHttpResponse response = client.execute(httpGet);

        JSONObject result = new JSONObject(EntityUtils.toString
        		(response.getEntity(), StandardCharsets.UTF_8));
        client.close();
        return result;
    }

    
    public static JSONArray sendIOTAGetRequestWithAuth
    (String endpoint, String jwt) throws ClientProtocolException, IOException {
    	CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(endpoint);

        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwt);

        CloseableHttpResponse response = client.execute(httpGet);

        JSONArray result = new JSONArray(EntityUtils.toString
        		(response.getEntity(), StandardCharsets.UTF_8));
        client.close();
        return result;
    }

}
