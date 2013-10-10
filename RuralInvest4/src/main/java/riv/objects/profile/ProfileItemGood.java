package riv.objects.profile;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A Good cost associated to a Profile
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("0")
public class ProfileItemGood extends ProfileItem {

    private static final long serialVersionUID = 1L;
    @ManyToOne
	@JoinColumn(name="PROFILE_ID", nullable=false)
	private Profile profile;
	@Column(name="ECON_LIFE")
	private Double econLife;
	@Column(name="OWN_RESOURCE")
	private Double ownResource;
    private Double salvage;
    
	public ProfileItemGood() {
		super();
	}

	public ProfileItemGood(Integer glsId) {
		super(glsId);
	}
	
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	public Double getReserve() {
 		if (getUnitCost()==null || getSalvage()==null || getUnitNum()==null || econLife==null) return 0.0;
		return ((getUnitCost()-getSalvage())*getUnitNum())/econLife;
	}
	
    public Double getDonated() {
 		if (getUnitNum()==null || getUnitCost()==null || getOwnResource()==null) return 0.0;
     	return getTotal()-getOwnResource();
    }
    
    public Double getAnnualReserve() {
 		if (getUnitCost()==null || getUnitNum()==null || salvage==null || econLife==null) return 0.0;
    	return ((getUnitCost()*getUnitNum())-(getUnitNum()*salvage))/econLife;
    }

	public Double getEconLife() {
        return this.econLife;
    }
    
    public void setEconLife(Double econLife) {
        this.econLife = econLife;
    }
    
    public Double getOwnResource() {
        return ownResource;
    }
    
    public void setOwnResource(Double ownResource) {
        this.ownResource = ownResource;
    }

	public void setSalvage(Double salvage) {
		this.salvage = salvage;
	}

	public Double getSalvage() {
		return salvage;
	}
	
	@Override
	public ProfileItemGood copy() {
		ProfileItemGood newItem = new ProfileItemGood();
		newItem.setDescription(this.getDescription());
		newItem.setExportLinkedTo(this.getExportLinkedTo());
		newItem.setLinkedTo(this.getLinkedTo());
		newItem.setProfile(this.getProfile());
		newItem.setUnitCost(this.getUnitCost());
		newItem.setUnitNum(this.getUnitNum());
		newItem.setUnitType(this.getUnitType());
		newItem.setEconLife(econLife);
		newItem.setOwnResource(ownResource);
		newItem.setSalvage(salvage);
		newItem.setOrderBy(this.getOrderBy());
		return newItem;
	}
   
	/**
	 * Compare two ProfItem. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProfileItemGood x = (ProfileItemGood)obj;
		boolean isEqual = ownResource.equals(x.ownResource) &&
			salvage.equals(x.salvage) &&
			econLife.equals(x.econLife);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (ownResource!=null) code = multiplier * code + (int)Double.doubleToLongBits(ownResource);
	    if (salvage!=null) code = multiplier * code + (int)Double.doubleToLongBits(salvage);
	    if (econLife!=null) code = multiplier * code + (int)Double.doubleToLongBits(econLife);
	    return code;
	}
}
