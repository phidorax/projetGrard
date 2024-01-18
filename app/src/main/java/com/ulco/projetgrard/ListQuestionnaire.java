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

    public ListQuestionnaire(List<Questionnaire> quiz) {
        this.quiz = quiz;
        this.scores = new HashMap<>();
        this.moyenne = 0.;
    }

    public Questionnaire getQuiz(int index) {
        return quiz.get(index);
    }
    public void addQuiz(Questionnaire questionnaire) {
        quiz.add(questionnaire);
    }

    public Boolean isAlreadyPlayed(Integer index) {
        return scores.containsKey(quiz.get(index).category);
    }

    public List<String> getStringQuiz() {
        // On récupère la liste des catégories et on la renvoie sous forme de liste de String
        List<String> stringQuiz = new ArrayList<>();
        quiz.forEach(questionnaire -> stringQuiz.add(questionnaire.category));
        return stringQuiz;
    }

    public Integer getScore(String category) {
        // Si la catégorie n'a pas de score, on renvoie 0
        if (!scores.containsKey(category)) {
            return 0;
        }
        // Sinon on renvoie le score
        return scores.get(category);
    }

    public Map<String, Integer> getScores() {
        // On renvoie la liste des scores
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
        // On renvoie la liste des scores sous forme de tableau de String
        return listScores.toArray(new String[0]);
    }

    public void putScore(String category, Integer score) {
        // On ajoute le score à la catégorie
        scores.put(category, score);
        // On recalcule la moyenne
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
        // Si il n'y a pas de score, la moyenne est de 0
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
        // Si il n'y a pas de score, la moyenne est de 0
        if (total == 0.) {
            moyenne = 0.;
            return;
        }
        // Calcul de la moyenne
        moyenne = total / scores.size();
    }

    public void resetScores() {
        // On vide la liste des scores
        scores.clear();
        // On remet la moyenne à 0
        moyenne = 0.;
    }
}
