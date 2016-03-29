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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author liuqi
 */
public class UserDeserializer implements JsonDeserializer<User> {

    /*
    @Override
    public Campaigns deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        Campaign[] campaign = context.deserialize(jsonObject.get(""), Campaign[].class);

        final Campaigns campaigns = new Campaigns();
        campaigns.setCampaigns(campaign);
        return campaigns;
    }
*/
    @Override
    public User deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
        //System.out.println("Campaign deserializing");
        
        final JsonObject jsonObject = json.getAsJsonObject();

        final JsonElement jsonId = jsonObject.get("user");
        final String name = jsonId.getAsString();
        /*
        final JsonArray jsonAttrsArray = jsonObject.get("profile").getAsJsonArray();
        final String[] attributes = new String[jsonAttrsArray.size()];
        for (int i = 0; i < attributes.length; i++) {
            final JsonElement jsonAttrs = jsonAttrsArray.get(i);
            attributes[i] = jsonAttrs.getAsString();
        }*/
        //Profile profile = context.deserialize(jsonObject.get("profile"), Profile.class);
        //Profile[] profile_list = context.deserialize(jsonObject.get("profile"), Profile[].class);

        final JsonArray jsonProfile = jsonObject.get("profile").getAsJsonArray();
       //Set<Map.Entry<String, JsonElement>> entries = jsonProfile.entrySet();//will return members of your object
        int []profile =new int[jsonProfile.size()];
        int i = 0;
        for (JsonElement temp: jsonProfile) {
            //System.out.println(temp);
            JsonObject diu = temp.getAsJsonObject();
            JsonElement value = null;
            for (Entry<String, JsonElement> elementEntry : diu.entrySet())
            {
                String key = elementEntry.getKey();
                value= diu.get(key);
            }
            if(value != null){
                profile[i] = Integer.parseInt( value.getAsString().substring(1));
            }
            i++;
        }


        final User d= new User();
        d.setName(name);
        d.setProfile(profile);
        return d;
    }
    
}