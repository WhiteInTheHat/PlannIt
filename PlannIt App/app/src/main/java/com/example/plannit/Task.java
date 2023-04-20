package com.example.plannit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class Task implements Serializable {

    // Attributes
    private String name, dueTime;
    private ArrayList<String> labels;
    private ArrayList<Subtask> subtasks;
    private ArrayList<Integer> noteIDs;
    private Date dueDate;
    private Double duration;
    private boolean isPlanniActivated;
    private Integer splits, taskID, priority;
    private boolean isPlanniTask;
    private Integer parentID;


    //Constructors
    public Task() {
        this.name = null;
        this.labels = null;
        this.dueDate = null;
        this.priority = 0;
        this.subtasks = null;
        this.noteIDs = null;
    }


    public Task(String name, ArrayList<String> labels, Date DueDate, int priority, ArrayList<Subtask> subtasks, ArrayList<Integer> noteIDs, Double duration, String DueTime, Integer splits, boolean isPlanniActivated) {
        this.name = name;
        this.labels = labels;
        this.dueDate = DueDate;
        this.priority = priority;
        this.subtasks = subtasks;
        this.noteIDs = noteIDs;
        setID();
        this.isPlanniActivated=isPlanniActivated;
        this.duration =  duration;
        this.splits=splits;
        this.dueTime = DueTime;
        this.isPlanniTask = false;
        this.parentID=1;
    }

    public Task(String name, ArrayList<String> labels, Date DueDate, int priority, ArrayList<Subtask> subtasks, ArrayList<Integer> noteIDs, Integer taskID, Double duration, String DueTime, Integer splits, boolean isPlanniActivated) {
        this.name = name;
        this.labels = labels;
        this.dueDate = DueDate;
        this.priority = priority;
        this.subtasks = subtasks;
        this.noteIDs = noteIDs;
        this.taskID = taskID;
        this.isPlanniActivated=isPlanniActivated;
        this.duration =  duration;
        this.splits=splits;
        this.dueTime = DueTime;
        this.isPlanniTask = false;
        this.parentID=null;
    }

    public Task(String name, ArrayList<String> labels, Date DueDate, int priority, ArrayList<Subtask> subtasks, ArrayList<Integer> noteIDs, Double duration, String DueTime, Integer splits, boolean isPlanniActivated, boolean planniValue, int parentID) {
        this.name = name;
        this.labels = labels;
        this.dueDate = DueDate;
        this.priority = priority;
        this.subtasks = subtasks;
        this.noteIDs = noteIDs;
        setID();
        this.isPlanniActivated=isPlanniActivated;
        this.duration =  duration;
        this.splits=splits;
        this.dueTime = DueTime;
        this.isPlanniTask = planniValue;
        this.parentID=parentID;
    }

    public Task(String name, ArrayList<String> labels, Date DueDate, int priority, ArrayList<Subtask> subtasks, ArrayList<Integer> noteIDs, int taskID, Double duration, String DueTime, Integer splits, boolean isPlanniActivated, boolean planniValue, int parentID) {
        this.name = name;
        this.labels = labels;
        this.dueDate = DueDate;
        this.priority = priority;
        this.subtasks = subtasks;
        this.noteIDs = noteIDs;
        this.taskID = taskID;
        this.isPlanniActivated=isPlanniActivated;
        this.duration =  duration;
        this.splits=splits;
        this.dueTime = DueTime;
        this.isPlanniTask = planniValue;
        this.parentID=parentID;
    }






    //Methods:

    //Getters/Setters:
    public void setParentID(Integer parentID) {    this.parentID = parentID;}
    public Integer getParentID() {    return parentID;}
    public boolean isPlanniTask() {    return isPlanniTask;}
    public void setPlanniTask(boolean planniTask) {    isPlanniTask = planniTask;}

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public void setDueTime(String DueTime){
        this.dueTime = DueTime;
    }

    public String getDueTime(){
        return this.dueTime;
    }

    public void setSplits(Integer splits) {
        this.splits = splits;
    }

    public Integer getSplits(){return this.splits;}

    public void setName(String name) {
        this.name = name;
    }

    public void setLabels(ArrayList<String> labels) {
        this.labels = labels;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void setNotes(ArrayList<Integer> noteIDs) {
        this.noteIDs = noteIDs;
    }

    public void planniOn(){
        isPlanniActivated=true;
    }
    public void planniOff(){
        isPlanniActivated=false;
    }

    public boolean isPlanni() {
        return this.isPlanniActivated;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public ArrayList<Integer> getNotes() {
        return noteIDs;
    }
    public int getID() {
        return taskID;
    }

    public void setID() {
        this.taskID = idGenerator();
    }


    //Other:

    public void displayInfo(){
        System.out.println("Name:" + name);
        System.out.println("Labels:" + labels);
        System.out.println("DueDate:" + dueDate);
        System.out.println("Priority:" + priority);
        System.out.println("Subtasks:" +subtasks);
        System.out.println("noteIDs:" +noteIDs);
        System.out.println("taskID: "+taskID);
        System.out.println("duration: "+duration);
        System.out.println("splits: "+splits);
        System.out.println("DueTime "+ dueTime);
    }

    private int idGenerator(){
        Random random = new Random();
        int maxValue = 2147483600;
        int taskID = random.nextInt(maxValue);
        return taskID;
    }
}
