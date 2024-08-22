package com.terminal.gclo.Fragments.NavigationFragments;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.terminal.gclo.Adapters.TerminalAdapter.TerminalMessageAdapter;
import com.terminal.gclo.MainActivity;
import com.terminal.gclo.Models.LocationUpdate;
import com.terminal.gclo.Models.TerminalMessageModel;
import com.terminal.gclo.Models.RetainedFragment;
import com.terminal.gclo.R;
import com.terminal.gclo.Utility.Constants;
import com.terminal.gclo.Utility.GlobalVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TerminalFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;

    //    BluetoothSocket bluetoothSocket;
//    BluetoothDevice bluetoothDevice;
//    InputStream inputStream;
//    OutputStream outputStream;
//    BluetoothAdapter bluetoothAdapter;// it provides the functionality of ON and OFF the Bluetooth
    Set<BluetoothDevice> BTPairedDevices;
//    private BluetoothViewModel bluetoothViewModel;

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
    public static final String ACTION_LOCATION_UPDATE = "com.example.gclo.ACTION_LOCATION_UPDATE";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);


        imgTerminalSend = view.findViewById(R.id.imgTerminalSend);
        etTerminalWriteMessage = view.findViewById(R.id.etTerminalWriteMessage);
        rvTerminal = view.findViewById(R.id.rvTerminal);
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


        GlobalVariable.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

//        get the BluetoothSocket from RetainedFragment
        retainedFragment = getOrCreateRetainedFragment();
//        bluetoothSocket = retainedFragment.getBluetoothSocket();
//        bluetoothViewModel = new ViewModelProvider(this).get(BluetoothViewModel.class);

