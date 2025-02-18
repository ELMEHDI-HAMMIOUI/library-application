package modele.dao.Factories;

import modele.dao.implementations.AdminDaoImp; 
import modele.dao.implementations.BibliothecaireDaoImp;
import modele.dao.implementations.MembreDaoImp;
import modele.dao.implementations.*;

import modele.dao.interfaces.AdminDao;
import modele.dao.interfaces.BibliothecaireDao;
import modele.dao.interfaces.EmpruntDao;
import modele.dao.interfaces.LivreDao;
import modele.dao.interfaces.MembreDao;

public class CsvFactory {
	public static BibliothecaireDao getBibliothecaireDao() {
		return new BibliothecaireDaoImp();
	}
	public static MembreDao getMembreDao() {
		return new MembreDaoImp();
	}
	public static AdminDao getAdminDao() {
		return new AdminDaoImp();
	}
	public static LivreDao getLivreDao() {
		return new LivreDaoImp();
	}
	
	public static EmpruntDao getEmpruntDao() {
		return new EmpruntDaoImp();
	}
	
}

