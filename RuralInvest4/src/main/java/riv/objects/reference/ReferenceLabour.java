package  riv.objects.reference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.objects.Probase;
import riv.objects.profile.Profile;
import riv.objects.project.Project;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

/**
 * A labour item
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("2")
public class ReferenceLabour extends ReferenceItem {
    
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=true)
	protected Project project;
	@ManyToOne
	@JoinColumn(name="PROFILE_ID", nullable=true)
	private Profile profile;
	
	public ReferenceLabour() {
		super();
	}

	public ReferenceLabour(Integer refItemId) {
		super(refItemId);
	}
	
	 public Project getProject () {
	        return this.project;
	    }
	    
	   public void setProject (Project project) {
	        this.project = project;
	    }
	   
	   public Profile getProfile () {
	       return this.profile;
	   }
	   
	  public void setProfile (Profile profile) {
	       this.profile = profile;
	   }
	
	@Override
	public Probase getProbase() {
		return project == null ? profile : project;
	}  
	
	public String testingProperties(CurrencyFormatter cf) {
		StringBuilder sb = new StringBuilder();
		sb.append("step10.labour."+(this.getOrderBy()+1)+".description="+this.getDescription()+System.lineSeparator());
		sb.append("step10.labour."+(this.getOrderBy()+1)+".unitType="+this.getUnitType()+System.lineSeparator());
		sb.append("step10.labour."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(this.getUnitCost(), CurrencyFormat.ALL)+System.lineSeparator());
		return sb.toString();
	}
		
	public ReferenceLabour copy() {
		ReferenceLabour newRef = new ReferenceLabour();
		 newRef.setDescription(this.getDescription());
		 newRef.setOrderBy(this.getOrderBy());
		 newRef.setUnitCost(this.getUnitCost());
		 newRef.setUnitType(this.getUnitType());
		 return newRef;
	}
	
//	public void updateProbase() {
//		if (this.getProject()!=null) {
//			Project p = this.getProject();
//			for (ProjectItemLabour i : p.getLabours()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}
//			for (ProjectItemPersonnel i : p.getPersonnels()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}
//			for (Block b : p.getBlocks()) {
//				for (BlockLabour i : b.getLabours()) {
//					if (this.equals(i.getLinkedTo())) {
//						i.setUnitCost(new BigDecimal(this.getUnitCost()));
//					}
//				}
//			}
//		} else { // profile
//			for (ProfileProduct pp : this.getProfile().getProducts()) {
//				for (ProfileProductLabour i : pp.getProfileLabours()) {
//					if (this.equals(i.getLinkedTo())) {
//						i.setUnitCost(new BigDecimal(this.getUnitCost()));
//					}
//				}
//			}
//		}
//	}
}
