import com.github.javafaker.Faker;
import org.apache.commons.codec.digest.DigestUtils;
import org.bitcoinj.core.Base58;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class DID {


    public String[] createDID(String who) throws IOException {
        File myObj = new File(who + ".json");
        if(!myObj.exists()) {
            System.out.println("-------------------------- " + Utils.ANSI_GREEN +  who + Utils.ANSI_RESET + " --------------------------");
            System.out.println(who + " data does not exist!\nRequest in progress...");
            final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/identities/create?" + Utils.api_key;
            JSONObject json;

            Faker faker1 = new Faker();
            JSONObject jsonDevice = new JSONObject()
                    .put("username", faker1.name())
                    .put("claim", new JSONObject().put("type", "Device").put("category", new JSONArray().put("actuator"))
                            .put("controlledProperty", new JSONArray().put("fillingLevel").put("temperature"))
                            .put("firmwareVersion", "number")
                            .put("hardwareVersion", "number")
                            .put("ipAddress", new JSONArray().put("192.14.56.78"))
                            .put("serialNumber", "9845A")
                            .put("dateFirstUsed", "2014-09-11T11:00:00Z"));

            Faker faker2 = new Faker();
            JSONObject jsonOrganization = new JSONObject()
                    .put("username", faker2.name())
                    .put("claim", new JSONObject().put("type", "Organization").put("name", "randomName"))
                    .put("alternateName", "randomName")
                    .put("url", "www.random.com")
                    .put("address", faker2.address())
                    .put("email", "organization@test.com")
                    .put("faxNumber", "1234567890")
                    .put("telephone", "1234567890");

            if(who.equals("LogCreator")) {
                json = jsonOrganization;
            }
            else {
                json = jsonDevice;
            }


            JSONObject respons = Utils.sendIotaPostRequest(uri, json, null);
            System.out.println(Utils.ANSI_RED + "DID ID: " + Utils.ANSI_RESET +  respons.getJSONObject("doc").getString("id") + "\n" + Utils.ANSI_RED + "Private Key: " + Utils.ANSI_RESET + respons.getJSONObject("key").getString("secret") + "\n" + Utils.ANSI_RED + "Public Key: " + Utils.ANSI_RESET + respons.getJSONObject("key").getString("public"));
            String[] result = new String[] {respons.getJSONObject("key").getString("secret"), respons.getJSONObject("key").getString("public"), respons.getJSONObject("doc").getString("id")};

            JSONObject data = new JSONObject();
            data.put("PrivateKey", respons.getJSONObject("key").getString("secret"));
            data.put("PublicKey", respons.getJSONObject("key").getString("public"));
            data.put("ID", respons.getJSONObject("doc").getString("id"));

            FileWriter myWriter = new FileWriter(who + ".json");
            myWriter.write(data.toString());
            myWriter.close();
            System.out.println("Successfully wrote to the file json.");
            return result;
        }
        else {
            JSONParser jsonParser = new JSONParser();
            org.json.simple.JSONObject jsonObject = null;
            try {
                jsonObject = (org.json.simple.JSONObject) jsonParser.parse(new FileReader(who + ".json"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("-------------------------- " + Utils.ANSI_GREEN +  who + Utils.ANSI_RESET + " --------------------------");
            System.out.println(who + " data already exists!");
            System.out.println(Utils.ANSI_RED + "DID ID: " + Utils.ANSI_RESET +  (String) jsonObject.get("ID") + "\n" + Utils.ANSI_RED + "Private Key: " + Utils.ANSI_RESET + (String) jsonObject.get("PrivateKey") + "\n" + Utils.ANSI_RED + "Public Key: " + Utils.ANSI_RESET + (String) jsonObject.get("PublicKey"));
            return new String[] {(String) jsonObject.get("PrivateKey"), (String) jsonObject.get("PublicKey"), (String) jsonObject.get("ID")};
        }
    }

    public String createNonce(String did_id) throws IOException {
        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/authentication/prove-ownership/" + did_id + "?" + Utils.api_key;

        JSONObject respons = Utils.sendIOTAGetRequest(uri);
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

        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/authentication/prove-ownership/" + did_id + "?" + Utils.api_key;

        JSONObject json = new JSONObject()
                .put("signedNonce", sign);


        JSONObject response = Utils.sendIotaPostRequest(uri, json, null);

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
        return response.getString("jwt");
    }
}
