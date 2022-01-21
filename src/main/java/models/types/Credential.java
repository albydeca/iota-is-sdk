package models.types;

public class Credential<T> {
	private String id;
	private String type;
	private T subject;
	public Credential(String id, String type, T subject) {
		this.id = id;
		this.type = type;
		this.subject = subject;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public T getSubject() {
		return subject;
	}
	public void setSubject(T subject) {
		this.subject = subject;
	}
	
	
}
