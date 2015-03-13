package riv.objects.project;

import riv.objects.HasDonations;

public interface ProjectInvestment extends HasDonations {
	public java.lang.Integer getYearBegin();

	public void setYearBegin(java.lang.Integer YearBegin);

	public Double getFinanced();

	public Double getTotal();

	public Double getOwnResources();

	public String getDescription();

	public String getUnitType();

	public Double getUnitNum();

	public Double getUnitCost();
}

