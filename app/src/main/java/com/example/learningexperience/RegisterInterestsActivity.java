package com.example.learningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RegisterInterestsActivity extends AppCompatActivity {

    TextView algorithms, dataStructures, webDevelopment, testing;

    ArrayList<String> interests;
    Button nextButton;
    FirebaseFirestore fStore;
    FirebaseAuth mAuth;

    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_interests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        algorithms = findViewById(R.id.algorithms);
        dataStructures = findViewById(R.id.dataStructures);
        webDevelopment = findViewById(R.id.webDevelopment);
        testing = findViewById(R.id.testing);
        interests = new ArrayList<>();
        nextButton = findViewById(R.id.nextButton);

        algorithms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interests.add("algorithms");
            }
        });

        dataStructures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interests.add("Data Structures");
            }
        });

        webDevelopment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interests.add("Web Development");
            }
        });

        testing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interests.add("Testing");
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId = mAuth.getCurrentUser().getUid();
                DocumentReference docRef = fStore.collection("users").document(userId);

                for(int i = 0; i < interests.size(); i++){
                    String interest = interests.get(i);
                    docRef.update("interests", FieldValue.arrayUnion(interest));
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}