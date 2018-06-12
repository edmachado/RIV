package riv.web.service;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import riv.objects.AttachedFile;
import riv.objects.ExportFile;
import riv.objects.FilterCriteria;
import riv.objects.profile.Profile;
import riv.objects.profile.ProfileResult;
import riv.objects.project.Project;
import riv.objects.project.ProjectResult;
import riv.web.config.RivConfig;

@Component
public class Exporter {
	static final Logger LOG = LoggerFactory.getLogger(Exporter.class);
	
	@Autowired
	private DataService dataService;
	@Autowired
	private RivConfig rivConfig;
	@Autowired
	AttachTools attachTools;
	@Autowired
	ServletContext sc;
	
	public void errorDiagnostic(OutputStream out) {
		File log =  new File(sc.getRealPath("WEB-INF/riv-application.log"));
		File dataDir =  new File(sc.getRealPath("WEB-INF/data"));
		
		ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new BufferedOutputStream(out));
		
		addFileToZip(log, zos);
		for (File file : dataDir.listFiles()) {
			if (!file.isDirectory()) {
				addFileToZip(file, zos);
			}
		}
        try {
			zos.finish();
	        zos.close();
		} catch (IOException e) {
			LOG.error("Error creating diagnostic zip.",e);
		}
	}
	
	private void exportPros (FilterCriteria fc, File f, boolean project, boolean ig) {
		try (FileOutputStream os = new FileOutputStream(f)) { 
			
			if (project && ig) {
				fc.setObjType("igpj");
			} else if (project && !ig) {
				fc.setObjType("nigpj");
			} else if (!project && ig) {
				fc.setObjType("igpf");
			} else if (!project && !ig) {
				fc.setObjType("nigpf");
			}
			if (project) {
				List<ProjectResult> projects = dataService.getProjectResults(fc);
				batchExportProjects(projects, false, os);
			} else {
				List<ProfileResult> profiles = dataService.getProfileResults(fc);
				batchExportProfiles(profiles, false, os);
			}
		} catch (Exception e) {
			
		}
	}
	public void exportBackup(OutputStream out) {
		FilterCriteria fc = new FilterCriteria();
		fc.setFreeText("");
//		FileOutputStream os=null;
		ByteArrayOutputStream baos=null;
//		List<ProjectResult> projects;
//		List<ProfileResult> profiles;
		File[] files = new File[4];
		
		try {
			// ig projects
			files[0] = File.createTempFile("ig-projects.",".zip");
			exportPros(fc, files[0], true, true);
				
			// add nig projects
			files[1] = File.createTempFile("nig-projects.",".zip");
			exportPros(fc, files[1], true, false);
				
			// ig profiles
			files[2] = File.createTempFile("ig-profiles.",".zip"); 
			exportPros(fc, files[2], false, true);
				
			// nig profiles
			files[3] = File.createTempFile("nig-profiles.",".zip"); 
			exportPros(fc, files[3], false, false);
		} catch (IOException e) {
			throw new RuntimeException("Cannot create temp files", e);
		} 
			
		// create outer zip file
		ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new BufferedOutputStream(out));
		zos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
		addFileToZip(files[0], zos);
		addFileToZip(files[1], zos);
		addFileToZip(files[2], zos);
		addFileToZip(files[3], zos);
	
		// settings
		baos = new ByteArrayOutputStream();
		exportConfig(baos);
		addByteArrayToZip(baos.toByteArray(), "settings.riv", zos);	
		
		try {
			zos.finish();
			zos.close();
		} catch (IOException e) {
			LOG.error("Error",e);
			throw new RuntimeException("Error creating zip file", e);
		} finally {
//			os=null; 
			baos=null;
			files[0].delete();
			files[1].delete();
			files[2].delete();
			files[3].delete();
			System.gc();
		}
		
		System.gc();
	}
	
	public void batchExportProjects(List<ProjectResult> projects, boolean isGeneric, OutputStream out) {
		ArrayList<ExportFile> files = new ArrayList<ExportFile>();
		for (ProjectResult pr : projects) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			exportProject(pr.getProjectId(), isGeneric, baos);
			ExportFile ef = new ExportFile();
			ef.setFilename(pr.getProjectName()+".riv");
			ef.setContentBytes(baos.toByteArray());
			files.add(ef);
		}
		export(null, null, null, null, files, out);
	}
	
	public void batchExportProfiles(List<ProfileResult> profiles, boolean isGeneric, OutputStream out) {
		ArrayList<ExportFile> files = new ArrayList<ExportFile>();
		for (ProfileResult pr : profiles) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			exportProfile(pr.getProfileId(), isGeneric, baos);
			ExportFile ef = new ExportFile();
			ef.setFilename(pr.getProfileName()+".riv");
			ef.setContentBytes(baos.toByteArray());
			files.add(ef);
		}
		export(null, null, null, null, files, out);
	}
	
	public void exportProject(int id, boolean isGeneric, OutputStream out) {
		Project pOld = dataService.getProject(id,  -1, true);
		Project p = pOld.copy(true, dataService.getLatestVersion().getVersion());
		p.setTechnician(null);
		// for generic exports: convert currency and set generic configs
		if (isGeneric) {
			p.setGeneric(true);
			p.convertCurrency(1/p.getExchRate(), 2); // 2 decimal places for USD
			p.setBeneficiary(rivConfig.getBeneficiaries().get(-3));
			p.setFieldOffice(rivConfig.getFieldOffices().get(-4));
			p.setStatus(rivConfig.getStatuses().get(-7));
			p.setEnviroCategory(rivConfig.getEnviroCategories().get(-6));
			p.setAppConfig1(rivConfig.getAppConfig1s().get(-8));
			p.setAppConfig2(rivConfig.getAppConfig2s().get(-9));
			
			if (p.getIncomeGen()) { p.setProjCategory(rivConfig.getCategories().get(-5)); }
			else { p.setProjCategory(rivConfig.getCategories().get(-2)); }
		}
		List<AttachedFile> files = attachTools.getAttached(id, true, true);
		
		export(p.getProjectName(), serializeObject(p), null, null, files, out);
	}
	
	public void exportProfile(int id, boolean isGeneric, OutputStream out) {
		Profile p = dataService.getProfile(id, -1).copy(true);
		p.setTechnician(null);
		if (isGeneric) {
			p.setGeneric(true);
			p.convertCurrency(1/p.getExchRate());
			p.setFieldOffice(rivConfig.getFieldOffices().get(-4));
			p.setStatus(rivConfig.getStatuses().get(-7));
		}
		List<AttachedFile> files = attachTools.getAttached(id, false, true);
		export(p.getProfileName(), serializeObject(p), null, null, files, out);
	}
	
	public void exportConfig(OutputStream out) {
		String filename = "settings";
		
		// save image directly in zip
		try {
			RivConfig rcExport = rivConfig.copyForExport();
			byte[] userLogo = rcExport.getSetting().getOrgLogo();
			rcExport.getSetting().setOrgLogo(null);
			
			export(filename, serializeObject(rcExport), userLogo, "rivOrgLogo", null, out);
		} catch (Exception e) {
			LOG.error("Error serializing RuralInvest configuration.",e);
		}
	}
	
	/*
	 * Export with apache-commons-compress
	 */
	private void export(String filename, byte[] serialized, byte[] fileToZip, String bytesFileName, List<? extends ExportFile> filesToZip, OutputStream out) {
		try {
			ZipArchiveOutputStream zos = new ZipArchiveOutputStream(new BufferedOutputStream(out));
			zos.setCreateUnicodeExtraFields(ZipArchiveOutputStream.UnicodeExtraFieldPolicy.ALWAYS);
	            
			 // 1. zip main file
			if (serialized!=null) {
				addByteArrayToZip(serialized, getDownloadName(filename)+".riv", zos);
			}
            
            // 2.second file
            if (fileToZip!=null) {
            	addByteArrayToZip(fileToZip,  getDownloadName(bytesFileName), zos);
            }
            
            // 3. other attached files
            if (filesToZip!=null) {
	            for (ExportFile pf : filesToZip) {            	
					addByteArrayToZip(pf.getContentBytes(),  getDownloadName(pf.getFilename()), zos);
				}
            }
            
            zos.finish();
	        zos.close();

		} catch (Exception e) {
			LOG.error("Error",e);
		}
	}

	private void addFileToZip(File file, ZipArchiveOutputStream zos) {
		ZipArchiveEntry entry = new ZipArchiveEntry(file.getName());
		
		FileInputStream fis = null;
		try {
			zos.putArchiveEntry(entry);
			fis = new FileInputStream(file.getAbsolutePath());
			IOUtils.copy(fis, zos);
			zos.closeArchiveEntry();
		} catch (Exception e) {
			LOG.error("Error adding file to zip stream.",e);
		} finally {
            org.apache.commons.io.IOUtils.closeQuietly(fis);
        }
		
		
	}
	
	private void addByteArrayToZip(byte[] bytes, String filename, ZipArchiveOutputStream zos) {
		ZipArchiveEntry entry = new ZipArchiveEntry(filename);
        entry.setSize(bytes.length);
        
        try {
            zos.putArchiveEntry(entry);
        	zos.write(bytes);
			zos.closeArchiveEntry();
		} catch (IOException e) {
			LOG.error("Error adding file (byte array) to zip stream.",e);
			throw new RuntimeException("Error adding file (byte array) to zip stream.",e);
		}
	}
	
	private byte[] serializeObject(Object toSerialize) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		XMLEncoder encoder = prepareEncoder(baos);
		encoder.writeObject(toSerialize);
		encoder.close();
		return  baos.toByteArray();
	}
	private XMLEncoder prepareEncoder(ByteArrayOutputStream baos) {
		XMLEncoder encoder = new XMLEncoder(baos);
		encoder.setPersistenceDelegate(BigDecimal.class,
				new DefaultPersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						BigDecimal bd = (BigDecimal) oldInstance;
						return new Expression(oldInstance, oldInstance
								.getClass(), "new", new Object[] { bd
								.toString() });
					}

					protected boolean mutatesTo(Object oldInstance,
							Object newInstance) {
						return oldInstance.equals(newInstance);
					}
				});

		encoder.setPersistenceDelegate(Timestamp.class,
				new DefaultPersistenceDelegate() {
					protected Expression instantiate(Object oldInstance,
							Encoder out) {
						Timestamp date = (Timestamp) oldInstance;
						return new Expression(oldInstance, Timestamp.class,
								"new", new Object[] { Long.valueOf(date
										.getTime()) });
					}
				});
		
		return encoder;
	}
	
	// for downloading
	public String getDownloadName(String input)  {
		// filename shouldn't contain unacceptable characters
		String output = input.replaceAll("[\"']", "")
				.replace("х", "x") // cyrillic x -- for some reason this creates a problem
				.replaceAll("[:<>\\.|\\?\\*/\\\\\"\\s]", "_");
		output = output.substring(0, Math.min(output.length(), 50));
		return output;
	}

	
}
