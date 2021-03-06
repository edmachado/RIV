package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import riv.objects.project.ProjectItemNongenBase;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;
/**
 * Labour cost associated to a project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("8")
public class ProjectItemNongenLabour extends ProjectItemNongenBase {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJECT_ID", nullable=false)
	private Project project;
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}
	
	public String testingProperties(RivConfig rivConfig) {
		String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step8.labour"+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step8.labour"+(this.getOrderBy()+1)+".unitType="+rivConfig.getLabourTypes().get(unitType)+lineSeparator);
		   sb.append("step8.labour"+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step8.labour"+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.labour"+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.labour"+(this.getOrderBy()+1)+".ownResource="+cf.formatCurrency(getOwnResource(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.labour"+(this.getOrderBy()+1)+".donated="+cf.formatCurrency(getDonated(), CurrencyFormat.ALL)+lineSeparator);
		   for (int i=0;i<getProject().getDonors().size();i++) {
			   sb.append("step8.labour"+(this.getOrderBy()+1)+".donations."+(i+1)+"="+cf.formatCurrency(this.getDonations().containsKey(i) ? this.getDonations().get(i) : 0.0, CurrencyFormat.ALL)+lineSeparator);
		   }
		   return sb.toString();
	   }
	
	@Override
	 public ProjectItemNongenLabour copy() {
		ProjectItemNongenLabour item = new ProjectItemNongenLabour();
	   item.setDescription(description);
	   item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(getProject());
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setOrderBy(this.getOrderBy());
	   for (Integer donorOrder : this.getDonations().keySet()) {
		  item.getDonations().put(donorOrder, this.getDonations().get(donorOrder));
	   }
	   return item;
 }
}
