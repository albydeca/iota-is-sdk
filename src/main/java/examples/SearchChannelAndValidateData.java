package examples;

import java.util.Date;
import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

import clients.ChannelClient;
import clients.IdentityClient;
import models.types.*;

public class SearchChannelAndValidateData {
	
	public static void executeExample() throws Exception {
		ChannelClient ownerClient = new ChannelClient();
		ChannelClient userClient = new ChannelClient();
		IdentityClient identityClient = new IdentityClient();
		
		 // Creating a channel owner who creates the channel and a channel user who will be authorized to read the channel
		 // We will use two instances of the channel api client. One is getting authorized by the owner and the other one by the user.
		JSONObject jsonClaim =  new JSONObject().put("type", "Person")
    			.put("name", "Driver Bob");
		
		JSONObject channelOwner = identityClient.create("ChannelOwner", new Claim(jsonClaim));
		
		
		ownerClient.authenticate(channelOwner.getJSONObject("doc").getString("id"),
				channelOwner.getJSONObject("key").getString("secret"));
		
		JSONObject jsonClaim2 =  new JSONObject().put("type", "Person")
    			.put("name", "Passenger Mike");
		
		JSONObject channelUser = identityClient.create("ChannelUser", new Claim(jsonClaim2));
		
		
		userClient.authenticate(channelUser.getJSONObject("doc").getString("id"),
				channelUser.getJSONObject("key").getString("secret"));
		
		Map<String, String> topics = new HashMap<String, String>();
		topics.put("example-data", "data-creator");
		
		List<Map<String, String>> allTopics = new ArrayList<Map<String, String>>();
		allTopics.add(topics);
		
		// The owner creates a channel where he/she want to publish data of type 'example-data'.
		JSONObject newChannelDetails = ownerClient.create(null, allTopics, null, null, null);
		System.out.println("Created channel with address "+
		newChannelDetails.getString("channelAddress"));
		
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.HOUR_OF_DAY, 0 );
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		Date todayAtMidnight = calendar.getTime();

		List<ChannelInfo> channels = userClient.search(null,
				channelOwner.getJSONObject("doc").getString("id"),
				"example-data", null, todayAtMidnight, null, null, null);
		
		if(channels.isEmpty()) {
			throw new Exception("Could not find channels matching search criteria");
		}
		
		String channelAddress = channels.get(0).getChannelAddress();
		System.out.println("Requesting subscrption to channel "+ channelAddress);
		JSONObject subRequest = userClient.requestSubscription(channelAddress, new JSONObject());
		
		ownerClient.authorizeSubscription(
				channelAddress, 
				new JSONObject().put("subscriptionLink", 
						subRequest.getString("subscriptionLink")));
		
		System.out.println("Writing to channel...");
		ownerClient.write(channelAddress, "log",
				null, new JSONObject().put("log", "This is log number 1"));
		
		List<ChannelData> channelData = userClient.read(channelAddress, null, null, null, null, null);
		
		JSONArray validated = userClient.validate(channelAddress, channelData);
		
		System.out.println("Validated result: "+ validated);
		
		JSONObject tampered = new JSONObject().put("log", "this log is not the original");
		channelData.get(0).getLog().setPayload(tampered);
		
		validated = userClient.validate(channelAddress, channelData);
		System.out.println("Validated result with manipulated data: "+ validated);
	}

}
