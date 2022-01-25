import org.apache.http.client.ClientProtocolException;
import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;


public class LogCreator {
    private String privateKey = null;
    private String publicKey = null;
    private String didId = null;
    private String nonce = null;
    private String jwt = null;
    public String channelAddress = null;
    private static final int DATA_LIMIT = 5;
    Utils info = new Utils();

    {
        DID did = new DID();
        String[] date;
        try {
            date = did.createDID("LogCreator");
            this.privateKey = date[0];
            this.publicKey = date[1];
            this.didId = date[2];
            this.nonce = did.createNonce(this.didId);
            this.jwt = did.signNonce(this.privateKey,
            		this.publicKey, this.nonce, this.didId);
            createChannel(Boolean.parseBoolean
            		(System.getProperty("persist-channel")));
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }
    }

    public void createChannel(boolean persist) throws IOException {
    	JSONObject response;
    	final String path = "persistent_channels";
    	final String filename =
    			this.didId.substring(9) + "_persistent_channel.json";
    	if(persist) {
    		File myObj = new File(path,filename);
    		if(!myObj.exists()) {
    			System.out.println(
    					this.didId + " channel does not exist!\n"
    							+ "Request in progress...");
    			response = createNewChannel();
        		this.channelAddress = response.getString("channelAddress");
        		FileWriter myWriter = new FileWriter(myObj);
                myWriter.write(new JSONObject().put(
                		"channel_address", this.channelAddress).toString());
                myWriter.close();
                System.out.println(
                	"Successfully saved channel address to the persistent json.");
    		} else {
    			System.out.println("Fetching the existing JSON file from DID ID");
    			JSONParser jsonParser = new JSONParser();
                org.json.simple.JSONObject jsonObject = null;
                try {
                    jsonObject = (org.json.simple.JSONObject) jsonParser.parse(
                    		new FileReader(myObj));
                    this.channelAddress = (String) jsonObject.get(
                    		"channel_address");
                } catch (ParseException e) {
                    e.printStackTrace();
                } 
    		}
    	} else {
    		response = createNewChannel();
    		this.channelAddress = response.getString("channelAddress");
    	}
        
        info.setChannelAddress(this.channelAddress);
        System.out.println(
        		"Channel successfully created: " + this.channelAddress);
    }

	public String getChannelAddress() {
		return channelAddress;
	}

	private JSONObject createNewChannel()
			throws ClientProtocolException, IOException {
		final String uri = 
				Utils.iotastreamsApiBaseUrl +"channels/create" +
						"?" + Utils.apiKey;

        JSONObject json = new JSONObject()
                .put("topics", new JSONArray().put(new JSONObject()
                		.put("type", "example-channel-data")
                		.put("source", "channel-creator")))
                .put("encrypted", "true");

        JSONObject response = Utils.sendIotaPostRequest(uri, json, jwt);
		return response;
	}

    public void writeDataOnChannel(JSONObject input) throws IOException {
        final String uri = 
        		Utils.iotastreamsApiBaseUrl +"channels/logs/" +
        				this.channelAddress + "?" + Utils.apiKey;

        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String nowAsISO = df.format(new Date());
		JSONObject json = new JSONObject()
                .put("type", "example-channel-data")
                .put("created", nowAsISO)
                .put("metadata", "example-meta-data")
                .put("payload", input);

        Utils.sendIotaPostRequest(uri, json, jwt);
        System.out.println("Message send to channel: " + input +
        		" " + nowAsISO);
    }

    public void getDataFromChannel() throws IOException {
        final String uri = Utils.iotastreamsApiBaseUrl +"channels/logs/" +
        		this.channelAddress + "?limit="+LogCreator.DATA_LIMIT+
        		"&asc=true" + "&" + Utils.apiKey;

        JSONArray response = Utils.sendIOTAGetRequestWithAuth(uri, jwt);
        System.out.println("Message from channel: " + 
        		response.getJSONObject(0).getJSONObject("channelLog")
        		.getJSONObject("payload"));
    }

    public JSONArray getAllSubscriptions() throws IOException {
    	// TODO filter by channel
        final String uri = Utils.iotastreamsApiBaseUrl +"subscriptions/" + 
        		this.channelAddress + "?" +
        		Utils.apiKey;
	
        return Utils.sendIOTAGetRequestWithAuth(uri, jwt);

    }
    
    public Tuple<Boolean, String> auditorIsSubscribedToChannel(String auditorId)
    		throws IOException {
    	JSONArray allSubs = getAllSubscriptions();
    	JSONObject selectedSub = null;
    	for (int i = 0; i < allSubs.length(); i++) {
    		  JSONObject sub = allSubs.getJSONObject(i);
    		  if (sub.getString("identityId").equals(auditorId)) {
    			  selectedSub = sub;
    			  break;
    		  }
    		}
    	if (selectedSub == null) {
    		return new Tuple<Boolean, String>(false,null);
    	} else {
    		return new Tuple<Boolean, String>(
    				selectedSub.getBoolean("isAuthorized"),
    				selectedSub.getString("subscriptionLink"));
    	}
    }

    public void authorizeSubscriptionToChannel(String subscriptionLink)
    		throws Exception {
    	
        final String uri =
        		Utils.iotastreamsApiBaseUrl +"subscriptions/authorize/" + 
        		this.channelAddress + "?" + Utils.apiKey;

        JSONObject json = new JSONObject()
        		.put("subscriptionLink", subscriptionLink);

      
        JSONObject response = Utils.sendIotaPostRequest(uri, json, jwt);
        System.out.println("Authorized entry: " +
        		response.getString("keyloadLink"));
    }

}
