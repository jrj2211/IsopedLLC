package com.ravensclaw.isoped.isoped.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.ravensclaw.isoped.isoped.R;

/**
 * Created by CAD Station on 12/21/2016.
 */

public class Bluetooth {
    public static final int PERMISSION_REQUEST_BLUETOOTH = 100;

    private final int REQUEST_ENABLE_BT = 100;
    private BluetoothAdapter mBluetoothAdapter;
    private Activity mActivity;
    private BluetoothStateChangedCalledback mCallback;

    public Bluetooth(Activity activity, BluetoothStateChangedCalledback callback) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mActivity = activity;
        mCallback = callback;

        // Phone does not support Bluetooth so let the user know and exit.
        if (mBluetoothAdapter == null) {
            new AlertDialog.Builder(mActivity)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        mActivity.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    public boolean checkPermissions(Fragment fragment) {
        boolean hasPerm = true;
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PermissionChecker.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_REQUEST_BLUETOOTH);
            }
            hasPerm = false;
        }

        return hasPerm;
    }

    public boolean requestEnabled() {
        boolean wait = false;
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            wait = true;
        }

        return wait;
    }

    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // It means the user has changed his bluetooth state.
            if (intent.getAction().equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                mCallback.onStateChanged(mBluetoothAdapter.getState());
            }
        }
    };

    public interface BluetoothStateChangedCalledback {
        void onStateChanged(int state);
    }
}
