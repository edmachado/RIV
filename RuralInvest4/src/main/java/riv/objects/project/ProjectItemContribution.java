package riv.objects.project;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

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
public class ProjectItemContribution extends ProjectItem {
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	
	@Column(name="YEAR_BEGIN")
	private Integer year;
//	@ManyToOne(fetch=FetchType.LAZY)
//	@JoinColumn(name="DONOR_ID")
//	private Donor donor;
	@Column(name="DONOR_ORDER_BY")
	private int donorOrderBy;
	
	@Transient // only for importing from previous versions
	private String oldDonor;
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public int getDonorOrderBy() {
		return donorOrderBy;
	}

	public void setDonorOrderBy(int donorOrderBy) {
		this.donorOrderBy = donorOrderBy;
	}

//	public Donor getDonor() {
//		return donor;
//	}
//
//	public void setDonor(Donor donor) {
//		this.donor = donor;
//	}

	public String getOldDonor() {
		return oldDonor;
	}

	public void setOldDonor(String oldDonor) {
		this.oldDonor = oldDonor;
	}

	public double getTotal() {
		if (getUnitNum()==null || getUnitCost()==null) return 0;
		return this.getUnitCost()*this.getUnitNum();
	}
	
	public String testingProperties(RivConfig rivConfig) {
		Map<Integer, Donor> donorsByOrder = new HashMap<Integer, Donor>();
		for (Donor d : getProject().getDonors()) {
			donorsByOrder.put(d.getOrderBy(), d);
		}
		
		String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".donorOrderBy="+donorsByOrder.get(donorOrderBy).getDescription()+lineSeparator);
//		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".contribType="+rivConfig.getContribTypes().get(contribType)+lineSeparator);
//		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".contributor="+contributor+lineSeparator);
		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step10.year."+year+".contribution."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   return sb.toString();
	   }
	
	 @Override
	 public ProjectItemContribution copy() {
	 ProjectItemContribution item = new ProjectItemContribution();
	 	item.setYear(year);
	   item.setDescription(description);
	   ///item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(project);
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setDonorOrderBy(donorOrderBy);
//	   item.setDonor(donor);
//	   item.setContribType(contribType);
//	   item.setContributor(contributor);
	   item.setOrderBy(getOrderBy());
	   return item;
   }
	 
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemContribution x = (ProjectItemContribution)obj;
		boolean isEqual = 
//		contribType.equals(x.contribType)
//				&& contributor.equals(x.contributor) &&
		((year==null && this.getProjItemId()==x.getProjItemId()) || (year!=null && year==x.getYear()));
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
//	    if (contribType!=null) { code = multiplier * code + contribType; }	
	    if (year!=null) { code = multiplier * code + year.hashCode(); }
//	    if (contributor!=null) {code=multiplier * code + contributor.hashCode(); }
	    return code;
	}
}
