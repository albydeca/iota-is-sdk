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
            System.out.println("-------------------------- " +
            		Utils.ANSI_GREEN +  who + Utils.ANSI_RESET +
            		" --------------------------");
            System.out.println(who + " data does not exist!\n"
            		+ "Request in progress...");
            final String uri = Utils.iotastreamsApiBaseUrl+
            		"identities/create?" + Utils.apiKey;
            JSONObject json;

            Faker faker1 = new Faker();
            JSONObject jsonDevice = new JSONObject()
                    .put("username", faker1.name())
                    .put("claim", new JSONObject().put("type", "Device")
                    		.put("category", new JSONArray().put("actuator"))
                            .put("controlledProperty", new JSONArray()
                            		.put("fillingLevel").put("temperature"))
                            .put("firmwareVersion", "number")
                            .put("hardwareVersion", "number")
                            .put("ipAddress", new JSONArray()
                            		.put("192.14.56.78"))
                            .put("serialNumber", "9845A")
                            .put("dateFirstUsed", "2014-09-11T11:00:00Z"));

            Faker faker2 = new Faker();
            JSONObject jsonOrganization = new JSONObject()
                    .put("username", faker2.name())
                    .put("claim", new JSONObject().put("type", "Organization")
                    		.put("name", "randomName"))
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


            JSONObject jsonResponse = Utils.sendIotaPostRequest(uri, json, null);
            System.out.println(Utils.ANSI_RED + "DID ID: " + Utils.ANSI_RESET +
            		jsonResponse.getJSONObject("doc").getString("id") + "\n" +
            		Utils.ANSI_RED + "Private Key: " + Utils.ANSI_RESET + 
            		jsonResponse.getJSONObject("key").getString("secret") +
            		"\n" + Utils.ANSI_RED + "Public Key: " + Utils.ANSI_RESET +
            		jsonResponse.getJSONObject("key").getString("public"));
           
            String[] result = new String[] {jsonResponse.getJSONObject("key")
            		.getString("secret"), jsonResponse.getJSONObject("key")
            		.getString("public"), jsonResponse.getJSONObject("doc")
            		.getString("id")};

            JSONObject data = new JSONObject();
            data.put("PrivateKey", jsonResponse.getJSONObject("key")
            		.getString("secret"));
            data.put("PublicKey", jsonResponse.getJSONObject("key")
            		.getString("public"));
            data.put("ID", jsonResponse.getJSONObject("doc")
            		.getString("id"));

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
                jsonObject = (org.json.simple.JSONObject) jsonParser.parse(
                		new FileReader(who + ".json"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("-------------------------- " + 
            		Utils.ANSI_GREEN +  who +
            		Utils.ANSI_RESET + " --------------------------");
            System.out.println(who + " data already exists!");
            
            System.out.println(Utils.ANSI_RED + "DID ID: " + Utils.ANSI_RESET +
            		(String) jsonObject.get("ID") + "\n" + Utils.ANSI_RED +
            		"Private Key: " + Utils.ANSI_RESET + 
            		(String) jsonObject.get("PrivateKey") + "\n" +
            		Utils.ANSI_RED + "Public Key: " + Utils.ANSI_RESET + 
            		(String) jsonObject.get("PublicKey"));
            
            return new String[] {(String) jsonObject.get("PrivateKey"),
            		(String) jsonObject.get("PublicKey"),
            		(String) jsonObject.get("ID")};
        }
    }

    public String createNonce(String didId) throws IOException {
        final String uri = Utils.iotastreamsApiBaseUrl +
        		"authentication/prove-ownership/" + didId + "?" +
        		Utils.apiKey;

        JSONObject response = Utils.sendIOTAGetRequest(uri);
        System.out.println(System.getProperty("api-key"));
        return response.getString("nonce");
    }

    public String signNonce
    	(String privateKey, String publicKey, String nonce, String didId) 
    			throws IOException, CryptoException {

        byte[] b58key = Base58.decode(privateKey);    // Decode a base58 key and encode it as hex key
        String b58keyHex = DatatypeConverter.printHexBinary(b58key)
        		.toLowerCase();
        byte[] convertKey = DatatypeConverter.parseHexBinary(b58keyHex);

        String hashNonceHex = DigestUtils.sha256Hex(nonce); // Hash a nonce with SHA-256 (apache_commons)
        byte[] convertNonce = DatatypeConverter.parseHexBinary(hashNonceHex);


        //https://stackoverflow.com/questions/53921655/rebuild-of-ed25519-keys-with-bouncy-castle-java
        Ed25519PrivateKeyParameters privateKeyParams = 
        		new Ed25519PrivateKeyParameters(convertKey, 0);  // Encode in PrivateKey
        Signer signer = new Ed25519Signer();    // Sign a nonce using the private key
        signer.init(true, privateKeyParams);
        signer.update(convertNonce, 0, convertNonce.length);
        byte[] signature = signer.generateSignature();

        //https://stackoverflow.com/questions/6625776/java-security-invalidkeyexception-key-length-not-128-192-256-bits
        String sign = DatatypeConverter.printHexBinary(signature).toLowerCase();

        final String uri = Utils.iotastreamsApiBaseUrl +
        		"authentication/prove-ownership/" + didId + "?" + 
        		Utils.apiKey;

        JSONObject json = new JSONObject()
                .put("signedNonce", sign);


        JSONObject response = Utils.sendIotaPostRequest(uri, json, null);

        // Verify Signature
        byte[] b58keyPrimary = Base58.decode(publicKey);
        String b58keyPrimaryHex = DatatypeConverter.
        		printHexBinary(b58keyPrimary).toLowerCase();
        byte[] convert_primarykey = DatatypeConverter.
        		parseHexBinary(b58keyPrimaryHex);

        Ed25519PublicKeyParameters primaryKeyVerify = 
        		new Ed25519PublicKeyParameters(convert_primarykey, 0);
        Signer verifier = new Ed25519Signer();
        verifier.init(false, primaryKeyVerify);
        verifier.update(convertNonce, 0, convertNonce.length);
        boolean verified = verifier.verifySignature(signature);

        System.out.println("Verify Signature: " + verified);
        return response.getString("jwt");
    }
}
