package riv.web.controller;
 
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.POIXMLException;
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
import riv.objects.profile.ProfileItemGoodWithout;
import riv.objects.profile.ProfileItemLabour;
import riv.objects.profile.ProfileItemLabourWithout;
import riv.objects.profile.ProfileProductBase;
import riv.objects.profile.ProfileProductIncome;
import riv.objects.profile.ProfileProductInput;
import riv.objects.profile.ProfileProductLabour;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockIncome;
import riv.objects.project.BlockInput;
import riv.objects.project.BlockLabour;
import riv.objects.project.Donor;
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
	
	@RequestMapping(value="/import/project/{type}/{id}", method=RequestMethod.POST, produces = "text/plain;charset=UTF-8")
	public @ResponseBody String projectImport(@PathVariable Integer id,@PathVariable String type, MultipartHttpServletRequest request, HttpServletResponse response) {
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
		} catch (ExcelImportException e) {
			e.printStackTrace(System.out);
			error = e.getMessage();
		} catch (Exception e) {
			error = messageSource.getMessage("import.excel.read", null, LocaleContextHolder.getLocale());
		}
		
		if (error==null) {
			return "{\"success\": \"success\"}";
		} else {
			return "{\"error\": \""+error.replace("\"", "\\\"")+"\"}";
		}
	}
	
	private void importBlock(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = openWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		BlockBase b = dataService.getBlock(id);
		boolean ig=b.getProject().getIncomeGen();
		BlockItemValidator validator = new BlockItemValidator();
		validator.setIncomeGen(b.getProject().getIncomeGen());
		validator.setFromExcel(true);
		XlsImportTable<BlockIncome> tableInc = new XlsImportTable<BlockIncome>(BlockIncome.class, rowNum, 7, validator);
		if (ig) {
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
				if (!ig) {
					tableInp.addColumn(8,  "donations(0)", true);
				}
		List<BlockInput> inps = tableInp.readTable(sheet, messageSource);
		rowNum = rowNum+inps.size()+4;
		
		XlsImportTable<BlockLabour> tableLab = new XlsImportTable<BlockLabour>(BlockLabour.class, rowNum, 7, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitNum", true)
				.addColumn(3, "qtyIntern", true)
				.addColumn(5, "unitCost", true);
			if (!ig) {
				tableLab.addColumn(8,  "donations(0)", true);
			}
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
		XSSFWorkbook workbook = openWorkbook(file);
		
		ProjectItemValidator validator = new ProjectItemValidator();
		Project p = dataService.getProject(id, 10);
		validator.setIncomeGen(false);
		validator.setDuration(p.getDuration()); 
		
		Map<String, Integer> donorsByOrder = new HashMap<String, Integer>();
		for (Donor d : p.getDonors()) {
			String desc;
			if (d.getNotSpecified()) { desc = translate("project.donor.notSpecified"); }
			else if (d.getContribType()==4 && d.getDescription().equals("state-public")) { desc = translate("project.donor.statePublic"); }
			else { desc = d.getDescription(); }
			donorsByOrder.put(desc, d.getOrderBy());
		}
		
//		List<ProjectItemContribution> items = new ArrayList<ProjectItemContribution>();
//		XlsImportTable<ProjectItemContribution> table = new XlsImportTable<ProjectItemContribution>(ProjectItemContribution.class, 0, 5, validator)
//				.addColumn(0, "description", false)
//				.addSelectColumn(1, "donorOrderBy", donorsByOrder)
//				.addColumn(2, "unitType", false)
//				.addColumn(3, "unitNum", true)
//				.addColumn(4, "unitCost", true);
		
		
//		for (int year=1; year<=p.getDuration();year++) {
//			if (p.isPerYearContributions() || year==1) {
//				table.setStartRow(2+items.size()+(year-1)*4);
//				List<ProjectItemContribution> yearItems = table.readTable(workbook.getSheetAt(0), messageSource);
//				for (ProjectItemContribution i : yearItems) {
// 					i.setYear(year);
//				}
//				items.addAll(yearItems);
//				yearItems.clear();
//			}
//		}
		
//		dataService.replaceProjectContribution(id, items);
	}
	
	private void importProjectGeneralNongen(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = openWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		Project p = dataService.getProject(id, 1);
		ProjectItemValidator validator = new ProjectItemValidator();
		validator.setFromExcel(true);
		validator.setDonors(p.getDonors());
		validator.setIncomeGen(false);
		validator.setDuration(p.getDuration());
	
		XlsImportTable<ProjectItemNongenMaterials> tableMaterials = new XlsImportTable<ProjectItemNongenMaterials>(ProjectItemNongenMaterials.class, rowNum, 6, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5,  "donations(0)", true);
		List<ProjectItemNongenMaterials> materials = tableMaterials.readTable(sheet, messageSource);
		rowNum = materials.size()+6;
		
		XlsImportTable<ProjectItemNongenLabour> tableLabour = new XlsImportTable<ProjectItemNongenLabour>(ProjectItemNongenLabour.class, rowNum, 6, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5,  "donations(0)", true);
		List<ProjectItemNongenLabour> labours = tableLabour.readTable(sheet, messageSource);
		rowNum = rowNum+labours.size()+3;
		
		XlsImportTable<ProjectItemNongenMaintenance> tableMaintenance = new XlsImportTable<ProjectItemNongenMaintenance>(ProjectItemNongenMaintenance.class, rowNum, 6, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5,  "donations(0)", true);
		List<ProjectItemNongenMaintenance> maints = tableMaintenance.readTable(sheet, messageSource);
		
		dataService.replaceProjectGeneralNongen(id, materials, labours, maints);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}	
	}
	
	private XSSFWorkbook openWorkbook(InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook=null;
	
		try {
			workbook= getWorkbook(file);
		} catch (POIXMLException e) {
			throw new ExcelImportException(messageSource.getMessage("import.excel.read", null, LocaleContextHolder.getLocale()));
		}
		return workbook;
	}
	
	private void importProjectGeneral(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = openWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheet = workbook.getSheetAt(0);
		ProjectItemValidator validator = new ProjectItemValidator();
		
		Project p = dataService.getProject(id, 1);
		int yearsInReport=p.isPerYearGeneralCosts()?p.getDuration():1;
		
		validator.setIncomeGen(true);
		validator.setDuration(yearsInReport);
	
		// supplies
		XlsImportTable<ProjectItemGeneral> tableGen = new XlsImportTable<ProjectItemGeneral>(ProjectItemGeneral.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitCost", true)
				.addPerYearColumns(3, yearsInReport, "unitNum")
				.addPerYearColumns(3+yearsInReport*2, yearsInReport, "ownResources");
		List<ProjectItemGeneral> gensWith = tableGen.readTable(sheet, messageSource);
		rowNum = gensWith.size()+6;
		
		
		// personnel
		XlsImportTable<ProjectItemPersonnel> tablePers = new XlsImportTable<ProjectItemPersonnel>(ProjectItemPersonnel.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addSelectColumn(1, "unitType", labourTypes())
				.addColumn(2, "unitCost", true)
				.addPerYearColumns(3, yearsInReport, "unitNum")
				.addPerYearColumns(3+yearsInReport*2, yearsInReport, "ownResources");
		List<ProjectItemPersonnel> persWith = tablePers.readTable(sheet, messageSource);
		rowNum = rowNum+persWith.size()+6;
		
		List<ProjectItemGeneralWithout> gensWithout;
		List<ProjectItemPersonnelWithout> persWithout;
		
		
		if (p.isWithWithout()) {
			rowNum=0;
			
			if (workbook.getNumberOfSheets()==1) { // without project sheet is missing
				throw new ExcelImportException(translate("import.excel.error.noWithout"));
			}
			XSSFSheet sheetWithout = workbook.getSheetAt(1);
		
			// supplies Without
			XlsImportTable<ProjectItemGeneralWithout> tableGenWithout = new XlsImportTable<ProjectItemGeneralWithout>(ProjectItemGeneralWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2, "unitCost", true)
					.addPerYearColumns(3, yearsInReport, "unitNum")
					.addPerYearColumns(3+yearsInReport*2, yearsInReport, "ownResources");
			gensWithout = tableGenWithout.readTable(sheetWithout, messageSource);
			rowNum = rowNum+gensWithout.size()+6;
			
			// personnel Without
			XlsImportTable<ProjectItemPersonnelWithout> tablePersWithout = new XlsImportTable<ProjectItemPersonnelWithout>(ProjectItemPersonnelWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addSelectColumn(1, "unitType", labourTypes())
					.addColumn(2, "unitCost", true)
					.addPerYearColumns(3, yearsInReport, "unitNum")
					.addPerYearColumns(3+yearsInReport*2, yearsInReport, "ownResources");
			persWithout = tablePersWithout.readTable(sheetWithout, messageSource);	
		} else {
			gensWithout = new ArrayList<ProjectItemGeneralWithout>();
			persWithout = new ArrayList<ProjectItemPersonnelWithout>();
		}
		
		
		dataService.replaceProjectGeneral(id, gensWith, persWith, gensWithout, persWithout);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}		
	}
	
	
	private void importProjectInvest(int id, InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook = openWorkbook(file);
		
		int rowNum=0;
		XSSFSheet sheetWith = workbook.getSheetAt(0);
		ProjectItemValidator validator = new ProjectItemValidator();
		
		Project p = dataService.getProject(id, 1);
		validator.setIncomeGen(p.getIncomeGen());
		validator.setDuration(p.getDuration());
		validator.setDonors(p.getDonors());
		validator.setFromExcel(true);
		// assets
		XlsImportTable<ProjectItemAsset> tableAssets = new XlsImportTable<ProjectItemAsset>(ProjectItemAsset.class, rowNum, 4, validator)
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2, "unitNum", true)
				.addColumn(3, "unitCost", true)
				.addColumn(5,  "donations(0)", true)
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
				.addColumn(5,  "donations(0)", true)
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
				.addColumn(5,  "donations(0)", true)
				.addColumn(6, "ownResources", true)
				.addColumn(12, "yearBegin", true);
		List<ProjectItemService> servicesWith = tableService.readTable(sheetWith, messageSource);
		
		List<ProjectItemAssetWithout> assetsWithout=null;
		List<ProjectItemLabourWithout> laboursWithout=null;
		List<ProjectItemServiceWithout> servicesWithout=null;
		if (p.isWithWithout()) {
			rowNum=0;
			
			if (workbook.getNumberOfSheets()==1) { // without project sheet is missing
				throw new ExcelImportException(translate("import.excel.error.noWithout"));
			}
			XSSFSheet sheetWithout = workbook.getSheetAt(1);
			
			// assets
			XlsImportTable<ProjectItemAssetWithout> tableAssetsWo = new XlsImportTable<ProjectItemAssetWithout>(ProjectItemAssetWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2, "unitNum", true)
					.addColumn(3, "unitCost", true)
					.addColumn(5,  "donations(0)", true)
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
					.addColumn(5,  "donations(0)", true)
					.addColumn(6, "ownResources", true)
					.addColumn(12, "yearBegin", true);
			laboursWithout = tableLabourWo.readTable(sheetWithout, messageSource);
			rowNum = rowNum+laboursWithout.size()+3;
			
			// services
			XlsImportTable<ProjectItemServiceWithout> tableServiceWo = new XlsImportTable<ProjectItemServiceWithout>(ProjectItemServiceWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2, "unitNum", true)
					.addColumn(3, "unitCost", true)
					.addColumn(5,  "donations(0)", true)
					.addColumn(6, "ownResources", true)
					.addColumn(12, "yearBegin", true);
			servicesWithout = tableServiceWo.readTable(sheetWithout, messageSource);
		}
		
		dataService.replaceProjectInvest(id, assetsWith, laboursWith, servicesWith, assetsWithout, laboursWithout, servicesWithout);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}
	}

	@RequestMapping(value="/import/profile/{type}/{id}", method=RequestMethod.POST, produces = "text/plain;charset=UTF-8")
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
			error = "import.excel.read";
		} catch (ExcelImportException e) {
			error = e.getMessage();
		}
		
		if (error==null) {
			return "{\"success\": \"success\"}";
		} else {
			return "{\"error\": \""+error.replace("\"", "\\\"")+"\"}";
		}
	}
	
	private void importProfileProduct(int id, InputStream file) throws ExcelImportException {
		Workbook workbook = openWorkbook(file);
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
		XSSFWorkbook workbook = openWorkbook(file);
		Profile p = dataService.getProfile(id, 1);
		
		XlsImportTable<ProfileItemGeneral> table = new XlsImportTable<ProfileItemGeneral>(ProfileItemGeneral.class, 0, 4, new ProfileItemValidator())
				.addColumn(0, "description", false)
				.addColumn(1, "unitType", false)
				.addColumn(2,"unitNum", true)
				.addColumn(3, "unitCost", true);
		
		List<ProfileItemGeneral> generals = table.readTable(workbook.getSheetAt(0), messageSource);
		
		List<ProfileItemGeneralWithout> generalsWithout=null;
		if (p.getWithWithout()) {
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
		XSSFWorkbook workbook = openWorkbook(file);
		Profile p = dataService.getProfile(id, 1);
		
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
		
		List<ProfileItemGoodWithout> goodsWo=null;
		List<ProfileItemLabourWithout> laboursWo=null;
		if (p.getWithWithout()) {
			rowNum=0;
			if (workbook.getNumberOfSheets()==1) { // without project sheet is missing
				throw new ExcelImportException(translate("import.excel.error.noWithout"));
			}
			sheet = workbook.getSheetAt(1);
			// goods and materials table
			XlsImportTable<ProfileItemGoodWithout>tableGoodWo = new XlsImportTable<ProfileItemGoodWithout>(ProfileItemGoodWithout.class, rowNum, 4, validator)
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2,"unitNum", true)
					.addColumn(3, "unitCost", true)
					.addColumn(5, "ownResource", true)
					.addColumn(7, "econLife", true)
					.addColumn(8, "salvage", true);
			
			goodsWo = tableGoodWo.readTable(sheet, messageSource);
			
			rowNum = goodsWo.size()+6;
		
			// labours table
			XlsImportTable<ProfileItemLabourWithout>labourWoTable = new XlsImportTable<ProfileItemLabourWithout>(ProfileItemLabourWithout.class, rowNum,  4, validator)
					.addColumn(0, "description", false)
					.addColumn(1, "unitType", false)
					.addColumn(2,"unitNum", true)
					.addColumn(3, "unitCost", true)
					.addColumn(5, "ownResource", true);
			laboursWo = labourWoTable.readTable(sheet, messageSource);
		}
		
		dataService.replaceProfileInvest(id, goods, labours, goodsWo, laboursWo);
		
		try {
			file.close();
		} catch (IOException e) {
			throw new ExcelImportException(e.getMessage());
		}
	}
	
	private String translate(String text) {
		return messageSource.getMessage(text, new Object[0], LocaleContextHolder.getLocale());
	}
	private String translate(String text, String lang) {
		return messageSource.getMessage(text,  new Object[0], new Locale(lang));
	}
	
	private Map<String, String> labourTypes(){
//		return rivConfig.getLabourTypes(); 
		HashMap<String, String>labourTypes=new HashMap<String, String>();
		for (String lang : new String[]{"en","fr","es","pt","ru","mn","tr","ar"}) {
			labourTypes.put(translate("units.pyears", lang), "0");
			labourTypes.put(translate("units.pmonths", lang), "1");
			labourTypes.put(translate("units.pweeks", lang), "2");
			labourTypes.put(translate("units.pdays", lang), "3");
		}
		
		return labourTypes;
	}
	
	private XSSFWorkbook getWorkbook(InputStream file) throws ExcelImportException {
		XSSFWorkbook workbook=null;
		try {
			workbook = new XSSFWorkbook(file);
		} catch (IOException e) {
			throw new ExcelImportException(translate("import.excel.read"));
		}
		return workbook;
	}
}
