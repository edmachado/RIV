package riv.web.service;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
//import org.apache.poi.ss.usermodel.ComparisonOperator;
import org.apache.poi.hssf.record.CFRuleBase.ComparisonOperator;
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
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import riv.objects.FilterCriteria;
import riv.objects.FinanceMatrix;
import riv.objects.FinanceMatrix.Amortization;
import riv.objects.FinanceMatrix.ProjectScenario;
import riv.objects.ProfileMatrix;
import riv.objects.ProjectFinanceData;
import riv.objects.ProjectFinanceNongen;
import riv.objects.ProjectMonthsInYear;
import riv.objects.config.Setting;
import riv.objects.config.User;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileProduct;
import riv.objects.profile.ProfileProductBase;
import riv.objects.profile.ProfileResult;
import riv.objects.project.Block;
import riv.objects.project.BlockBase;
import riv.objects.project.BlockChron;
import riv.objects.project.BlockPattern;
import riv.objects.project.BlockWithout;
import riv.objects.project.Donor;
import riv.objects.project.HasPerYearItems;
import riv.objects.project.PerYearItem;
import riv.objects.project.Project;
import riv.objects.project.ProjectItem;
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
	
	public void profileResults(ExcelWrapper report, List<ProfileResult> results, FilterCriteria filter) {
		String reportName = filter.isIncomeGen() ? "profile.report.results.IG" : "profile.report.results.NIG";
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROFILE_RESULTS));
		sheet.setSelected(true);
		setColumnWidth(sheet, 0, 200);
		String[] colTitles = filter.isIncomeGen() ?
			new String[] // Income generating
		    {"profile.profileName", "profile.user", "profile.fieldOffice", "profile.benefNum", "profile.investTotal", 
				"profile.investOwn", "profile.investExt", "profile.incomeAfterAnnual", "profile.yearsToRecover"
		    } :
	    	new String[] // Non income generating
		    {"profile.profileName", "profile.user", "profile.fieldOffice", "profile.benefNum", "profile.investTotal", 
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
	
	public void projectResults(ExcelWrapper report, List<ProjectResult> results, FilterCriteria filter){//, HashMap<Integer, String> projectStatuses) {
		String reportName = filter.isIncomeGen() ? "project.report.results.IG" : "project.report.results.NIG";
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_RESULTS));
		sheet.setSelected(true);
		setColumnWidth(sheet, 0, 200);
		
		// header
		String[] colTitles;
		if (filter.isIncomeGen()) {
			ArrayList<String> titles = new ArrayList<String>();
			titles.add("project.projectName");
			titles.add("project.userCode");
			titles.add("project.user");
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
  		    {"project.projectName","project.userCode","project.user","project.fieldOffice", "project.status", "project.enviroCat", "project.investTotal", 
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
	
	private String translate(SheetName name) {
		return translate(name.toString());
	}
	private String translate(String messageCode) {
		return messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale());
	}
	
	private String writeFormula(String formula, int row) {
		return formula.replace("X",Integer.toString(row));
	}
	
	public Sheet block(ExcelWrapper report, BlockBase block, boolean incomeGen) {
		XlsTable[] xlsTables = blockXlsTables(report, incomeGen);
		Sheet sheet = report.getWorkbook().createSheet();
		((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		addBlock(block, incomeGen, report, sheet, 0, xlsTables);
		autoSizeColumns(sheet, 9);
		return sheet;
	}
	
	public void blocks(ExcelWrapper report, Project project, boolean isWithout) {
		// data
		int rowNum=0; 
		
		String title = project.getIncomeGen() ? translate("project.report.blockDetail") : translate("project.report.activityDetail");
		SheetName sheetname;
		if (project.isWithWithout()) {
			if (isWithout)  {
				title =  title + " ("+translate("projectBlock.with.without")+")";
				sheetname = SheetName.PROJECT_BLOCKS_WITHOUT;
			} else {
				title = title +  " ("+translate("projectBlock.with.with")+")";
				sheetname = SheetName.PROJECT_BLOCKS_WITH;
			}
		} else {
			sheetname = project.getIncomeGen() ? SheetName.PROJECT_BLOCKS : SheetName.PROJECT_ACTIVITIES;
		}
		Sheet sheet = report.getWorkbook().createSheet(translate(sheetname));
		((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 4));
		
		XlsTable[] xlsTables = blockXlsTables(report, project.getIncomeGen());
		
		Set<? extends BlockBase> blocks = isWithout ? project.getBlocksWithout() : project.getBlocks();		
		for (BlockBase block : blocks) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, block.getDescription(), Style.H2);

			
			rowNum = addBlock(block, project.getIncomeGen(), report, sheet, rowNum, xlsTables);
		}
		autoSizeColumns(sheet, 9);
		sheet.setColumnHidden(10, true);
		sheet.setColumnHidden(11, true);
	}
	
	private XlsTable[] blockXlsTables(ExcelWrapper report, boolean incomeGen) {
		XlsTable[] tables = new XlsTable[3];
		String[] incomeTitles = new String[] 
			    {"projectBlockIncome.desc","projectBlockIncome.unitType","projectBlockIncome.unitNum",
					"projectBlockIncome.qtyIntern","projectBlockIncome.qtyExtern","projectBlockIncome.unitCost",
					"projectBlockIncome.transport","projectBlockIncome.total","","projectBlockIncome.totalCash"};

			String[] incomeTitlesNIG = new String[] 
	   		    {"projectActivityCharge.desc","projectActivityCharge.unitType","projectActivityCharge.unitNum",
	   				"", "", "projectActivityCharge.unitCost", "", "projectActivityCharge.total"};

			String[] inputTitles = new String[] 
			    {"projectBlockInput.desc","projectBlockInput.unitType","projectBlockInput.unitNum",
					"projectBlockInput.qtyIntern","projectBlockInput.qtyExtern","projectBlockInput.unitCost",
					"projectBlockInput.transport","projectBlockInput.total",incomeGen ? "" : "projectBlockInput.donated", 
							"projectBlockInput.totalCash"};
			
			String[] labourTitles = new String[] 
	 		    {"projectBlockLabour.desc","projectBlockLabour.unitType","projectBlockLabour.unitNum",
					"projectBlockLabour.qtyIntern","projectBlockLabour.qtyExtern",
					"projectBlockLabour.unitCost","","projectBlockLabour.total",incomeGen ? "" : "projectBlockLabour.donated",
							"projectBlockLabour.totalCash"};
			
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
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.FORMULA, "(CX-DX)*(FX-GX)", true)
				.addColumn(XlsColumnType.FORMULA, "(CX-DX)*FX", true)
				.addColumn(XlsColumnType.FORMULA, "(CX-DX)*GX", true);
			} else {
				tables[0] = new XlsTable(report, incomeTitlesNIG)
				.addColumn(XlsColumnType.TEXT, "getDescription", false)
				.addColumn(XlsColumnType.TEXT, "getUnitType", false)
				.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.FORMULA, "CX*FX", true)
				.addColumn(XlsColumnType.NONE, "", false)
				.addColumn(XlsColumnType.FORMULA, "HX", true);
			}
			
			tables[1] = new XlsTable(report, inputTitles)
			.addColumn(XlsColumnType.TEXT, "getDescription", false)
			.addColumn(XlsColumnType.TEXT, "getUnitType", false)
			.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
			.addColumn(XlsColumnType.NUMERIC, "getQtyIntern", false)
			.addColumn(XlsColumnType.FORMULA, "CX-DX", false)
			.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
			.addColumn(XlsColumnType.CURRENCY, "getTransport", false)
			.addColumn(XlsColumnType.FORMULA, "CX*(FX+GX)", true);
			if (incomeGen) {
				tables[1]
					.addColumn(XlsColumnType.NONE, "", false)
					.addColumn(XlsColumnType.FORMULA, "(CX-DX)*(FX+GX)", true);
			} else {
				tables[1]
					.addColumn(XlsColumnType.CURRENCY, "getDonated", true)
					.addColumn(XlsColumnType.FORMULA, "(CX-DX)*(FX+GX)-IX", true);
			}
			
			tables[2] = new XlsTable(report, labourTitles)

			.addColumn(XlsColumnType.TEXT, "getDescription", false)
			.addSelectColumn("getUnitType", labourTypes())
			.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
			.addColumn(XlsColumnType.NUMERIC, "getQtyIntern", false)
			.addColumn(XlsColumnType.FORMULA, "CX-DX", false)
			.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
			.addColumn(XlsColumnType.NONE, null, false)
			.addColumn(XlsColumnType.FORMULA, "CX*FX", true);
			if (incomeGen) {
				tables[2]
					.addColumn(XlsColumnType.NONE, "", false)
					.addColumn(XlsColumnType.FORMULA, "(CX-DX)*(FX+GX)", true);
			} else {
				tables[2]
					.addColumn(XlsColumnType.CURRENCY, "getDonated", true)
					.addColumn(XlsColumnType.FORMULA, "(CX-DX)*(FX+GX)-IX", true);
			}
			
			return tables;
	}
	
	private int addBlock(BlockBase block, boolean incomeGen, ExcelWrapper report, Sheet sheet, int rowNum, XlsTable[] tables) {
		int[] sumRows = new int[3];
		String perUnit = incomeGen 
				? block.isCycles() ? " ("+translate("units.perUnitperCycle")+")" : " ("+translate("units.perUnitNoCycle")+")"
				: block.isCycles() ? " ("+translate("units.perUnitperCycle")+")" : " ("+translate("units.perUnitActivityNoCycle")+")";
		// income
		String title= (incomeGen ? translate("projectBlockIncome") : translate("projectActivityCharge")) + perUnit;
		report.addTextCell(sheet.createRow(rowNum++), 0, title, Style.H2);
		rowNum=tables[0].writeTable(sheet, rowNum++, block.getIncomes(), true);
		sumRows[0]=rowNum;
		rowNum=rowNum++;
		
		// input cost
		report.addTextCell(sheet.createRow(rowNum++), 0, translate("projectBlockInput").replace('/',' ')+ perUnit, Style.H2);
		rowNum=tables[1].writeTable(sheet, rowNum++, block.getInputs(), true);
		sumRows[1]=rowNum;
		rowNum=rowNum++;
		
		// labour cost
		report.addTextCell(sheet.createRow(rowNum++), 0, translate("projectBlockLabour")+ perUnit, Style.H2);
		rowNum=tables[2].writeTable(sheet, rowNum++, block.getLabours(), true);
		sumRows[2]=rowNum;
		rowNum=rowNum+2;
		
		// totals
		Row row = sheet.createRow(rowNum++);
		String totals= "project.report.blockDetail.totals";
		report.addTextCell(row, 6, translate(totals), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 6, translate("project.report.blockDetail.incomes"), Style.LABEL);
		report.addFormulaCell(row, 7, String.format("H%d",sumRows[0]), Style.CURRENCY);
		report.addFormulaCell(row, 9, String.format("J%d",sumRows[0]), Style.CURRENCY);
		if (incomeGen) {
			report.addFormulaCell(row, 10, String.format("K%d",sumRows[0]), Style.CURRENCY);
			report.addFormulaCell(row, 11, String.format("L%d",sumRows[0]), Style.CURRENCY);
		}

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 6, translate("project.report.blockDetail.costs"), Style.LABEL);
		report.addFormulaCell(row, 7, String.format("H%d+H%d",sumRows[1],sumRows[2]), Style.CURRENCY);
		report.addFormulaCell(row, 9, String.format("J%d+J%d",sumRows[1],sumRows[2]), Style.CURRENCY);
		if (incomeGen) {
			report.addFormulaCell(row, 10, String.format("K%d+K%d",sumRows[1],sumRows[2]), Style.CURRENCY);
			report.addFormulaCell(row, 11, String.format("L%d+L%d",sumRows[1],sumRows[2]), Style.CURRENCY);
		}


		row = sheet.createRow(rowNum++);
		title = incomeGen ? "project.report.blockDetail.netIncome" : "project.report.blockDetail.netIncome.nongen";
		report.addTextCell(row, 6, translate(title), Style.LABEL);
		report.addFormulaCell(row, 7, String.format("H%d-H%d", rowNum-2, rowNum-1), Style.CURRENCY);
		report.addFormulaCell(row, 9, String.format("J%d-J%d", rowNum-2, rowNum-1), Style.CURRENCY);
		

		if (report.isCompleteReport()) {
			ExcelBlockLink blockLink =  block.getClass()==Block.class ? report.getBlockLinks().get(block.getBlockId()) : report.getBlockLinksWithoutProject().get(block.getBlockId());
			blockLink.income="'"+sheet.getSheetName()+"'!$H$"+(rowNum-2);
			blockLink.incomeCash="'"+sheet.getSheetName()+"'!$J$"+(rowNum-2);
			blockLink.incomeCashNoTransport="'"+sheet.getSheetName()+"'!$K$"+(rowNum-2);
			blockLink.incomeCashOnlyTransport="'"+sheet.getSheetName()+"'!$L$"+(rowNum-2);
			
			blockLink.cost="'"+sheet.getSheetName()+"'!$H$"+(rowNum-1);
			blockLink.costCash="'"+sheet.getSheetName()+"'!$J$"+(rowNum-1);
			blockLink.netIncome="'"+sheet.getSheetName()+"'!$H$"+rowNum;
			blockLink.netIncomeCash="'"+sheet.getSheetName()+"'!$J$"+rowNum;
		}
		
		return rowNum;
	}
	
	public Sheet projectChronology(ExcelWrapper report, Project project) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_CHRONOLOGY));

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
			if (report.isCompleteReport()) {
				report.getBlockLinks().get(block.getBlockId()).harvestRow=rowNum;
			}
			
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
				if (report.isCompleteReport()) {
					report.getBlockLinksWithoutProject().get(block.getBlockId()).harvestRow=rowNum;
				}
				
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
	
	public Sheet projectProduction(ExcelWrapper report, Project project) {
		boolean incomeGen = project.getIncomeGen();
		int duration = project.getDuration();
		
		// show cycle labels if there is at least one block that has cycles
		boolean hasBlockWithCycles = false;
		for (BlockBase b : project.getBlocks()) {
			if (b.isCycles()) {
				hasBlockWithCycles=true;
				break;
			}
		}
		if (!hasBlockWithCycles) {
			for (BlockBase b : project.getBlocksWithout()) {
				if (b.isCycles()) {
					hasBlockWithCycles=true;
					break;
				}
			}
		}

		String name = (incomeGen) ? "projectBlock.name" : "projectActivity.name";
		String unit = (incomeGen) ? "projectBlock.prodUnit" : "projectActivity.prodUnit";
		
		String[] titles = new String[11+project.getDuration()];
		titles[0]=translate(name);
		titles[1]="";
		titles[2]=translate(unit);
		titles[3]=hasBlockWithCycles ? translate("projectBlock.cycleLength") : "";
		titles[4]="";
		titles[5]=hasBlockWithCycles ? translate("projectBlock.cyclePerYear") : "";
		titles[6]="";
		for (int i=1; i <= duration; i++) {
			titles[6+i]=String.valueOf(i);
		}
		titles[7+duration]="";
		titles[8+duration]="";
		titles[9+duration]="";
		titles[10+duration]="";
		
		String reportName = incomeGen ? translate("project.report.production") : translate("project.report.production.nonGen");
		int rowNum = 0;
		
		Sheet sheet = report.getWorkbook().createSheet(translate(incomeGen ? SheetName.PROJECT_PATTERN : SheetName.PROJECT_PATTERN_NONGEN));
		
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
		
		if (block.isCycles()) {
			report.addNumericCell(row, cellNum++, block.getCycleLength());
			report.addSelectCell(row, cellNum++, block.getLengthUnit(), lengthUnits(), sheet);
			report.addNumericCell(row, cellNum++, block.getCyclePerYear());
			report.addTextCell(row, cellNum++, translate("units.perYear"));
		} else {
			cellNum=cellNum+4;
		}

		ExcelBlockLink blockLink = new ExcelBlockLink();
		blockLink.noCycles=!block.isCycles();
		int duration=block.getProject().getDuration();
		blockLink.cyclesPerYear = "'"+sheet.getSheetName()+"'!F"+rowNum;
		blockLink.qtyPerYear = new String[duration];
		
		for (int i=1; i <= duration; i++) {
			BlockPattern pattern = block.getPatterns().get(i);
			report.addNumericCell(row, cellNum++, pattern.getQty());
			if (report.isCompleteReport()) {
				blockLink.qtyPerYear[i-1]="'"+sheet.getSheetName()+"'!"+getColumn(6+i)+rowNum;
			}
		}
		
//		if (block.getProject().getIncomeGen() && block.isCycles()) {
//			report.addNumericCell(row, cellNum++, block.getCycleFirstYearIncome());
//			blockLink.cyclesFirstYearPayment=String.format("'%s'!$%s$%d",sheet.getSheetName(),getColumn(9+block.getProject().getDuration()),rowNum);
//			report.addTextCell(row, cellNum++, translate("units.perYear"));
//			report.addNumericCell(row, cellNum++, block.getCycleFirstYear());
//			blockLink.cyclesFirstYearProduction=String.format("'%s'!$%s$%d",sheet.getSheetName(),getColumn(7+block.getProject().getDuration()),rowNum);
//			report.addTextCell(row, cellNum++, translate("units.perYear"));
//		}
		
		if (report.isCompleteReport()) {
			if (block.getClass()==Block.class) {
				report.getBlockLinks().put(block.getBlockId(), blockLink);
			} else {
				report.getBlockLinksWithoutProject().put(block.getBlockId(), blockLink);
			}
		}	
		return rowNum;
	}
	
	public Sheet projectGeneralDescription(ExcelWrapper report, Project project) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_DESCRIPTION));

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
		report.addTextCell(row, 0, translate("settings.discountRate"));
		report.addNumericCell(row, 2, rivConfig.getSetting().getDiscountRate());
		report.addTextCell(row, 3, "%");
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_DISCOUNT_RATE, "'"+sheet.getSheetName()+"'!$C$"+rowNum);
		}

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.duration"));
		report.addNumericCell(row, 2, project.getDuration());
		report.addTextCell(row, 3, translate("units.years"));
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_DURATION, "'"+sheet.getSheetName()+"'!$C$"+rowNum);
		}
		
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
			report.addTextCell(row, 2, project.getAppConfig1()==null ? translate("customFields.appConfig1.generic"): project.getAppConfig1().getDescription());
		}
		if (rivConfig.getSetting().getAdmin2Enabled()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, rivConfig.getSetting().getAdmin2Title());
			report.addTextCell(row, 2, project.getAppConfig2()==null ? translate("customFields.appConfig2.generic") : project.getAppConfig2().getDescription());
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

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.createdBy"));
		report.addTextCell(row, 2, project.getCreatedBy());
		
		rowNum++;	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.general.benefs"), Style.H2);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.benefName"));
		report.addTextCell(row, 2, project.getBenefName());
		rowNum = addBigTextCell(report, sheet, rowNum++,translate("project.benefDesc"), project.getBenefDesc(), project.getBenefDescQualitative(), rivConfig.getSetting().isQualBenefDescEnabled());

		
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
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.justification"), project.getJustification(), project.getJustificationQualitative(), rivConfig.getSetting().isQualJustificationEnabled());
		
		rowNum++;	
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.projectDescription"), project.getProjDesc(), project.getProjDescQualitative(), rivConfig.getSetting().isQualProjDescEnabled());

		rowNum++;	
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.activities"), project.getActivities(), project.getActivitiesQualitative(), rivConfig.getSetting().isQualActivitiesEnabled());

		rowNum++;	
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.technology"), project.getTechnology(), project.getTechnologyQualitative(), rivConfig.getSetting().isQualTechnologyEnabled());

		rowNum++;
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.requirements"), project.getRequirements(), project.getRequirementsQualitative(), rivConfig.getSetting().isQualRequirementsEnabled());

		if (!project.getIncomeGen()) {
			rowNum++;	
			rowNum= addBigTextCell(report, sheet, rowNum, translate("project.sustainability.nongen"), project.getSustainability(), project.getSustainabilityQualitative(), rivConfig.getSetting().isQualSustainabilityEnabled());

		}
		
		rowNum++;
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.enviroImpact"), project.getEnviroImpact(), project.getEnviroImpactQualitative(), rivConfig.getSetting().isQualEnviroImpactEnabled());

		rowNum++;
		String label;
		if (project.getIncomeGen()) {
			label = "project.market";
		} else {
			label = "project.demand";
		}
		rowNum= addBigTextCell(report, sheet, rowNum, translate(label), project.getMarket(), project.getMarketQualitative(), rivConfig.getSetting().isQualMarketEnabled());

		rowNum++;
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.organization"), project.getOrganization(), project.getOrganizationQualitative(), rivConfig.getSetting().isQualOrganizationEnabled());

		rowNum++;
		rowNum= addBigTextCell(report, sheet, rowNum, translate("project.assumptions"), project.getAssumptions(), project.getAssumptionsQualitative(), rivConfig.getSetting().isQualAssumptionsEnabled());
		return sheet;
	}
	
	private int addBigTextCell(ExcelWrapper report, Sheet sheet, int rowNum, String title, String text, Short stars, boolean showStars) {
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.H2);
		if (showStars) {
			report.addTextCell(row, 2, translate("qualitativeAnalysis.report.stars"), Style.LABEL_RIGHT);
			report.addTextCell(row, 3, stars+"/5");
		}
		row = sheet.createRow(rowNum++);
		row.getSheet().addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 4));
		report.addTextCell(row, 0, text, Style.BIGTEXT);
		row.setHeight((short)(row.getHeight()*8)); 
		return rowNum;
	}
	
	public Sheet projectGeneralDetailNongen(ExcelWrapper report, Project project, boolean template) {

		int[] subtotalRows = new int[3];
		int rowNum=0;
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_GENERAL));

		sheet.setColumnWidth(1, 75*36);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.generalCostsDetail"), Style.TITLE);

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectNongenInput"), Style.H2);
		
		String[] headerInputs = new String[]{"projectNongenInput.description","projectNongenInput.unitType","projectNongenInput.unitNum",
				"projectNongenInput.unitCost","projectNongenInput.total","projectNongenInput.donated",
				"projectNongenInput.ownResource"};
		XlsTable table = new XlsTable(report, headerInputs)
		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "DX*CX", true)
		.addColumn(XlsColumnType.CURRENCY, "getDonated", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX", true);
		rowNum=table.writeTable(sheet, rowNum++, template ? null : project.getNongenMaterials(), true);
		subtotalRows[0]=rowNum++;

		String[] headerLabours = new String[]{"projectNongenLabour.description","projectNongenLabour.unitType","projectNongenLabour.unitNum",
				"projectNongenLabour.unitCost","projectNongenLabour.total","projectNongenLabour.donated",
				"projectNongenLabour.ownResource"};
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectNongenLabour"), Style.H2);
		XlsTable tableLabour = new XlsTable(report, headerLabours)

		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addSelectColumn("getUnitType", labourTypes())
		.addColumn(XlsColumnType.NUMERIC, "getUnitNum", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addColumn(XlsColumnType.FORMULA, "DX*CX", true)
		.addColumn(XlsColumnType.CURRENCY, "getDonated", true)
		.addColumn(XlsColumnType.FORMULA, "EX-FX", true);
		rowNum=tableLabour.writeTable(sheet, rowNum++, template ? null : project.getNongenLabours(), true);
		subtotalRows[1]=rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectNongenGeneral"), Style.H2);

		String[] headerGeneral = new String[]{"projectNongenGeneral.description","projectNongenGeneral.unitType","projectNongenGeneral.unitNum",
				"projectNongenGeneral.unitCost","projectNongenGeneral.total","projectNongenGeneral.donated",
				"projectNongenGeneral.ownResource"};
		table.titles=headerGeneral;
		rowNum = table.writeTable(sheet, rowNum++, template ? null : project.getNongenMaintenance(), true);
		subtotalRows[2]=rowNum++;
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 3, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, 4, String.format("E%d+E%d+E%d",subtotalRows[0],subtotalRows[1],subtotalRows[2]));
		report.addFormulaCell(row, 5, String.format("F%d+F%d+F%d",subtotalRows[0],subtotalRows[1],subtotalRows[2]));
		report.addFormulaCell(row, 6, String.format("G%d+G%d+G%d",subtotalRows[0],subtotalRows[1],subtotalRows[2]));

		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_GENERAL_TOTAL_ROW, String.valueOf(rowNum));
			report.addLink(ExcelLink.PROJECT_GENERAL_NONGEN_TOTAL, "'"+sheet.getSheetName()+"'!$E$"+rowNum);
			report.addLink(ExcelLink.PROJECT_GENERAL_NONGEN_DONATED, "'"+sheet.getSheetName()+"'!$F$"+rowNum);
			report.addLink(ExcelLink.PROJECT_GENERAL_NONGEN_FINANCED, "'"+sheet.getSheetName()+"'!$G$"+rowNum);
		}
		return sheet;
	}
	
	public Sheet projectGeneralDetail(ExcelWrapper report, Project project, boolean without, boolean template) {
		int rowNum=0;
		int yearsInReport=project.isPerYearGeneralCosts()?project.getDuration():1;
		
		String title = without ? translate("project.report.generalCostsDetail") + " " + translate("project.without")
				: project.isWithWithout() ? translate("project.report.generalCostsDetail") + " " + translate("project.with")
				: translate("project.report.generalCostsDetail");
		SheetName sheetname = project.isWithWithout() ? without ? SheetName.PROJECT_GENERAL_WITHOUT : SheetName.PROJECT_GENERAL_WITH : SheetName.PROJECT_GENERAL;
				
		Sheet sheet = report.getWorkbook().createSheet(translate(sheetname));
		((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		sheet.setSelected(!without);
		
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);
		
		ArrayList<String> headerSupplies = new ArrayList<String>();
		headerSupplies.add("projectGeneralSupplies.description");
		headerSupplies.add("projectGeneralSupplies.unitType"); 
		headerSupplies.add("projectGeneralSupplies.unitCost");
		
		ArrayList<String> headerPersonnel = new ArrayList<String>();
		headerPersonnel.add("projectGeneralPersonnel.description");
		headerPersonnel.add("projectGeneralPersonnel.unitType"); 
		headerPersonnel.add("projectGeneralPersonnel.unitCost");
		
		int[] totalRows = new int[2];
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectGeneralSupplies"), Style.H2);
		
		XlsTable table = new XlsTable(report, headerSupplies.toArray(new String[headerSupplies.size()]));
		table.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addPerYearColumns("getUnitNum", yearsInReport, false, false) // unitNum
		.addPerYearColumnsFormula("CX*%X", yearsInReport, true, new int[] {yearsInReport}) // total
		.addPerYearColumns("getOwnResources", yearsInReport, true, true) // ownResources
		.addPerYearColumnsFormula("%X-%X", yearsInReport, true, new int[] {yearsInReport*2, yearsInReport}); // totalCash
		rowNum = table.writeTable(sheet, rowNum, template ? null : !without ? project.getGenerals() : project.getGeneralWithouts(), true);
		
		// Start header
		Row headerRow = sheet.getRow(1);
		if (project.isPerYearGeneralCosts()) {
			for (int i=0;i<4;i++) {
				int begin=3+i*project.getDuration();
				for (int y=0;y<project.getDuration();y++) {
					report.addTextCell(headerRow, begin+y, String.valueOf(y+1));
				}
			}
		}
		
		headerRow = sheet.getRow(2);
		report.addTextCell(headerRow, 3, translate("projectGeneralSupplies.unitNum"), Style.LABEL);
		report.addTextCell(headerRow, 3+yearsInReport, translate("projectGeneralSupplies.totalCost"), Style.LABEL);
		report.addTextCell(headerRow, 3+2*yearsInReport, translate("projectGeneralSupplies.ownResources"), Style.LABEL);
		report.addTextCell(headerRow, 3+3*yearsInReport, translate("projectGeneralSupplies.external"), Style.LABEL);
		if (project.isPerYearGeneralCosts()) {
			mergeCells(sheet, 2, 3, 2, 3+yearsInReport-1); 
			mergeCells(sheet, 2, 3+yearsInReport, 2, 3+2*yearsInReport-1); 
			mergeCells(sheet, 2, 3+2*yearsInReport, 2, 3+3*yearsInReport-1); 
			mergeCells(sheet, 2, 3+3*yearsInReport, 2, 3+4*yearsInReport-1); 
		}
		// end header
		
		totalRows[0]=rowNum;
		rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectGeneralPersonnel"), Style.H2);
		
		table = new XlsTable(report, headerPersonnel.toArray(new String[headerPersonnel.size()]));
		table.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addSelectColumn("getUnitType", labourTypes())
		.addColumn(XlsColumnType.CURRENCY, "getUnitCost", false)
		.addPerYearColumns("getUnitNum", yearsInReport, false, false)
		.addPerYearColumnsFormula("CX*%X", yearsInReport, true, new int[] {yearsInReport})
		.addPerYearColumns("getOwnResources", yearsInReport, true, true)
		.addPerYearColumnsFormula("%X-%X", yearsInReport, true, new int[] {yearsInReport*2, yearsInReport});
		rowNum = table.writeTable(sheet, rowNum, template ? null : !without ? project.getPersonnels() : project.getPersonnelWithouts(), true);
		totalRows[1]=rowNum;
		
		headerRow = sheet.getRow(totalRows[0]+1);
		if (project.isPerYearGeneralCosts()) {
			for (int i=0;i<4;i++) {
				int begin=3+i*project.getDuration();
				for (int y=0;y<project.getDuration();y++) {
					report.addTextCell(headerRow, begin+y, String.valueOf(y+1));
				}
			}
		}
		headerRow = sheet.getRow(totalRows[0]+2);
		report.addTextCell(headerRow, 3, translate("projectGeneralPersonnel.unitNum"), Style.LABEL);
		report.addTextCell(headerRow, 3+yearsInReport, translate("projectGeneralPersonnel.totalCost"), Style.LABEL);
		report.addTextCell(headerRow, 3+2*yearsInReport, translate("projectGeneralPersonnel.ownResources"), Style.LABEL);
		report.addTextCell(headerRow, 3+3*yearsInReport, translate("projectGeneralPersonnel.external"), Style.LABEL);
		if (project.isPerYearGeneralCosts()) {
			mergeCells(sheet, totalRows[0]+2, 3, totalRows[0]+2, 3+yearsInReport-1); 
			mergeCells(sheet, totalRows[0]+2, 3+yearsInReport, totalRows[0]+2, 3+2*yearsInReport-1); 
			mergeCells(sheet, totalRows[0]+2, 3+2*yearsInReport, totalRows[0]+2, 3+3*yearsInReport-1); 
			mergeCells(sheet, totalRows[0]+2, 3+3*yearsInReport, totalRows[0]+2, 3+4*yearsInReport-1); 
		}
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 2+yearsInReport, translate("misc.total"), Style.LABEL);
		for (int z=1;z<=3;z++) {
			for (int i=0; i<yearsInReport; i++) {
				String column = getColumn(3+(z*yearsInReport)+i);
				report.addFormulaCell(row, 3+(z*yearsInReport)+i, String.format("%s%d+%s%d", column, totalRows[0], column, totalRows[1]), Style.CURRENCY);
			}
		}
	
		if (report.isCompleteReport()) {
			if (!without) {
				report.addLink(ExcelLink.PROJECT_GENERAL_TOTAL_ROW, String.valueOf(rowNum));
				report.addLink(ExcelLink.PROJECT_GENERAL_SHEET, "'"+sheet.getSheetName()+"'");
			} else {
				report.addLink(ExcelLink.PROJECT_GENERAL_WITHOUT_TOTAL_ROW, String.valueOf(rowNum));
				report.addLink(ExcelLink.PROJECT_GENERAL_WITHOUT_SHEET, "'"+sheet.getSheetName()+"'");
			}
		}
		
		autoSizeColumns(sheet, project.isPerYearGeneralCosts() ? 2+4*yearsInReport : 6);
		return sheet;
	}
	
	private void autoSizeColumns(Sheet sheet, int cols) {
		for (short i=0;i<cols;i++) {
			sheet.autoSizeColumn(i);
		}
	}
	
	public Sheet projectInvestmentDetail(ExcelWrapper report, Project project, boolean without, boolean template) {

		int rowNum=0; int[] totalRows = new int[3];
		String title = without ? translate("project.report.investDetail") + " " + translate("project.without")
				: project.isWithWithout() ? translate("project.report.investDetail") + " " + translate("project.with")
				: translate("project.report.investDetail");
		SheetName sheetname = project.isWithWithout() ? without ? SheetName.PROJECT_INVEST_WITHOUT : SheetName.PROJECT_INVEST_WITH : SheetName.PROJECT_INVEST;
				
		Sheet sheet = report.getWorkbook().createSheet(translate(sheetname));
		
		sheet.setSelected(!without);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectInvestAsset"), Style.H2);
		if (without) {
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_ROW, String.valueOf(rowNum+2));
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_SHEET, "'"+sheet.getSheetName()+"'");
		} else {
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW, String.valueOf(rowNum+2));
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET, "'"+sheet.getSheetName()+"'");
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
		
		if (report.isCompleteReport()) {
			String salvage = "IF(AND(ISNUMBER(CX),ISNUMBER(IX),IX>0,ISNUMBER(KX),ISNUMBER(MX)), IF(AND((OR(exact(LX,\"#\"),@3=MX+IX)),@3<>MX,MOD(@3-MX,IX)=0), CX*KX, 0), 0)";
			for (int i=0;i<project.getDuration();i++) {
				assetsTable.addColumn(XlsColumnType.FORMULA, salvage, true);
			}
			String investDonated = "IF(MX=@3,FX,0)";
			for (int i=0;i<project.getDuration();i++) {
				assetsTable.addColumn(XlsColumnType.FORMULA, investDonated, true);
			}
			String investmentOwn = "IF(MX=@3,GX,0)";
			for (int i=0;i<project.getDuration();i++) {
				assetsTable.addColumn(XlsColumnType.FORMULA, investmentOwn, true);
			}
			String investmentCost = "IF(MX=@3,EX,0)";
			for (int i=0;i<project.getDuration();i++) {
				assetsTable.addColumn(XlsColumnType.FORMULA, investmentCost, true);
			}
			String investmentReplace = "IF(AND(ISNUMBER(CX),ISNUMBER(DX),ISNUMBER(IX),IX>0,ISNUMBER(MX)), IF(AND(exact(LX,\"#\"),@3>MX,MOD(@3-MX,IX)=0), CX*DX, 0), 0)";
			for (int i=0;i<project.getDuration();i++) {
				assetsTable.addColumn(XlsColumnType.FORMULA, investmentReplace, true);
			}
			
			String yearsLeft = String.format("IF(IX-MOD(%s-MX+1,IX)=IX,0,IX-MOD(%s-MX+1,IX))", 
					report.getLink(ExcelLink.PROJECT_DURATION),
					report.getLink(ExcelLink.PROJECT_DURATION));
			assetsTable.addColumn(XlsColumnType.FORMULA, yearsLeft, false);
					
			String residual = String.format("IF(OR(exact(LX,\"#\"), MX-1+IX>%s)," +
					"CX*(DX-KX)/IX*"+getColumn(13+5*project.getDuration())+"X"+"+CX*KX"+
					",0)",
					report.getLink(ExcelLink.PROJECT_DURATION)
					);
			assetsTable.addColumn(XlsColumnType.FORMULA, residual, true);
			
			String maintenance = "IF(AND(MX<=@3,OR(exact(LX,\"#\"),@3<MX+IX)),CX*JX,0)";
			for (int i=0;i<project.getDuration();i++) {
				assetsTable.addColumn(XlsColumnType.FORMULA, maintenance, true);
			}
		}
		
		int assets = assetsTable.writeTable(sheet, rowNum, template ? null : without ? project.getAssetsWithout() : project.getAssets(), true);
		totalRows[0]=assets;
		
		// add label and years for "salvage value per year", "investment donation", "invest own resources", 
		// "investment costs" calculations, etc.
		if (report.isCompleteReport()) {
			Row r = sheet.getRow(rowNum-1);
			report.addTextCell(r, 13, translate("project.report.profitability.incomes.salvage"));
			report.addTextCell(r, project.getDuration()+13, translate("project.report.cashFlow.income.invest"));
			report.addTextCell(r, project.getDuration()*2+13, translate("project.report.cashFlow.income.own"));
			report.addTextCell(r, project.getDuration()*3+13, translate("project.report.cashFlow.costs.initial"));
			report.addTextCell(r, project.getDuration()*4+13, translate("project.report.profitability.costs.replacement"));
			report.addTextCell(r, project.getDuration()*5+13, translate("project.report.profitability.incomes.residual")+" 1");
			report.addTextCell(r, project.getDuration()*5+14, translate("project.report.profitability.incomes.residual")+" 2");
			report.addTextCell(r, project.getDuration()*5+15, translate("project.report.profitability.costs.maintenance"));
			r= sheet.getRow(rowNum);
			for (int i=0;i<project.getDuration();i++) {
				report.addNumericCell(r, i+13, i+1); // salvage year
				sheet.setColumnHidden(i+13, true);
				report.addNumericCell(r, i+project.getDuration()+13, i+1); // investment donation year
				sheet.setColumnHidden(i+project.getDuration()+13, true);
				report.addNumericCell(r, i+project.getDuration()*2+13, i+1); // investment own resources year
				sheet.setColumnHidden(i+project.getDuration()*2+13, true);
				report.addNumericCell(r, i+project.getDuration()*3+13, i+1); // investment costs
				sheet.setColumnHidden(i+project.getDuration()*3+13, true);
				report.addNumericCell(r, i+project.getDuration()*4+13, i+1); // investment replacement costs
				sheet.setColumnHidden(i+project.getDuration()*4+13, true);
				report.addNumericCell(r, i+project.getDuration()*5+15, i+1); // maintenance
				sheet.setColumnHidden(i+project.getDuration()*5+15, true);
			}
			sheet.setColumnHidden(project.getDuration()*5+13, true);
			sheet.setColumnHidden(project.getDuration()*5+14, true);
		}
		rowNum=assets+1;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectInvestLabour"), Style.H2);
		
		if (without) {
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTLABOUR_WITHOUT_ROW, String.valueOf(rowNum+2));
		} else {
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTLABOUR_ROW, String.valueOf(rowNum+2));
		}
		
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
		
		if (report.isCompleteReport()) {
			for (int i=0;i<project.getDuration();i++) { // no salvage
				labourTable.addColumn(XlsColumnType.NONE, null, false);
			}
			String investDonated = "IF(MX=@3,FX,0)";
			for (int i=0;i<project.getDuration();i++) {
				labourTable.addColumn(XlsColumnType.FORMULA, investDonated, true);
			}
			String investmentOwn = "IF(MX=@3,GX,0)";
			for (int i=0;i<project.getDuration();i++) {
				labourTable.addColumn(XlsColumnType.FORMULA, investmentOwn, true);
			}
			String investmentCost = "IF(MX=@3,EX,0)";
			for (int i=0;i<project.getDuration();i++) {
				labourTable.addColumn(XlsColumnType.FORMULA, investmentCost, true);
			}
			for (int i=0;i<project.getDuration();i++) { // no replacement
				labourTable.addColumn(XlsColumnType.NONE, null, false);
			}
			labourTable.addColumn(XlsColumnType.NONE, null, false); // no residual
			labourTable.addColumn(XlsColumnType.NONE, null, false); // no maintenance
		}
		
		rowNum = labourTable.writeTable(sheet, rowNum, template ? null : without ? project.getLaboursWithout() : project.getLabours(), true);
		totalRows[1]=rowNum;
		rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectInvestService"), Style.H2);
		
		if (without) {
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTSERVICE_WITHOUT_ROW, String.valueOf(rowNum+2));
		} else {
			report.addLink(ExcelLink.PROJECT_INVEST_FIRSTSERVICE_ROW, String.valueOf(rowNum+2));
		}
		
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
		if (report.isCompleteReport()) {
			for (int i=0;i<project.getDuration();i++) { // no salvage
				serviceTable.addColumn(XlsColumnType.NONE, null, false);
			}
			String investDonated = "IF(MX=@3,FX,0)";
			for (int i=0;i<project.getDuration();i++) {
				serviceTable.addColumn(XlsColumnType.FORMULA, investDonated, true);
			}
			String investmentOwn = "IF(MX=@3,GX,0)";
			for (int i=0;i<project.getDuration();i++) {
				serviceTable.addColumn(XlsColumnType.FORMULA, investmentOwn, true);
			}
			String investmentCost = "IF(MX=@3,EX,0)";
			for (int i=0;i<project.getDuration();i++) {
				serviceTable.addColumn(XlsColumnType.FORMULA, investmentCost, true);
			}
			for (int i=0;i<project.getDuration();i++) { // no replacement
				serviceTable.addColumn(XlsColumnType.NONE, null, false);
			}
			serviceTable.addColumn(XlsColumnType.NONE, null, false); // no residual
			serviceTable.addColumn(XlsColumnType.NONE, null, false); // no maintenance
		}
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
			report.addLink(ExcelLink.PROJECT_INVEST_FINANCED, "'"+sheet.getSheetName()+"'!$H$"+rowNum);
		}
		
