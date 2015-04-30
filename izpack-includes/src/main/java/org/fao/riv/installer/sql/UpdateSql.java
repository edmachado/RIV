package org.fao.riv.installer.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class UpdateSql  {
	private static String url;
	private static String installPath;
	private static String sqlPath;
	double current = -1;
	Connection connection = null;
	
	public void run(AbstractUIProcessHandler uih, String[] args) {
		uih.logOutput("Updating database structure.", false);
		installPath = args[0].replace("\\", "/");
		sqlPath = args[1].replace("\\", "/");
		boolean isRiv3 = args[2].trim().equals("true");
		
		url = String.format("jdbc:hsqldb:file:%s/webapp/WEB-INF/data/riv;hsqldb.lock_file=false;", installPath);
		
		System.out.println("installPath: "+installPath);
		System.out.println("sql path: "+sqlPath);
		System.out.println("isRiv3: "+args[2]);
		
		execute(isRiv3);
		System.out.println("Finished updating database structure.");
	}
	
	private void migrateDb() {
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
		} catch (SQLException e) {
			e.printStackTrace(System.out);
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
	
	private void execute(boolean isRiv3) {
		try {
			JAXBContext context = JAXBContext.newInstance(SQL.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();

			JAXBElement<SQL> element = unmarshaller.unmarshal(new StreamSource(sqlPath), SQL.class);
			SQL sql = element.getValue();
			
			try {
				connection = getDataSource(isRiv3).getConnection();
			} catch (SQLException e) {
				e.printStackTrace(System.out);
			}
			
			getCurrentVersion();
			
			// BUG FIX: new install of 3.0 mistakenly registered as 2.2
			if (Double.compare(current, 2.2)==0) {
				try {
					Statement stmt = connection.createStatement();
					stmt.execute("SELECT * FROM SETTING WHERE ADMIN1_TITLE=''"); // if really 2.2 will throw exception
					stmt.execute("INSERT INTO VERSION (version, description, install_time, recalculate) VALUES (3.0, 'not yet used', CURRENT_TIMESTAMP, true)");
					stmt.close();
					current=3.0;
				} catch (Exception e) { } 
			} else if (Double.compare(current, 4.0)==0) {
				try {
					Statement stmt = connection.createStatement();
					stmt.execute("SELECT * FROM project_block WHERE cycles IS FALSE"); // if really 4.0 will throw exception
					stmt.execute("UPDATE project_item SET year_begin=1 WHERE class=5 AND year_begin IS NULL"); // year_begin missing in sql command RIV4.1.1 and 4.1.2
					stmt.execute("INSERT INTO version (version, description, install_time, recalculate) VALUES (4.1, '4.1', CURRENT_TIMESTAMP, true)");
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
								e.printStackTrace(System.out);
							}
						}
					}
					stmt.execute(String.format(
							"INSERT INTO VERSION (version, description, install_time, recalculate) "+
							"VALUES (%s, '%s', CURRENT_TIMESTAMP, true)",
							version.getVersionNumber(), version.getVersionNumber()));
					stmt.getConnection().commit();
					stmt.close();
					System.out.println("Completed upgrade to: "+version.getVersionNumber());
					
					current=version.getVersionNumber();
					
					if (current==3.9) { // here we migrate from HSQLDB 1.8 to 2.3
						migrateDb();
					}
				}
			}
		} catch (SQLException sqle) {
			System.out.println("SQLException:"+sqle.getLocalizedMessage());
			log(sqle, "SQLException:"+sqle.getLocalizedMessage());
		} catch (JAXBException jaxbe) {
			System.out.println("JAXBException:"+jaxbe.getLocalizedMessage());
			log(jaxbe, "JAXBException:"+jaxbe.getLocalizedMessage());
		} finally {
			System.out.println("final close of db");
			close();
		}
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
				
			} catch (SQLException sqle) { // ops the database isn't yet updated.
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
		
	private void close() {
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
			e.printStackTrace(System.out);
		}
	}
	
	private void log(Exception e, String message) {
		e.printStackTrace(System.out);
	}
}