package riv.objects.project;

import riv.objects.HasDonations;

public interface ProjectInvestment extends HasDonations {
	public Integer getYearBegin();

	public void setYearBegin(Integer YearBegin);

	public Double getFinanced();

	public Double getTotal();

	public Double getOwnResources();

	public String getDescription();

	public String getUnitType();

	public Double getUnitNum();

	public Double getUnitCost();
}

