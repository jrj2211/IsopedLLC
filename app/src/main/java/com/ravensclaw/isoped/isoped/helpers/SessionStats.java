package com.ravensclaw.isoped.isoped.helpers;

import android.content.Context;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private STATE curState = STATE.STOPPED;
    private View stats;
    private int curTime = 0;
    private int curCycles = 0;
    private Context context;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (curState == STATE.RUNNING) {
                mHandler.postDelayed(mRunnable, delay);

                // Update SessionStats
                setTime(curTime + delay);

                if (curTime % 1000 == 0) {
                    Random r = new Random();
                    if (r.nextBoolean()) {
                        incrementCycles();
                    }
                }
            }
        }
    };

    public SessionStats(ViewGroup container, LayoutInflater inflater, String label) {
        stats = inflater.inflate(R.layout.session_stats, null);
        container.addView(stats);

        context = inflater.getContext();

        mHandler = new Handler();

        reset();

        setLabel(label);

        stats.findViewById(R.id.session_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        stats.findViewById(R.id.session_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    public void setLabel(String label) {
        ((TextView) stats.findViewById(R.id.timer_label)).setText(label);
    }

    public void start() {
        if (curState != STATE.RUNNING) {
            mHandler.postDelayed(mRunnable, delay);
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
        setCycles(0);
    }

    public void setCycles(int cycles) {
        curCycles = cycles;
        ((TextView) stats.findViewById(R.id.cycles)).setText(Integer.toString(cycles) + " cycles");
    }

    public void incrementCycles() {
        setCycles(curCycles + 1);
    }

    private void setTime(int ms) {
        curTime = ms;
        ((TextView) stats.findViewById(R.id.timer_value)).setText(formatTime(curTime));
    }

    private String formatTime(int ms) {
        long seconds = (ms / 1000) % 60;
        long minutes = (ms / (1000 * 60)) % 60;
        long hours = (ms / (1000 * 60 * 60)) % 24;

        String time = "";
        if (hours > 0) {
            time += Long.toString(hours) + "h ";
        }

        time += Long.toString(minutes) + "m ";

        time += Long.toString(seconds) + "s ";

        return time;
    }

    private void updateButtons() {
        if (curState == STATE.RUNNING) {
            ((Button) stats.findViewById(R.id.session_button)).setText("PAUSE SESSION");
            ((Button) stats.findViewById(R.id.session_button)).setBackgroundColor(ContextCompat.getColor(context, R.color.resistance));
        } else if (curState == STATE.STOPPED) {
            ((Button) stats.findViewById(R.id.session_button)).setText("START SESSION");
            ((Button) stats.findViewById(R.id.session_button)).setBackgroundColor(ContextCompat.getColor(context, R.color.assistance));
        } else if (curState == STATE.PAUSED) {
            ((Button) stats.findViewById(R.id.session_button)).setText("RESUME SESSION");
            ((Button) stats.findViewById(R.id.session_button)).setBackgroundColor(ContextCompat.getColor(context, R.color.assistance));
        }
    }

    public enum STATE {
        STOPPED,
        RUNNING,
        PAUSED
    }
}
