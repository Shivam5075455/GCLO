package com.example.gclo.Fragments.NavigationFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
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
    private BluetoothAdapter bluetoothAdapter;// it provides the functionality of ON and OFF the Bluetooth
    Set<BluetoothDevice> BTPairedDevices = null;
    private static final String TAG_DEBUG = "DEBUG_MA";
    boolean bisBtConnected = false;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice = null;
    static public final int BT_CON_STATUS_NOT_CONNECTED = 0;
    static public final int BT_CON_STATUS_CONNECTING = 1;
    static public final int BT_CON_STATUS_CONNECTED = 2;
    static public final int BT_CON_STATUS_FAILED = 3;
    static public final int BT_CON_STATUS_CONNECTION_LOST = 4;
    static int iBTConnectionStatus = BT_CON_STATUS_NOT_CONNECTED;
    static final int BT_STATE_LISTENING = 1;
    static final int BT_STATE_CONNECTING = 2;
    static final int BT_STATE_CONNECTED = 3;
    static final int BT_STATE_CONNECTION_FAILED = 4;
    static final int BT_STATE_MESSAGE_RECEIVE = 5;

    List<TerminalMessageModel> messageModelList;
    RecyclerView rvTerminal;
    Spinner spinnerBTPairedDevices;
    private EditText etTerminalWriteMessage;
    private ImageView imgTerminalSend;
    private Button btnConnect, btnM1, btnM2, btnM3, btnM4, btnM5;
    static final String TAG = "RetainedFragment";
    private RetainedFragment retainedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);


        imgTerminalSend = view.findViewById(R.id.imgTerminalSend);
        etTerminalWriteMessage = view.findViewById(R.id.etTerminalWriteMessage);
        rvTerminal = view.findViewById(R.id.rvTerminal);
        spinnerBTPairedDevices = view.findViewById(R.id.spinnerBTPairedDevices);
        btnConnect = view.findViewById(R.id.btnConnect);
        btnM1 = view.findViewById(R.id.btnM1);
        btnM2 = view.findViewById(R.id.btnM2);
        btnM3 = view.findViewById(R.id.btnM3);
        btnM4 = view.findViewById(R.id.btnM4);
        btnM5 = view.findViewById(R.id.btnM5);

        messageModelList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvTerminal.setLayoutManager(linearLayoutManager);
        TerminalMessageAdapter terminalMessageAdapter = new TerminalMessageAdapter(messageModelList);
        rvTerminal.setAdapter(terminalMessageAdapter);

        checkBluetoothPermission();
        turnOnBluetooth();
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                if (bisBtConnected == false) {
                    Log.d(TAG_DEBUG, "btnConnect clicked");
                    if (spinnerBTPairedDevices.getSelectedItemPosition() == 0) {
                        Toast.makeText(getContext(), "Please select bluetooth device", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String sSelectedDevice = spinnerBTPairedDevices.getSelectedItem().toString();
                    Log.d(TAG_DEBUG, "Selected device: " + sSelectedDevice);

                    for (BluetoothDevice btDevice : BTPairedDevices) {
                        if (sSelectedDevice.equals(btDevice.getName())) {
                            bluetoothDevice = btDevice;
                            Log.d(TAG_DEBUG, "Selected device UUID: " + bluetoothDevice.getAddress());
                            cBluetoothConnect bluetoothConnect = new cBluetoothConnect(bluetoothDevice);
                            bluetoothConnect.start();

                        }
                    }
                } else {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d(TAG_DEBUG, "bluetooth disconnected exception: " + e.getMessage());
                    }
                    Log.d(TAG_DEBUG, "bluetooth disconnected");
                    btnConnect.setText("Connect");
                    bisBtConnected = false;
                }
            }
        });


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



        getPairedDevices();
        populateSpinnerWithPairedDevices();
    }

    private void handleBackPressed() {

        ((MainActivity) requireActivity()).showExitConfirmationDialog();
    }

    public void scrollToLast() {
        if (messageModelList.size() > 0) {
            rvTerminal.smoothScrollToPosition(messageModelList.size() - 1);
        }
    }

    private RetainedFragment getOrCreateRetainedFragment() {

        RetainedFragment fragment = (RetainedFragment) getFragmentManager().findFragmentByTag(RetainedFragment.TAG);

        if (fragment == null) {
            fragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(fragment, RetainedFragment.TAG).commit();
        }

        return fragment;
    }

    //    Bluetooth work
    // Check and request Bluetooth permissions
    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_BLUETOOTH_PERMISSION);
        }
    }//checkBluetoothPermission


    private void turnOnBluetooth() {
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Your device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }//bluetoothON

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(requireContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }//onActivityResult

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, proceed with Bluetooth operations
                Toast.makeText(getContext(), "Bluetooth connect permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Permissions denied, handle accordingly
                Toast.makeText(getContext(), "Bluetooth connect permission deny", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // get paired devices
    @SuppressLint("MissingPermission")
    void getPairedDevices() {
        Log.d(TAG_DEBUG, "\ngetPairedDevices - started");

            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            BTPairedDevices = bluetoothAdapter.getBondedDevices();//return all paired devicdes
            Log.d(TAG_DEBUG, "BT paired devices count: " + BTPairedDevices.size());
            for (BluetoothDevice btDevice : BTPairedDevices) {
                Log.d("BTDevice", btDevice.getName() + ", " + btDevice.getAddress());
            }
        }

    }//getPairedDevices

    // add paired devices into spinner
    @SuppressLint("MissingPermission")
    void populateSpinnerWithPairedDevices() {
        Log.d(TAG_DEBUG, "populateSpinnerWithPairedDevices started");
        ArrayList<String> allPairedDevices = new ArrayList<>();
        allPairedDevices.add("Select");
        if (bluetoothAdapter.isEnabled()) {
            for (BluetoothDevice btDevice : BTPairedDevices) {
                allPairedDevices.add(btDevice.getName());
            }
        }

        final ArrayAdapter<String> aaPairedDevices = new ArrayAdapter<>(getContext(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, allPairedDevices);
        aaPairedDevices.setDropDownViewResource(androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item);
        spinnerBTPairedDevices.setAdapter(aaPairedDevices);

    }//populateSpinnerWithPairedDevices

    //    created class to connect bluetooth in other thread
    public class cBluetoothConnect extends Thread {
        private BluetoothDevice btdevice; // device

        @SuppressLint("MissingPermission")
        public cBluetoothConnect(BluetoothDevice BTDevice) {
            Log.d(TAG_DEBUG, "cBluetoothConnect - started");
            btdevice = BTDevice;
            try {
                bluetoothSocket = btdevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG_DEBUG, "class cBluetoothConnect exception: " + e.getMessage());
            }
        }//cBluetoothConnect method

        @SuppressLint("MissingPermission")
        public void run() {
            try {
                bluetoothSocket.connect();
                Message message = Message.obtain();
                message.what = BT_STATE_CONNECTED;
                handler.sendMessage(message);

            } catch (IOException e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = BT_STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
                Log.d(TAG_DEBUG, "cBluetoothConnect class while disconnecting bluetooth - exception: " + e.getMessage());
            }
        }


    }//cBluetoothConnect class


    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case BT_STATE_LISTENING:
                    Log.d(TAG_DEBUG, "BT_STATE_LISTENING");
                    break;
                case BT_STATE_CONNECTING:
                    iBTConnectionStatus = BT_CON_STATUS_CONNECTING;
                    btnConnect.setText("Connecting...");
                    Log.d(TAG_DEBUG, "BT_STATE_CONNECTING");
                    break;
                case BT_STATE_CONNECTED:
                    iBTConnectionStatus = BT_CON_STATUS_CONNECTED;
                    Log.d(TAG_DEBUG, "BT_CON_STATUS_CONNECTED");
                    btnConnect.setText("Disconnect");

                    classBTInitDataCommunication BTSendReceive = new classBTInitDataCommunication(bluetoothSocket);
                    BTSendReceive.start();
                    bisBtConnected = true;
                    break;
                case BT_STATE_CONNECTION_FAILED:
                    iBTConnectionStatus = BT_CON_STATUS_FAILED;
                    bisBtConnected = false;
                    break;
                case BT_STATE_MESSAGE_RECEIVE:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    Log.d(TAG, "Message receive (" + tempMsg.length() + " ) data : " + tempMsg);
                    break;
            }

            return true;
        }
    });


    public class classBTInitDataCommunication extends Thread {
        private final BluetoothSocket bluetoothSocket1;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public classBTInitDataCommunication(BluetoothSocket bSocket) {
            Log.d(TAG_DEBUG, "classBTInitDataCommunication - started");
            bluetoothSocket1 = bSocket;
            InputStream tempInputStream = null;
            OutputStream tempOutputStream = null;

            try {
                tempInputStream = bluetoothSocket1.getInputStream();
                tempOutputStream = bluetoothSocket1.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG_DEBUG, "classBTInitDataCommunication - started exception: " + e.getMessage());
            }
            inputStream = tempInputStream;
            outputStream = tempOutputStream;
        }//classBTInitDataCommunication constructor

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            while (bluetoothSocket.isConnected()) {
                try {
                    bytes = inputStream.read(buffer);
                    handler.obtainMessage(BT_STATE_MESSAGE_RECEIVE, bytes, -1, buffer).sendToTarget();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "BT disconnect from decide end, exception " + e.getMessage());
                    iBTConnectionStatus = BT_CON_STATUS_CONNECTION_LOST;
                }

                try {
                    // disconnect bluetooth
                    Log.d(TAG_DEBUG, "Disconnecting MY BTConnection");
                    if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
                        bluetoothSocket.close();
                    }
                    btnConnect.setText("Connect");
                    bisBtConnected = false;
                    Log.d(TAG_DEBUG, "Disconnected MY BTConnection");
                } catch (IOException ex) {
                    ex.printStackTrace();
                    Log.d(TAG_DEBUG, "classBTInitDataCommunication disconnect excepton" + ex.getMessage());
                }
            }
        }
    }//classBTInitDataCommunication class
}