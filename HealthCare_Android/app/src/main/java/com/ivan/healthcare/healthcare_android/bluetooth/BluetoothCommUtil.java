package com.ivan.healthcare.healthcare_android.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.ivan.healthcare.healthcare_android.Configurations;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 蓝牙通信类
 * 实现连接指定设备以及蓝牙通信
 * Created by Ivan on 16/1/22.
 */
public class BluetoothCommUtil {

    public final static int DISCONNECT = 0x30;
    public final static int CONNCTED = 0x31;
    public final static int CONNECT_LOST = 0x32;
    public final static int CONNECT_FAIL = 0x33;

    public final static String CONNECT_STATE = "state";
    public final static String BLUETOOTH_DATA = "data";
    public final static String BLUETOOTH_DATA_LENGTH = "length";
    /**
     * 标识由mHandler发送的数据是蓝牙连接状态(false)还是蓝牙接收的数据(true)
     */
    public final static String DATAorSTATE = "dataorstate";

    private int connectionState = DISCONNECT;

    private BluetoothAdapter btAdapter;
    private BluetoothSocket connectSocket;

    private AcceptThread acceptThread;
    private ConnectingThread connectingThread;
    private TrasfferThread trasfferThread;

    private Handler mHandler;

    private boolean listening = false;

    public BluetoothCommUtil(BluetoothAdapter adapter, Handler handler) {
        btAdapter = adapter;
        mHandler = handler;
    }

    /**
     * 获取蓝牙链接状态
     * {@link #DISCONNECT}
     * {@link #CONNCTED}
     * {@link #CONNECT_LOST}
     * {@link #CONNECT_FAIL}
     * @return 蓝牙状态常量
     */
    public int getConnectionState() {
        return connectionState;
    }

    private void setConnctionState(int state) {
        connectionState = state;
        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putBoolean(DATAorSTATE, false);
        bundle.putInt(CONNECT_STATE, state);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    /**
     * 开始监听蓝牙连接请求
     */
    public synchronized void start() {
        listening = false;
        setConnctionState(DISCONNECT);
        if (connectingThread != null) {
            connectingThread.cancel();
            connectingThread = null;
        }
        if (trasfferThread != null) {
            trasfferThread.cancel();
            trasfferThread = null;
        }
        if (acceptThread == null) {
            acceptThread = new AcceptThread();
            acceptThread.start();
        }
    }

    /**
     * 连接到蓝牙设备
     */
    public synchronized boolean connect(BluetoothDevice device) {
        listening = false;
        if (connectingThread != null) {
            connectingThread.cancel();
            connectingThread = null;
        }
        if (connectSocket != null) {
            try {
                connectSocket.close();
                connectSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        connectingThread = new ConnectingThread(device);
        connectingThread.start();
        return true;
    }

    /**
     * 与蓝牙设备通信
     */
    private synchronized void trasffer() {
        if (trasfferThread != null) {
            trasfferThread.cancel();
            trasfferThread = null;
        }
        trasfferThread = new TrasfferThread();
        trasfferThread.start();
    }

    /**
     * 监听蓝牙连接请求的线程类
     */
    private class AcceptThread extends Thread {

        private BluetoothServerSocket serverSocket;

        public AcceptThread() {

        }

        @Override
        public void run() {
            super.run();

            try {
                serverSocket = btAdapter.listenUsingRfcommWithServiceRecord(Configurations.btServerName, Configurations.btUUID);
                connectSocket = serverSocket.accept();
                trasffer();
            } catch (IOException e) {
                e.printStackTrace();
                setConnctionState(CONNECT_FAIL);
            }
        }

        public void cancel() {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 连接到蓝牙设备的线程类
     */
    private class ConnectingThread extends Thread {

        private BluetoothDevice mDevice;

        public ConnectingThread(BluetoothDevice device) {
            mDevice = device;
        }

        @Override
        public void run() {
            super.run();
            try {
                BluetoothSocket clientSocket = mDevice.createRfcommSocketToServiceRecord(Configurations.btUUID);
                clientSocket.connect();
                BluetoothCommUtil.this.connectSocket = clientSocket;
                setConnctionState(CONNCTED);
                trasffer();
            } catch (IOException e) {
                e.printStackTrace();
                connectionFailed();
            }
        }

        public void cancel() {
            if (connectSocket != null) {
                try {
                    connectSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 蓝牙传输的线程类
     */
    private class TrasfferThread extends Thread {

        private InputStream inputStream;
        private OutputStream outputStream;

        public TrasfferThread() {
            connectingThread = null;
            if (acceptThread != null) {
                acceptThread.cancel();
                acceptThread = null;
            }
            try {
                inputStream = connectSocket.getInputStream();
                outputStream = connectSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                inputStream = null;
                outputStream = null;
                connectionLost();
            }
        }

        @Override
        public void run() {
            super.run();

            listening = true;
            int size = 1024;
            byte[] buffer = new byte[size];
            int length;
            while (listening) {
                try {
                    length = inputStream.read(buffer);
                    if (length != -1) {
                        readMessages(buffer, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    listening = false;
                    connectionLost();
                }
            }
        }

        public boolean write(byte[] msg) {
            try {
                outputStream.write(msg);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                connectionLost();
                return false;
            }
        }

        public void cancel() {
            if (connectSocket != null) {
                try {
                    connectSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void readMessages(byte[] buffer, int length) {
        Message msg = mHandler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putBoolean(DATAorSTATE, false);
        bundle.putByteArray(BLUETOOTH_DATA, buffer);
        bundle.putInt(BLUETOOTH_DATA_LENGTH, length);
        msg.setData(bundle);
        msg.sendToTarget();
    }

    /**
     * 向对方蓝牙设备写入数据
     * @param msg 数据的字节流
     * @return 是否写入成功
     */
    public boolean write(byte[] msg) {
        if (trasfferThread != null) {
            return trasfferThread.write(msg);
        } else {
            return false;
        }
    }

    private void connectionFailed() {
        setConnctionState(CONNECT_FAIL);
        if (connectSocket != null) {
            try {
                connectSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectSocket = null;
        }
    }

    private void connectionLost() {
        setConnctionState(CONNECT_LOST);
        if (connectSocket != null) {
            try {
                connectSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            connectSocket = null;
        }
    }

    /**
     * 关闭连接
     */
    public void stop() {
        try {
            if (connectSocket != null) {
                connectSocket.close();
                connectSocket = null;
            }
            acceptThread = null;
            connectingThread = null;
            trasfferThread = null;
            setConnctionState(DISCONNECT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
