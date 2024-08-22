package com.terminal.gclo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.terminal.gclo.Fragments.Login.LoginFragment;
import com.terminal.gclo.MainActivity;
import com.terminal.gclo.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity implements LoginFragment.OnLoginSuccessListener{
    FirebaseAuth auth;
    Button btnLogin;
    FirebaseDatabase database;
    EditText etEmail;
    EditText etPassword;
    LoginFragment loginFragment = new LoginFragment();
    ProgressDialog progressDialog;
    TextView tvForgotPassword;
    TextView tvSignup;
    TextView tvVerifyEmail;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        window.setStatusBarColor(getColor(R.color.status_bar));



        loadFragment(new LoginFragment());

    } //onCreate


    public void kill() {
        finish();
    } // it's use in LoginFragment.java

    public void login() {
        String email = this.etEmail.getText().toString();
        String password = this.etPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            this.auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() { // from class: com.example.gpsapplication.LoginActivity.1
                @Override // com.google.android.gms.tasks.OnCompleteListener
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (LoginActivity.this.auth.getCurrentUser().isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Logged In", Toast.LENGTH_SHORT).show();
                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                            return;
                        }
                        LoginActivity.this.tvVerifyEmail.setVisibility(View.VISIBLE);
                        Toast.makeText(LoginActivity.this, "Please verify your email id.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(LoginActivity.this, "Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.loginFrameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onLoginSuccess() {
        finish();
    }
}
