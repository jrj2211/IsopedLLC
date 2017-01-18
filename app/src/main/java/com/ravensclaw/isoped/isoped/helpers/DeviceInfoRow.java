package com.ravensclaw.isoped.isoped.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;

/**
 * Created by CAD Station on 1/17/2017.
 */

public class DeviceInfoRow {

    private View rootView;
    private int value;

    public DeviceInfoRow(ViewGroup container, LayoutInflater inflater) {
        rootView = inflater.inflate(R.layout.device_info_row, null);
        container.addView(rootView);
    }

    public DeviceInfoRow setOnClick(final CustomCallback cb) {
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.run();
            }
        });

        return this;
    }

    public DeviceInfoRow setIcon(int icon) {
        ((ImageView) rootView.findViewById(R.id.icon)).setBackgroundResource(icon);
        return this;
    }

    public DeviceInfoRow setLabel(String label) {
        ((TextView) rootView.findViewById(R.id.label)).setText(label);
        return this;
    }

    public DeviceInfoRow setUnits(String units) {
        ((TextView) rootView.findViewById(R.id.units)).setText(units);
        return this;
    }

    public int getValue() {
        return value;
    }

    public DeviceInfoRow setValue(int value) {
        this.value = value;
        ((TextView) rootView.findViewById(R.id.value)).setText(Integer.toString(value));
        return this;
    }
}
