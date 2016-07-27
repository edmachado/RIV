package riv.objects.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@DiscriminatorValue("0")
public class ProjectItemGeneralPerYear extends PerYearItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="OWN_RESOURCES")
	protected Double ownResources;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJ_ITEM_ID", nullable=false)
	private HasPerYearItems<ProjectItemGeneralPerYear> parent;
	
	public  HasPerYearItems<ProjectItemGeneralPerYear> getParent() {
		return parent;
	}
	public void setParent(HasPerYearItems parent) {
		this.parent = parent;
	}
	
	public Double getOwnResources() {
		return ownResources;
	}
	public void setOwnResources(Double or) {
		ownResources = or;
	}
	
	public Double getTotal() {
		if (getParent().getUnitCost()==null || getUnitNum()==null) return 0.0;
		return getParent().getUnitCost()*getUnitNum();
	}
	public Double getExternal() {
		if (ownResources==null) return 0.0;
		return (getTotal()) - ownResources;
	}
	
	public void convertCurrency(Double exchange, int scale) {
		this.setOwnResources(getParent().getProject().round(ownResources*exchange, scale));
	}
}
