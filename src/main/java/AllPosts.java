import java.io.BufferedReader;
import java.io.IOException;

	import java.io.PrintWriter;
	import java.util.ArrayList;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Random;
	import java.util.Set;

	import javax.servlet.ServletException;
	import javax.servlet.ServletOutputStream;
	import javax.servlet.annotation.WebServlet;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;

	import com.google.appengine.api.datastore.DatastoreService;
	import com.google.appengine.api.datastore.DatastoreServiceFactory;

	import com.google.appengine.api.datastore.Entity;
	import com.google.appengine.api.datastore.EntityNotFoundException;
	import com.google.appengine.api.datastore.FetchOptions;
	import com.google.appengine.api.datastore.Key;
	import com.google.appengine.api.datastore.KeyFactory;
	import com.google.appengine.api.datastore.KeyRange;
	import com.google.appengine.api.datastore.PreparedQuery;
	import com.google.appengine.api.datastore.PropertyProjection;
	import com.google.appengine.api.datastore.Query;
	import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
	import com.google.appengine.api.datastore.Query.FilterOperator;
	import com.google.appengine.api.datastore.Query.FilterPredicate;

	import com.google.appengine.repackaged.com.google.datastore.v1.CompositeFilter;
	import com.google.appengine.repackaged.com.google.datastore.v1.Projection;
	import com.google.appengine.repackaged.com.google.datastore.v1.PropertyFilter;
import com.google.gson.Gson;

/**
 * 
 * Servlet qui renvoie tout les AllPosts d'un user de la datastores.
 *
 */


	@WebServlet(name = "AllPosts", urlPatterns = { "/AllPosts" })
	
	public class AllPosts extends HttpServlet {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		@Override
		public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.addHeader("Access-Control-Allow-Origin", "*");
		    response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		    
		    /*
	    	Entity result = Datastore.verifier(5,datastore);
	    	ArrayList<Entity> User_list = new ArrayList<Entity>();
	    	User_list.add(result);*/
			

			ArrayList<Entity> entity_list = Datastore.AllPosts(datastore);
	    	ServletOutputStream out = response.getOutputStream();	
			
	    	//String json = new Gson().toJson(User_list);

			JsonConverter converter = new JsonConverter();
	        String output = converter.convertToJson(entity_list);
	        out.print(output);
	        
		}
	}
