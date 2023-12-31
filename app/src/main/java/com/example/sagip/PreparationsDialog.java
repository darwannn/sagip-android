package com.example.sagip;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class PreparationsDialog {
    public static MainActivity mainActivity;
    public static Boolean locationOnUpdate, locationEnabledUpdate, cameraEnabledUpdate =  false;
    public static String actionUpdate = "";
    private static AlertDialog currentDialog;

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick();
    }

    public static Boolean locationOnInitial, locationEnabledInitial, cameraEnabledInitial =  false;

    public static void showAlertDialog(MainActivity activity, String action, boolean locationEnabled, boolean cameraEnabled, boolean locationOn) {
        actionUpdate = action;
        mainActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.preparations_dialog_layout, null);


        Button prePositiveButton = view.findViewById(R.id.prePositiveButton);
        Button preNegativeButton = view.findViewById(R.id.preNegativeButton);

        ImageView cameraPermissionButton = view.findViewById(R.id.cameraPermissionArrow);
        ImageView locationDisabledButton = view.findViewById(R.id.locationDisabledArrow);
        ImageView locationPermissionButton = view.findViewById(R.id.locationPermissionArrow);

        LinearLayout cameraPermissionLayout = view.findViewById(R.id.cameraPermissionLayout);
        LinearLayout locationPermissionLayout = view.findViewById(R.id.locationPermissionLayout);
        LinearLayout locationDisabledLayout = view.findViewById(R.id.locationDisabledLayout);
        locationOnInitial= locationOn;
        locationEnabledInitial=locationEnabled;
        cameraEnabledInitial=cameraEnabled;

        if (locationEnabled) {
            locationPermissionLayout.setVisibility(View.GONE);
        }

        if (locationOn) {
            locationDisabledLayout.setVisibility(View.GONE);
        }

        if (cameraEnabled) {
            cameraPermissionLayout.setVisibility(View.GONE);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        setCurrentDialog(dialog);



        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.setCanceledOnTouchOutside(false);
        if(action.equals("onLoad") ){
            prePositiveButton.setText("Close");
            prePositiveButton.setEnabled(true);
            prePositiveButton.setBackgroundResource(R.drawable.rounded_button);
            preNegativeButton.setVisibility(View.GONE);
        } else {
            prePositiveButton.setText("Continue");
            prePositiveButton.setEnabled(false);

            preNegativeButton.setVisibility(View.VISIBLE);
            if(locationOn && locationEnabled && cameraEnabled) {
                prePositiveButton.setBackgroundResource(R.drawable.rounded_button);
            }else {
                prePositiveButton.setBackgroundResource(R.drawable.rounded_button_disabled);
            }
        }
        prePositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(action.equals("onLoad"))) {
                    if (locationOnUpdate && locationEnabledUpdate && cameraEnabledUpdate) {
                        prePositiveButton.setBackgroundResource(R.drawable.rounded_button);

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mainActivity != null) {
                                    if (action.equals("request")) {
                                       // activity.changeWebViewUrl("https://www.sagip.live/emergency-reports");
                                    }
//                                    else {
//                                        activity.changeWebViewUrl("https://www.sagip.live/hazard-reports");
//                                    }
                                }
                            }
                        });
                    } else {
                        prePositiveButton.setBackgroundResource(R.drawable.rounded_button_disabled);
                    }
                    dialog.dismiss();
                } else {
                    prePositiveButton.setBackgroundResource(R.drawable.rounded_button);
                    dialog.dismiss();
                }
            }
        });


        preNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (action.equals("request")) {
                            activity.changeWebViewUrl("https://www.sagip.live/home");
                        }
                        else {
                            activity.changeWebViewUrl("https://www.sagip.live/hazard-reports");
                        }
                    }
                });


                dialog.dismiss();
            }
        });

        cameraPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivity.openAppSettings();
            }
        });

        locationPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivity.openAppSettings();
            }
        });

        locationDisabledButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainActivity.openLocationSettings();
            }
        });

