package riv.web.service;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
	
	public void batchExportProjects(List<ProjectResult> projects, boolean isGeneric, OutputStream out) {
		ArrayList<ExportFile> files = new ArrayList<ExportFile>();
		for (ProjectResult pr : projects) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			exportProject(pr.getProjectId(), isGeneric, baos);
			ExportFile ef = new ExportFile();
			ef.setFilename(pr.getProjectName());
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
			ef.setFilename(pr.getProfileName());
			ef.setContentBytes(baos.toByteArray());
			files.add(ef);
		}
		export(null, null, null, null, files, out);
	}
	
	public void exportProject(int id, boolean isGeneric, OutputStream out) {
		Project p = dataService.getProject(id,  -1).copy(true, dataService.getLatestVersion().getVersion());
		p.setTechnician(null);
		// for generic exports: convert currency and set generic configs
		if (isGeneric) {
			p.setGeneric(true);
			p.convertCurrency(1/p.getExchRate());
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
		
		// better to save image directly in zip
		try {
			RivConfig rcExport = rivConfig.copyForExport();
			//Blob orgLogo = rcExport.getSetting().getOrgLogo();
			byte[] userLogo = rcExport.getSetting().getOrgLogo();// orgLogo.getBytes(1L, (int) orgLogo.length());
			//orgLogo.free(); // performance call
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

		/*Map delegates = HibernatePersistenceDelegate.getDelegates();
		for (java.util.Iterator i = delegates.keySet().iterator(); i.hasNext();) {
			Class clazz = (Class) i.next();
			encoder.setPersistenceDelegate(clazz,
					(java.beans.PersistenceDelegate) delegates.get(clazz));
		}*/
		return encoder;
	}
	
	// for downloading
	public String getDownloadName(String input) {
		// filename shouldn't contain unacceptable characters
		return input.replaceAll("[:<>\\.|\\?\\*/\\\\\"\\s]", "_");
	}

	
}
