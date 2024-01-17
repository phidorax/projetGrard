package com.ulco.projetgrard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {
    List<Question> questions = new ArrayList<>();
    Question currentQuestion;
    ArrayAdapter<String> answersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_quiz);

        // On récupère la ListView
        ListView listView = (ListView) findViewById(R.id.answersList);
        // On crée l'adapter
        answersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        // On attache l'adapter à la ListView
        listView.setAdapter(answersAdapter);
    }

    public void onClickAddAnswerButton(View view) {
        // On récupère la réponse
        TextView answer = (TextView) findViewById(R.id.answerEditText);
        String answerText = answer.getText().toString();
        if (answerText.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_answer, Toast.LENGTH_LONG).show();
            return;
        }
        if (currentQuestion == null) {
            // On crée la question
            currentQuestion = new Question(answerText);
        }
        // On récupère le bouton radio
        Switch answerSwitch = (Switch) findViewById(R.id.answerSwitch);
        // On vérifie si la réponse est la bonne réponse et on l'ajoute à la question
        currentQuestion.addAnswer(answerText, answerSwitch.isChecked());
        // On l'ajoute à l'adapter
        displayCurrentAnswers();
        // On vide le champ de réponse
        answer.setText("");
        // On remet le switch à false
        answerSwitch.setChecked(false);
    }

    public void onClickAddQuestionButton(View view) {
        // On récupère la question
        TextView questionView = (TextView) findViewById(R.id.questionEditText);
        String questionText = questionView.getText().toString();
        if (questionText.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_question, Toast.LENGTH_LONG).show();
            return;
        }
        // On crée la question
        Question question = new Question(questionText);
        // On ajoute les réponses à la question
        if (currentQuestion != null) {
            for (int i = 0; i < currentQuestion.getNbAnswers(); i++) {
                question.addAnswer(currentQuestion.getAnswer(i), currentQuestion.isCorrect(i));
            }
        }
        // On ajoute la question à la liste
        questions.add(question);
        // On vide le champ de question
        questionView.setText("");
        // On vide la question courante
        currentQuestion = null;
        // On vide l'adapter
        displayCurrentAnswers();
        // On récupère la réponse
        TextView answer = (TextView) findViewById(R.id.answerEditText);
        // On vide le champ de réponse
        answer.setText("");
        // On récupère le switch
        Switch answerSwitch = (Switch) findViewById(R.id.answerSwitch);
        // On remet le switch à false
        answerSwitch.setChecked(false);
        // On affiche la question dans un Toast
        String resume = question.getQuestion() + "\n" + question.getNbAnswers() + getString(R.string.toast_answer);
        Toast.makeText(this, resume, Toast.LENGTH_LONG).show();
    }

    public void onClickAddQuizButton(View view) {
        // On récupère la catégorie du quiz
        TextView category = (TextView) findViewById(R.id.categoryEditText);
        String categoryText = category.getText().toString();
        if (categoryText.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_category, Toast.LENGTH_LONG).show();
            return;
        }
        Questionnaire questionnaire = new Questionnaire(categoryText);
        if (questions.isEmpty()) {
            Toast.makeText(this, R.string.toast_empty_quiz, Toast.LENGTH_LONG).show();
            return;
        }
        // On ajoute les questions au questionnaire
        for (Question question : questions) {
            questionnaire.addQuestion(question);
        }
        // On envoie le questionnaire à l'activité précédente
        getIntent().putExtra(MainActivity.QUIZ, questionnaire);
        setResult(RESULT_OK, getIntent());
        // On ferme l'activité
        finish();
    }

    private void displayCurrentAnswers() {
        // On vide l'adapter
        answersAdapter.clear();
        // On ajoute les réponses à l'adapter
        if (currentQuestion != null) {
            // On ajoute les réponses à l'adapter
            for (int i = 0; i < currentQuestion.getNbAnswers(); i++) {
                answersAdapter.add(currentQuestion.isCorrect(i)
                        ? ">" + currentQuestion.getAnswer(i) + "<"
                        : currentQuestion.getAnswer(i));
            }
        }
    }
}