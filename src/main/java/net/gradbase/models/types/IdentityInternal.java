package net.gradbase.models.types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class IdentityInternal extends IOTAAPIDataItem {

	private String id;
	private String publicKey;
	private String username;
	private String registrationDate;
	private List<VerifiableCredential> verifiableCredentials;
	private String role;
	private UserType claim;
	private Boolean isPrivate;
	private Boolean isServerIdentity;

	@Override
	public String toString() {
		return "IdentityInternal [id=" + id + ", publicKey=" + publicKey + ", username=" + username
				+ ", registrationDate=" + registrationDate + ", verifiableCredentials=" + verifiableCredentials
				+ ", role=" + role + ", claim=" + claim + ", isPrivate=" + isPrivate + ", isServerIdentity="
				+ isServerIdentity + "]";
	}

	public IdentityInternal(JSONObject source) {
		this.id = source.getString("id");
		this.publicKey = source.getString("publicKey");

		try {
			this.username = source.getString("username");
		} catch (JSONException ex) {
			this.username = null;
		}

		try {
			this.registrationDate = source.getString("registrationDate");
		} catch (JSONException ex) {
			this.registrationDate = null;
		}

		try {
			this.verifiableCredentials = new ArrayList<VerifiableCredential>();
			JSONArray creds = source.getJSONArray("verifiableCredentials");
			for (int i = 0; i < creds.length(); i++) {
				JSONObject o = creds.getJSONObject(i);
				VerifiableCredential ct = new VerifiableCredential(o);
				this.verifiableCredentials.add(ct);
			}

		} catch (JSONException ex) {
			this.verifiableCredentials = null;
		}

		try {
			this.role = source.getString("role");
		} catch (JSONException ex) {
			this.role = null;
		}

		try {
			switch (source.getJSONObject("claim").getString("type")) {
			case "Person":
				this.claim = UserType.PERSON;
			case "Service":
				this.claim = UserType.SERVICE;
			case "Organization":
				this.claim = UserType.ORG;
			case "Device":
				this.claim = UserType.DEVICE;
			case "Product":
				this.claim = UserType.PRODUCT;
			default:
				this.claim = UserType.OTHER;

			}
		} catch (JSONException ex) {
			this.claim = null;
		}

		try {
			this.isPrivate = source.getBoolean("isPrivate");
		} catch (JSONException ex) {
			this.isPrivate = null;
		}

		try {
			this.isServerIdentity = source.getBoolean("isServerIdentity");
		} catch (JSONException ex) {
			this.isServerIdentity = null;
		}
	}

	public IdentityInternal(JSONObject source, String id, String publicKey, String username, String registrationDate,
			List<VerifiableCredential> verifiableCredentials, String role, UserType claim, Boolean isPrivate,
			Boolean isServerIdentity) {
		super(source);
		this.id = id;
		this.publicKey = publicKey;
		this.username = username;
		this.registrationDate = registrationDate;
		this.verifiableCredentials = verifiableCredentials;
		this.role = role;
		this.claim = claim;
		this.isPrivate = isPrivate;
		this.isServerIdentity = isServerIdentity;
	}

	public String getId() {
		return id;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getUsername() {
		return username;
	}

	public String getRegistrationDate() {
		return registrationDate;
	}

	public List<VerifiableCredential> getVerifiableCredentials() {
		return verifiableCredentials;
	}

	public String getRole() {
		return role;
	}

	public UserType getClaim() {
		return claim;
	}

	public Boolean getIsPrivate() {
		return isPrivate;
	}

	public Boolean getIsServerIdentity() {
		return isServerIdentity;
	}

	@Override
	public JSONObject toJson() {
		JSONObject result = new JSONObject().put("id", this.id).put("publicKey", this.publicKey);

		if (this.username != null) {
			result.put("username", this.username);
		}

		if (this.registrationDate != null) {
			result.put("registrationDate", this.registrationDate);
		}

		if (this.verifiableCredentials != null) {
			JSONArray creds = new JSONArray();
			for (VerifiableCredential ct : this.verifiableCredentials) {
				creds.put(ct.toJson());
			}
			result.put("verifiableCredentials", creds);
		}

		if (this.role != null) {
			result.put("role", this.role);
		}

		if (this.claim != null) {
			result.put("claim", new JSONObject().put("type", this.claim));
		}

		if (this.isPrivate != null) {
			result.put("isPrivate", this.isPrivate);
		}

		if (this.isServerIdentity != null) {
			result.put("isServerIdentity", this.isServerIdentity);
		}

		return result;
	}
}
