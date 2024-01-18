package com.ulco.projetgrard;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ScoreActivity extends AppCompatActivity {
    private ArrayList<String> scoreList;
    private Map<String, String> scores;
    private Double moyenne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        // On récupère le score depuis l'intent
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String[] origList = intent.getStringArrayExtra(MainActivity.SCORES);
                if (origList != null) {
                    scoreList = new ArrayList<>(Arrays.asList(origList));
                }
                moyenne = extras.getDouble(MainActivity.MOYENNE);
            }
        }
        // On récupère le score depuis le savedInstanceState
        if (savedInstanceState != null) {
            String[] origList = savedInstanceState.getStringArray(MainActivity.SCORES);
            if (origList != null) {
                scoreList = new ArrayList<>(Arrays.asList(origList));
            }
            moyenne = savedInstanceState.getDouble(MainActivity.MOYENNE);
        }
        // On parse les scores
        parseScores();
        // On affiche les scores
        displayScores();
    }


    private void displayScores() {
        // On récupère la TableLayout
        TableLayout tableLayout = findViewById(R.id.notesLayout);
        // On crée le LayoutParams pour les TextView
        LinearLayout.LayoutParams paramTextView = new TableRow.LayoutParams(
                TableRow.LayoutParams.WRAP_CONTENT,
                TableRow.LayoutParams.WRAP_CONTENT,
                1.0f
        );
        // On ajoute les scores
        for (Map.Entry<String, String> entry : scores.entrySet()) {
            // On crée une nouvelle ligne
            TableRow tableRow = new TableRow(this);
            // On crée un nouveau TextView pour le thème
            TextView themeTextView = new TextView(this);
            themeTextView.setText(getResources().getString(R.string.note_quiz_title, entry.getKey()));
            // On ajoute les LayoutParams
            themeTextView.setLayoutParams(paramTextView);
            // On crée un nouveau TextView pour le score
            TextView scoreTextView = new TextView(this);
            scoreTextView.setText(entry.getValue());
            // On ajoute les TextView à la ligne
            tableRow.addView(themeTextView);
            tableRow.addView(scoreTextView);
            // On ajoute la ligne à la table
            tableLayout.addView(tableRow);
        }
        // On récupère le TextView pour la moyenne
        TextView moyenneTextView = findViewById(R.id.averageScore);
        // On affiche la moyenne
        moyenneTextView.setText(String.format("%s/20", moyenne.toString()));

    }

    private void parseScores() {
        // On récupère le score
        if (scoreList == null) {
            scoreList = new ArrayList<>();
        }
        // On crée la map des scores à partir de la liste
        scores = new HashMap<>(scoreList.size());
        for (String score : scoreList) {
            String[] split = score.split(" : ");
            scores.put(split[0], split[1]);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // On sauvegarde le score et la moyenne
        outState.putStringArray(MainActivity.SCORES, scoreList.toArray(new String[0]));
        outState.putDouble(MainActivity.MOYENNE, moyenne);
        super.onSaveInstanceState(outState);
    }

    public void onOkButtonClick(View view) {
        finish();
    }
}