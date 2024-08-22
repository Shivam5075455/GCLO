package com.terminal.gclo.Models;

import android.bluetooth.BluetoothDevice;

public class BtListModel {
    String btAddress;
    String btName;
    private BluetoothDevice bluetoothDevice; // New field

    public BtListModel(String btName, String btAddress) {
        this.btName = btName;
        this.btAddress = btAddress;
    }

    public String getBtName() {
        return this.btName;
    }

    public void setBtName(String btName) {
        this.btName = btName;
    }

    public String getBtAddress() {
        return this.btAddress;
    }

    public void setBtAddress(String btAddress) {
        this.btAddress = btAddress;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }


}
