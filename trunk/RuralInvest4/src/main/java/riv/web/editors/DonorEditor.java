package riv.web.editors;

import java.beans.PropertyEditorSupport;

import riv.objects.project.Donor;
import riv.web.service.DataService;

public class DonorEditor extends PropertyEditorSupport {
	private DataService dataService;
	
	public DonorEditor(DataService dataService) {
		this.dataService=dataService;
	}

	@Override
	public String getAsText() {
		if (getValue()==null) { return null; }
		return ((Donor)getValue()).getDonorId().toString();
	}

	@Override
	public void setAsText(String id) throws IllegalArgumentException {
		Donor d = dataService.getDonor(Integer.parseInt(id));
		setValue(d);	
	}
}