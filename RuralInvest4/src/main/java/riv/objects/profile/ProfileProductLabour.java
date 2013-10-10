package riv.objects.profile;

import java.math.BigDecimal;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A Labout cost associated to a Profile's product or activity
 */
@Entity
@DiscriminatorValue("2")
public class ProfileProductLabour extends ProfileProductItem implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PRODUCT_ID", nullable=false)
   ProfileProduct profileProduct;
   
	// Constructors

    /** default constructor */
    public ProfileProductLabour() {
		super();
    }
    
    /** constructor with id */
    public ProfileProductLabour(Integer inoutId) {
        super(inoutId);
    }
    
    public BigDecimal getTotal() {
    	if (getUnitCost()==null || getUnitNum()==null) return BigDecimal.valueOf(0);
    	return getUnitNum().multiply(getUnitCost());
    }
    
    public ProfileProduct getProfileProduct() {
        return this.profileProduct;
    }
    
    public void setProfileProduct(ProfileProduct profileProduct) {
        this.profileProduct = profileProduct;
    }
    
    @Override
    public ProfileProductLabour copy() {
    	ProfileProductLabour item = new ProfileProductLabour();
    	item.setDescription(this.getDescription());
    	item.setExportLinkedTo(getExportLinkedTo());
    	item.setLinkedTo(getLinkedTo());
    	item.setProfileProduct(getProfileProduct());
    	item.setUnitCost(getUnitCost());
    	item.setUnitNum(getUnitNum());
    	item.setUnitType(getUnitType());
    	item.setOrderBy(getOrderBy());
    	return item;
    }
}