package riv.objects.profile;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * A Labour cost associated to a Profile
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("5")
public class ProfileItemLabourWithout extends ProfileItem {

	private static final long serialVersionUID = 1L;
	@ManyToOne
	@JoinColumn(name="PROFILE_ID", nullable=false)
	private Profile profile;
	@Column(name="OWN_RESOURCE")
	private Double ownResource;
    
	public ProfileItemLabourWithout() {
		super();
	}

	public ProfileItemLabourWithout(Integer glsId) {
		super(glsId);
	}
	
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
    public Double getDonated() {
		if (getOwnResource()==null) return 0.0;
     	return getTotal()-getOwnResource();
     }
	
	public Double getOwnResource() {
        return this.ownResource;
    }
    
    public void setOwnResource(Double ownResource) {
        this.ownResource = ownResource;
    }
    
    @Override
	public ProfileItemLabourWithout copy() {
    	ProfileItemLabourWithout newItem = new ProfileItemLabourWithout();
		newItem.setDescription(this.getDescription());
		newItem.setExportLinkedTo(this.getExportLinkedTo());
		newItem.setLinkedTo(this.getLinkedTo());
		newItem.setProfile(this.getProfile());
		newItem.setUnitCost(this.getUnitCost());
		newItem.setUnitNum(this.getUnitNum());
		newItem.setUnitType(this.getUnitType());
		newItem.setOwnResource(ownResource);
		newItem.setOrderBy(this.getOrderBy());
		return newItem;
	}
	
	/**
	 * Compare two ProfItem. 
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProfileItemLabourWithout x = (ProfileItemLabourWithout)obj;
		boolean isEqual = ownResource.equals(x.ownResource);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (ownResource!=null) code = multiplier * code + (int)Double.doubleToLongBits(ownResource);
	    return code;
	}
}
