package clients;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
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
import org.json.JSONException;
import org.json.JSONObject;
import javax.xml.bind.DatatypeConverter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.Map;
import java.util.Date;

import exceptions.InvalidAPIResponseException;

/**
 * This is the base client used as a parent class for all clients using the
 * integration services api.
 */

public class BaseClient {

	private String apiKey;
	private String baseUrl;
	private String apiVersion;
	private String jwt;

	public BaseClient() throws FileNotFoundException, IOException {
		Properties appProps = new Properties();
		appProps.load(new FileInputStream("env.properties"));
		this.apiVersion = appProps.getProperty("api-version");
		this.apiKey = appProps.getProperty("api-key");
		this.baseUrl = appProps.getProperty("api-url") + apiVersion + "/";
		this.jwt = null;
	}

	public BaseClient(String apiKey, String apiUrl, String apiVersion) {
		this.apiKey = apiKey;
		this.apiVersion = apiVersion;
		this.baseUrl = apiUrl + apiVersion + "/";
		this.jwt = null;
	}

	private HttpEntity sendPostRequest(String endpoint, Object body, CloseableHttpClient client, boolean needsBearer)
			throws URISyntaxException, ClientProtocolException, IOException, InvalidAPIResponseException {
		URIBuilder builder = new URIBuilder(this.baseUrl + endpoint);
		builder.setParameter("api-key", this.apiKey);

		final URI urlFinal = builder.build();
		System.out.println("POST " + urlFinal.toString());
		HttpPost httpPost = new HttpPost(urlFinal);

		StringEntity entity = new StringEntity(body.toString());
		httpPost.setEntity(entity);
		httpPost.setHeader(HttpHeaders.ACCEPT, "application/json");
		if (needsBearer && this.jwt != null) {
			httpPost.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
		}
		httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

		CloseableHttpResponse response = client.execute(httpPost);
		int statusCode = response.getStatusLine().getStatusCode();

		final HttpEntity response_body = response.getEntity();
		if (statusCode != 200 && statusCode != 201) {
			throw new InvalidAPIResponseException(
					statusCode + EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		}
		return response_body;
	}

	private HttpEntity sendGetRequest(String endpoint, Map<String, String> params, boolean needsBearer,
			String presharedKey, CloseableHttpClient client) throws URISyntaxException, ClientProtocolException,
			IOException, ParseException, InvalidAPIResponseException {
		URIBuilder builder = new URIBuilder(this.baseUrl + endpoint);

		if (params != null) {
			for (Map.Entry<String, String> e : params.entrySet()) {
				builder.setParameter(e.getKey(), e.getValue());
			}
		}
		if (presharedKey != null) {
			builder.setParameter("preshared-key", presharedKey);
		}

		builder.setParameter("api-key", this.apiKey);
		final URI urlFinal = builder.build();
		System.out.println("GET " + urlFinal.toString());
		HttpGet httpGet = new HttpGet(urlFinal);

		httpGet.setHeader(HttpHeaders.ACCEPT, "application/json");
		if (needsBearer && this.jwt != null) {
			httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
		}

		CloseableHttpResponse response = client.execute(httpGet);
		final HttpEntity response_body = response.getEntity();
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			throw new InvalidAPIResponseException(
					statusCode + EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		}
		return response_body;
	}

//	
	public JSONObject sendIOTAPostRequest(String endpoint, JSONObject body, boolean withAuth)
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		CloseableHttpClient client = HttpClients.createDefault();
		final HttpEntity response_body = sendPostRequest(endpoint, body, client, withAuth);
		if (response_body == null) {
			return null;
		}

		try {
			JSONObject result = new JSONObject(EntityUtils.toString(response_body, StandardCharsets.UTF_8));
			return result;

		} catch (JSONException ex) {
			return new JSONObject();
		} finally {
			client.close();
		}

	}

