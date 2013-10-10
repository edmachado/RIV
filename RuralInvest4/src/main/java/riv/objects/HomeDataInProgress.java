package riv.objects;

import java.util.Date;

public class HomeDataInProgress {
	private String name;
	private boolean isProject;
	private Date lastModified;
	private int id;
	//private String technician;
	
	public HomeDataInProgress(String name, boolean isProject, Date lastModified, int id) {//, String technician) {
		this.name = name;
		this.isProject = isProject;
		this.lastModified = lastModified;
		this.id = id;
		//this.technician = technician;
	}
	
	public String getName() {
		return name;
	}

	public boolean isProject() {
		return isProject;
	}

	public java.util.Date getLastModified() {
		return lastModified;
	}

	public int getId() {
		return id;
	}
	
	/*public String getTechnician() {
		return technician;
	}*/
}