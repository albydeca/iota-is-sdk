package models.types;

import org.json.JSONException;
import org.json.JSONObject;

//CLAIM EXAMPLES SHOWN HERE ("claim" entries):
//JSONObject jsonDevice = new JSONObject()
//.put("username", "foobarbaz")
//.put("claim", new JSONObject().put("type", "Device")
//		.put("category", new JSONArray().put("actuator"))
//     .put("controlledProperty", new JSONArray()
//     		.put("fillingLevel").put("temperature"))
//     .put("firmwareVersion", "number")
//     .put("hardwareVersion", "number")
//     .put("ipAddress", new JSONArray()
//     		.put("192.14.56.78"))
//     .put("serialNumber", "9845A")
//     .put("dateFirstUsed", "2014-09-11T11:00:00Z"));
//
//JSONObject jsonOrganization = new JSONObject()
//.put("username", "foobarbaz")
//.put("claim", new JSONObject().put("type", "Organization")
//		.put("name", "randomName"))
//.put("alternateName", "randomName")
//.put("url", "www.random.com")
//.put("address", "21 Jump Street, Baltimore, USA")
//.put("email", "organization@test.com")
//.put("faxNumber", "1234567890")
//.put("telephone", "1234567890");

public class Claim extends IOTAAPIDataItem {
	private UserType type;
	private JSONObject body;

	public Claim(JSONObject source) {
		this.body = source;

		try {
			switch (source.getString("type")) {
			case "Person":
				this.type = UserType.PERSON;
			case "Service":
				this.type = UserType.SERVICE;
			case "Organization":
				this.type = UserType.ORG;
			case "Device":
				this.type = UserType.DEVICE;
			case "Product":
				this.type = UserType.PRODUCT;
			default:
				this.type = UserType.OTHER;

			}
		} catch (JSONException ex) {
			this.type = null;
		}
	}

	public Claim(UserType type) {
		this.type = type;
		this.body = new JSONObject().put("type", type.toString());
	}

	public UserType getType() {
		return type;
	}

	public JSONObject getBody() {
		return body;
	}

	@Override
	public String toString() {
		return "Claim [type=" + type + ", body=" + body + "]";
	}

	public JSONObject toJson() {
		return body;
	}
}
