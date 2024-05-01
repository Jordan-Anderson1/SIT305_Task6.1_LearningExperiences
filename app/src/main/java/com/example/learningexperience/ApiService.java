package com.example.learningexperience;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET("")
    Call<QuizResponse> getQuizData(@Url String url);
}
