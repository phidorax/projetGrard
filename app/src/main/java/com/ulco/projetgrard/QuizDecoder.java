package com.ulco.projetgrard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class QuizDecoder {
    public static Questionnaire decodeQuiz(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            // La première ligne est la catégorie du quiz
            Questionnaire quiz = new Questionnaire(reader.readLine());
            String line;
            Question currentQuestion = null;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                String trimmedLine = line.trim();
                if (line.endsWith(" x")) {
                    assert currentQuestion != null;
                    currentQuestion.addAnswer(trimmedLine.substring(0, trimmedLine.length() - 2), true);
                } else if (Character.isWhitespace(line.charAt(0))) {
                    assert currentQuestion != null;
                    currentQuestion.addAnswer(trimmedLine, false);
                } else {
                    currentQuestion = new Question(line);
                    quiz.addQuestion(currentQuestion);
                }
            }
            return quiz;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}