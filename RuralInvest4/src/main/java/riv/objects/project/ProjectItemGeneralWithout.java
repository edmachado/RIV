package riv.objects.project;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * General cost associated to a project
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("6")
public class ProjectItemGeneralWithout extends ProjectItemGeneralBase {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	protected Project project;

	public Project getProject () {
		return this.project;
	}

	public void setProject (Project project) {
		this.project = project;
	}
	
	protected String propertyLabel() {
		return "supplyWo";
	}
	
	 
	 @Override
		public ProjectItemGeneralWithout copy() {
			return (ProjectItemGeneralWithout) super.copy(ProjectItemGeneralWithout.class);
		}
}
