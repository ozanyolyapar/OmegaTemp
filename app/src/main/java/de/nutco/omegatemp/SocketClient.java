package de.nutco.omegatemp;

import android.app.Activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by ozan on 13.12.2017.
 */

public class SocketClient {
    private Socket mSocket;
    private OutputStream outputStream;
    private BufferedReader bufferedReader;
    private Activity mActivity;

    private String mAddress;
    private int mPort;
    private Callback mCallback = null;

    public SocketClient(String address, int port, Activity activity) {
        mAddress = address;
        mPort = port;
        mActivity = activity;
    }

    public void connect() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mSocket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress(mAddress, mPort);
                try {
                    mSocket.connect(socketAddress);
                    outputStream = mSocket.getOutputStream();
                    bufferedReader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                    new ReceiveThread().start();

                    if (mCallback != null){
                        mCallback.onConnect(mSocket);
                    }
                } catch (IOException e) {
                    if (mCallback != null){
                        mCallback.onConnectError(mSocket, e.getMessage());
                    }
                }
            }
        });
    }

    public void disconnect() {
        try {
            mSocket.close();
        } catch (IOException e) {
            if (mCallback != null){
                mCallback.onDisconnect(mSocket, e.getMessage());
            }
        }
    }

    public void send(String message) {
        try {
            outputStream.write(message.getBytes());
        } catch (IOException e) {
            if (mCallback != null){
                mCallback.onDisconnect(mSocket, e.getMessage());
            }
        }
    }

    private class ReceiveThread extends Thread implements Runnable {
        public void run() {
            String message;
            try {
                while((message = bufferedReader.readLine()) != null) {
                    if (mCallback != null){
                        mCallback.onMessage(message);
                    }
                }
            } catch (IOException e) {
                if (mCallback != null){
                    mCallback.onDisconnect(mSocket, e.getMessage());
                }
            }
        }
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public void removeCallback() {
        mCallback = null;
    }

    public interface Callback {
        void onMessage(String message);
        void onConnect(Socket socket);
        void onDisconnect(Socket socket, String message);
        void onConnectError(Socket socket, String message);
    }
}
