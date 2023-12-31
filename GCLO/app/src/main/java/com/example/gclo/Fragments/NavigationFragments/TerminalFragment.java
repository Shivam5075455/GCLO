package com.example.gclo.Fragments.NavigationFragments;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gclo.MainActivity;
import com.example.gclo.Models.RetainedFragment;
import com.example.gclo.R;

public class TerminalFragment extends Fragment {

    private TextView tvTerminal;
    private EditText etTerminalWriteMessage;
    private ImageView imgTerminalSend;
    private Button btnM1, btnM2, btnM3, btnM4, btnM5;
    static final String TAG = "RetainedFragment";
    private RetainedFragment retainedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terminal, container, false);

        imgTerminalSend = view.findViewById(R.id.imgTerminalSend);
        tvTerminal = view.findViewById(R.id.tvTerminal);
        etTerminalWriteMessage = view.findViewById(R.id.etTerminalWriteMessage);
        btnM1 = view.findViewById(R.id.btnM1);
        btnM2 = view.findViewById(R.id.btnM2);
        btnM3 = view.findViewById(R.id.btnM3);
        btnM4 = view.findViewById(R.id.btnM4);
        btnM5 = view.findViewById(R.id.btnM5);
// this is used to save the message state
        retainedFragment = getOrCreateRetainedFragment();
        tvTerminal.setText("-> " + retainedFragment.getTerminalContent());
        imgTerminalSend.setOnClickListener(v -> {
            String message = etTerminalWriteMessage.getText().toString();
            if (message != null) {
                retainedFragment.addMessage("-> " + message);
                tvTerminal.setText(retainedFragment.getTerminalContent());
                etTerminalWriteMessage.setText("");
            } else {
                Toast.makeText(getContext(), "Please write a message", Toast.LENGTH_SHORT).show();
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
    }

    private void handleBackPressed() {

        ((MainActivity) requireActivity()).showExitConfirmationDialog();
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