//		autoSizeColumns(sheet, 13);
		
		return sheet;
	}

	public Sheet projectSummary(ExcelWrapper report, Project project, ProjectResult pr) {		
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_SUMMARY));
		
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
			cellNum = 3;
			
			report.addTextCell(row, 0, translate("project.report.summary.indicators"), Style.H2);
			
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, String.format("%s (%s)", translate("project.irr"), translate("project.report.summary.indicators.total")));
			report.addNumericCell(row, cellNum, pr.getIrr().multiply(BigDecimal.valueOf(100.0)));

			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, String.format("%s (%s)", translate("project.npv"), translate("project.report.summary.indicators.total")));
			report.addNumericCell(row, cellNum, pr.getNpv(),Style.CURRENCY);

			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("project.report.summary.indicators.negativeFlow"));
			report.addNumericCell(row, cellNum, pr.getNegativeYears());
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
			report.addFormulaCell(row, cellNum++, String.format("B%d / %s", rowNum, "'"+translate(SheetName.PROJECT_DESCRIPTION)+"'!C3"), Style.CURRENCYUSD);
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
			report.addFormulaCell(row, cellNum++, String.format("B%d/%s", rowNum, "'"+translate(SheetName.PROJECT_DESCRIPTION)+"'!$C$3"), Style.CURRENCYUSD);
		} else {
			report.addFormulaCell(row, cellNum++, String.format("B%d/%f", rowNum, exchRate), Style.CURRENCYUSD);
		}
		if (percentage) {
			report.addFormulaCell(row, cellNum++, String.format("B%d/B%d", rowNum, totalRowNum+1),Style.PERCENT);
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
	
	public Sheet projectSustainability(ExcelWrapper report, Project project, ProjectResult result) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_CASH_FLOW_NONGEN));

		sheet.setSelected(true);
		
		ArrayList<ProjectFinanceNongen> data = ProjectFinanceNongen.analyzeProject(project);
		
		int rowNum=0;
		//int cellNum = 0;
		StringBuilder formulaBuild;
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
		Row rowAccumulated = sheet.createRow(rowNum++);
		report.addTextCell(rowAccumulated, 0, translate("project.report.cashFlowNongen.accumulated"), Style.LABEL);


		if (report.isCompleteReport()) {
			String assetSheetName = report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
			String contribSheetName = translate(SheetName.PROJECT_CONTRIBUTIONS);
			
			int assetSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW))+project.getAssets().size();
			int labourSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTLABOUR_ROW))+project.getLabours().size(); 
			int serviceSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTSERVICE_ROW))+project.getServices().size(); 
			
			for (int year=1;year<=project.getDuration();year++) {
				String col = getColumn(year);
				report.addNumericCell(sheet.getRow(1), year, year);
				
				// income
				// investment donation
				formulaBuild = new StringBuilder();
				formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(year+12+project.getDuration()), assetSumRow));
				formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(year+12+project.getDuration()), labourSumRow));
				formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(year+12+project.getDuration()), serviceSumRow));
				report.addFormulaCell(rows[0], year, formulaBuild.toString(), Style.CURRENCY);
				
				// own contribution
				formulaBuild = new StringBuilder();
				formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(year+12+project.getDuration()*2), assetSumRow));
				formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(year+12+project.getDuration()*2), labourSumRow));
				formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(year+12+project.getDuration()*2), serviceSumRow));
				report.addFormulaCell(rows[1], year, formulaBuild.toString(), Style.CURRENCY);
				
				// salvage
				String formula = String.format("%s!$%s$%d",  assetSheetName, getColumn(year+12), assetSumRow);
				report.addFormulaCell(rows[2], year, formula, Style.CURRENCY);
				
				// charges 
				formulaBuild = new StringBuilder();
				
				for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
					formulaBuild.append("("+blockLink.incomeCash+"*"+blockLink.qtyPerYear[year-1]);
					if (!blockLink.noCycles) {
						formulaBuild.append("*"+blockLink.cyclesPerYear);
					}	
					formulaBuild.append(")+");
				}
				report.addFormulaCell(rows[3], year, formulaBuild.deleteCharAt(formulaBuild.length()-1).toString(), Style.CURRENCY);
				
				// contributions to general costs
				report.addFormulaCell(rows[4], year, report.getLink(ExcelLink.PROJECT_GENERAL_NONGEN_DONATED), Style.CURRENCY);
				
				// contributions
				report.addFormulaCell(rows[5], year, 
						String.format("%s!$%s$%d", contribSheetName, getColumn(project.isPerYearContributions()? 3+project.getDuration()+year : 5), 4+project.getContributions().size())
					, Style.CURRENCY);
				
				// total
				report.addFormulaCell(rowTotal1, year, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 3, 8), Style.CURRENCY);
				
				// investment costs
				formulaBuild = new StringBuilder();
				formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(year+12+project.getDuration()*3), assetSumRow));
				formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(year+12+project.getDuration()*3), labourSumRow));
				formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(year+12+project.getDuration()*3), serviceSumRow));
				report.addFormulaCell(rows[6], year, formulaBuild.toString(), Style.CURRENCY);
				
				// replacement
				formula = String.format("%s!$%s$%d",  assetSheetName, getColumn(year+12+project.getDuration()*4), assetSumRow);
				report.addFormulaCell(rows[7], year, formula, Style.CURRENCY);

				// maintenance
				formulaBuild = new StringBuilder();
				formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(year+14+project.getDuration()*5), assetSumRow));
				report.addFormulaCell(rows[8], year, formulaBuild.toString(), Style.CURRENCY);

				// operation
				formulaBuild = new StringBuilder();
				for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
					formulaBuild.append("("+blockLink.costCash+"*"+blockLink.qtyPerYear[year-1]);
					if (!blockLink.noCycles) {
						formulaBuild.append("*"+blockLink.cyclesPerYear);
					}
					formulaBuild.append(")+");
				}
				report.addFormulaCell(rows[9], year, formulaBuild.deleteCharAt(formulaBuild.length()-1).toString(), Style.CURRENCY);

				// general
				report.addFormulaCell(rows[10], year, report.getLink(ExcelLink.PROJECT_GENERAL_NONGEN_TOTAL), Style.CURRENCY);
				
				// Costs total
				report.addFormulaCell(rowTotal2, year, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 12, 16), Style.CURRENCY);
	
				// Net Balance
				report.addFormulaCell(rowBalance, year, String.format("%1$s9-%1$s17", col), Style.CURRENCY);
				// Accumulated net balance
				report.addFormulaCell(rowAccumulated, year, year==1 ? "B18":String.format("%s19+%s18",getColumn(year-1),col), Style.CURRENCY);
			}
		} else {
			int cellNum = 1;
			for (ProjectFinanceNongen pfn : data) {
				String col = getColumn(cellNum);
				
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
				// Accumulated net balance
				report.addFormulaCell(rowAccumulated, cellNum, cellNum==1 ? "B18":String.format("%s19+%s18",getColumn(cellNum-1),col), Style.CURRENCY);
				
				cellNum++;
			}
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
	
	public Sheet projectContributions(ExcelWrapper report, Project project, boolean template) {
		int yearsInReport=project.isPerYearContributions()?project.getDuration():1;
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_CONTRIBUTIONS));

		sheet.setSelected(true);
		int rowNum=0;
		int cellNum = 0;
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.report.contributions"), Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("projectContribution"), Style.H2);
		String[] header = new String[]{"projectContribution.description","projectContribution.contributor","projectContribution.unitType","projectContribution.unitCost"};
		
		Map<Integer, String> donorsByOrder = new HashMap<Integer,String>();
		for (Donor d : project.getDonors()) {
			String desc;
			if (d.getNotSpecified()) { desc = translate("project.donor.notSpecified"); }
			else if (d.getContribType()==4 && d.getDescription().equals("state-public")) { desc = translate("project.donor.statePublic"); }
			else { desc = d.getDescription(); }
			donorsByOrder.put(d.getOrderBy(), desc);
		}
		
		XlsTable table = new XlsTable(report, header)
		.addColumn(XlsColumnType.TEXT, "getDescription", false)
		.addSelectColumnIntBased("getDonorOrderBy", donorsByOrder)
		.addColumn(XlsColumnType.TEXT, "getUnitType", false)
		.addColumn(XlsColumnType.NUMERIC, "getUnitCost", false)
		.addPerYearColumns("getUnitNum", yearsInReport, false, false) // unitNum
		.addPerYearColumnsFormula("DX*%X", yearsInReport, true, new int[] {yearsInReport}); // total
		rowNum=table.writeTable(sheet, rowNum, template ? null : project.getContributions(), true);
		
		// Start header
		Row headerRow = sheet.getRow(1);
		if (project.isPerYearContributions()) {
			for (int i=0;i<=1;i++) {
				int begin=4+i*project.getDuration();
				for (int y=0;y<project.getDuration();y++) {
					report.addTextCell(headerRow, begin+y, String.valueOf(y+1));
				}
			}
		}
		
		headerRow = sheet.getRow(2);
		report.addTextCell(headerRow, 4, translate("projectGeneralSupplies.unitNum"), Style.LABEL);
		report.addTextCell(headerRow, 4+yearsInReport, translate("projectGeneralSupplies.totalCost"), Style.LABEL);
		if (project.isPerYearContributions()) {
			mergeCells(sheet, 2, 4, 2, 4+yearsInReport-1); 
			mergeCells(sheet, 2, 4+yearsInReport, 2, 4+2*yearsInReport-1); 
		}
		// end header
		
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
	
	public Sheet projectAmortization(ExcelWrapper report, Project project, FinanceMatrix matrix, boolean loan1) {
		// helper links to line numbers for loan amortization calculation
		final int loanTotal=3;
		final int loanCapital=4;
		final int loanInterest=5;
		final int loanInterestCapital=6;
		final int loanInterestDuringGrace=7;
		final int loanInterestAfterGrace=8;
		
		String title=loan1 ? translate("project.loan1") : translate("project.loan2");
		Sheet sheet = report.getWorkbook().createSheet(title);
		sheet.setSelected(true);
		
		int payPerYear = loan1 ? project.getLoan1PaymentsPerYear() : project.getLoan2PaymentsPerYear();
		
		// setup header and row titles
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.amortization.title")+ ": "+title, Style.TITLE);
		sheet.setColumnWidth(0, 165*36);
		
		Row rowYear = sheet.createRow(rowNum++);
		report.addTextCell(rowYear, 0, translate("projectBlock.pattern.years"), Style.LABEL);
		
		Row rowPeriod = sheet.createRow(rowNum++);
		report.addTextCell(rowPeriod, 0, translate("project.report.amortization.period"), Style.LABEL);
		
		rowNum=loanTotal;
		if (report.isCompleteReport()) {
			String[] labels = new String[] {"project.report.amortization.total","project.report.amortization.capital", "project.report.amortization.interest",
					"project.report.amortization.capitalDuringGrace","project.report.amortization.interestDuringGrace","project.report.amortization.interestDuringGraceCapital"};
			for (String s : labels) {
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate(s));
			}
		} else {
			String[] labels = new String[] {"project.report.amortization.capital", "project.report.amortization.interest"};
			rowNum++;
			for (String s : labels) {
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate(s));
			}
		}
		
		
		int cellNum; int payPeriod=1;
		for (short index=1; index <= project.getDuration(); index++) {
			cellNum=(index-1)*payPerYear+1;
			report.addTextCell(rowYear, cellNum, String.valueOf(index), Style.LABEL);
					
			if (payPerYear>1) {
				mergeCells(sheet, rowYear.getRowNum(), cellNum, rowYear.getRowNum(), cellNum+payPerYear-1);
			}
			
			for (int per=0; per<payPerYear; per++) {
				report.addNumericCell(rowPeriod, cellNum+per, payPeriod++);
			}
		}
		
		// data
		payPeriod=0; String col;
		String formula;
		
		if (report.isCompleteReport()) {
		
			String paramLoanPeriods = loan1 ? report.getLink(ExcelLink.PROJECT_LOAN1_PAYMENTS_PER_YEAR) : report.getLink(ExcelLink.PROJECT_LOAN2_PAYMENTS_PER_YEAR);
			String paramAnnualInfl =report.getLink(ExcelLink.PROJECT_INFLATION)+"/"+paramLoanPeriods;
			String paramLoanAmt = loan1 ? report.getLink(ExcelLink.PROJECT_LOAN1_AMOUNT) : report.getLink(ExcelLink.PROJECT_LOAN2_AMOUNT);
			String paramLoanInterest = loan1 ? report.getLink(ExcelLink.PROJECT_LOAN1_RATE) : report.getLink(ExcelLink.PROJECT_LOAN2_RATE);
			String paramLoanDuration = loan1 ? report.getLink(ExcelLink.PROJECT_LOAN1_DURATION) : report.getLink(ExcelLink.PROJECT_LOAN2_DURATION);
			String paramLoanGraceCapital = loan1 ? report.getLink(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL) : report.getLink(ExcelLink.PROJECT_LOAN2_GRACE_CAPITAL) ;
			String paramLoanGraceInterest = loan1 ? report.getLink(ExcelLink.PROJECT_LOAN1_GRACE_INTEREST)  : report.getLink(ExcelLink.PROJECT_LOAN2_GRACE_INTEREST);
			String paramLoan2Year = report.getLink(ExcelLink.PROJECT_LOAN2_YEAR);
			
			for (int y=0;y<project.getDuration();y++) {
				for (int per=0;per<payPerYear;per++) {
					payPeriod++;
					col = getColumn(payPeriod);
					
					// total payment
					formula = String.format("IF(%s3<=%s,0,IF(%s3>%s,0,PMT((%s-%s)/100,%s-%s,-MAX(%s%d,%s),0)))", 
						 col, 
						 loan1 ? paramLoanGraceCapital : paramLoanGraceCapital+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods, 
						 col, 
						 loan1 ? paramLoanDuration : paramLoanDuration+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods, 
						 paramLoanInterest, paramAnnualInfl,
						 paramLoanDuration, paramLoanGraceCapital, 
						 col, loanInterestCapital+1, paramLoanAmt
					);
					report.addFormulaCell(sheet.getRow(loanTotal), payPeriod, formula, Style.CURRENCY);
					
					// capital
					formula = String.format("IF(%s3<=%s,0,IF(%s3>%s,0,PPMT((%s-%s)/100,%s3-%s,%s-%s,-MAX(%s%d,%s),0)))", 
						col, 
						loan1 ? paramLoanGraceCapital : paramLoanGraceCapital+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods, 
						col, 
						loan1 ? paramLoanDuration : paramLoanDuration+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods, 
						
						paramLoanInterest, paramAnnualInfl, 
						
						col, 
						loan1 ? paramLoanGraceCapital : paramLoanGraceCapital+"-("+paramLoan2Year+"-1)*"+paramLoanPeriods, 
						
						paramLoanDuration, paramLoanGraceCapital,
						
						col, loanInterestCapital+1, paramLoanAmt
					);
					report.addFormulaCell(sheet.getRow(loanCapital), payPeriod, formula, Style.CURRENCY);
					
					// interest
					formula = String.format("%s%d-%s%d+IF(%s3<=%s,0,%s%d)", 
							col, loanTotal+1, col, loanCapital+1, col, 
							loan1 ? paramLoanGraceInterest : paramLoanGraceInterest+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods, 
							col, loanInterestAfterGrace+1
					);
					report.addFormulaCell(sheet.getRow(loanInterest), payPeriod, formula, Style.CURRENCY);
					
					// initial capital grace on interest
					formula = String.format("IF(%s3<%s,0,IF(%s3=%s,%s,IF(%s3<=%s,%s%d+%s%d,0)))", 
						col, loan1 ? "1" : "("+paramLoan2Year+"-1)*"+paramLoanPeriods+"+1",
						col, 
						loan1 ? "1" : "("+paramLoan2Year+"-1)*"+paramLoanPeriods+"+1",
						paramLoanAmt,
						col,
						loan1 ? paramLoanDuration : paramLoanDuration+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods,
						getColumn(payPeriod-1), loanInterestDuringGrace,
						getColumn(payPeriod-1), loanInterestAfterGrace
						);	
					report.addFormulaCell(sheet.getRow(loanInterestCapital), payPeriod, formula, Style.CURRENCY);
					
					//  interest grace for interest
					formula = String.format("IF(%s3<%s,0,IF(%s3<=%s,%s7*(%s-%s)/100,0))",
						col,
						loan1 ? "1" : "("+paramLoan2Year+"-1)*"+paramLoanPeriods+"+1",
						col, 
						loan1 ? paramLoanGraceInterest : paramLoanGraceInterest+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods, 
						col, 
						paramLoanInterest, paramAnnualInfl
					);
					report.addFormulaCell(sheet.getRow(loanInterestDuringGrace), payPeriod, formula, Style.CURRENCY);
					
					// interest grace on capital
					formula = String.format("IF(%s8>0,0,IF(%s3<=%s,%s7*(%s-%s)/100,0))", 
							col, col, 
							loan1 ? paramLoanGraceCapital : paramLoanGraceCapital+"+("+paramLoan2Year+"-1)*"+paramLoanPeriods,
							col, 
							paramLoanInterest, paramAnnualInfl
					);
					report.addFormulaCell(sheet.getRow(loanInterestAfterGrace), payPeriod, formula, Style.CURRENCY);
				}
			}
			sheet.getRow(loanTotal).setZeroHeight(true);
			sheet.getRow(loanInterestCapital).setZeroHeight(true);
			sheet.getRow(loanInterestDuringGrace).setZeroHeight(true);
			sheet.getRow(loanInterestAfterGrace).setZeroHeight(true);
		}  else {
			List<Amortization> loan = loan1 ? matrix.getLoan1() : matrix.getLoan2();
			for (int y=0;y<project.getDuration();y++) {
				for (int per=0;per<payPerYear;per++) {
					payPeriod++;
					col = getColumn(payPeriod);
					
					// capital
					double value = loan.get(payPeriod-1).getCapital();
					report.addNumericCell(sheet.getRow(loanCapital), payPeriod, value, Style.CURRENCY);
					
					// interest
					value = loan.get(payPeriod-1).getInterest();
					report.addNumericCell(sheet.getRow(loanInterest), payPeriod, value, Style.CURRENCY);
				}
			}
		}
		return sheet;
	}
	
	public Sheet projectCashFlow(ExcelWrapper report, Project project, FinanceMatrix matrix, boolean without) {
		// helper links to line numbers for loan amortization calculation
		final String loan1Sheet=translate("project.loan1");
		final String loan2Sheet=translate("project.loan2");
		
		final int loanCapital=5;
		final int loanInterest=6;

		List<ProjectFinanceData> data = matrix.getYearlyData();
		
		String sheetname;
		String title;
		if (!without) {
			if (project.isWithWithout()) {
				sheetname=translate(SheetName.PROJECT_CASH_FLOW_WITH);
				title=translate("project.report.cashFlow") + " "+translate("project.with");
			} else {
				sheetname=translate(SheetName.PROJECT_CASH_FLOW);
				title=translate("project.report.cashFlow");
			}
		} else { // without
			sheetname=translate(SheetName.PROJECT_CASH_FLOW_WITHOUT);
			title=translate("project.report.cashFlow") + " "+translate("project.without");
		}
		
		Sheet sheet = report.getWorkbook().createSheet(sheetname);
		sheet.setSelected(true);
		
		// setup header and row titles
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);
		sheet.setColumnWidth(0, 165*36);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectBlock.pattern.years"), Style.LABEL);
		for (short index=1; index <= data.size(); index++) {
			report.addNumericCell(row, index, index);
			sheet.setColumnWidth(index, 90*36);
		}
		
		// Incomes
		rowNum = addRowTitles(new String[] { "project.report.profitability.incomes",
				"project.report.cashFlow.income.main","project.report.cashFlow.income.sales", "project.report.profitability.incomes.salvage","misc.subtotal","","project.report.cashFlow.income.finance","project.report.cashFlow.income.invest", "project.report.cashFlow.income.own",
				without ? "project.report.cashFlow.income.investWithout" : "project.report.cashFlow.income.loans","project.report.cashFlow.income.loanwc","project.report.cashFlow.income.wcDonation","project.report.cashFlow.income.wcOwn","misc.subtotal"
		}, rowNum++, sheet, report);
		
		row = sheet.createRow(rowNum);
		report.addTextCell(row, 0, translate("project.report.cashFlow.income.total"), Style.LABEL);
		rowNum=rowNum+2;
		
		// Expenses
		rowNum = addRowTitles(new String[] { "project.report.cashFlow.costs",
				"project.report.cashFlow.costs.invest","project.report.cashFlow.costs.initial", "project.report.profitability.costs.replacement","misc.subtotal"
		,"",
				"project.report.cashFlow.costs.recurrent","project.report.profitability.costs.operation", "project.report.profitability.costs.general",
				 "project.report.profitability.costs.maintenance","misc.subtotal"
		,"",
				 "project.report.cashFlow.financing","project.report.cashFlow.wcCapital","project.report.cashFlow.wcInterest", "project.report.cashFlow.priCapital", "project.report.cashFlow.priInterest",
				"project.report.cashFlow.secCapital", "project.report.cashFlow.secInterest", "misc.subtotal"
		}, rowNum++, sheet, report);
		
		row = sheet.createRow(rowNum);
		report.addTextCell(row, 0, translate("project.report.cashFlow.costs.total"), Style.LABEL);
		rowNum=rowNum+2;
		
		// totals
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.cashFlow.profitBefore"));
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, 
				without
					? translate("project.report.cashFlow.profit")
					: translate("project.report.cashFlow.profitAfter"));
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.report.cashFlow.cumulative"));
		
		int[] labels = {3,8,20,25,31};
		for (int i : labels) {
			sheet.getRow(i).getCell(0).setCellStyle(report.getStyles().get(Style.LABEL));
		}
		
		String col;
		boolean hasBlocks = (without && project.getBlocksWithout().size()>0) || (!without && project.getBlocks().size()>0);
		for (int yearNum=1;yearNum<=project.getDuration();yearNum++) {
			col = getColumn(yearNum);
			if (report.isCompleteReport()) {
				StringBuilder formulaBuild;
				String formula;
				
				// INCOME
				// sales
				if (hasBlocks) {
					formulaBuild = new StringBuilder();
					
					for (ExcelBlockLink blockLink : without ? report.getBlockLinksWithoutProject().values() : report.getBlockLinks().values()) {
						formulaBuild.append("("+blockLink.incomeCash+"*"+blockLink.qtyPerYear[yearNum-1]);
						if (!blockLink.noCycles) {
								formulaBuild.append("*"+blockLink.cyclesPerYear);
						}	
						formulaBuild.append(")+");
					}
					formulaBuild.deleteCharAt(formulaBuild.length()-1);
					report.addFormulaCell(sheet.getRow(4), yearNum, formulaBuild.toString(), Style.CURRENCY);
				} else {
					report.addNumericCell(sheet.getRow(4), yearNum, 0, Style.CURRENCY);
				}
				
				// salvage
				String assetSheetName = report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
				int assetSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW))+project.getAssets().size();
				int labourSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTLABOUR_ROW))+project.getLabours().size(); 
				int serviceSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTSERVICE_ROW))+project.getServices().size(); 
				
				String assetWithoutSheetName=null;
				int assetWithoutSumRow=0, labourWithoutSumRow=0, serviceWithoutSumRow=0;
				
				if (!without) {
					formula = String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12), assetSumRow);
				} else {
					assetWithoutSheetName = report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_SHEET);
					assetWithoutSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_ROW))+project.getAssetsWithout().size();
					labourWithoutSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTLABOUR_WITHOUT_ROW))+project.getLaboursWithout().size(); 
					serviceWithoutSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTSERVICE_WITHOUT_ROW))+project.getServicesWithout().size(); 
					
					formula = String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+12), assetWithoutSumRow);
				}
				report.addFormulaCell(sheet.getRow(5), yearNum, formula, Style.CURRENCY);
				
				// investment donation
				formulaBuild = new StringBuilder();
				if (!without) {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()), assetSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()), labourSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12+project.getDuration()), serviceSumRow));
				} else {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), assetWithoutSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), labourWithoutSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), serviceWithoutSumRow));
				}				
				report.addFormulaCell(sheet.getRow(9), yearNum, formulaBuild.toString(), Style.CURRENCY);
				
				// investment own resources
				formulaBuild = new StringBuilder();
				if (!without) {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()*2), assetSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()*2), labourSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12+project.getDuration()*2), serviceSumRow));
				} else {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*2), assetWithoutSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*2), labourWithoutSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*2), serviceWithoutSumRow));
				}
				report.addFormulaCell(sheet.getRow(10), yearNum, formulaBuild.toString(), Style.CURRENCY);
				
				if (without) {
					// financed investment
					formula = String.format("%s!$%s$%d-%s!$%s$%d-%s!$%s$%d",
							assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), assetWithoutSumRow,
							assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*2), assetWithoutSumRow,
							assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), assetWithoutSumRow
					);
					report.addFormulaCell(sheet.getRow(11), yearNum, formula, Style.CURRENCY);
				} else {
					// loan(s) received
					formula=String.format("IF(%s2=1,%s,0)+IF(%s2=%s,%s,0)", 
							col,report.getLink(ExcelLink.PROJECT_LOAN1_AMOUNT),
							col, report.getLink(ExcelLink.PROJECT_LOAN2_YEAR),
							report.getLink(ExcelLink.PROJECT_LOAN2_AMOUNT));
					report.addFormulaCell(sheet.getRow(11), yearNum, formula, Style.CURRENCY);
					
					// wc loan received
					if (yearNum==1) {
						report.addFormulaCell(sheet.getRow(12), 1, report.getLink(ExcelLink.PROJECT_WC_FINANCED), Style.CURRENCY);
					} else {
						report.addNumericCell(sheet.getRow(12), yearNum, 0, Style.CURRENCY);
					}
					
					// wc donated
					if (yearNum==1) {
						report.addFormulaCell(sheet.getRow(13), yearNum, report.getLink(ExcelLink.PROJECT_WC_DONATED), Style.CURRENCY);
					} else {
						report.addNumericCell(sheet.getRow(13), yearNum, 0, Style.CURRENCY);
					}
					
					// wc own resources
					if (yearNum==1) {
						report.addFormulaCell(sheet.getRow(14), yearNum, report.getLink(ExcelLink.PROJECT_WC_OWN), Style.CURRENCY);
					} else {
						report.addNumericCell(sheet.getRow(14), yearNum, 0, Style.CURRENCY);
					}
				}
				
				// COSTS
				// investment
				formulaBuild = new StringBuilder();
				if (!without) {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()*3), assetSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()*3), labourSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12+project.getDuration()*3), serviceSumRow));
				} else {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), assetWithoutSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), labourWithoutSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), serviceWithoutSumRow));
				}
				report.addFormulaCell(sheet.getRow(21), yearNum, formulaBuild.toString(), Style.CURRENCY);
				
				// replacement
				if (!without) {
					formula = String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12+project.getDuration()*4), assetSumRow);
				} else {
					formula = String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*4), assetWithoutSumRow);
				}
				report.addFormulaCell(sheet.getRow(22), yearNum, formula, Style.CURRENCY);

				// operation
				if (hasBlocks) {
					formulaBuild = new StringBuilder();
					for (ExcelBlockLink blockLink : without ? report.getBlockLinksWithoutProject().values() : report.getBlockLinks().values()) {
						formulaBuild.append("("+blockLink.costCash+"*"+blockLink.qtyPerYear[yearNum-1]);
						if (!blockLink.noCycles) {
							formulaBuild.append("*"+blockLink.cyclesPerYear);
						}
						formulaBuild.append(")+");
					}
					if (formulaBuild.length()>0) {
						formulaBuild.deleteCharAt(formulaBuild.length()-1);
					}
					
					report.addFormulaCell(sheet.getRow(26), yearNum, formulaBuild.toString(), Style.CURRENCY);
				} else {
					report.addNumericCell(sheet.getRow(26), yearNum, 0, Style.CURRENCY);
				}
				
				// general
				String generalSheetName = report.getLink(ExcelLink.PROJECT_GENERAL_SHEET);
				String generalWithoutSheetName = report.getLink(ExcelLink.PROJECT_GENERAL_WITHOUT_SHEET);
				int generalYears=project.isPerYearGeneralCosts()?project.getDuration():1;
				formula = String.format("%s!$%s$%s", 
						without ? generalWithoutSheetName : generalSheetName, 
						project.isPerYearGeneralCosts() ? getColumn(2+yearNum+generalYears*3) : "G", 
						without ? report.getLink(ExcelLink.PROJECT_GENERAL_WITHOUT_TOTAL_ROW) : report.getLink(ExcelLink.PROJECT_GENERAL_TOTAL_ROW)
				);
				report.addFormulaCell(sheet.getRow(27), yearNum, formula, Style.CURRENCY);
				
				// maintenance
				if (!without) {
					formula=String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+14+project.getDuration()*5), assetSumRow);
				} else {
					formula=String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+14+project.getDuration()*5), assetWithoutSumRow);
				}
				report.addFormulaCell(sheet.getRow(28), yearNum, formula, Style.CURRENCY);

				//FINANCING
				if (!without) {
					// wc capital
					formula = String.format("IF(AND(%s>((%s2-1)*12),%s<((%s2-1)*12)),$B$13,0)",
							report.getLink(ExcelLink.PROJECT_WC_PERIOD),
							col,
							report.getLink(ExcelLink.PROJECT_WC_PERIOD),
							getColumn(yearNum+1)
						);
					report.addFormulaCell(sheet.getRow(32), yearNum, formula, Style.CURRENCY);
	
					// wc interest
					formula = String.format("IF(%s33<>0, B13*(%s-%s)*0.01/12*%s,0)",
							col,
							report.getLink(ExcelLink.PROJECT_WC_INTEREST),
							report.getLink(ExcelLink.PROJECT_INFLATION),
							report.getLink(ExcelLink.PROJECT_WC_PERIOD_AVG));
					report.addFormulaCell(sheet.getRow(33), yearNum, formula, Style.CURRENCY);
					
					// primary capital
					formula = String.format("SUM('%s'!%s%d:%s%d)",
							loan1Sheet, getColumn((yearNum-1)*project.getLoan1PaymentsPerYear()+1), loanCapital, 
							getColumn((yearNum-1)*project.getLoan1PaymentsPerYear()+project.getLoan1PaymentsPerYear()), loanCapital);
					report.addFormulaCell(sheet.getRow(34), yearNum, formula, Style.CURRENCY);
					// primary interest
					formula = String.format("SUM('%s'!%s%d:%s%d)",
							loan1Sheet, getColumn((yearNum-1)*project.getLoan1PaymentsPerYear()+1), loanInterest, 
							getColumn((yearNum-1)*project.getLoan1PaymentsPerYear()+project.getLoan1PaymentsPerYear()), loanInterest);
					report.addFormulaCell(sheet.getRow(35), yearNum, formula, Style.CURRENCY);
					// secondary capital
					formula = String.format("SUM('%s'!%s%d:%s%d)",
							loan2Sheet, getColumn((yearNum-1)*project.getLoan2PaymentsPerYear()+1), loanCapital, 
							getColumn((yearNum-1)*project.getLoan2PaymentsPerYear()+project.getLoan2PaymentsPerYear()), loanCapital);
					report.addFormulaCell(sheet.getRow(36), yearNum, formula, Style.CURRENCY);
					// secondary interest
					formula = String.format("SUM('%s'!%s%d:%s%d)",
							loan2Sheet, getColumn((yearNum-1)*project.getLoan2PaymentsPerYear()+1), loanInterest, 
							getColumn((yearNum-1)*project.getLoan2PaymentsPerYear()+project.getLoan2PaymentsPerYear()), loanInterest);
					report.addFormulaCell(sheet.getRow(37), yearNum, formula, Style.CURRENCY);
		
				}
			} else {
				ProjectFinanceData pfd = data.get(yearNum-1);
				
				// income
				if (!without) {
					report.addNumericCell(sheet.getRow(4), yearNum, pfd.getIncSalesExternal(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(5), yearNum, pfd.getIncSalvage(), Style.CURRENCY);
					
					report.addNumericCell(sheet.getRow(9), yearNum, pfd.getCostInvestDonated(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(10), yearNum, pfd.getCostInvestOwn(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(11), yearNum, pfd.getLoanReceived(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(12), yearNum, pfd.getWorkingCapitalReceived(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(13), yearNum, pfd.getIncCapitalDonation(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(14), yearNum, pfd.getIncCapitalOwn(), Style.CURRENCY);
					
					report.addNumericCell(sheet.getRow(21), yearNum, pfd.getCostInvest(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(22), yearNum, pfd.getCostReplace(), Style.CURRENCY);
					
					report.addNumericCell(sheet.getRow(26), yearNum, pfd.getCostOperation()-pfd.getCostOperationInternal(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(27), yearNum, pfd.getCostGeneral()-pfd.getCostGeneralOwn(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(28), yearNum, pfd.getCostMaintenance(), Style.CURRENCY);
					
					report.addNumericCell(sheet.getRow(32), yearNum, pfd.getWorkingCapitalCapital(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(33), yearNum, pfd.getWorkingCapital(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(34), yearNum, pfd.getLoan1capital(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(35), yearNum, pfd.getLoan1interest(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(36), yearNum, pfd.getLoan2capital(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(37), yearNum, pfd.getLoan2interest(), Style.CURRENCY);
				} else {
					report.addNumericCell(sheet.getRow(4), yearNum, pfd.getIncSalesExternalWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(5), yearNum, pfd.getIncSalvageWithout(), Style.CURRENCY);
					
					report.addNumericCell(sheet.getRow(9), yearNum, pfd.getCostInvestDonatedWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(10), yearNum, pfd.getCostInvestOwnWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(11), yearNum, pfd.getCostInvestFinanceWithout(), Style.CURRENCY);
					
					report.addNumericCell(sheet.getRow(21), yearNum, pfd.getCostInvestWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(22), yearNum, pfd.getCostReplaceWithout(), Style.CURRENCY);
					
					report.addNumericCell(sheet.getRow(26), yearNum, pfd.getCostOperationWithout()-pfd.getCostOperationInternalWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(27), yearNum, pfd.getCostGeneralWithout()-pfd.getCostGeneralWithoutOwn(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(28), yearNum, pfd.getCostMaintenanceWithout(), Style.CURRENCY);
				}
			}
			
			// sum rows with formulas
			report.addFormulaCell(sheet.getRow(6), yearNum, String.format("SUM(%s5:%s6)",col,col), Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(15), yearNum, String.format("SUM(%s10:%s15)",col,col), Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(17), yearNum, String.format("%s7+%s16",col,col), Style.CURRENCY);

			report.addFormulaCell(sheet.getRow(23), yearNum, String.format("SUM(%s22:%s23)",col,col), Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(29), yearNum, String.format("SUM(%s27:%s29)",col,col), Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(38), yearNum, String.format("SUM(%s33:%s38)",col,col), Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(40), yearNum, String.format("%s24+%s30+%s39",col,col, col), Style.CURRENCY);
		
			report.addFormulaCell(sheet.getRow(42), yearNum, String.format("%s7-%s24-%s30",col,col,col), Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(43), yearNum, String.format("%s18-%s41",col,col), Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(44), yearNum, yearNum==1 ? "B44" : String.format("%s45+%s44",getColumn(yearNum-1),col), Style.CURRENCY);
			

			// some rows hidden for "without project" scenario
			if (without) {
				for (int i=12;i<=14;i++) {
					sheet.getRow(i).setZeroHeight(true);
				}
				for (int i=31;i<=38;i++) {
					sheet.getRow(i).setZeroHeight(true);
				}
				sheet.getRow(42).setZeroHeight(true);
			}
		}
		
		return sheet;
	}
	
	public Sheet projectProfitability(ExcelWrapper report, Project project, ProjectResult result, FinanceMatrix matrix, ProjectScenario scenario) {
		List<ProjectFinanceData> data = matrix.getYearlyData();
		
		String sheetname;
		String title;
		if (scenario==ProjectScenario.With) {
			if (project.isWithWithout()) {
				sheetname=translate(SheetName.PROJECT_PROFITABILITY_WITH);
				title=translate("project.report.profitability") + " "+translate("project.with");
			} else {
				sheetname=translate(SheetName.PROJECT_PROFITABILITY);
				title=translate("project.report.profitability");
			}
		} else if (scenario==ProjectScenario.Without) {
			sheetname=translate(SheetName.PROJECT_PROFITABILITY_WITHOUT);
			title=translate("project.report.profitability")+ " "+translate("project.without");
		} else { // incremental
			sheetname=translate(SheetName.PROJECT_PROFITABILITY_INCREMENTAL);
			title=translate("project.report.profitability") + " "+translate("project.incremental");
		}
		
		Sheet sheet = report.getWorkbook().createSheet(sheetname);
		sheet.setSelected(true);
		
		// setup header and row titles
		int rowNum=0;
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);
		sheet.setColumnWidth(0, 165*36);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("projectBlock.pattern.years"), Style.LABEL);
		for (short index=1; index <= data.size(); index++) {
			report.addNumericCell(row, index, index);
			sheet.setColumnWidth(index, 90*36);
		}

		rowNum = addRowTitles(new String[] {"project.report.profitability.incomes","project.report.profitability.incomes.sales", "project.report.profitability.incomes.salvage",
				"project.report.profitability.incomes.residual","misc.total"}, rowNum++, sheet, report);
		
		rowNum = addRowTitles(new String[] {"project.report.profitability.costs","project.report.profitability.costs.operation", "project.report.profitability.costs.replacement",
				"project.report.profitability.costs.general", "project.report.profitability.costs.maintenance", "project.report.profitability.costs.investment","misc.total", "project.report.profitability.donations.netBefore", "project.report.profitability.cumulative"}, rowNum++, sheet, report);
		
		rowNum = addRowTitles(new String[] {"project.report.profitability.donations","project.report.profitability.donations.wc","project.report.profitability.donations.investment","misc.total","project.report.profitability.donations.netAfter", "project.report.profitability.cumulative"}, rowNum++, sheet, report);
		
		row = sheet.createRow(25);
		report.addTextCell(row, 0, translate("project.report.profitability.totalMinusInvest.before"), Style.LABEL);
		row = sheet.createRow(26);
		row = sheet.createRow(27);
		report.addTextCell(row, 0, translate("project.report.profitability.totalMinusInvest.after"), Style.LABEL);
		row = sheet.createRow(28);
		
		// real data
		String col;
		StringBuilder formulaBuild;
		String formula;
		for (int yearNum=1;yearNum<=project.getDuration();yearNum++) {
			//yearNum++;
			 col = getColumn(yearNum);
			if (report.isCompleteReport()) {
				// sales income
				formulaBuild = new StringBuilder();
				if (scenario==ProjectScenario.With||scenario==ProjectScenario.Incremental) {
					for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
						formulaBuild.append("("+blockLink.income+"*"+blockLink.qtyPerYear[yearNum-1]);
						if (!blockLink.noCycles) {
							formulaBuild.append("*"+blockLink.cyclesPerYear);
						}
						formulaBuild.append(")+");
					}
					formulaBuild.deleteCharAt(formulaBuild.length()-1);
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append("-(");
				}
				if (scenario==ProjectScenario.Incremental||scenario==ProjectScenario.Without) {
					if (project.getBlocksWithout().size()>0) {
						for (ExcelBlockLink blockLink : report.getBlockLinksWithoutProject().values()) {
							formulaBuild.append("("+blockLink.income+"*"+blockLink.qtyPerYear[yearNum-1]);
							if (!blockLink.noCycles) {
								formulaBuild.append("*"+blockLink.cyclesPerYear);
							}
							formulaBuild.append(")+");
						}
						formulaBuild.deleteCharAt(formulaBuild.length()-1);
					} else {
						formulaBuild.append("0");
					}
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append(")");
				}
				report.addFormulaCell(sheet.getRow(3), yearNum, formulaBuild.toString(), Style.CURRENCY);
				
				// salvage
				String assetSheetName = report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
				String assetWithoutSheetName = report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_SHEET);

				int assetSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW))+project.getAssets().size();
				int labourSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTLABOUR_ROW))+project.getLabours().size();
				int serviceSumRow = Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTSERVICE_ROW))+project.getServices().size();
				Integer assetWithoutSumRow = !project.isWithWithout() ? null : Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_ROW))+(project.getAssetsWithout().size()>0?project.getAssetsWithout().size():1);
				Integer labourWithoutSumRow = !project.isWithWithout() ? null : Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTLABOUR_WITHOUT_ROW))+project.getLaboursWithout().size();
				Integer serviceWithoutSumRow = !project.isWithWithout() ? null : Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTSERVICE_WITHOUT_ROW))+project.getServicesWithout().size();

				if (scenario==ProjectScenario.With) {
					formula=String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12), assetSumRow);
				} else if (scenario==ProjectScenario.Without) {
					formula = String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+12), assetWithoutSumRow);
				} else { // incremental
					formula = String.format("%s!$%s$%d-%s!$%s$%d", assetSheetName, getColumn(yearNum+12), assetSumRow, assetWithoutSheetName, getColumn(yearNum+12), assetWithoutSumRow);
				}
				report.addFormulaCell(sheet.getRow(4), yearNum, formula, Style.CURRENCY);
				
				// residual value
				if (yearNum==project.getDuration()) {
					if (scenario==ProjectScenario.With) {
						formula=String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+14+project.getDuration()*4), assetSumRow);
					} else if (scenario==ProjectScenario.Without) {
						formula=String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+14+project.getDuration()*4), assetWithoutSumRow);
					} else { // incremental
						formula=String.format("%s!$%s$%d-%s!$%s$%d",  assetSheetName, getColumn(yearNum+14+project.getDuration()*4), assetSumRow,  assetWithoutSheetName, getColumn(yearNum+14+project.getDuration()*4), assetWithoutSumRow);
					}
					report.addFormulaCell(sheet.getRow(5), yearNum, formula, Style.CURRENCY);
				} else {
					report.addNumericCell(sheet.getRow(5), yearNum, 0, Style.CURRENCY);
				}
								
				// COSTS
				// operation costs
				formulaBuild = new StringBuilder();
				
				if (scenario==ProjectScenario.With||scenario==ProjectScenario.Incremental) {
					for (ExcelBlockLink blockLink : report.getBlockLinks().values()) {
						formulaBuild.append("("+blockLink.cost+"*"+blockLink.qtyPerYear[yearNum-1]);
						if (!blockLink.noCycles) {
							formulaBuild.append("*"+blockLink.cyclesPerYear);
						}
						formulaBuild.append(")+");
					}
					if (formulaBuild.length()>0) {
						formulaBuild.deleteCharAt(formulaBuild.length()-1);
					}
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append("-(");
				}
				if (scenario==ProjectScenario.Incremental||scenario==ProjectScenario.Without) {
					if (project.getBlocksWithout().size()>0){
						for (ExcelBlockLink blockLink : report.getBlockLinksWithoutProject().values()) {
							formulaBuild.append("("+blockLink.cost+"*"+blockLink.qtyPerYear[yearNum-1]);
							if (!blockLink.noCycles) {
								formulaBuild.append("*"+blockLink.cyclesPerYear);
							}
							formulaBuild.append(")+");
						}
						formulaBuild.deleteCharAt(formulaBuild.length()-1);
					} else {
						formulaBuild.append("0");
					}
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append(")");
				}
				report.addFormulaCell(sheet.getRow(9), yearNum, formulaBuild.toString(), Style.CURRENCY);
				
				// replacement
				if (scenario==ProjectScenario.With) {
					formula = String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12+project.getDuration()*4), assetSumRow);
				} else if (scenario==ProjectScenario.Without) {
					formula = String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*4), assetWithoutSumRow);
				} else { // incremental
					formula=String.format("%s!$%s$%d-%s!$%s$%d", assetSheetName, getColumn(yearNum+12+project.getDuration()*4), assetSumRow, assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*4), assetWithoutSumRow);
				}
				report.addFormulaCell(sheet.getRow(10), yearNum, formula, Style.CURRENCY);

				// general
				String generalSheetName = report.getLink(ExcelLink.PROJECT_GENERAL_SHEET);
				String generalWithoutSheetName = report.getLink(ExcelLink.PROJECT_GENERAL_WITHOUT_SHEET);
				int generalYears=project.isPerYearGeneralCosts()?project.getDuration():1;
				
				String column = project.isPerYearGeneralCosts() ? getColumn(2+yearNum+generalYears) : "E";
				if (scenario==ProjectScenario.With) {
					formula=String.format("%s!$%s$%s", generalSheetName, column, report.getLink(ExcelLink.PROJECT_GENERAL_TOTAL_ROW));
				} else if (scenario==ProjectScenario.Without) {
					formula=String.format("%s!$%s$%s", generalWithoutSheetName, column, report.getLink(ExcelLink.PROJECT_GENERAL_WITHOUT_TOTAL_ROW));
				} else { // incremental
					formula=String.format("%s!$%s$%s-%s!$%s$%s", 
							generalSheetName, column, report.getLink(ExcelLink.PROJECT_GENERAL_TOTAL_ROW),
							generalWithoutSheetName, column, report.getLink(ExcelLink.PROJECT_GENERAL_WITHOUT_TOTAL_ROW));
				}
				report.addFormulaCell(sheet.getRow(11), yearNum, formula, Style.CURRENCY);
				
				// maintenance
				if (scenario==ProjectScenario.With) {
					formula = String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+14+project.getDuration()*5), assetSumRow);
				} else if (scenario==ProjectScenario.Without) {
					formula = String.format("%s!$%s$%d",  assetWithoutSheetName, getColumn(yearNum+14+project.getDuration()*5), assetWithoutSumRow);
				} else { // incremental
					formula= String.format("%s!$%s$%d-%s!$%s$%d", assetSheetName, getColumn(yearNum+14+project.getDuration()*5), assetSumRow, assetWithoutSheetName, getColumn(yearNum+14+project.getDuration()*5), assetWithoutSumRow);
				}
				report.addFormulaCell(sheet.getRow(12), yearNum, formula, Style.CURRENCY);

				// investment
				formulaBuild = new StringBuilder();
				if (scenario==ProjectScenario.With||scenario==ProjectScenario.Incremental) {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()*3), assetSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()*3), labourSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12+project.getDuration()*3), serviceSumRow));
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append("-(");
				}
				if (scenario==ProjectScenario.Incremental||scenario==ProjectScenario.Without) {
					formulaBuild.append(String.format("IF(ISNUMBER(%s!$%s$%d),%s!$%s$%d,0)", assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), assetWithoutSumRow, assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), assetWithoutSumRow));
					formulaBuild.append(String.format("+IF(ISNUMBER(%s!$%s$%d),%s!$%s$%d,0)", assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), labourWithoutSumRow, assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), labourWithoutSumRow));
					formulaBuild.append(String.format("+IF(ISNUMBER(%s!$%s$%d),%s!$%s$%d,0)", assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), serviceWithoutSumRow, assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()*3), serviceWithoutSumRow));
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append(")");
				}
				report.addFormulaCell(sheet.getRow(13), yearNum, formulaBuild.toString(), Style.CURRENCY);
				
				// FINANCIAL COSTS
				// wc donation (first year)
				report.addFormulaCell(sheet.getRow(19), yearNum, String.format("IF(%s2=1,%s,0)",col,report.getLink(ExcelLink.PROJECT_WC_DONATED)), Style.CURRENCY);
			
				// investment donation
				formulaBuild = new StringBuilder();
				if (scenario==ProjectScenario.With||scenario==ProjectScenario.Incremental) {
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()), assetSumRow));
					formulaBuild.append(String.format("%s!$%s$%d+",  assetSheetName, getColumn(yearNum+12+project.getDuration()), labourSumRow));
					formulaBuild.append(String.format("%s!$%s$%d",  assetSheetName, getColumn(yearNum+12+project.getDuration()), serviceSumRow));
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append("-(");
				}
				if (scenario==ProjectScenario.Incremental||scenario==ProjectScenario.Without) {
					formulaBuild.append(String.format("IF(ISNUMBER(%s!$%s$%d),%s!$%s$%d,0)", assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), assetWithoutSumRow, assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), assetWithoutSumRow));
					formulaBuild.append(String.format("+IF(ISNUMBER(%s!$%s$%d),%s!$%s$%d,0)", assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), labourWithoutSumRow, assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), labourWithoutSumRow));
					formulaBuild.append(String.format("+IF(ISNUMBER(%s!$%s$%d),%s!$%s$%d,0)", assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), serviceWithoutSumRow, assetWithoutSheetName, getColumn(yearNum+12+project.getDuration()), serviceWithoutSumRow));
				}
				if (scenario==ProjectScenario.Incremental) {
					formulaBuild.append(")");
				}
				report.addFormulaCell(sheet.getRow(20), yearNum, formulaBuild.toString(), Style.CURRENCY);
			} else {
				ProjectFinanceData pfd = data.get(yearNum-1);
				if (scenario==ProjectScenario.With) {
					// Income
					report.addNumericCell(sheet.getRow(3), yearNum, pfd.getIncSales(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(4), yearNum, pfd.getIncSalvage(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(5), yearNum, pfd.getIncResidual(), Style.CURRENCY);

					// Costs
					report.addNumericCell(sheet.getRow(9), yearNum, pfd.getCostOperation(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(10), yearNum, pfd.getCostReplace(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(11), yearNum, pfd.getCostGeneral(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(12), yearNum, pfd.getCostMaintenance(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(13), yearNum, pfd.getCostInvest(), Style.CURRENCY);
					
					// Donations
					report.addNumericCell(sheet.getRow(19), yearNum, pfd.getIncCapitalDonation(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(20), yearNum, pfd.getCostInvestDonated(), Style.CURRENCY);
				} else if (scenario==ProjectScenario.Without) {
					// Income
					report.addNumericCell(sheet.getRow(3), yearNum, pfd.getIncSalesWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(4), yearNum, pfd.getIncSalvageWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(5), yearNum, pfd.getIncResidualWithout(), Style.CURRENCY);
	
					// Costs
					report.addNumericCell(sheet.getRow(9), yearNum, pfd.getCostOperationWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(10), yearNum, pfd.getCostReplaceWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(11), yearNum, pfd.getCostGeneralWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(12), yearNum, pfd.getCostMaintenanceWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(13), yearNum, pfd.getCostInvestWithout(), Style.CURRENCY);
					
					// Donations
					report.addNumericCell(sheet.getRow(19), yearNum, 0, Style.CURRENCY);
					report.addNumericCell(sheet.getRow(20), yearNum, pfd.getCostInvestDonatedWithout(), Style.CURRENCY);
				} else { // incremental
					// Income
					report.addNumericCell(sheet.getRow(3), yearNum, pfd.getIncSales()-pfd.getIncSalesWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(4), yearNum, pfd.getIncSalvage()-pfd.getIncSalvageWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(5), yearNum, pfd.getIncResidual()-pfd.getIncResidualWithout(), Style.CURRENCY);
	
					// Costs
					report.addNumericCell(sheet.getRow(9), yearNum, pfd.getCostOperation()-pfd.getCostOperationWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(10), yearNum, pfd.getCostReplace()-pfd.getCostReplaceWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(11), yearNum, pfd.getCostGeneral()-pfd.getCostGeneralWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(12), yearNum, pfd.getCostMaintenance()-pfd.getCostMaintenanceWithout(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(13), yearNum, pfd.getCostInvest()-pfd.getCostInvestWithout(), Style.CURRENCY);
					
					// Donations
					report.addNumericCell(sheet.getRow(19), yearNum, pfd.getIncCapitalDonation(), Style.CURRENCY);
					report.addNumericCell(sheet.getRow(20), yearNum, pfd.getCostInvestDonated()-pfd.getCostInvestDonatedWithout(), Style.CURRENCY);
				}
			}
			
			// formulas for both complete report and profitability-only report
			// income total
			report.addFormulaCell(sheet.getRow(6), yearNum, String.format("SUM(%1$s%2$d:%1$s%3$d)", col, 4, 6), Style.CURRENCY);
			// cost total
			report.addFormulaCell(sheet.getRow(14), yearNum, "SUM("+col+"10:"+col+"14)", Style.CURRENCY);
			// net income before donation
			report.addFormulaCell(sheet.getRow(15), yearNum, col+"7-"+col+"15", Style.CURRENCY);
			// cumulative net income before donation
			formula = yearNum==1 ? col+"16" : getColumn(yearNum-1)+"17+"+col+"16";
			report.addFormulaCell(sheet.getRow(16), yearNum, formula, Style.CURRENCY);
			// donation total
			report.addFormulaCell(sheet.getRow(21), yearNum, col+"20+"+col+"21", Style.CURRENCY);
			// net income after donation
			report.addFormulaCell(sheet.getRow(22), yearNum, col+"16+"+col+"22", Style.CURRENCY);
			// cumulative net income after donation
			formula = yearNum==1 ? col+"23" : getColumn(yearNum-1)+"24+"+col+"23";
			report.addFormulaCell(sheet.getRow(23), yearNum, formula, Style.CURRENCY);

			// helper rows for irr/npv flows
			report.addFormulaCell(sheet.getRow(26), yearNum, yearNum==1 ? "B16-A27" : col+"16", Style.CURRENCY);
			report.addFormulaCell(sheet.getRow(28), yearNum, yearNum==1 ? "B23-A29" : col+"23", Style.CURRENCY);
		}
		
		// helper rows for npv/irr
		report.addFormulaCell(sheet.getRow(26), 0, "-1*B14", Style.CURRENCY);
		report.addFormulaCell(sheet.getRow(28), 0, "A27+B21", Style.CURRENCY);
		
		// Indicators
		int tind = 4;
		row = sheet.createRow(30);
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, data.size()));
		report.addTextCell(row, 0, translate("project.report.profitability.indicators"), Style.H2);

		row = sheet.createRow(31);
		report.addTextCell(row, 1, translate("project.report.profitability.totInvestAllCosts"), Style.H2);
		report.addTextCell(row, tind, translate("project.report.profitability.totInvestApplicant"), Style.H2);

		row = sheet.createRow(32);
		report.addTextCell(row, 0, translate("project.irr.long"), Style.H2);
		report.addFormulaCell(row, 1, String.format("IF(ISNUMBER(IRR(A27:%1$s27)),IRR(A27:%1$s27),\""+translate("misc.undefined")+"\")", getColumn(data.size())),Style.PERCENT);
		report.addFormulaCell(row, tind, String.format("IF(ISNUMBER(IRR(A29:%1$s29)),IRR(A29:%1$s29),\""+translate("misc.undefined")+"\")", getColumn(data.size())),Style.PERCENT);
		
		row = sheet.createRow(33);
		report.addTextCell(row, 0, translate("project.npv.long"), Style.H2);
		if (report.isCompleteReport()) {
			report.addFormulaCell(row, 1, String.format("IF(ISNUMBER(NPV((%1$s/100),B27:%2$s27)),NPV((%1$s/100),B27:%2$s27)+A27,\""+translate("misc.undefined")+"\")", report.getLink(ExcelLink.PROJECT_DISCOUNT_RATE), getColumn(data.size())), Style.CURRENCY);
			report.addFormulaCell(row, tind, String.format("IF(ISNUMBER(NPV((%1$s/100),B29:%2$s29)),NPV((%1$s/100),B29:%2$s29)+A29,\""+translate("misc.undefined")+"\")", report.getLink(ExcelLink.PROJECT_DISCOUNT_RATE), getColumn(data.size())), Style.CURRENCY);
		} else {
			report.addFormulaCell(row, 1, String.format("IF(ISNUMBER(NPV(%1$f,B27:%2$s27)),NPV(%1$f,B27:%2$s27)+A27,\""+translate("misc.undefined")+"\")", rivConfig.getSetting().getDiscountRate()/100, getColumn(data.size())), Style.CURRENCY);
			report.addFormulaCell(row, tind, String.format("IF(ISNUMBER(NPV(%1$f,B29:%2$s29)),NPV(%1$f,B29:%2$s29)+A29,\""+translate("misc.undefined")+"\")", rivConfig.getSetting().getDiscountRate()/100, getColumn(data.size())), Style.CURRENCY);
		}
		
		row = sheet.createRow(34);
		report.addTextCell(row, 0, translate("project.report.profitability.lastNeg"), Style.H2);
		report.addFormulaCell(row, 1, String.format("MATCH(2,1/MMULT(1,-(B17:%s17<0)))", getColumn(data.size())), Style.CURRENCY);
		report.addFormulaCell(row, tind, String.format("MATCH(2,1/MMULT(1,-(B24:%s24<0)))", getColumn(data.size())), Style.CURRENCY);
		
		row = sheet.createRow(35);
		report.addTextCell(row, 0, translate("project.report.profitability.payback"), Style.H2);
		report.addFormulaCell(row, 1,"ROUND(B35+(ABS(INDIRECT(ADDRESS(17,B35+1)))/INDIRECT(ADDRESS(16,B35+2))),2)");
		report.addFormulaCell(row, tind, "ROUND(E35+(ABS(INDIRECT(ADDRESS(24,E35+1)))/INDIRECT(ADDRESS(23,E35+2))),2)");
		
		rowNum=37;
		row = sheet.createRow(rowNum);
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, data.size()));
		report.addTextCell(row, 0, translate("project.irr.desc") + " / " + translate("project.npv.desc"), Style.LABEL);
		row = sheet.createRow(++rowNum);
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, data.size()));
		report.addTextCell(row, 0, translate("project.irr.help"));

		row = sheet.createRow(++rowNum);
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, data.size()));
		report.addTextCell(row, 0, translate("project.irrWithDonation.desc") + " / " + translate("project.irrWithDonation.desc"), Style.LABEL);
		row = sheet.createRow(++rowNum);
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, data.size()));
		report.addTextCell(row, 0, translate("project.irrApplicant.help"));
		
		rowNum++;
		row = sheet.createRow(++rowNum);
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, data.size()));
		report.addTextCell(row, 0, translate("project.report.profitability.footnote"));
		
		// hidden rows
		sheet.getRow(16).setZeroHeight(true);
		sheet.getRow(23).setZeroHeight(true);
		sheet.getRow(34).setZeroHeight(true);
		
		return sheet;
	}

	public Sheet projectWorkingCapital(ExcelWrapper report, Project project, ProjectResult result, boolean without, int decimals) {

		ProjectMonthsInYear[] setOfMonthsInYear =  ProjectMonthsInYear.getProjectPerMonths(project, without, decimals);
		String sheetname; String titlePart1; String title;
		int numBlocks = without ? project.getBlocksWithout().size() : project.getBlocks().size();
		titlePart1 = translate("project.report.workingcapital");
		
		if (!without) {
			if (project.isWithWithout()) {
				sheetname=translate(SheetName.PROJECT_WORKING_CAPITAL_WITH);
				title=titlePart1 + " " + translate("project.with");
			} else {
				sheetname=translate(SheetName.PROJECT_WORKING_CAPITAL);
				title=titlePart1;
			}
		} else {
			sheetname=translate(SheetName.PROJECT_WORKING_CAPITAL_WITHOUT);
			title=titlePart1 + " " + translate("project.without");
		}
		Sheet sheet = report.getWorkbook().createSheet(sheetname);
		((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		
		sheet.setSelected(true);
		int rowNum;
		
		String chronSheet = "'"+translate(SheetName.PROJECT_CHRONOLOGY)+"'";
		
		// create rows in advance
		for (int i=0; i<=13+project.getBlocks().size()*2+project.getBlocksWithout().size()*2;i++) {
			sheet.createRow(i);
		}
		
		// 1st column : labels
		report.addTextCell(sheet.getRow(0), 0, title, Style.TITLE);
		mergeCells(sheet, 0, 0, 0, 12);
		
		report.addTextCell(sheet.getRow(3), 0, translate("project.report.cashFlowFirst.operIncomes"), Style.H2);
		rowNum=4;
		for (BlockBase block : without ? project.getBlocksWithout() : project.getBlocks()) {
			report.addTextCell(sheet.getRow(rowNum++), 0, block.getDescription());
		}
		
		rowNum++;
		report.addTextCell(sheet.getRow(rowNum++), 0, translate("project.report.cashFlowFirst.operCosts"), Style.H2);
		for (BlockBase block : without ? project.getBlocksWithout() : project.getBlocks()) {
			report.addTextCell(sheet.getRow(rowNum++), 0, block.getDescription());
		}
		
		rowNum++;
		
		report.addTextCell(sheet.getRow(rowNum++), 0, translate("project.report.cashFlowFirst.otherCosts"), Style.H2);
		report.addTextCell(sheet.getRow(rowNum++), 0, translate("project.report.cashFlowFirst.maint"));
		report.addTextCell(sheet.getRow(rowNum++), 0, translate("project.report.cashFlowFirst.general"));
		report.addTextCell(sheet.getRow(rowNum++), 0, translate("misc.subtotal"), Style.LABEL);
		report.addTextCell(sheet.getRow(rowNum++), 0, translate("project.report.cashFlowFirst.monthFlow"), Style.LABEL);
		report.addTextCell(sheet.getRow(rowNum++), 0, translate("project.report.cashFlowFirst.cumFlow"), Style.LABEL);
		
		// year and month labels
		Row rowYears = sheet.getRow(1);
		Row rowMonths = sheet.getRow(2);
		report.addTextCell(rowYears, 0, translate("units.year"), Style.LABEL);
		int rep=0; int cellNum=1;
		while (rep==0 || rep<project.getDuration()) {
			// year label
				report.addTextCell(rowYears, cellNum, String.valueOf(rep+1), Style.LABEL);
				mergeCells(sheet, rowYears.getRowNum(), cellNum, rowYears.getRowNum(), cellNum+11);
//			}
			for (String month : months(messageSource, project)) {
				// month label
				report.addTextCell(rowMonths, cellNum++, month, Style.LABEL);
			}
			rep++;
		}
//		if (firstYearOnly) {
//			report.addTextCell(rowMonths, cellNum++, translate("misc.total"), Style.LABEL);
//		}

		Row row;
		if (report.isCompleteReport()) {
			rowNum=4;
			int years = project.getDuration();
			
			// operation incomes
			for (BlockBase block : without ? project.getBlocksWithout() : project.getBlocks()) {
				row = sheet.getRow(rowNum++);
				ExcelBlockLink blockLink = without ? report.getBlockLinksWithoutProject().get(block.getBlockId()) : report.getBlockLinks().get(block.getBlockId());
				for (int year=0;year<years;year++) {
					for (int month=0;month<12;month++) {
						String formula;
						if (blockLink.noCycles) {
							formula=String.format("IF(COUNTIF(%s!$B$%d:$Y$%d,\"#\")>0,%s/COUNTIF(%s!$B$%d:$Y$%d,\"#\")"+
									"*COUNTIF(%s!$%s$%d:$%s$%d,\"#\")*%s,0)", 
								chronSheet, blockLink.paymentRow, blockLink.paymentRow, 
								project.getIncomeGen() ? blockLink.incomeCashNoTransport : blockLink.incomeCash, 
								chronSheet, blockLink.paymentRow, blockLink.paymentRow,
								chronSheet,getColumn(month*2+1), blockLink.paymentRow,
								getColumn(month*2+2),blockLink.paymentRow,
								blockLink.qtyPerYear[year]
								);
						} else {
							formula=String.format("IF(COUNTIF(%s!$B$%d:$Y$%d,\"#\")>0,%s/COUNTIF(%s!$B$%d:$Y$%d,\"#\")"+
									"*COUNTIF(%s!$%s$%d:$%s$%d,\"#\")*%s*%s,0)", 
								chronSheet, blockLink.paymentRow, blockLink.paymentRow, 
								project.getIncomeGen() ? blockLink.incomeCashNoTransport : blockLink.incomeCash, 
								chronSheet, blockLink.paymentRow, blockLink.paymentRow,
								chronSheet,getColumn(month*2+1), blockLink.paymentRow,
								getColumn(month*2+2),blockLink.paymentRow,
								blockLink.qtyPerYear[year],
								blockLink.cyclesPerYear
								);
						}
						report.addFormulaCell(row, year*12+month+1, formula, Style.CURRENCY);
					}
				}
//				if (firstYearOnly) {
//					report.addFormulaCell(row, 13, writeFormula("SUM(BX:MX)", rowNum), Style.CURRENCY);
//				}
			}
			
			// operation costs
			rowNum++;
			for (BlockBase block : without ? project.getBlocksWithout() : project.getBlocks()) {
				ExcelBlockLink blockLink = without ? report.getBlockLinksWithoutProject().get(block.getBlockId()) : report.getBlockLinks().get(block.getBlockId());
				row = sheet.getRow(++rowNum);
				
				for (int year=0;year<years;year++) {
					for (int month=0;month<12;month++) {
						String formula;
						if (blockLink.noCycles) {
							formula=String.format("%s/COUNTIF(%s!$B$%d:$Y$%d,\"#\")*COUNTIF(%s!$%s$%d:$%s$%d,\"#\")*%s",
									blockLink.costCash,
									chronSheet,blockLink.productionRow,blockLink.productionRow,
									chronSheet, getColumn(month*2+1), blockLink.productionRow,
									getColumn(month*2+2), blockLink.productionRow,
									blockLink.qtyPerYear[year]
								);
							if (project.getIncomeGen()) {
								formula = formula + String.format("+IF(COUNTIF(%s!$B$%d:$Y$%d,\"#\")>0,%s/COUNTIF(%s!$B$%d:$Y$%d,\"#\")"+
										"*COUNTIF(%s!$%s$%d:$%s$%d,\"#\")*%s,0)", 
										chronSheet, blockLink.harvestRow, blockLink.harvestRow, 
										blockLink.incomeCashOnlyTransport, 
										chronSheet, blockLink.harvestRow, blockLink.harvestRow,
										chronSheet,getColumn(month*2+1), blockLink.harvestRow,
										getColumn(month*2+2),blockLink.harvestRow,
										blockLink.qtyPerYear[year]
										);
							}
						} else {
							formula=String.format("%s/COUNTIF(%s!$B$%d:$Y$%d,\"#\")*COUNTIF(%s!$%s$%d:$%s$%d,\"#\")*%s*%s",
									blockLink.costCash,
									chronSheet,blockLink.productionRow,blockLink.productionRow,
									chronSheet, getColumn(month*2+1), blockLink.productionRow,
									getColumn(month*2+2), blockLink.productionRow,
									blockLink.qtyPerYear[year],
									blockLink.cyclesPerYear
								);
							if (project.getIncomeGen()) {
								formula = formula + String.format("+IF(COUNTIF(%s!$B$%d:$Y$%d,\"#\")>0,%s/COUNTIF(%s!$B$%d:$Y$%d,\"#\")"+
										"*COUNTIF(%s!$%s$%d:$%s$%d,\"#\")*%s*%s,0)", 
										chronSheet, blockLink.harvestRow, blockLink.harvestRow, 
										blockLink.incomeCashOnlyTransport, 
										chronSheet, blockLink.harvestRow, blockLink.harvestRow,
										chronSheet,getColumn(month*2+1), blockLink.harvestRow,
										getColumn(month*2+2),blockLink.harvestRow,
										blockLink.qtyPerYear[year],
										blockLink.cyclesPerYear
								);
							}
						}
						report.addFormulaCell(row, year*12+month+1, formula, Style.CURRENCY);
					}
				}
//				if (firstYearOnly) {
//					report.addFormulaCell(row, 13, writeFormula("SUM(BX:MX)", rowNum+1), Style.CURRENCY);
//				}
			}
			
			// other costs
			// maintenance
			rowNum=8+numBlocks*2;
			row = sheet.getRow(rowNum++);
			int sumRow = without ? Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_ROW))+project.getAssetsWithout().size() : Integer.parseInt(report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_ROW))+project.getAssets().size();
			String sheetName = without ? report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_WITHOUT_SHEET) : report.getLink(ExcelLink.PROJECT_INVEST_FIRSTASSET_SHEET);
			
			for (int year=0;year<years;year++) {
				StringBuilder formula = new StringBuilder();
				formula.append(String.format("%s!$%s$%d",  sheetName, getColumn(year+15+project.getDuration()*5), sumRow));
	
				if (formula.length()>0) { 
					formula.insert(0, "ROUND((");
					formula.append(")/12,"+decimals+")");
				}
				addCashFlowOtherRow(report, row, year*12, formula.toString());
			}
			
			// general
			row = sheet.getRow(rowNum++);
			String generalSheetName = report.getLink(ExcelLink.PROJECT_GENERAL_SHEET);
			String generalWithoutSheetName = report.getLink(ExcelLink.PROJECT_GENERAL_WITHOUT_SHEET);
			int generalYears=project.isPerYearGeneralCosts()?project.getDuration():1;
			
			for (int year=0;year<years;year++) {
				String formulax;
				if (!without) {
					formulax=String.format("ROUND(%s!$%s$%s/12,%d)", generalSheetName, getColumn(3+generalYears*3), report.getLink(ExcelLink.PROJECT_GENERAL_TOTAL_ROW), decimals);
				} else {
					formulax=String.format("ROUND(%s!$%s$%s/12,%d)", generalWithoutSheetName, getColumn(3+generalYears*3), report.getLink(ExcelLink.PROJECT_GENERAL_WITHOUT_TOTAL_ROW), decimals);
				}
				addCashFlowOtherRow(report, row, year*12, formulax);
			}
			
			rowNum=10+numBlocks*2;
		} else {
			for (ProjectMonthsInYear ppm : setOfMonthsInYear) {
//				if (!firstYearOnly || ppm.getYear()==1) {
					// incomes
					rowNum=4;
					rowNum=addMonthFlowArrays(report, sheet, rowNum, ppm.getIncomes(), ppm.getYear());
					
					// operation costs
					rowNum=6+numBlocks;
					rowNum=addMonthFlowArrays(report, sheet, rowNum, ppm.getCosts(), ppm.getYear());
					
					
					// other costs
					rowNum+=2;
					row = sheet.getRow(rowNum++);
					addCashFlowOtherRow(report, row, (ppm.getYear()-1)*12+1, ppm.getMaintenanceCost());
					row = sheet.getRow(rowNum++);
					addCashFlowOtherRow(report, row, (ppm.getYear()-1)*12+1, ppm.getGeneralCost());
//				}
			}
		}
		
		// both complete and stand-alone
		// subtotal
		int subtotalColumns = project.getDuration()*12;
		row = sheet.getRow(rowNum++);
		for (int i=1;i<=subtotalColumns; i++) {
			report.addFormulaCell(row, i, String.format("SUM(%s%d:%s%d)",getColumn(i), rowNum-2, getColumn(i), rowNum-1), Style.CURRENCY);
		}
		
		// monthly flow
		int flowColumns = project.getDuration()*12;
		row = sheet.getRow(rowNum++);
		for (short i=1;i<=flowColumns;i++) {
			report.addFormulaCell(row, i, String.format("SUM(%s5:%s%d)-SUM(%s%d:%s%d)-%s%d",
					getColumn(i),getColumn(i),4+numBlocks,
					getColumn(i),7+numBlocks,getColumn(i),6+numBlocks*2,
					getColumn(i),rowNum-1
					),Style.CURRENCY);
		}
		
		// cumulative flow
		flowColumns = project.getDuration()*12;
		row = sheet.getRow(rowNum++);
		report.addFormulaCell(row, 1, String.format("B%d", rowNum-1), Style.CURRENCY);
		for (int i=2;i<=flowColumns;i++) {
			report.addFormulaCell(row, i, String.format("%s%d+%s%d", getColumn(i), rowNum-1, getColumn(i-1), rowNum), Style.CURRENCY);	
		}
		
		// 
