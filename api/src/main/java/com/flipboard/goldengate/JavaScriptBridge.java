package com.flipboard.goldengate;

import android.webkit.WebView;

import java.util.Random;

/**
 * Base class with a couple helper methods for the generated bridges
 */
public abstract class JavaScriptBridge {

    private static JsonSerializer jsonSerializer;

    private final Random random = new Random();
    protected final WebView webView;

    public JavaScriptBridge(WebView webView) {
        this.webView = webView;
    }

    protected <T> String toJson(T stuff) {
        if (jsonSerializer == null) {
            jsonSerializer = new GsonJsonSerializer();
        }
        return jsonSerializer.toJson(stuff);
    }

    protected <T> T fromJson(String json, Class<T> type) {
        if (jsonSerializer == null) {
            jsonSerializer = new GsonJsonSerializer();
        }
        return jsonSerializer.fromJson(json, type);
    }

    public static void setJsonSerializer(JsonSerializer serializer) {
        jsonSerializer = serializer;
    }

}
