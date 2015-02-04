package com.flipboard;

public interface JsonSerializer {

    <T> String toJson(T stuff);

    <T> T fromJson(String json, Class<T> type);

}
