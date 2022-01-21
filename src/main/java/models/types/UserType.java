package models.types;

public enum UserType {
	PERSON {
		public String toString() {
			return "Person";
		}
	},
	SERVICE {
		public String toString() {
			return "Service";
		}
	},
	ORG {
		public String toString() {
			return "Organization";
		}
	},
	DEVICE {
		public String toString() {
			return "Device";
		}
	},
	PRODUCT {
		public String toString() {
			return "Product";
		}
	},
	OTHER {
		public String toString() {
			return "Unknown";
		}
	}
}
