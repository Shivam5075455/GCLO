package com.example.gclo.Models;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

public class RetainedFragment extends Fragment {

    public static final String TAG = "RetainedFragment";

    private StringBuilder terminalContent = new StringBuilder();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public void addMessage(String message) {
        terminalContent.append(message).append("\n");
    }

    public String getTerminalContent() {
        return terminalContent.toString();
    }
}

