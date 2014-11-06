package riv.objects.project;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import riv.objects.LinkedToable;
import riv.objects.OrderByable;
import riv.objects.Probase;
import riv.objects.reference.ReferenceItem;


/**
 * Superclass for 1) investment costs associated to a project, both income- and non-income-generating;
 * 2) general costs only for income-generating projects; and 3) contributions for non-income-generating projects.
 */
@Entity
@Table(name="PROJECT_ITEM")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class ProjectItem implements java.io.Serializable, OrderByable, LinkedToable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="PROJ_ITEM_ID", nullable = false)
	private int projItemId;
	protected String description;
	@Column(name="ORDER_BY")
	private Integer OrderBy;
	@Column(name="UNIT_TYPE")
	protected java.lang.String unitType;
	@Column(name="UNIT_NUM")
	protected Double unitNum;
	@Column(name="UNIT_COST")
	protected Double unitCost;
	@Size(max=2000)
	private String note;
	@ManyToOne
	@JoinColumn(name="LINKED_TO")
	private ReferenceItem linkedTo;
	@Transient
	protected Integer exportLinkedTo;

	// Constructors

	/** default constructor */
	public ProjectItem() {
	}

	/** constructor with id */
	public ProjectItem(java.lang.Integer ProjItemId) {
		this.projItemId = ProjItemId;
	}

	public int getProjItemId () {
		return this.projItemId;
	}

	public void setProjItemId (int ProjItemId) {
		this.projItemId = ProjItemId;
	}


	public void setOrderBy(Integer orderBy) {
		OrderBy = orderBy;
	}

	public Integer getOrderBy() {
		return OrderBy;
	}
	
	 public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public java.lang.String getUnitType () {
		return this.unitType;
	}

	public void setUnitType (java.lang.String UnitType) {
		this.unitType = UnitType;
	}

	public Double getUnitNum () {
		return this.unitNum;
	}

	public void setUnitNum (Double UnitNum) {
		this.unitNum = UnitNum;
	}

	public Double getUnitCost () {
		return unitCost;
	}

	public void setUnitCost (Double UnitCost) {
		unitCost = UnitCost;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setLinkedTo(ReferenceItem linkedTo) {
		this.linkedTo = linkedTo;
	}

	public ReferenceItem getLinkedTo() {
		return linkedTo;
	}

	public void setExportLinkedTo(Integer exportLinkedTo) {
		this.exportLinkedTo = exportLinkedTo;
	}

	public Integer getExportLinkedTo() {
		return exportLinkedTo;
	}

	public abstract Project getProject();
	public abstract void setProject(Project p);
	public abstract ProjectItem copy(); 
	public Probase getProbase() {
		return this.getProject();
	}
	
	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		// at the moment, only ProjectItemContribution is grouped by year and order, not only by order
		// if other classes implement this feature, they should use an interface
		if (this.getClass().isAssignableFrom(ProjectItemContribution.class) && i.getClass().isAssignableFrom(ProjectItemContribution.class)) {
			int compare = ((ProjectItemContribution)this).getYear() - ((ProjectItemContribution)i).getYear();
			if (compare!=0) { return compare; }
		}
		int compare = this.getOrderBy() - i.getOrderBy();
//		if(compare == 0) return 1;
		return compare; 
	}

	/** Override equals()
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		if (this == obj) return true;
		if (getClass() != obj.getClass()) return false;
		ProjectItem other = (ProjectItem) obj;
		boolean isEqual = OrderBy.equals(other.OrderBy) &&
				unitCost.equals(other.unitCost) &&
			unitNum.equals(other.unitNum) &&
			unitType.equals(other.unitType) &&
			description.equals(other.description)
			&& (getClass().isAssignableFrom(ProjectItemContribution.class) && obj.getClass().isAssignableFrom(ProjectItemContribution.class) && ((ProjectItemContribution)this).getYear()==((ProjectItemContribution)obj).getYear());
		return isEqual;
	}
	
	public int hashCode() {
		final int multiplier = 23;
	    int code = 133;
	    if (OrderBy!=null) code = multiplier * code + OrderBy;
	    if (unitCost!=null) code = multiplier * unitCost.intValue();
	    if (unitNum!=null) code = multiplier * code + unitNum.intValue();
	    if (unitType!=null) code = multiplier * code + unitType.hashCode();
	    if (description!=null) code = multiplier * code + description.hashCode();
	    if (getClass().isAssignableFrom(ProjectItemContribution.class) && ((ProjectItemContribution)this).getYear()!=null) 
	    	code = multiplier * code + ((ProjectItemContribution)this).getYear().hashCode();
	    return code;
	}

	
	

}