package com.ravensclaw.isoped.isoped.helpers;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ravensclaw.isoped.isoped.activities.MainActivity;

/**
 * Created by CAD Station on 12/20/2016.
 */

public class AppSettings {
    private static AppSettings mInstance;
    final private String KEY_COMPLETE = "complete";
    final private String KEY_TYPE = "type";
    final private String KEY_PIN = "pin";
    final private String KEY_USER = "user";
    final private String KEY_PINNED_APP = "pinapp";
    final private String SAVED_BLE_DEVICE = "bledevice";

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Activity mActivity;

    private AppSettings(Activity activity) {
        // Load the preferences file
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        editor = preferences.edit();
        mActivity = activity;
    }

    public static AppSettings getInstance(Activity activity) {
        if (mInstance == null || mInstance.mActivity != activity) {
            mInstance = new AppSettings(activity);
        }

        return mInstance;
    }

    public String getSavedBleDevice() {
        return preferences.getString(SAVED_BLE_DEVICE, null);
    }

    public void setSavedBleDevice(String macAddress) {
        editor.putString(SAVED_BLE_DEVICE, macAddress);
        editor.commit();
    }

    public void setType(TYPES type) {
        editor.putString(KEY_TYPE, type.toString());
    }

    public void setPin(String pin) {
        editor.putString(KEY_PIN, pin);
    }

    public void setComplete(boolean complete) {
        editor.putBoolean("setupComplete", complete);
    }

    public void commit(boolean completed) {
        editor.putBoolean(KEY_COMPLETE, completed);
        editor.commit();
    }

    public long getUser() {
        return preferences.getLong(KEY_USER, -1);
    }

    public void setUser(long uid) {
        editor.putLong(KEY_USER, uid);
        setAskedToPin(false);
        editor.commit();
    }

    public boolean isSetupComplete() {
        return preferences.getBoolean(KEY_COMPLETE, false);
    }

    public boolean checkPin(String pin) {
        return (preferences.getString(KEY_PIN, "").equals(pin));
    }

    public boolean isType(TYPES type) {
        return preferences.getString(KEY_TYPE, "").equals(type.toString());
    }

    public boolean getAskedToPin() {
        return preferences.getBoolean(KEY_PINNED_APP, false);
    }

    public void setAskedToPin(boolean pinned) {
        editor.putBoolean(KEY_PINNED_APP, pinned);
        editor.commit();
    }

    public void reset() {
        editor.clear().commit();
    }

    // TODO MAKE THIS A CALLBACK
    public void finishSetup() {
        // Start main activity
        Intent intent = new Intent(mActivity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mActivity.startActivity(intent);
        mActivity.finish();
    }

    public enum TYPES {
        PROFESSIONAL("professional"),
        PERSONAL("personal");

        private final String name;

        private TYPES(final String text) {
            this.name = text;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
