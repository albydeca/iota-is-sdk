package net.gradbase.models.types;
import org.json.JSONObject;

abstract class IOTAAPIDataItem {

	public IOTAAPIDataItem(JSONObject source) {
	}
	
	public IOTAAPIDataItem() {
	}

	public JSONObject toJson() {
		return null;
	}
}
