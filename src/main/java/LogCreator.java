import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;


public class LogCreator {
    private String private_key = null;
    private String public_key = null;
    private String did_id = null;
    private String nonce = null;
    private String jwt = null;
    public String channel_address = null;
    private static final int DATA_LIMIT = 5;
    Utils info = new Utils();

    {
        DID did = new DID();
        String[] date;
        try {
            date = did.createDID("LogCreator");
            this.private_key = date[0];
            this.public_key = date[1];
            this.did_id = date[2];
            this.nonce = did.createNonce(this.did_id);
            this.jwt = did.sigantureNonce(this.private_key, this.public_key, this.nonce, this.did_id);
            createChannel();
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }
    }

    public void createChannel() throws IOException {
        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/channels/create" + "?" + Utils.api_key;

        JSONObject json = new JSONObject()
                .put("topics", new JSONArray().put(new JSONObject().put("type", "example-channel-data").put("source", "channel-creator")))
                .put("encrypted", "true");

        JSONObject response = Utils.sendIotaPostRequest(uri, json, jwt);
        this.channel_address = response.getString("channelAddress");
        info.setChannel_address(this.channel_address);
        System.out.println("Channel successfully created: " + this.channel_address);
    }

    public void writeDataOnChannel(JSONObject input) throws IOException {
        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/channels/logs/" + this.channel_address + "?" + Utils.api_key;

        JSONObject json = new JSONObject()
                .put("type", "example-channel-data")
                .put("created", "2021-07-23T05:25:42.325Z")
                .put("metadata", "example-meta-data")
                .put("payload", input);

        Utils.sendIotaPostRequest(uri, json, jwt);
        System.out.println("Message send to channel: " + input);
    }

    public void getDataFromChannel() throws IOException {
        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/channels/logs/" + this.channel_address + "?limit="+LogCreator.DATA_LIMIT+"&asc=true" + "&" + Utils.api_key;

        JSONArray response = Utils.sendIOTAGetRequestWithAuth(uri, jwt);
        System.out.println("Message from channel: " + response.getJSONObject(0).getJSONObject("channelLog").getJSONObject("payload"));
    }

    public String getAllSubscriptions() throws IOException {
        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/subscriptions/" + this.channel_address + "?is-authorized=false" + "&" + Utils.api_key;
	
        JSONArray response = Utils.sendIOTAGetRequestWithAuth(uri, jwt);
        String sub_link = response.getJSONObject(0).getString("subscriptionLink");
        System.out.println("SubscriptionLink: " + sub_link);
        return sub_link;
    }

    public void authorizedSubscriptions(String subscriptionLink) throws IOException {
        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/subscriptions/authorize/" + this.channel_address + "?" + Utils.api_key;

        JSONObject json = new JSONObject().put("subscriptionLink", subscriptionLink);

      
        JSONObject response = Utils.sendIotaPostRequest(uri, json, jwt);
        System.out.println("Authorized entry: " + response.getString("keyloadLink"));
    }

}
