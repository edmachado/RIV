package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name="PROJECT_ITEM_PER_YEAR", uniqueConstraints={  @UniqueConstraint(columnNames={"proj_item_id","year"})   })
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class PerYearItem  {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID")
	private Long id;
	
	@Column(name="YEAR", nullable=false)
	private Integer year;
	
	@Column(name="UNIT_NUM", nullable=false)
	private Double unitNum;
	
	
	public abstract HasPerYearItems<? extends PerYearItem> getParent() ;
	public abstract void setParent(HasPerYearItems<? extends PerYearItem> parent);
	
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