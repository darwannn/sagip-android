package com.example.sagip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.PusherEvent;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private WebView sagipWebView;
    private EditText searchBar;

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";

    private Pusher pusher;
    private Channel channel;
    private String fcmToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // removes action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //invoke functions
        getFcmToken();
        createNotificationChannel();

        receivePusher();

        sagipWebView();
    }


    // ----------- override

    @Override
    public void onBackPressed() {
        if (sagipWebView.canGoBack()) {
            sagipWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pusher != null) {
            pusher.disconnect();
        }
    }

    // ----------- functions

    // get FCM token assigned to the device
    private void getFcmToken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.d(TAG, "onComplete: Failed to get the Token");
                }
                fcmToken = task.getResult();
                Log.v("myTag",fcmToken);
                Toast.makeText(MainActivity.this, ""+fcmToken, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "sagipNotificationChannel";
            String description = "Receive Firebase Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

//    app_id = "1614935"
//    key = "83ffcdeb54b7ffa56946"
//    secret = "0844c188d0d0bf89aee3"
//    cluster = "ap1"

    private void receivePusher() {
        PusherOptions options = new PusherOptions();
        options.setCluster(BuildConfig.PUSHER_CLUSTER);

        pusher = new Pusher(BuildConfig.PUSHER_KEY, options);
        pusher.connect(new ConnectionEventListener() {
            @Override
            public void onConnectionStateChange(ConnectionStateChange change) {
                Log.i("Pusher", "State changed from " + change.getPreviousState() +
                        " to " + change.getCurrentState());
            }
            @Override
            public void onError(String message, String code, Exception e) {
                Log.i("Pusher", "There was a problem connecting!" +
                        "\ncode: " + code +
                        "\nmessage: " + message +
                        "\nException: " + e);
            }
        }, ConnectionState.ALL);

        channel = pusher.subscribe("sagipChannel");
        channel.bind("sagipEvent", new SubscriptionEventListener() {
            @Override
            public void onEvent(PusherEvent event) {
                try {
                    JSONObject eventData = new JSONObject(event.getData());

                    String from = eventData.getString("from");
                    String to = eventData.getString("to");
                    String purpose = eventData.getString("purpose");

                    JSONObject contentObject = eventData.getJSONObject("content");
                    double latitude = contentObject.getDouble("latitude");
                    double longitude = contentObject.getDouble("longitude");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "from: " + from, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing event data: " + e.toString());
                }
            }
        });
    }

    private void sagipWebView() {
        sagipWebView = findViewById(R.id.sagipWebView);
        WebSettings webSettings = sagipWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        sagipWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("WebView", consoleMessage.message()); // Log the console message
                return true;
            }
        });

        sagipWebView.addJavascriptInterface(MainActivity.this, "AndroidInterface");
        sagipWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
        });
        webSettings.setGeolocationEnabled(true);

        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String urlOrSearchTerm = searchBar.getText().toString();
                    loadUrl(urlOrSearchTerm);
                    return true;
                }
                return false;
            }
        });
    }

    private void loadUrl(String urlOrSearchTerm) {
        sagipWebView.loadUrl(urlOrSearchTerm);
    }

    // ----------- javascript interface

    //put request
    @JavascriptInterface
    public void updateFcmToken(String identifier) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Identifier: " + identifier, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "https://sagip.onrender.com/account/fcm";

                Map<String, String> params = new HashMap<>();
                params.put("identifier", identifier);
                params.put("fcmToken", fcmToken);

                StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                        response -> {
                            Log.d("Response", response);
                        },
                        error -> {
                            Log.e("Error", error.toString());
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        return params;
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/x-www-form-urlencoded");
                        return headers;
                    }
                };
                queue.add(putRequest);
            }
        });
    }

    public void vibrateOnHold() {
        Vibrator vibrator = (Vibrator) getSystemService(MainActivity.this.VIBRATOR_SERVICE);
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }

}
