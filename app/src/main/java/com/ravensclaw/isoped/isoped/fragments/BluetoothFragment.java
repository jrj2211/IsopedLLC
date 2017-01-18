package com.ravensclaw.isoped.isoped.fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.dialogs.ScanBluetoothDialog;
import com.ravensclaw.isoped.isoped.helpers.Bluetooth;
import com.ravensclaw.isoped.isoped.helpers.ListAdapterBluetooth;

import java.util.ArrayList;

/**
 * Created by CAD Station on 1/12/2017.
 */

public class BluetoothFragment extends BaseFragment {

    private ListAdapterBluetooth btAdapter;
    private ArrayList<BluetoothDevice> devices;
    private ListView btListView;

    private View.OnClickListener scanListener = new View.OnClickListener() {
        public void onClick(View v) {
            ScanBluetoothDialog btDialog = new ScanBluetoothDialog((AppCompatActivity) getActivity());
            btDialog.show();
        }
    };

    @Override
    public String getTitle() {
        return "Settings";
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);

        rootView = inflater.inflate(R.layout.bluetooth, container, false);

        rootView.findViewById(R.id.settings_scan).setOnClickListener(scanListener);

        // Get the bluetooth wrapper
        final Bluetooth btController = new Bluetooth(getActivity());

        // Setup the list view adapter to populate when devices are found
        devices = btController.getPairedDevices();
        btAdapter = new ListAdapterBluetooth((AppCompatActivity) getActivity(), devices);
        btListView = (ListView) rootView.findViewById(R.id.bluetoothDevices);
        btListView.setAdapter(btAdapter);

        return rootView;
    }

}
