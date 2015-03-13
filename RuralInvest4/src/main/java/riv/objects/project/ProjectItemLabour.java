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
	@Column(name="YEAR_BEGIN")
	private java.lang.Integer YearBegin;
	
	@Formula(value="(SELECT ISNULL(SUM(d.amount),0) FROM project_item_donation d WHERE d.item_id=proj_item_id)")
	private Double donated;
	public Double getDonated() {
		return donated;
	}
	
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyColumn(name="donor_order_by")
	@Column(name="amount")
	@CollectionTable(name="PROJECT_ITEM_DONATION", joinColumns=@JoinColumn(name="item_id"))
	Map<Integer,Double> donations = new HashMap<Integer,Double>();
	
	public Map<Integer,Double> getDonations() { return donations; }
	
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
	
//	public Double getDonated() {
//	    return this.Donated;
//	}
//
//	public void setDonated(Double Donated) {
//	    this.Donated = Donated;
//	}

	public java.lang.Integer getYearBegin() {
	    return this.YearBegin;
	}

	public void setYearBegin(java.lang.Integer YearBegin) {
	    this.YearBegin = YearBegin;
	}

	public Double getFinanced() {
		   if (getOwnResources()==null || getDonated()==null) return 0.0;
		   return (getTotal() - getOwnResources() - getDonated());
	   }
	
	   public String testingProperties(RivConfig rivConfig) {
			String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".unitType="+rivConfig.getLabourTypes().get(unitType.toString())+lineSeparator);
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(OwnResources, CurrencyFormat.ALL)+lineSeparator);
		  // sb.append("step7.labour."+(this.getOrderBy()+1)+".donated="+cf.formatCurrency(Donated, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".financed="+cf.formatCurrency(getFinanced(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.labour."+(this.getOrderBy()+1)+".yearBegin="+YearBegin+lineSeparator);
		   return sb.toString();
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
//	   item.setDonated(Donated);
	   item.setYearBegin(YearBegin);
	   
	   item.setOrderBy(this.getOrderBy());
	   return item;
 }
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemLabour x = (ProjectItemLabour)obj;
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
}
