package riv.objects.config;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

@Entity
@Table(name="VERSION")
public class Version  {
	@Id
	private double version;
	@Column(name="DESCRIPTION", nullable = false)
	@Size(max=255)
	private String description;
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	private Date installTime;
	@Column
	private boolean recalculate;
	
	public void setVersion(double version) {
		this.version = version;
	}
	public double getVersion() {
		return version;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public void setInstallTime(Date install_time) {
		this.installTime = install_time;
	}
	public Date getInstallTime() {
		return installTime;
	}
	public void setRecalculate(boolean recalculate) {
		this.recalculate = recalculate;
	}
	public boolean isRecalculate() {
		return recalculate;
	}
}