/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package simpleserver;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

/**
 *
 * @author liuqi
 */
public class CampaignDeserializer implements JsonDeserializer<Campaign> {

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
    public Campaign deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
        //System.out.println("Campaign deserializing");
        
        final JsonObject jsonObject = json.getAsJsonObject();

        final JsonElement jsonId = jsonObject.get("campaign_name");
        final String name = jsonId.getAsString();
        final double  price = jsonObject.get("price").getAsDouble();
        
        
        target[] target_list = context.deserialize(jsonObject.get("target_list"), target[].class);
        
        /*
        final String date = jsonObject.get("date").getAsString();
        final String text = jsonObject.get("text").getAsString();
        final float latitude = jsonObject.get("latitude").getAsFloat();
        final float longitude = jsonObject.get("longitude").getAsFloat();
        final String share = jsonObject.get("share").getAsString();
        final String image = jsonObject.get("image").getAsString();
        final String imageurl = jsonObject.get("imageurl").getAsString();
        final String audio = jsonObject.get("sound").getAsString();
        final String userid = jsonObject.get("userid").getAsString();
        //Log.d("http","getAsString() till imageUri");
        final String imageUri = jsonObject.get("imageUri").getAsString();
        //Log.d("http","getAsString() finished upon imageUri");
        /*
        final JsonArray jsonAuthorsArray = jsonObject.get("authors").getAsJsonArray();
        final String[] authors = new String[jsonAuthorsArray.size()];
        for (int i = 0; i < authors.length; i++) {
            final JsonElement jsonAuthor = jsonAuthorsArray.get(i);
            authors[i] = jsonAuthor.getAsString();
        }*/

        final Campaign d= new Campaign();
        d.setName(name);
        d.setPrice(price);
        d.setTarget(target_list);
        return d;
    }
    
}
