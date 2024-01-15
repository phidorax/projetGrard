package com.ulco.projetgrard;

import java.io.Serializable;
import java.util.ArrayList;

public class PlayQuestionnaire implements Serializable {
    private final Questionnaire questionnaire;
    private final ArrayList<Boolean> played;
    private Integer score;
    private Integer countQuestion;
    private Integer currentQuestion;

    public PlayQuestionnaire(Questionnaire questionnaire) {
        this.questionnaire = questionnaire;
        this.played = new ArrayList<>(questionnaire.getNbQuestions());
        for (int i = 0; i < questionnaire.getNbQuestions(); i++) {
            played.add(false);
        }
        this.score = 0;
        this.countQuestion = 0;
        this.currentQuestion = null;
    }

    public String getCategory() {
        return questionnaire.category;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getCountQuestion() {
        return countQuestion;
    }

    public Integer getNbQuestions() {
        return questionnaire.getNbQuestions();
    }

    public Question getCurrentQuestion() {
        return questionnaire.getQuestion(currentQuestion);
    }

    public Question getNextQuestion() {
        // Si on a joué toutes les questions, on retourne null
        if (played.stream().allMatch(Boolean::booleanValue)) {
            return null;
        }
        // Pioche une question au hasard
        int index = (int) (Math.random() * questionnaire.getNbQuestions());
        // Si la question a déjà été jouée, on en pioche une autre
        while (played.get(index)) {
            index = (int) (Math.random() * questionnaire.getNbQuestions());
        }
        // On retient que la question a été jouée
        currentQuestion = index;
        countQuestion++;
        // On retourne la question
        return questionnaire.getQuestion(index);
    }

    public void answerQuestion(Integer answer) {
        if (currentQuestion == null) {
            throw new IllegalStateException("No question to answer");
        }
        if (questionnaire.getQuestion(currentQuestion).isCorrect(answer)) {
            score++;
        }
        played.set(currentQuestion, true);
        currentQuestion = null;
    }

    public void cancelPlay() {
        currentQuestion = null;
        score = 0;
    }
}
