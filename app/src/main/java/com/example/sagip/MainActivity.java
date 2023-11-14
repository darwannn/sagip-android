package com.example.sagip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.location.LocationManagerCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 1006;
    private static final int CAMERA_PERMISSION_REQUEST = 300;

    private NetworkReceiver networkStateChangeReceiver;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private ConnectivityManager connectivityManager;
    private AudioManager audioManager;
    private WebView sagipWebView;
    private LinearLayout mainLayout;
    private RelativeLayout onBoardingLayout;
    private EditText searchBar;

    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1003;
    public static final String EXTRA_CLOSE_PREVIOUS_INSTANCE = "close_previous_instance";

    private String fcmToken;
    public static boolean isMainActivityActive = false;
    private String mediaChooser;
    private static final String TAGGG = MainActivity.class.getSimpleName();

    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    private boolean isAppInRecent;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            // android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            //android.Manifest.permission.ACCESS_COARSE_LOCATION,
            //android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            //android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.POST_NOTIFICATIONS
    };

    private Button startButton;
    private Button stopButton;

    private PermissionBroadcastReceiver permissionReceiver;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isAppInRecent = true;
        permissionReceiver = new PermissionBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        intentFilter.addAction("com.example.sagip.PERMISSION_CHANGED");
        registerReceiver(permissionReceiver, intentFilter);

        SocketManager.connectSocket();
        //isLocationEnabled("onLoad");

           // permissionPreparation("onLoad");

        networkStateChangeReceiver = new NetworkReceiver();
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        String retrievedMyToken = PersistentStorage.getFromPersistentStorage(this, "myToken");
        String retrievedUserId = PersistentStorage.getFromPersistentStorage(this, "userId");
        String retrievedAssistanceReqId = PersistentStorage.getFromPersistentStorage(this, "assistanceReqId");
        if (!TextUtils.isEmpty(retrievedUserId) && !TextUtils.isEmpty(retrievedAssistanceReqId)) {
            startSharingLocation(retrievedMyToken,retrievedUserId,retrievedAssistanceReqId);

        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Location services are enabled, proceed with location-related operations
              //  isCameraEnabled();
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startSharingLocation("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0YXJnZXQiOiJsb2dpbiIsImlkIjoiNjQ3ODhkZmQyOTVlMmYxODRlNTVkMjBmIiwidXNlclR5cGUiOiJyZXNwb25kZXIiLCJzdGF0dXMiOiJ2ZXJpZmllZCIsImlkZW50aWZpZXIiOiIiLCJpYXQiOjE2OTUxMjU2NTMsImV4cCI6MTY5NTczMDQ1M30.sKDakxziMbSR7ckgDjhuzpRZyL9GjT3G4mQqAMbEQqU", "64788dfd295e2f184e55d20f","65082993d2c94183011a5412");
                    }
                });

            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSharingLocation();
               // isLocationEnabled("responder");

            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //isWifiEnabled();

        //invoke functions
        getFcmToken();
        subscribeToTopic();
        createNotificationChannel();


        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        mainLayout = findViewById(R.id.mainLayout);
        onBoardingLayout = findViewById(R.id.onBoardingLayout);
        sagipWebView = findViewById(R.id.sagipWebView);
        WebSettings webSettings = sagipWebView.getSettings();
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        sagipWebView.clearCache(true);
        sagipWebView.clearHistory();

        webSettings.setUseWideViewPort(true);
        webSettings.setAllowFileAccess(true);

        webSettings.setDomStorageEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);


        webSettings.setAllowContentAccess(true);
        sagipWebView.addJavascriptInterface(MainActivity.this, "AndroidInterface");

        sagipWebView.setWebChromeClient(new WebChromeClient() {
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (!isCameraEnabled("true")) {

                    return false;
                }
                if (mUMA != null) {
                    mUMA.onReceiveValue(null);
                }
                mUMA = filePathCallback;

                String[] mimeTypes = {"image/*"};
                Intent intent = null;

                if (mediaChooser.equals("camera")) {

                        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(MainActivity.this.getPackageManager()) != null) {

                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                Log.e(TAGGG, "Image file creation failed", ex);
                            }
                            if (photoFile != null) {
                                mCM = "file:" + photoFile.getAbsolutePath();

                                Uri imageUri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            } else {
                                intent = null;
                            }
                        } else {

                        }

                } else if (mediaChooser.equals("camcorder")) {

                        intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
                        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

                } else {

                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }

                if (intent != null) {
                    startActivityForResult(intent, FCR);

                    return true;
                } else {
                    return false;
                }
            }


            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }


            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            protected FrameLayout mFullscreenContainer;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;


            public Bitmap getDefaultVideoPoster()
            {
                if (mCustomView == null) {
                    return null;
                }
                return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
            }

            public void onHideCustomView()
            {
                ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
            }

            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback)
            {
                if (this.mCustomView != null)
                {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.equals("https://www.sagip.me/")) {
                    view.clearCache(true);
//                onBoardingLayout.setVisibility(View.GONE);
//                mainLayout.setVisibility(View.VISIBLE);

//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
                            onBoardingLayout.setVisibility(View.GONE);
                            mainLayout.setVisibility(View.VISIBLE);
                           // isLocationEnabled("onLoad");
//                        }
//                    }, 3000);

//                AnimationUtils.applyFadeAnimation(onBoardingLayout, 0);
//                AnimationUtils.applyFadeAnimation(mainLayout, 1);
                }
            }

        });


        searchBar = findViewById(R.id.searchBar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    String urlOrSearchTerm = searchBar.getText().toString();
                    loadUrl(urlOrSearchTerm);
                    //Toast.makeText(MainActivity.this, ""+urlOrSearchTerm, Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            }
        });

        String url = getIntent().getStringExtra(ForegroundService.KEY_URL);
        if (url != null && !url.isEmpty()) {
            sagipWebView.loadUrl(url);
        } else {
            sagipWebView.loadUrl("https://www.sagip.me/");

        }


    }

    public void changeWebViewUrl(String newUrl) {
        sagipWebView.loadUrl(newUrl);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            // Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        // Capture Photo if no image available
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
                if (null == mUMA) return;
                Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
                mUMA.onReceiveValue(new Uri[]{result});
                mUMA = null;
            }
        }
    }


    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String imageFileName = "IMG" + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File imageFile = new File(storageDir, imageFileName); // Create a temporary image file
        return imageFile; // Return the created file
    }

    @Override
    public void onBackPressed() {
        if (sagipWebView.canGoBack()) {
            sagipWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }


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
    protected void onStart() {
        super.onStart();
        isAppInRecent = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketManager.disconnectSocket();
        if (permissionReceiver != null) {
            unregisterReceiver(permissionReceiver);
        }

        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isMainActivityActive = true;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateChangeReceiver, intentFilter);
        PreparationsDialog.updateDialogLayout(isLocationOn("false"), isCameraEnabled("false"),isLocationEnabled("false","resident"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainActivityActive = false;
        unregisterReceiver(networkStateChangeReceiver);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        sagipWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        sagipWebView.restoreState(savedInstanceState);
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
               // PreparationsDialog.updateDialogLayout(isLocationOn("false"), isCameraEnabled("false"),isLocationEnabled("false","resident"));
            }
        } else if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle notification-related tasks
            } else {
               // PreparationsDialog.updateDialogLayout(isLocationOn("false"), isCameraEnabled("false"),isLocationEnabled("false","resident"));

            }
        } else if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
           if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, handle location-related tasks
            } else {
               // PreparationsDialog.updateDialogLayout(isLocationOn("false"), isCameraEnabled("false"),isLocationEnabled("false","resident"));

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
                    fcmToken = null;  // Set the token to null if fetching fails
                } else {
                    fcmToken = task.getResult();
                    Log.v("myTag", fcmToken);
                    // Toast.makeText(MainActivity.this, "" + fcmToken, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("sagip").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Toast.makeText(MainActivity.this, "subscribed", Toast.LENGTH_SHORT).show();
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

    private void loadUrl(String urlOrSearchTerm) {

        // Run WebView on a separate thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sagipWebView.loadUrl(urlOrSearchTerm);
            }
        }, 1000);
        //sagipWebView.loadUrl(urlOrSearchTerm);
    }


    // ----------- javascript interface

    //put request
    @JavascriptInterface
    public void updateFcmToken(String identifier) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //   Toast.makeText(MainActivity.this, "Identifier: " + identifier, Toast.LENGTH_SHORT).show();
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
    public void removeFcmToken() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //   Toast.makeText(MainActivity.this, "Identifier: " + identifier, Toast.LENGTH_SHORT).show();
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                String url = "https://sagip.onrender.com/account/remove-fcm";

                Map<String, String> params = new HashMap<>();

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
        //  Toast.makeText(this, "showing", Toast.LENGTH_SHORT).show();
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }

    @JavascriptInterface
    public void setMediaChooser(String option) {
        //  Toast.makeText(this, "set to " + option, Toast.LENGTH_SHORT).show();
        mediaChooser = option;
//        Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void routeTo(String lat, String lng) {
        String latitude = lat;
        String longitude = lng;
       // latitude = "14.8527";
       // longitude = "120.8160";
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
          //  Toast.makeText(this, "Google Maps is installed", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this, "Kindly consider installing Google Maps to utilize this functionality", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(this)
                    .setMessage("Kindly consider installing Google Maps to utilize this functionality")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
        }
        startActivity(mapIntent);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @JavascriptInterface
    public void startSharingLocation(String myToken, String userId, String assistanceReqId) {
        if (isLocationEnabled("false","responder")) {

            if (!isServiceRunning(ForegroundService.class)) {

               // Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
                Intent serviceIntent = new Intent(this, ForegroundService.class);
                serviceIntent.putExtra("residentUserId", userId);
                serviceIntent.putExtra("assistanceReqId", assistanceReqId);
                startService(serviceIntent);
                PersistentStorage.saveToPersistentStorage(this, myToken, userId, assistanceReqId);
            } else {
                Toast.makeText(this, "Service is already running", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @JavascriptInterface
    public void stopSharingLocation() {
       // Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
        PersistentStorage.clearPersistentStorage(this);
    }
    private void showAlert(String title, String message, String intentType, String buttonText) {
        PermissionDialog.showAlertDialog(this, title, message,intentType,
                buttonText, new PermissionDialog.OnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        if(intentType.equals("settings") ) {
//                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package", getPackageName(), null);
//                            intent.setData(uri);
//                            startActivity(intent);
                            openAppSettings();
                        } else if(intentType.equals("location")){
//                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                            startActivity(intent);
                            openLocationSettings();
                        }
                    }
                });

    }
    @JavascriptInterface
    public boolean isCameraEnabled(String showAlert) {
        String[] PERMISSIONS = { android.Manifest.permission.CAMERA };

        if (!hasPermissions(this, PERMISSIONS)) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CAMERA)) {

                ActivityCompat.requestPermissions(this, PERMISSIONS, CAMERA_PERMISSION_REQUEST_CODE);

                return false;
            } else {
                if(showAlert.equals("true")) {
                    showAlert("Camera Permission", "To continue, please enable the camera permission in the app settings", "settings", "Open App Settings");
                }

                return false;
            }
        } else {

            return true;
        }
    }
    @JavascriptInterface
    public boolean isLocationEnabled( String showAlert, String userType) {

        String[] RESIDENT_PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        String[] RESPONDER_PERMISSIONS = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };
        if (userType.equals("resident") || userType.equals("onLoad") ) {
            if (!hasPermissions(this, RESIDENT_PERMISSIONS)) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                        ActivityCompat.requestPermissions(this, RESIDENT_PERMISSIONS, LOCATION_PERMISSION_REQUEST_CODE);

                        return false;
                    } else {
                       if(showAlert.equals("true")) {
                           showAlert("Location Permission", "To continue, please enable the location permission in the app settings.", "settings", "Open App Settings");
                       }
                        return false;
                    }
            } else {
                return true;
            }

        }
        if (userType.equals("responder")) {
            if (!hasPermissions(this, RESPONDER_PERMISSIONS)) {
                if(showAlert.equals("true")) {
                    showAlert("Location Permission", "To continue, please enable the location permission in the app settings and set it to  \"Allow all the time\".", "settings", "Open App Settings");
                }
                        return false;

            } else {

                return true;
            }
        }
        return false;
    }
    @JavascriptInterface
    public boolean isLocationOn( String showAlert) {

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) {

            if (showAlert.equals("true")) {
                showAlert("Location Disabled", "To continue, kindly turn on your device location.", "location", "Open Location Settings");
            }


            return false;
        } else {

            return true;
        }
    }



    @JavascriptInterface
    public void playSOS() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
        mediaPlayer = MediaPlayer.create(this, R.raw.sos);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
        isPlaying = true;
    }

    @JavascriptInterface
    public void stopSOS() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        isPlaying = false;
    }

    @JavascriptInterface
    public boolean isInSAGIPApp() {
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNotificationAction(intent);
    }

    private void handleNotificationAction(Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("com.example.sagip.FINISH_ACTIVITY")) {
                // Finish the MainActivity
                finish();
            }
        }
    }

    @JavascriptInterface
    public void openAppSettings () {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    @JavascriptInterface
    public void openLocationSettings () {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @JavascriptInterface
    public boolean permissionPreparation (String action) {

        if (!isLocationEnabled("false", "resident") || !isCameraEnabled("false") || !isLocationOn("false")) {

            PreparationsDialog.showAlertDialog(this, action, isLocationEnabled("false", "resident"), isCameraEnabled("false"), isLocationOn("false"));
            return false;
        } else {
            return true;
        }
    }

    @JavascriptInterface
    public boolean permissionPreparationOnLoad() {
        if(isAppInRecent) {
            return false;
        } else {
            permissionPreparation("onLoad");
            return true;
        }

    }


}
