package com.example.gclo.Utility;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;

public class GlobalVariable {
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothSocket bluetoothSocket;



    public static void disconnectBluetoothDevice(){
        try{
            if(bluetoothSocket != null){
                bluetoothSocket.close();
            }
            if(bluetoothDevice!=null){
                bluetoothDevice=null;
            }
            Log.d("TAG", "Socket closed");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
