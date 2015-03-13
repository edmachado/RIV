package riv.objects.project;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.Formula;

import riv.objects.HasDonations;
/**
 * Base implementation for general cost associated to an NIG project
 * @author Bar Zecharya
 *
 */
@Entity
public abstract class ProjectItemNongenBase extends ProjectItem implements HasDonations {
	private static final long serialVersionUID = 1L;
	
	@Formula(value="(SELECT ISNULL(SUM(d.amount),0) FROM project_item_donation d WHERE d.item_id=proj_item_id)")
	private Double donated;
	public Double getDonated() {
		try {
			donations.size();
			donated=0.0;
			for (double val : donations.values()) {
				donated+=val;
			}
		} catch (Exception e) {}
		return donated;
	}
	
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyColumn(name="donor_order_by")
	@Column(name="amount")
	@CollectionTable(name="PROJECT_ITEM_DONATION", joinColumns=@JoinColumn(name="item_id"))
	protected Map<Integer,Double> donations = new HashMap<Integer, Double>();
	
	public Map<Integer,Double> getDonations() { return donations; }
	public void setDonations(Map<Integer,Double> donations)  { 
		// required for XML Encoder, not used elsewhere
		throw new RuntimeException("setDonations() field should not be used."); 
	}
	
	public abstract Project getProject();
	public abstract void setProject(Project project);
	
//	public Double getStatePublic() {
//		return publicState;
//	}
//
//	public void setStatePublic(Double publicState) {
//		this.publicState = publicState;
//	}
//
//	public Double getOther1() {
//		return 0.0;//other1;
//	}
//
//	public void setOther1(Double other1) {
//		this.other1 = other1;
//	}

	public Double getTotal() {
		if (getUnitCost()==null || getUnitNum()==null) return 0.0;
		return this.getUnitCost()*this.getUnitNum();
	}
	
	public Double getOwnResource() {
		   if (donated==null) return 0.0;
		   return (getTotal() - donated);
	   }
	
	public abstract ProjectItemNongenBase copy();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
//		result = prime * result + ((other1 == null) ? 0 : other1.hashCode());
//		result = prime * result
//				+ ((publicState == null) ? 0 : publicState.hashCode());
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
//		if (other1 == null) {
//			if (other.other1 != null)
//				return false;
//		} else if (!other1.equals(other.other1))
//			return false;
//		if (publicState == null) {
//			if (other.publicState != null)
//				return false;
//		} else if (!publicState.equals(other.publicState))
//			return false;
		if (this.getProject() == null) {
			if (other.getProject() != null)
				return false;
		} else if (!getProject().equals(other.getProject()))
			return false;
		return true;
	}
	
	
}
