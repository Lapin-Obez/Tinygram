 
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

public class JsonConverter {

    private final Gson gson;

    public JsonConverter() {

        gson = new GsonBuilder().create();
    }

    public String convertToJson(List<Entity> list_users) {

        JsonArray jarray = gson.toJsonTree(list_users).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("list_users", jarray);

        return jsonObject.toString();
    }
    
    public String convertToJsonUser(List<User> list_users) {

        JsonArray jarray = gson.toJsonTree(list_users).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("list_users", jarray);

        return jsonObject.toString();
    }
}

