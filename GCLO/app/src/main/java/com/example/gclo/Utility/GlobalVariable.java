package com.example.gclo.Utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gclo.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GlobalVariable {
    public static BluetoothDevice bluetoothDevice;
    public static BluetoothSocket bluetoothSocket;
    public static BluetoothAdapter bluetoothAdapter;
    public static OutputStream outputStream;
    public static InputStream inputStream;



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
    }//disconnectBluetoothDevice

}
