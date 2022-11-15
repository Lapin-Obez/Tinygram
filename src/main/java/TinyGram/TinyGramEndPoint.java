package TinyGram;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Key;
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
import com.google.appengine.api.datastore.KeyFactory;
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
    private static final Logger log = Logger.getLogger(TinyGramEndPoint.class.getName());
    Random r = new Random();

    @ApiMethod(name = "addUser", httpMethod = HttpMethod.POST)
	public Entity addUser(@Nullable @Named("urlPhoto")String urlPhoto,User user) throws UnauthorizedException {

		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        String email = user.getEmail();
		Entity e = new Entity("User",user.getId());
        e.setProperty("ID", user.getId());
        e.setProperty("name", email);
        List l =new LinkedList<String>();
        l.add("MrCool");
        e.setProperty("iFollowThem", l);
        e.setProperty("photo", urlPhoto);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(e);

		return e;
	}
    @ApiMethod(name = "myFollows", httpMethod = HttpMethod.GET)
	public List<String> myFollows(User user) throws UnauthorizedException {
		if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        Query q = new Query("User").setFilter(new FilterPredicate("name", FilterOperator.EQUAL, user.getEmail()));
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    PreparedQuery pq = datastore.prepare(q);
        Entity e = pq.asSingleEntity();
        if(e == null) throw new UnauthorizedException("Plusieurs Users on la même adresse mail");
        List<String> l = (List<String>)e.getProperty("iFollowThem");
	    return  l;
	}

    @ApiMethod(name = "myPicture", httpMethod = HttpMethod.GET)
	public Object myPicture(@Named("mailUser")String mailUser) throws UnauthorizedException {
		if (mailUser == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        Query q = new Query("User").setFilter(new FilterPredicate("name", FilterOperator.EQUAL, mailUser));
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    PreparedQuery pq = datastore.prepare(q);
        Entity e = pq.asSingleEntity();
        if(e == null) throw new UnauthorizedException("Plusieurs Users on la même adresse mail ou l'adresse mail est inexistante");
        Object o = e.getProperty("photo");
	    return o;
	}

    @ApiMethod(name = "followSomeone", httpMethod = HttpMethod.GET)
    public Entity followSomeone(@Named("mailUser")String mailUser,User me)throws UnauthorizedException, Exception {
        if (me == null) {
			throw new UnauthorizedException("Invalid credentials");
		}
        Key myID = KeyFactory.createKey("User", me.getId());
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        Query query = new Query("User").setFilter(new FilterPredicate("__key__",FilterOperator.EQUAL,myID));
        PreparedQuery pq = datastore.prepare(query);
        Entity e = pq.asSingleEntity();
        //log.info("ENTITY" +e.toString());
        //Si on récupère plusieurs entity y a un problème wala
        if(e == null) throw new UnauthorizedException("Plusieurs messages ont le même ID, ACHTUNG !!!!!");
        try {
            List<String> l = (List<String>)e.getProperty("iFollowThem");
            //Query qtest = new Query("Post").setFilter(new FilterPredicate("__key__",FilterOperator.EQUAL,idMessageKey)).setFilter(new FilterPredicate("likeU",FilterOperator.EQUAL,user.getEmail()));
            if (l.contains(mailUser)){
                throw new UnauthorizedException("Vous avez déjà like ce post : -> tocard (de toute façon seul un margoulin peut aller lire ce message)");
            }
            l.add(mailUser);
            e.setProperty("iFollowThem",l);
            datastore.put(e);
            txn.commit();
        } catch (Exception error) {
            error.printStackTrace();
        }
        return e;
    }

    //Renvoie les post d'un user passé en paramètre
	@ApiMethod(name = "userPost", httpMethod = HttpMethod.GET)
	public CollectionResponse<Entity> userPost(@Named("name") String name, @Nullable @Named("next") String cursorString) {
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
    
	@ApiMethod(name = "allPost",httpMethod = ApiMethod.HttpMethod.GET)
	public CollectionResponse<Entity> allPost(@Nullable @Named("next") String cursorString)
			throws UnauthorizedException {     
		Query q = new Query("Post");
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
        LinkedList l = new LinkedList<String>();
        l.add("MrCool");
        e.setProperty("likeU", l);//Liste des users qui ont like c'te connerie
		e.setProperty("date", new Date());
        ///Solution pour pas projeter les listes
        //Entity pi = new Entity("PostIndex", e.getKey());
        //HashSet<String> rec=new HashSet<String>();
        //pi.setProperty("receivers",rec);
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Transaction txn = datastore.beginTransaction();
		datastore.put(e);
		txn.commit();
		return e;
	}

    //Notre gestion des likes ne scale pas mais elle est fonctionnelle.
    @ApiMethod(name = "likeMessage", httpMethod = HttpMethod.GET)
    public Entity likeMessage(@Named("idMessage")String idMessage,User user)throws UnauthorizedException, Exception {
        if (user == null) {
			throw new UnauthorizedException("Invalid credentials");
		}

        Key idMessageKey = KeyFactory.createKey("Post", idMessage);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        Transaction txn = datastore.beginTransaction();
        Query query = new Query("Post").setFilter(new FilterPredicate("__key__",FilterOperator.EQUAL,idMessageKey));
        PreparedQuery pq = datastore.prepare(query);
        Entity e = pq.asSingleEntity();
        //log.info("ENTITY" +e.toString());
        //Si on récupère plusieurs entity y a un problème wala
        if(e == null) throw new UnauthorizedException("Mehrere Nachrichten auf derselben ID, ACHTUNG !!!!!");

        try {
            List<String> l = (List<String>)e.getProperty("likeU");
            //Query qtest = new Query("Post").setFilter(new FilterPredicate("__key__",FilterOperator.EQUAL,idMessageKey)).setFilter(new FilterPredicate("likeU",FilterOperator.EQUAL,user.getEmail()));
            if (l.contains(user.getEmail())){
                throw new UnauthorizedException("Vous avez déjà like ce post (de toute façon seul un margoulin peut aller lire ce message)");
            }
            l.add(user.getEmail());
            e.setProperty("likeU",l);
            long c = (long)e.getProperty("likec")+1;
            //log.info("LIKEC"+c);
            e.setProperty("likec",c);
            //log.info("LIKEU %s"+l.toString());
            datastore.put(e);
            txn.commit();
        } catch (Exception error) {
            error.printStackTrace();
        }
        return e;
    }
}
