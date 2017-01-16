package com.ravensclaw.isoped.isoped.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;
import com.ravensclaw.isoped.isoped.database.DbHelper;
import com.ravensclaw.isoped.isoped.database.User;
import com.ravensclaw.isoped.isoped.fragments.usermode.DeviceControlFragment;
import com.ravensclaw.isoped.isoped.helpers.AppSettings;

/**
 * Created by Joe Jankowiak on 12/19/2016.
 */

public class UserListFragment extends BaseFragment {
    private LayoutInflater inflater;

    private View.OnClickListener addUserListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Show the add user screen
            BaseFragment.startFragment(getActivity(), new UserAddFragment());
        }
    };

    private View.OnClickListener selectUserListener = new View.OnClickListener() {
        public void onClick(View v) {
            // Get the user ID
            settings.setUser((Long) v.getTag());
            BaseFragment.startFragment(getActivity(), new DeviceControlFragment());
        }
    };

    @Override
    public String getTitle() {
        if (settings.isType(AppSettings.TYPES.PROFESSIONAL)) {
            return "User List";
        } else {
            return "Switch User";
        }
    }

    @Override
    public boolean showUpNavigation() {
        if (settings.isType(AppSettings.TYPES.PROFESSIONAL)) {
            return false;
        } else {
            return true;
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        super.onCreate(savedInstance);

        this.inflater = inflater;

        rootView = inflater.inflate(R.layout.users_list, container, false);

        updateTable();

        rootView.findViewById(R.id.user_add).setOnClickListener(addUserListener);

        return rootView;
    }

    public void updateTable() {
        TableLayout table = (TableLayout) rootView.findViewById(R.id.user_table);

        DbHelper dbHelper = DbHelper.getInstance(getContext());

        Cursor list = User.getList(getActivity());

        if (list.getCount() == 0) {
            rootView.findViewById(R.id.no_users).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.no_users).setVisibility(View.GONE);

            while (list.moveToNext()) {
                Long uid = list.getLong(list.getColumnIndex(User.Columns._ID));
                User user = new User(getActivity(), uid);
                TableRow row = (TableRow) inflater.inflate(R.layout.user_list_row, null);
                ((TextView) row.findViewById(R.id.userName)).setText(user.getFullName());
                row.setTag(uid);
                table.addView(row);
                row.setOnClickListener(selectUserListener);
            }
        }
    }
}
