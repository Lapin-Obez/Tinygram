import java.util.ArrayList;

import com.google.appengine.api.datastore.*;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

/**
 * 
 * Classe regroupant les methodes de gestion de la datastore.
 *
 */

public class Datastore {
	
	// DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	// Save google id after login
	public static void save_id_user (int user_id, String nom, String Prenom, DatastoreService datastore) {
		
			Entity a = new Entity("User");
			a.setProperty("id_user", user_id);
			a.setProperty("firstName", user_id );
			a.setProperty("lastName", Prenom );	
			datastore.put(a); 
		}
	
	
	public static void save_Post (String user_id, String description, DatastoreService datastore) {
		
		Entity a = new Entity("Post");
		a.setProperty("id_user", user_id);
		a.setProperty("description", description );
		datastore.put(a); 
	}
		
	// Methode un seul utilisateur à partir de son id 
	public static Entity verifier (int user_id, DatastoreService datastore) {
		
		Query q = new Query("User").setFilter(new FilterPredicate("id_user", FilterOperator.EQUAL, user_id)); 
		PreparedQuery PrepReq = datastore.prepare(q);
		Entity result = PrepReq.asSingleEntity();
		
//		 String firstName = (String) result.getProperty("firstName");
//		 String id = (String) result.getProperty("id");
//		 System.out.print(id + firstName);
		 
		 return result;
	}
	
	// Methode pour récupérer tous les users de la base de données
	public static ArrayList<Entity> AllUser (DatastoreService datastore) {
		
		ArrayList<Entity> User_list = new ArrayList<Entity>();
		Query q = new Query("User"); 
		PreparedQuery PrepReq = datastore.prepare(q);
		for (Entity result : PrepReq.asIterable()) {
				User_list.add(result); 
			}	
		return User_list;
	}	
	
	// Methode pour récupérer tous les users de la base de données
		public static ArrayList<Entity> AllPosts (DatastoreService datastore) {
			
			ArrayList<Entity> User_list = new ArrayList<Entity>();
			Query q = new Query("Post").setFilter(new FilterPredicate("id_user", FilterOperator.EQUAL, 2)); ; 
			PreparedQuery PrepReq = datastore.prepare(q);
					
			for (Entity result : PrepReq.asIterable()) {
					User_list.add(result); 
				}	
			return User_list;
		}	
		 
	// Methode pour récupérer les post des users followés par le user
	public static ArrayList<Entity> timeline (long id_user, DatastoreService datastore) {
	//  Modifier la requete pour avoir le dernier post du user followé, pas tout
		
		ArrayList<Entity> list_postes = new ArrayList<Entity>() ;
		Query q = new Query("follow").setFilter(new FilterPredicate("id_user", FilterOperator.EQUAL, id_user )); 
		PreparedQuery PrepReq = datastore.prepare(q);
	
		for (Entity result : PrepReq.asIterable()) {
		
			Query query_post = new Query("Post").setFilter(new FilterPredicate("id_user", FilterOperator.EQUAL, result.getProperty("id_followed"))); 
			PreparedQuery posts = datastore.prepare(query_post);
			
			for (Entity post : posts.asIterable()) {
				list_postes.add(post); 
			}	
		
		}	
		return list_postes;
	}
	
	public static ArrayList<Entity> AllUser (int user_id, DatastoreService datastore) {
		
		ArrayList<Entity> User_list = new ArrayList<Entity>();
		Query q = new Query("follow").setFilter(new FilterPredicate("id_user", FilterOperator.EQUAL, user_id )); 
		PreparedQuery PrepReq = datastore.prepare(q);
				
		for (Entity result : PrepReq.asIterable()) {
				User_list.add(result); 
			}	
		return User_list;
	}	
}
