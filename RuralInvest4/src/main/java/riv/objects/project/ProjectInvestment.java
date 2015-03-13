package riv.objects.project;

import java.util.Set;

public interface ProjectInvestment {
	public Double getDonated();

//	public void setDonated(Double Donated);

	public java.lang.Integer getYearBegin();

	public void setYearBegin(java.lang.Integer YearBegin);

	public Double getFinanced();

	public Double getTotal();

	public Double getOwnResources();

	public String getDescription();

	public String getUnitType();

	public Double getUnitNum();

	public Double getUnitCost();
	
	public Set<ProjectItemDonation> getDonations();
	public void setDonations(Set<ProjectItemDonation> donations);
}

