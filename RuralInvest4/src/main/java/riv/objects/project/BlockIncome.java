package riv.objects.project;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

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
		if (transport!=null) { // ig 
			return this.getUnitNum().subtract(this.getQtyIntern()).multiply(this.getUnitCost().subtract(this.transport));
		} else {
			return this.getUnitNum().multiply(this.getUnitCost());
		}
	}
	public BigDecimal getTotalCashIncomeWithoutTransport() {
		if (this.getUnitNum()==null) return new BigDecimal(0);
		return this.getUnitNum().subtract(this.getQtyIntern()).multiply(this.getUnitCost());
	}
	public BigDecimal getTotalCashIncomeOnlyTransportCost() {
		if (this.getUnitNum()==null || transport==null) return new BigDecimal(0);
		return this.getUnitNum().subtract(this.getQtyIntern()).multiply(this.transport);
		
	}
	
	public String testingProperties(RivConfig rc) {
		String lineSeparator = System.getProperty("line.separator");
		CurrencyFormatter cf=rc.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   String base="step9."+this.getBlock().getPropertiesType()+"."+(this.getBlock().getOrderBy()+1)+".income."+(this.getOrderBy()+1)+".";
		   sb.append(base+"description="+this.getDescription()+lineSeparator);
		   sb.append(base+"unitType="+this.getUnitType()+lineSeparator);
		   sb.append(base+"unitNum="+rc.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append(base+"unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   if (block.getProject().getIncomeGen()) {
			   sb.append(base+"qtyIntern="+rc.getSetting().getDecimalFormat().format(qtyIntern)+lineSeparator);
			   sb.append(base+"qtyExtern="+rc.getSetting().getDecimalFormat().format(this.getExtern())+lineSeparator);
			   sb.append(base+"transport="+cf.formatCurrency(transport, CurrencyFormat.ALL)+lineSeparator);
			   sb.append(base+"totalCash="+cf.formatCurrency(getTotalCash(), CurrencyFormat.ALL)+lineSeparator);
		   }
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
	public void convertCurrency(Double exchange, int scale) {
		if (block.getProject().getIncomeGen()) {
			transport=block.getProject().round(transport.multiply(BigDecimal.valueOf(exchange)),scale);
		}
		unitCost = block.getProject().round(unitCost.multiply(BigDecimal.valueOf(exchange)),scale);				
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
