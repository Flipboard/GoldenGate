package com.flipboard.goldengate.sample;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.flipboard.goldengate.Callback;


public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final WebView webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        final SampleBridge bridge = new SampleBridge(webview);
        webview.loadUrl("https://flipboard.com/@news/the-daily-edition-3adc9613z");
        webview.postDelayed(new Runnable() {
            @Override
            public void run() {
                bridge.getNavigator(new Callback<Navigator>() {
                    @Override
                    public void onResult(Navigator result) {
                        Toast.makeText(MainActivity.this, result.platform, Toast.LENGTH_SHORT).show();
                    }
                });
                bridge.getWindowWidth(new Callback<Float>() {
                    @Override
                    public void onResult(Float result) {
                        Toast.makeText(MainActivity.this, "window width = " + result, Toast.LENGTH_SHORT).show();
                    }
                });
                bridge.looptyLoop(new Callback<Integer>() {
                    @Override
                    public void onResult(Integer val) {
                        Log.d("looptyLoop", "val: " + val);
                    }
                });
                bridge.alert("tjena");
            }
        }, 1000);
    }
}
