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
import org.json.JSONObject;


import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;


public class Api {
    private final String api_key = "api-key=94F5BA49-12B6-4E45-A487-BF91C442276D";
    private String private_key = null;
    private String public_key = null;
    private String did_id = null;
    private String nonce = null;
    private String jwt = null;
    private String channel_address = null;

    public void createDID() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/identities/create?" + api_key);

        String json_in = "{\n" +                    //TODO: Convert into json and fix parameters
                "  \"username\": \"test\",\n" +
                "  \"claim\": {\n" +
                "    \"type\": \"Device\",\n" +
                "    \"category\": [\n" +
                "      \"sensor\"\n" +
                "    ],\n" +
                "    \"controlledProperty\": [\n" +
                "      \"fillingLevel\",\n" +
                "      \"temperature\"\n" +
                "    ],\n" +
                "    \"controlledAsset\": [\n" +
                "      \"wastecontainer-Osuna-100\"\n" +
                "    ],\n" +
                "    \"ipAddress\": [\n" +
                "      \"192.14.56.78\"\n" +
                "    ],\n" +
                "    \"mcc\": \"214\",\n" +
                "    \"mnc\": \"07\",\n" +
                "    \"serialNumber\": \"9845A\",\n" +
                "    \"refDeviceModel\": \"myDevice-wastecontainer-sensor-345\",\n" +
                "    \"dateFirstUsed\": \"2014-09-11T11:00:00Z\",\n" +
                "    \"owner\": [\n" +
                "      \"did:iota:CtPnfQqSZBmZEe5A5iNZzJ6pkCqUxtsFsErNfA3CeHpY\"\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        StringEntity entity = new StringEntity(json_in);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        this.did_id = respons.getJSONObject("doc").getString("id");
        this.private_key = respons.getJSONObject("key").getString("secret");
        this.public_key = respons.getJSONObject("key").getString("public");
        System.out.println("DID ID: " + this.did_id + "\n" + "Private Key: " + this.private_key + "\n" + "Public Key: " + this.public_key);
        client.close();
    }

    public void createNonce() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://ensuresec.solutions.iota.org/api/v0.1/authentication/prove-ownership/" + this.did_id + "?" + this.api_key);
        CloseableHttpResponse response = client.execute(httpGet);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        this.nonce = respons.getString("nonce");
        System.out.println("Nonce: " + this.nonce);
        client.close();
    }

    public void sigantureNonce() throws IOException, CryptoException {

        byte[] b58key = Base58.decode(this.private_key);    // Decode a base58 key and encode it as hex key
        String b58key_hex = DatatypeConverter.printHexBinary(b58key).toLowerCase();
        byte[] convert_key = DatatypeConverter.parseHexBinary(b58key_hex);

        String hash_nonce_hex = DigestUtils.sha256Hex(this.nonce); // Hash a nonce with SHA-256 (apache_commons)
        byte[] convert_nonce = DatatypeConverter.parseHexBinary(hash_nonce_hex);


        //https://stackoverflow.com/questions/53921655/rebuild-of-ed25519-keys-with-bouncy-castle-java
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(convert_key, 0);  // Encode in PrivateKey
        Signer signer = new Ed25519Signer();    // Sign a nonce using the private key
        signer.init(true, privateKey);
        signer.update(convert_nonce, 0, convert_nonce.length);
        byte[] signature = signer.generateSignature();

        //https://stackoverflow.com/questions/6625776/java-security-invalidkeyexception-key-length-not-128-192-256-bits
        String sign = DatatypeConverter.printHexBinary(signature).toLowerCase();

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/authentication/prove-ownership/" + this.did_id + "?" + this.api_key);
        String json_in = "{\n" +            //TODO: Convert into json
                "  \"signedNonce\": \"" + sign + "\"\n" +
                "}";

        StringEntity entity = new StringEntity(json_in);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        this.jwt = respons.getString("jwt");
        System.out.println("JWT: " + this.jwt);
        client.close();

        // Verify Signature
        byte[] b58key_primary = Base58.decode(this.public_key);
        String b58key_primary_hex = DatatypeConverter.printHexBinary(b58key_primary).toLowerCase();
        byte[] convert_primarykey = DatatypeConverter.parseHexBinary(b58key_primary_hex);

        Ed25519PublicKeyParameters primaryKeyVerify = new Ed25519PublicKeyParameters(convert_primarykey, 0);
        Signer verifier = new Ed25519Signer();
        verifier.init(false, primaryKeyVerify);
        verifier.update(convert_nonce, 0, convert_nonce.length);
        boolean verified = verifier.verifySignature(signature);

        System.out.println("Verify Signature: " + verified);
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
        System.out.println("Channel Address: " + this.channel_address);
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
        System.out.println(respons);

    }
}
