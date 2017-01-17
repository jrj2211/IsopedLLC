package com.ravensclaw.isoped.isoped.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.dialogs.PinDialog;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.fragments.BluetoothFragment;
import com.ravensclaw.isoped.isoped.fragments.HelpFragment;
import com.ravensclaw.isoped.isoped.fragments.SettingsFragment;
import com.ravensclaw.isoped.isoped.fragments.UserListFragment;
import com.ravensclaw.isoped.isoped.fragments.usermode.DeviceControlFragment;
import com.ravensclaw.isoped.isoped.fragments.usermode.ProgressFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;
import com.ravensclaw.isoped.isoped.helpers.Bluetooth;
import com.ravensclaw.isoped.isoped.helpers.NavigationCallback;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;

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

        // Check that bluetooth is running
        Bluetooth bt = new Bluetooth(this);
        bt.requestEnabled();

        // Setup the navigation toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        setSupportActionBar(toolbar);

        setNavigationView();
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
            case R.id.drawer_devices:
                BaseFragment.startFragment(this, new BluetoothFragment());
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
}
