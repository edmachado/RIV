package riv.web.controller;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Workbook;
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

import riv.objects.profile.Profile;
import riv.objects.profile.ProfileItemGeneral;
import riv.objects.profile.ProfileItemGeneralWithout;
import riv.objects.profile.ProfileItemGood;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileProductBase;
import riv.objects.profile.ProfileProductIncome;
import riv.objects.profile.ProfileProductInput;
import riv.objects.profile.ProfileProductLabour;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockIncome;
import riv.objects.project.BlockInput;
import riv.objects.project.BlockLabour;
import riv.objects.project.Project;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemAssetWithout;
import riv.objects.project.ProjectItemContribution;
import riv.objects.project.ProjectItemGeneral;
import riv.objects.project.ProjectItemGeneralWithout;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemLabourWithout;
import riv.objects.project.ProjectItemNongenLabour;
import riv.objects.project.ProjectItemNongenMaintenance;
import riv.objects.project.ProjectItemNongenMaterials;
import riv.objects.project.ProjectItemPersonnel;
import riv.objects.project.ProjectItemPersonnelWithout;
import riv.objects.project.ProjectItemService;
import riv.objects.project.ProjectItemServiceWithout;
import riv.util.ExcelImportException;
import riv.util.XlsImportTable;
import riv.util.validators.BlockItemValidator;
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
				importProjectGeneral(id, mpf.getInputStream());
			} else if (type.equals("generalNongen")) {
				importProjectGeneralNongen(id, mpf.getInputStream());
			} else if (type.equals("block")) {
				importBlock(id, mpf.getInputStream());
			} else if (type.equals("contribution")) {
				importContribution(id, mpf.getInputStream());
			}
		} catch (IOException e) {
			error = "import.error.excel.read";
		} catch (ExcelImportException e) {
			e.printStackTrace(System.out);
			error = e.getMessage();
		}
		
		if (error==null) {
			return "{\"success\": \"success\"}";
		} else {
			return "{\"error\": \""+error+"\"}";
		}
	}
	
	private void importBlock(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = getWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		BlockBase b = dataService.getBlock(id);
		BlockItemValidator validator = new BlockItemValidator();
		validator.setIncomeGen(b.getProject().getIncomeGen());
		
		XlsImportTable<BlockIncome> tableInc = new XlsImportTable<BlockIncome>(BlockIncome.class, rowNum, 7, validator);
		if (b.getProject().getIncomeGen()) {
			tableInc.addColumn(0, "description", false)
			.addColumn(1, "unitType", false)
			.addColumn(2, "unitNum", true)
			.addColumn(3, "qtyIntern", true)
			.addColumn(5, "unitCost", true)
			.addColumn(6, "transport", true);
		} else {
			tableInc.addColumn(0, "description", false)
			.addColumn(1, "unitType", false)
			.addColumn(2, "unitNum", true)
			.addColumn(5, "unitCost", true);
		}
		List<BlockIncome> incs = tableInc.readTable(sheet, messageSource);
		rowNum = incs.size()+3;
		
		XlsImportTable<BlockInput> tableInp = new XlsImportTable<BlockInput>(BlockInput.class, rowNum, 7, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "qtyIntern", true)
				.addColumn(5, "unitCost", true)
				.addColumn(6, "transport", true);
		List<BlockInput> inps = tableInp.readTable(sheet, messageSource);
		rowNum = rowNum+inps.size()+4;
		
		XlsImportTable<BlockLabour> tableLab = new XlsImportTable<BlockLabour>(BlockLabour.class, rowNum, 7, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitNum", true)
				.addColumn(3, "qtyIntern", true)
				.addColumn(5, "unitCost", true);
		List<BlockLabour> labs = tableLab.readTable(sheet, messageSource);
		rowNum = labs.size()+3;
		
		dataService.replaceBlock(id, incs, inps, labs);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}	
	}
	
	private void importContribution(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = getWorkbook(file);
		
		ProjectItemValidator validator = new ProjectItemValidator();
		Project p = dataService.getProject(id, 10);
		validator.setIncomeGen(false);
		validator.setDuration(p.getDuration()); 
		
		List<ProjectItemContribution> items = new ArrayList<ProjectItemContribution>();
		XlsImportTable<ProjectItemContribution> table = new XlsImportTable<ProjectItemContribution>(ProjectItemContribution.class, 0, 5, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "contribType", contribTypes())
				.addColumn(2, "unitType", false)
				.addColumn(3, "unitNum", true)
				.addColumn(4, "unitCost", true);
		
		for (int year=1; year<=p.getDuration();year++) {
			table.setStartRow(2+items.size()+(year-1)*4);
			List<ProjectItemContribution> yearItems = table.readTable(workbook.getSheetAt(0), messageSource);
			for (ProjectItemContribution i : yearItems) {
				i.setYear(year);
			}
			items.addAll(yearItems);
			yearItems.clear();
		}
		dataService.replaceProjectContribution(id, items);
	}
	
	private void importProjectGeneralNongen(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = getWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		ProjectItemValidator validator = new ProjectItemValidator();
		
		Project p = dataService.getProject(id, 1);
		validator.setIncomeGen(false);
		validator.setDuration(p.getDuration());
	
		XlsImportTable<ProjectItemNongenMaterials> tableMaterials = new XlsImportTable<ProjectItemNongenMaterials>(ProjectItemNongenMaterials.class, rowNum, 7, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "statePublic", true)
				.addColumn(6, "other1", true);
		List<ProjectItemNongenMaterials> materials = tableMaterials.readTable(sheet, messageSource);
		rowNum = materials.size()+6;
		
		XlsImportTable<ProjectItemNongenLabour> tableLabour = new XlsImportTable<ProjectItemNongenLabour>(ProjectItemNongenLabour.class, rowNum, 7, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "statePublic", true)
				.addColumn(6, "other1", true);
		List<ProjectItemNongenLabour> labours = tableLabour.readTable(sheet, messageSource);
		rowNum = rowNum+labours.size()+3;
		
		XlsImportTable<ProjectItemNongenMaintenance> tableMaintenance = new XlsImportTable<ProjectItemNongenMaintenance>(ProjectItemNongenMaintenance.class, rowNum, 7, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "statePublic", true)
				.addColumn(6, "other1", true);
		List<ProjectItemNongenMaintenance> maints = tableMaintenance.readTable(sheet, messageSource);
		
		dataService.replaceProjectGeneralNongen(id, materials, labours, maints);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}	
	}
	
	private void importProjectGeneral(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = getWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		ProjectItemValidator validator = new ProjectItemValidator();
		
		Project p = dataService.getProject(id, 1);
		validator.setIncomeGen(true);
		validator.setDuration(p.getDuration());
	
		// supplies
		XlsImportTable<ProjectItemGeneral> tableGen = new XlsImportTable<ProjectItemGeneral>(ProjectItemGeneral.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "ownResources", true);
		List<ProjectItemGeneral> gensWith = tableGen.readTable(sheet, messageSource);
		rowNum = gensWith.size()+6;
		
		// personnel
		XlsImportTable<ProjectItemPersonnel> tablePers = new XlsImportTable<ProjectItemPersonnel>(ProjectItemPersonnel.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "ownResources", true);
		List<ProjectItemPersonnel> persWith = tablePers.readTable(sheet, messageSource);
		rowNum = rowNum+persWith.size()+6;
		
		// supplies Without
		//rowNum=rowNum+;
		XlsImportTable<ProjectItemGeneralWithout> tableGenWithout = new XlsImportTable<ProjectItemGeneralWithout>(ProjectItemGeneralWithout.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "ownResources", true);
		List<ProjectItemGeneralWithout> gensWithout = tableGenWithout.readTable(sheet, messageSource);
		rowNum = rowNum+gensWithout.size()+6;
		
		// personnel Without
		XlsImportTable<ProjectItemPersonnelWithout> tablePersWithout = new XlsImportTable<ProjectItemPersonnelWithout>(ProjectItemPersonnelWithout.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5, "ownResources", true);
		List<ProjectItemPersonnelWithout> persWithout = tablePersWithout.readTable(sheet, messageSource);	
		
		dataService.replaceProjectGeneral(id, gensWith, persWith, gensWithout, persWithout);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}		
	}
	
	
	private void importProjectInvest(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = getWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheetWith = workbook.getSheetAt(0);
		ProjectItemValidator validator = new ProjectItemValidator();
		
		Project p = dataService.getProject(id, 1);
		validator.setIncomeGen(p.getIncomeGen());
		validator.setDuration(p.getDuration());
		
		// assets
		XlsImportTable<ProjectItemAsset> tableAssets = new XlsImportTable<ProjectItemAsset>(ProjectItemAsset.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5,  "donated", true)
				.addColumn(6, "ownResources", true)
				.addColumn(8, "econLife", true)
				.addColumn(9, "maintCost", true)
				.addColumn(10, "salvage", true)
				.addBooleanColumn(11, "replace")
				.addColumn(12, "yearBegin", true);
		List<ProjectItemAsset> assetsWith = tableAssets.readTable(sheetWith, messageSource);
		rowNum = assetsWith.size()+6;
		
		// labour items
		XlsImportTable<ProjectItemLabour> tableLabour = new XlsImportTable<ProjectItemLabour>(ProjectItemLabour.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5,  "donated", true)
				.addColumn(6, "ownResources", true)
				.addColumn(12, "yearBegin", true);
		List<ProjectItemLabour> laboursWith = tableLabour.readTable(sheetWith, messageSource);
		rowNum = rowNum+laboursWith.size()+3;
		
		// services
		XlsImportTable<ProjectItemService> tableService = new XlsImportTable<ProjectItemService>(ProjectItemService.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5,  "donated", true)
				.addColumn(6, "ownResources", true)
				.addColumn(12, "yearBegin", true);
		List<ProjectItemService> servicesWith = tableService.readTable(sheetWith, messageSource);
		
		List<ProjectItemAssetWithout> assetsWithout=null;
		List<ProjectItemLabourWithout> laboursWithout=null;
		List<ProjectItemServiceWithout> servicesWithout=null;
		if (p.getIncomeGen()) {
			rowNum=0;
			XSSFSheet sheetWithout = workbook.getSheetAt(1);
			
			// assets
			XlsImportTable<ProjectItemAssetWithout> tableAssetsWo = new XlsImportTable<ProjectItemAssetWithout>(ProjectItemAssetWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2, "unitNum", true)
					.addColumn(3, "unitCost", true)
					.addColumn(5,  "donated", true)
					.addColumn(6, "ownResources", true)
					.addColumn(8, "econLife", true)
					.addColumn(9, "maintCost", true)
					.addColumn(10, "salvage", true)
					.addBooleanColumn(11, "replace")
					.addColumn(12, "yearBegin", true);
			assetsWithout = tableAssetsWo.readTable(sheetWithout, messageSource);
			rowNum = assetsWithout.size()+6;
			
			// labour items
			XlsImportTable<ProjectItemLabourWithout> tableLabourWo = new XlsImportTable<ProjectItemLabourWithout>(ProjectItemLabourWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addSelectColumn(1, "unitType", labourTypes())
					.addColumn(2, "unitNum", true)
					.addColumn(3, "unitCost", true)
					.addColumn(5,  "donated", true)
					.addColumn(6, "ownResources", true)
					.addColumn(12, "yearBegin", true);
			laboursWithout = tableLabourWo.readTable(sheetWith, messageSource);
			rowNum = rowNum+laboursWithout.size()+3;
			
			// services
			XlsImportTable<ProjectItemServiceWithout> tableServiceWo = new XlsImportTable<ProjectItemServiceWithout>(ProjectItemServiceWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2, "unitNum", true)
					.addColumn(3, "unitCost", true)
					.addColumn(5,  "donated", true)
					.addColumn(6, "ownResources", true)
					.addColumn(12, "yearBegin", true);
			servicesWithout = tableServiceWo.readTable(sheetWith, messageSource);
		}
		
		dataService.replaceProjectInvest(id, assetsWith, laboursWith, servicesWith, assetsWithout, laboursWithout, servicesWithout);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}
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
		Workbook workbook = getWorkbook(file);
		int rowNum=0;
		ProfileProductBase pp = dataService.getProfileProduct(id);
		ProfileProductItemValidator validator = new ProfileProductItemValidator();
		validator.setIncomeGen(pp.getProfile().getIncomeGen());
		
		XlsImportTable<ProfileProductIncome> tableInc = new XlsImportTable<ProfileProductIncome>(ProfileProductIncome.class, rowNum, 5, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true);
		if (pp.getProfile().getIncomeGen()) {
			tableInc.addColumn(4, "transport", true);
		}
		List<ProfileProductIncome> incs = tableInc.readTable(workbook.getSheetAt(0), messageSource);
		
		rowNum = incs.size()+6;
		XlsImportTable<ProfileProductInput> tableInp = new XlsImportTable<ProfileProductInput>(ProfileProductInput.class, rowNum, 5, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(4, "transport", true);
		List<ProfileProductInput> inps = tableInp.readTable(workbook.getSheetAt(0), messageSource);
		rowNum = rowNum+inps.size()+3;
		
		XlsImportTable<ProfileProductLabour> tableLab = new XlsImportTable<ProfileProductLabour>(ProfileProductLabour.class, rowNum, 5, validator)
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
		XSSFWorkbook workbook = getWorkbook(file);
		Profile p = dataService.getProfile(id, 1);
		
		XlsImportTable<ProfileItemGeneral> table = new XlsImportTable<ProfileItemGeneral>(ProfileItemGeneral.class, 0, 4, new ProfileItemValidator())
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true);
		
		List<ProfileItemGeneral> generals = table.readTable(workbook.getSheetAt(0), messageSource);
		
		List<ProfileItemGeneralWithout> generalsWithout=null;
		if (p.getIncomeGen()) {
			XlsImportTable<ProfileItemGeneralWithout> tableWo = new XlsImportTable<ProfileItemGeneralWithout>(ProfileItemGeneralWithout.class, generals.size()+6, 4, new ProfileItemValidator())
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2,"unitNum", true)
					.addColumn(3, "unitCost", true);
			generalsWithout = tableWo.readTable(workbook.getSheetAt(0), messageSource);
		}
		
		dataService.replaceProfileGeneral(id, generals, generalsWithout);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}
	}
	
	private void importProfileInvest(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = getWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		ProfileItemValidator validator = new ProfileItemValidator();
		// goods and materials table
		XlsImportTable<ProfileItemGood> table = new XlsImportTable<ProfileItemGood>(ProfileItemGood.class, rowNum, 4, validator)
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
		XlsImportTable<ProfileItemLabour> labourTable = new XlsImportTable<ProfileItemLabour>(ProfileItemLabour.class, rowNum,  4, validator)
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
	
	private String translate(String text) {
		return messageSource.getMessage(text, new Object[0], LocaleContextHolder.getLocale());
	}
	
	private Map<String, String> labourTypes(){
		HashMap<String, String>labourTypes=new HashMap<String, String>();
		labourTypes.put("0", translate("units.pyears"));
		labourTypes.put("1", translate("units.pmonths"));
		labourTypes.put("2", translate("units.pweeks"));
		labourTypes.put("3", translate("units.pdays"));
		return labourTypes;
	}
	
	private Map<Integer, String> contribTypes() {
		HashMap<Integer, String> contribTypes = new HashMap<Integer, String>();
		contribTypes.put(0, translate("projectContribution.contribType.govtCentral"));
		contribTypes.put(1, translate("projectContribution.contribType.govtLocal"));
		contribTypes.put(2, translate("projectContribution.contribType.ngoLocal"));
		contribTypes.put(3, translate("projectContribution.contribType.ngoIntl"));
		contribTypes.put(4, translate("projectContribution.contribType.beneficiary"));
		contribTypes.put(4, translate("projectContribution.contribType.other"));
		return contribTypes;
	}
	
	private XSSFWorkbook getWorkbook(InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook=null;
		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			throw new ExcelImportException(translate("import.error.excel.read"));
		}
		return workbook;
	}
}