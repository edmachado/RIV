package org.fao.riv.installer.sql;

public class GenericComparator {
	
	public static int compare(int value1, int value2) {
		int compare = 0; // values are the same

		if (value1 > value2) {
			compare = 1;
		} else if (value1 < value2) {
			compare = -1;
		}

		return compare;
	}	
	
}
