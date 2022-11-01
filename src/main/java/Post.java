public class Post {

	String description;
	String  ur_image;
	String  id_user;
	
	
	public Post(String description, String ur_image, String id_user) {
		 
		this.description = description;
		this.ur_image = ur_image;
		this.id_user = id_user;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUr_image() {
		return ur_image;
	}
	public void setUr_image(String ur_image) {
		this.ur_image = ur_image;
	}
	public String getId_user() {
		return id_user;
	}
	public void setId_user(String id_user) {
		this.id_user = id_user;
	}
	


}

