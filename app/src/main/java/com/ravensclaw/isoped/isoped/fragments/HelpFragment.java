package com.ravensclaw.isoped.isoped.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ravensclaw.isoped.isoped.R;

/**
 * Created by CAD Station on 1/3/2017.
 */

public class HelpFragment extends BaseFragment {
    @Override
    public String getTitle() {
        return "Help";
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);

        this.inflater = inflater;

        rootView = inflater.inflate(R.layout.help, container, false);

        return rootView;
    }

}
