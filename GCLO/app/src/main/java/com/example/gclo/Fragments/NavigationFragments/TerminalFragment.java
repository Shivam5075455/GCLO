package com.example.gclo.Fragments.NavigationFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Adapters.TerminalAdapter.TerminalMessageAdapter;
import com.example.gclo.MainActivity;
import com.example.gclo.Models.TerminalMessageModel;
import com.example.gclo.Models.RetainedFragment;
import com.example.gclo.R;
import com.example.gclo.Utility.Constants;
import com.example.gclo.Utility.GlobalVariable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TerminalFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;

    //    BluetoothSocket bluetoothSocket;
//    BluetoothDevice bluetoothDevice;
    InputStream inputStream;
    OutputStream outputStream;
    private BluetoothAdapter bluetoothAdapter;// it provides the functionality of ON and OFF the Bluetooth
    Set<BluetoothDevice> BTPairedDevices;

    private static final String TAG_DEBUG = "DEBUG_MA";
    boolean bisBtConnected = false;
    UUID MY_UUID = UUID.fromString(Constants.uuid);


    List<TerminalMessageModel> messageModelList;
    List<TerminalMessageModel> messageModelListReceiver;

    RecyclerView rvTerminal;
    Spinner spinnerBTPairedDevices;
    private EditText etTerminalWriteMessage;
    private ImageView imgTerminalSend;
    private Button btnConnect, btnM1, btnM2, btnM3, btnM4, btnM5;
    static final String TAG = "RetainedFragment";
    private RetainedFragment retainedFragment;
    TerminalMessageAdapter terminalMessageAdapter;
//    TerminalMessageAdapter terminalMessageAdapterReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);


        imgTerminalSend = view.findViewById(R.id.imgTerminalSend);
        etTerminalWriteMessage = view.findViewById(R.id.etTerminalWriteMessage);
        rvTerminal = view.findViewById(R.id.rvTerminal);
//        spinnerBTPairedDevices = view.findViewById(R.id.spinnerBTPairedDevices);
//        btnConnect = view.findViewById(R.id.btnConnect);
        btnM1 = view.findViewById(R.id.btnM1);
        btnM2 = view.findViewById(R.id.btnM2);
        btnM3 = view.findViewById(R.id.btnM3);
        btnM4 = view.findViewById(R.id.btnM4);
        btnM5 = view.findViewById(R.id.btnM5);

        messageModelList = new ArrayList<>();
        messageModelListReceiver = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvTerminal.setLayoutManager(linearLayoutManager);
        terminalMessageAdapter = new TerminalMessageAdapter(messageModelList);
        rvTerminal.setAdapter(terminalMessageAdapter);


//        LinearLayoutManager linearLayoutManagerReceiver = new LinearLayoutManager(getContext());
//        linearLayoutManagerReceiver.setStackFromEnd(true);
//        rvTerminal.setLayoutManager(linearLayoutManagerReceiver);
//        terminalMessageAdapterReceiver = new TerminalMessageAdapter(messageModelListReceiver);
//        rvTerminal.setAdapter(terminalMessageAdapterReceiver);


        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();


// To save the messages after switching the fragment
        retainedFragment = getOrCreateRetainedFragment();
        List<TerminalMessageModel> retainedMessages = retainedFragment.getMessages();
        if (retainedMessages != null && !retainedMessages.isEmpty()) {
            messageModelList.addAll(retainedMessages);
            terminalMessageAdapter.notifyDataSetChanged();
            scrollToLast();
        }

        imgTerminalSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etTerminalWriteMessage.getText().toString();
                if (!message.isEmpty()) {
                    retainedFragment.addMessage(new TerminalMessageModel(message, TerminalMessageModel.sent_by_admin));
                    messageModelList.add(new TerminalMessageModel(message, TerminalMessageModel.sent_by_admin));
                    terminalMessageAdapter.notifyDataSetChanged();
                    scrollToLast();
                    sendMessage(message + "\n");
                    etTerminalWriteMessage.setText("");

                }
            }
        });


        // Handle back button press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // Handle back button press here
