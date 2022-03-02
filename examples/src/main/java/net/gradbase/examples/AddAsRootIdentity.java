package net.gradbase.examples;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.crypto.CryptoException;
import org.json.JSONObject;

import net.gradbase.clients.IdentityClient;
import net.gradbase.exceptions.InvalidAPIResponseException;
import net.gradbase.models.types.*;

public class Preliminary {

	public static void main(String args[]) throws Exception {
		IdentityClient client = new IdentityClient();

		final String didId = authenticateRootIdentity(client);

		client.createCredential(null, didId, CredentialType.VERIFIED_IDENTITY, new Claim(UserType.SERVICE));
		System.out.println("created Root Identity");

	}

	public static String authenticateRootIdentity(IdentityClient client) throws IOException, FileNotFoundException,
			CryptoException, URISyntaxException, InvalidAPIResponseException {
		Properties appProps = new Properties();
		appProps.load(new FileInputStream("env.properties"));
		InputStream is = new FileInputStream(appProps.getProperty("identity-file"));
		String jsonTxt = IOUtils.toString(is, "UTF-8");
		JSONObject json = new JSONObject(jsonTxt);
		final String didId = json.getJSONObject("doc").getString("id");

		client.authenticate(didId, json.getJSONObject("key").getString("public"),
				json.getJSONObject("key").getString("secret"));
		return didId;
	}

}
