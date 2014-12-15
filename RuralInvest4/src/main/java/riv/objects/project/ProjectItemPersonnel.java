package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

@Entity
@DiscriminatorValue("3")
public class ProjectItemPersonnel extends ProjectItem {// implements GeneralCosts { // GeneralCostsDetail

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	
	@Column(name="OWN_RESOURCES")
	protected Double OwnResources;
	
	public void setOwnResources(Double ownResources) {
		OwnResources = ownResources;
	}

	public Double getOwnResources() {
		return OwnResources;
	}
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}

	public Double getTotal() {
		if (getUnitCost()==null || getUnitNum()==null) return 0.0;
		return this.getUnitCost()*this.getUnitNum();
	}
	
	public Double getExternal() {
		if (getOwnResources()==null) return 0.0;
		return getTotal() - getOwnResources();
	}
	
	 public String testingProperties(RivConfig rivConfig) {
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step8.personnel."+(this.getOrderBy()+1)+".description="+description+System.lineSeparator());
		   sb.append("step8.personnel."+(this.getOrderBy()+1)+".unitType="+rivConfig.getLabourTypes().get(unitType)+System.lineSeparator());
		   sb.append("step8.personnel."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+System.lineSeparator());
		   sb.append("step8.personnel."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step8.personnel."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step8.personnel."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(OwnResources, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step8.personnel."+(this.getOrderBy()+1)+".external="+cf.formatCurrency(getExternal(), CurrencyFormat.ALL)+System.lineSeparator());
		   return sb.toString();
	   }
	
	@Override
	 public ProjectItemPersonnel copy() {
		ProjectItemPersonnel item = new ProjectItemPersonnel();
	   item.setDescription(description);
	  // item.setExportLinkedTo(exportLinkedTo);
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
		ProjectItemPersonnel x = (ProjectItemPersonnel)obj;
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
	
}

