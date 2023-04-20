package com.example.plannit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TodayFragment extends Fragment implements addTaskFragment.taskAdded, task_recyclerAdapter.taskInterface, taskRecyclerViewInterface{

    //Attributes:
    private Context context;
    private int a = 0;
    private ArrayList<Task> allTasks, todayTasks;
    private FloatingActionButton addTaskBtn;
    private RecyclerView recyclerView;
    private task_recyclerAdapter adapter;
    private View rootView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private ArrayList<Schedule> allSchedules;
    private FloatingActionButton planniBtn;


    public TodayFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        try {
            todayTasks = new ArrayList<>();
            allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date();
            String formattedDate = dateFormat.format(currentDate);
            for (Task task: allTasks){
                String taskFormattedDate = dateFormat.format(task.getDueDate());
                if (taskFormattedDate.equals(formattedDate)) {
                    todayTasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_today, container, false);
        addTaskBtn = rootView.findViewById(R.id.addTaskBtn);
        addTaskBtn.setOnClickListener(this::onClick);
        planniBtn = rootView.findViewById(R.id.planniButton);
        planniBtn.setOnClickListener(this::planniAlgorithm);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        adapter = new task_recyclerAdapter(context, todayTasks, this, rootView.findViewById(R.id.planniButton));
        adapter.setTaskInterfaceListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            private final Drawable background = ContextCompat.getDrawable(getContext(), R.drawable.cosmicbackground4);

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.showMenu(viewHolder.getAdapterPosition());
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> adapter.closeMenu());

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return rootView;
    }


    // Planni Section
    // Create a new temporary task that expires once the day is over add it to the calendar, to Today. This should be a completely functional task.
    // This task must be in a unique colour, different from tasks created by the user.
    // Set time to work on, and duration, should be seen in the calendar but NOT the TODAY section
    public void planniAlgorithm(View view) {

        System.out.println("Size of Adapter: " + adapter.getItemCount());
        // Loads all schedules
        allSchedules = xmlUtils.loadSchedules(getContext());
        allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));


        // Loads all tasks
        ArrayList<Task> newPlanniTasks = Planni.setUp(allSchedules,allTasks, context);

        for (Task task:newPlanniTasks) {
            System.out.println("PLANNI TASKS: " + task.getName());
        }
        for (Task task : newPlanniTasks) {
            try {
                xmlUtils.singleWriteTaskXML(task,context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));

        todayTasks.clear();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);
        for (Task task: allTasks){
            String taskFormattedDate = dateFormat.format(task.getDueDate());
            if (taskFormattedDate.equals(formattedDate)) {
                todayTasks.add(task);
            }
        }

        adapter = new task_recyclerAdapter(context, todayTasks, this, rootView.findViewById(R.id.planniButton));
        adapter.setTaskInterfaceListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));




        System.out.println("Size of Adapter: " + adapter.getItemCount());


    }

    //On Click Methods:

    public void onClick(View view) {
        if(view.getId() == R.id.addTaskBtn) {
            replaceFragment(new addTaskFragment(context));
        }
    }

    @Override
    public void onItemClick(int position) {
        allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));
        try {
            todayTasks = new ArrayList<>();
            allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date currentDate = new Date();
            String formattedDate = dateFormat.format(currentDate);
            for (Task task: allTasks){
                String taskFormattedDate = dateFormat.format(task.getDueDate());
                if (taskFormattedDate.equals(formattedDate)) {
                    todayTasks.add(task);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        showSubtasks(todayTasks.get(position), context);
        System.out.println("pressed");
    }



    //Other Methods:

    public void newTaskAdded() {

        TextView lblTasks = rootView.findViewById(R.id.lblToday);
        lblTasks.setVisibility(View.VISIBLE);
        RecyclerView recycler = rootView.findViewById(R.id.recyclerView);
        recycler.setVisibility(View.VISIBLE);
        addTaskBtn.setVisibility(View.VISIBLE);

        allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));

        todayTasks.clear();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date currentDate = new Date();
        String formattedDate = dateFormat.format(currentDate);
        for (Task task: allTasks){
            String taskFormattedDate = dateFormat.format(task.getDueDate());
            if (taskFormattedDate.equals(formattedDate)) {
                todayTasks.add(task);
            }
        }

        adapter = new task_recyclerAdapter(context, todayTasks, this, rootView.findViewById(R.id.planniButton));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));


    }

    private void showSubtasks(Task task, Context context){

        if (task.isPlanniTask()) {
            //Don't open for planni tasks
            Toast.makeText(getContext(), "This is a planni task. It's info cannot be displayed.", Toast.LENGTH_SHORT).show();
        }
        else {
            // Sets up the dialog box
            dialogBuilder = new AlertDialog.Builder(context);
            final View showSubtasksView = getLayoutInflater().inflate(R.layout.task_display_info, null);
            TextView lblSplits = showSubtasksView.findViewById(R.id.lblSplits);
            TextView txtSplits = showSubtasksView.findViewById(R.id.txtSplits);
            Spinner splitSpinner = showSubtasksView.findViewById(R.id.splitSpinner);
            Switch mySwitch = showSubtasksView.findViewById(R.id.displaySwitch);

            String[] splitsArr = {"1", "2", "3", "4", "5", "6", "7", "8"};
            ArrayAdapter<String> splitSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, splitsArr);
            splitSpinner.setAdapter(splitSpinnerAdapter);
            splitSpinner.setSelection(task.getSplits() - 1);

            splitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override

                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    System.out.println("itemselkect");
                    String splitSelectedValue = parent.getItemAtPosition(position).toString();
                    lblSplits.setText(splitSelectedValue);
                    lblSplits.bringToFront();
                    Integer intSplit = Integer.parseInt(splitSelectedValue);
                    task.setSplits(intSplit);
                    System.out.println(intSplit);
                    task.displayInfo();
                    int currentTask = allTasks.indexOf(task);
                    for (Task globalTask : allTasks) {
                        if (task.getID() == globalTask.getID()) {
                            globalTask.setSplits(intSplit);
                        }
                    }
                    //allTasks.set(allTasks.indexOf(currentTask),task);

                    xmlUtils.replaceTasks(context, allTasks);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //|Do nothing
                }
            });

            if (!task.isPlanni()) {
                lblSplits.setVisibility(View.INVISIBLE);
                txtSplits.setVisibility(View.INVISIBLE);
                splitSpinner.setVisibility(View.INVISIBLE);
            } else {
                lblSplits.setVisibility(View.VISIBLE);
                txtSplits.setVisibility(View.VISIBLE);
                splitSpinner.setVisibility(View.VISIBLE);
                mySwitch.setChecked(true);
                lblSplits.setText(String.valueOf(task.getSplits()));
            }

            RecyclerView subtaskRecycler = showSubtasksView.findViewById(R.id.subtaskRecyclerView);
            subtask_recyclerAdapter subtaskRecyclerAdapter = new subtask_recyclerAdapter(getContext(), task);
            subtaskRecycler.setAdapter(subtaskRecyclerAdapter);
            subtaskRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            subtaskRecycler.addItemDecoration(new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL));

            RecyclerView noteRecycler = showSubtasksView.findViewById(R.id.noteRecyclerView);

            note_recyclerAdapter noteRecyclerAdapter = new note_recyclerAdapter(context, task, true);
            noteRecycler.setAdapter(noteRecyclerAdapter);
            noteRecycler.setLayoutManager(new LinearLayoutManager(context));
            noteRecycler.addItemDecoration(new DividerItemDecoration(context,
                    DividerItemDecoration.VERTICAL));

            TextView title = showSubtasksView.findViewById(R.id.displayTaskName);
            title.setText(task.getName());


            mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    task.planniOn();
                    System.out.println("if");
                    System.out.println("Splits: " + task.getSplits());
                    if (task.getSplits() == -1) {
                        task.setSplits(1);
                    }
                    lblSplits.setText("1");
                    lblSplits.setVisibility(View.VISIBLE);
                    txtSplits.setVisibility(View.VISIBLE);
                    splitSpinner.setVisibility(View.VISIBLE);
                    for (Task globalTask : allTasks) {
                        if (task.getID() == globalTask.getID()) {
                            globalTask.planniOn();
                        }
                    }
                    xmlUtils.replaceTasks(context, allTasks);
                    System.out.println("We passed it");
                } else {
                    System.out.println("else");
                    task.setSplits(-1);
                    task.planniOff();
                    lblSplits.setVisibility(View.INVISIBLE);
                    txtSplits.setVisibility(View.INVISIBLE);
                    splitSpinner.setVisibility(View.INVISIBLE);
                    task.displayInfo();
                    for (Task globalTask : allTasks) {
                        if (task.getID() == globalTask.getID()) {
                            globalTask.planniOff();
                        }
                    }
                    xmlUtils.replaceTasks(context, allTasks);
                }
            });

            dialogBuilder.setView(showSubtasksView);
            dialog = dialogBuilder.create();
            dialog.show();
        }
    }

    @Override
    public void editTask(int position) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", todayTasks.get(position));
        bundle.putSerializable("taskPos", position);
        Fragment frag = new addTaskFragment(getContext(), bundle);
        frag.setArguments(bundle);
        replaceFragment(frag);
    }

    @Override
    public void deleteTask(int position){
        Task task = todayTasks.get(position);
        allTasks.remove(task);
        xmlUtils.replaceTasks(context,allTasks);
        replaceFragment(new TodayFragment());
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }



    //TODO What does Charlie need to do?
        // 1. Charlie needs to implement the long term tasks
        // 2. Charlie needs to work on making Today and Tasks look better, try and make it look more like notes.
        // 3. Plannit Code
}