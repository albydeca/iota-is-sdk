package clients;

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

import exceptions.InvalidAPIResponseException;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

import models.types.*;


public class ChannelClient extends BaseClient {

	public ChannelClient() throws FileNotFoundException, IOException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/**
	   * Create a new channel. An author can create a new channel with specific topics where other clients can subscribe to.
	   * @param SubscriptionPassword
	   * @param topics
	   * @param hasPresharedKey
	   * @param seed
	   * @param presharedKey
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public JSONObject create(String subscriptionPassword,
			List<Map<String, String>> topics, Boolean hasPresharedKey,
			String seed, String presharedKey) 
					throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "channels/create";
		JSONArray topicsArray = new JSONArray();
		for(int i = 0; i<topics.size(); i++) {
			Map<String, String> t = topics.get(i);
			JSONObject topic = new JSONObject()
					.put("type", t.get("type"))
					.put("source", t.get("source"));
			topicsArray.put(topic);
		}
		
		JSONObject body = new JSONObject()
				.put("topics", topicsArray);
		
		if(subscriptionPassword != null) {
			body.put("subscriptionPassword", subscriptionPassword);
		}
		
		if(hasPresharedKey != null) {
			body.put("hasPresharedKey", hasPresharedKey.booleanValue());
		}
		
		if(seed != null) {
			body.put("seed", seed);
		}
		
		if(presharedKey != null) {
			body.put("presharedKey", presharedKey);
		}
		
		return sendIOTAPostRequest(endpoint, body, true);		
	}
	
	  /**
	   * Write data to a channel with address channel address. Write permission is mandatory. The type and metadata fields are not encrypted to have a possibility to search for events. The payload is stored encrypted for encrypted channels.
	   * @param channelAddress
	   * @param type
	   * @param metadata
	   * @param payload
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public ChannelData write(String channelAddress, String type, String metadata,
			JSONObject payload) throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "channels/logs/" + channelAddress;
		TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
		JSONObject json = new JSONObject()
                .put("type", type)
                .put("created", nowAsISO)
                .put("metadata", metadata)
                .put("payload", payload);

        
        System.out.println("Message send to channel: " + payload +
        		" " + nowAsISO);
		final JSONObject response = sendIOTAPostRequest(endpoint, json, true);
		if (response == null) {return null;}
		return new ChannelData(response);
	}
	
	  /**
	   * Get data from the channel with address channel address. The first possible message a subscriber can receive is the time the subscription got approved all messages before are not received. Read permission is mandatory.
	   * @param channelAddress
	   * @param limit
	   * @param index
	   * @param asc
	   * @param start
	   * @param end
	 * @throws InvalidAPIResponseException 
	 * @throws ParseException 
	   * @returns
	   */
	public List<ChannelData> read(String channelAddress, Integer limit, Integer index,
			Boolean asc, Date start, Date end) 
					throws ClientProtocolException, IOException, URISyntaxException, ParseException, InvalidAPIResponseException {
		String endpoint = "channels/logs/" + channelAddress;
		
        Map<String, String> params = new HashMap<String, String>();
        
        if(start != null) {
        	params.put("start-date", prepareDateForGetParam(start));
        }
        
        if(end != null) {
        	params.put("end-date", prepareDateForGetParam(end));
        }
        
        if(limit != null) {
        	params.put("limit", limit.toString());
        }
        
        if(index != null) {
        	params.put("index", index.toString());
        }
        
        if(asc != null) {
        	params.put("asc", asc.toString());
        } else {
        	params.put("asc", "true");
        }
        
        JSONArray response =  sendIOTAGetRequestArray(endpoint, params, true);
        List<ChannelData> result = new ArrayList<ChannelData>();
        
        for (int i = 0; i < response.length(); i++) {
        	  result.add(new ChannelData(response.getJSONObject(i)));
        }
        return result;
	}
	
	 /**
	   * Get all data of a channel using a shared key (in case of encrypted channels). Mainly used from auditors to evaluate a log stream.
	   * @param channelAddress
	   * @param presharedKey
	 * @throws InvalidAPIResponseException 
	 * @throws ParseException 
	   * @returns
	   */
	public List<ChannelData> readHistory(String channelAddress, String presharedKey)
			throws ClientProtocolException, URISyntaxException, IOException, ParseException, InvalidAPIResponseException {
		String endpoint = "channels/history/" + channelAddress;
		JSONArray response = sendIOTAGetRequestWithPresharedKey(endpoint, presharedKey, null);
		
		List<ChannelData> result = new ArrayList<ChannelData>();
        
        for (int i = 0; i < response.length(); i++) {
        	  result.add(new ChannelData(response.getJSONObject(i)));
        }
        return result;
	}
	
