package com.ravensclaw.isoped.isoped.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.PillBox;
import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;
import com.ravensclaw.isoped.isoped.helpers.NavigationCallback;

/**
 * Created by CAD Station on 12/22/2016.
 */

public class PinDialog extends Dialog implements View.OnClickListener {
    private AppCompatActivity activity;
    private Dialog d;
    private Button unlock, close;
    private PillBox pill;
    private AppSettings settings;
    private NavigationCallback callback;

    public PinDialog(AppCompatActivity a, NavigationCallback cb) {
        super(a);
        this.activity = a;
        this.setContentView(R.layout.pin_validate);

        callback = cb;
        settings = AppSettings.getInstance(activity);
        pill = new PillBox((TextView) this.findViewById(R.id.validatePillBox));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        unlock = (Button) findViewById(R.id.button_unlock);
        close = (Button) findViewById(R.id.button_close);
        unlock.setOnClickListener(this);
        close.setOnClickListener(this);

        // Open the keyboard automatically
        EditText pinInput = (EditText) findViewById(R.id.pin_text);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_unlock:
                EditText pinInput = (EditText) findViewById(R.id.pin_text);

                if (settings.checkPin(pinInput.getText().toString())) {
                    // Do what ever is specified after validated
                    callback.run();
                    dismiss();
                } else {
                    pill.setMessage("Invalid PIN");
                    pinInput.setText("");
                }

                break;
            case R.id.button_close:
                dismiss();
                break;
            default:
                break;
        }
    }
}
