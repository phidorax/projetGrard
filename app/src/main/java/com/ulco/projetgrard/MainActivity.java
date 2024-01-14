package com.ulco.projetgrard;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Questionnaire> quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getQuiz();
        displayAvailableQuiz();
    }

    private void getQuiz() {
        // On récupère les questions du quiz
        this.quiz = new ArrayList<>();
        try (InputStream fis1 = getAssets().open("qcm01.txt")) {
            quiz.add(QuizDecoder.decodeQuiz(fis1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream fis2 = getAssets().open("qcm02.txt")) {
            quiz.add(QuizDecoder.decodeQuiz(fis2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (InputStream fis3 = getAssets().open("qcm03.txt")) {
            quiz.add(QuizDecoder.decodeQuiz(fis3));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayAvailableQuiz() {
        // On récupère la ListView
        ListView quizListView = findViewById(R.id.availableQuizListView);
        // On crée l'adapter
        ArrayAdapter<Questionnaire> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, quiz);
        // On l'associe à la ListView
        quizListView.setAdapter(adapter);
    }

    public void onSeeScoresButtonClick(View view) {
        // On crée l'activité ScoreActivity
        Intent intent = new Intent(this, ScoreActivity.class);
        // On lance l'activité
        startActivity(intent);
    }

    public void onResetScoresButtonClick(View view) {
    }

    public void onCreateQuizButtonClick(View view) {
        // On demande le mot de passe admin avec alert dialog
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.title_password_admin);
        alertDialog.setMessage(R.string.msg_password_admin);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alertDialog.setView(input);

        alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> {
            // On récupère le mot de passe entré
            String password = input.getText().toString();
            // On vérifie le mot de passe
            // Si le mot de passe est bon
            if (checkPassword(password)) {
                // On crée l'activité CreateQuizActivity
                Intent intent = new Intent(this, CreateQuizActivity.class);
                // On lance l'activité
                startActivity(intent);
            } else {
                // Sinon on affiche un message d'erreur
                Toast.makeText(this, R.string.password_incorrect, Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        alertDialog.show();
    }

    private boolean checkPassword(String password) {
        // On vérifie le mot de passe
        return password.equals("MDP");
    }

    public void onQuitAppButtonClick(View view) {
        finish();
    }
}