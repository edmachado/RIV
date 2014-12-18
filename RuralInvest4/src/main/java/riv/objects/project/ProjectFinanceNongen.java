package riv.objects.project;

import java.util.ArrayList;
import java.util.List;

public class ProjectFinanceNongen {
	private int year;
	private double donated;
	private double ownContribution;
	private double charges;
	private double contributions;
	private double contributionsGeneral;
	private double salvage;
	private double costInvest;
	private double replacement;
	private double operation;
	private double general;
	private double maintenance;
	public ProjectFinanceNongen(int year) {
		this.year=year;
	}
	public int getYear() {
		return year;
	}
	
	// calculated fields
	public double getTotal() {
		return getTotalIncome()-getTotalCosts();
	}
	public double getTotalIncome() {
		return ownContribution+donated+salvage+charges+contributions+contributionsGeneral;
	}
	public double getTotalCosts() {
		return costInvest+replacement+operation+maintenance+general;
	}
	
	
	// property accessors
	public void setCharges(double charges) {
		this.charges = charges;
	}
	public double getCharges() {
		return charges;
	}
	public void setDonated(double donated) {
		this.donated = donated;
	}
	public double getDonated() {
		return donated;
	}
	public void setOwnContribution(double ownContribution) {
		this.ownContribution = ownContribution;
	}
	public double getOwnContribution() {
		return ownContribution;
	}
	public void setContributions(double contributions) {
		this.contributions = contributions;
	}
	public double getContributions() {
		return contributions;
	}
	public void setContributionsGeneral(double contributionsGeneral) {
		this.contributionsGeneral = contributionsGeneral;
	}
	public double getContributionsGeneral() {
		return contributionsGeneral;
	}
	public void setSalvage(double salvage) {
		this.salvage = salvage;
	}
	public double getSalvage() {
		return salvage;
	}
	public void setCostInvest(double investmentCosts) {
		this.costInvest = investmentCosts;
	}
	public double getCostInvest() {
		return costInvest;
	}
	public void setReplace(double replacement) {
		this.replacement = replacement;
	}
	public double getReplace() {
		return replacement;
	}
	public void setOperation(double operation) {
		this.operation = operation;
	}
	public double getOperation() {
		return operation;
	}
	public void setGeneral(double general) {
		this.general = general;
	}
	public double getGeneral() {
		return general;
	}
	public void setMaintenance(double maintenance) {
		this.maintenance = maintenance;
	}
	public double getMaintenance() {
		return maintenance;
	}

	public static List<double[]> getSummary(ArrayList<ProjectFinanceNongen> nongens) {
		List<double[]> summaries = new ArrayList<double[]>();

		double[] incomes = new double[nongens.size()];
		double[] costs = new double[nongens.size()];
		double[] totals = new double[nongens.size()];
		double[] cumulative = new double[nongens.size()];
		for (ProjectFinanceNongen ng : nongens) {
			int year=ng.getYear()-1;
			incomes[year]=ng.getTotalIncome();
			costs[year]=ng.getTotalCosts();
			totals[year]=ng.getTotal();
			cumulative[year]=year==0?ng.getTotal():cumulative[year-1]+ng.getTotal();
		}
		
		summaries.add(incomes);
		summaries.add(costs);
		summaries.add(totals);
		summaries.add(cumulative);
		
		return summaries;
	}
	
