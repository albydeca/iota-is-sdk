package examples;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import clients.IdentityClient;
import models.types.Claim;
import models.types.IdentityInternal;

public class DeleteUser {
	
	private static final String RANDOM_USERNAME_TODELETE = "randomUsernameTODELETE";

	public static void executeExample() throws Exception{
		IdentityClient client = new IdentityClient();

		InputStream is = new FileInputStream("LogCreator.json");
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        System.out.println(jsonTxt);
        JSONObject json = new JSONObject(jsonTxt);       
 
        
        final String didId = json.getString("ID");
		client.authenticate(didId, json.getString("PrivateKey"));
		
		System.out.println("User authenticated");
		
		JSONObject jsonClaim =  new JSONObject().put("type", "Organization")
    			.put("name", "randomNameTODELETE");

        
        Claim claim = new Claim(jsonClaim);
        
        JSONObject newUserIdentity = client.create(RANDOM_USERNAME_TODELETE, claim);
        
        Date in = new Date();
        LocalDateTime yesterday = LocalDateTime.ofInstant(in.toInstant(),
        		ZoneId.systemDefault()).minusDays(1);
        Date out = Date.from(yesterday.atZone(ZoneId.systemDefault()).toInstant());
        
        List<IdentityInternal> identities = client.search(null, RANDOM_USERNAME_TODELETE,
				out, null, null);
        
        List<IdentityInternal> toRevoke = new ArrayList<IdentityInternal>();
        
        if(identities.size() > 0) {
        	for(int i = 0; i < identities.size(); i++) {
        		if(!(identities.get(i).getIsServerIdentity().booleanValue())) {
        			toRevoke.add(identities.get(i));
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
