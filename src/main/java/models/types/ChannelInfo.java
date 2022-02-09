package models.types;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

public class ChannelInfo extends IOTAAPIDataItem {
	private String channelAddress;
	private String authorId;
	private List<String> subscriberIds;
	private List<ChannelTopic> topics;
	private String created;
	private String latestMessage;

	public ChannelInfo(String channelAddress, String authorId, List<String> subscriberIds, List<ChannelTopic> topics,
			String created, String latestMessage) {
		this.channelAddress = channelAddress;
		this.authorId = authorId;
		this.subscriberIds = subscriberIds;
		this.topics = topics;
		this.created = created;
		this.latestMessage = latestMessage;
	}

	public ChannelInfo(JSONObject source) {
		this.channelAddress = source.getString("channelAddress");
		this.authorId = source.getString("authorId");

		try {
			this.subscriberIds = new ArrayList<String>();
			JSONArray ids = source.getJSONArray("subscriberIds");
			for (int i = 0; i < ids.length(); i++) {
				this.subscriberIds.add(ids.getString(i));
			}
		} catch (JSONException ex) {
			this.subscriberIds = null;
		}

		this.topics = new ArrayList<ChannelTopic>();
		JSONArray tps = source.getJSONArray("topics");
		for (int i = 0; i < tps.length(); i++) {
			JSONObject o = tps.getJSONObject(i);
			ChannelTopic ct = new ChannelTopic(o);
			this.topics.add(ct);
		}

		try {
			this.created = source.getString("created");
		} catch (JSONException ex) {
			this.created = null;
		}

		try {
			this.latestMessage = source.getString("latestMessage");
		} catch (JSONException ex) {
			this.latestMessage = null;
		}

	}

	public String getChannelAddress() {
		return channelAddress;
	}

	public String getAuthorId() {
		return authorId;
	}

	public List<String> getSubscriberIds() {
		return subscriberIds;
	}

	public List<ChannelTopic> getTopics() {
		return topics;
	}

	public String getCreated() {
		return created;
	}

	public String getLatestMessage() {
		return latestMessage;
	}

	@Override
	public JSONObject toJson() {
		JSONObject result = new JSONObject();
		result.put("channelAddress", this.channelAddress).put("authorId", this.authorId);

		if (this.created != null) {
			result.put("created", this.created);
		}

		if (this.latestMessage != null) {
			result.put("latestMessage", this.latestMessage);
		}

		if (this.subscriberIds != null) {
			result.put("subscriberIds", new JSONArray(this.subscriberIds));
		}

		JSONArray jsonTopics = new JSONArray();
		for (ChannelTopic ct : this.topics) {
			jsonTopics.put(ct.toJson());
		}

		result.put("topics", jsonTopics);

		return result;
	}

}
