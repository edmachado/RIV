package org.fao.riv.installer.sql;

import javax.xml.bind.annotation.XmlElement;

public class Patch {
	
	protected String query;
	protected String description;
	protected int number = 0;
	
	public Patch() {
		
	}
	
	public Patch(String query, String description) {
		this.query = query;
		this.description = description;
	}
	
	
	@XmlElement(required=true)
	public String getQuery() {
		return query;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}
	
}
