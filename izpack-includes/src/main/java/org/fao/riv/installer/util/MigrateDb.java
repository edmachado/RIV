package org.fao.riv.installer.util;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class MigrateDb {
	public void run(AbstractUIProcessHandler uih, String[] args) {
		File oldPath = new File(args[0]);
		File newInstall = new File(args[1]);
		try {
			System.out.println("Migrating database");
			newInstall.mkdirs();
			System.out.println("Copying from " + oldPath + "  to " + newInstall);
			FileUtils.copyDirectory(oldPath, newInstall);
			System.out.println("Finished migrating database.");
		} catch (IOException e) {
			e.printStackTrace(System.out);
		}
	}
}