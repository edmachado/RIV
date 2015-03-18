package riv.tests;

import org.junit.Test;
import org.junit.Assert;

import riv.objects.project.Project;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;

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
		a.setSalvage(0.0);
		a.setMaintCost(0.0);
		a.setReplace(true);
		p.addAsset(a);
		Assert.assertEquals(600.00, a.getResidual(), 0.01);
		
		ProjectItemAssetWithout b = new ProjectItemAssetWithout();
		b.setEconLife(5);
		b.setYearBegin(7);
		b.setUnitCost(3000.0);
		b.setUnitNum(1.0);
		b.setOwnResources(3.0);
		b.setSalvage(0.0);
		b.setMaintCost(0.0);
		b.setReplace(true);
		p.addAssetWithout(b);
		Assert.assertEquals(600.00, b.getResidual(), 0.01);
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
		a.setSalvage(0.0);
		a.setMaintCost(0.0);
		a.setReplace(false);
		p.addAsset(a);
		Assert.assertEquals(4725.0, a.getResidual().doubleValue(), 0.01);
		
		ProjectItemAssetWithout b = new ProjectItemAssetWithout();
		b.setEconLife(25);
		b.setYearBegin(1);
		b.setUnitCost(15.0);
		b.setUnitNum(525.0);
		b.setOwnResources(0.0);
		b.setSalvage(0.0);
		b.setMaintCost(0.0);
		b.setReplace(false);
		p.addAsset(a);
		Assert.assertEquals(4725.0, b.getResidual().doubleValue(), 0.01);
	}
}
