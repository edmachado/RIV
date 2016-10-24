package riv.objects.profile;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
import org.jfree.util.Log;
import org.safehaus.uuid.UUIDGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import riv.objects.Probase;
import riv.objects.ProfileMatrix;
import riv.objects.config.FieldOffice;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.objects.project.Block;
import riv.objects.project.BlockWithout;
import riv.objects.project.Donor;
import riv.objects.project.Project;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemContribution;
import riv.objects.project.ProjectItemGeneral;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemNongenLabour;
import riv.objects.project.ProjectItemNongenMaintenance;
import riv.objects.project.ProjectItemNongenMaterials;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectItemService;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceLabour;

/**
 * A RuralInvest Profile.
 * @author Bar Zecharya
 *
 */
@Entity
@Table(name="PROFILE")
public class Profile extends Probase implements java.io.Serializable {
	static final Logger LOG = LoggerFactory.getLogger(Profile.class);
	private static final long serialVersionUID = 1L;

	// Fields    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PROFILE_ID", nullable = false)
	private Integer profileId;
	
	// FOREIGN-KEY INDICATORS
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="FIELD_OFFICE")
	private FieldOffice fieldOffice;
	@ManyToOne //(fetch=FetchType.LAZY)
	@JoinColumn(name="TECHNICIAN")
	private User technician;
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="STATUS")
	private Status status;
	
	@Column(name="UNIQUE_ID")
	private byte[] uniqueId;
	//private String currency;
	@Column(name="WIZARD_STEP")
	private Integer wizardStep;
	@Column(name="PROFILE_NAME")
	@Size(max=100)
	private String profileName;
	@Column(name="INCOME_GEN")
	private boolean incomeGen;
	@Transient
	private boolean generic;
	@Transient
	private Double rivVersion;
	@Column(name="EXCH_RATE")
	private Double exchRate;
	@Column(name="PREP_DATE")
	@DateTimeFormat(iso=ISO.DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date prepDate;
	@Column(name="CREATED_BY")
	private String createdBy;
	@Column(name="LAST_UPDATE")
	@DateTimeFormat(iso=ISO.DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpdate;
	@Column(name="LOCATION1")
	@Size(max=50)
	private String location1;
	@Column(name="LOCATION2")
	@Size(max=50)
	private String location2;
	@Column(name="LOCATION3")
	@Size(max=50)
	private String location3;
	@Column(name="BENEF_NAME")
	@Size(max=150)
	private String benefName;
	@Column(name="BENEF_DESC")
	@Size(max=10000)
	private String benefDesc;
	@Column(name="BENEF_NUM")
	private Integer benefNum;
	@Column(name="BENEF_FAMILIES")
	private Integer benefFamilies;
	@Column(name="PROJ_DESC")
	@Size(max=10000)
	private String projDesc;
	@Column(name="RECC_CODE")
	private Short reccCode;
	@Column(name="RECC_DATE")
	@DateTimeFormat(iso=ISO.DATE)
	@Temporal(TemporalType.TIMESTAMP)
	private Date reccDate;
	@Column(name="RECC_DESC")
	@Size(max=10000)
	private String reccDesc;
	@Size(max=10000)
	private String market;
	@Column(name="ENVIRO_IMPACT")
	@Size(max=10000)
	private String enviroImpact;
	@Size(max=10000)
	private String organization;
	@Column(name="SOURCE_FUNDS")
	@Size(max=10000)
	private String sourceFunds;
	private boolean shared=true;
	@Column(name="WITH_WITHOUT")
	private boolean withWithout;
	
	@OneToMany(mappedBy="profile", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='0'")
	private Set<ProfileProduct> products = new HashSet<ProfileProduct>();
	@OneToMany(mappedBy="profile", cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='1'")
	private Set<ProfileProductWithout> productsWithout = new HashSet<ProfileProductWithout>();
	
	
	@OneToMany(mappedBy="profile", targetEntity=ProfileItemGood.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='0'")
	private Set<ProfileItemGood> glsGoods;
	@OneToMany(mappedBy="profile", targetEntity=ProfileItemGoodWithout.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='4'")
	private Set<ProfileItemGoodWithout> glsGoodsWithout = new HashSet<ProfileItemGoodWithout>();
	@OneToMany(mappedBy="profile", targetEntity=ProfileItemLabour.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='1'")
	private Set<ProfileItemLabour> glsLabours;
	@OneToMany(mappedBy="profile", targetEntity=ProfileItemLabourWithout.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='5'")
	private Set<ProfileItemLabourWithout> glsLaboursWithout = new HashSet<ProfileItemLabourWithout>();
	@OneToMany(mappedBy="profile", targetEntity=ProfileItemGeneral.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='2'")
	private Set<ProfileItemGeneral> glsGeneral;
	@OneToMany(mappedBy="profile", targetEntity=ProfileItemGeneralWithout.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='3'")
	private Set<ProfileItemGeneralWithout> glsGeneralWithout = new HashSet<ProfileItemGeneralWithout>();
	@OneToMany(mappedBy="profile", targetEntity=ReferenceCost.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY") 
	@Where(clause="class='0'")
	private Set<ReferenceCost> refCosts;
	@OneToMany(mappedBy="profile", targetEntity=ReferenceIncome.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='1'")
	private Set<ReferenceIncome> refIncomes;
	@OneToMany(mappedBy="profile", targetEntity=ReferenceLabour.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='2'")
	private Set<ReferenceLabour> refLabours;
	
	// Constructors

	/** default constructor */
	public Profile() {
	}

	// Common properties for Probase interface
	public Integer getProId() {
		return this.profileId;
	}
	public boolean isProject() {
		return false;
	}

	// Property accessors

	public Integer getProfileId() {
		return this.profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public void setUniqueId(byte[] uniqueId) {
		this.uniqueId = uniqueId;
	}
	public byte[] getUniqueId() {
		return uniqueId;
	}

	public FieldOffice getFieldOffice() {
		return this.fieldOffice;
	}

	public void setFieldOffice(FieldOffice fieldOffice) {
		this.fieldOffice = fieldOffice;
	}

	public User getTechnician() {
		return this.technician;
	}

	public void setTechnician(User user) {
		this.technician = user;
	}

	public void setWizardStep(Integer wizardStep) {
		this.wizardStep = wizardStep;
	}

	public Integer getWizardStep() {
		return wizardStep;
	}

	public String getProfileName() {
		return this.profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public boolean getIncomeGen() {
		return this.incomeGen;
	}

	public void setIncomeGen(boolean incomeGen) {
		this.incomeGen = incomeGen;
	}

	/**
	 * @param generic the generic to set
	 */
	 public void setGeneric(boolean generic) {
		this.generic = generic;
	}
	/**
	 * @return the generic
	 */
	 public boolean isGeneric() {
		 return generic;
	 }
	 public Double getRivVersion() {
		return rivVersion;
	}

	public void setRivVersion(Double rivVersion) {
		this.rivVersion = rivVersion;
	}

	public Double getExchRate() {
		 return this.exchRate;
	 }

	 public void setExchRate(Double exchRate) {
		 this.exchRate = exchRate;
	 }

	 public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus() {
		return status;
	}
	public Date getPrepDate() {
		 return this.prepDate;
	 }

	 public void setPrepDate(Date prepDate) {
		 this.prepDate = prepDate;
	 }

	 public void setCreatedBy(String createdBy) {
		 this.createdBy = createdBy;
	 }
	 public String getCreatedBy() {
		 return createdBy;
	 }
	 public void setLastUpdate(Date lastUpdate) {
		 this.lastUpdate = lastUpdate;
	 }

	 public Date getLastUpdate() {
		 return lastUpdate;
	 }

	 public String getLocation1() {
		 return this.location1;
	 }

	 public void setLocation1(String location1) {
		 this.location1 = location1;
	 }

	 public String getLocation2() {
		 return this.location2;
	 }

	 public void setLocation2(String location2) {
		 this.location2 = location2;
	 }

	 public String getLocation3() {
		 return this.location3;
	 }

	 public void setLocation3(String location3) {
		 this.location3 = location3;
	 }

	 public String getBenefName() {
		 return this.benefName;
	 }

	 public void setBenefName(String benefName) {
		 this.benefName = benefName;
	 }

	 public String getBenefDesc() {
		 return this.benefDesc;
	 }

	 public void setBenefDesc(String benefDesc) {
		 this.benefDesc = benefDesc;
	 }

	 public Integer getBenefNum() {
		 return this.benefNum;
	 }

	 public void setBenefNum(Integer benefNum) {
		 this.benefNum = benefNum;
	 }

	 public void setBenefFamilies(Integer benefFamilies) {
		 this.benefFamilies = benefFamilies;
	 }
	 public Integer getBenefFamilies() {
		 return benefFamilies;
	 }
	 public String getProjDesc() {
		 return this.projDesc;
	 }

	 public void setProjDesc(String projDesc) {
		 this.projDesc = projDesc;
	 }

	 public Short getReccCode() {
		 return this.reccCode;
	 }

	 public void setReccCode(Short reccCode) {
		 this.reccCode = reccCode;
	 }

	 public Date getReccDate() {
		 return this.reccDate;
	 }

	 public void setReccDate(Date reccDate) {
		 this.reccDate = reccDate;
	 }

	 public String getReccDesc() {
		 return this.reccDesc;
	 }

	 public void setReccDesc(String reccDesc) {
		 this.reccDesc = reccDesc;
	 }

	 public String getMarket() {
		 return this.market;
	 }

	 public void setMarket(String market) {
		 this.market = market;
	 }

	 public String getEnviroImpact() {
		 return this.enviroImpact;
	 }

	 public void setEnviroImpact(String enviroImpact) {
		 this.enviroImpact = enviroImpact;
	 }

	 public String getOrganization() {
		 return this.organization;
	 }

	 public void setOrganization(String organization) {
		 this.organization = organization;
	 }

	 public String getSourceFunds() {
		 return this.sourceFunds;
	 }

	 public void setSourceFunds(String sourceFunds) {
		 this.sourceFunds = sourceFunds;
	 }

	 public void setShared(boolean shared) {
		 this.shared = shared;
	 }

	 public boolean getShared() {
		 return shared;
	 }

	 public boolean isShared() {
		 return shared;
	 }

	 public void setWithWithout(boolean withWithout) {
		 if (incomeGen && !withWithout) { 
			 // convert "without" products to "with"
			for (ProfileProductWithout pw : productsWithout) {
				 ProfileProduct pp = (ProfileProduct)pw.copy(ProfileProduct.class);
				 pw.setOrderBy(products.size());
				 addProfileProduct(pp);
			 }
			productsWithout.clear();
			 // delete "without" investment and general costs
			 glsGeneralWithout.clear();
			 glsGoodsWithout.clear();
			 glsLaboursWithout.clear();
		 }
		 this.withWithout = withWithout;
		 if (!this.withWithout && productsWithout!=null) { // convert without blocks to with blocks
				for (ProfileProductWithout pw : productsWithout) {
					ProfileProduct newB = (ProfileProduct)pw.copy(ProfileProduct.class);
					newB.setOrderBy(products.size());
					addProfileProduct(newB);
				}
			}
	 }

	 public boolean getWithWithout() {
		 return withWithout;
	 }

	public Set<ProfileProduct> getProducts() {
		 return this.products;
	 }

	 public void setProducts(Set<ProfileProduct> profileProducts) {
		 this.products = profileProducts;
	 }
	public Set<ProfileProductWithout> getProductsWithout() {
		 return this.productsWithout;
	 }

	 public void setProductsWithout(Set<ProfileProductWithout> profileProducts) {
		 this.productsWithout = profileProducts;
	 }

	 public void addProfileProduct(ProfileProductBase pp) {
		 pp.setProfile(this);
		 if (pp.getClass()==ProfileProduct.class) {
			 products.add((ProfileProduct)pp);
		 } else {
			 productsWithout.add((ProfileProductWithout)pp);
		 }
	 }

	 public void setGlsGoods(Set<ProfileItemGood> profileGoods) {
		 this.glsGoods = profileGoods;
	 }

	 public Set<ProfileItemGood> getGlsGoods() {
		 return glsGoods;
	 }

	 /**
	  * Adds a Good cost to the Profile's Good costs collection. 
	  * Sets the cost's Profile property to this profile as well.
	  * @param good Good cost to add
	  */
	  public void addGlsGood(ProfileItemGood good) {
		 good.setProfile(this);
		 this.glsGoods.add(good);
	 }

	 public Set<ProfileItemGoodWithout> getGlsGoodsWithout() {
		return glsGoodsWithout;
	}

	public void setGlsGoodsWithout(Set<ProfileItemGoodWithout> glsGoodsWithout) {
		this.glsGoodsWithout = glsGoodsWithout;
	}
	 public void addGlsGoodWithout(ProfileItemGoodWithout good) {
		 good.setProfile(this);
		 this.glsGoodsWithout.add(good);
	 }

	public void setGlsLabours(Set<ProfileItemLabour> profileGlsLabours) {
		 this.glsLabours = profileGlsLabours;
	 }

	 public Set<ProfileItemLabour> getGlsLabours() {
		 return glsLabours;
	 }
	 
	 /**
	  * Adds a Labour cost to the Profile's Labour costs collection. 
	  * Sets the cost's Profile property to this profile as well.
	  * @param labour
	  */
	 public void addGlsLabour(ProfileItemLabour labour){
		 labour.setProfile(this);
		 glsLabours.add(labour);
	 }

	 public Set<ProfileItemLabourWithout> getGlsLaboursWithout() {
		return glsLaboursWithout;
	}

	public void setGlsLaboursWithout(Set<ProfileItemLabourWithout> glsLaboursWithout) {
		this.glsLaboursWithout = glsLaboursWithout;
	}
	public void addGlsLabourWithout(ProfileItemLabourWithout labour){
		 labour.setProfile(this);
		 glsLaboursWithout.add(labour);
	 }


	public void setGlsGeneral(Set<ProfileItemGeneral> profileGlsGeneral) {
		 this.glsGeneral = profileGlsGeneral;
	 }

	 public Set<ProfileItemGeneral> getGlsGeneral() {
		 return glsGeneral;
	 }

	 /**
	  * Adds a General cost to the Profile's General costs collection. 
	  * Sets the cost's Profile property to this profile as well.
	  * @param general
	  */
	 public void addGlsGeneral(ProfileItemGeneral general){
		 general.setProfile(this);
		 glsGeneral.add(general);
	 }
	 
	 public void setGlsGeneralWithout(Set<ProfileItemGeneralWithout> profileGlsGeneral) {
		 this.glsGeneralWithout = profileGlsGeneral;
	 }

	 public Set<ProfileItemGeneralWithout> getGlsGeneralWithout() {
		 return glsGeneralWithout;
	 }
	 public void addGlsGeneralWithout(ProfileItemGeneralWithout general){
		 general.setProfile(this);
		 glsGeneralWithout.add(general);
	 }

	 public void setRefCosts(Set<ReferenceCost> refCosts) {
		 this.refCosts = refCosts;
	 }
	 public Set<ReferenceCost> getRefCosts() {
		 return refCosts;
	 }
	 public void addReferenceCost(ReferenceCost item) {
		 item.setProfile(this);
		 refCosts.add(item);
	 }
	 public void setRefIncomes(Set<ReferenceIncome> refIncomes) {
		 this.refIncomes = refIncomes;
	 }
	 public Set<ReferenceIncome> getRefIncomes() {
		 return refIncomes;
	 }
	 public void addReferenceIncome(ReferenceIncome item) {
		 item.setProfile(this);
		 refIncomes.add(item);
	 }
	 public void setRefLabours(Set<ReferenceLabour> refLabours) {
		 this.refLabours = refLabours;
	 }
	 public Set<ReferenceLabour> getRefLabours() {
		 return refLabours;
	 }
	 public void addReferenceLabour(ReferenceLabour item) {
		 item.setProfile(this);
		 refLabours.add(item);
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
		 //this.exchRate=exchange;
		 for (ProfileItemGood i : glsGoods) {
			 i.setOwnResource(round(i.getOwnResource()*exchange));
			 i.setSalvage(round(i.getSalvage()*exchange));
			 i.setUnitCost(round(i.getUnitCost()*exchange));
		 }
		 for (ProfileItemGoodWithout i : glsGoodsWithout) {
			 i.setOwnResource(round(i.getOwnResource()*exchange));
			 i.setSalvage(round(i.getSalvage()*exchange));
			 i.setUnitCost(round(i.getUnitCost()*exchange));
		 }
		 for (ProfileItemLabour i : glsLabours) {
			 i.setOwnResource(round(i.getOwnResource()*exchange));
			 i.setUnitCost(round(i.getUnitCost()*exchange));			
		 }
		 for (ProfileItemLabourWithout i : glsLaboursWithout) {
			 i.setOwnResource(round(i.getOwnResource()*exchange));
			 i.setUnitCost(round(i.getUnitCost()*exchange));			
		 }
		 for (ProfileItemGeneral i : glsGeneral) {
			 i.setUnitCost(round(i.getUnitCost()*exchange));			
		 }
		 for (ProfileItemGeneralWithout i : glsGeneralWithout) {
			 i.setUnitCost(round(i.getUnitCost()*exchange));			
		 }
		 for (ProfileProduct prod : products) {
			 for (ProfileProductIncome i : prod.getProfileIncomes()) {
				 if (this.incomeGen) {
					 i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				 }
				 i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));
			 }
			 for (ProfileProductInput i : prod.getProfileInputs()) {
				 i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				 i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));
			 }
			 for (ProfileProductLabour i : prod.getProfileLabours()) {
				 i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));				
			 }
		 }
		 for (ProfileProductWithout prod : productsWithout) {
			 for (ProfileProductIncome i : prod.getProfileIncomes()) {
				 if (this.incomeGen) {
					 i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				 }
				 i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));
			 }
			 for (ProfileProductInput i : prod.getProfileInputs()) {
				 i.setTransport(round(i.getTransport().multiply(BigDecimal.valueOf(exchange))));
				 i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));
			 }
			 for (ProfileProductLabour i : prod.getProfileLabours()) {
				 i.setUnitCost(round(i.getUnitCost().multiply(BigDecimal.valueOf(exchange))));				
			 }
		 }
		// refitems
			for (ReferenceIncome i : refIncomes) {
				i.setUnitCost(i.getUnitCost()*exchange);
				if (i.getTransport()!=null) i.setTransport(round(i.getTransport()*exchange));
			}
			for (ReferenceCost i : refCosts) {
				i.setUnitCost(i.getUnitCost()*exchange);
				if (i.getTransport()!=null) i.setTransport(round(i.getTransport()*exchange));
			}
			for (ReferenceLabour i : refLabours) {
				i.setUnitCost(round(i.getUnitCost()*exchange));
			}
	 }
	 
	 public boolean isBlockOrderProblem() {
		 	for (ProfileProduct b : products) {
				if (b.getOrderBy()==null) {
					Log.info("Products out of order");
					return true;
				}
			}
		 
			TreeSet<ProfileProduct> blocks = new TreeSet<ProfileProduct>(this.products);
			int i=0;
			for (ProfileProduct b : blocks) {
				if (b.getOrderBy()!=i++) {
					System.out.println("Products out of order");
					return true;
				}
			}
			
			return false;
		}

		public void correctBlockOrderProblem() {
			int i=0;
			for (ProfileProduct b : this.products) {
				b.setOrderBy(i++);
			}
		}


	 /**
	  * checks if profile table rows are missing orders (imported from RIV < 2.0)
	  * checks glsGoods table since rows are required, so only one table needs to be checked
	  * @return true if orders are missing
	  */
	 public boolean isMissingOrders() {
		 for (ProfileItem item : glsGoods) {
			 if (item.getOrderBy()==null) { 
				 return true; 
			 }
		 }
		 return false;
	 }

	 /**
	  * Adds order for table rows
	  */
	 public void addOrders() {
		 int i=0;
		 // glsGoods
		 for (ProfileItem item : glsGoods)  { item.setOrderBy(i++); } i=0; 
		 for (ProfileItem item : glsLabours)  { item.setOrderBy(i++); } i=0; 
		 for (ProfileItem item : glsGeneral)  { item.setOrderBy(i++); } i=0; 

		 int p=0;
		 for (ProfileProduct prod : products) {
			 prod.setOrderBy(p++);
			 for (ProfileProductItem item : prod.getProfileIncomes()) { item.setOrderBy(i++); } i=0; 
			 for (ProfileProductItem item : prod.getProfileInputs()) { item.setOrderBy(i++); } i=0; 
			 for (ProfileProductItem item : prod.getProfileLabours()) { item.setOrderBy(i++); } i=0; 
		 }
		 p=0;
		 for (ProfileProductWithout prod : productsWithout) {
			 prod.setOrderBy(p++);
			 for (ProfileProductItem item : prod.getProfileIncomes()) { item.setOrderBy(i++); } i=0; 
			 for (ProfileProductItem item : prod.getProfileInputs()) { item.setOrderBy(i++); } i=0; 
			 for (ProfileProductItem item : prod.getProfileLabours()) { item.setOrderBy(i++); } i=0; 
		 }
	 }
	 
	 public void importRefLinks() {
		for (ProfileItemGood item : glsGoods) {
			linkedToImport(item);
		}
		for (ProfileItemGeneral item : glsGeneral) {
			linkedToImport(item);
		}
		for (ProfileItemGeneralWithout item : glsGeneralWithout) {
			linkedToImport(item);
		}
		for (ProfileItemLabour item : glsLabours) {
			linkedToImport(item);
		}
		for (ProfileProduct p : products) {
			for (ProfileProductIncome item : p.getProfileIncomes()) {
				linkedToImport(item);
			}
			for (ProfileProductInput item : p.getProfileInputs()) {
				linkedToImport(item);
			}
			for (ProfileProductLabour item : p.getProfileLabours()) {
				linkedToImport(item);
			}
		}
		for (ProfileProductWithout p : productsWithout) {
			for (ProfileProductIncome item : p.getProfileIncomes()) {
				linkedToImport(item);
			}
			for (ProfileProductInput item : p.getProfileInputs()) {
				linkedToImport(item);
			}
			for (ProfileProductLabour item : p.getProfileLabours()) {
				linkedToImport(item);
			}
		}
	}

	 /**
	  * Creates a copy of the Profile.  Please note the following:
	  * <ul><li>the uniqueId property is retained</li>
	  * <li>collection items (goods, labour and general costs, etc) are copied by value.  
	  * Thus saving the copy will not affect the contents of the original profile's collection.</li></ul>
	  * @param	forExport	If true, removes references to "linkedTo" fields of collection items 
	  * 							 and uses "exportLinkedTo" field which hold the "orderBy" value
	  * 							 of the linked reference item.
	  * 					If false, re-creates links to the correct reference table items (copied by value)
	  * @return a copy of the Profile
	  */
	 public Profile copy(boolean forExport) {
		 Profile newProf = new Profile();
			if (forExport) {
				newProf.setRivVersion(getRivVersion());
			}
			
		 newProf.setUniqueId(uniqueId);
		 newProf.setBenefDesc(benefDesc);
		 newProf.setBenefName(benefName);
		 newProf.setBenefNum(this.benefNum);
		 newProf.setBenefFamilies(this.benefFamilies);
		 newProf.setCreatedBy(createdBy);
		 newProf.setEnviroImpact(this.enviroImpact);
		 newProf.setExchRate(this.exchRate);
		 newProf.setFieldOffice(this.fieldOffice);
		 newProf.setIncomeGen(this.incomeGen);
		 newProf.setLocation1(this.location1);
		 newProf.setLocation2(this.location2);
		 newProf.setLocation3(this.location3);
		 newProf.setMarket(this.market);
		 newProf.setOrganization(this.organization);
		 newProf.setProfileName(this.profileName);
		 newProf.setProjDesc(this.projDesc);
		 newProf.setReccDate(reccDate);
		 newProf.setReccCode(reccCode);
		 newProf.setReccDesc(reccDesc);
		 newProf.setSourceFunds(sourceFunds);
		 newProf.setStatus(status);
		 newProf.setWithWithout(this.withWithout);
		 newProf.setGlsGoods(new HashSet<ProfileItemGood>());
		 newProf.setGlsGoodsWithout(new HashSet<ProfileItemGoodWithout>());
		 newProf.setGlsLabours(new HashSet<ProfileItemLabour>());
		 newProf.setGlsLaboursWithout(new HashSet<ProfileItemLabourWithout>());
		 newProf.setGlsGeneral(new HashSet<ProfileItemGeneral>());
		 newProf.setGlsGeneralWithout(new HashSet<ProfileItemGeneralWithout>());
		 newProf.setRefCosts(new HashSet<ReferenceCost>());
		 newProf.setRefIncomes(new HashSet<ReferenceIncome>());
		 newProf.setRefLabours(new HashSet<ReferenceLabour>());
		 newProf.setProducts(new HashSet<ProfileProduct>());
		 newProf.setProductsWithout(new HashSet<ProfileProductWithout>());
		 
		 // reference items
		 for (ReferenceIncome ref : refIncomes) {
			 ReferenceIncome newRef = ref.copy();
			 newProf.addReferenceIncome(newRef);
		 }
		 for (ReferenceCost ref : refCosts) {
			 ReferenceCost newRef = ref.copy();
			 newProf.addReferenceCost(newRef);
		 }
		 for (ReferenceLabour ref : refLabours) {
			 ReferenceLabour newRef = ref.copy();
			 newProf.addReferenceLabour(newRef);
		 }
		 
		 // cost and income items
		 for (ProfileItemGood item : glsGoods) {
			ProfileItemGood newItem = item.copy();
			newProf.addGlsGood(newItem);
			prepareLinkedToableForExport(newItem, forExport);
		 }
		 for (ProfileItemGoodWithout item : glsGoodsWithout) {
			ProfileItemGoodWithout newItem = item.copy();
			newProf.addGlsGoodWithout(newItem);
			prepareLinkedToableForExport(newItem, forExport);
		 }
		 for (ProfileItemLabour item : glsLabours) {
			 ProfileItemLabour newItem = item.copy();
			 newProf.addGlsLabour(newItem);
			 prepareLinkedToableForExport(newItem, forExport);
		 }
		 for (ProfileItemLabourWithout item : glsLaboursWithout) {
			 ProfileItemLabourWithout newItem = item.copy();
			 newProf.addGlsLabourWithout(newItem);
			 prepareLinkedToableForExport(newItem, forExport);
		 }
		 for (ProfileItemGeneral item : glsGeneral) {
			 ProfileItemGeneral newItem= item.copy();
			 newProf.addGlsGeneral(newItem);
			 prepareLinkedToableForExport(newItem, forExport);
		 }
		 for (ProfileItemGeneralWithout item : glsGeneralWithout) {
			 ProfileItemGeneralWithout newItem= item.copy();
			 newProf.addGlsGeneralWithout(newItem);
			 prepareLinkedToableForExport(newItem, forExport);
		 }
		 
		// prepare linkedto of profileproductitems for export
		for (ProfileProduct prod : products) {
			ProfileProduct newProd = (ProfileProduct)prod.copy(ProfileProduct.class);
			newProf.addProfileProduct(newProd);
			
			for (ProfileProductIncome item : newProd.getProfileIncomes()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (ProfileProductInput item : newProd.getProfileInputs()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (ProfileProductLabour item : newProd.getProfileLabours()) {
				prepareLinkedToableForExport(item, forExport);
			} 
		 }
		for (ProfileProductWithout prod : productsWithout) {
			ProfileProductWithout newProd = (ProfileProductWithout) prod.copy(ProfileProductWithout.class);
			newProf.addProfileProduct(newProd);
			
			for (ProfileProductIncome item : newProd.getProfileIncomes()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (ProfileProductInput item : newProd.getProfileInputs()) {
				prepareLinkedToableForExport(item, forExport);
			}
			for (ProfileProductLabour item : newProd.getProfileLabours()) {
				prepareLinkedToableForExport(item, forExport);
			} 
		 }

		 return newProf;
	 }

	 
	 
	 public Project convertToProject() {
		 Project proj = new Project();

		 proj.setAssets(new HashSet<ProjectItemAsset>());
		 proj.setLabours(new HashSet<ProjectItemLabour>());
		 proj.setServices(new HashSet<ProjectItemService>());
		 proj.setNongenMaintenance(new HashSet<ProjectItemNongenMaintenance>());
		 proj.setNongenMaterials(new HashSet<ProjectItemNongenMaterials>());
		 proj.setNongenLabours(new HashSet<ProjectItemNongenLabour>());
		 proj.setGenerals(new HashSet<ProjectItemGeneral>());
		 proj.setPersonnels(new HashSet<ProjectItemPersonnel>());
		 proj.setContributions(new HashSet<ProjectItemContribution>());
		 proj.setRefCosts(new HashSet<ReferenceCost>());
		 proj.setRefIncomes(new HashSet<ReferenceIncome>());
		 proj.setRefLabours(new HashSet<ReferenceLabour>());
		 proj.setBlocks(new HashSet<Block>());
		 proj.setBlocksWithout(new HashSet<BlockWithout>());

		 proj.setIncomeGen(this.incomeGen);
		 proj.setUniqueId(UUIDGenerator.getInstance().generateTimeBasedUUID().toByteArray());
		 proj.setWizardStep(1);
		 proj.setPrepDate(new Date());
		 // step 1
		 proj.setProjectName(this.getProfileName());
		 proj.setExchRate(this.exchRate);
		 proj.setBeneDirectNum(this.getBenefFamilies());
		 if (proj.getIncomeGen()) proj.setWithWithout(this.withWithout);
		 proj.setShared(this.isShared());
		 proj.setFieldOffice(this.fieldOffice);
		 proj.setLocation1(this.location1);
		 proj.setLocation2(this.location2);
		 proj.setLocation3(this.location3);
		 proj.setWithWithout(this.withWithout);
		 // step 2
		 proj.setBenefDesc(this.benefDesc);
		 proj.setBenefName(this.benefName);
		 proj.setProjDesc(this.projDesc);
		 proj.setSustainability(this.sourceFunds);
		 
		 Donor donor = new Donor();
		 donor.setDescription("not specified");
		 donor.setContribType(4);
		 donor.setNotSpecified(true);
		 donor.setOrderBy(0);
		 donor.setProject(proj);
		 proj.getDonors().add(donor);
		 
		 // step3
		 proj.setMarket(this.market);
		 proj.setEnviroImpact(this.enviroImpact);

		 // add investment costs

		 // add general costs

		 // add products
		 for (ProfileProduct prod : products) {
			 Block block = new Block();
			 block.setDescription(prod.getDescription());
			 block.setOrderBy(prod.getOrderBy());
			 block.setUnitType(prod.getUnitType());
			 block.setCycleLength(prod.getCycleLength());
			 block.setCyclePerYear(prod.getCyclePerYear().intValue());
			 block.setLengthUnit(prod.getLengthUnit());
			 proj.addBlock(block);
		 }
		 for (ProfileProductWithout prod : productsWithout) {
			 BlockWithout block = new BlockWithout();
			 block.setDescription(prod.getDescription());
			 block.setOrderBy(prod.getOrderBy());
			 block.setUnitType(prod.getUnitType());
			 block.setCycleLength(prod.getCycleLength());
			 block.setCyclePerYear(prod.getCyclePerYear().intValue());
			 block.setLengthUnit(prod.getLengthUnit());
			 proj.addBlock(block);
		 }

		 // reference table
		 for (ReferenceIncome ref : refIncomes) {
			 proj.addReferenceIncome(ref.copy());
		 }
		 for (ReferenceCost ref : refCosts) {
			 proj.addReferenceCost(ref.copy());
		 }
		 for (ReferenceLabour ref : refLabours) {
			 proj.addReferenceLabour(ref.copy());
		 }

		 return proj;
	 }
	 
	 public ProfileResult getProfileResult() {
		 ProfileResult pr = new ProfileResult();
		 pr.setProfileId(this.profileId);
		 pr.setProfileName(this.profileName);
		 pr.setIncomeGen(this.incomeGen);
		 pr.setShared(this.shared);
		 pr.setTechnician(this.technician);
		 pr.setFieldOffice(this.fieldOffice);
		 pr.setStatus(this.status);
		 pr.setBenefNum(this.benefNum);
		 
		 ProfileMatrix matrix = new ProfileMatrix(this);
		 pr.setInvestmentTotal(matrix.totalInvestmentWith-matrix.totalInvestmentWithout);
		 pr.setInvestmentOwn(matrix.totalOwnResourcesWith-matrix.totalOwnResourcesWithout);
		 pr.setTotIncome(matrix.totalIncomeWith-matrix.totalIncomeWithout);
		 pr.setOperCost(matrix.operationCostWith-matrix.operationCostWithout);
		 pr.setGeneralCost(matrix.generalCostWith-matrix.generalCostWithout);
		 pr.setAnnualReserve(round(matrix.annualReserveWith-matrix.annualReserveWithout));
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
		Profile other = (Profile) obj;
		if (!Arrays.equals(uniqueId, other.uniqueId))
			return false;
		return true;
	}
	 
		 
}
