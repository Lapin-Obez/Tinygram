public class User {

	String nom;
	String  Prenom;
	
	public User(String nom, String prenom) {
		this.nom = nom;
		this.Prenom= prenom;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return Prenom;
	}

	public void setPrenom(String prenom) {
		Prenom = prenom;
	}
	
	
}

