package riv.objects;

import riv.objects.reference.ReferenceItem;

/**
 * @author Bar Zecharya
 *
 */
public interface LinkedToable extends HasProbase {
	public ReferenceItem getLinkedTo();
	public void setLinkedTo(ReferenceItem refItem);
	public Integer getExportLinkedTo();
	public void setExportLinkedTo(Integer exportLinkedTo);
}
