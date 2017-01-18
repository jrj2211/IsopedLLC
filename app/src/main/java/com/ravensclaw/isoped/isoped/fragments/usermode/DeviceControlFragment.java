package com.ravensclaw.isoped.isoped.fragments.usermode;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.dialogs.NumberPickerDialog;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;
import com.ravensclaw.isoped.isoped.helpers.CustomCallback;
import com.ravensclaw.isoped.isoped.helpers.DeviceInfoRow;
import com.ravensclaw.isoped.isoped.helpers.SessionStats;

/**
 * Created by CAD Station on 12/21/2016.
 */

public class DeviceControlFragment extends BaseFragment {

    private Button modeResistance;
    private Button modeAssistance;
    private User user = null;
    private String title = "ISOPED";
    private LayoutInflater inflater;
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
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.device_control, container, false);

        settings = AppSettings.getInstance(getActivity());

        setHasOptionsMenu(true);

        // Load the user if they exist
        if (settings.getUser() != -1) {
            user = new User(getActivity(), settings.getUser());
            title = user.getFullName();
        }

        // Setup the header mode buttons
        modeAssistance = (Button) rootView.findViewById(R.id.mode_assistance);
        modeResistance = (Button) rootView.findViewById(R.id.mode_resistance);
        modeAssistance.setOnClickListener(selectModeListener);
        modeResistance.setOnClickListener(selectModeListener);

        ViewGroup stats = (ViewGroup) rootView.findViewById(R.id.stats);

        selectMode(MODE.ASSISTANCE);

        SessionStats sessionStats = new SessionStats(getActivity());
        sessionStats.setCyclesView((TextView) rootView.findViewById(R.id.session_cycles));
        sessionStats.setTimerView((TextView) rootView.findViewById(R.id.session_time));
        sessionStats.setSessionButton((Button) rootView.findViewById(R.id.session_button));
        sessionStats.setResetButton((Button) rootView.findViewById(R.id.session_reset));
        sessionStats.reset();

        return rootView;
    }

    private void selectMode(MODE mode) {

        TableLayout stats = (TableLayout) rootView.findViewById(R.id.stats);
        stats.removeAllViews();

        if (mode == MODE.ASSISTANCE) {
            // Set the button colors
            modeAssistance.setBackgroundResource(R.drawable.mode_assistance_button);
            modeAssistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.assistance));
            modeResistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkGray));
            modeResistance.setBackgroundResource(0);

            final DeviceInfoRow moveSpeed = new DeviceInfoRow(stats, inflater).setLabel("Move Speed").setIcon(R.drawable.ic_speed_gray46).setUnits(" / 10");
            moveSpeed.setOnClick(new CustomCallback(getActivity()) {
                @Override
                public void run() {
                    NumberPickerDialog dialog = new NumberPickerDialog(getActivity(), "Move Speed", moveSpeed.getValue()).setMax(10).setMin(0);
                    dialog.show();

                    dialog.setOnChange(new NumberPickerDialog.OnChangeListener() {
                        @Override
                        public void onValueChange(int value) {
                            moveSpeed.setValue(value);
                        }
                    });
                }
            });
            new DeviceInfoRow(stats, inflater).setLabel("Stride Length").setIcon(R.drawable.ic_ruler_gray46).setUnits(" inch");
            new DeviceInfoRow(stats, inflater).setLabel("Device Angle").setIcon(R.drawable.ic_angle_gray46).setUnits(" deg");
            new DeviceInfoRow(stats, inflater).setLabel("Avg Cycle Pace").setIcon(R.drawable.ic_feet_gray46).setUnits(" / min");

        } else if (mode == MODE.RESISTANCE) {
            // Set the button colors
            modeResistance.setBackgroundResource(R.drawable.mode_resistance_button);
            modeResistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.resistance));
            modeAssistance.setTextColor(ContextCompat.getColor(getActivity(), R.color.darkGray));
            modeAssistance.setBackgroundResource(0);

            final DeviceInfoRow resistanceLevel = new DeviceInfoRow(stats, inflater).setLabel("Resistance Level").setIcon(R.drawable.ic_steps_gray46).setUnits(" / 10");
            resistanceLevel.setOnClick(new CustomCallback(getActivity()) {
                @Override
                public void run() {
                    NumberPickerDialog dialog = new NumberPickerDialog(getActivity(), "Resistance Level", resistanceLevel.getValue()).setMax(10).setMin(0).setUnits("inches");
                    dialog.show();

                    dialog.setOnChange(new NumberPickerDialog.OnChangeListener() {
                        @Override
                        public void onValueChange(int value) {
                            resistanceLevel.setValue(value);
                        }
                    });
                }
            });

            new DeviceInfoRow(stats, inflater).setLabel("Stride Length").setIcon(R.drawable.ic_ruler_gray46).setUnits(" inch");
            new DeviceInfoRow(stats, inflater).setLabel("Device Angle").setIcon(R.drawable.ic_angle_gray46).setUnits(" deg");

        }
    }

    public enum MODE {
        ASSISTANCE,
        RESISTANCE
    }
}
