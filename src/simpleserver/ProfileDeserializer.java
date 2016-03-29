/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 *
 * @author liuqi
 */
public class ProfileDeserializer  implements JsonDeserializer<Profile> {


    @Override
    public Profile deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
        
        //System.out.println("target deserializing");
         final JsonObject jsonObject = json.getAsJsonObject();

        final String attr = jsonObject.get("attr_").getAsString();
        /*
        final JsonArray jsonAttrsArray = jsonObject.get("profile").getAsJsonArray();
        final String[] attributes = new String[jsonAttrsArray.size()];
        for (int i = 0; i < attributes.length; i++) {
            final JsonElement jsonAttrs = jsonAttrsArray.get(i);
            attributes[i] = jsonAttrs.getAsString();
        }
        
         final Profile d= new Profile();
         */
         
         final Profile d= new Profile();
         d.setAttr(attr);

        //Log.d("http", "diary d deserialize finished");
        return d;
    }
    
}