package riv.web.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;

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

	@RequestMapping("/config/export/settings.riv")
	public void exportConfig(HttpServletRequest request, OutputStream out) {
		exporter.exportConfig(out);
	}
	
	@RequestMapping("/profile/export/{id}/{filename}")
	public void exportProfile(@PathVariable int id, @PathVariable String filename, HttpServletRequest request, OutputStream out)  {
		boolean isGeneric =  Boolean.parseBoolean(request.getParameter("generic"));
		exporter.exportProfile(id,isGeneric, out);
	}
	
	@RequestMapping("project/export/{id}/{filename}")
	public void exportProject(@PathVariable Integer id, @PathVariable String filename, HttpServletRequest request, OutputStream out) {
		boolean isGeneric =  Boolean.parseBoolean(request.getParameter("generic"));
		exporter.exportProject(id,isGeneric, out);
	}
}