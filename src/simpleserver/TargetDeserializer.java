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
public class TargetDeserializer implements JsonDeserializer<target> {


    @Override
    public target deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
        
        //System.out.println("target deserializing");
         final JsonObject jsonObject = json.getAsJsonObject();

        final JsonElement jsonId = jsonObject.get("target");
        final String name = jsonId.getAsString();
        
        final JsonArray jsonAuthorsArray = jsonObject.get("attr_list").getAsJsonArray();
        final String[] attributes = new String[jsonAuthorsArray.size()];
        for (int i = 0; i < attributes.length; i++) {
            final JsonElement jsonAuthor = jsonAuthorsArray.get(i);
            attributes[i] = jsonAuthor.getAsString();
        }
        
         final target d= new target();
         d.setName(name);
         d.setAttr_list(attributes);

        //Log.d("http", "diary d deserialize finished");
        return d;
    }
    
}
