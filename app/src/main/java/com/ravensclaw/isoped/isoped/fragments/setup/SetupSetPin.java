package com.ravensclaw.isoped.isoped.fragments.setup;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 12/20/2016.
 */

public class SetupSetPin extends Fragment {

    AppSettings settings;
    private View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settings = AppSettings.getInstance(getActivity());

        // Create the setup template
        rootView = inflater.inflate(R.layout.setup_set_pin, container, false);

        // Setup the buttons
        ((Button) rootView.findViewById(R.id.setup_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        ((Button) rootView.findViewById(R.id.setup_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        // Make the last input submit the form
        ((EditText) rootView.findViewById(R.id.pinRepeatInput)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    submit();
                    return true;
                }
                return false;
            }
        });

        return rootView;
    }

    private void submit() {
        // Get the error icon
        Drawable errorIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_error);
        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        // Validate the password
        EditText pin = (EditText) rootView.findViewById(R.id.pinInput);
        EditText pinRepeat = (EditText) rootView.findViewById(R.id.pinRepeatInput);

        if (pin.getText().length() <= 0) {
            pin.setError("PIN must be at least 4 digits", errorIcon);
        } else if (pin.getText().length() < 4) {
            pin.setError("PIN must be at least 4 digits", errorIcon);
        } else if (false == pin.getText().toString().equals(pinRepeat.getText().toString())) {
            pinRepeat.setError("PINs do not match", errorIcon);
        } else {
            // Save the settings
            settings.setPin(pin.getText().toString());
            settings.setType(AppSettings.TYPES.PROFESSIONAL);
            settings.setComplete(true);
            settings.commit(true);
            settings.finishSetup();
        }
    }

    private void back() {
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}
