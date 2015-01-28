package riv.objects.project;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;

/**
 * A chronology item associated with a project's block.  
 * Specifies whether in a particular half-month period of the year a block activity is expected to take place.
 * Key is composed X-XX-X, chronType-monthNum-firstPart. See property getters for further documentation.
 * @author Bar Zecharya
 *
 */
@Entity
@Table(name="PROJECT_BLOCK_CHRON")
public class BlockChron implements Serializable {
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="BLOCK_ID", nullable=false)
	private BlockBase block;
	@Id
	@Column(name="CHRON_ID")
	private String chronId;
	
	// neccessary for hibernate mapping
	@NaturalId
	@Column(name="CHRONS_KEY")
	private Long chronsKey;
	
//	public BlockChron() {}
//	public BlockChron(String key) {
//		BlockChron c = new BlockChron();
//		c.setChronId(key);
//	}
	
	
	public void setBlock(BlockBase block) {
		this.block = block;
	}
	public BlockBase getBlock() {
		return block;
	}
	
	public String getChronId() {
		return chronId;
	}
	public void setChronId(String chronId) {
		this.chronId = chronId;
	}
	
	public Long getChronsKey() {
		return chronsKey;
	}
	public void setChronsKey(Long chronsKey) {
		this.chronsKey = chronsKey;
	}
	
	/**
	 * Gets the zero-indexed number representing that the chronology item takes place in the nth month following the
	 * beginning of the project.  Thus a chronology item for October in a project that begins in September
	 * would be "1".
	 * @return the zero-indexed month number
	 */
	public int getMonthNum() {
		String num = getChronId().substring(getChronId().indexOf('-')+1, getChronId().lastIndexOf('-'));
		return Integer.parseInt(num);
	}
	/**
	 * Gets the type of activity this chronology item represents, as follows.<br/>
	 * 0 = Production<br/>1 = Harvest</br>2 = Payment<br/><br/>
	 * Future revisions may want to use an Enum instead of an integer.
	 * @return the activity type
	 */
	public int getChronType() {
		return Integer.parseInt(getChronId().substring(0, 1));
	}
	/**
	 * Gets whether the chronology item refers to the first (true) or second (false)
	 * half of the month.
	 * @return whether the item takes place in the first half of the month
	 */
	public boolean isFirstPart() {
		int lastSegment = Integer.parseInt(getChronId().substring(getChronId().length()-1, getChronId().length()));
		return lastSegment==0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((block == null) ? 0 : block.hashCode());
		result = prime * result + ((chronId == null) ? 0 : chronId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockChron other = (BlockChron) obj;
		if (block == null) {
			if (other.block != null)
				return false;
		} else if (!block.equals(other.block))
			return false;
		if (chronId == null) {
			if (other.chronId != null)
				return false;
		} else if (!chronId.equals(other.chronId))
			return false;
		return true;
	}
}
