package com.example.plannit;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
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

public class TaskFragment extends Fragment implements addTaskFragment.taskAdded, task_recyclerAdapter.taskInterface, taskRecyclerViewInterface {

    //Attributes:
    private Context context;
    private ArrayList<Task> theseTasks, allTasks;
    private ArrayList<Note> notes = new ArrayList<>();
    private FloatingActionButton addTaskBtn;
    private RecyclerView recyclerView;
    private task_recyclerAdapter adapter;
    private View rootView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private String title;

    public TaskFragment(String title, ArrayList<Task> specificTasks, Context context){
        this.theseTasks =specificTasks;
        this.title  = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context=getContext();
        try {
            this.allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("onCreate");
        xmlUtils.loadNotes(context, notes);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_tasks, container, false);
        System.out.println("onCreateView");
        addTaskBtn = rootView.findViewById(R.id.addTaskBtn3);
        addTaskBtn.setOnClickListener(this::onClick);

        recyclerView = rootView.findViewById(R.id.taskRecyclerView);
        adapter = new task_recyclerAdapter(context, theseTasks, this);
        adapter.setTaskInterfaceListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));

        TextView folderTitle = rootView.findViewById(R.id.lblSpecificTasks);
        folderTitle.setText(title);

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

    //On Click Methods:

    public void onClick(View view) {
        if(view.getId() == R.id.addTaskBtn3) {
            replaceFragment(new addTaskFragment(context));
        }
    }

    @Override
    public void onItemClick(int position) {
        showSubtasks(theseTasks.get(position), context);
        System.out.println("pressed");
    }

    //Other Methods:

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

                    String splitSelectedValue = parent.getItemAtPosition(position).toString();
                    lblSplits.setText(splitSelectedValue);
                    lblSplits.bringToFront();
                    Integer intSplit = Integer.parseInt(splitSelectedValue);
                    task.setSplits(intSplit);
                    System.out.println(intSplit);
                    task.displayInfo();

                    for (Task globalTask : allTasks) {
                        if (task.getID() == globalTask.getID()) {
                            globalTask.setSplits(intSplit);
                        }
                    }

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
                    if (task.getSplits() == -1) {
                        task.setSplits(1);
                    }
                    System.out.println("if");
                    System.out.println("Splits: " + task.getSplits());
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
        bundle.putSerializable("task", theseTasks.get(position));
        bundle.putSerializable("taskPos", position);
        Fragment frag = new addTaskFragment(getContext(), bundle);
        frag.setArguments(bundle);
        replaceFragment(frag);
    }

    @Override
    public void deleteTask(int position){
        Task thisTask = theseTasks.get(position);
        theseTasks.remove(position);
        for (Task task: allTasks) {
            if (task.getID() == thisTask.getID()) {
                thisTask=task;
            }
        }
        allTasks.remove(thisTask);
        xmlUtils.replaceTasks(context,allTasks);
        replaceFragment(new TaskFragment(title, theseTasks, context));
    }

    public void newTaskAdded() {

        TextView lblTasks = rootView.findViewById(R.id.lblToday);
        lblTasks.setVisibility(View.VISIBLE);
        RecyclerView recycler = rootView.findViewById(R.id.recyclerView);
        recycler.setVisibility(View.VISIBLE);
        addTaskBtn.setVisibility(View.VISIBLE);

        try {
            allTasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(context));
        } catch (Exception e) {
            e.printStackTrace();
        }

        theseTasks.clear();
        for (Task task : allTasks) {
            for (String label : task.getLabels()) {
                if (label.equals(title)) {
                    theseTasks.add(task);
                }
            }
        }

        adapter = new task_recyclerAdapter(context, theseTasks, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }
}