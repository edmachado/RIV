package org.fao.riv.installer.sql;

import java.io.Serializable;
import java.util.Comparator;

public class PatchComparator extends GenericComparator implements Comparator<Patch>, Serializable {
	
	private static final long serialVersionUID = 6447871627079222288L;

	public int compare(Patch sourcePatch, Patch targetPatch) {
		return compare(sourcePatch.getNumber(), targetPatch.getNumber());
	}
}