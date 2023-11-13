package com.example.sagip;
import android.app.Activity;
import android.app.AlertDialog;
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

public class PreparationsDialog {

    private static AlertDialog currentDialog;
    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick();
    }

    public static void showAlertDialog(Activity activity, boolean locationEnabled, boolean cameraEnabled, boolean locationOn) {
        View view = LayoutInflater.from(activity).inflate(R.layout.preparations_dialog_layout, null);
        ImageView imageView = view.findViewById(R.id.imageView);

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
        dialog.show();
    }

    private static void setCurrentDialog(AlertDialog dialog) {
        currentDialog = dialog;
    }

    public static void updateDialogLayout(boolean locationOn,boolean cameraEnabled,boolean locationEnabled) {
        if (currentDialog != null && currentDialog.isShowing()) {
            currentDialog.findViewById(R.id.locationDisabledLayout).post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout locationDisabledLayout = currentDialog.findViewById(R.id.locationDisabledLayout);

                    if (locationOn) {
                        locationDisabledLayout.setVisibility(View.GONE);
                    } else {
                        locationDisabledLayout.setVisibility(View.VISIBLE);
                    }

                }
            });

            currentDialog.findViewById(R.id.cameraPermissionLayout).post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout cameraPermissionLayout = currentDialog.findViewById(R.id.cameraPermissionLayout);

                    if (cameraEnabled) {
                        cameraPermissionLayout.setVisibility(View.GONE);
                    } else {
                        cameraPermissionLayout.setVisibility(View.VISIBLE);
                    }

                }
            });

            currentDialog.findViewById(R.id.locationDisabledLayout).post(new Runnable() {
                @Override
                public void run() {
                    LinearLayout locationPermissionLayout = currentDialog.findViewById(R.id.locationPermissionLayout);

                    if (locationEnabled) {
                        locationPermissionLayout.setVisibility(View.GONE);
                    } else {
                        locationPermissionLayout.setVisibility(View.VISIBLE);
                    }

                }
            });
        }
    }

}

