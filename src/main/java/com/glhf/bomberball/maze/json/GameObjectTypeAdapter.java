package com.glhf.bomberball.maze.json;

import com.glhf.bomberball.gameobject.GameObject;
import com.google.gson.*;

import java.lang.reflect.Type;

public class GameObjectTypeAdapter implements JsonSerializer<GameObject>, JsonDeserializer<GameObject>
{
    protected static Gson gson = new Gson();
    private static final String CLASS_META_KEY = "_class";

    @Override
    public GameObject deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObj = (JsonObject) jsonElement;
        String className = jsonObj.get(CLASS_META_KEY).getAsString();
        try {
            Class<?> clz = Class.forName(className);
            GameObject object = (GameObject) gson.fromJson(jsonElement, clz);
            object.initialize();
            return object;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(GameObject object, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonElement jsonEle = gson.toJsonTree(object);
        jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, object.getClass().getCanonicalName());
        /*if (object instanceof BonusWall) {
            jsonEle.getAsJsonObject().add("bonus", jsonSerializationContext.serialize(((BonusWall) object).bonus, GameObject.class));
        }*/
        return jsonEle;
    }
}
