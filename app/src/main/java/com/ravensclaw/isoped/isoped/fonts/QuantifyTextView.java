package com.ravensclaw.isoped.isoped.fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;

/**
 * Created by CAD Station on 12/29/2016.
 */

public class QuantifyTextView extends TextView {

    public QuantifyTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    public QuantifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public QuantifyTextView(Context context) {
        super(context);
        init(null);
    }

    private void init(AttributeSet attrs) {
        // Just Change your font name
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), getContext().getResources().getString(R.string.font_quantify));
        setTypeface(myTypeface);
    }

}