	  /**
	   * Validates channel data by comparing the log of each link with the data on the tangle.
	   * @param channelAddress
	   * @param datas
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public JSONArray validate(String channelAddress, List<ChannelData> datas) 
			throws ClientProtocolException, URISyntaxException, IOException, InvalidAPIResponseException {
		String endpoint = "channels/validate/" + channelAddress;
		
		JSONArray body = new JSONArray();
		for (ChannelData d : datas) {
			body.put(d.toJson());
		}
		
		return sendIOTAPostRequestArray(endpoint, body, true);
	}
	
	  /**
	   * The user can decide to re-import the data from the Tangle into the database. A reason for it could be a malicious state of the data.
	   * @param channelAddress
	   * @param body
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public void reimport(String channelAddress, JSONObject body) 
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		sendIOTAPostRequest("channels/re-import/" + channelAddress, body, true);
	}
	
	  /**
	   * Search for a channel. A client can search for a channel which it is interested in.
	   * @param author
	   * @param topic
	   * @param created
	   * @param latestMessage
	   * @param limit
	   * @param index
	 * @throws InvalidAPIResponseException 
	 * @throws ParseException 
	   * @returns
	   */
	public List<ChannelInfo> search(String author, String authorId, String topicType,
			String topicSource, Date created, Date latestMessage, Integer limit, Integer index)
					throws ClientProtocolException, IOException, URISyntaxException, ParseException, InvalidAPIResponseException {
		String endpoint = "channel-info/search";
		Map<String, String> params = new HashMap<String, String>();
		
		if(author != null) {
			params.put("author", author);
		}
		
		if(authorId != null) {
			params.put("author-id", authorId);
		}
		
		if(topicType != null) {
			params.put("topic-type", topicType);
		}
		
		if(topicSource != null) {
			params.put("topic-source", topicSource);
		}
		
		if(created != null) {
			params.put("created", prepareDateForGetParam(created));
		}
		
		if(latestMessage != null) {;
			params.put("latest-message", prepareDateForGetParam(latestMessage));
		}
		
		if(limit != null) {
			params.put("limit", limit.toString());
		} else {
			params.put("limit", "5");
		}
		
		if(index != null) {
			params.put("index", index.toString());
		}
		
		JSONArray response = sendIOTAGetRequestArray(endpoint, params, true);
		List<ChannelInfo> result = new ArrayList<ChannelInfo>();
        
        for (int i = 0; i < response.length(); i++) {
        	  result.add(new ChannelInfo(response.getJSONObject(i)));
        }
        return result;
	}
	
	  /**
	   * Get information about a channel with address channel-address.
	   * @param channelAddress
	 * @throws InvalidAPIResponseException 
	 * @throws ParseException 
	   * @returns
	   */
	public ChannelInfo info(String channelAddress) 
			throws ClientProtocolException, IOException, URISyntaxException, ParseException, InvalidAPIResponseException {
		String endpoint = "channel-info/channel/" + channelAddress;
		final JSONObject response = sendIOTAGetRequest(endpoint, null, false);
		if (response == null) {return null;}
		return new ChannelInfo(response);
	}
	
	  /**
	   * Add an existing channel into the database. Clients are able to add existing channels into the database so others can subscribe to them. This will be automatically called when a channel will be created.
	   * @param info
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public void add(ChannelInfo info) throws ClientProtocolException,
	IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "channel-info/channel";
		sendIOTAPostRequest(endpoint, info.toJson(), true);
	}
	
	  /**
	   * Update channel information. The author of a channel can update topics of a channel.
	   * @param info
	   * @returns
	   */
	public void update(ChannelInfo info) 
			throws ClientProtocolException, URISyntaxException, IOException {
		String endpoint = "channel-info/channel";
		sendIOTAPutRequestWithAuth(endpoint, info.toJson());
	}
	
	  /**
	   * Delete information of a channel with address channel-address. The author of a channel can delete its entry in the database. In this case all subscriptions will be deleted and the channel won’t be found in the system anymore. The data & channel won’t be deleted from the IOTA Tangle since its data is immutable on the tangle!
	   * @param channelAddress
	   * @returns
	   */
	public void remove(String channelAddress)
			throws JSONException, ParseException, IOException, URISyntaxException {
		String endpoint = "channel-info/channel/" + channelAddress;
		sendIOTADeleteRequestWithAuth(endpoint, null);
	}
	
