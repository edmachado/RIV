package riv;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.codehaus.plexus.util.FileUtils;
import org.junit.Test;

public class TestDbUpgrade {

	@Test
	public void test1() throws Exception {
		// RIV3.1 in Russian
		testUpgrade("example1");		
	}
	
	@Test
	public void test2() throws Exception {
		// Old test site
		testUpgrade("example2");		
	}
	
	@Test
	public void test3() throws Exception {
		// English demo site
		testUpgrade("example3");		
	}
	
	private void testUpgrade(String db) throws Exception {
		String dbPath = getClass().getResource("../"+db).getPath();
		
		File copyFrom = new File(dbPath);
		assertTrue(copyFrom.exists());
		
		File copyTo = File.createTempFile("testDb", "");
		copyTo.delete();
		copyTo.mkdir();
		FileUtils.deleteDirectory(copyTo);
		FileUtils.copyDirectory(copyFrom, copyTo);
	
		// shutdown old db using hsqldb 1.8 jar
		// get 1.8.0.10 connection
		Connection con = getDataSource(true, copyTo.getAbsolutePath()).getConnection();
		Statement stmt=con.createStatement();
		stmt.execute("SHUTDOWN SCRIPT");
		stmt.close();
		con.close();
		
		// get 2.3.0 connection
		// first run a shutdown compact
		con = getDataSource(false, copyTo.getAbsolutePath()).getConnection();
		stmt = con.createStatement();
		stmt.execute("SHUTDOWN");
		stmt.close();
		
		// then reopen and run an sql script
		con = getDataSource(false, copyTo.getAbsolutePath()).getConnection();
		stmt = con.createStatement();
		stmt.execute("ALTER TABLE setting ALTER COLUMN currency_sym VARCHAR(20);");
		stmt.getConnection().commit();
		stmt.close();
		con.close();
		
		// test if sql script has been successful
		con = getDataSource(false, copyTo.getAbsolutePath()).getConnection();
		stmt = con.createStatement();
		stmt.execute("UPDATE SETTING SET currency_sym='1234567890';");
		stmt.close();
		con.close();
		
		// no execptions have been thrown
		assertTrue(true);
	}
	
	private DataSource getDataSource(boolean isVersion18, String dbPath) {
		String url = "jdbc:hsqldb:"+dbPath+"/riv;hsqldb.lock_file=false;";
		if (isVersion18) {
			org.fao.riv.hsqldb18.jdbc.jdbcDataSource ds = new org.fao.riv.hsqldb18.jdbc.jdbcDataSource();
			ds.setDatabase(url);
			ds.setUser("sa");
			ds.setPassword("");
			return ds;
		} else {
			org.hsqldb.jdbc.JDBCDataSource ds= new org.hsqldb.jdbc.JDBCDataSource();
			ds.setDatabase(url);
			ds.setUser("sa");
			ds.setPassword("");
			return ds;
		}
	}
}