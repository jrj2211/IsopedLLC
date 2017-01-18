package com.ravensclaw.isoped.isoped.fragments;

import android.content.ContentValues;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.database.User;

/**
 * Created by CAD Station on 12/20/2016.
 */

public class UserAddFragment extends BaseFragment {

    private User user = null;

    @Override
    public String getTitle() {
        if (user != null) {
            return "Edit User";
        } else {
            return "Add New User";
        }
    }

    @Override
    public boolean showUpNavigation() {
        return true;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        Bundle args = getArguments();
        user = new User(getActivity(), args.getLong("uid"));

        super.onCreate(savedInstance);

        setHasOptionsMenu(true);

        rootView = inflater.inflate(R.layout.user_add, container, false);

        ((Button) rootView.findViewById(R.id.adduser_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (user != null) {
            inflater.inflate(R.menu.delete_menu, menu);
            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_delete:
                super.popBackStack();
                return true;
        }

        return false;
    }

    private void submit() {
        int numErrors = 0;

        // Get the error icon
        Drawable errorIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_error);
        errorIcon.setBounds(0, 0, errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        TextInputLayout firstNameView = (TextInputLayout) rootView.findViewById(R.id.adduser_name_first);
        TextInputLayout lastNameView = (TextInputLayout) rootView.findViewById(R.id.adduser_name_last);
        TextInputLayout phoneView = (TextInputLayout) rootView.findViewById(R.id.adduser_phone);

        String firstName = firstNameView.getEditText().getText().toString();
        String lastName = lastNameView.getEditText().getText().toString();
        String phone = phoneView.getEditText().getText().toString();

        if (firstName.isEmpty()) {
            firstNameView.setError("Cannot be blank");
            numErrors++;
        }

        if (lastName.isEmpty()) {
            lastNameView.setError("Cannot be blank");
            numErrors++;
        }

        if (phone.isEmpty()) {
            phoneView.setError("Cannot be blank");
            numErrors++;
        }

        if (numErrors == 0) {
            // Add a new user
            ContentValues values = new ContentValues();
            values.put(User.Columns.COLUMN_NAME_FIRST, firstName);
            values.put(User.Columns.COLUMN_NAME_LAST, lastName);
            User.insert(getActivity(), values);

            popBackStack();
        }
    }
}
