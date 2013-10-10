package riv.objects.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name="USER")
public class User implements UserDetails {
	private static final long serialVersionUID = 2L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="USER_ID", nullable = false)
	private Integer userId;
	
	@Column(name="USERNAME", nullable = false)
	@Size(max=20)
     private String username;
	@Column(name="DESCRIPTION", nullable = false)
	@Size(max=50)
     private String description;
	@Column(name="PASSWORD", nullable = false)
	@Size(max=100)
    private String password;
	@Size(max=100)
	private String organization;
	@Size(max=50)
	private String location;
	@Size(max=50)
	private String telephone;
	@Size(max=255)
	private String email;
	private boolean administrator;
	private String lang;
     @Column(name="RESULT_USER_CODE") private boolean resultUserCode;
     @Column(name="RESULT_TECHNICIAN") private boolean resultTechnician;
     @Column(name="RESULT_FIELD_OFFICE") private boolean resultFieldOffice;
     @Column(name="RESULT_STATUS") private boolean resultStatus;
     @Column(name="RESULT_ENVIRON") private boolean resultEnviron;
     @Column(name="RESULT_INVEST_TOTAL") private boolean resultInvestTotal;
     @Column(name="RESULT_INVEST_OWN") private boolean resultInvestOwn;
     @Column(name="RESULT_INVEST_DONATED") private boolean resultInvestDonated;
     @Column(name="RESULT_INVEST_FINANCED") private boolean resultInvestFinanced;
     @Column(name="RESULT_WORKING_CAPITAL") private boolean resultWorkingCapital;
     @Column(name="RESULT_NPV") private boolean resultNpv;
     @Column(name="RESULT_IRR") private boolean resultIrr;
     @Column(name="RESULT_NPV_WITH") private boolean resultNpvWith;
     @Column(name="RESULT_IRR_WITH") private boolean resultIrrWith;
     @Column(name="RESULT_BENEF_DIRECT") private boolean resultBenefDirect;
     @Column(name="RESULT_BENEF_INDIRECT") private boolean resultBenefIndirect;
     @Column(name="RESULT_INVEST_DIRECT") private boolean resultInvestDirect;
     @Column(name="RESULT_INVEST_INDIRECT") private boolean resultInvestIndirect;
     @Column(name="RESULT_ANNUAL_EMPLOY") private boolean resultAnnualEmploy;
     @Column(name="RESULT_ANNUAL_NET_INCOME") private boolean resultAnnualNetIncome;
     @Column(name="RESULT_WC_OWN") private boolean resultWcOwn;
     @Column(name="RESULT_WC_DONATED") private boolean resultWcDonated;
     @Column(name="RESULT_WC_FINANCED") private boolean resultWcFinanced;
     @Column(name="RESULT_TOTAL_COSTS") private boolean resultTotalCosts;
     @Column(name="RESULT_TOTAL_COSTS_OWN") private boolean resultTotalCostsOwn;
     @Column(name="RESULT_TOTAL_COSTS_DONATED") private boolean resultTotalCostsDonated;
     @Column(name="RESULT_TOTAL_COSTS_FINANCED") private boolean resultTotalCostsFinanced;
     
     @Column(name="RESULT_PROJECT_CATEGORY") private boolean resultProjectCategory;
     @Column(name="RESULT_BENEF_CATEGORY") private boolean resultBenefCategory;
     @Column(name="RESULT_ADMIN_CATEGORY1") private boolean resultAdminCategory1;
     @Column(name="RESULT_ADMIN_CATEGORY2") private boolean resultAdminCategory2;

    // Constructors

    /** default constructor */
    public User() {
    }
    
    /** constructor with id */
    public User(Integer userId) {
        this.userId = userId;
    }
   

    // Property accessors

    /**
     * 
     */
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 
     */
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	/**
     * 
     */
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }


    /**
     * 
     */
    public String getOrganization() {
        return this.organization;
    }
    
    public void setOrganization(String organization) {
        this.organization = organization;
    }

    /**
     * 
     */
    public String getLocation() {
        return this.location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * 
     */
    public String getTelephone() {
        return this.telephone;
    }
    
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * 
     */
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

   /*public Set<Profile> getProfiles() {
        return this.profiles;
    }
    
    public void setProfiles(Set<Profile> profiles) {
        this.profiles = profiles;
    }
	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
	public Set<Project> getProjects() {
		return projects;
	}*/

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public void setResultUserCode(boolean resultUserCode) {
		this.resultUserCode = resultUserCode;
	}
	public boolean isResultUserCode() {
		return resultUserCode;
	}
	public void setResultTechnician(boolean resultTechnician) {
		this.resultTechnician = resultTechnician;
	}
	
	public boolean isResultTechnician() {
		return resultTechnician;
	}
	public void setResultFieldOffice(boolean resultFieldOffice) {
		this.resultFieldOffice = resultFieldOffice;
	}
	
	public boolean isResultFieldOffice() {
		return resultFieldOffice;
	}
	
	public void setResultStatus(boolean resultStatus) {
		this.resultStatus = resultStatus;
	}
	
	public boolean isResultStatus() {
		return resultStatus;
	}
	
	public void setResultInvestTotal(boolean resultInvestTotal) {
		this.resultInvestTotal = resultInvestTotal;
	}
	
	public boolean isResultInvestTotal() {
		return resultInvestTotal;
	}
	
	public void setResultEnviron(boolean resultEnviron) {
		this.resultEnviron = resultEnviron;
	}
	
	public boolean isResultEnviron() {
		return resultEnviron;
	}
	
	public void setResultInvestOwn(boolean resultInvestOwn) {
		this.resultInvestOwn = resultInvestOwn;
	}
	
	public boolean isResultInvestOwn() {
		return resultInvestOwn;
	}
	
	public void setResultInvestDonated(boolean resultInvestDonated) {
		this.resultInvestDonated = resultInvestDonated;
	}
	
	public boolean isResultInvestDonated() {
		return resultInvestDonated;
	}
	
	public void setResultInvestFinanced(boolean resultInvestFinanced) {
		this.resultInvestFinanced = resultInvestFinanced;
	}
	
	public boolean isResultInvestFinanced() {
		return resultInvestFinanced;
	}
	
	public void setResultWorkingCapital(boolean resultWorkingCapital) {
		this.resultWorkingCapital = resultWorkingCapital;
	}
	
	public boolean isResultWorkingCapital() {
		return resultWorkingCapital;
	}
	
	public void setResultNpv(boolean resultNpv) {
		this.resultNpv = resultNpv;
	}
	
	public boolean isResultNpv() {
		return resultNpv;
	}
	
	public void setResultNpvWith(boolean resultNpvWith) {
		this.resultNpvWith = resultNpvWith;
	}
	public boolean isResultNpvWith() {
		return resultNpvWith;
	}
	public void setResultIrr(boolean resultIrr) {
		this.resultIrr = resultIrr;
	}
	
	public boolean isResultIrr() {
		return resultIrr;
	}
	
	public void setResultIrrWith(boolean resultIrrWith) {
		this.resultIrrWith = resultIrrWith;
	}
	public boolean isResultIrrWith() {
		return resultIrrWith;
	}
	public void setResultBenefDirect(boolean resultBenefDirect) {
		this.resultBenefDirect = resultBenefDirect;
	}
	
	public boolean isResultBenefDirect() {
		return resultBenefDirect;
	}
	
	public void setResultBenefIndirect(boolean resultBenefIndirect) {
		this.resultBenefIndirect = resultBenefIndirect;
	}
	
	public boolean isResultBenefIndirect() {
		return resultBenefIndirect;
	}
	
	public void setResultAnnualEmploy(boolean resultAnnualEmploy) {
		this.resultAnnualEmploy = resultAnnualEmploy;
	}
	
	public boolean isResultAnnualEmploy() {
		return resultAnnualEmploy;
	}
	
	public void setResultAnnualNetIncome(boolean resultAnnualNetIncome) {
		this.resultAnnualNetIncome = resultAnnualNetIncome;
	}
	
	public boolean isResultAnnualNetIncome() {
		return resultAnnualNetIncome;
	}
	
	public void setResultWcOwn(boolean resultWcOwn) {
		this.resultWcOwn = resultWcOwn;
	}
	public boolean isResultWcOwn() {
		return resultWcOwn;
	}
	public void setResultWcDonated(boolean resultWcDonated) {
		this.resultWcDonated = resultWcDonated;
	}
	public boolean isResultWcDonated() {
		return resultWcDonated;
	}
	public void setResultWcFinanced(boolean resultWcFinanced) {
		this.resultWcFinanced = resultWcFinanced;
	}
	public boolean isResultWcFinanced() {
		return resultWcFinanced;
	}
	public void setResultTotalCosts(boolean resultTotalCosts) {
		this.resultTotalCosts = resultTotalCosts;
	}
	public boolean isResultTotalCosts() {
		return resultTotalCosts;
	}
	public void setResultTotalCostsOwn(boolean resultTotalCostsOwn) {
		this.resultTotalCostsOwn = resultTotalCostsOwn;
	}
	public boolean isResultTotalCostsOwn() {
		return resultTotalCostsOwn;
	}
	public void setResultTotalCostsDonated(boolean resultTotalCostsDonated) {
		this.resultTotalCostsDonated = resultTotalCostsDonated;
	}
	public boolean isResultTotalCostsDonated() {
		return resultTotalCostsDonated;
	}
	public void setResultTotalCostsFinanced(boolean resultTotalCostsFinanced) {
		this.resultTotalCostsFinanced = resultTotalCostsFinanced;
	}
	public boolean isResultTotalCostsFinanced() {
		return resultTotalCostsFinanced;
	}
	public void setResultProjectCategory(boolean resultProjectCategory) {
		this.resultProjectCategory = resultProjectCategory;
	}
	public boolean isResultProjectCategory() {
		return resultProjectCategory;
	}
	public void setResultBenefCategory(boolean resultBenefCategory) {
		this.resultBenefCategory = resultBenefCategory;
	}
	public boolean isResultBenefCategory() {
		return resultBenefCategory;
	}
	public void setResultAdminCategory1(boolean resultAdminCategory1) {
		this.resultAdminCategory1 = resultAdminCategory1;
	}
	public boolean isResultAdminCategory1() {
		return resultAdminCategory1;
	}
	public void setResultAdminCategory2(boolean resultAdminCategory2) {
		this.resultAdminCategory2 = resultAdminCategory2;
	}
	public boolean isResultAdminCategory2() {
		return resultAdminCategory2;
	}
	public void setResultInvestDirect(boolean resultInvestDirect) {
		this.resultInvestDirect = resultInvestDirect;
	}
	
	public boolean isResultInvestDirect() {
		return resultInvestDirect;
	}
	
	public void setResultInvestIndirect(boolean resultInvestIndirect) {
		this.resultInvestIndirect = resultInvestIndirect;
	}
	
	public boolean isResultInvestIndirect() {
		return resultInvestIndirect;
	}
	


	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
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
		User other = (User) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// everyone is a user!
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return authorities;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	
}