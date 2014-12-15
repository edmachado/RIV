package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

@Entity
@DiscriminatorValue("13")
public class ProjectItemServiceWithout extends ProjectItem implements ProjectInvestment {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	
	private Double donated;
	@Column(name="YEAR_BEGIN")
	private Integer yearBegin;
	@Column(name="OWN_RESOURCES")
	protected Double ownResources;
	
	public void setOwnResources(Double ownResources) {
		this.ownResources = ownResources;
	}

	public Double getOwnResources() {
		return ownResources;
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
	
	public Double getDonated() {
	    return this.donated;
	}

	public void setDonated(Double Donated) {
	    this.donated = Donated;
	}

	public java.lang.Integer getYearBegin() {
	    return this.yearBegin;
	}

	public void setYearBegin(java.lang.Integer YearBegin) {
	    this.yearBegin = YearBegin;
	}

	public Double getFinanced() {
		   if (getOwnResources()==null || donated==null) return 0.0;
		   return (getTotal() - getOwnResources() - this.donated);
	}
	
	public String testingProperties(CurrencyFormatter cf) {
		   StringBuilder sb = new StringBuilder();
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".description="+description+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".unitType="+unitType+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".unitNum="+unitNum+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(ownResources, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".donated="+cf.formatCurrency(donated, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".financed="+cf.formatCurrency(getFinanced(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step7.serviceWo."+(this.getOrderBy()+1)+".yearBegin="+yearBegin+System.lineSeparator());
		   return sb.toString();
	   }

	@Override
	 public ProjectItemServiceWithout copy() {
		ProjectItemServiceWithout item = new ProjectItemServiceWithout();
	   item.setDescription(description);
	   //item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(project);
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setOwnResources(ownResources);
	   item.setDonated(donated);
	   item.setYearBegin(yearBegin);
	   
	   item.setOrderBy(getOrderBy());
	   return item;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemServiceWithout x = (ProjectItemServiceWithout)obj;
		boolean isEqual = ownResources.equals(x.ownResources) &&
			donated.equals(x.donated) &&
			yearBegin.equals(x.yearBegin);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (ownResources!=null) code = multiplier * code + ownResources.intValue();	   
	    if (donated!=null) code = multiplier * code + donated.intValue();	   
	    if (yearBegin!=null) code = multiplier * code + yearBegin;	    
	    return code;
	}

}
