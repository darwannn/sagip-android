package com.example.sagip;
import android.content.Context;
import android.content.SharedPreferences;

public class PersistenceStorage {

    private static final String PREFERENCE_FILE_KEY = "com.example.myapp.PREFERENCES";

    public static void saveToPersistenceStorage(Context context, String myToken, String userId, String assistanceReqId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        editor.putString("myToken", myToken);
        editor.putString("userId", userId);
        editor.putString("assistanceReqId", assistanceReqId);

        editor.apply();
    }

    public static String getFromPersistenceStorage(Context context, String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public static void clearPersistenceStorage(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Clear all data
        editor.clear();
        editor.apply();
    }
}
