package com.ulco.projetgrard;

import androidx.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Question implements Serializable {
    private final String question;
    private final List<String> answers;
    private Integer correctAnswer;

    Question(String question) {
        this.question = question;
        this.answers = new ArrayList<>();
    }

    public void addAnswer(String answer, Boolean isCorrect) {
        if (answer.isEmpty()) {
            return;
        }
        answers.add(answer);
        if (isCorrect) {
            correctAnswer = answers.size() - 1;
        }
    }

    public Boolean isCorrect(Integer answer) {
        return answer.equals(correctAnswer);
    }

    public Integer getNbAnswers() {
        return answers.size();
    }

    public String getAnswer(Integer index) {
        return answers.get(index);
    }

    public String getQuestion() {
        return question;
    }

    public void writeInFile(BufferedWriter writer) {
        try {
            writer.write(question);
            writer.newLine();
            for (int i = 0; i < answers.size(); i++) {
                writer.write("    " + answers.get(i));
                if (i == correctAnswer) {
                    writer.write(" x");
                }
                writer.newLine();
            }
            writer.newLine();
        } catch (Exception e) {
            e.printStackTrace();
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