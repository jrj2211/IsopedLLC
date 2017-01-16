package com.ravensclaw.isoped.isoped.fragments.usermode;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;
import com.ravensclaw.isoped.isoped.helpers.NumberPicker;
import com.ravensclaw.isoped.isoped.helpers.SessionStats;

/**
 * Created by CAD Station on 12/21/2016.
 */

public class DeviceControlFragment extends BaseFragment {

    private Button modeResistance;
    private Button modeAssistance;
    private TextView levelDisplay;
    private int currentLevel = 0;
    private User user = null;
    private String title = "ISOPED";
    private SessionStats assistanceSession;
    private SessionStats resistanceSession;
    private View.OnClickListener selectModeListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.mode_assistance:
                    selectMode(MODE.ASSISTANCE);
                    break;
                case R.id.mode_resistance:
                    selectMode(MODE.RESISTANCE);
                    break;
            }
        }
    };

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
        return title;
    }

    @Override
    public boolean toolbarElevation() {
        return false;
    }

    @Override
    public boolean pinnedMode() {
        return true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);

        settings = AppSettings.getInstance(getActivity());

        rootView = inflater.inflate(R.layout.device_control, container, false);

        setHasOptionsMenu(true);

        // Setup the header mode buttons
        modeAssistance = (Button) rootView.findViewById(R.id.mode_assistance);
        modeResistance = (Button) rootView.findViewById(R.id.mode_resistance);
        modeAssistance.setOnClickListener(selectModeListener);
        modeResistance.setOnClickListener(selectModeListener);

        // Create all the views
        setupAssistanceMode();
        setupResistanceMode();

        // Default to assistance mode
        selectMode(MODE.ASSISTANCE);

        // Load the user if they exist
        if (settings.getUser() != -1) {
            user = new User(getActivity(), settings.getUser());
            title = user.getFullName();
        }

        return rootView;
    }

    public void setupAssistanceMode() {
        ViewGroup options = (ViewGroup) rootView.findViewById(R.id.assistance_options);
        NumberPicker resistance = new NumberPicker(options, getActivity().getLayoutInflater(), "Move Speed (0-10)", 0, 10);
        resistance.setValue(0);

        NumberPicker moveDistance = new NumberPicker(options, getActivity().getLayoutInflater(), "Stride Length (0-15)", 0, 15);
        moveDistance.setIncrement(1);
        moveDistance.setUnits(" inches", " inch");
        moveDistance.setValue(0);

        assistanceSession = new SessionStats(options, getActivity().getLayoutInflater(), "Assistance Session");
    }

    public void setupResistanceMode() {
        ViewGroup options = (ViewGroup) rootView.findViewById(R.id.resistance_options);
        NumberPicker resistance = new NumberPicker(options, getActivity().getLayoutInflater(), "Resistance Level (0-10)", 0, 10);
        resistance.setValue(0);

        NumberPicker moveDistance = new NumberPicker(options, getActivity().getLayoutInflater(), "Stride Length (0-15)", 0, 15);
        moveDistance.setIncrement(1);
        moveDistance.setUnits(" inches", " inch");
        moveDistance.setValue(0);

        resistanceSession = new SessionStats(options, getActivity().getLayoutInflater(), "Resistance Session");
    }

    private void selectMode(MODE mode) {
        if (mode == MODE.ASSISTANCE) {
            // Set the button colors
            modeAssistance.setBackgroundResource(R.drawable.mode_assistance_button);
            modeAssistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.assistance));
            modeResistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkGray));
            modeResistance.setBackgroundResource(0);

            // Show the right container
            rootView.findViewById(R.id.assistance_options).setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.resistance_options).setVisibility(View.GONE);
        } else if (mode == MODE.RESISTANCE) {
            // Set the button colors
            modeResistance.setBackgroundResource(R.drawable.mode_resistance_button);
            modeResistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.resistance));
            modeAssistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkGray));
            modeAssistance.setBackgroundResource(0);

            // Show the right container
            rootView.findViewById(R.id.assistance_options).setVisibility(View.GONE);
            rootView.findViewById(R.id.resistance_options).setVisibility(View.VISIBLE);
        }

        assistanceSession.pause();
        resistanceSession.pause();
    }

    public enum MODE {
        ASSISTANCE,
        RESISTANCE
    }
}
