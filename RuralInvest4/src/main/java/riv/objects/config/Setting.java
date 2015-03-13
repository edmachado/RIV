package riv.objects.config;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import riv.util.CurrencyFormatter;

@Entity
@Table(name="SETTING")
public class Setting implements java.io.Serializable {
	@Transient
	private CurrencyFormatter currencyFormatter;
	@Transient
	private DecimalFormat decimalFormat;

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="SETTING_ID")
	private Integer settingId;
	@Column(name="ORG_NAME")
	@Size(max=100)
	private String orgName;
	@Lob
	@Column(name="ORG_LOGO", length = 20971520)
	@Basic(fetch = FetchType.LAZY)
	private byte[] orgLogo;
	@Transient
	private byte[] userLogo; // for importing from older XML Serialization
	@Column(name="LANG")
	@Size(max=2)
	@NotEmpty
	private String lang;
	@Column(name="CURRENCY_NAME")
	@Size(max=50)
	@NotEmpty
	private String currencyName;
	@Column(name="CURRENCY_SYM")
	@Size(max=20)
	@NotEmpty
	private String currencySym;
	@Column(name="DEC_SEPARATE")
	@Size(max=1)
	@NotEmpty
	private String decimalSeparator=".";
	@Column(name="THOU_SEPARATE")
	@Size(max=1)
	@NotEmpty
	private String thousandSeparator=",";
	@Column(name="DEC_LENGTH")
	@Max(4)
	private Integer decimalLength=2;
	@Column(name="EXCH_RATE")
	@NotNull
	@Max(9999)
	private Double exchRate;
	@Column(name="LOCATION1")
	@NotEmpty
	@Size(max=20)
	private String location1;
	@Column(name="LOCATION2")
	@NotEmpty
	@Size(max=20)
	private String location2;
	@Column(name="LOCATION3")
	@NotEmpty
	@Size(max=20)
	private java.lang.String Location3;
	@Column(name="DISCOUNT_RATE")
	@Max(1000)
	@NotNull
	private Double discountRate;
	@Column(name="MAX_DURATION")
	@NotNull
	private Integer maxDuration;
	@Column(name="LOAN1_MAX")
	@NotNull
	private Integer loan1Max;
	@Column(name="LOAN1_GRACE_CAPITAL")
	@NotNull
	private Integer loan1GraceCapital;
	@Column(name="LOAN1_GRACE_INTEREST")
	@NotNull
	private Integer loan1GraceInterest;
	@Column(name="LOAN2_MAX")
	@NotNull
	private Integer loan2Max;
	@Column(name="LOAN2_GRACE_CAPITAL")
	@NotNull
	private Integer loan2GraceCapital;
	@Column(name="LOAN2_GRACE_INTEREST")
	@NotNull
	private Integer loan2GraceInterest;
	
	@Column(name="LINK1_TEXT")
	@Size(max=100)
	private String link1Text;
	@Column(name="LINK1_URL")
	@Size(max=100)
	private String link1Url;
	@Column(name="LINK2_TEXT")
	@Size(max=100)
	private String link2Text;
	@Column(name="LINK2_URL")
	@Size(max=100)
	private String link2Url;
	@Column(name="LINK3_TEXT")
	@Size(max=100)
	private String link3Text;
	@Column(name="LINK3_URL")
	@Size(max=100)
	private String link3Url;
	@Column(name="LINK4_TEXT")
	@Size(max=100)
	private String link4Text;
	@Column(name="LINK4_URL")
	@Size(max=100)
	private String link4Url;
	
	@Column(name="ADMIN1_TITLE")
	@Size(max=100)
	private String admin1Title;
	@Column(name="ADMIN1_HELP")
	@Size(max=500)
	private String admin1Help;
	@Column(name="ADMIN1_ENABLED")
	private boolean admin1Enabled;
	@Column(name="ADMIN2_TITLE")
	@Size(max=100)
	private String admin2Title;
	@Column(name="ADMIN2_HELP")
	@Size(max=500)
	private String admin2Help;
	@Column(name="ADMIN2_ENABLED")
	private boolean admin2Enabled;
	
	@Column(name="ADMIN_MISC1_TITLE")
	@Size(max=100)
	private String adminMisc1Title;
	@Column(name="ADMIN_MISC1_HELP")
	@Size(max=500)
	private String adminMisc1Help;
	@Column(name="ADMIN_MISC1_MULTILINE")
	private boolean adminMisc1Multiline;
	@Column(name="ADMIN_MISC1_ENABLED")
	private boolean adminMisc1Enabled;
	@Column(name="ADMIN_MISC2_TITLE")
	@Size(max=100)
	private String adminMisc2Title;
	@Column(name="ADMIN_MISC2_HELP")
	@Size(max=500)
	private String adminMisc2Help;
	@Column(name="ADMIN_MISC2_MULTILINE")
	private boolean adminMisc2Multiline;
	@Column(name="ADMIN_MISC2_ENABLED")
	private boolean adminMisc2Enabled;
	@Column(name="ADMIN_MISC3_TITLE")
	@Size(max=100)
	private String adminMisc3Title;
	@Column(name="ADMIN_MISC3_HELP")
	@Size(max=500)
	private String adminMisc3Help;
	@Column(name="ADMIN_MISC3_MULTILINE")
	private boolean adminMisc3Multiline;
	@Column(name="ADMIN_MISC3_ENABLED")
	private boolean adminMisc3Enabled;

	// methods
	public CurrencyFormatter getCurrencyFormatter() {
		if (currencyFormatter==null) currencyFormatter=new CurrencyFormatter(thousandSeparator, decimalSeparator, decimalLength);
		return currencyFormatter;
	}
	public DecimalFormat getDecimalFormat() {
		if (decimalFormat==null) {
			DecimalFormatSymbols dfs = new DecimalFormatSymbols();
			dfs.setDecimalSeparator(decimalSeparator.charAt(0));
			dfs.setGroupingSeparator(thousandSeparator.charAt(0));
			decimalFormat=new DecimalFormat("#,##0.####", dfs);
		}
		return decimalFormat;
	}

	public void refreshCurrencyFormatter() {
		currencyFormatter=null; decimalFormat=null;
		
	}
	// Constructors

	/** default constructor */
	public Setting() {
	}

	/** constructor with id */
	public Setting(Integer SettingId) {
		this.settingId = SettingId;
	}

	// Property accessors
	public Integer getSettingId() {
		return this.settingId;
	}

	public void setSettingId(java.lang.Integer SettingId) {
		this.settingId = SettingId;
	}

	public java.lang.String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(java.lang.String OrgName) {
		this.orgName = OrgName;
	}

	public void setOrgLogo(byte[] orgLogo) {
		this.orgLogo = orgLogo;
	}

	public byte[] getOrgLogo() {
		return orgLogo;
	}

	public byte[] getUserLogo() {
		return userLogo;
	}
	public void setUserLogo(byte[] userLogo) {
		this.userLogo = userLogo;
	}
	
	public java.lang.String getLang() {
		return this.lang;
	}

	public void setLang(java.lang.String Lang) {
		this.lang = Lang;
	}

	public String getDecimalSeparator() {
		return this.decimalSeparator;
	}

	public void setDecimalSeparator(String decimalSeparator) {
		this.decimalSeparator = decimalSeparator;
	}

	public String getThousandSeparator() {
		return this.thousandSeparator;
	}

	public void setThousandSeparator(String thousandSeparator) {
		this.thousandSeparator = thousandSeparator;
	}

	public void setDecimalLength(Integer decimalLength) {
		this.decimalLength = decimalLength;
	}

	public Integer getDecimalLength() {
		return decimalLength;
	}

	public java.lang.String getCurrencyName() {
		return this.currencyName;
	}

	public void setCurrencyName(java.lang.String CurrencyName) {
		this.currencyName = CurrencyName;
	}

	public java.lang.String getCurrencySym() {
		return this.currencySym;
	}

	public void setCurrencySym(java.lang.String CurrencySym) {
		this.currencySym = CurrencySym;
	}

	public Double getExchRate() {
		return this.exchRate;
	}

	public void setExchRate(Double ExchRate) {
		this.exchRate = ExchRate;
	}

	public java.lang.String getLocation1() {
		return this.location1;
	}

	public void setLocation1(java.lang.String Location1) {
		this.location1 = Location1;
	}

	public java.lang.String getLocation2() {
		return this.location2;
	}

	public void setLocation2(java.lang.String Location2) {
		this.location2 = Location2;
	}

	public java.lang.String getLocation3() {
		return this.Location3;
	}

	public void setLocation3(java.lang.String Location3) {
		this.Location3 = Location3;
	}

	public Double getDiscountRate() {
		return this.discountRate;
	}

	public void setDiscountRate(Double DiscountRate) {
		this.discountRate = DiscountRate;
	}

	public java.lang.Integer getMaxDuration() {
		return this.maxDuration;
	}

	public void setMaxDuration(java.lang.Integer MaxDuration) {
		this.maxDuration = MaxDuration;
	}

	public java.lang.Integer getLoan1Max() {
		return this.loan1Max;
	}

	public void setLoan1Max(java.lang.Integer Loan1Max) {
		this.loan1Max = Loan1Max;
	}

	public java.lang.Integer getLoan1GraceCapital() {
		return this.loan1GraceCapital;
	}

	public void setLoan1GraceCapital(java.lang.Integer Loan1GraceCapital) {
		this.loan1GraceCapital = Loan1GraceCapital;
	}

	public java.lang.Integer getLoan1GraceInterest() {
		return this.loan1GraceInterest;
	}

	public void setLoan1GraceInterest(java.lang.Integer Loan1GraceInterest) {
		this.loan1GraceInterest = Loan1GraceInterest;
	}

	public java.lang.Integer getLoan2Max() {
		return this.loan2Max;
	}

	public void setLoan2Max(java.lang.Integer Loan2Max) {
		this.loan2Max = Loan2Max;
	}

	public java.lang.Integer getLoan2GraceCapital() {
		return this.loan2GraceCapital;
	}

	public void setLoan2GraceCapital(java.lang.Integer Loan2GraceCapital) {
		this.loan2GraceCapital = Loan2GraceCapital;
	}

	public java.lang.Integer getLoan2GraceInterest() {
		return this.loan2GraceInterest;
	}

	public void setLoan2GraceInterest(java.lang.Integer Loan2GraceInterest) {
		this.loan2GraceInterest = Loan2GraceInterest;
	}

	public void setLink1Text(String link1Text) {
		this.link1Text = link1Text;
	}

	public String getLink1Text() {
		return link1Text;
	}

	public void setLink1Url(String link1Url) {
		this.link1Url = link1Url;
	}

	public String getLink1Url() {
		return link1Url;
	}

	public void setLink2Text(String link2Text) {
		this.link2Text = link2Text;
	}

	public String getLink2Text() {
		return link2Text;
	}

	public void setLink2Url(String link2Url) {
		this.link2Url = link2Url;
	}

	public String getLink2Url() {
		return link2Url;
	}

	public void setLink3Text(String link3Text) {
		this.link3Text = link3Text;
	}

	public String getLink3Text() {
		return link3Text;
	}

	public void setLink3Url(String link3Url) {
		this.link3Url = link3Url;
	}

	public String getLink3Url() {
		return link3Url;
	}

	public void setLink4Text(String link4Text) {
		this.link4Text = link4Text;
	}

	public String getLink4Text() {
		return link4Text;
	}

	public void setLink4Url(String link4Url) {
		this.link4Url = link4Url;
	}

	public String getLink4Url() {
		return link4Url;
	}

	public void setAdmin1Title(String admin1Title) {
		this.admin1Title = admin1Title;
	}

	public String getAdmin1Title() {
		return admin1Title;
	}

	public void setAdmin1Help(String admin1Help) {
		this.admin1Help = admin1Help;
	}

	public String getAdmin1Help() {
		return admin1Help;
	}

	public void setAdmin1Enabled(boolean admin1Enabled) {
		this.admin1Enabled = admin1Enabled;
	}

	public boolean getAdmin1Enabled() {
		return admin1Enabled;
	}

	public void setAdmin2Title(String admin2Title) {
		this.admin2Title = admin2Title;
	}

	public String getAdmin2Title() {
		return admin2Title;
	}

	public void setAdmin2Help(String admin2Help) {
		this.admin2Help = admin2Help;
	}

	public String getAdmin2Help() {
		return admin2Help;
	}

	public void setAdmin2Enabled(boolean admin2Enabled) {
		this.admin2Enabled = admin2Enabled;
	}

	public boolean getAdmin2Enabled() {
		return admin2Enabled;
	}

	public void setAdminMisc1Title(String adminMisc1Title) {
		this.adminMisc1Title = adminMisc1Title;
	}

	public String getAdminMisc1Title() {
		return adminMisc1Title;
	}

	public void setAdminMisc1Help(String adminMisc1Help) {
		this.adminMisc1Help = adminMisc1Help;
	}

	public String getAdminMisc1Help() {
		return adminMisc1Help;
	}

	public void setAdminMisc1Multiline(boolean adminMisc1Multiline) {
		this.adminMisc1Multiline = adminMisc1Multiline;
	}

	public boolean isAdminMisc1Multiline() {
		return adminMisc1Multiline;
	}

	public void setAdminMisc1Enabled(boolean adminMisc1Enabled) {
		this.adminMisc1Enabled = adminMisc1Enabled;
	}

	public boolean isAdminMisc1Enabled() {
		return adminMisc1Enabled;
	}

	public void setAdminMisc2Title(String adminMisc2Title) {
		this.adminMisc2Title = adminMisc2Title;
	}

	public String getAdminMisc2Title() {
		return adminMisc2Title;
	}

	public void setAdminMisc2Help(String adminMisc2Help) {
		this.adminMisc2Help = adminMisc2Help;
	}

	public String getAdminMisc2Help() {
		return adminMisc2Help;
	}

	public void setAdminMisc2Multiline(boolean adminMisc2Multiline) {
		this.adminMisc2Multiline = adminMisc2Multiline;
	}

	public boolean isAdminMisc2Multiline() {
		return adminMisc2Multiline;
	}

	public void setAdminMisc2Enabled(boolean adminMisc2Enabled) {
		this.adminMisc2Enabled = adminMisc2Enabled;
	}

	public boolean isAdminMisc2Enabled() {
		return adminMisc2Enabled;
	}

	public void setAdminMisc3Title(String adminMisc3Title) {
		this.adminMisc3Title = adminMisc3Title;
	}

	public String getAdminMisc3Title() {
		return adminMisc3Title;
	}

	public void setAdminMisc3Help(String adminMisc3Help) {
		this.adminMisc3Help = adminMisc3Help;
	}

	public String getAdminMisc3Help() {
		return adminMisc3Help;
	}

	public void setAdminMisc3Multiline(boolean adminMisc3Multiline) {
		this.adminMisc3Multiline = adminMisc3Multiline;
	}

	public boolean isAdminMisc3Multiline() {
		return adminMisc3Multiline;
	}

	public void setAdminMisc3Enabled(boolean adminMisc3Enabled) {
		this.adminMisc3Enabled = adminMisc3Enabled;
	}

	public boolean isAdminMisc3Enabled() {
		return adminMisc3Enabled;
	}
	
	public Setting copy() {
		Setting s = new Setting(this.settingId);
		s.setAdmin1Enabled(admin1Enabled);
		s.setAdmin1Help(admin1Help);
		s.setAdmin1Title(admin1Title);
		s.setAdmin2Enabled(admin2Enabled);
		s.setAdmin2Help(admin2Help);
		s.setAdmin2Title(admin2Title);
		s.setAdminMisc1Title(adminMisc1Title);
		s.setAdminMisc1Enabled(adminMisc1Enabled);
		s.setAdminMisc1Help(adminMisc1Help);
		s.setAdminMisc1Multiline(adminMisc1Multiline);
		s.setAdminMisc2Title(adminMisc2Title);
		s.setAdminMisc2Enabled(adminMisc2Enabled);
		s.setAdminMisc2Help(adminMisc2Help);
		s.setAdminMisc2Multiline(adminMisc2Multiline);
		s.setAdminMisc3Title(adminMisc3Title);
		s.setAdminMisc3Enabled(adminMisc3Enabled);
		s.setAdminMisc3Help(adminMisc3Help);
		s.setAdminMisc3Multiline(adminMisc3Multiline);
		s.setCurrencyName(currencyName);
		s.setCurrencySym(currencySym);
		s.setDecimalLength(decimalLength);
		s.setDecimalSeparator(decimalSeparator);
		s.setDiscountRate(discountRate);
		s.setExchRate(exchRate);
		s.setLang(lang);
		s.setLink1Text(link1Text);
		s.setLink1Url(link1Url);
		s.setLink2Text(link2Text);
		s.setLink2Url(link2Url);
		s.setLink3Text(link3Text);
		s.setLink3Url(link3Url);
		s.setLink4Text(link4Text);
		s.setLink4Url(link4Url);
		s.setLoan1GraceCapital(loan1GraceCapital);
		s.setLoan1GraceInterest(loan1GraceInterest);
		s.setLoan1Max(loan1Max);
		s.setLoan2GraceCapital(loan2GraceCapital);
		s.setLoan2GraceInterest(loan2GraceInterest);
		s.setLoan2Max(loan2Max);
		s.setLocation1(location1);
		s.setLocation2(location2);
		s.setLocation3(Location3);
		s.setMaxDuration(maxDuration);
		s.setOrgLogo(orgLogo);
		s.setOrgName(orgName);
		s.setThousandSeparator(thousandSeparator);
		return s;
	}
}