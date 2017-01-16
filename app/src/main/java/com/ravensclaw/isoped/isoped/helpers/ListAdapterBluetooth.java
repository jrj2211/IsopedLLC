package com.ravensclaw.isoped.isoped.helpers;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;

import java.util.ArrayList;

/**
 * Created by CAD Station on 1/12/2017.
 */

public class ListAdapterBluetooth extends ArrayAdapter<BluetoothDevice> {

    public ListAdapterBluetooth(Context context, ArrayList<BluetoothDevice> devices) {
        super(context, 0, devices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_list_item, parent, false);
        }

        // Lookup view for data population
        TextView bluetoothName = (TextView) convertView.findViewById(R.id.bluetoothName);
        TextView bluetoothMac = (TextView) convertView.findViewById(R.id.bluetoothMac);

        // Populate the data into the template view using the data object
        bluetoothName.setText(device.getName());
        bluetoothMac.setText(device.getAddress());

        // Return the completed view to render on screen
        return convertView;
    }
}

