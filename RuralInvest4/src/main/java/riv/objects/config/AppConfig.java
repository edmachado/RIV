package riv.objects.config;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @author Bar Zecharya
 * @see Beneficiary
 * Superclass that represents application configuration tables.
 */
@Entity
@Table(name="APP_CONFIG")
@Inheritance (strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn (name="CLASS", discriminatorType=DiscriminatorType.STRING)
public abstract class AppConfig implements Serializable {
	private static final long serialVersionUID = 3432446162198495627L;
	
	@Id
	@Column(name="CONFIG_ID", nullable = false)
	private Integer configId;
	@Size(max = 50)
	@NotEmpty
	private String description;

	public void setConfigId(Integer configId) {
		this.configId = configId;
	}

	public Integer getConfigId() {
		return configId;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configId == null) ? 0 : configId.hashCode());
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
		AppConfig other = (AppConfig) obj;
		if (configId == null) {
			if (other.configId != null)
				return false;
		} else if (!configId.equals(other.configId))
			return false;
		return true;
	}
	
	
}
