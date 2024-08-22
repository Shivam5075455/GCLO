package com.terminal.gclo.Fragments.NavigationFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.terminal.gclo.Adapters.BluetoothAdapter.BtListAdapter;
import com.terminal.gclo.MainActivity;
import com.terminal.gclo.Models.BluetoothViewModel;
import com.terminal.gclo.Models.BtListModel;
import com.terminal.gclo.Models.RetainedFragment;
import com.terminal.gclo.R;
import com.terminal.gclo.Utility.Constants;
import com.terminal.gclo.Utility.GlobalVariable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DevicesFragment extends Fragment implements BtListAdapter.OnItemClickListener {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    boolean bisBtConnected = false;
    boolean isPermissionGranted = true;
    UUID MY_UUID = UUID.fromString(Constants.uuid);

    //    BluetoothAdapter bluetoothAdapter;
//        BluetoothSocket bluetoothSocket;
//    BluetoothDevice bluetoothDevice;
//    OutputStream outputStream;
//    InputStream inputStream;
    List<BtListModel> btListModels;
    private BluetoothViewModel bluetoothViewModel;
    BtListAdapter btListAdapter;

    String deviceName;
    String deviceAddress;
    RecyclerView recyclerView;
    TextView tvScan;
    Button btnConnect;
    private static final String TAG = "DEBUG_MA";
    RetainedFragment retainedFragment = new RetainedFragment();

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
//        listViewBt = view.findViewById(R.id.listviewBt);
        tvScan = view.findViewById(R.id.tvScan);
        recyclerView = view.findViewById(R.id.recyclerView);
        btnConnect = view.findViewById(R.id.btnConnect);
        btListModels = new ArrayList<>();
        GlobalVariable.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        tvScan.setOnClickListener(view2 -> {

            scan();
        });

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        // Permission granted, proceed with Bluetooth operation
                        enableBluetoothIfNecessary();

                    } else {
                        // Permission denied, handle accordingly
                        Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                    }
                });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestBluetoothConnectPermission();
        }

        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());
            }
        });

        btListAdapter = new BtListAdapter(getContext(), btListModels, this);
        recyclerView.setAdapter(btListAdapter);

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalVariable.disconnectBluetoothDevice(getContext());
            }
        });
        scan();
        return view;
    }//onCreateView

    /*public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }*/


    public void scan() {
        Log.d(TAG, "scan: started");
        if (GlobalVariable.bluetoothAdapter.isEnabled()) {
            listDevices();
        }
    }

