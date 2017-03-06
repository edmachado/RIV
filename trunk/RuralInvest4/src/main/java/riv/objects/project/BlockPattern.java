package riv.objects.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A production pattern element for a ProjectBlock.  Represents how many of the unit of production
 * will be produced in a given year of the project.
 * @author Bar Zecharya
 *
 */
@Entity
@Table(name="PROJECT_BLOCK_PATTERN")
public class BlockPattern implements Serializable {
	private static final long serialVersionUID = -6009098764232774609L;
	
	@Id
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BLOCK_ID", nullable=false)
	private BlockBase block;
	@Id
	@Column(name="YEAR_NUM")
	private int yearNum;
	private double qty;

	public void setBlock(BlockBase blockBase) {
		this.block = blockBase;
	}
	public BlockBase getBlock() {
		return block;
	}
	public void setYearNum(int yearNum) {
		this.yearNum = yearNum;
	}
	public int getYearNum() {
		return yearNum;
	}
	public void setQty(double qty) {
		this.qty = qty;
	}
	public double getQty() {
		return qty;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((block == null) ? 0 : block.hashCode());
		result = prime * result + yearNum;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockPattern other = (BlockPattern) obj;
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		if (yearNum != other.yearNum)
			return false;
		return true;
	}
}
