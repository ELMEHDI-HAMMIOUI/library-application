package modele.dao.implementations;

import java.util.List;  
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.*;

import modele.beans.Bibliothecaire;
import modele.dao.interfaces.BibliothecaireDao;
import modele.dao.configuration.DataBase;

public class BibliothecaireDaoImp  implements BibliothecaireDao {
    private String BibliothecaireFilePath = DataBase.getBibliothecaireCsvFilePath();
    private String tmpFile = DataBase.getTmpBibliothecaireFilePath();

    @Override
    public boolean ajouterBibliothecaire(Bibliothecaire user) {
        try (FileWriter fw = new FileWriter(BibliothecaireFilePath, true)) {
            String line = getNextId() + ";" +
                    user.getNom() + ";" +
                    user.getPrenom() + ";" +
                    user.getCni() + ";" +
                    user.getSexe() + ";" +
                    user.getAdresse() + ";" +
                    user.getTelephone() + ";" +
                    user.getDateEmbauche() + ";" +
                    user.getSalaire() + ";" +
                    user.getEmail() + ";" +
                    hashPassword(user.getPassword()) + "\n";

            fw.write(line);
            fw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimerBibliothecaire(int id) {
        File inputFile = new File(BibliothecaireFilePath);
        File tempFile = new File(tmpFile);

        boolean isDeleted = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            boolean isHeader = true;

            while ((line = reader.readLine()) != null) {
                if (isHeader) {
                    writer.write(line);
                    writer.newLine();
                    isHeader = false;
                    continue;
                }

                String[] columns = line.split(";");
                int currentId = Integer.parseInt(columns[0]);

                if (currentId != id) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    isDeleted = true;
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        if (isDeleted) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                System.err.println("Failed to replace the original file.");
                return false;
            }
        } else {
            tempFile.delete();
        }

        return isDeleted;
    }

    @Override
    public boolean modifierBibliothecaire(Bibliothecaire b) {
    	File inputFile = new File(BibliothecaireFilePath); 
	    File tempFile = new File(tmpFile); 
	    boolean isUpdated = false;
	    String hashedPassword="";
	    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
			
			String line;
			line = reader.readLine();
			writer.write("id;nom;prenom;cni;sexe;adresse;telephone;dateEmbauche;salaire;email;password\n");
			while ((line = reader.readLine()) != null) {
			    String[] columns = line.split(";");
			    
			    if (Integer.parseInt(columns[0]) ==b.getId()) {
			    	if(b.getPassword().trim().isEmpty()) {
			    		hashedPassword=columns[10];
			    	}else {
			    		hashedPassword=hashPassword(b.getPassword());
			    	}
			        columns[1] =b.getNom();
			        columns[2] =b.getPrenom();
			        columns[3] =b.getCni();
			        columns[4] =b.getSexe();
			        columns[5] =b.getAdresse();
			        columns[6] =b.getTelephone();
			        columns[7] = b.getDateEmbauche();
			        columns[8] =""+b.getSalaire();
			        columns[9] =b.getEmail();
			        columns[10] =hashedPassword;
			
			        isUpdated = true; 
			    }
			
			    writer.write(String.join(";", columns));
			    writer.newLine();
			        }
			    } catch (IOException | NumberFormatException e) {
			        e.printStackTrace();
			        return false;
			    }
			
			    if (isUpdated) {
			        if (inputFile.delete() && tempFile.renameTo(inputFile)) {
			            return true; 
			        } else {
			            System.err.println("Échec du remplacement du fichier original.");
			            tempFile.delete(); 
			            return false;
			        }
			    } else {
			        tempFile.delete();
			        return false;
			    }	 
    }

    @Override
    public List<Bibliothecaire> getAllBibliothecaires() {
        List<Bibliothecaire> bibliothecaires = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(BibliothecaireFilePath))) {
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                Bibliothecaire biblio = new Bibliothecaire(
                        Integer.parseInt(cols[0]),
                        cols[1],
                        cols[2],
                        cols[3],
                        cols[4],

                        cols[5],
                        cols[6],
                        cols[7],
                        Double.parseDouble(cols[8]),
                        cols[9],
                        cols[10]
                );
                bibliothecaires.add(biblio);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bibliothecaires;
    }

    @Override
    public Bibliothecaire getBibliothecaireById(int id) {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(BibliothecaireFilePath))) {
            br.readLine(); 

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                if (Integer.parseInt(cols[0]) == id) {
                    return new Bibliothecaire(
                            Integer.parseInt(cols[0]),
                            cols[1],
                            cols[2],
                            cols[3],
                            cols[4],
                            cols[5],
                            cols[6],
                            cols[7],
                            Double.parseDouble(cols[8]),
                            cols[9],
                            cols[10]
                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public int checkLogin(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(BibliothecaireFilePath))) {
            br.readLine(); // Skip header
            String line;

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                if (cols[9].equals(email) && cols[10].equals(hashPassword(password))) {
                    return Integer.parseInt(cols[0]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    private int getNextId() {
        int nextId = 1;
        int maxId = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(BibliothecaireFilePath))) {
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
