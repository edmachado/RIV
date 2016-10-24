package riv.objects.profile;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.objects.OrderByable;
/**
 * A General cost associated to a Profile.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("2")
public class ProfileItemGeneral extends ProfileItem implements OrderByable {
    
	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROFILE_ID", nullable=false)
	private Profile profile;
	
	public ProfileItemGeneral() {
		super();
	}

	public ProfileItemGeneral(Integer glsId) {
		super(glsId);
	}   
	
	public Profile getProfile() {
		return this.profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}
	
	@Override
	public ProfileItemGeneral copy() {
		ProfileItemGeneral newItem = new ProfileItemGeneral();
		newItem.setDescription(this.getDescription());
		newItem.setExportLinkedTo(this.getExportLinkedTo());
		newItem.setLinkedTo(this.getLinkedTo());
		newItem.setProfile(this.getProfile());
		newItem.setUnitCost(this.getUnitCost());
		newItem.setUnitNum(this.getUnitNum());
		newItem.setUnitType(this.getUnitType());
		newItem.setOrderBy(this.getOrderBy());
		return newItem;
	}
}
