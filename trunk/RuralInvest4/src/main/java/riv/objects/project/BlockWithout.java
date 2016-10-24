package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.objects.OrderByable;

/**
 * A production block (income-generating) or activity (non-income-generating) of a Project.
 *
 */
@Entity
@DiscriminatorValue("1")
public class BlockWithout extends BlockBase {
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
	
	@Override
	public String getPropertiesType() {
		return "blockWo";
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