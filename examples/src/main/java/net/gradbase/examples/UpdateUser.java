package net.gradbase.examples;

import java.util.List;

import org.json.JSONObject;
import net.gradbase.clients.IdentityClient;
import net.gradbase.models.types.*;

public class UpdateUser {

	public static void main(String args[]) throws Exception {
		IdentityClient client = new IdentityClient();

		Preliminary.authenticateRootIdentity(client);

		System.out.println("User authenticated");

		JSONObject jsonClaim = new JSONObject().put("type", "Person").put("name", "randomName");

		Claim claim = new Claim(jsonClaim);

		final String username = Utils.getRandomUsernameOfLength(5);
		System.out.println("username: " + username);
		JSONObject newUserIdentity = client.create(username, claim);
		System.out.println("created new user " + newUserIdentity);

		// Search for identities with username 'User' in it
		List<IdentityInternal> identities = client.search(null, username, null, null, null);

		System.out.println("Found the following identities:");
		for (int i = 0; i < identities.size(); i++) {
			System.out.println(identities.get(i));
		}

		if (identities.size() > 0) {
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
