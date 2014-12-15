package riv.objects.project;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

/**
 * An income or user charge associated with a project block.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("0")
public class BlockIncome extends BlockItem {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="BLOCK_ID", nullable=false)
	private BlockBase block;
	@Column(precision=12, scale=4)
	private BigDecimal transport;
	
	public void setTransport(BigDecimal transport) {
		this.transport = transport;
	}

	public BigDecimal getTransport() {
		return transport;
	}
	
	public BlockBase getBlock () {
		return this.block;
	}

	public void setBlock (BlockBase block) {
		this.block = block;
	}
	
	// calculated
	public BigDecimal getTotal() {
		if (this.getUnitNum()==null) return new BigDecimal(0);
		return (this.transport==null) 
			? this.getUnitNum().multiply(this.getUnitCost()) 
			: this.getUnitNum().multiply(this.getUnitCost().subtract(this.transport));
	}
	public BigDecimal getTotalCash() {
		if (this.getUnitNum()==null) return new BigDecimal(0);
		return (this.transport==null) 
			? this.getUnitNum().subtract(this.getQtyIntern()).multiply(this.getUnitCost())
			: this.getUnitNum().subtract(this.getQtyIntern()).multiply(this.getUnitCost().subtract(this.transport));
	}
	
	public String testingProperties(CurrencyFormatter cf) {
		   StringBuilder sb = new StringBuilder();
		   String base="step9."+this.getBlock().getPropertiesType()+"."+(this.getBlock().getOrderBy()+1)+".income."+(this.getOrderBy()+1)+".";
		   sb.append(base+"description="+this.getDescription()+System.lineSeparator());
		   sb.append(base+"unitType="+this.getUnitType()+System.lineSeparator());
		   sb.append(base+"unitNum="+cf.formatCurrency(unitNum, CurrencyFormat.INTEGER)+System.lineSeparator());
		   sb.append(base+"qtyIntern="+cf.formatCurrency(qtyIntern, CurrencyFormat.INTEGER)+System.lineSeparator());
		   sb.append(base+"qtyExtern="+cf.formatCurrency(this.getExtern(), CurrencyFormat.INTEGER)+System.lineSeparator());
		   sb.append(base+"unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append(base+"transport="+cf.formatCurrency(transport, CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append(base+"total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+System.lineSeparator());
		   sb.append(base+"totalCash="+cf.formatCurrency(getTotalCash(), CurrencyFormat.ALL)+System.lineSeparator());
		   return sb.toString();
	}

	@Override
	public BlockIncome copy() {
		BlockIncome item = new BlockIncome();
		item.setDescription(description);
		//item.setExportLinkedTo(exportLinkedTo);
		item.setLinkedTo(getLinkedTo());
		item.setBlock(getBlock());
		item.setQtyIntern(qtyIntern);
		item.setTransport(transport);
		item.setUnitCost(unitCost);
		item.setUnitNum(unitNum);
		item.setUnitType(unitType);
		item.setOrderBy(getOrderBy());
		
		return item;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		BlockIncome x = (BlockIncome)obj;
		boolean isEqual = transport.equals(x.transport);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (transport!=null) code = multiplier * code + transport.intValue();
	    return code;
	}
	
}
