package modele.beans;

public class Emprunt {
    private int idEmprunt; 
    private String isbn; 
    private int idMembre; 
    private String dateEmprunt; 
    private String dateRetourPrevue; 
    private String dateRetourEffective; 
    private double tarif; 
    private double penalite; 

    // Constructor
    public Emprunt(int idEmprunt, String isbn, int idMembre, String dateEmprunt, String dateRetourPrevue, String dateRetourEffective, double tarif, double penalite) {
        this.idEmprunt = idEmprunt;
        this.isbn = isbn;
        this.idMembre = idMembre;
        this.dateEmprunt = dateEmprunt;
        this.dateRetourPrevue = dateRetourPrevue;
        this.dateRetourEffective = dateRetourEffective;
        this.tarif = tarif;
        this.penalite = penalite;
    }
    public  Emprunt() {}
    // Getters and Setters
    public int getIdEmprunt() {
        return idEmprunt;
    }

    public void setIdEmprunt(int idEmprunt) {
        this.idEmprunt = idEmprunt;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getIdMembre() {
        return idMembre;
    }

    public void setIdMembre(int idMembre) {
        this.idMembre = idMembre;
    }

    public String getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(String dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public String getDateRetourPrevue() {
        return dateRetourPrevue;
    }

    public void setDateRetourPrevue(String dateRetourPrevue) {
        this.dateRetourPrevue = dateRetourPrevue;
    }

    public String getDateRetourEffective() {
        return dateRetourEffective;
    }

    public void setDateRetourEffective(String dateRetourEffective) {
        this.dateRetourEffective = dateRetourEffective;
    }

    public double getTarif() {
        return tarif;
    }

    public void setTarif(double tarif) {
        this.tarif = tarif;
    }

    public double getPenalite() {
        return penalite;
    }

    public void setPenalite(double penalite) {
        this.penalite = penalite;
    }

    @Override
    public String toString() {
        return "Emprunt{" +
                "idEmprunt=" + idEmprunt +
                ", isbn='" + isbn + '\'' +
                ", idMembre=" + idMembre +
                ", dateEmprunt='" + dateEmprunt + '\'' +
                ", dateRetourPrevue='" + dateRetourPrevue + '\'' +
                ", dateRetourEffective='" + dateRetourEffective + '\'' +
                ", tarif=" + tarif +
                ", penalite=" + penalite +
                '}';
    }
}