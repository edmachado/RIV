package riv.web.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.record.CFRuleRecord.ComparisonOperator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PatternFormatting;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.SheetConditionalFormatting;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import riv.objects.FilterCriteria;
import riv.objects.config.Setting;
import riv.objects.config.User;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileResult;
import riv.objects.project.Block;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockChron;
import riv.objects.project.BlockPattern;
import riv.objects.project.BlockWithout;
import riv.objects.project.Project;
import riv.objects.project.ProjectFinanceData;
import riv.objects.project.ProjectFinanceData.AnalysisType;
import riv.objects.project.ProjectFinanceNongen;
import riv.objects.project.ProjectFirstYear;
import riv.objects.project.ProjectItem;
import riv.objects.project.ProjectItemAsset;
import riv.objects.project.ProjectItemLabour;
import riv.objects.project.ProjectItemService;
import riv.objects.project.ProjectMonthFlow;
import riv.objects.project.ProjectResult;
import riv.util.CurrencyFormat;
import riv.util.ExcelBlockLink;
import riv.util.ExcelLink;
import riv.util.ExcelWrapper;
import riv.util.ExcelWrapper.Style;
import riv.web.config.RivConfig;

@Component
public class ExcelWorksheetBuilder {
	static final Logger LOG = LoggerFactory.getLogger(ExcelWorksheetBuilder.class);
	
	@Autowired
	RivConfig rivConfig;
	@Autowired
	MessageSource messageSource;
	
	private String getColumn(int number){
        String converted = "";
        // Repeatedly divide the number by 26 and convert the
        // remainder into the appropriate letter.
        while (number >= 0) {
            int remainder = number % 26;
            converted = (char)(remainder + 'A') + converted;
            number = (number / 26) - 1;
        }
        return converted;
    }
	
