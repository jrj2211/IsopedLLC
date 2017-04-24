package com.ravensclaw.isoped.isoped.fragments.usermode;

import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.activities.MainActivity;
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.dialogs.NumberPickerDialog;
import com.ravensclaw.isoped.isoped.dialogs.ScanBluetoothDialog;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;
import com.ravensclaw.isoped.isoped.helpers.Bluetooth;
import com.ravensclaw.isoped.isoped.helpers.Bluetooth.BluetoothStateChangedCalledback;
import com.ravensclaw.isoped.isoped.helpers.CustomCallback;
import com.ravensclaw.isoped.isoped.helpers.DeviceInfoRow;
import com.ravensclaw.isoped.isoped.helpers.IsopedController;
import com.ravensclaw.isoped.isoped.helpers.SessionStats;
import com.ravensclaw.isoped.isoped.services.BluetoothLeService;

import java.util.List;

/**
 * Created by CAD Station on 12/21/2016.
 */

public class DeviceControlFragment extends BaseFragment {

    private final static String TAG = DeviceControlFragment.class.getSimpleName();

    private Button modeResistance;
    private Button modeAssistance;
    private User user = null;
    private String title = "ISOPED";
    private LayoutInflater inflater;
    private SessionStats sessionStats;
    private Bluetooth mBluetooth;
    private IsopedController mIsopedController;
    private ScanBluetoothDialog mBtScanDialog;
    private Fragment mFragment;

    public enum MODE {
        ASSISTANCE,
        RESISTANCE
    }

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

