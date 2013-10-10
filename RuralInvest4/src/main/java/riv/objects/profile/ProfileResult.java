package riv.objects.profile;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import riv.objects.config.FieldOffice;
import riv.objects.config.Status;
import riv.objects.config.User;
/**
 * Calculated and aggregated data of a project, pre-saved in a table to increase lookup/search/reporting. 
 * @author Bar Zecharya
 *
 */
@Entity
@Table(name="PROFILE_RESULT")
public class ProfileResult implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="PROFILE_ID")
	private Integer profileId;
	@Column(name="PROFILE_NAME")
	private String profileName;
	@ManyToOne
	@JoinColumn(name="TECHNICIAN")
	private User technician;
	@ManyToOne
	@JoinColumn(name="FIELD_OFFICE")
	private FieldOffice fieldOffice;
	@ManyToOne
	@JoinColumn(name="STATUS")
	private Status status;
	@Column(name="INCOME_GEN")
	private boolean incomeGen;
	private boolean shared;
	@Column(name="BENEF_NUM")
	private int benefNum;
	@Column(name="INVESTMENT_TOTAL", precision=12, scale=4)
	private Double investmentTotal;
	@Column(name="INVESTMENT_OWN", precision=12, scale=4)
	private Double investmentOwn;
	@Column(name="TOT_INCOME", precision=12, scale=4)
	private Double totIncome;
	@Column(name="OPER_COST", precision=12, scale=4)
	private Double operCost;
	@Column(name="GENERAL_COST", precision=12, scale=4)
	private Double generalCost;
	@Column(name="ANNUAL_RESERVE", precision=12, scale=4)
	private Double annualReserve;
	
	public ProfileResult() {}
	
	// for downloading
	public String getDownloadName() {
		// filename shouldn't contain unacceptable characters
		return profileName.replaceAll("[:<>\\.|\\?\\*/\\\\\"\\s]", "_");
	}

	
	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	/*public void setProfile(Profile profile) {
		this.profile = profile;
	}
	public Profile getProfile() {
		return profile;
	}*/
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getProfileName() {
		return profileName;
	}
	
	public void setTechnician(User technician) {
		this.technician = technician;
	}

	public User getTechnician() {
		return technician;
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

	public void setFieldOffice(FieldOffice fieldOffice) {
		this.fieldOffice = fieldOffice;
	}

	public FieldOffice getFieldOffice() {
		return fieldOffice;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Status getStatus() {
		return status;
	}

	public void setBenefNum(int benefNum) {
		this.benefNum = benefNum;
	}
	public int getBenefNum() {
		return benefNum;
	}
	public void setInvestmentTotal(Double investmentTotal) {
		this.investmentTotal = investmentTotal;
	}
	public Double getInvestmentTotal() {
		return investmentTotal;
	}
	public void setInvestmentOwn(Double investmentOwn) {
		this.investmentOwn = investmentOwn;
	}
	public Double getInvestmentOwn() {
		return investmentOwn;
	}
	public void setTotIncome(Double netIncome) {
		this.totIncome = netIncome;
	}
	public Double getTotIncome() {
		return totIncome;
	}

	public void setOperCost(Double operCost) {
		this.operCost = operCost;
	}

	public Double getOperCost() {
		return operCost;
	}

	public void setGeneralCost(Double generalCost) {
		this.generalCost = generalCost;
	}

	public Double getGeneralCost() {
		return generalCost;
	}

	public void setAnnualReserve(Double annualReserve) {
		this.annualReserve = annualReserve;
	}

	public Double getAnnualReserve() {
		return annualReserve;
	}
	
	// calculated fields
	public Double getInvestmentExt() {
		return investmentTotal-investmentOwn;
	}
	public Double getIncomeAfterAnnual() {
		return totIncome-operCost-generalCost-annualReserve;
	}
	public Double getYearsToRecover() {
		Double value = investmentTotal/(totIncome-operCost-generalCost);;
		return  (double)Math.round(value * 10) / 10;
	}
	public Double getInvestPerBenef() {
		return investmentTotal/benefNum;
	}
	public Double getCostPerBenef() {
		return (generalCost+operCost)/benefNum;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProfileResult other = (ProfileResult) obj;
		if (annualReserve == null) {
			if (other.annualReserve != null)
				return false;
		} else if (!annualReserve.equals(other.annualReserve))
			return false;
		if (benefNum != other.benefNum)
			return false;
		if (fieldOffice == null) {
			if (other.fieldOffice != null)
				return false;
		} else if (!fieldOffice.equals(other.fieldOffice))
			return false;
		if (generalCost == null) {
			if (other.generalCost != null)
				return false;
		} else if (!generalCost.equals(other.generalCost))
			return false;
		if (incomeGen != other.incomeGen)
			return false;
		if (investmentOwn == null) {
			if (other.investmentOwn != null)
				return false;
		} else if (!investmentOwn.equals(other.investmentOwn))
			return false;
		if (investmentTotal == null) {
			if (other.investmentTotal != null)
				return false;
		} else if (!investmentTotal.equals(other.investmentTotal))
			return false;
		if (operCost == null) {
			if (other.operCost != null)
				return false;
		} else if (!operCost.equals(other.operCost))
			return false;
		if (profileId != other.getProfileId())
			return false;
		if (profileName == null) {
			if (other.profileName != null)
				return false;
		} else if (!profileName.equals(other.profileName))
			return false;
		if (shared != other.shared)
			return false;
		if (technician == null) {
			if (other.technician != null)
				return false;
		} else if (!technician.equals(other.technician))
			return false;
		if (totIncome == null) {
			if (other.totIncome != null)
				return false;
		} else if (!totIncome.equals(other.totIncome))
			return false;
		return true;
	}
	
	
}
