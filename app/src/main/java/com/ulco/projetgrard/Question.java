package com.ulco.projetgrard;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Question implements Serializable {
    String question;
    List<String> answers = new ArrayList<>();
    Integer correctAnswer;

    Question(String question) {
        this.question = question;
    }

    void addAnswer(String answer, boolean isCorrect) {
        answers.add(answer);
        if (isCorrect) {
            correctAnswer = answers.size() - 1;
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Question: " + question + " " +
                "Bonne réponse: " + answers.get(correctAnswer) + " " +
                "Réponses: " + answers;
    }
}