package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Contribution item used by non-income-generating projects.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("5")
public class ProjectItemContribution extends ProjectItem {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	
	@Column(name="CONTRIB_TYPE")
	private Integer contribType;
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}

	public void setContribType(Integer contribType) {
		this.contribType = contribType;
	}

	public Integer getContribType() {
		return contribType;
	}
	public double getTotal() {
		if (getUnitNum()==null || getUnitCost()==null) return 0;
		return this.getUnitCost()*this.getUnitNum();
	}
	
	 @Override
	 public ProjectItemContribution copy() {
	 ProjectItemContribution item = new ProjectItemContribution();
	   item.setDescription(description);
	   ///item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(project);
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setContribType(contribType);
	   
	   item.setOrderBy(getOrderBy());
	   return item;
   }
	 
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemContribution x = (ProjectItemContribution)obj;
		boolean isEqual = contribType.equals(x.contribType);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (contribType!=null) code = multiplier * code + contribType;	    
	    return code;
	}
}
