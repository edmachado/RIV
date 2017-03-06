package riv.objects.project;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Where;

import riv.objects.OrderByable;
import riv.objects.ProductOrBlock;
import riv.util.CurrencyFormat;
import riv.util.CurrencyFormatter;
import riv.web.config.RivConfig;

/**
 * A production block (income-generating) or activity (non-income-generating) of a Project.
 *
 */
@Entity
@Table(name="PROJECT_BLOCK")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class BlockBase implements ProductOrBlock, Serializable, OrderByable {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="BLOCK_ID", nullable = false)
	private java.lang.Integer BlockId;
	private String description;
	@Column(name="ORDER_BY")
	private Integer OrderBy;
	@Column(name="LENGTH_UNIT")
	private Integer lengthUnit;
	@Column(name="CYCLE_LENGTH")
	private Double CycleLength;
	@Column(name="CYCLE_PER_YEAR")
	private Double CyclePerYear;
	@Column(name="CYCLE_FIRST_YEAR")
	private Double cycleFirstYearCosts;
	@Column(name="CYCLE_FIRST_YEAR_INCOME")
	private Double cycleFirstYearIncome;
	@Column(name="UNIT_TYPE")	
	private String UnitType;
	@Column(name="CYCLES")
	private boolean cycles=true;

	@OneToMany(mappedBy="block", targetEntity=BlockIncome.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='0'")
	private Set<BlockIncome> incomes;
	@OneToMany(mappedBy="block", targetEntity=BlockInput.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='1'")
	private Set<BlockInput> inputs;
	@OneToMany(mappedBy="block", targetEntity=BlockLabour.class, orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@OrderBy("ORDER_BY")
	@Where(clause="class='2'")
	private Set<BlockLabour> labours;
	@OneToMany(mappedBy="block", orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@MapKey(name="chronId")
	private Map<String, BlockChron> chrons;
	@OneToMany(mappedBy="block", orphanRemoval=true, cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@MapKey(name="yearNum")
	private Map<Integer, BlockPattern> patterns;
	
	@Transient 
	private String patternsError;
	@Transient
	private String chronError;
	
	// default constructor. Initializes collections 
    public BlockBase() {
    	this.chrons = new HashMap <String, BlockChron>();
    	this.patterns =new HashMap<Integer, BlockPattern>();
    	this.labours=new HashSet<BlockLabour>();
    	this.incomes=new HashSet<BlockIncome>();
    	this.inputs=new HashSet<BlockInput>();
    }
    
    public void shiftStartupMonth(int difference) {
    	List<BlockChron> newChrons = new ArrayList<BlockChron>();
		for (BlockChron chron : chrons.values()) {
			int newMonth=(chron.getMonthNum()+difference)%12;
			String newChronKey = String.format("%d-%d-%d", chron.getChronType(), newMonth, chron.isFirstPart()?0:1);
			BlockChron newChron = new BlockChron();
			newChron.setChronId(newChronKey);
			newChrons.add(newChron);
		}
		chrons.clear();
		for (BlockChron c : newChrons) {
			addChron(c);
		}
    }
    
    public void projectDurationChanged() {
    	if (patterns.size()==0 || patterns.size()==getProject().getDuration()) { 
    		// do nothing
    	} else if (patterns.size()>getProject().getDuration()) {
        	// project is now shorter (need to remove extra patterns)
    		List<Integer> removes = new ArrayList<Integer>();
	    	for (Integer year : patterns.keySet()) {
	    		if (year>this.getProject().getDuration()) {
	    			removes.add(year);
	    		}
	    	}
	    	for (Integer i : removes) {
	    		patterns.remove(i);
	    	}
    	} else {
	    	// project is now longer (need to add patterns)
	    	double qty = patterns==null ? 0.0 : patterns.get(patterns.size()).getQty();
	    	for (int i=patterns.size()+1;i<=getProject().getDuration();i++) {
	    		BlockPattern pat = new BlockPattern();
				pat.setQty(qty);
				pat.setYearNum(i);
				addPattern(pat);
	    	}
    	}
    }
    
    public abstract String getPropertiesType();
    public String testingProperties(RivConfig rc) {
    	CurrencyFormatter cf=rc.getSetting().getCurrencyFormatter();
		   StringBuilder sb = new StringBuilder();
		   String lineSeparator = System.getProperty("line.separator");
		   String base="step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".";
		   sb.append(base+"description="+this.getDescription()+lineSeparator);
//		   sb.append(base+"withoutProject="+(this.getClass().isAssignableFrom(BlockWithout.class)?"true":"false")+lineSeparator);
		   sb.append(base+"unitType="+this.getUnitType()+lineSeparator);
		   sb.append(base+"cycleLength="+rc.getSetting().getDecimalFormat().format(this.getCycleLength())+lineSeparator);
		   sb.append(base+"lengthUnit="+rc.getLengthUnits().get(this.getLengthUnit())+lineSeparator);
		   sb.append(base+"cyclePerYear="+rc.getSetting().getDecimalFormat().format(this.getCyclePerYear())+lineSeparator);
		   if (this.getProject().getIncomeGen()) {
				   sb.append(base+"cycleFirstYear="+rc.getSetting().getDecimalFormat().format(this.getCycleFirstYear())+lineSeparator);
				   sb.append(base+"cycleFirstYearIncome="+rc.getSetting().getDecimalFormat().format(this.getCycleFirstYearIncome())+lineSeparator);
		   }
		   sb.append(base+"cycles="+cycles+lineSeparator);
		   
		   for (int type=0;type<3;type++) {
			   for (int month=0;month<12;month++) {
				   for (int half=0;half<=1;half++) {
					   String key=type+"-"+month+"-"+half;
					   sb.append(base+"ch"+key+"="+(this.getChrons().containsKey(key)?"true":"false")+lineSeparator);
				   }
			   }
		   }
		   for (int i=1;i<=getProject().getDuration();i++) {
			   sb.append("step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".pat"+i+"="+rc.getSetting().getDecimalFormat().format(this.getPatterns().get(i).getQty())+lineSeparator);
		   }
		   
		   sb.append("step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".income.count="+this.getIncomes().size()+lineSeparator);
		   Double total=0.0;
		   Double totalCash=0.0;
		   for (BlockIncome i : this.getIncomes()) {
			   total+=i.getTotal().doubleValue();
			   totalCash+=i.getTotalCash().doubleValue();
			   sb.append(i.testingProperties(rc));
		   }
		   base="step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".income.Sum.";
		   sb.append(base+"description="+lineSeparator);
		   sb.append(base+"unitType="+lineSeparator);
		   sb.append(base+"unitNum="+lineSeparator);
		   sb.append(base+"qtyIntern="+lineSeparator);
		   sb.append(base+"qtyExtern="+lineSeparator);
		   sb.append(base+"unitCost="+lineSeparator);
		   sb.append(base+"transport="+lineSeparator);
		   sb.append(base+"total="+cf.formatCurrency(total, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"totalCash="+cf.formatCurrency(totalCash, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(lineSeparator);

		   sb.append("step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".input.count="+this.getInputs().size()+lineSeparator);
		   total=0.0; totalCash=0.0; Double donated=0.0;
		   for (BlockInput i : this.getInputs()) {
			   total+=i.getTotal().doubleValue();
			   totalCash+=i.getTotalCash().doubleValue();
			   donated+=i.getDonated();
			   sb.append(i.testingProperties(rc));
		   }
		   base="step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".input.Sum.";
		   sb.append(base+"description="+lineSeparator);
		   sb.append(base+"unitType="+lineSeparator);
		   sb.append(base+"unitNum="+lineSeparator);
		   sb.append(base+"qtyIntern="+lineSeparator);
		   sb.append(base+"qtyExtern="+lineSeparator);
		   sb.append(base+"unitCost="+lineSeparator);
		   sb.append(base+"transport="+lineSeparator);
		   sb.append(base+"total="+cf.formatCurrency(total, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"donated="+cf.formatCurrency(donated, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"totalCash="+cf.formatCurrency(totalCash, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(lineSeparator);
		   
		   sb.append("step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".labour.count="+this.getLabours().size()+lineSeparator);
		   total=0.0; totalCash=0.0; donated=0.0;
		   for (BlockLabour i : this.getLabours()) {
			   total+=i.getTotal().doubleValue();
			   totalCash+=i.getTotalCash().doubleValue();
			   donated+=i.getDonated();
			   sb.append(i.testingProperties(rc));
		   }
		   base="step9."+getPropertiesType()+"."+(this.getOrderBy()+1)+".labour.Sum.";
		   sb.append(base+"description="+lineSeparator);
		   sb.append(base+"unitType="+lineSeparator);
		   sb.append(base+"unitNum="+lineSeparator);
		   sb.append(base+"qtyIntern="+lineSeparator);
		   sb.append(base+"qtyExtern="+lineSeparator);
		   sb.append(base+"unitCost="+lineSeparator);
		   sb.append(base+"total="+cf.formatCurrency(total, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"donated="+cf.formatCurrency(donated, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(base+"totalCash="+cf.formatCurrency(totalCash, CurrencyFormat.ALL)+lineSeparator);
		   sb.append(lineSeparator);
		   
		   return sb.toString();
	   }

    // non-property accessors
    /**
     * Calculates the block or activity's total income by cycling through the income collection
     * @return total income
     */
    public BigDecimal getTotalIncome() {
    	BigDecimal income=new BigDecimal(0);
    	for (BlockIncome inc:incomes) {
    		income=income.add(inc.getTotal());
    	}
    	return income;
    }
    /**
     * Calculates the block or activity's total cash income by cycling through the income collection
     * @return total cash income
     */
    public BigDecimal getTotalCashIncome() {
    	BigDecimal income=new BigDecimal(0);
    	for (BlockIncome inc:incomes) {
    		income=income.add(inc.getTotalCash());
    	}
    	return income;
    }
    public BigDecimal getTotalCashIncomeWithoutTransport() {
    	BigDecimal income=new BigDecimal(0);
    	for (BlockIncome inc:incomes) {
    		income=income.add(inc.getTotalCashIncomeWithoutTransport());
    	}
    	return income;
    }
    public BigDecimal getTotalCashOnlyIncomeTransportCost() {
    	BigDecimal income=new BigDecimal(0);
    	for (BlockIncome inc:incomes) {
    		income=income.add(inc.getTotalCashIncomeOnlyTransportCost());
    	}
    	return income;
    }
    
    /**
     * Calculates the block or activity's total cost by cycling through the input and labour collections
     * @return total cost
     */
    public BigDecimal getTotalCost() {
    	BigDecimal cost=new BigDecimal(0);
    	for (BlockInput inp:inputs)
    		cost=cost.add(inp.getTotal());
    	for (BlockLabour lab:labours)
    		cost=cost.add(lab.getTotal());  	
    	return cost;
    }
    /**
     * Calculates the block or activity's total cash cost by cycling through the input and labour collections
     * @return total cash cost
     */
    public BigDecimal getTotalCashCost() {
    	BigDecimal cost=new BigDecimal(0);
    	for (BlockInput inp:inputs)
    		cost=cost.add(inp.getTotalCash());
    	for (BlockLabour lab:labours)
    		cost=cost.add(lab.getTotalCash());
    	return cost;
    }
    
    public List<double[]> getBlockSummary() {
    	List<double[]> list = new ArrayList<double[]>();
    	double[] incomes = new double[this.getProject().getDuration()];
    	double[] costs = new double[this.getProject().getDuration()];
    	double[] totals = new double[this.getProject().getDuration()];
    	double[] cumulative =  new double[this.getProject().getDuration()];
    	
    	for (BlockPattern pat : this.getPatterns().values()) {
    		int year = pat.getYearNum();
	    		if (year<=this.getProject().getDuration()) {
	    		if (year==1 && this.getProject().getIncomeGen()) {
	    			incomes[0]=getTotalIncome().doubleValue()*getCycleFirstYearIncome()*pat.getQty();
					costs[0]=getTotalCost().doubleValue()*getCycleFirstYear()*pat.getQty();
	    		} else {
	    			incomes[year-1]=getTotalIncome().doubleValue()*getCyclePerYear()*pat.getQty();
	    			costs[year-1]=getTotalCost().doubleValue()*getCyclePerYear()*pat.getQty();
	    		}
	    		totals[year-1]=incomes[year-1]-costs[year-1];
	    		cumulative[year-1]=year==1 ? totals[0]:totals[year-1]+cumulative[year-2];
    		}
    	}
    	
    	list.add(incomes);
    	list.add(costs);
    	list.add(totals);
    	list.add(cumulative);
    	return list;
    }

    // Property accessors
    public java.lang.Integer getBlockId () {
        return this.BlockId;
    }
    
   public void setBlockId (java.lang.Integer BlockId) {
        this.BlockId = BlockId;
    }
    
//    public Project getProject () {
//        return this.project;
//    }
//    
//   public void setProject (Project Project) {
//        this.project = Project;
//    }
   
   public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
    /**
 * @param lengthUnit the lengthUnit to set
 */
public void setLengthUnit(Integer lengthUnit) {
	this.lengthUnit = lengthUnit;
}

/**
 * @return the lengthUnit
 */
public Integer getLengthUnit() {
	return lengthUnit;
}

	public Double getCycleLength () {
        return this.CycleLength;
    }
    
   public void setCycleLength (Double CycleLength) {
        this.CycleLength = CycleLength;
    }
    
    public Double getCyclePerYear () {
        return this.CyclePerYear;
    }
    
   public void setCyclePerYear (Double CyclePerYear) {
        this.CyclePerYear = CyclePerYear;
    }
    
    public void setCycleFirstYear(Double cycleFirstYearCosts) {
    	this.cycleFirstYearCosts = cycleFirstYearCosts;
	}
	
	public Double getCycleFirstYear() {
		return cycleFirstYearCosts;
	}
	
	public void setCycleFirstYearIncome(Double cycleFirstYearIncome) {
		this.cycleFirstYearIncome = cycleFirstYearIncome;
	}
	
	public Double getCycleFirstYearIncome() {
		return cycleFirstYearIncome;
	}
	
	public void setOrderBy(Integer orderBy) {
		OrderBy = orderBy;
	}
	
	public Integer getOrderBy() {
		return OrderBy;
	}

	public java.lang.String getUnitType () {
        return this.UnitType;
    }
    
   public void setUnitType (java.lang.String UnitType) {
        this.UnitType = UnitType;
    }

   public boolean isCycles() {
		return cycles;
	}
	
	public void setCycles(boolean cycles) {
		this.cycles = cycles;
	}

	//    public boolean isWithProject() {
//    	return withProject;
//    }
//    public void setWithProject(boolean withProject) {
//    	this.withProject=withProject;
//    }
    public void setChrons(Map<String, BlockChron> chrons) {
		this.chrons = chrons;
	}
    
    
    public abstract Project getProject();
   public abstract void setProject (Project Project);
    
    
	/**
	 * Adds a production chronology item to the block's chronology collection.  
	 * The chron's chronId property is set here according to its data.
	 * @param chron
	 */
	public void addChron(BlockChron chron) {
		chron.setBlock(this);
		chrons.put(chron.getChronId(),chron);
	}
	
	public Map<String, BlockChron> getChrons() {
		return chrons;
	}
	
	public void setPatterns(Map<Integer, BlockPattern> patterns) {
		this.patterns = patterns;
	}
	public void addPattern(BlockPattern pattern) {
		pattern.setBlock(this);
		patterns.put(pattern.getYearNum(), pattern);
	}
	public Map<Integer, BlockPattern> getPatterns() {
		return this.patterns;
	}

	public String getPatternsError() {
		return patternsError;
	}

	public void setPatternsError(String patternsError) {
		this.patternsError = patternsError;
	}

	public String getChronError() {
		return chronError;
	}

	public void setChronError(String chronError) {
		this.chronError = chronError;
	}

	public void setInputs(Set<BlockInput> profileInputs) {
		this.inputs = profileInputs;
	}

	public Set<BlockInput> getInputs() {
		return inputs;
	}
	public void addInput(BlockInput in) {
		in.setBlock(this);
		inputs.add(in);
	}
	public void setIncomes(Set<BlockIncome> profileIncomes) {
		this.incomes = profileIncomes;
	}
	
	public Set<BlockIncome> getIncomes() {
		return incomes;
	}
	
	public void addIncome(BlockIncome in) {
		in.setBlock(this);
		incomes.add(in);
	}

	public void setLabours(Set<BlockLabour> profileLabours) {
		this.labours = profileLabours;
	}

	public Set<BlockLabour> getLabours() {
		return labours;
	}
	public void addLabour(BlockLabour lab) {
		lab.setBlock(this);
		labours.add(lab);
	}
	
	/**
	 * Copies the block or activity.  Collection items are copied by value
	 * so as not to interfere with the collections of the original block or activity.
	 * @return
	 */
	public BlockBase copy(Class<? extends BlockBase> newClass) {
		BlockBase newBlock = newClass.isAssignableFrom(Block.class) ? new Block() : new BlockWithout();
		newBlock.setCycles(cycles);
		newBlock.setOrderBy(OrderBy);
		newBlock.setDescription(description);
		newBlock.setUnitType(UnitType);
		newBlock.setCycleLength(CycleLength);
		newBlock.setCyclePerYear(CyclePerYear);
		newBlock.setCycleFirstYear(cycleFirstYearCosts);
		newBlock.setCycleFirstYearIncome(cycleFirstYearIncome);
		newBlock.setLengthUnit(lengthUnit);
		newBlock.setIncomes(new HashSet<BlockIncome>());
		newBlock.setInputs(new HashSet<BlockInput>());
		newBlock.setLabours(new HashSet<BlockLabour>());
		newBlock.setChrons(new HashMap<String,BlockChron>());
		newBlock.setPatterns(new HashMap<Integer,BlockPattern>());
		
		for (BlockIncome item : this.getIncomes()) {
			newBlock.addIncome(item.copy());
		}
		
		for (BlockInput item : this.getInputs()) {
			newBlock.addInput(item.copy());
		}
		

		for (BlockLabour item : this.getLabours()) {
			newBlock.addLabour(item.copy());
		}
		
		for (BlockChron chron : this.getChrons().values()) {
			BlockChron newChron = new BlockChron();
			newChron.setChronId(chron.getChronId());
			newBlock.addChron(newChron);
		}
		
		for (BlockPattern pat : this.getPatterns().values()) {
			BlockPattern newPat = new BlockPattern();
			newPat.setQty(pat.getQty());
			newPat.setYearNum(pat.getYearNum());
			newBlock.addPattern(newPat);
		}
		return newBlock;
	}
	
	/** Override equals()
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockBase other = (BlockBase) obj;
		if (BlockId == null) {
			if (other.BlockId != null)
				return false;
		} else if (!BlockId.equals(other.BlockId))
			return false;
		if (cycleFirstYearCosts == null) {
			if (other.cycleFirstYearCosts != null)
				return false;
		} else if (!cycleFirstYearCosts.equals(other.cycleFirstYearCosts))
			return false;
		if (CycleLength == null) {
			if (other.CycleLength != null)
				return false;
		} else if (!CycleLength.equals(other.CycleLength))
			return false;
		if (CyclePerYear == null) {
			if (other.CyclePerYear != null)
				return false;
		} else if (!CyclePerYear.equals(other.CyclePerYear))
			return false;
		if (getDescription()== null) {
			if (other.getDescription() != null)
				return false;
		} else if (!getDescription().equals(other.getDescription()))
			return false;
		if (OrderBy == null) {
			if (other.OrderBy != null)
				return false;
		} else if (!OrderBy.equals(other.OrderBy))
			return false;
		if (UnitType == null) {
			if (other.UnitType != null)
				return false;
		} else if (!UnitType.equals(other.UnitType)) {
			return false;
		} 
		if (chrons == null) {
			if (other.chrons != null)
				return false;
		} else if (!chrons.equals(other.chrons))
			return false;
		if (incomes == null) {
			if (other.incomes != null)
				return false;
		} else if (!incomes.equals(other.incomes))
			return false;
		if (inputs == null) {
			if (other.inputs != null)
				return false;
		} else if (!inputs.equals(other.inputs))
			return false;
		if (labours == null) {
			if (other.labours != null)
				return false;
		} else if (!labours.equals(other.labours))
			return false;
		if (lengthUnit == null) {
			if (other.lengthUnit != null)
				return false;
		} else if (!lengthUnit.equals(other.lengthUnit))
			return false;
		if (patterns == null) {
			if (other.patterns != null)
				return false;
		} else if (!patterns.equals(other.patterns))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		int hash = 0;	
		if (getDescription()!=null)
			hash = getDescription().hashCode();
		return hash;
	}


	public int compareTo(OrderByable i) {
		if (this==i) return 0;
		int compare = this.getOrderBy() - i.getOrderBy();
		if(compare == 0) return 1;
		return compare; 
	}
	
}
