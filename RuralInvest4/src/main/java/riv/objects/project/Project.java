package riv.objects.project;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Where;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import riv.objects.Probase;
import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Setting;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.project.ProjectFinanceData.AnalysisType;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceLabour;
import riv.util.Calculator;

/**
 * A RuralInvest Project
 */
@Entity
@Table(name="PROJECT")
public class Project extends Probase implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PROJECT_ID", nullable = false)
	private Integer projectId;
	
	// FOREIGN-KEY INDICATORS
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FIELD_OFFICE")
	private FieldOffice fieldOffice;
	@ManyToOne //(fetch=FetchType.LAZY)
	@JoinColumn(name="TECHNICIAN")
	private riv.objects.config.User technician;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJ_CATEGORY")
	private ProjectCategory projCategory;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BENEFICIARY")
	private Beneficiary beneficiary;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ENVIRO_CATEGORY")
	private EnviroCategory enviroCategory;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STATUS")
	private Status status;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="APP_CONFIG1")
	 private AppConfig1 appConfig1;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="APP_CONFIG2")
	private AppConfig2 appConfig2;
	
	//FIELDS
	@Column(name="UNIQUE_ID")
	private byte[] uniqueId;
	@Column(name="PROJECT_NAME")
	private String projectName;
	@Column(name="USER_CODE")
	private String userCode;
	@Column(name="INCOME_GEN")
	private boolean incomeGen;
	@Column(name="STARTUP_MONTH")
	private Integer startupMonth;
	private boolean shared=true;
	// generic: if project has been exported w/ generic settings
	@Transient
	private boolean generic;
	@Transient
	private Double rivVersion;
	@Column(name="WIZARD_STEP")
	private Integer wizardStep;
	@Column(name="EXCH_RATE")
	private Double ExchRate;
	@Column(name="WITH_WITHOUT")
	private boolean withWithout;
	@Column(name="PREP_DATE")
	@DateTimeFormat(iso=ISO.DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date prepDate;
	@Column(name="LAST_UPDATE")
	@DateTimeFormat(iso=ISO.DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date lastUpdate;
	@Column(name="CREATED_BY")
	private String createdBy;
	private String location1;
	private String location2;
	private String location3;
	@Column(name="BENEF_NAME")
	private String benefName;
	@Size(max=10000)
	@Column(name="BENEF_DESC")
	private String benefDesc;
	private Integer duration;
	@Size(max=10000)
	@Column(name="PROJ_DESC")
	private String projDesc;
	@Column(name="BENE_DIRECT_NUM")
	private Integer beneDirectNum;
	@Column(name="BENE_DIRECT_MEN")
	private Integer beneDirectMen;
	@Column(name="BENE_DIRECT_WOMEN")
	private Integer beneDirectWomen;
	@Column(name="BENE_DIRECT_CHILD")
	private Integer beneDirectChild;
	@Column(name="BENE_INDIRECT_NUM")
	private Integer beneIndirectNum;
	@Column(name="BENE_INDIRECT_MEN")
	private Integer beneIndirectMen;
	@Column(name="BENE_INDIRECT_WOMEN")
	private Integer beneIndirectWomen;
	@Column(name="BENE_INDIRECT_CHILD")
	private Integer beneIndirectChild;
	@Size(max=10000)
	private String justification;
	@Size(max=10000)
	private String activities;
	@Size(max=10000)
	private String technology;
	@Size(max=10000)
	private String requirements;
	@Size(max=10000)
	private String sustainability;
	@Size(max=10000)
	private String market;
	@Size(max=10000)
	@Column(name="ENVIRO_IMPACT")
	private String enviroImpact;
	@Size(max=10000)
	private String organization;
	@Size(max=10000)
	private String assumptions;
	@Column(name="LOAN1_INTEREST")
	private Double loan1Interest;
	@Column(name="LOAN1_DURATION")
	private Integer loan1Duration;
	@Column(name="LOAN1_GRACE_CAPITAL")
	 private Integer loan1GraceCapital;
	@Column(name="LOAN1_GRACE_INTEREST")
	private Integer loan1GraceInterest;
	@Column(name="LOAN2_AMT")
	private Double loan2Amt;
	@Column(name="LOAN2_INTEREST")
	private Double loan2Interest;
	@Column(name="LOAN2_DURATION")
	private Integer loan2Duration;
	@Column(name="LOAN2_GRACE_CAPITAL")
	private Integer loan2GraceCapital;
	@Column(name="LOAN2_GRACE_INTEREST")
	private Integer loan2GraceInterest;
	@Column(name="LOAN2_INIT_PERIOD")
	private Integer loan2InitPeriod;
	@Column(name="INFLATION_ANNUAL")
	private Double inflationAnnual;
	@Column(name="CAPITAL_INTEREST")
	private Double capitalInterest;
	@Column(name="CAPITAL_DONATE")
	private Double capitalDonate;
	@Column(name="CAPITAL_OWN")
	private Double capitalOwn;
	@Column(name="RECC_CODE")
	private Integer reccCode;
	@Column(name="RECC_DATE")
	@DateTimeFormat(iso=ISO.DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private java.util.Date reccDate;
	@Size(max=10000)
	@Column(name="RECC_DESC")
	private String reccDesc;
	@Column(name="PER_YEAR_CONTRIB")
	private boolean perYearContributions;
	
	@Column(name="ADMIN_MISC1")
	private String adminMisc1;
	@Column(name="ADMIN_MISC2")
	private String adminMisc2;
	@Column(name="ADMIN_MISC3")
	private String adminMisc3;
	

	@Transient
	private BigDecimal wcAmountRequired;
	@Transient
	private int wcFinancePeriod;
	

	@OneToMany(mappedBy="project", orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='0'")
	private Set<Block> blocks;
	@OneToMany(mappedBy="project", orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='1'")
	private Set<BlockWithout> blocksWithout;
	
	@OneToMany(mappedBy="project", targetEntity=ProjectItemAsset.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='0'")
	private Set<ProjectItemAsset> assets;
	@OneToMany(mappedBy="project", targetEntity=ProjectItemAssetWithout.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='11'")
	private Set<ProjectItemAssetWithout> assetsWithout;
	
	@OneToMany(mappedBy="project", targetEntity=ProjectItemGeneral.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='2'")
	private Set<ProjectItemGeneral> generals;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemLabour.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='1'")
	private Set<ProjectItemLabour> labours;
	@OneToMany(mappedBy="project", targetEntity=ProjectItemLabourWithout.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='12'")
	private Set<ProjectItemLabourWithout> laboursWithout;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemPersonnel.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='3'")
	private Set<ProjectItemPersonnel> personnels;
	
	@OneToMany(mappedBy="project", targetEntity=ProjectItemService.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='4'")
	private Set<ProjectItemService> services;
	@OneToMany(mappedBy="project", targetEntity=ProjectItemServiceWithout.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='13'")
	private Set<ProjectItemServiceWithout> servicesWithout;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemContribution.class, orphanRemoval=true, cascade = CascadeType.ALL)
	//@OrderBy({"YEAR_BEGIN","ORDER_BY"})
	@Where(clause="class='5'")
	private Set<ProjectItemContribution> contributions;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemNongenMaterials.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='9'")
	private Set<ProjectItemNongenMaterials> nongenMaterials;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemNongenLabour.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='8'")
	private Set<ProjectItemNongenLabour> nongenLabours;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemNongenMaintenance.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='10'")
	private Set<ProjectItemNongenMaintenance> nongenMaintenance;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemGeneralWithout.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='6'")
	private Set<ProjectItemGeneralWithout> generalWithouts;

	@OneToMany(mappedBy="project", targetEntity=ProjectItemPersonnelWithout.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='7'")
	private Set<ProjectItemPersonnelWithout> personnelWithouts;

	@OneToMany(mappedBy="project", targetEntity=ReferenceCost.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='0'")
	private Set<ReferenceCost> refCosts;

	@OneToMany(mappedBy="project", targetEntity=ReferenceIncome.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='1'")
	private Set<ReferenceIncome> refIncomes;

	@OneToMany(mappedBy="project", targetEntity=ReferenceLabour.class, orphanRemoval=true, cascade = CascadeType.ALL)
	@OrderBy("ORDER_BY")
	@Where(clause="class='2'")
	private Set<ReferenceLabour> refLabours;
	
	
	/* Calculated fields for working capital estimation */
	public Double getLoan1Amt() {
		return (loan2Amt==null) ? null : getInvestmentTotal()-loan2Amt;
	}
	
	public BigDecimal getWcAmountRequired() {
		return wcAmountRequired;
	}
	public void setWcAmountRequired(BigDecimal wcAmountRequired) {
		this.wcAmountRequired = wcAmountRequired;
	}
	public int getWcFinancePeriod() {
		return wcFinancePeriod;
	}

	public void setWcFinancePeriod(int wcFinancePeriod) {
		this.wcFinancePeriod = wcFinancePeriod;
	}

	public BigDecimal getWcAmountFinanced() {
		return (wcAmountRequired==null || capitalDonate==null || capitalOwn==null) ? null : wcAmountRequired.subtract(BigDecimal.valueOf(capitalDonate+capitalOwn));
	}

/**
 * Calculates the project's total investment costs, for use in determining the capital required.
 * @return total investment cost
 */
public double getInvestmentTotal() {
	double investTotal=0;
	// calculate investmentTotal
	for(ProjectItemAsset ass: assets)
		investTotal+=ass.getUnitCost()*ass.getUnitNum()-ass.getOwnResources()-ass.getDonated();
	for(ProjectItemLabour lab: labours)
		investTotal+=lab.getUnitCost()*lab.getUnitNum()-lab.getOwnResources()-lab.getDonated();
	for(ProjectItemService ser: services)
		investTotal+=ser.getUnitCost()*ser.getUnitNum()-ser.getOwnResources()-ser.getDonated();
	return investTotal;
}

	public HashMap<Integer, SortedSet<ProjectItemContribution>> getContributionsByYear() {
		HashMap<Integer, SortedSet<ProjectItemContribution>> contribsByYear = new HashMap<Integer, SortedSet<ProjectItemContribution>>();
		for (int i=1; i<=duration; i++) {
			contribsByYear.put(i, new TreeSet<ProjectItemContribution>());
		}
		for (ProjectItemContribution contrib : contributions) {
			contribsByYear.get(contrib.getYear()).add(contrib);
		} 
		return contribsByYear;
	}

    // Constructors
	/** default constructor */
    public Project() {
    }
    
    /** constructor with id */
    public Project(java.lang.Integer ProjectId) {
        this.projectId = ProjectId;
    }
   
    // Common properties for Probase interface
    public Integer getProId() {
    	return this.projectId;
    }
    public boolean isProject() {
    	return true;
    }

    // Property accessors
    public java.lang.Integer getProjectId () {
        return this.projectId;
    }
    
   public void setProjectId (java.lang.Integer ProjectId) {
        this.projectId = ProjectId;
    }
    public void setUniqueId(byte[] uniqueId) {
    	this.uniqueId = uniqueId;
	}
	public byte[] getUniqueId() {
		return uniqueId;
	}
    
    public FieldOffice getFieldOffice () {
        return this.fieldOffice;
    }
    
   public void setFieldOffice (FieldOffice FieldOffice) {
        this.fieldOffice = FieldOffice;
    }
    
    public User getTechnician () {
        return this.technician;
    }
    
   public void setTechnician (User Technician) {
        this.technician = Technician;
    }
    
    public java.lang.String getProjectName () {
        return this.projectName;
    }
    
   public void setProjectName (java.lang.String ProfileName) {
        this.projectName = ProfileName;
    }
    public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	
	public String getUserCode() {
		return userCode;
	}
    public boolean getIncomeGen () {
        return this.incomeGen;
    }
    
   public void setIncomeGen (boolean IncomeGen) {
        this.incomeGen = IncomeGen;
    }
    public void setShared(boolean shared) {
		this.shared = shared;
	}
	
	public boolean isShared() {
		return shared;
	}

	public void setGeneric(boolean generic) {
		this.generic = generic;
	}
	public boolean isGeneric() {
		return generic;
	}
	public Double getRivVersion() {
		return rivVersion;
	}

	public void setRivVersion(Double rivVersion) {
		this.rivVersion = rivVersion;
	}

	public void setWizardStep(Integer wizardStep) {
		this.wizardStep = wizardStep;
	}
	
	public Integer getWizardStep() {
		return wizardStep;
	}

	public Double getExchRate () {
        return this.ExchRate;
    }
    
   public void setExchRate (Double ExchRate) {
        this.ExchRate = ExchRate;
    }
    
   public java.util.Date getPrepDate () {
        return this.prepDate;
    }
    
   public void setPrepDate (java.util.Date PrepDate) {
        this.prepDate = PrepDate;
    }
    public void setCreatedBy(String createdBy) {
	this.createdBy = createdBy;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setLastUpdate(java.util.Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
	
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public java.lang.String getLocation1 () {
        return this.location1;
    }
    
   public void setLocation1 (java.lang.String Location1) {
        this.location1 = Location1;
    }
   public java.lang.String getLocation2 () {
        return this.location2;
    }
    
   public void setLocation2 (java.lang.String Location2) {
        this.location2 = Location2;
    }
    public java.lang.String getLocation3 () {
        return this.location3;
    }
    
   public void setLocation3 (java.lang.String Location3) {
        this.location3 = Location3;
    }
    public java.lang.String getBenefName () {
        return this.benefName;
    }
    
   public void setBenefName (java.lang.String BenefName) {
        this.benefName = BenefName;
    }
    public java.lang.String getBenefDesc () {
        return this.benefDesc;
    }
    
   public void setBenefDesc (java.lang.String BenefDesc) {
        this.benefDesc = BenefDesc;
    }
    
    public EnviroCategory getEnviroCategory () {
        return enviroCategory;
    }
    
   public void setEnviroCategory (EnviroCategory enviroCategory) {
        this.enviroCategory = enviroCategory;
    }
    public java.lang.Integer getDuration () {
        return this.duration;
    }
    
   public void setDuration (java.lang.Integer Duration) {
        this.duration = Duration;
    }
    
    public java.lang.String getProjDesc () {
        return this.projDesc;
    }
    
   public void setProjDesc (java.lang.String ProjDesc) {
        this.projDesc = ProjDesc;
    }
    
    public Integer getBeneDirectNum () {
        return this.beneDirectNum;
    }
    
   public void setBeneDirectNum (Integer BeneDirectNum) {
        this.beneDirectNum = BeneDirectNum;
    }
   
    public java.lang.Integer getBeneDirectMen () {
        return this.beneDirectMen;
    }
    
   public void setBeneDirectMen (java.lang.Integer BeneDirectMen) {
        this.beneDirectMen = BeneDirectMen;
    }
    
    public java.lang.Integer getBeneDirectWomen () {
        return this.beneDirectWomen;
    }
    
   public void setBeneDirectWomen (java.lang.Integer BeneDirectWomen) {
        this.beneDirectWomen = BeneDirectWomen;
    }
   
    public java.lang.Integer getBeneDirectChild () {
        return this.beneDirectChild;
    }
    
   public void setBeneDirectChild (java.lang.Integer BeneDirectChild) {
        this.beneDirectChild = BeneDirectChild;
    }
   
   public Integer getBeneIndirectNum () {
        return this.beneIndirectNum;
    }
    
   public void setBeneIndirectNum (Integer BeneIndirectNum) {
        this.beneIndirectNum = BeneIndirectNum;
    }
   public java.lang.Integer getBeneIndirectMen () {
        return this.beneIndirectMen;
    }
    
   public void setBeneIndirectMen (java.lang.Integer BeneIndirectMen) {
        this.beneIndirectMen = BeneIndirectMen;
    }
    
    public java.lang.Integer getBeneIndirectWomen () {
        return this.beneIndirectWomen;
    }
    
   public void setBeneIndirectWomen (java.lang.Integer BeneIndirectWomen) {
        this.beneIndirectWomen = BeneIndirectWomen;
    }
    
    public java.lang.Integer getBeneIndirectChild () {
        return this.beneIndirectChild;
    }
    
   public void setBeneIndirectChild (java.lang.Integer BeneIndirectChild) {
        this.beneIndirectChild = BeneIndirectChild;
    }
    
    public java.lang.String getJustification () {
        return this.justification;
    }
    
   public void setJustification (java.lang.String Justification) {
        this.justification = Justification;
    }
    
    public java.lang.String getActivities () {
        return this.activities;
    }
    
   public void setActivities (java.lang.String Activities) {
        this.activities = Activities;
    }
    
    public java.lang.String getTechnology () {
        return this.technology;
    }
    
   public void setTechnology (java.lang.String Technology) {
        this.technology = Technology;
    }
    
    public java.lang.String getRequirements () {
        return this.requirements;
    }
    
   public void setRequirements (java.lang.String Requirements) {
        this.requirements = Requirements;
    }
    
    public java.lang.String getSustainability () {
        return this.sustainability;
    }
    
   public void setSustainability (java.lang.String Sustainability) {
        this.sustainability = Sustainability;
    }
    
    public java.lang.String getMarket () {
        return this.market;
    }
    
   public void setMarket (java.lang.String Market) {
        this.market = Market;
    }
    
    public java.lang.String getEnviroImpact () {
        return this.enviroImpact;
    }
    
   public void setEnviroImpact (java.lang.String EnviroImpact) {
        this.enviroImpact = EnviroImpact;
    }
    
    public java.lang.String getOrganization () {
        return this.organization;
    }
    
   public void setOrganization (java.lang.String Organization) {
        this.organization = Organization;
    }
    
    public java.lang.String getAssumptions () {
        return this.assumptions;
    }
    
   public void setAssumptions (java.lang.String Assumptions) {
        this.assumptions = Assumptions;
    }
    
//    public java.lang.String getCategoryOther () {
//        return this.categoryOther;
//    }
//    
//   public void setCategoryOther (java.lang.String CategoryOther) {
//        this.categoryOther = CategoryOther;
//    }
    public Double getLoan1Interest () {
        return this.loan1Interest;
    }
    
   public void setLoan1Interest (Double Loan1Interest) {
        this.loan1Interest = Loan1Interest;
    }
    
    public java.lang.Integer getLoan1Duration () {
        return this.loan1Duration;
    }
    
   public void setLoan1Duration (java.lang.Integer Loan1Duration) {
        this.loan1Duration = Loan1Duration;
    }
    
    public java.lang.Integer getLoan1GraceCapital () {
        return this.loan1GraceCapital;
    }
    
   public void setLoan1GraceCapital (java.lang.Integer Loan1GraceCapital) {
        this.loan1GraceCapital = Loan1GraceCapital;
    }
    
    public java.lang.Integer getLoan1GraceInterest () {
        return this.loan1GraceInterest;
    }
    
   public void setLoan1GraceInterest (java.lang.Integer Loan1GraceInterest) {
        this.loan1GraceInterest = Loan1GraceInterest;
    }
    
    public Double getLoan2Amt () {
        return this.loan2Amt;
    }
    
   public void setLoan2Amt (Double Loan2Amt) {
        this.loan2Amt = Loan2Amt;
    }
    
    public Double getLoan2Interest () {
        return this.loan2Interest;
    }
    
   public void setLoan2Interest (Double Loan2Interest) {
        this.loan2Interest = Loan2Interest;
    }
    
    public java.lang.Integer getLoan2Duration () {
        return this.loan2Duration;
    }
    
   public void setLoan2Duration (java.lang.Integer Loan2Duration) {
        this.loan2Duration = Loan2Duration;
    }
    public java.lang.Integer getLoan2GraceCapital () {
        return this.loan2GraceCapital;
    }
    
   public void setLoan2GraceCapital (java.lang.Integer Loan2GraceCapital) {
        this.loan2GraceCapital = Loan2GraceCapital;
    }
    
    public java.lang.Integer getLoan2GraceInterest () {
        return this.loan2GraceInterest;
    }
    
   public void setLoan2GraceInterest (java.lang.Integer Loan2GraceInterest) {
        this.loan2GraceInterest = Loan2GraceInterest;
    }
    
    public java.lang.Integer getLoan2InitPeriod () {
        return this.loan2InitPeriod;
    }
    
   public void setLoan2InitPeriod (java.lang.Integer Loan2InitPeriod) {
        this.loan2InitPeriod = Loan2InitPeriod;
    }
    
    public Double getInflationAnnual () {
        return this.inflationAnnual;
    }
    
   public void setInflationAnnual (Double InflationAnnual) {
        this.inflationAnnual = InflationAnnual;
    }
    
    public Double getCapitalInterest () {
        return this.capitalInterest;
    }
    
   public void setCapitalInterest (Double CapitalInterest) {
        this.capitalInterest = CapitalInterest;
    }
    
    public Double getCapitalDonate () {
        return this.capitalDonate;
    }
    
   public void setCapitalDonate (Double CapitalDonate) {
        this.capitalDonate = CapitalDonate;
    }

	public void setCapitalOwn(Double capiralOwn) {
		capitalOwn = capiralOwn;
	}
	public Double getCapitalOwn() {
		return capitalOwn;
	}
	public void setAssets(Set<ProjectItemAsset> assets) {
		this.assets = assets;
	}
	
	public Set<ProjectItemAsset> getAssets() {
		return assets;
	}
	public void addAsset(ProjectItemAsset asset) {
		asset.setProject(this);
		assets.add(asset);
	}
	
	public void setAssetsWithout(Set<ProjectItemAssetWithout> assets) {
		this.assetsWithout = assets;
	}
	
	public Set<ProjectItemAssetWithout> getAssetsWithout() {
		return assetsWithout;
	}
	public void addAssetWithout(ProjectItemAssetWithout asset) {
		asset.setProject(this);
		assetsWithout.add(asset);
	}
	
	public void setGenerals(Set<ProjectItemGeneral> generals) {
		this.generals = generals;
	}
	
	public Set<ProjectItemGeneral> getGenerals() {
		return generals;
	}
	public void addGeneral(ProjectItemGeneral gen) {
		gen.setProject(this);
		generals.add(gen);
	}
	
	public void setLabours(Set<ProjectItemLabour> labours) {
		this.labours = labours;
	}
	
	public Set<ProjectItemLabour> getLabours() {
		return labours;
	}
	public void addLabour(ProjectItemLabour labour) {
		labour.setProject(this);
		labours.add(labour);
	}
	
	public void setLaboursWithout(Set<ProjectItemLabourWithout> labours) {
		this.laboursWithout = labours;
	}
	
	public Set<ProjectItemLabourWithout> getLaboursWithout() {
		return laboursWithout;
	}
	public void addLabourWithout(ProjectItemLabourWithout labour) {
		labour.setProject(this);
		laboursWithout.add(labour);
	}
	
	public void setPersonnels(Set<ProjectItemPersonnel> personnels) {
		this.personnels = personnels;
	}
	public void addPersonnel(ProjectItemPersonnel pers) {
		pers.setProject(this);
		personnels.add(pers);
	}
	
	public Set<ProjectItemPersonnel> getPersonnels() {
		return personnels;
	}
	
	public void setServices(Set<ProjectItemService> services) {
		this.services = services;
	}
	public Set<ProjectItemService> getServices() {
		return services;
	}
	public void addService(ProjectItemService service) {
		service.setProject(this);
		services.add(service);
	}
	
	public void setServicesWithout(Set<ProjectItemServiceWithout> services) {
		this.servicesWithout = services;
	}
	public Set<ProjectItemServiceWithout> getServicesWithout() {
		return servicesWithout;
	}
	public void addServiceWithout(ProjectItemServiceWithout service) {
		service.setProject(this);
		servicesWithout.add(service);
	}
	
	public void setContributions(Set<ProjectItemContribution> contributions) {
		this.contributions = contributions;
	}
	public Set<ProjectItemContribution> getContributions() {
		return contributions;
	}
	public void addContribution(ProjectItemContribution cont) {
		cont.setProject(this);
		contributions.add(cont);
	}
	public void setStartupMonth(Integer startupMonth) {
		this.startupMonth = startupMonth;
	}
	
	public Integer getStartupMonth() {
		return startupMonth;
	}
	
	public void setProjCategory(ProjectCategory projCategory) {
		this.projCategory = projCategory;
	}
	
	public ProjectCategory getProjCategory() {
		return projCategory;
	}
	
	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}
	
	public Beneficiary getBeneficiary() {
		return beneficiary;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setWithWithout(boolean withWithout) {
		this.withWithout = withWithout;
	}
	
	public boolean isWithWithout() {
		return withWithout;
	}
	
	public void setBlocks(Set<Block> blocks) {
		this.blocks = blocks;
	}
	
	public Set<Block> getBlocks() {
		return blocks;
	}
	
	public void addBlock(BlockBase block) {
		block.setProject(this);
		if (block.getClass()==Block.class) {
			blocks.add((Block)block);
		} else {
			blocksWithout.add((BlockWithout)block);
		}
	}
	
	public Set<BlockWithout> getBlocksWithout() {
		return blocksWithout;
	}

	public void setBlocksWithout(Set<BlockWithout> blocksWithout) {
		this.blocksWithout = blocksWithout;
	}

	public void setNongenMaterials(Set<ProjectItemNongenMaterials> nongenMaterials) {
		this.nongenMaterials = nongenMaterials;
	}
	public Set<ProjectItemNongenMaterials> getNongenMaterials() {
		return nongenMaterials;
	}
	public void addNongenMaterial(ProjectItemNongenMaterials material) {
		material.setProject(this);
		nongenMaterials.add(material);
	}
	
	public void setNongenLabours(Set<ProjectItemNongenLabour> nongenLabours) {
		this.nongenLabours = nongenLabours;
	}
	public Set<ProjectItemNongenLabour> getNongenLabours() {
		return nongenLabours;
	}
	public void addNongenLabour(ProjectItemNongenLabour labour) {
		labour.setProject(this);
		nongenLabours.add(labour);
	}
	
	public void setNongenMaintenance(Set<ProjectItemNongenMaintenance> nongenMaintenance) {
		this.nongenMaintenance = nongenMaintenance;
	}
	public Set<ProjectItemNongenMaintenance> getNongenMaintenance() {
		return nongenMaintenance;
	}
	public void addNongenMaintenance(ProjectItemNongenMaintenance maintenance) {
		maintenance.setProject(this);
		nongenMaintenance.add(maintenance);
	}
	
	public void setGeneralWithouts(Set<ProjectItemGeneralWithout> generalWithouts) {
		this.generalWithouts = generalWithouts;
	}
	public Set<ProjectItemGeneralWithout> getGeneralWithouts() {
		return generalWithouts;
	}
	public void addGeneralWithout(ProjectItemGeneralWithout gen) {
		gen.setProject(this);
		generalWithouts.add(gen);
	}
	public void setPersonnelWithouts(Set<ProjectItemPersonnelWithout> personnelWithouts) {
		this.personnelWithouts = personnelWithouts;
	}
	public Set<ProjectItemPersonnelWithout> getPersonnelWithouts() {
		return personnelWithouts;
	}
	public void addPersonnelWithout(ProjectItemPersonnelWithout p) {
		p.setProject(this);
		personnelWithouts.add(p);
	}
	public void setRefCosts(Set<ReferenceCost> refCosts) {
		this.refCosts = refCosts;
	}
	public Set<ReferenceCost> getRefCosts() {
		return refCosts;
	}
	public void addReferenceCost(ReferenceCost item) {
		item.setProject(this);
		refCosts.add(item);
	}
	public void setRefIncomes(Set<ReferenceIncome> refIncomes) {
		this.refIncomes = refIncomes;
	}
	public Set<ReferenceIncome> getRefIncomes() {
		return refIncomes;
	}
	public void addReferenceIncome(ReferenceIncome item) {
		item.setProject(this);
		refIncomes.add(item);
	}
	public void setRefLabours(Set<ReferenceLabour> refLabours) {
		this.refLabours = refLabours;
	}
	public Set<ReferenceLabour> getRefLabours() {
		return refLabours;
	}
	public void addReferenceLabour(ReferenceLabour item) {
		item.setProject(this);
		refLabours.add(item);
	}
	public void setReccCode(Integer reccCode) {
		this.reccCode = reccCode;
	}
	public Integer getReccCode() {
		return reccCode;
	}
	public void setReccDate(java.util.Date reccDate) {
		this.reccDate = reccDate;
	}
	public java.util.Date getReccDate() {
		return reccDate;
	}
	public void setReccDesc(String reccDesc) {
		this.reccDesc = reccDesc;
	}
	public String getReccDesc() {
		return reccDesc;
	}
	
	public boolean isPerYearContributions() {
		return perYearContributions;
	}

	public void setPerYearContributions(boolean perYearContributions) {
		this.perYearContributions = perYearContributions;
	}

	public void setAppConfig1(AppConfig1 appConfig1) {
		this.appConfig1 = appConfig1;
	}
	public AppConfig1 getAppConfig1() {
		return appConfig1;
	}
	public void setAppConfig2(AppConfig2 appConfig2) {
		this.appConfig2 = appConfig2;
	}
	public AppConfig2 getAppConfig2() {
		return appConfig2;
	}
	public void setAdminMisc1(String adminMisc1) {
		this.adminMisc1 = adminMisc1;
	}
	public String getAdminMisc1() {
		return adminMisc1;
	}
	public void setAdminMisc2(String adminMisc2) {
		this.adminMisc2 = adminMisc2;
	}
	public String getAdminMisc2() {
		return adminMisc2;
	}
	public void setAdminMisc3(String adminMisc3) {
		this.adminMisc3 = adminMisc3;
	}
	public String getAdminMisc3() {
		return adminMisc3;
	}
	
	
	
	public Integer getBeneDirectTotal() {
		return (beneDirectChild==null || beneDirectMen==null || beneDirectWomen==null) ? null : beneDirectChild+beneDirectMen+beneDirectWomen;
	}
	public Integer getBeneIndirectTotal() {
		return (beneIndirectChild==null || beneIndirectMen==null || beneIndirectWomen==null) ? null : beneIndirectChild+beneIndirectMen+beneIndirectWomen;
	}
	
	
	private double employPerType(BigDecimal unitNum, double qty, String unitType) {
		return employPerType(unitNum.doubleValue(), qty, unitType);
	}
	
	public double employPerType(double unitNum, double qty, String unitType) {
		double employment=0.0;
		if (unitType.equals("0")) { // years
			employment+=unitNum*qty;
		} else if (unitType.equals("1")) { // months (12 per year)
			employment+=unitNum/12*qty;
		} else if (unitType.equals("2")) { // weeks (48 per year)
			employment+=unitNum/48*qty;
		} else if (unitType.equals("3")) { // days (288 per year)
			employment+=unitNum/288*qty;
		}
		return employment;
	}
	
	public double getAnnualEmploymentOperation() {
		double employment=0.0;
		for (Block block : blocks) {
			double qty = block.getPatterns().get(duration).getQty()*block.getCyclePerYear();
			for (BlockLabour lab : block.getLabours()) {
				employment+=employPerType(lab.getUnitNum(), qty, lab.getUnitType());
			}	
		}
		return employment;
	}
	
	public double getAnnualEmploymentInvestment() {
		double employment=0.0;
		
		if (!incomeGen) {
			for (ProjectItemNongenLabour lab : nongenLabours) {
				employment+=employPerType(lab.getUnitNum(), 1.0, lab.getUnitType());
			}
		} else {
			for (ProjectItemLabour lab : labours) {
				employment+=employPerType(lab.getUnitNum(), 1.0, lab.getUnitType());
			}
		}
		return employment;
	}
	
	public double getAnnualEmploymentGeneral() {
		double employment=0.0;
		
		if (incomeGen) {
			for (ProjectItemPersonnel lab : personnels) {
				employment+=employPerType(lab.getUnitNum(), 1.0, lab.getUnitType());
			}
		} else {
			for (ProjectItemNongenLabour lab : nongenLabours) {
				employment+=employPerType(lab.getUnitNum(), 1.0, lab.getUnitType());
			}
		}
		
		return employment;
	}
	
	/**
	 * Calculates annual employment
	 * @return annual employment generated
	 */
	public double getAnnualEmployment() {
		return getAnnualEmploymentInvestment()+getAnnualEmploymentOperation()+getAnnualEmploymentGeneral();
	}
	
	private double round(double d) {
		 BigDecimal bd = new BigDecimal(Double.toString(d));
		    bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP);
		    return bd.doubleValue();
	 }
	 private BigDecimal round(BigDecimal d) {
		 return d.setScale(2, BigDecimal.ROUND_HALF_UP);
	 }
	 
	/**
	 * Converts all currency values according to provided exchange rate
	 * @param exchange
	 */
	public void convertCurrency(Double exchange) {
		//this.ExchRate=1/exchange;
		if (this.incomeGen) {
			capitalDonate=round(capitalDonate*exchange);
			this.loan2Amt=round(loan2Amt*exchange);
		}
		//  step 7
		for (ProjectItemAsset ass : assets) {
			ass.setDonated(round(ass.getDonated()*exchange));
			ass.setMaintCost(round(ass.getMaintCost()*exchange));
			ass.setOwnResources(round(ass.getOwnResources()*exchange));
			ass.setSalvage(round(ass.getSalvage()*exchange));
			ass.setUnitCost(round(ass.getUnitCost()*exchange));
		}
		for (ProjectItemLabour lab : labours) {
			lab.setDonated(round(lab.getDonated()*exchange));
			lab.setOwnResources(round(lab.getOwnResources()*exchange));
			lab.setUnitCost(round(lab.getUnitCost()*exchange));
		}
		for (ProjectItemService serv : services) {
			serv.setDonated(round(serv.getDonated()*exchange));
			serv.setOwnResources(round(serv.getOwnResources()*exchange));
			serv.setUnitCost(round(serv.getUnitCost()*exchange));
		}
		for (ProjectItemAssetWithout ass : assetsWithout) {
			ass.setDonated(round(ass.getDonated()*exchange));
			ass.setMaintCost(round(ass.getMaintCost()*exchange));
			ass.setOwnResources(round(ass.getOwnResources()*exchange));
			ass.setSalvage(round(ass.getSalvage()*exchange));
			ass.setUnitCost(round(ass.getUnitCost()*exchange));
		}
		for (ProjectItemLabourWithout lab : laboursWithout) {
			lab.setDonated(round(lab.getDonated()*exchange));
			lab.setOwnResources(round(lab.getOwnResources()*exchange));
			lab.setUnitCost(round(lab.getUnitCost()*exchange));
		}
		for (ProjectItemServiceWithout serv : servicesWithout) {
			serv.setDonated(round(serv.getDonated()*exchange));
			serv.setOwnResources(round(serv.getOwnResources()*exchange));
			serv.setUnitCost(round(serv.getUnitCost()*exchange));
		}
		for (ProjectItemNongenMaterials nongen : nongenMaterials) {
			nongen.setOther1(round(nongen.getOther1()*exchange));
			nongen.setStatePublic(round(nongen.getStatePublic()*exchange));
			nongen.setUnitCost(round(nongen.getUnitCost()*exchange));
		}
		for (ProjectItemNongenMaintenance nongen : nongenMaintenance) {
			nongen.setOther1(round(nongen.getOther1()*exchange));
			nongen.setStatePublic(round(nongen.getStatePublic()*exchange));
			nongen.setUnitCost(round(nongen.getUnitCost()*exchange));			
		}
		for (ProjectItemNongenLabour nongen : nongenLabours) {
			nongen.setOther1(round(nongen.getOther1()*exchange));
			nongen.setStatePublic(round(nongen.getStatePublic()*exchange));
			nongen.setUnitCost(round(nongen.getUnitCost()*exchange));
		}
		//  step 8
		for (ProjectItemGeneral i : generals) {
			i.setOwnResources(round(i.getOwnResources()*exchange));
			i.setUnitCost(round(i.getUnitCost()*exchange));
		}
		for (ProjectItemPersonnel i : personnels) {
			i.setOwnResources(round(i.getOwnResources()*exchange));
			i.setUnitCost(round(i.getUnitCost()*exchange));
		}
		for (ProjectItemGeneralWithout i : generalWithouts) {
			i.setOwnResources(round(i.getOwnResources()*exchange));
			i.setUnitCost(round(i.getUnitCost()*exchange));
		}
		for (ProjectItemPersonnelWithout i : personnelWithouts) {
			i.setOwnResources(round(i.getOwnResources()*exchange));
			i.setUnitCost(round(i.getUnitCost()*exchange));
		}
		for (Block block : this.getBlocks()) {
			for (BlockIncome i : block.getIncomes()) {
				if (block.getProject().getIncomeGen()) i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));				
			}
			for (BlockInput i : block.getInputs()) {
				i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));				
			}
			for (BlockLabour i : block.getLabours()) {
				i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));
			}
		}
		for (BlockWithout block : this.getBlocksWithout()) {
			for (BlockIncome i : block.getIncomes()) {
				if (block.getProject().getIncomeGen()) i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));				
			}
			for (BlockInput i : block.getInputs()) {
				i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));				
			}
			for (BlockLabour i : block.getLabours()) {
				i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));
			}
		}
		//  step 10
		for (ProjectItemContribution i : contributions) {
			i.setUnitCost(round(i.getUnitCost()*exchange));
		}
		
		// refitems
		for (ReferenceIncome i : refIncomes) {
			i.setUnitCost(round(i.getUnitCost()*exchange));
			if (i.getTransport()!=null) i.setTransport(round(i.getTransport()*exchange));
		}
		for (ReferenceCost i : refCosts) {
			i.setUnitCost(round(i.getUnitCost()*exchange));
			if (i.getTransport()!=null) i.setTransport(round(i.getTransport()*exchange));
		}
		for (ReferenceLabour i : refLabours) {
			i.setUnitCost(round(i.getUnitCost()*exchange));
		}
	}
	
	public void importRefLinks() {
		for (ProjectItemAsset item : assets) {
			linkedToImport(item);
		}
		for (ProjectItemLabour item : labours) {
			linkedToImport(item);
		}
		for (ProjectItemService item : services) {
			linkedToImport(item);
		}
		for (ProjectItemAssetWithout item : assetsWithout) {
			linkedToImport(item);
		}
		for (ProjectItemLabourWithout item : laboursWithout) {
			linkedToImport(item);
		}
		for (ProjectItemServiceWithout item : servicesWithout) {
			linkedToImport(item);
		}
		for (ProjectItemNongenMaterials item : nongenMaterials) {
			linkedToImport(item);
		}
		for (ProjectItemNongenMaintenance item : nongenMaintenance) {
			linkedToImport(item);
		}
		for (ProjectItemNongenLabour item : nongenLabours) {
			linkedToImport(item);
		}
		for (ProjectItemGeneral item : generals) {
			linkedToImport(item);
		}
		for (ProjectItemPersonnel item : personnels) {
			linkedToImport(item);
		}
		for (ProjectItemGeneralWithout item : generalWithouts) {
			linkedToImport(item);
		}
		for (ProjectItemPersonnelWithout item : personnelWithouts) {
			linkedToImport(item);
		}
		for (ProjectItemContribution item : contributions) {
			linkedToImport(item);
		}
		for (Block block : blocks) {
			for (BlockIncome item : block.getIncomes()) {
				linkedToImport(item);
			}
			for (BlockInput item : block.getInputs()) {
				linkedToImport(item);
			}
			for (BlockLabour item : block.getLabours()) {
				linkedToImport(item);
			}
		}
	}		

	/**
	 * Creates a copy of the Project.  The uniqueID field is retained and collections are 
	 * copied by value so as not to interfere with the original Project.
	 * @return the copied Project
	 */
	public Project copy(boolean forExport, Double rivVersion) {
		Project newProj = new Project();
		if (forExport) {
			newProj.setRivVersion(rivVersion);
		}
		
		newProj.setUniqueId(this.uniqueId);
		newProj.setActivities(this.getActivities());
		newProj.setAssumptions(this.getAssumptions());
		newProj.setBeneDirectChild(this.getBeneDirectChild());
		newProj.setBeneDirectMen(this.getBeneDirectMen());
		newProj.setBeneDirectNum(this.getBeneDirectNum());
		newProj.setBeneDirectWomen(this.getBeneDirectWomen());
		newProj.setBenefDesc(this.getBenefDesc());
		newProj.setBeneficiary(this.getBeneficiary());
		newProj.setBenefName(this.getBenefName());
		newProj.setBeneIndirectChild(this.getBeneIndirectChild());
		newProj.setBeneIndirectMen(this.getBeneIndirectMen());
		newProj.setBeneIndirectNum(this.getBeneIndirectMen());
		newProj.setBeneIndirectNum(this.getBeneIndirectNum());
		newProj.setBeneIndirectWomen(this.getBeneIndirectWomen());
		newProj.setCapitalDonate(this.getCapitalDonate());
		newProj.setCapitalInterest(this.getCapitalInterest());
		newProj.setCapitalOwn(this.getCapitalOwn());
//		newProj.setCategoryOther(this.getCategoryOther());
		newProj.setCreatedBy(this.getCreatedBy());
		newProj.setDuration(this.getDuration());
		newProj.setEnviroCategory(this.getEnviroCategory());
		newProj.setEnviroImpact(this.getEnviroImpact());
		newProj.setExchRate(this.getExchRate());
		newProj.setFieldOffice(this.getFieldOffice());
		newProj.setIncomeGen(this.getIncomeGen());
		newProj.setInflationAnnual(this.getInflationAnnual());
		newProj.setJustification(this.getJustification());
		newProj.setLoan1Duration(this.getLoan1Duration());
		newProj.setLoan1GraceCapital(this.getLoan1GraceCapital());
		newProj.setLoan1GraceInterest(this.getLoan1GraceInterest());
		newProj.setLoan1Interest(this.getLoan1Interest());
		newProj.setLoan2Amt(this.getLoan2Amt());
		newProj.setLoan2Duration(this.getLoan2Duration());
		newProj.setLoan2GraceCapital(this.getLoan2GraceCapital());
		newProj.setLoan2GraceInterest(this.getLoan2GraceInterest());
		newProj.setLoan2InitPeriod(this.getLoan2InitPeriod());
		newProj.setLoan2Interest(this.getLoan2Interest());
		newProj.setLocation1(this.getLocation1());
		newProj.setLocation2(this.getLocation2());
		newProj.setLocation3(this.getLocation3());
		newProj.setMarket(this.getMarket());
		newProj.setOrganization(this.getOrganization());
		newProj.setPerYearContributions(perYearContributions);
		newProj.setProjectName(this.projectName);
		newProj.setProjCategory(this.getProjCategory());
		newProj.setProjDesc(this.getProjDesc());
		newProj.setReccDate(reccDate);
		newProj.setReccCode(reccCode);
		
		newProj.setRequirements(this.getRequirements());
		newProj.setStartupMonth(this.getStartupMonth());
		newProj.setStatus(this.getStatus());
		newProj.setSustainability(this.getSustainability());
		newProj.setTechnology(this.getTechnology());
		newProj.setUserCode(this.getUserCode());
		newProj.setWithWithout(this.isWithWithout());
		
		newProj.setReccDesc(reccDesc);
		
		newProj.setAppConfig1(appConfig1);
		newProj.setAppConfig2(appConfig2);
		newProj.setAdminMisc1(adminMisc1);
		newProj.setAdminMisc2(adminMisc2);
		newProj.setAdminMisc3(adminMisc3);
		
		
		newProj.setAssets(new HashSet<ProjectItemAsset>());
		newProj.setLabours(new HashSet<ProjectItemLabour>());
		newProj.setServices(new HashSet<ProjectItemService>());
		newProj.setAssetsWithout(new HashSet<ProjectItemAssetWithout>());
		newProj.setLaboursWithout(new HashSet<ProjectItemLabourWithout>());
		newProj.setServicesWithout(new HashSet<ProjectItemServiceWithout>());
		newProj.setNongenMaintenance(new HashSet<ProjectItemNongenMaintenance>());
		newProj.setNongenMaterials(new HashSet<ProjectItemNongenMaterials>());
		newProj.setNongenLabours(new HashSet<ProjectItemNongenLabour>());
		newProj.setGenerals(new HashSet<ProjectItemGeneral>());
		newProj.setGeneralWithouts(new HashSet<ProjectItemGeneralWithout>());
		newProj.setPersonnels(new HashSet<ProjectItemPersonnel>());
		newProj.setPersonnelWithouts(new HashSet<ProjectItemPersonnelWithout>());
		newProj.setContributions(new HashSet<ProjectItemContribution>());
		newProj.setRefCosts(new HashSet<ReferenceCost>());
		newProj.setRefIncomes(new HashSet<ReferenceIncome>());
		newProj.setRefLabours(new HashSet<ReferenceLabour>());
		newProj.setBlocks(new HashSet<Block>());
		newProj.setBlocksWithout(new HashSet<BlockWithout>());
		
		// copy reference table
		for (ReferenceCost item : refCosts) {
			newProj.addReferenceCost(item.copy());
		}
		for (ReferenceIncome item : refIncomes) {
			newProj.addReferenceIncome(item.copy());
		}
		for (ReferenceLabour item : refLabours) {
			newProj.addReferenceLabour(item.copy());
		}
		
		// copy step 7
		for (ProjectItemAsset item : assets) {
			ProjectItemAsset newAss = item.copy();
			newProj.addAsset(newAss);
			prepareLinkedToableForExport(newAss, forExport);
		}
		for (ProjectItemAssetWithout item : assetsWithout) {
			ProjectItemAssetWithout newAss = item.copy();
			newProj.addAssetWithout(newAss);
			prepareLinkedToableForExport(newAss, forExport);
		}
		for (ProjectItemLabour item : labours) {
			ProjectItemLabour newLab = item.copy();
			newProj.addLabour(newLab);
			prepareLinkedToableForExport(newLab, forExport);
		}
		for (ProjectItemLabourWithout item : laboursWithout) {
			ProjectItemLabourWithout newLab = item.copy();
			newProj.addLabourWithout(newLab);
			prepareLinkedToableForExport(newLab, forExport);
		}
		for (ProjectItemService item : services) {
			ProjectItemService newServ = item.copy();
			newProj.addService(newServ);
			prepareLinkedToableForExport(newServ, forExport);
		}
		for (ProjectItemServiceWithout item : servicesWithout) {
			ProjectItemServiceWithout newServ = item.copy();
			newProj.addServiceWithout(newServ);
			prepareLinkedToableForExport(newServ, forExport);
		}
		
		// copy step 8 (and nig step 10)
		if (incomeGen){ 
			for (ProjectItemGeneral item : generals) {
				ProjectItemGeneral newGen = item.copy();
				newProj.addGeneral(newGen);
				prepareLinkedToableForExport(newGen, forExport);
			}
			for (ProjectItemGeneralWithout item : generalWithouts) {
				ProjectItemGeneralWithout newGen = item.copy();
				newProj.addGeneralWithout(newGen);
				prepareLinkedToableForExport(newGen, forExport);
			}
			for (ProjectItemPersonnel item : personnels) {
				ProjectItemPersonnel newPers = item.copy();
				newProj.addPersonnel(newPers);
				prepareLinkedToableForExport(newPers, forExport);
			}
			for (ProjectItemPersonnelWithout item : personnelWithouts) {
				ProjectItemPersonnelWithout newPers = item.copy();
				newProj.addPersonnelWithout(newPers);
				prepareLinkedToableForExport(newPers, forExport);
			}
		} else  {
			for (ProjectItemNongenMaterials item : nongenMaterials) {
				ProjectItemNongenMaterials newNongen = item.copy();
				newProj.addNongenMaterial(newNongen);
				prepareLinkedToableForExport(newNongen, forExport);
			}
			for (ProjectItemNongenMaintenance item : nongenMaintenance) {
				ProjectItemNongenMaintenance newNongen = item.copy();
				newProj.addNongenMaintenance(newNongen);
				prepareLinkedToableForExport(newNongen, forExport);
			}
			for (ProjectItemNongenLabour item : nongenLabours) {
				ProjectItemNongenLabour newNongen = item.copy();
				newProj.addNongenLabour(newNongen);
				prepareLinkedToableForExport(newNongen, forExport);
			}
			
			// step 10 nongen
			for (ProjectItemContribution item : contributions) {
				ProjectItemContribution newCont = item.copy();
				newProj.addContribution(newCont);
				prepareLinkedToableForExport(newCont, forExport);
			}
		}
		
		
		for (Block block : this.getBlocks()) {
			Block newBlock = (Block)block.copy();
			newProj.addBlock(newBlock);
			
			// prepare linked to for export
			for (BlockIncome item : newBlock.getIncomes()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (BlockInput item : newBlock.getInputs()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (BlockLabour item : newBlock.getLabours()) {
				prepareLinkedToableForExport(item, forExport);
			}
		}
		for (BlockWithout block : this.getBlocksWithout()) {
			BlockWithout newBlock = (BlockWithout)block.copy();
			newProj.addBlock(newBlock);
			
			// prepare linked to for export
			for (BlockIncome item : newBlock.getIncomes()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (BlockInput item : newBlock.getInputs()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (BlockLabour item : newBlock.getLabours()) {
				prepareLinkedToableForExport(item, forExport);
			}
		}
		return newProj;
	}
	
	public ProjectResult getProjectResult(Setting setting) {
		ProjectResult pr = new ProjectResult();
		
		pr.setProjectId(this.getProjectId());
		pr.setProjectName(this.getProjectName());
		pr.setUserCode(this.getUserCode());
		pr.setTechnician(this.getTechnician());
		pr.setFieldOffice(this.getFieldOffice());
		pr.setEnviroCategory(this.getEnviroCategory());
		pr.setBeneficiary(this.getBeneficiary());
		pr.setProjCategory(this.getProjCategory());
		pr.setIncomeGen(this.getIncomeGen());
		pr.setShared(this.isShared());
		pr.setStatus(this.getStatus());
		pr.setBeneDirect(this.getBeneDirectChild()+this.getBeneDirectMen()+this.getBeneDirectWomen());
		pr.setBeneIndirect(this.getBeneIndirectChild()+this.getBeneIndirectMen()+this.getBeneIndirectWomen());
		pr.setAnnualEmployment(this.getAnnualEmployment());
		
		pr.setProjCategory(this.projCategory);
		pr.setBeneficiary(this.beneficiary);
		pr.setAppConfig1(this.appConfig1);
		pr.setAppConfig2(this.appConfig2);
		
		double investTotal=0.0;
		double investOwn=0.0;
		double investDonated=0.0;
		
		if (this.getIncomeGen()) { // INCOME GENERATING
			ArrayList<ProjectFinanceData> finData = ProjectFinanceData.analyzeProject(this, AnalysisType.Incremental);
			double annualNetIncome=0.0;
			annualNetIncome = finData.get(finData.size()-1).getNetIncome() - finData.get(finData.size()-1).getIncResidual();
			pr.setAnnualNetIncome(annualNetIncome);
			
			// !! npv and irr without donation
			double discount = setting.getDiscountRate()/100;
			pr.setNpv(Calculator.netPresentValue(discount, finData, false));
			BigDecimal irrWithout = Calculator.internalRateOfReturn(discount,finData, false);
			pr.setIrr(irrWithout);
			
			// npv and irr with donation
			//ArrayList<ProjectFinanceData> data = ProjectFinanceData.analyzeProject(this, AnalysisType.Incremental);
			double npvWith = Calculator.netPresentValue(discount, finData, true);
			BigDecimal irrWith = Calculator.internalRateOfReturn(discount, finData, true);
			pr.setNpvWithDonation(npvWith);
			pr.setIrrWithDonation(irrWith);
//					(irrWith.compareTo(new BigDecimal(-1000))!=-1
//					&& irrWith.compareTo(new BigDecimal(1000))!=-1)
//					? irrWith : null
//				);
			
			// add working capital
			ProjectFirstYear pfy = new ProjectFirstYear(this);
			double[] pfyResults = ProjectFirstYear.WcAnalysis(pfy);
			BigDecimal amtRequired = new BigDecimal(-1*pfyResults[1]);
			pr.setWorkingCapital(amtRequired.doubleValue());
			pr.setWcPeriod((int)pfyResults[0]);
			pr.setWcOwn(this.getCapitalOwn());
			pr.setWcDonated(this.getCapitalDonate());
			pr.setWcFinanced(amtRequired.doubleValue()-this.getCapitalDonate()-this.getCapitalOwn());
			
			// investment costs
			for(ProjectFinanceData pfd:finData) {
				investTotal+=pfd.getCostInvest();
				investOwn+=pfd.getCostInvestOwn();
				investDonated+=pfd.getCostInvestDonated();
			}
		} else { // NON INCOME GENERATING
			for (ProjectItemAsset asset : this.getAssets()) {
				investTotal += asset.getUnitNum()*asset.getUnitCost();
				investOwn += asset.getOwnResources();
				investDonated += asset.getDonated();
			}
			for (ProjectItemLabour lab : this.getLabours()) {
				investTotal += lab.getUnitNum()*lab.getUnitCost();
				investOwn += lab.getOwnResources();
				investDonated += lab.getDonated();
			}
			for (ProjectItemService serv : this.getServices()) {
				investTotal += serv.getUnitNum()*serv.getUnitCost();
				investOwn += serv.getOwnResources();
				investDonated += serv.getDonated();
			}
		}
			
		pr.setInvestmentTotal(investTotal);
		pr.setInvestmentOwn(investOwn);
		pr.setInvestmentDonated(investDonated);
		
		return pr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(uniqueId);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (!Arrays.equals(uniqueId, other.uniqueId))
			return false;
		return true;
	}
}