package modele.beans;
	
	public class Livre {
		    private String titre;
		    private String auteur;
		    private String genre;
		    private String isbn;
		    private int anneePublication;
		    private int nombreExemplaires;
		    private double tarifEmpruntParJour;
		    private double penaliteParJour;
		    private String imagePath;
		    // constructr//
			public Livre(String titre, String auteur, String genre, String isbn, int anneePublication,
					int nombreExemplaires, double tarifEmpruntParJour, double penaliteParJour, String imagePath) {
				super();
				this.titre = titre;
				this.auteur = auteur;
				this.genre = genre;
				this.isbn = isbn;
				this.anneePublication = anneePublication;
				this.nombreExemplaires = nombreExemplaires;
				this.tarifEmpruntParJour = tarifEmpruntParJour;
				this.penaliteParJour = penaliteParJour;
				this.imagePath=imagePath;
			}
		    //getter setter
			public String getTitre() {
				return titre;
			}
			public void setTitre(String titre) {
				this.titre = titre;
			}
			public String getAuteur() {
				return auteur;
			}
			public void setAuteur(String auteur) {
				this.auteur = auteur;
			}
			public String getGenre() {
				return genre;
			}
			public void setGenre(String genre) {
				this.genre = genre;
			}
			public String getIsbn() {
				return isbn;
			}
			public void setIsbn(String isbn) {
				this.isbn = isbn;
			}
			public int getAnneePublication() {
				return anneePublication;
			}
			public void setAnneePublication(int anneePublication) {
				this.anneePublication = anneePublication;
			}
			public int getNombreExemplaires() {
				return nombreExemplaires;
			}
			public void setNombreExemplaires(int nombreExemplaires) {
				this.nombreExemplaires = nombreExemplaires;
			}
			public double getTarifEmpruntParJour() {
				return tarifEmpruntParJour;
			}
			public void setTarifEmpruntParJour(double tarifEmpruntParJour) {
				this.tarifEmpruntParJour = tarifEmpruntParJour;
			}
			public double getPenaliteParJour() {
				return penaliteParJour;
			}
			public void setPenaliteParJour(double penaliteParJour) {
				this.penaliteParJour = penaliteParJour;
			}
			
			public String getImagePath() {
				return imagePath;
			}
			public void setImagePath(String imagePath) {
				this.imagePath = imagePath;
			}
			@Override
			public String toString() {
				return "Livre [titre=" + titre + ", auteur=" + auteur + ", genre=" + genre + ", isbn=" + isbn
						+ ", anneePublication=" + anneePublication + ", nombreExemplaires=" + nombreExemplaires
						+ ", tarifEmpruntParJour=" + tarifEmpruntParJour + ", penaliteParJour=" + penaliteParJour
						+ ", imagePath=" + imagePath + "]";
			}
			
			

	
	}