	public JSONArray sendIOTAPostRequestArray(String endpoint, JSONArray body, boolean withAuth)
			throws URISyntaxException, ClientProtocolException, IOException, InvalidAPIResponseException {
		CloseableHttpClient client = HttpClients.createDefault();
		final HttpEntity response_body = sendPostRequest(endpoint, body, client, withAuth);

		JSONArray result = new JSONArray(EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		client.close();
		return result;
	}

	public JSONObject sendIOTAGetRequest(String endpoint, Map<String, String> params, boolean withAuth)
			throws ClientProtocolException, IOException, URISyntaxException, ParseException,
			InvalidAPIResponseException {
		CloseableHttpClient client = HttpClients.createDefault();
		final HttpEntity response_body = sendGetRequest(endpoint, params, withAuth, null, client);
		if (response_body == null) {
			return null;
		}

		JSONObject result = new JSONObject(EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		client.close();
		return result;
	}

	public JSONArray sendIOTAGetRequestArray(String endpoint, Map<String, String> params, boolean withAuth)
			throws ClientProtocolException, IOException, URISyntaxException, ParseException,
			InvalidAPIResponseException {
		CloseableHttpClient client = HttpClients.createDefault();
		final HttpEntity response_body = sendGetRequest(endpoint, params, withAuth, null, client);

		JSONArray result = new JSONArray(EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		client.close();
		return result;
	}

	public JSONArray sendIOTAGetRequestWithPresharedKey(String endpoint, String presharedKey,
			Map<String, String> params) throws URISyntaxException, ClientProtocolException, IOException, ParseException,
			InvalidAPIResponseException {
		CloseableHttpClient client = HttpClients.createDefault();
		final HttpEntity response_body = sendGetRequest(endpoint, params, false, presharedKey, client);

		JSONArray result = new JSONArray(EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		client.close();
		return result;
	}

	public JSONObject sendIOTAPutRequestWithAuth(String endpoint, JSONObject body)
			throws URISyntaxException, ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder(this.baseUrl + endpoint);
		builder.setParameter("api-key", this.apiKey);
		HttpPut httpPut = new HttpPut(builder.build());

		StringEntity entity = new StringEntity(body.toString());
		httpPut.setEntity(entity);
		httpPut.setHeader(HttpHeaders.ACCEPT, "application/json");
		if (this.jwt != null) {
			httpPut.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);
		}
		httpPut.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

		CloseableHttpResponse response = client.execute(httpPut);

		final HttpEntity response_body = response.getEntity();
		if (response_body == null) {
			return null;
		}

		JSONObject result = new JSONObject(EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		client.close();
		return result;

	}

	public void sendIOTADeleteRequestWithAuth(String endpoint, Map<String, String> params)
			throws org.apache.http.ParseException, IOException, URISyntaxException, InvalidAPIResponseException {
		CloseableHttpClient client = HttpClients.createDefault();
		URIBuilder builder = new URIBuilder(this.baseUrl + endpoint);

		if (params != null) {
			for (Map.Entry<String, String> e : params.entrySet()) {
				builder.setParameter(e.getKey(), e.getValue());
			}
		}

		builder.setParameter("api-key", this.apiKey);

		HttpDelete httpDelete = new HttpDelete(builder.build());

		httpDelete.setHeader(HttpHeaders.ACCEPT, "application/json");
		httpDelete.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.jwt);

		CloseableHttpResponse response = client.execute(httpDelete);

		final HttpEntity response_body = response.getEntity();

		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200 && statusCode != 201) {
			throw new InvalidAPIResponseException(
					statusCode + EntityUtils.toString(response_body, StandardCharsets.UTF_8));
		}
		client.close();
	}

	public void authenticate(String didId, String publicKey, String privateKeyB58)
			throws IOException, CryptoException, URISyntaxException, ParseException, InvalidAPIResponseException {
		String nonce = createNonce(didId);
		signNonce(privateKeyB58, publicKey, nonce, didId);
	}

	private String createNonce(String didId)
			throws IOException, URISyntaxException, ParseException, InvalidAPIResponseException {
		final String endpoint = "authentication/prove-ownership/" + didId;

		JSONObject response = sendIOTAGetRequest(endpoint, null, false);
		return response.getString("nonce");
	}

	public void signNonce(String privateKey, String publicKey, String nonce, String didId)
			throws IOException, CryptoException, URISyntaxException, InvalidAPIResponseException {

		byte[] b58key = Base58.decode(privateKey); // Decode a base58 key and encode it as hex key
		String b58keyHex = DatatypeConverter.printHexBinary(b58key).toLowerCase();
		byte[] convertKey = DatatypeConverter.parseHexBinary(b58keyHex);

		String hashNonceHex = DigestUtils.sha256Hex(nonce); // Hash a nonce with SHA-256 (apache_commons)
		byte[] convertNonce = DatatypeConverter.parseHexBinary(hashNonceHex);

		// https://stackoverflow.com/questions/53921655/rebuild-of-ed25519-keys-with-bouncy-castle-java
		Ed25519PrivateKeyParameters privateKeyParams = new Ed25519PrivateKeyParameters(convertKey, 0); // Encode in
																										// PrivateKey
		Signer signer = new Ed25519Signer(); // Sign a nonce using the private key
		signer.init(true, privateKeyParams);
		signer.update(convertNonce, 0, convertNonce.length);
		byte[] signature = signer.generateSignature();

		// https://stackoverflow.com/questions/6625776/java-security-invalidkeyexception-key-length-not-128-192-256-bits
		String sign = DatatypeConverter.printHexBinary(signature).toLowerCase();

		final String endpoint = "authentication/prove-ownership/" + didId;

		JSONObject json = new JSONObject().put("signedNonce", sign);

		JSONObject response = sendIOTAPostRequest(endpoint, json, false);
		byte[] b58keyPrimary = Base58.decode(publicKey);
		String b58keyPrimaryHex = DatatypeConverter.printHexBinary(b58keyPrimary).toLowerCase();
		byte[] convert_primarykey = DatatypeConverter.parseHexBinary(b58keyPrimaryHex);

		Ed25519PublicKeyParameters primaryKeyVerify = new Ed25519PublicKeyParameters(convert_primarykey, 0);
		Signer verifier = new Ed25519Signer();
		verifier.init(false, primaryKeyVerify);
		verifier.update(convertNonce, 0, convertNonce.length);
		boolean verified = verifier.verifySignature(signature);

		System.out.println("Verify Signature: " + verified);
		this.jwt = response.getString("jwt");

	}

	protected static String prepareDateForGetParam(Date date) {
		String formattedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);
		return formattedDate.replaceAll(":", "%3A").replaceAll("\\+", "%2B");
	}

}
