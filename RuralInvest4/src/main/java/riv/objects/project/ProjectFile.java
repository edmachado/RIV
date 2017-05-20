package riv.objects.project;

import java.io.Serializable;
import java.sql.Blob;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import riv.objects.AttachedFile;


@Entity
@Table(name="PROJECT_FILE", uniqueConstraints = {@UniqueConstraint(columnNames={"project_id", "filename"})})
public class ProjectFile extends AttachedFile implements Serializable {
	static final Logger LOG = LoggerFactory.getLogger(ProjectFile.class);
	private static final long serialVersionUID = 5336919910060825578L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="ID", nullable = false)
	private Integer id;
	
	@Column
	private String filename;
	
	@Column
	private long length;
	
	@Lob
	@Column(name="CONTENT", length = 5242880)
	@Basic(fetch = FetchType.LAZY)
	private Blob content;
	
	@Transient
	private byte[] contentBytes;
	
	@Column(name="PROJECT_ID", nullable=false)
	private int probaseId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public Blob getContent() {
		return content;
	}

	public void setContent(Blob content) {
		this.content = content;
	}
	
	public byte[] getContentBytes() {
		return contentBytes;
	}

	public void setContentBytes(byte[] contentBytes) {
		this.contentBytes = contentBytes;
	}

	public int getProbaseId() {
		return probaseId;
	}

	public void setProbaseId(int projectId) {
		this.probaseId = projectId;
	}
	
}