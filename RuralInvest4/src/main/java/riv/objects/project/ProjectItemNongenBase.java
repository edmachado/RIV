package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.Entity;
/**
 * Base implementation for general cost associated to an NIG project
 * @author Bar Zecharya
 *
 */
@Entity
public abstract class ProjectItemNongenBase extends ProjectItem {
	private static final long serialVersionUID = 1L;

	@Column(name="OWN_RESOURCES")
	private Double publicState;
	@Column(name="DONATED")
	private Double other1;
	
	public abstract Project getProject();
	public abstract void setProject(Project project);
	
	public Double getStatePublic() {
		return publicState;
	}

	public void setStatePublic(Double publicState) {
		this.publicState = publicState;
	}

	public Double getOther1() {
		return other1;
	}

	public void setOther1(Double other1) {
		this.other1 = other1;
	}

	public Double getTotal() {
		if (getUnitCost()==null || getUnitNum()==null) return 0.0;
		return this.getUnitCost()*this.getUnitNum();
	}
	
	
	
	public Double getOwnResource() {
		   if (publicState==null || other1==null) return 0.0;
		   return (getTotal() - publicState - other1);
	   }
	
	public abstract ProjectItemNongenBase copy();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((other1 == null) ? 0 : other1.hashCode());
		result = prime * result
				+ ((publicState == null) ? 0 : publicState.hashCode());
		result = prime * result + ((this.getProject() == null) ? 0 : this.getProject().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectItemNongenBase other = (ProjectItemNongenBase) obj;
		if (other1 == null) {
			if (other.other1 != null)
				return false;
		} else if (!other1.equals(other.other1))
			return false;
		if (publicState == null) {
			if (other.publicState != null)
				return false;
		} else if (!publicState.equals(other.publicState))
			return false;
		if (this.getProject() == null) {
			if (other.getProject() != null)
				return false;
		} else if (!getProject().equals(other.getProject()))
			return false;
		return true;
	}
	
	
}
