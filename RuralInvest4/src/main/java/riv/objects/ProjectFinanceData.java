package riv.objects;

import riv.objects.FinanceMatrix.ProjectScenario;

/**
 * Contains aggregated financial data for a project, organized per year.  
 * Typically an array of ProjectFinanceData, one per year of a project, is used to calculate
 * figures such as Internal Rate of Return and Net Present Value
 * @author Bar Zecharya
 *
 */
public class ProjectFinanceData implements java.io.Serializable{
	private static final long serialVersionUID = 1L;

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
	protected double workingCapitalReceived;
	protected double loan1interest;
	protected double loan1capital;
	protected double loan2interest;
	protected double loan2capital;
	protected double workingCapitalDonation;
	protected double workingCapitalOwn;
	protected double loanReceived;
	protected double cumulativeBeforeDonation;
	protected double cumulativeAfterDonation;
	
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
	
	public double getTotalIncomeProfitability(ProjectScenario scenario) {
		if (scenario==ProjectScenario.With) {
			return getTotalIncomeProfitabilityWith();
		} else if (scenario==ProjectScenario.Without) {
			return getTotalIncomeProfitabilityWithout();
		} else { // incremental
			return getTotalIncomeProfitabilityWith()-getTotalIncomeProfitabilityWithout();
		}
	}

	public double getTotalIncomeProfitabilityWith() {
		return incSales+incSalvage+incResidual;
	}
	public double getTotalIncomeProfitabilityWithout() {
		return incSalesWithout+incSalvageWithout+incResidualWithout;
	}
	
	public double getCostInvestDonated(ProjectScenario scenario) {
		if (scenario==ProjectScenario.With) {
			return costInvestDonated;
		} else if (scenario==ProjectScenario.Without) {
			return costInvestDonatedWithout;
		} else {
			return costInvestDonated-costInvestDonatedWithout;
		}
	}
	
	public double getNetIncomeProfitabilityBefore(ProjectScenario scenario) {
		return getTotalIncomeProfitability(scenario) - getTotalCostsProfitability(scenario);
	}
	public double getNetIncomeProfitabilityAfter(ProjectScenario scenario) {
		if (scenario==ProjectScenario.With || scenario==ProjectScenario.Incremental) {
			return getNetIncomeProfitabilityBefore(scenario)+workingCapitalDonation+getCostInvestDonated(scenario);
		} else { // without
			return getNetIncomeProfitabilityBefore(scenario)+getCostInvestDonated(scenario);
		}		
	}

	public double getTotalCostsCashFlowNoFinance() {
		return costInvest+costReplace+costOperation-costOperationInternal+costGeneral-costGeneralOwn+costMaintenance;
	}
	public double getTotalCostsCashFlow() {
		return getTotalCostsCashFlowNoFinance()
		+ loan1capital+loan1interest+loan2capital+loan2interest+workingCapitalCapital+workingCapitalInterest;
	}
	public double getTotalCostsCashFlowWithout() {
		return costInvestWithout+costReplaceWithout+costOperationWithout-costOperationInternalWithout
				+costGeneralWithout-costGeneralWithoutOwn+costMaintenanceWithout;
	}
	
	public double getTotalCostsProfitability(ProjectScenario scenario) {
		if (scenario==ProjectScenario.With) {
			return getTotalCostsProfitabilityWith();
		} else if (scenario==ProjectScenario.Without) {
			return getTotalCostsProfitabilityWithout();
		} else {
			return getTotalCostsProfitabilityWith() - getTotalCostsProfitabilityWithout();
		}
	}
	public double getTotalCostsProfitabilityWith() {
		return costOperation+costReplace+costGeneral+costMaintenance+costInvest;
	}
	public double getTotalCostsProfitabilityWithout() {
		return costOperationWithout+costReplaceWithout+costGeneralWithout+costMaintenanceWithout+costInvestWithout;
	}
	
	public ProjectFinanceData(int year) {
		this.year=year;
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
	public double getWorkingCapitalReceived() {
		return workingCapitalReceived;
	}
	public void setWorkingCapitalReceived(double workingCapitalReceived) {
		this.workingCapitalReceived = workingCapitalReceived;
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
//	public double getCumulativeBeforeDonation() {
//		return cumulativeBeforeDonation;
//	}
//	public void setCumulativeBeforeDonation(double cumulativeBeforeDonation) {
//		this.cumulativeBeforeDonation = cumulativeBeforeDonation;
//	}
//	public double getCumulateiveAfterDonation() {
//		return cumulateiveAfterDonation;
//	}
//	public void setCumulateiveAfterDonation(double cumulateiveAfterDonation) {
//		this.cumulateiveAfterDonation = cumulateiveAfterDonation;
//	}
}