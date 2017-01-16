package com.ravensclaw.isoped.isoped.fragments.setup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 12/19/2016.
 */

public class SetupSplash extends Fragment {

    private View rootView;
    private AppSettings settings;

    private View.OnClickListener startButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Go to next setup step
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new SetupTypeFragment());
            transaction.commit();
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settings = AppSettings.getInstance(getActivity());

        // Create the setup template
        rootView = inflater.inflate(R.layout.setup_splash, container, false);

        // Setup the buttons
        rootView.findViewById(R.id.setupStart).setOnClickListener(startButtonListener);

        // Ensure the keyboard is hidden
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return rootView;
    }
}
