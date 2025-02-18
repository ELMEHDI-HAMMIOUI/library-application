package controle;

import modele.beans.Emprunt; 
import modele.beans.Livre;
import modele.beans.Membre;
import modele.dao.Factories.CsvFactory;

import modele.dao.interfaces.*;
import vue.bibliothecaire.BibliothecaireVue;

import java.time.LocalDate;
import java.util.List;
	
public class BibliothecaireController {

	private LivreDao livreDao;
    private EmpruntDao empruntDao;
    private MembreDao membredao;
   
    public BibliothecaireController() {
        livreDao = CsvFactory.getLivreDao();;
        empruntDao=CsvFactory.getEmpruntDao();
        membredao=CsvFactory.getMembreDao();
    }
    public void runFram() {
        BibliothecaireVue lv=new BibliothecaireVue();
    }
   
    public void ajouterLivre(Livre livre) throws Exception {
        if (livre == null) {
            throw new Exception("Le livre ne peut pas être null");
        }
        livreDao.ajouterLivre(livre);
    }

    
    public List<Livre> listerLivres() throws Exception {
        return livreDao.getAllLivres();
    }


    public boolean supprimerLivre(String isbn) throws Exception {
    	boolean canDelete =true;
    	if(!empruntDao.getAllEmprunteByISBN(isbn).isEmpty()) {
    		List<Emprunt> emprunts = empruntDao.getAllEmprunteByISBN(isbn);
    		for(Emprunt emp: emprunts) {
    			if(emp.getDateRetourEffective().equals("-")) {
    				canDelete=false;
    				break;
    			}
    		}
    	}
       if(canDelete) {
        	return livreDao.supprimerLivre(isbn);
        }
		return false;    
    }
    
    
    public boolean modifierLivre(Livre livre) {
        return livreDao.modifierLivre(livre);
    }

    public Livre getLivreByIsbn(String Isbn) {
    	return livreDao.getLivreByIsbn(Isbn);
    }
    
    


    public boolean emprunterLivre(Emprunt emp) {
        return empruntDao.emprunterLivre(emp);
    }

    public boolean marquerRetour(Emprunt emp) {
        return empruntDao.marquerRetour(emp);
    }

    public List<Emprunt> getAllEmprunts() {
        return empruntDao.getAllEmprunte();
    }

    public List<Emprunt> getEmpruntsByISBN(String isbn) {
        return empruntDao.getAllEmprunteByISBN(isbn);
    }

    public List<Emprunt> getEmpruntsByMembreId(String idMembre) {
        return empruntDao.getAllEmprunteByMembreId(idMembre);
    }

    public Emprunt getEmpruntById(int idEmprunt) {
        return empruntDao.getEmprunteById(idEmprunt);
    }
    
    public List<Membre> getAllMembres(){
    	return membredao.getAllMembres();
    }
    
    public Membre getMembreById(int id_membre){
    	return membredao.getMembreById(id_membre);
    }
}

