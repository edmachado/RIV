package org.fao.riv.utils;

import static net.sourceforge.jwebunit.junit.JWebUnit.*;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import net.sourceforge.jwebunit.html.Row;
import net.sourceforge.jwebunit.html.Table;

import org.fao.riv.utils.CollectionInputParam;
import org.fao.riv.utils.InputParam.InputParamType;



public class TestTable {
	ArrayList<InputParam> params;
	String tableId;
	String propPrefix;
	String addLink;
	Callable<Void> submit;
	boolean hasSumRow;
	boolean hasCopy=true;
	boolean hasMoveRows=true;
	List<Integer> ignoreInputRows = new ArrayList<Integer>();
	
	public TestTable(String tableId, String propPrefix, String addLink, boolean hasSumRow, Callable<Void> submit) {
		this.tableId=tableId;
		this.propPrefix=propPrefix;
		this.addLink=addLink;
		this.submit=submit;
		this.hasSumRow=hasSumRow;
		params= new ArrayList<InputParam>();
	}
	
	public void setHasCopy(boolean hasCopy) {
		this.hasCopy=hasCopy;
	}
	public void setHasMoveRows(boolean hasMove) {
		this.hasMoveRows=hasMove;
	}
	
	public TestTable addParam(String name) {
		return addParam(name, InputParamType.TEXT, false);
	}
	public TestTable addParam(String name, InputParamType type, boolean calculated) {
		InputParam ip = new InputParam(name, type, calculated);
		params.add(ip);
		return this;
	}
	public TestTable addCheckboxParam(String name, String[] options) {
		InputParam ip = new InputParam(name, InputParamType.CHECKBOX, false);
		ip.setOptions(options);
		params.add(ip);
		return this;
	}
	public TestTable addCollectionParam(String collectionName, String sumProperty, int elementCount) {
		CollectionInputParam ip = new CollectionInputParam();
		ip.setParamType(InputParamType.COLLECTION);
		ip.setCollectionName(collectionName);
		ip.setName(sumProperty);
		ip.setElementCount(elementCount);
		params.add(ip);
		return this;
	}
	
	public TestTable addBlanks(int num) {
		for (int i=0; i<num; i++) addParam("",InputParamType.NONE, false);
		return this;
	}
	
	public void setIgnoreInputRows(int[] ignoredRows) {
		for (int i=0;i<ignoredRows.length;i++) {
			ignoreInputRows.add(ignoredRows[i]);
		}
	}
	public List<Integer> getIgnoreInputRows() {
		return ignoreInputRows;
	}

	public void testOutput() {
		Table compare = createCompareTable();
		assertTablePresent(tableId);
		
		// copy header row and summary divider row before comparing
		if (hasSumRow) {
			compare.getRows().add(0, getTable(tableId).getRows().get(1));
		}
		compare.getRows().add(0, getTable(tableId).getRows().get(0));
		
		// compare tables
		assertTableRowCountEquals(tableId, compare.getRowCount());
		assertTableRowsEqual(tableId, 0, compare);
		assertTableEquals(tableId, compare);
	}
	
	private void inputCollectionValue(CollectionInputParam cip, int element, String value) {
		setTextField(cip.getCollectionName()+"["+(element-1)+"]", value);
		assertTextFieldEquals(cip.getCollectionName()+"["+(element-1)+"]", value);
	}
	private void inputValue(InputParam ip, String value) {
		switch (ip.getParamType()) {
			case TEXT: 
				if (!ip.isCalculated()) { // add data
					setTextField(ip.getName(), value);
				} else { // check autocalculate
					//TODO: simulate keyup or mouseover or similar
					//assertTextFieldEquals(ip.getName(), getMessage(value));
				}
				break;
			case SELECT:
				selectOption(ip.getName(), value);
				break;
			case CHECKBOX:
				if ((ip.getOptions()!=null && value.equals(ip.getOptions()[0])) ||  value.equals("Yes")) {
					checkCheckbox(ip.getName());
				} else {
					uncheckCheckbox(ip.getName());
				}
				break;
			case LINKED:
				checkCheckbox("addLink");
				break;
			default:
				break;
		
		}
	}

