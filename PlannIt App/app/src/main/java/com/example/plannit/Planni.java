package com.example.plannit;

import android.content.Context;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;



public class Planni {

    /**
     * This is the function that is called from the TODAY FRAGMENT whenever a user wishes to use Planni
     * @param allSchedules - Takes in a list of All Schedules that have been created by the user
     * @param allTasks - Takes in a list of all Tasks that have been created by the user
     */
    public static ArrayList<Task> setUp(ArrayList<Schedule> allSchedules, ArrayList<Task> allTasks, Context context) {

        ArrayList<Task> newAllTasks = new ArrayList<>();
        newAllTasks.addAll(allTasks);
        for (Task task: allTasks) {
            if(task.isPlanniTask()) {
                newAllTasks.remove(task);

            }

        }

        allTasks = newAllTasks;
        xmlUtils.replaceTasks(context, allTasks);

        ArrayList<Schedule> sortedFilteredSchedules = new ArrayList<>();
        ArrayList<Schedule> filteredSchedules = new ArrayList<>();
        LocalDate dateRequested;
        ArrayList<Task> listOfPlanniTasks = new ArrayList<>();
        ArrayList<Task> newPlanniTasks = new ArrayList<>();


        dateRequested = LocalDate.now();
        fetchSpecificSchedules(allSchedules, filteredSchedules, dateRequested);
        sortedFilteredSchedules(sortedFilteredSchedules,filteredSchedules);

        listOfPlanniTasks = fetchSpecificTasks(sortedFilteredSchedules,allTasks);

        System.out.println("Start: List of Planni Tasks");
        for (int i = 0; i < listOfPlanniTasks.size(); i ++) {
            Task currentTask = listOfPlanniTasks.get(i);
            System.out.println(currentTask.getName());
        }
        System.out.println("End: List of Planni Tasks");

        // Using the List of Planni Tasks, we must create Tasks
        for (int i = 0; i < listOfPlanniTasks.size(); i++) {
            Task parentTask = listOfPlanniTasks.get(i);
            Date childrenTaskDate = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
            double childrenTaskDuration = parentTask.getDuration()/ parentTask.getSplits();
            Task newTask = new Task(parentTask.getName() + " Planni",parentTask.getLabels(),childrenTaskDate,parentTask.getPriority(),parentTask.getSubtasks(),parentTask.getNotes(),childrenTaskDuration,"Time:",-1,false,true, parentTask.getID());


            newPlanniTasks.add(newTask);
        }

        return newPlanniTasks;


    }

    // TODO javadoc
    private static void sortedFilteredSchedules(ArrayList<Schedule> sortedFilteredSchedules, ArrayList<Schedule> filteredSchedules) {

        // Store all the start times for the schedules
        ArrayList<Double> startTimes = new ArrayList<>();

        // Fill the array list
        for (int i = 0; i < filteredSchedules.size(); i++) {
            String startTimeString = filteredSchedules.get(i).getStartDate();
            String[] startTimeSplit = startTimeString.split(":");
            double hourStartFloat = Float.parseFloat(startTimeSplit[0]);
            double minutesStartFloat = Float.parseFloat(startTimeSplit[1]);
            if (minutesStartFloat == 15) {
                minutesStartFloat = 0.25;
            }
            else if(minutesStartFloat == 30) {
                minutesStartFloat = 0.5;
            }
            else if(minutesStartFloat == 45) {
                minutesStartFloat = 0.75;
            }
            else {
                minutesStartFloat = 0;
            }

            double time = hourStartFloat + minutesStartFloat;
            startTimes.add(time);

        }

        // sort in increasing order
        while (filteredSchedules.size() != 0) {

            int minPos = startTimes.indexOf(Collections.min(startTimes));
            startTimes.remove(minPos);
            sortedFilteredSchedules.add(filteredSchedules.get(minPos));
            filteredSchedules.remove(minPos);


        }

    }


    // Fetch schedules that are linked to the date

