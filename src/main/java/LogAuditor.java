
import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogAuditor {
    private String channelAddress = null;
    private String jwt = null;
    private String privateKey = null;
    private String publicKey = null;
    private String didId = null;
    private String nonce = null;
//    private String seed = null;
    Utils info = new Utils();

    {
        DID did = new DID();
        String[] data;
        try {
            data = did.createDID("LogAuditor");
            this.privateKey = data[0];
            this.publicKey = data[1];
            this.didId = data[2];
            this.nonce = did.createNonce(this.didId);
            this.jwt = did.signNonce
            		(this.privateKey, this.publicKey, this.nonce, this.didId);
            this.channelAddress = info.getChannel_address();
//            requestSubscription();
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }
    }

    public String getDid_id() {
		return didId;
	}

	public String requestSubscription() throws IOException {
        final String uri = Utils.iotastreamsApiBaseUrl +
        		"subscriptions/request/" + this.channelAddress +
        		"?" + Utils.apiKey;

        JSONObject json_inn = new JSONObject()
                .put("accessRights", "Read");

        JSONObject jsonResponse = Utils.sendIotaPostRequest(
        		uri, json_inn, this.jwt);
//        this.seed = json_response.getString("seed");
        String subscriptionLink = jsonResponse.getString("subscriptionLink");
        System.out.println("SubscriptionLink: " + subscriptionLink);
        return subscriptionLink;
    }

	public void getDataFromChannel() throws IOException {
        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        String startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        		.format(calendar.getTime());
        String endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
        		.format(Calendar.getInstance().getTime());
        String startDateQuery = startDate.replaceAll(":", "%3A")
        		.replaceAll("\\+", "%2B");
        String endDateQuery = endDate.replaceAll(":", "%3A")
        		.replaceAll("\\+", "%2B");

        System.out.println("startDate: " + startDate + "\nendDate: " + endDate);
        System.out.println("startDateQuery: " + startDateQuery +
        		"\nendDateQuery: " + endDateQuery);

        final String uri = 
        		Utils.iotastreamsApiBaseUrl +"channels/logs/" +
        				this.channelAddress + "?limit=5&asc=true" + 
        				"&start-date=" + startDateQuery + "&end-date=" +
        				endDateQuery + "&" + Utils.apiKey;


        JSONArray jsonResponse = Utils.sendIOTAGetRequestWithAuth(uri, jwt);
        for(int i = 0; i < jsonResponse.length(); i++) {
        	System.out.println("Message from channel: " + 
            		jsonResponse.getJSONObject(i).getJSONObject("log")
            		.getJSONObject("payload"));
        }
        
    }

}
