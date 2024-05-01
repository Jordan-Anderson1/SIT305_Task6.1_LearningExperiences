package com.example.learningexperience;

import java.util.List;

public class QuizResponse {

    private List<QuizItem> quiz;

    public List<QuizItem> getQuiz() {
        return quiz;
    }

    public class QuizItem {
        private String correct_answer;
        private List<String> options;
        private String question;

        public String getCorrectAnswer() {
            return correct_answer;
        }

        public List<String> getOptions() {
            return options;
        }

        public String getQuestion() {
            return question;
        }
    }
}
