package net.gradbase.models.types;

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
//	private String credentialSubjectType;
	private String credentialSubjectInitiatorId;
	private String credentialSubjectContext;
	private Claim credentialSubjectClaim;

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
		for (int i = 0; i < types.length(); i++) {
			this.type.add(types.getString(i));
		}

		JSONObject subject = source.getJSONObject("credentialSubject");
		this.credentialSubjectId = subject.getString("id");
		subject.remove("id");
//		this.credentialSubjectType = subject.getString("type");
//		subject.remove("type");
		try {
			this.credentialSubjectInitiatorId = subject.getString("initiatorId");
			subject.remove("initiatorId");
		} catch (JSONException ex) {
			this.credentialSubjectInitiatorId = null;
		}

		try {
			this.credentialSubjectContext = subject.getString("@context");
			subject.remove("@context");
		} catch (JSONException ex) {
			this.credentialSubjectContext = null;
		}
		Claim credentialClaim = new Claim(subject);
		this.credentialSubjectClaim = credentialClaim;

		this.issuer = source.getString("issuer");
		this.issuanceDate = source.getString("issuanceDate");

		JSONObject proof = source.getJSONObject("proof");
		this.proofType = proof.getString("type");
		this.proofVerificationMethod = proof.getString("verificationMethod");
		this.proofSignatureValue = proof.getString("signatureValue");
	}

//	public VerifiableCredential(JSONObject source, String context, String id, List<String> type,
//			String credentialSubjectId, String credentialSubjectType, String credentialSubjectInitiatorId,
//			String issuer, String issuanceDate, String proofType, String proofVerificationMethod,
//			String proofSignatureValue) {
//		super(source);
//		this.context = context;
//		this.id = id;
//		this.type = type;
//		this.credentialSubjectId = credentialSubjectId;
//		this.credentialSubjectType = credentialSubjectType;
//		this.credentialSubjectInitiatorId = credentialSubjectInitiatorId;
//		this.issuer = issuer;
//		this.issuanceDate = issuanceDate;
//		this.proofType = proofType;
//		this.proofVerificationMethod = proofVerificationMethod;
//		this.proofSignatureValue = proofSignatureValue;
//	}

	@Override
	public String toString() {
		return "VerifiableCredential [context=" + context + ", id=" + id + ", type=" + type + ", credentialSubjectId="
				+ credentialSubjectId + ", credentialSubjectInitiatorId=" + credentialSubjectInitiatorId
				+ ", credentialSubjectContext=" + credentialSubjectContext + ", credentialSubjectClaim="
				+ credentialSubjectClaim + ", issuer=" + issuer + ", issuanceDate=" + issuanceDate + ", proofType="
				+ proofType + ", proofVerificationMethod=" + proofVerificationMethod + ", proofSignatureValue="
				+ proofSignatureValue + "]";
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

//
//	public String getCredentialSubjectType() {
//		return credentialSubjectType;
//	}

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
		JSONObject result = new JSONObject().put("@context", this.context).put("id", this.id).put("issuer", this.issuer)
				.put("issuanceDate", this.issuanceDate).put("type", new JSONArray(this.type));

		JSONObject credentialSubject = new JSONObject().put("id", this.credentialSubjectId);
//				.put("type", this.credentialSubjectType);

		if (this.credentialSubjectInitiatorId != null) {
			credentialSubject.put("initiatorId", this.credentialSubjectInitiatorId);
		}
		if (this.credentialSubjectContext != null) {
			credentialSubject.put("@context", this.credentialSubjectContext);
		}

		final JSONObject claim_json = this.credentialSubjectClaim.toJson();
		for (String k : JSONObject.getNames(claim_json)) {
			credentialSubject.put(k, claim_json.get(k));
		}
		result.put("credentialSubject", credentialSubject);

		JSONObject proof = new JSONObject().put("type", this.proofType)
				.put("verificationMethod", this.proofVerificationMethod)
				.put("signatureValue", this.proofSignatureValue);

		result.put("proof", proof);

		return result;

	}

}
