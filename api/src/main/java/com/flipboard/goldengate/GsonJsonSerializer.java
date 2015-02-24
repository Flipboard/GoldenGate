package com.flipboard.goldengate;

import com.google.gson.Gson;

public class GsonJsonSerializer implements JsonSerializer {
    
    private Gson gson = new Gson();

    public <T> String toJson(T stuff) {
        return gson.toJson(stuff);
    }

    public <T> T fromJson(String json, Class<T> type) {
        return gson.fromJson(json, type);
    }

}
