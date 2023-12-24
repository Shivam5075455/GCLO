package com.example.gclo.Fragments.Login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.gclo.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordFragment extends Fragment {

    private FirebaseAuth auth;
    private Button btnReset;
    private EditText etEmail;
    private ImageView imgBackArrow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgot_password, container, false);

        etEmail = view.findViewById(R.id.etForgotPasswordEmail);
        btnReset = view.findViewById(R.id.btnReset);
        imgBackArrow = view.findViewById(R.id.imgArrowBack);

        auth = FirebaseAuth.getInstance();

        imgBackArrow.setOnClickListener(view2 -> replaceFragment(new LoginFragment()));

        btnReset.setOnClickListener(v -> forgotPassword());

        // Set up onBackPressed callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                replaceFragment(new LoginFragment());
            }
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.loginFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void forgotPassword() {
        String emailText = etEmail.getText().toString().trim();

        if (!emailText.isEmpty()) {
            ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Sending password reset email...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            auth.sendPasswordResetEmail(emailText).addOnCompleteListener(task -> {
                progressDialog.dismiss();

                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Email sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to send an email", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            etEmail.setError("Enter email");
            Toast.makeText(getContext(), "Please enter a valid email", Toast.LENGTH_SHORT).show();
        }
    }
}
