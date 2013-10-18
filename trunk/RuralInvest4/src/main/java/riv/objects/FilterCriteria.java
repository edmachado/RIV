package riv.objects;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;

import riv.objects.config.*;

/**
 * Encapsulation of the criteria users can choose from when performing a query for Profiles and Projects.
 * @author Bar Zecharya
 *
 */
@Component
@Scope(value="session", proxyMode=ScopedProxyMode.TARGET_CLASS)
public class FilterCriteria implements Serializable {
	private static final long serialVersionUID = 6486656765735918373L;
	
	private String objType; // igpj, nigpj, igpf, nigpf
	private boolean unfinished;
	private String freeText;
	private List<Status> statuses;
	private List<FieldOffice> offices;
	private List<ProjectCategory> categories;
	private List<EnviroCategory> enviroCategories;
	private List<User> users;
	private List<Beneficiary> beneficiaries;
	private List<AppConfig1> appConfig1s;
	private List<AppConfig2> appConfig2s;
	private int irrCriteria; // 0=all, 1=equals, 2=gt, 3=lt
	private BigDecimal irrValue;
	//private boolean npvAndOr;
	private int npvCriteria; // 0=all, 1=equals, 2=gt, 3=lt
	private double npvValue;
	//private boolean totInvestAndOr;
	private int totInvestCriteria; // 0=all, 1=equals, 2=gt, 3=lt
	private double totInvestValue;
	//private boolean externAndOr;
	private int externCriteria; // 0=all, 1=equals, 2=gt, 3=lt
	private double externValue;
	
	/**
	 * Constructor.  Instantiates collections and sets a default object type to "igpf", or Income Generating Profile.
	 */
	public FilterCriteria() {
		objType="igpj";
		statuses = new ArrayList<Status>();
		offices = new ArrayList<FieldOffice>();
		categories = new ArrayList<ProjectCategory>();
		enviroCategories = new ArrayList<EnviroCategory>();
		beneficiaries = new ArrayList<Beneficiary>();
		statuses = new ArrayList<Status>();
		users = new ArrayList<User>();
		appConfig1s = new ArrayList<AppConfig1>();
		appConfig2s = new ArrayList<AppConfig2>();
	}
	
	/* methods */
	public boolean isIncomeGen() {
		return objType.startsWith("ig");
	}
	public boolean isProfile() {
		return objType.endsWith("pf");
	}
	
	/* property accessors */
	public void setObjType(String objType) {
		this.objType = objType;
	}
	public String getObjType() {
		return objType;
	}
	
	public boolean isUnfinished() {
		return unfinished;
	}
	public void setUnfinished(boolean unfinished) {
		this.unfinished=unfinished;
	}
	
	public void setStatuses(ArrayList<Status> statuses) {
		this.statuses = statuses;
	}
	public List<Status> getStatuses() {
		return statuses;
	}
	public void setOffices(List<FieldOffice> offices) {
		this.offices = offices;
	}
	public List<FieldOffice> getOffices() {
		return offices;
	}
	public void setCategories(List<ProjectCategory> categories) {
		this.categories = categories;
	}
	public List<ProjectCategory> getCategories() {
		return categories;
	}
	public void setEnviroCategories(List<EnviroCategory> enviroCategories) {
		this.enviroCategories = enviroCategories;
	}
	public List<EnviroCategory> getEnviroCategories() {
		return enviroCategories;
	}
	public void setUsers(List<User> users) {
		this.users = users;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setBeneficiaries(List<Beneficiary> beneficiaries) {
		this.beneficiaries = beneficiaries;
	}

	public List<Beneficiary> getBeneficiaries() {
		return beneficiaries;
	}

	public void setAppConfig1s(List<AppConfig1> appConfig1s) {
		this.appConfig1s = appConfig1s;
	}

	public List<AppConfig1> getAppConfig1s() {
		return appConfig1s;
	}

	public void setAppConfig2s(List<AppConfig2> appConfig2s) {
		this.appConfig2s = appConfig2s;
	}

	public List<AppConfig2> getAppConfig2s() {
		return appConfig2s;
	}

	public void setIrrCriteria(int irrCriteria) {
		this.irrCriteria = irrCriteria;
	}
	public int getIrrCriteria() {
		return irrCriteria;
	}
	public void setIrrValue(BigDecimal irrValue) {
		this.irrValue = irrValue;
	}
	public BigDecimal getIrrValue() {
		return irrValue;
	}
//	public void setNpvAndOr(boolean npvAndOr) {
//		this.npvAndOr = npvAndOr;
//	}
//	public boolean isNpvAndOr() {
//		return npvAndOr;
//	}
	public void setNpvCriteria(int npvCriteria) {
		this.npvCriteria = npvCriteria;
	}
	public int getNpvCriteria() {
		return npvCriteria;
	}
	public void setNpvValue(double npvValue) {
		this.npvValue = npvValue;
	}
	public double getNpvValue() {
		return npvValue;
	}
//	public void setTotInvestAndOr(boolean totInvestAndOr) {
//		this.totInvestAndOr = totInvestAndOr;
//	}
//	public boolean isTotInvestAndOr() {
//		return totInvestAndOr;
//	}
	public void setTotInvestCriteria(int totInvestCriteria) {
		this.totInvestCriteria = totInvestCriteria;
	}
	public int getTotInvestCriteria() {
		return totInvestCriteria;
	}
	public void setTotInvestValue(double totInvestValue) {
		this.totInvestValue = totInvestValue;
	}
	public double getTotInvestValue() {
		return totInvestValue;
	}
//	public void setExternAndOr(boolean externAndOr) {
//		this.externAndOr = externAndOr;
//	}
//	public boolean isExternAndOr() {
//		return externAndOr;
//	}
	public void setExternCriteria(int externCriteria) {
		this.externCriteria = externCriteria;
	}
	public int getExternCriteria() {
		return externCriteria;
	}
	public void setExternValue(double externValue) {
		this.externValue = externValue;
	}
	public double getExternValue() {
		return externValue;
	}
	public void setFreeText(String freeText) {
		this.freeText = freeText;
	}
	public String getFreeText() {
		return freeText;
	}
}