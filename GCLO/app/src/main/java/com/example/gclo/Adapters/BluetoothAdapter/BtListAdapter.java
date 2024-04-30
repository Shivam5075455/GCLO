package com.example.gclo.Adapters.BluetoothAdapter;


import static com.google.android.material.color.MaterialColors.getColor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gclo.Models.BtListModel;
import com.example.gclo.R;
import java.util.List;
import java.util.UUID;


public class BtListAdapter extends RecyclerView.Adapter<BtListAdapter.viewHolder> {
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    List<BtListModel> btListModels;
    Context context;
//    private AdapterView.OnItemClickListener listener;
    public OnItemClickListener listener;
    private int selectedItem = -1; // Track the selected item position

    public interface OnItemClickListener {
        void onItemClick(String deviceAddress);
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
        // Set text color based on the selected item
        if (selectedItem == position) {
            holder.tvBtDeviceName.setTextColor(context.getResources().getColor(R.color.green));
            holder.tvBtDeviceAddress.setTextColor(context.getResources().getColor(R.color.green));
        } else {
            holder.tvBtDeviceName.setTextColor(context.getResources().getColor(R.color.black));
            holder.tvBtDeviceAddress.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return this.btListModels.size();
    }

    public void setSelectedItem(int position) {
        this.selectedItem = position;
        notifyDataSetChanged(); // Notify the adapter that the data set has changed
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
                        setSelectedItem(position);

                    }
                }
            });

        }
    }

}//BtListAdapter
