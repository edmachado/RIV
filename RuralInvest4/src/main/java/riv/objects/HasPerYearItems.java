package riv.objects;

import java.util.Map;

public interface HasPerYearItems<T extends PerYearItem> {
	public Map<Integer, T> getYears();
	public void setYears(Map<Integer,T> years);
}