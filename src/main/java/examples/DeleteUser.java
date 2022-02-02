package examples;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.json.JSONObject;

import clients.IdentityClient;
import models.types.Claim;
import models.types.IdentityInternal;

public class DeleteUser {
	
	private static final String RANDOM_USERNAME_TODELETE = "randomUsernameTODELETE";

	public static void executeExample() throws Exception{
		IdentityClient client = new IdentityClient();

		Preliminary.authenticateRootIdentity(client);
		
		System.out.println("User authenticated");
		
		JSONObject jsonClaim =  new JSONObject().put("type", "Organization")
    			.put("name", "randomNameTODELETE");

        
        Claim claim = new Claim(jsonClaim);
        
        client.create(RANDOM_USERNAME_TODELETE, claim);
        
        Date in = new Date();
        LocalDateTime yesterday = LocalDateTime.ofInstant(in.toInstant(),
        		ZoneId.systemDefault()).minusDays(1);
        Date out = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());
        
        List<IdentityInternal> identities = client.search(null, RANDOM_USERNAME_TODELETE,
				out, null, null);
        
        List<IdentityInternal> toRevoke = new ArrayList<IdentityInternal>();
        
        if(identities.size() > 0) {
        	for(IdentityInternal identity : identities) {
        		System.out.println(identity);
        		boolean idToRevoke = false;
        		
        		Boolean isServerIdentity = identity.getIsServerIdentity();
        		
        		if(isServerIdentity == null) {
        			idToRevoke = true;
        		} else {
        			idToRevoke = !(isServerIdentity.booleanValue());
        		}
        		if(idToRevoke) {
        			System.out.println("identity is to be removed");
        			toRevoke.add(identity);
        		}
        	}
        	
        	if(toRevoke.size() > 0) {
        		IdentityInternal firstToRevoke = toRevoke.get(0);
        		
        		System.out.println("Revoking identity " + firstToRevoke.toString());
        		
        		client.remove(firstToRevoke.getId(), true);
        		System.out.println("Successfully revoked");
        		
        		JSONObject recoveredIdentity = client.latestDocument(firstToRevoke.getId());
        		System.out.println("Retrieved identity document of the deceased: " + recoveredIdentity);
        		
        		
        	} else {
        		System.out.println("no death row!");
        	}
        } else {
        	System.out.println("Could not find identities with the username " + RANDOM_USERNAME_TODELETE);
        }
	}

}
