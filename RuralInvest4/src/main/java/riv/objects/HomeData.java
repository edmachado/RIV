package riv.objects;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// helper class to hold data used in user's home page
public class HomeData {
	// how many profiles, projects, complete, incomplete etc in DB
	private int[] dbStats;
	private List<HomeDataInProgress> inProgress = new ArrayList<HomeDataInProgress>(3);
	
	public void setDbStats(int[] dbStats) {
		this.dbStats = dbStats;
	}
	public int[] getDbStats() {
		return dbStats;
	}
	
	public void setProjs(List<Object[]> projs) {
		for (Object[] p : projs) {
			if (inProgress.size()>3) {break;}
			HomeDataInProgress ip = new HomeDataInProgress((String)p[1], true, (Date)p[2], Integer.parseInt(p[0].toString()));
			inProgress.add(ip);
		}
	}
	
	public void setProfs(List<Object[]> profs) {
		for (Object x : profs) {
			if (inProgress.size()>3) {break;}
			Object[] p = (Object[])x;
			HomeDataInProgress ip = new HomeDataInProgress((String)p[1], false, (java.util.Date)p[2], Integer.parseInt(p[0].toString()));//, (String)p[3]);
			inProgress.add(ip);
		}
	}

	public List<HomeDataInProgress> getInProgress() {
		return inProgress;
	}
	
	protected void finalize() {
		inProgress.clear();
	}
}
