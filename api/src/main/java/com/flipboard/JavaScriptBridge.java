package com.flipboard;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Base class with a couple helper methods for the generated bridges
 */
public abstract class JavaScriptBridge {

    private static JsonSerializer jsonSerializer;

    public class ResultBridge {

        private Map<String, Callback<String>> callbacks = new HashMap<>();

        public void registerCallback(String receiver, Callback<String> cb) {
            callbacks.put(receiver, cb);
        }

        @JavascriptInterface
        public void onResult(String result) {
            try {
                JSONObject json = new JSONObject(result);
                String receiver = json.getString("receiver");
                String realResult = json.get("result").toString();

                callbacks.remove(receiver).onResult(realResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private final Random random = new Random();
    protected final WebView webView;
    protected final JavaScriptBridge.ResultBridge resultBridge;

    public JavaScriptBridge(WebView webView) {
        this.webView = webView;
        this.resultBridge = new JavaScriptBridge.ResultBridge();
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

    protected String randomUUID() {
        byte[] randBytes = new byte[64];
        random.nextBytes(randBytes);
        return new String(randBytes);
    }

    public static void setJsonSerializer(JsonSerializer serializer) {
        jsonSerializer = serializer;
    }

}
