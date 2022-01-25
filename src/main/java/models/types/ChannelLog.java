package models.types;

import org.json.JSONObject;
import org.json.JSONException;

public class ChannelLog extends IOTAAPIDataItem {
	private String type;
	private String created;
	private String metadata; // is 'Any' on Node version
	private Object payload; // is 'Any' on Node version
	private String publicPayload; // is 'Any' on Node version
	
	public ChannelLog(String type, String created, String metadata, String payload, String publicPayload) {
		this.type = type;
		this.created = created;
		this.metadata = metadata;
		this.payload = payload;
		this.publicPayload = publicPayload;
	}
	
	public ChannelLog(JSONObject source) {
		
		try {
			this.type = source.getString("type");
		} catch(JSONException ex) {
			this.type = null;
		}
		
		try {
			this.created = source.getString("created");
		} catch(JSONException ex) {
			this.created = null;
		}
		
		try {
			this.metadata = source.getString("metadata");
		} catch(JSONException ex) {
			this.metadata = null;
		}

		try {
			this.publicPayload = source.getString("publicPayload");
		} catch(JSONException ex) {
			this.publicPayload = null;
		}
		
		try {
			this.payload = source.get("payload");
		} catch(JSONException ex) {
			this.payload = null;
		}

	}

	public String getType() {
		return type;
	}

	public String getCreated() {
		return created;
	}

	public String getMetadata() {
		return metadata;
	}

	public Object getPayload() {
		return payload;
	}
	
	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public String getPublicPayload() {
		return publicPayload;
	}

	@Override
	public String toString() {
		return "ChannelLog [type=" + type + ", created=" + created + ", metadata=" + metadata + ", payload=" + payload
				+ ", publicPayload=" + publicPayload + "]";
	}
	
	@Override
	public JSONObject toJson() {
		JSONObject result = new JSONObject();
		
		if(this.type != null) {
			result.put("type", this.type);
		}
		
		if(this.created != null) {
			result.put("created", this.created);
		}
		
		if(this.metadata != null) {
			result.put("metadata", this.metadata);
		}
		
		if(this.payload != null) {
			result.put("payload", this.payload);
		}
		
		if(this.publicPayload != null) {
			result.put("publicPayload", this.publicPayload);
		}
		return result;
	}
}
