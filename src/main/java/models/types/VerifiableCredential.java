package models.types;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VerifiableCredential extends IOTAAPIDataItem {
	private String context;
	private String id;
	private List<String> type;
	
	private String credentialSubjectId;
	private String credentialSubjectType;
	private String credentialSubjectInitiatorId;
	
	private String issuer;
	private String issuanceDate;
	
	private String proofType;
	private String proofVerificationMethod;
	private String proofSignatureValue;
	
	public VerifiableCredential(JSONObject source) {
		this.context = source.getString("@context");
		this.id = source.getString("id");
		
		this.type = new ArrayList<String>();
		JSONArray types = source.getJSONArray("type");
		for(int i = 0; i < types.length(); i++){
			this.type.add(types.getString(i));
		}
		
		JSONObject subject = source.getJSONObject("credentialSubject");
		this.credentialSubjectId = subject.getString("id");
		this.credentialSubjectType = subject.getString("type");
		try {
			this.credentialSubjectInitiatorId = subject.getString("initiatorId");
		} catch(JSONException ex) {
			this.credentialSubjectInitiatorId = null;
		}
		
		this.issuer = source.getString("issuer");
		this.issuanceDate = source.getString("issuanceDate");
		
		JSONObject proof = source.getJSONObject("proof");
		this.proofType = proof.getString("type");
		this.proofVerificationMethod = proof.getString("verificationMethod");
		this.proofSignatureValue = proof.getString("signatureValue");
	}
	

	public VerifiableCredential(JSONObject source, String context, String id, List<String> type,
			String credentialSubjectId, String credentialSubjectType, String credentialSubjectInitiatorId,
			String issuer, String issuanceDate, String proofType, String proofVerificationMethod,
			String proofSignatureValue) {
		super(source);
		this.context = context;
		this.id = id;
		this.type = type;
		this.credentialSubjectId = credentialSubjectId;
		this.credentialSubjectType = credentialSubjectType;
		this.credentialSubjectInitiatorId = credentialSubjectInitiatorId;
		this.issuer = issuer;
		this.issuanceDate = issuanceDate;
		this.proofType = proofType;
		this.proofVerificationMethod = proofVerificationMethod;
		this.proofSignatureValue = proofSignatureValue;
	}



	public String getCredentialSubjectInitiatorId() {
		return credentialSubjectInitiatorId;
	}



	public String getContext() {
		return context;
	}



	public String getId() {
		return id;
	}



	public List<String> getType() {
		return type;
	}



	public String getCredentialSubjectId() {
		return credentialSubjectId;
	}



	public String getCredentialSubjectType() {
		return credentialSubjectType;
	}



	public String getIssuer() {
		return issuer;
	}



	public String getIssuanceDate() {
		return issuanceDate;
	}



	public String getProofType() {
		return proofType;
	}



	public String getProofVerificationMethod() {
		return proofVerificationMethod;
	}



	public String getProofSignatureValue() {
		return proofSignatureValue;
	}


	@Override
	public JSONObject toJson() {
		JSONObject result = new JSONObject().put("@context", this.context)
				.put("id", this.id)
				.put("issuer", this.issuer)
				.put("issuanceDate", this.issuanceDate)
				.put("type", new JSONArray(this.type));
		
		JSONObject credential = new JSONObject().put("id", this.credentialSubjectId)
				.put("type", this.credentialSubjectType);
		
		if(this.credentialSubjectInitiatorId != null) {
			credential.put("initiatorId", this.credentialSubjectInitiatorId);
		}
		
		result.put("credentialSubject", credential);
		
		JSONObject proof = new JSONObject().put("type", this.proofType)
				.put("verificationMethod", this.proofVerificationMethod)
				.put("signatureValue", this.proofSignatureValue);
		
		result.put("proof", proof);
		
		return result;
		
	}
	
}
