package com.example.sagip;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 1006;
    public static com.example.sagip.TimerManager TimerManager;

    private NetworkReceiver networkStateChangeReceiver;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private ConnectivityManager connectivityManager;
    private AudioManager audioManager;
    private WebView sagipWebView;
    private EditText searchBar;
    private Timer intervalTimer;
    private static final String TAG = "PushNotification";
    private static final String CHANNEL_ID = "101";
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1002;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1003;
    private Pusher pusher;
    private Channel channel;
    private String fcmToken;
    public static boolean isMainActivityActive = false;
    private String mediaChooser;
    private static final String TAGGG = MainActivity.class.getSimpleName();
    public ValueCallback<Uri> mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 5173;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS,
            // android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.VIBRATE,
            android.Manifest.permission.READ_SMS,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
//            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.POST_NOTIFICATIONS
    };
    String jwtToken, residentUserId;
    private Button startButton;
    private Button stopButton;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;


    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SocketManager.connectSocket();
        networkStateChangeReceiver = new NetworkReceiver();
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Location services are enabled, proceed with location-related operations
                startButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        startSharingLocation("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0YXJnZXQiOiJsb2dpbiIsImlkIjoiNjQ3ODhkZmQyOTVlMmYxODRlNTVkMjBmIiwidXNlclR5cGUiOiJyZXNwb25kZXIiLCJzdGF0dXMiOiJ2ZXJpZmllZCIsImlkZW50aWZpZXIiOiIiLCJpYXQiOjE2OTUxMjU2NTMsImV4cCI6MTY5NTczMDQ1M30.sKDakxziMbSR7ckgDjhuzpRZyL9GjT3G4mQqAMbEQqU", "64788dfd295e2f184e55d20f");
                    }
                });

            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSharingLocation();

            }
        });


        //Button playButton = findViewById(R.id.btnPlay);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        isWifiEnabled();

//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isPlaying) {
//                    playSound();
//                    maximizeVolume();
//                    Toast.makeText(MainActivity.this, "Alarm play", Toast.LENGTH_SHORT).show();
//                    playButton.setText("Stop");
//                } else {
//                    stopSound();
//                    Toast.makeText(MainActivity.this, "Alarm stop", Toast.LENGTH_SHORT).show();
//                    playButton.setText("Play");
//                }
//            }
//        });


        // removes action bar
        //ActionBar actionBar = getSupportActionBar();
        //actionBar.hide();

        // fix error exposed beyond app through ClipData.Item.getUri()
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());

        //invoke functions
        getFcmToken();
        subscribeToTopic();
        createNotificationChannel();


        //sagipWebView();
        //checkLocationEnabled();

        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }


        sagipWebView = findViewById(R.id.sagipWebView);
        WebSettings webSettings = sagipWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

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
                    intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
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


//            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//
//                if (mUMA != null) {
//                    mUMA.onReceiveValue(null);
//                }
//                mUMA = filePathCallback;
//
//                String[] mimeTypes = {"image/*"};
//
//
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.setType("*/*");
//                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//
//                if (intent != null) {
//                    startActivityForResult(intent, FCR);
//
//                    return true;
//                } else {
//                    return false;
//                }
//            }


            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
            }

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
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
                view.clearCache(true);
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

        //change
        String url = getIntent().getStringExtra(ForegroundService.KEY_URL);
        if (url != null && !url.isEmpty()) {
            // Load the URL in the WebView
            sagipWebView.loadUrl(url);
        }

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
    protected void onDestroy() {
        super.onDestroy();
        SocketManager.disconnectSocket();
//        if (pusher != null) {
//            pusher.disconnect();
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isMainActivityActive = true;
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkStateChangeReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMainActivityActive = false;
        unregisterReceiver(networkStateChangeReceiver);
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
                Log.v("myTag", fcmToken);
                //Toast.makeText(MainActivity.this, ""+fcmToken, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("sagip").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Toast.makeText(MainActivity.this, "subascriba", Toast.LENGTH_SHORT).show();
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

    private void checkLocationEnabled() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String[] PERMISSIONS = {

                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        };

        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        } else {
            // Location permission granted, check if location services are enabled
            if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
                Toast.makeText(this, "Location services are not enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Location services are enabled", Toast.LENGTH_SHORT).show();
            }
        }
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
        latitude = "14.8527";
        longitude = "120.8160";
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
            Toast.makeText(this, "Google Maps is installed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Google Maps is installed", Toast.LENGTH_SHORT).show();
        }
        startActivity(mapIntent);
    }

