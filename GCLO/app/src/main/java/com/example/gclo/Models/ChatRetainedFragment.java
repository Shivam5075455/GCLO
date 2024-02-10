package com.example.gclo.Models;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class ChatRetainedFragment extends Fragment {
    public static final String TAG = "ChatRetainedFragment";

    private StringBuilder chatContent = new StringBuilder();
    private List<ChatMessageModel> chatMessageModels;

    public String getChatContent() {
        return chatContent.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public List<ChatMessageModel> getChatMessage(){
        if(chatMessageModels == null){
            chatMessageModels = new ArrayList<>();
        }
        return chatMessageModels;
    }
    public void addMessage(ChatMessageModel message) {
        getChatMessage().add(message);
    }

    // This method is called when the fragment is being destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        // Here, you can save any data if necessary
    }

    // This method is called when the fragment is being detached
    @Override
    public void onDetach() {
        super.onDetach();
        // Here, you can clean up any references
    }

}
