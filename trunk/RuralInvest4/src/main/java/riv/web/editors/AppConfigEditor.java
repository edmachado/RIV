package riv.web.editors;

import java.beans.PropertyEditorSupport;

import riv.web.config.RivConfig;
import riv.objects.config.*;

public class AppConfigEditor extends PropertyEditorSupport {

	private RivConfig rivConfig; 
	@SuppressWarnings("rawtypes")
	private Class appConfigType;

	public AppConfigEditor(RivConfig rivConfig, @SuppressWarnings("rawtypes") Class appConfigType) {
		this.rivConfig = rivConfig; 
		this.appConfigType=appConfigType;
	}

	@Override
	public String getAsText() {
		if (getValue()==null) { return null; }
		String id=null;
		if (appConfigType.equals(FieldOffice.class)) {
			id = ((FieldOffice) getValue()).getConfigId().toString();
		} else if (appConfigType.equals(ProjectCategory.class)) {
			id = ((ProjectCategory) getValue()).getConfigId().toString();
		} else if (appConfigType.equals(Beneficiary.class)) {
			id = ((Beneficiary) getValue()).getConfigId().toString();
		} else if (appConfigType.equals(Status.class)){
			id = ((Status) getValue()).getConfigId().toString();
		} else if (appConfigType.equals(EnviroCategory.class)) {
			id = ((EnviroCategory)getValue()).getConfigId().toString();
		} else if (appConfigType.equals(AppConfig1.class)){
			id = ((AppConfig1)getValue()).getConfigId().toString();
		} else if (appConfigType.equals(AppConfig2.class)){
			id = ((AppConfig2)getValue()).getConfigId().toString();
		}
		
		return id;
	}

	@Override
	public void setAsText(String id) throws IllegalArgumentException {
		AppConfig ac=null;
		
		if (appConfigType.equals(FieldOffice.class)) {
			ac = rivConfig.getFieldOffices().get(Integer.valueOf(id));
		} else if (appConfigType.equals(ProjectCategory.class)) {
			ac = rivConfig.getCategories().get(Integer.valueOf(id));
		} else if (appConfigType.equals(Beneficiary.class)) {
			ac = rivConfig.getBeneficiaries().get(Integer.valueOf(id));
		} else if (appConfigType.equals(Status.class)){
			ac = rivConfig.getStatuses().get(Integer.valueOf(id));
		} else if (appConfigType.equals(EnviroCategory.class)) {
			ac = rivConfig.getEnviroCategories().get(Integer.valueOf(id));
		} else if (appConfigType.equals(AppConfig1.class)){
			ac = rivConfig.getAppConfig1s().get(Integer.valueOf(id));
		} else if (appConfigType.equals(AppConfig2.class)){
			ac = rivConfig.getAppConfig2s().get(Integer.valueOf(id));
		}

		setValue(ac);
	}

}