package riv.util;

import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class RtlReportConverter extends Task  {
	static final Logger LOG = LoggerFactory.getLogger(RtlReportConverter.class);
	
	static final Pattern matchReportHeader = Pattern.compile("<jasperReport([\\s\\S]*)pageWidth=\"(\\d+)([\\s\\S]*)columnWidth=\"(\\d+)\" leftMargin=\"(\\d+)\" rightMargin=\"(\\d+)([\\s\\S]*)\">");
	static final Pattern matchElement = Pattern.compile("<reportElement([\\s\\S]*)x=\"(-?\\d+)([\\s\\S]*)width=\"(\\d+)([\\s\\S]*)");
	static final Pattern matchTextElementLeft = Pattern.compile("<textElement textAlignment=\"Left\"(.*)");
	static final Pattern matchTextElementJustified = Pattern.compile("<textElement textAlignment=\"Justified\"(.*)");
	
	private String path;
	
	public void execute() throws BuildException {
		File dir  = new File(path);
		//FilenameFilter filter = new FilenameFilter() { public boolean accept(File dir, String name) { return name.endsWith(".jrxml") &! name.endsWith("_ar.jrxml"); } }; 
		for (File f :  dir.listFiles()) {
			if (f.isDirectory()) {
				for (File f2 : f.listFiles()) {
					convertToFile(f2);
				}
			} else {
			convertToFile(f);
			}
		}
	}
	
	public static void convertToFile(File source) {
		File target = new File(source.getAbsolutePath().replace(".jrxml", "_ar.jrxml"));
		String original=null;
		try {
			original = FileUtils.readFileToString(source, "utf-8");
		} catch (IOException e) {
			LOG.error("Error reading jasper template from file.",e);
		}
		String result = convertString(original);
		try {
			FileUtils.write(target, result, "utf-8");
		} catch (IOException e) {
			LOG.error("Error writing jasper template to file.",e);
		}
		
	}
	
	public static InputStream convert(InputStream  streamIn) {
		String notConverted = justRead(streamIn);
		String converted = convertString(notConverted);
		InputStream streamOut=null;
		try {
			streamOut = new ByteArrayInputStream(converted.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			LOG.error("Error streaming out converted report", e);
		}
		return streamOut;
	}
	
	private static String justRead(InputStream inputStream) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(inputStream, writer, "utf8");
		} catch (IOException e) {
			LOG.error("Error reading in jrxml file.", e);
		}
		return writer.toString();
	}
	
	private static String convertString(String stringIn)  {
		
		int totalWidth=0;
		int leftMargin=0;
		int rightMargin=0;
		int columnWidth=0;
		boolean hasColumns=false;
		
		StringBuilder sb = new StringBuilder();
		List<String> lines=null;
		try {
			lines = IOUtils.readLines(new StringReader(stringIn));
		} catch (IOException e) {
			LOG.error("Error reading in jrxml data", e);
		}
		
		for (String text : lines) {
			Matcher matcher = matchReportHeader.matcher(text);
			if (matcher.find()) {
				// has columns
				if (text.indexOf("columnCount")!=-1) {
					hasColumns=true;	
				}
				
				// get variables
				int pageWidth = Integer.parseInt(matcher.group(2));
				columnWidth = Integer.parseInt(matcher.group(4));
				leftMargin = Integer.parseInt(matcher.group(5));
				rightMargin = Integer.parseInt(matcher.group(6));
				totalWidth = pageWidth-leftMargin-rightMargin;
				
				// replace left and right margin
				String untilColWidth = "<jasperReport"+matcher.group(1)+"columnDirection=\"RTL\" pageWidth=\""+matcher.group(2)+matcher.group(3)+"columnWidth=\""+columnWidth;
				String untilLeft = "\" leftMargin=\""+rightMargin;
				String beforeRight = "\" rightMargin=\""+leftMargin;
				String afterRight = matcher.group(7)+"\">";
				sb.append(untilColWidth+untilLeft+beforeRight+afterRight);
			} else {
				matcher = matchElement.matcher(text);
				if (matcher.find()) {
					// calculate 
					int x = Integer.parseInt(matcher.group(2));
					int width = Integer.parseInt(matcher.group(4));
					int newX = hasColumns && width<=leftMargin+columnWidth ? columnWidth-x-width : totalWidth-x-width;
					// replace line
					String piece1 = "<reportElement"+matcher.group(1);
					String piece2 = "x=\""+newX;
					String piece3 = matcher.group(3);
					String piece4 = "width=\""+matcher.group(4);
					String piece5 = matcher.group(5);
					
					sb.append(piece1+piece2+piece3+piece4+piece5);
				} else {
					if (text.trim().equals("<textElement>")) {
						sb.append("<textElement textAlignment=\"Right\">");
					} else {
						matcher = matchTextElementLeft.matcher(text);
						if (matcher.find()) {
							String flipped = "<textElement textAlignment=\"Right\""+matcher.group(1);
							sb.append(flipped);
						} else {
							matcher = matchTextElementJustified.matcher(text);
							if (matcher.find()) {
								String flipped = "<textElement textAlignment=\"Right\""+matcher.group(1);
								sb.append(flipped);
							} else
								// copy line
								sb.append(text);
						}
					}
				}
			}
		}
		
		return sb.toString();
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}