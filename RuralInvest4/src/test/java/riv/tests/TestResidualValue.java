package riv.tests;

import org.junit.Test;
import org.junit.Assert;

import riv.objects.project.Project;
import riv.objects.project.ProjectItemAsset;

public class TestResidualValue {

	@Test
	public void ResidualValueTest1() {
		Project p = new Project();
		p.setDuration(10);
		ProjectItemAsset a = new ProjectItemAsset();
		a.setEconLife(5);
		a.setYearBegin(7);
		a.setUnitCost(3000.0);
		a.setUnitNum(1.0);
		a.setOwnResources(3.0);
		a.setDonated(0.0);
		a.setSalvage(0.0);
		a.setMaintCost(0.0);
		a.setReplace(true);
		p.addAsset(a);
		Assert.assertTrue(a.getResidual()==1800.00);
	}
	
	@Test
	public void ResidualValueTest2() {
		Project p = new Project();
		p.setDuration(10);
		ProjectItemAsset a = new ProjectItemAsset();
		a.setEconLife(25);
		a.setYearBegin(1);
		a.setUnitCost(15.0);
		a.setUnitNum(525.0);
		a.setOwnResources(0.0);
		a.setDonated(0.0);
		a.setSalvage(0.0);
		a.setMaintCost(0.0);
		a.setReplace(false);
		p.addAsset(a);
		Assert.assertEquals(a.getResidual().doubleValue(), 5355.0, 0.01);
	}
}
