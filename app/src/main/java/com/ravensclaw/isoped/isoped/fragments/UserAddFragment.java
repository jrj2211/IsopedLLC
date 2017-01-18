package com.ravensclaw.isoped.isoped.fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.helpers.FormValidation;

/**
 * Created by CAD Station on 12/20/2016.
 */

public class UserAddFragment extends BaseFragment {

    private User user = null;
    private FormValidation validator;

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
        super.onCreate(savedInstance);

        rootView = inflater.inflate(R.layout.user_add, container, false);

        // Try and load the user to edit
        Bundle args = getArguments();
        if (args != null && args.containsKey("uid")) {
            user = new User(getActivity(), args.getLong("uid"));

            // Update with users information if they exist
            ((TextInputLayout) rootView.findViewById(R.id.adduser_name_first)).getEditText().setText(user.getFirstName());
            ((TextInputLayout) rootView.findViewById(R.id.adduser_name_last)).getEditText().setText(user.getLastName());
            ((TextInputLayout) rootView.findViewById(R.id.adduser_phone)).getEditText().setText(user.getPhone());
            ((TextInputLayout) rootView.findViewById(R.id.adduser_notes)).getEditText().setText(user.getNotes());
        }

        ((Button) rootView.findViewById(R.id.adduser_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        // Setup validation
        validator = new FormValidation();
        validator.add(rootView.findViewById(R.id.adduser_name_first), FormValidation.TYPES.NAME, true);
        validator.add(rootView.findViewById(R.id.adduser_name_last), FormValidation.TYPES.NAME, true);
        validator.add(rootView.findViewById(R.id.adduser_phone), FormValidation.TYPES.PHONE, false);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (user != null) {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.delete_menu, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_delete:
                deleteUser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteUser() {
        new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_error)
                .setTitle("Delete user")
                .setMessage("Are you sure you want to permanently delete this user?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user.delete();
                        getActivity().onBackPressed();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

    private void submit() {
        boolean hasErrors = validator.validate();

        TextInputLayout firstName = (TextInputLayout) rootView.findViewById(R.id.adduser_name_first);
        TextInputLayout lastName = (TextInputLayout) rootView.findViewById(R.id.adduser_name_last);
        TextInputLayout phone = (TextInputLayout) rootView.findViewById(R.id.adduser_phone);
        TextInputLayout notes = (TextInputLayout) rootView.findViewById(R.id.adduser_notes);

        if (hasErrors == false) {
            ContentValues values = new ContentValues();
            values.put(User.Columns.COLUMN_NAME_FIRST, validator.getValue(firstName));
            values.put(User.Columns.COLUMN_NAME_LAST, validator.getValue(lastName));
            values.put(User.Columns.COLUMN_PHONE, validator.getValue(phone));
            values.put(User.Columns.COLUMN_NOTES, validator.getValue(notes));

            CharSequence text;

            if (user == null) {
                // Add a new user
                Long newUID = User.insert(getActivity(), values);
                User newUser = new User(getActivity(), newUID);

                // Toast message
                text = newUser.getFullName() + " was added!";
            } else {
                // Update current user information
                user.update(values);

                // Toast message
                text = user.getFullName() + " updated!";
            }

            // Show a toast
            Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
            toast.show();

            getActivity().onBackPressed();
        }
    }

}