//		if (!firstYearOnly) {
		if (!without) {
			String formula1; String formula2; String formula3; String col;
			int rowCumulative = rowNum; 
			int rowNumWf1 = ++rowNum+1;
			Row wf1 = sheet.createRow(rowNum++);
			report.addTextCell(wf1, 0, "Weight factor 1");
			
			Row wf2 = sheet.createRow(rowNum++);
			report.addTextCell(wf2, 0, "Weight factor 2");
			
			Row wf3 = sheet.createRow(rowNum++);
			report.addTextCell(wf3, 0, "Weight factor 3");
			
			String wcPeriod;
//			String wcPeriodAvg;
			String wcRequired;
			
			if (report.isCompleteReport()) {
				wcPeriod = report.getLink(ExcelLink.PROJECT_WC_PERIOD);
				wcRequired = report.getLink(ExcelLink.PROJECT_WC_REQUIRED);
			} else {
				rowNum++;
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate("project.amtRequired"));
				String formula = String.format("IF(MIN(B%d:%s%d)<0,MIN(B%d:%s%d)*-1,0)", 
						rowCumulative, getColumn(flowColumns), rowCumulative,
						rowCumulative, getColumn(flowColumns), rowCumulative);
				report.addFormulaCell(row, 1, formula, Style.CURRENCY);
				wcRequired = "$B$"+rowNum;
				
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate("project.period"));
				formula = String.format("IF(%s>0, MATCH(2,1/MMULT(1,-(B%d:%s%d<0)))+2, 0)", 
						wcRequired, rowCumulative, getColumn(flowColumns), rowCumulative);
				report.addFormulaCell(row, 1, formula);
				wcPeriod = "$B$"+rowNum;
				
				row = sheet.createRow(rowNum++);
				report.addTextCell(row, 0, translate("project.periodAvg"));
				formula = String.format("ROUND(AVERAGE(INDIRECT(CONCATENATE(\"B%d:\"&ADDRESS(%d,B%d+1)))),2)", 
						rowCumulative+4, rowCumulative+4,rowNum-1);
				report.addFormulaCell(row, 1, formula);
