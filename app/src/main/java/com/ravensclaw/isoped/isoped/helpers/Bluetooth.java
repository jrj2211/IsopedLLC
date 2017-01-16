package com.ravensclaw.isoped.isoped.helpers;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.ravensclaw.isoped.isoped.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by CAD Station on 12/21/2016.
 */

public class Bluetooth {
    private final int PERMISSION_REQUEST_BLUETOOTH = 100;
    private final int REQUEST_ENABLE_BT = 100;
    private BluetoothAdapter mBluetoothAdapter;
    private Activity mActivity;

    public Bluetooth(Activity activity) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mActivity = activity;

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
    }

    public boolean checkPermissions() {
        boolean wait = false;
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_CALENDAR) == PermissionChecker.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSION_REQUEST_BLUETOOTH);
            wait = true;
        }

        return wait;
    }

    public boolean requestEnabled() {
        boolean wait = false;
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            wait = true;
        }

        return wait;
    }

    public void scan(BroadcastReceiver receiver) {

        // Definitely want to ensure the bluetooth is on before scanning
        requestEnabled();

        // Android >6.0 requires location so get that permission if we dont have it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {  // Only ask for these permissions on runtime when running Android 6.0 or higher
            switch (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                case PackageManager.PERMISSION_DENIED:
                    if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mActivity,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                mActivity.getResources().getInteger(R.integer.permissionCourseLocation));
                    }
                    break;
                case PackageManager.PERMISSION_GRANTED:
                    break;
            }
        }

        // Restart Discovery if already discovering
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Start the discovery
        mBluetoothAdapter.startDiscovery();

        // Setup what actions we are looking for
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        // Subscribe to the actions we want
        mActivity.registerReceiver(receiver, filter);
    }

    public ArrayList<BluetoothDevice> getPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        return new ArrayList<BluetoothDevice>(pairedDevices);
    }

    public boolean pairDevice(BluetoothDevice device) throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(device);
        return returnValue.booleanValue();
    }
}