	  /**
	   * Get all subscriptions of a channel. Use the is-authorized query parameter to filter for authorized subscriptions.
	   * @param channelAddress
	   * @param isAuthorized
	 * @throws InvalidAPIResponseException 
	 * @throws ParseException 
	   * @returns
	   */
	public List<SubscriptionInternal> findAllSubscriptions(String channelAddress, Boolean isAuthorized)
			throws ClientProtocolException, IOException, URISyntaxException, ParseException, InvalidAPIResponseException {
		String endpoint = "subscriptions/" + channelAddress;
		JSONArray response;
		if(isAuthorized != null) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("isAuthorized", isAuthorized.toString());
			response =  sendIOTAGetRequestArray(endpoint, params, true);
		} else {
			response =  sendIOTAGetRequestArray(endpoint, null, true); 
		}
		
		List<SubscriptionInternal> result = new ArrayList<SubscriptionInternal>();
        
        for (int i = 0; i < response.length(); i++) {
        	  result.add(new SubscriptionInternal(response.getJSONObject(i)));
        }
        return result;
		
	}
	
	  /**
	   * Get a subscription of a channel by identity id.
	   * @param channelAddress
	   * @param id
	 * @throws InvalidAPIResponseException 
	 * @throws ParseException 
	   * @returns
	   */
	public SubscriptionInternal findSubscription(String channelAddress, String id) 
			throws ClientProtocolException, IOException, URISyntaxException, ParseException, InvalidAPIResponseException {
		String endpoint = "subscriptions/" + channelAddress + "/" + id;
		final JSONObject response = sendIOTAGetRequest(endpoint, null, true);
		if (response == null) {return null;}
		return new SubscriptionInternal(response);
	}
	
	  /**
	   * Request subscription to a channel with address channel-address. A client can request a subscription to a channel which it then is able to read/write from.
	   * @param channelAddress
	   * @param options
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public JSONObject requestSubscription(String channelAddress, JSONObject options)
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "subscriptions/request/" + channelAddress;
		
		return sendIOTAPostRequest(endpoint, options, true);
	}
	
	  /**
	   * Authorize a subscription to a channel with address channel-address. The author of a channel can authorize a subscriber to read/write from a channel. Eventually after verifying its identity (using the Ecommerce-SSI Bridge).
	   * @param channelAddress
	   * @param subId
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public JSONObject authorizeSubscription(String channelAddress, JSONObject subId) 
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "subscriptions/authorize/" + channelAddress;
		
		return sendIOTAPostRequest(endpoint, subId, true);
	}
	
	  /**
	   * Revoke subscription to a channel. Only the author of a channel can revoke a subscription from a channel.
	   * @param channelAddress
	   * @param subId
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public void revokeSubscription(String channelAddress, JSONObject subId) 
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "subscriptions/revoke/" + channelAddress;
		
		sendIOTAPostRequest(endpoint, subId, true);
	}
	
	  /**
	   * Adds an existing subscription (e.g. the subscription was not created with the api but locally.)
	   * @param channelAddress
	   * @param id
	   * @param sub
	 * @throws InvalidAPIResponseException 
	   * @returns
	   */
	public SubscriptionInternal addSubscription(String channelAddress, String id, SubscriptionInternal sub) 
			throws ClientProtocolException, IOException, URISyntaxException, InvalidAPIResponseException {
		String endpoint = "subscriptions/" + channelAddress + "/" + id;
		final JSONObject response = sendIOTAPostRequest(endpoint, sub.toJson(), true);
		if (response == null) {return null;}
		return new SubscriptionInternal(response);
	}
	
	 /**
	   * Updates an existing subscription.
	   * @param channelAddress
	   * @param id
	   * @param updatedSub
	   * @returns
	   */
	public void updateSubscription(String channelAddress, String id, JSONObject updatedSub)
			throws ClientProtocolException, URISyntaxException, IOException {
		String endpoint = "subscriptions/" + channelAddress + "/" + id;
		sendIOTAPutRequestWithAuth(endpoint, updatedSub);
	}
	
	  /**
	   * Deletes an existing subscription.
	   * @param channelAddress
	   * @param id
	   * @returns
	   */
	public void removeSubscription(String channelAddress, String id) 
			throws JSONException, ParseException, IOException, URISyntaxException {
		String endpoint = "subscriptions/" + channelAddress + "/" + id;
		sendIOTADeleteRequestWithAuth(endpoint, null);
	}

}
