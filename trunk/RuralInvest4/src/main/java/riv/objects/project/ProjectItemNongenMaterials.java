package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
/**
 * Labour cost associated to a project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("9")
public class ProjectItemNongenMaterials extends ProjectItemNongenBase {
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="PROJECT_ID", nullable=false)
	private Project project;
	
	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}
	
	@Override
	 public ProjectItemNongenMaterials copy() {
		ProjectItemNongenMaterials item = new ProjectItemNongenMaterials();
	   item.setDescription(description);
	   item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(getProject());
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
	   item.setStatePublic(getStatePublic());
	   item.setOther1(getOther1());
	   item.setOrderBy(getOrderBy());
	   return item;
 }
}
