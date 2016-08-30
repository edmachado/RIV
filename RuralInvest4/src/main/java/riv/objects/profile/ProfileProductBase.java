package riv.objects.profile;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;

import riv.objects.HasProbase;
import riv.objects.OrderByable;
import riv.objects.ProductOrBlock;

/**
 * A product (income-generating) or an activity (non-income-generating) held by a Profile.
 * @author Bar Zecharya
 *
 */
@Entity
@Table(name="PROFILE_PRODUCT")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class ProfileProductBase  implements ProductOrBlock, Serializable, OrderByable, HasProbase {
   private static final long serialVersionUID = 1L;
   @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PRODUCT_ID", nullable = false)
   private Integer productId;
  
   @Column(name="ORDER_BY")
     private Integer orderBy;
   @Size(max=50)
     private String description;
     @Column(name="LENGTH_UNIT")
     private Integer lengthUnit;
     @Column(name="CYCLE_LENGTH")
     private Double cycleLength;
     @Column(name="CYCLE_PER_YEAR")
     private Double cyclePerYear;
     @Column(name="UNIT_TYPE")
     private String unitType;
     @Column(name="UNIT_NUM")
     private Double unitNum;
 	@Column(name="CYCLES")
 	private boolean cycles=true;
     
     @OneToMany(mappedBy="profileProduct", targetEntity=ProfileProductIncome.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
     @OrderBy("ORDER_BY")
     @Where(clause="class='0'")
     private Set<ProfileProductIncome> profileIncomes;
     @OneToMany(mappedBy="profileProduct", targetEntity=ProfileProductInput.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
     @OrderBy("ORDER_BY")
     @Where(clause="class='1'")
     private Set<ProfileProductInput> profileInputs;
     @OneToMany(mappedBy="profileProduct", targetEntity=ProfileProductLabour.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
     @OrderBy("ORDER_BY")
     @Where(clause="class='2'")
     private Set<ProfileProductLabour> profileLabours;
     
  // default constructor. Initializes collections 
     public ProfileProductBase() {
     	this.profileIncomes=new HashSet<ProfileProductIncome>();
     	this.profileInputs=new HashSet<ProfileProductInput>();
     	this.profileLabours=new HashSet<ProfileProductLabour>();
     }
     
     public abstract Profile getProfile();
     public abstract void setProfile(Profile profile);

	 
	 // non-property accessors
	/**
	 * Calculates the product's total income by cycling through the income collection
	 * @return total income
	 */
	public BigDecimal getTotalIncome() {
		 BigDecimal total=new BigDecimal(0);
		 for(ProfileProductIncome inc : profileIncomes) {
			 total=total.add(inc.getTotal());
		 }
		 return total.multiply(new BigDecimal(this.cyclePerYear*this.unitNum));
	 }
	 /**
	  * Calculates the product's total cost by cycling through the input and labour collections
	 * @return total cost
	 */
	public BigDecimal getTotalCost() {
		 BigDecimal total=new BigDecimal(0);
		 for (ProfileProductInput inp:profileInputs)
			 total=total.add(inp.getTotal());
		 for (ProfileProductLabour lab:profileLabours)
			 total=total.add(lab.getTotal());
		 return total.multiply(new BigDecimal(this.cyclePerYear*this.unitNum));
	 }
    
    // Property accessors
    public Integer getProductId() {
        return this.productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }

 
    public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}
	public Integer getOrderBy() {
		return orderBy;
	}
	public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

   /**
	 * @param lengthUnit the lengthUnit to set
	 */
	public void setLengthUnit(Integer lengthUnit) {
		this.lengthUnit = lengthUnit;
	}
	/**
	 * @return the lengthUnit
	 */
	public Integer getLengthUnit() {
		return lengthUnit;
	}
	public Double getCycleLength() {
        return this.cycleLength;
    }
    
    public void setCycleLength(Double cycleLength) {
        this.cycleLength = cycleLength;
    }

    public Double getCyclePerYear() {
        return this.cyclePerYear;
    }
    
    public void setCyclePerYear(Double cyclePerYear) {
        this.cyclePerYear = cyclePerYear;
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

    public boolean isCycles() {
    	return cycles;
    }
    public void setCycles(boolean cycles) {
		this.cycles = cycles;
	}

	public Set<ProfileProductInput> getProfileInputs() {
        return this.profileInputs;
    }
    
    public void setProfileInputs(Set<ProfileProductInput> profileInputs) {
        this.profileInputs = profileInputs;
    }
	
	public void addProfileInput(ProfileProductInput pi) {
		pi.setProfileProduct(this);
		profileInputs.add(pi);
	}
	
	public void setProfileIncomes(Set<ProfileProductIncome> profileIncomes) {
		this.profileIncomes = profileIncomes;
	}

	public Set<ProfileProductIncome> getProfileIncomes() {
		return profileIncomes;
	}

	public void addProfileIncome(ProfileProductIncome pi) {
		pi.setProfileProduct(this);
		profileIncomes.add(pi);
	}
	
	public void setProfileLabours(Set<ProfileProductLabour> profileLabours) {
		this.profileLabours = profileLabours;
	}

	public Set<ProfileProductLabour> getProfileLabours() {
		return profileLabours;
	}
	
	public void addProfileLabour(ProfileProductLabour pl) {
		pl.setProfileProduct(this);
		profileLabours.add(pl);
	}

	/**
	 * Copies a profileProduct.  Collections are copied by value, 
	 * so collection items in the original product will not be affected.
	 * With the exception of "linkedto" for reference table items,
	 * which must be dealt with separately according to business logic.
	 * @return copy of the ProfileProduct
	 */
	public ProfileProductBase copy(Class<? extends ProfileProductBase> newClass) {
		ProfileProductBase newProd = newClass.isAssignableFrom(ProfileProduct.class) ? new ProfileProduct() : new ProfileProductWithout();
		newProd.setOrderBy(getOrderBy());
		newProd.setCycleLength(getCycleLength());
		newProd.setCyclePerYear(getCyclePerYear());
		newProd.setDescription(getDescription());
		newProd.setLengthUnit(getLengthUnit());
		newProd.setUnitNum(getUnitNum());
		newProd.setUnitType(getUnitType());
		newProd.setCycles(cycles);
		newProd.setProfileIncomes(new HashSet<ProfileProductIncome>());
		newProd.setProfileInputs(new HashSet<ProfileProductInput>());
		newProd.setProfileLabours(new HashSet<ProfileProductLabour>());
		for (ProfileProductIncome inc : getProfileIncomes()) {
			newProd.addProfileIncome(inc.copy());
		}
		for (ProfileProductInput inp : getProfileInputs()) {
			newProd.addProfileInput(inp.copy());
		}
		for (ProfileProductLabour lab : getProfileLabours()) {
			newProd.addProfileLabour(lab.copy());
		}		
		return newProd;
	}
	 
	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		int compare = this.getOrderBy() - i.getOrderBy();
		if(compare == 0) return 1;
		return compare; 
	}
	
	public int hashCode() {
		int code = 133;
		final int multiplier = 23;
	    if (orderBy!=null) code = multiplier * code + orderBy;
	    if (lengthUnit!=null) code = multiplier * code + lengthUnit;
	    if (description!=null) code = multiplier * code + description.hashCode();
	    if (cycleLength!=null) code = multiplier * code + cycleLength.intValue();
	    if (cyclePerYear!=null) code = multiplier * code + cyclePerYear.intValue();
	    if (unitType!=null) code = multiplier * code + unitType.hashCode();
	    if (unitNum!=null) code = multiplier * code + unitNum.intValue();
//	    code = withWithout ? multiplier * code + 1 : code;
	    return code;
	}
	
	/** Override equals().
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ProfileProductBase other = (ProfileProductBase) obj;
		boolean isEqual = orderBy.equals(other.orderBy) &&
			cycleLength.equals(other.cycleLength) &&
			cyclePerYear.equals(other.cyclePerYear) &&
			description.equals(other.description) &&
			lengthUnit.equals(other.lengthUnit) &&
			unitNum.equals(other.unitNum) &&
			unitType.equals(other.unitType);
		return isEqual;
	}
	
	
}
