package com.example.learningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultsActivity extends AppCompatActivity {

    String answer1, answer2, answer3, correctAnswer1, correctAnswer2, correctAnswer3;

    TextView q1userAnswer, q1correctAnswer, q2userAnswer, q2correctAnswer, q3userAnswer, q3correctAnswer;

    Button returnHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        answer1 = intent.getStringExtra("answer1");
        answer2 = intent.getStringExtra("answer2");
        answer3 = intent.getStringExtra("answer3");
        correctAnswer1 = intent.getStringExtra("correctAnswer1");
        correctAnswer2 = intent.getStringExtra("correctAnswer2");
        correctAnswer3 = intent.getStringExtra("correctAnswer3");

        q1userAnswer = findViewById(R.id.q1userAnswer);
        q2userAnswer = findViewById(R.id.q2userAnswer);
        q3userAnswer = findViewById(R.id.q3userAnswer);

        q1correctAnswer = findViewById(R.id.q1correctAnswer);
        q2correctAnswer = findViewById(R.id.q2correctAnswer);
        q3correctAnswer = findViewById(R.id.q3correctAnswer);

        q1userAnswer.setText("Your answer: " + answer1);
        q2userAnswer.setText("Your answer: " + answer2);
        q3userAnswer.setText("Your answer: " + answer3);

        q1correctAnswer.setText("Correct answer: " + correctAnswer1);
        q2correctAnswer.setText("Correct answer: " + correctAnswer2);
        q3correctAnswer.setText("Correct answer: " + correctAnswer3);

        returnHomeButton = findViewById(R.id.returnHomeButton);
        returnHomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });




    }
}