package com.ravensclaw.isoped.isoped.fragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.dialogs.PinDialog;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;
import com.ravensclaw.isoped.isoped.helpers.NavigationCallback;

/**
 * Created by CAD Station on 12/21/2016.
 */

public abstract class BaseFragment extends Fragment {

    protected View rootView;
    protected AppSettings settings;
    protected LayoutInflater inflater;

    public static void startFragment(Activity activity, BaseFragment newFragment) {
        FragmentManager fragManager = ((AppCompatActivity) activity).getSupportFragmentManager();
        BaseFragment currentFragment = (BaseFragment) fragManager.findFragmentById(R.id.fragment_container);

        // Start the transactions
        FragmentTransaction transaction = fragManager.beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment);

        // If there is already a fragment then we want it on the backstack
        if (currentFragment != null) {
            transaction.addToBackStack(null);
        }

        // Show it
        transaction.commit();
    }

    @TargetApi(21)
    private void lockMode(boolean start) {
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            if (start) {
                getActivity().startLockTask();
            } else {
                getActivity().stopLockTask();
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get a reference to the app settings
        settings = AppSettings.getInstance(getActivity());

        // Don't want keyboard to stay open between fragments
        hideKeyboard();

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (toolbarElevation()) {
                actionBar.setElevation(4 * getActivity().getResources().getDisplayMetrics().density);
            } else {
                actionBar.setElevation(0);
            }
        }

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Set the title up
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getTitle());

        // Enable the home button in the action bar
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Change the home button icon for menu or back
        if (showUpNavigation()) {
            toolbar.setNavigationIcon(R.drawable.ic_navigation_back_white);
        } else {
            toolbar.setNavigationIcon(R.drawable.ic_menu_white);
        }

        if (isAppInLockTaskMode() == true && pinnedMode() == false) {
            lockMode(false);
        }

        setDrawerMenu();
    }

    public boolean getAuthRequired() {
        return true;
    }

    public boolean isBackAllowed() {
        return true;
    }

    public boolean toolbarElevation() {
        return true;
    }

    public String getTitle() {
        return "ISOPED";
    }

    public boolean pinnedMode() {
        return false;
    }

    public boolean showUpNavigation() {
        return false;
    }

    public void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // check if no view has focus:
        View v = getActivity().getCurrentFocus();
        if (v == null) {
            return;
        }

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void setDrawerMenu() {
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.drawer_navigation);
        Integer menuID = null;
        Integer currentMenuId = null;

        if (settings.isType(AppSettings.TYPES.PERSONAL)) {
            menuID = R.menu.drawer_personal;
        } else if (settings.isType(AppSettings.TYPES.PROFESSIONAL)) {
            if (getAuthRequired() == true) {
                menuID = R.menu.drawer_professional_locked;
            } else {
                menuID = R.menu.drawer_professional_unlocked;
            }
        }

        if (menuID != null) {
            if (navigationView.getTag() != null) {
                currentMenuId = (Integer) navigationView.getTag();
            }

            if (currentMenuId == null || navigationView.getMenu().size() == 0 || currentMenuId != menuID) {
                navigationView.getMenu().clear();
                navigationView.inflateMenu(menuID);
                navigationView.setTag(Integer.valueOf(menuID));
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);

        switch (item.getItemId()) {
            case android.R.id.home:
                if (showUpNavigation()) {
                    getActivity().onBackPressed();
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                return true;
            case R.id.menu_pin:
                if (isAppInLockTaskMode()) {
                    PinDialog dialog = new PinDialog((AppCompatActivity) getActivity(), new NavigationCallback((AppCompatActivity) getActivity()) {
                        @Override
                        public void run() {
                            lockMode(false);
                        }
                    });
                    dialog.show();
                } else {
                    lockMode(true);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isAppInLockTaskMode() {
        ActivityManager activityManager;

        activityManager = (ActivityManager)
                getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // For SDK version 23 and above.
            return activityManager.getLockTaskModeState()
                    != ActivityManager.LOCK_TASK_MODE_NONE;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // When SDK version >= 21. This API is deprecated in 23.
            return activityManager.isInLockTaskMode();
        }

        return false;
    }
}
