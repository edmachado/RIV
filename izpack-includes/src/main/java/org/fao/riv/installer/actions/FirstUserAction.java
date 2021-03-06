package org.fao.riv.installer.actions;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.izforge.izpack.panels.process.AbstractUIProcessHandler;

public class FirstUserAction {
	
	private class FirstUserData {
		public String webroot;
		public String username;
		public String name;
		public String password;
		public String organization;
		public String location;
		public String telephone;
		public String email;
		public String language;
		public boolean newInstall;
	}
	
	public void run(AbstractUIProcessHandler uih, String[] args) {
		FirstUserData user = new FirstUserData();
		user.webroot = String.format("%s/webapp/WEB-INF/data/riv", args[0]);
		user.username = args[1];
		user.name = args[2];
		user.password = args[3];
		user.organization = args[4];
		user.location = args[5];
		user.telephone = args[6];
		user.email = args[7];
		user.language = args[8];
		user.newInstall = args[9].equalsIgnoreCase("true");

		if (user.newInstall) {
			uih.logOutput("Adding first user.", false);
		} else {
			uih.logOutput("Adding new user.", false);
		}
		try {
			execute(user);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	private void execute(FirstUserData user) throws Exception {
		Connection conn=null;
		
		try {
			// delete riv.lck
			File dblock = new File(user.webroot+"/riv.lck");
			dblock.delete();
			
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			System.out.println(String.format("Attempting to write to database: %s\n", user.webroot).getBytes());

			conn = DriverManager.getConnection("jdbc:hsqldb:file:" + user.webroot);

			if (user.newInstall) {
				try (Statement delete = conn.createStatement()){
					delete.executeUpdate("DELETE FROM User");
					delete.close();
				} catch (SQLException sqle) {
					System.out.println(String.format("Cannot delete existing users: %s", sqle.getMessage()).getBytes());
				}
			}

			String sqlSelect = "SELECT user_id from User where username = ?";
			
			ResultSet rs=null;
			try (PreparedStatement prepareSelect = conn.prepareStatement(sqlSelect);) {
				prepareSelect.setString(1, user.username);
	
				rs  = prepareSelect.executeQuery();
				if (rs.next()) {
					String sql = "UPDATE User set username = ?, description = ?, password = ?, "
								 + "organization = ?, location = ?, telephone = ?, email = ?, "
								 + "administrator = true, lang = ? where user_id = ?";
					try (PreparedStatement st = conn.prepareStatement(sql);) {
						st.setString(1, user.username);
						st.setString(2, user.name);
						st.setString(3, computeSha1OfByteArray(user.password.getBytes("UTF8")));
						st.setString(4, user.organization);
						st.setString(5, user.location);
						st.setString(6, user.telephone);
						st.setString(7, user.email);
						st.setString(8, user.language);
						st.setInt(9, rs.getInt("user_id"));
		
						int result = st.executeUpdate();
						System.out.println(String.format("Result of update: %d", result).getBytes());
					}
				} else {
					// add new user
					String sql = "INSERT into User (username, description, password, organization, location, telephone, email, administrator, lang) "
								 + "values (?,?,?,?,?,?,?,true,?)";
					System.out.println(sql.getBytes());
					
					try (PreparedStatement st = conn.prepareStatement(sql);) {
						st.setString(1, user.username);
						st.setString(2, user.name);
						st.setString(3, computeSha1OfByteArray(user.password.getBytes("UTF8")));
						st.setString(4, user.organization);
						st.setString(5, user.location);
						st.setString(6, user.telephone);
						st.setString(7, user.email);
						st.setString(8, user.language);
		
						int result = st.executeUpdate();
						System.out.println(String.format("Result of insert: %d", result).getBytes());
					}
				}
			} finally {
				try { if (rs!=null) { rs.close(); } } catch (Exception e) { /* ignore */ }
			}

			try (Statement close = conn.createStatement();) {
				close.executeUpdate("SHUTDOWN COMPACT;");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			try { if (conn!=null) { conn.close(); }} catch (Exception e){ /* ignore */ }
		}
	}
	
	private String computeSha1OfByteArray(final byte[] message)
		    throws UnsupportedOperationException {
		        try {
		            MessageDigest md = MessageDigest.getInstance("SHA-1");
		            md.update(message);
		            byte[] res = md.digest();
		            return convertToHex(res);
		        } catch (NoSuchAlgorithmException ex) {
		            throw new UnsupportedOperationException(ex);
		        }
		}
	
	private String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}