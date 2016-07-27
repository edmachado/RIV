package riv.objects.project;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;

import riv.objects.HasPerYearItems;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;
/**
 * Base class for General cost associated with a project
 * @author Bar Zecharya
 *
 */
@Entity
public abstract class ProjectItemGeneralBase extends ProjectItem implements HasPerYearItems<ProjectItemGeneralPerYear> {
	private static final long serialVersionUID = 1L;

	@Column(name="OWN_RESOURCES")
	protected Double OwnResources;

	@OneToMany(mappedBy="general", orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@MapKey(name="year")
	private Map<Integer, ProjectItemGeneralPerYear> years = new HashMap<Integer, ProjectItemGeneralPerYear>();
	
	public abstract Project getProject();
	public abstract void setProject (Project Project);


	protected abstract String propertyLabel(); 
	
	
	public Map<Integer, ProjectItemGeneralPerYear> getYears() {
		return years;
	}
	public void setYears(Map<Integer, ProjectItemGeneralPerYear> years) {
		this.years=years;
	}
	public void addYears(int years) {
		this.years=new HashMap<Integer, ProjectItemGeneralPerYear>();
		for (int i=0;i<years;i++) {
			ProjectItemGeneralPerYear py = new ProjectItemGeneralPerYear();
			py.setYear(i);
			py.setGeneral(this);
			this.years.put(i, py);
		}
	}

	@Override
	public Double getUnitNum() {
		throw new RuntimeException("UnitNum should be called on a specific year of a general cost.");
	}
	
	public ProjectItemGeneralSingleYear getOneYearData(int year) {
		if (year>getProject().getDuration()) {
			throw new RuntimeException("Attempting to access a year beyond the project duration. This should not be allowed to occur.");
		}
		
		ProjectItemGeneralSingleYear single = new ProjectItemGeneralSingleYear();
		ProjectItemGeneralPerYear perYear = years.get(year);
		if (perYear==null) {
			throw new RuntimeException("ProjectItemGeneralPerYear is missing. This should not occur at runtime.");
		}
		single.setProjItemId(this.getProjItemId());
		single.setDescription(description);
		single.setUnitType(unitType);
		single.setUnitCost(unitCost);
		single.setUnitNum(perYear.getUnitNum());
		single.setOwnResources(perYear.getOwnResources());
		single.setTotalCost(perYear.getTotal());
		single.setExternal(perYear.getExternal());
		single.setLinked(this.getLinkedTo()!=null);
		
		return single;
	}
	
	
	 protected ProjectItemGeneralBase copy(Class<? extends ProjectItemGeneralBase> newClass) {
	 ProjectItemGeneralBase item;
	 if (newClass.isAssignableFrom(ProjectItemGeneral.class)) {
		 item = new ProjectItemGeneral();
	 } else if (newClass.isAssignableFrom(ProjectItemGeneralWithout.class)) {
		 item = new ProjectItemGeneralWithout();
	 } else if (newClass.isAssignableFrom(ProjectItemPersonnel.class)) {
		 item = new ProjectItemPersonnel();
	 } else {
		 item = new ProjectItemPersonnelWithout();
	 }
	 
	   item.setDescription(description);
	   item.setExportLinkedTo(exportLinkedTo);
	   item.setLinkedTo(getLinkedTo());
	   item.setProject(this.getProject());
	   item.setUnitCost(unitCost);
	   item.setUnitNum(unitNum);
	   item.setUnitType(unitType);
//	   item.setOwnResources(OwnResources);
	   item.setOrderBy(getOrderBy());
	   item.setYears(new HashMap<Integer, ProjectItemGeneralPerYear>());
	   for (ProjectItemGeneralPerYear oldYear : this.getYears().values()) {
		   ProjectItemGeneralPerYear newYear = new ProjectItemGeneralPerYear();
		   newYear.setUnitNum(oldYear.getUnitNum());
		   newYear.setOwnResources(oldYear.getOwnResources());
		   newYear.setYear(oldYear.getYear());
		   newYear.setGeneral(item);
		   item.getYears().put(newYear.getYear(), newYear);
	   }
	   return item;
   }
	 
	 public String testingProperties(RivConfig rivConfig) {
			String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".unitCost="+cf.formatCurrency(unitCost, CurrencyFormat.ALL)+lineSeparator);
		   for (ProjectItemGeneralPerYear py : getYears().values()) {
			   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".year."+py.getYear()+".unitNum="+rivConfig.getSetting().getDecimalFormat().format(py.getUnitNum())+lineSeparator);
			   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".year."+py.getYear()+".ownResources="+cf.formatCurrency(py.getOwnResources(), CurrencyFormat.ALL)+lineSeparator);
			   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".year."+py.getYear()+".total="+cf.formatCurrency(py.getTotal(), CurrencyFormat.ALL)+lineSeparator);
			   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".year."+py.getYear()+".external="+cf.formatCurrency(py.getExternal(), CurrencyFormat.ALL)+lineSeparator);
		   }
		   return sb.toString();
	   }
	
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) return false;
		ProjectItemGeneralBase x = (ProjectItemGeneralBase)obj;
		boolean isEqual = OwnResources.equals(x.OwnResources);
		return isEqual;
	}
	
	@Override
	public int hashCode() {
		int code = super.hashCode();
		final int multiplier = 23;
	    if (OwnResources!=null) code = multiplier * code + OwnResources.intValue();	    
	    return code;
	}
	
	@Override
	public void convertCurrency(Double exchange, int scale) {
		this.setUnitCost(this.getProject().round(this.getUnitCost()*exchange, scale));
		for (ProjectItemGeneralPerYear year : this.getYears().values()) {
			year.convertCurrency(exchange, scale);
		}
	}
}
