package riv.objects;


/**
 * Contains aggregated financial data for a project, organized per year.  
 * Typically an array of ProjectFinanceData, one per year of a project, is used to calculate
 * figures such as Internal Rate of Return and Net Present Value
 * @author Bar Zecharya
 *
 */
public class ProjectFinanceData implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	//private AnalysisType analType;
	protected int year;
	protected double incOperationDonation;
	protected double incOperationDonationWithout;
	protected double incSales;
	protected double incSalesWithout;
	protected double incSalesInternal;
	protected double incSalesInternalWithout;
	protected double incSalvage;
	protected double incSalvageWithout;
	protected double incResidual;
	protected double incResidualWithout;
	protected double costOperation;
	protected double costOperationWithout;
	protected double costOperationInternal;
	protected double costOperationInternalWithout;
	protected double costReplace;
	protected double costReplaceWithout;
	protected double costGeneral;
	protected double costGeneralOwn;
	protected double costGeneralWithout;
	protected double costGeneralWithoutOwn;
	protected double costMaintenance;
	protected double costMaintenanceWithout;
	protected double costInvest;
	protected double costInvestOwn;
	protected double costInvestDonated;
	protected double costInvestWithout;
	protected double costInvestOwnWithout;
	protected double costInvestDonatedWithout;
	protected double workingCapitalCapital;
	protected double workingCapitalInterest;
	protected double loan1interest;
	protected double loan1capital;
	protected double loan2interest;
	protected double loan2capital;
	protected double workingCapitalDonation;
	protected double workingCapitalOwn;
	protected double loanReceived;
	
	// calculated fields
	public double getIncSalesExternal() {
		return incSales-incSalesInternal;
	}
	public double getIncSalesExternalWithout() {
		return incSalesWithout-incSalesInternalWithout;
	}
	
	public double getCostInvestFinance() {
		return costInvest-costInvestOwn-costInvestDonated;
	}
	public double getCostInvestFinanceWithout() {
		return costInvestWithout-costInvestOwnWithout-costInvestDonatedWithout;
	}
	
	public double getTotalIncomeCashFlow() {
		return incSales-incSalesInternal+incSalvage+loanReceived+workingCapitalCapital+costInvestDonated+costInvestOwn+workingCapitalDonation+workingCapitalOwn;
	}
	public double getTotalIncomeCashFlowWithout() {
		return incSalesWithout-incSalesInternalWithout+incSalvageWithout+costInvestDonatedWithout+costInvestOwnWithout+getCostInvestFinanceWithout();
	}
//	public double getTotalIncomeProfitabilityx() {
//		return incSales-incSalesWithout+incSalvage-incSalvageWithout+incResidual-incResidualWithout;
//	}
	public double getTotalIncomeProfitabilityWith() {
		return incSales+incSalvage+incResidual;
	}
	public double getTotalIncomeProfitabilityWithout() {
		return incSalesWithout+incSalvageWithout+incResidualWithout;
	}
//	public double getTotalIncome() {
//		if (analType==AnalysisType.CashFlow) {
//			return getMainIncome()+loanReceived+workingCapitalCapital+costInvestDonated+costInvestOwn+workingCapitalDonation+workingCapitalOwn;
//		} else { 
//			return incSales-incSalesWithout+incSalvage-incSalvageWithout+incResidual-incResidualWithout;
//		}
//	}
//	
//	public double getMainIncome() {
//		if (analType!=AnalysisType.CashFlow) {
//			throw new RuntimeException("This method should only be used for Cash Flow analysis, not profitability analysis.");
//		}
//		return incSales-incSalesInternal+incSalvage;
//	}
//	
//	public double getInvestAndRecurringCosts() {
//		if (analType!=AnalysisType.CashFlow) {
//			throw new RuntimeException("This method should only be used for Cash Flow analysis, not profitability analysis.");
//		}
//		return costInvest+costReplace+costOperation-costOperationInternal+costGeneral-costGeneralOwn+costMaintenance;
//	}
//	public double getFinancingCosts() {
//		if (analType!=AnalysisType.CashFlow) {
//			throw new RuntimeException("This method should only be used for Cash Flow analysis, not profitability analysis.");
//		}
//		return loan1capital+loan1interest+loan2capital+loan2interest+workingCapitalCapital+workingCapitalInterest;
//	}
//	
	public double getTotalCostsCashFlow() {
		return costInvest+costReplace+costOperation-costOperationInternal+costGeneral-costGeneralOwn+costMaintenance
		+ loan1capital+loan1interest+loan2capital+loan2interest+workingCapitalCapital+workingCapitalInterest;
	}
	public double getTotalCostsCashFlowWithout() {
		return costInvestWithout+costReplaceWithout+costOperationWithout-costOperationInternalWithout
				+costGeneralWithout-costGeneralWithoutOwn+costMaintenanceWithout;
	}
	public double getTotalCostsProfitabilityWith() {
		return costOperation+costReplace+costGeneral+costMaintenance+costInvest;
	}
	public double getTotalCostsProfitabilityWithout() {
		return costOperationWithout+costReplaceWithout+costGeneralWithout+costMaintenanceWithout+costInvestWithout;
	}
	
