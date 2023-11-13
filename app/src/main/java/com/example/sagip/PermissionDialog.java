package com.example.sagip;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class PermissionDialog {

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick();
    }

    public static void showAlertDialog(Activity activity, String title, String message, String intentType, String positiveButtonText, final OnPositiveButtonClickListener positiveButtonClickListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.permission_dialog_layout, null);
//        ImageView imageView = view.findViewById(R.id.imageView);

        TextView perTitleTextView = view.findViewById(R.id.perTitleTextView);
        TextView perMessageTextView = view.findViewById(R.id.perMessageTextView);
        Button perPositiveButton = view.findViewById(R.id.perPositiveButton);
        Button perNegativeButton = view.findViewById(R.id.perNegativeButton);

        perTitleTextView.setText(title);
        perMessageTextView.setText(message);


//        if ("Camera Permission".equals(title)) {
//            imageView.setImageResource(R.drawable.call_red_icon);
//        } else if ("Location Permission".equals(title)) {
//            imageView.setImageResource(R.drawable.fire_truck);
//        }else if ("Location Disabled".equals(title)) {
//            imageView.setImageResource(R.drawable.fire_truck);
//        }


        perPositiveButton.setText(positiveButtonText);
        perNegativeButton.setText("Cancel");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.setCanceledOnTouchOutside(false);


        perPositiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButtonClickListener != null) {
                    positiveButtonClickListener.onPositiveButtonClick();
                }
                dialog.dismiss();
            }
        });
        perNegativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
