	package riv.web.controller;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import riv.objects.profile.ProfileItemGeneral;
import riv.objects.profile.ProfileItemGeneralWithout;
import riv.objects.profile.ProfileItemGood;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileProductIncome;
import riv.objects.profile.ProfileProductInput;
import riv.objects.profile.ProfileProductLabour;
import riv.util.ExcelImportException;
import riv.util.XlsImportTable;
import riv.util.validators.ProfileItemValidator;
import riv.util.validators.ProfileProductItemValidator;
import riv.util.validators.ProjectItemValidator;
import riv.web.service.DataService;
 
@Controller
public class ExcelImportController {
	static final Logger LOG = LoggerFactory.getLogger(ExcelImportController.class);
	
	@Autowired
    private DataService dataService;
	@Autowired
	private MessageSource messageSource;
	
	@RequestMapping(value="/import/project/{type}/{id}", method=RequestMethod.POST)
	public @ResponseBody String projectImport(@PathVariable Integer id,@PathVariable String type, MultipartHttpServletRequest request) {
		Iterator<String> itr =  request.getFileNames();
		MultipartFile mpf = request.getFile(itr.next());
		
		String error=null;
		try {
			if (type.equals("invest")) {
				importProjectInvest(id, mpf.getInputStream());
			} else if (type.equals("general")) {
//				importProjectGeneral(id, mpf.getInputStream());
			} else if (type.equals("product")) {
//				importBlock(id, mpf.getInputStream());
			}
		} catch (IOException e) {
			error = "import.error.excel.read";
		} catch (ExcelImportException e) {
			error = e.getMessage();
		}
		
		if (error==null) {
			return "{\"success\": \"success\"}";
		} else {
			return "{\"error\": \""+error+"\"}";
		}
	}
	
	private void importProjectInvest(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook=null;
		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			throw new ExcelImportException(messageSource.getMessage("import.error.excel.read", new Object[0], LocaleContextHolder.getLocale()));
		}
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		ProjectItemValidator validator = new ProjectItemValidator();
		
		
//		// goods and materials table
//		XlsImportTable<ProfileItemGood> table = new XlsImportTable<ProfileItemGood>(ProfileItemGood.class, rowNum, 3, validator)
//				.addColumn(0, "description", false)
//				.addColumn(1, "unitType", false)
//				.addColumn(2,"unitNum", true)
//				.addColumn(3, "unitCost", true)
//				.addColumn(5, "ownResource", true)
//				.addColumn(7, "econLife", true)
//				.addColumn(8, "salvage", true);
//		
//		List<ProfileItemGood> goods = table.readTable(sheet, messageSource);
//		
//		rowNum = goods.size()+6;
//	
//		// labours table
//		XlsImportTable<ProfileItemLabour> labourTable = new XlsImportTable<ProfileItemLabour>(ProfileItemLabour.class, rowNum,  3, validator)
//				.addColumn(0, "description", false)
//				.addColumn(1, "unitType", false)
//				.addColumn(2,"unitNum", true)
//				.addColumn(3, "unitCost", true)
//				.addColumn(5, "ownResource", true);
//		List<ProfileItemLabour> labours = labourTable.readTable(sheet, messageSource);
//		
//		dataService.replaceProfileInvest(id, goods, labours);
//		
//		try {
//			file.close();
//		} catch (IOException e) {
//			throw new ExcelImportException(e.getMessage());
//		}
	}
	
	
	@RequestMapping(value="/import/profile/{type}/{id}", method=RequestMethod.POST)
	public @ResponseBody String profileImport(@PathVariable Integer id,@PathVariable String type, MultipartHttpServletRequest request) {
		Iterator<String> itr =  request.getFileNames();
		MultipartFile mpf = request.getFile(itr.next());
		
		String error=null;
		try {
			if (type.equals("invest")) {
				importProfileInvest(id, mpf.getInputStream());
			} else if (type.equals("general")) {
				importProfileGeneral(id, mpf.getInputStream());
			} else if (type.equals("product")) {
				importProfileProduct(id, mpf.getInputStream());
			}
		} catch (IOException e) {
			error = "import.error.excel.read";
		} catch (ExcelImportException e) {
			error = e.getMessage();
		}
		
		if (error==null) {
			return "{\"success\": \"success\"}";
		} else {
			return "{\"error\": \""+error+"\"}";
		}
	}
	
	private void importProfileProduct(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook=null;
		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			throw new ExcelImportException(messageSource.getMessage("import.error.excel.read", new Object[0], LocaleContextHolder.getLocale()));
		}
		
		ProfileProductItemValidator validator = new ProfileProductItemValidator();
		XlsImportTable<ProfileProductIncome> tableInc = new XlsImportTable<ProfileProductIncome>(ProfileProductIncome.class, 0, 3, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(4, "transport", true);
		List<ProfileProductIncome> incs = tableInc.readTable(workbook.getSheetAt(0), messageSource);
		
		XlsImportTable<ProfileProductInput> tableInp = new XlsImportTable<ProfileProductInput>(ProfileProductInput.class, 0, 3, new ProfileProductItemValidator())
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(4, "transport", true);
		List<ProfileProductInput> inps = tableInp.readTable(workbook.getSheetAt(0), messageSource);
		
		XlsImportTable<ProfileProductLabour> tableLab = new XlsImportTable<ProfileProductLabour>(ProfileProductLabour.class, 0, 3, new ProfileProductItemValidator())
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true);
		List<ProfileProductLabour> labs = tableLab.readTable(workbook.getSheetAt(0), messageSource);
		
		dataService.replaceProfileProduct(id, incs, inps, labs);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}
	}
	
	private void importProfileGeneral(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook=null;
		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			throw new ExcelImportException(messageSource.getMessage("import.error.excel.read", new Object[0], LocaleContextHolder.getLocale()));
		}
		
		XlsImportTable<ProfileItemGeneral> table = new XlsImportTable<ProfileItemGeneral>(ProfileItemGeneral.class, 0, 3, new ProfileItemValidator())
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true);
		
		List<ProfileItemGeneral> generals = table.readTable(workbook.getSheetAt(0), messageSource);
		
		XlsImportTable<ProfileItemGeneralWithout> tableWo = new XlsImportTable<ProfileItemGeneralWithout>(ProfileItemGeneralWithout.class, 0, 3, new ProfileItemValidator())
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true);
		List<ProfileItemGeneralWithout> generalsWithout = tableWo.readTable(workbook.getSheetAt(1), messageSource);
		dataService.replaceProfileGeneral(id, generals, generalsWithout);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}
	}
	
	private void importProfileInvest(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook=null;
		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			throw new ExcelImportException(messageSource.getMessage("import.error.excel.read", new Object[0], LocaleContextHolder.getLocale()));
		}
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		ProfileItemValidator validator = new ProfileItemValidator();
		// goods and materials table
		XlsImportTable<ProfileItemGood> table = new XlsImportTable<ProfileItemGood>(ProfileItemGood.class, rowNum, 3, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "ownResource", true)
				.addColumn(7, "econLife", true)
				.addColumn(8, "salvage", true);
		
		List<ProfileItemGood> goods = table.readTable(sheet, messageSource);
		
		rowNum = goods.size()+6;
	
		// labours table
		XlsImportTable<ProfileItemLabour> labourTable = new XlsImportTable<ProfileItemLabour>(ProfileItemLabour.class, rowNum,  3, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "ownResource", true);
		List<ProfileItemLabour> labours = labourTable.readTable(sheet, messageSource);
		
		dataService.replaceProfileInvest(id, goods, labours);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}
	}
}