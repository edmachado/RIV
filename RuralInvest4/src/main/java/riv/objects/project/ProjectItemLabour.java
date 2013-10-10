package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
/**
 * Labour cost associated to a project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("1")
public class ProjectItemLabour extends ProjectItem implements ProjectInvestment {
	private static final long serialVersionUID = 1L;


	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	@Column(name="OWN_RESOURCES")
	protected Double OwnResources;
	private Double Donated;
	@Column(name="YEAR_BEGIN")
	private java.lang.Integer YearBegin;
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}
	
	public void setOwnResources(Double ownResources) {
		OwnResources = ownResources;
	}

	public Double getOwnResources() {
		return OwnResources;
	}

	public Double getTotal() {
		if (getUnitCost()==null || getUnitNum()==null) return 0.0;
		return this.getUnitCost()*this.getUnitNum();
	}
	
	public Double getDonated() {
	    return this.Donated;
	}

	public void setDonated(Double Donated) {
	    this.Donated = Donated;
	}

	public java.lang.Integer getYearBegin() {
	    return this.YearBegin;
	}

	public void setYearBegin(java.lang.Integer YearBegin) {
	    this.YearBegin = YearBegin;
	}

	public Double getFinanced() {
		   if (getOwnResources()==null || Donated==null) return 0.0;
		   return (getTotal() - getOwnResources() - this.Donated);
	   }
	
	@Override
	 public ProjectItemLabour copy() {
		ProjectItemLabour item = new ProjectItemLabour();
	   item.setDescription(description);
	   //item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(project);
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setOwnResources(OwnResources);
	   item.setDonated(Donated);
	   item.setYearBegin(YearBegin);
	   
	   item.setOrderBy(this.getOrderBy());
	   return item;
 }
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemLabour x = (ProjectItemLabour)obj;
		boolean isEqual = OwnResources.equals(x.OwnResources) &&
			Donated.equals(x.Donated) &&
			YearBegin.equals(x.YearBegin);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (OwnResources!=null) code = multiplier * code + OwnResources.intValue();	   
	    if (Donated!=null) code = multiplier * code + Donated.intValue();	   
	    if (YearBegin!=null) code = multiplier * code + YearBegin;	    
	    return code;
	}
}
