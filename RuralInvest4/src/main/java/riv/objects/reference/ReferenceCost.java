package riv.objects.reference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.objects.Probase;
import riv.objects.profile.Profile;
import riv.objects.project.Project;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

/**
 * A cost item
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("0")
public class ReferenceCost extends ReferenceItem {
    
	private static final long serialVersionUID = 1L;
	private Double transport; 
	@ManyToOne
	@JoinColumn(name="PROFILE_ID", nullable=true)
	private Profile profile;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=true)
	protected Project project;
	
	public ReferenceCost() {
		super();
	}

	public ReferenceCost(Integer costInputUd) {
		super(costInputUd);
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

	/**
	 * @param transport the transport to set
	 */
	public void setTransport(Double transport) {
		this.transport = transport;
	}

	/**
	 * @return the transport
	 */
	public Double getTransport() {
		return transport;
	}
	
	public String testingProperties(RivConfig rc) {
		CurrencyFormatter cf = rc.getSetting().getCurrencyFormatter();
		StringBuilder sb = new StringBuilder();
		String base="step"+(this.getProbase().getIncomeGen()?"10":"11")+".input."+(this.getOrderBy()+1)+".";
		sb.append(base+"description="+this.getDescription()+System.lineSeparator());
		sb.append(base+"unitType="+this.getUnitType()+System.lineSeparator());
		sb.append(base+"unitCost="+cf.formatCurrency(this.getUnitCost(), CurrencyFormat.ALL)+System.lineSeparator());
		if (transport!=null) {
			sb.append(base+"transport="+cf.formatCurrency(this.getTransport(), CurrencyFormat.ALL)+System.lineSeparator());
		} else {
			sb.append(base+"transport="+System.lineSeparator());
		}
		return sb.toString();
	}
	
	public ReferenceCost copy() {
		 ReferenceCost newItem = new ReferenceCost();
		 newItem.setDescription(this.getDescription());
		 newItem.setOrderBy(this.getOrderBy());
		 newItem.setTransport(this.getTransport());
		 newItem.setUnitCost(this.getUnitCost());
		 newItem.setUnitType(this.getUnitType());
		 return newItem;
	}
	
//	public void updateProbase() {
//		if (this.getProject()!=null) {
//			Project p = this.getProject();
//			for (ProjectItemAsset i : p.getAssets()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}
//			for (ProjectItemGeneral i : p.getGenerals()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}for (ProjectItemService i : p.getServices()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}
//			for (Block b : p.getBlocks()) {
//				for (BlockInput i : b.getInputs()) {
//					if (this.equals(i.getLinkedTo())) {
//						i.setUnitCost(new BigDecimal(this.getUnitCost()));
//						i.setTransport(new BigDecimal(this.transport));
//					}
//				}
//			}
//		} else if (this.getProfile() != null) {
//			Profile p = this.getProfile();
//			for (ProfileItemGood i : p.getGlsGoods()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}
//			for (ProfileItemLabour i : p.getGlsLabours()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}
//			for (ProfileItemGeneral i : p.getGlsGeneral()) {
//				if (this.equals(i.getLinkedTo())) {
//					i.setUnitCost(this.getUnitCost());
//				}
//			}
//			for (ProfileProduct pp : p.getProducts()) {
//				for (ProfileProductInput i : pp.getProfileInputs()) {
//					if (this.equals(i.getLinkedTo())) {
//						i.setUnitCost(new BigDecimal(this.getUnitCost()));
//						i.setTransport(new BigDecimal(this.transport));
//					}
//				}
//			}
//		}
//	}

	/** Override equals()
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferenceCost other = (ReferenceCost) obj;
		if (!super.equals(other))
			return false;
		if (transport == null) {
			if (other.transport != null)
				return false;
		} else if (!transport.equals(other.transport))
			return false;
		return true;
	}

	@Override
	public Probase getProbase() {
    	return project == null ? profile : project;
	}   
	
	
	
}
