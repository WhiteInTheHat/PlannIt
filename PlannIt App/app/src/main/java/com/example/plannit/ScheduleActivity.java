package com.example.plannit;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class ScheduleActivity extends AppCompatActivity implements recyclerViewInterface {

    private RecyclerView scheduleRecyclerView;
    private ArrayList<Schedule> scheduleList;
    private Context context;
    private ScheduleRecyclerAdapter adapter;
    private ArrayList<String> labelArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Initialising Variables to be used through out the code.
        scheduleRecyclerView = findViewById(R.id.scheduleRecylerView);
        scheduleList = new ArrayList<>();
        labelArray = new ArrayList<>();
        context = getApplicationContext();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(scheduleRecyclerView);

        loadLabelSpinner();
        scheduleList = xmlUtils.loadSchedules(context);
        validateSchedules(scheduleList);
        scheduleList = xmlUtils.loadSchedules(context);
        initScheduleRecyclerAdapter();


    }

    /**
     * This will fetch all the Labels the user has previously created, from the shared preferences file
     */
    private void loadLabelSpinner() {

        SharedPreferences sharedPreferences = getSharedPreferences("LabelPrefs", Context.MODE_PRIVATE);
        HashSet<String> mySet = (HashSet<String>) sharedPreferences.getStringSet("labelSet", new HashSet<>());
        labelArray = new ArrayList<>(mySet);

    }

    /**
     * This function checks if a schedule has been rendered useless
     * @param scheduleList - All the schedules the user has created
     */
    private void validateSchedules(ArrayList<Schedule> scheduleList) {

        LocalDate currentDate = LocalDate.now();

        for (int i = 0; i < scheduleList.size(); i++) {

            Schedule currentSchedule = scheduleList.get(i);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate scheduleDateCreated = LocalDate.parse(currentSchedule.getDateCreated(), formatter);
            if (scheduleDateCreated.isBefore(currentDate) && !currentSchedule.isReoccurring()) {
                currentSchedule.removeNodeXML(context);
            }
        }




    }

    /**
     * This method initialises the recycler adapter that will be used for schedules
     */
    private void initScheduleRecyclerAdapter() {
        adapter = new ScheduleRecyclerAdapter(this, scheduleList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        scheduleRecyclerView.setLayoutManager(layoutManager);
        scheduleRecyclerView.setItemAnimator(new DefaultItemAnimator());
        scheduleRecyclerView.setAdapter(adapter);
    }

    /**
     * When the + button is clicked, a popup will appear asking the user all the details required for the schedule that is to be created
     * @param view the + button on the schedule page
     */
    public void addSchedulePopup(View view){
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        Button confirmationButton, cancelationButton;
        Spinner labelSpinner, startTimeSpinner, endTimeSpinner;
        ToggleButton isMondayButton, isTuesdayButton, isWednesdayButton, isThursdayButton, isFridayButton, isSaturdayButton, isSundayButton, isReoccurringButton;



        // Connects variables to the items within the popup box
        dialogBuilder = new AlertDialog.Builder(this);
        final View addDialogView = getLayoutInflater().inflate(R.layout.addschedulepopup,null);
        labelSpinner = addDialogView.findViewById(R.id.labelSpinner);
        startTimeSpinner = addDialogView.findViewById(R.id.startTimeSpinner);
        endTimeSpinner = addDialogView.findViewById(R.id.endTimeSpinner);
        isMondayButton = addDialogView.findViewById(R.id.mondayToggleButton);
        isTuesdayButton = addDialogView.findViewById(R.id.tuesdayToggleButton);
        isWednesdayButton = addDialogView.findViewById(R.id.wednesdayToggleButton);
        isThursdayButton = addDialogView.findViewById(R.id.thursdayToggleButton);
        isFridayButton = addDialogView.findViewById(R.id.fridayToggleButton);
        isSaturdayButton = addDialogView.findViewById(R.id.saturdayToggleButton);
        isSundayButton = addDialogView.findViewById(R.id.sundayToggleButton);
        isReoccurringButton = addDialogView.findViewById(R.id.reoccuringToggleButton);
        confirmationButton = addDialogView.findViewById(R.id.confirmationButton);
        cancelationButton = addDialogView.findViewById(R.id.cancelationButton);

        // Creates a dropdown menu based on labels user has
        ArrayAdapter<String> labelAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,labelArray);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelSpinner.setAdapter(labelAdapter);

        // Create a start time dropdown menu
        ArrayList<String> startTimeArray = generateTimes(0);
        ArrayAdapter<String> startTimeAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item,startTimeArray);
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(startTimeAdapter);

        // Create a end time dropdown menu, dependent on start time dropdown menu
        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String startTime = startTimeSpinner.getSelectedItem().toString();
                String[] startTimeArray = startTime.split(":");
                String hours = startTimeArray[0];
                String mins = startTimeArray[1];

                ArrayList<String> endTimeArray = generateTimes(Integer.parseInt(hours),mins);
                ArrayAdapter<String> endTimeAdapter = new ArrayAdapter<>(context,android.R.layout.simple_spinner_item,endTimeArray);
                endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                endTimeSpinner.setAdapter(endTimeAdapter);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // Display the dropdown menu
        dialogBuilder.setView(addDialogView);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String label = labelSpinner.getSelectedItem().toString();
                String startTime = startTimeSpinner.getSelectedItem().toString();
                String endTime = endTimeSpinner.getSelectedItem().toString();
                boolean isMonday = isMondayButton.isChecked();
                boolean isTuesday = isTuesdayButton.isChecked();
                boolean isWednesday = isWednesdayButton.isChecked();
                boolean isThursday = isThursdayButton.isChecked();
                boolean isFriday = isFridayButton.isChecked();
                boolean isSaturday = isSaturdayButton.isChecked();
                boolean isSunday = isSundayButton.isChecked();
                boolean isReoccurring = isReoccurringButton.isChecked();
                String dateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                Schedule potentialSchedule = new Schedule(label, startTime, endTime, isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday, isReoccurring, dateCreated);

                // Check whether a schedule clashes with another schedule, if it doesn't go here
                if (!isMonday && !isTuesday && !isWednesday && !isThursday && !isFriday && !isSaturday && !isSunday) {
                    Toast.makeText(context, "You cannot have a schedule for no dates!", Toast.LENGTH_LONG).show();

                }
                else if (startTime.equals(endTime)) {
                    Toast.makeText(context, "Start time and end time are the same!", Toast.LENGTH_LONG).show();
                }
                else if (checkExistingSchedule(scheduleList, potentialSchedule)) {
                    createSchedule(label,startTime,endTime,isMonday,isTuesday,isWednesday,isThursday,isFriday,isSaturday,isSunday,isReoccurring,dateCreated);
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(context, "This clashes with one of your existing schedules!", Toast.LENGTH_LONG).show();
                }

            }
        });

        cancelationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * This method will take in all the details of the schedule and create a completely new one
     * @param label - Schedule Label
     * @param startTime - Schedule Start Time
     * @param endTime - Schedule End Time
     * @param isMonday - Is Monday checked
     * @param isTuesday - Is Tuesday checked
     * @param isWednesday - Is Wednesday checked
     * @param isThursday - Is Thursday checked
     * @param isFriday - Is Friday checked
     * @param isSaturday - Is Saturday checked
     * @param isSunday - Is Sunday checked
     * @param isReoccurring - Is this a reoccurring schedule
     * @param dateCreated - What date was the schedule created
     */
    private void createSchedule(String label, String startTime, String endTime, boolean isMonday, boolean isTuesday, boolean isWednesday, boolean isThursday, boolean isFriday, boolean isSaturday, boolean isSunday, boolean isReoccurring, String dateCreated)  {


        // Adds a new note
        // Creates a new note and adds it to the note list
        scheduleList.add(new Schedule(label,startTime,endTime,isMonday,isTuesday,isWednesday,isThursday,isFriday,isSaturday,isSunday,isReoccurring,dateCreated));

        // Gets the Notes.xml file and deletes it.
        File dir = getFilesDir();
        File file = new File(dir, "Schedule.xml");
        file.delete();
        // Creates a new Notes.xml file
        xmlUtils.loadScheduleXML(context);
        // Writes into the new Notes.xml file all the notes. This is done to prevent duplicates of the note being created
        xmlUtils.writeScheduleXML(context,scheduleList);
        // Refreshes the Recycler Adapter
        initScheduleRecyclerAdapter();


    }

    /**
     * This method is used to edit an existing schedule
     * @param scheduleId - The schedule ID
     * @param label - Schedule Label
     * @param startTime - Schedule Start Time
     * @param endTime - Schedule End Time
     * @param isMonday - Is Monday checked
     * @param isTuesday - Is Tuesday checked
     * @param isWednesday - Is Wednesday checked
     * @param isThursday - Is Thursday checked
     * @param isFriday - Is Friday checked
     * @param isSaturday - Is Saturday checked
     * @param isSunday - Is Sunday checked
     * @param isReoccurring - Is this a reoccurring schedule
     * @param dateCreated - What date was the schedule created
     */
    private void createSchedule(int scheduleId, String label, String startTime, String endTime, boolean isMonday, boolean isTuesday, boolean isWednesday, boolean isThursday, boolean isFriday, boolean isSaturday, boolean isSunday, boolean isReoccurring, String dateCreated)  {


        // Adds a new note
        // Creates a new note and adds it to the note list
        scheduleList.add(new Schedule(scheduleId,label,startTime,endTime,isMonday,isTuesday,isWednesday,isThursday,isFriday,isSaturday,isSunday,isReoccurring,dateCreated));

        // Gets the Notes.xml file and deletes it.
        File dir = getFilesDir();
        File file = new File(dir, "Schedule.xml");
        file.delete();
        // Creates a new Notes.xml file
        xmlUtils.loadScheduleXML(context);
        // Writes into the new Notes.xml file all the notes. This is done to prevent duplicates of the note being created
        xmlUtils.writeScheduleXML(context,scheduleList);
        // Refreshes the Recycler Adapter
        initScheduleRecyclerAdapter();


    }


    /**
     * This section of code allows the user to DELETE a schedule by swiping to the left on the schedule.
     */
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction) {
                case ItemTouchHelper.LEFT:
                    Schedule currentSchedule = scheduleList.get(position);
                    currentSchedule.removeNodeXML(context);
                    scheduleList.remove(position);
                    adapter.notifyItemRemoved(position);
                    break;
            }
        }
    };

    /**
     * This piece of code allows the user to EDIT the schedule they have previously created by clicking on the schedule.
     * It is very similar to the addSchedulePopup, however the difference is that the values of the schedule have been set by default
     * @param position - This returns the position of the item that was clicked in the array
     */
    @Override
    public void onItemClick(int position) {
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        Button confirmationButton, cancelationButton;
        Spinner labelSpinner, startTimeSpinner, endTimeSpinner;
        ToggleButton isMondayButton, isTuesdayButton, isWednesdayButton, isThursdayButton, isFridayButton, isSaturdayButton, isSundayButton, isReoccurringButton;
        Schedule currentSchedule = scheduleList.get(position);


        // Sets up the dialog box
        dialogBuilder = new AlertDialog.Builder(this);
        final View addDialogView = getLayoutInflater().inflate(R.layout.addschedulepopup,null);
        labelSpinner = addDialogView.findViewById(R.id.labelSpinner);
        startTimeSpinner = addDialogView.findViewById(R.id.startTimeSpinner);
        endTimeSpinner = addDialogView.findViewById(R.id.endTimeSpinner);
        isMondayButton = addDialogView.findViewById(R.id.mondayToggleButton);
        isTuesdayButton = addDialogView.findViewById(R.id.tuesdayToggleButton);
        isWednesdayButton = addDialogView.findViewById(R.id.wednesdayToggleButton);
        isThursdayButton = addDialogView.findViewById(R.id.thursdayToggleButton);
        isFridayButton = addDialogView.findViewById(R.id.fridayToggleButton);
        isSaturdayButton = addDialogView.findViewById(R.id.saturdayToggleButton);
        isSundayButton = addDialogView.findViewById(R.id.sundayToggleButton);
        isReoccurringButton = addDialogView.findViewById(R.id.reoccuringToggleButton);
        confirmationButton = addDialogView.findViewById(R.id.confirmationButton);
        cancelationButton = addDialogView.findViewById(R.id.cancelationButton);

        // Creates a dropdown menu based on labels user has
        ArrayAdapter labelAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,labelArray);
        labelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        labelSpinner.setAdapter(labelAdapter);
        labelSpinner.setSelection(labelArray.indexOf(currentSchedule.getLabel()));

        // Create time dropdown menu
        ArrayList<String> startTimeArray = generateTimes(0);
        ArrayAdapter startTimeAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,startTimeArray);
        startTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startTimeSpinner.setAdapter(startTimeAdapter);
        String startTime = currentSchedule.getStartDate();
        String[] startSplit = startTime.split(":");
        String startHours = startSplit[0];
        String startMinutes = startSplit[1];
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
        startTimeSpinner.setSelection(startTimeSpinnerPosition);

        startTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String startTime = startTimeSpinner.getSelectedItem().toString();
                String[] startTimeArray = startTime.split(":");
                String hours = startTimeArray[0];
                String mins = startTimeArray[1];

                ArrayList<String> endTimeArray = generateTimes(Integer.parseInt(hours),mins);
                ArrayAdapter endTimeAdapter = new ArrayAdapter(context,android.R.layout.simple_spinner_item,endTimeArray);
                endTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                endTimeSpinner.setAdapter(endTimeAdapter);

                String endTime = currentSchedule.getEndDate();
                String[] endSplit = endTime.split(":");
                String endHours = endSplit[0];
                String endMinutes = endSplit[1];
                int endExtraAdd = 0;
                if (endMinutes.equals("15")) {
                    endExtraAdd = 1;
                }
                else if (endMinutes.equals("30")) {
                    endExtraAdd = 2;
                }
                else if (endMinutes.equals("45")) {
                    endExtraAdd = 3;
                }
                endTimeSpinner.setSelection(((Integer.parseInt(endHours) * 4) + endExtraAdd)-startTimeSpinnerPosition);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if (currentSchedule.isMonday()) {
            isMondayButton.setChecked(true);
        }
        if (currentSchedule.isTuesday()) {
            isTuesdayButton.setChecked(true);
        }
        if (currentSchedule.isWednesday()) {
            isWednesdayButton.setChecked(true);
        }
        if (currentSchedule.isThursday()) {
            isThursdayButton.setChecked(true);
        }
        if (currentSchedule.isFriday()) {
            isFridayButton.setChecked(true);
        }
        if (currentSchedule.isSaturday()) {
            isSaturdayButton.setChecked(true);
        }
        if (currentSchedule.isSunday()) {
            isSundayButton.setChecked(true);
        }
        if (currentSchedule.isReoccurring()) {
            isReoccurringButton.setChecked(true);
        }


        dialogBuilder.setView(addDialogView);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int scheduleId = currentSchedule.getScheduleID();
                String label = labelSpinner.getSelectedItem().toString();
                String startTime = startTimeSpinner.getSelectedItem().toString();
                String endTime = endTimeSpinner.getSelectedItem().toString();
                boolean isMonday = isMondayButton.isChecked();
                boolean isTuesday = isTuesdayButton.isChecked();
                boolean isWednesday = isWednesdayButton.isChecked();
                boolean isThursday = isThursdayButton.isChecked();
                boolean isFriday = isFridayButton.isChecked();
                boolean isSaturday = isSaturdayButton.isChecked();
                boolean isSunday = isSundayButton.isChecked();
                boolean isReoccurring = isReoccurringButton.isChecked();
                String dateCreated = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                Schedule potentialSchedule = new Schedule(scheduleId, label, startTime, endTime, isMonday, isTuesday, isWednesday, isThursday, isFriday, isSaturday, isSunday, isReoccurring, dateCreated);

                Schedule oldSchedule = scheduleList.get(position);
                scheduleList.remove(position);


                if (checkExistingSchedule(scheduleList, potentialSchedule)) {
                    createSchedule(scheduleId,label,startTime,endTime,isMonday,isTuesday,isWednesday,isThursday,isFriday,isSaturday,isSunday,isReoccurring,dateCreated);
                    dialog.dismiss();
                }
                else {
                    scheduleList.add(position,oldSchedule);
                    Toast.makeText(context, "This clashes with one of your existing schedules!", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancelationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    /**
     * This function checks if there exists a schedule in the same time that the user already has one
     * @param scheduleList - This is a list of all the schedules the user has created
     * @param potentialSchedule - This is the schedule that is about to be made
     * @return - This will return a boolean on whether or not a schedule can be created
     */
    private boolean checkExistingSchedule(ArrayList<Schedule> scheduleList, Schedule potentialSchedule) {

        ArrayList<ArrayList<String>> allSchedulesDetails= new ArrayList<>();

        for (int i = 0; i < scheduleList.size(); i++) {

            Schedule currentSchedule = scheduleList.get(i);
            ArrayList<String> currentScheduleInformation = currentSchedule.extractDateAndTimes();
            allSchedulesDetails.add(currentScheduleInformation);
            System.out.println(allSchedulesDetails.get(i));

        }


        ArrayList<String> potentialScheduleDetails = potentialSchedule.extractDateAndTimes();



        for (int i = 0; i < allSchedulesDetails.size(); i++) {

            ArrayList<String> comparisonScheduleDetails = allSchedulesDetails.get(i);

            int comparisonEndTimeValue = Integer.parseInt(((comparisonScheduleDetails.get(1)).split(":"))[0]);
            int potentialScheduleStartTimeValue = Integer.parseInt(((potentialScheduleDetails.get(0)).split(":"))[0]);
            int looper = potentialScheduleDetails.size();
            try {
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
                Date comparisonStartTime = timeFormatter.parse(comparisonScheduleDetails.get(0));
                Date comparisonEndTime = timeFormatter.parse(comparisonScheduleDetails.get(1));
                Date potentialStartTime = timeFormatter.parse(potentialScheduleDetails.get(0));
                Date potentialEndTime = timeFormatter.parse(potentialScheduleDetails.get(1));

                for (int k = 2; k < looper; k++) {

                    // Check if the schedule contains the same days to be worked on, if it does compare it
                    if (comparisonScheduleDetails.contains(potentialScheduleDetails.get(k))) {

                        // If the schedule is clashes do this
                        if (comparisonStartTime.equals(potentialStartTime) || comparisonEndTime.equals(potentialEndTime) || (potentialStartTime.before(comparisonStartTime) && potentialStartTime.after(comparisonEndTime)) || (potentialEndTime.after(comparisonStartTime) && potentialEndTime.before(comparisonEndTime)) || (potentialStartTime.after(comparisonStartTime) && potentialStartTime.before(comparisonEndTime)) || (potentialStartTime.before(comparisonStartTime) && potentialEndTime.after(comparisonEndTime)) || potentialEndTime.after(comparisonStartTime) && potentialEndTime.before(comparisonEndTime)) {
                            return false;
                        }

                    }

                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }






        }
        return true;

    }

    /**
     * This is a helper method to generate times for the start spinner
     * @param start - the start time for when times should be generated
     * @return returns an arraylist of times
     */
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
                String totalTime = Integer.toString(i) + ":" + mins;
                timeArray.add(totalTime);
            }
        }
        return timeArray;
    }


    /**
     * This is a method that helps generate the times the user can select when creating a schedule.
     * This is a helper function.
     * @param start - This will take the hour the user wants to generate times from
     * @param minutes - This takes the minutes the use wants to generate times from
     * @return This returns an ArrayList containing strings that will be used to fill in the start and end time spinners in the popup
     */
    private ArrayList<String> generateTimes(int start, String minutes) {
        ArrayList<String> timeArray = new ArrayList<String>();
        String startHour = Integer.toString(start);

        if (minutes.equals("00")) {
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
                    else {
                        mins = "45";
                    }
                    String totalTime = Integer.toString(i) + ":" + mins;
                    timeArray.add(totalTime);
                }
            }
        }
        else {
            switch (minutes) {
                case "15": {
                    String totalTime = startHour + ":" + "15";
                    timeArray.add(totalTime);
                    totalTime = startHour + ":" + "30";
                    timeArray.add(totalTime);
                    totalTime = startHour + ":" + "45";
                    timeArray.add(totalTime);
                    break;
                }
                case "30": {
                    String totalTime = startHour + ":" + "30";
                    timeArray.add(totalTime);
                    totalTime = startHour + ":" + "45";
                    timeArray.add(totalTime);
                    break;
                }
                case "45": {
                    String totalTime = startHour + ":" + "45";
                    timeArray.add(totalTime);
                    break;
                }
            }
            for (int i = start+1; i < 24; i++) {
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
                    else {
                        mins = "45";
                    }
                    String totalTime = Integer.toString(i) + ":" + mins;
                    timeArray.add(totalTime);
                }
            }
        }

        return timeArray;
    }

    // Does nothing
    @Override
    public void onRvMenuItem(int position, MenuItem menuItem, Note note) {

    }



}