	public ExcelWrapper create() {
		String currencyPattern = rivConfig.getSetting().getCurrencyFormatter().getDecimalFormat(CurrencyFormat.ALL).toPattern();
		ExcelWrapper wrap = new ExcelWrapper(currencyPattern);
		return wrap;
	}
	
	
	public void ProfileResults(ExcelWrapper report, List<ProfileResult> results, FilterCriteria filter) {
		String reportName = filter.isIncomeGen() ? "profile.report.results.IG" : "profile.report.results.NIG";
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate(reportName)));
		sheet.setSelected(true);
		setColumnWidth(sheet, 0, 200);
		String[] colTitles = filter.isIncomeGen() ?
			new String[] // Income generating
		    {"profile.profileName", "profile.technician", "profile.fieldOffice", "profile.benefNum", "profile.investTotal", 
				"profile.investOwn", "profile.investExt", "profile.incomeAfterAnnual", "profile.yearsToRecover"
		    } :
	    	new String[] // Non income generating
		    {"profile.profileName", "profile.technician", "profile.fieldOffice", "profile.benefNum", "profile.investTotal", 
				"profile.investOwn", "profile.investExt", "profile.investmentPerBenef", "profile.costPerBenef"
		    };
	    // data
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate(reportName), Style.TITLE);
		
		addHeaderRow(report, sheet, rowNum++, colTitles);
		
		for (ProfileResult pr : results) {
			int c=0;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, c++, pr.getProfileName());
			report.addTextCell(row, c++, pr.getTechnician().getDescription());
			report.addTextCell(row, c++, pr.getFieldOffice().getDescription());
			report.addNumericCell(row, c++, pr.getBenefNum());
			report.addNumericCell(row, c++, pr.getInvestmentTotal(), Style.CURRENCY);
			report.addNumericCell(row, c++, pr.getInvestmentOwn(), Style.CURRENCY);
			report.addNumericCell(row, c++, pr.getInvestmentExt(), Style.CURRENCY);
			if (filter.isIncomeGen()) {
				report.addNumericCell(row, c++, pr.getIncomeAfterAnnual(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getYearsToRecover());
			} else {
				report.addNumericCell(row, c++, pr.getInvestPerBenef(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getCostPerBenef(), Style.CURRENCY);
			}
		}
		
		// totals
		row = sheet.createRow(rowNum);
		report.addTextCell(row, 0, translate("misc.total"));
		report.addFormulaCell(row, 3, String.format("SUM(D%d:D%d)", 2, rowNum));
		report.addFormulaCell(row, 4, String.format("SUM(E%d:E%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 5, String.format("SUM(F%d:F%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 6, String.format("SUM(G%d:G%d)", 2, rowNum), Style.CURRENCY);
		if (filter.isIncomeGen()) {
			report.addFormulaCell(row, 7, String.format("SUM(H%d:H%d)", 2, rowNum), Style.CURRENCY);
		}
			    
	}
	
	private String sheetName(String rawName) {
		rawName=rawName.replace(":", "-");
		return (rawName.length()>31) ? rawName.substring(0, 31) : rawName;
	}
	
	public void ProjectResults(ExcelWrapper report, List<ProjectResult> results, FilterCriteria filter){//, HashMap<Integer, String> projectStatuses) {
		String reportName = filter.isIncomeGen() ? "project.report.results.IG" : "project.report.results.NIG";
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate(reportName)));
		sheet.setSelected(true);
		setColumnWidth(sheet, 0, 200);
		
		// header
		String[] colTitles;
		if (filter.isIncomeGen()) {
			ArrayList<String> titles = new ArrayList<String>();
			titles.add("project.projectName");
			titles.add("project.userCode");
			titles.add("project.technician");
			titles.add("project.fieldOffice");
			titles.add("project.status");
			titles.add("project.category");
			titles.add("project.benefType");
			titles.add("project.enviroCat");
			if (rivConfig.getSetting().getAdmin1Enabled()) titles.add("");
			if (rivConfig.getSetting().getAdmin2Enabled()) titles.add("");
			titles.add("project.investTotal");
			titles.add("project.investOwn");
			titles.add("project.investExt");
			titles.add("project.investCredit");
			titles.add("project.annualEmployment");
			titles.add("project.annualIncome");
			titles.add("project.results.workCapital");
			titles.add("project.results.workCapital.own");
			titles.add("project.results.workCapital.donated");
			titles.add("project.results.workCapital.financed");
			titles.add("project.results.totalCosts");
			titles.add("project.results.totalCosts.own");
			titles.add("project.results.totalCosts.donated");
			titles.add("project.results.totalCosts.financed");
			titles.add("project.npv.desc");
			titles.add("project.irr.desc");
			titles.add("project.npvWithDonation.desc");
			titles.add("project.irrWithDonation.desc");
			titles.add("project.benefs.direct");
			titles.add("project.benefs.indirect");
			colTitles=titles.toArray(new String[titles.size()]);
		} else { 
			colTitles = new String[] // Non income generating
  		    {"project.projectName","project.userCode","project.technician","project.fieldOffice", "project.status", "project.enviroCat", "project.investTotal", 
  				"project.investOwn", "project.investExt", "project.investCredit.nongen", "project.annualEmployment", 
  				"project.investPerDirect", "project.investPerIndirect",
  				"project.benefs.direct", "project.benefs.indirect"
  			};
		}
		// data
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate(reportName), Style.TITLE);

		
		addHeaderRow(report, sheet, rowNum++, colTitles);
		
		for (ProjectResult pr : results) {
			int c=0;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, c++, pr.getProjectName());
			report.addTextCell(row, c++, pr.getUserCode());
			report.addTextCell(row, c++, pr.getTechnician().getDescription());
			report.addTextCell(row, c++, pr.getFieldOffice().getDescription());
			report.addTextCell(row, c++, pr.getStatus().getDescription());
			report.addTextCell(row, c++, pr.getProjCategory().getDescription());
			report.addTextCell(row, c++, pr.getBeneficiary().getDescription());
			report.addTextCell(row, c++, pr.getEnviroCategory().getDescription());
			if (rivConfig.getSetting().getAdmin1Enabled()) report.addTextCell(row, c++, pr.getAppConfig1().getDescription());
			if (rivConfig.getSetting().getAdmin2Enabled()) report.addTextCell(row, c++, pr.getAppConfig2().getDescription());
			report.addNumericCell(row, c++, pr.getInvestmentTotal(), Style.CURRENCY);
			report.addNumericCell(row, c++, pr.getInvestmentOwn(), Style.CURRENCY);
			report.addNumericCell(row, c++, pr.getInvestmentDonated(), Style.CURRENCY);
			report.addNumericCell(row, c++, pr.getInvestmentFinanced(), Style.CURRENCY);
			report.addNumericCell(row, c++, pr.getAnnualEmployment());
			if (filter.isIncomeGen()) {
				report.addNumericCell(row, c++, pr.getAnnualNetIncome(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getWorkingCapital(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getWcOwn(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getWcDonated(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getWcFinanced(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getTotalCosts(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getTotalCostsOwn(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getTotalCostsDonated(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getTotalCostsFinanced(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getNpv());
				if (pr.getIrr().compareTo(BigDecimal.valueOf(1000))==1 || pr.getIrr().compareTo(BigDecimal.valueOf(-1000))==-1) {
					report.addTextCell(row, c++, translate("misc.undefined"));
				} else {
					report.addNumericCell(row, c++, pr.getIrr());
				}
				report.addNumericCell(row, c++, pr.getNpvWithDonation());
				if (pr.getIrrWithDonation().compareTo(BigDecimal.valueOf(1000))==1 || pr.getIrrWithDonation().compareTo(BigDecimal.valueOf(-1000))==-1) {
					report.addTextCell(row, c++, translate("misc.undefined"));
				} else {
					report.addNumericCell(row, c++, pr.getIrrWithDonation(), Style.CURRENCY);
				}
			} else {
				report.addNumericCell(row, c++, pr.getInvestPerBenefDirect(), Style.CURRENCY);
				report.addNumericCell(row, c++, pr.getInvestPerBenefIndirect(), Style.CURRENCY);
			}
			report.addNumericCell(row, c++, pr.getBeneDirect());
			report.addNumericCell(row, c++, pr.getBeneIndirect());
		}
		
		// totals
		int incr=0;
		if (rivConfig.getSetting().getAdmin1Enabled()) incr++;
		if (rivConfig.getSetting().getAdmin2Enabled()) incr++;
		row = sheet.createRow(rowNum);
		report.addTextCell(row, 0, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, 8+incr, String.format("SUM("+getColumn(8+incr)+"%d:"+getColumn(8+incr)+"%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 9+incr, String.format("SUM("+getColumn(9+incr)+"%d:"+getColumn(9+incr)+"%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 10+incr, String.format("SUM("+getColumn(10+incr)+"%d:"+getColumn(10+incr)+"%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 11+incr, String.format("SUM("+getColumn(11+incr)+"%d:"+getColumn(11+incr)+"%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 12+incr, String.format("SUM("+getColumn(12+incr)+"%d:"+getColumn(12+incr)+"%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 13+incr, String.format("SUM("+getColumn(13+incr)+"%d:"+getColumn(13+incr)+"%d)", 2, rowNum), Style.CURRENCY);
		report.addFormulaCell(row, 14+incr, String.format("SUM("+getColumn(14+incr)+"%d:"+getColumn(14+incr)+"%d)", 2, rowNum), Style.CURRENCY);
		if (filter.isIncomeGen()) {
			report.addFormulaCell(row, 26+incr, String.format("SUM("+getColumn(26+incr)+"%d:"+getColumn(26+incr)+"%d)", 2, rowNum));
			report.addFormulaCell(row, 27+incr, String.format("SUM("+getColumn(27+incr)+"%d:"+getColumn(27+incr)+"%d)", 2, rowNum));
		}
	}
	


	private void addHeaderRow(ExcelWrapper report, Sheet sheet, int rowNum, String[] titles) {
		addHeaderRow(report, sheet, rowNum, titles, false);

	}
	private void addHeaderRow(ExcelWrapper report, Sheet sheet, int rowNum, String[] titles, boolean noTranslate) {

		Row row = sheet.createRow(rowNum);
		for (int i=0; i<titles.length; i++) {
			String title=noTranslate? titles[i] : translate(titles[i]);
			report.addTextCell(row, i, title, Style.LABEL);

			if (title!=null &! title.equals("")) {
				//sheet.autoSizeColumn((short)i);
			}
		}
	}
	
	private String translate(String messageCode) {
		return messageSource.getMessage(messageCode, null, new Locale(rivConfig.getSetting().getLang()));
	}
	
	private String writeFormula(String formula, int row) {
		return formula.replace("X",Integer.toString(row));
	}
	
	public Sheet getBlock(ExcelWrapper report, BlockBase block, boolean incomeGen) {
		XlsTable[] xlsTables = blockXlsTables(report, incomeGen);
		Sheet sheet = report.getWorkbook().createSheet();
		addBlock(block, incomeGen, report, sheet, 0, xlsTables);
		return sheet;
	}
	
	public Sheet getBlocks(ExcelWrapper report, Project project, boolean isWithout) {
		// data
		int rowNum=0; 
		
		String title = project.getIncomeGen() ? translate("project.report.blockDetail") : translate("project.report.activityDetail");
		if (project.isWithWithout()) {
			if (isWithout)  {
				title =  "("+translate("projectBlock.with.without")+") "+ title;
			} else {
				title =  "("+translate("projectBlock.with.with")+") "+ title;
			}
		}
		Sheet sheet = report.getWorkbook().createSheet(sheetName(title));

		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);
		
		XlsTable[] xlsTables = blockXlsTables(report, project.getIncomeGen());
		
		Set<? extends BlockBase> blocks = isWithout ? project.getBlocksWithout() : project.getBlocks();		
		for (BlockBase block : blocks) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, block.getDescription(), Style.H2);

			
			rowNum = addBlock(block, project.getIncomeGen(), report, sheet, rowNum, xlsTables);
		}
		
		return null;
	}
	
	private XlsTable[] blockXlsTables(ExcelWrapper report, boolean incomeGen) {
		XlsTable[] tables = new XlsTable[3];
		String[] incomeTitles = new String[] 
			    {"projectBlockIncome.desc","projectBlockIncome.unitType","projectBlockIncome.unitNum",
					"projectBlockIncome.qtyIntern","projectBlockIncome.qtyExtern","projectBlockIncome.unitCost",
					"projectBlockIncome.transport","projectBlockIncome.total","projectBlockIncome.totalCash"};

			String[] incomeTitlesNIG = new String[] 
	   		    {"projectActivityCharge.desc","projectActivityCharge.unitType","projectActivityCharge.unitNum",
	   				"", "", "projectActivityCharge.unitCost", "", "projectActivityCharge.total"};

			String[] inputTitles = new String[] 
			    {"projectBlockInput.desc","projectBlockInput.unitType","projectBlockInput.unitNum",
					"projectBlockInput.qtyIntern","projectBlockInput.qtyExtern","projectBlockInput.unitCost",
					"projectBlockInput.transport","projectBlockInput.total","projectBlockInput.totalCash"};
			
			String[] labourTitles = new String[] 
	 		    {"projectBlockLabour.desc","projectBlockLabour.unitType","projectBlockLabour.unitNum",
					"projectBlockLabour.qtyIntern","projectBlockLabour.qtyExtern",
					"projectBlockLabour.unitCost","","projectBlockLabour.total","projectBlockLabour.totalCash"};
			
//			XlsTable incomeTable;
			if (incomeGen) {
				tables[0] = new XlsTable(report, incomeTitles)
				.addColumn(XlsColumnType.TEXT, "getDescription", false)
				.addColumn(XlsColumnType.TEXT, "getUnitType", false)
				.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
				.addColumn(XlsColumnType.NUMERIC, "getQtyIntern", false)
				.addColumn(XlsColumnType.FORMULA, "CX-DX", false)
				.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
				.addColumn(XlsColumnType.CURRENCY, "getTransport", false)
				.addColumn(XlsColumnType.FORMULA, "CX*(FX-GX)", true)
				.addColumn(XlsColumnType.FORMULA, "(CX-DX)*(FX-GX)", true);
			} else {
				tables[0] = new XlsTable(report, incomeTitlesNIG)
				.addColumn(XlsColumnType.TEXT, "getDescription", false)
				.addColumn(XlsColumnType.TEXT, "getUnitType", false)
				.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.FORMULA, "CX*FX", true);
			}
			
			tables[1] = new XlsTable(report, inputTitles)
			.addColumn(XlsColumnType.TEXT, "getDescription", false)
			.addColumn(XlsColumnType.TEXT, "getUnitType", false)
			.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
			.addColumn(XlsColumnType.NUMERIC, "getQtyIntern", false)
			.addColumn(XlsColumnType.FORMULA, "CX-DX", false)
			.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
			.addColumn(XlsColumnType.CURRENCY, "getTransport", false)
			.addColumn(XlsColumnType.FORMULA, "CX*(FX+GX)", true)
			.addColumn(XlsColumnType.FORMULA, "(CX-DX)*(FX+GX)", true);
			
			tables[2] = new XlsTable(report, labourTitles)

			.addColumn(XlsColumnType.TEXT, "getDescription", false)
			.addSelectColumn("getUnitType", labourTypes())
			.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
			.addColumn(XlsColumnType.NUMERIC, "getQtyIntern", false)
			.addColumn(XlsColumnType.FORMULA, "CX-DX", false)
			.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
			.addColumn(XlsColumnType.NONE, null, false)
			.addColumn(XlsColumnType.FORMULA, "CX*FX", true)
			.addColumn(XlsColumnType.FORMULA, "(CX-DX)*FX", true);
			
			return tables;
	}
	
	private int addBlock(BlockBase block, boolean incomeGen, ExcelWrapper report, Sheet sheet, int rowNum, XlsTable[] tables) {
		int[] sumRows = new int[3];
		
		// income
		String title= incomeGen ? "projectBlockIncome" : "projectActivityCharge";
		report.addTextCell(sheet.createRow(rowNum++), 0, translate(title), Style.H2);
		rowNum=tables[0].writeTable(sheet, rowNum++, block.getIncomes(), true);
		sumRows[0]=rowNum;
		rowNum=rowNum++;
		
		// input cost
		report.addTextCell(sheet.createRow(rowNum++), 0, translate("projectBlockInput").replace('/',' '), Style.H2);
		rowNum=tables[1].writeTable(sheet, rowNum++, block.getInputs(), true);
		sumRows[1]=rowNum;
		rowNum=rowNum++;
		
		// labour cost
		report.addTextCell(sheet.createRow(rowNum++), 0, translate("projectBlockLabour"), Style.H2);
		rowNum=tables[2].writeTable(sheet, rowNum++, block.getLabours(), true);
		sumRows[2]=rowNum;
		rowNum=rowNum+2;
		
		// totals
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 6, translate("project.report.blockDetail.totalPerUnitCycle"), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 6, translate("project.report.blockDetail.incomes"), Style.LABEL);
		report.addFormulaCell(row, 7, String.format("H%d",sumRows[0]), Style.CURRENCY);
		String formula = incomeGen ? "I%d" : "H%d";
		report.addFormulaCell(row, 8, String.format(formula,sumRows[0]), Style.CURRENCY);


		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 6, translate("project.report.blockDetail.costs"), Style.LABEL);
		report.addFormulaCell(row, 7, String.format("H%d+H%d",sumRows[1],sumRows[2]), Style.CURRENCY);
		report.addFormulaCell(row, 8, String.format("I%d+I%d",sumRows[1],sumRows[2]), Style.CURRENCY);


		row = sheet.createRow(rowNum++);
		title = incomeGen ? "project.report.blockDetail.netIncome" : "project.report.blockDetail.netIncome.nongen";
		report.addTextCell(row, 6, translate(title), Style.LABEL);
		report.addFormulaCell(row, 7, String.format("H%d-H%d", rowNum-2, rowNum-1), Style.CURRENCY);
		report.addFormulaCell(row, 8, String.format("I%d-I%d", rowNum-2, rowNum-1), Style.CURRENCY);

		if (report.isCompleteReport()) {
			ExcelBlockLink blockLink =  block.getClass()==Block.class ? report.getBlockLinks().get(block.getBlockId()) : report.getBlockLinksWithoutProject().get(block.getBlockId());
			blockLink.income="'"+sheet.getSheetName()+"'!$H$"+(rowNum-2);
			blockLink.incomeCash="'"+sheet.getSheetName()+"'!$I$"+(rowNum-2);
			blockLink.cost="'"+sheet.getSheetName()+"'!$H$"+(rowNum-1);
			blockLink.costCash="'"+sheet.getSheetName()+"'!$I$"+(rowNum-1);
			blockLink.netIncome="'"+sheet.getSheetName()+"'!$H$"+rowNum;
			blockLink.netIncomeCash="'"+sheet.getSheetName()+"'!$I$"+rowNum;
		}
		
		return rowNum;
	}
	
	public Sheet getProjectChronology(ExcelWrapper report, Project project) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.chronology")));

		
		SheetConditionalFormatting formatting = sheet.getSheetConditionalFormatting();
		ConditionalFormattingRule rule = formatting.createConditionalFormattingRule(ComparisonOperator.NOT_EQUAL, "");
		PatternFormatting fill = rule.createPatternFormatting();
		fill.setFillBackgroundColor(IndexedColors.YELLOW.index);
		CellRangeAddress[] regions = {CellRangeAddress.valueOf(String.format("B6:Y%d", 6+4*project.getBlocks().size()))};
		formatting.addConditionalFormatting(regions, rule);
		
		sheet.setColumnWidth(0, 235*36);
		for (int i=1; i<=24; i++) {
			sheet.setColumnWidth(i, 30*36);
		}
		sheet.setSelected(true);

		int rowNum = 0;
		int cellNum = 1;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.chronology"), Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectBlock.chronology"), Style.H2);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectBlock.name"));
		ArrayList<String> months = months(messageSource, project);

		for (String month : months) {
			sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, cellNum, cellNum+1));
			Cell cell = report.addTextCell(row, cellNum, month);
			cell.setCellStyle(report.getStyles().get(Style.CENTERED));

			cellNum += 2;
		}
		
		rowNum++;	
	
		if (project.isWithWithout()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectBlock.with.with"), Style.H2);
		}
		
		for (Block block : project.getBlocks()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, block.getDescription(), Style.LABEL);
			
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectBlock.chronology.production"));
			chronologyMap(report, block.getChrons(), row, 0);
			if (report.isCompleteReport()) {
				report.getBlockLinks().get(block.getBlockId()).productionRow=rowNum;
			}
	
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectBlock.chronology.harvest"));
			chronologyMap(report, block.getChrons(), row, 1);
			
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectBlock.chronology.payment"));
			chronologyMap(report, block.getChrons(), row, 2);
			if (report.isCompleteReport()) {
				report.getBlockLinks().get(block.getBlockId()).paymentRow=rowNum;
			}
		}
		
		if (project.isWithWithout()) {
			rowNum++;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectBlock.with.with"), Style.H2);
		
			for (BlockWithout block : project.getBlocksWithout()) {
				row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, block.getDescription(), Style.LABEL);
				
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate("projectBlock.chronology.production"));
				chronologyMap(report, block.getChrons(), row, 0);
				if (report.isCompleteReport()) {
					report.getBlockLinksWithoutProject().get(block.getBlockId()).productionRow=rowNum;
				}
				
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate("projectBlock.chronology.harvest"));
				chronologyMap(report, block.getChrons(), row, 1);
				
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate("projectBlock.chronology.payment"));
				chronologyMap(report, block.getChrons(), row, 2);
				if (report.isCompleteReport()) {
					report.getBlockLinksWithoutProject().get(block.getBlockId()).paymentRow=rowNum;
				}
			}
		}
		
		return sheet;
	}
	
	private void chronologyMap(ExcelWrapper report, Map<String, BlockChron> map, Row row, int rowNum) {

		int cellNum = 0;
		for (int i=0; i < 12; i++) {
			cellNum++;
			if (map.containsKey(String.format("%d-%d-0", rowNum, i))) {
				report.addTextCell(row, cellNum, "#");
			}
			cellNum++;
			if (map.containsKey(String.format("%d-%d-1", rowNum, i))) {
				report.addTextCell(row, cellNum, "#");
			}
		}
	}
	
	public Sheet getProjectProduction(ExcelWrapper report, Project project) {
		boolean incomeGen = project.getIncomeGen();
		int duration = project.getDuration();

		String name = (incomeGen) ? "projectBlock.name" : "projectActivity.name";
		String unit = (incomeGen) ? "projectBlock.prodUnit" : "projectActivity.prodUnit";
		
		String[] titles = new String[11+project.getDuration()];
		titles[0]=translate(name);
		titles[1]="";
		titles[2]=translate(unit);
		titles[3]=translate("projectBlock.cycleLength");
		titles[4]="";
		titles[5]=translate("projectBlock.cyclePerYear");
		titles[6]="";
		for (int i=1; i <= duration; i++) {
			titles[6+i]=String.valueOf(i);
		}
		if (project.getIncomeGen()) {
			titles[7+duration]=translate("projectBlock.cycleFirstYear");
			titles[8+duration]="";
			titles[9+duration]=translate("projectBlock.cycleFirstYearIncome");
			titles[10+duration]="";
		} else {
			titles[7+duration]="";
			titles[8+duration]="";
			titles[9+duration]="";
			titles[10+duration]="";
		}
		
		String reportName = incomeGen ? translate("project.report.production") : translate("project.report.production.nonGen");
		int rowNum = 0;
		
		Sheet sheet = report.getWorkbook().createSheet(reportName);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, reportName, Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 7, 7+duration-1));
		report.addTextCell(row, 7, translate("projectBlock.pattern.years")).setCellStyle(report.getStyles().get(Style.CENTERED));

		
		row = sheet.createRow(rowNum++);
		addHeaderRow(report, sheet, rowNum-1, titles, true);

		sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 3,4));
		sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, 5,6));
		sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, duration+7,duration+8));
		sheet.addMergedRegion(new CellRangeAddress(rowNum-1, rowNum-1, duration+9,duration+10));

		sheet.setColumnWidth(3, 40*36);
		sheet.setColumnWidth(4, 80*36);
		sheet.setColumnWidth(5, 40*36);
		sheet.setColumnWidth(6, 80*36);
		sheet.setColumnWidth(duration+7, 40*36);
		sheet.setColumnWidth(duration+8, 80*36);
		sheet.setColumnWidth(duration+9, 40*36);
		sheet.setColumnWidth(duration+10, 80*36);
		
		for (int i=1; i <= duration; i++) {
			sheet.setColumnWidth(6+i,40*36);
		}
		
		if (project.isWithWithout()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectBlock.with.with"), Style.H2);
		}
		for (Block block : project.getBlocks()) {
			rowNum = addBlock(report, sheet, rowNum, block);
		}
		
		if (project.isWithWithout()) {
			rowNum++;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectBlock.with.without"), Style.H2);
			for (BlockWithout block : project.getBlocksWithout()) {
				rowNum = addBlock(report, sheet, rowNum, block);
			}
		}
		return sheet;
	}
	
	private int addBlock(ExcelWrapper report, Sheet sheet, int rowNum, BlockBase block) {
		int cellNum = 0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum++, block.getDescription());
		cellNum++;
		
		report.addTextCell(row, cellNum++, block.getUnitType());
		report.addNumericCell(row, cellNum++, block.getCycleLength());
		report.addTextCell(row, cellNum++, lengthUnits().get(block.getLengthUnit()));
		report.addNumericCell(row, cellNum++, block.getCyclePerYear());
		report.addTextCell(row, cellNum++, translate("units.perYear"));
		
		ExcelBlockLink blockLink = new ExcelBlockLink();

		int duration=block.getProject().getDuration();
		blockLink.cycles = "'"+sheet.getSheetName()+"'!F"+rowNum;
		blockLink.qtyPerYear = new String[duration];
		
		for (int i=1; i <= duration; i++) {
			BlockPattern pattern = block.getPatterns().get(i);
			report.addNumericCell(row, cellNum++, pattern.getQty());
			if (report.isCompleteReport()) {
				blockLink.qtyPerYear[i-1]="'"+sheet.getSheetName()+"'!"+getColumn(6+i)+rowNum;
			}
		}
		
		if (block.getProject().getIncomeGen()) {
			report.addNumericCell(row, cellNum++, block.getCycleFirstYearIncome());
			blockLink.cyclesFirstYearPayment="'"+sheet.getSheetName()+"'!Y"+rowNum;
			report.addTextCell(row, cellNum++, translate("units.perYear"));
			report.addNumericCell(row, cellNum++, block.getCycleFirstYear());
			blockLink.cyclesFirstYearProduction="'"+sheet.getSheetName()+"'!W"+rowNum;
			report.addTextCell(row, cellNum++, translate("units.perYear"));
		}
		
		if (report.isCompleteReport()) {
			if (block.getClass()==Block.class) {
				report.getBlockLinks().put(block.getBlockId(), blockLink);
			} else {
				report.getBlockLinksWithoutProject().put(block.getBlockId(), blockLink);
			}
		}	
		return rowNum;
	}
	
	public Sheet getProjectGeneralDescription(ExcelWrapper report, Project project) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.general")));


		setColumnWidth(sheet, 0, 286);
		setColumnWidth(sheet, 2, 200);
		
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.general"), Style.TITLE);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("settings.currency.name"));
		report.addTextCell(row, 2, rivConfig.getSetting().getCurrencyName());

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.exchRate"));
		report.addNumericCell(row, 2, project.getExchRate(), Style.CURRENCY);
		report.addTextCell(row, 3, translate("units.perUSD"));

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.category"));
		report.addTextCell(row, 2, project.getProjCategory().getDescription());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.enviroCat"));
		report.addTextCell(row, 2, project.getEnviroCategory().getDescription());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.status"));
		report.addTextCell(row, 2, project.getStatus().getDescription());
		if (rivConfig.getSetting().getAdmin1Enabled()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, rivConfig.getSetting().getAdmin1Title());
			report.addTextCell(row, 2, project.getAppConfig1().getDescription());
		}
		if (rivConfig.getSetting().getAdmin2Enabled()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, rivConfig.getSetting().getAdmin2Title());
			report.addTextCell(row, 2, project.getAppConfig2().getDescription());

		}
		
		rowNum++;	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.step1.2"), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.fieldOffice"));
		report.addTextCell(row, 2, project.getFieldOffice().getDescription());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, rivConfig.getSetting().getLocation1());
		report.addTextCell(row, 2, project.getLocation1());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, rivConfig.getSetting().getLocation2());
		report.addTextCell(row, 2, project.getLocation2());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, rivConfig.getSetting().getLocation3());
		report.addTextCell(row, 2, project.getLocation3());
		
		rowNum++;	
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.step1.3"), Style.H2);

		row = sheet.createRow(rowNum++);
		User user = project.getTechnician();
		report.addTextCell(row, 0, translate("user.description"));
		report.addTextCell(row, 2, user.getDescription());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.organization"));
		report.addTextCell(row, 2, user.getOrganization());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.location"));
		report.addTextCell(row, 2, user.getLocation());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.telephone"));
		report.addTextCell(row, 2, user.getTelephone());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.email"));
		report.addTextCell(row, 2, user.getEmail());

		
		rowNum++;	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.general.benefs"), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.benefName"));
		report.addTextCell(row, 2, project.getBenefName());
		rowNum = report.addBigTextCell(sheet, rowNum++,translate("project.benefDesc"), project.getBenefDesc());

		
		rowNum++;	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.step2.2"), Style.H2);
		report.addTextCell(row, 2, translate("project.step2.3"), Style.H2);

		String men = translate("project.benefMen");
		String women = translate("project.benefWomen");
		String child = translate("project.benefChild");
		String total = translate("project.benefTotal");
		String families = translate("project.benefFamilies");
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, men);	report.addTextCell(row, 2, men);
		report.addNumericCell(row, 1, project.getBeneDirectMen());
		report.addNumericCell(row, 3, project.getBeneIndirectMen());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, women);	report.addTextCell(row, 2, women);
		report.addNumericCell(row, 1, project.getBeneDirectWomen());
		report.addNumericCell(row, 3, project.getBeneIndirectWomen());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, child);	report.addTextCell(row, 2, child);
		report.addNumericCell(row, 1, project.getBeneDirectChild());
		report.addNumericCell(row, 3, project.getBeneIndirectChild());
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, total, Style.LABEL);	report.addTextCell(row, 2, total, Style.LABEL);
		report.addFormulaCell(row, 1, String.format("SUM(B%d:B%d)", rowNum-4, rowNum-1), Style.LABEL);
		report.addFormulaCell(row, 3, String.format("SUM(D%d:D%d)", rowNum-4, rowNum-1), Style.LABEL);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, families);	report.addTextCell(row, 2, families);
		report.addNumericCell(row, 1, project.getBeneDirectNum());
		report.addNumericCell(row, 3, project.getBeneIndirectNum());
		
		rowNum++;
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.justification"), project.getJustification());
		
		rowNum++;	
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.projectDescription"), project.getProjDesc());

		rowNum++;	
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.activities"), project.getActivities());

		rowNum++;	
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.technology"), project.getTechnology());

		rowNum++;
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.requirements"), project.getRequirements());

		if (!project.getIncomeGen()) {
			rowNum++;	
			rowNum= report.addBigTextCell(sheet, rowNum, translate("project.sustainability.nongen"), project.getSustainability());

		}
		
		rowNum++;
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.enviroImpact"), project.getEnviroImpact());

		rowNum++;
		String label;
		if (project.getIncomeGen()) {
			label = "project.market";
		} else {
			label = "project.demand";
		}
		rowNum= report.addBigTextCell(sheet, rowNum, translate(label), project.getMarket());

		rowNum++;
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.organization"), project.getOrganization());

		rowNum++;
		rowNum= report.addBigTextCell(sheet, rowNum, translate("project.assumptions"), project.getAssumptions());
		return sheet;
	}
	
	public Sheet getProjectGeneralDetailNongen(ExcelWrapper report, Project project, boolean template) {

		int[] subtotalRows = new int[3];
		int rowNum=0;
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.generalCostsDetail")));

		sheet.setColumnWidth(1, 75*36);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.generalCostsDetail"), Style.TITLE);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectNongenInput"), Style.H2);
		
		String[] headerInputs = new String[]{"projectNongenInput.description","projectNongenInput.unitType","projectNongenInput.unitNum",
				"projectNongenInput.unitCost","projectNongenInput.total","projectNongenInput.statePublic","projectNongenInput.other1",
				"projectNongenInput.ownResource"};
		XlsTable table = new XlsTable(report, headerInputs)
		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "DX*CX", true)
		.addColumn(XlsColumnType.CURRENCY, "getStatePublic", true)
		.addColumn(XlsColumnType.CURRENCY, "getOther1", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX-GX", true);
		rowNum=table.writeTable(sheet, rowNum++, project.getNongenMaterials(), true);
		subtotalRows[0]=rowNum++;

		String[] headerLabours = new String[]{"projectNongenLabour.description","projectNongenLabour.unitType","projectNongenLabour.unitNum",
				"projectNongenLabour.unitCost","projectNongenLabour.total","projectNongenLabour.statePublic","projectNongenLabour.other1",
				"projectNongenLabour.ownResource"};
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectNongenLabour"), Style.H2);
		XlsTable tableLabour = new XlsTable(report, headerLabours)

		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addSelectColumn("getUnitType", labourTypes())
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "DX*CX", true)
		.addColumn(XlsColumnType.CURRENCY, "getStatePublic", true)
		.addColumn(XlsColumnType.CURRENCY, "getOther1", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX-GX", true);
		rowNum=tableLabour.writeTable(sheet, rowNum++, project.getNongenLabours(), true);
		subtotalRows[1]=rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectNongenGeneral"), Style.H2);

		String[] headerGeneral = new String[]{"projectNongenGeneral.description","projectNongenGeneral.unitType","projectNongenGeneral.unitNum",
				"projectNongenGeneral.unitCost","projectNongenGeneral.total","projectNongenGeneral.statePublic","projectNongenGeneral.other1",
				"projectNongenGeneral.ownResource"};
		table.titles=headerGeneral;
		rowNum = table.writeTable(sheet, rowNum++, project.getNongenMaintenance(), true);
		subtotalRows[2]=rowNum++;
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 3, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, 4, String.format("E%d+E%d+E%d",subtotalRows[0],subtotalRows[1],subtotalRows[2]));
		report.addFormulaCell(row, 5, String.format("F%d+F%d+F%d",subtotalRows[0],subtotalRows[1],subtotalRows[2]));
		report.addFormulaCell(row, 6, String.format("G%d+G%d+G%d",subtotalRows[0],subtotalRows[1],subtotalRows[2]));
		report.addFormulaCell(row, 7, String.format("H%d+H%d+H%d",subtotalRows[0],subtotalRows[1],subtotalRows[2]));

		return sheet;
	}
	
	
	public Sheet getProjectGeneralDetail(ExcelWrapper report, Project project, boolean template) {
		int rowNum=0;
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.generalCostsDetail")));

		sheet.setColumnWidth(1, 75*36);
		sheet.setSelected(true);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.generalCostsDetail"), Style.TITLE);
		
		if (project.getIncomeGen() && project.isWithWithout()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectGeneral.with"), Style.H2);
		} else {
			rowNum++;
		}
		
		rowNum=addGeneralCosts(report, true, project, rowNum, sheet, template);

		
		if (project.getIncomeGen() && project.isWithWithout()) {
			rowNum=rowNum+2;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("projectGeneral.without"), Style.H2);
			
			rowNum = addGeneralCosts(report, false, project, rowNum, sheet, template);

			
		}
		return sheet;
	}
	
	private int addGeneralCosts(ExcelWrapper report, boolean withProject, Project project, int rowNum, Sheet sheet, boolean template) {

		String[] headerSupplies = new String[]{"projectGeneralSupplies.description", "projectGeneralSupplies.unitType", 
				"projectGeneralSupplies.unitNum", "projectGeneralSupplies.unitCost", "projectGeneralSupplies.totalCost", 
				"projectGeneralSupplies.ownResources", "projectGeneralSupplies.external"};
		String[] headerPersonnel = new String[]{"projectGeneralPersonnel.description", "projectGeneralPersonnel.unitType", 
				"projectGeneralPersonnel.unitNum", "projectGeneralPersonnel.unitCost", "projectGeneralPersonnel.totalCost", 
				"projectGeneralPersonnel.ownResources", "projectGeneralPersonnel.external"};
		
		int[] totalRows = new int[2];
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectGeneralSupplies"), Style.H2);
		
		XlsTable table = new XlsTable(report, headerSupplies);
		table.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true)
		.addColumn(XlsColumnType.CURRENCY, "getOwnResources", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX", true);
		rowNum = table.writeTable(sheet, rowNum, template ? null : withProject ? project.getGenerals() : project.getGeneralWithouts(), true);
		totalRows[0]=rowNum;
		rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectGeneralPersonnel"), Style.H2);
		
		
		table = new XlsTable(report, headerPersonnel);
		table.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addSelectColumn("getUnitType", labourTypes())
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true)
		.addColumn(XlsColumnType.CURRENCY, "getOwnResources", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX", true);
		rowNum = table.writeTable(sheet, rowNum, template ? null : withProject ? project.getPersonnels() : project.getPersonnelWithouts(), true);
		totalRows[1]=rowNum;
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 3, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, 4, String.format("E%d+E%d", totalRows[0],totalRows[1]), Style.CURRENCY);
		report.addFormulaCell(row, 5, String.format("F%d+F%d", totalRows[0],totalRows[1]), Style.CURRENCY);
		report.addFormulaCell(row, 6, String.format("G%d+G%d", totalRows[0],totalRows[1]), Style.CURRENCY);
		
		if (report.isCompleteReport()) {
			if (withProject) {
				report.getLinks().put(ExcelLink.PROJECT_GENERAL_TOTAL, "'"+sheet.getSheetName()+"'!E"+rowNum);
				report.getLinks().put(ExcelLink.PROJECT_GENERAL_OWN, "'"+sheet.getSheetName()+"'!R"+rowNum);
				report.getLinks().put(ExcelLink.PROJECT_GENERAL_CASH, "'"+sheet.getSheetName()+"'!G"+rowNum);
			} else {
				report.getLinks().put(ExcelLink.PROJECT_GENERAL_WITHOUT_TOTAL, "'"+sheet.getSheetName()+"'!E"+rowNum);
				report.getLinks().put(ExcelLink.PROJECT_GENERAL_WITHOUT_OWN, "'"+sheet.getSheetName()+"'!R"+rowNum);
				report.getLinks().put(ExcelLink.PROJECT_GENERAL_WITHOUT_CASH, "'"+sheet.getSheetName()+"'!G"+rowNum);
			}
		}
		
		return rowNum;
	}
	
	public Sheet getProjectInvestmentDetail(ExcelWrapper report, Project project, boolean without, boolean template) {

		int rowNum=0; int[] totalRows = new int[3];
		String title = without ? translate("project.report.investDetail") + " " + translate("project.without")
				: project.isWithWithout() ? translate("project.report.investDetail") + " " + translate("project.with")
				: translate("project.report.investDetail");
		Sheet sheet = report.getWorkbook().createSheet(title);

		for (short i = 3; i <= 10; i++) {
			if (! (i == 8) ) {
				sheet.setDefaultColumnStyle(i, report.getStyles().get(Style.CURRENCY));

			}
		}
		sheet.setColumnWidth(1, 75*36);
		sheet.setSelected(!without);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectInvestAsset"), Style.H2);
		if (!without) {
			report.getLinks().put(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW, String.valueOf(rowNum+2));
			report.getLinks().put(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET, "'"+sheet.getSheetName()+"'");
		}
		
		String[] headerAssets = new String[]{"projectInvestAsset.description", "projectInvestAsset.unitType", "projectInvestAsset.unitNum", "projectInvestAsset.unitCost", "projectInvestAsset.totalCost", "projectInvestAsset.donated", 
				"projectInvestAsset.ownResources", "projectInvestAsset.financed", "projectInvestAsset.econLife", "projectInvestAsset.maintCost", 
				"projectInvestAsset.salvage", "projectInvestAsset.replace", "projectInvestAsset.yearBegin"};
		XlsTable assetsTable = new XlsTable(report, headerAssets);
		assetsTable.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true)
		.addColumn(XlsColumnType.CURRENCY, "getDonated", true)
		.addColumn(XlsColumnType.CURRENCY, "getOwnResources", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX-GX", true)
		.addColumn(XlsColumnType.NUMERIC, "getEconLife", false)
		.addColumn(XlsColumnType.CURRENCY, "getMaintCost", false)
		.addColumn(XlsColumnType.CURRENCY, "getSalvage", false)
		.addColumn(XlsColumnType.YESNO, "getReplace", false)
		.addColumn(XlsColumnType.NUMERIC, "getYearBegin", false);
		rowNum = assetsTable.writeTable(sheet, rowNum, template ? null : without ? project.getAssetsWithout() : project.getAssets(), true);
		totalRows[0]=rowNum;
		rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectInvestLabour"), Style.H2);
		
		String[] headerLabours = new String[]{"projectInvestLabour.description", "projectInvestLabour.unitType", "projectInvestLabour.unitNum", "projectInvestLabour.unitCost", "projectInvestLabour.totalCost", "projectInvestLabour.donated", 
				"projectInvestLabour.ownResources", "projectInvestLabour.financed", "", "", "", "", "projectInvestLabour.yearBegin"};
		XlsTable labourTable = new XlsTable(report, headerLabours);
		labourTable.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addSelectColumn("getUnitType", labourTypes())
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true)
		.addColumn(XlsColumnType.CURRENCY, "getDonated", true)
		.addColumn(XlsColumnType.CURRENCY, "getOwnResources", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX-GX", true)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NUMERIC, "getYearBegin", false);
		rowNum = labourTable.writeTable(sheet, rowNum, template ? null : without ? project.getLaboursWithout() : project.getLabours(), true);
		totalRows[1]=rowNum;
		rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectInvestService"), Style.H2);
		
		String[] headerServices = new String[]{"projectInvestService.description", "projectInvestService.unitType", "projectInvestService.unitNum", "projectInvestService.unitCost", "projectInvestService.totalCost", "projectInvestService.donated", 
				"projectInvestService.ownResources", "projectInvestService.financed", "", "", "", "", "projectInvestService.yearBegin"};
		XlsTable serviceTable = new XlsTable(report, headerServices);
		serviceTable.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true)
		.addColumn(XlsColumnType.CURRENCY, "getDonated", true)
		.addColumn(XlsColumnType.CURRENCY, "getOwnResources", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX-GX", true)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.NUMERIC, "getYearBegin", false);
		rowNum = serviceTable.writeTable(sheet, rowNum, template ? null : without ? project.getServicesWithout() : project.getServices(), true);
		totalRows[2]=rowNum;
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 3, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, 4, getColumn(4)+totalRows[0]+"+"+getColumn(4)+totalRows[1]+"+"+getColumn(4)+totalRows[2], Style.CURRENCY);
		report.addFormulaCell(row, 5, getColumn(5)+totalRows[0]+"+"+getColumn(5)+totalRows[1]+"+"+getColumn(5)+totalRows[2], Style.CURRENCY);
		report.addFormulaCell(row, 6, getColumn(6)+totalRows[0]+"+"+getColumn(6)+totalRows[1]+"+"+getColumn(6)+totalRows[2], Style.CURRENCY);
		report.addFormulaCell(row, 7, getColumn(7)+totalRows[0]+"+"+getColumn(7)+totalRows[1]+"+"+getColumn(7)+totalRows[2], Style.CURRENCY);
		
		if (report.isCompleteReport() &! without) {
			report.getLinks().put(ExcelLink.PROJECT_INVEST_FINANCED, "'"+sheet.getSheetName()+"'!$H$"+rowNum);
		}
		return sheet;
	}

	public Sheet getProjectSummary(ExcelWrapper report, Project project, ProjectResult pr) {

		
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.summary")));

		sheet.setSelected(true);
		Setting setting = rivConfig.getSetting();
		setColumnWidth(sheet, 0, 300);
		for (short i=1; i<=3; i++) {
			sheet.setDefaultColumnStyle(i, report.getStyles().get(Style.CURRENCY));

			setColumnWidth(sheet, i, 100);
		}
		
		int rowNum=0;
		int cellNum=0;
		int startRow=0;
		

		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary"), Style.TITLE);

		
	// Location
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.step1.2"), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.fieldOffice"));
		report.addTextCell(row, 1, project.getFieldOffice().getDescription().trim());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, setting.getLocation1());
		report.addTextCell(row, 1, project.getLocation1());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, setting.getLocation2());
		report.addTextCell(row, 1, project.getLocation2());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, setting.getLocation3());
		report.addTextCell(row, 1, project.getLocation3());
		
	// Category
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);	
		report.addTextCell(row, 0, translate("project.category"), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, project.getProjCategory().getDescription());

		
	//Generated employment
		cellNum = 3;
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary.employment"), Style.H2);

		row = sheet.createRow(rowNum++);	startRow = rowNum;
		report.addTextCell(row, 0, translate("project.report.summary.employment.investment"));
		report.addNumericCell(row, cellNum, project.getAnnualEmploymentInvestment(), Style.CURRENCY);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary.employment.general"));
		report.addNumericCell(row, cellNum, project.getAnnualEmploymentGeneral(), Style.CURRENCY);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary.employment.operation"));
		report.addNumericCell(row, cellNum, project.getAnnualEmploymentOperation(), Style.CURRENCY);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary.employment.total"));
		report.addFormulaCell(row, cellNum, String.format("SUM(%s%d:%s%d)", "D", startRow, "D", rowNum-1), Style.CURRENCY); 
		row = sheet.createRow(rowNum++);
		
	// Financial indicators
		if (project.getIncomeGen()) {
			row = sheet.createRow(rowNum++);
			// count years with negative flow
			ArrayList<ProjectFinanceData> data = ProjectFinanceData.analyzeProject(project, AnalysisType.CashFlow);
			ProjectFinanceData.AddLoanAmortization(project, data);
			ProjectFinanceData.AddWorkingCapital(project, data);
			ProjectFinanceData.CalculateCumulative(data);
			
			cellNum = 3;
			int yearsNeg=0;
			for(ProjectFinanceData pfd : data) { 
				if (pfd.getProfitAfterFinance()<0) { 
					yearsNeg++;
				}
			}
			report.addTextCell(row, 0, translate("project.report.summary.indicators"), Style.H2);

			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, String.format("%s (%s)", translate("project.irr"), translate("project.report.summary.indicators.total")));
			report.addNumericCell(row, cellNum, pr.getIrr().multiply(BigDecimal.valueOf(100.0)));

			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, String.format("%s (%s)", translate("project.npv"), translate("project.report.summary.indicators.total")));
			report.addNumericCell(row, cellNum, pr.getNpv(),Style.CURRENCY);

			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("project.report.summary.indicators.negativeFlow"));
			report.addNumericCell(row, cellNum, yearsNeg);
		}
		
		// Total costs - Breakdown by source
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary.costs.source"), Style.H2);
		getSummaryTitleRow(report, sheet, rowNum++, false);
		addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.donation", (pr.getWcDonated() + pr.getInvestmentDonated().doubleValue()), project.getExchRate(), true, rowNum+2);
		addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.ownResource", (pr.getWcOwn() + pr.getInvestmentOwn()), project.getExchRate(), true, rowNum+1);
		addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.financed", (pr.getWcFinanced() + pr.getInvestmentFinanced()), project.getExchRate(), true, rowNum);
		addSummaryTotal(report, sheet, rowNum++, project.getExchRate(), pr);


	//	Working Capital - Breakdown by source
		if (project.getIncomeGen()) {
			rowNum++;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("project.report.summary.wc.source"), Style.H2);
			getSummaryTitleRow(report, sheet, rowNum++, false);
			addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.donation", pr.getWcDonated(), project.getExchRate(), true, rowNum+2);
			addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.ownResource", pr.getWcOwn(), project.getExchRate(), true, rowNum+1);
			addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.financed", pr.getWcFinanced(), project.getExchRate(), true, rowNum);
			addSummaryTotal(report, sheet, rowNum++, project.getExchRate(), pr);

		}
		
	//	Investment - Breakdown by source
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary.investment.source"), Style.H2);
		getSummaryTitleRow(report, sheet, rowNum++, false);
		addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.donation", pr.getInvestmentDonated(), project.getExchRate(), true, rowNum+2);
		addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.ownResource", pr.getInvestmentOwn(), project.getExchRate(), true, rowNum+1);
		addSummaryCalculated(report, sheet, rowNum++, "project.report.summary.investment.source.financed", pr.getInvestmentFinanced(), project.getExchRate(), true, rowNum);
		addSummaryTotal(report, sheet, rowNum++, project.getExchRate(), pr);

		
		// Investment - Breakdown by type
		//ArrayList<Value> list = new ArrayList<Value>();
		double investAssets = 0.0;
		double investLabour = 0.0;
		double investService= 0.0;
		
		for (ProjectItem asset : project.getAssets()) {
			investAssets += asset.getUnitNum()*asset.getUnitCost();
		}
		for (ProjectItemLabour labour : project.getLabours()) {
			investLabour += labour.getUnitNum()*labour.getUnitCost();
		}
		for (ProjectItemService service : project.getServices()) {
			investService += service.getUnitNum()*service.getUnitCost();
		}
		row = sheet.createRow(rowNum++);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.summary.investment.type"), Style.H2);
		getSummaryTitleRow(report, sheet, rowNum++, false);
		addSummaryCalculated(report, sheet, rowNum++, "projectInvestAsset", investAssets, project.getExchRate(), true, rowNum+2);
		addSummaryCalculated(report, sheet, rowNum++, "projectInvestLabour", investLabour, project.getExchRate(), true, rowNum+1);
		addSummaryCalculated(report, sheet, rowNum++, "projectInvestService", investService, project.getExchRate(), true, rowNum);
		addSummaryTotal(report, sheet, rowNum++, project.getExchRate(), pr);

		
	// Investment per beneficiary (families)
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, String.format("%s (%s)", translate("project.report.summary.perBenef"), translate("project.benefFamilies")), Style.H2);
		getSummaryTitleRow(report, sheet, rowNum++, true);
		addSummaryCalculated(report, sheet, rowNum++, "project.step2.2", (pr.getInvestmentTotal()/project.getBeneDirectNum()), project.getExchRate(), false, 0);
		addSummaryCalculated(report, sheet, rowNum++, "project.step2.3", (pr.getInvestmentTotal()/project.getBeneIndirectNum()), project.getExchRate(), false, 0);

		
	// Investment per beneficiary (men + women + children)
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, String.format("%s (%s)", translate("project.report.summary.perBenef"), translate("project.report.summary.perBenef.menWomenChild")), Style.H2);
		getSummaryTitleRow(report, sheet, rowNum++, true);
		addSummaryCalculated(report, sheet, rowNum++, "project.step2.2", (pr.getInvestmentTotal()/pr.getBeneDirect()), project.getExchRate(), false, 0);
		addSummaryCalculated(report, sheet, rowNum++, "project.step2.3", (pr.getInvestmentTotal()/pr.getBeneIndirect()), project.getExchRate(), false, 0);		


		return sheet;
	}

	private Row addSummaryTotal(ExcelWrapper report, Sheet sheet, int rowNum, double exchRate, ProjectResult pr) {
		Row row = sheet.createRow(rowNum++);
		int cellNum = 0;
		report.addTextCell(row, cellNum++, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, cellNum++, String.format("SUM(B%d:B%d)",rowNum-3,rowNum-1) ,Style.CURRENCY);
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum++, String.format("B%d / %s", rowNum, "'"+sheetName(translate("project.report.general"))+"'!C3"), Style.CURRENCYUSD);
			} else {
			report.addFormulaCell(row, cellNum++, String.format("B%d / %f", rowNum, exchRate),Style.CURRENCYUSD);
		}
		report.addNumericCell(row, cellNum++, 1,Style.PERCENT);

		return row;
	}

	private Row addSummaryCalculated(ExcelWrapper report, Sheet sheet, int rowNum, String key, double value, double exchRate, boolean percentage, int totalRowNum) {
		Row row = sheet.createRow(rowNum++);
		int cellNum = 0;
		report.addTextCell(row, cellNum++, translate(key));
		report.addNumericCell(row, cellNum++, value, Style.CURRENCY);
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum++, String.format("B%d / %s", rowNum, "'"+sheetName(translate("project.report.general"))+"'!C3"), Style.CURRENCYUSD);
		} else {
			report.addFormulaCell(row, cellNum++, String.format("B%d / %f", rowNum, exchRate), Style.CURRENCYUSD);
		}
		if (percentage) {
			report.addFormulaCell(row, cellNum++, String.format("B%d / B%d", rowNum, totalRowNum+1),Style.PERCENT);
		}
		return row;
	}
	
	private Row getSummaryTitleRow(ExcelWrapper report, Sheet sheet, int rowNum, boolean beneficiary) {

		Row row = sheet.createRow(rowNum);
		int cellNum = 0;
		String amount = translate("project.report.summary.amount");
		String title = beneficiary ? "project.report.summary.perBenef.type" : "project.report.summary.type";
		report.addTextCell(row, cellNum++, translate(title), Style.LABEL);
		report.addTextCell(row, cellNum++, String.format("%s (%s)", amount, rivConfig.getSetting().getCurrencySym()), Style.LABEL);
		report.addTextCell(row, cellNum++, String.format("%s (%s)", amount, translate("units.USD")), Style.LABEL);
		if (!beneficiary)	{
			report.addTextCell(row, cellNum++, translate("project.report.summary.percent"), Style.LABEL);

		}
		return row;
	}
	
	public Sheet getProjectSustainability(ExcelWrapper report, Project project, ProjectResult result) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.cashFlowNongen")));

		sheet.setSelected(true);
		
		ArrayList<ProjectFinanceNongen> data = ProjectFinanceNongen.analyzeProject(project);
		
		int rowNum=0;
		int cellNum = 0;

		String netBalance = translate("project.report.cashFlowNongen.netBalance");

		String[] rowHeaderIncome = new String[] {"project.report.cashFlowNongen.investDonated", 
				"project.report.cashFlowNongen.investOwn", "project.report.cashFlowNongen.salvage", 
				"project.report.cashFlowNongen.userCharges", "project.report.cashFlowNongen.contributionsGeneral", 
				"project.report.cashFlowNongen.contributions"};
		
		String[] rowHeaderCosts = new String[] {"project.report.cashFlowNongen.investCosts", 
				"project.report.cashFlowNongen.replacement", "project.report.cashFlowNongen.investMaint", 
				"project.report.cashFlowNongen.operation", "project.report.cashFlowNongen.general"};

		
		Row[] rows = new Row[rowHeaderIncome.length + rowHeaderCosts.length];
				
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.cashFlowNongen"), Style.TITLE);

		
		Row firstRow = sheet.createRow(rowNum++);
		sheet.setColumnWidth(0, 187*36);
		report.addTextCell(firstRow, 0, translate("reference.incomes"), Style.H2);
		rowNum = addRows(report, sheet, rows, rowHeaderIncome, rowNum++, 0);

		Row rowTotal1 = sheet.createRow(rowNum++);
		report.addTextCell(rowTotal1, 0, translate("misc.total"), Style.LABEL);

		
		
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.cashFlowNongen.costs"), Style.H2);
		rowNum = addRows(report, sheet, rows, rowHeaderCosts, rowNum++, rowHeaderIncome.length);

		Row rowTotal2 = sheet.createRow(rowNum++);
		report.addTextCell(rowTotal2, 0, translate("misc.total"), Style.LABEL);

		
		Row rowBalance = sheet.createRow(rowNum++);
		report.addTextCell(rowBalance, 0, netBalance, Style.LABEL);


		cellNum = 1;
		for (ProjectFinanceNongen pfn : data) {
			String col = getColumn(cellNum);
//			if (data.size() > COLS.length) {
//				int mod = cellNum % COLS.length;
//				int colIndex = (int) ( (data.size() - mod) / COLS.length);
//				col = getColumn(colIndex] + getColumn(mod];
//			} else {
//				col = getColumn(cellNum];
//			}
			
			report.addNumericCell(firstRow, cellNum, cellNum);
			
			// Income
			report.addNumericCell(rows[0], cellNum, pfn.getDonated(), Style.CURRENCY);
			report.addNumericCell(rows[1], cellNum, pfn.getOwnContribution(), Style.CURRENCY);
			report.addNumericCell(rows[2], cellNum, pfn.getSalvage(), Style.CURRENCY);
			report.addNumericCell(rows[3], cellNum, pfn.getCharges(), Style.CURRENCY);
			report.addNumericCell(rows[4], cellNum, pfn.getContributionsGeneral(), Style.CURRENCY);
			report.addNumericCell(rows[5], cellNum, pfn.getContributions(), Style.CURRENCY);

			
			// Income total
			report.addFormulaCell(rowTotal1, cellNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 3, 8), Style.CURRENCY);

			
			// Costs
			report.addNumericCell(rows[6], cellNum, pfn.getCostInvest(), Style.CURRENCY);
			report.addNumericCell(rows[7], cellNum, pfn.getReplace(), Style.CURRENCY);
			report.addNumericCell(rows[8], cellNum, pfn.getMaintenance(), Style.CURRENCY);
			report.addNumericCell(rows[9], cellNum, pfn.getOperation(), Style.CURRENCY);
			report.addNumericCell(rows[10], cellNum, pfn.getGeneral(), Style.CURRENCY);

			
			// Costs total
			report.addFormulaCell(rowTotal2, cellNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 12, 16), Style.CURRENCY);

			
			// Net Balance
			report.addFormulaCell(rowBalance, cellNum, String.format("%1$s9-%1$s17", col), Style.CURRENCY);

			
			cellNum++;
		}
		
		return sheet;
	}
	
	private int addRows(ExcelWrapper report, Sheet sheet, Row[] rows, String[] titles, int rowNum, int index) {

		for (String title : titles) {
			Row row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate(title));

			rows[index++] = row;
		}
		return rowNum;
	}
	
	public Sheet getProjectContributions(ExcelWrapper report, Project project, boolean template) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.step10.nongen")));

		sheet.setSelected(true);
		int rowNum=0;
		int cellNum = 0;
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.step10.nongen"), Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("projectContribution"), Style.H2);
		String[] header = new String[]{"projectContribution.description","projectContribution.contribType","projectContribution.unitType","projectContribution.unitNum","projectContribution.unitCost","projectContribution.totalCost"};
		XlsTable table = new XlsTable(report, header)
		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addSelectColumnIntBased("getContribType", getContribTypes())
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "DX*EX", true);
		table.writeTable(sheet, rowNum, template ? null : project.getContributions(), true);

		
		return sheet;
	}
	
	/*
	 * Creates rows and adds title in first cell of each row.
	 * The first title is given H2 style, other rows NORMAL.
	 */
	private int addRowTitles(String[] titles, int rowStart, Sheet sheet, ExcelWrapper report) {
		Row row = sheet.createRow(rowStart);
		report.addTextCell(row, 0, translate(titles[0]), Style.H2);
		for (int i=1; i<titles.length; i++) {
			row = sheet.createRow(rowStart+i);
			report.addTextCell(row, 0, translate(titles[i]));
		}
		return rowStart+titles.length+1;
	}
	
	public Sheet getProjectCashFlow2(ExcelWrapper report, Project project, ProjectResult result) {
		ArrayList<ProjectFinanceData> data = ProjectFinanceData.analyzeProject(project, AnalysisType.CashFlow);
		ProjectFinanceData.AddLoanAmortization(project, data);
		ProjectFinanceData.AddWorkingCapital(project, data);
		ProjectFinanceData.CalculateCumulative(data);
		
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.cashFlow")));
		sheet.setSelected(true);
		
		// setup header and row titles
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.cashFlow"), Style.TITLE);
		sheet.setColumnWidth(0, 165*36);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectBlock.pattern.years"), Style.LABEL);
		for (short index=1; index <= data.size(); index++) {
			report.addNumericCell(row, index, index);
			sheet.setColumnWidth(index, 90*36);
		}

		rowNum = addRowTitles(new String[] {"project.report.profitability.incomes","project.report.profitability.incomes.sales", "project.report.profitability.incomes.salvage", 
				"project.report.profitability.incomes.capDonation","misc.total"}, rowNum++, sheet, report);
		
		rowNum = addRowTitles(new String[] {"project.report.profitability.costs","project.report.profitability.costs.operation", "project.report.profitability.costs.replacement", "project.report.profitability.costs.general",
				 "project.report.profitability.costs.maintenance","misc.total","project.report.cashFlow.profitBefore"}, rowNum++, sheet, report);
		
		rowNum = addRowTitles(new String[] {"project.report.cashFlow.financing","project.report.cashFlow.wcInterest", "project.report.cashFlow.priCapital", "project.report.cashFlow.priInterest",
				"project.report.cashFlow.secCapital", "project.report.cashFlow.secInterest", "project.report.cashFlow.profitAfter", "project.report.cashFlow.cumulative"}, rowNum++, sheet, report);
		
		// hidden rows for repayment amortization helper rows
		for (int i=rowNum;i<rowNum+7;i++) {
			sheet.createRow(i);
		}
		
		// real data
		int yearNum=0;
		String col;
		StringBuilder formulaBuild;
		for (ProjectFinanceData pfd : data) {
			yearNum++;
			 col = getColumn(yearNum);
			if (report.isCompleteReport()) {
				// income
				formulaBuild = new StringBuilder();
				for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
					if (yearNum==1) {
						formulaBuild.append("("+blockLink.incomeCash+"*"+blockLink.cyclesFirstYearPayment+"*"+blockLink.qtyPerYear[yearNum-1]+")+");
					} else {
						formulaBuild.append("("+blockLink.incomeCash+"*"+blockLink.cycles+"*"+blockLink.qtyPerYear[yearNum-1]+")+");
					}
				}
				report.addFormulaCell(sheet.getRow(3), yearNum, formulaBuild.deleteCharAt(formulaBuild.length()-1).toString(), Style.CURRENCY);
				
				
				formulaBuild = new StringBuilder();
				int firstRow = Integer.parseInt(report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW));
				String assetSheetName = report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
				for (int i=firstRow; i<firstRow+project.getAssets().size();i++) {
					formulaBuild.append(
						"IF(AND(" +col+"2<>"+assetSheetName+"!$M$"+i+
						",MOD("+col+"2-"+assetSheetName+"!$M$"+i+","+assetSheetName+"!$I$"+i+")=0)" +
						", "+assetSheetName+"!$C$"+i+"*"+assetSheetName+"!$K$"+i+", 0)+"
					);
				}
				report.addFormulaCell(sheet.getRow(4), yearNum, formulaBuild.deleteCharAt(formulaBuild.length()-1).toString(), Style.CURRENCY);
			
				if (yearNum==1) {
					report.addFormulaCell(sheet.getRow(5), yearNum, "'"+sheetName(translate("project.report.parameters"))+"'!B25", Style.CURRENCY);
				} else {
					report.addNumericCell(sheet.getRow(5), yearNum, 0, Style.CURRENCY);
				}
				report.addFormulaCell(sheet.getRow(6), yearNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 4, 6), Style.CURRENCY);	//	pfd.getTotalIncome() - pfd.getIncSalesInternal() - pfd.getIncResidual()
				
				// costs
				formulaBuild = new StringBuilder();
				for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
					if (yearNum==1) {
						formulaBuild.append("("+blockLink.costCash+"*"+blockLink.cyclesFirstYearProduction+"*"+blockLink.qtyPerYear[yearNum-1]+")+");
					} else {
						formulaBuild.append("("+blockLink.costCash+"*"+blockLink.cycles+"*"+blockLink.qtyPerYear[yearNum-1]+")+");
					}
				}
				report.addFormulaCell(sheet.getRow(9), yearNum, formulaBuild.deleteCharAt(formulaBuild.length()-1).toString(), Style.CURRENCY);

				formulaBuild = new StringBuilder();
				firstRow = Integer.parseInt(report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW));
				for (int i=firstRow; i<firstRow+project.getAssets().size();i++) {
					formulaBuild.append(
						"IF(" +
						"AND(" +col+"2<>"+assetSheetName+"!$M$"+i+
						",MOD("+col+"2-"+assetSheetName+"!$M$"+i+","+assetSheetName+"!$I$"+i+")=0), "+assetSheetName+"!C"+i+"*"+assetSheetName+"!D"+i+", 0)+"
					);
				}
				report.addFormulaCell(sheet.getRow(10), yearNum, formulaBuild.deleteCharAt(formulaBuild.length()-1).toString(), Style.CURRENCY);

				report.addFormulaCell(sheet.getRow(11), yearNum, report.getLinks().get(ExcelLink.PROJECT_GENERAL_CASH), Style.CURRENCY);
				
				formulaBuild = new StringBuilder();
				firstRow = Integer.parseInt(report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW));
				for (int i=firstRow; i<firstRow+project.getAssets().size();i++) {
					formulaBuild.append(
					"IF("+assetSheetName+"!$M$"+i+"<="+ yearNum +", "+assetSheetName+"!$C$"+i+"*"+assetSheetName+"!$J$"+i+",0)+"
					);
				}
				report.addFormulaCell(sheet.getRow(12), yearNum, formulaBuild.deleteCharAt(formulaBuild.length()-1).toString(), Style.CURRENCY);

				report.addFormulaCell(sheet.getRow(13), yearNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 10, 13), Style.CURRENCY);
				report.addFormulaCell(sheet.getRow(14), yearNum, String.format("%1$s%2$d-%1$s%3$d", col, 7, 14), Style.CURRENCY);
				
				// financing
				report.addNumericCell(sheet.getRow(17), yearNum, pfd.getWorkingCapital(), Style.CURRENCY);
				
				String formula = String.format("IF(%s2<=%s,0,IF(%s2>=%s+%s,0,PPMT((%s-%s)/100,%s2-%s,%s-%s,-MIN(%s27,%s),0)))"
						, col, report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL), col,
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_DURATION), report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL),
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_RATE), report.getLinks().get(ExcelLink.PROJECT_INFLATION), col,
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL), report.getLinks().get(ExcelLink.PROJECT_LOAN1_DURATION),
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL), col, report.getLinks().get(ExcelLink.PROJECT_LOAN1_AMOUNT)
						);
				report.addFormulaCell(sheet.getRow(18), yearNum, formula, Style.CURRENCY);
				
				formula = String.format("%s26-%s19+%s28+%s29", col, col, col, col);
				report.addFormulaCell(sheet.getRow(19), yearNum, formula, Style.CURRENCY);
				
				
				report.addNumericCell(sheet.getRow(20), yearNum, pfd.getLoan2capital(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(21), yearNum, pfd.getLoan2interest(), Style.CURRENCY);
				report.addFormulaCell(sheet.getRow(22), yearNum, String.format("%1$s%2$d-SUM(%1$s%3$d:%1$s%4$d)", col, 15, 18, 22), Style.CURRENCY);
				formula = col.equals("B") ? "B23"
						: String.format("%1$s%2$d+%3$s%4$d", getColumn(yearNum-1), 24, col, 23);
				report.addFormulaCell(sheet.getRow(23), yearNum, formula, Style.CURRENCY);
				

				// financing helper rows
				formula = String.format("IF(%s2<=%s,0,IF(%s2>=%s+%s,0,PMT((%s-%s)/100,%s-%s,-MIN(%s27,%s),0)))"
						,col, report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL), col,
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_DURATION), report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL),
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_RATE), report.getLinks().get(ExcelLink.PROJECT_INFLATION),
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_DURATION), report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL),
						col, report.getLinks().get(ExcelLink.PROJECT_LOAN1_AMOUNT)
						);
				report.addFormulaCell(sheet.getRow(25), yearNum, formula, Style.CURRENCY);
				
				
				
				formula = yearNum==1 ? report.getLinks().get(ExcelLink.PROJECT_LOAN1_AMOUNT) : String.format("%s27+%s28", getColumn(yearNum-1), getColumn(yearNum-1));
				report.addFormulaCell(sheet.getRow(26), yearNum, formula, Style.CURRENCY);
				
				formula = String.format("IF(%s2<=%s,%s27*(%s-%s)/100,0)",
						col, report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_INTEREST), col,
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_RATE), report.getLinks().get(ExcelLink.PROJECT_INFLATION)
						);
				report.addFormulaCell(sheet.getRow(27), yearNum, formula, Style.CURRENCY);
				
				formula = String.format("IF(%s28<0,0,IF(%s2<=%s,%s27*(%s-%s)/100,0))",
						col, col, report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL), col,
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_RATE), report.getLinks().get(ExcelLink.PROJECT_INFLATION)
						);
				report.addFormulaCell(sheet.getRow(28), yearNum, formula, Style.CURRENCY);
				
			} else {
				// income
				report.addNumericCell(sheet.getRow(3), yearNum, pfd.getIncSales() - pfd.getIncSalesInternal(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(4), yearNum, pfd.getIncSalvage(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(5), yearNum, pfd.getIncCapitalDonation(), Style.CURRENCY);
				report.addFormulaCell(sheet.getRow(6), yearNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 4, 6), Style.CURRENCY);	//	pfd.getTotalIncome() - pfd.getIncSalesInternal() - pfd.getIncResidual()
				
				// costs
				report.addNumericCell(sheet.getRow(9), yearNum, pfd.getCostOperation() - pfd.getCostOperationInternal(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(10), yearNum, pfd.getCostReplace(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(11), yearNum, pfd.getCostGeneral()-pfd.getCostGeneralOwn(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(12), yearNum, pfd.getCostMaintenance(), Style.CURRENCY);
				report.addFormulaCell(sheet.getRow(13), yearNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 10, 13), Style.CURRENCY);
				report.addFormulaCell(sheet.getRow(14), yearNum, String.format("%1$s%2$d-%1$s%3$d", col, 7, 14), Style.CURRENCY);
				
				// financing
				report.addNumericCell(sheet.getRow(17), yearNum, pfd.getWorkingCapital(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(18), yearNum, pfd.getLoan1capital(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(19), yearNum, pfd.getLoan1interest(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(20), yearNum, pfd.getLoan2capital(), Style.CURRENCY);
				report.addNumericCell(sheet.getRow(21), yearNum, pfd.getLoan2interest(), Style.CURRENCY);
				report.addFormulaCell(sheet.getRow(22), yearNum, String.format("%1$s%2$d-SUM(%1$s%3$d:%1$s%4$d)", col, 15, 18, 22), Style.CURRENCY);
				String cumFormula = col.equals("B") ? "B23"
						: String.format("%1$s%2$d+%3$s%4$d", getColumn(yearNum-1), 24, col, 23);
				report.addFormulaCell(sheet.getRow(23), yearNum, cumFormula, Style.CURRENCY);
			}
		}
		
		
		return sheet;
	}

	
	public Sheet getProjectCashFlow(ExcelWrapper report, Project project, ProjectResult result) {

		ArrayList<ProjectFinanceData> data = ProjectFinanceData.analyzeProject(project, AnalysisType.CashFlow);
		ProjectFinanceData.AddLoanAmortization(project, data);
		ProjectFinanceData.AddWorkingCapital(project, data);
		ProjectFinanceData.CalculateCumulative(data);
		
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.cashFlow")));

		sheet.setSelected(true);
		
		
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.cashFlow"), Style.TITLE);

		
		sheet.setColumnWidth(0, 165*36);
		
		String[] incomeHeader = new String[] {"project.report.profitability.incomes.sales", "project.report.profitability.incomes.salvage", 
				"project.report.profitability.incomes.capDonation"};
		String[] costsHeader = new String[] {"project.report.profitability.costs.operation", "project.report.profitability.costs.replacement", "project.report.profitability.costs.general",
				 "project.report.profitability.costs.maintenance"};
		String[] financingHeader = new String[] {"project.report.cashFlow.wcInterest", "project.report.cashFlow.priCapital", "project.report.cashFlow.priInterest",
				"project.report.cashFlow.secCapital", "project.report.cashFlow.secInterest", "project.report.cashFlow.profitAfter", "project.report.cashFlow.cumulative"};
		
		Row[] rows = new Row[incomeHeader.length + costsHeader.length + financingHeader.length];
		
		row = sheet.createRow(rowNum);
		report.addTextCell(row, 0, translate("projectBlock.pattern.years"), Style.LABEL);
		for (short index=1; index <= data.size(); index++) {
			report.addTextCell(row, index, String.valueOf(index));
			sheet.setColumnWidth(index, 90*36);
		}
		
	// Income
		row = sheet.createRow(++rowNum);
		report.addTextCell(row, 0, translate("project.report.profitability.incomes"), Style.H2);


		rowNum = addRows(report, sheet, rows, incomeHeader, ++rowNum, 0);
		
		Row rowTotalIncome = sheet.createRow(rowNum++);
		report.addTextCell(rowTotalIncome, 0, translate("misc.total"));

		
	// Costs
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.profitability.costs"), Style.H2);

		
		rowNum = addRows(report, sheet, rows, costsHeader, rowNum++, incomeHeader.length);
		
		Row rowTotalCosts = sheet.createRow(rowNum++);
		report.addTextCell(rowTotalCosts, 0, translate("misc.total"));


		
		Row rowProfitBefore = sheet.createRow(rowNum++);
		report.addTextCell(rowProfitBefore, 0, translate("project.report.cashFlow.profitBefore"));

	
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.cashFlow.financing"), Style.H2);


		rowNum = addRows(report, sheet, rows, financingHeader, rowNum++, incomeHeader.length + costsHeader.length);
	
	
		int cellNum = 1;
		for (ProjectFinanceData pfd : data) {
			String col = getColumn(cellNum); 
			
			int index = 0;
			
			// Income
			if (report.isCompleteReport()) {
				StringBuilder formula = new StringBuilder();
				for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
					formula.append("("+blockLink.incomeCash+"*"+blockLink.cycles+"*"+blockLink.qtyPerYear[cellNum-1]+")+");
				}
				report.addFormulaCell(rows[index++], cellNum, formula.deleteCharAt(formula.length()-1).toString(), Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getIncSales() - pfd.getIncSalesInternal(), Style.CURRENCY);
			}
			
			if (report.isCompleteReport()) {
				StringBuilder formula = new StringBuilder();
				int firstRow = Integer.parseInt(report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW));
				String sheetName = report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
				for (int i=firstRow; i<firstRow+project.getAssets().size();i++) {
					formula.append(
						"IF(" +
						"AND(" +getColumn(cellNum)+"2/1<>"+sheetName+"!M"+i+
						",MOD("+getColumn(cellNum)+"2-"+sheetName+"!M"+i+","+sheetName+"!I"+i+")=0)" +
						", "+sheetName+"!C"+i+"*"+sheetName+"!K"+i+", 0)+"
					);
				}
				report.addFormulaCell(rows[index++], cellNum, formula.deleteCharAt(formula.length()-1).toString(), Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getIncSalvage(), Style.CURRENCY);
			}
			
			if (report.isCompleteReport()) {
				report.addFormulaCell(rows[index++], cellNum, "'"+sheetName(translate("project.report.parameters"))+"'!B25", Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getIncCapitalDonation(), Style.CURRENCY);
			}
			
			// Income total
			report.addFormulaCell(rowTotalIncome, cellNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 4, 6), Style.CURRENCY);	//	pfd.getTotalIncome() - pfd.getIncSalesInternal() - pfd.getIncResidual()
	
				
			// Costs
			if (report.isCompleteReport()) {
				StringBuilder formula = new StringBuilder();
				for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
					formula.append("("+blockLink.costCash+"*"+blockLink.cycles+"*"+blockLink.qtyPerYear[cellNum-1]+")+");
				}
				report.addFormulaCell(rows[index++], cellNum, formula.deleteCharAt(formula.length()-1).toString(), Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getCostOperation() - pfd.getCostOperationInternal(), Style.CURRENCY);
			}
			
			
			if (report.isCompleteReport()) {
				StringBuilder formula = new StringBuilder();
				int firstRow = Integer.parseInt(report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW));
				String sheetName = report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
				for (int i=firstRow; i<firstRow+project.getAssets().size();i++) {
					formula.append(
						"IF(" +
						"AND(" +getColumn(cellNum)+"2/1<>"+sheetName+"!M"+i+
						",MOD("+getColumn(cellNum)+"2-"+sheetName+"!M"+i+","+sheetName+"!I"+i+")=0), "+sheetName+"!C"+i+"*"+sheetName+"!D"+i+", 0)+"
					);
				}
				report.addFormulaCell(rows[index++], cellNum, formula.deleteCharAt(formula.length()-1).toString(), Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getCostReplace(), Style.CURRENCY);
			}
			
			
			if (report.isCompleteReport()) {
				report.addFormulaCell(rows[index++], cellNum, report.getLinks().get(ExcelLink.PROJECT_GENERAL_CASH), Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getCostGeneral()-pfd.getCostGeneralOwn(), Style.CURRENCY);
			}
			
			if (report.isCompleteReport()) {
				StringBuilder formula = new StringBuilder();
				int firstRow = Integer.parseInt(report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW));
				String sheetName = report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
				for (int i=firstRow; i<firstRow+project.getAssets().size();i++) {
					formula.append(
					"IF("+sheetName+"!M"+i+"<="+ cellNum +", "+sheetName+"!C"+i+"*"+sheetName+"!J"+i+",0)+"
					);
				}
				report.addFormulaCell(rows[index++], cellNum, formula.deleteCharAt(formula.length()-1).toString(), Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getCostMaintenance(), Style.CURRENCY);
			}
	
			// Costs total
			report.addFormulaCell(rowTotalCosts, cellNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 10, 13), Style.CURRENCY);
				
			// Profit before financing
			report.addFormulaCell(rowProfitBefore, cellNum, String.format("%1$s%2$d-%1$s%3$d", col, 7, 14), Style.CURRENCY);
	
				
			// Financing
			report.addNumericCell(rows[index++], cellNum, pfd.getWorkingCapital(), Style.CURRENCY);
			
			if (report.isCompleteReport()) {
				String formula = String.format(
						"IF(%s2<=%s,0,IF(%s2>=%s+%s,0,PPMT((%s-%s)/100,%s2-%s,%s-%s,-MIN(%s2,%s),0)))",
						col, report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL), getColumn(cellNum-1),
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_DURATION), report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL),
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_RATE), report.getLinks().get(ExcelLink.PROJECT_INFLATION),
						getColumn(cellNum-1), report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL), 
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_DURATION), report.getLinks().get(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL),
						getColumn(cellNum-1),
						report.getLinks().get(ExcelLink.PROJECT_LOAN1_AMOUNT));
				report.addFormulaCell(rows[index++], cellNum, formula, Style.CURRENCY);
			} else {
				report.addNumericCell(rows[index++], cellNum, pfd.getLoan1capital(), Style.CURRENCY);
			}
			
			report.addNumericCell(rows[index++], cellNum, pfd.getLoan1interest(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getLoan2capital(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getLoan2interest(), Style.CURRENCY);
			report.addFormulaCell(rows[index++], cellNum, String.format("%1$s%2$d-SUM(%1$s%3$d:%1$s%4$d)", col, 15, 18, 22), Style.CURRENCY);

			String formula = col.equals("B") ? "B23"
					: String.format("%1$s%2$d+%3$s%4$d", getColumn(cellNum-1), 24, col, 23);
			report.addFormulaCell(rows[index++], cellNum, formula, Style.CURRENCY);

			
			// financing - hidden rows
			if (report.isCompleteReport()) {
				rowNum++;
				
				// primary total payment
				formula = String.format("");
				report.addFormulaCell(rows[index++], cellNum, formula);
				
				
				
			}
			
			
			cellNum++;
		}
		
		return sheet;
	}

	public Sheet getProjectProfitability(ExcelWrapper report, Project project, ProjectResult pr) {

		ArrayList<ProjectFinanceData> data = ProjectFinanceData.analyzeProject(project, AnalysisType.TotalCosts);
		ProjectFinanceData.AddLoanAmortization(project, data);
		ProjectFinanceData.AddWorkingCapital(project, data);
		ProjectFinanceData.CalculateCumulative(data);
		
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.profitability")));

		sheet.setSelected(true);

		int rowNum=0;
		
		sheet.setColumnWidth(0, 165*36);
		
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.profitability"), Style.TITLE);

		
		String[] incomeHeader = new String[] {"project.report.profitability.incomes.sales", "project.report.profitability.incomes.salvage",
				"project.report.profitability.incomes.residual"};
		String[] costsHeader = new String[] {"project.report.profitability.costs.operation", "project.report.profitability.costs.replacement",
				"project.report.profitability.costs.general", "project.report.profitability.costs.maintenance", "project.report.profitability.costs.investment"};
		String[] donationHeaders = new String[] {"project.report.profitability.donations.wc","project.report.profitability.donations.investment"};
		       
		Row[] rows = new Row[incomeHeader.length + costsHeader.length + donationHeaders.length];
		
		row = sheet.createRow(rowNum);
		for (int index=1; index <= data.size(); index++) {
			report.addNumericCell(row, index, index).setCellStyle(report.getStyles().get(Style.DATE));
			sheet.setDefaultColumnStyle(index, report.getStyles().get(Style.CURRENCY));

			sheet.setColumnWidth(index, 90*36);
		}
		
		// Income
		row = sheet.createRow(++rowNum);
		report.addTextCell(row, 0, translate("project.report.profitability.incomes"), Style.H2);

		
		rowNum = addRows(report, sheet, rows, incomeHeader, ++rowNum, 0);
		
		Row rowTotalIncome = sheet.createRow(rowNum++);
		report.addTextCell(rowTotalIncome, 0, translate("misc.total"), Style.LABEL);

		
		// Costs
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.profitability.costs"), Style.H2);

		
		rowNum = addRows(report, sheet, rows, costsHeader, rowNum++, incomeHeader.length);
		
		Row rowTotalCosts = sheet.createRow(rowNum++);
		report.addTextCell(rowTotalCosts, 0, translate("misc.total"), Style.LABEL);

		
		row = sheet.createRow(rowNum++);
		Row rowNetIncome = sheet.createRow(rowNum++);
		report.addTextCell(rowNetIncome, 0, translate("project.report.profitability.donations.netBefore"), Style.LABEL);

	
		// Donations
		row = sheet.createRow(rowNum++); row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.profitability.donations"), Style.H2);

		
		rowNum = addRows(report, sheet, rows, donationHeaders, rowNum++, costsHeader.length+incomeHeader.length);
		
		Row rowTotalDonations = sheet.createRow(rowNum++);
		report.addTextCell(rowTotalDonations, 0, translate("misc.total"), Style.LABEL);

		
		Row rowNetIncomeWithDonation = sheet.createRow(rowNum++);
		report.addTextCell(rowNetIncomeWithDonation, 0, translate("project.report.profitability.donations.netAfter"), Style.LABEL);

		
		int cellNum = 1;
		for (ProjectFinanceData pfd : data) {
			String col = getColumn(cellNum);
			
			int index = 0;
			
			// Income
			report.addNumericCell(rows[index++], cellNum, pfd.getIncSales()-pfd.getIncSalesWithout(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getIncSalvage(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getIncResidual(), Style.CURRENCY);

			
			// Income total
			report.addFormulaCell(rowTotalIncome, cellNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 4, 6), Style.CURRENCY);

			
			// Costs
			report.addNumericCell(rows[index++], cellNum, pfd.getCostOperation()-pfd.getCostOperationWithout(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getCostReplace(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getCostGeneral()-pfd.getCostGeneralWithout(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getCostMaintenance(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getCostInvest(), Style.CURRENCY);

			
			// Costs total
			report.addFormulaCell(rowTotalCosts, cellNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 10, 14), Style.CURRENCY);

			
			// Net Balance
			report.addFormulaCell(rowNetIncome, cellNum, String.format("SUM(%1$s7-%1$s15)", col), Style.CURRENCY);

			
			// Donations
			report.addNumericCell(rows[index++], cellNum, pfd.getIncCapitalDonation(), Style.CURRENCY);
			report.addNumericCell(rows[index++], cellNum, pfd.getCostInvestDonated(), Style.CURRENCY);

			
			// Donation total
			report.addFormulaCell(rowTotalDonations, cellNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 20, 21), Style.CURRENCY);

			
			// Net balance with donation
			report.addFormulaCell(rowNetIncomeWithDonation, cellNum, String.format("SUM(%1$s17+%1$s22)", col), Style.CURRENCY);

			
			cellNum++;
		}
		
		
		// Indicators
		int tind = 4;
		row = sheet.createRow(++rowNum);	row = sheet.createRow(++rowNum);
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, data.size()));
		report.addTextCell(row, 0, translate("project.report.profitability.indicators"), Style.H2);

		
		row = sheet.createRow(++rowNum);
		report.addTextCell(row, 1, translate("project.report.profitability.totInvestAllCosts"), Style.H2);
		report.addTextCell(row, tind, translate("project.report.profitability.totInvestApplicant"), Style.H2);
		
		/*double npvNoDonation = pr.getNpv();
		BigDecimal irrNoDonation = pr.getIrr();
		double npv = pr.getNpvWithDonation();
		BigDecimal irr = pr.getIrrWithDonation();*/

		row = sheet.createRow(++rowNum);	cellNum = 0;
		report.addTextCell(row, 0, translate("project.irr.long"));

		
		report.addFormulaCell(row, 1, String.format("IRR(B17:%1$s17)", getColumn(data.size())),Style.PERCENT);
		report.addFormulaCell(row, tind, String.format("IRR(B23:%1$s23)", getColumn(data.size())),Style.PERCENT);


		/*if (irr.doubleValue()*100 >= 1000.0 || irr.doubleValue()*100 <= -1000.0) {//irr.compareTo(new BigDecimal(10)) <= 0) {
			report.addTextCell(row, 1, translate("misc.undefined"));
		} else {
			report.addNumericCell(row, 1, irr).setCellStyle(percStyle);
		}
		
		if (irrNoDonation.doubleValue()*100 >= 1000.0 || irrNoDonation.doubleValue()*100 <= -1000.0) {
			report.addTextCell(row, tind, translate("misc.undefined"));
		} else {
			report.addNumericCell(row, tind, irrNoDonation).setCellStyle(percStyle);
		}*/
		
		
		row = sheet.createRow(++rowNum);	cellNum = 0;
		report.addTextCell(row, 0, translate("project.npv.long"));
		
		report.addFormulaCell(row, 1, String.format("NPV(%1$f,B17:%2$s17)", rivConfig.getSetting().getDiscountRate()/100, getColumn(data.size())));
		report.addFormulaCell(row, tind, String.format("NPV(%1$f,B23:%2$s23)", rivConfig.getSetting().getDiscountRate()/100, getColumn(data.size())));
		
		/*report.addNumericCell(row, 1, npv);
		report.addNumericCell(row, tind, npvNoDonation);*/
		
		return sheet;
	}

	public Sheet getProjectCashFlowFirst(ExcelWrapper report, Project project, ProjectResult result) {

		ProjectFirstYear pfy = new ProjectFirstYear(project);
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.cashFlowFirst")));

		sheet.setSelected(true);
		int rowNum=0;
		int[] firstRows = new int[2];
		sheet.setColumnWidth(0, 165*36);
		
		for (int i = 1; i <= 13; i++) {
			sheet.setColumnWidth(i, 90*36);
		}
		
		Row row = sheet.createRow(rowNum);
		report.addTextCell(row, 0, translate("project.report.cashFlowFirst"), Style.TITLE);

		
		row = sheet.createRow(++rowNum);
		int cellNum = 0;
		cellNum++;
		for (String month : months(messageSource, project)) {
			report.addTextCell(row, cellNum++, month, Style.LABEL);

		}
		report.addTextCell(row, cellNum++, translate("misc.total"), Style.LABEL);

		
		// Operation income
		firstRows[0]=rowNum+3;
		
		if (report.isCompleteReport()) {
			row = sheet.createRow(++rowNum);
			report.addTextCell(row, 0, translate("project.report.cashFlowFirst.operIncomes"), Style.H2);
			String chronSheet = "'"+sheetName(translate("project.report.chronology"))+"'";
			
			for (Block block : project.getBlocks()) {
				ExcelBlockLink blockLink = report.getBlockLinks().get(block.getBlockId());
				row = sheet.createRow(++rowNum);
				report.addTextCell(row, 0, block.getDescription());
				for (int month=0;month<12;month++) {
					String formula=
							blockLink.incomeCash+"/COUNTIF("+chronSheet+"!B"+blockLink.paymentRow+":Y"+blockLink.paymentRow+",\"#\")" +
							"*COUNTIF("+chronSheet+"!"+getColumn(month*2+1)+blockLink.paymentRow+":"+getColumn(month*2+2)+blockLink.paymentRow+",\"#\")" +
							"*"+blockLink.qtyPerYear[0]
							+"*"+blockLink.cyclesFirstYearPayment;
					report.addFormulaCell(row, month+1, formula, Style.CURRENCY);
				}
				report.addFormulaCell(row, 13, writeFormula("SUM(BX:MX)", rowNum+1), Style.CURRENCY);
			}
			
		} else {
			rowNum = addCashFlowRow(report, sheet, rowNum, 
					translate("project.report.cashFlowFirst.operIncomes"), pfy.getIncomes());
		}
		
		row = sheet.createRow(++rowNum);
		
		
		// Operation costs
		firstRows[1]=rowNum+2;
		
		if (report.isCompleteReport()) {
			row = sheet.createRow(++rowNum);
			report.addTextCell(row, 0, translate("project.report.cashFlowFirst.operCosts"), Style.H2);
			
			String chronSheet = "'"+sheetName(translate("project.report.chronology"))+"'";
			
			for (Block block : project.getBlocks()) {
				ExcelBlockLink blockLink = report.getBlockLinks().get(block.getBlockId());
				row = sheet.createRow(++rowNum);
				report.addTextCell(row, 0, block.getDescription());
				for (int month=0;month<12;month++) {
					String formula=
							blockLink.costCash+"/COUNTIF("+chronSheet+"!B"+blockLink.productionRow+":Y"+blockLink.productionRow+",\"#\")" +
							"*COUNTIF("+chronSheet+"!"+getColumn(month*2+1)+blockLink.productionRow+":"+getColumn(month*2+2)+blockLink.productionRow+",\"#\")" +
							"*"+blockLink.qtyPerYear[0]
							+"*"+blockLink.cyclesFirstYearProduction;
					report.addFormulaCell(row, month+1, formula, Style.CURRENCY);
				}
				report.addFormulaCell(row, 13, writeFormula("SUM(BX:MX)", rowNum+1), Style.CURRENCY);
			}
			
		} else {
			rowNum = addCashFlowRow(report, sheet, rowNum, translate("project.report.cashFlowFirst.operCosts"), 
				pfy.getCosts());
		}
		row = sheet.createRow(++rowNum);
		
		// Other costs	
		row = sheet.createRow(++rowNum);
		report.addTextCell(row, 0, translate("project.report.cashFlowFirst.otherCosts"), Style.H2);
		
		row = sheet.createRow(++rowNum);
		if (report.isCompleteReport()) {
			int firstRow = Integer.parseInt(report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW));
			String sheetName = report.getLinks().get(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
			StringBuilder formula = new StringBuilder();
			for (int i=firstRow; i<firstRow+project.getAssets().size();i++) {
				formula.append(
					"IF(" + sheetName+"!M"+i+"=1,"
					+sheetName+"!C"+i+"*"+sheetName+"!J"+i+"/12, 0)+"
				);
			}
			addCashFlowOtherRow(report, row, "project.report.cashFlowFirst.maint", formula.deleteCharAt(formula.length()-1).toString());	
		} else {
			addCashFlowOtherRow(report, row, "project.report.cashFlowFirst.maint", pfy.getMaintenanceCost());
		}

		row = sheet.createRow(++rowNum);
		if (report.isCompleteReport()) {
			addCashFlowOtherRow(report, row, "project.report.cashFlowFirst.general", report.getLinks().get(ExcelLink.PROJECT_GENERAL_TOTAL)+"/12");
		} else {
			addCashFlowOtherRow(report, row, "project.report.cashFlowFirst.general", pfy.getGeneralCost());
		}
		
		row = sheet.createRow(++rowNum);
		report.addTextCell(row, 0, translate("misc.subtotal"), Style.LABEL);
		for (short i=1;i<=13; i++) {
			report.addFormulaCell(row, i, String.format("SUM(%s%d:%s%d)",getColumn(i), rowNum-1, getColumn(i), rowNum), Style.CURRENCY);

		}
		
		row = sheet.createRow(++rowNum);
		report.addTextCell(row, 0, translate("project.report.cashFlowFirst.monthFlow"), Style.LABEL);
		for (short i=1;i<=13;i++) {
			report.addFormulaCell(row, i, String.format("SUM(%s%d:%s%d)-SUM(%s%d:%s%d)-%s%d",

					getColumn(i),firstRows[0],getColumn(i),firstRows[0]+project.getBlocks().size()-1,
					getColumn(i),firstRows[1]+1,getColumn(i),firstRows[1]+project.getBlocks().size(),
					getColumn(i),rowNum
					),Style.CURRENCY);
		}

		row = sheet.createRow(++rowNum);
		report.addTextCell(row, 0, translate("project.report.cashFlowFirst.cumFlow"), Style.LABEL);
		report.addFormulaCell(row, 1, String.format("B%d", rowNum), Style.CURRENCY);
		for (short i=2;i<=12;i++) {
			report.addFormulaCell(row, i, String.format("%s%d+%s%d", getColumn(i), rowNum, getColumn(i-1), rowNum+1), Style.CURRENCY);

		}
		return sheet;
	}

	private void addCashFlowOtherRow (ExcelWrapper report, Row row, String key, double[] values) {

		int cellNum = 0;
		report.addTextCell(row, cellNum++, translate(key));

		for (double value : values) {
			report.addNumericCell(row, cellNum++, value, Style.CURRENCY);

		}
		report.addFormulaCell(row, cellNum, String.format("SUM(B%d:M%d)",row.getRowNum()+1, row.getRowNum()+1), Style.CURRENCY);

		
	}
	
	private void addCashFlowOtherRow(ExcelWrapper report, Row row, String key, String formula) {
		report.addTextCell(row, 0, translate(key));
		for (int i=1;i<=12;i++) {
			report.addFormulaCell(row,  i, formula, Style.CURRENCY);
		}
		report.addFormulaCell(row, 13, String.format("SUM(B%d:M%d)",row.getRowNum()+1, row.getRowNum()+1), Style.CURRENCY);
	}
	private void addCashFlowOtherRow (ExcelWrapper report, Row row, String key, double value) {

		double[] values = new double[12];
		for (int i = 0; i < 12; i++) {
			values[i] = value;
		}
		addCashFlowOtherRow(report, row, key, values);

	}
	
	/**
	 * Add rows for Operation Income and costs
	 * @param wb
	 * @param rowNum
	 * @param message
	 * @param months
	 * @return
	 */
	private int addCashFlowRow(ExcelWrapper report, Sheet sheet, int rowNum, String message, ArrayList<ProjectMonthFlow> months) {

		Row row = sheet.createRow(++rowNum);	
		int cellNum = 0;
		report.addTextCell(row, cellNum++, message, Style.H2);

		for (ProjectMonthFlow pmf : months) {
			row = sheet.createRow(++rowNum);	cellNum = 0;
			report.addTextCell(row, cellNum++, pmf.getDescription());

			for (double flowData : pmf.getFlowData()) {
				report.addNumericCell(row, cellNum++, flowData, Style.CURRENCY);
			}
			report.addFormulaCell(row, 13, writeFormula("SUM(BX:MX)", rowNum+1), Style.CURRENCY);
		}

		return rowNum;
	}

	
	private HashMap<Integer, String> lengthUnits() {
		HashMap<Integer, String> lengthUnits = new HashMap<Integer, String>();
		lengthUnits.put(0, translate("units.months"));
		lengthUnits.put(1, translate("units.weeks"));
		lengthUnits.put(2, translate("units.days.calendar"));
		lengthUnits.put(3, translate("units.days.week"));
		return lengthUnits;
	}
	
	public ArrayList<String> months(MessageSource messages, Project project) {
		ArrayList<String> months = new ArrayList<String>();
		for (int i=0;i<12;i++) {
			String monthName= (i+project.getStartupMonth()<=12)
					? translate("calendar.month."+(i+project.getStartupMonth()))
					: translate("calendar.month."+((i+project.getStartupMonth())%12));
			months.add(monthName);
		}
		return months;
	}

	
	private HashMap<Integer, String> getContribTypes() {
		HashMap<Integer, String> contribTypes = new HashMap<Integer, String>();
		contribTypes.put(0, translate("projectContribution.contribType.govtCentral"));
		contribTypes.put(1, translate("projectContribution.contribType.govtLocal"));
		contribTypes.put(2, translate("projectContribution.contribType.ngoLocal"));
		contribTypes.put(3, translate("projectContribution.contribType.ngoIntl"));
		contribTypes.put(4, translate("projectContribution.contribType.other"));
		return contribTypes;
	}
	
	private void setColumnWidth(Sheet sheet, int col, int size) {
		sheet.setColumnWidth(col, size*36);
	}

	public Sheet getProjectRecommendation(ExcelWrapper report, Project project) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.recommendation")));

		sheet.setSelected(true);
		int rowNum=0;
		short cellNum = 0;
		short step = 7;
		setColumnWidth(sheet, 0, 700);
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.report.recommendation"), Style.TITLE);	

		
//		row = sheet.createRow(rowNum++);
//		report.addTextCell(row, cellNum, translate("project.recommendation"), Style.LABEL);		


		row = sheet.createRow(rowNum++);
		if(project.getReccCode()!= null && project.getReccCode()==2) {
			report.addTextCell(row, cellNum, translate("project.recommendation.reject"));
		}
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.recommendation.date"), Style.LABEL);
		row = sheet.createRow(rowNum++);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (project.getReccDate() != null)
			report.addTextCell(row, cellNum, sdf.format(project.getReccDate()));

		rowNum++;	
		report.addBigTextCell(sheet, rowNum++, translate("project.justification"), project.getReccDesc());

		
		rowNum += step+1;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.report.recommendation.signature"), Style.LABEL);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, project.getTechnician().getDescription());
		
		return sheet;
		
	}

	public Sheet getProjectParameters(ExcelWrapper report, Project project) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.parameters")));

		sheet.setSelected(true);
		int rowNum=0;
		short cellNum = 0;
		setColumnWidth(sheet, 0, 200);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.report.parameters"), Style.TITLE);	

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.report.parameters.general"), Style.H2);	
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("settings.3"));
		report.addTextCell(row, cellNum+1, rivConfig.getSetting().getCurrencyName());

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.inflationAnnual"));
		report.addNumericCell(row, cellNum+1, project.getInflationAnnual(), Style.CURRENCY);
		report.addTextCell(row, cellNum+2, "%");
		if (report.isCompleteReport()) {
			report.getLinks().put(ExcelLink.PROJECT_INFLATION, "'"+sheet.getSheetName()+"'!$B$4");
		}


		// calculate investmentTotal - 
		// TODO: could also get from ProjectResult with DB call
		double investTotal=0;
		for(ProjectItemAsset ass:project.getAssets())
			investTotal+=ass.getUnitCost()*ass.getUnitNum()-ass.getOwnResources()-ass.getDonated();
		for(ProjectItemLabour lab:project.getLabours())
			investTotal+=lab.getUnitCost()*lab.getUnitNum()-lab.getOwnResources()-lab.getDonated();
		for(ProjectItemService ser:project.getServices())
			investTotal+=ser.getUnitCost()*ser.getUnitNum()-ser.getOwnResources()-ser.getDonated();
		
		// get working capital info
		ProjectFirstYear pfy = new ProjectFirstYear(project);
		double[] pfyResults = ProjectFirstYear.WcAnalysis(pfy);
		double financePrd = pfyResults[0];
		double amtRequired = -1*pfyResults[1];	
		double loan2 = project.getLoan2Amt();
		double loan1 = investTotal -loan2;

		row = sheet.createRow(rowNum++);
		if (report.isCompleteReport()) {
			String loan1amt = report.getLinks().get(ExcelLink.PROJECT_INVEST_FINANCED)+"-$B$15";
			rowNum = addLoans(report, sheet, rowNum, translate("project.loan1"), 0.0, loan1amt, project.getLoan1Interest(), 
					project.getLoan1Duration(),	project.getLoan1GraceCapital(), project.getLoan1GraceInterest());
			report.getLinks().put(ExcelLink.PROJECT_LOAN1_AMOUNT, "'"+sheet.getSheetName()+"'!$B$7");
			report.getLinks().put(ExcelLink.PROJECT_LOAN1_RATE, "'"+sheet.getSheetName()+"'!$B$8");
			report.getLinks().put(ExcelLink.PROJECT_LOAN1_DURATION, "'"+sheet.getSheetName()+"'!$B$9");
			report.getLinks().put(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL, "'"+sheet.getSheetName()+"'!$B$10");
			report.getLinks().put(ExcelLink.PROJECT_LOAN1_GRACE_INTEREST, "'"+sheet.getSheetName()+"'!$B$11");
		} else {
			rowNum = addLoans(report, sheet, rowNum, translate("project.loan1"), loan1, null, project.getLoan1Interest(), 
				project.getLoan1Duration(),	project.getLoan1GraceCapital(), project.getLoan1GraceInterest());
		}
		
		row = sheet.createRow(rowNum++);

		row = sheet.createRow(rowNum++);
		rowNum = addLoans(report, sheet, rowNum, translate("project.loan2"), loan2,null,  project.getLoan1Interest(), 
				project.getLoan1Duration(), project.getLoan1GraceCapital(), project.getLoan1GraceInterest());

		
		rowNum++;	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.workingCapital"), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.amtRequired"));
		if (report.isCompleteReport()) {
			String range = "'"+sheetName(translate("project.report.cashFlowFirst"))+"'!B18:M18";
			String formula = 
				"IF(COUNTIF("+range+",\"<0\")>0,MIN("+range+")*-1,0)";	
			report.addFormulaCell(row, 1, formula, Style.CURRENCY);
		} else {
			report.addNumericCell(row, 1, amtRequired, Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.period"));
		if (report.isCompleteReport()) {
			String range = "'"+sheetName(translate("project.report.cashFlowFirst"))+"'!B18:M18";
			String formula = 
					"IF(COUNTIF("+range+",\"<0\"), IF(MATCH(2,1/MMULT(1,-("+range+"<0)))>9,12,MATCH(2,1/MMULT(1,-("+range+"<0)))+2),0)";	
			report.addFormulaCell(row, 1, formula);
		} else {
			report.addNumericCell(row, 1, financePrd);
		}
		report.addTextCell(row, 2, translate("units.months"));
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.capitalInterest"));
		report.addNumericCell(row, 1, project.getCapitalInterest(), Style.CURRENCY);
		report.addTextCell(row, 2, "%");

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.capitalDonate"));
		report.addNumericCell(row, 1, project.getCapitalDonate(), Style.CURRENCY);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.capitalOwn"));
		report.addNumericCell(row, 1, project.getCapitalOwn(), Style.CURRENCY);


		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.amtFinanced"));
		report.addFormulaCell(row, 1, "B22-B25-B26", Style.CURRENCY);

		return sheet;
	}
	
	private int addLoans(ExcelWrapper report, Sheet sheet, int rowNum, String title, double amount, String amountFormula, double interest, 
			int duration, int graceCapital, int graceInterest) {
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.H2);

		String years = translate("units.years");
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.amount"));
		if (amountFormula==null) {
			report.addNumericCell(row, 1, amount,Style.CURRENCY);
		} else {
			report.addFormulaCell(row, 1, amountFormula, Style.CURRENCY);
		}
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.interest"));
		report.addNumericCell(row, 1, interest,Style.CURRENCY);
		report.addTextCell(row, 2, "%");
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.duration"));
		report.addNumericCell(row, 1, duration);
		report.addTextCell(row, 2, years);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.graceCapital"));
		report.addNumericCell(row, 1, graceCapital);
		report.addTextCell(row, 2, years);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.graceInterest"));
		report.addNumericCell(row, 1, graceInterest);
		report.addTextCell(row, 2, years);
		
		return rowNum;
	}

	
	
	public Sheet getProfileSummary(ExcelWrapper report, Profile profile) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("profile.report.summary")));

		Setting setting = rivConfig.getSetting();
		setColumnWidth(sheet, 0, 300);
		for (short i=1; i<=3; i++) {
			sheet.setDefaultColumnStyle(i, report.getStyles().get(Style.CURRENCY));

			setColumnWidth(sheet, i, 100);
		}
		
		int rowNum=0;
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profile.report.summary"), Style.TITLE);

		
	//	General information
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profile.report.summary.general"), Style.H2);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("settings.currency.name"), Style.LABEL);
		report.addTextCell(row, 1, setting.getCurrencyName());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profile.exchRate"), Style.LABEL);
		report.addTextCell(row, 1, String.format("%s %s", profile.getExchRate(), translate("units.perUSD")));
		
	// Location
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.step1.2"), Style.H2);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.fieldOffice"), Style.LABEL);
		report.addTextCell(row, 1, profile.getFieldOffice().getDescription().trim());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, setting.getLocation1(), Style.LABEL);
		report.addTextCell(row, 1, profile.getLocation1());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, setting.getLocation2(), Style.LABEL);
		report.addTextCell(row, 1, profile.getLocation2());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, setting.getLocation3(), Style.LABEL);
		report.addTextCell(row, 1, profile.getLocation3());
		
	// Technician
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		User user = profile.getTechnician();
		report.addTextCell(row, 0, translate("profile.technician"), Style.H2);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.description"), Style.LABEL);
		report.addTextCell(row, 1, user.getDescription());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.organization"), Style.LABEL);
		report.addTextCell(row, 1, user.getOrganization());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.location"), Style.LABEL);
		report.addTextCell(row, 1, user.getLocation());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.telephone"), Style.LABEL);
		report.addTextCell(row, 1, user.getTelephone());
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("user.email"), Style.LABEL);
		report.addTextCell(row, 1, user.getEmail());
		
	// Beneficiaries
		row = sheet.createRow(rowNum++);	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profile.beneficiaries"), Style.H2);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, profile.getBenefName());
		rowNum=report.addBigTextCell(sheet, rowNum++, translate("profile.benefDesc"), profile.getBenefDesc());
		

		
	//	Objective and Major Activities
		rowNum=report.addBigTextCell(sheet, rowNum++, translate("profile.objective"),profile.getProjDesc());
		
	//	Anticipated market
		String title;
		if (profile.getIncomeGen()) {
			title = "profile.market";
		} else {
			title = "profile.demand";
		}
		rowNum=report.addBigTextCell(sheet, rowNum++, translate(title), profile.getMarket());
		
	//	Environmental impact		
		rowNum=report.addBigTextCell(sheet, rowNum++, translate("profile.envirImpact"), profile.getEnviroImpact());
		
		return sheet;
		
	}
	
	public void getProfileInvest(ExcelWrapper report, Profile profile, boolean template) {

		//	goods and materials
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("profile.report.investDetail")));

		sheet.setSelected(true);
		setColumnWidth(sheet, 0, 150);
		setColumnWidth(sheet, 4, 100);
				
		int rowNum = 0;
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profile.report.investDetail"), Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profileGoods"), Style.H2);
		setColumnWidth(sheet, 0, 150);
		for (short col=3; col<=9; col++) {
			setColumnWidth(sheet, col, 80);
		}
		
		// header
		String[] colTitles = new String[] 
		    {"profileGoods.description","profileGoods.unitType","profileGoods.unitNum",
				"profileGoods.unitCost","profileGoods.totalCost","profileGoods.ownResource",
				"profileGoods.externalResources","profileGoods.econLife","profileGoods.salvage",
				"profileGoods.reserve"};
		XlsTable table = new XlsTable(report, colTitles)
		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true)
		.addColumn(XlsColumnType.CURRENCY, "getOwnResource", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX", true)
		.addColumn(XlsColumnType.NUMERIC, "getEconLife", false)
		.addColumn(XlsColumnType.CURRENCY, "getSalvage", false)
		.addColumn(XlsColumnType.FORMULA, "((DX-IX)*CX)/HX", true);
		rowNum = table.writeTable(sheet, rowNum++, template ? null : profile.getGlsGoods(), true);
		if (report.isCompleteReport()) {
			report.getLinks().put(ExcelLink.PROFILE_INVEST_GOODS_RESERVE, "'"+sheet.getSheetName()+"'!J"+rowNum);
		}
		
		int sumGoods = rowNum;
		rowNum++;
	
		//	labour and services
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profileLabour"), Style.H2);

		// header
		String[] colTitles2 = new String[] 
		    {"profileGoods.description","profileGoods.unitType","profileGoods.unitNum",
				"profileGoods.unitCost","profileGoods.totalCost","profileGoods.ownResource",
				"profileGoods.externalResources"};
		table = new XlsTable(report, colTitles2)
		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true)
		.addColumn(XlsColumnType.CURRENCY, "getOwnResource", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX", true);
		rowNum = table.writeTable(sheet, rowNum++, template ? null : profile.getGlsLabours(), true);	

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 3, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, 4, String.format("E%d+E%d", sumGoods, rowNum-1), Style.CURRENCY);
		report.addFormulaCell(row, 5, String.format("F%d+F%d", sumGoods, rowNum-1), Style.CURRENCY);
		report.addFormulaCell(row, 6, String.format("G%d+G%d", sumGoods, rowNum-1), Style.CURRENCY);
		if (report.isCompleteReport()) {
			report.getLinks().put(ExcelLink.PROFILE_INVEST_TOTAL, "'"+sheet.getSheetName()+"'!E"+rowNum);
			report.getLinks().put(ExcelLink.PROFILE_INVEST_OWN, "'"+sheet.getSheetName()+"'!F"+rowNum);
			report.getLinks().put(ExcelLink.PROFILE_INVEST_EXTERNAL, "'"+sheet.getSheetName()+"'!G"+rowNum);
		}
		
	}

	public void getProfileGeneral(ExcelWrapper report, Profile profile, boolean template) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("profileGeneral")));

		sheet.setSelected(true);
		setColumnWidth(sheet, 0, 130);
		sheet.setDefaultColumnStyle((short)3, report.getStyles().get(Style.CURRENCY));
		sheet.setDefaultColumnStyle((short)4, report.getStyles().get(Style.CURRENCY));

		int rowNum=0;
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profile.report.costsDetail"), Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		if (profile.getWithWithout()) {
			report.addTextCell(row, 0, translate("profileGeneral") + " " + translate("project.with"), Style.H2);
		} else {
			report.addTextCell(row, 0, translate("profileGeneral"), Style.H2);
		}
	
		XlsTable table = new XlsTable(report, new String[] {"profileGeneral.description","profileGeneral.unitType","profileGeneral.unitNum","profileGeneral.unitCost","profileGeneral.totalCost"});
		table.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true);
		rowNum = table.writeTable(sheet, rowNum, template ? null : profile.getGlsGeneral(), true);
		if (report.isCompleteReport()) {
			report.getLinks().put(ExcelLink.PROFILE_GENERAL_TOTAL, "'"+sheet.getSheetName()+"'!E"+rowNum);
		}
		
		
		if (profile.getWithWithout()) {
			rowNum++;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("profileGeneral") + " " + translate("project.without"), Style.H2);
			rowNum = table.writeTable(sheet, rowNum, profile.getGlsGeneralWithout(), true);
			if (report.isCompleteReport()) {
				report.getLinks().put(ExcelLink.PROFILE_GENERAL_WITHOUT_TOTAL, "'"+sheet.getSheetName()+"'!E"+(rowNum-1));
			}
		}
		
	}
	
	protected class XlsTable {
		private String[] titles;
		private ArrayList<XlsColumn> columns;
		private ExcelWrapper report;
		
		public XlsTable(ExcelWrapper report, String[] headers) {
			this.titles=headers;
			this.report=report;
			columns = new ArrayList<XlsColumn>();
		}
		
		public XlsTable addColumn(XlsColumnType type, String data, boolean sum) {
			columns.add(new XlsColumn(type, data, sum));
			return this;
		}
		public XlsTable addSelectColumn(String data, Map<String, String>options) {
			XlsColumn newCol = new XlsColumn(XlsColumnType.SELECT, data, false);
			newCol.options=options;
			columns.add(newCol);
			return this;
		}
		public XlsTable addSelectColumnIntBased(String data, Map<Integer, String>options) {
			XlsColumn newCol = new XlsColumn(XlsColumnType.SELECT, data, false);
			newCol.intOptions=options;
			columns.add(newCol);
			return this;
		}
		
		public int writeTable(Sheet sheet, int startRow, @SuppressWarnings("rawtypes") Collection data, boolean subtotal) {

			int rowNum=startRow;
			
			// setup validations
			for (int i=0; i<columns.size(); i++) {
				if (columns.get(i).type==XlsColumnType.SELECT) {
					String[] options = (String[]) (columns.get(i).options != null ?
							columns.get(i).options.values().toArray(new String[0]) :
						columns.get(i).intOptions.values().toArray(new String[0]));
					
					XSSFDataValidationConstraint constraint = new XSSFDataValidationConstraint(options);
					int rows = (data==null || data.size()==0) ? 1 : data.size();
					CellRangeAddressList range = new CellRangeAddressList(startRow+1, startRow+rows, i, i);
					DataValidationHelper helper = sheet.getDataValidationHelper();
					DataValidation dv = helper.createValidation(constraint, range);
					dv.setShowErrorBox(true);
					sheet.addValidationData(dv);
				}
			}
			
			addHeaderRow(report, sheet, rowNum++, titles);
			
			// determine first (if any) column requires a subtotal
			Integer firstSum=null;
			for (int i=0; i<columns.size(); i++) {
				if (columns.get(i).sum) {
					firstSum=i;
					break;
				}
			}
			
			// write table data
			if (data==null || data.size()==0) {
				// add blank row
				Row row = sheet.createRow(rowNum++);
				for (int i=0; i<columns.size(); i++) {
					XlsColumn col = columns.get(i);
					if (col.type==XlsColumnType.FORMULA) {
						report.addFormulaCell(row, i, writeFormula(col.data, rowNum));
					}
				}
			} else {
				@SuppressWarnings("rawtypes")
				Iterator datas = data.iterator();
				while (datas.hasNext()) {
					Object o = datas.next();
					Row row = sheet.createRow(rowNum++);
					for (int i=0; i<columns.size(); i++) {
						XlsColumn col = columns.get(i);
						if (col.type==XlsColumnType.TEXT) {
							String field = callMethod(o, col.data);
							report.addTextCell(row, i, field);

						} else if (col.type==XlsColumnType.NUMERIC) { 
							String field = callMethod(o, col.data);
							report.addNumericCell(row, i, Double.parseDouble(field));
						} else if (col.type==XlsColumnType.CURRENCY) {
							String field = callMethod(o, col.data);
							report.addNumericCell(row, i, Double.parseDouble(field), Style.CURRENCY);

						} else if (col.type==XlsColumnType.FORMULA) {
							report.addFormulaCell(row, i, writeFormula(col.data, rowNum), Style.CURRENCY);

						} else if (col.type==XlsColumnType.YESNO) {
							String field = callMethod(o, col.data);
							if (Boolean.parseBoolean(field)) {
								report.addTextCell(row, i, "#");
							}
							//report.addTextCell(row, i, Boolean.parseBoolean(field) ? translate("misc.yes") : translate("misc.no"));
						} else if (col.type==XlsColumnType.SELECT) {
							String key = callMethod(o, col.data);
							if (col.options!=null) {
								report.addTextCell(row, i, col.options.get(key));
							} else if (col.intOptions!=null) {
								report.addTextCell(row, i, col.intOptions.get(Integer.parseInt(key)));
							}
						}
					}
				}
			}
			
			// add subtotal
			if (subtotal && firstSum!=null) {
				Row row = sheet.createRow(rowNum++);
				report.addTextCell(row, firstSum-1, translate("misc.subtotal"), Style.LABEL);

				
				for (int i=0; i<columns.size(); i++) {
					if (columns.get(i).sum) {
						report.addFormulaCell(row, i, String.format("SUM("+getColumn(i)+"%d:"+getColumn(i)+"%d)", startRow+2, rowNum-1), Style.CURRENCY);

					}
				} 
			}
			return rowNum;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private String callMethod(Object o, String method) {
		Class c = o.getClass();
		Method m;
		try {
			m = c.getMethod(method);
			return m.invoke(o, new Object[0]).toString();
		} catch (Exception e) {
			Log.warn("Couldn't get field data when writing table.", e);
			e.printStackTrace();
			return null;
		}
	}

	protected class XlsColumn {
		public XlsColumn (XlsColumnType type, String data, boolean sum) {
			this.type=type;
			this.data=data;
			this.sum=sum;
		}
		private XlsColumnType type;
		private String data;
		private boolean sum;
		private Map<String, String> options; // for SELECT type
		private Map<Integer, String> intOptions;
	}
	private enum XlsColumnType { TEXT, NUMERIC, CURRENCY, FORMULA, SELECT, YESNO, NONE }
	
	private XlsTable[] productTables(ExcelWrapper report, boolean incomeGen) {
		XlsTable[] tables = new XlsTable[3];
		
		String[] titlesIncome = incomeGen ?
				new String[] 

		    {"profileProductIncome.desc","profileProductIncome.unitType","profileProductIncome.unitNum",
				"profileProductIncome.unitCost", "profileProductIncome.transport","profileProductIncome.total"}
				
			: new String[]
		    {"profileActivityCharge.desc","profileActivityCharge.unitType","profileActivityCharge.unitNum",
				"profileActivityCharge.unitCost","","profileActivityCharge.total"

				};
		
		tables[0] = new XlsTable(report, titlesIncome)

		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false);
		if (incomeGen) {
			tables[0].addColumn(XlsColumnType.CURRENCY, "getTransport", false)
			.addColumn(XlsColumnType.FORMULA, "CX*(DX-EX)", true);
		} else {
			tables[0].addColumn(XlsColumnType.NONE, null, false)
			.addColumn(XlsColumnType.FORMULA, "CX*DX", true);
		}
		
		String[] titlesInput = new String[] 
			    {"profileProductInput.desc","profileProductInput.unitType","profileProductInput.unitNum",
					"profileProductInput.unitCost","profileProductInput.transport","profileProductInput.total"};
		tables[1] = new XlsTable(report, titlesInput)

		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.CURRENCY, "getTransport", false)
		.addColumn(XlsColumnType.FORMULA, "CX*(DX+EX)", true);
		
		String[] titlesLabour = new String[] 
		    {"profileProductLabour.desc","profileProductLabour.unitType","profileProductLabour.unitNum",
				"profileProductLabour.unitCost","","profileProductLabour.total"};
		
		tables[2] = new XlsTable(report, titlesLabour)
		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.NONE, null, false)
		.addColumn(XlsColumnType.FORMULA, "CX*DX", true);
		
		return tables;
	}
	
	public void getProfileProducts(ExcelWrapper report, Profile profile) {
		boolean ig = profile.getIncomeGen();
		int rowNum = 0;
		String sheetName = null;
		if (ig) {
			sheetName = "profile.report.productDetail";
		} else {
			sheetName = "profile.report.productDetailNongen";
		}
		sheetName = translate(sheetName);
		
		Sheet sheet = report.getWorkbook().createSheet(sheetName);
		setColumnWidth(sheet, 0, 200);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, sheetName, Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		// header
		
		String incomeTitle = profile.getIncomeGen() ? translate("profileProductIncome") : translate("profileActivityCharge");

		XlsTable[] tables = productTables(report, profile.getIncomeGen());
		
		for (ProfileProduct prod : profile.getProducts()) {
			rowNum = addProduct(prod, rowNum, incomeTitle, sheet, report, tables);
		}
		
		setColumnWidth(sheet, 5, 100);
	}
	
	public Sheet getProduct(ExcelWrapper report, ProfileProduct product, boolean incomeGen) {
		XlsTable[] tables = productTables(report, incomeGen);
		Sheet sheet = report.getWorkbook().createSheet();
		String incomeTitle = incomeGen ? translate("profileProductIncome") : translate("profileActivityCharge");
		addProduct(product, 0, incomeTitle, sheet, report, tables);
		return sheet;
	}
	
	private int addProduct(ProfileProduct prod, int rowNum, String incomeTitle, Sheet sheet, ExcelWrapper report, XlsTable[] tables) {
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, prod.getDescription(), Style.H2);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, incomeTitle, Style.H2);
		rowNum=tables[0].writeTable(sheet, rowNum++, prod.getProfileIncomes(), true);
		if (report.isCompleteReport()) {
			String value = "'"+sheet.getSheetName()+"'!F"+rowNum;
			ExcelLink incomeLink = ExcelLink.PROFILE_PRODUCT_INCOME;
			if (report.getLinks().get(incomeLink)==null) {
				report.getLinks().put(incomeLink, value);
			} else {
			report.getLinks().put(incomeLink, report.getLinks().get(incomeLink)+"+"+value);
			}
		}
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profileProductInput").replace('/',' '), Style.H2);
		rowNum=tables[1].writeTable(sheet, rowNum++, prod.getProfileInputs(), true);
		if (report.isCompleteReport()) {
			String value = "'"+sheet.getSheetName()+"'!F"+rowNum;
			ExcelLink inputLink = ExcelLink.PROFILE_PRODUCT_INPUT;
			if (report.getLinks().get(inputLink)==null) {
				report.getLinks().put(inputLink, value);
			} else {
			report.getLinks().put(inputLink, report.getLinks().get(inputLink)+"+"+value);
			}
		}
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profileProductLabour"), Style.H2);
		rowNum=tables[2].writeTable(sheet, rowNum++, prod.getProfileLabours(), true);
		if (report.isCompleteReport()) {
			String value = "'"+sheet.getSheetName()+"'!F"+rowNum;
			ExcelLink labourLink = ExcelLink.PROFILE_PRODUCT_LABOUR;
			if (report.getLinks().get(labourLink)==null) {
				report.getLinks().put(labourLink, value);
			} else {
				report.getLinks().put(labourLink, report.getLinks().get(labourLink)+"+"+value);
			}
		}
		return rowNum+2;
	}

	public void getProfilePrelimAnalysis(ExcelWrapper report, ProfileResult result) {

		boolean incomeGen = result.isIncomeGen();
		String[] titles = new String[5];
		
		if (incomeGen) {
			titles[0] = translate("profile.report.preliminary");
			titles[1] = translate("profile.report.preliminary.annualTotal");
			titles[2] = translate("profile.report.preliminary.annualGross");
			titles[3] = translate("profile.report.preliminary.annualNetIncome");
			titles[4] = String.format("%s ( (A+B) / (C-D-E) )", translate("profile.report.preliminary.yearsRequired"));
		} else {
			titles[0] = translate("profile.report.benefits");
			titles[1] = translate("profile.report.benefits.totCharge");
			titles[2] = translate("profile.report.benefits.netBalance");
			titles[3] = translate("profile.report.benefits.netBalanceMinusReserve");
			titles[4] = String.format("%s (G)", translate("profile.benefNum"));
		}
		
		Sheet sheet = report.getWorkbook().createSheet(titles[0]);

		int rowNum=0;
		short rowHeader = 0;
		short cellNum = 1;
		setColumnWidth(sheet, rowHeader, 500);
		setColumnWidth(sheet, cellNum, 100);
		sheet.setDefaultColumnStyle(cellNum, report.getStyles().get(Style.CURRENCY));
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, titles[0], Style.TITLE);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (A+B)", translate("profile.report.preliminary.investTotal")));
		report.addFormulaCell(row, cellNum, "B3+B4", Style.CURRENCY);
		
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (A)", translate("profile.report.preliminary.investExtern")));		
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum, report.getLinks().get(ExcelLink.PROFILE_INVEST_EXTERNAL), Style.CURRENCY);
		} else {
			report.addNumericCell(row, cellNum, result.getInvestmentExt(), Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (B)", translate("profile.report.preliminary.investOwn")));		
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum, report.getLinks().get(ExcelLink.PROFILE_INVEST_OWN), Style.CURRENCY);
		} else {
			report.addNumericCell(row, cellNum, result.getInvestmentOwn(), Style.CURRENCY);
		}
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (C)", titles[1]));		
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum, report.getLinks().get(ExcelLink.PROFILE_PRODUCT_INCOME), Style.CURRENCY);
		} else {
			report.addNumericCell(row, cellNum, result.getTotIncome(), Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (D)", translate("profile.report.preliminary.annualCost")));		
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum, report.getLinks().get(ExcelLink.PROFILE_PRODUCT_INPUT)+"+"+report.getLinks().get(ExcelLink.PROFILE_PRODUCT_LABOUR), Style.CURRENCY);
		} else {
			report.addNumericCell(row, cellNum, result.getOperCost(), Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (E)", translate("profile.report.preliminary.annualGeneral")));		
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum, report.getLinks().get(ExcelLink.PROFILE_GENERAL_TOTAL), Style.CURRENCY);
		} else {
			report.addNumericCell(row, cellNum, result.getGeneralCost(), Style.CURRENCY);
		}
		row = sheet.createRow(rowNum++);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (C-D-E)", titles[2]));		
		report.addFormulaCell(row, cellNum, "B5-B6-B7", Style.CURRENCY);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (F)", translate("profile.report.preliminary.annualReserve")));		
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, cellNum, report.getLinks().get(ExcelLink.PROFILE_INVEST_GOODS_RESERVE), Style.CURRENCY);
		} else {
			report.addNumericCell(row, cellNum, result.getAnnualReserve(), Style.CURRENCY);
		}
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (C-D-E-F)", titles[3]));
		report.addFormulaCell(row, cellNum, "B9-B10", Style.CURRENCY);
		
		row = sheet.createRow(rowNum++);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, titles[4]);		
		if(incomeGen) {
			report.addFormulaCell(row, cellNum, "B2 / B9");
		} else {
			report.addNumericCell(row, cellNum, result.getBenefNum());
		}
		if (!incomeGen) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, rowHeader, String.format("%s ( (D+E) / G )", translate("profile.report.benefits.costPerBenef")));		
			report.addFormulaCell(row, cellNum, "(B6+B7)/B13", Style.CURRENCY);
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, rowHeader, String.format("%s ( (A+B) / G )", translate("profile.investmentPerBenef")));		
			report.addFormulaCell(row, cellNum, "B2 / B13", Style.CURRENCY);
			row = sheet.createRow(rowNum++);
		}
	}

	public Sheet getProfileRecommendation(ExcelWrapper report, Profile profile) {
		Sheet sheet = report.getWorkbook().createSheet(sheetName(translate("project.report.recommendation")));

		int rowNum=0;
		short cellNum = 0;
		setColumnWidth(sheet, 0, 500);
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.report.recommendation"), Style.TITLE);	


		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("profile.recommendation"), Style.LABEL);	

		row = sheet.createRow(rowNum++);
		if(profile.getReccCode()!= null && profile.getReccCode()==2) {
			report.addTextCell(row, cellNum, translate("profile.recommendation.reject"));
		} else {
			report.addTextCell(row, cellNum, translate("profile.recommendation.approve"));
		}
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("profile.recommendation.date"), Style.LABEL);
		row = sheet.createRow(rowNum++);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (profile.getReccDate() != null)
			report.addTextCell(row, cellNum, sdf.format(profile.getReccDate()));

		rowNum++;	
		
		rowNum=report.addBigTextCell(sheet, rowNum, translate("profile.justification"), profile.getReccDesc());

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("profile.report.recommendation.signature"), Style.LABEL);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, profile.getTechnician().getDescription());
		
		return sheet;
	}

	   private HashMap<String, String> labourTypes(){
			HashMap<String, String>labourTypes=new HashMap<String, String>();
			labourTypes.put("0", translate("units.pyears"));
			labourTypes.put("1", translate("units.pmonths"));
			labourTypes.put("2", translate("units.pweeks"));
			labourTypes.put("3", translate("units.pdays"));
			return labourTypes;
		}
	   
	    private void copyRow(Sheet worksheet, int sourceRowNum, int destinationRowNum) {
	        // Get the source / new row
	        Row newRow = worksheet.getRow(destinationRowNum);
	        Row sourceRow = worksheet.getRow(sourceRowNum);

	        // If the row exist in destination, push down all rows by 1 else create a new row
	        if (newRow != null) {
	            worksheet.shiftRows(destinationRowNum, worksheet.getLastRowNum(), 1);
	        } else {
	            newRow = worksheet.createRow(destinationRowNum);
	        }

	        // Loop through source columns to add to new row
	        for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
	            // Grab a copy of the old/new cell
	            Cell oldCell = sourceRow.getCell(i);
	            Cell newCell = newRow.createCell(i);

	            // If the old cell is null jump to next cell
	            if (oldCell == null) {
	                newCell = null;
	                continue;
	            }

	            // Copy style from old cell and apply to new cell
	            newCell.setCellStyle(oldCell.getCellStyle());

	            // If there is a cell comment, copy
	            if (oldCell.getCellComment() != null) {
	                newCell.setCellComment(oldCell.getCellComment());
	            }

	            // If there is a cell hyperlink, copy
	            if (oldCell.getHyperlink() != null) {
	                newCell.setHyperlink(oldCell.getHyperlink());
	            }

	            // Set the cell data type
	            newCell.setCellType(oldCell.getCellType());

	            // Set the cell data value
	            switch (oldCell.getCellType()) {
	                case Cell.CELL_TYPE_BLANK:
	                    newCell.setCellValue(oldCell.getStringCellValue());
	                    break;
	                case Cell.CELL_TYPE_BOOLEAN:
	                    newCell.setCellValue(oldCell.getBooleanCellValue());
	                    break;
	                case Cell.CELL_TYPE_ERROR:
	                    newCell.setCellErrorValue(oldCell.getErrorCellValue());
	                    break;
	                case Cell.CELL_TYPE_FORMULA:
	                    newCell.setCellFormula(oldCell.getCellFormula());
	                    break;
	                case Cell.CELL_TYPE_NUMERIC:
	                    newCell.setCellValue(oldCell.getNumericCellValue());
	                    break;
	                case Cell.CELL_TYPE_STRING:
	                    newCell.setCellValue(oldCell.getRichStringCellValue());
	                    break;
	            }
	        }

	        // If there are are any merged regions in the source row, copy to new row
	        for (int i = 0; i < worksheet.getNumMergedRegions(); i++) {
	            CellRangeAddress cellRangeAddress = worksheet.getMergedRegion(i);
	            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum()) {
	                CellRangeAddress newCellRangeAddress = new CellRangeAddress(newRow.getRowNum(),
	                        (newRow.getRowNum() +
	                                (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()
	                                        )),
	                        cellRangeAddress.getFirstColumn(),
	                        cellRangeAddress.getLastColumn());
	                worksheet.addMergedRegion(newCellRangeAddress);
	            }
	        }
	    }
}

class Value {
	private String key;
	private Double value;
	
	public Value(String key, Double value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}
}
