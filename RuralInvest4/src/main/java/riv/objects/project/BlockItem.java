package riv.objects.project;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import riv.objects.LinkedToable;
import riv.objects.OrderByable;
import riv.objects.Probase;
import riv.objects.reference.ReferenceItem;
import riv.util.CurrencyFormatter;

/**
 * Superclass for Incomes and Costs associated with a project block.
 */
@Entity
@Table(name="PROJECT_BLOCK_ITEM")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class BlockItem  implements Serializable, OrderByable, LinkedToable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PROD_ITEM_ID", nullable = false)
	private java.lang.Integer prodItemId;
	@Column(name="ORDER_BY")
	private Integer orderBy;
	protected java.lang.String description;
	@Column(name="UNIT_TYPE")
	protected java.lang.String unitType;
	@Column(name="UNIT_NUM", precision=12, scale=4)
	protected BigDecimal unitNum;
	@Column(name="UNIT_COST", precision=12, scale=4)
	protected BigDecimal unitCost;
	@Column(name="QTY_INTERN", precision=12, scale=4)
	protected BigDecimal qtyIntern;
	@Size(max=2000)
	private String note;
	@ManyToOne
	@JoinColumn(name="LINKED_TO")
	private ReferenceItem linkedTo;
	@Transient
	protected Integer exportLinkedTo;
	// Constructors

	/** default constructor */
	public BlockItem() {
	}
	

//    public abstract String testingProperties(CurrencyFormatter cf);

	// Property accessors

	public java.lang.Integer getProdItemId () {
		return this.prodItemId;
	}

	public void setProdItemId (java.lang.Integer ProdItemId) {
		this.prodItemId = ProdItemId;
	}
	

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public java.lang.String getDescription () {
		return this.description;
	}

	public void setDescription (java.lang.String Description) {
		this.description = Description;
	}

	public java.lang.String getUnitType () {
		return this.unitType;
	}

	public void setUnitType (java.lang.String UnitType) {
		this.unitType = UnitType;
	}

	public BigDecimal getUnitNum () {
		return this.unitNum;
	}

	public void setUnitNum (BigDecimal UnitNum) {
		this.unitNum = UnitNum;
	}

	public BigDecimal getUnitCost () {
		return this.unitCost;
	}

	public void setUnitCost (BigDecimal UnitCost) {
		this.unitCost = UnitCost;
	}

	public BigDecimal getQtyIntern () {
		return this.qtyIntern;
	}

	public void setQtyIntern (BigDecimal QtyIntern) {
		this.qtyIntern = QtyIntern;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setLinkedTo(ReferenceItem linkedTo) {
		this.linkedTo = linkedTo;
	}

	public ReferenceItem getLinkedTo() {
		return linkedTo;
	}

	public void setExportLinkedTo(Integer exportLinkedTo) {
		this.exportLinkedTo = exportLinkedTo;
	}

	public Integer getExportLinkedTo() {
		return exportLinkedTo;
	}

	// calculated 
	public BigDecimal getExtern() {
		if (this.getUnitNum()==null || getQtyIntern()==null) return new BigDecimal(0);
		return this.getUnitNum().subtract(this.getQtyIntern());
	}

	public abstract BlockItem copy();
	public abstract BlockBase getBlock();
	public abstract void setBlock(BlockBase b);
	public Probase getProbase() {
		return this.getBlock().getProject();
	}
	
	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		int compare = this.getOrderBy() - i.getOrderBy();
		if(compare == 0) return 1;
		return compare; 
	}
	
	/** Override equals()
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BlockItem other = (BlockItem) obj;
		boolean isEqual = 
			description.equals(other.description) &&
			orderBy.equals(other.orderBy) &&
			qtyIntern.equals(other.qtyIntern) &&
			unitCost.equals(other.unitCost) &&
			unitNum.equals(other.unitNum) &&
			unitType.equals(other.unitType);
		return isEqual;
	}
	
	public int hashCode() {
		final int multiplier = 23;
	    int code = 133;
	    if (orderBy!=null) code = multiplier * code + orderBy;
	    if (unitCost!=null) code = multiplier * code + unitCost.intValue();
	    if (unitNum!=null) code = multiplier * code + unitNum.intValue();
	    if (qtyIntern!=null) code = multiplier * code + qtyIntern.intValue();
	    if (unitType!=null) code = multiplier * code + unitType.hashCode();
	    if (description!=null) code = multiplier * code + description.hashCode();
	    return code;
	}
	
}