    private BluetoothStateChangedCalledback mBluetoothStateChanged = new BluetoothStateChangedCalledback() {
        @Override
        public void onStateChanged(int state) {
            setRequestScreen();
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

        mBluetooth = new Bluetooth(getActivity(), mBluetoothStateChanged);

        mFragment = this;

        setRequestScreen();

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

        // Create a session controller
        sessionStats = new SessionStats(getActivity());
        sessionStats.setCyclesView((TextView) rootView.findViewById(R.id.session_cycles));
        sessionStats.setTimerView((TextView) rootView.findViewById(R.id.session_time));
        sessionStats.setSessionButton((Button) rootView.findViewById(R.id.session_button));
        sessionStats.setResetButton((Button) rootView.findViewById(R.id.session_reset));
        sessionStats.reset();

        selectMode(MODE.ASSISTANCE);

        return rootView;
    }

    private void setRequestScreen() {
        rootView.findViewById(R.id.device_control).setVisibility(View.GONE);
        rootView.findViewById(R.id.request_screen).setVisibility(View.GONE);

        if(!mBluetooth.checkPermissions(mFragment)) {
            rootView.findViewById(R.id.request_screen).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.request_screen_text)).setText("Location permission required");
            ((Button) rootView.findViewById(R.id.request_screen_button)).setText("Grant Permission");
            rootView.findViewById(R.id.request_screen_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBluetooth.checkPermissions(mFragment);
                }
            });
        } else if(!mBluetooth.isEnabled()) {
            rootView.findViewById(R.id.request_screen).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.request_screen_text)).setText("Bluetooth is required to continue");
            ((Button) rootView.findViewById(R.id.request_screen_button)).setText("Enable Bluetooth");
            rootView.findViewById(R.id.request_screen_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBluetooth.requestEnabled();
                }
            });
        } else if(mIsopedController == null && settings.getSavedBleDevice() == null) {
            rootView.findViewById(R.id.request_screen).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.request_screen_text)).setText("Not connected to an Isoped Device");
            ((Button) rootView.findViewById(R.id.request_screen_button)).setText("Scan Devices");
            rootView.findViewById(R.id.request_screen_button).setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View v) {
                    mBtScanDialog = new ScanBluetoothDialog((AppCompatActivity) getActivity());
                    mBtScanDialog.show();
                }
            });
        } else {
            rootView.findViewById(R.id.device_control).setVisibility(View.VISIBLE);
        }
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
                            if(mIsopedController != null) {
                                mIsopedController.setAssistanceLevel(value);
                            }
                        }
                    });
                }
            });

            final DeviceInfoRow strideLength = new DeviceInfoRow(stats, inflater).setLabel("Stride Length").setIcon(R.drawable.ic_ruler_gray46).setUnits(" inch");
            strideLength.setOnClick(new CustomCallback(getActivity()) {
                @Override
                public void run() {
                    NumberPickerDialog dialog = new NumberPickerDialog(getActivity(), "Stride Length", strideLength.getValue()).setMax(10).setMin(0).setUnits("inches");
                    dialog.show();

                    dialog.setOnChange(new NumberPickerDialog.OnChangeListener() {
                        @Override
                        public void onValueChange(int value) {
                            strideLength.setValue(value);
                            if(mIsopedController != null) {
                                mIsopedController.setStrideLength(value);
                            }
                        }
                    });
                }
            });

            final DeviceInfoRow deviceAngle = new DeviceInfoRow(stats, inflater).setLabel("Device Angle").setIcon(R.drawable.ic_angle_gray46).setUnits(" deg");
            sessionStats.setDeviceAngleView(deviceAngle);

            deviceAngle.setOnClick(new CustomCallback(getActivity()) {
                @Override
                public void run() {
                    mIsopedController.getElevationAngle();
                }
            });

            final DeviceInfoRow cyclePace = new DeviceInfoRow(stats, inflater).setLabel("Avg Cycle Pace").setIcon(R.drawable.ic_feet_gray46).setUnits(" / min");
            sessionStats.setCyclePaceView(cyclePace);

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
                    NumberPickerDialog dialog = new NumberPickerDialog(getActivity(), "Resistance Level", resistanceLevel.getValue()).setMax(10).setMin(0);
                    dialog.show();

                    dialog.setOnChange(new NumberPickerDialog.OnChangeListener() {
                        @Override
                        public void onValueChange(int value) {
                            resistanceLevel.setValue(value);
                            mIsopedController.setResistanceLevel(value);
                        }
                    });
                }
            });

            final DeviceInfoRow strideLength = new DeviceInfoRow(stats, inflater).setLabel("Stride Length").setIcon(R.drawable.ic_ruler_gray46).setUnits(" inch");
            strideLength.setOnClick(new CustomCallback(getActivity()) {
                @Override
                public void run() {
                    NumberPickerDialog dialog = new NumberPickerDialog(getActivity(), "Stride Length", strideLength.getValue()).setMax(10).setMin(0).setUnits("inches");
                    dialog.show();

                    dialog.setOnChange(new NumberPickerDialog.OnChangeListener() {
                        @Override
                        public void onValueChange(int value) {
                            strideLength.setValue(value);
                            if(mIsopedController != null) {
                                mIsopedController.setStrideLength(value);
                            }
                        }
                    });
                }
            });

            final DeviceInfoRow deviceAngle = new DeviceInfoRow(stats, inflater).setLabel("Device Angle").setIcon(R.drawable.ic_angle_gray46).setUnits(" deg");
            sessionStats.setDeviceAngleView(deviceAngle);

            deviceAngle.setOnClick(new CustomCallback(getActivity()) {
                @Override
                public void run() {
                    mIsopedController.getElevationAngle();
                }
            });
        }
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                Log.d(TAG, "Bluetooth Connected!");
            } else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                Log.d(TAG, "Bluetooth Disconnected!");
                mIsopedController = null;
                setRequestScreen();
            } else if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "Bluetooth Discovered!");
                mIsopedController = new IsopedController(getActivity(), ((MainActivity)getActivity()).mBluetoothLeService, ((MainActivity)getActivity()).mBluetoothLeService.getSupportedGattServices());
                setRequestScreen();
                if(mBtScanDialog != null) {
                    mBtScanDialog.cancel();
                }
            } else if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, intent.getStringExtra(BluetoothLeService.CHARACTERISTIC));
                if(intent.getStringExtra(BluetoothLeService.CHARACTERISTIC).equals(getString(R.string.gatt_elevation_characteristic))) {
                    byte[] bytes = intent.getByteArrayExtra(BluetoothLeService.EXTRA_DATA);
                    sessionStats.setElevation((int)bytes[0]);
                }
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);

        getActivity().registerReceiver(mGattUpdateReceiver, intentFilter);

        setRequestScreen();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Bluetooth.PERMISSION_REQUEST_BLUETOOTH: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setRequestScreen();
                }
                return;
            }
        }
    }
}
