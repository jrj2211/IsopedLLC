package com.ravensclaw.isoped.isoped.fragments.usermode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 1/13/2017.
 */

public class ProgressFragment extends BaseFragment {

    @Override
    public boolean getAuthRequired() {
        return false;
    }

    @Override
    public boolean isBackAllowed() {
        return false;
    }

    @Override
    public String getTitle() {
        return "My Progress";
    }

    @Override
    public boolean pinnedMode() {
        return true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);

        settings = AppSettings.getInstance(getActivity());

        rootView = inflater.inflate(R.layout.progress, container, false);

        setHasOptionsMenu(true);

        return rootView;
    }
}
