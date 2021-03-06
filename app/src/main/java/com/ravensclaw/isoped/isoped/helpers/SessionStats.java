package com.ravensclaw.isoped.isoped.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ravensclaw.isoped.isoped.R;

import java.util.Random;

/**
 * Created by CAD Station on 1/5/2017.
 */

public class SessionStats {

    private Handler mHandler;
    private int delay = 100;
    private int curTime = 0;
    private int curCycles = 0;
    private STATE curState = STATE.STOPPED;
    private Activity activity;
    private TextView timerView;
    private TextView cyclesView;
    private Button sessionButton;
    private Button resetButton;
    private DeviceInfoRow deviceAngle;
    private DeviceInfoRow cyclePace;

    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (curState == STATE.RUNNING) {
                mHandler.postDelayed(mTimerRunnable, delay);

                // Update SessionStats
                setTime(curTime + delay);
            }
        }
    };

    public SessionStats(Activity a) {
        activity = a;
        mHandler = new Handler();
        reset();
    }

    public SessionStats setDeviceAngleView(DeviceInfoRow info) {
        deviceAngle = info;
        return this;
    }

    public SessionStats setCyclePaceView(DeviceInfoRow info) {
        cyclePace = info;
        return this;
    }

    public SessionStats setTimerView(TextView view) {
        timerView = view;
        return this;
    }

    public SessionStats setCyclesView(TextView view) {
        cyclesView = view;
        return this;
    }

    public void start() {
        if (curState != STATE.RUNNING) {
            mHandler.postDelayed(mTimerRunnable, delay);
            curState = STATE.RUNNING;
        }
        updateButtons();
    }

    public void pause() {
        if (curState == STATE.RUNNING) {
            curState = STATE.PAUSED;
            updateButtons();
        }
    }

    public void stop() {
        curState = STATE.STOPPED;
        updateButtons();
    }

    public void toggle() {
        if (curState == STATE.RUNNING) {
            pause();
        } else {
            start();
        }
    }

    public boolean isRunning() {
        return curState == STATE.RUNNING;
    }

    public void reset() {
        stop();
        setTime(0);
        setCycles(0, false);
    }

    public void setCycles(int cycles, boolean increment) {
        if (increment) {
            curCycles += cycles;
        } else {
            curCycles = cycles;
        }

        if (cyclesView != null) {
            cyclesView.setText(Integer.toString(curCycles));
        }
    }

    public void setElevation(int elevation) {
        deviceAngle.setValue(elevation);
    }

    public void setSessionButton(Button button) {
        sessionButton = button;

        sessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    public void setResetButton(Button button) {
        resetButton = button;

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setIcon(R.drawable.ic_error)
                        .setTitle("Reset Session")
                        .setMessage("Are you sure you want to stop the session?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reset();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void setTime(int ms) {
        if (timerView != null) {
            curTime = ms;
            timerView.setText(formatTime(curTime));
        }
    }

    private String formatTime(int ms) {
        long seconds = (ms / 1000) % 60;
        long minutes = (ms / (1000 * 60)) % 60;
        long hours = (ms / (1000 * 60 * 60)) % 24;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    private void updateButtons() {
        if (sessionButton != null) {
            if (curState == STATE.RUNNING) {
                sessionButton.setText("STOP");
                sessionButton.setBackgroundColor(ContextCompat.getColor(activity, R.color.resistance));
            } else if (curState == STATE.STOPPED) {
                sessionButton.setText("START");
                sessionButton.setBackgroundColor(ContextCompat.getColor(activity, R.color.assistance));
            } else if (curState == STATE.PAUSED) {
                sessionButton.setText("RESUME");
                sessionButton.setBackgroundColor(ContextCompat.getColor(activity, R.color.assistance));
            }
        }
    }

    public enum STATE {
        STOPPED,
        RUNNING,
        PAUSED
    }
}
