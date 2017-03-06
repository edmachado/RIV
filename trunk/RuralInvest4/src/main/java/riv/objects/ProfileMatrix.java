package riv.objects;

import riv.objects.profile.Profile;
import riv.objects.profile.ProfileItemGeneral;
import riv.objects.profile.ProfileItemGeneralWithout;
import riv.objects.profile.ProfileItemGood;
import riv.objects.profile.ProfileItemGoodWithout;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileItemLabourWithout;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductWithout;

public class ProfileMatrix {
	public double totalInvestmentWith;
	public double totalOwnResourcesWith;
	public double totalIncomeWith;
	public double operationCostWith;
	public double generalCostWith;
	public double annualReserveWith;
	public double totalInvestmentWithout;
	public double totalOwnResourcesWithout;
	public double totalIncomeWithout;
	public double operationCostWithout;
	public double generalCostWithout;
	public double annualReserveWithout;
	
	public ProfileMatrix(Profile p) {
		 for (ProfileItemGood good : p.getGlsGoods()) {
			 totalInvestmentWith += good.getUnitNum()*good.getUnitCost();
			 totalOwnResourcesWith += good.getOwnResource();
			 annualReserveWith += ((good.getUnitCost()-good.getSalvage())*good.getUnitNum())/good.getEconLife();
		 }
		 
		 for (ProfileItemGoodWithout good : p.getGlsGoodsWithout()) {
			 totalInvestmentWithout += good.getUnitNum()*good.getUnitCost();
			 totalOwnResourcesWithout += good.getOwnResource();
			 annualReserveWithout += ((good.getUnitCost()-good.getSalvage())*good.getUnitNum())/good.getEconLife();
		 }

		 for(ProfileItemLabour lab : p.getGlsLabours()) {
			 totalInvestmentWith += lab.getUnitNum()*lab.getUnitCost();
			 totalOwnResourcesWith += lab.getOwnResource();
		 }
		 
		 for(ProfileItemLabourWithout lab : p.getGlsLaboursWithout()) {
			 totalInvestmentWithout += lab.getUnitNum()*lab.getUnitCost();
			 totalOwnResourcesWithout += lab.getOwnResource();
		 }

		 for (ProfileItemGeneral gen : p.getGlsGeneral()) {
			 generalCostWith += gen.getUnitCost()*gen.getUnitNum();
		 }
		 for (ProfileItemGeneralWithout gen : p.getGlsGeneralWithout()) {
			 generalCostWithout += gen.getUnitCost()*gen.getUnitNum();
		 }

		 for (ProfileProduct prod : p.getProducts()) {
			 totalIncomeWith+=prod.getTotalIncome().doubleValue();
			 operationCostWith+=prod.getTotalCost().doubleValue();			
		 }
		 if (p.getWithWithout()) {
			 for (ProfileProductWithout prod : p.getProductsWithout()) {
				 totalIncomeWithout+=prod.getTotalIncome().doubleValue();
				 operationCostWithout+=prod.getTotalCost().doubleValue();			
			 }
		 }
	}
}
