package riv.objects.project;

import java.math.BigDecimal;
import java.util.ArrayList;

import riv.util.Calculator;

/**
 * Contains aggregated financial data for a project, organized per year.  
 * Typically an array of ProjectFinanceData, one per year of a project, is used to calculate
 * figures such as Internal Rate of Return and Net Present Value
 * @author Bar Zecharya
 *
 */
public class ProjectFinanceData implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private AnalysisType analType;
	private int year;
	private double incSales;
	private double incSalesWithout;
	private double incSalesInternal;
	private double incSalesInternalWithout;
	private double incSalvage;
	private double incSalvageWithout;
	private double incResidual;
	private double incResidualWithout;
	private double costOperation;
	private double costOperationWithout;
	private double costOperationInternal;
	private double costOperationInternalWithout;
	private double costReplace;
	private double costReplaceWithout;
	private double costGeneral;
	private double costGeneralOwn;
	private double costGeneralWithout;
	private double costGeneralWithoutOwn;
	private double costMaintenance;
	private double costMaintenanceWithout;
	private double costInvest;
	private double costInvestOwn;
	private double costInvestDonated;
	private double costInvestWithout;
	private double costInvestOwnWithout;
	private double costInvestDonatedWithout;
	private double cumulative;
	private double workingCapitalCapital;
	private double workingCapitalInterest;
	private double loan1interest;
	private double loan1capital;
	private double loan2interest;
	private double loan2capital;
	private double workingCapitalDonation;
	private double workingCapitalOwn;
	private double loanReceived;
	
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
	
	public double getTotalIncome() {
		if (analType==AnalysisType.CashFlow) {
			return incSales-incSalesInternal+incSalvage+workingCapitalDonation;
		} else { 
			return incSales-incSalesWithout+incSalvage-incSalvageWithout+incResidual-incResidualWithout;
		}
	}
	
	public double getTotalCosts() {
		if (analType==AnalysisType.CashFlow) {
			return costOperation-costOperationInternal+costReplace+costGeneral-costGeneralOwn+costMaintenance;
		} else { 
			return costOperation-costOperationWithout+costReplace-costReplaceWithout+costGeneral-costGeneralWithout+costMaintenance-costMaintenanceWithout+costInvest-costInvestWithout;
		}
	}
	
	public double getNetIncome() {
		return getTotalIncome()-getTotalCosts();
	}
	public double getNetIncomeAfterDonation() {
		return getNetIncome()+workingCapitalDonation+costInvestDonated;
	}
	
	public double getProfitAfterFinance() {
		return getNetIncome()-workingCapitalInterest-loan1interest-loan1capital-loan2interest-loan2capital;
	}
	
	public ProjectFinanceData(int year, AnalysisType analType) {
		this.year=year;
		this.analType=analType;
	}
	
	// property accessors
	public void setYear(int year) {
		this.year=year;
	}
	public int getYear() {
		return year;
	}
	public void setIncSales(double incSales) {
		this.incSales = incSales;
	}
	public double getIncSales() {
		return incSales;
	}
	public void setIncSalesWithout(double incSales) {
		this.incSalesWithout = incSales;
	}
	public double getIncSalesWithout() {
		return incSalesWithout;
	}
	public void setIncSalesInternal(double incSalesInternal) {
		this.incSalesInternal = incSalesInternal;
	}
	public double getIncSalesInternal() {
		return incSalesInternal;
	}
	public void setIncSalesInternalWithout(double incSalesInternal) {
		this.incSalesInternalWithout = incSalesInternal;
	}
	public double getIncSalesInternalWithout() {
		return incSalesInternalWithout;
	}
	public void setIncSalvage(double incSalvage) {
		this.incSalvage = incSalvage;
	}
	public double getIncSalvage() {
		return incSalvage;
	}
	public double getIncSalvageWithout() {
		return incSalvageWithout;
	}

	public void setIncSalvageWithout(double incSalvageWithout) {
		this.incSalvageWithout = incSalvageWithout;
	}

	public void setIncResidual(double incResidual) {
		this.incResidual = incResidual;
	}
	public double getIncResidual() {
		return incResidual;
	}
	public double getIncResidualWithout() {
		return incResidualWithout;
	}

	public void setIncResidualWithout(double incResidualWithout) {
		this.incResidualWithout = incResidualWithout;
	}

	public void setIncCapitalDonation(double incCapitalDonation) {
		this.workingCapitalDonation = incCapitalDonation;
	}
	public double getIncCapitalDonation() {
		return workingCapitalDonation;
	}
	public void setIncCapitalOwn(double incCapitalOwn) {
		this.workingCapitalOwn = incCapitalOwn;
	}
	public double getIncCapitalOwn() {
		return workingCapitalOwn;
	}
	
	public void setCostOperation(double costOperation) {
		this.costOperation = costOperation;
	}
	public double getCostOperation() {
		return costOperation;
	}
	public void setCostOperationWithout(double costOperation) {
		this.costOperationWithout = costOperation;
	}
	public double getCostOperationWithout() {
		return costOperationWithout;
	}
	public void setCostOperationInternal(double costOperationInternal) {
		this.costOperationInternal = costOperationInternal;
	}
	public double getCostOperationInternal() {
		return costOperationInternal;
	}
	public void setCostOperationInternalWithout(double costOperationInternal) {
		this.costOperationInternalWithout = costOperationInternal;
	}
	public double getCostOperationInternalWithout() {
		return costOperationInternalWithout;
	}
	public void setCostReplace(double costReplace) {
		this.costReplace = costReplace;
	}
	public double getCostReplace() {
		return costReplace;
	}
	public double getCostReplaceWithout() {
		return costReplaceWithout;
	}

	public void setCostReplaceWithout(double costReplaceWithout) {
		this.costReplaceWithout = costReplaceWithout;
	}

	public void setCostGeneral(double costGeneral) {
		this.costGeneral = costGeneral;
	}
	public double getCostGeneral() {
		return costGeneral;
	}
	public double getCostGeneralOwn() {
		return costGeneralOwn;
	}
	public void setCostGeneralOwn(double cgo) {
		this.costGeneralOwn=cgo;
	}
	public void setCostGeneralWithout(double costGeneralWithout) {
		this.costGeneralWithout = costGeneralWithout;
	}
	public double getCostGeneralWithout() {
		return costGeneralWithout;
	}
	public void setCostGeneralWithoutOwn(double cgwo) {
		this.costGeneralWithoutOwn = cgwo;
	}
	public double getCostGeneralWithoutOwn() {
		return costGeneralWithoutOwn;
	}
	public void setCostMaintenance(double costMaintenance) {
		this.costMaintenance = costMaintenance;
	}
	public double getCostMaintenance() {
		return costMaintenance;
	}
	public double getCostMaintenanceWithout() {
		return costMaintenanceWithout;
	}

	public void setCostMaintenanceWithout(double costMaintenanceWithout) {
		this.costMaintenanceWithout = costMaintenanceWithout;
	}

	public void setCostInvest(double costInvest) {
		this.costInvest = costInvest;
	}
	public double getCostInvest() {
		return costInvest;
	}
	
	public void setCostInvestOwn(double costInvestOwn) {
		this.costInvestOwn = costInvestOwn;
	}
	public double getCostInvestOwn() {
		return costInvestOwn;
	}
	public void setCostInvestDonated(double costInvestDonated) {
		this.costInvestDonated = costInvestDonated;
	}
	public double getCostInvestDonated() {
		return costInvestDonated;
	}
	public double getCostInvestWithout() {
		return costInvestWithout;
	}

	public void setCostInvestWithout(double costInvestWithout) {
		this.costInvestWithout = costInvestWithout;
	}

	private double getCostInvestOwnWithout() {
		return costInvestOwnWithout;
	}

	private void setCostInvestOwnWithout(double costInvestOwnWithout) {
		this.costInvestOwnWithout = costInvestOwnWithout;
	}

	public double getCostInvestDonatedWithout() {
		return costInvestDonatedWithout;
	}

	public void setCostInvestDonatedWithout(double costInvestDonatedWithout) {
		this.costInvestDonatedWithout = costInvestDonatedWithout;
	}

	public void setCumulative(double cumulative) {
		this.cumulative = cumulative;
	}
	public double getCumulative() {
		return cumulative;
	}
	public void setWorkingCapitalCapital(double wcCapital) {
		this.workingCapitalCapital=wcCapital;
	}
	public double getWorkingCapitalCapital() {
		return workingCapitalCapital;
	}
	public void setWorkingCapital(double workingCapital) {
		this.workingCapitalInterest = workingCapital;
	}
	public double getWorkingCapital() {
		return workingCapitalInterest;
	}
	public void setLoan1interest(double loan1interest) {
		this.loan1interest = loan1interest;
	}
	public double getLoan1interest() {
		return loan1interest;
	}
	public void setLoan1capital(double loan1capital) {
		this.loan1capital = loan1capital;
	}
	public double getLoan1capital() {
		return loan1capital;
	}
	public void setLoan2interest(double loan2interest) {
		this.loan2interest = loan2interest;
	}
	public double getLoan2interest() {
		return loan2interest;
	}
	public void setLoan2capital(double loan2capital) {
		this.loan2capital = loan2capital;
	}
	public double getLoan2capital() {
		return loan2capital;
	}
	
	public double getLoanReceived() {
		return loanReceived;
	}
	public void setLoanReceived(double loanReceived) {
		this.loanReceived = loanReceived;
	}

	public enum AnalysisType {
		CashFlow, Incremental// TotalCosts//, ProducerCosts
	}
	
	/**
	 * Analyses a Project and aggregates its financial data into one ProjectFinanceData object per year
	 * of the project.
	 * @param project project to be analysed
	 * @param incremental true if 
	 * @param ownResourcesAsCost if true general cost includes own resources
	 * @return an array of ProjectFinanceData objects, one per year of the project
	 */
	public static ArrayList<ProjectFinanceData> analyzeProject(Project project, AnalysisType analType) {
		ArrayList<ProjectFinanceData> data = new ArrayList<ProjectFinanceData>();
		for (int i=0;i<project.getDuration();i++) { data.add(new ProjectFinanceData(i+1, analType)); }
		
		// INVESTMENT COSTS: ASSETS (WITH PROJECT)
		for (ProjectItemAsset asset : project.getAssets()) {
			// original purchase (once)
			data.get(asset.getYearBegin()-1).setCostInvest(data.get(asset.getYearBegin()-1).getCostInvest()+asset.getTotal());
			data.get(asset.getYearBegin()-1).setCostInvestOwn(data.get(asset.getYearBegin()-1).getCostInvestOwn()+asset.getOwnResources());
			data.get(asset.getYearBegin()-1).setCostInvestDonated(data.get(asset.getYearBegin()-1).getCostInvestDonated()+asset.getDonated());
			
			int lastAssetYear = asset.getReplace() ? project.getDuration() : asset.getYearBegin()-1+asset.getEconLife();
			if (lastAssetYear>project.getDuration()) lastAssetYear = project.getDuration();
			for (int i=asset.getYearBegin()-1; i<lastAssetYear; i++) {
				// maintenance (every year)
				data.get(i).setCostMaintenance(data.get(i).getCostMaintenance()+(asset.getMaintCost()*asset.getUnitNum()));
				// replace and salvage (year after every expiry)
				if (asset.getReplace()&&i!=asset.getYearBegin()-1&&(i+1-asset.getYearBegin())%asset.getEconLife()==0) {
					data.get(i).setCostReplace(data.get(i).getCostReplace()+asset.getUnitNum()*asset.getUnitCost());
					data.get(i).setIncSalvage(data.get(i).getIncSalvage()+asset.getSalvage()*asset.getUnitNum());
				// salvage value for non-replacing assets
				} else if (!asset.getReplace() && i==lastAssetYear-1 && !(asset.getYearBegin()-1+asset.getEconLife()>project.getDuration())) {
					data.get(i).setIncSalvage(data.get(i).getIncSalvage()+asset.getSalvage()*asset.getUnitNum());
				}
			}
			data.get(data.size()-1).setIncResidual(data.get(data.size()-1).getIncResidual()+asset.getResidual());
		}
		
		// INVESTMENT COSTS: ASSETS (WITHOUT PROJECT)
		for (ProjectItemAssetWithout asset : project.getAssetsWithout()) {
			// original purchase (once)
			data.get(asset.getYearBegin()-1).setCostInvestWithout(data.get(asset.getYearBegin()-1).getCostInvestWithout()+asset.getUnitCost()*asset.getUnitNum());
			data.get(asset.getYearBegin()-1).setCostInvestOwnWithout(data.get(asset.getYearBegin()-1).getCostInvestOwnWithout()+asset.getOwnResources());
			data.get(asset.getYearBegin()-1).setCostInvestDonatedWithout(data.get(asset.getYearBegin()-1).getCostInvestDonatedWithout()+asset.getDonated());
			
			int lastAssetYear = asset.getReplace() ? project.getDuration() : asset.getYearBegin()-1+asset.getEconLife();
			if (lastAssetYear>project.getDuration()) lastAssetYear = project.getDuration();
			for (int i=asset.getYearBegin()-1; i<lastAssetYear; i++) {
				// maintenance (every year)
				data.get(i).setCostMaintenanceWithout(data.get(i).getCostMaintenanceWithout()+(asset.getMaintCost()*asset.getUnitNum()));
				// replace and salvage (year after every expiry)
				if (asset.getReplace()&&i!=asset.getYearBegin()-1&&(i+1-asset.getYearBegin())%asset.getEconLife()==0) {
					data.get(i).setCostReplaceWithout(data.get(i).getCostReplaceWithout()+asset.getUnitNum()*asset.getUnitCost());
					data.get(i).setIncSalvageWithout(data.get(i).getIncSalvageWithout()+asset.getSalvage()*asset.getUnitNum());
				// salvage value for non-replacing assets
				} else if (!asset.getReplace() && i==lastAssetYear-1 && !(asset.getYearBegin()-1+asset.getEconLife()>project.getDuration())) {
					data.get(i).setIncSalvageWithout(data.get(i).getIncSalvageWithout()+asset.getSalvage()*asset.getUnitNum());
				}
			}
			data.get(data.size()-1).setIncResidualWithout(data.get(data.size()-1).getIncResidualWithout()+asset.getResidual());
		}
		
		
		// INVESTMENT COSTS: INPUTS (WITH PROJECT)
		for (ProjectItemLabour labour : project.getLabours()) {
			data.get(0).setCostInvest(data.get(0).getCostInvest()+labour.getUnitCost()*labour.getUnitNum());
			data.get(0).setCostInvestOwn(data.get(0).getCostInvestOwn()+labour.getOwnResources());
			data.get(0).setCostInvestDonated(data.get(0).getCostInvestDonated()+labour.getDonated());
		}
		// INVESTMENT COSTS: INPUTS (WITHOUT PROJECT
		for (ProjectItemLabourWithout labour : project.getLaboursWithout()) {
			data.get(0).setCostInvestWithout(data.get(0).getCostInvestWithout()+labour.getUnitCost()*labour.getUnitNum());
			data.get(0).setCostInvestOwnWithout(data.get(0).getCostInvestOwnWithout()+labour.getOwnResources());
			data.get(0).setCostInvestDonatedWithout(data.get(0).getCostInvestDonatedWithout()+labour.getDonated());
		}
		
		// INVESTMENT COSTS: SERVICES (WITH PROJECT)
		for (ProjectItemService service : project.getServices()) {
			data.get(0).setCostInvest(data.get(0).getCostInvest()+service.getUnitCost()*service.getUnitNum());
			data.get(0).setCostInvestOwn(data.get(0).getCostInvestOwn()+service.getOwnResources());
			data.get(0).setCostInvestDonated(data.get(0).getCostInvestDonated()+service.getDonated());
		}
		// INVESTMENT COSTS: SERVICES (WITHOUT PROJECT)
		for (ProjectItemServiceWithout service : project.getServicesWithout()) {
			data.get(0).setCostInvestWithout(data.get(0).getCostInvestWithout()+service.getUnitCost()*service.getUnitNum());
			data.get(0).setCostInvestOwnWithout(data.get(0).getCostInvestOwnWithout()+service.getOwnResources());
			data.get(0).setCostInvestDonatedWithout(data.get(0).getCostInvestDonatedWithout()+service.getDonated());
		}
			
		// GENERAL COSTS (WITH PROJECT)
		for (ProjectItemGeneral general : project.getGenerals()) {
			for (int i=0;i<project.getDuration();i++) {
				data.get(i).setCostGeneral(data.get(i).getCostGeneral()+general.getTotal());
				data.get(i).setCostGeneralOwn(data.get(i).getCostGeneralOwn()+general.getOwnResources());
			}
		}
		for (ProjectItemPersonnel personnel : project.getPersonnels()) {
			for (int i=0;i<project.getDuration();i++) {
				data.get(i).setCostGeneral(data.get(i).getCostGeneral()+personnel.getTotal());
				data.get(i).setCostGeneralOwn(data.get(i).getCostGeneralOwn()+personnel.getOwnResources());
			}
		}
		
		// GENERAL COSTS (WITHOUT PROJECT)
		for (ProjectItemGeneralWithout gwo : project.getGeneralWithouts()) {
			for (int i=0;i<project.getDuration();i++) {
				data.get(i).setCostGeneralWithout(data.get(i).getCostGeneralWithout()+gwo.getTotal());
				data.get(i).setCostGeneralWithoutOwn(data.get(i).getCostGeneralWithoutOwn()+gwo.getOwnResources());
			}
		}
		for (ProjectItemPersonnelWithout pwo : project.getPersonnelWithouts()) {
			for (int i=0;i<project.getDuration();i++) {
				data.get(i).setCostGeneralWithout(data.get(i).getCostGeneralWithout()+pwo.getTotal());
				data.get(i).setCostGeneralWithoutOwn(data.get(i).getCostGeneralWithoutOwn()+pwo.getOwnResources());
			}
		}
	
		for (BlockWithout block : project.getBlocksWithout()) {
			int cycles = block.getCyclePerYear();
			// BLOCK: INCOME
			for (BlockIncome income : block.getIncomes()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
					if (i==0) { cycles = block.getCycleFirstYearIncome(); }
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					//double inc=(income.getUnitCost().doubleValue()-income.getTransport().doubleValue())*income.getUnitNum().doubleValue()*prodQty*cycles;
					//double incIntern= block.getProject().getIncomeGen()
					//		? (income.getUnitCost().doubleValue()-income.getTransport().doubleValue())*income.getQtyIntern().doubleValue()*prodQty*cycles
					//				: (income.getUnitCost().doubleValue())*income.getQtyIntern().doubleValue()*prodQty*cycles;
					data.get(i).setIncSalesWithout(data.get(i).getIncSalesWithout()+income.getTotal().multiply(new BigDecimal(prodQty*cycles)).doubleValue());
					data.get(i).setIncSalesInternalWithout(data.get(i).getIncSalesInternalWithout()+(income.getTotal().subtract(income.getTotalCash())).multiply(new BigDecimal(prodQty*cycles)).doubleValue());
					
					// use # of cycles if ending calc for first year
					if (i==0) { cycles = block.getCyclePerYear(); }
				}
			}
			
			// BLOCK: INPUT
			for (BlockInput input : block.getInputs()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
					if (i==0) { cycles = block.getCycleFirstYear(); }
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double opCost=input.getTotal().multiply(new BigDecimal(prodQty*cycles)).doubleValue();
					double opInternal = input.getTotal().subtract(input.getTotalCash()).multiply(new BigDecimal(prodQty*cycles)).doubleValue();
					data.get(i).setCostOperationWithout(data.get(i).getCostOperationWithout()+opCost);
					data.get(i).setCostOperationInternalWithout(data.get(i).getCostOperationInternalWithout()+opInternal);
					
					// use # of cycles if ending calc for first year
					if (i==0) { cycles = block.getCyclePerYear(); }
				}
			}
						
			// BLOCK: LABOUR
			for (BlockLabour labour : block.getLabours()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
					if (i==0) cycles = block.getCycleFirstYear();
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double cost=labour.getTotal().doubleValue()*prodQty*cycles;
					double costInternal = (labour.getTotal().subtract(labour.getTotalCash())).doubleValue()*prodQty*cycles;
					// subtract block info if w/wo situation and block is without 
					data.get(i).setCostOperationWithout(data.get(i).getCostOperationWithout()+cost);
					data.get(i).setCostOperationInternalWithout(data.get(i).getCostOperationInternalWithout()+costInternal);
					// use # of cycles if ending calc for first year
					if (i==0) cycles = block.getCyclePerYear();
				}
			} // close block:labour loop
		}
		
		for (Block block : project.getBlocks()) {
			int cycles = block.getCyclePerYear();
			// BLOCK: INCOME
			for (BlockIncome income : block.getIncomes()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
					if (i==0) { cycles = block.getCycleFirstYearIncome(); }
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double inc=income.getTotal().doubleValue()*prodQty*cycles;
					double incIntern= (income.getTotal().doubleValue()-income.getTotalCash().doubleValue())*prodQty*cycles;
					data.get(i).setIncSales(data.get(i).getIncSales()+inc);
					data.get(i).setIncSalesInternal(data.get(i).getIncSalesInternal()+incIntern);
					// use # of cycles if ending calc for first year
					if (i==0) { cycles = block.getCyclePerYear(); }
				}
			}
			
			// BLOCK: INPUT
			for (BlockInput input : block.getInputs()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
					if (i==0) cycles = block.getCycleFirstYear();
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double opCost=input.getTotal().doubleValue()*prodQty*cycles;
					double opInternal = (input.getTotal().doubleValue()-input.getTotalCash().doubleValue())*prodQty*cycles;
					data.get(i).setCostOperation(data.get(i).getCostOperation()+opCost);
					data.get(i).setCostOperationInternal(data.get(i).getCostOperationInternal()+opInternal);
					// use # of cycles if ending calc for first year
					if (i==0) cycles = block.getCyclePerYear();
				}
			}
			
			// BLOCK: LABOUR
			for (BlockLabour labour : block.getLabours()) {
				for (int i=0;i<project.getDuration();i++) {
					// use # of cycles for first year if calculating first year
					if (i==0) cycles = block.getCycleFirstYear();
					
					double prodQty = block.getPatterns().get(i+1).getQty();
					double cost=labour.getTotal().doubleValue()*prodQty*cycles;
					double costInternal = (labour.getTotal().doubleValue()-labour.getTotalCash().doubleValue())*prodQty*cycles;
					data.get(i).setCostOperation(data.get(i).getCostOperation()+cost);
					data.get(i).setCostOperationInternal(data.get(i).getCostOperationInternal()+costInternal);
					// use # of cycles if ending calc for first year
					if (i==0) cycles = block.getCyclePerYear();
				}
			}
		} // close block loop
		
		// add donated and own working capital
		data.get(0).setIncCapitalDonation(project.getCapitalDonate());
		data.get(0).setIncCapitalOwn(project.getCapitalOwn());
		// add loans received
		data.get(project.getLoan2InitPeriod()-1).setLoanReceived(project.getLoan2Amt());
		data.get(0).setLoanReceived(data.get(0).getLoanReceived()+project.getLoan1Amt());
		
		return data;
	}
	
	public static void AddLoanAmortization(Project project, ArrayList<ProjectFinanceData> data) {
		double loan1amt = project.getInvestmentTotal()-project.getLoan2Amt();
		double loan1interest=(project.getLoan1Interest()-project.getInflationAnnual())*.01;
		double loan2interest=(project.getLoan2Interest()-project.getInflationAnnual())*.01;
		// LOAN 1
		// calculate accumulation of interest during interest grace period
		// that is: increase amount of loan principle
		loan1amt = loan1amt*Math.pow(1+loan1interest,project.getLoan1GraceInterest());
		
		// calculate interest during capital grace period
		if (project.getLoan1GraceInterest()<project.getLoan1GraceCapital()) {
			for (int i=project.getLoan1GraceInterest();i<project.getLoan1GraceCapital();i++) {
				data.get(i).setLoan1interest(loan1amt*loan1interest);
			}
		}
		// calculate interest and capital payments from first year after capital grace period ends
		for (int i=0;i<project.getLoan1Duration()-project.getLoan1GraceCapital();i++) {
			double[] payments = Calculator.payments(loan1interest, (double)i+1, (double)project.getLoan1Duration()-project.getLoan1GraceCapital(), loan1amt, 0.0);
			int year=project.getLoan1GraceCapital()+i;
			data.get(year).setLoan1capital(payments[0]);
			data.get(year).setLoan1interest(payments[1]);
		}
		// LOAN 2
		// calculate accumulation of interest during interest grace period
		// that is: increase amount of loan principle
		double loan2amt = project.getLoan2Amt()*Math.pow(1+loan2interest,project.getLoan2GraceInterest());
		
		// calculate interest during capital grace period
		if (project.getLoan2GraceInterest()<project.getLoan2GraceCapital()) {
			for (int i=project.getLoan2GraceInterest();i<project.getLoan2GraceCapital();i++) {
				int year = i+project.getLoan2InitPeriod()-1;
				data.get(year).setLoan2interest(loan2amt*loan2interest);
			}
		}
		// calculate interest and capital payments from first year after capital grace period ends
		for (int i=0;i<project.getLoan2Duration()-project.getLoan2GraceCapital();i++) {
			int year = project.getLoan2GraceCapital()+i-1+project.getLoan2InitPeriod();
			double[] payments = Calculator.payments(loan2interest, (double)i+1, (double)project.getLoan2Duration()-project.getLoan2GraceCapital(), loan2amt, 0.0);
			data.get(year).setLoan2capital(payments[0]);
			data.get(year).setLoan2interest(payments[1]);
		}
	}
	
	
	public static void AddWorkingCapital(Project project, ArrayList<ProjectFinanceData> data) {
		// year 1
		ProjectFirstYear pfy = new ProjectFirstYear(project);
		double[] pfyResults = ProjectFirstYear.WcAnalysis(pfy);
		double wcFinanced = -1*pfyResults[1] -project.getCapitalDonate()-project.getCapitalOwn();
		data.get(0).setWorkingCapitalCapital(wcFinanced);
		double wc = wcFinanced*pfyResults[0]/12*(project.getCapitalInterest()*0.01);
		data.get(0).setWorkingCapital(wc); 
		
		// year 2
		double remainingNegative = pfy.getCumulative()[11];
		if (remainingNegative<0) {
			data.get(1).setWorkingCapitalCapital(-1*remainingNegative);
			double interestYear2 = remainingNegative * data.get(1).getTotalCosts()/data.get(0).getTotalCosts()
				* pfyResults[0]/12 *(project.getCapitalInterest()*0.01);
			data.get(1).setWorkingCapital(-1*interestYear2);
		}
	}
	
	public static void CalculateCumulative(ArrayList<ProjectFinanceData> data) {
		data.get(0).setCumulative(data.get(0).getProfitAfterFinance());
		for (int i=1; i<data.size(); i++) {
			data.get(i).setCumulative(data.get(i-1).getCumulative()+data.get(i).getProfitAfterFinance());
		}
	}
}
