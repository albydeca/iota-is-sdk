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
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LogAuditor {
    private String channel_address = null;
    private String jwt = null;
    private final String api_key = "api-key=94F5BA49-12B6-4E45-A487-BF91C442276D";
    private String private_key = null;
    private String public_key = null;
    private String did_id = null;
    private String nonce = null;
    private String seed = null;
    private String subscriptionLink = null;
    Utils info = new Utils();

    {
        DID did = new DID();
        String[] data;
        try {
            data = did.createDID("LogAuditor");
            this.private_key = data[0];
            this.public_key = data[1];
            this.did_id = data[2];
            this.nonce = did.createNonce(this.did_id);
            this.jwt = did.sigantureNonce(this.private_key, this.public_key, this.nonce, this.did_id);
            this.channel_address = info.getChannel_address();
            requestSubscription();
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }
    }

    public void requestSubscription() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/subscriptions/request/" + this.channel_address + "?" + this.api_key);

        String json_inn = new JSONObject()
                .put("accessRights", "Read")
                .toString();

        StringEntity entity = new StringEntity(json_inn);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        this.seed = respons.getString("seed");
        this.subscriptionLink = respons.getString("subscriptionLink");
        info.setSubscriptionLink(this.subscriptionLink);
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

}
