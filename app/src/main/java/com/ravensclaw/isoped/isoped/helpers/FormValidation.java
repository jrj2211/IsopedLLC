package com.ravensclaw.isoped.isoped.helpers;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CAD Station on 1/18/2017.
 */

public class FormValidation {

    private HashMap<View, Pair<TYPES, Boolean>> elements;

    public FormValidation() {
        elements = new HashMap<View, Pair<TYPES, Boolean>>();
    }

    public void add(final View container, final TYPES type, final boolean required) {
        elements.put(container, new Pair<TYPES, Boolean>(type, required));

        // When they click off of it, validate
        ((TextInputLayout) container).getEditText().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    validate(container, type, required);
                }
            }
        });

        // As they are typing
        ((TextInputLayout) container).getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                validate(container, type, required);
            }
        });
    }

    public boolean validate() {
        boolean errors = false;

        for (Map.Entry<View, Pair<TYPES, Boolean>> entry : elements.entrySet()) {
            if (validate(entry.getKey(), entry.getValue().first, entry.getValue().second) == false) {
                errors = true;
            }
        }

        return errors;
    }

    public boolean validate(View v, TYPES type, boolean required) {
        if (type == TYPES.NAME) {
            return validate_name(v, required);
        } else if (type == TYPES.PHONE) {
            return validate_phone(v, required);
        }

        return true;
    }

    private boolean validate_name(View v, boolean required) {
        boolean valid = true;
        String name = getValue(v);

        if (name.isEmpty() && required) {
            ((TextInputLayout) v).setErrorEnabled(true);
            ((TextInputLayout) v).setError("Cannot be blank");
            valid = false;
        } else {
            ((TextInputLayout) v).setErrorEnabled(false);
        }

        return valid;
    }

    private boolean validate_phone(View v, boolean required) {
        boolean valid = true;
        String phone = getValue(v);
        String regexStr = "(^[+]?[0-9-]{7,13}$)?";

        if (phone.isEmpty() && required) {
            ((TextInputLayout) v).setErrorEnabled(true);
            ((TextInputLayout) v).setError("Cannot be blank");
            valid = false;
        } else if (phone.matches(regexStr) == false) {
            ((TextInputLayout) v).setErrorEnabled(true);
            ((TextInputLayout) v).setError("Invalid phone number");
            valid = false;
        } else {
            ((TextInputLayout) v).setErrorEnabled(false);
        }

        return valid;
    }

    public String getValue(View view) {
        if (view instanceof TextInputLayout) {
            return ((TextInputLayout) view).getEditText().getText().toString();
        } else {
            return ((TextView) view).getText().toString();
        }
    }

    public enum TYPES {
        NAME,
        PHONE,
        BASIC
    }
}
