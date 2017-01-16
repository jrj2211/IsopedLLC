package com.ravensclaw.isoped.isoped.dialogs;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;

import com.ravensclaw.isoped.isoped.R;

/**
 * Created by CAD Station on 1/16/2017.
 */

public class ProfileDialog extends Dialog {

    private AppCompatActivity activity;

    public ProfileDialog(AppCompatActivity a, long uid) {
        super(a);
        this.activity = a;
        this.setContentView(R.layout.user_profile);
    }
}
