
import org.bouncycastle.crypto.CryptoException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LogAuditor {
    private String channel_address = null;
    private String jwt = null;
    private String private_key = null;
    private String public_key = null;
    private String did_id = null;
    private String nonce = null;
//    private String seed = null;
    private String subscriptionLink = null;
    Utils info = new Utils();

    {
        DID did = new DID();
        String[] data;
        try {
            data = did.createDID("LogAuditor");
            this.private_key = data[0];
            this.public_key = data[1];
            this.did_id = data[2];
            this.nonce = did.createNonce(this.did_id);
            this.jwt = did.sigantureNonce(this.private_key, this.public_key, this.nonce, this.did_id);
            this.channel_address = info.getChannel_address();
            requestSubscription();
        } catch (IOException | CryptoException e) {
            e.printStackTrace();
        }
    }

    public void requestSubscription() throws IOException {
        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/subscriptions/request/" + this.channel_address + "?" + Utils.api_key;

        JSONObject json_inn = new JSONObject()
                .put("accessRights", "Read");

        JSONObject json_response = Utils.sendIotaPostRequest(uri, json_inn, this.jwt);
//        this.seed = json_response.getString("seed");
        this.subscriptionLink = json_response.getString("subscriptionLink");
        System.out.println("SubscriptionLink: " + this.subscriptionLink);
    }

    public void getDataFromChannel() throws IOException {
        Calendar calendar = Calendar.getInstance(); // this would default to now
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        String startDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(calendar.getTime());
        String endDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(Calendar.getInstance().getTime());
        String startDateQuery = startDate.replaceAll(":", "%3A").replaceAll("\\+", "%2B");
        String endDateQuery = endDate.replaceAll(":", "%3A").replaceAll("\\+", "%2B");

        System.out.println("startDate: " + startDate + "\nendDate: " + endDate);
        System.out.println("startDateQuery: " + startDateQuery + "\nendDateQuery: " + endDateQuery);

        final String uri = "https://ensuresec.solutions.iota.org/api/v0.1/channels/logs/" + this.channel_address + "?limit=5&asc=true" + "&start-date=" + startDateQuery + "&end-date=" + endDateQuery + "&" + Utils.api_key;


        JSONArray json_response = Utils.sendIOTAGetRequestWithAuth(uri, jwt);
        System.out.println("Message from channel: " + json_response.getJSONObject(0).getJSONObject("log").getJSONObject("payload"));
    }

}
