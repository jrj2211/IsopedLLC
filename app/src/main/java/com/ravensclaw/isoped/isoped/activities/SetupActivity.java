package com.ravensclaw.isoped.isoped.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.fragments.setup.SetupSplash;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 12/20/2016.
 */

public class SetupActivity extends AppCompatActivity {

    AppSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_container);

        // Load the settings
        AppSettings settings = AppSettings.getInstance(this);

        if (settings.isSetupComplete()) {
            // Setup is good so just go to main activity
            settings.finishSetup();
        } else {
            settings.reset();

            // Start a new transaction
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SetupSplash());
            transaction.commit();
        }
    }
}
