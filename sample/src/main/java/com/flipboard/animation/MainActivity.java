package com.flipboard.animation;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.flipboard.Callback;
import com.flipboard.bridge.FlipmagBridge;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient());

        final FlipmagBridge bridge = new FlipmagBridge(webview);
        bridge.getWindowWidth(new Callback<Float>() {
            @Override
            public void onResult(Float result) {
                Toast.makeText(MainActivity.this, "window width = " + result, Toast.LENGTH_SHORT).show();
            }
        });
        bridge.alert("tjena");
    }
}
