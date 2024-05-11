package com.example.gclo.Adapters.TerminalAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Models.TerminalMessageModel;
import com.example.gclo.R;
import com.example.gclo.databinding.ReceiverMessageSampleLayoutBinding;
import com.example.gclo.databinding.SenderMessageSampleLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.List;

public class TerminalMessageAdapter extends RecyclerView.Adapter {
    List<TerminalMessageModel> messageModelList;
    final static int VIEW_TYPE_ADMIN = 1;
    final static int VIEW_TYPE_USER = 2;

    public TerminalMessageAdapter(List<TerminalMessageModel> messageModelList) {
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADMIN) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message_sample_layout, parent, false);
            return new UserTerminalHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_message_sample_layout, parent, false);
            return new BotTerminalHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        TerminalMessageModel message = messageModelList.get(position);
        if (message != null) {
//            if(holder.getItemViewType() == VIEW_TYPE_ADMIN){
//                holder.adminMessage.setText(message.getMessage());
//                holder.tvSenderTime.setText(getFormattedTime(message.getTimestamp()));
//            }else{
//                holder.userMessage.setText(message.getMessage());
//                holder.tvReceiverTime.setText(getFormattedTime(message.getTimestamp()));
//            }
            if (message.getSendtBy().equals(TerminalMessageModel.sent_by_admin)) {
//                holder.adminMessage.setText(message.getMessage());
//                holder.tvSenderTime.setText(getFormattedTime(message.getTimestamp()));
//                holder.adminMessage.setVisibility(View.VISIBLE);
//                holder.tvSenderTime.setVisibility(View.VISIBLE);
//                holder.constraintReceiver.setVisibility(View.GONE);
//                holder.userMessage.setVisibility(View.GONE);
//                holder.tvReceiverTime.setVisibility(View.GONE);

                UserTerminalHolder userTerminalHolder = (UserTerminalHolder) holder;
                userTerminalHolder.binding.tvSenderMessage.setText(message.getMessage());
                userTerminalHolder.binding.tvSenderTime.setText(getFormattedTime(message.getTimestamp()));

            } else {
//                holder.userMessage.setText(message.getMessage());
//                holder.tvReceiverTime.setText(getFormattedTime(message.getTimestamp()));
//                holder.userMessage.setVisibility(View.VISIBLE);
//                holder.tvReceiverTime.setVisibility(View.VISIBLE);
//                holder.constraintSender.setVisibility(View.GONE);
//                holder.adminMessage.setVisibility(View.GONE);
//                holder.tvSenderTime.setVisibility(View.GONE);
                BotTerminalHolder botTerminalHolder = (BotTerminalHolder) holder;
                botTerminalHolder.binding.tvReceiverMessage.setText(message.getMessage());
                botTerminalHolder.binding.tvReceiverTime.setText(getFormattedTime(message.getTimestamp()));
            }
        }


    }

    public String getFormattedTime(long timestamp) {
//      24 hours format ->  dd/MM/yyyy HH:mm
//      12 hours format ->  dd/MM/yyyy HH:mm a
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(timestamp);
    }

    @Override
    public int getItemViewType(int position) {
        if (messageModelList.get(position).getSendtBy().equals(TerminalMessageModel.sent_by_admin)) {
            return VIEW_TYPE_ADMIN;
        } else {
            return VIEW_TYPE_USER;
        }
    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class TerminalHolder extends RecyclerView.ViewHolder {
        TextView adminMessage, userMessage, tvSenderTime, tvReceiverTime;
        ConstraintLayout constraintSender, constraintReceiver;

        public TerminalHolder(View itemview) {
            super(itemview);
            adminMessage = itemview.findViewById(R.id.tvAdminMessage);
            userMessage = itemview.findViewById(R.id.tvUserMessage);
            tvSenderTime = itemview.findViewById(R.id.tvSenderTime);
            tvReceiverTime = itemview.findViewById(R.id.tvReceiverTime);
            constraintSender = itemview.findViewById(R.id.constraintSender);
            constraintReceiver = itemview.findViewById(R.id.constraintReceiver);
        }
    }


    public static class UserTerminalHolder extends RecyclerView.ViewHolder {

        SenderMessageSampleLayoutBinding binding;

        public UserTerminalHolder(@NonNull View itemView) {
            super(itemView);
            binding = SenderMessageSampleLayoutBinding.bind(itemView);
        }
    }

    public static class BotTerminalHolder extends RecyclerView.ViewHolder {
        ReceiverMessageSampleLayoutBinding binding;

        public BotTerminalHolder(@NonNull View itemView) {
            super(itemView);
            binding = ReceiverMessageSampleLayoutBinding.bind(itemView);
        }
    }
}
