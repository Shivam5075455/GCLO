package com.example.gclo.Fragments.NavigationFragments;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
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

public class DevicesFragment extends Fragment {
    private static final int REQUEST_BLUETOOTH_PERMISSION = 2;
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter bluetoothAdapter;
    List<BtListModel> btListModels;
    ListView listViewBt;

    UUID yourUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    BluetoothSocket bluetoothSocket = null;
    BluetoothDevice bluetoothDevice;

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

    RecyclerView recyclerView;
    TextView tvScan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        listViewBt = view.findViewById(R.id.listviewBt);
        tvScan = view.findViewById(R.id.tvScan);
        recyclerView = view.findViewById(R.id.recyclerView);
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
            BtListAdapter btListAdapter = new BtListAdapter(requireContext(), btListModels);
            recyclerView.setAdapter(btListAdapter);
            LinearLayoutManager linearLayout = new LinearLayoutManager(requireContext());
            recyclerView.setLayoutManager(linearLayout);
        }
    }

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

                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(yourUUID);
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
}
