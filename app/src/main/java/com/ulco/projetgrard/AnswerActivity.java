package com.ulco.projetgrard;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AnswerActivity extends AppCompatActivity {
    public static final String STATE = "com.ulco.projetgrard.STATE";
    private PlayQuestionnaire playQuestionnaire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        if (savedInstanceState != null) {
            playQuestionnaire = (PlayQuestionnaire) savedInstanceState.getSerializable(STATE);
        } else {
            // Récupération de l'intent
            Intent intent = getIntent();
            // Récupération du questionnaire
            Questionnaire questionnaire = (Questionnaire) intent.getSerializableExtra(MainActivity.QUIZ);
            if (questionnaire != null) {
                // Création du état du questionnaire
                playQuestionnaire = new PlayQuestionnaire(questionnaire);
                // Affichage du thème
                displayTheme();
                // Affichage de la question
                displayQuestion(playQuestionnaire.getNextQuestion());
            } else {
                // On affiche un message d'erreur
                Toast.makeText(this, R.string.error_load_quiz, Toast.LENGTH_LONG).show();
                // On retourne à l'activité précédente
                finish();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putSerializable(STATE, playQuestionnaire);
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        assert savedInstanceState != null;
        playQuestionnaire = (PlayQuestionnaire) savedInstanceState.getSerializable(STATE);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        playQuestionnaire = (PlayQuestionnaire) savedInstanceState.getSerializable(STATE);
    }

    private void displayTheme() {
        // Récupération du thème
        TextView themeTextView = findViewById(R.id.themeTitle);
        themeTextView.setText(playQuestionnaire.getCategory());
    }

    private void displayQuestion(Question question) {
        // Affichage de la question
        TextView questionTextView = findViewById(R.id.questionView);
        questionTextView.setText(question.getQuestion());
        // Récupération du nombre de questions
        TextView nbQuestionsTextView = findViewById(R.id.progressView);
        // Affichage du nombre de questions
        String progress = getResources().getString(R.string.progress_text,
                playQuestionnaire.getCountQuestion(), playQuestionnaire.getNbQuestions());
        nbQuestionsTextView.setText(progress);
        // Récupération du RadioGroup
        RadioGroup radioGroup = findViewById(R.id.answersGroup);
        // Créer un nouveau RadioButton
        RadioButton button;
        // Supprimer les RadioButtons du RadioGroup
        radioGroup.removeAllViews();
        // Ajouter des RadioButtons au RadioGroup dans une boucle
        for (int i = 0; i < question.getNbAnswers(); i++) {
            button = new RadioButton(this);
            button.setText(question.getAnswer(i));
            button.setId(i);
            radioGroup.addView(button);
        }
    }

    public void onStopButtonClick(View view) {
        // On annule le questionnaire
        playQuestionnaire.cancelPlay();
        // On envoie le résultat à l'activité précédente
        Intent intent = new Intent();
        intent.putExtra(MainActivity.RESULT, playQuestionnaire);
        setResult(RESULT_OK, intent);
        // On retourne à l'activité précédente
        finish();
    }

    public void onValideButtonClick(View view) {
        // Récupération du RadioGroup
        RadioGroup radioGroup = findViewById(R.id.answersGroup);
        // Récupération de l'index du RadioButton sélectionné
        int index = radioGroup.getCheckedRadioButtonId();
        // Si aucun RadioButton n'est sélectionné, on affiche un message d'erreur
        if (index == -1) {
            Toast.makeText(this, R.string.error_no_answer, Toast.LENGTH_LONG).show();
            return;
        }
        // On répond à la question
        playQuestionnaire.answerQuestion(index);
        // On récupère la prochaine question
        Question question = playQuestionnaire.getNextQuestion();
        // Si il y a encore des questions
        if (question != null) {
            // On affiche la question
            displayQuestion(question);
        } else {
            // Sinon on envoie le résultat à l'activité précédente
            Intent intent = new Intent();
            intent.putExtra(MainActivity.RESULT, playQuestionnaire);
            setResult(RESULT_OK, intent);
            // On retourne à l'activité précédente
            finish();
        }
    }
}