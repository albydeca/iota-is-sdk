package examples;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import clients.IdentityClient;
import models.types.*;

public class TrustedAuthorities {
	
	public static void executeExample() throws Exception {
		IdentityClient client = new IdentityClient();

		String didId = Preliminary.authenticateRootIdentity(client);
		
		System.out.println("User authenticated");
		
		// Create an identity for a driver to issue him a driving license
		JSONObject jsonClaim =  new JSONObject().put("type", "Person")
	    			.put("name", "Driver Bob");

	        
		Claim claim = new Claim(jsonClaim);
		    
	    JSONObject driverIdentity = client.create("Driver", claim);
	    System.out.println(driverIdentity);
	    
	    IdentityInternal adminIdentity = client.find(didId);
	    System.out.println("Public ID of admin "+ adminIdentity.getId());
	    
	  //Get root identity to issue an credential for the new driver
	    VerifiableCredential identityCredential = adminIdentity.getVerifiableCredentials().get(0);
	    
	 // List all trusted authorities, currently only one authority is trusted for issuing credentials
	    List<String> trustedAuthorities = client.getTrustedAuthorities();
	    System.out.println(trustedAuthorities);
	    
	    VerifiableCredential driverCredential = client.createCredential(
	    		identityCredential, 
	    		driverIdentity.getJSONObject("doc").getString("id"),
	    		CredentialType.BASIC_IDENTITY,
	    		new Claim(new JSONObject().put("type", "Person")
	    				.put("driveAllowance", false).put("issuanceDate", "2014-09-11T11:00:00Z")));
	    
	 // Verify the drivers license issued by the local authority.
	    // Verification result should be positive
	   boolean verified1 = client.checkCredential(driverCredential);
	   System.out.println("Verified1 internal"+ verified1);
	   
	// Verify the drivers license issued by an external authority.
	   // This drivers license will not be trusted because it was not added as an trusted authority by us.
	   InputStream is = new FileInputStream("data_source/externalDriverCredential1.json");
	   String jsonTxt = IOUtils.toString(is, "UTF-8");
	   VerifiableCredential externalCredential = new VerifiableCredential(new JSONObject(jsonTxt));
	   boolean verified2 = client.checkCredential(externalCredential);
	   System.out.println("Verified2 external untrusted authority"+ verified2);
	   
	   // Added the external authority to the trusted authorities.
	   // The id of the external authority can be found in the external credential
	   String externalToBeTrustedAuthority = externalCredential.getIssuer();
	   client.addTrustedAuthority(externalToBeTrustedAuthority);
	   
	// List all trustedAuthorities, to verify the external authority has been added
	   List<String> trustedAuthorities2 = client.getTrustedAuthorities();
	   System.out.println(trustedAuthorities2);
	    
	// Verify the drivers license issued by the local authority again
	   // Verification result should be true again
	   boolean verified4 = client.checkCredential(driverCredential);
	   System.out.println("Verified4 internal"+ verified4);
	   
	   // Verify the drivers license issued by an external authority
	   boolean verified5 = client.checkCredential(externalCredential);
	   System.out.println("Verified5 external (now trusted) authority"+ verified5);
	   
	// Remove the external authority again, just for repeatability
	   client.removeTrustedAuthority(externalToBeTrustedAuthority);
	}

}
