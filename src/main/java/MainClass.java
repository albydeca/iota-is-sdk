
import examples.*;

public class MainClass {
    public static void main(String args[]) 
    		throws Exception {
    	
    	// run example 0 - ok
    	Preliminary.doPrelims();
    	
    	// run example 1 - ok
    	CreateIdentityAndCredential.executeExample();
    	
    	// run example 2 OK
    	UpdateUser.executeExample();
    	
    	// run example 3 OK
    	DeleteUser.executeExample();
    	
    	// run example 4 - ok
    	TrustedAuthorities.executeExample();
    	
    	// run example 5 - OK
    	CreateChannel.executeExample();
    	
    	// run example 6 - OK (authorised user can only see the latest msg)
    	AuthorizeToChannel.executeExample();
    	
    	// run example 7 - OK
    	SearchChannelAndValidateData.executeExample();
    }
}
