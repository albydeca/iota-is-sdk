package net.gradbase.models.types;

import org.json.JSONException;
import org.json.JSONObject;

public class SubscriptionInternal extends IOTAAPIDataItem {
	private SubscriptionType type;
	private String channelAddress;
	private String id;
	private String state;
	private String subscriptionLink;
	private boolean isAuthorized;
	private AccessRights accessRights;
	private String publicKey;
	private String keyloadLink;
	private String sequenceLink;
	private String pskId;

	public SubscriptionInternal(JSONObject source) {
		switch (source.getString("type")) {
		case "Author":
			this.type = SubscriptionType.AUTHOR;
		case "Subscriber":
			this.type = SubscriptionType.SUBSCRIBER;
		}

		this.channelAddress = source.getString("channelAddress");
		this.id = source.getString("id");
		this.state = source.getString("state");

		try {
			this.subscriptionLink = source.getString("subscriptionLink");
		} catch (JSONException ex) {
			this.subscriptionLink = null;
		}

		this.isAuthorized = source.getBoolean("isAuthorized");

		switch (source.getString("accessRights")) {
		case "Audit":
			this.accessRights = AccessRights.AUDIT;
		case "Read":
			this.accessRights = AccessRights.READ;
		case "Write":
			this.accessRights = AccessRights.WRITE;
		case "ReadAndWrite":
			this.accessRights = AccessRights.READ_AND_WRITE;
		}

		try {
			this.publicKey = source.getString("publicKey");
		} catch (JSONException ex) {
			this.publicKey = null;
		}

		try {
			this.keyloadLink = source.getString("keyloadLink");
		} catch (JSONException ex) {
			this.keyloadLink = null;
		}

		try {
			this.sequenceLink = source.getString("sequenceLink");
		} catch (JSONException ex) {
			this.sequenceLink = null;
		}

		try {
			this.pskId = source.getString("pskId");
		} catch (JSONException ex) {
			this.pskId = null;
		}
	}

	public SubscriptionInternal(JSONObject source, SubscriptionType type, String channelAddress, String id,
			String state, String subscriptionLink, boolean isAuthorized, AccessRights accessRights, String publicKey,
			String keyloadLink, String sequenceLink, String pskId) {
		super(source);
		this.type = type;
		this.channelAddress = channelAddress;
		this.id = id;
		this.state = state;
		this.subscriptionLink = subscriptionLink;
		this.isAuthorized = isAuthorized;
		this.accessRights = accessRights;
		this.publicKey = publicKey;
		this.keyloadLink = keyloadLink;
		this.sequenceLink = sequenceLink;
		this.pskId = pskId;
	}

	public SubscriptionType getType() {
		return type;
	}

	public String getChannelAddress() {
		return channelAddress;
	}

	public String getId() {
		return id;
	}

	public String getState() {
		return state;
	}

	public String getSubscriptionLink() {
		return subscriptionLink;
	}

	public boolean isAuthorized() {
		return isAuthorized;
	}

	public AccessRights getAccessRights() {
		return accessRights;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public String getKeyloadLink() {
		return keyloadLink;
	}

	public String getSequenceLink() {
		return sequenceLink;
	}

	public String getPskId() {
		return pskId;
	}

	@Override
	public String toString() {
		return "SubscriptionInternal [type=" + type + ", channelAddress=" + channelAddress + ", id=" + id + ", state="
				+ state + ", subscriptionLink=" + subscriptionLink + ", isAuthorized=" + isAuthorized
				+ ", accessRights=" + accessRights + ", publicKey=" + publicKey + ", keyloadLink=" + keyloadLink
				+ ", sequenceLink=" + sequenceLink + ", pskId=" + pskId + "]";
	}

	@Override
	public JSONObject toJson() {
		JSONObject result = new JSONObject().put("type", this.type.toString())
				.put("channelAddress", this.channelAddress).put("id", this.id).put("state", this.state)
				.put("isAuthorized", this.isAuthorized).put("accessRights", this.accessRights.toString());

		if (this.subscriptionLink != null) {
			result.put("subscriptionLink", this.subscriptionLink);
		}

		if (this.publicKey != null) {
			result.put("publicKey", this.publicKey);
		}

		if (this.keyloadLink != null) {
			result.put("keyloadLink", this.keyloadLink);
		}

		if (this.sequenceLink != null) {
			result.put("sequenceLink", this.sequenceLink);
		}

		if (this.pskId != null) {
			result.put("pskId", this.pskId);
		}

		return result;
	}

}
