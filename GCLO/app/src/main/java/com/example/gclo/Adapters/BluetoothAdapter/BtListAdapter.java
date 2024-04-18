package com.example.gclo.Adapters.BluetoothAdapter;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gclo.Models.BtListModel;
import com.example.gclo.R;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class BtListAdapter extends RecyclerView.Adapter<BtListAdapter.viewHolder> {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    List<BtListModel> btListModels;
    Context context;
//    private AdapterView.OnItemClickListener listener;
    public OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String deviceName);
    }

    public BtListAdapter(Context context, List<BtListModel> btListModels, OnItemClickListener listener) {
        this.btListModels = btListModels;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.devices_list_layout, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        BtListModel btListModel = this.btListModels.get(position);
        holder.tvBtDeviceName.setText(btListModel.getBtName());
        holder.tvBtDeviceAddress.setText(btListModel.getBtAddress());

//        BluetoothDevice bluetoothDevice = // Get your BluetoothDevice instance
//                BtListModel btListModel = new BtListModel("DeviceName", "DeviceAddress", bluetoothDevice);
//        btListModels.add(btListModel);
/*

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                @SuppressLint("MissingPermission") Set<BluetoothDevice> bluetoothDevices = getDefaultAdapter().getBondedDevices();
                for(BluetoothDevice device: bluetoothDevices){
//                BluetoothDevice selectedDevice = btListModels.get();

                    String selectedDevice = device.getName();
//                    Log.d("selectedDevice","Selected device: "+selectedDevice);
                }
                // Get the position of the clicked item
                int selectedDevicePosition = holder.getAdapterPosition();
                if (selectedDevicePosition != RecyclerView.NO_POSITION) {
                    // Get the Bluetooth device at the selected position
                    BtListModel selectedDevice = btListModels.get(selectedDevicePosition);
                    String deviceName = selectedDevice.getBtName();
                    String deviceAddress = selectedDevice.getBtAddress();
                    // Do whatever you need with the selected device and its position
                    Toast.makeText(v.getContext(), "Selected device position: "+selectedDevicePosition, Toast.LENGTH_SHORT).show();
                    Log.d("selectedDevice", "Selected device: " + deviceName+" "+deviceAddress);
                    Log.d("selectedDevicePosition", "Selected device position: " + selectedDevicePosition);

                } else {
                    Log.e("selectedDevice", "Invalid position");
                }

//                connectToDevice(selectedDevice);
                // Get the selected Bluetooth device based on the position
//                BluetoothDevice selectedDevice = btListModels.get(position).getBluetoothDevice();
//
//                if (selectedDevice != null) {
//                    // Now you have the selected BluetoothDevice, you can connect to it
//                    connectToDevice(selectedDevice);
//                } else {
//                    // Handle the case where the BluetoothDevice is null
//                    Toast.makeText(context, "Selected device is null", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        */
    }

    @Override
    public int getItemCount() {
        return this.btListModels.size();
    }

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        BluetoothSocket socket;
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
            // At this point, the connection is established. You can perform further actions if needed.
            Toast.makeText(context, "Connected to " + device.getName(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Connection failed", Toast.LENGTH_SHORT).show();
        }
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView tvBtDeviceAddress;
        TextView tvBtDeviceName;
        RecyclerView recyclerView;

        public viewHolder(View view) {
            super(view);
            tvBtDeviceName =  view.findViewById(R.id.tvBtDeviceName);
            tvBtDeviceAddress =  view.findViewById(R.id.tvBtDeviceAddress);
            recyclerView =  view.findViewById(R.id.recyclerView);


            // Set click listener on the item view
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the adapter position of the clicked item
                    int position = getAdapterPosition();
                    // Check if the position is valid
                    if (position != RecyclerView.NO_POSITION) {
                        BtListModel btListModel = btListModels.get(position);
                        // Retrieve the Bluetooth device name at the clicked position
                        String deviceName = btListModel.getBtName();
                        // Retrieve the Bluetooth device address at the clicked position
                        String deviceAddress = btListModel.getBtAddress();
                        // Invoke the onItemClick method of the listener interface
                        listener.onItemClick(deviceAddress);
                    }
                }
            });


        }
        /*
        public void bind(final BtListModel item, final  OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item.getBtName());
                }
            });
        }//bind
*/
    }
}//BtListAdapter