//    @JavascriptInterface
//    public void startSharingLocation(String myToken, String userId) {
//        //isMicrophoneEnabled();
//        // isCameraEnabled();
//        intervalTimer = new Timer();
//        intervalTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                jwtToken = myToken;
//                residentUserId = userId;
//                sendLocationUpdate();
//            }
//        }, 0, 3000);
//        Intent serviceIntent = new Intent(this, ForegroundService.class);
//        serviceIntent.putExtra("inputExtra", "Foreground Service Example");
//        startService(serviceIntent);
//    }

    @JavascriptInterface
    public void startSharingLocation(String myToken, String userId) {
        Toast.makeText(this, "in1", Toast.LENGTH_SHORT).show();
        int backgroundLocationPermission = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        int locationMode = Settings.Secure.getInt(
                getContentResolver(),
                Settings.Secure.LOCATION_MODE,
                Settings.Secure.LOCATION_MODE_OFF
        );

        if (backgroundLocationPermission != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted or user hasn't allowed it "Allow all the time"
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                // Show a message to the user to guide them to enable "Allow all the time"
                Toast.makeText(this, "Please select \"Allow all the time\" for the location permission", Toast.LENGTH_LONG).show();
            }

            // Request the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSIONS_REQUEST_BACKGROUND_LOCATION);
        } else if (locationMode == Settings.Secure.LOCATION_MODE_OFF) {
            Toast.makeText(this, "in2", Toast.LENGTH_SHORT).show();
            // Location services are disabled, prompt the user to enable them
            Toast.makeText(this, "Please enable your location", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        } else {
            Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();



               // jwtToken = myToken;
               // residentUserId = userId;

                // Initialize FusedLocationProviderClient and LocationCallback
        //        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//
//            }
//        };

           //     sendLocationUpdate();

                Intent serviceIntent = new Intent(this, ForegroundService.class);
                serviceIntent.putExtra("residentUserId", userId);
                startService(serviceIntent);

        }
    }


    public void stopSharingLocation() {
        Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show();
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);

        //fusedLocationClient.removeLocationUpdates(locationCallback);
    }


//    public boolean isCameraAndMicEnabled() {
//
//        String[] PERMISSIONS = {
//                android.Manifest.permission.CAMERA,
//                android.Manifest.permission.RECORD_AUDIO,
//        };
//
//        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, "Mic and CAm  denied", Toast.LENGTH_SHORT).show();
//                }
//            });
//
//            return false;
//        } else {
//
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Mic and CAm enabled", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return true;
//
//        }
//    }

    public boolean isCameraEnabled() {

        String[] PERMISSIONS = {
                android.Manifest.permission.CAMERA
        };

        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            // Toast.makeText(MainActivity.this, "CAm  denied", Toast.LENGTH_SHORT).show();
//                }
//            });

            return false;
        } else {


//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            // Toast.makeText(MainActivity.this, "CAm enabled", Toast.LENGTH_SHORT).show();
//                }
//            });
            return true;

        }
    }

    public boolean isMicrophoneEnabled() {

        String[] PERMISSIONS = {
                android.Manifest.permission.RECORD_AUDIO
        };

        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            // Toast.makeText(MainActivity.this, "Mic  denied", Toast.LENGTH_SHORT).show();
//                }
//            });

            return false;
        } else {


//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            // Toast.makeText(MainActivity.this, "Mic enabled", Toast.LENGTH_SHORT).show();
//                }
//            });
            return true;

        }
    }

//    public boolean isLocationEnabled() {
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        String[] PERMISSIONS = {
//                android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION
//        };
//
//        if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
//            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, "Location permission is denied", Toast.LENGTH_SHORT).show();
//                }
//            });
//            intervalTimer.cancel();
//            return false;
//        } else {
//            if (!LocationManagerCompat.isLocationEnabled(locationManager)) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "Location services are not enabled", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                intervalTimer.cancel();
//                return false;
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        // Toast.makeText(MainActivity.this, "Location services are enabled", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return true;
//            }
//        }
//    }

//    public void sendLocationUpdate() {
//        if (isLocationEnabled()) {
//
//
//            LocationRequest locationRequest = LocationRequest.create();
//            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//            locationRequest.setInterval(10000); // Update location every 10 seconds
//
//
//            locationCallback = new LocationCallback() {
//                @Override
//                public void onLocationResult(LocationResult locationResult) {
//                    Location location = locationResult.getLastLocation();
//                    double latitude = location.getLatitude();
//                    double longitude = location.getLongitude();
//                    Log.d(TAG, "latitude " + latitude);
//                    Log.d(TAG, "longitude " + longitude);
//
//                    try {
//                        // Create JSON payload and send location update to the server
//                        JSONObject jsonBody = new JSONObject();
//                        jsonBody.put("receiver", residentUserId);
//                        jsonBody.put("event", "location");
//
//                        JSONObject contentJson = new JSONObject();
//                        contentJson.put("latitude", latitude);
//                        contentJson.put("longitude", longitude);
//
//                        jsonBody.put("content", contentJson);
//                        mSocket.emit("location", jsonBody);
//
//                        // Show location in toast
//                        runOnUiThread(() -> {
//                            Toast.makeText(MainActivity.this, "Lat: " + latitude + " Lng: " + longitude, Toast.LENGTH_SHORT).show();
//                        });
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            };
//
//            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
//                    || ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
//            } else {
//                Toast.makeText(MainActivity.this, "Location permission not granted", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//
//    }

    private void isWifiEnabled() {
//        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//        boolean isWifiOn = wifiNetworkInfo.isConnected();
//
//        if (isWifiOn) {
//            Toast.makeText(this, "Wi-Fi is ON", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "Wi-Fi is OFF", Toast.LENGTH_SHORT).show();
//        }

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {


            //  Toast.makeText(this, "Internet is available!", Toast.LENGTH_SHORT).show();
        }

        if (networkInfo == null || !networkInfo.isConnected()) {

            //  Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }
    }


    public void maximizeVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
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
}
