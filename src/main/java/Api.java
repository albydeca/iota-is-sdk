import com.google.common.hash.Hashing;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
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
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.json.JSONObject;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

import static org.bouncycastle.jcajce.spec.EdDSAParameterSpec.Ed25519;


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

    public void sigantureNonce() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, IOException, CryptoException {

        String prikey = Utils.toHex(Base64.getEncoder().encodeToString(Base58.decode(this.private_key)));    // Decode a base58 key and encode it as hex key

        String sha256hex = Hashing.sha256()     // Hash a nonce with SHA-256 (guava)
                .hashString(this.nonce, StandardCharsets.UTF_8)
                .toString();
        String prova1 = Utils.toHex(sha256hex);

        //https://stackoverflow.com/questions/53921655/rebuild-of-ed25519-keys-with-bouncy-castle-java
        Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(prikey.getBytes(StandardCharsets.UTF_8), 0);  // Encode in PrivateKey
        Signer signer = new Ed25519Signer();    // Sign a nonce using the private key
        signer.init(true, privateKey);
        signer.update(prova1.getBytes(StandardCharsets.UTF_8), 0, prova1.length());
        byte[] signature = signer.generateSignature();

        String prova = Utils.asHex(signature);
        System.out.println(prova + " lunghezza: " + prova.length());

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/authentication/prove-ownership/" + this.did_id + "?" + this.api_key);
        String json_in = "{\n" +
                "  \"signedNonce\": \"" + prova + "\"\n" +
                "}";

        StringEntity entity = new StringEntity(json_in);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        System.out.println(respons);
        client.close();

    }

}
