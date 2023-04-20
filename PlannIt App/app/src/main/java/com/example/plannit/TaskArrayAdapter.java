package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.List;

public class TaskArrayAdapter extends ArrayAdapter<Task> {
    public TaskArrayAdapter(@NonNull Context context, List<Task> tasks) {
        super(context, 0,tasks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Task task = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.task_cell,parent,false);
        }
        TextView taskCellTV = convertView.findViewById(R.id.taskCellTV);

        String taskTitle = task.getName() + " "+"TIMENOTDONE";
        //String taskTitle = task.getName() + " "+CalendarUtils.formattedTime(task.getTime()); NOTETOLUCAS: need to put time here

        taskCellTV.setText(taskTitle);
        return convertView;
    }


}
