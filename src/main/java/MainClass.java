
import examples.*;

public class MainClass {
    public static void main(String args[]) 
    		throws Exception {
    	
    	// run example 0 - bug with 500 cannot create credential
    	Preliminary.doPrelims();
    	
    	// run example 1 - bug with "identities/identity/" + id GET returns nothing
//    	CreateIdentityAndCredential.executeExample();
    	
    	// run example 2 OK
    	UpdateUser.executeExample();
    	
    	// run example 3 OK
//    	DeleteUser.executeExample();
    	
    	// run example 4 - bug with "identities/identity/" + id GET returns nothing
//    	TrustedAuthorities.executeExample();
    	
    	// run example 5 - OK
//    	CreateChannel.executeExample();
    	
    	// run example 6 - OK (authorised user can only see the latest msg)
//    	AuthorizeToChannel.executeExample();
    	
    	// run example 7 - OK
//    	SearchChannelAndValidateData.executeExample();
    }
}
