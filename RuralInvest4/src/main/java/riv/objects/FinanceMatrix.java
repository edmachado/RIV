package riv.objects;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.objects.project.Block;
import riv.objects.project.BlockIncome;
import riv.objects.project.BlockInput;
import riv.objects.project.BlockLabour;
import riv.objects.project.BlockWithout;
import riv.objects.project.Project;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemGeneral;
import riv.objects.project.ProjectItemGeneralPerYear;
import riv.objects.project.ProjectItemGeneralWithout;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemLabourWithout;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectItemPersonnelWithout;
import riv.objects.project.ProjectItemService;
import riv.objects.project.ProjectItemServiceWithout;

public class FinanceMatrix {
	static final Logger LOG = LoggerFactory.getLogger(FinanceMatrix.class);
	
	List<ProjectFinanceData> yearlyData;
	private List<Amortization> loan1;
	private List<Amortization> loan2;
	ProjectMonthsInYear[] firstYearData;
	double npvWithoutDonation;
	double npvWithDonation;
	double discountRate;
	int decimals;
	BigDecimal irrWithoutDonation;
	BigDecimal irrWithDonation;
	int wcPeriod;
	double wcPeriodAvg;
	double wcValue;
	
	public FinanceMatrix(Project project, double discountRate, int decimals) {
		this.discountRate = discountRate;
		this.decimals=decimals;
		analyzeProject(project);
		firstYearData =  ProjectMonthsInYear.getProjectPerMonths(project, false, decimals);
		addWorkingCapital(project);
		if ((project.getWizardStep()==null || project.getWizardStep()>11)
				&!(project.getLoan1Interest()==null||project.getLoan1GraceInterest()==null||project.getLoan1GraceCapital()==null||project.getLoan1Duration()==null
				||project.getLoan2Amt()==null||project.getLoan2Interest()==null||project.getLoan2GraceInterest()==null||project.getLoan2GraceCapital()==null||project.getLoan2Duration()==null)
		) {
			addLoanAmortization(project);
			npvWithoutDonation = getNpv(false, ProjectScenario.Incremental);
			irrWithoutDonation = getIrr(false, ProjectScenario.Incremental);
			npvWithDonation = getNpv(true, ProjectScenario.Incremental);
			irrWithDonation = getIrr(true, ProjectScenario.Incremental);
		}
	}
	
	protected void finalize() {
		yearlyData.clear();
	}
	
	public int getWcPeriod() {
		return wcPeriod;
	}
	public double getWcPeriodAvg() {
		return wcPeriodAvg;
	}
	public double getWcValue() {
		return wcValue;
	}
	
	public double getNpvWithoutDonation() {
		return npvWithoutDonation;
	}
	public double getNpvWithDonation() {
		return npvWithDonation;
	}
	public BigDecimal getIrrWithoutDonation() {
		return irrWithoutDonation;
	}
	public BigDecimal getIrrWithDonation() {
		return irrWithDonation;
	}
	
	public List<ProjectFinanceData> getYearlyData() {
		return yearlyData;
	}
	
	public ProjectMonthsInYear[] getFirstYearData() {
		return firstYearData;
	}
	
	public List<Amortization> getLoan1() {
		return loan1;
	}

	public List<Amortization> getLoan2() {
		return loan2;
	}

	public enum ProjectScenario {
		With(0), Without(1), Incremental(2);
		
		private final int value;
		private ProjectScenario (int value) {
			this.value=value;
		}
		
		public int getValue() {
			return value;
		}
	}
	
	public double[] getPayback(int duration, ProjectScenario scenario) {
		int lastNegYearBefore=0;
		double lastNegYearValueBefore=0.0;
		double cumulativeBefore=0.0;
		
		int lastNegYearAfter=0;
		double lastNegYearValueAfter=0.0;
		double cumulativeAfter=0.0;
		
		for (int i=0; i<duration; i++) {
			cumulativeBefore+=yearlyData.get(i).getNetIncomeProfitabilityBefore(scenario);
			if (cumulativeBefore<0) { 
				lastNegYearBefore=i; 
				lastNegYearValueBefore=cumulativeBefore;
			}
			
			cumulativeAfter+=yearlyData.get(i).getNetIncomeProfitabilityAfter(scenario);
			if (cumulativeAfter<0) { 
				lastNegYearAfter=i; 
				lastNegYearValueAfter=cumulativeAfter;
			}
		}
		
		int lastCumulativeYear = lastNegYearBefore+1<duration ? lastNegYearBefore+1 : duration-1;
		double paybackBefore = 
				yearlyData.get(lastCumulativeYear).getNetIncomeProfitabilityBefore(scenario)==0.0 
				? lastNegYearBefore+1
				: lastNegYearBefore+1 + (Math.abs(lastNegYearValueBefore)/yearlyData.get(lastCumulativeYear).getNetIncomeProfitabilityBefore(scenario));
		
		lastCumulativeYear = lastNegYearAfter+1<duration ? lastNegYearAfter+1 : duration-1;
		double paybackAfter = 
				yearlyData.get(lastCumulativeYear).getNetIncomeProfitabilityAfter(scenario)==0.0
				? lastNegYearAfter+1
				: lastNegYearAfter+1 + (Math.abs(lastNegYearValueAfter)/yearlyData.get(lastCumulativeYear).getNetIncomeProfitabilityAfter(scenario));
		
		return new double[]{ round(paybackBefore,2), round(paybackAfter,2) };
	}

