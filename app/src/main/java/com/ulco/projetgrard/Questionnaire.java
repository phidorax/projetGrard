package com.ulco.projetgrard;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Questionnaire implements Serializable {
    String category;
    List<Question> questions = new ArrayList<>();

    Questionnaire(String category) {
        this.category = category;
    }

    void addQuestion(Question question) {
        questions.add(question);
    }

    @NonNull
    @Override
    public String toString() {
        return category;
    }
}