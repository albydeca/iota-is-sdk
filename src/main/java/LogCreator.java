import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bitcoinj.core.Base58;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.json.JSONArray;
import org.json.JSONObject;


import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.charset.StandardCharsets;


public class LogCreator {
    private final String api_key = "api-key=94F5BA49-12B6-4E45-A487-BF91C442276D";
    private String private_key = null;
    private String public_key = null;
    private String did_id = null;
    private String nonce = null;
    private String jwt = null;
    public String channel_address = null;
    Utils info = new Utils();
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

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
            writeDataOnChannel();
            getDataFromChannel();
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }
    }

    public void createChannel() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/channels/create" + "?" + this.api_key);

        String json_in = "{\n" +        //TODO: Convert into json
                "  \"topics\": [\n" +
                "    {\n" +
                "      \"type\": \"example-channel-data\",\n" +
                "      \"source\": \"channel-creator\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"encrypted\": false\n" +
                "}";

        StringEntity entity = new StringEntity(json_in);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        this.channel_address = respons.getString("channelAddress");
        info.setChannel_address(this.channel_address);
        System.out.println("Channel Address created: " + this.channel_address);
    }

    public void writeDataOnChannel() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/channels/logs/" + this.channel_address + "?" + this.api_key);

        String json_in = "{\n" +            //TODO: Convert into json
                "  \"type\": \"example-channel-data\",\n" +
                "  \"created\": \"2021-07-23T05:25:42.325Z\",\n" +
                "  \"metadata\": \"example-meta-data\",\n" +
                "  \"payload\": {\n" +
                "    \"example\": 1\n" +
                "  }\n" +
                "}";

        StringEntity entity = new StringEntity(json_in);
        httpPost.setEntity(entity);
        httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
        httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE,"application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        System.out.println("Message send to channel: {example:1}");
        //System.out.println(respons);
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
