package modele.dao.interfaces;

import java.util.List;
import java.util.Map;

import modele.beans.Bibliothecaire;

public interface BibliothecaireDao {
	   boolean ajouterBibliothecaire(Bibliothecaire user);
	    boolean supprimerBibliothecaire(int id);
	    boolean modifierBibliothecaire(Bibliothecaire b);
	    List<Bibliothecaire> getAllBibliothecaires();
	    Bibliothecaire getBibliothecaireById(int id);
	    int checkLogin(String username, String password);
}
