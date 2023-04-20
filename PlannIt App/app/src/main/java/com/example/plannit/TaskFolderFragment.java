package com.example.plannit;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;

public class TaskFolderFragment extends Fragment implements taskRecyclerViewInterface{

    //Attributes:

    private View rootView;
    private ArrayList<Task> tasks = new ArrayList<>();
    private ArrayList<String> allLabels = new ArrayList<>();
    private ArrayList<String> folders = new ArrayList<>();
    private RecyclerView recyclerView;
    private folder_recyclerAdapter adapter;
    private FloatingActionButton addTaskBtn;
    private Context context;

    //Methods:

    //Constructors:

    TaskFolderFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        try {
            tasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));
            MainActivity activity = (MainActivity) getActivity();
            allLabels = activity.getPrefs();

            HashSet<String> setWithoutDuplicates = new HashSet<>(allLabels);
            folders = new ArrayList<>(setWithoutDuplicates);
            folders.add(0, "All Tasks");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.fragment_task_folder, container, false);
        addTaskBtn = rootView.findViewById(R.id.addTaskBtn2);
        addTaskBtn.setOnClickListener(this::onClick);

        recyclerView = rootView.findViewById(R.id.taskFolderRecycler);
        adapter = new folder_recyclerAdapter(folders, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        return rootView;
    }

    //On Click Methods:

    public void onClick(View view) {
        if(view.getId() == R.id.addTaskBtn2) {
            replaceFragment(new addTaskFragment(context));
        }
    }

    //Other Methods:

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }

    public void onItemClick(int position) {
        String label = (folders.get(position));
        ArrayList<Task> specificTasks = new ArrayList<>();
        if(label.equals("All Tasks")){
            System.out.println(tasks);
            replaceFragment(new TaskFragment(label, tasks, getContext()));
        } else {
            for (Task task : tasks) {
                for (String label2 : task.getLabels()) {
                    if (label.equals(label2)) {
                        specificTasks.add(task);
                    }
                }
            }
            replaceFragment(new TaskFragment(label, specificTasks, getContext()));
        }
    }
}
