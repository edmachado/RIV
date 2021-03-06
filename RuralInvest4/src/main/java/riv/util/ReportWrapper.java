package riv.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperPrint;

public class ReportWrapper {
	String reportTemplate;
	boolean landscape;
	Map<String, Object> params;
	String filename;
	JRDataSource dataSource;
	private JasperPrint jp;
	private int startPage;
	
	@SuppressWarnings("unused")
	private ReportWrapper() {}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ReportWrapper(String reportTemplate, boolean landscape, Object dataSource, String filename, int startPage) {
		this.reportTemplate=reportTemplate;
		this.landscape=landscape;
		this.filename=filename;
		this.startPage=startPage;
		this.params = new HashMap<String, Object>();
		
		if (dataSource==null) {
			this.dataSource=new net.sf.jasperreports.engine.JREmptyDataSource();
		} else if (dataSource instanceof List) {
			this.dataSource=new ReportSource((List)dataSource);
		} else if (dataSource instanceof Set) {
			this.dataSource = new ReportSource((Set)dataSource);
		} else if (dataSource instanceof Object[]) {
			this.dataSource = new ReportSource(new ArrayList(Arrays.asList(dataSource)));
		} else {
			List list = new ArrayList();
			list.add(dataSource);
			this.dataSource = new ReportSource(list);
		}
	}
	
	public boolean isLandscape() { return landscape; }
	public String getReportTemplate() { return reportTemplate; }
	public Map<String, Object> getParams() { return params; }
	public JRDataSource getDataSource() { return dataSource; }
	public String getFilename() { return filename; }
	public JasperPrint getJp() {
		return jp;
	}

	public void setJp(JasperPrint jp) {
		this.jp = jp;
	}

	public int getStartPage() {
		return startPage;
	}
}
