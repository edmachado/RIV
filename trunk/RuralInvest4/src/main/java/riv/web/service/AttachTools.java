package riv.web.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import riv.objects.AttachedFile;
import riv.objects.Probase;
import riv.objects.profile.ProfileFile;
import riv.objects.project.ProjectFile;
 
@Component
public class AttachTools implements Serializable {
	private static final long serialVersionUID = -6816402803334737761L;
	static final Logger LOG = LoggerFactory.getLogger(AttachTools.class);
	public static final long dirSizeLimit = 3145728;
	
	@Autowired
	DataService dataService;
	
	public void migrateFromVersion3(String dataPath) {
		FileFilter directoryFilter = new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		
		File dir = new File(dataPath+"/attach");
		if (dir.exists()) {
			File profilesDir = new File(dir.getAbsolutePath()+"/profiles");
			if (profilesDir.exists()) {
				File[] profiles = profilesDir.listFiles(directoryFilter);
				for (File p : profiles) {
					migrateProbaseAttached(false, p);
				}
				profilesDir.delete();
			}
		
			File projectsDir = new File(dir.getAbsolutePath()+"/projects");
			if (projectsDir.exists()) {
				File[] projects = projectsDir.listFiles(directoryFilter);
				for (File p : projects) {
					migrateProbaseAttached(true, p);
				}
				projectsDir.delete();
			}
			dir.delete();
		}
		
	}
	
	private void migrateProbaseAttached(boolean isProject, File probaseDir) {
		int probaseId = Integer.parseInt(probaseDir.getName());
		File[] files = probaseDir.listFiles();
		for (File f : files) {
			FileInputStream fis = null;
			ByteArrayOutputStream baos = null;
			//String normalizedName = normalizeUnicode(f.getName());
			try {
				fis = new FileInputStream(f);
				baos = new ByteArrayOutputStream();
				IOUtils.copy(fis, baos);
				addAttachedFile(probaseId, isProject, f.getName(), baos.toByteArray());
			} catch (Exception e) {
				LOG.error("Error moving attached file from file to db.",e);
			} finally {
				try {
					fis.close();
					f.delete();
					baos.close();
				} catch (Exception e) {
					LOG.error("Problem closing file connection.",e);
				}
			}
		}
		probaseDir.delete();
	}

	public boolean fileExists(Probase p, String filename) {
		return dataService.profileFileExistsWithFilename(p.getProId(), p.isProject(), filename);
	}
	
	public List<AttachedFile> getAttached(int id, boolean isProject, boolean loadContentBytes) {
		return dataService.getAttachedFiles(id, isProject, loadContentBytes);
	}
	
	public void addAttachedFile(Probase probase, String filename, byte[] data) {
		addAttachedFile(probase.getProId(), probase.isProject(), filename, data);
	}
		
	private void addAttachedFile(int proId, boolean isProject, String filename, byte[] data) {
		AttachedFile af = isProject ? new ProjectFile() : new ProfileFile();
		af.setFilename(filename);
		af.setLength(data.length);
		af.setProbaseId(proId);
		dataService.addAttachedFile(data, af);
	}
	
	public void copyAttached(Probase source, Probase target)  {
		dataService.copyAttachedFiles(source, target);
	}
	

	public void saveAttachedFilesFromZip(Probase p, byte[] zipBytes) {
		ByteArrayInputStream bais= new ByteArrayInputStream(zipBytes);
		ZipInputStream zis = new ZipInputStream(bais);
		ZipEntry entry;
		try {
			entry = zis.getNextEntry(); // skip first file (project.riv)
			while ((entry = zis.getNextEntry()) != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				IOUtils.copy(zis, baos);
				addAttachedFile(p, entry.getName(), baos.toByteArray());
				baos.close();
				bais.close();
			}
			zis.close();
		} catch (IOException e) {
			LOG.error("Error reading file from zip.",e);
		}
	}
	
	public void addAttachedFilesToZip(Probase p, ZipOutputStream zos) throws Exception {
		List<AttachedFile> files = getAttached(p.getProId(), p.isProject(), true);
		
		for (AttachedFile file : files) {
			ZipEntry entry = new ZipEntry(file.getFilename());
			entry.setSize(file.getLength());
			zos.putNextEntry(entry);
			IOUtils.write(file.getContentBytes(), zos);
		}
	}
	
	public byte[] getFileFromZip(byte[] file, int index) {
		ByteArrayInputStream bais= new ByteArrayInputStream(file);
		if (!isZipStream(bais)) {
			return file;
		} else {
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			
			try {
				ZipInputStream zis = new ZipInputStream(bais);
			
				for (int i=0;i<=index;i++) {
						zis.getNextEntry();
				}
			
				IOUtils.copy(zis, baos);
				zis.close();
				bais.close();
			    baos.close();
			} catch (IOException e) {
				LOG.info("Couldn't read zip file entry", e);
			}
		    return baos.toByteArray();
		}
	}
	
	
	// source: http://stackoverflow.com/questions/801935/formatting-file-sizes-in-java-jstl
	/**
	   * Given an integer, return a string that is in an approximate, but human 
	   * readable format. 
	   * It uses the bases 'k', 'm', and 'g' for 1024, 1024**2, and 1024**3.
	   * @param number the number to format
	   * @return a human readable form of the integer
	   */
	private DecimalFormat oneDecimal = new DecimalFormat("0.0");
	public String humanReadableInt(long number) {
	    long absNumber = Math.abs(number);
	    double result = number;
	    String suffix = "";
	    if (absNumber < 1024) {
	      // nothing
	    } else if (absNumber < 1024 * 1024) {
	      result = number / 1024.0;
	      suffix = " kb";
	    } else if (absNumber < 1024 * 1024 * 1024) {
	      result = number / (1024.0 * 1024);
	      suffix = " Mb";
	    } else {
	      result = number / (1024.0 * 1024 * 1024);
	      suffix = " Gb";
	    }
	    return oneDecimal.format(result) + suffix;
	  }

// Source: http://notepad2.blogspot.co.il/2012/07/java-detect-if-stream-or-file-is-zip.html
 private static byte[] MAGIC = { 'P', 'K', 0x3, 0x4 };
 
 /**
  * The method to test if a input stream is a zip archive.
  *
  * @param in
  *            the input stream to test.
  * @return
  */
 public static boolean isZipStream(InputStream in) {
  if (!in.markSupported()) {
   in = new BufferedInputStream(in);
  }
  boolean isZip = true;
  try {
   in.mark(MAGIC.length);
   for (int i = 0; i < MAGIC.length; i++) {
    if (MAGIC[i] != (byte) in.read()) {
     isZip = false;
     break;
    }
   }
   in.reset();
  } catch (IOException e) {
   isZip = false;
  }
  return isZip;
 }
 
 /**
  * Test if a file is a zip file.
  *
  * @param f
  *            the file to test.
  * @return
  */
 public static boolean isZipFile(File f) {
 
  boolean isZip = true;
  byte[] buffer = new byte[MAGIC.length];
  try {
   RandomAccessFile raf = new RandomAccessFile(f, "r");
   raf.readFully(buffer);
   for (int i = 0; i < MAGIC.length; i++) {
    if (buffer[i] != MAGIC[i]) {
     isZip = false;
     break;
    }
   }
   raf.close();
  } catch (Throwable e) {
   isZip = false;
  }
  return isZip;
 }
}
 