package com.terminal.gclo.Fragments.Login;


import android.app.ProgressDialog;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.terminal.gclo.Activity.LoginActivity;
import com.terminal.gclo.MainActivity;
import com.terminal.gclo.R;
import com.terminal.gclo.Activity.SignupActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginFragment extends Fragment {

    FirebaseAuth auth;
    Button btnLogin;
    FirebaseDatabase database;

    ProgressDialog progressDialog;

    TextInputEditText textInputEditTextPassword;
    TextInputLayout textInputLayout;
    TextView tvVerifyEmail;
    TextView tvForgotPassword;
    TextView tvSignup;
    EditText etLoginEmail;
    private Context context;
    OnLoginSuccessListener onLoginSuccessListener;

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
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Logging In...");
        btnLogin.setOnClickListener(view2 -> {
            login();
        });
        tvSignup.setOnClickListener(view2 -> {
            startActivity(new Intent(getContext(), SignupActivity.class));
            requireActivity().finish();
        });

        tvForgotPassword.setOnClickListener(view2 -> replaceFragment(new ForgotPasswordFragment()));

        return view;
    }//onCreateView

    public interface OnLoginSuccessListener {
        void onLoginSuccess();
    }

    public void login() {

        String email = etLoginEmail.getText().toString();
        String password = textInputEditTextPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            progressDialog.show();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (auth.getCurrentUser().isEmailVerified()) {
                        Toast.makeText(context, "Logged In", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        Log.e("mainActivity", "Login to Main");
                        LoginActivity loginActivity = new LoginActivity();
                        if(onLoginSuccessListener!=null){
                            onLoginSuccessListener.onLoginSuccess();
                            progressDialog.dismiss();
                        }
                    } else {

                        tvVerifyEmail.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "Please verify your email id.", Toast.LENGTH_SHORT).show();
                        Log.e("mainActivity", "Pls vry email");
                        progressDialog.dismiss();
                    }
                } else {
                    progressDialog.dismiss();
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

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);
        if(context instanceof OnLoginSuccessListener){
            onLoginSuccessListener = (OnLoginSuccessListener) context;
        }else{
            throw new ClassCastException(context.toString() + " must implement OnLoginSuccessListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        onLoginSuccessListener = null;
    }
}
