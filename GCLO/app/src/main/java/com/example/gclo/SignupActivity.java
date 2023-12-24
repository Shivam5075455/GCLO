package com.example.gclo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private Button btnSignup;
    private String confirmPassword, password;

    private EditText etUsername, etEmail,etSignupName;
    private ProgressDialog progressDialog;
    private TextView tvSignupLogin;
    private TextInputLayout signupTextInputLayoutPassword, signupTextInputLayoutPasswordConfirm;
    private TextInputEditText signupTextInputEditTextPassword, signupTextInputEditTextPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Window window = this.getWindow();
        window.setStatusBarColor(getColor(R.color.status_bar));


        auth = FirebaseAuth.getInstance();
        btnSignup = findViewById(R.id.btnSignup);
        etUsername = findViewById(R.id.etSignupUsername);
        etSignupName = findViewById(R.id.etSignupName);
        etEmail = findViewById(R.id.etSignupEmail);
        tvSignupLogin = findViewById(R.id.tvSignupLogin);
        signupTextInputLayoutPassword = findViewById(R.id.signupTextInputLayoutPassword);
        signupTextInputLayoutPasswordConfirm = findViewById(R.id.signupTextInputLayoutPasswordConfirm);
        signupTextInputEditTextPassword = findViewById(R.id.signupTextInputEditTextPassword);
        signupTextInputEditTextPasswordConfirm = findViewById(R.id.signupTextInputEditTextPasswordConfirm);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering User...");

        password = signupTextInputEditTextPassword.getText().toString();
        textInputEditTextPassword();

        btnSignup.setOnClickListener(view -> {
            String name = etSignupName.getText().toString();
            String userName = etUsername.getText().toString();
            String email = etEmail.getText().toString();
            password = signupTextInputEditTextPassword.getText().toString();
            confirmPassword = signupTextInputEditTextPasswordConfirm.getText().toString();

            if (password.equals(confirmPassword)) {
                progressDialog.setCancelable(false);
                progressDialog.show();
                sendVerificationEmail();
                signup(email, password, userName,name);
            } else {
                signupTextInputEditTextPasswordConfirm.setError("Password didn't match");
            }
        });

        tvSignupLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void signup(String email, String password, String userName,String name) {
        if (!userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = Objects.requireNonNull(auth.getCurrentUser().getUid());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://gps-application-de939-default-rtdb.firebaseio.com/").getReference("Users").child(uid);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", auth.getCurrentUser().getUid());
                    hashMap.put("name",name);
                    hashMap.put("username", userName);
                    hashMap.put("email", email);
                    hashMap.put("password", password);
                    hashMap.put("imageUrl", "default");
                    hashMap.put("post", "");
                    hashMap.put("address", "");
                    hashMap.put("phoneNumber", "");
                    hashMap.put("gender", "");

                    databaseReference.setValue(hashMap).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(SignupActivity.this, "Account Successfully Created. Please Verify your email id.", Toast.LENGTH_SHORT).show();
                            sendVerificationEmail();
                            progressDialog.dismiss();
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    progressDialog.dismiss();
                    btnSignup.setEnabled(true);
                    Toast.makeText(SignupActivity.this, "This account is already created", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Please Enter credentials", Toast.LENGTH_SHORT).show();
            if (userName.isEmpty()) {
                etUsername.setError("Enter username");
            }
            if (email.isEmpty()) {
                etEmail.setError("Enter email");
            }
            if (password.isEmpty()) {
                signupTextInputEditTextPassword.setError("Enter password");
            }
        }
    }

    private void textInputEditTextPassword() {
        signupTextInputEditTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                Pattern pattern = Pattern.compile("[^a-zA-Z0-9]");
                Matcher matcher = pattern.matcher(password);
                boolean isPasswordMatched = matcher.find();

                if (password.length() < 6) {
                    signupTextInputEditTextPassword.setError("Password must be at least 6 characters long.");
                    return;
                }

                if (isPasswordMatched) {
                    signupTextInputLayoutPassword.setHelperText("weak password");
                    signupTextInputLayoutPassword.setError("");
                    if (password.length() >= 10) {
                        signupTextInputLayoutPassword.setHelperText("very strong password");
                        signupTextInputLayoutPassword.setError("");
                    } else {
                        signupTextInputLayoutPassword.setError("");
                    }
                } else {
                    signupTextInputLayoutPassword.setHelperText("Password must be a combination of upper letter, lower letter, one number, and one special character");
                    signupTextInputLayoutPassword.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("EmailVerification", "Email sent.");
                    Toast.makeText(SignupActivity.this, "Email sent successfully", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SignupActivity.this, "Email sent failed", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
