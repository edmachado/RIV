package riv.objects.project;

import java.util.Map;

import javax.persistence.Entity;

@Entity
public abstract class HasPerYearItems<T extends PerYearItem> extends ProjectItem {
	private static final long serialVersionUID = 1L;
	
	public abstract Map<Integer, T> getYears();
	public abstract void setYears(Map<Integer, T> years);
	public abstract void addYears(int years);
	
	@Override
	
	public Double getUnitNum() {
		throw new UnsupportedOperationException("UnitNum should be called on a specific year of a general cost or nig contribution.");
	}
}