package org.fao.riv.installer.sql;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class UpdateSql  {
	private static String url;
	private static String installPath;
	private static String sqlPath;
	private static String build;
	private static final String insertIntoVersion = "INSERT INTO VERSION " +
								"(version, description, install_time, recalculate) " +
								"VALUES (%s, '%s', CURRENT_TIMESTAMP, true)";
	double current = -1;
	Connection connection = null;
	
	public boolean run(AbstractUIProcessHandler uih, String[] args) {
		
		uih.logOutput("Updating database structure.", false);
		installPath = args[0].replace("\\", "/");
		sqlPath = args[1].replace("\\", "/");
		boolean isRiv3 = args[2].trim().equals("true");
		build = args[3];
		
		url = String.format("jdbc:hsqldb:file:%s/webapp/WEB-INF/data/riv;hsqldb.lock_file=false;", installPath);
		
		System.out.println("installPath: "+installPath);
		System.out.println("sql path: "+sqlPath);
		System.out.println("isRiv3: "+Boolean.toString(isRiv3));
		System.out.println("build: "+build);
		
		boolean result=execute(isRiv3, uih);
		if (result) {
			System.out.println("Finished updating database structure.");
		}
		return result;
	}
	
	private boolean migrateDb(AbstractUIProcessHandler uih) {
		try {
			// close well with hsqldb 1.8
			System.out.println("Closing well with HSQLDB 1.8");
			Statement stmt=connection.createStatement();
			stmt.execute("SHUTDOWN SCRIPT");
			stmt.close();
			connection.close();
			
			// open and close with hsqldb 2.3
			System.out.println("Opening and closing with HSQLDB 2.3.1");
			connection = getDataSource(false).getConnection();
			stmt = connection.createStatement();
			stmt.execute("SHUTDOWN");
			stmt.close();
			connection.close();
			
			// make fresh connection using HSQLDB 2.3.1
			System.out.println("Reopening new HSQLB 2.3.1 connection");
			connection = getDataSource(false).getConnection();

			return true;
		} catch (SQLException e) {
			logError(uih, e);
			return false;
		}
	}
	
	private DataSource getDataSource(boolean useHsqldb18) {
		if (useHsqldb18) {
			org.fao.riv.hsqldb18.jdbc.jdbcDataSource ds = new org.fao.riv.hsqldb18.jdbc.jdbcDataSource();
			System.out.println(ds.getClass());
			ds.setDatabase(url);
			ds.setUser("sa");
			ds.setPassword("");
			return ds;
		} else {
			org.hsqldb.jdbc.JDBCDataSource ds= new org.hsqldb.jdbc.JDBCDataSource();
			System.out.println(ds.getClass());
			ds.setDatabase(url);
			ds.setUser("sa");
			ds.setPassword("");
			return ds;
		}
	}
	
	private boolean execute(boolean isRiv3, AbstractUIProcessHandler uih) {
		boolean result=true;
		try {
			JAXBContext context = JAXBContext.newInstance(SQL.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			JAXBElement<SQL> element = unmarshaller.unmarshal(new StreamSource(sqlPath), SQL.class);
			SQL sql = element.getValue();
			
			try {
				connection = getDataSource(isRiv3).getConnection();
			} catch (SQLException e) {
				logError(uih, e);
				result=false;
			}
			
			getCurrentVersion();

			// BUG FIX: new install of 3.0 mistakenly registered as 2.2
			if (Double.compare(current, 2.2)==0) {
				try {
					Statement stmt = connection.createStatement();
					stmt.execute("SELECT * FROM SETTING WHERE ADMIN1_TITLE=''"); // if really 2.2 will throw exception
					stmt.execute(String.format(insertIntoVersion, "3.0", "not yet used"));
					stmt.close();
					current=3.0;
				} catch (Exception e) { } 
			} else if (Double.compare(current, 4.0)==0) {
				try {
					Statement stmt = connection.createStatement();
					stmt.execute("SELECT * FROM project_block WHERE cycles IS FALSE"); // if really 4.0 will throw exception
					stmt.execute("UPDATE project_item SET year_begin=1 WHERE class=5 AND year_begin IS NULL"); // year_begin missing in sql command RIV4.1.1 and 4.1.2
					stmt.execute(String.format(insertIntoVersion, "4.1", "4.1"));
					stmt.close();
					current=4.1;
				} catch (Exception e) { } 
			}
			
			for (Version version : sql.getVersions()) {
				if (version.getVersionNumber() > current) {
					System.out.println("Upgrading to: "+version.getVersionNumber());
					Statement stmt = connection.createStatement();
					String[] queries = version.getQuery().replace("&gt;", ">").replace("&lt;", "<").split("\n");
					for (String q : queries) {
						if (!q.trim().startsWith("--") && !q.trim().isEmpty()) {
							try {
								System.out.println(q);
								System.out.println(stmt.execute(q));
							} catch (Exception e) {
								uih.logOutput("SQLException:"+e.getLocalizedMessage(), false);
								uih.emitError("Error updating database.", "Error updating database, see log for more information.");
								uih.finishProcess();
								result=false;
							}
						}
					}
					stmt.execute(String.format(insertIntoVersion, version.getVersionNumber(),
											   version.getVersionNumber()));
					stmt.getConnection().commit();
					stmt.close();
					System.out.println("Completed upgrade to: "+version.getVersionNumber());
					
					current=version.getVersionNumber();
					
					if (current==3.9) { // here we migrate from HSQLDB 1.8 to 2.3
						result = migrateDb(uih);
					}
				}
			}
			Statement stmt = connection.createStatement();
			stmt.execute(String.format("UPDATE version v SET v.build='%s' WHERE v.version=(SELECT MAX(vv.version) FROM version vv)", build));
			stmt.getConnection().commit();
			stmt.close();
			System.out.println("build # " + build);
		} catch (Exception e) {
			logError(uih, e);
			result=false;
		} finally {
			System.out.println("final close of db");
			close(uih);
		}
		return result;
	}
	
	private void getCurrentVersion() throws SQLException {
		if (current == -1) {
			Statement stmt = null;
			try {
				stmt = connection.createStatement();
				String query = "SELECT max(version) as CURRENT FROM VERSION";
				ResultSet rs = stmt.executeQuery(query);
				if (rs.next()) { // we can only have one max
					current = rs.getDouble("CURRENT");
				}
				System.out.println("Current db version: "+current);
				
			} catch (SQLException sqle) { // oops the database isn't yet updated.
											// So we assume it's version 1
				if (stmt != null) {
					String query = "CREATE CACHED TABLE version ("
							+ "version DECIMAL(4,2) NOT NULL PRIMARY KEY, "
							+ "description VARCHAR(255), "
							+ "install_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
							+ "recalculate BOOLEAN NOT NULL);";
					stmt.execute(query);
					stmt.getConnection().commit();
					current = 0;
					stmt.close();
				} else {
					System.out.println("Unable to create VERSION table");
					throw new SQLException("Unable to create VERSION table", sqle);
				}
			}
		}
	}
		
	private void close(AbstractUIProcessHandler uih) {
		try {
			if (connection != null && !connection.isClosed()) {
				System.out.println("Closing open connection");
				Statement stmt = connection.createStatement();
					stmt.execute("SHUTDOWN COMPACT");
					stmt.close();
					connection.close();

				new File(String.format("%s/webapp/WEB-INF/data/riv.lck", installPath)).delete();
			}
		} catch (Exception e) {
			logError(uih, e);
		}
	}
	
	private void logError(AbstractUIProcessHandler uih, Exception e) {
		e.printStackTrace(System.out);
		e.printStackTrace(System.err);
		
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String stackTrace = sw.toString(); // stack trace as a string
		
		uih.logOutput("Error updating database. "+ stackTrace, true);
		uih.emitError("We apologize for the difficulty.", "Error updating database. Please contact the RuralInvest team at TCI-Ruralinvest@fao.org and include the error message displayed in this screen.");
		uih.finishProcess();
	}
}