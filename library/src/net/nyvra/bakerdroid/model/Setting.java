package net.nyvra.bakerdroid.model;

public class Setting {
	private String mKey;
	private String mValue;
	private Type mType;
	
	public enum Type {
		STRING, BOOL, COLOR, NUMBER
	}

	public String getKey() {
		return mKey;
	}

	public void setKey(String mKey) {
		this.mKey = mKey;
	}

	public String getValue() {
		return mValue;
	}

	public void setValue(String mValue) {
		this.mValue = mValue;
	}

	public Type getType() {
		return mType;
	}

	public void setType(Type mType) {
		this.mType = mType;
	}

}
