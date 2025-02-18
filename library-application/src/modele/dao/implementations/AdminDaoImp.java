package modele.dao.implementations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import modele.dao.configuration.DataBase;
import modele.dao.interfaces.AdminDao;

public class AdminDaoImp implements AdminDao {

    private String AdminFilePath = DataBase.getAdminCsvFileCsvPath();
   // private String AdmintmpFile = DataBase.getTmpAdminFilePath(); 
    
	@Override
	public int checkLogin(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(AdminFilePath))) {
            br.readLine(); // Skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                if (cols[1].equals(username) && cols[2].equals(hashPassword(password))) {
                    return Integer.parseInt(cols[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
	}
	 public boolean ajouterAdmin(String username,String password) {
	        try (FileWriter fw = new FileWriter(AdminFilePath, true)) {
	            String line = getNextId() + ";"+ username + ";"+hashPassword(password)+"\n"; 
	            fw.write(line);
	            fw.flush();
	            return true;
	        } catch (IOException e) {
	            e.printStackTrace();
	            return false;
	        }
	    }

	    private int getNextId() {
	        int nextId = 1;
	        int maxId = 0;

	        try (BufferedReader br = new BufferedReader(new FileReader(AdminFilePath))) {
	            br.readLine(); 
	            String line;

	            while ((line = br.readLine()) != null) {
	                int id = Integer.parseInt(line.split(";")[0]);
	                if (id > maxId) {
	                    maxId = id;
	                }
	            }
	            nextId = maxId + 1;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        return nextId;
	    }
	 private String hashPassword(String password) {
	        try {
	            MessageDigest md = MessageDigest.getInstance("SHA-256");
	            byte[] hashedBytes = md.digest(password.getBytes());
	            return Base64.getEncoder().encodeToString(hashedBytes);
	        } catch (NoSuchAlgorithmException e) {
	            e.printStackTrace();
	        }
	        return password;
	}

}
