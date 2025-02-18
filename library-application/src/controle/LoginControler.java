package controle;

import modele.beans.Livre;
import modele.dao.Factories.CsvFactory;
import modele.dao.implementations.LivreDaoImp;
import modele.dao.interfaces.*;
import modele.dao.interfaces.BibliothecaireDao;
import modele.dao.interfaces.MembreDao;
import vue.interfaceGUI.generale.LoginVue;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginControler {
    private static LoginVue loginView;
    private BibliothecaireDao bibliothecaireDao;
    private MembreDao membreDao;
    private AdminDao adminDao;
    

    public LoginControler() {
        this.bibliothecaireDao =CsvFactory.getBibliothecaireDao();
        this.membreDao =CsvFactory.getMembreDao();
        this.adminDao = CsvFactory.getAdminDao();
        
    }

    public void handleLogin(String username, String password) {
    	
        if (username.isEmpty() || password.isEmpty()) {
            return;
        }

        // Example of login verification using DAOs
        if (bibliothecaireDao.checkLogin(username, password) != -1) {
        	BibliothecaireController bc =new BibliothecaireController();
        	bc.runFram();
            loginView.dispose();
        } else if (membreDao.checkLogin(username, password) != -1) {
        } else if (adminDao.checkLogin(username, password) != -1) {  
            AdminController guc = new AdminController();  
            guc.runFram();
            // Optionally close the login window
            loginView.dispose();
        } else {
          loginView.setStatus("Invalid username or password.");
        }
    }

    public static void main(String[] args) {
        LivreDao lDao=CsvFactory.getLivreDao();
    	loginView = new LoginVue(lDao.getAllLivres());
        loginView.setController(new LoginControler());
    }
   
}
