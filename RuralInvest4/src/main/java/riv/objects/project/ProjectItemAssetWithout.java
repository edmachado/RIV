package riv.objects.project;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;

import org.hibernate.annotations.Formula;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

/**
 * Asset cost associated to a Project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("11")
public class ProjectItemAssetWithout extends ProjectItem implements ProjectInvestment {
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
	
	@Formula(value="(SELECT ISNULL(SUM(d.amount),0) FROM project_item_donation d WHERE d.item_id=proj_item_id)")
	private Double donated;
	public Double getDonated() {
		try {
			donations.size();
			donated=0.0;
			for (double val : donations.values()) {
				donated+=val;
			}
			
		} catch (Exception e) {
			
		}
		return donated;
	}

	@ElementCollection(fetch=FetchType.LAZY)
	@MapKeyColumn(name="donor_order_by")
	@Column(name="amount")
	@CollectionTable(name="PROJECT_ITEM_DONATION", joinColumns=@JoinColumn(name="item_id"))
	private Map<Integer,Double> donations = new HashMap<Integer, Double>();
	
	public Map<Integer,Double> getDonations() { return donations; }
	public void setDonations(Map<Integer,Double> donations)  { 
		// required for XML Encoder, not used elsewhere
		throw new RuntimeException("setDonations() field should not be used."); 
	}
	
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
				double yearsLeft = econLife-(econLife-yearBegin-1)%econLife;
				if (yearsLeft==econLife) {
					yearsLeft=0;
				}
				return unitNum*annualReserve*yearsLeft+unitNum*salvage;
			} else {
				return 0.0;
			}
		}

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
	   
	   public String testingProperties(RivConfig rivConfig) {
			String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(unitNum)+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".total="+cf.formatCurrency(getTotal(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".ownResources="+cf.formatCurrency(ownResources, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".donated="+cf.formatCurrency(donated, CurrencyFormat.ALL)+lineSeparator);
		   for (int i=0;i<getProject().getDonors().size();i++) {
			   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".donations."+(i+1)+"="+cf.formatCurrency(donations.containsKey(i) ? donations.get(i) : 0.0, CurrencyFormat.ALL)+lineSeparator);
		   }
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".financed="+cf.formatCurrency(getFinanced(), CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".econLife="+econLife+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".maintCost="+cf.formatCurrency(maintCost, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".salvage="+cf.formatCurrency(salvage, CurrencyFormat.ALL)+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".replace="+(replace?"Yes":"No")+lineSeparator);
		   sb.append("step7.assetWo."+(this.getOrderBy()+1)+".yearBegin="+yearBegin+lineSeparator);
		   return sb.toString();
	   }
	   
	   @Override
	   public ProjectItemAssetWithout copy() {
		   ProjectItemAssetWithout item = new ProjectItemAssetWithout();
		   item.setDescription(description);
//		   item.setDonated(donated);
		   item.setEconLife(econLife);
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
		   
		   for (Integer donorOrder : donations.keySet()) {
			  item.getDonations().put(donorOrder, donations.get(donorOrder));
		   }
		   return item;
	   }

	   @Override
		public boolean equals(Object obj) {
			if (!super.equals(obj)) return false;
			ProjectItemAssetWithout x = (ProjectItemAssetWithout)obj;
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
