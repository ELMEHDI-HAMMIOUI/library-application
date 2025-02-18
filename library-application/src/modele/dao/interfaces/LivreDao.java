package modele.dao.interfaces;


import java.util.List ;
import java.util.Map;

import modele.beans.*;

public interface LivreDao {
		boolean ajouterLivre(Livre titre);
	    boolean supprimerLivre(String isbn);
	    boolean modifierLivre(Livre livre);
	    List<Livre> getAllLivres ();
	    Livre getLivreByIsbn(String isbn);
	    boolean livreEstEmprunter(String isbn);
	    boolean livreEstRetourner(String isbn);
	    double getPenaliter(String isbn);
	    double getTarif(String isbn);
}
	
	
