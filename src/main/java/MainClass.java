import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import examples.*;

public class MainClass {
    public static void main(String args[]) 
    		throws Exception {
    	
    	// must do this always
    	Preliminary.doPrelims();
    	
    	// execute examples in this order to avoid heisenbugs
    	// run example 1
//    	CreateIdentityAndCredential.executeExample();
    	
    	// run example 2
//    	UpdateUser.executeExample();
    	
    	// run example 3
//    	DeleteUser.executeExample();
    	
    	// run example 4
//    	TrustedAuthorities.executeExample();
    	
    	// run example 5
//    	CreateChannel.executeExample();
    	
    	// run example 6
//    	AuthorizeToChannel.executeExample();
    	
    	// run example 7
//    	SearchChannelAndValidateData.executeExample();
    }
}
