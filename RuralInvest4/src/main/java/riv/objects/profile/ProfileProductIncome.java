package riv.objects.profile;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * An income or user charge associated to a profile's product (income-generating) or activity (non-income-generating)
 */
@Entity
@DiscriminatorValue("0")
public class ProfileProductIncome extends ProfileProductItem implements java.io.Serializable {

   private static final long serialVersionUID = 1L;
   
   @ManyToOne
	@JoinColumn(name="PRODUCT_ID", nullable=false)
   ProfileProduct profileProduct;
   @Column(name="transport", precision=12, scale=4)
   private BigDecimal transport;


    // Constructors

    /** default constructor */
    public ProfileProductIncome() {
		super();
    }
    
    /** constructor with id */
    public ProfileProductIncome(Integer inoutId) {
        super(inoutId);
    }
    public ProfileProduct getProfileProduct() {
        return this.profileProduct;
    }
    
    public void setProfileProduct(ProfileProduct profileProduct) {
        this.profileProduct = profileProduct;
    }
    
    public BigDecimal getTotal() {
    	if (getUnitCost()==null || getUnitNum()==null 
    			|| (getProfileProduct().getProfile().getIncomeGen() && getTransport()==null)) 
    		return BigDecimal.valueOf(0);
    	return getProfileProduct().getProfile().getIncomeGen() 
    		? getUnitNum().multiply(getUnitCost().subtract(getTransport()))
    		: getUnitNum().multiply(getUnitCost());
    }
   
    public BigDecimal getTransport() {
        return this.transport;
    }
    
    public void setTransport(BigDecimal transport) {
        this.transport = transport;
    }
    
    
    @Override
    public ProfileProductIncome copy() {
    	ProfileProductIncome item = new ProfileProductIncome();
    	item.setDescription(this.getDescription());
    	item.setExportLinkedTo(getExportLinkedTo());
    	item.setLinkedTo(getLinkedTo());
    	item.setProfileProduct(getProfileProduct());
    	item.setTransport(transport);
    	item.setUnitCost(getUnitCost());
    	item.setUnitNum(getUnitNum());
    	item.setUnitType(getUnitType());
    	item.setOrderBy(this.getOrderBy());
    	return item;
    }
    
    @Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProfileProductIncome x = (ProfileProductIncome)obj;
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