//package com.example.gclo.BTClien;
//
//import static android.icu.lang.UProperty.NAME;
//import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
//
//
//import android.bluetooth.BluetoothServerSocket;
//import android.bluetooth.BluetoothSocket;
//import android.util.Log;
//
//import java.io.IOException;
//import java.util.UUID;
//
//
//public class AcceptThread extends Thread {
//UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
//    private final BluetoothServerSocket mmServerSocket;
//
//    public AcceptThread() {
//        // Use a temporary object that is later assigned to mmServerSocket
//        // because mmServerSocket is final.
//        BluetoothServerSocket tmp = null;
//        try {
//            // MY_UUID is the app's UUID string, also used by the client code.
//            tmp = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
//        } catch (IOException e) {
//            Log.e(TAG, "Socket's listen() method failed", e);
//        }
//        mmServerSocket = tmp;
//    }
//
//    public void run() {
//        BluetoothSocket socket = null;
//        // Keep listening until exception occurs or a socket is returned.
//        while (true) {
//            try {
//                socket = mmServerSocket.accept();
//            } catch (IOException e) {
//                Log.e(TAG, "Socket's accept() method failed", e);
//                break;
//            }
//
//            if (socket != null) {
//                // A connection was accepted. Perform work associated with
//                // the connection in a separate thread.
//                manageMyConnectedSocket(socket);
//                mmServerSocket.close();
//                break;
//            }
//        }
//    }
//
//    // Closes the connect socket and causes the thread to finish.
//    public void cancel() {
//        try {
//            mmServerSocket.close();
//        } catch (IOException e) {
//            Log.e(TAG, "Could not close the connect socket", e);
//        }
//    }
//}
