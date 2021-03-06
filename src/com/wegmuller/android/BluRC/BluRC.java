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

package com.wegmuller.android.BluRC;

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

import java.util.LinkedList;

import static android.widget.SeekBar.OnSeekBarChangeListener;
import static java.lang.Thread.sleep;

/**
 * This is the main Activity that displays the current chat session.
 */
public class BluRC extends Activity
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
    public static final int  FEATURES_THROTTLE   =   0x01;
    public static final int  FEATURES_REVERSE1   =   0x02;
    public static final int  FEATURES_REVERSE2   =   0x04;
    public static final int  FEATURES_DRAIN1     =   0x08;
    public static final int  FEATURES_DRAIN2     =   0x10;
    public static final int  FEATURES_WHISTLE    =   0x20;

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
    public static final int  REG_SAFE_THROTTLE_HI =  6;
    public static final int  REG_SAFE_THROTTLE_LO =  7;
    public static final int  REG_SAFE_REVERSE1_HI =  8;
    public static final int  REG_SAFE_REVERSE1_LO =  9;
    public static final int  REG_SAFE_REVERSE2_HI =  10;
    public static final int  REG_SAFE_REVERSE2_LO =  11;
    public static final int  REG_SAFE_DRAIN1_HI   =  12;
    public static final int  REG_SAFE_DRAIN1_LO   =  13;
    public static final int  REG_SAFE_DRAIN2_HI   =  14;
    public static final int  REG_SAFE_DRAIN2_LO   =  15;
    public static final int  REG_SAFE_WHISTLE_HI  =  16;
    public static final int  REG_SAFE_WHISTLE_LO  =  17;
    public static final int  REG_DESC_SIZE_HI     =  60;
    public static final int  REG_DESC_SIZE_LO     =  61;
    public static final int  REG_DESCRIPTION_HI   =  62;
    public static final int  REG_DESCRIPTION_LO   =  63;

    // Message sent to the setup activity
    public final static String EXTRA_MESSAGE = "com.example.android.BluRC.MESSAGE";

    // Debugging
    private static final String TAG = "BluRC";
    private static final boolean D = false;

    // Message types sent from the BTService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BTService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int SAVE_SETUP = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private SeekBar mThrottleBar;
    private SeekBar mWhistleBar;
    private Button mSetupButton;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;

    // Remote device address
    private String mRemoteAddress = new String("");

    // Number of retries before giving up on connection
    private int mConnectRetries = 10;

    // Member object for the chat services
    private static BTService mBTservice = null;

    // Calibration data stored inside the receiver's EEPROM.
    private static String mCalibrationData = new String("");

    // Calibration values
    public static int mCalibValues[] = new int[16];
    public static final int CALIB_THROTTLE_LOW = 0;
    public static final int CALIB_THROTTLE_MID = 1;
    public static final int CALIB_THROTTLE_HI = 2;
    public static final int CALIB_REVERSE1_LOW = 3;
    public static final int CALIB_REVERSE1_MID = 4;
    public static final int CALIB_REVERSE1_HI = 5;
    public static final int CALIB_REVERSE2_LOW = 6;
    public static final int CALIB_REVERSE2_MID = 7;
    public static final int CALIB_REVERSE2_HI = 8;
    public static final int CALIB_DRAIN1_LOW = 9;
    public static final int CALIB_DRAIN1_HI = 10;
    public static final int CALIB_DRAIN2_LOW = 11;
    public static final int CALIB_DRAIN2_HI = 12;
    public static final int CALIB_WHISTLE_LOW = 13;
    public static final int CALIB_WHISTLE_HI = 14;

    // Other EEPROM registers
    public int mEEPROMValid = 0x42;
    public static int mFeatures = 0x00;
    public static int mTimeout = 0x00; // No timeout for now, while debugging

    // Safe Values
    public static int mSafeThrottle;
    public static int mSafeReverse1;
    public static int mSafeReverse2;
    public static int mSafeDrain1;
    public static int mSafeDrain2;
    public static int mSafeWhistle;
    private String mSafeData = new String();

    private static boolean mWarnOnNoConnection = true;
    private static boolean mWaitingForAck = false;

    // Locomotive name
    public static String mLocomotiveName = new String();
    public int mNameLength;

    private void default_calibration()
    {
        mCalibValues[0] = 70;
        mCalibValues[1] = 90;
        mCalibValues[2] = 110;
        mCalibValues[3] = 70;
        mCalibValues[4] = 90;
        mCalibValues[5] = 110;
        mCalibValues[6] = 70;
        mCalibValues[7] = 90;
        mCalibValues[8] = 110;
        mCalibValues[9] = 70;
        mCalibValues[10] = 110;
        mCalibValues[11] = 70;
        mCalibValues[12] = 110;
        mCalibValues[13] = 70;
        mCalibValues[14] = 110;
    }

    // Button availability flags
    private static boolean setupButtonActive = false;
    private boolean connectButtonActive = true;

    // Message queue
    private static LinkedList<String> mTxQueue = new LinkedList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(D) Log.e(TAG, "+++ ON CREATE +++");

        // Set up the window layout
        setContentView(R.layout.main);

        // Initial values for calibration
        default_calibration();

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null)
        {
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
                progress = Calibrate(progress, mCalibValues[CALIB_THROTTLE_LOW], mCalibValues[CALIB_THROTTLE_MID], mCalibValues[CALIB_THROTTLE_HI]);
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
        mWhistleBar = (SeekBar) findViewById(R.id.seekBar_whistle);

        mWhistleBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
        {
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser)
            {
                // Build a packet based on the new whistle value.
                String message = new String();
                message = "@C0501";
                progress = Calibrate(progress, mCalibValues[CALIB_WHISTLE_LOW], mCalibValues[CALIB_WHISTLE_HI]);
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
        // setupLink() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else
        {
            if (mBTservice == null)
            {
                setupLink();
            }
        }
    }

    @Override
    public synchronized void onResume()
    {
        mSetupButton = (Button)findViewById(R.id.button_setup);
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");
        mWaitingForAck = false;
    }

    private void setupLink()
    {
        if (D) Log.d(TAG, "setupLink()");

        // Initialize the seek bar variables
        mThrottleBar = (SeekBar) findViewById(R.id.seekBar_Throttle);
        mWhistleBar = (SeekBar) findViewById(R.id.seekBar_whistle);
        // Initialize the BTService to perform bluetooth connections
        mBTservice = new BTService(this, mHandler);

    }

    @Override
    public synchronized void onPause()
    {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
        setSafe();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if(D) Log.e(TAG, "-- ON STOP --");
        if(mBTservice != null)
        {
            mBTservice.stop();
        }
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (mBTservice != null)
        {
            mBTservice.stop();
        }
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

    public static int string2Value(String str, int offset)
    {
        char buff[] = new char[3];

        str.getChars(offset, offset + 2, buff, 0);
        return (ascii2Digit(buff[0]) << 4) + (ascii2Digit(buff[1]));
    }

    public static int calib2Value(int register)
    {
        char buff[] = new char[3];

        mCalibrationData.getChars(register, register + 2, buff, 0);
        return (ascii2Digit(buff[0]) << 4) + (ascii2Digit(buff[1]));
    }

    public int safe2Value(int register)
    {
        char buff[] = new char[3];

        mSafeData.getChars(register, register + 2, buff, 0);
        return (ascii2Digit(buff[0]) << 4) + (ascii2Digit(buff[1]));
    }

    public static String int2String(int value)
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
        return doCalibration(value, lowLimit, highLimit, 255);
    }

    // Calibration for axes with a center value in addition to the lower and upper limits
    private int Calibrate(int value, int lowLimit, int midLimit, int highLimit)
    {
        // Check which side of the center is the value on
        if(value == 127)
        {
            return midLimit;
        }
        if(value < 127)
        {
            // We need to produce a value between lowLimit and midLimit
            return doCalibration(value, lowLimit, midLimit, 127);
        }
        else
        {
            // We need to produce a value between midLimit and highLimit
            return doCalibration(value - 127, midLimit, highLimit, 127);
        }
    }

    /**
     * Sends a message.
     * @param message  A string of text to send.
     */
    public static void sendMessageRc(String message)
    {
        if (D) Log.i(TAG, "message Tx: " + message);

        // Check that we're actually connected before trying anything
        if ((mBTservice.getState() != BTService.STATE_CONNECTED) && mWarnOnNoConnection)
        {
            //Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            mWarnOnNoConnection = false;
            return;
        }

        // we can only send if there is not another transaction in progress
        if(false == mWaitingForAck)
        {
            if (D) Log.i(TAG, "message Tx.");
            // Check that there's actually something to send
            if (message.length() > 0)
            {
                // Put the current message in the queue
                mTxQueue.add(message);
                // Get the oldest message from the queue
                message = mTxQueue.remove();
                if (!message.contentEquals("$$$"))
                {
                    message = message.concat("\r");
                }
                // Get the message bytes and tell the BTService to write
                byte[] send = message.getBytes();
                mBTservice.write(send);
                mWaitingForAck = true;
            }
            else
            {
                // Just emptying the queue
                if(mTxQueue.size() > 0)
                {
                    message = mTxQueue.remove();
                    if (!message.contentEquals("$$$"))
                    {
                        message = message.concat("\r");
                    }
                    // Get the message bytes and tell the BTService to write
                    byte[] send = message.getBytes();
                    mBTservice.write(send);
                    mWaitingForAck = true;
                }
            }
        }
        else
        {
            if (D) Log.i(TAG, "message Queue.");
            // We can't send right now: put the current message in the queue
            // If the message we want to send is a servo value, check if other values for the same channel are
            // already in the queue: if so replace them with the new value
            String Reg = message.substring(1, 6);
            if(Reg.contains("C00") || Reg.contains("C01") || Reg.contains("C02") || Reg.contains("C03") || Reg.contains("C04") || Reg.contains("C05"))
            {
                for(int i = 0; i < mTxQueue.size(); i++)
                {
                    String element = mTxQueue.get(i);
                    if (element.contains(Reg))
                    {
                       // Replace the existing message in the queue by the up to date value
                       mTxQueue.set(i,message);
                       return;
                    }
                }
                // This message does not exist in the queue: add it
            }
            else
            {
                mTxQueue.add(message);
            }
        }
    }


    private void buildCalibrationString()
    {
        String message = new String();
        message = "@C0611";
        message = message.concat(int2String(0x42));
        message = message.concat(int2String(mFeatures));
        int loopCt;
        for(loopCt = 0; loopCt < 15; loopCt++)
        {
            message = message.concat(int2String(mCalibValues[loopCt]));
        }
        message = message.concat(int2String(mTimeout));
        message = message.concat("!");
        mCalibrationData = message;
    }

    private void buildSafeString()
    {
        String message = new String();
        message = "@C1B06";
        message = message.concat(int2String(mSafeThrottle));
        message = message.concat(int2String(mSafeReverse1));
        message = message.concat(int2String(mSafeReverse2));
        message = message.concat(int2String(mSafeDrain1));
        message = message.concat(int2String(mSafeDrain2));
        message = message.concat(int2String(mSafeWhistle));
        message = message.concat("!");
        mSafeData = message;
    }

    private void setSafe()
    {
        buildSafeString();
        String setMessage = mSafeData;
        setMessage = setMessage.replace("@C1B", "@C00");
        sendMessageRc(setMessage);
    }

    private String buildNameString()
    {
        String message = new String();
        message = "@C21";
        if(0 == mLocomotiveName.length())
        {
            // A blank name confuses the receiver firmware
            message = message.concat("0201W");
        }
        else
        {
            if(mLocomotiveName.length() < 16)
            {
                // The packet data length is the string length plus one
                message = message.concat(int2String(mLocomotiveName.length() + 1));
                // The first register sent contains the string length
                message = message.concat(int2String(mLocomotiveName.length()));
                // The rest of the data is the string
                message = message.concat(mLocomotiveName);
            }
            else
            {
                message = message.concat("1110");
                message = message.concat(mLocomotiveName.substring(0, 16));
            }
        }
        message = message.concat("!");
        return message;
    }

    private void updateSafeValues()
    {
        mSafeThrottle = safe2Value(REG_SAFE_THROTTLE_HI);
        mSafeReverse1 = safe2Value(REG_SAFE_REVERSE1_HI);
        mSafeReverse2 = safe2Value(REG_SAFE_REVERSE2_HI);
        mSafeDrain1 = safe2Value(REG_SAFE_DRAIN1_HI);
        mSafeDrain2 = safe2Value(REG_SAFE_DRAIN2_HI);
        mSafeWhistle = safe2Value(REG_SAFE_WHISTLE_HI);
    }

    // Decodes messages coming from the locomotive.
    private void decodeMessages(String message)
    {
        mWaitingForAck = false;
        sendMessageRc(""); // call with an empty message to send any old message that might still be in the queue

//        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
//        toast.show();
        if (message.contains("#A0612"))
        {
            // This message contains the calibration data
            mCalibrationData = message;
            if (0x42 == calib2Value(REG_EEPROM_VALID_HI))
            {
                if (D) Log.i(TAG, "EEPROM valid ");
                // This is valid calibration data: use it!
                mEEPROMValid = 0x42;
                int loopCt;
                for(loopCt = 0; loopCt < 15; loopCt++)
                {
                    mCalibValues[loopCt] = calib2Value((loopCt * 2) + 10);
                }
            }
            else
            {
                if (D) Log.i(TAG, "EEPROM invalid ");
                // Invalid EEPROM data: use safe defaults.
                mEEPROMValid = 0;
                default_calibration();
                buildCalibrationString();
            }

        }

        if (message.contains("#N"))
        {
            // Let the user know we received a negative ack. Something is up!
            message = message.concat(" error! ");
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG);
            toast.show();
        }

        if(message.contains("#A1B06"))
        {
            // This message contains the safe values
            // only trust them if the EEPROM is valid
            if(0x42 == mEEPROMValid)
            {
                mSafeData = message;
                updateSafeValues();
            }
            else
            {
                // Use defaults
                mSafeThrottle = mCalibValues[CALIB_THROTTLE_LOW];
                mSafeReverse1 = mCalibValues[CALIB_REVERSE1_MID];
                mSafeReverse2 = mCalibValues[CALIB_REVERSE2_MID];
                mSafeDrain1 = mCalibValues[CALIB_DRAIN1_HI];
                mSafeDrain2 = mCalibValues[CALIB_DRAIN2_HI];
                mSafeWhistle = mCalibValues[CALIB_WHISTLE_LOW];
            }
            buildSafeString();
        }

        if(message.contains("#A21"))
        {
            if(0x42 == mEEPROMValid)
            {
            // This message contains the name string
                mLocomotiveName = message.substring(8, 8 + string2Value(message, 6));
            }
            else
            {
                mLocomotiveName = "Name";
            }
            // Now we allow the user to go into the setup
            setupButtonActive = true;
            if (D) Log.i(TAG, "Setup button enabled ");
        }
    }

    // The Handler that gets information back from the BTService
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
                            case BTService.STATE_CONNECTED:
                                // Get ready to warn the user once if the connection goes away.
                                mWarnOnNoConnection = true;
                                // Query the receiver for its calibration data
                                sendMessageRc("@Q0612!");
                                // Query the receiver for the safe values
                                sendMessageRc("@Q1B06!");
                                // Query the receiver for its name
                                sendMessageRc("@Q210F!");
                                break;
                            case BTService.STATE_LOST:
                                setupButtonActive = false;
                                connectButtonActive = true;
                                mThrottleBar.setProgress(0);
                                mWhistleBar.setProgress(0);
                                mEEPROMValid = 0x00;
                                break;
                            case BTService.STATE_CONNECTING:
                            case BTService.STATE_NONE:
                            default:
                                break;
                            case BTService.STATE_RETRY:
                                if(0 != mConnectRetries)
                                {
                                    mConnectRetries--;
                                    mBluetoothAdapter.disable();
                                    mBluetoothAdapter.enable();
                                    connectDevice();
                                }
                                break;
                        }
                        break;
                    case MESSAGE_WRITE:
                        if (D) Log.e(TAG, "MESSAGE_WRITE");
                        break;
                    case MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        // construct a string from the valid bytes in the buffer
                        String readMessage = new String(readBuf, 0, msg.arg1);
                        if (D) Log.i(TAG, "MESSAGE_READ: " + readMessage);
                        decodeMessages(readMessage);
                        //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                        break;
                    case MESSAGE_DEVICE_NAME:
                        // save the connected device's name
                        if (D) Log.e(TAG, "MESSAGE_DEVICE_NAME");
                        mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        break;
                    case MESSAGE_TOAST:
                        if (D) Log.e(TAG, "MESSAGE_TOAST");
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
                    mRemoteAddress =  data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    connectDevice();
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK)
                {
                    // Bluetooth is now enabled, so set up a chat session
                    setupLink();
                }
                else
                {
                    // User did not enable Bluetooth or an error occurred
                    if (D) Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
            case SAVE_SETUP:
                // Save the calibration data to the device's EEPROM.
                buildCalibrationString();
                sendMessageRc(mCalibrationData);
                buildSafeString();
                sendMessageRc(mSafeData);
                sendMessageRc(buildNameString());
                mEEPROMValid = 0x42;
        }
    }

    private void connectDevice()
    {
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(mRemoteAddress);
        // Attempt to connect to the device
        mBTservice.connect(device);
    }

    public void OnConnectButtonClick(View view)
    {
        mConnectRetries = 3;
        Intent serverIntent = null;
        // Launch the DeviceListActivity to see devices and do scan
        serverIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
    }

    public void OnSetupButtonClick(View view)
    {
        if (D) Log.i(TAG, "Setup clicked");
        if(true == setupButtonActive)
        {
            // Make sure the calibration string is valid
            buildCalibrationString();
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, setup.class);
            serverIntent.putExtra(EXTRA_MESSAGE, mCalibrationData);
            startActivityForResult(serverIntent, SAVE_SETUP);
            if (D) Log.i(TAG, "Setup launched");
        }
    }

    public void OnSetReverserForward(View view)
    {
        // Send an update to the reverser servos
        String message = new String();
        message = "@C0102";
        message = message.concat(int2String(mCalibValues[CALIB_REVERSE1_HI]));
        message = message.concat(int2String(mCalibValues[CALIB_REVERSE2_HI]));
        message = message.concat("!");
        sendMessageRc(message);
    }

    public void OnSetReverserStop(View view)
    {
        // Send an update to the reverser servos
        String message = new String();
        message = "@C0102";
        message = message.concat(int2String(mCalibValues[CALIB_REVERSE1_MID]));
        message = message.concat(int2String(mCalibValues[CALIB_REVERSE2_MID]));
        message = message.concat("!");
        sendMessageRc(message);
    }

    public void OnSetReverserReverse(View view)
    {
        // Send an update to the reverser servos
        String message = new String();
        message = "@C0102";
        message = message.concat(int2String(mCalibValues[CALIB_REVERSE1_LOW]));
        message = message.concat(int2String(mCalibValues[CALIB_REVERSE2_LOW]));
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
            message = message.concat(int2String(mCalibValues[CALIB_DRAIN1_HI]));
            message = message.concat(int2String(mCalibValues[CALIB_DRAIN2_HI]));
        }
        else
        {
            message = message.concat(int2String(mCalibValues[CALIB_DRAIN1_LOW]));
            message = message.concat(int2String(mCalibValues[CALIB_DRAIN2_LOW]));
        }
        message = message.concat("!");
        sendMessageRc(message);
    }
}
