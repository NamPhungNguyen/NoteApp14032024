package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText email_edit_text,password_edit_text;
    Button login_btn;
    ProgressBar progress_bar;
    TextView create_account_btn_tv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email_edit_text = findViewById(R.id.email_edit_text);
        password_edit_text = findViewById(R.id.password_edit_text);
        login_btn = findViewById(R.id.login_btn);
        progress_bar = findViewById(R.id.progress_bar);
        create_account_btn_tv = findViewById(R.id.create_account_tv_btn);

        login_btn.setOnClickListener(v -> loginUser());
        create_account_btn_tv.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class)));
    }

    void loginUser() {
        String email = email_edit_text.getText().toString().trim();
        String password = password_edit_text.getText().toString().trim();

        boolean isValidated = validateData(email, password);
        if (!isValidated) {
            return;
        }

        loginAccountInFireBase(email, password);
    }

    void loginAccountInFireBase(String email, String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if (task.isSuccessful()) {
                    // login success
                    if (firebaseAuth.getCurrentUser().isEmailVerified()) {
                        // go to mainactivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                    else  {
                        Utility.showToast(LoginActivity.this, "Email not verified, Please verify your email.");
                    }
                }else {
                    Utility.showToast(LoginActivity.this, task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inProgress) {
        if (inProgress) {
            progress_bar.setVisibility(View.VISIBLE);
            login_btn.setVisibility(View.GONE);
        }else {
            progress_bar.setVisibility(View.GONE);
            login_btn.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateData(String email, String password) {
        // validate the data that are input by user.
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_edit_text.setError("Email is invalid");
            return false;
        }

        if (password.length() < 6) {
            password_edit_text.setError("Password length is invalid");
            return false;
        }

        return true;
    }
}