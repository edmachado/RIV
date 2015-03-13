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
@DiscriminatorValue("9")
public class ProjectItemNongenMaterials extends ProjectItemNongenBase {
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
		   sb.append("step8.input"+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step8.input"+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   sb.append("step8.input"+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step8.input"+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.input"+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.input"+(this.getOrderBy()+1)+".ownResource="+cf.formatCurrency(getOwnResource(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.input."+(this.getOrderBy()+1)+".donated="+cf.formatCurrency(getDonated(), CurrencyFormat.ALL)+lineSeparator);
		   for (int i=0;i<getProject().getDonors().size();i++) {
			   sb.append("step8.input."+(this.getOrderBy()+1)+".donations."+(i+1)+"="+cf.formatCurrency(donations.containsKey(i) ? donations.get(i) : 0.0, CurrencyFormat.ALL)+lineSeparator);
		   }
		   
//		   sb.append("step8.input"+(this.getOrderBy()+1)+".statePublic="+cf.formatCurrency(getStatePublic(), CurrencyFormat.ALL)+lineSeparator);
//		   sb.append("step8.input"+(this.getOrderBy()+1)+".other1="+cf.formatCurrency(getOther1(), CurrencyFormat.ALL)+lineSeparator);
		   return sb.toString();
	   }
	
	@Override
	 public ProjectItemNongenMaterials copy() {
		ProjectItemNongenMaterials item = new ProjectItemNongenMaterials();
	   item.setDescription(description);
	   item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(getProject());
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
//	   item.setStatePublic(getStatePublic());
//	   item.setOther1(getOther1());
	   item.setOrderBy(getOrderBy());
	   for (Integer donorOrder : donations.keySet()) {
		  item.getDonations().put(donorOrder, donations.get(donorOrder));
	   }
	   return item;
 }
}
