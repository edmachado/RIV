package riv.objects.project;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

/**
 * Contribution item used by non-income-generating projects.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("5")
public class ProjectItemContribution extends HasPerYearItems<ProjectItemContributionPerYear> {
	static final Logger LOG = LoggerFactory.getLogger(HasPerYearItems.class);
	private static final long serialVersionUID = 1L;
	

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	
	@OneToMany(mappedBy="parent", orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@MapKey(name="year")
	private Map<Integer, ProjectItemContributionPerYear> years = new HashMap<Integer, ProjectItemContributionPerYear>();
	public Map<Integer, ProjectItemContributionPerYear> getYears() {
		return years;
	}
	public void setYears(Map<Integer, ProjectItemContributionPerYear> years) {
		this.years = years;
	}
	
	@Column(name="DONOR_ORDER_BY")
	private int donorOrderBy;
	
	@Transient // only for importing from previous versions
	private String oldDonor;
	
	public void addYears(int years) {
		this.setYears(new HashMap<Integer, ProjectItemContributionPerYear>());
		for (int i=0;i<years;i++) {
			try {
				ProjectItemContributionPerYear py = new ProjectItemContributionPerYear();
				py.setYear(i);
				py.setParent(this);
				this.getYears().put(i, py);
			} catch (Exception e) {
				LOG.warn("Error adding years.", e);
			}
		}
	}

	@Override
	public Double getUnitNum() {
		throw new UnsupportedOperationException("UnitNum should be called on a specific year of a contribution.");
	}
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}

	public int getDonorOrderBy() {
		return donorOrderBy;
	}

	public void setDonorOrderBy(int donorOrderBy) {
		this.donorOrderBy = donorOrderBy;
	}

	public String getOldDonor() {
		return oldDonor;
	}

	public void setOldDonor(String oldDonor) {
		this.oldDonor = oldDonor;
	}

	
	public String testingProperties(RivConfig rivConfig) {
		Map<Integer, Donor> donorsByOrder = new HashMap<Integer, Donor>();
		for (Donor d : getProject().getDonors()) {
			donorsByOrder.put(d.getOrderBy(), d);
		}
		
		String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder(); 
		   int order=this.getOrderBy()+1;
		   
		   sb.append("step10.contribution."+order+".description="+description+lineSeparator);
		   String desc = donorsByOrder.get(donorOrderBy).getNotSpecified()?"Not specified":donorsByOrder.get(donorOrderBy).getDescription();
		   sb.append("step10.contribution."+order+".donorOrderBy="+desc+lineSeparator);
		   sb.append("step10.contribution."+order+".unitType="+unitType+lineSeparator);
		   sb.append("step10.contribution."+order+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   
		   for (ProjectItemContributionPerYear py : getYears().values()) {
			   sb.append("step10.contribution."+order+".year."+py.getYear()+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(py.getUnitNum())+lineSeparator);
			   sb.append("step10.contribution."+order+".year."+py.getYear()+".total="+cf.formatCurrency(py.getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   }
		   
		   return sb.toString();
	   }
	
	 @Override
	 public ProjectItemContribution copy() {
	 ProjectItemContribution item = new ProjectItemContribution();
	 	item.setDescription(description);
	 	item.setLinkedTo(getLinkedTo());
	 	item.setProject(project);
	 	item.setUnitCost(unitCost);
	 	item.setUnitType(unitType);
	 	item.setDonorOrderBy(donorOrderBy);
	 	item.setOrderBy(getOrderBy());
	 	item.setYears(new HashMap<Integer, ProjectItemContributionPerYear>());
	 	for (ProjectItemContributionPerYear oldYear : this.getYears().values()) {
	 		ProjectItemContributionPerYear newYear = new ProjectItemContributionPerYear();
			   newYear.setUnitNum(oldYear.getUnitNum());
			   newYear.setYear(oldYear.getYear());
			   newYear.setParent(item);
			   item.getYears().put(newYear.getYear(), newYear);
	 	}
	 	
	 	return item;
	 }
	 
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemContribution x = (ProjectItemContribution)obj;
		return x.donorOrderBy==this.donorOrderBy;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    code = multiplier * code + donorOrderBy;
	    return code;
	}
	
	@Override
	public void convertCurrency(Double exchange, int scale) {
		unitCost = project.round(unitCost*exchange, scale);
	}

}