//				wcPeriodAvg = "$B$"+rowNum;
			}
			
			for (int i=1;i<=flowColumns;i++) {
				col = getColumn(i);
				formula1 = String.format("-IF(%s%d<0,%s%d/%s,0)", col, rowCumulative, col, rowCumulative, wcRequired);
				formula2 = i==1 ? String.format("IF(COLUMN()-1<=%s,%s%d,0)", wcPeriod, col, rowNumWf1) : String.format("IF(COLUMN()-1<=%s,MAX(%s%d,%s%d),0)", wcPeriod, getColumn(i-1), rowNumWf1+1, col, rowNumWf1);
				formula3 = String.format("ROUND((%s-COLUMN()+2)*%s%d,2)", wcPeriod, col, rowNumWf1+1);
						
				report.addFormulaCell(wf1, i, formula1, Style.PERCENT);
				report.addFormulaCell(wf2, i, formula2, Style.PERCENT);
				report.addFormulaCell(wf3, i, formula3);
			}
			
			int extra = without ? project.getBlocksWithout().size()*2 : project.getBlocks().size()*2;
			int firstWeightRow = 14+extra;
			for (int i=firstWeightRow;i<=firstWeightRow+2;i++) {
				sheet.getRow(i).setZeroHeight(true);
			}
		}
		
		autoSizeColumns(sheet, project.getDuration()*12 + 3);
		
		return sheet;
	}
	
	private int addMonthFlowArrays(ExcelWrapper report, Sheet sheet, int rowNum, List<ProjectMonthFlow> flows,int year) {//, boolean firstYearOnly) {
		for (ProjectMonthFlow pmf : flows) {
				Row row = sheet.getRow(rowNum);
				int cellNum = (year-1)*12+1;
				
				for (double flowData : pmf.getFlowData()) {
					report.addNumericCell(row, cellNum++, flowData, Style.CURRENCY);
				}
				
//				if (firstYearOnly) {
//					report.addFormulaCell(row, 13, writeFormula("SUM(BX:MX)", rowNum+1), Style.CURRENCY);
//				}
				
				rowNum++;
		}
		return rowNum;
	}

	private void addCashFlowOtherRow (ExcelWrapper report, Row row, int cellNum, double value) {//, boolean firstYearOnly) {
		double[] values = new double[12];
		for (int i = 0; i < 12; i++) {
			values[i] = value;
		}
		addCashFlowOtherRow(report, row, cellNum, values);
	}
	private void addCashFlowOtherRow (ExcelWrapper report, Row row,  int cellNum, double[] values) {//, boolean firstYearOnly) {
		for (double value : values) {
			report.addNumericCell(row, cellNum++, value, Style.CURRENCY);

		}
//		if (firstYearOnly) {
//			report.addFormulaCell(row, cellNum, String.format("SUM(B%d:M%d)",row.getRowNum()+1, row.getRowNum()+1), Style.CURRENCY);
//		}
	}
	
	private void addCashFlowOtherRow(ExcelWrapper report, Row row, int cellNum, String formula) {//, boolean firstYearOnly) {
		for (int i=1;i<=12;i++) {
			report.addFormulaCell(row,  cellNum+i, formula, Style.CURRENCY);
		}
//		if (firstYearOnly) {
//			report.addFormulaCell(row, 13, String.format("SUM(B%d:M%d)",row.getRowNum()+1, row.getRowNum()+1), Style.CURRENCY);
//		}
	}
	

	
	private Map<Integer, String> lengthUnits() {
		return rivConfig.getLengthUnits();
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
	
	private void setColumnWidth(Sheet sheet, int col, int size) {
		sheet.setColumnWidth(col, size*36);
	}

	public Sheet projectRecommendation(ExcelWrapper report, Project project) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_RECOMMENDATION));

		sheet.setSelected(true);
		int rowNum=0;
		short cellNum = 0;
		//short step = 7;
		setColumnWidth(sheet, 0, 700);
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.report.recommendation"), Style.TITLE);	

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, rivConfig.getRecommendationTypes().get(project.getReccCode()));
		
		rowNum++;
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("project.recommendation.date"), Style.LABEL);
		row = sheet.createRow(rowNum++);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (project.getReccDate() != null)
			report.addTextCell(row, cellNum, sdf.format(project.getReccDate()));

		rowNum++;	
		addBigTextCell(report, sheet, rowNum++, translate("project.recc.justification"), project.getReccDesc(), null, false);
		
		return sheet;
		
	}

	public Sheet projectParameters(ExcelWrapper report, Project project, ProjectResult result) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROJECT_PARAMETERS));
		
		String cashFlowMonthsSheet = project.isWithWithout() 
				? translate(SheetName.PROJECT_WORKING_CAPITAL_WITH)
				: translate(SheetName.PROJECT_WORKING_CAPITAL);

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
			report.addLink(ExcelLink.PROJECT_INFLATION, "'"+sheet.getSheetName()+"'!$B$4");
		}
		
		row = sheet.createRow(rowNum++);
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_LOAN2_AMOUNT, "'"+sheet.getSheetName()+"'!$B$16");
			
			String loan1amt = report.getLink(ExcelLink.PROJECT_INVEST_FINANCED)+"-"+report.getLink(ExcelLink.PROJECT_LOAN2_AMOUNT);
			rowNum = addLoans(report, sheet, rowNum, translate("project.loan1"), 0.0, loan1amt, project.getLoan1Interest(), 
					project.getLoan1Duration(),	project.getLoan1GraceCapital(), project.getLoan1GraceInterest(), project.getLoan1PaymentsPerYear());
			report.addLink(ExcelLink.PROJECT_LOAN1_AMOUNT, "'"+sheet.getSheetName()+"'!$B$7");
			report.addLink(ExcelLink.PROJECT_LOAN1_RATE, "'"+sheet.getSheetName()+"'!$D$8");
			report.addLink(ExcelLink.PROJECT_LOAN1_DURATION, "'"+sheet.getSheetName()+"'!$D$9");
			report.addLink(ExcelLink.PROJECT_LOAN1_GRACE_CAPITAL, "'"+sheet.getSheetName()+"'!$D$10");
			report.addLink(ExcelLink.PROJECT_LOAN1_GRACE_INTEREST, "'"+sheet.getSheetName()+"'!$D$11");
			report.addLink(ExcelLink.PROJECT_LOAN1_PAYMENTS_PER_YEAR, "'"+sheet.getSheetName()+"'!$B$12");
		} else {
			rowNum = addLoans(report, sheet, rowNum, translate("project.loan1"), project.getInvestmentTotal()-project.getLoan2Amt(), null, project.getLoan1Interest(), 
				project.getLoan1Duration(),	project.getLoan1GraceCapital(), project.getLoan1GraceInterest(), project.getLoan1PaymentsPerYear());
		}
		
		row = sheet.createRow(rowNum++);

		row = sheet.createRow(rowNum++);
		rowNum = addLoans(report, sheet, rowNum, translate("project.loan2"), project.getLoan2Amt(), null,  project.getLoan2Interest(), 
				project.getLoan2Duration(), project.getLoan2GraceCapital(), project.getLoan2GraceInterest(), project.getLoan2PaymentsPerYear());
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_LOAN2_RATE, "'"+sheet.getSheetName()+"'!$D$17");
			report.addLink(ExcelLink.PROJECT_LOAN2_DURATION, "'"+sheet.getSheetName()+"'!$D$18");
			report.addLink(ExcelLink.PROJECT_LOAN2_GRACE_CAPITAL, "'"+sheet.getSheetName()+"'!$D$19");
			report.addLink(ExcelLink.PROJECT_LOAN2_GRACE_INTEREST, "'"+sheet.getSheetName()+"'!$D$20");
			report.addLink(ExcelLink.PROJECT_LOAN2_PAYMENTS_PER_YEAR, "'"+sheet.getSheetName()+"'!$B$21");
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan2InitPeriod"));
		report.addNumericCell(row, 1, project.getLoan2InitPeriod());
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_LOAN2_YEAR, "'"+sheet.getSheetName()+"'!$B$"+rowNum);			
		}
		
		// working capital
		rowNum++;	row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.workingCapital"), Style.H2);

		int firstYearCumulativeRow = 13+project.getBlocks().size()*2;
		String range = 
				String.format("'%s'!$B$%d:$%s$%d",
						cashFlowMonthsSheet,
						firstYearCumulativeRow, 
						getColumn(12*project.getDuration()), firstYearCumulativeRow);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.amtRequired"));
		if (report.isCompleteReport()) {		
			String formula = 
				"IF(MIN("+range+")<0,MIN("+range+")*-1,0)";	
			report.addFormulaCell(row, 1, formula, Style.CURRENCY);

			if (report.isCompleteReport()) {
				report.addLink(ExcelLink.PROJECT_WC_REQUIRED, "'"+sheet.getSheetName()+"'!$B$"+rowNum);
			}
		} else {
			report.addNumericCell(row, 1, result.getWorkingCapital(), Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.period"));
		if (report.isCompleteReport()) {
			String formula = 
					"IF(B25>0, MATCH(2,1/MMULT(1,-("+range+"<0)))+2, 0)";	
			report.addFormulaCell(row, 1, formula);
			report.addLink(ExcelLink.PROJECT_WC_PERIOD, "'"+sheet.getSheetName()+"'!$B$"+rowNum);
		} else {
			report.addNumericCell(row, 1, result.getWcPeriod());
		}
		report.addTextCell(row, 2, translate("units.months"));
		
		// average loan duration
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.periodAvg"));
		if (report.isCompleteReport()) {
			String formula = 
					String.format("ROUND(AVERAGE(INDIRECT(CONCATENATE(\"'%s'!$B$%d:\"&ADDRESS(%d,B26+1)))),2)",
							cashFlowMonthsSheet, firstYearCumulativeRow+4,
									firstYearCumulativeRow+4
						);	
			report.addFormulaCell(row, 1, formula);
			report.addLink(ExcelLink.PROJECT_WC_PERIOD_AVG, "'"+sheet.getSheetName()+"'!$B$"+rowNum);
		} else {
			report.addNumericCell(row, 1, result.getWcPeriodAvg());
		}
		report.addTextCell(row, 2, translate("units.months"));
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.capitalInterest"));
		report.addNumericCell(row, 1, project.getCapitalInterest(), Style.CURRENCY);
		report.addTextCell(row, 2, "%");
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_WC_INTEREST, "'"+sheet.getSheetName()+"'!$B$"+rowNum);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.capitalDonate"));
		report.addNumericCell(row, 1, project.getCapitalDonate(), Style.CURRENCY);
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_WC_DONATED, "'"+sheet.getSheetName()+"'!$B$"+rowNum);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.capitalOwn"));
		report.addNumericCell(row, 1, project.getCapitalOwn(), Style.CURRENCY);
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_WC_OWN, "'"+sheet.getSheetName()+"'!$B$"+rowNum);
		}

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.amtFinanced"));
		report.addFormulaCell(row, 1, "B25-B29-B30", Style.CURRENCY);
		if (report.isCompleteReport()) {
			report.addLink(ExcelLink.PROJECT_WC_FINANCED, "'"+sheet.getSheetName()+"'!$B$"+rowNum);
		}
		return sheet;
	}
	
	private int addLoans(ExcelWrapper report, Sheet sheet, int rowNum, String title, double amount, String amountFormula, double interest, 
			int duration, int graceCapital, int graceInterest, int periodsPerYear) {
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.H2);

		String years = translate("units.years");
		String periods = translate("project.loan.periods");
		String perYear = translate("units.perYear");
		
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
		report.addTextCell(row, 2, perYear);
		report.addFormulaCell(row,  3,  "B"+rowNum+"/B"+(rowNum+4), Style.CURRENCYUSD);
		report.addTextCell(row, 4, translate("project.loan.perPeriod"));
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.duration"));
		report.addNumericCell(row, 1, duration);
		report.addTextCell(row, 2, years);
		report.addFormulaCell(row,  3,  "B"+rowNum+"*B"+(rowNum+3));
		report.addTextCell(row, 4, periods);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.graceCapital"));
		report.addNumericCell(row, 1, graceCapital);
		report.addTextCell(row, 2, years);
		report.addFormulaCell(row,  3,  "B"+rowNum+"*B"+(rowNum+2));
		report.addTextCell(row, 4, periods);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.graceInterest"));
		report.addNumericCell(row, 1, graceInterest);
		report.addTextCell(row, 2, years);
		report.addFormulaCell(row,  3,  "B"+rowNum+"*B"+(rowNum+1));
		report.addTextCell(row, 4, periods);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("project.loan.paymentsPerYear"));
		report.addNumericCell(row, 1, periodsPerYear);
		
		return rowNum;
	}
	
	public Sheet profileSummary(ExcelWrapper report, Profile profile) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROFILE_SUMMARY));

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
		report.addTextCell(row, 0, translate("profile.user"), Style.H2);
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
		rowNum=addBigTextCell(report, sheet, rowNum++, translate("profile.benefDesc"), profile.getBenefDesc(), null, false);
		

		
	//	Objective and Major Activities
		rowNum=addBigTextCell(report, sheet, rowNum++, translate("profile.objective"),profile.getProjDesc(), null, false);
		
	//	Anticipated market
		String title;
		if (profile.getIncomeGen()) {
			title = "profile.market";
		} else {
			title = "profile.demand";
		}
		rowNum=addBigTextCell(report, sheet, rowNum++, translate(title), profile.getMarket(), null, false);
		
	//	Environmental impact		
		rowNum=addBigTextCell(report, sheet, rowNum++, translate("profile.envirImpact"), profile.getEnviroImpact(), null, false);
		
		return sheet;
		
	}
	
	public void profileInvest(ExcelWrapper report, Profile profile, boolean without, boolean template) {
		String title = without ? translate("profile.report.investDetail") + " " + translate("project.without")
				: profile.getWithWithout() ? translate("profile.report.investDetail") + " " + translate("project.with")
				: translate("profile.report.investDetail");
		SheetName sheetname = profile.getWithWithout() ? without ? SheetName.PROFILE_INVEST_WITHOUT : SheetName.PROFILE_INVEST_WITH : SheetName.PROFILE_INVEST;
		
		Sheet sheet = report.getWorkbook().createSheet(translate(sheetname));
		((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		sheet.setSelected(!without);
				
		int rowNum = 0;		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profileGoods"), Style.H2);
		
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
		.addColumn(XlsColumnType.FORMULA, "IF(ISNUMBER(((DX-IX)*CX)/HX),((DX-IX)*CX)/HX,0)", true);
		rowNum = table.writeTable(sheet, rowNum++, template ? null : without ? profile.getGlsGoodsWithout() : profile.getGlsGoods(), true);
		if (report.isCompleteReport()) {
			if (without) {
				report.addLink(ExcelLink.PROFILE_INVEST_WITHOUT_GOODS_RESERVE, "'"+sheet.getSheetName()+"'!J"+rowNum);
			} else {
				report.addLink(ExcelLink.PROFILE_INVEST_GOODS_RESERVE, "'"+sheet.getSheetName()+"'!J"+rowNum);
			}
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
		rowNum = table.writeTable(sheet, rowNum++, template ? null : without ? profile.getGlsLaboursWithout() : profile.getGlsLabours(), true);	

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 3, translate("misc.total"), Style.LABEL);
		report.addFormulaCell(row, 4, String.format("E%d+E%d", sumGoods, rowNum-1), Style.CURRENCY);
		report.addFormulaCell(row, 5, String.format("F%d+F%d", sumGoods, rowNum-1), Style.CURRENCY);
		report.addFormulaCell(row, 6, String.format("G%d+G%d", sumGoods, rowNum-1), Style.CURRENCY);
		if (report.isCompleteReport()) {
			if (without) {
				report.addLink(ExcelLink.PROFILE_INVEST_WITHOUT_TOTAL, "'"+sheet.getSheetName()+"'!E"+rowNum);
				report.addLink(ExcelLink.PROFILE_INVEST_WITHOUT_OWN, "'"+sheet.getSheetName()+"'!F"+rowNum);
				report.addLink(ExcelLink.PROFILE_INVEST_WITHOUT_EXTERNAL, "'"+sheet.getSheetName()+"'!G"+rowNum);
			} else {
				report.addLink(ExcelLink.PROFILE_INVEST_TOTAL, "'"+sheet.getSheetName()+"'!E"+rowNum);
				report.addLink(ExcelLink.PROFILE_INVEST_OWN, "'"+sheet.getSheetName()+"'!F"+rowNum);
				report.addLink(ExcelLink.PROFILE_INVEST_EXTERNAL, "'"+sheet.getSheetName()+"'!G"+rowNum);
			}
		}
		
		autoSizeColumns(sheet, 10);
	}

	public void profileGeneral(ExcelWrapper report, Profile profile, boolean template) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROFILE_GENERAL));
		((SXSSFSheet) sheet).trackAllColumnsForAutoSizing();
		
		sheet.setSelected(true);
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
			report.addLink(ExcelLink.PROFILE_GENERAL_TOTAL, "'"+sheet.getSheetName()+"'!E"+rowNum);
		}
		
		
		if (profile.getWithWithout()) {
			rowNum++;
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("profileGeneral") + " " + translate("project.without"), Style.H2);
			rowNum = table.writeTable(sheet, rowNum, template ? null : profile.getGlsGeneralWithout(), true);
			if (report.isCompleteReport()) {
				report.addLink(ExcelLink.PROFILE_GENERAL_WITHOUT_TOTAL, "'"+sheet.getSheetName()+"'!E"+(rowNum-1));
			}
		}
		
		autoSizeColumns(sheet, 5);
	}
	
	private void mergeCells(Sheet sheet, int row1, int col1, int row2, int col2) {
		sheet.addMergedRegion(new CellRangeAddress(
		        row1, //first row (0-based)
		        row2, //last row  (0-based)
		        col1, //first column (0-based)
		        col2  //last column  (0-based)
		));
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
		public XlsTable addPerYearColumns(String data, int years, boolean sum, boolean currency) {
			for (int i=0;i<years;i++) {
				XlsPerYearColumn newCol = new XlsPerYearColumn(data, sum, i, currency);
				columns.add(newCol);
			}
			return this;
		}
		public XlsTable addPerYearColumnsFormula(String data, int years, boolean sum, int[] colOffset) {
			for (int i=0;i<years;i++) {
				XlsPerYearColumn newCol = new XlsPerYearColumn(data, sum, i, colOffset, true, true);
				columns.add(newCol);
			}
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
					if (col.type==XlsColumnType.TEXT) { 
						report.addTextCell(row, i, " ");
					} else if (col.type==XlsColumnType.FORMULA) {
						//replace column variable
						String formula = col.data.replace("@", getColumn(i).replace("X", "x"));
						report.addFormulaCell(row, i, writeFormula(formula, rowNum), Style.CURRENCY);
					} else if (col.type==XlsColumnType.PER_YEAR_PROPERTY) {
						XlsPerYearColumn pyc = (XlsPerYearColumn)col;
						if (pyc.formula) {
							String formula = col.data.replace("@", getColumn(i).replace("X", "x"));
							for (int z=0;z<pyc.colOffset.length;z++) {
								formula=formula.replaceFirst("%", getColumn(i-pyc.colOffset[z]).replace("X", "x"));
							}
							report.addFormulaCell(row, i, writeFormula(formula, rowNum), Style.CURRENCY);
						}
					} else if (col.type==XlsColumnType.CURRENCY) {
						report.addNumericCell(row, i, 0, Style.CURRENCY);
					} else if (col.type==XlsColumnType.NUMERIC) {
						report.addNumericCell(row, i, 0);
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
							//replace column variable
							String formula = col.data.replace("@", getColumn(i).replace("X", "x"));
							report.addFormulaCell(row, i, writeFormula(formula, rowNum), Style.CURRENCY);
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
						} else if (col.type==XlsColumnType.PER_YEAR_PROPERTY) {
							XlsPerYearColumn pyc = (XlsPerYearColumn)col;
							if (pyc.colOffset==null) {
								@SuppressWarnings("unchecked")
								PerYearItem item = ((HasPerYearItems<PerYearItem>)o).getYears().get(pyc.year);
								String field = callMethod(item, col.data);
								if (pyc.currency) {
									report.addNumericCell(row, i, Double.parseDouble(field), Style.CURRENCY);
								} else {
									report.addNumericCell(row, i, Double.parseDouble(field));
								}
							} else {
								//replace column variable
								String formula = col.data.replace("@", getColumn(i).replace("X", "x"));
								for (int z=0;z<pyc.colOffset.length;z++) {
									formula=formula.replaceFirst("%", getColumn(i-pyc.colOffset[z]).replace("X", "x"));
								}
								report.addFormulaCell(row, i, writeFormula(formula, rowNum), Style.CURRENCY);
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
	
	protected class XlsPerYearColumn extends XlsColumn {
		int year;
		int[] colOffset;
		boolean currency;
		boolean formula;
		
		public XlsPerYearColumn (String data, boolean sum, int year, boolean currency) {
			super(XlsColumnType.PER_YEAR_PROPERTY, data, sum);
			this.year=year;
			this.currency=currency;
		}
		public XlsPerYearColumn (String data, boolean sum, int year, int[] colOffset, boolean currency, boolean formula) {
			super(XlsColumnType.PER_YEAR_PROPERTY, data, sum);
			this.year=year;
			this.colOffset=colOffset;
			this.currency=currency;
			this.formula=formula;
		}
	}
	
	private enum XlsColumnType { TEXT, NUMERIC, CURRENCY, FORMULA, SELECT, YESNO, PER_YEAR_PROPERTY, NONE }
	
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
	
	public Sheet profileProducts(ExcelWrapper report, Profile profile, boolean without) {
		boolean ig = profile.getIncomeGen();
		int rowNum = 0;
		String title = null;
		SheetName sheetname;
		if (ig) {
			title = translate("profile.report.productDetail");
			if (profile.getWithWithout()) {
				if (without) {
					title= "("+translate("profileProduct.with.without")+") "+title; 
					sheetname=SheetName.PROFILE_PRODUCTS_WITHOUT;
				} else {
					title = "("+translate("profileProduct.with.with")+") "+title;
					sheetname=SheetName.PROFILE_PRODUCTS_WITH;
				}
			} else {
				sheetname = SheetName.PROFILE_PRODUCTS;
			}
		} else {
			title = translate("profile.report.productDetailNongen");
			sheetname=SheetName.PROFILE_PRODUCTS;
		}
				
		Sheet sheet = report.getWorkbook().createSheet(translate(sheetname));
		setColumnWidth(sheet, 0, 200);
		
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, title, Style.TITLE);

		
		row = sheet.createRow(rowNum++);
		// header
		
		String incomeTitle = profile.getIncomeGen() ? translate("profileProductIncome") : translate("profileActivityCharge");

		XlsTable[] tables = productTables(report, profile.getIncomeGen());
		
		for (ProfileProductBase prod : without ? profile.getProductsWithout() : profile.getProducts()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, prod.getDescription(), Style.H2);
			rowNum = addProductDescription(prod, rowNum, sheet, report);
			rowNum = addProductTables(prod, rowNum, true, incomeTitle, sheet, report, tables);
		}
		
		setColumnWidth(sheet, 5, 100);
		return sheet;
	}
	
	public Sheet profileProduct(ExcelWrapper report, ProfileProductBase product, boolean incomeGen) {
		XlsTable[] tables = productTables(report, incomeGen);
		Sheet sheet = report.getWorkbook().createSheet();
		String incomeTitle = incomeGen ? translate("profileProductIncome") : translate("profileActivityCharge");
		addProductTables(product, 0, false, incomeTitle, sheet, report, tables);
		return sheet;
	}
	
	private int addProductDescription(ProfileProductBase prod, int rowNum, Sheet sheet, ExcelWrapper report) {		
		String prodType = prod.getProfile().getIncomeGen() ? "profileProduct" : "profileActivity";
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate(prodType+".prodUnit"));
		report.addTextCell(row, 1, prod.getUnitType());

		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate(prodType+".numUnits"));
		report.addNumericCell(row, 1, prod.getUnitNum());

		if (prod.isCycles()) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate(prodType+".cycleLength"));
			report.addNumericCell(row, 1, prod.getCycleLength());
			report.addSelectCell(row, 2, prod.getLengthUnit(), lengthUnits(), sheet);
	
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate(prodType+".cycles"));
			report.addNumericCell(row, 1, prod.getCyclePerYear());
			report.addTextCell(row, 2, translate("units.perYear"));
		}
		
		ExcelBlockLink blockLink = new ExcelBlockLink();
		
		if (prod.isCycles()) {
			blockLink.qtyProfileProduct = "'"+sheet.getSheetName()+"'!$B$"+(rowNum-2);
			blockLink.cyclesPerYear = "'"+sheet.getSheetName()+"'!$B$"+rowNum;
		} else {
			blockLink.qtyProfileProduct = "'"+sheet.getSheetName()+"'!$B$"+rowNum;
		}
		if (prod.getClass()==ProfileProduct.class) {
			report.getBlockLinks().put(prod.getProductId(), blockLink);
		} else {
			report.getBlockLinksWithoutProject().put(prod.getProductId(), blockLink);
		}
		
		return rowNum++;
	}
	
	
	private int addProductTables(ProfileProductBase prod, int rowNum, boolean showSummary, String incomeTitle, Sheet sheet, ExcelWrapper report, XlsTable[] tables) {
		ExcelBlockLink blockLink=null;
		if (showSummary) {
			blockLink = prod.getClass()==ProfileProduct.class ? report.getBlockLinks().get(prod.getProductId()) : report.getBlockLinksWithoutProject().get(prod.getProductId());
		}
		String perUnit = " ("+ (prod.isCycles() ? translate("units.perUnitperCycle") : translate("project.report.blockDetail.totalPerUnitNoCycle")) +")";
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, incomeTitle + perUnit, Style.H2);
		rowNum=tables[0].writeTable(sheet, rowNum++, prod.getProfileIncomes(), true);
		if (showSummary) {
			blockLink.income = "'"+sheet.getSheetName()+"'!$F$"+rowNum;
		}
		
		rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profileProductInput").replace('/',' ') + perUnit, Style.H2);
		rowNum=tables[1].writeTable(sheet, rowNum++, prod.getProfileInputs(), true);
		if (showSummary) {
			blockLink.cost = "'"+sheet.getSheetName()+"'!$F$"+rowNum;
		}
		
		rowNum++;
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, 0, translate("profileProductLabour") + perUnit, Style.H2);
		rowNum=tables[2].writeTable(sheet, rowNum++, prod.getProfileLabours(), true);
		if (showSummary) {
			blockLink.cost = "("+blockLink.cost+"+'"+sheet.getSheetName()+"'!$F$"+rowNum+")";
		}
		
		rowNum++;
		
		if (showSummary) {
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("profile.report.blockDetail.totals") + " (" + translate("units.perYear") + ")", Style.H2);
			
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("profile.report.blockDetail.incomes"));
			String formula = blockLink.cyclesPerYear==null
					? "("+blockLink.income+"*"+blockLink.qtyProfileProduct+")"
					: "("+blockLink.income+"*"+blockLink.qtyProfileProduct+"*"+blockLink.cyclesPerYear+")";
			report.addFormulaCell(row, 1, formula, Style.CURRENCY);
			if (report.isCompleteReport()) {
				String income= "'"+sheet.getSheetName()+"'!$B$"+rowNum;
				if (prod.getClass()==ProfileProduct.class) {
					if (report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME)==null) {
						report.addLink(ExcelLink.PROFILE_PRODUCT_INCOME, income);
					} else {
						report.addLink(ExcelLink.PROFILE_PRODUCT_INCOME, report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME)+"+"+income);
					}
				} else {
					if (report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME_WITHOUT)==null) {
						report.addLink(ExcelLink.PROFILE_PRODUCT_INCOME_WITHOUT, income);
					} else {
						report.addLink(ExcelLink.PROFILE_PRODUCT_INCOME_WITHOUT, report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME_WITHOUT)+"+"+income);
					}
				}
			}
			
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("profile.report.blockDetail.costs"));
			formula = blockLink.cyclesPerYear==null 
					? "("+blockLink.cost+"*"+blockLink.qtyProfileProduct+")"
					: "("+blockLink.cost+"*"+blockLink.qtyProfileProduct+"*"+blockLink.cyclesPerYear+")"; 
			report.addFormulaCell(row, 1, formula, Style.CURRENCY);
			if (report.isCompleteReport()) {
				String cost="'"+sheet.getSheetName()+"'!$B$"+rowNum;
				if (prod.getClass()==ProfileProduct.class) {
					if (report.getLink(ExcelLink.PROFILE_PRODUCT_COST)==null) {
						report.addLink(ExcelLink.PROFILE_PRODUCT_COST, cost);
					} else {
						report.addLink(ExcelLink.PROFILE_PRODUCT_COST, report.getLink(ExcelLink.PROFILE_PRODUCT_COST)+"+"+cost);
					}
				} else {
					if (report.getLink(ExcelLink.PROFILE_PRODUCT_COST_WITHOUT)==null) {
						report.addLink(ExcelLink.PROFILE_PRODUCT_COST_WITHOUT, cost);
					} else {
						report.addLink(ExcelLink.PROFILE_PRODUCT_COST_WITHOUT, report.getLink(ExcelLink.PROFILE_PRODUCT_COST_WITHOUT)+"+"+cost);
					}
				}
				
			}
			
			row = sheet.createRow(rowNum++);
			report.addTextCell(row, 0, translate("profile.report.blockDetail.netIncome"));
			report.addFormulaCell(row, 1, String.format("B%d-B%d", rowNum-2, rowNum-1), Style.CURRENCY);
		}
		return rowNum+2;
	}

	public void profilePrelimAnalysis(ExcelWrapper report, Profile profile, ProjectScenario scenario, ProfileMatrix matrix) {
		boolean incomeGen = profile.getIncomeGen();
		
		String sheetname;
		String title = incomeGen ? translate("profile.report.preliminary") : translate("profile.report.benefits");
		if (scenario==ProjectScenario.With) {
			if (profile.getWithWithout()) {
				sheetname=translate(SheetName.PROFILE_PRELIM_WITH);
				title+= " "+translate("project.with");
			} else {
				sheetname=translate(SheetName.PROFILE_PRELIM);
			}
		} else if (scenario==ProjectScenario.Without) {
			sheetname=translate(SheetName.PROFILE_PRELIM_WITHOUT);
			title += " "+translate("project.without");
		} else { // incremental
			sheetname=translate(SheetName.PROFILE_PRELIM_INCREMENTAL);
			title += " "+translate("project.incremental");
		}
		
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
		
		Sheet sheet = report.getWorkbook().createSheet(sheetname);

		int rowNum=0;
		short rowHeader = 0;
		short cellNum = 1;
		setColumnWidth(sheet, rowHeader, 500);
		setColumnWidth(sheet, cellNum, 100);
		sheet.setDefaultColumnStyle(cellNum, report.getStyles().get(Style.CURRENCY));
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, title, Style.TITLE);
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (A+B)", translate("profile.report.preliminary.investTotal")));
		report.addFormulaCell(row, cellNum, "B3+B4", Style.CURRENCY);
		
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (A)", translate("profile.report.preliminary.investExtern")));		
		if (report.isCompleteReport()) {
			String formula="";
			if (scenario==ProjectScenario.With || scenario==ProjectScenario.Incremental) {
				formula = report.getLink(ExcelLink.PROFILE_INVEST_EXTERNAL);
			}
			if (scenario==ProjectScenario.Incremental) {
				formula += " - ";
			}
			if (scenario==ProjectScenario.Without || scenario==ProjectScenario.Incremental) {
				formula += report.getLink(ExcelLink.PROFILE_INVEST_WITHOUT_EXTERNAL);
			}
			report.addFormulaCell(row, cellNum, formula, Style.CURRENCY);
		} else {
			double value;
			if (scenario==ProjectScenario.With) {
				value=matrix.totalInvestmentWith-matrix.totalOwnResourcesWith;
			} else if (scenario==ProjectScenario.Without) {
				value=matrix.totalInvestmentWithout-matrix.totalOwnResourcesWithout;
			} else { // incremental
				value=matrix.totalInvestmentWith-matrix.totalOwnResourcesWith-(matrix.totalInvestmentWithout-matrix.totalOwnResourcesWithout);
			}
			report.addNumericCell(row, cellNum, value, Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (B)", translate("profile.report.preliminary.investOwn")));		
		if (report.isCompleteReport()) {
			String formula="";
			if (scenario==ProjectScenario.With || scenario==ProjectScenario.Incremental) {
				formula = report.getLink(ExcelLink.PROFILE_INVEST_OWN);
			}
			if (scenario==ProjectScenario.Incremental) {
				formula += " - ";
			}
			if (scenario==ProjectScenario.Without || scenario==ProjectScenario.Incremental) {
				formula += report.getLink(ExcelLink.PROFILE_INVEST_WITHOUT_OWN);
			}
			report.addFormulaCell(row, cellNum, formula, Style.CURRENCY);
		} else {
			double value;
			if (scenario==ProjectScenario.With) {
				value=matrix.totalOwnResourcesWith;
			} else if (scenario==ProjectScenario.Without) {
				value=matrix.totalOwnResourcesWithout;
			} else { // incremental
				value=matrix.totalOwnResourcesWith-matrix.totalOwnResourcesWithout;
			}
			report.addNumericCell(row, cellNum, value, Style.CURRENCY);
		}
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (C)", titles[1]));		
		if (report.isCompleteReport()) {
			String formula="";
			if (scenario==ProjectScenario.With || scenario==ProjectScenario.Incremental) {
				formula = report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME);
			}
			if (scenario==ProjectScenario.Incremental && report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME_WITHOUT)!=null) {
				formula += " - ";
			}
			if ((scenario==ProjectScenario.Without || scenario==ProjectScenario.Incremental) && report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME_WITHOUT)!=null) {
				formula += report.getLink(ExcelLink.PROFILE_PRODUCT_INCOME_WITHOUT);
			}
			report.addFormulaCell(row, cellNum, formula, Style.CURRENCY);
		} else {
			double value;
			if (scenario==ProjectScenario.With) {
				value=matrix.totalIncomeWith;
			} else if (scenario==ProjectScenario.Without) {
				value=matrix.totalIncomeWithout;
			} else { // incremental
				value=matrix.totalIncomeWith-matrix.totalIncomeWithout;
			}
			report.addNumericCell(row, cellNum, value, Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (D)", translate("profile.report.preliminary.annualCost")));		
		if (report.isCompleteReport()) {
			String formula="";
			if (scenario==ProjectScenario.With || scenario==ProjectScenario.Incremental) {
				formula = report.getLink(ExcelLink.PROFILE_PRODUCT_COST);
			}
			if (scenario==ProjectScenario.Incremental && report.getLink(ExcelLink.PROFILE_PRODUCT_COST_WITHOUT)!=null) {
				formula += " - ";
			}
			if ((scenario==ProjectScenario.Without || scenario==ProjectScenario.Incremental) && report.getLink(ExcelLink.PROFILE_PRODUCT_COST_WITHOUT)!=null) {
				formula += report.getLink(ExcelLink.PROFILE_PRODUCT_COST_WITHOUT);
			}
			report.addFormulaCell(row, cellNum, formula, Style.CURRENCY);
		} else {
			double value;
			if (scenario==ProjectScenario.With) {
				value=matrix.operationCostWith;
			} else if (scenario==ProjectScenario.Without) {
				value=matrix.operationCostWithout;
			} else { // incremental
				value=matrix.operationCostWith-matrix.operationCostWithout;
			}
			report.addNumericCell(row, cellNum, value, Style.CURRENCY);
		}
		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (E)", translate("profile.report.preliminary.annualGeneral")));		
		if (report.isCompleteReport()) {
			String formula="";
			if (scenario==ProjectScenario.With || scenario==ProjectScenario.Incremental) {
				formula = report.getLink(ExcelLink.PROFILE_GENERAL_TOTAL);
			}
			if (scenario==ProjectScenario.Incremental && report.getLink(ExcelLink.PROFILE_GENERAL_WITHOUT_TOTAL)!=null) {
				formula += " - ";
			}
			if ((scenario==ProjectScenario.Without || scenario==ProjectScenario.Incremental) && report.getLink(ExcelLink.PROFILE_GENERAL_WITHOUT_TOTAL)!=null) {
				formula += report.getLink(ExcelLink.PROFILE_GENERAL_WITHOUT_TOTAL);
			}
			report.addFormulaCell(row, cellNum, formula, Style.CURRENCY);
		} else {
			double value;
			if (scenario==ProjectScenario.With) {
				value=matrix.generalCostWith;
			} else if (scenario==ProjectScenario.Without) {
				value=matrix.generalCostWithout;
			} else { // incremental
				value=matrix.generalCostWith-matrix.generalCostWithout;
			}
			report.addNumericCell(row, cellNum, value, Style.CURRENCY);
		}
		row = sheet.createRow(rowNum++);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (C-D-E)", titles[2]));		
		report.addFormulaCell(row, cellNum, "B5-B6-B7", Style.CURRENCY);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, rowHeader, String.format("%s (F)", translate("profile.report.preliminary.annualReserve")));		
		if (report.isCompleteReport()) {
			String formula="";
			if (scenario==ProjectScenario.With || scenario==ProjectScenario.Incremental) {
				formula = report.getLink(ExcelLink.PROFILE_INVEST_GOODS_RESERVE);
			}
			if (scenario==ProjectScenario.Incremental) {
				formula += " - ";
			}
			if (scenario==ProjectScenario.Without || scenario==ProjectScenario.Incremental) {
				formula += report.getLink(ExcelLink.PROFILE_INVEST_WITHOUT_GOODS_RESERVE);
			}
			report.addFormulaCell(row, cellNum, formula, Style.CURRENCY);	
		} else {
			double value;
			if (scenario==ProjectScenario.With) {
				value=matrix.annualReserveWith;
			} else if (scenario==ProjectScenario.Without) {
				value=matrix.annualReserveWithout;
			} else { // incremental
				value=matrix.annualReserveWith-matrix.annualReserveWithout;
			}
			report.addNumericCell(row, cellNum, value, Style.CURRENCY);
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
			report.addNumericCell(row, cellNum, profile.getBenefNum());
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

	public Sheet profileRecommendation(ExcelWrapper report, Profile profile) {
		Sheet sheet = report.getWorkbook().createSheet(translate(SheetName.PROFILE_RECCOMENDATION));

		int rowNum=0;
		short cellNum = 0;
		setColumnWidth(sheet, 0, 500);
		Row row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("profile.report.recommendation"), Style.TITLE);	


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
		
		rowNum=addBigTextCell(report, sheet, rowNum, translate("profile.justification"), profile.getReccDesc(), null, false);

		
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, translate("profile.report.recommendation.signature"), Style.LABEL);
		row = sheet.createRow(rowNum++);
		report.addTextCell(row, cellNum, profile.getTechnician().getDescription());
		
		return sheet;
	}

	   private Map<String, String> labourTypes(){
//		   User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			return rivConfig.getLabourTypes();
		}
}

