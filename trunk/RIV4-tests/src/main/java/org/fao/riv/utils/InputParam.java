package org.fao.riv.utils;

public class InputParam {
	private String name;
	private String value;
	private InputParamType paramType;
	private boolean calculated;
	private String[] options;
	
	// constructors
	public InputParam(String name) {
		this.setName(name);
		this.setParamType(InputParamType.TEXT);
	}
	
	public InputParam(String name, InputParamType paramType, boolean calculated) {
		this.setName(name);
		this.setParamType(paramType);
		this.setCalculated(calculated);
	}
	
	protected InputParam() { } // for subclasses
	
	// fields
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setParamType(InputParamType paramType) {
		this.paramType = paramType;
	}

	public InputParamType getParamType() {
		return paramType;
	}

	public void setCalculated(boolean calculated) {
		this.calculated = calculated;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public String[] getOptions() {
		return options;
	}

	public void setOptions(String[] options) {
		this.options = options;
	}

	public enum InputParamType { TEXT, SELECT, CHECKBOX, HIDDEN, FILE, NONE, LINKED, COLLECTION }
	
}