//        if(!(locationOn && locationEnabled && locationEnabled)) {
//            prePositiveButton.setEnabled(true);
//        } else {
//            prePositiveButton.setEnabled(false);
//        }
        dialog.show();
    }

    private static void setCurrentDialog(AlertDialog dialog) {
        currentDialog = dialog;
    }

    public static void updateDialogLayout(boolean locationOn, boolean cameraEnabled, boolean locationEnabled) {
        if (mainActivity != null && currentDialog != null && currentDialog.isShowing()) {
            try {
                ImageView cameraPermissionArrow = currentDialog.findViewById(R.id.cameraPermissionArrow);
                ImageView locationDisabledArrow = currentDialog.findViewById(R.id.locationDisabledArrow);
                ImageView locationPermissionArrow = currentDialog.findViewById(R.id.locationPermissionArrow);
                ImageView cameraPermissionCheck = currentDialog.findViewById(R.id.cameraPermissionCheck);
                ImageView locationDisabledCheck = currentDialog.findViewById(R.id.locationDisabledCheck);
                ImageView locationPermissionCheck = currentDialog.findViewById(R.id.locationPermissionCheck);
                if (locationPermissionArrow != null && locationPermissionCheck != null) {
                    currentDialog.findViewById(R.id.locationPermissionLayout).post(new Runnable() {
                        @Override
                        public void run() {
                            if (locationEnabled) {
                                locationEnabledUpdate = true;
                                locationPermissionArrow.setVisibility(View.GONE);
                                locationPermissionCheck.setVisibility(View.VISIBLE);
                            } else {
                                locationEnabledUpdate = false;
                                locationPermissionArrow.setVisibility(View.VISIBLE);
                                locationPermissionCheck.setVisibility(View.GONE);
                            }
                        }
                    });
                }


                if (cameraPermissionArrow != null && cameraPermissionCheck != null) {
                    currentDialog.findViewById(R.id.cameraPermissionLayout).post(new Runnable() {
                        @Override
                        public void run() {
                            if (cameraEnabled) {
                                cameraEnabledUpdate = true;
                                cameraPermissionArrow.setVisibility(View.GONE);
                                cameraPermissionCheck.setVisibility(View.VISIBLE);
                            } else {
                                cameraEnabledUpdate = false;
                                cameraPermissionArrow.setVisibility(View.VISIBLE);
                                cameraPermissionCheck.setVisibility(View.GONE);
                            }
                        }

                    });
                }

                if (locationDisabledArrow != null && locationDisabledCheck != null) {
                    currentDialog.findViewById(R.id.locationDisabledLayout).post(new Runnable() {
                        @Override
                        public void run() {
                            if (locationOn) {
                                locationOnUpdate = true;
                                locationDisabledArrow.setVisibility(View.GONE);
                                locationDisabledCheck.setVisibility(View.VISIBLE);
                            } else {
                                locationOnUpdate = false;
                                locationDisabledArrow.setVisibility(View.VISIBLE);
                                locationDisabledCheck.setVisibility(View.GONE);
                            }
                        }
                    });
                }

                currentDialog.findViewById(R.id.prePositiveButton).post(new Runnable() {
                    @Override
                    public void run() {
                        Button prePositiveButton = currentDialog.findViewById(R.id.prePositiveButton);
                        if (prePositiveButton != null) {
                            if (locationOn && locationEnabled && cameraEnabled) {
                                prePositiveButton.setEnabled(true);
                                prePositiveButton.setBackgroundResource(R.drawable.rounded_button);
                            } else {
                                if (actionUpdate.equals("onLoad")) {
                                    prePositiveButton.setEnabled(true);
                                    prePositiveButton.setBackgroundResource(R.drawable.rounded_button);
                                } else {
                                    prePositiveButton.setEnabled(false);
                                    prePositiveButton.setBackgroundResource(R.drawable.rounded_button_disabled);
                                }

                            }
                        }
                    }
                });

        } catch(Exception e){
            e.printStackTrace();
            Log.e("UpdateDialog", "Exception: " + e.getMessage());
        }
    }
    }



}

