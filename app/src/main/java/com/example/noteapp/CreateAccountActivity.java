package com.example.noteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEdt, passwordEdt, confirmPasswordEdt;
    Button createAccount;
    ProgressBar progressBar;
    TextView loginBtnTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEdt = findViewById(R.id.edt_email);
        passwordEdt = findViewById(R.id.edt_password);
        confirmPasswordEdt = findViewById(R.id.edt_confirm_password);
        createAccount = findViewById(R.id.btn_create_account);
        progressBar = findViewById(R.id.progress_bar_create_account);
        loginBtnTv = findViewById(R.id.login_tv_btn);

        createAccount.setOnClickListener(v->createAccount());
        loginBtnTv.setOnClickListener(v->finish());

    }

    void createAccount(){
        String email = emailEdt.getText().toString();
        String password = passwordEdt.getText().toString();
        String confirmPassword = confirmPasswordEdt.getText().toString();

        boolean isValidated = validateData(email, password, confirmPassword);
        if (!isValidated){
            return;
        }

        createAccountInFireBase(email, password);
    }

    private void createAccountInFireBase(String email, String password) {
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if (task.isSuccessful()){
                            Toast.makeText(CreateAccountActivity.this, "Successfully create account, please check email to verify", Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else {
                            Toast.makeText(CreateAccountActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void changeInProgress(boolean inProgress){
        if (inProgress){
            progressBar.setVisibility(View.VISIBLE);
            createAccount.setVisibility(View.GONE);
        }else {
            progressBar.setVisibility(View.GONE);
            createAccount.setVisibility(View.VISIBLE);
        }
    }

    boolean validateData(String email, String password, String confirmPassword){
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdt.setError("Email is invalid");
            return false;
        }
        if (password.length() < 6){
            passwordEdt.setError("Password length is invalid");
            return false;
        }
        if (!password.equals(confirmPassword)){
            confirmPasswordEdt.setError("Password not matched");
            return false;
        }
        return true;
    }
}