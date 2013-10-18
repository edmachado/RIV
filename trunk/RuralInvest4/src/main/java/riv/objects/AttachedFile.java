package riv.objects;

import java.sql.Blob;

public abstract class AttachedFile extends ExportFile {
	public abstract Integer getId();

	public abstract void setId(Integer id) ;

	public abstract String getFilename();

	public abstract void setFilename(String filename);

	public abstract long getLength();

	public abstract void setLength(long length);

	public abstract Blob getContent();

	public abstract void setContent(Blob content);
	
	public abstract byte[] getContentBytes();

	public abstract void setContentBytes(byte[] contentBytes);
	
	public abstract int getProbaseId();
	
	public abstract void setProbaseId(int id);
}