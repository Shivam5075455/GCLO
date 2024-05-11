package com.example.gclo.Adapters.ChatAdapter;

import static android.text.format.DateFormat.getDateFormat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Models.ChatMessageModel;
import com.example.gclo.Models.TerminalMessageModel;
import com.example.gclo.R;
import com.example.gclo.databinding.ReceiverMessageSampleLayoutBinding;
import com.example.gclo.databinding.SenderMessageSampleLayoutBinding;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter {

    List<ChatMessageModel> chatMessageModels;
    final static int VIEW_TYPE_ADMIN = 1;
    final static int VIEW_TYPE_USER = 2;

    public ChatMessageAdapter(List<ChatMessageModel> chatMessageModels) {
        this.chatMessageModels = chatMessageModels;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ADMIN) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender_message_sample_layout, parent, false);
            return new AdminViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receiver_message_sample_layout, parent, false);
            return new UserViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageModel chatMessage = chatMessageModels.get(position);
        if (chatMessage.getSentBy().equals(ChatMessageModel.sent_by_admin)) {
//            holder.adminMessage.setVisibility(View.VISIBLE);
//            holder.userMessage.setVisibility(View.GONE);
//            holder.adminMessage.setText(chatMessage.getMessage());
            AdminViewHolder adminViewHolder = (AdminViewHolder) holder;
            adminViewHolder.binding.tvSenderMessage.setText(chatMessage.getMessage());
            adminViewHolder.binding.tvSenderTime.setText(getFormattedTime(chatMessage.getTimestamp()));

        } else {
            UserViewHolder userViewHolder = (UserViewHolder) holder;
            userViewHolder.binding.tvReceiverMessage.setText(chatMessage.getMessage());
            userViewHolder.binding.tvReceiverTime.setText(getFormattedTime(chatMessage.getTimestamp()));

//            holder.adminMessage.setVisibility(View.GONE);
//            holder.userMessage.setVisibility(View.VISIBLE);
//            holder.userMessage.setText(chatMessage.getMessage());
        }
    }

    public String getFormattedTime(long time) {
        //      24 hours format ->  dd/MM/yyyy HH:mm
//      12 hours format ->  dd/MM/yyyy HH:mm a
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(time);
    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageModels.get(position).getSentBy().equals(ChatMessageModel.sent_by_admin)) {
            return VIEW_TYPE_ADMIN;
        } else {
            return VIEW_TYPE_USER;
        }
    }


    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView adminMessage, userMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            userMessage = itemView.findViewById(R.id.tvUserMessage);
            adminMessage = itemView.findViewById(R.id.tvAdminMessage);
        }
    }

    //    Admin Viewholder class
    public static class AdminViewHolder extends RecyclerView.ViewHolder {
        SenderMessageSampleLayoutBinding binding;

        public AdminViewHolder(View itemView) {
            super(itemView);
            binding = SenderMessageSampleLayoutBinding.bind(itemView);
        }
    }

    //    User Viewholder class
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        ReceiverMessageSampleLayoutBinding binding;

        public UserViewHolder(View itemview) {
            super(itemview);
            binding = ReceiverMessageSampleLayoutBinding.bind(itemview);
        }
    }

}