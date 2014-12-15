package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

/**
 * General cost associated to a project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("6")
public class ProjectItemGeneralWithout extends ProjectItem {//implements GeneralCosts {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	@Column(name="OWN_RESOURCES")	
	protected Double OwnResources;

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
	public Double getExternal() {
		if (getOwnResources()==null) return 0.0;
		return getTotal() - getOwnResources();
	}
	
	 public String testingProperties(CurrencyFormatter cf) {
		   StringBuilder sb = new StringBuilder();
		   sb.append("step8.supplyWo."+(this.getOrderBy()+1)+".description="+description+System.lineSeparator());
		   sb.append("step8.supplyWo."+(this.getOrderBy()+1)+".unitType="+unitType+System.lineSeparator());
		   sb.append("step8.supplyWo."+(this.getOrderBy()+1)+".unitNum="+unitNum+System.lineSeparator());
		   sb.append("step8.supplyWo."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step8.supplyWo."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step8.supplyWo."+(this.getOrderBy()+1)+".external="+cf.formatCurrency(getExternal(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append(System.lineSeparator());
		   return sb.toString();
	   }
	 
	@Override
	 public ProjectItemGeneralWithout copy() {
		 ProjectItemGeneralWithout item = new ProjectItemGeneralWithout();
	   item.setDescription(description);
	   //item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(project);
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
		ProjectItemGeneralWithout x = (ProjectItemGeneralWithout)obj;
		boolean isEqual = OwnResources.equals(x.OwnResources);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (OwnResources!=null)code = multiplier * code + OwnResources.intValue();	    
	    return code;
	}
}
