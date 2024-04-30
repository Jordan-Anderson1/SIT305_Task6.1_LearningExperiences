package com.example.learningexperience;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    EditText editTextUsername, editTextEmail, editTextConfirmEmail, editTextPassword, editTextConfirmPassword, editTextPhoneNumber;
    TextView textViewLoginNow;

    Button registerButton;

    FirebaseFirestore fStore;
    FirebaseAuth mAuth;

    String userId;

    ImageView profileImage;

    StorageReference storageReference;

    Uri imageUri;

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
        storageReference = FirebaseStorage.getInstance().getReference();

        profileImage = findViewById(R.id.profileImage);

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


        //submit click listener(includes firebase auth logic and adds phone and username to firestore)
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


                //firebase Auth logic
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

                                uploadImageToFirebase(imageUri);


                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("Register", "onSuccess: user profile is created for " + email + "/" + userId);
                                    }
                                });
                                Intent intent = new Intent(getApplicationContext(), RegisterInterestsActivity.class);
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


        //opens gallery when stock image is clicked to select profile image
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
            }
        });

    }


    //sets image to selected image and uploads to firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000 && data != null){
            if(resultCode == Activity.RESULT_OK){
                imageUri = data.getData();
                profileImage.setImageURI(imageUri);

            }
        }

    }

    private void uploadImageToFirebase(Uri imageUri) {

        if(imageUri == null){
            Toast.makeText(this, "Image URI is null. Upload canceled.", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();
        StorageReference fileRef = storageReference.child("users/"+user.getUid()+"/profile.jpeg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
