package riv.objects.project;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * An input cost associated with a projectBlock.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("1")
public class BlockInput extends BlockItem {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BLOCK_ID", nullable=false)
	private BlockBase block;
	
	private BigDecimal Transport;
	
	public void setTransport(BigDecimal transport) {
		Transport = transport;
	}

	public BigDecimal getTransport() {
		return Transport;
	}
	
	public BlockBase getBlock () {
		return this.block;
	}

	public void setBlock (BlockBase block) {
		this.block = block;
	}
	
	//calculated
	public BigDecimal getTotal() {
		if (getUnitCost()==null || this.getUnitNum()==null || this.Transport==null) return new BigDecimal(0);
		return this.getUnitNum().multiply(this.getUnitCost().add(this.Transport));
	}
	public BigDecimal getTotalCash() {
		if (this.getUnitNum()==null||getQtyIntern()==null||getUnitCost()==null||this.Transport==null) return new BigDecimal(0);
		return (this.getUnitNum().subtract(this.getQtyIntern())).multiply(this.getUnitCost().add(this.Transport));
	}

	@Override
	public BlockInput copy() {
		BlockInput item = new BlockInput();
		item.setDescription(description);
		//item.setExportLinkedTo(exportLinkedTo);
		item.setLinkedTo(getLinkedTo());
		item.setBlock(getBlock());
		item.setQtyIntern(qtyIntern);
		item.setTransport(Transport);
		item.setUnitCost(unitCost);
		item.setUnitNum(unitNum);
		item.setUnitType(unitType);
		item.setOrderBy(getOrderBy());
		
		return item;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		BlockInput x = (BlockInput)obj;
		boolean isEqual = Transport.equals(x.Transport);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (Transport!=null) code = multiplier * code + Transport.intValue();
	    return code;
	}
	
	
}
