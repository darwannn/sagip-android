package com.example.sagip;
import android.app.Activity;
import android.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CustomDialog {

    public interface OnPositiveButtonClickListener {
        void onPositiveButtonClick();
    }

    public static void showAlertDialog(Activity activity, String title, String message, String positiveButtonText, final OnPositiveButtonClickListener positiveButtonClickListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.custom_dialog_layout, null);


       // TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
      //  titleTextView.setText(title);
        messageTextView.setText(message);


        Button positiveButton = view.findViewById(R.id.positiveButton);
        Button negativeButton = view.findViewById(R.id.negativeButton);
        positiveButton.setText(positiveButtonText);
        negativeButton.setText("Cancel");

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.setCanceledOnTouchOutside(false);


        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButtonClickListener != null) {
                    positiveButtonClickListener.onPositiveButtonClick();
                }
                dialog.dismiss();
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
