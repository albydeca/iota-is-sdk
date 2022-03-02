package net.gradbase.models.types;

import org.json.JSONObject;
import org.json.JSONException;

public class ChannelData extends IOTAAPIDataItem {

	private String link;
	private String imported;
	private String messageId;
	private ChannelLog log;

	public ChannelData(String link, String imported, String messageId, ChannelLog log) {
		this.link = link;
		this.imported = imported;
		this.messageId = messageId;
		this.log = log;
	}

	public ChannelData(JSONObject source) {
		this.link = source.getString("link");

		try {
			this.imported = source.getString("imported");
		} catch (JSONException ex) {
			this.imported = null;
		}

		try {
			this.messageId = source.getString("messageId");
		} catch (JSONException ex) {
			this.messageId = null;
		}

		this.log = new ChannelLog(source.getJSONObject("log"));
	}

	@Override
	public String toString() {
		return "ChannelData [link=" + link + ", imported=" + imported + ", messageId=" + messageId + ", log=" + log
				+ "]";
	}

	public String getLink() {
		return link;
	}

	public String getImported() {
		return imported;
	}

	public String getMessageId() {
		return messageId;
	}

	public ChannelLog getLog() {
		return log;
	}

	@Override
	public JSONObject toJson() {
		JSONObject result = new JSONObject().put("link", this.link).put("log", log.toJson());

		if (this.imported != null) {
			result.put("imported", this.imported);
		}

		if (this.messageId != null) {
			result.put("messageId", this.messageId);
		}

		return result;
	}

}
