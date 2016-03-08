package com.flipboard.goldengate;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebView;

import java.util.Random;

/**
 * Base class with a couple helper methods for the generated bridges
 */
public abstract class JavaScriptBridge {

    private static JsonSerializer defaultJsonSerializer;

    private final Random random = new Random();
    protected final WebView webView;
    private final JsonSerializer jsonSerializer;

    public JavaScriptBridge(WebView webView) {
        this(webView, defaultJsonSerializer != null ? defaultJsonSerializer : new GsonJsonSerializer());
    }

    public JavaScriptBridge(WebView webView, JsonSerializer jsonSerializer) {
        this.webView = webView;
        this.jsonSerializer = jsonSerializer;
    }

    protected <T> String toJson(T stuff) {
        return jsonSerializer.toJson(stuff);
    }

    protected <T> T fromJson(String json, Class<T> type) {
        return jsonSerializer.fromJson(json, type);
    }

    public static void setJsonSerializer(JsonSerializer serializer) {
        defaultJsonSerializer = serializer;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected static void evaluateJavascript(WebView webView, String javascript) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.evaluateJavascript(javascript, null);
        } else  {
            webView.loadUrl("javascript:" + javascript);
        }
    }

}
