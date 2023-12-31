package it.unimib.readify.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import it.unimib.readify.model.OLDescription;

public class OLDescriptionDeserializer implements JsonDeserializer<OLDescription> {
    @Override
    public OLDescription deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonObject()) {
            JsonObject jsonObject = json.getAsJsonObject();
            String type = jsonObject.has("type") ? jsonObject.get("type").getAsString() : null;
            String value = jsonObject.has("value") ? jsonObject.get("value").getAsString() : null;
            return new OLDescription(type, value);
        } else if (json.isJsonPrimitive()) {
            String value = json.getAsString();
            return new OLDescription(null, value);
        } else {
            throw new JsonParseException("Unexpected JSON type for OLDescription");
        }
    }
}
