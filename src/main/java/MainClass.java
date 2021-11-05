import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MainClass {
    public static void main(String args[]) 
    		throws Exception {
    	final String filepath = System.getProperty("data-filepath");
        LogCreator creator = new LogCreator();
        LogAuditor auditor = new LogAuditor();
        System.out.println("-------------------------- " +
	        Utils.ANSI_GREEN +  "LogCreator" + Utils.ANSI_RESET +
	        " --------------------------");
        Tuple<Boolean, String> subbed = creator.
        		auditorIsSubscribedToChannel(auditor.getDid_id());
        if(!subbed.x) {
        	if(subbed.y == null) {
        		creator.authorizeSubscriptionToChannel(
        				auditor.requestSubscription());
        	} else {
        		creator.authorizeSubscriptionToChannel(subbed.y);
        	}
        }
        
        InputStream is = new FileInputStream(filepath);
        JSONObject data = new JSONObject(IOUtils.toString(is, "UTF-8"));
////        data.put("Test1", "1").put("Test2", "2").put("Test3", "3");
        creator.writeDataOnChannel(data);
        
        System.out.println("-------------------------- " +
	        Utils.ANSI_GREEN +  "LogAuditor" + Utils.ANSI_RESET +
	        " --------------------------");
        auditor.getDataFromChannel();
    }
}
