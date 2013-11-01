package org.fao.riv.installer.actions;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
	}
	
	public void run(AbstractUIProcessHandler uih, String[] args) {
		uih.logOutput("Adding first user.", false);
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
		
		try {
			execute(user);
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	private void execute(FirstUserData user) throws Exception {
		// Encrypt password
		String passwordHash = null;
		try {
			passwordHash = encryptSHA1(user.password);
		} catch (NoSuchAlgorithmException e) {
			throw (e);
		} catch (UnsupportedEncodingException e) {
			throw (e);
		}

		try {
			// delete riv.lck
			File dblock = new File(user.webroot+"/riv.lck");
			dblock.delete();
			
			Class.forName("org.hsqldb.jdbcDriver").newInstance();
			System.out.println(String.format("Attempting to write to database: %s\n", user.webroot).getBytes());

			Connection conn = DriverManager.getConnection("jdbc:hsqldb:file:" + user.webroot);

			try {
				Statement delete = conn.createStatement();
				delete.executeUpdate("DELETE FROM User");
				delete.close();
			} catch (SQLException sqle) {
				System.out.println(String.format("Cannot delete existing users: %s", sqle.getMessage()).getBytes());
			}

			// add new user
			String sql = "INSERT into User (username, description, password, organization, location, telephone, email, administrator, lang) "
					+ "values (?,?,?,?,?,?,?,true,?)";
			System.out.println(sql.getBytes());
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, user.username);
			st.setString(2, user.name);
			st.setString(3, passwordHash);
			st.setString(4, user.organization);
			st.setString(5, user.location);
			st.setString(6, user.telephone);
			st.setString(7, user.email);
			st.setString(8, user.language);

			int result = st.executeUpdate();
			System.out.println(String.format("Result of insert: %d", result).getBytes());
			st.close();
			
			Statement close = conn.createStatement();
           close.executeUpdate("SHUTDOWN COMPACT;");
           close.close();
               
			conn.close();
		} catch (InstantiationException ie) {
			ie.printStackTrace();
			throw ie;
		} catch (IllegalAccessException iae) {
			iae.printStackTrace();
			throw iae;
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
			throw cnfe;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			throw sqle;
		}
	}
	
	private String encryptSHA1(String text) throws NoSuchAlgorithmException,
		UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-1");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("UTF8"), 0, text.length());//"iso-8859-1"
		sha1hash = md.digest();
		return convertToHex(sha1hash);
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