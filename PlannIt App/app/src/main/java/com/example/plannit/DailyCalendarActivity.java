package com.example.plannit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class DailyCalendarActivity extends AppCompatActivity {

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        initWidgets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDayView();
    }

    private void setDayView() {
        monthDayText.setText(CalendarUtils.monthDayFromDate(CalendarUtils.selectedDate));
        String dayOfWeek = CalendarUtils.selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
    }

    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(),hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    private ArrayList<HourTask> hourEventList() {
        ArrayList<HourTask> list = new ArrayList<>();
        for (int hour = 0;hour < 24;hour++){
            LocalTime time = LocalTime.of(hour,0);
            ArrayList<Task> tasks = CalendarUtils.tasksForDateAndTime(CalendarUtils.selectedDate, time);
            HourTask hourTask = new HourTask(time, tasks);
            list.add(hourTask);
        }
        return list;
    }

    private void initWidgets(){
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);
        monthDayText = findViewById(R.id.monthDayText);
        hourListView = findViewById(R.id.hourListView);
    }

    public void previousDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusDays(1);
        setDayView();
    }

    public void nextDayAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusDays(1);
        setDayView();
    }

    public void backAction(View view) {
        finish();
    }

    public void newTaskAction(View view) {
        Toast.makeText(getApplicationContext(), "Open New Task", Toast.LENGTH_SHORT).show();
    }



}