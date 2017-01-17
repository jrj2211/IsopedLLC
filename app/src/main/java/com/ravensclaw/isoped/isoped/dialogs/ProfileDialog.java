package com.ravensclaw.isoped.isoped.dialogs;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.fragments.usermode.DeviceControlFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by CAD Station on 1/16/2017.
 */

public class ProfileDialog extends Dialog {

    private AppCompatActivity activity;
    private AppSettings settings;

    public ProfileDialog(AppCompatActivity a, final long uid) {
        super(a);
        this.activity = a;
        this.setContentView(R.layout.user_profile);

        settings = AppSettings.getInstance(activity);

        User user = new User(a, uid);

        ((TextView) findViewById(R.id.user_name)).setText(user.getFullName());

        findViewById(R.id.device_control).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settings.setUser(uid);
                BaseFragment.startFragment(activity, new DeviceControlFragment());
                dismiss();
            }
        });
    }
}