//	private double getTotalCosts() {
//		if (analType==AnalysisType.CashFlow) {
//			return getInvestAndRecurringCosts()+getFinancingCosts();
//		} else { 
//			return costOperation-costOperationWithout+costReplace-costReplaceWithout+costGeneral-costGeneralWithout+costMaintenance-costMaintenanceWithout+costInvest-costInvestWithout;
//		}
//	}
//	
//	public double getNetIncome() {
//		return getTotalIncome()-getTotalCosts();
//	}
	
	public ProjectFinanceData(int year) {//, AnalysisType analType) {
		this.year=year;
//		this.analType=analType;
	}
	
	// property accessors
	public void setYear(int year) {
		this.year=year;
	}
	public int getYear() {
		return year;
	}
	public double getIncOperationDonation() {
		return incOperationDonation;
	}
	public double getIncOperationDonationWithout() {
		return incOperationDonationWithout;
	}
	public double getIncSales() {
		return incSales;
	}
	public double getIncSalesWithout() {
		return incSalesWithout;
	}
	public double getIncSalesInternal() {
		return incSalesInternal;
	}
	public double getIncSalesInternalWithout() {
		return incSalesInternalWithout;
	}
	public double getIncSalvage() {
		return incSalvage;
	}
	public double getIncSalvageWithout() {
		return incSalvageWithout;
	}
	public double getIncResidual() {
		return incResidual;
	}
	public double getIncResidualWithout() {
		return incResidualWithout;
	}
	public double getIncCapitalDonation() {
		return workingCapitalDonation;
	}
	public double getIncCapitalOwn() {
		return workingCapitalOwn;
	}
	public double getCostOperation() {
		return costOperation;
	}
	public double getCostOperationWithout() {
		return costOperationWithout;
	}
	public double getCostOperationInternal() {
		return costOperationInternal;
	}
	public double getCostOperationInternalWithout() {
		return costOperationInternalWithout;
	}
	public double getCostReplace() {
		return costReplace;
	}
	public double getCostReplaceWithout() {
		return costReplaceWithout;
	}
	public double getCostGeneral() {
		return costGeneral;
	}
	public double getCostGeneralOwn() {
		return costGeneralOwn;
	}
	public double getCostGeneralWithout() {
		return costGeneralWithout;
	}
	public double getCostGeneralWithoutOwn() {
		return costGeneralWithoutOwn;
	}
	public double getCostMaintenance() {
		return costMaintenance;
	}
	public double getCostMaintenanceWithout() {
		return costMaintenanceWithout;
	}
	public double getCostInvest() {
		return costInvest;
	}
	public double getCostInvestOwn() {
		return costInvestOwn;
	}
	public double getCostInvestDonated() {
		return costInvestDonated;
	}
	public double getCostInvestWithout() {
		return costInvestWithout;
	}
	public double getCostInvestOwnWithout() {
		return costInvestOwnWithout;
	}
	public double getCostInvestDonatedWithout() {
		return costInvestDonatedWithout;
	}
	public double getWorkingCapitalCapital() {
		return workingCapitalCapital;
	}
	public double getWorkingCapital() {
		return workingCapitalInterest;
	}
	public double getLoan1interest() {
		return loan1interest;
	}
	public double getLoan1capital() {
		return loan1capital;
	}
	public double getLoan2interest() {
		return loan2interest;
	}
	public double getLoan2capital() {
		return loan2capital;
	}
	public double getLoanReceived() {
		return loanReceived;
	}
}
