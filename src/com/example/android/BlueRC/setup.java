package com.example.android.BlueRC;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

/*
 Setup activity.
 It is started by BlueRC when the user clicks on the setup button
 It allows the user to adjust the set points of the servos and to
 set other global parameters
*/
public class setup extends Activity
{
    private String mCalibrationData;

    // seek bar widgets
    private SeekBar mFullThrottleBar;
    private SeekBar mMidThrottleBar;
    private SeekBar mClosedThrottleBar;
    private SeekBar mReverse1Forward;
    private SeekBar mReverse1Stop;
    private SeekBar mReverse1Reverse;
    private SeekBar mReverse2Forward;
    private SeekBar mReverse2Stop;
    private SeekBar mReverse2Reverse;
    private SeekBar mDrain1Open;
    private SeekBar mDrain1Closed;
    private SeekBar mDrain2Open;
    private SeekBar mDrain2Closed;
    private SeekBar mWhistleOpen;
    private SeekBar mWhistleClosed;
    // text widgets
    private TextView mLocomotiveName;
    private TextView mTimeoutWidget;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set up the window layout
        setContentView(R.layout.setup);
        // Get the extra data that came from the main app (the current calibration data)
        Bundle b = getIntent().getExtras();

        mCalibrationData = b.getString(BlueRC.EXTRA_MESSAGE);

        // Set all the widget contents based on the calibration data
        mTimeoutWidget = (TextView)findViewById(R.id.text_timeout);
        mTimeoutWidget.setText(String.format("%d",BlueRC.calib2Value(BlueRC.REG_DISC_TIMEOUT_HI)));
    }
}
