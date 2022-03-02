package net.gradbase.examples;

import java.util.ArrayList;

import org.json.JSONObject;

import net.gradbase.clients.IdentityClient;
import net.gradbase.models.types.*;

public class CreateIdentityAndCredential {

	public static void main(String args[]) throws Exception {
		IdentityClient client = new IdentityClient();

		// Recover the admin identity
		final String didId = Preliminary.authenticateRootIdentity(client);
		IdentityInternal admin = client.find(didId);

		if (admin == null) {
			throw new Exception("admin identity is null");
		}

		// Get admin identity's VC
		ArrayList<VerifiableCredential> vcs = (ArrayList<VerifiableCredential>) admin.getVerifiableCredentials();

		if (vcs.size() > 0) {
			VerifiableCredential firstCredential = vcs.get(0);
			System.out.println(firstCredential);
		} else {
			throw new Exception("admin identity has no credentials");
		}

		// Create identity for user
		JSONObject jsonClaim = new JSONObject().put("type", "Person").put("name", "randomName");

		Claim claim = new Claim(jsonClaim);

		final String randomUsername = Utils.getRandomUsernameOfLength(5);
		System.out.println("username: " + randomUsername);
		JSONObject newUserIdentity = client.create(randomUsername, claim);
		System.out.println("created new user " + newUserIdentity);

		VerifiableCredential assignedCredential = client.createCredential(vcs.get(0),
				newUserIdentity.getJSONObject("doc").getString("id"), CredentialType.BASIC_IDENTITY,
				new Claim(new JSONObject().put("type", "Person").put("position", "Professor")));

		System.out.println("Created credential for new user " + assignedCredential.toString());

		boolean verified = client.checkCredential(assignedCredential);
		System.out.println("Verification result: " + verified);

	}

}
