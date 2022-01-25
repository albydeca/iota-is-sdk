package examples;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import clients.IdentityClient;
import models.types.*;

public class UpdateUser {
	
	public static void executeExample() throws Exception {
		IdentityClient client = new IdentityClient();

		// Recover the admin identity
		InputStream is = new FileInputStream("LogCreator.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        System.out.println(jsonTxt);
        JSONObject json = new JSONObject(jsonTxt);       
 
        
        final String didId = json.getString("ID");
		client.authenticate(didId, json.getString("PrivateKey"));
		
		System.out.println("User authenticated");
		
		// Search for identities with username 'User' in it
		List<IdentityInternal> identities = client.search(null, "randomUsername",
				null, null, null);
		
		System.out.println("Found the following identities:");
		for(int i = 0; i < identities.size(); i++) {
			System.out.println(identities.get(i));
		}
		
		if(identities.size() > 0) {
			// Take the first identities of the searched identities
			IdentityInternal userIdentity = identities.get(0);
			JSONObject userIdentityJson = userIdentity.toJson();
			userIdentityJson.put("username", "newUsernameWoohoo");
			
			IdentityInternal updatedUserIdentity = new IdentityInternal(userIdentityJson);
			// Update the claim of the identity with a new username
			client.update(updatedUserIdentity);
			System.out.println("Successfully updated identity with id: " + userIdentity.getId());
		} else {
			System.out.println("Could not find identities with the username randomUsername");
		}
	}

}
