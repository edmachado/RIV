package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.Entity;

import riv.web.config.RivConfig;
@Entity
public abstract class ProjectItemGeneralFromProfileBase extends ProjectItem {
	private static final long serialVersionUID = 1L;

	@Column(name="OWN_RESOURCES")
	private Double OwnResources;
	
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

	public Double getFinanced() {
		   if (getOwnResources()==null ) return 0.0;
		   return (getTotal() - getOwnResources() );
	   }
	
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemGeneralFromProfileBase x = (ProjectItemGeneralFromProfileBase)obj;
		boolean isEqual = OwnResources.equals(x.OwnResources);
//			Donated.equals(x.Donated) &&
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (OwnResources!=null) code = multiplier * code + OwnResources.intValue();	   
//	    if (Donated!=null) code = multiplier * code + Donated.intValue();	 
	    return code;
	}

	@Override
	public String testingProperties(RivConfig rivConfig) {
		// should not be necessary
		return null;
	}

	@Override
	public void convertCurrency(Double exchange, int scale) {
		// should not be necessary
	}
}
