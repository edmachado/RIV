package riv.objects.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

import riv.objects.PerYearItem;

@Entity
@DiscriminatorValue("1")
@Table(name="PROJECT_ITEM_PER_YEAR")
public class ProjectItemContributionPerYear extends PerYearItem implements Serializable {
	private static final long serialVersionUID = 54539059591647783L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;
	
	@NaturalId
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJ_ITEM_ID", nullable=false)
	private ProjectItemContribution contribution;
	
	@NaturalId
	@Column(name="YEAR", nullable=false)
	private Integer year;
	
	@Column(name="UNIT_NUM", nullable=false)
	private Double unitNum;
	
	public Double getTotal() {
		return unitNum*contribution.getUnitCost();
	}
	
	public ProjectItemContribution getContribution() {
		return contribution;
	}
	public void setContribution(ProjectItemContribution contribution) {
		this.contribution = contribution;
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
}
