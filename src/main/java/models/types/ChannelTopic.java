package models.types;

import org.json.JSONObject;

public class ChannelTopic extends IOTAAPIDataItem {
	private String type;
	private String source;

	public ChannelTopic(String type, String source) {
		this.type = type;
		this.source = source;
	}

	public ChannelTopic(JSONObject source) {
		this.type = source.getString("type");
		this.source = source.getString("source");
	}

	public String getType() {
		return type;
	}

	public String getSource() {
		return source;
	}

	@Override
	public String toString() {
		return "ChannelTopic [type=" + type + ", source=" + source + "]";
	}

	@Override
	public JSONObject toJson() {
		return new JSONObject().put("type", this.type).put("source", this.source);
	}
}
