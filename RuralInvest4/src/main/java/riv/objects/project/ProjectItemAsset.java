package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;

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
	private Double donated;
	@Column(name="YEAR_BEGIN")
	private Integer yearBegin;
	@Column(name="OWN_RESOURCES")
	protected Double ownResources;
		
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
		
		public Double getDonated() {
		    return this.donated;
		}

		public void setDonated(Double Donated) {
		    this.donated = Donated;
		}

		public java.lang.Integer getYearBegin() {
		    return this.yearBegin;
		}

		public void setYearBegin(java.lang.Integer YearBegin) {
		    this.yearBegin = YearBegin;
		}

		public Double getFinanced() {
			   if (ownResources==null || donated==null) return 0.0;
			   return (getTotal() - ownResources - this.donated);
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
	   
	   @Override
	   public ProjectItemAsset copy() {
		   ProjectItemAsset item = new ProjectItemAsset();
		   item.setDescription(description);
		   item.setDonated(donated);
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
				donated.equals(x.donated) &&
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
		    if (donated!=null) code = multiplier * code + donated.intValue();
		    if (yearBegin!=null) code = multiplier * code + yearBegin;
		    if (ownResources!=null) code = multiplier * code + ownResources.intValue();
		    
		    return code;
		}
}
