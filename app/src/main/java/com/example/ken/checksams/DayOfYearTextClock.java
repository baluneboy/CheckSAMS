package com.example.ken.checksams;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextClock;
import android.widget.Toast;

import android.content.*;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

/** TODO actually get DOY working
 * Implementation of a {@link TextClock} with native support for day of year field D.
 *
 * @author Ken Hrovat
 */
public class DayOfYearTextClock extends TextView {
    /** True if this view is currently attached to the window. */
    private boolean mAttached = false;
    /** The current time, as displayed. */
    private Calendar mTime;
    /** The current timezone. */
    private String mTimeZone;

    /** Show hours : minutes only */
    private static final String FORMAT="h:mm";

    public DayOfYearTextClock(Context context) {
        super(context);
    }

    public DayOfYearTextClock(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DayOfYearTextClock(Context context, AttributeSet attrs) {
        super(context, attrs);
        createTime(null);
    }

    /** Receive changes to timezone and change our clock accordingly */
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String timeZone = intent.getStringExtra("time-zone");
                createTime(timeZone);
            }
            onTimeChanged();
        }
    };

    /**
     * Runnable to change time every minute.
     */
    private final Runnable mTicker = new Runnable() {
        public void run() {
            onTimeChanged();
            // Wait a minute
            final long next = SystemClock.uptimeMillis() + (60 * 1000);
            getHandler().postAtTime(mTicker, next);
        }
    };

    /**
     * Initialize our time object to the current or default time zone
     * @param timeZone either a time zone specified in {@link TimeZone#getTimeZone(String)} or null to get the default
     *                 locale.
     */
    private void createTime(String timeZone) {
        if (timeZone != null) {
            mTime = Calendar.getInstance(TimeZone.getTimeZone(timeZone));
        } else {
            mTime = Calendar.getInstance();
        }
    }

    /**
     * Set the current view's time to the system time.
     */
    private void onTimeChanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        setText(DateFormat.format(FORMAT, mTime));
    }

    /**
     * Sets the specified time zone to use in this clock. When the time zone
     * is set through this method, system time zone changes (when the user
     * sets the time zone in settings for instance) will be ignored.
     *
     * @param timeZone The desired time zone's ID as specified in {@link TimeZone}
     *                 or null to user the time zone specified by the user
     *                 (system time zone)
     *
     * @see java.util.TimeZone#getAvailableIDs()
     * @see TimeZone#getTimeZone(String)
     */
    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
        createTime(timeZone);
        onTimeChanged();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!mAttached) {
            mAttached = true;
            registerReceiver();
        }
        onTimeChanged();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            unregisterReceiver();
            getHandler().removeCallbacks(mTicker);
            mAttached = false;
        }
    }

    private void registerReceiver() {
        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter, null, getHandler());
    }

    private void unregisterReceiver() {
        getContext().unregisterReceiver(mIntentReceiver);
    }
}