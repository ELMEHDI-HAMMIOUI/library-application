package modele.dao.configuration;

public final class DataBase {
	private static String MembreCsvFilePath ="src/dateBase/Membres.csv";
	private static String BibliothecaireCsvFilePath="src/dateBase/Bibliothecaire.csv";
	private static String AdminCsvFilePath="src/dateBase/Admin.csv";
	private static String tmpMembresFilePath ="src/dateBase/tmpMembres.csv";
	private static String tmpBibliothecaireFilePath ="src/dateBase/tmpBibliothecaire.csv";
	private static String tmpAdminFilePath ="src/dateBase/tmpAdmin.csv";
	private static String LivreCsvFilePath ="src/dateBase/livres.csv";
	private static String empruntFilePath = "src/dateBase/emprunts.csv";

	public DataBase() {}
	public static String getLivreCsvFilePath() {
		return LivreCsvFilePath;
	}
	public static String getMembreCsvFilePath() {
		return MembreCsvFilePath;
	}
	public static String getBibliothecaireCsvFilePath() {
		return BibliothecaireCsvFilePath;
	}
	
	public static String getAdminCsvFileCsvPath() {
		return AdminCsvFilePath;
	}
	
	
	//des fichier de suppresion 
	public static String getTmpMembresFilePath() {
		return tmpMembresFilePath;
	}
	public static String getTmpBibliothecaireFilePath() {
		return tmpBibliothecaireFilePath;
	}
	public static String getTmpAdminFilePath() {
		return tmpAdminFilePath;
	}
	public static String getEmpruntFilePath() {
		return empruntFilePath;
	}



}
