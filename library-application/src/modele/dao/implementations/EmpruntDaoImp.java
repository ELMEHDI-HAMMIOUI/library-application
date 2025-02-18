package modele.dao.implementations;

import modele.beans.Emprunt;
import modele.beans.Livre;
import modele.dao.Factories.CsvFactory;
import modele.dao.configuration.DataBase;
import modele.dao.interfaces.EmpruntDao;
import modele.dao.interfaces.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmpruntDaoImp implements EmpruntDao {
    private String empruntFilePath = DataBase.getEmpruntFilePath();
    private LivreDao ldao = CsvFactory.getLivreDao();
    private MembreDao mdao = CsvFactory.getMembreDao();

    @Override
    public boolean emprunterLivre(Emprunt emprunt) {
        String isbn = emprunt.getIsbn();
        
        int idEmprunt = getNextId();

        String csvEntry = 
                idEmprunt + ";" +
                isbn + ";" +
                emprunt.getIdMembre() + ";" +
                emprunt.getDateEmprunt() + ";" +
                emprunt.getDateRetourPrevue()+ ";" + 
                emprunt.getDateRetourEffective()+ ";" +
                emprunt.getTarif()+ ";" +
                emprunt.getPenalite()+ "\n";
                
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(empruntFilePath, true))) {
            writer.write(csvEntry);
        } catch (IOException e) {
            System.err.println("Une erreur s'est produite lors l'eregistrement d'emprunte : " + e.getMessage());
            return false; 
        }
        //pour decrementer nbr des exemplaire 
        ldao.livreEstEmprunter(isbn);

        return true;
    }
  

    @Override
    public List<Emprunt> getAllEmprunte() {
        List<Emprunt> emprunts = new ArrayList<>();
        String[] fields;
        try (BufferedReader br = new BufferedReader(new FileReader(empruntFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
            	fields=line.split(";");
                Emprunt emprunt = new Emprunt();
                emprunt.setIdEmprunt(Integer.parseInt(fields[0]));
                emprunt.setIsbn(fields[1]);
                emprunt.setIdMembre(Integer.parseInt(fields[2]));
                emprunt.setDateEmprunt(fields[3]);
                emprunt.setDateRetourPrevue(fields[4]);
                emprunt.setDateRetourEffective(fields[5]);
                emprunt.setTarif(Double.parseDouble(fields[6]));
                emprunt.setPenalite(Double.parseDouble(fields[7]));
                emprunts.add(emprunt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return emprunts;
    }

    //recuperer tous les emprunte d'un livre pour savoir s'il exeist des exemplere non retourner afin de s'avoir si on peut supprimer 
    //ce livre ou non (en cahire de charge (regle N°** on peut pas supprimer un livre si il il exeist un exemplaire non retourner))
    @Override
    public List<Emprunt> getAllEmprunteByISBN(String isbn) {
        List<Emprunt> emprunts = new ArrayList<>();
        for (Emprunt emprunt : getAllEmprunte()) {
            if (emprunt.getIsbn().equals(isbn)) {
                emprunts.add(emprunt);
            }
        }
        return emprunts;
    }

    //utilisation lors de l upgrade de l app (l'ajou de l'historique en membre interface )
    @Override
    public List<Emprunt> getAllEmprunteByMembreId(String id_membre) {
        List<Emprunt> emprunts = new ArrayList<>();
        for (Emprunt emprunt : getAllEmprunte()) {
            if (String.valueOf(emprunt.getIdMembre()).equals(id_membre)) {
                emprunts.add(emprunt);
            }
        }
        return emprunts;
    }

    @Override
    public Emprunt getEmprunteById(int id_emprunt) {
        for (Emprunt emprunt : getAllEmprunte()) {
            if ( emprunt.getIdEmprunt() == id_emprunt) {
                return emprunt;
            }
        }
        return null; 
    }


    
    @Override
    public boolean marquerRetour(Emprunt emprunt) {
        String empruntFilePath = DataBase.getEmpruntFilePath();
        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(empruntFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(";");
                if (columns.length > 0 && Integer.parseInt(columns[0]) == emprunt.getIdEmprunt()) {
                    String newLine = String.join(";", 
                        String.valueOf(emprunt.getIdEmprunt()),
                        emprunt.getIsbn(),
                        String.valueOf(emprunt.getIdMembre()),
                        emprunt.getDateEmprunt(),
                        emprunt.getDateRetourPrevue(),
                        emprunt.getDateRetourEffective(),
                        String.valueOf(emprunt.getTarif()),
                        String.valueOf(emprunt.getPenalite())
                    );
                    lines.add(newLine);
                    found = true; 
                } else {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; 
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(empruntFilePath))) {
        	String entete="id_emprunt;ISBN;id_membre;DateEmprunt;DateRetourPrevue;DateRetourEffective;tarif;penalite";
            bw.write(entete);
            bw.newLine();
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false; 
        }
        //si la modification est faite correctement en incremente le nbr des emplaire  par la mehtode livreEstRetourner
        if(found) {
        	ldao.livreEstRetourner(emprunt.getIsbn());
        }
        return found; 
    }
    
    
    
    

	@Override
	 public int getNextId() {
       int maxId = 0;

       try (BufferedReader br = new BufferedReader(new FileReader(empruntFilePath))) {
           String line;
           br.readLine();

           while ((line = br.readLine()) != null) {
               String[] fields = line.split(";");
               if (fields.length > 0) {
                   try {
                       int idEmprunt = Integer.parseInt(fields[0]);
                       if (idEmprunt > maxId) {
                           maxId = idEmprunt; 
                       }
                   } catch (NumberFormatException e) {
                       System.err.println("Invalid ID format: " + fields[0]);
                   }
               }
           }
       } catch (IOException e) {
           System.err.println("Error reading the file: " + e.getMessage());
       }

       return maxId + 1; 
   }
}