	private Table createCompareTable() {
		Table table = new Table();
		
		// summary row 
		if (hasSumRow) {
			Row sumRow = new Row();
			for (InputParam ip : params) {
				switch (ip.getParamType()) {
					case TEXT:
					case SELECT:
					case COLLECTION:
						sumRow.appendCell(getMessage(propPrefix+"Sum."+ip.getName()));
						break;
					case CHECKBOX:
					case NONE:
					case LINKED:
						sumRow.appendCell("");
						break;
					default:
						break;
				}
			}
			table.appendRow(sumRow);
		}
		
		// construct data rows
		for (int i=1;i<=Integer.parseInt(getMessage(propPrefix+"count"));i++) {
			Row testRow = new Row();
			for (InputParam ip : params) {
				String value =
					(ip.getParamType()==InputParamType.TEXT || 
					ip.getParamType()==InputParamType.SELECT ||
					ip.getParamType()==InputParamType.CHECKBOX ||
					ip.getParamType()==InputParamType.COLLECTION)
					? getMessage(propPrefix+i+"."+ip.getName())
					: "";
				testRow.appendCell(value);
			}
			table.appendRow(testRow);
		}
		return table;
	}
	
	public void testWithInput() throws Exception {
		for (int i=1;i<=Integer.parseInt(getMessage(propPrefix+"count"));i++) {
			if (!ignoreInputRows.contains(i)) {
				clickLink(addLink);
				for (InputParam ip : params) {
					if (ip instanceof CollectionInputParam) {
						CollectionInputParam cip = (CollectionInputParam)ip;
						for (int e=1;e<=cip.getElementCount();e++) {
							String value = getMessage(propPrefix+i+"."+cip.getCollectionName()+"."+e);
							inputCollectionValue(cip, e, value);
						}
					} else {
						String value = ip.getParamType()==InputParamType.LINKED || ip.getParamType()==InputParamType.NONE
								? "" : getMessage(propPrefix+i+"."+ip.getName());
						inputValue(ip, value);
					}
				}
				
				submit.call();
				assertTablePresent(tableId);
				int otherRows = 1;
				for (Integer x : ignoreInputRows) {
					if (x>i) { otherRows++; }
				}
				if (hasSumRow) { otherRows=otherRows+2; }
				assertTableRowCountEquals(tableId, otherRows+i);
			}
		}
		
		// test if entire table (including sum row if expected) is correct
		testOutput();
		
		// test copy and delete
		if (hasCopy 
				&& ignoreInputRows.size()==0) { // ignored rows breaks test
			testCopyAndDeleteRow();
		}
		
		if (hasMoveRows) { testMoveRows(); }
	}
	
	void testMoveRows() {
		assertTablePresent(tableId);
		Table tOriginal = getTable(tableId);
		if (tOriginal.getRowCount() > 1) {
			Row r1Original = tOriginal.getRows().get(1);
			Row r2Original = tOriginal.getRows().get(2);
			
			String xpathDown = "(//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -13) = 'arrow_down.png']]])[1]";
			String xpathUp = "(//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -11) = 'arrow_up.png']]])[1]";
			
			// should contain at least one "up" and one "down" link
			assertElementPresentByXPath(xpathDown);
			assertElementPresentByXPath(xpathUp);
		
			// move first row down
			clickElementByXPath(xpathDown);
			assertTablePresent(tableId);
			Table tAfter = getTable(tableId);
			
			assertRowsEqual(r1Original, tAfter.getRows().get(2));
			assertRowsEqual(r2Original, tAfter.getRows().get(1));
			
			// move second row back up
			assertElementPresentByXPath(xpathUp);
			clickElementByXPath(xpathUp);
			assertTablePresent(tableId);
			tAfter = getTable(tableId);
			assertRowsEqual(r1Original, tAfter.getRows().get(1));
			assertRowsEqual(r2Original, tAfter.getRows().get(2));
		}
	}
	
	void assertRowsEqual(Row r1, Row r2) {
		for (int c=0;c<r1.getCellCount();c++) {
			assertTrue(r1.getCells().get(0).getValue().equals(r2.getCells().get(0).getValue()));
		}
	}
	
	void testCopyAndDeleteRow() {
		int origRows = getTable(tableId).getRowCount();
		// copy row
		String xpath = "(//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -12) = 'duplicate.gif']]])[1]";
		assertElementPresentByXPath(xpath);
		clickElementByXPath(xpath);
		assertTableRowCountEquals(tableId, origRows+1);
		
		// test if 1st and last rows are equal
		Row firstRow = getTable(tableId).getRows().get(3);
		Row lastRow = getTable(tableId).getRows().get(origRows);
		assertRowsEqual(firstRow, lastRow);
		
		// delete row
		xpath = "(//table[@id='"+tableId+"']//a[img[@src[substring(., string-length() -9) = 'delete.gif']]])["+(origRows-2)+"]";
		assertElementPresentByXPath(xpath);
		clickElementByXPath(xpath);
		assertTableRowCountEquals(tableId, origRows);
	}
}