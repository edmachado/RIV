package org.fao.riv.installer.sql;

import java.util.TreeSet;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * XML example
 * 
 * <SQL>
	<version number="1.0">
		ALTER TABLE ... ; 
		UPDATE ... ; 
	</version>
 * </SQL>
 */
@XmlRootElement
public class SQL {
	
	TreeSet<Version> versions;

	public SQL() {
		versions = new TreeSet<Version>(new VersionComparator());
	}
	
	@XmlElement(name="version")
	public TreeSet<Version> getVersions() {
		return versions;
	}

	public void setVersions(TreeSet<Version> versions) {
		this.versions = versions;
	}
	
}