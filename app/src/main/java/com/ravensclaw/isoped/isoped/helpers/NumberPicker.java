package com.ravensclaw.isoped.isoped.helpers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;

/**
 * Created by CAD Station on 1/5/2017.
 */

public class NumberPicker {
    private View picker;
    private int value = 0;
    private int min;
    private int max;
    private int increment = 1;
    private String unitsPlural = "";
    private String unitsSingular = "";

    public NumberPicker(ViewGroup container, LayoutInflater inflater, String label, int min, int max) {
        this.min = min;
        this.max = max;

        this.picker = inflater.inflate(R.layout.number_picker, null);
        container.addView(picker);

        setLabel(label);
        setValue(0);

        ((ImageButton) picker.findViewById(R.id.picker_down)).setOnTouchListener(new RepeatListener(500, 75, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(value - increment);
            }
        }));

        ((ImageButton) picker.findViewById(R.id.picker_up)).setOnTouchListener(new RepeatListener(500, 75, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setValue(value + increment);
            }
        }));
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public void setUnits(String plural) {
        this.unitsPlural = plural;
    }

    public void setUnits(String plural, String singular) {
        this.unitsPlural = plural;
        this.unitsSingular = singular;
    }

    public void setValue(int val) {
        if (val > max) {
            val = max;
        }
        if (val < min) {
            val = min;
        }

        value = val;
        ((TextView) picker.findViewById(R.id.picker_display)).setText(Integer.toString(val) + getUnits(val));
    }

    public void setLabel(String label) {
        ((TextView) picker.findViewById(R.id.picker_label)).setText(label);
    }

    public String getUnits(int val) {
        if (!unitsSingular.isEmpty() && val == 1) {
            return unitsSingular;
        } else {
            return unitsPlural;
        }
    }
}