	public double getNpv(boolean withDonation, ProjectScenario scenario) {
		return netPresentValue(discountRate/100, yearlyData, withDonation, scenario);
	}
	public BigDecimal getIrr(boolean withDonation, ProjectScenario scenario) {
		return internalRateOfReturn(discountRate/100, yearlyData, withDonation, scenario);
	}
	
	public List<double[]> getSummary(boolean cashFlow, ProjectScenario scenario) {
		List<double[]> summaries = new ArrayList<double[]>();
		double[] incomes = new double[yearlyData.size()];
		double[] costs = new double[yearlyData.size()];
		double[] donations = new double[yearlyData.size()];
		double[] totals = new double[yearlyData.size()];
		double[] cumulative = new double[yearlyData.size()];
		
		for (ProjectFinanceData data : yearlyData) {
			int year=data.getYear()-1;
			if (scenario==ProjectScenario.With) {
				incomes[year]=cashFlow ? data.getTotalIncomeCashFlow() : data.getTotalIncomeProfitabilityWith();
				costs[year]= cashFlow ? data.getTotalCostsCashFlow() : data.getTotalCostsProfitabilityWith();
			} else if (scenario==ProjectScenario.Without) {
				incomes[year]=cashFlow ? data.getTotalIncomeCashFlowWithout() : data.getTotalIncomeProfitabilityWithout();
				costs[year]= cashFlow ? data.getTotalCostsCashFlowWithout() : data.getTotalCostsProfitabilityWithout();
			} else { // incremental
				incomes[year]=cashFlow ? data.getTotalIncomeCashFlow()-data.getTotalIncomeCashFlowWithout() : data.getTotalIncomeProfitabilityWith()-data.getTotalIncomeProfitabilityWithout();
				costs[year]= cashFlow ? data.getTotalCostsCashFlow()-data.getTotalCostsCashFlowWithout() : data.getTotalCostsProfitabilityWith()-data.getTotalCostsProfitabilityWithout();
			}
			
			if (cashFlow) {
				totals[year]=incomes[year]-costs[year];
			} else {
				if (scenario==ProjectScenario.With) {
					donations[year]=data.getIncCapitalDonation()+data.costInvestDonated;
				} else if (scenario==ProjectScenario.With) {
					donations[year]=data.getIncCapitalDonation()+data.costInvestDonatedWithout;
				} else { // incremental
					donations[year]=data.getIncCapitalDonation()+data.costInvestDonated-data.costInvestDonatedWithout;
				}
				totals[year]=incomes[year]-costs[year]+donations[year];
			}
			cumulative[year]=year==0?totals[0]:totals[year]+cumulative[year-1];
		}
		
		summaries.add(incomes);
		summaries.add(costs);
		if (!cashFlow) {
			summaries.add(donations);
		}
		summaries.add(totals);
		summaries.add(cumulative);
		
		return summaries;
	}
	
	
	private void addWorkingCapital(Project project) {
		int lastNegMonth=0;
		Double highestNeg=0.0;
		Double cumulative=0.0;
		for (int year=0;year<project.getDuration();year++) {
			for (int month=0;month<12;month++) {
				cumulative+=firstYearData[year].getTotals()[month];
				if (cumulative<0) {
					lastNegMonth=(year*12)+month+1; // add one because i is 0-based
					if (cumulative<highestNeg) {					
						highestNeg=cumulative;
					}
				}
			}
		}
		
		// wc received in year 1 and paid when cumulative is positive
		wcPeriod = lastNegMonth==0 ? 0 : (lastNegMonth+2) > project.getDuration()*12 ? project.getDuration()*12 : lastNegMonth+2; // month of highest negative +2 
		int wcYear = ((Double)(Math.ceil(((double)wcPeriod)/12)-1)).intValue();
		if (wcYear<0) { wcYear=0; }
		wcValue = round(highestNeg*-1);
		
		// calculate average period
		cumulative=0.0; int i=0; 
		double[] factor2=new double[wcPeriod];
		double[] factor3=new double[wcPeriod];
		
		while (i<wcPeriod) {
			int year = i/12;
			int month = i-year*12;
			cumulative+=firstYearData[year].getTotals()[month];
			double factor=-1*cumulative/wcValue;
			
			if (i==0) {
				factor2[0] = cumulative<0 ? factor : 0;
			} else {
				factor2[i] = (cumulative>0 || factor<=factor2[i-1]) ? factor2[i-1] : factor;
			}

			factor3[i]=round((wcPeriod-i)*factor2[i],2);
			i++;
		}
		
		// take avg (could be integrated into above, but done separately to facilitate debugging
		double sum = 0.0;
		for (i=0;i<wcPeriod;i++) {
			sum+=factor3[i];
		}
		wcPeriodAvg = wcPeriod==0 ? wcPeriod : round(sum/wcPeriod,2);
	
		yearlyData.get(0).workingCapitalReceived=wcValue-project.getCapitalDonate()-project.getCapitalOwn();
		yearlyData.get(wcYear).workingCapitalCapital=yearlyData.get(0).workingCapitalReceived;
		if (wcPeriod>0) {
			yearlyData.get(wcYear).workingCapitalInterest=round(yearlyData.get(wcYear).workingCapitalCapital*wcPeriodAvg/12*((project.getCapitalInterest()-project.getInflationAnnual())*0.01));
		}
	}
	
