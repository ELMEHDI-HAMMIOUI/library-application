package modele.dao.implementations;

import java.io.*; 
import java.util.*;

import modele.beans.*;
import modele.dao.configuration.DataBase;
import modele.dao.interfaces.LivreDao;


public class LivreDaoImp implements LivreDao {
    private String livreFilePath =DataBase.getLivreCsvFilePath();
    private String tmpFile = "src/dateBase/tmp_livres.csv";

    @Override
    public boolean ajouterLivre(Livre livre) {
        try (FileWriter fw = new FileWriter(livreFilePath, true)) {
            String line = livre.getTitre() + ";" +
                          livre.getAuteur() + ";" +
                          livre.getGenre() + ";" +
                          livre.getIsbn() + ";" +
                          livre.getAnneePublication() + ";" +
                          livre.getNombreExemplaires() + ";" +
                          livre.getTarifEmpruntParJour() + ";" +
                          livre.getPenaliteParJour() + ";"+
                          livre.getImagePath()+"\n";

            fw.write(line);
            fw.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimerLivre(String isbn) {
        File inputFile = new File(livreFilePath);
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
                if (!columns[3].equals(isbn)) {
                    writer.write(line);
                    writer.newLine();
                } else {
                    isDeleted = true;
                }
            }
        } catch (IOException e) {
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
    public boolean modifierLivre(Livre livre) {
        File inputFile = new File(livreFilePath);
        File tempFile = new File(tmpFile);
        boolean isUpdated = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            line = reader.readLine();
            writer.write("titre;auteur;genre;isbn;anneePublication;nombreExemplaires;tarifEmpruntParJour;penaliteParJour\n");

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(";");

                if (columns[3].equals(livre.getIsbn())) {
                    columns[0] = livre.getTitre();
                    columns[1] = livre.getAuteur();
                    columns[2] = livre.getGenre();
                    columns[4] = String.valueOf(livre.getAnneePublication());
                    columns[5] = String.valueOf(livre.getNombreExemplaires());
                    columns[6] = String.valueOf(livre.getTarifEmpruntParJour());
                    columns[7] = String.valueOf(livre.getPenaliteParJour());

                    isUpdated = true;
                }

                writer.write(String.join(";", columns));
                writer.newLine();
            }
        } catch (IOException e) {
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
    public List<Livre> getAllLivres() {
        List<Livre> livres = new ArrayList<>();
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(livreFilePath))) {
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                Livre livre = new Livre(
                    cols[0], // titre
                    cols[1], // auteur
                    cols[2], // genre
                    cols[3], // isbn
                    Integer.parseInt(cols[4]), // anneePublication
                    Integer.parseInt(cols[5]), // nombreExemplaires
                    Double.parseDouble(cols[6]), // tarifEmpruntParJour
                    Double.parseDouble(cols[7]),  // penaliteParJour
                    cols[8]  //image
                );
                livres.add(livre);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return livres;
    }

    @Override
    public Livre getLivreByIsbn(String isbn) {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(livreFilePath))) {
            br.readLine(); // Skip header

            while ((line = br.readLine()) != null) {
                String[] cols = line.split(";");
                if (cols[3].equals(isbn)) {
                    return new Livre(
                        cols[0], // titre
                        cols[1], // auteur
                        cols[2], // genre
                        cols[3], // isbn
                        Integer.parseInt(cols[4]), // anneePublication 
                        Integer.parseInt(cols[5]), // nombreExemplaires
                        Double.parseDouble(cols[6]), // tarifEmpruntParJour
                        Double.parseDouble(cols[7]),  // penaliteParJour
                        cols.length>8? cols[8]:"" //image

                    );
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    // cette methode nous aide à decremente le nembre des exemplaire d'un livre (utilisation en EmpruntDaoImp.Emprunterlivre())
    @Override
    public boolean livreEstEmprunter(String isbn) {
        boolean result = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(livreFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data[3].equals(isbn)) {
                    int nombreExemplaires = Integer.parseInt(data[5]);
                    if (nombreExemplaires > 0) {
                        data[5] = String.valueOf(nombreExemplaires - 1); 
                        result = true;
                    }
                }
                writer.write(String.join(";", data) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        replaceTmpFile();
        return result;
    }

    //cette methode permet 'incrementer nbr des exemplaire utilisation en EmpruntDaoImp.retournerLivre()
    @Override
    public boolean livreEstRetourner(String isbn) {
        boolean result = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(livreFilePath));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data[3].equals(isbn)) { 
                    int nombreExemplaires = Integer.parseInt(data[5]);
                    data[5] = String.valueOf(nombreExemplaires + 1);
                    result = true;
                }
                writer.write(String.join(";", data) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        replaceTmpFile();
        return result;
    }

    @Override
    public double getPenaliter(String isbn) {
        try (BufferedReader reader = new BufferedReader(new FileReader(livreFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data[3].equals(isbn)) { 
                    return Double.parseDouble(data[7]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getTarif(String isbn) {
        try (BufferedReader reader = new BufferedReader(new FileReader(livreFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data[3].equals(isbn)) {
                    return Double.parseDouble(data[6]); 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //dans cette methode prmet de remplacer le fichier original par le tmp lors d'une modification ou 
    private void replaceTmpFile() {
        File originalFile = new File(livreFilePath);
        File tmpFileObj = new File(tmpFile);

        if (originalFile.delete()) {
            tmpFileObj.renameTo(originalFile);
        } else {
            System.err.println("Erreur lors du remplacement du fichier original.");
        }
    }

	
}