enum SheetName {
	PROFILE_SUMMARY("profile.report.summary.sheetname"),
	PROFILE_GENERAL("profile.report.general.sheetname"),
	PROFILE_GENERAL_WITH("profile.report.costDetail.with.sheetname"),
	PROFILE_GENERAL_WITHOUT("profile.report.costDetail.without.sheetname"),
	PROFILE_INVEST("profile.report.investDetail.sheetname"),
	PROFILE_INVEST_WITH("profile.report.investDetail.with.sheetname"),
	PROFILE_INVEST_WITHOUT("profile.report.investDetail.without.sheetname"),
	PROFILE_PRODUCTS("profile.report.productDetail.sheetname"),
	PROFILE_PRODUCTS_WITH("profile.report.productDetail.with.sheetname"),
	PROFILE_PRODUCTS_WITHOUT("profile.report.productDetail.without.sheetname"),
	PROFILE_PRELIM("profile.report.preliminary.sheetname"),
	PROFILE_PRELIM_WITH("profile.report.preliminary.with.sheetname"),
	PROFILE_PRELIM_WITHOUT("profile.report.preliminary.without.sheetname"),
	PROFILE_PRELIM_INCREMENTAL("profile.report.preliminary.incremental.sheetname"),
	PROFILE_BENEFITS("profile.report.benefits.sheetname"),
	PROFILE_RECCOMENDATION("project.report.recommendation.sheetname"),
	PROFILE_RESULTS("profile.report.results.sheetname"),
	
