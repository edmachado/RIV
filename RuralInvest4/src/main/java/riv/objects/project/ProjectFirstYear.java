package riv.objects.project;

import java.util.ArrayList;

public class ProjectFirstYear {
	private Project project;
	private ArrayList<ProjectMonthFlow> incomes;
	private ArrayList<ProjectMonthFlow> costs;
	private double maintenanceCost;
	private double generalCost;
	private double[] totals;
	private double[] cumulative;
	
	public ProjectFirstYear(Project project) {
		this.project=project;
		incomes = new ArrayList<ProjectMonthFlow>();
		costs = new ArrayList<ProjectMonthFlow>();
		totals=new double[12];
		cumulative=new double[12];
		Calculate();
	}
	
	private void Calculate() {
		// fixed-month costs
		for (ProjectItemAsset ass : project.getAssets())
			if (ass.getYearBegin()==1)  maintenanceCost += (ass.getMaintCost()*ass.getUnitNum());
		maintenanceCost /= 12;
		for (ProjectItemGeneral gen : project.getGenerals())
			generalCost+=gen.getUnitCost()*gen.getUnitNum()-gen.getOwnResources();
		
		for (ProjectItemPersonnel per : project.getPersonnels()) {
			generalCost+=per.getUnitCost()*per.getUnitNum()-per.getOwnResources();
		}
		
		generalCost /= 12;
		for (int i=0;i<12;i++) {
			totals[i]=-(maintenanceCost+generalCost);
		}		
		
		// block-dependent costs and incomes
		for (Block block : project.getBlocks()) {
			
			// calculate income data
			ProjectMonthFlow incFlow = new ProjectMonthFlow();
			incFlow.setDescription(block.getDescription());
			int activeChrons = 0;
			for (BlockChron chron : block.getChrons().values()) {
				if (chron.getChronType()==2) activeChrons++;
			}
			double unitIncome = block.getTotalCashIncome().doubleValue()*block.getCycleFirstYearIncome()*block.getPatterns().get(1).getQty()/activeChrons;
			for (int i=0;i<12;i++) {
				if (block.getChrons().get("2-"+i+"-0")!=null) incFlow.getFlowData()[i] = unitIncome;
				if (block.getChrons().get("2-"+i+"-1")!=null) incFlow.getFlowData()[i] = incFlow.getFlowData()[i] + unitIncome;
				totals[i]+=incFlow.getFlowData()[i];
			}
			getIncomes().add(incFlow);
			
			// calculate cost data
			ProjectMonthFlow costFlow = new ProjectMonthFlow();
			costFlow.setDescription(block.getDescription());
			int activeCostChrons = 0;
			for (BlockChron chron : block.getChrons().values()) {
				if (chron.getChronType()==0) activeCostChrons++;
			}
			double unitCost = block.getTotalCashCost().doubleValue()*block.getCycleFirstYear()*block.getPatterns().get(1).getQty()/activeCostChrons;
			for (int i=0;i<12;i++) {
				if (block.getChrons().get("0-"+i+"-0")!=null) costFlow.getFlowData()[i] = unitCost;
				if (block.getChrons().get("0-"+i+"-1")!=null) costFlow.getFlowData()[i] = costFlow.getFlowData()[i] + unitCost;
				totals[i]-=costFlow.getFlowData()[i];
			}
			getCosts().add(costFlow);
		}
		
		// cumulative
		cumulative[0]=totals[0];
		for (int i=1;i<12; i++) {
			cumulative[i]=cumulative[i-1]+totals[i];
		}
	}
	
	public static double[] WcAnalysis(ProjectFirstYear pfy) {
		// results[0] is financing period (no. months)
		// results[1] is largest negative value
		// results[2] is number of negative months
		double[] results = new double[3];
		
		int lastNegMonth=0; int negMonths=0;
		Double highestNeg=0.0;		
		for (int i=0;i<12;i++) {
			if (pfy.getCumulative()[i]<0) {
				negMonths++;
				lastNegMonth=i+1; // add one because i is 0-based
				if (pfy.getCumulative()[i]<highestNeg) {					
					highestNeg=pfy.getCumulative()[i];
				}
			}
		}
		results[0] = lastNegMonth==0 ? 0 : lastNegMonth>10 ? 12 : lastNegMonth+2; // month of highest negative +2, not to exceed 12 
		results[1] = Math.round(highestNeg*100)/100;
		results[2] = negMonths;
		return results;
	}

	public ArrayList<ProjectMonthFlow> getIncomes() {
		return incomes;
	}

	public ArrayList<ProjectMonthFlow> getCosts() {
		return costs;
	}

	public double getMaintenanceCost() {
		return maintenanceCost;
	}

	public double getGeneralCost() {
		return generalCost;
	}

	public double[] getTotals() {
		return totals;
	}

	public double[] getCumulative() {
		return cumulative;
	}

}
