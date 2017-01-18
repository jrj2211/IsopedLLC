package com.ravensclaw.isoped.isoped.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.helpers.RepeatListener;

/**
 * Created by CAD Station on 1/17/2017.
 */

public class NumberPickerDialog extends Dialog {
    private Activity activity;
    private int value;
    private Integer min;
    private Integer max;
    private int increment = 1;
    private OnChangeListener onChange;

    public NumberPickerDialog(Activity a, String label, int defaultVal) {
        super(a);
        this.activity = a;
        this.setContentView(R.layout.number_picker);

        // Set the top label
        ((TextView) findViewById(R.id.label)).setText(label);

        // Set the default value
        setValue(defaultVal);

        ((ImageButton) findViewById(R.id.picker_down)).setOnTouchListener(new RepeatListener(500, 75, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(value - increment);
            }
        }));

        ((ImageButton) findViewById(R.id.picker_up)).setOnTouchListener(new RepeatListener(500, 75, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(value + increment);
            }
        }));

        ((Button) findViewById(R.id.close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setOnChange(OnChangeListener cb) {
        onChange = cb;
    }

    public NumberPickerDialog setMin(int min) {
        this.min = min;
        return this;
    }

    public NumberPickerDialog setMax(int max) {
        this.max = max;
        return this;
    }

    public NumberPickerDialog setUnits(String units) {
        if (units.isEmpty()) {
            ((TextView) findViewById(R.id.units)).setVisibility(View.GONE);
        } else {
            ((TextView) findViewById(R.id.units)).setText(units);
            ((TextView) findViewById(R.id.units)).setVisibility(View.VISIBLE);
        }
        return this;
    }

    public void setValue(int val) {
        // Check the bounds
        if (max != null && val > max) {
            val = max;
        }
        if (min != null && val < min) {
            val = min;
        }
        value = val;

        // Update the value display
        ((TextView) findViewById(R.id.picker_display)).setText(Integer.toString(value));

        // Notify any listeners
        if (onChange != null) {
            onChange.onValueChange(value);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface OnChangeListener {
        void onValueChange(int value);
    }
}
