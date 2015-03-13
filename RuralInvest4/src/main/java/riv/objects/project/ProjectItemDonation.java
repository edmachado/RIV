package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="PROJECT_ITEM_DONATION")
public class ProjectItemDonation implements java.io.Serializable {
	private static final long serialVersionUID = 1;
	
	@Id
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="ITEM_ID", nullable=false)
	private ProjectItem projectItem;
	@Id
	@Column(name="DONOR_ID")
	private int donorId;
	
	@Column
	private Double amount;

	public int getDonorId() {
		return donorId;
	}

	public void setDonorId(int donor) {
		this.donorId = donor;
	}

	public ProjectItem getProjectItem() {
		return projectItem;
	}

	public void setProjectItem(ProjectItem projectItem) {
		this.projectItem = projectItem;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
	
	
	

}
