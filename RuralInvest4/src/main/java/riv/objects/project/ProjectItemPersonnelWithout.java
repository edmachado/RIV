package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

@Entity
@DiscriminatorValue("7")
public class ProjectItemPersonnelWithout extends ProjectItemGeneralBase  {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;

	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}
	
	@Override
	public ProjectItemPersonnelWithout copy() {
		return (ProjectItemPersonnelWithout) super.copy(ProjectItemPersonnelWithout.class);
	}

	
	 public String testingProperties(RivConfig rivConfig) {
			String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".unitType="+rivConfig.getLabourTypes().get(unitType)+lineSeparator);
//		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
//		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
//		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(OwnResources, CurrencyFormat.ALL)+lineSeparator);
//		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".external="+cf.formatCurrency(getExternal(), CurrencyFormat.ALL)+lineSeparator);
		   
		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(this.getYears().get(0).getUnitNum())+lineSeparator);
		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(this.getYears().get(0).getOwnResources(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(this.getYears().get(0).getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step8.personnelWo."+(this.getOrderBy()+1)+".external="+cf.formatCurrency(this.getYears().get(0).getExternal(), CurrencyFormat.ALL)+lineSeparator);

		   return sb.toString();
	   }
}