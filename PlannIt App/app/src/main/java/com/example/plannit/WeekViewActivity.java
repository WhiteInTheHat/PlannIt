package com.example.plannit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener{
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView taskListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        initWidgets();
        setWeekView();
    }
    private void initWidgets(){
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
        taskListView = findViewById(R.id.taskListView);
    }

    private void setWeekView() {
        monthYearText.setText(CalendarUtils.monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> days = CalendarUtils.daysInWeekArray(CalendarUtils.selectedDate);
        ArrayList<Integer> taskNumbers = CalendarUtils.tasksInWeekArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(days,taskNumbers,this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
        setTaskAdapter();

    }


    public void nextWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    public void previousWeekAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void newTaskAction(View view) {
        Toast.makeText(getApplicationContext(), "Open New Task", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (CalendarUtils.selectedDate.isEqual(date)){
            setWeekView();
            startActivity(new Intent(this, DailyCalendarActivity.class));
            Log.d("DayView At", String.valueOf(date));// Switch to day view once made
        }
        else {
            CalendarUtils.selectedDate = date;
            Log.d("Selected Date", String.valueOf(date));
            setWeekView();
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        setTaskAdapter();
        setWeekView();
    }

    private void setTaskAdapter() {
        ArrayList<Task> taskTasks = CalendarUtils.tasksForDate(CalendarUtils.selectedDate);
        System.out.println(taskTasks);
        TaskArrayAdapter taskAdapter = new TaskArrayAdapter(getApplicationContext(),taskTasks);
        taskListView.setAdapter(taskAdapter);;
    }

    public void backAction(View view) {
        finish();
    }

}