package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;
/**
 * Labour cost associated to a project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("10")
public class ProjectItemNongenMaintenance extends ProjectItemNongenBase {
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
		   sb.append("step8.general"+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step8.general"+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   sb.append("step8.general"+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step8.general"+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.general"+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.general"+(this.getOrderBy()+1)+".ownResource="+cf.formatCurrency(getOwnResource(), CurrencyFormat.ALL)+lineSeparator);
//		   sb.append("step8.general"+(this.getOrderBy()+1)+".statePublic="+cf.formatCurrency(getStatePublic(), CurrencyFormat.ALL)+lineSeparator);
//		   sb.append("step8.general"+(this.getOrderBy()+1)+".other1="+cf.formatCurrency(getOther1(), CurrencyFormat.ALL)+lineSeparator);
		   return sb.toString();
	   }
	
	@Override
	 public ProjectItemNongenMaintenance copy() {
		ProjectItemNongenMaintenance item = new ProjectItemNongenMaintenance();
	   item.setDescription(description);
	   item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(getProject());
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
//	   item.setStatePublic(getStatePublic());
//	   item.setOther1(getOther1());
	   item.setOrderBy(this.getOrderBy());
	   for (Integer donorOrder : donations.keySet()) {
		  item.getDonations().put(donorOrder, donations.get(donorOrder));
	   }
	   return item;
 }
}
