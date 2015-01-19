package riv.objects.project;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

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
	
	public String testingProperties(RivConfig rc) {
		String lineSeparator = System.getProperty("line.separator");
		CurrencyFormatter cf=rc.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   String base="step9."+this.getBlock().getPropertiesType()+"."+(this.getBlock().getOrderBy()+1)+".labour."+(this.getOrderBy()+1)+".";
		   sb.append(base+"description="+this.getDescription()+lineSeparator);
		   sb.append(base+"unitType="+rc.getLabourTypes().get(this.getUnitType())+lineSeparator);
		   sb.append(base+"unitNum="+rc.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append(base+"qtyIntern="+rc.getSetting().getDecimalFormat().format(qtyIntern)+lineSeparator);
		   sb.append(base+"qtyExtern="+rc.getSetting().getDecimalFormat().format(this.getExtern())+lineSeparator);
		   sb.append(base+"unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"totalCash="+cf.formatCurrency(getTotalCash(), CurrencyFormat.ALL)+lineSeparator);
		   return sb.toString();
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
