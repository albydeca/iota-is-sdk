package net.gradbase.models.types;

public enum CredentialType {
	VERIFIED_IDENTITY {
		public String toString() {
			return "VerifiedIdentityCredential";
		}		
	},
	BASIC_IDENTITY {
		public String toString() {
			return "BasicIdentityCredential";
		}	
	}
}
