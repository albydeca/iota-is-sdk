package models.types;

public enum AccessRights {
	AUDIT {
		public String toString() {
			return "Audit";
		}
	},
	READ {
		public String toString() {
			return "Read";
		}
	},
	WRITE {
		public String toString() {
			return "Write";
		}
	},
	READ_AND_WRITE {
		public String toString() {
			return "ReadAndWrite";
		}
	}
}
