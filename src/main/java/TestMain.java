import org.json.JSONObject;

import java.io.IOException;

public class TestMain {
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static void main(String args[]) throws IOException {
        LogCreator creator = new LogCreator();
        LogAuditor auditor = new LogAuditor();
        System.out.println("-------------------------- " + ANSI_GREEN +  "LogCreator" + ANSI_RESET + " --------------------------");
        String subscriptionLink = creator.getAllSubscriptions();
        creator.authorizedSubscriptions(subscriptionLink);
        JSONObject data = new JSONObject();
        data.put("Test1", "1").put("Test2", "2").put("Test3", "3");
        creator.writeDataOnChannel(data);
        creator.writeDataOnChannel(data);
        creator.writeDataOnChannel(data);
        creator.writeDataOnChannel(data);
        creator.writeDataOnChannel(data);
        creator.writeDataOnChannel(data);
        creator.writeDataOnChannel(data);
        creator.writeDataOnChannel(data);
        System.out.println("-------------------------- " + ANSI_GREEN +  "LogAuditor" + ANSI_RESET + " --------------------------");
        auditor.getDataFromChannel();
    }
}
