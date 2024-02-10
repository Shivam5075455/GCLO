package com.example.gclo.Adapters.ChatAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Models.ChatMessageModel;
import com.example.gclo.Models.TerminalMessageModel;
import com.example.gclo.R;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {

    List<ChatMessageModel> chatMessageModels;

    public ChatMessageAdapter(List<ChatMessageModel> chatMessageModels) {
        this.chatMessageModels = chatMessageModels;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessageModel chatMessage = chatMessageModels.get(position);
        if (chatMessage.getSentBy().equals(ChatMessageModel.sent_by_admin)) {
            holder.adminMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setVisibility(View.GONE);
            holder.adminMessage.setText(chatMessage.getMessage());
        } else {
            holder.adminMessage.setVisibility(View.GONE);
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.userMessage.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageModels.size();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView adminMessage, userMessage;

        public ChatViewHolder(View itemView) {
            super(itemView);
            userMessage =  itemView.findViewById(R.id.tvUserMessage);
            adminMessage =  itemView.findViewById(R.id.tvAdminMessage);
        }
    }
}