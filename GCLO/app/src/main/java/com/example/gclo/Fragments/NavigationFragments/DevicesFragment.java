package com.example.gclo.Fragments.NavigationFragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Adapters.BluetoothAdapter.BtListAdapter;
import com.example.gclo.MainActivity;
import com.example.gclo.Models.BtListModel;
import com.example.gclo.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DevicesFragment extends Fragment implements BtListAdapter.OnItemClickListener{
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    List<BtListModel> btListModels;
//    ListView listViewBt;
    RecyclerView recyclerView;
    TextView tvScan;
    Button btnConnect;
    private static final String TAG = "DEBUG_MA";
    boolean bisBtConnected = false;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice = null;
    /*
        final BroadcastReceiver receiver = new BroadcastReceiver() {
            @SuppressLint("MissingPermission")
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        device.getName();
                        device.getAddress();
                    }
                }
            }
        };
    */


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
//        listViewBt = view.findViewById(R.id.listviewBt);
        tvScan = view.findViewById(R.id.tvScan);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnConnect = view.findViewById(R.id.btnConnect);
        btListModels = new ArrayList<>();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        tvScan.setOnClickListener(view2 -> scan());

        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                loadFragment(new TerminalFragment());
            }
        });

        BtListAdapter btListAdapter = new BtListAdapter(getContext(),btListModels,this);
        recyclerView.setAdapter(btListAdapter);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bisBtConnected==false){
                    Log.d(TAG,"btnConnect Clicked");

                }
            }
        });
        return view;
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }

    public void scan() {
        checkBluetoothPermission();
        bluetoothON();
        listDevices();
    }

    private void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_PERMISSION);
        }
    }

    private void bluetoothON() {
        if (bluetoothAdapter == null) {
            Toast.makeText(requireContext(), "Your device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
        } else if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
// get the response and handle it after permission granted
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == -1) {
                Toast.makeText(requireContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void listDevices() {
        @SuppressLint("MissingPermission") Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        btListModels.clear();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                @SuppressLint("MissingPermission") String deviceName = device.getName();
                String deviceAddress = device.getAddress();
                BtListModel btListModel = new BtListModel(deviceName, deviceAddress);
                btListModels.add(btListModel);
            }
            BtListAdapter btListAdapter = new BtListAdapter(requireContext(), btListModels,this);
            recyclerView.setAdapter(btListAdapter);
            LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(linearLayout);
        }
    }

    @Override
    public void onItemClick(String deviceName) {
        Log.d(TAG,"onItemClick - started");
        // Handle item click here
        Toast.makeText(requireContext(), "Selected device: " + deviceName, Toast.LENGTH_SHORT).show();
        // You can perform any actions based on the selected device name
//        loadFragment(new TerminalFragment());
    }

    // connect two bluetooth device on separate thread(not on main thread) because UI is not block
    public class cBluetoothConnect extends Thread {
        private BluetoothDevice btdevice;

        public cBluetoothConnect(BluetoothDevice BTDevice) {
                checkBluetoothPermission();
            btdevice = BTDevice;
            try {
                bluetoothSocket = btdevice.createRfcommSocketToServiceRecord(MY_UUID);
            }catch (IOException e){

            }
        }

        public void run(){
            checkBluetoothPermission();
            try{
                bluetoothSocket.connect();
                Message message = Message.obtain();
                message.what = BT_STATE_CONNECTED;
                handler.sendMessage(message);

            }catch (IOException e){
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = BT_STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }

    }//cBluetoothConnect class

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {

                case BT_STATE_LISTENING:
                    Log.d(TAG, "BT_STATE_LISTENING");
                    break;
                case BT_STATE_CONNECTING:
                    iBTConnectionStatus = BT_CON_STATUS_CONNECTING;
                    btnConnect.setText("Connecting...");
                    Log.d(TAG, "BT_STATE_CONNECTING");
                    break;
                case BT_STATE_CONNECTED:
                    iBTConnectionStatus = BT_CON_STATUS_CONNECTED;
                    Log.d(TAG, "BT_CON_STATUS_CONNECTED");
                    btnConnect.setText("Disconnect");

//                    classBTInitDataCommunication BTSendReceive = new classBTInitDataCommunication(BTSocket);
//                    BTSendReceive.start();

                    bisBtConnected = true;
                    break;
                case BT_STATE_CONNECTION_FAILED:
                    iBTConnectionStatus = BT_CON_STATUS_FAILED;
                    Log.d(TAG, "BT_STATE_CONNECTION_FAILED");
                    bisBtConnected = false;
                    break;
                case BT_STATE_MESSAGE_RECEIVE:
                    byte[] readBuff = (byte[]) msg.obj;
                    String tempMsg = new String(readBuff, 0, msg.arg1);
                    Log.d(TAG,"Message receive (" + tempMsg.length()+" ) data : "+tempMsg);

//                    tvReceiveMessage.setText(tempMsg);

                    break;

            }


            return true;
        }
    });

    // Additional method to connect to a Bluetooth device
    private void connectToDevice() {
        // Check if the selected device is available
        if (bluetoothDevice != null) {
            // Check if Bluetooth is enabled
            if (bluetoothAdapter.isEnabled()) {
                // Attempt to create a Bluetooth socket
                try {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    bluetoothSocket.connect();

                    // Connection successful, handle further actions if needed

                } catch (IOException e) {
                    // Connection failed, handle the exception
                    Log.e("BluetoothConnection", "Error connecting to device: " + e.getMessage());
                    Toast.makeText(requireContext(), "Error connecting to device", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Bluetooth is not enabled, prompt the user to enable it
                Toast.makeText(requireContext(), "Bluetooth is not enabled", Toast.LENGTH_SHORT).show();
            }
        } else {
            // No device selected, prompt the user to select a device
            Toast.makeText(requireContext(), "No Bluetooth device selected", Toast.LENGTH_SHORT).show();
        }
    }
}//DevicesFragment
