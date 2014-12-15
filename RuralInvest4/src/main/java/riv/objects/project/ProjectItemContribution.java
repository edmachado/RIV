package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

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
	
	@Column(name="CONTRIB_TYPE")
	private Integer contribType;
	@Column(name="YEAR_BEGIN")
	private Integer year;
	@Column
	private String contributor;
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}

	public void setContribType(Integer contribType) {
		this.contribType = contribType;
	}

	public Integer getContribType() {
		return contribType;
	}
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public String getContributor() {
		return contributor;
	}

	public void setContributor(String contributor) {
		this.contributor = contributor;
	}

	public double getTotal() {
		if (getUnitNum()==null || getUnitCost()==null) return 0;
		return this.getUnitCost()*this.getUnitNum();
	}
	
	public String testingProperties(CurrencyFormatter cf) {
		   StringBuilder sb = new StringBuilder();
		   sb.append("step10.contribution."+(this.getOrderBy()+1)+".description="+description+System.lineSeparator());
		   sb.append("step10.contribution."+(this.getOrderBy()+1)+".unitType="+unitType+System.lineSeparator());
		   sb.append("step10.contribution."+(this.getOrderBy()+1)+".unitNum="+unitNum+System.lineSeparator());
		   sb.append("step10.contribution."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step10.contribution."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append("step10.contribution."+(this.getOrderBy()+1)+".contribType="+contribType+System.lineSeparator());
		   sb.append("step10.contribution."+(this.getOrderBy()+1)+".contributor="+contributor+System.lineSeparator());
		   
		   sb.append(System.lineSeparator());
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
	   item.setContribType(contribType);
	   item.setContributor(contributor);
	   item.setOrderBy(getOrderBy());
	   return item;
   }
	 
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemContribution x = (ProjectItemContribution)obj;
		boolean isEqual = contribType.equals(x.contribType)
				&& contributor.equals(x.contributor)
		&&  ((year==null && this.getProjItemId()==x.getProjItemId()) || (year!=null && year==x.getYear()));
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (contribType!=null) { code = multiplier * code + contribType; }	
	    if (year!=null) { code = multiplier * code + year.hashCode(); }
	    if (contributor!=null) {code=multiplier * code + contributor.hashCode(); }
	    return code;
	}
}
