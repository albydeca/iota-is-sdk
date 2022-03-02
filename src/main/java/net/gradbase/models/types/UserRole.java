package net.gradbase.models.types;

public enum UserRole {
	ADMIN {
		public String toString() {
			return "Admin";
		}
	},
	MANAGER {
		public String toString() {
			return "Manager";
		}
	},
	USER {
		public String toString() {
			return "User";
		}
	}
}
