/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.BlueRC;

import android.app.ActionBar;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.*;

import static android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BlueRC extends Activity
{
    // Servo registers
    public static final int  REG_THROTTLE    =    0;
    public static final int  REG_REVERSE1    =    1;
    public static final int  REG_REVERSE2    =    2;
    public static final int  REG_DRAIN1      =    3;
    public static final int  REG_DRAIN2      =    4;
    public static final int  REG_WHISTLE     =    5;

    // Calibration register offsets in the calibration string
    public static final int  REG_EEPROM_VALID_HI =   6;
    public static final int  REG_EEPROM_VALID_LO =   7;
    public static final int  REG_FEATURES_HI     =   8;
    public static final int  REG_FEATURES_LO     =   9;
    public static final int  REG_LO_THROTTLE_HI  =   10;
    public static final int  REG_LO_THROTTLE_LO  =   11;
    public static final int  REG_MI_THROTTLE_HI  =   12;
    public static final int  REG_MI_THROTTLE_LO  =   13;
    public static final int  REG_HI_THROTTLE_HI  =   14;
    public static final int  REG_HI_THROTTLE_LO  =   15;
    public static final int  REG_LO_REVERSE1_HI  =   16;
    public static final int  REG_LO_REVERSE1_LO  =   17;
    public static final int  REG_MI_REVERSE1_HI  =   18;
    public static final int  REG_MI_REVERSE1_LO  =   19;
    public static final int  REG_HI_REVERSE1_HI  =   20;
    public static final int  REG_HI_REVERSE1_LO  =   21;
    public static final int  REG_LO_REVERSE2_HI  =   22;
    public static final int  REG_LO_REVERSE2_LO  =   23;
    public static final int  REG_MI_REVERSE2_HI  =   24;
    public static final int  REG_MI_REVERSE2_LO  =   25;
    public static final int  REG_HI_REVERSE2_HI  =   26;
    public static final int  REG_HI_REVERSE2_LO  =   27;
    public static final int  REG_LO_DRAIN1_HI    =   28;
    public static final int  REG_LO_DRAIN1_LO    =   29;
    public static final int  REG_HI_DRAIN1_HI    =   30;
    public static final int  REG_HI_DRAIN1_LO    =   31;
    public static final int  REG_LO_DRAIN2_HI    =   32;
    public static final int  REG_LO_DRAIN2_LO    =   33;
    public static final int  REG_HI_DRAIN2_HI    =   34;
    public static final int  REG_HI_DRAIN2_LO    =   35;
    public static final int  REG_LO_WHISTLE_HI   =   36;
    public static final int  REG_LO_WHISTLE_LO   =   37;
    public static final int  REG_HI_WHISTLE_HI   =   38;
    public static final int  REG_HI_WHISTLE_LO   =   39;
    public static final int  REG_DISC_TIMEOUT_HI =   40;
    public static final int  REG_DISC_TIMEOUT_LO =   41;
    public static final int  REG_VERSION_H_HI    =   42;
    public static final int  REG_VERSION_H_LO    =   43;
    public static final int  REG_VERSION_F_HI    =   44;
    public static final int  REG_VERSION_F_LO    =   45;
    public static final int  REG_VERSION_P_HI    =   46;
    public static final int  REG_VERSION_P_LO    =   47;
    public static final int  REG_SAFE_THROTTLE_HI =  48;
    public static final int  REG_SAFE_THROTTLE_LO =  49;
    public static final int  REG_SAFE_REVERSE1_HI =  50;
    public static final int  REG_SAFE_REVERSE1_LO =  51;
    public static final int  REG_SAFE_REVERSE2_HI =  52;
    public static final int  REG_SAFE_REVERSE2_LO =  53;
    public static final int  REG_SAFE_DRAIN1_HI   =  54;
    public static final int  REG_SAFE_DRAIN1_LO   =  55;
    public static final int  REG_SAFE_DRAIN2_HI   =  56;
    public static final int  REG_SAFE_DRAIN2_LO   =  57;
    public static final int  REG_SAFE_WHISTLE_HI  =  58;
    public static final int  REG_SAFE_WHISTLE_LO  =  59;
    public static final int  REG_DESC_SIZE_HI     =  60;
    public static final int  REG_DESC_SIZE_LO     =  61;
    public static final int  REG_DESCRIPTION_HI   =  62;
    public static final int  REG_DESCRIPTION_LO   =  63;

    // Message sent to the setup activity
    public final static String EXTRA_MESSAGE = "com.example.android.BlueRC.MESSAGE";

    // Debugging
    private static final String TAG = "BlueRC";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int SAVE_SETUP = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private SeekBar mThrottleBar;
    private SeekBar mWhistleBar;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    // Calibration data stored inside the receiver's EEPROM.
    private static String mCalibrationData = new String("");

    // Calibration values
    private int mCalibThrottleLow = 70;
    private int mCalibThrottleMid = 90;
    private int mCalibThrottleHigh = 110;
    private int mCalibReverse1Low = 70;
    private int mCalibReverse1Mid = 90;
    private int mCalibReverse1High = 110;
    private int mCalibReverse2Low = 70;
    private int mCalibReverse2Mid = 90;
    private int mCalibReverse2High = 110;
    private int mCalibDrain1Open = 70;
    private int mCalibDrain1Closed = 110;
    private int mCalibDrain2Open = 70;
    private int mCalibDrain2Closed = 110;
    private int mCalibWhistleOpen = 70;
    private int mCalibWhistleClosed = 110;

    // Other EEPROM registers
    private int mEEPROMValid = 0x42;
    private int mFeatures = 0x00;

    // Safe Values
    private int mSafeThrottle = mCalibThrottleLow;
    private int mSafeReverse1 = mCalibReverse1Mid;
    private int mSafeReverse2 = mCalibReverse2Mid;
    private int mSafeDrain1 = mCalibDrain1Open;
    private int mSafeDrain2 = mCalibDrain2Open;
    private int mSafeWhistle = mCalibWhistleClosed;

    private boolean mWarnOnNoConnection = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        mThrottleBar = (SeekBar) findViewById(R.id.seekBar_Throttle);

        mThrottleBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser)
            {
                // Build a packet based on the new throttle value.
                String message = new String();
                message = "@C0001";
                progress = Calibrate(progress, mCalibThrottleLow, mCalibThrottleMid, mCalibThrottleHigh);
                message = message.concat(int2String(progress));
                message = message.concat("!");
//                TextView debugText = (TextView) findViewById(R.id.text_whistle);
//                debugText.setText(message.toCharArray(),0,message.length());
                sendMessageRc(message);
            }
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
            }
        });
        mWhistleBar = (SeekBar) findViewById(R.id.seekBar_whistle);

        mWhistleBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser)
            {
                // Build a packet based on the new whistle value.
                String message = new String();
                message = "@C0501";
                progress = Calibrate(progress, mCalibWhistleOpen, mCalibWhistleClosed);
                message = message.concat(int2String(progress));
                message = message.concat("!");
                sendMessageRc(message);
            }
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
            }

            public void onStopTrackingTouch(SeekBar seekBar)
            {
                // TODO Auto-generated method stub
            }
        });


    }
    @Override
    public void onStart()
    {
        super.onStart();
        if (D) Log.e(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            if (mChatService == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume()
    {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null)
        {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE)
            {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private void setupChat()
    {
        Log.d(TAG, "setupChat()");

        // Initialize the seek bar variables
        mThrottleBar = (SeekBar) findViewById(R.id.seekBar_Throttle);
        mWhistleBar = (SeekBar) findViewById(R.id.seekBar_whistle);
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

    }

    @Override
    public synchronized void onPause()
    {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if (D) Log.e(TAG, "--- ON DESTROY ---");
    }


    public static int ascii2Digit(char c)
    {
        if ((c < 0x40) && (c > 0x2F))
        {
            return (int) (c - 0x30);
        }
        if ((c < 0x47) && (c > 0x40))
        {
            return (int) (c - 0x41 + 10);
        }
        return 0;
    }

    public static int calib2Value(int register)
    {
        char buff[] = new char[3];

        mCalibrationData.getChars(register, register + 1, buff, 0);
        return (ascii2Digit(buff[0]) << 4) + (ascii2Digit(buff[1]));
    }

    public String int2String(int value)
    {
        String s = new String();
        if (16 > value)
        {
            s = "0"; // Place a leading 0 to ensure a constant length of two characters per HEX digit
        }
        s = Integer.toHexString((value & 0x00F0) >> 4);
        s += Integer.toHexString(value & 0x000F);
        s = s.toUpperCase();
        return s;

    }

    private int doCalibration(int value, int lowLimit, int highLimit, int idealRange)
    {
        // Check for identical limits
        if(lowLimit == highLimit)
        {
            return lowLimit;
        }
        // Check if the range is inverted
        if(lowLimit > highLimit)
        {
            int temp = lowLimit;
            lowLimit = highLimit;
            highLimit = temp;
            value = idealRange - value;
        }
        int range = highLimit - lowLimit;
        float temp = (float) idealRange / (float) range;
        temp = (float)value / temp;
        return (int)temp + lowLimit;

    }
    // Calibration for axes that have just an upper and lower limit
    private int Calibrate(int value, int lowLimit, int highLimit)
    {
        return doCalibration(value, lowLimit, highLimit, 180);
    }

    // Calibration for axes with a center value in addition to the lower and upper limits
    private int Calibrate(int value, int lowLimit, int midLimit, int highLimit)
    {
        // Check which side of the center is the value on
        if(value == 90)
        {
            return midLimit;
        }
        if(value < 90)
        {
            // We need to produce a value between lowLimit and midLimit
            return doCalibration(value, lowLimit, midLimit, 90);
        }
        else
        {
            // We need to produce a value between midLimit and highLimit
            return doCalibration(value - 90, midLimit, highLimit, 90);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    private void sendMessageRc(String message)
    {
        // Check that we're actually connected before trying anything
        if ((mChatService.getState() != BluetoothChatService.STATE_CONNECTED) && mWarnOnNoConnection)
        {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            mWarnOnNoConnection = false;
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0)
        {
            if (!message.contentEquals("$$$"))
            {
                message = message.concat("\n");
            }
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //           mOutStringBuffer.setLength(0);
            //           mOutEditText.setText(mOutStringBuffer);
        }
    }


    private final void setStatus(int resId)
    {
        final ActionBar actionBar = getActionBar();
        if (null != actionBar)
        {
            actionBar.setSubtitle(resId);
        }
    }

    private final void setStatus(CharSequence subTitle)
    {
        final ActionBar actionBar = getActionBar();
        if (null != actionBar)
        {
            actionBar.setSubtitle(subTitle);
        }
    }

    private void buildCalibrationString()
    {
        String message = new String();
        mEEPROMValid = 0x42;
        message = "@C061B";
        message = message.concat(int2String(mEEPROMValid));
        message = message.concat(int2String(mFeatures));
        message = message.concat(int2String(mCalibThrottleLow));
        message = message.concat(int2String(mCalibThrottleMid));
        message = message.concat(int2String(mCalibThrottleHigh));
        message = message.concat(int2String(mCalibReverse1Low));
        message = message.concat(int2String(mCalibReverse1Mid));
        message = message.concat(int2String(mCalibReverse1High));
        message = message.concat(int2String(mCalibReverse2Low));
        message = message.concat(int2String(mCalibReverse2Mid));
        message = message.concat(int2String(mCalibReverse2High));
        message = message.concat(int2String(mCalibDrain1Open));
        message = message.concat(int2String(mCalibDrain1Closed));
        message = message.concat(int2String(mCalibDrain2Open));
        message = message.concat(int2String(mCalibDrain2Closed));
        message = message.concat(int2String(mCalibWhistleOpen));
        message = message.concat(int2String(mCalibWhistleClosed));
        mCalibrationData = message;
    }

    // Decodes messages coming from the locomotive.
    private void decodeMessages(String message)
    {
        if (message.contains("#A061B"))
        {
            // This message contains the calibration data
            mCalibrationData = message;
            if (0x42 == calib2Value(REG_EEPROM_VALID_HI))
            {
                // This is valid calibration data: use it!
                mCalibThrottleLow = calib2Value(REG_LO_THROTTLE_HI);
                mCalibThrottleMid = calib2Value(REG_MI_THROTTLE_HI);
                mCalibThrottleHigh = calib2Value(REG_HI_THROTTLE_HI);
                mCalibReverse1Low = calib2Value(REG_LO_REVERSE1_HI);
                mCalibReverse1Mid = calib2Value(REG_MI_REVERSE1_HI);
                mCalibReverse1High = calib2Value(REG_HI_REVERSE1_HI);
                mCalibReverse2Low = calib2Value(REG_LO_REVERSE2_HI);
                mCalibReverse2Mid = calib2Value(REG_MI_REVERSE2_HI);
                mCalibReverse2High = calib2Value(REG_HI_REVERSE2_HI);
                mCalibDrain1Open = calib2Value(REG_LO_DRAIN1_HI);
                mCalibDrain1Closed = calib2Value(REG_HI_DRAIN1_HI);
                mCalibDrain2Open = calib2Value(REG_LO_DRAIN2_HI);
                mCalibDrain2Closed = calib2Value(REG_HI_DRAIN2_HI);
                mCalibWhistleOpen = calib2Value(REG_LO_WHISTLE_HI);
                mCalibWhistleClosed = calib2Value(REG_HI_WHISTLE_HI);

            }
            else
            {
                // Invalid EEPROM data: use safe defaults.
                mCalibThrottleLow = 70;
                mCalibThrottleMid = 90;
                mCalibThrottleHigh = 110;
                mCalibReverse1Low = 70;
                mCalibReverse1Mid = 90;
                mCalibReverse1High = 110;
                mCalibReverse2Low = 70;
                mCalibReverse2Mid = 90;
                mCalibReverse2High = 110;
                mCalibDrain1Open = 70;
                mCalibDrain1Closed = 110;
                mCalibDrain2Open = 70;
                mCalibDrain2Closed = 110;
                mCalibWhistleOpen = 70;
                mCalibWhistleClosed = 110;
            }

        }
        if (message.contains("#N"))
        {
            // Let the user know we received a negative ack. Something is up!
            message = message.concat(" error! ");
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler;

    {
        mHandler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what)
                {
                    case MESSAGE_STATE_CHANGE:
                        if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                        switch (msg.arg1)
                        {
                            case BluetoothChatService.STATE_CONNECTED:
                                setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                                // Get ready to warn the user once if the connection goes away.
                                mWarnOnNoConnection = true;
                                // Query the receiver for its calibration data
                                mCalibrationData = "@Q061A!";
                                sendMessageRc(mCalibrationData);
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                setStatus(R.string.title_connecting);
                                break;
                            case BluetoothChatService.STATE_LISTEN:
                            case BluetoothChatService.STATE_NONE:
                                setStatus(R.string.title_not_connected);
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        // construct a string from the buffer
                        String writeMessage = new String(writeBuf);
                        //mConversationArrayAdapter.add("Me:  " + writeMessage);
                        break;
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        decodeMessages(readMessage);
                        //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                        break;
                    case MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_TOAST:
                        Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode)
        {
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK)
                {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                }
                else
                {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
            case SAVE_SETUP:
                // Save the calibration data to the device's EEPROM.
                String message = new String();
                message = data.getStringExtra(EXTRA_MESSAGE);
                // Transform the received packet into one ready to be sent
                message = message.replace("#A","@C");
                message = message.replace("?","!");
                sendMessageRc(message);
        }
    }

    private void connectDevice(Intent data, boolean secure)
    {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    public void OnConnectButtonClick(View view)
    {
        Intent serverIntent = null;
        // Launch the DeviceListActivity to see devices and do scan
        serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }

    public void OnSetupButtonClick(View view)
    {
        // Launch the DeviceListActivity to see devices and do scan
        Intent serverIntent = new Intent(this, setup.class);
        serverIntent.putExtra(EXTRA_MESSAGE, mCalibrationData);
        startActivityForResult(serverIntent, SAVE_SETUP);
    }

    public void OnSetReverserForward(View view)
    {
        // Send an update to the reverser servos
        String message = new String();
        message = "@C0102";
        message = message.concat(int2String(mCalibReverse1Low));
        message = message.concat(int2String(mCalibReverse2Low));
        message = message.concat("!");
        sendMessageRc(message);
    }

    public void OnSetReverserStop(View view)
    {
        // Send an update to the reverser servos
        String message = new String();
        message = "@C0102";
        message = message.concat(int2String(mCalibReverse1Mid));
        message = message.concat(int2String(mCalibReverse2Mid));
        message = message.concat("!");
        sendMessageRc(message);
    }

    public void OnSetReverserReverse(View view)
    {
        // Send an update to the reverser servos
        String message = new String();
        message = "@C0102";
        message = message.concat(int2String(mCalibReverse1High));
        message = message.concat(int2String(mCalibReverse2High));
        message = message.concat("!");
        sendMessageRc(message);
    }

    public void OnDrainCocks(View view)
    {
        String message = new String();
        message = "@C0302";
        boolean on = ((ToggleButton) view).isChecked();
        if (on)
        {
            message = message.concat(int2String(mCalibDrain1Open));
            message = message.concat(int2String(mCalibDrain2Open));
        }
        else
        {
            message = message.concat(int2String(mCalibDrain1Closed));
            message = message.concat(int2String(mCalibDrain2Closed));
        }
        message = message.concat("!");
        sendMessageRc(message);
    }
}
