package examples;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import clients.ChannelClient;
import clients.IdentityClient;
import models.types.*;
import exceptions.InvalidAPIResponseException;

public class AuthorizeToChannel {
	
	public static void executeExample() throws Exception {
		// In this example we will use two instances of the ChannelClient() both will authenticate a different user.
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
		
		String channelAddress = newChannelDetails.getString("channelAddress");
		System.out.println(channelAddress);
		
		// Writing data to the channel as the channel owner.
		System.out.println("Writing to channel...");
		ownerClient.write(channelAddress, "log",
				null, new JSONObject().put("log", "This is log number 1"));
		
		// This attempt to read the channel will fail because the channel user is no authorized to read the channel.
		try {
			List<ChannelData> data = userClient.read(channelAddress, null, null, null, null, null);
		} catch(InvalidAPIResponseException ex) {
			System.out.println("Whoops, userClient cannot read from channel");
		}
		
		
		System.out.println("Let's fix that...");
		// Request subscription to the channel as the user. The returned subscriptionLink can be used to authorize the user to the channel.
		JSONObject subRequest = userClient.requestSubscription(channelAddress, 
				new JSONObject().put("accessRights", AccessRights.READ_AND_WRITE.toString()));
		String subscriptionLink = subRequest.getString("subscriptionLink");
		System.out.println("subscription link " + subscriptionLink);
		
		// Find subscriptions to the channel that are not already authorized.
		List<SubscriptionInternal> allSubs = ownerClient.findAllSubscriptions(channelAddress, false);
		
		for(SubscriptionInternal sub : allSubs) {
			if(!(sub.isAuthorized())) {
				System.out.println("authorising subscription "+ sub.getId());
				JSONObject auth = ownerClient.authorizeSubscription(
						channelAddress, 
						new JSONObject().put("id", 
								channelUser.getJSONObject("doc").getString("id")));
				System.out.println("KeyloadLink: "+ auth.getString("keyloadLink"));
			}
		}
		
		 // Writing data to channel as the channel owner. Make sure to authorize potential channel readers beforehand.
		System.out.println("Writing to channel 2nd time...");
		ownerClient.write(channelAddress, "log",
				null, new JSONObject().put("log", "This is log number 2"));
		
		// Reading the channel as the user
		List<ChannelData> data2 = userClient.read(channelAddress, null, null, null, null, null);
		for(ChannelData d: data2) {
			System.out.println(d.getLog().toString());
		}
	}
}
