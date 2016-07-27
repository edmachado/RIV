package riv.objects.project;

public class ProjectItemGeneralSingleYear {
	private int projItemId;
	private String description;
	private String unitType;
	private Double unitNum;
	private Double unitCost;
	private Double totalCost;
	private Double ownResources;
	private Double external;
	private boolean linked;
	
	public int getProjItemId() {
		return projItemId;
	}
	public void setProjItemId(int projItemId) {
		this.projItemId = projItemId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUnitType() {
		return unitType;
	}
	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}
	public Double getUnitNum() {
		return unitNum;
	}
	public void setUnitNum(Double unitNum) {
		this.unitNum = unitNum;
	}
	public Double getUnitCost() {
		return unitCost;
	}
	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}
	public Double getTotal() {
		return totalCost;
	}
	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}
	public Double getOwnResources() {
		return ownResources;
	}
	public void setOwnResources(Double ownResources) {
		this.ownResources = ownResources;
	}
	public Double getExternal() {
		return external;
	}
	public void setExternal(Double external) {
		this.external = external;
	}
	public boolean isLinked() {
		return linked;
	}
	public void setLinked(boolean linked) {
		this.linked = linked;
	}
}