// To save the messages after switching the fragment
//        retainedFragment = getOrCreateRetainedFragment();
        List<TerminalMessageModel> retainedMessages = retainedFragment.getMessages();
        if (retainedMessages != null && !retainedMessages.isEmpty()) {
            messageModelList.addAll(retainedMessages);
            terminalMessageAdapter.notifyDataSetChanged();
            scrollToLast();
        }


        try {
            if (GlobalVariable.bluetoothDevice != null && GlobalVariable.bluetoothSocket != null && GlobalVariable.bluetoothSocket.isConnected()) {
                GlobalVariable.inputStream = GlobalVariable.bluetoothSocket.getInputStream();
                if (GlobalVariable.inputStream != null) {
                    receiveMessage();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

// Enter credentials to register with hardware
//Name: mynameRon
//Username: myusernameRon1
//Email: myemailron1@gmail.com
//Phone Number: myphone123456


        imgTerminalSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etTerminalWriteMessage.getText().toString();
                if (!message.isEmpty()) {
                    retainedFragment.addMessage(new TerminalMessageModel(message, TerminalMessageModel.sent_by_admin, new Date().getTime()));
                    messageModelList.add(new TerminalMessageModel(message, TerminalMessageModel.sent_by_admin, new Date().getTime()));
                    terminalMessageAdapter.notifyDataSetChanged();
                    scrollToLast();
//                    if (outputStream != null) {
                    sendMessage(message + "\n");
//                    }
                    etTerminalWriteMessage.setText("");
                }
            }
        });

        btnM1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    registerCurrentUserWithHardware();

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        btnM2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageModelList.clear();
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

        if (GlobalVariable.bluetoothAdapter == null && GlobalVariable.bluetoothSocket == null) {
            assert false;
            if (!GlobalVariable.bluetoothSocket.isConnected()) {
                Toast.makeText(getContext(), "Bluetooth is not connected", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }//onCreateView

    @Override
    public void onStart() {
        super.onStart();
//        getPairedDevices();
//        populateSpinnerWithPairedDevices();
        if (GlobalVariable.bluetoothAdapter == null || !GlobalVariable.bluetoothAdapter.isEnabled()) {
            Log.d(TAG_DEBUG, "Bluetooth is not enabled.");
        }
    }

    private void handleBackPressed() {

        ((MainActivity) requireActivity()).showExitConfirmationDialog();
    }

    public void scrollToLast() {
        if (!messageModelList.isEmpty()) {
            rvTerminal.smoothScrollToPosition(messageModelList.size() - 1);
        }

    }

    private RetainedFragment getOrCreateRetainedFragment() {

        assert getFragmentManager() != null;
        RetainedFragment fragment = (RetainedFragment) getFragmentManager().findFragmentByTag(RetainedFragment.TAG);

        if (fragment == null) {
            fragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(fragment, RetainedFragment.TAG).commit();
        }

        return fragment;
    }


    @SuppressLint("MissingPermission")
    public void sendMessage(String message) {
        Log.d(TAG_DEBUG, "sendMessage() started");
        try {
//            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("BluetoothPref", Context.MODE_PRIVATE);
//            String remoteDevice = sharedPreferences.getString("remoteDevice", null);
//            String strBluetoothSocket = sharedPreferences.getString("bluetoothSocket", null);
//            sharedPreferences.edit().apply();
//            bluetoothDevice = bluetoothViewModel.getBluetoothDevice();
//            bluetoothSocket = bluetoothViewModel.getBluetoothSocket();

            Log.d(TAG_DEBUG, "btCommunication remoteDevice: " + GlobalVariable.bluetoothDevice);
            Log.d(TAG_DEBUG, "btCommunication bluetoothSocket: " + GlobalVariable.bluetoothSocket);
            if (GlobalVariable.bluetoothDevice != null && GlobalVariable.bluetoothSocket != null) {
//                GlobalVariable.bluetoothDevice = bluetoothAdapter.getRemoteDevice(remoteDevice);
//                bluetoothDevice = bluetoothAdapter.getRemoteDevice(remoteDevice);

//                bluetoothSocket = bluetoothAdapter.getRemoteDevice(strBluetoothSocket).createRfcommSocketToServiceRecord(MY_UUID);
                Log.d(TAG_DEBUG, "bluetoothDevice: " + GlobalVariable.bluetoothDevice.getName());

//                if (GlobalVariable.bluetoothSocket != null && GlobalVariable.bluetoothSocket.isConnected()) {
                if (GlobalVariable.bluetoothSocket != null && GlobalVariable.bluetoothSocket.isConnected()) {
                    Log.d(TAG_DEBUG, "Bluetooth socket is connected.");
//                outputStream = GlobalVariable.bluetoothSocket.getOutputStream();
                    GlobalVariable.outputStream = GlobalVariable.bluetoothSocket.getOutputStream();
                    if (GlobalVariable.outputStream != null) {
                        GlobalVariable.outputStream.write(message.getBytes());
                        GlobalVariable.outputStream.flush(); // Flush the stream to ensure data is sent immediately
                        Log.d(TAG_DEBUG, "Bluetooth message sent " + message);
                    } else {
                        Log.d(TAG_DEBUG, "Output stream is null.");
                    }
                } else {
                    Log.d(TAG_DEBUG, "Bluetooth socket is not connected.");

                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }//end of sendMessage

    public void receiveMessage() {
        Log.d(TAG_DEBUG, "receiveMessage() started");
        new Thread(new Runnable() {
            @Override
            public void run() {

                byte[] buffer = new byte[1024];
                int bytes;
                StringBuilder receivedMessage = new StringBuilder();
//                while (true) {
                try {
//                        bytes = GlobalVariable.inputStream.read(buffer);

                    // Handle incoming message
                    while ((bytes = GlobalVariable.inputStream.read(buffer)) != -1) {
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
                                        if (!completeMessage.startsWith("chat") && !completeMessage.isEmpty()) {
                                            String userId = "", latitude = "27.123456", longitude = "78.987654", distance = "100", zone = "in";
                                            if (completeMessage.startsWith("lat")) {
                                                latitude = completeMessage.substring(3);
                                            }
                                            if (completeMessage.startsWith("lon")) {
                                                longitude = completeMessage.substring(3);
                                            }
                                            if (completeMessage.startsWith("dist")) {
                                                distance = completeMessage.substring(4);
                                            }
                                            if (completeMessage.startsWith("zone")) {
                                                zone = completeMessage.substring(4);
                                            }
                                            if (completeMessage.startsWith("accountverified")) {
                                                Toast.makeText(getContext(), "You are verified", Toast.LENGTH_SHORT).show();
                                            }

                                            setLocationData(latitude, longitude, distance, zone);
                                            LocationUpdate locationUpdate = new LocationUpdate(userId, latitude, longitude, distance, zone);
                                            Intent intent = new Intent(ACTION_LOCATION_UPDATE);
                                            intent.putExtra("uderId",userId);
                                            intent.putExtra("latitude", latitude);
                                            intent.putExtra("longitude", longitude);
                                            intent.putExtra("distance", distance);
                                            intent.putExtra("zone", zone);
                                            LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
//                                            getContext().sendBroadcast(intent);
                                            Log.d(TAG, "Location data received: Latitude: $latitude, Longitude: $longitude, Distance: $distance, Zone: $zone");
                                            Log.d(TAG_DEBUG, "Latitude: " + latitude);
                                            retainedFragment.addMessage(new TerminalMessageModel(completeMessage, TerminalMessageModel.received_by_user, new Date().getTime()));
                                            Log.d(TAG, "retainded fragment receiver message");
                                            messageModelList.add(new TerminalMessageModel(completeMessage, TerminalMessageModel.received_by_user, new Date().getTime()));
                                        }
                                        terminalMessageAdapter.notifyDataSetChanged();
                                        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(GlobalVariable.SETTING_PREFS_NAME, Context.MODE_PRIVATE);
                                        boolean autoScroll = sharedPreferences.getBoolean(GlobalVariable.AUTO_SCROLL_KEY, false);
                                        if (autoScroll) {
                                            scrollToLast();
                                        }
                                        Log.d(TAG_DEBUG, "Received message: " + completeMessage);
                                    }
                                });
                            }
                            receivedMessage.setLength(0); // Clear the StringBuilder for the next message
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG_DEBUG, "Error reading from input stream", e);
                }
//                }
            }
        }).start();
    }//end of receiveMessage

    public void setLocationData(String lat, String lon, String distance, String zone) {
// set Loaction(Latitude and Longitude), distance and zone on local storage
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("LocationPreferences", getContext().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("latitude", lat);
        editor.putString("longitude", lon);
        editor.putString("distance", distance);
        editor.putString("zone", zone);
        editor.apply();
    }


    public void registerCurrentUserWithHardware() {

//        Read current user data from local storage SQlite database
        SharedPreferences sp = requireActivity().getSharedPreferences("CurrentUserData", Context.MODE_PRIVATE);
        String name = sp.getString("name", "local name");
        String username = sp.getString("username", "local username");
        String post = sp.getString("post", "local post");
        String email = sp.getString("email", "local email");
        String gender = sp.getString("gender", "");
        String address = sp.getString("address", "lcoal address");
        String phoneNumber = sp.getString("phoneNumber", "6162636465");


        if (GlobalVariable.bluetoothAdapter != null && GlobalVariable.bluetoothAdapter.isEnabled()
                && GlobalVariable.bluetoothSocket != null && GlobalVariable.bluetoothSocket.isConnected()
        ) {
            sendMessage("currentusername" + username + "\n");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage("currentname" + name + "\n");
                }
            }, 2000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage("currentemail" + email + "\n");
                }
            }, 4000);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    sendMessage("currentphonenumber" + phoneNumber + "\n");
                }
            }, 6000);
        }

    }

}