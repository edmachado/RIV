package org.fao.riv.installer.sql;

import java.io.Serializable;
import java.util.Comparator;

public class VersionComparator extends GenericComparator implements Serializable, Comparator<Version> {

	private static final long serialVersionUID = 1072917907436905088L;

	public int compare(Version sourceVersion, Version targetVersion) {
		
		int comparition = compare( sourceVersion.getMajor(), targetVersion.getMajor() );
		if ( comparition == 0 ) {															// majors are the same

			// let check minors
			comparition = compare( sourceVersion.getMinor(), targetVersion.getMinor() );			
			if ( comparition == 0 ) {														// minors are the same: there are patches

				if (sourceVersion.hasPatches() && targetVersion.hasPatches()) {				// will be compared the lowest patches numbers
					comparition = compare(sourceVersion.getPatches().first().getNumber(), targetVersion.getPatches().first().getNumber()); 

				} else if ( sourceVersion.hasPatches() && !targetVersion.hasPatches()) {	// we assume source follow target
					comparition = 1;
					
				} else if ( !sourceVersion.hasPatches() && targetVersion.hasPatches()) {	// we assume target follow source
					comparition = -1;
				}
			}
		}
			
		return comparition;
	}
}
