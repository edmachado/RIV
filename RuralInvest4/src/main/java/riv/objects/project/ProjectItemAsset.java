package riv.objects.project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

/**
 * Asset cost associated to a Project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("0")
public class ProjectItemAsset extends ProjectItem implements ProjectInvestment {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;
	@Column(name="ECON_LIFE")
	private Integer econLife;
	@Column(name="MAINT_COST")
	private Double maintCost;
	private Double salvage;
	private boolean replace=true;
	@Column(name="YEAR_BEGIN")
	private Integer yearBegin;
	@Column(name="OWN_RESOURCES")
	protected Double ownResources;
	
	
//	private Double donated;
	public Double getDonated() {
//		if (donated==null) {
		double donated = 0.0;
		for (double d : donations.values()) {
			donated+=d;
		}
		return donated;
	}
	
	@ElementCollection(fetch=FetchType.EAGER)
	@MapKeyColumn(name="donor_id")
	@Column(name="amount")
	@CollectionTable(name="PROJECT_ITEM_DONATION", joinColumns=@JoinColumn(name="item_id"))
	Map<Integer,Double> donations = new HashMap<Integer,Double>();
	
	public Map<Integer,Double> getDonations() { return donations; }
	
//	@OneToMany(mappedBy="projectItem", orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.EAGER)
//	@MapKeyColumn(name="donor_id")
//	@MapKey(name="donorId")
//	private Map<Integer,ProjectItemDonation> donations;
	
		public Project getProject () {
			return this.project;
		}

		public void setProject (Project project) {
			this.project = project;
		}
		
		public Double getTotal() {
			if (getUnitNum()==null || this.getUnitCost()==null) return 0.0;
			return this.getUnitCost()*this.getUnitNum();
		}
		public Double getResidual() {
			if (replace || yearBegin-1+econLife>this.getProject().getDuration()) {
				double annualReserve = (unitCost-salvage)/econLife;
				double yearsLeft = econLife-(this.getProject().getDuration()-yearBegin-1)%econLife;
				if (yearsLeft==econLife) {
					yearsLeft=0;
				}
				return unitNum*annualReserve*yearsLeft+unitNum*salvage;
			} else {
				return 0.0;
			}
		}
		
//		public Double getDonated() {
//		    return this.donated;
//		}
//
//		public void setDonated(Double Donated) {
//		    this.donated = Donated;
//		}

		public java.lang.Integer getYearBegin() {
		    return this.yearBegin;
		}

		public void setYearBegin(java.lang.Integer YearBegin) {
		    this.yearBegin = YearBegin;
		}

		public Double getFinanced() {
			   if (ownResources==null || getDonated()==null) return 0.0;
			   return (getTotal() - ownResources - getDonated());
		   }
		public void setOwnResources(Double ownResources) {
			this.ownResources = ownResources;
		}

		public Double getOwnResources() {
			return ownResources;
		}

	 public Integer getEconLife () {
	        return this.econLife;
	    }
	    
	   public void setEconLife (Integer EconLife) {
	        this.econLife = EconLife;
	    }
	    
	    public Double getMaintCost () {
	        return this.maintCost;
	    }
	    
	   public void setMaintCost (Double MaintCost) {
	        this.maintCost = MaintCost;
	    }
	    
	    public Double getSalvage () {
	        return this.salvage;
	    }
	    
	   public void setSalvage (Double Salvage) {
	        this.salvage = Salvage;
	    }
	    
	    public boolean getReplace () {
	        return this.replace;
	    }
	    
	   public void setReplace (boolean Replace) {
	        this.replace = Replace;
	    }
	   

//	public Map<Integer,Double> getDonations() {
//		return donations;
//	}
//	public Map<String, ProjectItemDonation> getDonations2() {
//		HashMap<String, ProjectItemDonation> foo = new HashMap<String, ProjectItemDonation>();
//		for (Integer i : donations.keySet()) {
//			foo.put(i.toString(), donations.get(i));
//		}
//		return foo;
//	}
	
//	public void addDonation(Donor donor, double amount) {
//		ProjectItemDonation d = new ProjectItemDonation();
//		d.setDonor(donor);
//		d.setAmount(amount);
//		donations.put(donor.getId(), d);
//		donated=null;
//	}

//	   @Override
//	public void setDonations(Set<ProjectItemDonation> donations) {
//		this.donations = donations;
//	}

	public String testingProperties(RivConfig rivConfig) {
			String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(ownResources, CurrencyFormat.ALL)+lineSeparator);
	//	   sb.append("step7.asset."+(this.getOrderBy()+1)+".donated="+cf.formatCurrency(donated, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".financed="+cf.formatCurrency(getFinanced(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".econLife="+econLife+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".maintCost="+cf.formatCurrency(maintCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".salvage="+cf.formatCurrency(salvage, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".replace="+(replace?"Yes":"No")+lineSeparator);
		   sb.append("step7.asset."+(this.getOrderBy()+1)+".yearBegin="+yearBegin+lineSeparator);
		   return sb.toString();
	   }
	   
	   @Override
	   public ProjectItemAsset copy() {
		   ProjectItemAsset item = new ProjectItemAsset();
		   item.setDescription(description);
//		   item.setDonated(donated);
		   item.setEconLife(econLife);
		   //item.setExportLinkedTo(exportLinkedTo);
		   item.setLinkedTo(getLinkedTo());
		   item.setMaintCost(maintCost);
		   item.setOwnResources(ownResources);
		   item.setProject(project);
		   item.setReplace(replace);
		   item.setSalvage(salvage);
		   item.setUnitCost(unitCost);
		   item.setUnitNum(unitNum);
		   item.setUnitType(unitType);
		   item.setYearBegin(yearBegin);
		   
		   item.setOrderBy(this.getOrderBy());
		   return item;
	   }

	   @Override
		public boolean equals(Object obj) {
			if (!super.equals(obj)) return false;
			ProjectItemAsset x = (ProjectItemAsset)obj;
			boolean isEqual = econLife.equals(x.econLife) &&
				salvage.equals(x.salvage) &&
				maintCost.equals(x.maintCost) &&
				replace==x.replace &&
//				donated.equals(x.donated) &&
				yearBegin.equals(x.yearBegin) &&
				ownResources.equals(x.ownResources);
			return isEqual;
		}
		
		@Override
		public int hashCode() {
			int code = super.hashCode();
			final int multiplier = 23;
		    code = replace ? multiplier * code + 1 : code;
		    if (econLife!=null) code = multiplier * code + econLife;
		    if (salvage!=null) code = multiplier * code + salvage.intValue();
		    if (maintCost!=null) code = multiplier * code + maintCost.intValue();
//		    if (donated!=null) code = multiplier * code + donated.intValue();
		    if (yearBegin!=null) code = multiplier * code + yearBegin;
		    if (ownResources!=null) code = multiplier * code + ownResources.intValue();
		    
		    return code;
		}
}
