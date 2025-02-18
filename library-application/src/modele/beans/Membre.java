package modele.beans;

import java.util.*; 

public class Membre {
	private int id_membre ;
	private String nom ;
	private String prenom ;
	private String cni ;
	private String sexe ;
	private String date_n ;
	private String email ;
	private String paswd ;
	private String N_tele ;
	private String addresse ;
	private String  date_inscription ;
	private String status;
	private String photo;
	public Membre() {}
	
	public Membre(int id_membre, String nom, String prenom,String cni,String sexe ,String  date_n, String email, String paswd, String N_tele, String addresse, String  date_inscription, String status,String photo) {
	    this.id_membre = id_membre;
	    this.nom = nom;
	    this.prenom = prenom;
	    this.cni=cni;
	    this.sexe=sexe;
	    this.date_n = date_n;
	    this.email = email;
	    this.paswd = paswd;
	    this.N_tele = N_tele;
	    this.addresse = addresse;
	    this.date_inscription = date_inscription;
	    this.status = status;
	    this.setPhoto(photo);
	}
	
	public int getId_membre() {
		return id_membre;
	}
	public void setId_membre(int id_membre) {
		this.id_membre = id_membre;
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
	public String  getDate_n() {
		return date_n;
	}
	public void setDate_n(String  date_n) {
		this.date_n = date_n; 
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPaswd() {
		return paswd;
	}
	public void setPaswd(String paswd) {
		this.paswd = paswd;
	}
	public String getN_tele() {
		return N_tele;
	}
	public void setN_tele(String n_tele) {
		N_tele = n_tele;
	}
	public String getAddresse() {
		return addresse;
	}
	public void setAddresse(String addresse) {
		this.addresse = addresse;
	}
	public String  getDate_inscription() {
		return date_inscription;
	}
	public void setDate_inscription(String  date_inscription) {
		this.date_inscription = date_inscription;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "Membre [id_membre=" + id_membre + ", nom=" + nom + ", prenom=" + prenom + ", date_n=" + date_n
				+ ", email=" + email + ", paswd=" + paswd + ", N_tele=" + N_tele + ", addresse=" + addresse
				+ ", date_inscription=" + date_inscription + ", status=" + status + "]";
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

	public void dateNaissance(String  dateNaissance) {
		this.date_n=dateNaissance;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}
	


	
}
