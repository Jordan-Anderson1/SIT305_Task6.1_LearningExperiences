package com.example.learningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextConfirmEmail, editTextPassword, editTextConfirmPassword, editTextPhoneNumber;
    TextView textViewLoginNow;

    Button registerButton;

    FirebaseFirestore fStore;
    FirebaseAuth mAuth;

    String userId;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        editTextUsername = findViewById(R.id.usernameInput);
        editTextEmail = findViewById(R.id.emailInput);
        editTextConfirmEmail = findViewById(R.id.confirmEmailInput);
        editTextPassword = findViewById(R.id.passwordInput);
        editTextConfirmPassword = findViewById(R.id.confirmPasswordInput);
        editTextPhoneNumber = findViewById(R.id.phoneNumberInput);

        registerButton = findViewById(R.id.registerButton);

        textViewLoginNow = findViewById(R.id.loginNow);

        textViewLoginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username, email, confirmEmail, password, confirmPassword, phone;

                username = editTextUsername.getText().toString();
                email = editTextEmail.getText().toString();
                confirmEmail = editTextConfirmEmail.getText().toString();
                password = editTextPassword.getText().toString();
                confirmPassword = editTextConfirmPassword.getText().toString();
                phone = editTextPhoneNumber.getText().toString();

                //check no empty fields and passwords and emails match
                if(TextUtils.isEmpty(username)
                        || TextUtils.isEmpty(email)
                        || TextUtils.isEmpty(confirmEmail)
                        || TextUtils.isEmpty(password)
                        || TextUtils.isEmpty(confirmPassword)
                        || TextUtils.isEmpty(phone)){
                    Toast.makeText(RegisterActivity.this, "Please complete all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!email.equals(confirmEmail)){
                    Toast.makeText(RegisterActivity.this, "Emails do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.equals(confirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "User created.",
                                            Toast.LENGTH_SHORT).show();

                                    userId = mAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(userId);
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("username", username);
                                    user.put("phone", phone);
                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Register", "onSuccess: user profile is created for " + email + "/" + userId);
                                        }
                                    });
                                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Error!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });




            }
        });


    }
}