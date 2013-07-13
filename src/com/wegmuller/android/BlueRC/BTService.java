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

package com.wegmuller.android.BlueRC;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread  for connecting with a device and
 * performing data transmissions when connected.
 */
public class BTService
{
    // Debugging
    private static final String TAG = "BlueRC";
    private static final boolean D = false;

    // Unique UUID for this application
    private static final UUID MY_UUID_INSECURE =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // Member fields
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private WorkerThread mConnectThread;
    private int mState;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 2;  // now connected to a remote device
    public static final int STATE_LOST = 3;       // The connection was dropped
    public static final int STATE_ERROR = 4;       // The connection was dropped
    public static final int STATE_RETRY = 5;       // The connection was dropped


    /**
     * Constructor. Prepares a new BlueRC session.
     * @param context  The UI Activity Context
     * @param handler  A Handler to send messages back to the UI Activity
     */
    public BTService(Context context, Handler handler)
    {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
    }

    /**
     * Set the current state of the chat connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(int state)
    {
        if (D)
        {
            Log.d(TAG, "setState() " + mState + " -> " + state);
        }
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(BlueRC.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state. */
    public synchronized int getState()
    {
        return mState;
    }



    /**
     * Start the WorkerThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device)
    {
        if (D) Log.d(TAG, "connect to: " + device);

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new WorkerThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Stop the worker thread
     */
    public synchronized void stop()
    {
        if (D) Log.d(TAG, "stop");

        if (mConnectThread != null)
        {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        mState = STATE_NONE;
    }

    /**
     * Write to the ConnectThread in an unsynchronized manner
     * @param out The bytes to write
     * @see WorkerThread#write(byte[])
     */
    public void write(byte[] out)
    {
        // Create temporary object
        WorkerThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this)
        {
            if(mState != STATE_CONNECTED)
            {
                return;
            }
            r = mConnectThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed()
    {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BlueRC.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BlueRC.TOAST, "Can't connect");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_RETRY);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost()
    {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(BlueRC.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BlueRC.TOAST, "Connection lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(STATE_LOST);
    }




    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class WorkerThread extends Thread
    {
        private BluetoothSocket mmSocket = null;
        private final BluetoothDevice mmDevice;
        private String mRxPacket = new String("");
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private boolean running = true;

        public WorkerThread(BluetoothDevice device)
        {
            mmDevice = device;
            mState = STATE_CONNECTING;
            BluetoothSocket tmp = null;
            running = true;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try
            {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            }
            catch (IOException e)
            {
                Log.e(TAG, "Socket create() failed", e);
                setState(STATE_ERROR);
                return;
            }
            mmSocket = tmp;
        }

        public void run()
        {

            if (D) Log.i(TAG, "BEGIN mConnectThread ");
            setName("WorkerThread");

            // Always cancel discovery because it will slow down a connection
            if(mAdapter.isDiscovering())
            {
                mAdapter.cancelDiscovery();
            }

            // Make a connection to the BluetoothSocket
            try
            {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            }
            catch (IOException e)
            {
                if (D) Log.e(TAG, "Exception while doing a .connect: ", e);
                // Close the socket
                try
                {
                    mmSocket.close();
                }
                catch (IOException e2)
                {
                    Log.e(TAG, "unable to close() socket during connection failure", e2);
                }
                connectionFailed();
                return;
            }

            // Send the name of the connected device back to the UI Activity
            Message msg = mHandler.obtainMessage(BlueRC.MESSAGE_DEVICE_NAME);
            Bundle bundle = new Bundle();
            bundle.putString(BlueRC.DEVICE_NAME, mmDevice.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);


            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try
            {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            }
            catch (IOException e)
            {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            byte[] buffer = new byte[1024];
            int bytes;
            setState(STATE_CONNECTED);

            // Keep listening to the InputStream while connected
            while (running)
            {
                try
                {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    if(0 == mRxPacket.length())
                    {
                        if('#' == buffer[0])
                        {
                            mRxPacket = "#";
                            int i;
                            for(i = 1; i < bytes; i++)
                            {
                                mRxPacket += (char)buffer[i];
                            }
                        }
                    }
                    else
                    {
                        int i;
                        for(i = 0; i < bytes; i++)
                        {
                            mRxPacket += (char)buffer[i];
                        }
                    }
                    if(mRxPacket.contains("?"))
                    {
                        // We have a complete packet: send it to the UI
                        mHandler.obtainMessage(BlueRC.MESSAGE_READ, mRxPacket.length(), -1, mRxPacket.getBytes()).sendToTarget();
                        mRxPacket = "";
                    }
                }
                catch (IOException e)
                {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "close() of connect socket failed", e);
            }

        }

        public void cancel()
        {
            running = false;
            try
            {
                mmSocket.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }

        public void write(byte[] buffer)
        {
            try
            {
                mRxPacket = ""; // Empty the rx packet buffer, to clear any left overs from a past failed reception.
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(BlueRC.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            }
            catch (IOException e)
            {
                Log.e(TAG, "Exception during write", e);
            }
        }
    }
}