    /**
     * This function is used to fetch schedules that are linked to Today's date
     * @param allSchedules - This is a list of all Schedules
     * @param dateRequested - This is the date that Planni was called
     */
    private static void fetchSpecificSchedules(ArrayList<Schedule> allSchedules, ArrayList<Schedule> filteredSchedules,LocalDate dateRequested) {
        // This is todays date, and we are taking what day of the week it is
        String dayOfWeek = dateRequested.getDayOfWeek().toString();

        for(int i = 0; i < allSchedules.size(); i++) {

            Schedule currentSchedule = allSchedules.get(i);
            if (currentSchedule.isMonday() && dayOfWeek.equals("MONDAY")) {
                filteredSchedules.add(currentSchedule);
            }
            else if (currentSchedule.isTuesday() && dayOfWeek.equals("TUESDAY")) {
                filteredSchedules.add(currentSchedule);
            }
            else if (currentSchedule.isWednesday() && dayOfWeek.equals("WEDNESDAY")) {
                filteredSchedules.add(currentSchedule);

            }
            else if (currentSchedule.isThursday() && dayOfWeek.equals("THURSDAY")) {
                filteredSchedules.add(currentSchedule);

            }
            else if (currentSchedule.isFriday() && dayOfWeek.equals("FRIDAY")) {
                filteredSchedules.add(currentSchedule);

            }
            else if (currentSchedule.isSaturday() && dayOfWeek.equals("SATURDAY")) {
                filteredSchedules.add(currentSchedule);

            }
            else if (currentSchedule.isSunday() && dayOfWeek.equals("SUNDAY")) {
                filteredSchedules.add(currentSchedule);

            }

        }
    }








    /**
     * This function will output the ArrayList of Planni Tasks to be created for the specific day the algorithm is being asked to calculate it for
     * @param filteredSchedules - This is an ArrayList of Schedules that are applicable for Today
     * @param allTasks - This is an ArrayList of All tasks that exist in the XML document
     * @return - This will return all tasks that are to be displayed in the Today section for the user.
     */

    private static ArrayList<Task> fetchSpecificTasks(ArrayList<Schedule> filteredSchedules, ArrayList<Task> allTasks) {

        // Initialising all the ArrayLists used within this schedule
        ArrayList<String> listOfScheduleLabels = new ArrayList<>();
        ArrayList<Double> listOfScheduleWorkTime = new ArrayList<>();
        ArrayList<Double> listOfTaskPriority = new ArrayList<>();
        ArrayList<Task> listOfValidTasks = new ArrayList<>();
        ArrayList<Task> listOfPlanniTasks = new ArrayList<>();


        // Collecting information regarding schedules EG How long each schedule is and the label associated with that schedule
        for (int i = 0; i < filteredSchedules.size(); i++) {
            Schedule currentSchedule = filteredSchedules.get(i);
            listOfScheduleLabels.add(currentSchedule.getLabel());
            listOfScheduleWorkTime.add(calculateTimeDifference(currentSchedule.getStartDate(),currentSchedule.getEndDate()));
        }

        // Filter Tasks so only Planni tasks are chosen
        for (int i = 0; i < allTasks.size(); i++) {

            Task currentTask = allTasks.get(i);

            if (!currentTask.isPlanni()) {
                allTasks.remove(i);
            }

        }


        // Looking for tasks that match the schedules labels
        for (int i = 0; i < allTasks.size(); i++) {

            Task currentTask = allTasks.get(i);
            ArrayList<String> currentTaskLabels = currentTask.getLabels();

            outerloop:
            for (int j = 0; j < currentTaskLabels.size(); j++) {


                for (int k = 0; k < listOfScheduleLabels.size(); k++) {

                    if (currentTaskLabels.get(j).equals(listOfScheduleLabels.get(k))) {
                        listOfValidTasks.add(currentTask);
                        break outerloop;
                    }

                }


            }

        }

        // Based on priority for the tasks (Sort from highest to lowest) choose schedules that fit the time slots
        for (int i = 0; i < listOfValidTasks.size(); i++) {

            Task currentTask = listOfValidTasks.get(i);
            listOfTaskPriority.add(calculatePriority(currentTask));

        }

        for (int i = 0; i < listOfTaskPriority.size(); i++) {
            System.out.println(listOfTaskPriority.get(i));
        }

        ArrayList<Task> sortedFilteredTasks = sortFilteredTaskList(listOfValidTasks, listOfTaskPriority);

        // A loop that goes through all labels the schedules for the day has
        for (int i = 0; i < listOfScheduleLabels.size(); i++) {

            String currentLabel = listOfScheduleLabels.get(i);
            ArrayList<Task> toBeRemovedTasks = new ArrayList<>();
            double currentWorkTime = listOfScheduleWorkTime.get(i);

            // Continue to loop until the schedule has no time left or there are no tasks left
            for (int completedChecks = 0; completedChecks < sortedFilteredTasks.size(); completedChecks++){

                if (currentWorkTime == 0) {
                    break;
                }

                Task currentTask = sortedFilteredTasks.get(completedChecks);
                System.out.println("currentTask: " + currentTask.getName());

                // If the label is correct go here
                if(currentTask.getLabels().contains(currentLabel)) {

                    // If there is enough time to add the task to the schedule, add the task
                    double currentTaskWorkTime = currentTask.getDuration()/currentTask.getSplits();
                    System.out.println("Schedule Time Left: " + currentWorkTime);
                    System.out.println("Task Work Time: " + currentTaskWorkTime);

                    int positionOfSchedule = i;
                    if (currentTaskWorkTime <= currentWorkTime) {

                        System.out.println("addedTask: " + currentTask.getName());
                        currentWorkTime -= currentTaskWorkTime;

                        toBeRemovedTasks.add(currentTask);
                        listOfPlanniTasks.add(currentTask);
                    }

                }

            }

            sortedFilteredTasks.removeAll(toBeRemovedTasks);


        }

        return listOfPlanniTasks;

    }


