package modele.dao.interfaces;

import java.util.List;
import java.util.Map;

import modele.beans.Membre;

public interface MembreDao {
    boolean ajouterMembre(Membre user);
    boolean supprimerMembre(int id);
	boolean modifierMembre(Membre m);
    List<Membre> getAllMembres();
    Membre getMembreById(int id);
    int checkLogin(String username, String password);
}
