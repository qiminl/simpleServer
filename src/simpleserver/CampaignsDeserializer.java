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
import java.lang.ProcessBuilder.Redirect.Type;

/**
 *
 * @author liuqi
 */
public class CampaignsDeserializer implements JsonDeserializer<Campaigns> {

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
    public Campaigns deserialize(JsonElement json, java.lang.reflect.Type type, JsonDeserializationContext context) throws JsonParseException {
        
        
        JsonObject jsonObject;
        jsonObject = json.getAsJsonObject();

        Campaign[] campaign = context.deserialize(jsonObject.get(""), Campaign[].class);

        final Campaigns campaigns = new Campaigns();
        campaigns.setCampaigns(campaign);
        return campaigns;
    }
    
}