	public static ArrayList<ProjectFinanceNongen> analyzeProject(Project project) {
		ArrayList<ProjectFinanceNongen> data = new ArrayList<ProjectFinanceNongen>();
		for (int i=0;i<project.getDuration();i++)	data.add(new ProjectFinanceNongen(i+1));
		
		
		// general maintenance and contributions to general costs
		double maintenance=0.0;
		double contributionsGeneral=0.0;
		for (ProjectItemNongenMaterials inp : project.getNongenMaterials()) {
			maintenance+=inp.getUnitCost()*inp.getUnitNum();
			contributionsGeneral+=inp.getStatePublic()+inp.getOther1();
		}
		for (ProjectItemNongenLabour lab : project.getNongenLabours()) {
			maintenance+=lab.getUnitCost()*lab.getUnitNum();
			contributionsGeneral+=lab.getStatePublic()+lab.getOther1();
		}
		for (ProjectItemNongenMaintenance gen : project.getNongenMaintenance()) {
			maintenance+=gen.getUnitCost()*gen.getUnitNum();
			contributionsGeneral+=gen.getStatePublic()+gen.getOther1();
		}
		
		// contributions
		if (project.isPerYearContributions()) {
			for (ProjectItemContribution cont : project.getContributions()) {
				data.get(cont.getYear()-1).setContributions(data.get(cont.getYear()-1).getContributions()+cont.getTotal());
			}
		} else { // simplified approach 
			for (ProjectItemContribution c : project.getContributions()) {
				for (int i=0;i<project.getDuration();i++) {
					data.get(i).setContributions(data.get(i).getContributions()+c.getTotal());
				}
			}
		}
				
		// add general and maintenance (invariable per year)
		for (ProjectFinanceNongen pfn : data) {
			pfn.setGeneral(maintenance);
			pfn.setContributionsGeneral(contributionsGeneral);
		}
		
		// investment income and costs
		for (ProjectItemAsset asset : project.getAssets()) {
			// original purchase (once)
			data.get(asset.getYearBegin()-1).setCostInvest(data.get(asset.getYearBegin()-1).getCostInvest()+asset.getUnitCost()*asset.getUnitNum());
			data.get(asset.getYearBegin()-1).setOwnContribution(data.get(asset.getYearBegin()-1).getOwnContribution()+asset.getOwnResources());
			data.get(asset.getYearBegin()-1).setDonated(data.get(asset.getYearBegin()-1).getDonated()+asset.getDonated());
			
			int lastAssetYear = asset.getReplace() ? project.getDuration() : asset.getYearBegin()-1+asset.getEconLife();
			if (lastAssetYear>project.getDuration()) lastAssetYear = project.getDuration();
			for (int i=asset.getYearBegin()-1; i<lastAssetYear; i++) {
				// maintenance (every year)
				data.get(i).setMaintenance(data.get(i).getMaintenance()+(asset.getMaintCost()*asset.getUnitNum()));
				// replace and salvage (year after every expiry)
				if (asset.getReplace()&&i!=asset.getYearBegin()-1&&(i+1-asset.getYearBegin())%asset.getEconLife()==0) {
					data.get(i).setReplace(data.get(i).getReplace()+asset.getUnitNum()*asset.getUnitCost());
					data.get(i).setSalvage(data.get(i).getSalvage()+asset.getSalvage()*asset.getUnitNum());
				// salvage value for non-replacing assets
				} else if (!asset.getReplace() && i==lastAssetYear-1) {
					data.get(i).setSalvage(data.get(i).getSalvage()+asset.getSalvage()*asset.getUnitNum());
				}
			}
			// no residual for NIG
			/*if (asset.getReplace() || asset.getYearBegin()-1+asset.getEconLife()>project.getDuration()) {
				double annualReserve = (asset.getUnitCost()-asset.getSalvage())/asset.getEconLife();
				double yearsLeft = asset.getEconLife()-((project.getDuration()+1-asset.getYearBegin())%asset.getEconLife());
				double residual = asset.getUnitNum()*annualReserve*yearsLeft+asset.getUnitNum()*asset.getSalvage();
				data.get(data.size()-1).setIncResidual(data.get(data.size()-1).getIncResidual()+residual);
			}*/
		}
		for (ProjectItemLabour labour : project.getLabours()) {
			data.get(0).setCostInvest(data.get(0).getCostInvest()+labour.getUnitCost()*labour.getUnitNum());
			data.get(0).setOwnContribution(data.get(0).getOwnContribution()+labour.getOwnResources());
			data.get(0).setDonated(data.get(0).getDonated()+labour.getDonated());
		}
		for (ProjectItemService service : project.getServices()) {
			data.get(0).setCostInvest(data.get(0).getCostInvest()+service.getUnitCost()*service.getUnitNum());
			data.get(0).setOwnContribution(data.get(0).getOwnContribution()+service.getOwnResources());
			data.get(0).setDonated(data.get(0).getDonated()+service.getDonated());
		}
		
		// variable costs and income
		for (Block block : project.getBlocks()) {
			int cycles = block.getCyclePerYear();
			for (BlockIncome inc : block.getIncomes()) {
				for (int i=0;i<project.getDuration();i++) {
					double prodQty = block.getPatterns().get(i+1).getQty();
					data.get(i).setCharges(data.get(i).getCharges()+cycles*prodQty*inc.getUnitCost().doubleValue()*inc.getUnitNum().doubleValue());
				}
			}
			for (BlockInput inp : block.getInputs()) {
				for (int i=0;i<project.getDuration();i++) {
					double prodQty = block.getPatterns().get(i+1).getQty();
					data.get(i).setOperation(data.get(i).getOperation()+cycles*prodQty*(inp.getUnitCost().doubleValue()+inp.getTransport().doubleValue())*inp.getUnitNum().doubleValue());
				}
			}
			for (BlockLabour lab : block.getLabours()) {
				for (int i=0;i<project.getDuration();i++) {
					double prodQty = block.getPatterns().get(i+1).getQty();
					data.get(i).setOperation(data.get(i).getOperation()+cycles*prodQty*lab.getUnitCost().doubleValue()*lab.getUnitNum().doubleValue());
				}
			}
		}		
		
		return data;
	}
}
