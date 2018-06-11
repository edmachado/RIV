package riv.objects.project;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;
/**
 * Base class for General cost associated with a project
 * @author Bar Zecharya
 *
 */
@Entity
public abstract class ProjectItemGeneralBase extends HasPerYearItems<ProjectItemGeneralPerYear> {
	private static final long serialVersionUID = 1L;
	
	@Transient
	private Double unitNumJson;
	public Double getUnitNumJson() {
		return unitNumJson;
	}
	public void setUnitNumJson(Double i) {
		this.unitNumJson=i;
	}
	
	@OneToMany(mappedBy="parent", orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@MapKey(name="year")
	private Map<Integer, ProjectItemGeneralPerYear> years = new HashMap<Integer, ProjectItemGeneralPerYear>();
	public Map<Integer, ProjectItemGeneralPerYear> getYears() {
		return years;
	}
	public void setYears(Map<Integer, ProjectItemGeneralPerYear> years) {
		this.years = years;
	}
	
	public void addYears(int years) {
		this.setYears(new HashMap<Integer, ProjectItemGeneralPerYear>());
		for (int i=0;i<years;i++) {
			try {
				ProjectItemGeneralPerYear py = new ProjectItemGeneralPerYear();
				py.setYear(i);
				py.setParent(this);
				this.getYears().put(i, py);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected abstract String propertyLabel();
	
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
	   item.setOrderBy(getOrderBy());
	   item.setYears(new HashMap<Integer, ProjectItemGeneralPerYear>());
	   for (ProjectItemGeneralPerYear oldYear : this.getYears().values()) {
		   ProjectItemGeneralPerYear newYear = new ProjectItemGeneralPerYear();
		   newYear.setUnitNum(oldYear.getUnitNum());
		   newYear.setOwnResources(oldYear.getOwnResources());
		   newYear.setYear(oldYear.getYear());
		   newYear.setParent(item);
		   item.getYears().put(newYear.getYear(), newYear);
	   }
	   return item;
   }
	 
	 public String testingProperties(RivConfig rivConfig) {
			String lineSeparator = System.getProperty("line.separator");
		   CurrencyFormatter cf = rivConfig.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".description="+description+lineSeparator);
		   if (propertyLabel().startsWith("suppl")) {
			   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".unitType="+unitType+lineSeparator);
		   } else {
			   sb.append("step8."+propertyLabel()+"."+(this.getOrderBy()+1)+".unitType="+rivConfig.getLabourTypes().get(unitType)+lineSeparator);
		   }

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
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public void convertCurrency(Double exchange, int scale) {
		this.setUnitCost(this.getProject().round(this.getUnitCost()*exchange, scale));
		if (this.getProject().isPerYearGeneralCosts()) {
			for (ProjectItemGeneralPerYear year : this.getYears().values()) {
				year.convertCurrency(exchange, scale);
			}
		} else {
			this.getYears().get(0).convertCurrency(exchange, scale);
		}
	}
}
