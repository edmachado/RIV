package riv.objects.profile;

import java.io.Serializable;

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

import riv.objects.HasProbase;
import riv.objects.LinkedToable;
import riv.objects.OrderByable;
import riv.objects.Probase;
import riv.objects.reference.ReferenceItem;

/**
 * Superclass for a Profile's cost table items.
 * 
 * @author Bar Zecharya
 * 
 */
@Entity
@Table(name="PROFILE_ITEM")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class ProfileItem  implements Serializable, OrderByable, LinkedToable, HasProbase {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PROF_ITEM_ID", nullable = false)
	private Integer profItemId;
	@Column(name="ORDER_BY")
	private Integer orderBy;
	@Size(max=50)
	private String description;
	@Column(name="UNIT_TYPE")
	@Size(max=50)
	private String unitType;
	@Column(name="UNIT_NUM", precision=12, scale=4)
	private Double unitNum;
	@Column(name="UNIT_COST", precision=12, scale=4)
	private Double unitCost;
	@ManyToOne
	@JoinColumn(name="LINKED_TO")
	private ReferenceItem linkedTo;
	@Transient
	private Integer exportLinkedTo;
	
	
	public Double getTotal() {
		if (getUnitCost()==null || getUnitNum()==null)
			return 0.0;
		return getUnitCost() * getUnitNum();
	}

	// Constructors

	/** default constructor */
	public ProfileItem() {
	}

	/** constructor with id */
	public ProfileItem(Integer profItemId) {
		this.profItemId = profItemId;
	}
	
	public Integer getProfItemId() {
		return this.profItemId;
	}

	public void setProfItemId(Integer profItemId) {
		this.profItemId = profItemId;
	}

	public abstract Profile getProfile();

	public abstract void setProfile(Profile profile);
	
	public Probase getProbase() {
		return this.getProfile();
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getOrderBy() {
		return orderBy;
	}
	
	 public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUnitType() {
		return this.unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public Double getUnitNum() {
		return this.unitNum;
	}

	public void setUnitNum(Double unitNum) {
		this.unitNum = unitNum;
	}

	public Double getUnitCost() {
		return this.unitCost;
	}

	public void setUnitCost(Double unitCost) {
		this.unitCost = unitCost;
	}

	/**
	 * @param linkedTo
	 *            the linkedTo to set
	 */
	public void setLinkedTo(ReferenceItem linkedTo) {
		this.linkedTo = linkedTo;
	}

	/**
	 * @return the linkedTo
	 */
	public ReferenceItem getLinkedTo() {
		return linkedTo;
	}

	
	public void setExportLinkedTo(Integer exportLinkedTo) {
		this.exportLinkedTo = exportLinkedTo;
	}

	public Integer getExportLinkedTo() {
		return exportLinkedTo;
	}
	
	public abstract ProfileItem copy(); 

	/**
	 * Compare two ProfItem. 
	 */
	//@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ProfileItem other = (ProfileItem) obj;
		boolean equals = 
			orderBy.equals(other.orderBy) &&
			//linked == other.linked &&
			//linkedTo.equals(other.linkedTo) &&
			//profItemId.equals(other.profItemId) &&
			//profile.equals(other.profile)) &&
			unitCost.equals(other.unitCost) &&
			unitNum.equals(other.unitNum) &&
			unitType.equals(other.unitType) &&
			description.equals(other.description) &&
			((exportLinkedTo==null && other.exportLinkedTo==null) 
				|| (exportLinkedTo!=null && exportLinkedTo.equals(other.exportLinkedTo)));
		return equals;
	}
	
	public int hashCode() {
		final int multiplier = 23;
	    int code = 133;
	    if (orderBy!=null) code = multiplier * code + orderBy;
	    if (unitCost!=null) code = multiplier * code + (int)Double.doubleToLongBits(unitCost);
	    if (unitNum!=null) code = multiplier * code + (int)Double.doubleToLongBits(unitNum);
	    if (unitType!=null) code = multiplier * code + unitType.hashCode();
	    if (description!=null) code = multiplier * code + description.hashCode();
	    return code;
	}
	
	

	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		int compare = this.getOrderBy() - i.getOrderBy();
		if(compare == 0) return 1;
		return compare; 
	}
}