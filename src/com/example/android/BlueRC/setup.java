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
        mFullThrottleBar.setOnSeekBarChangeListener(mSeekListener);
        mMidThrottleBar = (SeekBar)findViewById(R.id.seek_mid_throttle);
        mMidThrottleBar.setProgress(BlueRC.calib2Value(BlueRC.REG_MI_THROTTLE_HI));
        mMidThrottleBar.setOnSeekBarChangeListener(mSeekListener);
        mClosedThrottleBar = (SeekBar)findViewById(R.id.seek_closed_throttle);
        mClosedThrottleBar.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_THROTTLE_HI));
        mClosedThrottleBar.setOnSeekBarChangeListener(mSeekListener);

        mReverse1CkWidget = (CheckBox)findViewById(R.id.ck_reverse1);
        mReverse1CkWidget.setChecked(!(0 == (BlueRC.FEATURES_REVERSE1 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mReverse1Forward = (SeekBar)findViewById(R.id.seek_forward1);
        mReverse1Forward.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_REVERSE1_HI));
        mReverse1Forward.setOnSeekBarChangeListener(mSeekListener);
        mReverse1Stop = (SeekBar)findViewById(R.id.seek_stop1);
        mReverse1Stop.setProgress(BlueRC.calib2Value(BlueRC.REG_MI_REVERSE1_HI));
        mReverse1Stop.setOnSeekBarChangeListener(mSeekListener);
        mReverse1Reverse = (SeekBar)findViewById(R.id.seek_reverse1);
        mReverse1Reverse.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_REVERSE1_HI));
        mReverse1Reverse.setOnSeekBarChangeListener(mSeekListener);

        mReverse2CkWidget = (CheckBox)findViewById(R.id.ck_reverse2);
        mReverse2CkWidget.setChecked(!(0 == (BlueRC.FEATURES_REVERSE2 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mReverse2Forward = (SeekBar)findViewById(R.id.seek_forward2);
        mReverse2Forward.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_REVERSE1_HI));
        mReverse2Forward.setOnSeekBarChangeListener(mSeekListener);
        mReverse2Stop = (SeekBar)findViewById(R.id.seek_stop1);
        mReverse2Stop.setProgress(BlueRC.calib2Value(BlueRC.REG_MI_REVERSE2_HI));
        mReverse2Stop.setOnSeekBarChangeListener(mSeekListener);
        mReverse2Reverse = (SeekBar)findViewById(R.id.seek_reverse2);
        mReverse2Reverse.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_REVERSE2_HI));
        mReverse2Reverse.setOnSeekBarChangeListener(mSeekListener);

        mDrain1CkWidget = (CheckBox)findViewById(R.id.ck_drain1);
        mDrain1CkWidget.setChecked(!(0 == (BlueRC.FEATURES_DRAIN1 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mDrain1Open = (SeekBar)findViewById(R.id.seek_drain_on1);
        mDrain1Open.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_DRAIN1_HI));
        mDrain1Open.setOnSeekBarChangeListener(mSeekListener);
        mDrain1Closed = (SeekBar)findViewById(R.id.seek_drain_off1);
        mDrain1Closed.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_DRAIN1_HI));
        mDrain1Closed.setOnSeekBarChangeListener(mSeekListener);

        mDrain2CkWidget = (CheckBox)findViewById(R.id.ck_drain2);
        mDrain2CkWidget.setChecked(!(0 == (BlueRC.FEATURES_DRAIN2 & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mDrain2Open = (SeekBar)findViewById(R.id.seek_drain_on2);
        mDrain2Open.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_DRAIN2_HI));
        mDrain2Open.setOnSeekBarChangeListener(mSeekListener);
        mDrain2Closed = (SeekBar)findViewById(R.id.seek_drain_off2);
        mDrain2Closed.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_DRAIN2_HI));
        mDrain2Closed.setOnSeekBarChangeListener(mSeekListener);

        mWhistleCkWidget = (CheckBox)findViewById(R.id.ck_whistle);
        mWhistleCkWidget.setChecked(!(0 == (BlueRC.FEATURES_WHISTLE & BlueRC.calib2Value(BlueRC.REG_FEATURES_HI))));

        mWhistleOpen = (SeekBar)findViewById(R.id.seek_whistle_on);
        mWhistleOpen.setProgress(BlueRC.calib2Value(BlueRC.REG_HI_WHISTLE_HI));
        mWhistleOpen.setOnSeekBarChangeListener(mSeekListener);
        mWhistleClosed = (SeekBar)findViewById(R.id.seek_whistle_off);
        mWhistleClosed.setProgress(BlueRC.calib2Value(BlueRC.REG_LO_WHISTLE_HI));
        mWhistleClosed.setOnSeekBarChangeListener(mSeekListener);
    }

    private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener()
    {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            if((seekBar.equals(mFullThrottleBar)) || (seekBar.equals(mMidThrottleBar)) || (seekBar.equals(mClosedThrottleBar)))
            {
                String message = new String();
                message = "@C0001";
                message = message.concat(BlueRC.int2String(progress));
                message = message.concat("!");
                BlueRC.sendMessageRc(message);
            }
            if((seekBar.equals(mReverse1Forward)) || (seekBar.equals(mReverse1Stop)) || (seekBar.equals(mReverse1Reverse)))
            {
                String message = new String();
                message = "@C0101";
                message = message.concat(BlueRC.int2String(progress));
                message = message.concat("!");
                BlueRC.sendMessageRc(message);
            }
            if((seekBar.equals(mReverse2Forward)) || (seekBar.equals(mReverse2Stop)) || (seekBar.equals(mReverse2Reverse)))
            {
                String message = new String();
                message = "@C0201";
                message = message.concat(BlueRC.int2String(progress));
                message = message.concat("!");
                BlueRC.sendMessageRc(message);
            }
            if((seekBar.equals(mDrain1Open)) || (seekBar.equals(mDrain1Closed)))
            {
                String message = new String();
                message = "@C0301";
                message = message.concat(BlueRC.int2String(progress));
                message = message.concat("!");
                BlueRC.sendMessageRc(message);
            }
            if((seekBar.equals(mDrain2Open)) || (seekBar.equals(mDrain2Closed)))
            {
                String message = new String();
                message = "@C0401";
                message = message.concat(BlueRC.int2String(progress));
                message = message.concat("!");
                BlueRC.sendMessageRc(message);
            }
            if((seekBar.equals(mWhistleOpen)) || (seekBar.equals(mWhistleClosed)))
            {
                String message = new String();
                message = "@C0501";
                message = message.concat(BlueRC.int2String(progress));
                message = message.concat("!");
                BlueRC.sendMessageRc(message);
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar)
        {
            // TODO Auto-generated method stub
        }

        public void onStopTrackingTouch(SeekBar seekBar)
        {
            // TODO Auto-generated method stub
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Before we go back to the main page, update all the calibration variables
        BlueRC.mTimeout = Integer.parseInt(mTimeoutWidget.getText().toString());

        // Throttle
        if(mThrottleCkWidget.isChecked())
        {
            BlueRC.mFeatures |= BlueRC.FEATURES_THROTTLE;
        }
        else
        {
            BlueRC.mFeatures &= ~BlueRC.FEATURES_THROTTLE;
        }
        BlueRC.mCalibValues[BlueRC.CALIB_THROTTLE_HI] = mFullThrottleBar.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_THROTTLE_MID] = mMidThrottleBar.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_THROTTLE_LOW] = mClosedThrottleBar.getProgress();

        // Reverser 1
        if(mReverse1CkWidget.isChecked())
        {
            BlueRC.mFeatures |= BlueRC.FEATURES_REVERSE1;
        }
        else
        {
            BlueRC.mFeatures &= ~BlueRC.FEATURES_REVERSE1;
        }
        BlueRC.mCalibValues[BlueRC.CALIB_REVERSE1_HI] = mReverse1Forward.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_REVERSE1_MID] = mReverse1Stop.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_REVERSE1_LOW] = mReverse1Reverse.getProgress();

        // Reverser 2
        if(mReverse2CkWidget.isChecked())
        {
            BlueRC.mFeatures |= BlueRC.FEATURES_REVERSE2;
        }
        else
        {
            BlueRC.mFeatures &= ~BlueRC.FEATURES_REVERSE2;
        }
        BlueRC.mCalibValues[BlueRC.CALIB_REVERSE2_HI] = mReverse2Forward.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_REVERSE2_MID] = mReverse2Stop.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_REVERSE2_LOW] = mReverse2Reverse.getProgress();

        // Drain 1
        if(mDrain1CkWidget.isChecked())
        {
            BlueRC.mFeatures |= BlueRC.FEATURES_DRAIN1;
        }
        else
        {
            BlueRC.mFeatures &= ~BlueRC.FEATURES_DRAIN1;
        }
        BlueRC.mCalibValues[BlueRC.CALIB_DRAIN1_HI] = mDrain1Open.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_DRAIN1_LOW] = mDrain1Closed.getProgress();

        // Drain 2
        if(mDrain2CkWidget.isChecked())
        {
            BlueRC.mFeatures |= BlueRC.FEATURES_DRAIN2;
        }
        else
        {
            BlueRC.mFeatures &= ~BlueRC.FEATURES_DRAIN2;
        }
        BlueRC.mCalibValues[BlueRC.CALIB_DRAIN2_HI] = mDrain2Open.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_DRAIN2_LOW] = mDrain2Closed.getProgress();

        // Whistle
        if(mWhistleCkWidget.isChecked())
        {
            BlueRC.mFeatures |= BlueRC.FEATURES_WHISTLE;
        }
        else
        {
            BlueRC.mFeatures &= ~BlueRC.FEATURES_WHISTLE;
        }
        BlueRC.mCalibValues[BlueRC.CALIB_WHISTLE_HI] = mWhistleOpen.getProgress();
        BlueRC.mCalibValues[BlueRC.CALIB_WHISTLE_LOW] = mWhistleClosed.getProgress();
    }
}
