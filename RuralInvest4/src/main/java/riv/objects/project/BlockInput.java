package riv.objects.project;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;

import org.hibernate.LazyInitializationException;
import org.hibernate.annotations.Formula;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.objects.HasDonations;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

/**
 * An input cost associated with a projectBlock.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("1")
public class BlockInput extends BlockItem implements HasDonations {
	static final Logger LOG = LoggerFactory.getLogger(BlockInput.class);
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BLOCK_ID", nullable=false)
	private BlockBase block;
	
	private BigDecimal Transport;
	
	@Formula(value="(SELECT ISNULL(SUM(d.amount),0) FROM project_block_item_donation d WHERE d.block_item_id=prod_item_id)")
	private Double donated;
	public Double getDonated() {
		try {
			donations.size();
			donated=0.0;
			for (double val : donations.values()) {
				donated+=val;
			}
		} catch (LazyInitializationException e) {
			// use value from formula rather than calculate from collection
			LOG.trace("using formula value for getDonated", e);
		} catch (NullPointerException e) {
			// use value from formula rather than calculate from collection
			LOG.trace("using formula value for getDonated", e);
		}
		return donated;
	}
	
	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyColumn(name="donor_order_by")
	@Column(name="amount")
	@CollectionTable(name="PROJECT_BLOCK_ITEM_DONATION", joinColumns=@JoinColumn(name="block_item_id"))
	private Map<Integer,Double> donations = new HashMap<Integer, Double>();
	
	public Map<Integer,Double> getDonations() { return donations; }
	public void setDonations(Map<Integer,Double> donations)  { 
		// required for XML Encoder, not used elsewhere
		throw new UnsupportedOperationException("setDonations() field should not be used."); 
	}
	
	
	
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
		if (getUnitCost()==null || this.getUnitNum()==null || this.Transport==null) { return new BigDecimal(0); }
		return this.getUnitNum().multiply(this.getUnitCost().add(this.Transport));
	}
	public BigDecimal getTotalCash() {
		if (this.getUnitNum()==null||getQtyIntern()==null||getUnitCost()==null||this.Transport==null) { return new BigDecimal(0); }
		return (this.getUnitNum().subtract(this.getQtyIntern())).multiply(this.getUnitCost().add(this.Transport)).subtract(new BigDecimal(getDonated()));
	}
	
	public String testingProperties(RivConfig rc) {
		String lineSeparator = System.getProperty("line.separator");
		CurrencyFormatter cf=rc.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   String base="step9."+this.getBlock().getPropertiesType()+"."+(this.getBlock().getOrderBy()+1)+".input."+(this.getOrderBy()+1)+".";
		   sb.append(base+"description="+this.getDescription()+lineSeparator);
		   sb.append(base+"unitType="+this.getUnitType()+lineSeparator);
		   sb.append(base+"unitNum="+rc.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append(base+"qtyIntern="+rc.getSetting().getDecimalFormat().format(qtyIntern)+lineSeparator);
		   sb.append(base+"qtyExtern="+rc.getSetting().getDecimalFormat().format(this.getExtern())+lineSeparator);
		   sb.append(base+"unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"transport="+cf.formatCurrency(Transport, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);

		   sb.append(base+"donated="+cf.formatCurrency(getDonated(), CurrencyFormat.ALL)+lineSeparator);
		   for (int i=0;i<getBlock().getProject().getDonors().size();i++) {
			   sb.append(base+"donations."+(i+1)+"="+cf.formatCurrency(donations.containsKey(i) ? donations.get(i) : 0.0, CurrencyFormat.ALL)+lineSeparator);
		   }
		   sb.append(base+"totalCash="+cf.formatCurrency(getTotalCash(), CurrencyFormat.ALL)+lineSeparator);
		   
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
		if (!getProbase().getIncomeGen()) {
			for (Integer donorOrder : donations.keySet()) {
				  item.getDonations().put(donorOrder, donations.get(donorOrder));
		   }
		}
		return item;
	}
	
	@Override
	public void convertCurrency(Double exchange, int scale) {
		Transport=new BigDecimal(block.getProject().round(Transport.doubleValue()*exchange,scale));
		unitCost = new BigDecimal(block.getProject().round(unitCost.doubleValue()*exchange,scale));		
		for (Integer key : donations.keySet()) {
			donations.put(key, block.getProject().round(donations.get(key)*exchange, scale));
		}
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
