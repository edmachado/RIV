package riv.objects;

import java.util.Set;

import riv.objects.config.User;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileProductIncome;
import riv.objects.profile.ProfileProductLabour;
import riv.objects.project.BlockIncome;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemLabourWithout;
import riv.objects.project.ProjectItemNongenLabour;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectItemPersonnelWithout;
import riv.objects.reference.ReferenceCost;
import riv.objects.reference.ReferenceIncome;
import riv.objects.reference.ReferenceLabour;

public abstract class Probase {
	public abstract Integer getProId();
	public abstract boolean getIncomeGen();
	public abstract boolean isProject();
	public abstract boolean isShared();
	public abstract Integer getWizardStep();
	public abstract void setWizardStep(Integer step);
	public abstract User getTechnician();
	
	public abstract void setRefCosts(Set<ReferenceCost> refCosts);
	public abstract Set<ReferenceCost> getRefCosts();
	public abstract void addReferenceCost(ReferenceCost item);
	
	public abstract void setRefIncomes(Set<ReferenceIncome> refIncomes);
	public abstract Set<ReferenceIncome> getRefIncomes();
	public abstract void addReferenceIncome(ReferenceIncome item);
	
	public abstract void setRefLabours(Set<ReferenceLabour> refLabours);
	public abstract Set<ReferenceLabour> getRefLabours();
	public abstract void addReferenceLabour(ReferenceLabour item);
	
	 /**
	 * Removes "reference table item" from LinkedToable to be used from Probase.copy().
	 * Depending on whether the result is designed for export to .riv file, the reference item is stored as
	 * "linkedToExport" (holding the "orderBy" field of the reference item), or (for internal clone function)
	 * the links are recreated using the new profile's reference item collections.
	 * @param item 			the new LinkedToable being created
	 * @param forExport		if true, intended result is .riv file. if false, intended result is internal clone
	 */
	protected void prepareLinkedToableForExport(LinkedToable item, boolean forExport) {
		 if (item.getLinkedTo()!=null) {
			item.setExportLinkedTo(item.getLinkedTo().getOrderBy());
			item.setLinkedTo(null);
			if (!forExport) {
				linkedToImport(item);
			}
		 }
	 }
	
	protected  void linkedToImport(LinkedToable item) {
		if (item.getExportLinkedTo()!=null) {
			if (item instanceof ProfileProductIncome || item instanceof BlockIncome) {
				for (ReferenceIncome inc : item.getProbase().getRefIncomes()) {
					if (inc.getOrderBy()==item.getExportLinkedTo()) {
						item.setLinkedTo(inc);
						item.setExportLinkedTo(null);
						break;
					}
				}
			}  else if (item instanceof ProfileItemLabour || item instanceof ProfileProductLabour
					|| item instanceof ProjectItemLabour || item instanceof ProjectItemLabourWithout
					|| item instanceof ProjectItemNongenLabour
					|| item instanceof ProjectItemPersonnel || item instanceof ProjectItemPersonnelWithout) {
				for (ReferenceLabour lab : item.getProbase().getRefLabours()) {
					if (lab.getOrderBy()==item.getExportLinkedTo()) {
						item.setLinkedTo(lab);
						item.setExportLinkedTo(null);
						break;
					}
				}
			} else {
				for (ReferenceCost cost : item.getProbase().getRefCosts()) {
					if (cost.getOrderBy()==item.getExportLinkedTo()) {
						item.setLinkedTo(cost);
						item.setExportLinkedTo(null);
						break;
					}
				}
			}
		}
	}
}