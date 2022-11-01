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

	@WebServlet(name = "NewUser", urlPatterns = { "/NewUser" })
	public class NewUser extends HttpServlet {
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		@Override
	    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
			response.setContentType("text/html");
	        response.setCharacterEncoding("UTF-8");
	
	        // Recupérer les données du form
	        int id_user = Integer.parseInt(request.getParameter("id_user"));
	        String nom = request.getParameter("nom");
	        String prenom = request.getParameter("prenom");
	        	 	         
	        Datastore.save_id_user(id_user, nom, prenom, datastore);
	        
	        
	        /*        
	        StringBuilder buffer = new StringBuilder();
	        BufferedReader reader = request.getReader();
	        String line;
	        while ((line = reader.readLine()) != null) {
	            buffer.append(line);
	            buffer.append(System.lineSeparator());
	        }
	        String data = buffer.toString();
	         */
	        

	    }
	}

