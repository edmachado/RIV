package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

@Entity
@Table(name="DONOR")
public class Donor implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID", nullable = false)
	private Integer donorId;
	@Size(max=150)
	@Column(name="DESCRIPTION")
	private String description;
	@Column(name="CONTRIB_TYPE")
	private Integer contribType;
	@Column(name="NOT_SPECIFIED")
	private Boolean notSpecified=false;
	
	@ManyToOne
	@JoinColumn(name="PROJECT_ID", nullable=false)
	private Project project;
	
	public Integer getDonorId() {
		return donorId;
	}
	public void setDonorId(Integer id) {
		this.donorId = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getContribType() {
		return contribType;
	}
	public void setContribType(Integer contribType) {
		this.contribType = contribType;
	}
	public Boolean getNotSpecified() {
		return notSpecified;
	}
	public void setNotSpecified(Boolean notSpecified) {
		this.notSpecified = notSpecified;
	}
	public Project getProject() {
		return project;
	}
	public void setProject(Project project) {
		this.project = project;
	}
}
