package com.example.gclo.Fragments.NavigationFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Adapters.ChatAdapter.ChatMessageAdapter;
import com.example.gclo.MainActivity;
import com.example.gclo.Models.ChatMessageModel;
import com.example.gclo.Models.ChatRetainedFragment;
import com.example.gclo.Models.RetainedFragment;
import com.example.gclo.Models.TerminalMessageModel;
import com.example.gclo.R;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {


    List<ChatMessageModel> messageModelList;
    RecyclerView rvChat;
    ImageView imgSend;
    EditText etWriteMessage;
    //    private RetainedFragment retainedFragment;
    private ChatRetainedFragment chatRetainedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        etWriteMessage = view.findViewById(R.id.etWriteMessage);
        imgSend = view.findViewById(R.id.imgSend);
        rvChat = view.findViewById(R.id.rvChat);

        messageModelList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(linearLayoutManager);
        ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(messageModelList);
        rvChat.setAdapter(chatMessageAdapter);

// To save the messages after switching the fragments
        chatRetainedFragment = getOrCreateRetainedFragment();
        List<ChatMessageModel> retainedMessage = chatRetainedFragment.getChatMessage();
        if (retainedMessage != null && !retainedMessage.isEmpty()) {
            messageModelList.addAll(retainedMessage);
            chatMessageAdapter.notifyDataSetChanged();
            scrollToLast();
        }


        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Update the toolbar title when back is pressed
                ((MainActivity) requireActivity()).changeToolbarTitle("Terminal");
                loadFragment(new TerminalFragment());
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etWriteMessage.getText().toString();
                if (message != null) {
                    chatRetainedFragment.addMessage(new ChatMessageModel(message, ChatMessageModel.sent_by_admin));
                    messageModelList.add(new ChatMessageModel(message, ChatMessageModel.sent_by_admin));
                    chatMessageAdapter.notifyDataSetChanged();
                    scrollToLast();
                    etWriteMessage.setText("");
                }
            }
        });

        return view;
    }

    private void scrollToLast() {
        if (messageModelList.size() > 0) {
            rvChat.smoothScrollToPosition(messageModelList.size() - 1);
        }
    }

    private ChatRetainedFragment getOrCreateRetainedFragment() {
        ChatRetainedFragment fragment = (ChatRetainedFragment) getFragmentManager().findFragmentByTag(ChatRetainedFragment.TAG);
        if (fragment == null) {
            fragment = new ChatRetainedFragment();
            getFragmentManager().beginTransaction().add(fragment, ChatRetainedFragment.TAG).commit();
        }
        return fragment;
    }



    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayoutContainer, fragment);
        fragmentTransaction.commit();
    }
}
