package com.example.plannit;

import static com.example.plannit.CalendarUtils.daysInMonthArray;
import static com.example.plannit.CalendarUtils.monthYearFromDate;
import static com.example.plannit.CalendarUtils.tasksInMonthArray;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.time.LocalDate;
import java.util.ArrayList;




public class CalendarFragment extends Fragment implements CalendarAdapter.OnItemListener{


    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private View view;
    private Button switchToWeeklyView;
    private Button nextMonthView;
    private Button previousMonthView;
    private ImageButton scheduleView;
    public static ArrayList<Task> tasks = new ArrayList<>();





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_calendar,container,false);


        switchToWeeklyView = view.findViewById(R.id.weeklyViewButton);
        switchToWeeklyView.setOnClickListener(this::weeklyAction);
        nextMonthView = view.findViewById(R.id.nextMonthButton);
        nextMonthView.setOnClickListener(this::nextMonthAction);
        previousMonthView = view.findViewById(R.id.previousMonthButton);
        previousMonthView.setOnClickListener(this::previousMonthAction);
        scheduleView = view.findViewById(R.id.scheduleButton);
        scheduleView.setOnClickListener(this::planniAction);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initWidgets();
        tasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(getContext()));

        CalendarUtils.selectedDate = LocalDate.now();
        setMonthView();


    }

    private void initWidgets(){
        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        monthYearText = view.findViewById(R.id.monthYearTV);

    }

    private void setMonthView() {
        monthYearText.setText(monthYearFromDate(CalendarUtils.selectedDate));
        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalendarUtils.selectedDate);
        ArrayList<Integer> numberOfTasks = tasksInMonthArray(CalendarUtils.selectedDate);

        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, numberOfTasks, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        setMonthView();
    }

    public void previousMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.minusMonths(1);
        setMonthView();
    }

    public void nextMonthAction(View view) {
        CalendarUtils.selectedDate = CalendarUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null){
            if (CalendarUtils.selectedDate.isEqual(date)){
                startActivity(new Intent(getContext(),DailyCalendarActivity.class));
                Log.d("DayView At", String.valueOf(date));// Switch to day view once made
            }
            else{
                CalendarUtils.selectedDate = date;
                Log.d("Selected Date", String.valueOf(date));
                setMonthView();
            }

        }
    }

    public void weeklyAction(View view) {
        startActivity(new Intent(getContext(),WeekViewActivity.class));
    }

    public void planniAction(View view) {
        startActivity(new Intent(getContext(), ScheduleActivity.class));
    }


}