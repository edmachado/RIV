package riv.objects.project;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.config.User;

@Entity
@Table(name="PROJECT_RESULT")
public class ProjectResult implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="PROJECT_ID")
	private Integer projectId;
	
	@ManyToOne
	@JoinColumn(name="FIELD_OFFICE")
	private FieldOffice fieldOffice;
	@ManyToOne
	@JoinColumn(name="TECHNICIAN")
	private User technician;
	@ManyToOne
	@JoinColumn(name="PROJ_CATEGORY")
	private ProjectCategory projCategory;
	@ManyToOne
	@JoinColumn(name="BENEFICIARY")
	private Beneficiary beneficiary;
	@ManyToOne
	@JoinColumn(name="ENVIRO_CATEGORY")
	private EnviroCategory enviroCategory;
	@ManyToOne
	@JoinColumn(name="STATUS")
	private Status status;
	@ManyToOne
	@JoinColumn(name="APP_CONFIG1")
	 private AppConfig1 appConfig1;
	@ManyToOne
	@JoinColumn(name="APP_CONFIG2")
	private AppConfig2 appConfig2;
	
	@Column(name="PROJECT_NAME")
	private String projectName;
	@Column(name="USER_CODE")
	private String userCode;
	@Column(name="INCOME_GEN")
	private boolean incomeGen;
	private boolean shared;
	@Column(name="BENE_DIRECT")
	private int beneDirect;
	@Column(name="BENE_INDIRECT")
	private int beneIndirect;
	@Column(name="INVEST_TOTAL")
	private double investmentTotal;
	@Column(name="INVEST_OWN")
	private Double investmentOwn;
	@Column(name="INVEST_DONATED")
	private Double investmentDonated;
	@Column(name="NPV", precision=12, scale=4)
	private double npv;
	@Column(name="NPV_WITH_DONATION")
	private double npvWithDonation;
	@Column(name="IRR", precision=12, scale=4)
	private BigDecimal irr;
	@Column(name="IRR_WITH_DONATION")
	private BigDecimal irrWithDonation;
	@Column(name="WORKING_CAPITAL")
	private double workingCapital;
	@Column(name="ANNUAL_EMPLOYMENT")
	private double annualEmployment;
	@Column(name="ANNUAL_NET_INCOME")
	private double annualNetIncome;
	@Column(name="WC_OWN")
	private double wcOwn;
	@Column(name="WC_DONATED")
	private double wcDonated;
	@Column(name="WC_FINANCED")
	private double wcFinanced;
	@Column(name="WC_PERIOD")
	private int wcPeriod;
	@Column(name="NEGATIVE_YEARS")
	private int negativeYears;
	
	// for downloading
	public String getDownloadName() throws UnsupportedEncodingException {
		// filename shouldn't contain unacceptable characters
		String output = projectName.replaceAll("[:<>\\.|\\?\\*/\\\\\"\\s]", "_");
		output = output.substring(0, Math.min(output.length(), 50));
		// get rid of cyrillic x -- for some reason this creates a problem
		output = output.replace("х", "x");
		return output;
	}

	
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}
	public int getProjectId() {
		return projectId;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setUserCode(String userCode) {
		this.userCode = userCode;
	}
	public String getUserCode() {
		return userCode;
	}
	public void setTechnician(User technician) {
		this.technician = technician;
	}
	public User getTechnician() {
		return technician;
	}
	public void setFieldOffice(FieldOffice office) {
		this.fieldOffice = office;
	}
	public FieldOffice getFieldOffice() {
		return fieldOffice;
	}
	public void setEnviroCategory(EnviroCategory env) {
		this.enviroCategory = env;
	}
	public EnviroCategory getEnviroCategory() {
		return enviroCategory;
	}
	public void setBeneficiary(Beneficiary beneficiary) {
		this.beneficiary = beneficiary;
	}
	public Beneficiary getBeneficiary() {
		return beneficiary;
	}
	public void setProjCategory(ProjectCategory projCategory) {
		this.projCategory = projCategory;
	}
	public ProjectCategory getProjCategory() {
		return projCategory;
	}
	public void setIncomeGen(boolean incomeGen) {
		this.incomeGen = incomeGen;
	}
	public boolean isIncomeGen() {
		return incomeGen;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	public boolean isShared() {
		return shared;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Status getStatus() {
		return status;
	}
	public void setBeneDirect(int beneDirect) {
		this.beneDirect = beneDirect;
	}
	public int getBeneDirect() {
		return beneDirect;
	}
	public void setBeneIndirect(int benIndirect) {
		this.beneIndirect = benIndirect;
	}
	public int getBeneIndirect() {
		return beneIndirect;
	}
	public void setInvestmentTotal(double investmentTotal) {
		this.investmentTotal = investmentTotal;
	}
	public double getInvestmentTotal() {
		return investmentTotal;
	}
	public void setInvestmentOwn(Double investmentOwn) {
		this.investmentOwn = investmentOwn;
	}
	public Double getInvestmentOwn() {
		return investmentOwn;
	}
	public void setInvestmentDonated(Double investmentDonated) {
		this.investmentDonated = investmentDonated;
	}
	public Double getInvestmentDonated() {
		return investmentDonated;
	}
	public void setNpv(double npv) {
		this.npv = npv;
	}
	public double getNpv() {
		return npv;
	}
	public void setNpvWithDonation(double npvWithDonation) {
		this.npvWithDonation = npvWithDonation;
	}
	public double getNpvWithDonation() {
		return npvWithDonation;
	}
	public void setIrr(BigDecimal irr) {
		this.irr = onlyValidIrrs(irr);
	}
	public BigDecimal getIrr() {
		return irr;
	}
	public void setIrrWithDonation(BigDecimal irrWithDonation) {
		this.irrWithDonation = onlyValidIrrs(irrWithDonation);
	}
	public BigDecimal getIrrWithDonation() {
		return irrWithDonation;
	}
	public void setWorkingCapital(double workingCapital) {
		this.workingCapital = workingCapital;
	}
	public double getWorkingCapital() {
		return workingCapital;
	}
	public void setAnnualEmployment(double annualEmployment) {
		this.annualEmployment = annualEmployment;
	}
	public double getAnnualEmployment() {
		return annualEmployment;
	}
	public void setAnnualNetIncome(double annualNetIncome) {
		this.annualNetIncome = annualNetIncome;
	}
	public double getAnnualNetIncome() {
		return annualNetIncome;
	}
	
	public void setWcOwn(double wcOwn) {
		this.wcOwn = wcOwn;
	}
	public double getWcOwn() {
		return wcOwn;
	}
	public void setWcDonated(double wcDonated) {
		this.wcDonated = wcDonated;
	}
	public double getWcDonated() {
		return wcDonated;
	}
	public void setWcFinanced(double wcFinanced) {
		this.wcFinanced = wcFinanced;
	}
	public double getWcFinanced() {
		return wcFinanced;
	}

	public int getWcPeriod() {
		return wcPeriod;
	}


	public void setWcPeriod(int wcPeriod) {
		this.wcPeriod = wcPeriod;
	}


	public int getNegativeYears() {
		return negativeYears;
	}


	public void setNegativeYears(int negativeYears) {
		this.negativeYears = negativeYears;
	}


	public void setAppConfig1(AppConfig1 adminCategory1) {
		this.appConfig1 = adminCategory1;
	}
	public AppConfig1 getAppConfig1() {
		return appConfig1;
	}
	public void setAppConfig2(AppConfig2 adminCategory2) {
		this.appConfig2 = adminCategory2;
	}
	public AppConfig2 getAppConfig2() {
		return appConfig2;
	}
	// for results report PDF
	public String getTechnicianDescription() {
		return technician.getDescription();
	}
	
	// calculated fields
	public double getInvestmentFinanced() {
		return investmentTotal-investmentOwn-investmentDonated;
	}
	public double getInvestPerBenefDirect() {
		return investmentTotal/beneDirect;
	}
	public double getInvestPerBenefIndirect() {
		return investmentTotal/beneIndirect;
	}

	public double getTotalCosts() {
		return workingCapital+investmentTotal;
	}
	public double getTotalCostsOwn() {
		return wcOwn+investmentOwn;
	}
	public double getTotalCostsDonated() {
		return wcDonated+investmentDonated;
	}
	public double getTotalCostsFinanced() {
		return wcFinanced+investmentTotal-investmentDonated-investmentOwn;
	}
	
	private BigDecimal onlyValidIrrs(BigDecimal irr) {
		if (irr.compareTo(new BigDecimal(1000))==1) {
			irr = new BigDecimal(9999999);
		} else if (irr.compareTo(new BigDecimal(-1000))==-1) {
			irr = new BigDecimal(-9999999);
		}
		return irr;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
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
		ProjectResult other = (ProjectResult) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		return true;
	}
}
	