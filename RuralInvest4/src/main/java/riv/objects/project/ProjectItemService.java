package riv.objects.project;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.Formula;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

@Entity
@DiscriminatorValue("4")
public class ProjectItemService extends ProjectItem implements ProjectInvestment {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	@Column(name="YEAR_BEGIN")
	private Integer yearBegin;
	@Column(name="OWN_RESOURCES")
	protected Double ownResources;
	
	@Formula(value="(SELECT ISNULL(SUM(d.amount),0) FROM project_item_donation d WHERE d.item_id=proj_item_id)")
	private Double donated;
	public Double getDonated() {
		return donated;
	}
	
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyColumn(name="donor_id")
	@Column(name="amount")
	@CollectionTable(name="PROJECT_ITEM_DONATION", joinColumns=@JoinColumn(name="item_id"))
	Map<Integer,Double> donations = new HashMap<Integer,Double>();
	
	public Map<Integer,Double> getDonations() { return donations; }
	
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

	public java.lang.Integer getYearBegin() {
	    return this.yearBegin;
	}

	public void setYearBegin(java.lang.Integer YearBegin) {
	    this.yearBegin = YearBegin;
	}

	public Double getFinanced() {
		   if (getOwnResources()==null || getDonated()==null) return 0.0;
		   return (getTotal() - getOwnResources() - getDonated());
	}
	
	 public String testingProperties(RivConfig rivConfig) {
			String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step7.service."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step7.service."+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   sb.append("step7.service."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step7.service."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.service."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.service."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(ownResources, CurrencyFormat.ALL)+lineSeparator);
		  // sb.append("step7.service."+(this.getOrderBy()+1)+".donated="+cf.formatCurrency(donated, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.service."+(this.getOrderBy()+1)+".financed="+cf.formatCurrency(getFinanced(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.service."+(this.getOrderBy()+1)+".yearBegin="+yearBegin+lineSeparator);
		   return sb.toString();
	   }

	@Override
	 public ProjectItemService copy() {
		ProjectItemService item = new ProjectItemService();
	   item.setDescription(description);
	   //item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(project);
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setOwnResources(ownResources);
//	   item.setDonated(donated);
	   item.setYearBegin(yearBegin);
	   
	   item.setOrderBy(getOrderBy());
	   return item;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemService x = (ProjectItemService)obj;
		boolean isEqual = ownResources.equals(x.ownResources) &&
//			donated.equals(x.donated) &&
			yearBegin.equals(x.yearBegin);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (ownResources!=null) code = multiplier * code + ownResources.intValue();	   
//	    if (donated!=null) code = multiplier * code + donated.intValue();	   
	    if (yearBegin!=null) code = multiplier * code + yearBegin;	    
	    return code;
	}

}
