package examples;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import clients.IdentityClient;
import models.types.*;

public class Preliminary {
	public void doPrelims() throws Exception {
		IdentityClient client = new IdentityClient();
		
		InputStream is = new FileInputStream("LogCreator.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        System.out.println(jsonTxt);
        JSONObject json = new JSONObject(jsonTxt);  
        
		final String didId = json.getString("ID");
		client.authenticate(didId, json.getString("PrivateKey"));
		
		VerifiableCredential vc = client.createCredential(null, didId,
				CredentialType.VERIFIED_IDENTITY, new Claim(new JSONObject()));
		System.out.println("created Root Identity");
	}
}
