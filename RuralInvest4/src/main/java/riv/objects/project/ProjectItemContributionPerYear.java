package riv.objects.project;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("1")
public class ProjectItemContributionPerYear extends PerYearItem implements Serializable {
	private static final long serialVersionUID = 54539059591647783L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJ_ITEM_ID", nullable=false)
	private HasPerYearItems<ProjectItemContributionPerYear> parent;
	
	public  HasPerYearItems<ProjectItemContributionPerYear> getParent() {
		return parent;
	}
	public void setParent(HasPerYearItems parent) {
		this.parent = parent;
	}
	
	public Double getTotal() {
		return getUnitNum()*getParent().getUnitCost();
	}
}