	public class Amortization {
		private int year;
		private int period;
		private double capital;
		private double interest;
		
		public Amortization(int year, int period) {
			this.year=year; this.period=period;
		}
		
		public int getYear() { return year; }
		public int getPeriod() { return period; }
		
		
		
		public double getTotal() {
			return capital+interest;
		}

		public double getCapital() {
			return capital;
		}

		public void setCapital(double capital) {
			this.capital = capital;
		}

		public double getInterest() {
			return interest;
		}

		public void setInterest(double interest) {
			this.interest = interest;
		}
	}
	
	private void addLoanAmortization(Project project) {
		if (project.getLoan1PaymentsPerYear()==0) {
			return;
		}
		if (project.getLoan2PaymentsPerYear()==0 && project.getLoan2Amt()>0) {
			return;
		}
		
		double loan1amt = project.getInvestmentTotal()-project.getLoan2Amt();
		double loan1interest=(project.getLoan1Interest()-project.getInflationAnnual())/project.getLoan1PaymentsPerYear()*.01;
		double loan2interest=(project.getLoan2Interest()-project.getInflationAnnual())/project.getLoan2PaymentsPerYear()*.01;
		
		// LOAN 1
		loan1 = new ArrayList<Amortization>(project.getDuration()*project.getLoan1PaymentsPerYear());
		for (int i=0; i<project.getDuration(); i++) {
			for (int x=0; x<project.getLoan1PaymentsPerYear(); x++) {
				getLoan1().add(new Amortization(i+1, i*project.getLoan1PaymentsPerYear()+x+1));
			}
		}
		
		// calculate accumulation of interest during interest grace period
		// that is: increase amount of loan principle
		loan1amt = loan1amt*Math.pow(1+loan1interest,project.getLoan1GraceInterest()*project.getLoan1PaymentsPerYear());
		
		// calculate interest during capital grace period
		if (project.getLoan1GraceInterest()<project.getLoan1GraceCapital()) {
			for (int i=project.getLoan1GraceInterest()*project.getLoan1PaymentsPerYear();i<project.getLoan1GraceCapital()*project.getLoan1PaymentsPerYear();i++) {
				getLoan1().get(i).setInterest(loan1amt*loan1interest);
			}
		}
		
		// calculate interest and capital payments from first year after capital grace period ends
		for (int i=0;i<project.getLoan1Duration()*project.getLoan1PaymentsPerYear()-project.getLoan1GraceCapital()*project.getLoan1PaymentsPerYear();i++) {
			double[] payments = payments(loan1interest, (double)i+1, (double)project.getLoan1Duration()*project.getLoan1PaymentsPerYear()-project.getLoan1GraceCapital()*project.getLoan1PaymentsPerYear(), loan1amt, 0.0);
			int period=project.getLoan1GraceCapital()*project.getLoan1PaymentsPerYear()+i;
			getLoan1().get(period).setCapital(payments[0]);
			getLoan1().get(period).setInterest(payments[1]);
		}
		
		// aggregate by year
		for (int i=0; i<project.getDuration(); i++) {
			for (int x=0; x<project.getLoan1PaymentsPerYear(); x++) {
				yearlyData.get(i).loan1capital+=getLoan1().get(i*project.getLoan1PaymentsPerYear()+x).getCapital();
				yearlyData.get(i).loan1interest+=getLoan1().get(i*project.getLoan1PaymentsPerYear()+x).getInterest();
			}
		}
		
		// LOAN 2
		loan2 = new ArrayList<Amortization>(project.getDuration()*project.getLoan2PaymentsPerYear());
		for (int i=0; i<project.getDuration(); i++) {
			for (int x=0; x<project.getLoan2PaymentsPerYear(); x++) {
				getLoan2().add(new Amortization(i+1, i*project.getLoan2PaymentsPerYear()+x+1));
			}
		}
		
		// calculate accumulation of interest during interest grace period
		// that is: increase amount of loan principle
		double loan2amt = project.getLoan2Amt()*Math.pow(1+loan2interest,project.getLoan2GraceInterest()*project.getLoan2PaymentsPerYear());
		
		// calculate interest during capital grace period
		if (project.getLoan2GraceInterest()<project.getLoan2GraceCapital()) {
			for (int i=project.getLoan2GraceInterest()*project.getLoan2PaymentsPerYear();i<project.getLoan2GraceCapital()*project.getLoan2PaymentsPerYear();i++) {
				getLoan2().get(i+(project.getLoan2InitPeriod()-1)*project.getLoan2PaymentsPerYear())
					.setInterest(loan2amt*loan2interest);
			}
		}
		// calculate interest and capital payments from first year after capital grace period ends
		for (int i=0;i<project.getLoan2Duration()*project.getLoan2PaymentsPerYear()-project.getLoan2GraceCapital()*project.getLoan2PaymentsPerYear();i++) {
			int period = (project.getLoan2GraceCapital()-1)*project.getLoan2PaymentsPerYear()+i+project.getLoan2InitPeriod()*project.getLoan2PaymentsPerYear();
			if (period<=getLoan2().size()) { 
				double[] payments = payments(loan2interest, (double)i+1, (double)project.getLoan2Duration()*project.getLoan2PaymentsPerYear()-project.getLoan2GraceCapital()*project.getLoan2PaymentsPerYear(), loan2amt, 0.0);
				getLoan2().get(period).setCapital(payments[0]);
				getLoan2().get(period).setInterest(payments[1]);
			}
		}
		
		// aggregate by year
		for (int i=0; i<project.getDuration(); i++) {
			for (int x=0; x<project.getLoan2PaymentsPerYear(); x++) {
				yearlyData.get(i).loan2capital+=getLoan2().get(i*project.getLoan2PaymentsPerYear()+x).getCapital();
				yearlyData.get(i).loan2interest+=getLoan2().get(i*project.getLoan2PaymentsPerYear()+x).getInterest();
			}
		}
	}
	
