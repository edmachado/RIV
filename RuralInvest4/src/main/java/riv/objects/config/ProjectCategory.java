package riv.objects.config;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * A Project Category.  Every Project must be associated to one of these.  Each category is applicable 
 * either to income-generating projects or non-income-generating projects.
 * @author Bar Zecharya
 *
 */
@Entity
@DiscriminatorValue("2")
public class ProjectCategory extends AppConfig {
	private static final long serialVersionUID = -4201640305037826704L;

	public ProjectCategory() {} // default constructor
	
	@Column(name="INCOME_GEN")
	private boolean incomeGen;

    /**
     * Gets whether the ProjectCategory applies to 
     * income-generating projects (true) or non-income-generating projects (false).
     * @return true if the Category applies to income-generating projects
     */
    public boolean getIncomeGen () {
        return this.incomeGen;
    }
    
   /**
    * Sets whether the ProjectCategory applies to 
     * income-generating projects (true) or non-income-generating projects (false).
	 * @param incomeGen true if the Category applies to income-generating projects
	 */
    public void setIncomeGen (boolean incomeGen) {
        this.incomeGen = incomeGen;
    }
 
}
