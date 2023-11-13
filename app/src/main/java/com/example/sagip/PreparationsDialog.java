package com.example.sagip;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class PreparationsDialog {
    public static MainActivity mainActivity;
    public static Boolean locationOnUpdate, locationEnabledUpdate, cameraEnabledUpdate =  false;
    private static AlertDialog currentDialog;
    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick();
    }

    public static void showAlertDialog(MainActivity activity, String action, boolean locationEnabled, boolean cameraEnabled, boolean locationOn) {
        mainActivity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.preparations_dialog_layout, null);


        Button prePositiveButton = view.findViewById(R.id.prePositiveButton);
        Button preNegativeButton = view.findViewById(R.id.preNegativeButton);

        LinearLayout cameraPermissionButton = view.findViewById(R.id.cameraPermissionButton);
        LinearLayout locationDisabledButton = view.findViewById(R.id.locationDisabledButton);
        LinearLayout locationPermissionButton = view.findViewById(R.id.locationPermissionButton);

      LinearLayout cameraPermissionLayout = view.findViewById(R.id.cameraPermissionLayout);
        LinearLayout locationPermissionLayout = view.findViewById(R.id.locationPermissionLayout);
    LinearLayout locationDisabledLayout = view.findViewById(R.id.locationDisabledLayout);

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
            preNegativeButton.setVisibility(View.GONE);
        } else {
            prePositiveButton.setText("Continue");
            preNegativeButton.setVisibility(View.VISIBLE);
        }
        prePositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(activity, "Click", Toast.LENGTH_SHORT).show();
                if(!(action.equals("onLoad")) ){
                    if(locationOnUpdate && locationEnabledUpdate && cameraEnabledUpdate) {
                        prePositiveButton.setEnabled(true);
                        prePositiveButton.setBackgroundResource(R.drawable.rounded_button);
                       // Toast.makeText(activity, "activity", Toast.LENGTH_SHORT).show();
                        if (mainActivity != null) {
                            //Toast.makeText(activity, "activity1", Toast.LENGTH_SHORT).show();
                            activity.changeWebViewUrl("https://www.sagip.me/emergency-reports");
                        }
                    }else {
                        //Toast.makeText(activity, "elseeee", Toast.LENGTH_SHORT).show();
                        prePositiveButton.setEnabled(false);
                        prePositiveButton.setBackgroundResource(R.drawable.rounded_button_disabled);
                    }
                    dialog.dismiss();
                } else {
                    //Toast.makeText(activity, "else", Toast.LENGTH_SHORT).show();
                    prePositiveButton.setEnabled(false);
                    dialog.dismiss();
                }

            }
        });

        preNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
//
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
        if (currentDialog != null && currentDialog.isShowing()) {
            ImageView cameraPermissionArrow = currentDialog.findViewById(R.id.cameraPermissionArrow);
            ImageView locationDisabledArrow = currentDialog.findViewById(R.id.locationDisabledArrow);
            ImageView locationPermissionArrow = currentDialog.findViewById(R.id.locationPermissionArrow);

            ImageView cameraPermissionCheck = currentDialog.findViewById(R.id.cameraPermissionCheck);
            ImageView locationDisabledCheck = currentDialog.findViewById(R.id.locationDisabledCheck);
            ImageView locationPermissionCheck = currentDialog.findViewById(R.id.locationPermissionCheck);

            if (locationDisabledArrow != null && locationDisabledCheck != null) {
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

            if (cameraPermissionArrow != null && cameraPermissionCheck != null) {

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

            if (locationPermissionArrow != null && locationPermissionCheck != null) {
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

            Button prePositiveButton = currentDialog.findViewById(R.id.prePositiveButton);
            if (prePositiveButton != null) {
                if (locationOn && locationEnabled && cameraEnabled) {
                    prePositiveButton.setEnabled(true);
                    prePositiveButton.setBackgroundResource(R.drawable.rounded_button);
                } else {
                    prePositiveButton.setEnabled(false);
                    prePositiveButton.setBackgroundResource(R.drawable.rounded_button_disabled);
                }
            }
        }
    }


}

