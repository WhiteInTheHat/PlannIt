package com.example.plannit;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plannit.CalendarAdapter;

import java.time.LocalDate;
import java.util.ArrayList;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public final View parentView;
    public final TextView dayOfMonth;
    public final TextView tasksOnDay;
    private final ArrayList<LocalDate> days;

    private final CalendarAdapter.OnItemListener onItemListener;
    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener, ArrayList<LocalDate> days) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.taskDayText);
        tasksOnDay = itemView.findViewById(R.id.cellTaskCount);
        parentView = itemView.findViewById(R.id.parentView);
        this.onItemListener = onItemListener;
        this.days = days;
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemListener.onItemClick(getAdapterPosition(),days.get(getAdapterPosition()));
    }
}
