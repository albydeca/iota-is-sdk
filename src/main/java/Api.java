import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.json.JSONObject;


import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;


public class Api {
    private final String api_key = "api-key=94F5BA49-12B6-4E45-A487-BF91C442276D";
    private String private_key = null;
    private String public_key = null;
    private String did_id = null;
    private String nonce = null;

    public void createDID() throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/identities/create?" + api_key);

        String json_in = "{\n" +
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

    public void sigantureNonce() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        String testOutput = null;

        Base58 b58 = new Base58();
        //testOutput = new String(b58.decode(this.nonce), StandardCharsets.UTF_8);
        //testOutput = String.format("%040x", new BigInteger(1, arg.getBytes(b58.decode(this.nonce))));
        String hexnonceb58 = Hex.encodeHexString(b58.decode(this.nonce));
        System.out.println(hexnonceb58);
        String sha256nonce = DigestUtils.sha256Hex(hexnonceb58);
        String hexsha256nonce = Hex.encodeHexString(sha256nonce.getBytes(StandardCharsets.UTF_8));
        System.out.println(hexsha256nonce);

        KeyPairGenerator kpg = KeyPairGenerator.getInstance("Ed25519");
        KeyPair kp = kpg.generateKeyPair();

        byte[] msg = hexsha256nonce.getBytes(StandardCharsets.UTF_8);

        Signer signer = new Ed25519Signer();



    }

}