    // HELPER FUNCTIONS

    /**
     * This function will take in listOfValidTasks and sort them based on their Priority.
     * @param listOfValidTasks - This is an arraylist containing all tasks that can be used for this day
     * @param taskPriorityList - This is an arraylist storing the priority of each task in listOfValidTasks
     * @return This method will return a sortedFilteredTask arraylist
     */
    private static ArrayList<Task> sortFilteredTaskList(ArrayList<Task> listOfValidTasks, ArrayList<Double> taskPriorityList) {

        // Initialise the ArrayList that will be returned
        ArrayList<Task> sortedFilteredTasks = new ArrayList<>();
        // This will store how many times the code must be iterated, it must be stored as a variable because,
        // during the for loop below the sizes of the array will change.
        int looper = taskPriorityList.size();

        // This for loop goes through listOfValidTasks AND priority, locates the maximum value, stores that task
        // inside the sortedFilteredTasks and remove it from the listOfValidTasks and taskPriorityList
        // this is done to make sure duplicate tasks are not returned
        for (int i = 0; i < looper; i++) {

            // Fetch the max value inside the priority list
            Double tempMax = Collections.max(taskPriorityList);
            // Stores the position where the max value was found
            int positionOfMax = taskPriorityList.indexOf(tempMax);
            // Stores the Task that is associated to the max value
            Task tempMaxTask = listOfValidTasks.get(positionOfMax);
            System.out.println("Max Task: " + tempMaxTask.getName());
            // Add the max value to the final arraylist
            sortedFilteredTasks.add(tempMaxTask);
            // Remove from taskPriorityList and filteredLists
            taskPriorityList.remove(positionOfMax);
            listOfValidTasks.remove(positionOfMax);

        }

        return sortedFilteredTasks;

    }


    /**
     * This function is used to calculate the difference between two times
     * @param startTime - This is the start time of the schedule
     * @param endTime - This is the end time of the schedule
     * @return - This will return a double which is the difference between two times
     */
    private static double calculateTimeDifference(String startTime, String endTime) {

        System.out.println("Start Time: " + startTime);
        System.out.println("End Time: " + endTime);

        String[] startTimeSplit = startTime.split(":");
        String[] endTimeSplit = endTime.split(":");
        double hourStartFloat = Float.parseFloat(startTimeSplit[0]);
        double minutesStartFloat = Float.parseFloat(startTimeSplit[1]);
        double hourEndFloat = Float.parseFloat(endTimeSplit[0]);
        double minutesEndFloat = Float.parseFloat(endTimeSplit[1]);

        if (minutesStartFloat == 15) {
            minutesStartFloat = 0.25;
        }
        else if(minutesStartFloat == 30) {
            minutesStartFloat = 0.5;
        }
        else if(minutesStartFloat == 45) {
            minutesStartFloat = 0.75;
        }

        if (minutesEndFloat == 15) {
            minutesEndFloat = 0.25;
        }
        else if(minutesEndFloat == 30) {
            minutesEndFloat = 0.5;
        }
        else if(minutesEndFloat == 45) {
            minutesEndFloat = 0.75;
        }

        double endTimeTotal = hourEndFloat + minutesEndFloat;
        double startTimeTotal = hourStartFloat + minutesStartFloat;

        System.out.println("Schedule Time: " + (endTimeTotal-startTimeTotal));

        return endTimeTotal - startTimeTotal;


    }

    /**
     * This method is used to calculate what task is more important to be completed.
     * This is based on the task priority, and the number of days left to complete the task.
     * @param filteredTask - This takes in all tasks that are to be considered for today's schedule
     * @return - This will return a double that is its priority
     */
    private static double calculatePriority(Task filteredTask) {

        int priority = filteredTask.getPriority();
        double daysLeftOfTask;
        // Make a fraction
        if (filteredTask.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().equals(LocalDate.now())) {
            daysLeftOfTask = 100.0;
        }
        else if(filteredTask.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.now())) {
            daysLeftOfTask = 1000.0;
        }
        else {
            daysLeftOfTask = 1.0/(ChronoUnit.DAYS.between(LocalDate.now(), filteredTask.getDueDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
        }

        return priority + daysLeftOfTask;

    }


}
