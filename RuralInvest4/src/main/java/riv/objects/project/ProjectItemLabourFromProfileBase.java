package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.Entity;

import riv.web.config.RivConfig;
@Entity
public abstract class ProjectItemLabourFromProfileBase extends ProjectItem {
	private static final long serialVersionUID = 1L;


	
	@Column(name="OWN_RESOURCES")
	private Double OwnResources;
	@Column(name="YEAR_BEGIN")
	private java.lang.Integer YearBegin;
	
	
	

	
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
	
	public java.lang.Integer getYearBegin() {
	    return this.YearBegin;
	}

	public void setYearBegin(java.lang.Integer YearBegin) {
	    this.YearBegin = YearBegin;
	}

	public Double getFinanced() {
		   if (getOwnResources()==null ) return 0.0;
		   return (getTotal() - getOwnResources() );
	   }
	
	   
	
//	@Override
//	 public ProjectItemLabourFromProfileBase copy() {
//		ProjectItemLabourFromProfileBase item = new ProjectItemLabourFromProfileBase();
//	   item.setDescription(description);
//	   //item.setExportLinkedTo(exportLinkedTo);
//	   item.setLinkedTo(getLinkedTo());
//	   item.setProject(project);
//	   item.setUnitCost(unitCost);
//	   item.setUnitNum(unitNum);
//	   item.setUnitType(unitType);
//	   item.setOwnResources(OwnResources);
//	   item.setYearBegin(YearBegin);
//	   item.setOrderBy(this.getOrderBy());
//	   return item;
// }
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemLabourFromProfileBase x = (ProjectItemLabourFromProfileBase)obj;
		boolean isEqual = OwnResources.equals(x.OwnResources) &&
//			Donated.equals(x.Donated) &&
			YearBegin.equals(x.YearBegin);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (OwnResources!=null) code = multiplier * code + OwnResources.intValue();	   
//	    if (Donated!=null) code = multiplier * code + Donated.intValue();	   
	    if (YearBegin!=null) code = multiplier * code + YearBegin;	    
	    return code;
	}

	@Override
	public String testingProperties(RivConfig rivConfig) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void convertCurrency(Double exchange, int scale) {
		// TODO Auto-generated method stub
		
	}
}
