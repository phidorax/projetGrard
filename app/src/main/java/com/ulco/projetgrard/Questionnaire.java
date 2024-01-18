package com.ulco.projetgrard;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
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

    Integer getNbQuestions() {
        return questions.size();
    }

    Question getQuestion(Integer index) {
        return questions.get(index);
    }

    public void writeInFile(BufferedWriter writer) {
        // On écrit la catégorie et les questions dans le fichier
        try {
            writer.write(category);
            writer.newLine();
            for (Question question : questions) {
                question.writeInFile(writer);
            }
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "Questionnaire: " + category + " " +
                "Questions: " + questions;
    }
}
