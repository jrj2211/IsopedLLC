package com.ravensclaw.isoped.isoped.helpers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;

/**
 * Created by CAD Station on 12/30/2016.
 */

public class NavigationCallback {
    private AppCompatActivity mActivity;
    private Fragment mFragment;

    public NavigationCallback(AppCompatActivity activity, Fragment fragment) {
        mActivity = activity;
        mFragment = fragment;
    }

    public NavigationCallback(AppCompatActivity activity) {
        mActivity = activity;
    }

    public void run() {
        if (mFragment != null) {
            FragmentManager fragManager = mActivity.getSupportFragmentManager();

            BaseFragment currentFragment = (BaseFragment) fragManager.findFragmentById(R.id.fragment_container);

            if (currentFragment != null) {
                if (currentFragment.getAuthRequired() != ((BaseFragment) mFragment).getAuthRequired()) {
                    Log.e("Clear", "CLEAR THE BACKSTACK!");
                }
            }

            FragmentTransaction transaction = fragManager.beginTransaction();

            transaction.replace(R.id.fragment_container, mFragment);

            transaction.addToBackStack(null);

            transaction.commit();
        } else {
            mActivity.onBackPressed();
        }
    }
}
