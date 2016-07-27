package riv.objects.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

@Entity
@Table(name="PROJECT_ITEM_PER_YEAR")
public class ProjectItemGeneralPerYear implements Serializable {
	private static final long serialVersionUID = 54539059591647783L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;
	
	@NaturalId
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJ_ITEM_ID", nullable=false)
	private ProjectItemGeneralBase general;
	
	@NaturalId
	@Column(name="YEAR", nullable=false)
	private Integer year;
	
	@Column(name="UNIT_NUM", nullable=false)
	private Double unitNum;
	
	@Column(name="OWN_RESOURCES")
	protected Double ownResources;
	
	public ProjectItemGeneralBase getGeneral() {
		return general;
	}
	public void setGeneral(ProjectItemGeneralBase general) {
		this.general = general;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer y) {
		year=y;
	}
	public Double getUnitNum() {
		return unitNum;
	}
	public void setUnitNum(Double unitNum) {
		this.unitNum = unitNum;
	}
	public Double getOwnResources() {
		return ownResources;
	}
	public void setOwnResources(Double or) {
		ownResources = or;
	}
	
	public Double getTotal() {
		if (getGeneral().getUnitCost()==null || getUnitNum()==null) return 0.0;
		return getGeneral().getUnitCost()*getUnitNum();
	}
	public Double getExternal() {
		if (ownResources==null) return 0.0;
		return (getTotal()) - ownResources;
	}
	
	public void convertCurrency(Double exchange, int scale) {
		this.setOwnResources(getGeneral().getProject().round(ownResources*exchange, scale));
	}
}
