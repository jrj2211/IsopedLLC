package com.ravensclaw.isoped.isoped.fragments.setup;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 12/19/2016.
 */

public class SetupTypeFragment extends Fragment {

    private View rootView;
    private AppSettings settings;

    private View.OnClickListener typeButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            FragmentTransaction transaction;

            switch (v.getId()) {
                case R.id.personalButton:
                    // Update preferences
                    transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new SetupName());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    settings.setType(AppSettings.TYPES.PERSONAL);
                    break;
                case R.id.professionalButton:
                    // Go to next setup step
                    transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, new SetupSetPin());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    break;
            }
        }
    };

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        settings = AppSettings.getInstance(getActivity());

        // Create the setup template
        rootView = inflater.inflate(R.layout.setup_select_type, container, false);

        // Setup the buttons
        Button professional = (Button) rootView.findViewById(R.id.professionalButton);
        Button personal = (Button) rootView.findViewById(R.id.personalButton);
        professional.setOnClickListener(typeButtonListener);
        personal.setOnClickListener(typeButtonListener);

        // Ensure the keyboard is hidden
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return rootView;
    }
}
