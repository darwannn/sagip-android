package com.example.sagip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {


    private WebView sagipWebView;
    private EditText searchBar;

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1003;
    private Pusher pusher;
    private Channel channel;
    private String fcmToken;

    private static final String TAGGG = MainActivity.class.getSimpleName();
    public ValueCallback<Uri> mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 5173;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // removes action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        checkAndRequestPermissions();
        //invoke functions
        getFcmToken();
        createNotificationChannel();

        receivePusher();

        //sagipWebView();
        checkLocationEnabled();

        if(!hasPermissions(getApplicationContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        sagipWebView = findViewById(R.id.sagipWebView);
        WebSettings webSettings = sagipWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        sagipWebView.addJavascriptInterface(MainActivity.this, "AndroidInterface");


        sagipWebView.setWebChromeClient(new WebChromeClient(){

            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;

                String[] mimeTypes = {"image/*"};
                Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (imageCaptureIntent.resolveActivity(MainActivity.this.getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.e(TAGGG, "Image file creation failed", ex);
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.getAbsolutePath();
                        imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        imageCaptureIntent = null;
                    }
                }

                Intent videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                videoCaptureIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3);
                videoCaptureIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("*/*");
                contentSelectionIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

                Intent[] intentArray;
                if (imageCaptureIntent != null && videoCaptureIntent != null) {
                    intentArray = new Intent[]{imageCaptureIntent, videoCaptureIntent};
                } else if (imageCaptureIntent != null) {
                    intentArray = new Intent[]{imageCaptureIntent};
                } else if (videoCaptureIntent != null) {
                    intentArray = new Intent[]{videoCaptureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "File Chooser");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooserIntent, FCR);

                return true;
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

        });


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
        });



        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String urlOrSearchTerm = searchBar.getText().toString();
                    loadUrl(urlOrSearchTerm);
                    Toast.makeText(MainActivity.this, ""+urlOrSearchTerm, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);


        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    // Create an image file
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String uniqueSuffix = timeStamp + "_" + Math.round(Math.random() * 1E9);
        String imageName =  uniqueSuffix + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageName, null, storageDir);
    }

    @Override
    public void onBackPressed() {
        if(sagipWebView.canGoBack()) {
            sagipWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.CAMERA
    };

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // ----------- override


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pusher != null) {
            pusher.disconnect();
        }
    }

    // ----------- permission
    private void checkAndRequestPermissions() {
        // Check and request camera permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE
                );
            }
        }

        // Check and request notification permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.VIBRATE},
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }// Check and request location permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }

    // Handle permission request results
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle camera-related tasks
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle notification-related tasks
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle location-related tasks
            } else {
                // Permission denied, handle accordingly (e.g., show a message)
            }
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
                //Toast.makeText(MainActivity.this, ""+fcmToken, Toast.LENGTH_SHORT).show();
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

        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);

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
        });

        sagipWebView.addJavascriptInterface(MainActivity.this, "AndroidInterface");
        sagipWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin,
                                                           GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                // Implement your file chooser logic here
                return true;
            }
//            public void onPermissionRequest(final PermissionRequest request) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    request.grant(request.getResources());
//                }
//            }
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

    private void checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
            Toast.makeText(this, "Location services are not enabled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Location services are enabled", Toast.LENGTH_SHORT).show();
        }
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

    @JavascriptInterface
    public void vibrateOnHold() {
        Vibrator vibrator = (Vibrator) getSystemService(MainActivity.this.VIBRATOR_SERVICE);
        Toast.makeText(this, "showing", Toast.LENGTH_SHORT).show();
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }

//        String latitude = "14.8527";
//        String longitude = "120.8160";
//        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
//
//        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//        mapIntent.setPackage("com.google.android.apps.maps");
//
//        //  if (mapIntent.resolveActivity(getPackageManager()) != null) {
//        startActivity(mapIntent);
//        Toast.makeText(this, "Google Maps is installed", Toast.LENGTH_SHORT).show();
////        } else {
////            Toast.makeText(this, "Google Maps is installed", Toast.LENGTH_SHORT).show();
////        }



//        startActivity(mapIntent);
    }

}

