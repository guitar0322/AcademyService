package com.gongamhada.parent;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(new AndroidBridge(webView, true), "Android");
        webView.loadUrl("file:///android_asset/index.html");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new MyWebViewClient());
    }
    public class AndroidBridge {
        private final Handler handler = new Handler();
        private WebView mWebView;
        private  boolean newtwork;

        // 생성자
        public AndroidBridge(WebView mWebView, boolean newtwork) {
            this.mWebView = mWebView;
            this.newtwork = newtwork;
        }

        // DB데이터 가져오기
        @JavascriptInterface
        public void requestData() { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("HybridApp", "데이터 요청");
                    mWebView.loadUrl("javascript:getAndroidData('"+"test"+"')");
                }
            });
        }

        // DB에 데이터 저장하기
        @JavascriptInterface
        public void saveData(final String item, final int num) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("HybridApp", "데이터 저장");
                    mWebView.loadUrl("javascript:getAndroidData('"+"test"+"')");
                }
            });
        }
    }

    public class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Log.d("auth_test", url);
            view.loadUrl(url);
            return true;
        }
    }
}
