package riv.objects.profile;

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

import riv.objects.HasProbase;
import riv.objects.LinkedToable;
import riv.objects.OrderByable;
import riv.objects.Probase;
import riv.objects.reference.ReferenceItem;

/**
 * Superclass for items contained by a ProfileProduct
 */
@Entity
@Table(name="PROFILE_PRODUCT_ITEM")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class ProfileProductItem  implements Serializable, OrderByable, LinkedToable, HasProbase {

    private static final long serialVersionUID = 1L;
	// Fields    
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PROD_ITEM_ID", nullable = false)
    private Integer prodItemId;
     @Column(name="ORDER_BY")
     private Integer OrderBy;
     @Size(max=50)
     private String description;
     @Column(name="UNIT_TYPE")
     @Size(max=50)
     private String unitType;
     @Column(name="UNIT_NUM")
     private BigDecimal unitNum;
     @Column(name="UNIT_COST", precision=12, scale=4)
     private BigDecimal unitCost;
     @ManyToOne
 	@JoinColumn(name="LINKED_TO")
 	private ReferenceItem linkedTo;
 	@Transient
 	private Integer exportLinkedTo;

     public Integer getProfileProductID() {
    	 return this.getProfileProduct().getProductId();
     }
     
     public Probase getProbase() {
    	 return this.getProfileProduct().getProbase();
     }
     public abstract ProfileProduct getProfileProduct();
     public abstract void setProfileProduct(ProfileProduct profileProduct);
     
    // Constructors

    /** default constructor */
    public ProfileProductItem() {
    }
    
    /** constructor with id */
    public ProfileProductItem(Integer prodItemId) {
        this.prodItemId = prodItemId;
    }
   
    
    

    // Property accessors
    public Integer getProdItemId() {
        return this.prodItemId;
    }
    
    public void setProdItemId(Integer prodItemId) {
        this.prodItemId = prodItemId;
    }

    

    public void setOrderBy(Integer orderBy) {
		OrderBy = orderBy;
	}

	public Integer getOrderBy() {
		return OrderBy;
	}

	public String getDescription() {
        return this.description;
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

    public BigDecimal getUnitNum() {
        return this.unitNum;
    }
    
    public void setUnitNum(BigDecimal unitNum) {
        this.unitNum = unitNum;
    }

    public BigDecimal getUnitCost() {
        return this.unitCost;
    }
    
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
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
	
	public abstract ProfileProductItem copy();

	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		int compare = this.getOrderBy() - i.getOrderBy();
		if(compare == 0) return 1;
		return compare; 
	}
	
	//@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ProfileProductItem other = (ProfileProductItem) obj;
		boolean equals = 
			OrderBy.equals(other.OrderBy) &&
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
	    if (OrderBy!=null) code = multiplier * code + OrderBy;
	    if (unitCost!=null) code = multiplier * code +unitCost.intValue();
	    if (unitNum!=null) code = multiplier * code + unitNum.intValue();
	    if (unitType!=null) code = multiplier * code + unitType.hashCode();
	    if (description!=null) code = multiplier * code + description.hashCode();
	    return code;
	}
	
	
}