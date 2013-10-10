package  riv.objects.reference;

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
import javax.persistence.Table;

import riv.objects.HasProbase;
import riv.objects.OrderByable;
import riv.objects.Probase;
import riv.objects.profile.Profile;
import riv.objects.project.Project;


/**
 * Superclass for a Reference Item.
 * @author Bar Zecharya
 *
 */
@Entity
@Table(name="REF_ITEM")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class ReferenceItem  implements Serializable, OrderByable, HasProbase  {

    private static final long serialVersionUID = 1L;
	
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="REF_ITEM_ID", nullable = false)
    private Integer refItemId;
	@Column(name="ORDER_BY")
	private Integer orderBy;
    private String description;
    @Column(name="UNIT_TYPE")
    private String unitType;
    @Column(name="UNIT_COST")
    private Double unitCost;
     
    // Constructors

    /** default constructor */
    public ReferenceItem() {
    }
    
    /** constructor with id */
    public ReferenceItem(Integer refItemId) {
        this.refItemId = refItemId;
    }
   
   // Property accessors

     public Integer getRefItemId() {
        return this.refItemId;
    }
    
    public void setRefItemId(Integer refItemId) {
        this.refItemId = refItemId;
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

    public String getUnitType() {
        return this.unitType;
    }
    
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Double getUnitCost() {
        return this.unitCost;
    }
    
    public void setUnitCost(Double unitCost) {
        this.unitCost = unitCost;
    }
    
    // other methods
   abstract public Probase getProbase();
   abstract public Project getProject();
   abstract public void setProject(Project p);
   abstract public Profile getProfile();
   abstract public void setProfile(Profile p);
   abstract public ReferenceItem copy();
   //abstract public void updateProbase();

	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		int compare = this.getOrderBy() - i.getOrderBy();
		if(compare == 0) return 1;
		return compare; 
	}
	
	/**
	 * Compare ReferenceItem. Override equals().
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferenceItem other = (ReferenceItem) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (orderBy == null) {
			if (other.orderBy != null)
				return false;
		} else if (!orderBy.equals(other.orderBy))
			return false;
		if (refItemId == null) {
			if (other.refItemId != null)
				return false;
		} else if (!refItemId.equals(other.refItemId))
			return false;
		if (unitCost == null) {
			if (other.unitCost != null)
				return false;
		} else if (!unitCost.equals(other.unitCost))
			return false;
		if (unitType == null) {
			if (other.unitType != null)
				return false;
		} else if (!unitType.equals(other.unitType))
			return false;
		return true;
	}
}