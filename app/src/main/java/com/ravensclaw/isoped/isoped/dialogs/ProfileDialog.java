package com.ravensclaw.isoped.isoped.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.fragments.BaseFragment;
import com.ravensclaw.isoped.isoped.fragments.UserAddFragment;
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

        String phone = user.getPhone();
        String notes = user.getNotes();

        if (phone == null || phone.isEmpty()) {
            phone = "No phone number saved";
        }

        if (notes == null || notes.isEmpty()) {
            notes = "No notes saved";
        }

        ((TextView) findViewById(R.id.user_name)).setText(user.getFullName());
        ((TextView) findViewById(R.id.phone)).setText(phone);
        ((TextView) findViewById(R.id.notes)).setText(notes);

        findViewById(R.id.edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putLong("uid", uid);
                BaseFragment frag = new UserAddFragment();
                frag.setArguments(bundle);

                BaseFragment.startFragment(activity, frag);
                dismiss();
            }
        });
    }
}
