package com.ravensclaw.isoped.isoped;

import android.view.View;
import android.widget.TextView;

/**
 * Created by CAD Station on 12/20/2016.
 */

public class PillBox {

    TextView pill;

    public PillBox(TextView textview) {
        pill = textview;

        hide();
    }

    public void setMessage(String message) {
        pill.setText(message);
        pill.setVisibility(View.VISIBLE);
    }

    public void hide() {
        pill.setVisibility(View.GONE);
    }
}
