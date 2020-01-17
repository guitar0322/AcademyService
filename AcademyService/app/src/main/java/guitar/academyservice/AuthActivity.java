package guitar.academyservice;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d("javascript_test", cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }
        });
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

        @JavascriptInterface
        public void requestData() { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("javascript_test", "데이터 요청");
                }
            });
        }

        @JavascriptInterface
        public void saveData(final String item, final int num) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Log.d("HybridApp", "데이터 저장");
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
