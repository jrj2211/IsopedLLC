package com.ravensclaw.isoped.isoped.fragments.setup;

import android.content.ContentValues;
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
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 1/6/2017.
 */

public class SetupName extends Fragment {

    AppSettings settings;
    private View rootView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settings = AppSettings.getInstance(getActivity());

        // Create the setup template
        rootView = inflater.inflate(R.layout.setup_set_name, container, false);

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

        // Make last input submit form on enter key
        ((EditText) rootView.findViewById(R.id.lastNameInput)).setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
        int numErrors = 0;

        // Get the error icon
        Drawable errorIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_error);
        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        // Validate the form
        TextView firstName = (TextView) rootView.findViewById(R.id.firstNameInput);
        TextView lastName = (TextView) rootView.findViewById(R.id.lastNameInput);

        if (firstName.getText().length() <= 0) {
            firstName.setError("First name cannot be blank", errorIcon);
            numErrors++;
        }

        if (lastName.getText().length() <= 0) {
            lastName.setError("Last name cannot be blank", errorIcon);
            numErrors++;
        }

        if (numErrors == 0) {
            ContentValues values = new ContentValues();
            values.put(User.Columns.COLUMN_NAME_FIRST, firstName.getText().toString());
            values.put(User.Columns.COLUMN_NAME_LAST, lastName.getText().toString());
            long uid = User.insert(getActivity(), values);

            if (uid != -1) {
                // Save the settings
                settings.setComplete(true);
                settings.setUser(uid);
                settings.commit(true);
                settings.finishSetup();
            }
        }
    }

    private void back() {
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }

}