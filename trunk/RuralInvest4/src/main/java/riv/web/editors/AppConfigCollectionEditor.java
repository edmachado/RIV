package riv.web.editors;

import java.beans.PropertyEditorSupport;
import java.util.List;

import riv.objects.config.AppConfig1;
import riv.objects.config.AppConfig2;
import riv.objects.config.Beneficiary;
import riv.objects.config.EnviroCategory;
import riv.objects.config.FieldOffice;
import riv.objects.config.ProjectCategory;
import riv.objects.config.Status;
import riv.objects.config.User;
import riv.web.config.RivConfig;

public class AppConfigCollectionEditor extends PropertyEditorSupport {
	private RivConfig rivConfig;
	@SuppressWarnings("rawtypes")
	private Class appConfigType;
 
	@SuppressWarnings("rawtypes")
	public AppConfigCollectionEditor(RivConfig rivConfig, Class collectionType, Class appConfigType) { 
        super(collectionType);
        this.rivConfig=rivConfig;
        this.appConfigType=appConfigType;
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void setAsText(String text) throws IllegalArgumentException {
        Object obj = getValue();
        List list = (List) obj;
        for (String str : text.split(",")) {
        	if (appConfigType.equals(FieldOffice.class)) {
        		list.add(rivConfig.getFieldOffices().get(Integer.valueOf(str)));
        	} else if (appConfigType.equals(ProjectCategory.class)) {
        		list.add(rivConfig.getCategories().get(Integer.valueOf(str)));
        	} else if (appConfigType.equals(Beneficiary.class)) {
        		list.add(rivConfig.getBeneficiaries().get(Integer.valueOf(str)));
        	} else if (appConfigType.equals(Status.class)) {
        		list.add(rivConfig.getStatuses().get(Integer.valueOf(str)));
        	} else if (appConfigType.equals(EnviroCategory.class)) {
        		list.add(rivConfig.getEnviroCategories().get(Integer.valueOf(str)));
        	} else if (appConfigType.equals(AppConfig1.class)) {
        		list.add(rivConfig.getAppConfig1s().get(Integer.valueOf(str)));
        	} else if (appConfigType.equals(AppConfig2.class)) {
        		list.add(rivConfig.getAppConfig2s().get(Integer.valueOf(str)));
        	} else if (appConfigType.equals(User.class)) {
        		list.add(rivConfig.getUsers().get(Integer.valueOf(str)));
        	}
        }
    }
 
    @Override
    public String getAsText() {
    	 return super.getAsText();
    }
}