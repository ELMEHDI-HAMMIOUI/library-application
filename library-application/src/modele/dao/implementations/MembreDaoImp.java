package modele.dao.implementations;

import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import modele.beans.Membre;
import modele.dao.interfaces.MembreDao;
import modele.dao.configuration.*;

public class MembreDaoImp implements MembreDao {
	private String MembreFilePath=DataBase.getMembreCsvFilePath();
	private String tmpfile=DataBase.getTmpMembresFilePath();

	@Override
	public boolean ajouterMembre(Membre user)   {
		try (FileWriter fw = new FileWriter(MembreFilePath, true))  {
			String line = getNextId() + ";" +
                          user.getNom() + ";" +
                          user.getPrenom() + ";" +
                          user.getCni() + ";" +
                          user.getSexe() + ";" +
                          user.getDate_n() + ";" +
                          user.getEmail() + ";" +
                          hashPassword(user.getPaswd())+ ";" +
                          user.getN_tele() + ";" +
                          user.getAddresse() + ";" +
                          user.getDate_inscription() + ";" +
                          user.getStatus() + ";"+
						  user.getPhoto() + "\n";

            fw.write(line); 
            fw.flush();
            fw.close();
            
            return true; 
        } catch (IOException e) {
            e.printStackTrace();
        	return false; 
        }
	}

	//cette methode permet de copier tous element de fichier Membre.csv sauf la ligne de l id volue dans un fichier tmpFile 
	//puis reecrire tous les donnée de tmpFile en MembreFIle
	@Override
	public boolean supprimerMembre(int id) {
		File inputFile = new File(MembreFilePath);
	    File tempFile = new File(tmpfile); 

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
	
	public boolean modifierMembre(Membre m) {
	    File inputFile = new File(MembreFilePath); // Fichier original
	    File tempFile = new File(tmpfile); // Fichier temporaire
	    boolean isUpdated = false;
	    String hashedPassword="";
	    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
	         BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

	        String line;
	        line = reader.readLine();
	        writer.write("id_membre;nom;prenom;cni;sexe;date_n;email;paswd;N_tele;addresse;date_inscription;status\n");
	        while ((line = reader.readLine()) != null) {
	            String[] columns = line.split(";");

	            if (Integer.parseInt(columns[0]) == m.getId_membre()) {
	            	if(m.getPaswd().trim().isEmpty()) {
	            		hashedPassword=columns[7];
	            	}else {
	            		hashedPassword=hashPassword(m.getPaswd());
	            	}
	                columns[1] = m.getNom();
	                columns[2] = m.getPrenom();
	                columns[3] = m.getCni();
	                columns[4] = m.getSexe();
	                columns[5] = m.getDate_n();
	                columns[6] = m.getEmail();
	                columns[7] = hashedPassword;
	                columns[8] = m.getN_tele();
	                columns[9] = m.getAddresse();
	                columns[10] = m.getDate_inscription();
	                columns[11] = m.getStatus();
	                columns[12] = m.getPhoto();
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
	public List<Membre> getAllMembres() {
		List<Membre> membres =new ArrayList<Membre>();
		String line;
		String[] cols;
		Membre mbr;
		try {
			FileReader fr=new FileReader(MembreFilePath);
			BufferedReader br=new BufferedReader(fr);
			line=br.readLine();
			while((line=br.readLine())!=null) {
				cols=line.split(";");
				mbr=new Membre(
						 Integer.parseInt(cols[0]),                 
					        cols[1],                                   
					        cols[2],  									
					        cols[3],                                   
					        cols[4],                                   
					        cols[5],       			
					        cols[6],                                   
					        cols[7],                                   
					        cols[8],                                   
					        cols[9],                                   
					        cols[10],        			
					        cols[11] ,					        
					        cols[12]                            
						);
				membres.add(mbr);
			}
			br.close();
			fr.close();
			return membres;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Membre getMembreById(int id) {
		FileReader fr;
		BufferedReader br;
		String line;
		String[] cols;
		Membre mbr = null;
		try {
			fr=new FileReader(MembreFilePath);
			br=new BufferedReader(fr);
			line=new String(br.readLine());
			while((line=br.readLine()) !=null) {
				cols=line.split(";");
				if(Integer.parseInt(cols[0])==id) {
					mbr=new Membre( 
							 Integer.parseInt(cols[0]),                 // id_membre
						        cols[1],                                   // nom
						        cols[2],  								// prenom
						        cols[3],                                   // cni
						        cols[4],                                   // sexe
						        cols[5],       // date_n
						        cols[6],                                   // email
						        cols[7],                                   // paswd (hashed or encrypted, if applicable)
						        cols[8],                                   // N_tele
						        cols[9],                                   // addresse
						        cols[10],       // date_inscription
						        cols[11] ,
						        cols[12]                                    // status

					        );		
					break;
				}
			}
			br.close();
			fr.close();
			return mbr;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return null;
	}

	@Override
	public int checkLogin(String email, String password) {
		FileReader fr;
		BufferedReader br;
		String line;
		String[] cols;
		int id_membre=-1;
		try {
			fr=new FileReader(MembreFilePath);
			br=new BufferedReader(fr);
			line=new String(br.readLine());
			while((line=br.readLine()) !=null) {
				cols=line.split(";");
				if(cols[4].equals(email)) {
					if(cols[5].equals(hashPassword(password))) {
						id_membre=Integer.parseInt(cols[0]);
					}
				}
			}
			br.close();
			fr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return id_membre;
		
	}
	
	public int getNextId() {
	    int nextId = 1; // Starting ID
	    int maxId = 0;
	    String line;

	    try {
	        FileReader fr = new FileReader(MembreFilePath);
	        BufferedReader br = new BufferedReader(fr);
	        br.readLine();

	        while ((line = br.readLine()) != null) {
	            String[] columns = line.split(";");
	            int id = Integer.parseInt(columns[0]); 

	            if (id > maxId) {
	                maxId = id;
	            }
	        }

	        nextId = maxId + 1;
	        br.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
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
		}catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return password;
	    
	}
}
