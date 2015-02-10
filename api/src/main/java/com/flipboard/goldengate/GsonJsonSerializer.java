package com.flipboard.goldengate;

import com.google.gson.Gson;

public class GsonJsonSerializer implements JsonSerializer {

    public <T> String toJson(T stuff) {
        return new Gson().toJson(stuff);
    }

    public <T> T fromJson(String json, Class<T> type) {
        return new Gson().fromJson(json, type);
    }

}
