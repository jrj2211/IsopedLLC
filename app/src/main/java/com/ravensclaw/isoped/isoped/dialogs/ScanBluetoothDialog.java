package com.ravensclaw.isoped.isoped.dialogs;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.helpers.Bluetooth;
import com.ravensclaw.isoped.isoped.helpers.ListAdapterBluetooth;

import java.util.ArrayList;

/**
 * Created by CAD Station on 12/23/2016.
 */

public class ScanBluetoothDialog extends Dialog {

    private AppCompatActivity activity;
    private ListAdapterBluetooth btAdapter;
    private ArrayList<BluetoothDevice> devices;
    private ListView btListView;
    private BroadcastReceiver receiver;
    private Bluetooth btController;

    public ScanBluetoothDialog(AppCompatActivity a) {
        super(a);
        this.activity = a;
        this.setContentView(R.layout.bluetooth_scan);

        // Should just cancel instead of hide
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        // Get the bluetooth wrapper
        btController = new Bluetooth(activity);

        // Setup the list view adapter to populate when devices are found
        devices = new ArrayList<BluetoothDevice>();
        btAdapter = new ListAdapterBluetooth((AppCompatActivity) activity, devices);
        btListView = (ListView) findViewById(R.id.bluetoothDevices);
        btListView.setAdapter(btAdapter);

        // What to do when we get actions
        receiver = createReceiver();

        onDiscoveryStart();

        // Start the scan
        btController.scan(receiver);

        // Retry scan button
        findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiver = createReceiver();
                btController.scan(receiver);
            }
        });

        btListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice item = btAdapter.getItem(position);
                startPairing(item, view);
            }
        });

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                removeReceiver();
            }
        });
    }

    private void startPairing(BluetoothDevice device, View view) {
        view.findViewById(R.id.bluetoothSpinner).setVisibility(View.VISIBLE);
        view.findViewById(R.id.bluetoothMac).setVisibility(View.GONE);

        boolean success = false;

        try {
            success = btController.pairDevice(device);
        } catch (Exception e) {
        }

        Log.e("success?", Boolean.toString(success));
        if (!success) {
            view.findViewById(R.id.bluetoothSpinner).setVisibility(View.GONE);
            view.findViewById(R.id.bluetoothMac).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.bluetoothMac)).setText("Pair Failed");
        }
    }

    private BroadcastReceiver createReceiver() {
        return new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                //Finding devices
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                        btAdapter.add(device);
                    }
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    onDiscoveryStart();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    onDiscoveryFinish();
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onDiscoveryStart() {
        findViewById(R.id.noResults).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.bluetoothStatus)).setText("Scanning...");

        findViewById(R.id.spinner).setVisibility(View.VISIBLE);

        btAdapter.clear();
    }

    public void onDiscoveryFinish() {
        ((TextView) findViewById(R.id.bluetoothStatus)).setText("Select A Device");

        findViewById(R.id.spinner).setVisibility(View.GONE);

        if (btAdapter.getCount() == 0) {
            findViewById(R.id.noResults).setVisibility(View.VISIBLE);
        }

        removeReceiver();
    }

    private void removeReceiver() {
        if (receiver != null) {
            activity.unregisterReceiver(receiver);
            receiver = null;
        }
    }
}
