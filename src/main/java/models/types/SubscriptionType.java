package models.types;

public enum SubscriptionType {
	
	AUTHOR {
		public String toString() {
			return "Author";
		}
	},
	SUBSCRIBER {
		public String toString() {
			return "Subscriber";
		}
	}
}
