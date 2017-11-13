package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
@DiscriminatorValue("7")
public class ProjectItemPersonnelWithout extends ProjectItemGeneralBase  {

	private static final long serialVersionUID = 1L;

	@Transient
	private Double unitNumJson;
	public Double getUnitNumJson() {
		return unitNumJson;
	}
	public void setUnitNumJson(Double i) {
		this.unitNumJson=i;
	}
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;

	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}
	
	@Override
	public ProjectItemPersonnelWithout copy() {
		return (ProjectItemPersonnelWithout) super.copy(ProjectItemPersonnelWithout.class);
	}

	protected String propertyLabel() {
		return "personnelWo";
	}
}