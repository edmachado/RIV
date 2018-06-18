package riv.objects.profile;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An income item associated to a Profile's product or activity
 */
@Entity
@DiscriminatorValue("1")
public class ProfileProductInput extends ProfileProductItem implements java.io.Serializable {
	static final Logger LOG = LoggerFactory.getLogger(ProfileProductInput.class);

	private static final long serialVersionUID = 1L;
	@ManyToOne
	@JoinColumn(name="PRODUCT_ID", nullable=false)
   ProfileProductBase profileProduct;
	@Column(name="transport", precision=12, scale=4)
	private BigDecimal transport;


    // Constructors
	/** default constructor */
    public ProfileProductInput() {
		super();
    }
    
    /** constructor with id */
    public ProfileProductInput(Integer inoutId) {
        super(inoutId);
    }
   
    public ProfileProductBase getProfileProduct() {
        return this.profileProduct;
    }
    
    public void setProfileProduct(ProfileProductBase profileProduct) {
        this.profileProduct = profileProduct;
    }
    public BigDecimal getTotal() {
    	//return BigDecimal.valueOf(0);
    	if (getUnitCost()==null || getUnitNum()==null || getTransport()==null) return BigDecimal.valueOf(0);
    	return getUnitNum().multiply(getUnitCost().add(getTransport()));
    }
    
    public BigDecimal getTransport() {
		if (transport==null) {
			return null;
		} else {
			try {
				return this.transport.setScale(2, RoundingMode.HALF_UP);
			} catch (Exception e) {
	    		LOG.warn("Exception rounding ppi transport.");
	    		return transport;
	    	}
		}
    }
    
    public void setTransport(BigDecimal transport) {
        this.transport = transport;
    }
    
    @Override
    public ProfileProductInput copy() {
    	ProfileProductInput item = new ProfileProductInput();
    	item.setDescription(this.getDescription());
    	item.setExportLinkedTo(getExportLinkedTo());
    	item.setLinkedTo(getLinkedTo());
    	item.setProfileProduct(getProfileProduct());
    	item.setTransport(transport);
    	item.setUnitCost(getUnitCost());
    	item.setUnitNum(getUnitNum());
    	item.setUnitType(getUnitType());
    	item.setOrderBy(getOrderBy());
    	return item;
    }
    
    /**
	 * Compare two ProfItem. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProfileProductInput x = (ProfileProductInput)obj;
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