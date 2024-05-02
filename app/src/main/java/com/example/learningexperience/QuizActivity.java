package com.example.learningexperience;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QuizActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    TextView questionOneDetails, questionTwoDetails, questionThreeDetails, number, description;
    RadioButton q1option1, q1option2, q1option3, q2option1, q2option2, q2option3, q3option1, q3option2, q3option3;
    RadioGroup q1radioGroup, q2radioGroup, q3radioGroup;
    String answer1, answer2, answer3;
    Button submitButton;
    String correctAnswer1, correctAnswer2, correctAnswer3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quiz);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //get the selected interest from mainactivity
        Intent intent = getIntent();
        String interest = intent.getStringExtra("interest");
        String url = "/getQuiz?topic=" + interest;

        String taskNumber = intent.getStringExtra("taskNumber");

        questionOneDetails = findViewById(R.id.questionOneDetails);
        questionTwoDetails = findViewById(R.id.questionTwoDetails);
        questionThreeDetails = findViewById(R.id.questionThreeDetails);
        q1option1 = findViewById(R.id.q1option1);
        q1option2 = findViewById(R.id.q1option2);
        q1option3 = findViewById(R.id.q1option3);
        q2option1 = findViewById(R.id.q2option1);
        q2option2 = findViewById(R.id.q2option2);
        q2option3 = findViewById(R.id.q2option3);
        q3option1 = findViewById(R.id.q3option1);
        q3option2 = findViewById(R.id.q3option2);
        q3option3 = findViewById(R.id.q3option3);

        q1radioGroup = findViewById(R.id.q1radioGroup);
        q2radioGroup = findViewById(R.id.q2radioGroup);
        q3radioGroup = findViewById(R.id.q3radioGroup);

        submitButton = findViewById(R.id.submitButton);

        number = findViewById(R.id.number);
        description = findViewById(R.id.description);

        number.setText("Generated task " + taskNumber);
        description.setText("here is your task based on your interest in " + interest);




        //API logic to retrieve quiz info.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000")
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient.Builder().readTimeout(10, java.util.concurrent.TimeUnit.MINUTES).build()) // this will set the read timeout for 10mins (IMPORTANT: If not your request will exceed the default read timeout)
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<QuizResponse> call = apiService.getQuizData(url);
        call.enqueue(new Callback<QuizResponse>() {
            @Override
            public void onResponse(Call<QuizResponse> call, Response<QuizResponse> response) {
                if(response.isSuccessful()){
                    Log.d("API", "succcess");
                    QuizResponse quizResponse = response.body();
                    if(quizResponse != null){
                        List<QuizResponse.QuizItem> quizItems = quizResponse.getQuiz();



                        //set questions and answer options
                        String firstQuestion = quizItems.get(0).getQuestion();
                        List<String> q1options= quizItems.get(0).getOptions();
                        q1option1.setText(q1options.get(0));
                        q1option2.setText(q1options.get(1));
                        q1option3.setText(q1options.get(2));

                        //converts correct answer from A, B, or C to string result.
                        correctAnswer1 = quizItems.get(0).getCorrectAnswer();
                        switch(correctAnswer1){
                            case "A":
                                correctAnswer1 = q1options.get(0);
                                break;
                            case "B":
                                correctAnswer1 = q1options.get(1);
                                break;
                            case "C":
                                correctAnswer1 = q1options.get(2);
                                break;
                            default:
                                Log.d("Switch", "default");
                                break;
                        }


                        String secondQuestion = quizItems.get(1).getQuestion();
                        List<String> q2options = quizItems.get(1).getOptions();
                        q2option1.setText(q2options.get(0));
                        q2option2.setText(q2options.get(1));
                        q2option3.setText(q2options.get(2));
                        correctAnswer2 = quizItems.get(1).getCorrectAnswer();

                        correctAnswer2 = quizItems.get(0).getCorrectAnswer();
                        switch(correctAnswer2){
                            case "A":
                                correctAnswer2 = q2options.get(0);
                                break;
                            case "B":
                                correctAnswer2 = q2options.get(1);
                                break;
                            case "C":
                                correctAnswer2 = q2options.get(2);
                                break;
                            default:
                                Log.d("Switch", "default");
                                break;
                        }

                        String thirdQuestion = quizItems.get(2).getQuestion();
                        List<String> q3options = quizItems.get(2).getOptions();
                        q3option1.setText(q3options.get(0));
                        q3option2.setText(q3options.get(1));
                        q3option3.setText(q3options.get(2));
                        correctAnswer3 = quizItems.get(2).getCorrectAnswer();

                        correctAnswer3 = quizItems.get(0).getCorrectAnswer();
                        switch(correctAnswer3){
                            case "A":
                                correctAnswer3 = q3options.get(0);
                                break;
                            case "B":
                                correctAnswer3 = q3options.get(1);
                                break;
                            case "C":
                                correctAnswer3 = q3options.get(2);
                                break;
                            default:
                                Log.d("Switch", "default");
                                break;
                        }


                        //set question descriptions
                        questionOneDetails.setText(firstQuestion);
                        questionTwoDetails.setText(secondQuestion);
                        questionThreeDetails.setText(thirdQuestion);

                    }
                }else{
                    Toast.makeText(getApplicationContext(), "API request unsuccessful",
                            Toast.LENGTH_SHORT).show();
                    Log.d("API", "fail");
                }
            }

            @Override
            public void onFailure(Call<QuizResponse> call, Throwable throwable) {

            }
        });

        //set on click listener to get selected answers
        q1radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                if(radioButton != null) {
                    answer1 = radioButton.getText().toString();

                }
            }
        });

        q2radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                if(radioButton != null) {
                    answer2 = radioButton.getText().toString();
                }
            }
        });

        q3radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                if(radioButton != null) {
                   answer3 = radioButton.getText().toString();
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
                intent.putExtra("answer1", answer1);
                intent.putExtra("answer2", answer2);
                intent.putExtra("answer3", answer3);
                intent.putExtra("correctAnswer1", correctAnswer1);
                intent.putExtra("correctAnswer2", correctAnswer2);
                intent.putExtra("correctAnswer3", correctAnswer3);




                startActivity(intent);
                finish();
            }
        });
    }
}