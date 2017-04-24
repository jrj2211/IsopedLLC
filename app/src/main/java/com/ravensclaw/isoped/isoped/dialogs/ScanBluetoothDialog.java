package com.ravensclaw.isoped.isoped.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.activities.MainActivity;
import com.ravensclaw.isoped.isoped.helpers.BleDeviceScanner;
import com.ravensclaw.isoped.isoped.helpers.ListAdapterBluetooth;
import com.ravensclaw.isoped.isoped.services.BluetoothLeService;

import java.util.ArrayList;

/**
 * Created by CAD Station on 12/23/2016.
 */

public class ScanBluetoothDialog extends Dialog {

    private final static String TAG = ScanBluetoothDialog.class.getSimpleName();

    private AppCompatActivity mActivity;
    private BleDeviceScanner mBleDeviceScanner;
    private ListAdapterBluetooth mDevicesListAdapter;
    private ListView mDevicesListView;
    private LocationManager mLocationManager;

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            mDevicesListAdapter.addDevice(device);
        }
    };

    private BleDeviceScanner.ScanStoppedCallback mScanStoppedCallback = new BleDeviceScanner.ScanStoppedCallback() {
        @Override
        public void onStopped() {
            onScanFinish();
        }
    };

    public ScanBluetoothDialog(AppCompatActivity a) {
        super(a);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.mActivity = a;
        this.setContentView(R.layout.bluetooth_scan);

        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        // Should just cancel instead of hide
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // Get the bluetooth LE adapter
        mBleDeviceScanner = new BleDeviceScanner();
        mBleDeviceScanner.onStop(mScanStoppedCallback);

        // Setup the list view adapter to populate when mFoundDevices are found
        mDevicesListAdapter = new ListAdapterBluetooth(getContext());
        mDevicesListView = (ListView) findViewById(R.id.bluetoothDevices);
        mDevicesListView.setAdapter(mDevicesListAdapter);

        if(checkLocationEnabled()) {
            // Automatically start a scan
            onScanStart();
        } else {
            // Request to enable location
            showEnableLocation();
        }

        // Retry scan button
        findViewById(R.id.scanErrorButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLocationEnabled()) {
                    onScanStart();
                } else {
                    getContext().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            }
        });

        mDevicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "trying to connect");
                ((MainActivity) mActivity).connectToGatt(mDevicesListAdapter.getItem(position));

                // Stop the scan
                mBleDeviceScanner.scanLeDevice(false, mLeScanCallback);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            if(checkLocationEnabled()) {
                onScanStart();
            } else {
                showEnableLocation();
            }
        }
    }

    public void onScanStart() {
        // Start Scanning
        mBleDeviceScanner.scanLeDevice(true, mLeScanCallback);

        // Remove no results
        findViewById(R.id.scanError).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.bluetoothStatus)).setText("Scanning...");

        // Enable the spinner
        findViewById(R.id.spinner).setVisibility(View.VISIBLE);

        // Reset the device list view
        mDevicesListAdapter.clear();
    }

    public void onScanFinish() {
        ((TextView) findViewById(R.id.bluetoothStatus)).setText("Select A Device");

        findViewById(R.id.spinner).setVisibility(View.GONE);

        if(checkLocationEnabled()) {
            if (mDevicesListAdapter.getCount() == 0) {
                findViewById(R.id.scanError).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.scanErrorMessage)).setText("No results found");
                ((Button) findViewById(R.id.scanErrorButton)).setText("Retry Scan");
            }
        } else {
            showEnableLocation();
        }
    }

    public boolean checkLocationEnabled() {
        return (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) &&
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }

    private void showEnableLocation() {
        ((TextView) findViewById(R.id.bluetoothStatus)).setText("Location Required");
        findViewById(R.id.scanError).setVisibility(View.VISIBLE);
        findViewById(R.id.spinner).setVisibility(View.GONE);

        ((TextView) findViewById(R.id.scanErrorMessage)).setText("Location services need to be enabled");
        ((Button) findViewById(R.id.scanErrorButton)).setText("Enable Location");
    }
}
