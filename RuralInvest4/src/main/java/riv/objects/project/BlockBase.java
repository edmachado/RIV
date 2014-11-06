package riv.objects.project;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
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

import org.hibernate.annotations.Where;

import riv.objects.OrderByable;
import riv.objects.ProductOrBlock;

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
	private Integer CyclePerYear;
	@Column(name="CYCLE_FIRST_YEAR")
	private Integer cycleFirstYearCosts;
	@Column(name="CYCLE_FIRST_YEAR_INCOME")
	private Integer cycleFirstYearIncome;
	@Column(name="UNIT_TYPE")	
	private String UnitType;
	@Column(name="CYCLES")
	private boolean cycles=true;
//	@Column(name="WITH_PROJECT")
//	private boolean withProject;

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
	
	// default constructor. Initializes collections 
    public BlockBase() {
    	this.chrons = new HashMap <String, BlockChron>();
    	this.patterns =new HashMap<Integer, BlockPattern>();
    	this.labours=new HashSet<BlockLabour>();
    	this.incomes=new HashSet<BlockIncome>();
    	this.inputs=new HashSet<BlockInput>();
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
    
    public Integer getCyclePerYear () {
        return this.CyclePerYear;
    }
    
   public void setCyclePerYear (Integer CyclePerYear) {
        this.CyclePerYear = CyclePerYear;
    }
    
    public void setCycleFirstYear(Integer cycleFirstYearCosts) {
    	this.cycleFirstYearCosts = cycleFirstYearCosts;
	}
	
	public Integer getCycleFirstYear() {
		return cycleFirstYearCosts;
	}
	
	public void setCycleFirstYearIncome(Integer cycleFirstYearIncome) {
		this.cycleFirstYearIncome = cycleFirstYearIncome;
	}
	
	public Integer getCycleFirstYearIncome() {
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
	public BlockBase copy() {
		BlockBase newBlock = this.getClass()==Block.class ? new Block() : new BlockWithout();
		newBlock.setOrderBy(this.getOrderBy());
		newBlock.setDescription(this.getDescription());
		newBlock.setUnitType(this.getUnitType());
		newBlock.setCycleLength(this.getCycleLength());
		newBlock.setCyclePerYear(this.getCyclePerYear());
		newBlock.setCycleFirstYear(this.getCycleFirstYear());
		newBlock.setCycleFirstYearIncome(cycleFirstYearIncome);
		newBlock.setLengthUnit(this.getLengthUnit());
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