	private void analyzeProject(Project project) {
		yearlyData = new ArrayList<ProjectFinanceData>(project.getDuration());
		for (int i=0;i<project.getDuration();i++) { yearlyData.add(new ProjectFinanceData(i+1)); }
		
		// INVESTMENT COSTS: ASSETS (WITH PROJECT)
		for (ProjectItemAsset asset : project.getAssets()) {
			// original purchase (once)
			yearlyData.get(asset.getYearBegin()-1).costInvest+=asset.getTotal();
			yearlyData.get(asset.getYearBegin()-1).costInvestOwn+=asset.getOwnResources();
			yearlyData.get(asset.getYearBegin()-1).costInvestDonated+=asset.getDonated();
			
			int lastAssetYear = asset.getReplace() ? project.getDuration() : asset.getYearBegin()-1+asset.getEconLife();
			if (lastAssetYear>project.getDuration()) lastAssetYear = project.getDuration();
			for (int i=asset.getYearBegin()-1; i<lastAssetYear; i++) {
				// maintenance (every year)
				yearlyData.get(i).costMaintenance+=asset.getMaintCost()*asset.getUnitNum();
				// replace and salvage (year after every expiry)
				if (asset.getReplace()&&i!=asset.getYearBegin()-1&&(i+1-asset.getYearBegin())%asset.getEconLife()==0) {
					yearlyData.get(i).costReplace+=asset.getUnitNum()*asset.getUnitCost();
					yearlyData.get(i).incSalvage+=asset.getSalvage()*asset.getUnitNum();
				// salvage value for non-replacing assets
				} else if (!asset.getReplace() && i==lastAssetYear-1) {
					yearlyData.get(i).incSalvage+=+asset.getSalvage()*asset.getUnitNum();
				}
			}
			yearlyData.get(yearlyData.size()-1).incResidual+=asset.getResidual();
		}
		
		// INVESTMENT COSTS: ASSETS (WITHOUT PROJECT)
		for (ProjectItemAssetWithout asset : project.getAssetsWithout()) {
			// original purchase (once)
			yearlyData.get(asset.getYearBegin()-1).costInvestWithout+=asset.getUnitCost()*asset.getUnitNum();
			yearlyData.get(asset.getYearBegin()-1).costInvestOwnWithout+=asset.getOwnResources();
			yearlyData.get(asset.getYearBegin()-1).costInvestDonatedWithout+=asset.getDonated();
			
			int lastAssetYear = asset.getReplace() ? project.getDuration() : asset.getYearBegin()-1+asset.getEconLife();
			if (lastAssetYear>project.getDuration()) lastAssetYear = project.getDuration();
			for (int i=asset.getYearBegin()-1; i<lastAssetYear; i++) {
				// maintenance (every year)
				yearlyData.get(i).costMaintenanceWithout+=asset.getMaintCost()*asset.getUnitNum();
				// replace and salvage (year after every expiry)
				if (asset.getReplace()&&i!=asset.getYearBegin()-1&&(i+1-asset.getYearBegin())%asset.getEconLife()==0) {
					yearlyData.get(i).costReplaceWithout+=asset.getUnitNum()*asset.getUnitCost();
					yearlyData.get(i).incSalvageWithout+=asset.getSalvage()*asset.getUnitNum();
				// salvage value for non-replacing assets
				} else if (!asset.getReplace() && i==lastAssetYear-1 && !(asset.getYearBegin()-1+asset.getEconLife()>project.getDuration())) {
					yearlyData.get(i).incSalvageWithout+=asset.getSalvage()*asset.getUnitNum();
				}
			}
			yearlyData.get(yearlyData.size()-1).incResidualWithout+=asset.getResidual();
		}
		
		
		// INVESTMENT COSTS: INPUTS (WITH PROJECT)
		for (ProjectItemLabour labour : project.getLabours()) {
			yearlyData.get(0).costInvest+=labour.getUnitCost()*labour.getUnitNum();
			yearlyData.get(0).costInvestOwn+=labour.getOwnResources();
			yearlyData.get(0).costInvestDonated+=labour.getDonated();
		}
		// INVESTMENT COSTS: INPUTS (WITHOUT PROJECT
		for (ProjectItemLabourWithout labour : project.getLaboursWithout()) {
			yearlyData.get(0).costInvestWithout+=labour.getUnitCost()*labour.getUnitNum();
			yearlyData.get(0).costInvestOwnWithout+=labour.getOwnResources();
			yearlyData.get(0).costInvestDonatedWithout+=labour.getDonated();
		}
		
		// INVESTMENT COSTS: SERVICES (WITH PROJECT)
		for (ProjectItemService service : project.getServices()) {
			yearlyData.get(0).costInvest+=service.getUnitCost()*service.getUnitNum();
			yearlyData.get(0).costInvestOwn+=service.getOwnResources();
			yearlyData.get(0).costInvestDonated+=service.getDonated();
		}
		// INVESTMENT COSTS: SERVICES (WITHOUT PROJECT)
		for (ProjectItemServiceWithout service : project.getServicesWithout()) {
			yearlyData.get(0).costInvestWithout+=service.getUnitCost()*service.getUnitNum();
			yearlyData.get(0).costInvestOwnWithout+=service.getOwnResources();
			yearlyData.get(0).costInvestDonatedWithout+=service.getDonated();
		}
			
		// GENERAL COSTS (WITH PROJECT)
		for (ProjectItemGeneral general : project.getGenerals()) {
			ProjectItemGeneralPerYear y;
			for (int i=0;i<project.getDuration();i++) {
				y = project.isPerYearGeneralCosts() ? general.getYears().get(i) : general.getYears().get(0);
				yearlyData.get(i).costGeneral+=y.getTotal();
				yearlyData.get(i).costGeneralOwn+=y.getOwnResources();
			}
		}
		for (ProjectItemPersonnel personnel : project.getPersonnels()) {
			ProjectItemGeneralPerYear y;
			for (int i=0;i<project.getDuration();i++) {
				y = project.isPerYearGeneralCosts() ? personnel.getYears().get(i) : personnel.getYears().get(0);
				yearlyData.get(i).costGeneral+=y.getTotal();
				yearlyData.get(i).costGeneralOwn+=y.getOwnResources();
			}
		}
		
		// GENERAL COSTS (WITHOUT PROJECT)
		for (ProjectItemGeneralWithout gwo : project.getGeneralWithouts()) {
			ProjectItemGeneralPerYear y;
			for (int i=0;i<project.getDuration();i++) {
				y = project.isPerYearGeneralCosts() ? gwo.getYears().get(i) : gwo.getYears().get(0);
				yearlyData.get(i).costGeneralWithout+=y.getTotal();
				yearlyData.get(i).costGeneralWithoutOwn+=y.getOwnResources();
			}
		}
		for (ProjectItemPersonnelWithout pwo : project.getPersonnelWithouts()) {
			ProjectItemGeneralPerYear y;
			for (int i=0;i<project.getDuration();i++) {
				y = project.isPerYearGeneralCosts() ? pwo.getYears().get(i) : pwo.getYears().get(0);
				yearlyData.get(i).costGeneralWithout+=y.getTotal();
				yearlyData.get(i).costGeneralWithoutOwn+=y.getOwnResources();
			}
		}
	
		for (BlockWithout block : project.getBlocksWithout()) {
			double cycles = block.getCyclePerYear();
			// BLOCK: INCOME
			for (BlockIncome income : block.getIncomes()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
//					if (i==0) { cycles = block.getCycleFirstYearIncome(); }
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					//double inc=(income.getUnitCost().doubleValue()-income.getTransport().doubleValue())*income.getUnitNum().doubleValue()*prodQty*cycles;
					//double incIntern= block.getProject().getIncomeGen()
					//		? (income.getUnitCost().doubleValue()-income.getTransport().doubleValue())*income.getQtyIntern().doubleValue()*prodQty*cycles
					//				: (income.getUnitCost().doubleValue())*income.getQtyIntern().doubleValue()*prodQty*cycles;
					yearlyData.get(i).incSalesWithout+=income.getTotal().multiply(new BigDecimal(prodQty*cycles)).doubleValue();
					yearlyData.get(i).incSalesInternalWithout+=(income.getTotal().subtract(income.getTotalCash())).multiply(new BigDecimal(prodQty*cycles)).doubleValue();
					
					// use # of cycles if ending calc for first year
					if (i==0) { cycles = block.getCyclePerYear(); }
				}
			}
			
			// BLOCK: INPUT
			for (BlockInput input : block.getInputs()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
//					if (i==0) { cycles = block.getCycleFirstYear(); }
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double opCost=input.getTotal().multiply(new BigDecimal(prodQty*cycles)).doubleValue();
					double opInternal = input.getTotal().subtract(input.getTotalCash()).multiply(new BigDecimal(prodQty*cycles)).doubleValue();
					yearlyData.get(i).costOperationWithout+=opCost;
					yearlyData.get(i).costOperationInternalWithout+=opInternal;
					
					yearlyData.get(i).incOperationDonationWithout+=input.getDonated();
					
					// use # of cycles if ending calc for first year
					if (i==0) { cycles = block.getCyclePerYear(); }
				}
			}
						
