package com.ulco.projetgrard;

import static android.os.Environment.DIRECTORY_DOCUMENTS;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String QUIZ = "com.ulco.projetgrard.QUIZ";
    public static final String RESULT = "com.ulco.projetgrard.RESULT";
    private static final String LIST_QUIZ = "com.ulco.projetgrard.LIST_QUIZ";
    public static final String SCORES = "com.ulco.projetgrard.SCORES";
    public static final String MOYENNE = "com.ulco.projetgrard.MOYENNE";
    private ListQuestionnaire listQuestionnaire;
    private final ActivityResultLauncher<Intent> answerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent mess = result.getData();
                    if (mess != null) {
                        Bundle extras = mess.getExtras();
                        PlayQuestionnaire quizResult = null;
                        if (extras != null) {
                            // On récupère le résultat
                            quizResult = (PlayQuestionnaire) extras.getSerializable(RESULT);
                        }
                        if (quizResult != null) {
                            // On récupère le score
                            int score = quizResult.getScore();
                            // On récupère le thème
                            String category = quizResult.getCategory();
                            // On met à jour le score
                            listQuestionnaire.putScore(category, score);
                            // On met à jour la ListView
                            displayAvailableQuiz();
                        }
                    }
                }
            });

    private final ActivityResultLauncher<Intent> createQuizLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent mess = result.getData();
                    if (mess != null) {
                        Bundle extras = mess.getExtras();
                        Questionnaire quiz = null;
                        if (extras != null) {
                            // On récupère le quiz
                            quiz = (Questionnaire) extras.getSerializable(QUIZ);
                        }
                        if (quiz != null) {
                            // On écrit le quiz dans un fichier dans le dossier public DIRECTORY_DOCUMENTS avec pour nom qcm-yyMMddHHmm.txt
                            File directory = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "projetGrard");
                            // On récupère la date de création du quiz
                            String creationDate;
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                creationDate = "qcm-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm"));
                            } else {
                                creationDate = "qcm-" + quiz.category;
                            }
                            // On crée le fichier
                            File file = new File(directory, creationDate + ".txt");
                            try {
                                boolean newFile = file.createNewFile();
                                if (!newFile) {
                                    // Si le fichier n'a pas pu être créé, on affiche un message d'erreur
                                    Toast.makeText(this, R.string.error_create_file, Toast.LENGTH_LONG).show();
                                    // On quitte l'application
                                    finish();
                                }
                                // On copie le fichier
                                FileOutputStream fos = new FileOutputStream(file);
                                OutputStreamWriter osw = new OutputStreamWriter(fos);
                                BufferedWriter bw = new BufferedWriter(osw);
                                quiz.writeInFile(bw);
                                bw.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            // On ajoute le quiz à la liste
                            listQuestionnaire.addQuiz(quiz);
                            // On met à jour la ListView
                            displayAvailableQuiz();
                        }
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MaterialToolbar toolbar = findViewById(R.id.materialToolbar);
        setSupportActionBar(toolbar);

        // On récupère les données dans l'état de l'activité
        if (savedInstanceState != null) {
            listQuestionnaire = (ListQuestionnaire) savedInstanceState.getSerializable(LIST_QUIZ);
        }
        if (listQuestionnaire == null) {
            listQuestionnaire = new ListQuestionnaire(getQuiz());
            // On récupère les données persistantes
            SharedPreferences shared = getSharedPreferences(SCORES, MODE_PRIVATE);
            // On récupère les scores
            for (String category : listQuestionnaire.getStringQuiz()) {
                if (shared.contains(category)) {
                    listQuestionnaire.putScore(category, shared.getInt(category, 0));
                }
            }
        }
        displayAvailableQuiz();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(LIST_QUIZ, listQuestionnaire);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.see_scores) {
            onSeeScoresButtonClick();
            return true;
        } else if (id == R.id.reset_scores) {
            onResetScoresButtonClick();
            return true;
        } else if (id == R.id.create_quiz) {
            onCreateQuizButtonClick();
            return true;
        } else if (id == R.id.quit_app) {
            onQuitAppButtonClick(true);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    //onClose
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // On lance la sauvegarde des scores
        onQuitAppButtonClick(false);
    }

    private ArrayList<Questionnaire> getQuiz() {
        ArrayList<Questionnaire> quiz = new ArrayList<>();
        // Récupère le dossier public DIRECTORY_DOCUMENTS
        File directory = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOCUMENTS), "projetGrard");
        // Crée une liste pour stocker les fichiers

        // Vérifie si le dossier existe
        if (!directory.exists()) {
            // Si le dossier n'existe pas, on le crée
            boolean directoryOk = directory.mkdir();
            if (!directoryOk) {
                // Si le dossier n'a pas pu être créé, on affiche un message d'erreur
                Toast.makeText(this, R.string.error_create_directory, Toast.LENGTH_LONG).show();
                // On quitte l'application
                finish();
            }
        }

        // Parcours tous les fichiers du dossier donc l'application a accès
        // Ajoute le fichier à la liste
        File[] listFiles = directory.listFiles();
        List<File> files = listFiles != null ? new ArrayList<>(Arrays.asList(listFiles)) : new ArrayList<>();

        // Si aucun fichier n'a été trouvé dans le dossier, on ajout les fichiers par défaut
        if (files.size() == 0) {
            addAssetsFiles(directory);
        }

        // Lit chaque fichier
        for (File file : files) {
            // Lit le contenu du fichier
            try {
                try (FileInputStream fis = new FileInputStream(file)) {
                    quiz.add(QuizDecoder.decodeQuiz(fis));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return quiz;
    }

    private void addAssetsFiles(File directory) {
        // On ajoute les fichiers par défaut qui sont dans les ressources
        List<String> assetsFiles = new ArrayList<>(Arrays.asList("qcm01", "qcm02", "qcm03"));
        assetsFiles.forEach((assetFile) -> {
            // On copie le fichier dans le dossier
            try {
                InputStream inputStream = getAssets().open(assetFile + ".txt");
                File file = new File(directory, assetFile + ".txt");
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    // Si le fichier n'a pas pu être créé, on affiche un message d'erreur
                    Toast.makeText(this, R.string.error_create_file, Toast.LENGTH_LONG).show();
                    // On quitte l'application
                    finish();
                }
                // On copie le fichier
                FileOutputStream fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                BufferedWriter bw = new BufferedWriter(osw);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        bw.write(line);
                        bw.newLine();
                    }
                }
                inputStream.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        });
    }

    private void displayAvailableQuiz() {
        // On récupère la ListView
        ListView quizListView = findViewById(R.id.availableQuizListView);
        // On crée l'adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        // On ajoute les quiz à l'adapter
        listQuestionnaire.getStringQuiz().forEach((quiz) -> {
            if (listQuestionnaire.isAlreadyPlayed(listQuestionnaire.getStringQuiz().indexOf(quiz))) {
                quiz += " (déjà joué)";
            }
            adapter.add(quiz);
        });
        // On l'associe à la ListView
        quizListView.setAdapter(adapter);
        // Ajout d'un listener sur la ListView
        quizListView.setOnItemClickListener((parent, view, position, id) -> {
            // On récupère le quiz sélectionné
            Questionnaire questionnaire = listQuestionnaire.getQuiz(position);
            // On vérifie si le quiz a déjà été joué
            if (listQuestionnaire.isAlreadyPlayed(position)) {
                // Si le quiz a déjà été joué, on affiche un message d'erreur
                Toast.makeText(this, R.string.quiz_already_played, Toast.LENGTH_SHORT).show();
                return;
            }
            // On crée l'activité AnswerActivity
            Intent intent = new Intent(this, AnswerActivity.class);
            // On passe le quiz à l'activité
            intent.putExtra(QUIZ, questionnaire);
            // On lance l'activité
            answerLauncher.launch(intent);
        });
    }

    public void onSeeScoresButtonClick() {
        // On crée l'activité ScoreActivity
        Intent intent = new Intent(this, ScoreActivity.class);
        // On passe les scores à l'activité
        intent.putExtra(SCORES, listQuestionnaire.getListScores());
        // On passe la moyenne à l'activité
        intent.putExtra(MOYENNE, listQuestionnaire.getMoyenne());
        // On lance l'activité
        startActivity(intent);
    }

    public void onResetScoresButtonClick() {
        // On reset les scores
        listQuestionnaire.resetScores();
        // On récupère les données persistantes
        SharedPreferences shared = getSharedPreferences(SCORES, MODE_PRIVATE);
        // On récupère l'éditeur
        SharedPreferences.Editor editor = shared.edit();
        // On supprime les scores
        editor.clear();
        // On applique les modifications
        editor.apply();
        // On met à jour la ListView
        displayAvailableQuiz();
        // On affiche un message
        Toast.makeText(this, R.string.scores_reset, Toast.LENGTH_SHORT).show();
    }

    public void onCreateQuizButtonClick() {
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
                createQuizLauncher.launch(intent);
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

    public void onQuitAppButtonClick(boolean closeApp) {
        // On sauvegarde les scores dans les données persistantes
        // On récupère les données persistantes
        SharedPreferences shared = getSharedPreferences(SCORES, MODE_PRIVATE);
        // On récupère l'éditeur
        SharedPreferences.Editor editor = shared.edit();
        // On sauvegarde les scores
        listQuestionnaire.getScores().keySet().forEach(category -> editor.putInt(category, listQuestionnaire.getScore(category)));
        // On applique les modifications
        editor.apply();
        // On quitte l'application si closeApp est vrai
        if (closeApp) {
            finish();
        }
    }
}