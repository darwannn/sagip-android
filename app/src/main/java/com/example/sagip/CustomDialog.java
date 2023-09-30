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
        // Inflate the custom layout for the dialog
        View view = LayoutInflater.from(activity).inflate(R.layout.custom_dialog_layout, null);

        // Set the title and message
       // TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);
      //  titleTextView.setText(title);
        messageTextView.setText(message);

        // Set up the buttons
        Button positiveButton = view.findViewById(R.id.positiveButton);
        Button negativeButton = view.findViewById(R.id.negativeButton);
        positiveButton.setText(positiveButtonText);
        negativeButton.setText("Cancel");

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);

        // Create and show the dialog
        final AlertDialog dialog = builder.create();

        // Set dialog window parameters to position at the bottom
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Make the dialog not cancelable outside touch
        dialog.setCanceledOnTouchOutside(false);

        // Set click listener for positive button
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (positiveButtonClickListener != null) {
                    positiveButtonClickListener.onPositiveButtonClick();
                }
                dialog.dismiss(); // Dismiss the dialog
            }
        });
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // Dismiss the dialog
            }
        });
        dialog.show();
    }
}
