package com.example.sagip;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class PermissionBroadcastReceiver extends BroadcastReceiver {

   public MainActivity mainActivity;
    public PermissionBroadcastReceiver() {

    }
   public PermissionBroadcastReceiver(MainActivity mainActivity) {
       this.mainActivity = mainActivity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                    //    Toast.makeText(context, "taostify", Toast.LENGTH_SHORT).show();
                        PreparationsDialog.updateDialogLayout(
                                mainActivity.isLocationOn("false"),
                                mainActivity.isCameraEnabled("false"),
                                mainActivity.isLocationEnabled("false", "resident")
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
