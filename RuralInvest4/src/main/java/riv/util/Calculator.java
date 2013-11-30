package riv.util;

import java.util.ArrayList;
import java.math.BigDecimal;

import riv.objects.project.ProjectFinanceData;

/**
 * @author Bar Zecharya
 * Performs financial calculations such as Net Present Value and Internal Rate of Return.
 */
public class Calculator {
	
	private static double[] getFlow(ArrayList<ProjectFinanceData> financeData, boolean includeDonation) {
//		double[] cashFlows = new double[financeData.size()+1];
//		double firstYrInvestment = financeData.get(0).getCostInvestDonated()-financeData.get(0).getCostInvestDonatedWithout();
//		cashFlows[0] = firstYrInvestment;
//		for (ProjectFinanceData data : financeData) {
//			cashFlows[data.getYear()] = includeDonation ? data.getNetIncomeAfterDonation() : data.getNetIncome();
//		}
//		cashFlows[1]=cashFlows[1]-firstYrInvestment;
		
		double[] cashFlows = new double[financeData.size()];
	   	int index=0;
	   	 for(ProjectFinanceData data:financeData) {
	   		 cashFlows[index]= includeDonation ? data.getNetIncomeAfterDonation() : data.getNetIncome();
	   		 index++;
	   	 }
		
		return cashFlows;
	}
	
	/**
	 * Wrapper for netPresentValue(double discountRate, double[] cashFlows).  Uses ArrayList of ProjectFinanceData to a cash flow array.
	 * @param discountRate The discount rate at which to calculate NPV
	 * @param financeData ProjectFinanceDatas from which the cash flows are extracted
	 * @param includeDonation whether or not to consider investment donations in calculation.  wc donation is not part of the calculation because wc donation should already be excluded or not (specified as a parameter in analyzeProject).
	 * @return the calculated Net Present Value
	 */
	public static final double  netPresentValue (double discountRate, ArrayList<ProjectFinanceData> financeData, boolean includeDonation)	{
		double[] cashFlows = getFlow(financeData, includeDonation);
	   	return netPresentValue(discountRate, cashFlows);
	}
	
	/**
	 * Calculates Net Present Value based on an array of cash flows.
	 * @param discountRate the discount rate at which to calculate NPV
	 * @param cashFlows the cash flows used to calculate NPV
	 * @return the Net Present Value
	 */
	private static final double  netPresentValue (double discountRate, double[] cashFlows)	{	   	 
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
	public static final BigDecimal  internalRateOfReturn (double irrEstimate, ArrayList<ProjectFinanceData> financeData, boolean includeDonation) {
		double[] cashFlows = getFlow(financeData, includeDonation);
	   	return internalRateOfReturn(irrEstimate, cashFlows);
	 }
	 
    /**
     * Calculated the Internal Rate of Return for a set of cash flows based on a guess IRR.
     * @param irrEstimate estimated IRR
     * @param cashFlows cash flows to calculate IRR
     * @return calculated IRR
     */
    private static final BigDecimal  internalRateOfReturn(double irrEstimate, double[] cashFlows) {
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
    public static double[] payments(double rate, double per, double nper, double pv, double fv) {
    	double[] pays= new double[2];
    	double totalPayment = getPay(rate, nper, pv, fv, 0.0);
    	double interest = (-getPrinc(pv, totalPayment, rate, per - 1.0)) * rate;
    	double principal = totalPayment-interest;
    	pays[0]=-principal;
    	pays[1]=-interest;
    	return pays;
    }
            
    static double getPay(double rate, double nper, double pv, double fv, double type) {
    	if (rate==0) return -pv/nper;
    	double pvif, fvifa;
    	pvif = Math.pow(1 + rate, nper);
    	fvifa = (Math.pow(1 + rate, nper) - 1.0) / rate;
    	return ((-pv * pvif - fv) / ((1.0 + rate * type) * fvifa));
    }
    
    static double getPrinc(double start, double pay, double rate, double period) {
    	if (rate==0) { return pay; }
    	return (start * Math.pow(1.0 + rate, period) +
    		pay * ((Math.pow(1 + rate, period) - 1) / rate));
    }
}