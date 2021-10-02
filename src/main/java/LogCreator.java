import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;


public class LogCreator {
    private final String api_key = "api-key=94F5BA49-12B6-4E45-A487-BF91C442276D";
    private String private_key = null;
    private String public_key = null;
    private String did_id = null;
    private String nonce = null;
    private String jwt = null;
    public String channel_address = null;
    Utils info = new Utils();

    {
        DID did = new DID();
        String[] date;
        try {
            date = did.createDID("LogCreator");
            this.private_key = date[0];
            this.public_key = date[1];
            this.did_id = date[2];
            this.nonce = did.createNonce(this.did_id);
            this.jwt = did.sigantureNonce(this.private_key, this.public_key, this.nonce, this.did_id);
            createChannel();
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }
    }

    public void createChannel() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/channels/create" + "?" + this.api_key);

        String json = new JSONObject()
                .put("topics", new JSONArray().put(new JSONObject().put("type", "example-channel-data").put("source", "channel-creator")))
                .put("encrypted", "true")
                .toString();

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        this.channel_address = respons.getString("channelAddress");
        info.setChannel_address(this.channel_address);
        System.out.println("Channel successfully created: " + this.channel_address);
    }

    public void writeDataOnChannel(JSONObject input) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/channels/logs/" + this.channel_address + "?" + this.api_key);

        LocalDateTime myObj = LocalDateTime.now();
        String json = new JSONObject()
                .put("type", "example-channel-data")
                .put("created", "2021-07-23T05:25:42.325Z")
                .put("metadata", "example-meta-data")
                .put("payload", input)
                .toString();

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        //JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        System.out.println("Message send to channel: " + input);
    }

    public void getDataFromChannel() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://ensuresec.solutions.iota.org/api/v0.1/channels/logs/" + this.channel_address + "?limit=5&asc=true" + "&" + this.api_key);

        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);

        CloseableHttpResponse response = client.execute(httpGet);

        JSONArray respons = new JSONArray(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        System.out.println("Message from channel: " + respons.getJSONObject(0).getJSONObject("channelLog").getJSONObject("payload"));
        client.close();
    }

    public String getAllSubscriptions() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://ensuresec.solutions.iota.org/api/v0.1/subscriptions/" + this.channel_address + "?is-authorized=false" + "&" + this.api_key);

        httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);

        CloseableHttpResponse response = client.execute(httpGet);

        JSONArray respons = new JSONArray(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        String request = respons.getJSONObject(0).getString("subscriptionLink");
        System.out.println("SubscriptionLink: " + request);
        client.close();
        return respons.getJSONObject(0).getString("subscriptionLink");
    }

    public void authorizedSubscriptions(String subscriptionLink) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/subscriptions/authorize/" + this.channel_address + "?" + this.api_key);

        String json = new JSONObject().put("subscriptionLink", subscriptionLink)
                .toString();

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        System.out.println("Authorized entry: " + respons.getString("keyloadLink"));
    }

}