			// BLOCK: LABOUR
			for (BlockLabour labour : block.getLabours()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
//					if (i==0) cycles = block.getCycleFirstYear();
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double cost=labour.getTotal().doubleValue()*prodQty*cycles;
					double costInternal = (labour.getTotal().subtract(labour.getTotalCash())).doubleValue()*prodQty*cycles;
					// subtract block info if w/wo situation and block is without 
					yearlyData.get(i).costOperationWithout+=cost;
					yearlyData.get(i).costOperationInternalWithout+=costInternal;
					
					yearlyData.get(i).incOperationDonationWithout+=labour.getDonated();
					
					// use # of cycles if ending calc for first year
					if (i==0) cycles = block.getCyclePerYear();
				}
			} // close block:labour loop
		}
		
		for (Block block : project.getBlocks()) {
			double cycles = block.getCyclePerYear();
			// BLOCK: INCOME
			for (BlockIncome income : block.getIncomes()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
//					if (i==0) { cycles = block.getCycleFirstYearIncome(); }
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double inc=income.getTotal().doubleValue()*prodQty*cycles;
					double incIntern= (income.getTotal().doubleValue()-income.getTotalCash().doubleValue())*prodQty*cycles;
					yearlyData.get(i).incSales+=+inc;
					yearlyData.get(i).incSalesInternal+=incIntern;
					// use # of cycles if ending calc for first year
					if (i==0) { cycles = block.getCyclePerYear(); }
				}
			}
			
			// BLOCK: INPUT
			for (BlockInput input : block.getInputs()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
//					if (i==0) cycles = block.getCycleFirstYear();
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double opCost=input.getTotal().doubleValue()*prodQty*cycles;
					double opInternal = (input.getTotal().doubleValue()-input.getTotalCash().doubleValue())*prodQty*cycles;
					yearlyData.get(i).costOperation+=opCost;
					yearlyData.get(i).costOperationInternal+=opInternal;
					
					yearlyData.get(i).incOperationDonation+=input.getDonated();
					
					// use # of cycles if ending calc for first year
					if (i==0) cycles = block.getCyclePerYear();
				}
			}
			
			// BLOCK: LABOUR
			for (BlockLabour labour : block.getLabours()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
//					if (i==0) cycles = block.getCycleFirstYear();
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double cost=labour.getTotal().doubleValue()*prodQty*cycles;
					double costInternal = (labour.getTotal().doubleValue()-labour.getTotalCash().doubleValue())*prodQty*cycles;
					yearlyData.get(i).costOperation+=cost;
					yearlyData.get(i).costOperationInternal+=costInternal;

					yearlyData.get(i).incOperationDonation+=labour.getDonated();
					
					// use # of cycles if ending calc for first year
					if (i==0) cycles = block.getCyclePerYear();
				}
			}
		} // close block loop
		
		if (project.getWizardStep()==null||project.getWizardStep()>11) {
			// add donated and own working capital
			yearlyData.get(0).workingCapitalDonation=project.getCapitalDonate()==null?0.0:project.getCapitalDonate();
			yearlyData.get(0).workingCapitalOwn=project.getCapitalOwn()==null?0.0:project.getCapitalOwn();
			// add loans received
			yearlyData.get(project.getLoan2InitPeriod()-1).loanReceived=project.getLoan2Amt()==null?0.0:project.getLoan2Amt();
			yearlyData.get(0).loanReceived+=project.getLoan1Amt()==null?project.getInvestmentTotal():project.getLoan1Amt();
		}
	}

	private double[] getFlow(List<ProjectFinanceData> financeData, boolean includeDonation, ProjectScenario scenario) {
		double[] cashFlows = new double[financeData.size()+1];
		
		if (scenario==ProjectScenario.With) {
			cashFlows[0] = -1*financeData.get(0).getCostInvest();
		} else if (scenario==ProjectScenario.Without) {
			cashFlows[0] = -1*financeData.get(0).getCostInvestWithout();
		} else { // incremental
			cashFlows[0] = -1*(financeData.get(0).getCostInvest()-financeData.get(0).getCostInvestWithout());
		}
		
		
		double netIncome;
		for (ProjectFinanceData data : financeData) {
			if (scenario==ProjectScenario.With) {
				netIncome = data.getTotalIncomeProfitabilityWith()-data.getTotalCostsProfitabilityWith();
				cashFlows[data.getYear()] = includeDonation ? round(netIncome+data.getIncCapitalDonation()+data.getCostInvestDonated()) : round(netIncome);
			} else if (scenario==ProjectScenario.Without) {
				netIncome = data.getTotalIncomeProfitabilityWithout()-data.getTotalCostsProfitabilityWithout();
				cashFlows[data.getYear()] = includeDonation ? round(netIncome+data.getCostInvestDonatedWithout()) : round(netIncome);
			} else { // incremental
				netIncome = data.getTotalIncomeProfitabilityWith()-data.getTotalIncomeProfitabilityWithout()-(data.getTotalCostsProfitabilityWith()-data.getTotalCostsProfitabilityWithout());
				cashFlows[data.getYear()] = includeDonation ? round(netIncome+data.getIncCapitalDonation()+data.getCostInvestDonated()-data.getCostInvestDonatedWithout()) : round(netIncome);
			}
		}
		
		if (includeDonation) {
			double donation;
			if (scenario==ProjectScenario.With) {
				donation = round(financeData.get(0).getCostInvestDonated());
			} else if (scenario==ProjectScenario.Without) {
				donation = round(financeData.get(0).getCostInvestDonatedWithout());
			} else {
				donation = round(financeData.get(0).getCostInvestDonated()-financeData.get(0).getCostInvestDonatedWithout());
			}
			cashFlows[0] = cashFlows[0]+donation;
		}
		
		cashFlows[1]=cashFlows[1]-cashFlows[0];
		
		return cashFlows;
	}
	
	private double round(double d) {
		 BigDecimal bd = new BigDecimal(Double.toString(d));
		    bd = bd.setScale(decimals,BigDecimal.ROUND_HALF_UP);
		    return bd.doubleValue();
	 }
	private double round(double d, int scale) {
		 BigDecimal bd = new BigDecimal(Double.toString(d));
		    bd = bd.setScale(scale,BigDecimal.ROUND_HALF_UP);
		    return bd.doubleValue();
	}
	
	/**
	 * Wrapper for netPresentValue(double discountRate, double[] cashFlows).  Uses array of ProjectFinanceData to a cash flow array.
	 * @param discountRate The discount rate at which to calculate NPV
	 * @param financeData ProjectFinanceDatas from which the cash flows are extracted
	 * @param includeDonation whether or not to consider investment donations in calculation.  wc donation is not part of the calculation because wc donation should already be excluded or not (specified as a parameter in analyzeProject).
	 * @return the calculated Net Present Value
	 */
	private double netPresentValue (double discountRate, List<ProjectFinanceData> financeData, boolean includeDonation, ProjectScenario scenario)	{
		double[] cashFlowsIrr = getFlow(financeData, includeDonation, scenario);
		// leave off first flow (investment 1st year + donation)
		double[] cashFlowsNpv = new double[cashFlowsIrr.length-1];
		for (int i=0;i<cashFlowsNpv.length;i++) {
			cashFlowsNpv[i]=cashFlowsIrr[i+1];
		}
		double npv = netPresentValue(discountRate, cashFlowsNpv);
	   	return npv+cashFlowsIrr[0];	
	}
	
	/**
	 * Calculates Net Present Value based on an array of cash flows.
	 * @param discountRate the discount rate at which to calculate NPV
	 * @param cashFlows the cash flows used to calculate NPV
	 * @return the Net Present Value
	 */
	private double netPresentValue (double discountRate, double[] cashFlows)	{	   	 
		double  npv = 0.0;
	   	for (int i=0; i<cashFlows.length; i++ ) {
	   		npv += cashFlows[i] / Math.pow ( 1.0 + discountRate, i+1 );
	   	}
	   	return npv;
    }
     
	/**
	 * Wrapper for internalRateOfReturn(double irrEstimate, double[] cashFlows).  Uses ArrayList of ProjectFinanceData to extract cash flows
	 * @param irrEstimate estimated IRR
	 * @param financeData used to extract cash flows
	 * @return calculated IRR
	 */
	private BigDecimal  internalRateOfReturn (double irrEstimate, List<ProjectFinanceData> financeData, boolean includeDonation, ProjectScenario scenario) {
		double[] cashFlows = getFlow(financeData, includeDonation, scenario);
	   	return internalRateOfReturn(irrEstimate, cashFlows);
	 }
	 
    /**
     * Calculated the Internal Rate of Return for a set of cash flows based on a guess IRR.
     * @param irrEstimate estimated IRR
     * @param cashFlows cash flows to calculate IRR
     * @return calculated IRR
     */
    private  BigDecimal  internalRateOfReturn(double irrEstimate, double[] cashFlows) {
    	 double  irr = irrEstimate==0 ? 0.001 : irrEstimate;

    	 double  delta = -irr * 0.1;
    	 double  oldNpv = 0.0;

       while (true) {
    	   if (Double.isInfinite(irr) || Double.isNaN(irr)) {
    		   return new BigDecimal(1001.0);
    	   }
    	   
    	   double  npv = netPresentValue ( irr, cashFlows );

    	   if ( npv == 0.0 ) {				return new BigDecimal(irr); }
    	   if (oldNpv < 0.0) {
    		   if (npv > 0.0) {				delta *= -0.9; 
    	   		} else if (npv > oldNpv) {		delta *= 1.1;
    	   		} else if (npv < oldNpv) {		delta = -delta;
    	   		} else {							delta = 0.0; }
    	   } else if (oldNpv > 0.0) {
    		   if (npv < 0.0) {				delta *= -0.9;
    		   } else if (npv < oldNpv)	{	delta *= 1.1;
    		   } else if (npv > oldNpv)	{	delta = -delta;
    		   } else {							delta = 0.0; }
    	   }

    	   if (delta == 0.0) {	return new BigDecimal(irr); }
    	   irr += delta;
    	   oldNpv = npv;
       }
	}    
    
    /**
     * Calculates the interest and principal payments on a loan for a particular payment period
     * @param rate the interest rate at which the loan is charged
     * @param per period for which payments are being calculated
     * @param nper the number of payment periods
     * @param pv the loan amount
     * @param fv desired value of the loan at the end of the period. Should be 0 if the loan is to be paid in full
     * @return array containing two doubles.  The first element is the principal payment and the second is the interest payment
     */
    private double[] payments(double rate, double per, double nper, double pv, double fv) {
    	double[] pays= new double[2];
    	double totalPayment = getPay(rate, nper, pv, fv, 0.0);
    	double interest = (-getPrinc(pv, totalPayment, rate, per - 1.0)) * rate;
    	double principal = totalPayment-interest;
    	pays[0]=-principal;
    	pays[1]=-interest;
    	return pays;
    }
            
    private double getPay(double rate, double nper, double pv, double fv, double type) {
    	if (rate==0) return -pv/nper;
    	double pvif, fvifa;
    	pvif = Math.pow(1 + rate, nper);
    	fvifa = (Math.pow(1 + rate, nper) - 1.0) / rate;
    	return ((-pv * pvif - fv) / ((1.0 + rate * type) * fvifa));
    }
    
    private double getPrinc(double start, double pay, double rate, double period) {
    	if (rate==0) { return pay; }
    	return (start * Math.pow(1.0 + rate, period) +
    		pay * ((Math.pow(1 + rate, period) - 1) / rate));
    }
}