	PROJECT_SUMMARY("project.report.summary.sheetname"),
	PROJECT_DESCRIPTION("project.report.general.sheetname"),
	PROJECT_GENERAL("project.report.generalCostsDetail.sheetname"),
	PROJECT_GENERAL_WITH("project.report.generalCostsDetail.with.sheetname"),
	PROJECT_GENERAL_WITHOUT("project.report.generalCostsDetail.without.sheetname"),
	PROJECT_INVEST("project.report.investDetail.sheetname"),
	PROJECT_INVEST_WITH("project.report.investDetail.with.sheetname"),
	PROJECT_INVEST_WITHOUT("project.report.investDetail.without.sheetname"),
	PROJECT_CHRONOLOGY("project.report.chronology.sheetname"),
	PROJECT_PATTERN("project.report.production.sheetname"),
	PROJECT_PATTERN_NONGEN("project.report.production.nonGen.sheetname"),
	PROJECT_BLOCKS("project.report.blockDetail.sheetname"),
	PROJECT_BLOCKS_WITH("project.report.blockDetail.with.sheetname"),
	PROJECT_BLOCKS_WITHOUT("project.report.blockDetail.without.sheetname"),
	PROJECT_ACTIVITIES("project.report.activityDetail.sheetname"),
	PROJECT_PARAMETERS("project.report.parameters.sheetname"),
	PROJECT_WORKING_CAPITAL("project.report.workingcapital.sheetname"),
	PROJECT_WORKING_CAPITAL_WITH("project.report.workingcapital.with.sheetname"),
	PROJECT_WORKING_CAPITAL_WITHOUT("project.report.workingcapital.without.sheetname"),
	PROJECT_CASH_FLOW("project.report.cashFlow.sheetname"),
	PROJECT_CASH_FLOW_WITH("project.report.cashFlow.with.sheetname"),
	PROJECT_CASH_FLOW_WITHOUT("project.report.cashFlow.without.sheetname"),
	PROJECT_CASH_FLOW_INCREMENTAL("project.report.cashFlow.incremental.sheetname"),
	PROJECT_CASH_FLOW_NONGEN("project.report.cashFlowNongen.sheetname"),
	PROJECT_PROFITABILITY("project.report.profitability.sheetname"),
	PROJECT_PROFITABILITY_WITH("project.report.profitability.with.sheetname"),
	PROJECT_PROFITABILITY_WITHOUT("project.report.profitability.without.sheetname"),
	PROJECT_PROFITABILITY_INCREMENTAL("project.report.profitability.incremental.sheetname"),
	PROJECT_CONTRIBUTIONS("project.report.contributions.sheetname"),
	PROJECT_RECOMMENDATION("project.report.recommendation.sheetname"),
	PROJECT_RESULTS("project.report.results.sheetname");
	
	private final String name;
	private SheetName(String name) {
		this.name=name;
	}
	@Override public String toString() {
		return this.name;
	}
}
