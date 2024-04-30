package com.example.gclo.Fragments.NavigationFragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.gclo.Utility.GlobalVariable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    static final String TAG = "MY_DEBUG";
    List<ChatMessageModel> messageModelList;
    ChatMessageAdapter chatMessageAdapter;
    RecyclerView rvChat;
    ImageView imgSend;
    EditText etWriteMessage;
    //    private RetainedFragment retainedFragment;
    private ChatRetainedFragment chatRetainedFragment;

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    OutputStream outputStream;
    InputStream inputStream;

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
        chatMessageAdapter = new ChatMessageAdapter(messageModelList);
        rvChat.setAdapter(chatMessageAdapter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                ((MainActivity) requireActivity()).replaceFragments(new TerminalFragment());

            }
        });

        if (GlobalVariable.bluetoothDevice != null && GlobalVariable.bluetoothSocket != null && GlobalVariable.bluetoothSocket.isConnected()) {

            try {
                GlobalVariable.inputStream = GlobalVariable.bluetoothSocket.getInputStream();
                receiveMessage();
            } catch (IOException e) {
                    Log.d(TAG, "Error: " + e.getMessage());
            }
        }

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etWriteMessage.getText().toString();
                if (message != null) {
                    chatRetainedFragment.addMessage(new ChatMessageModel(message, ChatMessageModel.sent_by_admin));
                    messageModelList.add(new ChatMessageModel(message, ChatMessageModel.sent_by_admin));
                    chatMessageAdapter.notifyDataSetChanged();
                    scrollToLast();
                    sendMessage("chat" + message + "\n");
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


    public void sendMessage(String message) {

        if (GlobalVariable.bluetoothDevice != null && GlobalVariable.bluetoothSocket != null && GlobalVariable.bluetoothSocket.isConnected()) {
            try {
                GlobalVariable.outputStream = GlobalVariable.bluetoothSocket.getOutputStream();
                if (GlobalVariable.outputStream != null) {
                    GlobalVariable.outputStream.write(message.getBytes());
                    GlobalVariable.outputStream.flush();
                    Log.d(TAG, "Message sent: " + message);
                } else {
                    Log.d(TAG, "OutputStream is null");
                }
            } catch (IOException e) {

            }
        }
    }//end of sendMessage()

    public void receiveMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (GlobalVariable.bluetoothDevice != null && GlobalVariable.bluetoothSocket != null && GlobalVariable.bluetoothSocket.isConnected()) {

                    byte[] buffer = new byte[1024];
                    int bytes;
                    StringBuilder receivedMessage = new StringBuilder();
                    while (true) {
                        try {

                            while ((bytes = GlobalVariable.inputStream.read(buffer)) > 0) {
                                // Handle incoming message
                                String message = new String(buffer, 0, bytes);
                                receivedMessage.append(message);
                                Log.d(TAG, "Received message: " + message);
                            // Check for termination character or sequence
                                if (receivedMessage.toString().endsWith("\n")) {
                                    String completeMessage = receivedMessage.toString();
                                    if (getActivity() != null) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (completeMessage.contains("chat")) {
                                                    String mymessage = completeMessage.substring(4);
                                                    messageModelList.add(new ChatMessageModel(mymessage, ChatMessageModel.sent_by_user));
                                                    chatMessageAdapter.notifyDataSetChanged();
                                                    scrollToLast();
                                                    Log.d(TAG, "Received message: " + completeMessage);
                                                }
                                            }
                                        });
                                    }
                                    receivedMessage = new StringBuilder();
                                }
                            }
                        } catch (IOException e) {

                        }
                    }
                }
            }
        }).start();
    }
}
