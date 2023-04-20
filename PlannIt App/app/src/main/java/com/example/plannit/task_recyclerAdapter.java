package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class task_recyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<taskRecyclerEntity> taskRecyclerEntities;
    private ArrayList<Task> theseTasks, allTasks;
    private Context context;
    private taskInterface listener;
    private taskRecyclerViewInterface taskRecyclerViewInterface;
    private View planniButton;


    public ArrayList<taskRecyclerEntity> tasksToEntities(ArrayList<Task> tasks){
        ArrayList<taskRecyclerEntity> recyclerEntities = new ArrayList<>();
        for(Task task: tasks) {
            recyclerEntities.add(new taskRecyclerEntity(task, false));
        }
        return recyclerEntities;
    }

    private final int SHOW_MENU = 1;
    private final int HIDE_MENU = 2;

    public task_recyclerAdapter(Context context, ArrayList<Task> tasks, taskRecyclerViewInterface taskRecyclerViewInterface, View planniButton) {
        this.context = context;
        this.theseTasks = tasks;
        this.taskRecyclerEntities = tasksToEntities(tasks);
        this.taskRecyclerViewInterface = taskRecyclerViewInterface;
        this.allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));
        this.planniButton = planniButton;

    }
    public task_recyclerAdapter(Context context, ArrayList<Task> tasks, taskRecyclerViewInterface taskRecyclerViewInterface) {
        this.context = context;
        this.theseTasks = tasks;
        this.taskRecyclerEntities = tasksToEntities(tasks);
        this.taskRecyclerViewInterface = taskRecyclerViewInterface;
        this.allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView taskName, taskDate;
        CheckBox taskCheckBox;

        public MyViewHolder(@NonNull View itemView, taskRecyclerViewInterface recyclerViewInterface) {

            super(itemView);
            taskName = itemView.findViewById(R.id.taskName);
            taskDate = itemView.findViewById(R.id.taskDue);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                recyclerViewInterface.onItemClick(pos);
            });
        }
    }

    public interface taskInterface {
        void editTask(int position);
        void deleteTask(int position);
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        ImageButton editButton, deleteButton;
        int pos;

        public MenuViewHolder(@NonNull View itemView, taskRecyclerViewInterface taskRecyclerViewInterface) {
            super(itemView);

            rootView = itemView;
            editButton = rootView.findViewById(R.id.editButton);
            deleteButton=rootView.findViewById(R.id.deleteButton);

            deleteButton.setOnClickListener(view -> {
                pos = getAdapterPosition();
                removeItem(theseTasks, pos);
                notifyDataSetChanged();
            });
        }
    }

    @Override
    public int getItemViewType(int position) {

        try {
            if (taskRecyclerEntities.get(position).isShowMenu()) {
                return SHOW_MENU;
            } else {
                return HIDE_MENU;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HIDE_MENU;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==SHOW_MENU) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.task_layout_menu, parent, false);
            return new MenuViewHolder(view, taskRecyclerViewInterface);
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.task_layout, parent, false);
            return new MyViewHolder(view, taskRecyclerViewInterface);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        System.out.println(holder);
        try {
            if (holder instanceof MyViewHolder) {
                System.out.println("inMyView");
                Date date = taskRecyclerEntities.get(position).getTask().getDueDate();
                DateFormat format = DateFormat.getDateInstance();
                String strDate = format.format(date);

                ((MyViewHolder) holder).taskName.setText(taskRecyclerEntities.get(position).getTask().getName());
                ((MyViewHolder) holder).taskDate.setText(strDate);
                ((MyViewHolder) holder).taskCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                    int position1 = holder.getAdapterPosition();

                    if (isChecked) {

                        for (Task task: allTasks) {
                            System.out.println("This is a task inside All Tasks: " + task.getName());
                        }
                        for (Task task: theseTasks) {
                            System.out.println("This is a task inside These Tasks: " + task.getName());
                        }
                        System.out.println("This is the Size of Adapter: " + getItemCount());
                        // TODO CHECK FOR PLANNI TASK
                        // USE PARENT ID TO FIND PARENT TASK
                        // DECREMENT SPLITS AND DECREMENT DURATION BY DURATION/OLDSPLITS
                        Task currentTask = theseTasks.get(position1);
                        ArrayList<Task> newAllTasks = new ArrayList<>();
                        if (currentTask.isPlanniTask()) {
                            newAllTasks.addAll(allTasks);
                            for (Task parentTask: allTasks) {
                                System.out.println("Parent Task: " + parentTask.getID());
                                System.out.println("Child Task: " + currentTask.getParentID());
                                if (parentTask.getID() == currentTask.getParentID()) {
                                    System.out.println(" WE MADE IT ");
                                    parentTask.setDuration(currentTask.getDuration());
                                    parentTask.setSplits(parentTask.getSplits()-1);
                                    System.out.println(parentTask.getDuration());
                                    System.out.println(parentTask.getSplits());

                                    int b = 0;
                                    for (Task task: newAllTasks) {

                                        if (task.getID() == currentTask.getID()) {
                                            break;
                                        }
                                        b += 1;


                                    }

                                    newAllTasks.remove(b);
                                    //notifyItemRemoved(position1);
                                    for (Task task: newAllTasks) {
                                        System.out.println("This happens after removing planni task: " + task.getName());
                                    }
                                    xmlUtils.replaceTasks(context,newAllTasks);
                                    if (parentTask.getSplits() == 0) {
                                        int i = -1;
                                        int p = newAllTasks.indexOf(parentTask);
                                        newAllTasks.remove(parentTask);
                                        xmlUtils.replaceTasks(context,newAllTasks);
                                        for (int c = 0; c < theseTasks.size(); c++) {

                                            Task task = theseTasks.get(c);

                                            System.out.println(task.getName());

                                            if (task.getID() == parentTask.getID()) {
                                                i = c;
                                                break;
                                            }
                                        }

                                        if (i != -1) {
                                            removeItem(theseTasks,i);
                                        }

                                    }
                                    break;
                                }

                            }
                            allTasks = newAllTasks;
                            for (Task task: theseTasks) {
                                System.out.println("This is a task inside These Tasks: BEFORE " + task.getName());
                                System.out.println("Task Splits: " + task.getName() + " " + task.getSplits());
                            }
                            removeItem(theseTasks,theseTasks.indexOf(currentTask));
                        }
                        else {
                            System.out.println("We have entered the Else Statement!");
                            removeItem(theseTasks, position1);
                            allTasks.remove(position1);
                        }
                        xmlUtils.replaceTasks(context,allTasks);




                        for (Task task: allTasks) {
                            System.out.println("This is a task inside All Tasks: AFTER " + task.getName());
                            System.out.println("Task Splits: " + task.getName() + " " + task.getSplits());
                        }
                        for (Task task: theseTasks) {
                            System.out.println("This is a task inside These Tasks: AFTER " + task.getName());
                            System.out.println("Task Splits: " + task.getName() + " " + task.getSplits());
                        }

                    }
                });
            }
            else if (holder instanceof MenuViewHolder) {
                try {
                    planniButton.setVisibility(View.INVISIBLE);
                } catch (Exception e) {}
                System.out.println("inMenuView");
                int position1 = holder.getAdapterPosition();
                System.out.println(position1);
                System.out.println(listener);
                for (Task task: allTasks) {
                    System.out.println("This is a task inside All Tasks: " + task.getName());
                }
                for (Task task: theseTasks) {
                    System.out.println("This is a task inside These Tasks: " + task.getName());
                }
                ((MenuViewHolder) holder).editButton.setOnClickListener(view -> listener.editTask(position1));
                ((MenuViewHolder) holder).deleteButton.setOnClickListener(view -> listener.deleteTask(position1));

            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

    public void setTaskInterfaceListener(taskInterface listener) {
        System.out.println("set listener");
        this.listener = listener;
    }

    public void removeItem(ArrayList<Task> sentTasks, int position) {
        System.out.println("Tasks2:");
        for (Task task: sentTasks){
            System.out.println("Tasks in the array sent: " + task.getName());
        }
        System.out.println("Tasks3:");
        for (Task task: theseTasks) {
            System.out.println("Tasks in the theseTasks array BEFORE: " + task.getName());
        }
        theseTasks.remove(position);
        for (Task task: theseTasks) {
            System.out.println("Tasks in the theseTasks array AFTER: " + task.getName());
        }
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return theseTasks.size();
    }

    public void showMenu(int position) {
        for(int i=0; i<taskRecyclerEntities.size(); i++){
            taskRecyclerEntities.get(i).setShowMenu(false);
        }
        taskRecyclerEntities.get(position).setShowMenu(true);
        notifyDataSetChanged();
    }

    public void closeMenu() {
        for(int i=0; i<taskRecyclerEntities.size(); i++){
            taskRecyclerEntities.get(i).setShowMenu(false);
        }
        notifyDataSetChanged();
    }

}
