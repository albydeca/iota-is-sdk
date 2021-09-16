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
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class DID {
    private final String api_key = "api-key=94F5BA49-12B6-4E45-A487-BF91C442276D";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";

    public String[] createDID(String who) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/identities/create?" + api_key);

        String json = new JSONObject()
                .put("username", "test")
                .put("claim", new JSONObject().put("type", "Device").put("category", new JSONArray().put("sensor"))
                                                                    .put("controlledProperty", new JSONArray().put("fillingLevel").put("temperature"))
                                                                    .put("controlledAsset", new JSONArray().put("wastecontainer-Osuna-100"))
                                                                    .put("ipAddress", new JSONArray().put("192.14.56.78"))
                                                                    .put("mcc", "214")
                                                                    .put("mnc", "07")
                                                                    .put("serialNumber", "9845A")
                                                                    .put("refDeviceModel", "myDevice-wastecontainer-sensor-345")
                                                                    .put("dateFirstUsed", "2014-09-11T11:00:00Z")
                                                                    .put("owner", new JSONArray().put("did:iota:CtPnfQqSZBmZEe5A5iNZzJ6pkCqUxtsFsErNfA3CeHpY")))
                                                                    .toString();
        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        System.out.println("-------------------------- " + ANSI_GREEN +  who + ANSI_RESET + " --------------------------");
        System.out.println(ANSI_RED + "DID ID: " + ANSI_RESET +  respons.getJSONObject("doc").getString("id") + "\n" + ANSI_RED + "Private Key: " + ANSI_RESET + respons.getJSONObject("key").getString("secret") + "\n" + ANSI_RED + "Public Key: " + ANSI_RESET + respons.getJSONObject("key").getString("public"));
        client.close();
        String[] result = new String[] {respons.getJSONObject("key").getString("secret"), respons.getJSONObject("key").getString("public"), respons.getJSONObject("doc").getString("id")};
        return result;
    }

    public String createNonce(String did_id) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("https://ensuresec.solutions.iota.org/api/v0.1/authentication/prove-ownership/" + did_id + "?" + this.api_key);
        CloseableHttpResponse response = client.execute(httpGet);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        client.close();
        return respons.getString("nonce");
    }

    public String sigantureNonce(String private_key, String public_key, String nonce, String did_id) throws IOException, CryptoException {

        byte[] b58key = Base58.decode(private_key);    // Decode a base58 key and encode it as hex key
        String b58key_hex = DatatypeConverter.printHexBinary(b58key).toLowerCase();
        byte[] convert_key = DatatypeConverter.parseHexBinary(b58key_hex);

        String hash_nonce_hex = DigestUtils.sha256Hex(nonce); // Hash a nonce with SHA-256 (apache_commons)
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
        HttpPost httpPost = new HttpPost("https://ensuresec.solutions.iota.org/api/v0.1/authentication/prove-ownership/" + did_id + "?" + this.api_key);

        String json = new JSONObject()
                .put("signedNonce", sign)
                .toString();

        StringEntity entity = new StringEntity(json);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);

        JSONObject respons = new JSONObject(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8));
        client.close();

        // Verify Signature
        byte[] b58key_primary = Base58.decode(public_key);
        String b58key_primary_hex = DatatypeConverter.printHexBinary(b58key_primary).toLowerCase();
        byte[] convert_primarykey = DatatypeConverter.parseHexBinary(b58key_primary_hex);

        Ed25519PublicKeyParameters primaryKeyVerify = new Ed25519PublicKeyParameters(convert_primarykey, 0);
        Signer verifier = new Ed25519Signer();
        verifier.init(false, primaryKeyVerify);
        verifier.update(convert_nonce, 0, convert_nonce.length);
        boolean verified = verifier.verifySignature(signature);

        System.out.println("Verify Signature: " + verified);
        return respons.getString("jwt");
    }
}
