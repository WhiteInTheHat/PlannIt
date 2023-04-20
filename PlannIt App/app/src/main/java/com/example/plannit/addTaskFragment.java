package com.example.plannit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

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
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class addTaskFragment extends Fragment {

    private Context mContext;
    private Task task, newTask;
    private taskAdded addTaskListener;
    private boolean isEdit;
    private int taskPos;
    private Integer splits = -1;
    private boolean isPlanni;
    private

    //Lists
    ArrayList<Task> tasks = new ArrayList<>();
    ArrayList<String> priorities = new ArrayList<>();
    ArrayList<String> currentTaskLabels = new ArrayList<>();
    ArrayList<String> prefLabels;
    ArrayList<Subtask> subtasks = new ArrayList<>();
    ArrayList<Note> notes = new ArrayList<>();
    ArrayList<Integer> noteIDs = new ArrayList<>();

    //Views
    View rootView;
    TextView name, inputSubtask, lblPriority, lblDuration;
    AutoCompleteTextView lblInput;
    Chip dateChip, timeChip;
    Spinner prioritySpinner, durationSpinner;
    Button addTaskButton;
    RecyclerView labelRecyclerView, subtaskRecyclerView, noteRecyclerView;
    label_recyclerAdapter labelRecyclerAdapter;
    subtask_recyclerAdapter subtaskRecyclerAdapter;
    note_recyclerAdapter noteRecyclerAdapter;
    Bundle taskBundle;


    //Constructors:

    public addTaskFragment(Context mContext){
        this.mContext = mContext;
        isEdit = false;
    }

    public addTaskFragment(Context mContext, Bundle bundle){
        System.out.println("created with bundle");
        this.mContext = mContext;
        this.taskBundle = bundle;
        task = (Task) taskBundle.getSerializable("task");
        taskPos = (int) taskBundle.getSerializable("taskPos");
        isEdit = true;
    }

    //Creation Methods:

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build());

        try {
            tasks = xmlUtils.loadTasks(xmlUtils.readTaskXML(mContext));
            MainActivity activity = (MainActivity) getActivity();
            prefLabels = activity.getPrefs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_add_task, container, false);

        try {
            priorities = xmlToPriorities(readPriorityXML());
        } catch (Exception e){e.printStackTrace();}
        prioritySpinner = rootView.findViewById(R.id.prioritySpinner);
        String[] arrPriorities = priorities.toArray(new String[0]);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, arrPriorities);

        prioritySpinner.setAdapter(spinnerAdapter);

        prioritySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = parent.getItemAtPosition(position).toString();
                TextView selectedOption = rootView.findViewById(R.id.lblPriority);
                selectedOption.setText(selectedValue);
                selectedOption.bringToFront();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        durationSpinner = rootView.findViewById(R.id.durationSpinner);
        ArrayList<String> times = generateTimes(0);
        ArrayAdapter<String> durationSpinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, times);
        durationSpinner.setAdapter(durationSpinnerAdapter);

        durationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String durationSelectedValue = parent.getItemAtPosition(position).toString();
                TextView durationSelectedOption = rootView.findViewById(R.id.lblDuration);
                durationSelectedOption.setText(durationSelectedValue);
                durationSelectedOption.bringToFront();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        addTaskButton = rootView.findViewById(R.id.completeAddTask);
        addTaskButton.setOnClickListener(v -> {
            try {
                addTaskClicked(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dateChip = rootView.findViewById(R.id.dateChip);
        dateChip.setOnClickListener(view -> {
            try {
                showDatePickerDialog(view);
            } catch (Exception e){e.printStackTrace();}
        });

        timeChip = rootView.findViewById(R.id.timeChip);
        timeChip.setOnClickListener(view -> {
            try {
                showTimePickerDialog(view);
            } catch (Exception e){e.printStackTrace();}
        });

        ImageButton addLabelButton = rootView.findViewById(R.id.addLabelButton);
        addLabelButton.setOnClickListener(v -> {
            try {
                addLabel(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ImageButton addSubtaskButton = rootView.findViewById(R.id.addSubtaskButton);
        addSubtaskButton.setOnClickListener(v -> {
            try {
                addSubtask(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button cancelTaskButton = rootView.findViewById(R.id.cancelAddTask);
        cancelTaskButton.setOnClickListener(v -> {
            try {
                cancelTaskClicked(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        lblInput = rootView.findViewById(R.id.lblInput);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_dropdown_item_1line, prefLabels);
        lblInput.setAdapter(adapter);



        if (isEdit){
            System.out.println("enetered");
            System.out.println(taskBundle);
            name = rootView.findViewById(R.id.txtName);
            name.setText(task.getName());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(task.getDueDate());
            dateChip.setText(strDate);
            timeChip.setText(task.getDueTime());
            lblPriority = rootView.findViewById(R.id.lblPriority);
            lblPriority.setText(String.valueOf(task.getPriority()));
            currentTaskLabels = task.getLabels();
            subtasks = task.getSubtasks();
            noteIDs = task.getNotes();

            for (Note note: notes){
                for (int id: noteIDs) {
                    if (note.getId()==id) {

                    }
                }
            }
            Double minsDuration = task.getDuration() * 60;
            int minutes = minsDuration.intValue();
            int hours = minutes / 60;
            int mins = minutes % 60;
            String str_duration = String.format("%d:%02d", hours, mins);

            String[] timeArr = str_duration.split(":");
            String startHours = timeArr[0];
            String startMinutes = timeArr[1];
            int startExtraAdd = 0;
            if (startMinutes.equals("15")) {
                startExtraAdd = 1;
            }
            else if (startMinutes.equals("30")) {
                startExtraAdd = 2;
            }
            else if (startMinutes.equals("45")) {
                startExtraAdd = 3;
            }
            int startTimeSpinnerPosition = (Integer.parseInt(startHours) * 4) + startExtraAdd;

            lblDuration = rootView.findViewById(R.id.lblDuration);
            lblDuration.setText(str_duration);
            lblDuration.bringToFront();
            splits=task.getSplits();
            isPlanni=task.isPlanni();

            prioritySpinner.setSelection(task.getPriority());
            durationSpinner.setSelection(startTimeSpinnerPosition);

        }

        labelRecyclerView = rootView.findViewById(R.id.labelRecycler);
        labelRecyclerAdapter = new label_recyclerAdapter(getContext(), currentTaskLabels);
        labelRecyclerView.setAdapter(labelRecyclerAdapter);
        labelRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        labelRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        subtaskRecyclerView = rootView.findViewById(R.id.subtaskRecycler);
        subtaskRecyclerAdapter = new subtask_recyclerAdapter(getContext(), task);
        subtaskRecyclerView.setAdapter(subtaskRecyclerAdapter);
        subtaskRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        subtaskRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        notes = noteIDstoNotes(noteIDs);

        noteRecyclerView = rootView.findViewById(R.id.noteRecycler);
        noteRecyclerAdapter = new note_recyclerAdapter(getContext(), task, false);
        noteRecyclerView.setAdapter(noteRecyclerAdapter);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        noteRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        return rootView;
    }

    //MultiClass Methods:

    public interface taskAdded {
        void newTaskAdded();
    }

    @Override
    public void onAttach(Context mContext) {
        super.onAttach(mContext);
        addTaskListener = (taskAdded) getParentFragment();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (addTaskListener !=null){
            addTaskListener.newTaskAdded();
        }
    }

    //On Click Methods:

    public void cancelTaskClicked(View view){
        tasks.clear();
        replaceFragment(new TodayFragment());
    }

    public void addTaskClicked(View view) {
        try {
            newTask = storeInfo();
            if (isEdit) {
                tasks.set(taskPos, task);
                xmlUtils.replaceTasks(getContext(),tasks);
            } else {
                tasks.add(task);
                xmlUtils.singleWriteTaskXML(task,getContext());
            }
            task.displayInfo();
            replaceFragment(new TodayFragment());
        } catch (Exception e) {
            Toast.makeText(getContext(), "Incorrect Input, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void addLabel(View view) {
        lblInput = rootView.findViewById(R.id.lblInput);
        String lblTxt = lblInput.getText().toString();
        if (!lblTxt.equals("")){
            currentTaskLabels.add(lblTxt);
            labelRecyclerAdapter.notifyItemInserted(currentTaskLabels.size()-1);
            lblInput.setText("");
        }
    }

    //Other Methods:

    public void addSubtask(View view) {
        inputSubtask = rootView.findViewById(R.id.inputSubtask);
        if (!inputSubtask.getText().toString().equals("")) {
            Subtask subtask = new Subtask(inputSubtask.getText().toString());
            subtasks.add(subtask);
            subtaskRecyclerAdapter.notifyItemInserted(subtasks.size()-1);
            inputSubtask.setText("");
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getChildFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getChildFragmentManager(), "timePicker");
    }

    private ArrayList<String> generateTimes(int start) {
        ArrayList<String> timeArray = new ArrayList<String>();
        for (int i = start; i < 24; i++) {
            for (int j = 0; j < 4; j++){
                String mins = "";
                if (j == 0) {
                    mins = "00";
                }
                else if (j == 1) {
                    mins = "15";
                }
                else if (j == 2) {
                    mins = "30";
                }
                else if (j == 3) {
                    mins = "45";
                }
                String totalTime = i + ":" + mins;
                timeArray.add(totalTime);
            }
        }
        return timeArray;
    }

    public ArrayList<Note> noteIDstoNotes(ArrayList<Integer> noteIDs) {
        ArrayList<Note> noteArrayList = new ArrayList<>();
        for (Integer noteID: noteIDs) {
            for (Note note: notes) {
                if (note.getId() == noteID) {
                    noteArrayList.add(note);
                }
            }
        }
        return noteArrayList;
    }

    public ArrayList<Integer> notestoNoteIDs(ArrayList<Note> notes) {
        ArrayList<Integer> noteIDArrayList = new ArrayList<>();
        for (Note note: notes) {
            noteIDArrayList.add(note.getId());
        }
        return noteIDArrayList;
    }

    private Task storeInfo() throws ParseException {

        name = rootView.findViewById(R.id.txtName);
        if (!name.getText().toString().equals("")) {

            String _name = name.getText().toString();

            dateChip = rootView.findViewById(R.id.dateChip);
            String str_DueDate = dateChip.getText().toString();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date DueDate = formatter.parse(str_DueDate);

            timeChip =  rootView.findViewById(R.id.timeChip);
            String DueTime = timeChip.getText().toString();

            lblPriority = rootView.findViewById(R.id.lblPriority);
            String str_priority = lblPriority.getText().toString();
            int priority = Integer.parseInt(str_priority);

            notes = noteRecyclerAdapter.getNotes();
            noteIDs = notestoNoteIDs(notes);

            TextView lblDuration = rootView.findViewById(R.id.lblDuration);
            String str_duration = lblDuration.getText().toString();
            String[] timeArr = str_duration.split(":");
            Double mins = Double.parseDouble(timeArr[1])/60;
            Double hours = Double.parseDouble(timeArr[0]);
            Double duration = hours+mins;

            prefLabels.addAll(currentTaskLabels);
            HashSet<String> prefSet = new HashSet<>(prefLabels);
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("LabelPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("labelSet", prefSet);
            editor.apply();

            task = new Task(_name, currentTaskLabels, DueDate, priority, subtasks, noteIDs, duration, DueTime, splits, isPlanni);

        } else {
            task = new Task();
        }

        return task;
    }

    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();

    }


    // TODO Lucas: What is gonna happen with with this code, we don't have custom priorities anymore, is it worth just deleting?
    private Document readPriorityXML() throws ParserConfigurationException, IOException, SAXException {
        String prioFile = "priorities.xml";
        File dir = new File(String.valueOf(mContext.getFilesDir()));
        File filePrioFile = new File(dir, prioFile);
        if (filePrioFile.isFile()) {
        } else {
            OutputStreamWriter out = new OutputStreamWriter(mContext.openFileOutput(prioFile, 0));
            String str = ("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                    "<priorities>\n" +
                    "</priorities>\n");
            out.write(str);
            out.flush();
            out.close();
        }

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.parse(filePrioFile);
    }

    private ArrayList<String> xmlToPriorities(Document doc) throws ParseException {
        ArrayList<String> newPriorities = new ArrayList<>();
        NodeList rootList = doc.getElementsByTagName("priorities");

        int numPrio = rootList.getLength();
        System.out.println(numPrio);

        for (int i =0; i<numPrio;i++) {
            Node node = rootList.item(i);
            Element element = (Element) node;

            try {
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                newPriorities.add(name);
            } catch (NullPointerException e) {
                ArrayList<String> basicPriorities = new ArrayList<>();
                for (int j =0;j<6;j++) {
                    basicPriorities.add(String.valueOf(j));
                }
                return basicPriorities;
            }

        }
        return newPriorities;
    }
}