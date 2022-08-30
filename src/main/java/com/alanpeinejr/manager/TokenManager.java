package com.alanpeinejr.manager;

import com.alanpeinejr.encryption.IDecrypt;
import com.alanpeinejr.encryption.IEncrypt;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;


import java.lang.reflect.Type;
import java.time.Instant;

/**
 * Encrypts and decrypts, serializes, deserializes to give you tokens.
 */
public class TokenManager {

    private final Gson gson;
    private final int tokenLifeTimeSeconds;

    public TokenManager(int tokenLifeTimeSeconds) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantTypeConverter());
        gson = gsonBuilder.create();
        this.tokenLifeTimeSeconds = tokenLifeTimeSeconds;
    }

    public String getTokenString(User user, IEncrypt encryptor) {
        String jsonToken = gson.toJson(new Token(user.getUsername(), user.getRoles(), user.getLoginTime().plusSeconds(tokenLifeTimeSeconds)));
        return encryptor.encrypt(jsonToken);
    }

    public Token readTokenString(String token, IDecrypt decryptor) throws JsonSyntaxException {
        return gson.fromJson(decryptor.decrypt(token), Token.class);
    }


    /**
     * Private class to serialize instants into just an epoch sec long.
     */
    private static class InstantTypeConverter
            implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
        @Override
        public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
            return new JsonPrimitive(src.getEpochSecond());
        }

        @Override
        public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            return Instant.ofEpochSecond(json.getAsLong());
        }
    }

}
