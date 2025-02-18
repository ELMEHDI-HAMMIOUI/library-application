package modele.dao.interfaces;



public interface AdminDao {
	int checkLogin(String username, String password);
	public boolean ajouterAdmin(String username,String password);
}
