package com.ravensclaw.isoped.isoped.activities;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.dialogs.PinDialog;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.fragments.HelpFragment;
import com.ravensclaw.isoped.isoped.fragments.SettingsFragment;
import com.ravensclaw.isoped.isoped.fragments.UserListFragment;
import com.ravensclaw.isoped.isoped.fragments.usermode.DeviceControlFragment;
import com.ravensclaw.isoped.isoped.fragments.usermode.ProgressFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;
import com.ravensclaw.isoped.isoped.helpers.NavigationCallback;
import com.ravensclaw.isoped.isoped.services.BluetoothLeService;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout drawer;
    public BluetoothLeService mBluetoothLeService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppSettings settings = AppSettings.getInstance(this);

        // Check that the layout is valid
        if (findViewById(R.id.fragment_container) != null) {
            if (settings.isType(AppSettings.TYPES.PROFESSIONAL)) {
                BaseFragment.startFragment(this, new UserListFragment());
            } else {
                BaseFragment.startFragment(this, new DeviceControlFragment());
            }
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // Create the service for the BLE device
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

        // Setup the navigation toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        setSupportActionBar(toolbar);

        setNavigationView();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    public void setNavigationView() {
        ((NavigationView) findViewById(R.id.drawer_navigation)).setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection()
    {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service)
        {
            Log.e(TAG, "Service Connected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();

            if (!mBluetoothLeService.initialize())
            {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            mBluetoothLeService = null;
        }
    };

    public void selectDrawerItem(MenuItem menuItem) {
        boolean checkItem = true;

        switch (menuItem.getItemId()) {
            case R.id.drawer_help:
                BaseFragment.startFragment(this, new HelpFragment());
                break;
            case R.id.drawer_user_list:
                BaseFragment.startFragment(this, new UserListFragment());
                break;
            case R.id.drawer_device_control:
                BaseFragment.startFragment(this, new DeviceControlFragment());
                break;
            case R.id.drawer_settings:
                BaseFragment.startFragment(this, new SettingsFragment());
                break;
            case R.id.drawer_switch_user:
                BaseFragment.startFragment(this, new UserListFragment());
                break;
            case R.id.drawer_unlock:
                PinDialog dialog = new PinDialog(this, new NavigationCallback(this, new UserListFragment()));
                dialog.show();
                checkItem = false;
                break;
            case R.id.drawer_progress:
                BaseFragment.startFragment(this, new ProgressFragment());
                break;
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(checkItem);

        // Close the drawer
        drawer.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragManager = getSupportFragmentManager();
        BaseFragment currentFragment = (BaseFragment) fragManager.findFragmentById(R.id.fragment_container);

        if (currentFragment.isBackAllowed()) {
            super.onBackPressed();
        }
    }

    public void connectToGatt(BluetoothDevice device) {
        // Connect to the device
        mBluetoothLeService.connect(device.getAddress());
    }
}
