package com.ravensclaw.isoped.isoped.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.activities.SetupActivity;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 12/21/2016.
 */

public class SettingsFragment extends BaseFragment {

    private View.OnClickListener rerunSetupListener = new View.OnClickListener() {
        public void onClick(View v) {
            AppSettings settings = AppSettings.getInstance(getActivity());
            settings.reset();

            Intent i = new Intent(getActivity(), SetupActivity.class);
            startActivity(i);
            getActivity().finish();
        }
    };

    @Override
    public String getTitle() {
        return "Settings";
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);

        rootView = inflater.inflate(R.layout.settings, container, false);

        rootView.findViewById(R.id.settings_rerun).setOnClickListener(rerunSetupListener);

        return rootView;
    }
}
