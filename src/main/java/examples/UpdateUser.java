package examples;

import org.json.JSONObject;
import clients.IdentityClient;
import models.types.*;

public class UpdateUser {
	
	public static void executeExample() throws Exception {
		IdentityClient client = new IdentityClient();

		Preliminary.authenticateRootIdentity(client);
		
		System.out.println("User authenticated");
		
		// Search for identities with username 'User' in it
		List<IdentityInternal> identities = client.search(null, "User",
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
