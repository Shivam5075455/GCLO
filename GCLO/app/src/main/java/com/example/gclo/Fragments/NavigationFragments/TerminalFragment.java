package com.example.gclo.Fragments.NavigationFragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gclo.Adapters.TerminalAdapter.TerminalMessageAdapter;
import com.example.gclo.MainActivity;
import com.example.gclo.Models.TerminalMessageModel;
import com.example.gclo.Models.RetainedFragment;
import com.example.gclo.R;

import java.util.ArrayList;
import java.util.List;

public class TerminalFragment extends Fragment {


    List<TerminalMessageModel> messageModelList;
    RecyclerView rvTerminal;
    private EditText etTerminalWriteMessage;
    private ImageView imgTerminalSend;
    private Button btnM1, btnM2, btnM3, btnM4, btnM5;
    static final String TAG = "RetainedFragment";
    private RetainedFragment retainedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);


        imgTerminalSend = view.findViewById(R.id.imgTerminalSend);
        etTerminalWriteMessage = view.findViewById(R.id.etTerminalWriteMessage);
        rvTerminal = view.findViewById(R.id.rvTerminal);
        btnM1 = view.findViewById(R.id.btnM1);
        btnM2 = view.findViewById(R.id.btnM2);
        btnM3 = view.findViewById(R.id.btnM3);
        btnM4 = view.findViewById(R.id.btnM4);
        btnM5 = view.findViewById(R.id.btnM5);

        messageModelList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        rvTerminal.setLayoutManager(linearLayoutManager);
        TerminalMessageAdapter terminalMessageAdapter = new TerminalMessageAdapter(messageModelList);
        rvTerminal.setAdapter(terminalMessageAdapter);
// To save the messages after switching the fragment
        retainedFragment = getOrCreateRetainedFragment();
        List<TerminalMessageModel> retainedMessages = retainedFragment.getMessages();
        if (retainedMessages != null && !retainedMessages.isEmpty()) {
            messageModelList.addAll(retainedMessages);
            terminalMessageAdapter.notifyDataSetChanged();
            scrollToLast();
        }

        imgTerminalSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etTerminalWriteMessage.getText().toString();
                if (!message.isEmpty()) {
                    retainedFragment.addMessage(new TerminalMessageModel(message, TerminalMessageModel.sent_by_admin));
                    messageModelList.add(new TerminalMessageModel(message, TerminalMessageModel.sent_by_admin));
                    terminalMessageAdapter.notifyDataSetChanged();
                    scrollToLast();
                    etTerminalWriteMessage.setText("");

                }
            }
        });

        // Handle back button press
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    // Handle back button press here
//                    Toast.makeText(getContext(), "Back button pressed", Toast.LENGTH_SHORT).show();
                    handleBackPressed();
                    return true; // Consumed the event
                }
                return false; // Let the system handle the event
            }
        });


        return view;
    }//onCreateView

    private void handleBackPressed() {

        ((MainActivity) requireActivity()).showExitConfirmationDialog();
    }

    public void scrollToLast() {
        if (messageModelList.size() > 0) {
            rvTerminal.smoothScrollToPosition(messageModelList.size() - 1);
        }
    }

    private RetainedFragment getOrCreateRetainedFragment() {

        RetainedFragment fragment = (RetainedFragment) getFragmentManager().findFragmentByTag(RetainedFragment.TAG);

        if (fragment == null) {
            fragment = new RetainedFragment();
            getFragmentManager().beginTransaction().add(fragment, RetainedFragment.TAG).commit();
        }

        return fragment;
    }
}