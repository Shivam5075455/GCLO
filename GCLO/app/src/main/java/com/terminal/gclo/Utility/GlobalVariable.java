package com.terminal.gclo.Utility;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.terminal.gclo.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GlobalVariable {

    public static BluetoothDevice bluetoothDevice;
    public static BluetoothSocket bluetoothSocket;
    public static BluetoothAdapter bluetoothAdapter;
    public static OutputStream outputStream;
    public static InputStream inputStream;
    public static final String SETTING_PREFS_NAME = "setting_prefs";
    public static final String AUTO_SCROLL_KEY = "autoScroll";



    public static void disconnectBluetoothDevice(Context context){
        try{
            if(bluetoothSocket != null){
                bluetoothSocket.close();
                Toast.makeText(context, "Device disconnected", Toast.LENGTH_SHORT).show();
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
