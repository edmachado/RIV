package riv.web.editors;

import java.beans.PropertyEditorSupport;
import java.util.List;

import riv.web.service.DataService;

public class UserCollectionEditor extends PropertyEditorSupport {
	private DataService dataService;
	
	@SuppressWarnings("rawtypes")
	public UserCollectionEditor(DataService dataService, Class collectionType) { 
        super(collectionType);
        this.dataService=dataService;
    }
 
    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public void setAsText(String text) throws IllegalArgumentException {
        Object obj = getValue();
        List list = (List) obj;
        for (String str : text.split(",")) {
        	list.add(dataService.getUser(Integer.valueOf(str)));
        }
    }
 
    @Override
    public String getAsText() {
    	 return super.getAsText();
    }
}