//                    Toast.makeText(getContext(), "Back button pressed", Toast.LENGTH_SHORT).show();
                    handleBackPressed();
                    return true; // Consumed the event
                }
                return false; // Let the system handle the event
            }
        });


        return view;
    }//onCreateView

    @Override
    public void onStart() {
        super.onStart();
//        getPairedDevices();
//        populateSpinnerWithPairedDevices();
    }

    private void handleBackPressed() {

        ((MainActivity) requireActivity()).showExitConfirmationDialog();
    }

    public void scrollToLast() {
        if (!messageModelList.isEmpty()) {
            rvTerminal.smoothScrollToPosition(messageModelList.size() - 1);
        }

    }

    public void scrollToLastReceiver() {
//        if (!messageModelListReceiver.isEmpty()) {
//            rvTerminal.smoothScrollToPosition(messageModelListReceiver.size() - 1);
//        }
    }

    private RetainedFragment getOrCreateRetainedFragment() {

        RetainedFragment fragment = (RetainedFragment) getFragmentManager().findFragmentByTag(RetainedFragment.TAG);

        if (fragment == null) {
            fragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(fragment, RetainedFragment.TAG).commit();
        }

        return fragment;
    }


    @SuppressLint("MissingPermission")
    public void sendMessage(String message) {
        try {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("BluetoothPref", Context.MODE_PRIVATE);
            String remoteDevice = sharedPreferences.getString("remoteDevice", null);
            String strBluetoothSocket = sharedPreferences.getString("bluetoothSocket", null);
            sharedPreferences.edit().apply();

            Log.d(TAG_DEBUG, "btCommunication remoteDevice: " + remoteDevice);
            Log.d(TAG_DEBUG, "btCommunication strBluetoothSocket: " + strBluetoothSocket);
            if (remoteDevice != null && strBluetoothSocket != null) {

                GlobalVariable.bluetoothDevice = bluetoothAdapter.getRemoteDevice(remoteDevice);
                Log.d(TAG_DEBUG, "bluetoothDevice: " + GlobalVariable.bluetoothDevice.getName());
            }
            outputStream = GlobalVariable.bluetoothSocket.getOutputStream();
            if (outputStream != null) {
                outputStream.write(message.getBytes());
            }
            inputStream = GlobalVariable.bluetoothSocket.getInputStream();
            if (inputStream != null) {
                receiveMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG_DEBUG, "btCommunication exception: " + e.getMessage());
        }
    }

    public void receiveMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                byte[] buffer = new byte[1024];
                int bytes;
                StringBuilder receivedMessage = new StringBuilder();
                while (true) {
                    try {
                        bytes = inputStream.read(buffer);

                        // Handle incoming message

                        while ((bytes = inputStream.read(buffer)) != -1) {
                            String data = new String(buffer, 0, bytes);
                            receivedMessage.append(data);
                            // Check for termination character or sequence
                            if (receivedMessage.toString().endsWith("\n")) {
                                String completeMessage = receivedMessage.toString().trim(); // Trim to remove whitespace characters

//                                String message = new String(buffer, 0, bytes);
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {

                                            messageModelList.add(new TerminalMessageModel(completeMessage, TerminalMessageModel.received_by_user));
                                            terminalMessageAdapter.notifyDataSetChanged();
                                            scrollToLastReceiver();
                                            Log.d(TAG_DEBUG, "Received message: " + completeMessage);
                                        }
                                    });
                                }
                                receivedMessage.setLength(0); // Clear the StringBuilder for the next message
                            }
                        }
                    } catch (IOException e) {
                        getActivity().runOnUiThread(() -> Log.e(TAG_DEBUG, "Error reading from input stream", e));
                    }
                }
            }
        }).start();
    }

}