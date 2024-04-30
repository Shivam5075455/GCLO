package com.example.gclo.Fragments.Login;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.gclo.MainActivity;
import com.example.gclo.R;
import com.example.gclo.Activity.SignupActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {

    FirebaseAuth auth;
    Button btnLogin;
    FirebaseDatabase database;

    TextInputEditText textInputEditTextPassword;
    TextInputLayout textInputLayout;
    TextView tvVerifyEmail;
    TextView tvForgotPassword;
    TextView tvSignup;
    EditText etLoginEmail;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        textInputLayout = view.findViewById(R.id.textInputLayout);
        textInputEditTextPassword = view.findViewById(R.id.textInputLayoutEdittextPassword);
        btnLogin = view.findViewById(R.id.btnLogin);
        tvSignup = view.findViewById(R.id.tvLoginSignup);
        etLoginEmail = view.findViewById(R.id.etLoginEmail);
        tvForgotPassword = view.findViewById(R.id.tvForgotPassword);
        context = getContext();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        btnLogin.setOnClickListener(view2 -> login());
        tvSignup.setOnClickListener(view2 -> {
            startActivity(new Intent(getContext(), SignupActivity.class));
            requireActivity().finish();
        });

        tvForgotPassword.setOnClickListener(view2 -> replaceFragment(new ForgotPasswordFragment()));

        return view;
    }

    public void login() {
        String email = etLoginEmail.getText().toString();
        String password = textInputEditTextPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (auth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(context, "Logged In", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        Log.e("mainActivity", "Login to Main");
                        requireActivity().finish();
                    } else {

                        tvVerifyEmail.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Please verify your email id.", Toast.LENGTH_SHORT).show();
                        Log.e("mainActivity", "Pls vry email");
                    }
                } else {
                    Toast.makeText(getContext(), "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getContext(), "Please enter credentials", Toast.LENGTH_SHORT).show();
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.loginFrameLayout, fragment);
        fragmentTransaction.commit();
    }
}
