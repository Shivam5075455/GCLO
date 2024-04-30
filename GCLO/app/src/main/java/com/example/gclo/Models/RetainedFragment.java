package com.example.gclo.Models;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.gclo.Adapters.ChatAdapter.ChatMessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class RetainedFragment extends Fragment {
    /*
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        public void addMessage(String message) {
            terminalContent.append(message).append("\n");
        }
    */
    public static final String TAG = "RetainedFragment";
    private StringBuilder terminalContent = new StringBuilder();
    private List<TerminalMessageModel> messages;

    private BluetoothSocket bluetoothSocket;

    public String getTerminalContent() {
        return terminalContent.toString();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    public List<TerminalMessageModel> getMessages() {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket){
        this.bluetoothSocket = bluetoothSocket;
    }
    public BluetoothSocket getBluetoothSocket(){
        return bluetoothSocket;
    }

    public void addMessage(TerminalMessageModel message) {
        getMessages().add(message);
    }

    // This method is called when the fragment is being destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Here, you can save any data if necessary
    }

    // This method is called when the fragment is being detached
    @Override
    public void onDetach() {
        super.onDetach();
        // Here, you can clean up any references
    }


}

