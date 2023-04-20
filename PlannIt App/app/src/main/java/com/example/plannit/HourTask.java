package com.example.plannit;

import java.time.LocalTime;
import java.util.ArrayList;

public class HourTask {
    LocalTime time;
    ArrayList<Task> tasks;

    public HourTask(LocalTime time, ArrayList<Task> tasks) {
        this.time = time;
        this.tasks = tasks;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ArrayList<Task> getEvents() {
        return tasks;
    }

    public void setEvents(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }
}