/*    @RequiresApi(api = Build.VERSION_CODES.S)
    public void checkBluetoothPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(requireActivity(), new String[]{
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN
            }, REQUEST_BLUETOOTH_PERMISSION);
        }
    }*/

    private void bluetoothON() {
        if (GlobalVariable.bluetoothAdapter == null) {
            if (isPermissionGranted) {
                Toast.makeText(requireContext(), "Your device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
            }
        } else if (!GlobalVariable.bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            isPermissionGranted = false;
        }
    }

   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                bluetoothON();
                Toast.makeText(getContext(), "Bluetooth permissions granted", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Permission denied
            Toast.makeText(getContext(), "Bluetooth permissions denied", Toast.LENGTH_SHORT).show();
        }
    }//onRequestPermissionsResult

    // get the response and handle it after permission granted
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_ENABLE_BT) {
//            if (resultCode == RESULT_OK) {
//                Toast.makeText(requireContext(), "Bluetooth Enabled", Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(requireContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requestBluetoothConnectPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            // Permission already granted, proceed with Bluetooth operation
            enableBluetoothIfNecessary();
        }
    }


    private void enableBluetoothIfNecessary() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @SuppressLint("MissingPermission")
    private void listDevices() {

        if (GlobalVariable.bluetoothAdapter == null) {
            Log.e(TAG, "Bluetooth is not supported on this device");
            return;
        }
        Set<BluetoothDevice> pairedDevices = GlobalVariable.bluetoothAdapter.getBondedDevices();
        btListModels.clear();
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                deviceName = device.getName();
                deviceAddress = device.getAddress();
                BtListModel btListModel = new BtListModel(deviceName, deviceAddress);
                btListModels.add(btListModel);
            }
            btListAdapter = new BtListAdapter(requireContext(), btListModels, this);
            recyclerView.setAdapter(btListAdapter);
            LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(linearLayout);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onItemClick(String deviceAddress) {
        Log.d(TAG, "onItemClick - started");
        // Handle item click here
        Toast.makeText(requireContext(), "Selected device: " + deviceAddress, Toast.LENGTH_SHORT).show();
        // You can perform any actions based on the selected device name
//        loadFragment(new TerminalFragment());
        // Connect to the selected device
// Discover devices
//        if (!bluetoothAdapter.isDiscovering()) {
//            bluetoothAdapter.startDiscovery();
//        }
        // Start Bluetooth discovery (optional, you can skip this if the device is already paired)
//        bluetoothAdapter.startDiscovery();
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        bluetoothViewModel.setBluetoothDevice(bluetoothAdapter.getRemoteDevice(deviceAddress));
//        bluetoothDevice = bluetoothViewModel.getBluetoothDevice();
        GlobalVariable.bluetoothDevice = GlobalVariable.bluetoothAdapter.getRemoteDevice(deviceAddress);
        Log.d("bluetooth", "Bluetooth device: " +
                GlobalVariable.bluetoothDevice);
        connectToDevice(deviceAddress);
    }

    public void connectToDevice(String deviceAddress) {
//        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);

        new Thread(new Runnable() {
            @SuppressLint("MissingPermission")
            @Override
            public void run() {
                try {
                    // Create a Bluetooth socket
                    GlobalVariable.bluetoothSocket = GlobalVariable.bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                    bluetoothViewModel.setBluetoothSocket(bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID));
//                    bluetoothSocket = bluetoothViewModel.getBluetoothSocket();
                    Log.d(TAG, "Bluetooth socket: " + GlobalVariable.bluetoothSocket);
//                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                    bluetoothAdapter.cancelDiscovery();
                    // Connect to the remote device through the socket. This is a blocking call.
//                    GlobalVariable.bluetoothSocket.connect();
                    GlobalVariable.bluetoothSocket.connect();

                    // After successfully connecting to the Bluetooth device keep the BluetoothSocket object into retainedfragment

//                    retainedFragment.setBluetoothSocket(bluetoothSocket);
//                    bluetoothViewModel.setBluetoothSocket(bluetoothSocket);

                    // If the connection is successful, you can get the OutputStream to send data
//                    outputStream = bluetoothSocket.getOutputStream();
                    // Notify the user that the connection was successful
//                    store connected bluetooth device and bluetoothSocket
//                    SharedPreferences sharedPreferences = requireContext().getSharedPreferences("BluetoothPref", Context.MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("remoteDevice", bluetoothDevice.toString());
//                    editor.putString("bluetoothSocket", bluetoothSocket.toString());
//                    editor.apply();

                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Connected to: " + GlobalVariable.bluetoothDevice.getName(), Toast.LENGTH_SHORT).show();
                        btnConnect.setText("Disconnect");
                        bisBtConnected = true;
                        ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                        ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());
                    });

                } catch (Exception e) {
                    e.printStackTrace();

                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Connection failed", Toast.LENGTH_SHORT).show();
//                        btnConnect.setText("Connect");
//                        bisBtConnected = false;
                    });

                    try {
//                        GlobalVariable.bluetoothSocket.close();
                        GlobalVariable.bluetoothSocket.close();
                    } catch (IOException closeException) {
                        Log.e(TAG, "Could not close the client socket", closeException);
                    }
                }
            }
        }).start();
    }

/*

    public void disconnectBluetoothDevice(){
        try{
            if(GlobalVariable.bluetoothSocket != null){
                GlobalVariable.bluetoothSocket.close();
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream=null;
            }
            if(inputStream!=null){
                inputStream.close();
                inputStream=null;
            }
            if(GlobalVariable.bluetoothDevice!=null){
                GlobalVariable.bluetoothDevice=null;
            }
            Log.d(TAG, "Socket closed");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
*/

/*    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            Log.d(TAG, "Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


}//DevicesFragment
/*


Wrote stack traces to tombstoned
2024-04-18 20:40:34.896  1634-11911 ActivityManager         system_server                        E  ANR in com.example.gclo (com.example.gclo/.MainActivity)
                                                                                   0.3% 1184/android.hardware.bluetooth@1.0-service-qti: 0% user + 0.3% kernel / faults: 20 minor 16 major
2024-04-18 20:40:34.896  1634-11911 ActivityManager         system_server                        E    0.3% 1190/android.hardware.gnss@2.1-service-qti: 0% user + 0.3% kernel / faults: 74 minor 43 major

 */