package com.example.gclo.Adapters.BluetoothAdapter;

import static android.bluetooth.BluetoothAdapter.getDefaultAdapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

    public BtListAdapter(Context context, List<BtListModel> btListModels) {
        this.btListModels = btListModels;
        this.context = context;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("MissingPermission") Set<BluetoothDevice> bluetoothDevices = getDefaultAdapter().getBondedDevices();
                for(BluetoothDevice device: bluetoothDevices){
//                BluetoothDevice selectedDevice = btListModels.get();

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

        public viewHolder(View view) {
            super(view);

            tvBtDeviceName =  view.findViewById(R.id.tvBtDeviceName);
            tvBtDeviceAddress =  view.findViewById(R.id.tvBtDeviceAddress);
        }
    }
}
