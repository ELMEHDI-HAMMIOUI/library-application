package modele.beans;

import java.time.LocalDate;

public class Bibliothecaire {

	private int id;
    private String nom;
    private String prenom;
	private String cni ;
	private String sexe ;
    private String adresse;
    private String telephone;
    private String dateEmbauche;
    private double salaire;
    private String email;
    private String password;

    public Bibliothecaire(int id, String nom, String prenom,String cni ,String sexe, String adresse, String telephone,
    		String dateEmbauche, double salaire, String email, String password) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.cni=cni ;
        this.sexe=sexe;
        this.adresse = adresse;
        this.telephone = telephone;
        this.dateEmbauche = dateEmbauche;
        this.salaire = salaire;
        this.email = email;
        this.password = password;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDateEmbauche() {
        return dateEmbauche;
    }

    public void setDateEmbauche(String dateEmbauche) {
        this.dateEmbauche = dateEmbauche;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Bibliothecaire{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", telephone='" + telephone + '\'' +
                ", dateEmbauche=" + dateEmbauche +
                ", salaire=" + salaire +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

	public String getCni() {
		return cni;
	}

	public void setCni(String cni) {
		this.cni = cni;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}
}
