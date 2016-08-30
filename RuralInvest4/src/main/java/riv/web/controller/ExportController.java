package riv.web.controller;

import java.io.OutputStream;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import riv.web.service.Exporter;

@Controller
public class ExportController {
	static final Logger LOG = LoggerFactory.getLogger(ExportController.class);
	
	@Autowired
	Exporter exporter;
	
	@RequestMapping("/config/export/backup.riv")
	public void backup(HttpServletRequest request, HttpServletResponse response, OutputStream out) {
		response.setHeader("Content-Disposition", "attachment;filename=\"backup.riv\"");
		response.setHeader("Set-Cookie", "fileDownload=true; path=/");
		exporter.exportBackup(out);
	}

	@RequestMapping("/config/export/settings.riv")
	public void exportConfig(HttpServletRequest request, HttpServletResponse response, OutputStream out) {
		response.setHeader("Content-Disposition", "attachment;filename=\"settings.riv\"");
		exporter.exportConfig(out);
	}
	
	@RequestMapping("/profile/export/{id}/{filename}")
	public void exportProfile(@PathVariable int id, @PathVariable String filename, HttpServletRequest request, HttpServletResponse response, OutputStream out)  {
		response.setHeader("Content-Disposition", "attachment;filename=\"" + exporter.getDownloadName(filename) + ".riv\"");
		boolean isGeneric =  Boolean.parseBoolean(request.getParameter("generic"));
		exporter.exportProfile(id,isGeneric, out);
	}
	
	@RequestMapping("project/export/{id}/{filename}")
	public void exportProject(@PathVariable Integer id, @PathVariable String filename, HttpServletRequest request, HttpServletResponse response, OutputStream out) {
		response.setHeader("Content-Disposition", "attachment;filename=\"" + exporter.getDownloadName(filename) + ".riv\"");
		boolean isGeneric =  Boolean.parseBoolean(request.getParameter("generic"));
		exporter.exportProject(id,isGeneric, out);
	}
}