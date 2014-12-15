package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.objects.OrderByable;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

/**
 * A production block (income-generating) or activity (non-income-generating) of a Project.
 *
 */
@Entity
@DiscriminatorValue("0")
public class Block extends BlockBase {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	private Project project;
	
	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	public String testingProperties(CurrencyFormatter cf) {
		   StringBuilder sb = new StringBuilder();
		   
		   sb.append("step9.block."+(this.getOrderBy()+1)+".description="+this.getDescription()+System.lineSeparator());
		   sb.append("step9.block."+(this.getOrderBy()+1)+".unitType="+this.getUnitType()+System.lineSeparator());
		   sb.append("step9.block."+(this.getOrderBy()+1)+".cycleLength="+this.getCycleLength()+System.lineSeparator());
		   sb.append("step9.block."+(this.getOrderBy()+1)+".lengthUnit="+this.getLengthUnit()+System.lineSeparator());
		   sb.append("step9.block."+(this.getOrderBy()+1)+".cyclePerYear="+this.getCyclePerYear()+System.lineSeparator());
		   sb.append("step9.block."+(this.getOrderBy()+1)+".cycleFirstYear="+this.getCycleFirstYear()+System.lineSeparator());
		   sb.append("step9.block."+(this.getOrderBy()+1)+".cycleFirstYearIncome="+this.getCycleFirstYearIncome()+System.lineSeparator());
		   
		   for (int type=0;type<3;type++) {
			   for (int month=1;month<=12;month++) {
				   for (int half=0;half<=1;half++) {
					   String key=type+"-"+month+"-"+half;
					   sb.append("step9.block."+(this.getOrderBy()+1)+".ch"+key+"="+(this.getChrons().containsKey(key)?"true":"false")+System.lineSeparator());
				   }
			   }
		   }
		   for (int i=1;i<=getProject().getDuration();i++) {
			   sb.append("step9.block."+(this.getOrderBy()+1)+".pat"+i+"="+(this.getPatterns().containsKey(i)?"true":"false")+System.lineSeparator());
		   }
		   
//		   for (BlockIncome i : this.getIncomes()) {
//			   sb.append(i.testingProperties(cf));
//		   }
//		   sb.append(System.lineSeparator());
//		   
//		   for (BlockInput i : this.getInputs()) {
//			   sb.append(i.testingProperties(cf));
//		   }
//		   sb.append(System.lineSeparator());
//		   
//		   for (BlockLabour i : this.getLabours()) {
//			   sb.append(i.testingProperties(cf));
//		   }
//		   sb.append(System.lineSeparator());
		   
		   return sb.toString();
	   }


	@Override
	public int hashCode() {
		int hash = 0;	
		if (getDescription()!=null)
			hash = getDescription().hashCode();
		return hash;
	}


	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		int compare = this.getOrderBy() - i.getOrderBy();
		if(compare == 0) return 1;
		return compare; 
	}
	
}