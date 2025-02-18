package controle;

import java.util.List;

import modele.beans.Bibliothecaire;
import modele.beans.Membre;
import modele.dao.Factories.CsvFactory;
import modele.dao.interfaces.*;
import vue.interfaceGUI.admin.AdminVue;

public class AdminController {
    private  MembreDao mdao;
    private  BibliothecaireDao bdao;

    public AdminController() {
        this.mdao = CsvFactory.getMembreDao();
        this.bdao = CsvFactory.getBibliothecaireDao();
    }

    public void runFram() {
        new AdminVue(mdao.getAllMembres(), bdao.getAllBibliothecaires());
    }
  
    public boolean newBibliothecaire(Bibliothecaire b) {
    	return bdao.ajouterBibliothecaire(b);
    }

    public List<Bibliothecaire> getBibliothecaires() {
    	return bdao.getAllBibliothecaires();
    }
    public Bibliothecaire getBibliothecairesById(int id) {
    	return bdao.getBibliothecaireById(id);
    }
    
    public Boolean updateBibliothecaire(Bibliothecaire b) {
    	System.out.print(bdao.modifierBibliothecaire(b));
    	return true;
    }
    
    public Boolean suprimerBibliothecaire(int id) {
    	return bdao.supprimerBibliothecaire(id);
    }
    
    public Membre  getMembreById(int id) {
    	return mdao.getMembreById(id);
    }
    public boolean updateMembre(Membre m) {
    	return mdao.modifierMembre(m);
    }


	public boolean supprimerMembre(int id_membre) {
		return mdao.supprimerMembre(id_membre);
	}
	public boolean newMembre(Membre membre) {
        boolean result = mdao.ajouterMembre(membre);
        return result;
    }

    public List<Membre> getMembres() {
        List<Membre> membres = mdao.getAllMembres();
        return membres;
    }
}