package com.example.sagip;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.widget.Toast;

public class PermissionBroadcastReceiver extends BroadcastReceiver {
    public Boolean isLocationOn = false;
   public MainActivity mainActivity;
    public PermissionBroadcastReceiver() {

    }
   public PermissionBroadcastReceiver(MainActivity mainActivity) {
       this.mainActivity = mainActivity;
    }



@Override
public void onReceive(Context context, Intent intent) {
    if (intent != null && LocationManager.PROVIDERS_CHANGED_ACTION.equals(intent.getAction())) {

        if (mainActivity.isLocationOn("resident")) {
            showToast(mainActivity, "Location is ON");

            isLocationOn = true;
        } else {
            showToast(mainActivity, "Location is OFF");
            isLocationOn  =false;
        }

        PreparationsDialog.updateDialogLayout(mainActivity.isLocationOn("resident"), mainActivity.isCameraEnabled(),mainActivity.isLocationEnabled("resident"));

    }

}



    private void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}

