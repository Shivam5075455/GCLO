package com.terminal.gclo.BTClien;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.util.UUID;

public class BluetoothConnection {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @SuppressLint("MissingPermission")
    public static void connect(String macAddress) throws IOException {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice remoteDevice = bluetoothAdapter.getRemoteDevice(macAddress);
        try (BluetoothSocket bluetoothSocket = remoteDevice.createRfcommSocketToServiceRecord(MY_UUID)) {

            try {
                bluetoothSocket.connect();
            } catch (IOException e) {
                throw new IOException("Could not connect to device", e);
            }
        }

        // Use the BluetoothSocket to read and write data
    }
}
