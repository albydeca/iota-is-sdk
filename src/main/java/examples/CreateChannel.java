package examples;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import clients.ChannelClient;
import clients.IdentityClient;
import models.types.*;

public class CreateChannel {
	
	public static void executeExample() throws Exception {
		IdentityClient identityClient = new IdentityClient();
		ChannelClient channelClient = new ChannelClient();
		
		// Create a new user. The user is used for authentication only.
		JSONObject jsonClaim =  new JSONObject().put("type", "Person")
    			.put("name", "Driver Bob");
		
		JSONObject newUser = identityClient.create("ChannelOwner", new Claim(jsonClaim));
		
		// Authenticate as the user
		channelClient.authenticate(newUser.getJSONObject("doc").getString("id"),
				newUser.getJSONObject("key").getString("secret"));
		
		Map<String, String> topics = new HashMap<String, String>();
		topics.put("example-data", "data-creator");
		
		List<Map<String, String>> allTopics = new ArrayList<Map<String, String>>();
		allTopics.add(topics);
		
		// Create a new channel for example data
		JSONObject newChannelDetails = channelClient.create(null, allTopics, null, null, null);
		
		// The channel address is used to read and write to channels
		String channelAddress = newChannelDetails.getString("channelAddress");
		System.out.println(channelAddress);
		
		// Writing 5 data packets to channel
		for(int i=0;i<5;i++) {
			System.out.println("writing data to channel no "+ i);
			channelClient.write(channelAddress, "log",
					null, new JSONObject().put("log", "This is log number "+ i));
		}
		
		// Reading channel
		List<ChannelData> datas = channelClient.read(channelAddress,
				null, null, null, null, null);
		
		for(ChannelData data: datas) {
			System.out.println(data.getLog().toString());
		}
	}

}
 