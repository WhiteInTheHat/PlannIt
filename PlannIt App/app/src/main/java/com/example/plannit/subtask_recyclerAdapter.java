package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class subtask_recyclerAdapter extends RecyclerView.Adapter<subtask_recyclerAdapter.MyViewHolder> {

    private ArrayList<Subtask> subtasks;
    private Context context;
    private Task task;
    private ArrayList<Task> allTasks;

    public subtask_recyclerAdapter(Context context, Task task) {
        this.context = context;
        this.subtasks = new ArrayList<>();
        this.task=task;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.subtask_layout, parent, false);

        allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.content.setText(subtasks.get(position).getContent());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(subtasks.get(position).getContent());
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            int position1 = holder.getAdapterPosition();
            if (isChecked) {
                removeItem(position1);

            }
        });

    }

    public void removeItem(int position) {
        subtasks.remove(position);
        notifyItemRemoved(position);
        task.setSubtasks(subtasks);

        int i = 0;
        for (Task task1: allTasks) {
            if (task1.getID()==task.getID()) {
                break;
            }
            i++;
        }

        allTasks.remove(i);
        allTasks.add(i, task);

        xmlUtils.replaceTasks(context, allTasks);
    }


    @Override
    public int getItemCount() {
        return subtasks.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        CheckBox checkBox;
        ImageView arrow;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            content = itemView.findViewById(R.id.subtaskContent);
            checkBox = itemView.findViewById(R.id.subtaskCheckbox);
            arrow = itemView.findViewById(R.id.subtaskArrow);
        }
    }
}