package com.example.plannit;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plannit.CalendarUtils;
import com.example.plannit.CalendarViewHolder;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private final ArrayList<LocalDate> days;
    private final ArrayList<Integer> numberOfTasksArray;
    private final OnItemListener onItemListener;

    public CalendarAdapter(ArrayList<LocalDate> days, ArrayList<Integer> numberOfTasksArray, OnItemListener onItemListener) {
        this.days = days;
        this.numberOfTasksArray = numberOfTasksArray;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calendar_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if(days.size()>8){
            layoutParams.height = (int) (parent.getHeight()/6);

        } else if (days.size()==7) {
            layoutParams.height = (int)(parent.getHeight());
        }
        return new CalendarViewHolder(view, onItemListener,days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        final LocalDate date = days.get(position);
        if (date==null){
            holder.dayOfMonth.setText("");
            holder.tasksOnDay.setText("");
        }
        else{
            if (date.getMonth().equals(CalendarUtils.selectedDate.getMonth())){
                holder.dayOfMonth.setTextColor(Color.BLACK);
                holder.tasksOnDay.setTextColor(Color.BLACK);
            }
            else{
                holder.dayOfMonth.setTextColor(Color.LTGRAY);
                holder.tasksOnDay.setTextColor(Color.LTGRAY);
            }
            if (date.equals(CalendarUtils.selectedDate)){
                holder.parentView.setBackgroundColor(Color.DKGRAY);
                holder.dayOfMonth.setTextColor(Color.WHITE);
                holder.tasksOnDay.setTextColor(Color.WHITE);
            }
            if (date.equals(LocalDate.now())){
                holder.dayOfMonth.setTextColor(Color.RED);
                holder.dayOfMonth.setTypeface(null, Typeface.BOLD);
                holder.tasksOnDay.setTextColor(Color.RED);
                holder.tasksOnDay.setTypeface(null, Typeface.BOLD);
            }
            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
            holder.tasksOnDay.setText("●".repeat(numberOfTasksArray.get(position)));
        }

    }

    @Override
    public int getItemCount() {
        return days.size();
    }
    public interface OnItemListener{
        void onItemClick(int position, LocalDate date);

    }
}
