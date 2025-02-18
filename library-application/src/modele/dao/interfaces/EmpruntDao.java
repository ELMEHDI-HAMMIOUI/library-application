package modele.dao.interfaces;

import modele.beans.Emprunt;
import java.util.List;

public interface EmpruntDao {
    List<Emprunt> getAllEmprunte();
    List<Emprunt> getAllEmprunteByISBN(String isbn);
    List<Emprunt> getAllEmprunteByMembreId(String id_membre);
    Emprunt getEmprunteById(int id_emprunt);
    boolean emprunterLivre(Emprunt emprunt);
    boolean marquerRetour(Emprunt emprunt);   
    int getNextId();
}
