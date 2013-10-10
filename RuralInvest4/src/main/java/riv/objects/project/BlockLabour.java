package riv.objects.project;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A labour cost associated with a ProjectBlock
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("2")
public class BlockLabour extends BlockItem {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BLOCK_ID", nullable=false)
	private BlockBase block;
	
	public BlockBase getBlock () {
		return this.block;
	}

	public void setBlock (BlockBase block) {
		this.block = block;
	}
	
	public BigDecimal getTotal() {
		if (getUnitCost()==null || this.getUnitNum()==null) return new BigDecimal(0);
		return this.getUnitCost().multiply(this.getUnitNum());
	}
	public BigDecimal getTotalCash() {
		if (getUnitCost()==null || this.getUnitNum()==null || getQtyIntern()==null) return new BigDecimal(0);
		return this.getUnitNum().subtract(this.getQtyIntern()).multiply(this.getUnitCost());
	}
	
	@Override
	public BlockLabour copy() {
		BlockLabour item = new BlockLabour();
		item.setDescription(description);
		//item.setExportLinkedTo(exportLinkedTo);
		item.setLinkedTo(getLinkedTo());
		item.setBlock(getBlock());
		item.setQtyIntern(qtyIntern);
		item.setUnitCost(unitCost);
		item.setUnitNum(unitNum);
		item.setUnitType(unitType);
		item.setOrderBy(getOrderBy());
		return item;
	}
}
