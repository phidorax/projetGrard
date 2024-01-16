package com.ulco.projetgrard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ListQuestionnaire implements Serializable {
    private final List<Questionnaire> quiz;
    private final Map<String, Integer> scores;
    private Double moyenne;

    public ListQuestionnaire() {
        this.quiz = new ArrayList<>();
        this.scores = new HashMap<>();
        this.moyenne = 0.;
    }

    public ListQuestionnaire(List<Questionnaire> quiz) {
        this.quiz = quiz;
        this.scores = new HashMap<>();
        this.moyenne = 0.;
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

    public String[] getListScores() {
        List<String> listScores = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : scores.entrySet()) {
            // On récupère le quiz grâce au nom de la catégorie
            String titleQuiz = entry.getKey();
            Questionnaire questionnaire = this.quiz.stream().filter(q -> q.category.equals(titleQuiz)).collect(Collectors.toList()).get(0);
            // On récupère le score
            Integer score = entry.getValue();
            // On ajoute le score à la liste
            listScores.add(titleQuiz + " : " + score + "/" + questionnaire.questions.size());
        }
        return listScores.toArray(new String[0]);
    }

    public void putScore(String category, Integer score) {
        scores.put(category, score);
        calculMoyenne();
    }

    public Double getMoyenne() {
        // Calcul de la moyenne si elle n'a pas été calculée
        if (moyenne == 0.) {
            calculMoyenne();
        }
        // Renvoie de la moyenne arrondie à 2 chiffres après la virgule
        return Math.round(moyenne * 100.) / 100.;
    }

    private void calculMoyenne() {
        if (scores.isEmpty()) {
            moyenne = 0.;
            return;
        }
        // Calcul de la moyenne sur 20
        double total = 0.;
        for (int index = 0; index < quiz.size(); index++) {
            if (scores.containsKey(quiz.get(index).category)) {
                Integer score = scores.get(quiz.get(index).category);
                if (score != null) {
                    total += (double) (score * 20) / quiz.get(index).questions.size();
                }
            }
        }
        if (total == 0.) {
            moyenne = 0.;
            return;
        }
        moyenne = total / scores.size();
    }

    public void resetScores() {
        scores.clear();
        moyenne = 0.;
    }
}
