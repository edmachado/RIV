package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.Entity;
/**
 * Base class for General cost associated with a project
 * @author Bar Zecharya
 *
 */
@Entity
public abstract class ProjectItemGeneralBase extends ProjectItem {

	private static final long serialVersionUID = 1L;

	@Column(name="OWN_RESOURCES")
	protected Double OwnResources;

	public void setOwnResources(Double ownResources) {
		OwnResources = ownResources;
	}

	public Double getOwnResources() {
		return OwnResources;
	}
	
	public abstract Project getProject();
	public abstract void setProject (Project Project);

	public Double getTotal() {
		if (getUnitCost()==null || getUnitNum()==null) return 0.0;
		return this.getUnitCost()*this.getUnitNum();
	}
	public Double getExternal() {
		if (getOwnResources()==null) return 0.0;
		return (getTotal()) - getOwnResources();
	}
	
	 protected ProjectItemGeneralBase copy(Class<? extends ProjectItemGeneralBase> newClass) {
	 ProjectItemGeneralBase item = newClass.isAssignableFrom(ProjectItemGeneral.class) ? new ProjectItemGeneral() : new ProjectItemGeneralWithout();
	   item.setDescription(description);
	   item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(this.getProject());
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setOwnResources(OwnResources);
	   
	   item.setOrderBy(getOrderBy());
	   return item;
   }
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemGeneralBase x = (ProjectItemGeneralBase)obj;
		boolean isEqual = OwnResources.equals(x.OwnResources);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (OwnResources!=null) code = multiplier * code + OwnResources.intValue();	    
	    return code;
	}
	
	@Override
	public void convertCurrency(Double exchange, int scale) {
		this.setOwnResources(this.getProject().round(this.getOwnResources()*exchange, scale));
		this.setUnitCost(this.getProject().round(this.getUnitCost()*exchange, scale));
	}
}
