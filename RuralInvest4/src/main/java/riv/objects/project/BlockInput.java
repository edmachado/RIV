package riv.objects.project;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

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
	
	public String testingProperties(CurrencyFormatter cf) {
		   StringBuilder sb = new StringBuilder();
		   String base="step9."+this.getBlock().getPropertiesType()+"."+(this.getBlock().getOrderBy()+1)+".input."+(this.getOrderBy()+1)+".";
		   sb.append(base+"description="+this.getDescription()+System.lineSeparator());
		   sb.append(base+"unitType="+this.getUnitType()+System.lineSeparator());
		   sb.append(base+"unitNum="+cf.formatCurrency(unitNum, CurrencyFormat.INTEGER)+System.lineSeparator());
		   sb.append(base+"qtyIntern="+cf.formatCurrency(qtyIntern, CurrencyFormat.INTEGER)+System.lineSeparator());
		   sb.append(base+"qtyExtern="+cf.formatCurrency(this.getExtern(), CurrencyFormat.INTEGER)+System.lineSeparator());
		   sb.append(base+"unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append(base+"transport="+cf.formatCurrency(Transport, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append(base+"total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append(base+"totalCash="+cf.formatCurrency(getTotalCash(), CurrencyFormat.ALL)+System.lineSeparator());
		   return sb.toString();
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
