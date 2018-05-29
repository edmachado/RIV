package riv.objects.profile;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.objects.OrderByable;
import riv.objects.Probase;

@Entity
@DiscriminatorValue("0")
public class ProfileProduct extends ProfileProductBase {
	private static final long serialVersionUID = 4480547276656860398L;
	
	@ManyToOne
		@JoinColumn(name="PROFILE_ID", nullable=false)
	   private Profile profile;
	 
	  public Profile getProfile() {
	        return this.profile;
	    }
	    
	    public void setProfile(Profile profile) {
	        this.profile = profile;
	    }
	    
	    public Probase getProbase() {
	    	return this.profile;
	    }

		@Override
		public int hashCode() {
			int hash = 0;	
			if (getDescription()!=null)
				hash = getDescription().hashCode();
			return hash;
		}
		
		@Override
		public boolean equals(Object o) {
			return super.equals(o);
		}


		public int compareTo(OrderByable i) {
			if (this==i) return 0;
			int compare = this.getOrderBy() - i.getOrderBy();
			if(compare == 0) return 1;
			return compare; 
		}
}