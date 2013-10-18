package org.fao.riv.installer.sql;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAttribute;

public class Version {
	private TreeSet<Patch> patches;
	private String query;
	private String number;
	private int major = 0;
	private int minor = 0;

	public Version() {
		patches = new TreeSet<Patch>(new PatchComparator());
	}
	
	public TreeSet<Patch> getPatches() {
		return patches;
	}

	public void setPatches(TreeSet<Patch> patches) {
		this.patches = patches;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	@XmlAttribute(required=true)
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
		
		String[] majorMinor = number.split("\\.");
		this.major = Integer.valueOf(majorMinor[0]);
		this.minor = Integer.valueOf(majorMinor[1]);
		
	}
	
	public int getMajor() {
		return this.major;
	}
	
	public int getMinor() {
		return this.minor;
	}
	
	public double getVersionNumber() {
		return major+minor*.1;
	}
	
	public boolean hasPatches() {
		boolean patched = false;
		if (patches != null && patches.size() > 0) {
			patched = true;
		}
		return patched;
	}
}
