package riv.objects;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.Date;

// helper class to hold data used in user's home page
public class HomeData {
	// how many profiles, projects, complete, incomplete etc in DB
	private int[] dbStats;
	private TreeMap<java.util.Date, HomeDataInProgress> inProgress = new TreeMap<java.util.Date, HomeDataInProgress>(java.util.Collections.reverseOrder());
	
	public void setDbStats(int[] dbStats) {
		this.dbStats = dbStats;
	}
	public int[] getDbStats() {
		return dbStats;
	}
	
	public void setProjs(List<Object[]> projs) {
		for (Object[] p : projs) {
			//Object[] p = (Object[])x;
			HomeDataInProgress ip = new HomeDataInProgress((String)p[1], true, (Date)p[2], Integer.parseInt(p[0].toString()));//, (String)p[3]);
			inProgress.put((Date)p[2], ip);
		}
	}
	
	public void setProfs(List<Object[]> profs) {
		for (Object x : profs) {
			Object[] p = (Object[])x;
			HomeDataInProgress ip = new HomeDataInProgress((String)p[1], false, (java.util.Date)p[2], Integer.parseInt(p[0].toString()));//, (String)p[3]);
			inProgress.put((Date)p[2], ip);
		}
	}

	public Collection<HomeDataInProgress> getInProgress() {
		return (Collection<HomeDataInProgress>)inProgress.values();
	}
}
