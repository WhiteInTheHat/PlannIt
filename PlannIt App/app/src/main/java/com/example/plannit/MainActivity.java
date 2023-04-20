package com.example.plannit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.plannit.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;


public class MainActivity extends AppCompatActivity {

    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText noteTitleEditText;
    private Button confirmationButton, cancelationButton;
    private ArrayList<String> labels;
    private String lastUsed, today;
    private int streak;

    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences sharedPreferences = getSharedPreferences("LabelPrefs", Context.MODE_PRIVATE);
        HashSet<String> mySet = (HashSet<String>) sharedPreferences.getStringSet("labelSet", new HashSet<>());
        labels = new ArrayList<>(mySet);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        today = dateFormat.format(currentDate);

        SharedPreferences dateSharedPreferences = getSharedPreferences("datePrefs", Context.MODE_PRIVATE);

        lastUsed = dateSharedPreferences.getString("str_date", "01/01/0000");
        streak = dateSharedPreferences.getInt("streak", 1);

        if (lastUsed.equals("01/01/0000")) {
            lastUsed = today;
        }

        System.out.println(streak);
        calculateStreak();
        System.out.println(streak);

        clearOldPlanniTasks();

        SharedPreferences.Editor editor = dateSharedPreferences.edit();
        editor.putString("str_date", today);
        editor.putInt("streak", streak);
        editor.apply();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView;
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.navigation_today);
        replaceFragment(new TodayFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()) {

                case R.id.navigation_tasks:
                    replaceFragment(new TaskFolderFragment());
                    break;
                case R.id.navigation_calendar:
                    replaceFragment(new CalendarFragment());
                    break;
                case R.id.navigation_today:
                    replaceFragment(new TodayFragment());
                    break;
                case R.id.navigation_notes:
                    replaceFragment(new NoteFragment());
                    break;
                case R.id.navigation_plannit:
                    replaceFragment(new PlannitFragment());
                    break;


            }


            return true;

        });
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }

    public void addNotePopup(View view){
        // Sets up the dialog box
        dialogBuilder = new AlertDialog.Builder(view.getContext());
        final View addDialogView = getLayoutInflater().inflate(R.layout.note_add_note_popup,null);
        noteTitleEditText = addDialogView.findViewById(R.id.addNoteTitle);
        confirmationButton = addDialogView.findViewById(R.id.confirmationButton);
        cancelationButton = addDialogView.findViewById(R.id.cancelationButton);

        dialogBuilder.setView(addDialogView);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noteTitle = noteTitleEditText.getText().toString();
                FragmentManager fm = getSupportFragmentManager();
                NoteFragment fragment = (NoteFragment) fm.findFragmentById(R.id.frameLayout);


                fragment.createNote(noteTitle);
                dialog.dismiss();
            }
        });

        cancelationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }



    private void clearOldPlanniTasks() {
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(this));

        ArrayList<Task> newAllTasks = new ArrayList<>();
        newAllTasks.addAll(allTasks);
        for (Task task: allTasks) {
            if (task.isPlanniTask() && !task.getDueDate().equals(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()))) {
                System.out.println("An old task was found: " + task.getName());
                newAllTasks.remove(task);
            }
        }
        allTasks = newAllTasks;
        xmlUtils.replaceTasks(this,allTasks);
    }

    public int calculateDaysSinceUsed() {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate date1 = LocalDate.parse(lastUsed, formatter);
        //LocalDate date1 = LocalDate.parse("25/04/2023", formatter);
        LocalDate date2 = LocalDate.parse(today, formatter);
        //LocalDate date2 = LocalDate.parse("27/04/2023", formatter);

        long daysBetween = ChronoUnit.DAYS.between(date1, date2);

        return (int) daysBetween;
    }

    public void calculateStreak() {

        if (calculateDaysSinceUsed() == 1) {
            streak++;
            System.out.println("if");
        } else if (calculateDaysSinceUsed() == 0) {
            //doNothing
            System.out.println("elif");
        } else {
            streak=1;
            System.out.println("else");
        }
    }

    public ArrayList<String> getPrefs(){
        return this.labels;
    }

    public String getLastUsed() {return this.lastUsed;}

    public int getStreak() {return this.streak;}

}