package riv.objects;

import java.util.ArrayList;
import java.util.List;

import riv.objects.project.BlockBase;
import riv.objects.project.BlockChron;
import riv.objects.project.Project;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemGeneral;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectMonthFlow;

public class ProjectFirstYear {
	private Project project;
	private ArrayList<ProjectMonthFlow> incomes;
	private ArrayList<ProjectMonthFlow> costs;
	private double maintenanceCost;
	private double generalCost;
	private double[] totals;
	private double[] cumulative;
	private boolean without;
	
	public ProjectFirstYear(Project project, boolean without) {
		this.project=project;
		this.without=without;
		incomes = new ArrayList<ProjectMonthFlow>();
		costs = new ArrayList<ProjectMonthFlow>();
		totals=new double[12];
		cumulative=new double[12];
		Calculate();
	}
	
	
	
	public List<double[]> getSummary() {
		List<double[]> summaries = new ArrayList<double[]>();

		double[] incomes = new double[12];
		for (ProjectMonthFlow f : this.incomes) {
			for (int i=0;i<12;i++) {
				incomes[i]+=f.getFlowData()[i];
			}
		}
		
		double[] costs = new double[12];
		for (ProjectMonthFlow f : this.costs) {
			for (int i=0;i<12;i++) {
				costs[i]+=f.getFlowData()[i];
			}
		}

		double[] maint = new double[12];
		for (int i=0;i<12;i++) { maint[i]=maintenanceCost; }
		
		double[] gen = new double[12];
		for (int i=0;i<12;i++) { gen[i]=generalCost; }
		
		summaries.add(incomes);
		summaries.add(costs);
		summaries.add(maint);
		summaries.add(gen);
		summaries.add(totals);
		summaries.add(cumulative);
		
		return summaries;
	}
	
	private void Calculate() {
		// fixed-month costs
		if (!without) {
			for (ProjectItemAsset ass : project.getAssets()) {
				if (ass.getYearBegin()==1)  { maintenanceCost += (ass.getMaintCost()*ass.getUnitNum()); }
			}
		} else {
			for (ProjectItemAssetWithout ass : project.getAssetsWithout()) {
				if (ass.getYearBegin()==1)  { maintenanceCost += (ass.getMaintCost()*ass.getUnitNum()); }
			}
		}
		maintenanceCost /= 12;
		
		for (ProjectItemGeneral gen : project.getGenerals()) {
			generalCost+=gen.getUnitCost()*gen.getUnitNum()-gen.getOwnResources();
		}
		for (ProjectItemPersonnel per : project.getPersonnels()) {
			generalCost+=per.getUnitCost()*per.getUnitNum()-per.getOwnResources();
		}
		
		generalCost /= 12;
		for (int i=0;i<12;i++) {
			totals[i]=-(maintenanceCost+generalCost);
		}		
		
		// block-dependent costs and incomes
		for (BlockBase block : without ? project.getBlocksWithout() : project.getBlocks()) {
			
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