package com.example.sagip;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewService extends Service {
    private WebView webView;
    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        WebViewService getService() {
            return WebViewService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.example.com");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public WebView getWebView() {
        return webView;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }
}
