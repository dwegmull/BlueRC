package com.example.android.BlueRC;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
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

    // Check box widgets
    private CheckBox mTimeoutCkWidget;
    private CheckBox mThrottleCkWidget;
    private CheckBox mReverse1CkWidget;
    private CheckBox mReverse2CkWidget;
    private CheckBox mDrain1CkWidget;
    private CheckBox mDrain2CkWidget;
    private CheckBox mWhistleCkWidget;


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
        mTimeoutCkWidget = (CheckBox)findViewById(R.id.ck_timeout);
        mTimeoutCkWidget.setChecked(0 == BlueRC.calib2Value(BlueRC.REG_DISC_TIMEOUT_HI));

        mThrottleCkWidget = (CheckBox)findViewById(R.id.check_enable_throttle);
        mThrottleCkWidget.setChecked(!(0 == (BlueRC.FEATURES_THROTTLE & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mFullThrottleBar = (SeekBar)findViewById(R.id.seek_full_throttle);
        mFullThrottleBar.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_THROTTLE_HI));
        mMidThrottleBar = (SeekBar)findViewById(R.id.seek_mid_throttle);
        mMidThrottleBar.setProgress(BlueRC.calib2Value(BlueRC.REG_MI_THROTTLE_HI));
        mClosedThrottleBar = (SeekBar)findViewById(R.id.seek_mid_throttle);
        mClosedThrottleBar.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_THROTTLE_HI));

        mReverse1CkWidget = (CheckBox)findViewById(R.id.ck_reverse1);
        mReverse1CkWidget.setChecked(!(0 == (BlueRC.FEATURES_REVERSE1 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mReverse1Forward = (SeekBar)findViewById(R.id.seek_forward1);
        mReverse1Forward.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_REVERSE1_HI));
        mReverse1Stop = (SeekBar)findViewById(R.id.seek_stop1);
        mReverse1Stop.setProgress(BlueRC.calib2Value(BlueRC.REG_MI_REVERSE1_HI));
        mReverse1Reverse = (SeekBar)findViewById(R.id.seek_reverse1);
        mReverse1Reverse.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_REVERSE1_HI));

        mReverse2CkWidget = (CheckBox)findViewById(R.id.ck_reverse2);
        mReverse2CkWidget.setChecked(!(0 == (BlueRC.FEATURES_REVERSE2 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mReverse2Forward = (SeekBar)findViewById(R.id.seek_forward2);
        mReverse2Forward.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_REVERSE1_HI));
        mReverse2Stop = (SeekBar)findViewById(R.id.seek_stop1);
        mReverse2Stop.setProgress(BlueRC.calib2Value(BlueRC.REG_MI_REVERSE2_HI));
        mReverse2Reverse = (SeekBar)findViewById(R.id.seek_reverse2);
        mReverse2Reverse.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_REVERSE2_HI));

        mDrain1CkWidget = (CheckBox)findViewById(R.id.ck_drain1);
        mDrain1CkWidget.setChecked(!(0 == (BlueRC.FEATURES_DRAIN1 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mDrain1Open = (SeekBar)findViewById(R.id.seek_drain_on1);
        mDrain1Open.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_DRAIN1_HI));
        mDrain1Closed = (SeekBar)findViewById(R.id.seek_drain_off1);
        mDrain1Closed.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_DRAIN1_HI));

        mDrain2CkWidget = (CheckBox)findViewById(R.id.ck_drain2);
        mDrain2CkWidget.setChecked(!(0 == (BlueRC.FEATURES_DRAIN2 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mDrain2Open = (SeekBar)findViewById(R.id.seek_drain_on2);
        mDrain2Open.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_DRAIN2_HI));
        mDrain2Closed = (SeekBar)findViewById(R.id.seek_drain_off2);
        mDrain2Closed.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_DRAIN2_HI));

        mWhistleCkWidget = (CheckBox)findViewById(R.id.ck_whistle);
        mWhistleCkWidget.setChecked(!(0 == (BlueRC.FEATURES_WHISTLE & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mWhistleOpen = (SeekBar)findViewById(R.id.seek_whistle_on);
        mWhistleOpen.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_WHISTLE_HI));
        mWhistleClosed = (SeekBar)findViewById(R.id.seek_whistle_off);
        mWhistleClosed.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_WHISTLE_HI));
    }
}
