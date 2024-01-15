package com.ulco.projetgrard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListQuestionnaire implements Serializable {
    private final List<Questionnaire> quiz;
    private final Map<String, Integer> scores;
    private Integer moyenne;

    public ListQuestionnaire() {
        this.quiz = new ArrayList<>();
        this.scores = new HashMap<>();
        this.moyenne = 0;
    }

    public ListQuestionnaire(List<Questionnaire> quiz) {
        this.quiz = quiz;
        this.scores = new HashMap<>();
        this.moyenne = 0;
    }

    public Questionnaire getQuiz(int index) {
        return quiz.get(index);
    }

    public Boolean isAlreadyPlayed(Integer index) {
        return scores.containsKey(quiz.get(index).category);
    }

    public List<String> getStringQuiz() {
        List<String> stringQuiz = new ArrayList<>();
        quiz.forEach(questionnaire -> stringQuiz.add(questionnaire.category));
        return stringQuiz;
    }

    public Integer getScore(String category) {
        return scores.get(category);
    }

    public Map<String, Integer> getScores() {
        return scores;
    }

    public void putScore(String category, Integer score) {
        calculMoyenne();
        scores.put(category, score);
    }

    public Integer getMoyenne() {
        return moyenne;
    }

    private void calculMoyenne() {
        // Calcul de la moyenne sur 20
        int total = 0;
        for (int index = 0; index < quiz.size(); index++) {
            if (scores.containsKey(quiz.get(index).category)) {
                Integer score = scores.get(quiz.get(index).category);
                if (score != null) {
                    total += score * 20 / quiz.get(index).questions.size();
                }
            }
        }
        moyenne = total / scores.size();
    }
}
