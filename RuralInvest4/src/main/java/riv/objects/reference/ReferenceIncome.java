package  riv.objects.reference;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import riv.objects.Probase;
import riv.objects.project.*;
import riv.objects.profile.*;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;

/**
 * An input item
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("1")
public class ReferenceIncome extends ReferenceItem {
    
	private static final long serialVersionUID = 1L;
	private Double transport;
	@ManyToOne
	@JoinColumn(name="PROFILE_ID", nullable=true)
	private Profile profile; 
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=true)
	protected Project project;

	public ReferenceIncome() {
		super();
	}

	public ReferenceIncome(Integer costInputUd) {
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
	
	
	@Override
	public Probase getProbase() {
    	return project == null ? profile : project;
	}  
	
	public String testingProperties(CurrencyFormatter cf) {
		StringBuilder sb = new StringBuilder();
		String base="step10.income."+(this.getOrderBy()+1)+".";
		sb.append(base+"description="+this.getDescription()+System.lineSeparator());
		sb.append(base+"unitType="+this.getUnitType()+System.lineSeparator());
		sb.append(base+"unitCost="+cf.formatCurrency(this.getUnitCost(), CurrencyFormat.ALL)+System.lineSeparator());
		if (transport!=null) {
			sb.append(base+"transport="+cf.formatCurrency(this.getTransport(), CurrencyFormat.ALL)+System.lineSeparator());
		}
		return sb.toString();
	}
	
	public ReferenceIncome copy() {
		ReferenceIncome newItem = new ReferenceIncome();
		 newItem.setDescription(this.getDescription());
		 newItem.setOrderBy(this.getOrderBy());
		 newItem.setTransport(this.getTransport());
		 newItem.setUnitCost(this.getUnitCost());
		 newItem.setUnitType(this.getUnitType());
		 return newItem;
	}
	
//	public void updateProbase() {
//		if (this.getProject()!=null) {
//			for (Block b : this.getProject().getBlocks()) {
//				for (BlockIncome i : b.getIncomes()) {
//					if (this.equals(i.getLinkedTo())) {
//						i.setUnitCost(new BigDecimal(this.getUnitCost()));
//						i.setTransport(new BigDecimal(transport));
//					}
//				}
//			}
//		} else if (this.getProfile() != null) {
//			for (ProfileProduct pp : this.getProfile().getProducts()) {
//				for (ProfileProductIncome i : pp.getProfileIncomes()) {
//					if (this.equals(i.getLinkedTo())) {
//						i.setUnitCost(new BigDecimal(this.getUnitCost()));
//						i.setTransport(new BigDecimal(this.getTransport()));
//					}
//				}
//			}
//		}
//	}
//	
}
