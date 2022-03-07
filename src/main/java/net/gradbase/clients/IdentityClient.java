package net.gradbase.clients;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.HashMap;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.gradbase.exceptions.InvalidAPIResponseException;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

import net.gradbase.models.types.*;

public class IdentityClient extends BaseClient {

	public IdentityClient() throws FileNotFoundException, IOException {
		super();
	}

	/**
	 * Create a new decentralized digital identity (DID). Identity DID document is
	 * signed and published to the ledger (IOTA Tangle). A digital identity can
	 * represent an individual, an organization or an object. The privateAuthKey
	 * controlling the identity is returned. It is recommended to securely (encrypt)
	 * store the privateAuthKey locally, since it is not stored on the APIs Bridge.
	 * 
	 * @param username
	 * @param claim
	 * @throws InvalidAPIResponseException
	 *
	 */
	public JSONObject create(String username, Claim claim)
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "identities/create";
		JSONObject body = new JSONObject().put("username", username).put("claim", claim.toJson());
		return sendIOTAPostRequest(endpoint, body, false);

	}

	/**
	 * Search for identities in the system and returns a list of existing
	 * identities.
	 * 
	 * @param type
	 * @param username
	 * @param registrationDate
	 * @param limit
	 * @param index
	 * @throws InvalidAPIResponseException
	 * @throws ParseException
	 *
	 */
	public List<IdentityInternal> search(String type, String username, Date registrationDate, Integer limit,
			Integer index) throws ClientProtocolException, IOException, URISyntaxException, ParseException,
			InvalidAPIResponseException {
		String endpoint = "identities/search";

		Map<String, String> params = new HashMap<String, String>();

		if (type != null) {
			params.put("type", type);
		}

		if (username != null) {
			params.put("username", username);
		}

		if (registrationDate != null) {
			params.put("registration-date", prepareDateForGetParam(registrationDate));
		}

		if (limit != null) {
			params.put("limit", limit.toString());
		} else {
			params.put("limit", "5");
		}

		if (index != null) {
			params.put("index", index.toString());
		}

		JSONArray response = sendIOTAGetRequestArray(endpoint, params, true);
		List<IdentityInternal> result = new ArrayList<IdentityInternal>();

		for (int i = 0; i < response.length(); i++) {
			result.add(new IdentityInternal(response.getJSONObject(i)));
		}
		return result;
	}

	/**
	 * Get information (including attached credentials) about a specific identity
	 * using the identity-id (DID identifier).
	 * 
	 * @param id
	 * @throws InvalidAPIResponseException
	 * @throws ParseException
	 *
	 */
	public IdentityInternal find(String id) throws ClientProtocolException, IOException, URISyntaxException,
			ParseException, InvalidAPIResponseException {
		String endpoint = "identities/identity/" + id;

		final JSONObject response = sendIOTAGetRequest(endpoint, null, true);
		if (response == null) {
			return null;
		}
		return new IdentityInternal(response);
	}

	/**
	 * Register an existing identity into the Bridge. This can be used if the
	 * identity already exists or it was only created locally. Registering an
	 * identity in the Bridge makes it possible to search for it by using some of
	 * the identity attributes, i.e., the username.
	 * 
	 * @param identity
	 * @throws InvalidAPIResponseException
	 *
	 */
	public void add(IdentityInternal identity)
			throws ClientProtocolException, URISyntaxException, IOException, InvalidAPIResponseException {
		String endpoint = "identities/identity";
		sendIOTAPostRequest(endpoint, identity.toJson(), true);
	}

	/**
	 * Update claim of a registered identity.
	 * 
	 * @param identity
	 *
	 */
	public void update(IdentityInternal identity) throws ClientProtocolException, URISyntaxException, IOException {
		String endpoint = "identities/identity";
		sendIOTAPutRequestWithAuth(endpoint, identity.toJson());
	}

	/**
	 * Removes an identity from the Bridge. An identity can only delete itself and
	 * is not able to delete other identities. Administrators are able to remove
	 * other identities. The identity cannot be removed from the immutable IOTA
	 * Tangle but only at the Bridge. Also the identity credentials will remain and
	 * the identity is still able to interact with other bridges.
	 * 
	 * @param id
	 * @param revokeCredentials
	 * @throws InvalidAPIResponseException
	 */
	public void remove(String id, Boolean revokeCredentials)
			throws JSONException, ParseException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "identities/identity/" + id;

		Map<String, String> params = new HashMap<String, String>();
		params.put("revoke-credentials", revokeCredentials.toString());

		sendIOTADeleteRequestWithAuth(endpoint, params);
	}

	// VERIFICATION

	/**
	 * Get the latest version of an identity document (DID) from the IOTA Tangle.
	 * 
	 * @param id
	 * @throws InvalidAPIResponseException
	 * @throws ParseException
	 *
	 */
	public JSONObject latestDocument(String id) throws ClientProtocolException, IOException, URISyntaxException,
			ParseException, InvalidAPIResponseException {
		String endpoint = "verification/latest-document/" + id;
		return sendIOTAGetRequest(endpoint, null, false);
	}

	/**
	 * Adds Trusted Root identity identifiers (DIDs). Trusted roots are DIDs of
	 * identities which are trusted by the Bridge. This identity DIDs can be DIDs of
	 * other organizations. By adding them to the list Trusted Roots their
	 * Verifiable Credentials (VCs) are automatically trusted when checking at the
	 * Bridge.
	 * 
	 * @param trustedRootId
	 * @throws InvalidAPIResponseException
	 *
	 */
	public void addTrustedAuthority(String trustedRootId)
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "verification/trusted-roots";
		JSONObject body = new JSONObject().put("trustedRootId", trustedRootId);
		sendIOTAPostRequest(endpoint, body, true);
	}

	/**
	 * Returns a list of Trusted Root identity identifiers (DIDs). Trusted roots are
	 * DIDs of identities which are trusted by the Bridge. This identity DIDs can be
	 * DIDs of other organizations. By adding them to the list Trusted Roots their
	 * Verifiable Credentials (VCs) are automatically trusted when checking at the
	 * Bridge.
	 * 
	 * @throws InvalidAPIResponseException
	 * @throws ParseException
	 *
	 */
	public List<String> getTrustedAuthorities() throws ClientProtocolException, IOException, URISyntaxException,
			ParseException, InvalidAPIResponseException {
		String endpoint = "verification/trusted-roots";

		JSONObject response = sendIOTAGetRequest(endpoint, null, false);
		JSONArray roots = response.getJSONArray("trustedRoots");
		List<String> result = new ArrayList<String>();
		for (int i = 0; i < roots.length(); i++) {
			result.add(roots.getString(i));
		}

		return result;
	}

	/**
	 * Remove Trusted Root identity identifiers (DIDs). Trusted roots are DIDs of
	 * identities which are trusted by the Bridge. This identity DIDs can be DIDs of
	 * other organizations. By adding them to the list Trusted Roots their
	 * Verifiable Credentials (VCs) are automatically trusted when checking at the
	 * Bridge.
	 * 
	 * @param trustedId
	 * @throws InvalidAPIResponseException
	 *
	 */
	public void removeTrustedAuthority(String trustedId)
			throws ParseException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "verification/trusted-roots/" + trustedId;
		sendIOTADeleteRequestWithAuth(endpoint, null);

	}

	/**
	 * Verify the authenticity of an identity (of an individual, organization or
	 * object) and issue a credential stating the identity verification status. Only
	 * previously verified identities (based on a network of trust) with assigned
	 * privileges can verify other identities. Having a verified identity provides
	 * the opportunity for other identities to identify and verify a the entity they
	 * interact to.
	 * 
	 * @param initiator
	 * @param targetDid
	 * @param credType
	 * @param claim
	 * @throws InvalidAPIResponseException
	 *
	 */
	public VerifiableCredential createCredential(VerifiableCredential initiator, String targetDid,
			CredentialType credType, Claim claim)
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "verification/create-credential";

		JSONObject subjectBody = new JSONObject().put("id", targetDid).put("credentialType", credType.toString())
				.put("claim", claim.toJson());

		JSONObject body = new JSONObject().put("subject", subjectBody);

		if (initiator != null) {
			body.put("initiatorVC", initiator.toJson());
		}

		System.out.println(body);

		JSONObject response = sendIOTAPostRequest(endpoint, body, true);
		if (response == null) {
			return null;
		}
		return new VerifiableCredential(response);
	}

	/**
	 * Check the verifiable credential of an identity. Validates the signed
	 * verifiable credential against the Issuer information stored onto the IOTA
	 * Tangle and checks if the issuer identity (DID) contained in the credential is
	 * from a trusted root.
	 * 
	 * @param credential
	 * @throws InvalidAPIResponseException
	 *
	 */
	public boolean checkCredential(VerifiableCredential credential)
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "verification/check-credential";

		JSONObject response = sendIOTAPostRequest(endpoint, credential.toJson(), false);
		return response.getBoolean("isVerified");
	}

	/**
	 * Revoke one specific verifiable credential of an identity. In the case of
	 * individual and organization identities the reason could be that the user has
	 * left the organization. Only organization admins (with verified identities) or
	 * the identity owner itself can do that.
	 * 
	 * @param signatureValue
	 * @throws InvalidAPIResponseException
	 *
	 */
	public void revokeCredential(String signatureValue)
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "verification/revoke-credential";

		JSONObject body = new JSONObject().put("signatureValue", signatureValue);
		sendIOTAPostRequest(endpoint, body, true);
	}
}
