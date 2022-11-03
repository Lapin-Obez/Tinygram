package TinyGram;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.api.server.spi.auth.common.User;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.UnauthorizedException;

import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.QueryResultList;
import com.google.appengine.api.datastore.Transaction;

@Api(name = "myApi",
     version = "v1",
     audiences = "622187817241-fqke0l023q148lc21gv1qthq82imi4mg.apps.googleusercontent.com",
  	 clientIds = "622187817241-fqke0l023q148lc21gv1qthq82imi4mg.apps.googleusercontent.com",
     namespace =
     @ApiNamespace(
		   ownerDomain = "helloworld.example.com",
		   ownerName = "helloworld.example.com",
		   packagePath = "")
     )

public class TinyGramEndPoint {
    
    Random r = new Random();

    @ApiMethod(name = "addUser", httpMethod = HttpMethod.GET)
	public Entity addUser(User user) throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        String email = user.getEmail();
		Entity e = new Entity("User",user.getId());
        e.setProperty("ID", user.getId());
        e.setProperty("name", email);
        e.setProperty("iFollowThem", new LinkedList<String>());
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);

		return e;
	}

	@ApiMethod(name = "mypost", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> mypost(@Named("name") String name, @Nullable @Named("next") String cursorString) {

	    Query q = new Query("Post").setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, name));

	    // https://cloud.google.com/appengine/docs/standard/python/datastore/projectionqueries#Indexes_for_projections
	    //q.addProjection(new PropertyProjection("body", String.class));
	    //q.addProjection(new PropertyProjection("date", java.util.Date.class));
	    //q.addProjection(new PropertyProjection("likec", Integer.class));
	    //q.addProjection(new PropertyProjection("url", String.class));

	    // looks like a good idea but...
	    // generate a DataStoreNeedIndexException -> 
	    // require compositeIndex on owner + date
	    // Explosion combinatoire.
	    // q.addSort("date", SortDirection.DESCENDING);
	    
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    PreparedQuery pq = datastore.prepare(q);
	    
	    FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);
	    
	    if (cursorString != null) {
		fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}
	    
	    QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
	    cursorString = results.getCursor().toWebSafeString();
	    
	    return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	    
	}
    
	@ApiMethod(name = "getPost",
		   httpMethod = ApiMethod.HttpMethod.GET)
	public CollectionResponse<Entity> getPost(User user, @Nullable @Named("next") String cursorString)
			throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        
		Query q = new Query("Post").
		    setFilter(new FilterPredicate("owner", FilterOperator.EQUAL, user.getEmail()));

		// Multiple projection require a composite index
		// owner is automatically projected...
		// q.addProjection(new PropertyProjection("body", String.class));
		// q.addProjection(new PropertyProjection("date", java.util.Date.class));
		// q.addProjection(new PropertyProjection("likec", Integer.class));
		// q.addProjection(new PropertyProjection("url", String.class));

		// looks like a good idea but...
		// require a composite index
		// - kind: Post
		//  properties:
		//  - name: owner
		//  - name: date
		//    direction: desc

		// q.addSort("date", SortDirection.DESCENDING);

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		PreparedQuery pq = datastore.prepare(q);

		FetchOptions fetchOptions = FetchOptions.Builder.withLimit(2);

		if (cursorString != null) {
			fetchOptions.startCursor(Cursor.fromWebSafeString(cursorString));
		}

		QueryResultList<Entity> results = pq.asQueryResultList(fetchOptions);
		cursorString = results.getCursor().toWebSafeString();

		return CollectionResponse.<Entity>builder().setItems(results).setNextPageToken(cursorString).build();
	}


	@ApiMethod(name = "postMessage", httpMethod = HttpMethod.POST)
	public Entity postMessage(User user, PostMessage pm) throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        Long date = Long.MAX_VALUE-(new Date()).getTime();
        String id = user.getEmail() +":"+ date;
		Entity e = new Entity("Post", id);//Il faut que le sender soit placé avant la date afin d'éviter de créer un hot spot
		e.setProperty("owner", user.getEmail());
		e.setProperty("url", pm.url);
		e.setProperty("body", pm.body);
		e.setProperty("likec", 0);
        e.setProperty("likeU", new LinkedList<String>());//Liste des users qui ont like c'te connerie
		e.setProperty("date", new Date());
        ///Solution pour pas projeter les listes
        //Entity pi = new Entity("PostIndex", e.getKey());
        //HashSet<String> rec=new HashSet<String>();
        //pi.setProperty("receivers",rec);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
        try {
            likeMessage(user, id);
        } catch (Exception test) {
            System.out.println("ça vallait le coup");
        }
        
		return e;
	}

    @ApiMethod(name = "likeMessage", httpMethod = HttpMethod.POST)
    public Entity likeMessage(User user, String idMessage)throws UnauthorizedException, Exception {
        if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        Query query = new Query("Post").setFilter(new FilterPredicate("__key__",
        FilterOperator.EQUAL,
        idMessage));
        PreparedQuery pq = datastore.prepare(query);
        Entity e = pq.asSingleEntity();
        
        //Si on récupère plusieurs entity y a un problème wala
        if(e == null) throw new UnauthorizedException("Plusieurs messages ont le même ID, ACHTUNG !!!!!");
        try {
            long c = (long)e.getProperty("likec");
            
            e.setProperty("likec",c+1);
            LinkedList<String> l = (LinkedList<String>)e.getProperty("likeU");
            
            e.setProperty("likeU",l.add(user.getEmail()));
            datastore.put(e);
            txn.commit();
        } catch (Exception error) {
            // TODO Auto-generated catch block
            error.printStackTrace();
        }
        
        
        

        
        return e;
    }
}
