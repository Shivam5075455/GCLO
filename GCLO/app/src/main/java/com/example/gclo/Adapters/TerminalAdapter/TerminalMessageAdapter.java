package com.example.gclo.Adapters.TerminalAdapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gclo.Models.TerminalMessageModel;
import com.example.gclo.R;
import java.util.List;

public class TerminalMessageAdapter extends RecyclerView.Adapter<TerminalMessageAdapter.TerminalHolder> {
    List<TerminalMessageModel> messageModelList;

    public TerminalMessageAdapter(List<TerminalMessageModel> messageModelList) {
        this.messageModelList = messageModelList;
    }

    @NonNull
    @Override
    public TerminalMessageAdapter.TerminalHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
        return new TerminalHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TerminalMessageAdapter.TerminalHolder holder, int position) {

        TerminalMessageModel message = messageModelList.get(position);
        if (message != null) {
            if (message.getSendtBy().equals(TerminalMessageModel.sent_by_admin)) {
                holder.adminMessage.setVisibility(View.VISIBLE);
                holder.userMessage.setVisibility(View.GONE);
                holder.adminMessage.setText(message.getMessage());
            } else {
                holder.adminMessage.setVisibility(View.GONE);
                holder.userMessage.setVisibility(View.VISIBLE);
                holder.userMessage.setText(message.getMessage());
            }
        }


    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class TerminalHolder extends RecyclerView.ViewHolder {
        TextView adminMessage, userMessage;

        public TerminalHolder(View itemview) {
            super(itemview);
            adminMessage = itemview.findViewById(R.id.tvAdminMessage);
            userMessage = itemview.findViewById(R.id.tvUserMessage);
        }
    }

}
