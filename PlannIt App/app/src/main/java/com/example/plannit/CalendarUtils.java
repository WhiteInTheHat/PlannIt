package com.example.plannit;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class CalendarUtils {
    public static LocalDate selectedDate;

    public static ArrayList<Task> tasksForDate(LocalDate date){
        ArrayList<Task> tasks = new ArrayList<>();
        LocalDate dueDate;

        for (Task task:CalendarFragment.tasks){
            dueDate= DateToLocalDate(task.getDueDate());
            if (dueDate.equals(date)){
                tasks.add(task);
                Log.d("Tasks", task.getName());
            }
        }

        return tasks;
    }
    public static Integer noTasksForDate(LocalDate date){
        Integer taskCount = 0;
        LocalDate dueDate;

        for (Task task:CalendarFragment.tasks){
            dueDate= DateToLocalDate(task.getDueDate());
            if (dueDate.equals(date)){
                taskCount++;
            }
        }

        return taskCount;
    }
    public static String formattedDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        return date.format(formatter);
    }

    public static String formattedTime(LocalTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return time.format(formatter);
    }

    public static String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    public static String monthDayFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d");
        return date.format(formatter);
    }
    public static ArrayList<LocalDate> daysInMonthArray(LocalDate date) {
        ArrayList<LocalDate> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(CalendarUtils.selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate prevMonth = CalendarUtils.selectedDate.minusMonths(1);
        LocalDate nextMonth = CalendarUtils.selectedDate.plusMonths(1);

        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        YearMonth nextYearMonth = YearMonth.from(nextMonth);
        int nextDaysInMonth = nextYearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i<=42;i++){
            if (i < dayOfWeek){
                //daysInMonthArray.add(null);
                daysInMonthArray.add(LocalDate.of(prevMonth.getYear(),prevMonth.getMonth(),prevDaysInMonth-dayOfWeek+i+1));
            }
            else if (i >= daysInMonth + dayOfWeek) {
                //daysInMonthArray.add(null);
                daysInMonthArray.add(LocalDate.of(nextMonth.getYear(),nextMonth.getMonth(),i-dayOfWeek+1-daysInMonth));
            }
            else{
                daysInMonthArray.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i-dayOfWeek+1)); // possibly need to add the ability to have current date red or highlighted for ease of use
            }
        }
        //Log.d("daysInMonthArray", String.valueOf(daysInMonthArray));
        return daysInMonthArray;
    }
    public static ArrayList<LocalDate> daysInWeekArray(LocalDate selectedDate) {
        ArrayList<LocalDate> days = new ArrayList<>();
        LocalDate current = mondayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while(current.isBefore(endDate)){
            days.add(current);
            current=current.plusDays(1);
        }

        return days;
    }

    private static LocalDate mondayForDate(LocalDate current) {
        if (current.getDayOfWeek() == DayOfWeek.MONDAY)return current;
        LocalDate oneWeekAgo = current.minusWeeks(1);
        while (current.isAfter(oneWeekAgo)){
            if(current.getDayOfWeek() == DayOfWeek.MONDAY)return current;
            current = current.minusDays(1);
        }
        return null;
    }

    public static ArrayList<Task> tasksForDateAndTime(LocalDate date, LocalTime time){
        ArrayList<Task> tasks = new ArrayList<>();

        for (Task task:CalendarFragment.tasks){
            LocalTime taskTime;
            if (task.getDueTime().equals("Time:")){
                taskTime = LocalTime.of(0,0);
            }
            else {
                taskTime = LocalTime.parse(task.getDueTime());
            }
            int taskHour = taskTime.getHour();
            int cellHour = time.getHour();
            if (DateToLocalDate(task.getDueDate()).isEqual(date) && taskHour == cellHour){
                tasks.add(task);
            }
        }

        return tasks;
    }
    private static LocalDate DateToLocalDate(Date date) {
        // Convert java.util.Date to Instant
        Instant instant = date.toInstant();

        // Convert Instant to ZonedDateTime using system default time zone
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zonedDateTime = instant.atZone(zoneId);

        // Extract LocalDate from ZonedDateTime
        LocalDate localDate = zonedDateTime.toLocalDate();
        return localDate;
    }
    public static ArrayList<Integer> tasksInMonthArray(LocalDate date) {
        ArrayList<Integer> numberOfTasksArray = new ArrayList<>();
        int numberOfTasks;

        YearMonth yearMonth = YearMonth.from(CalendarUtils.selectedDate);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate prevMonth = CalendarUtils.selectedDate.minusMonths(1);
        LocalDate nextMonth = CalendarUtils.selectedDate.plusMonths(1);

        YearMonth prevYearMonth = YearMonth.from(prevMonth);
        int prevDaysInMonth = prevYearMonth.lengthOfMonth();

        YearMonth nextYearMonth = YearMonth.from(nextMonth);
        int nextDaysInMonth = nextYearMonth.lengthOfMonth();

        LocalDate firstOfMonth = CalendarUtils.selectedDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for (int i = 1; i<=42;i++){
            if (i < dayOfWeek){
                //daysInMonthArray.add(null);
                numberOfTasks = noTasksForDate(LocalDate.of(prevMonth.getYear(),prevMonth.getMonth(),prevDaysInMonth-dayOfWeek+i+1));
                numberOfTasksArray.add(numberOfTasks);
            }
            else if (i >= daysInMonth + dayOfWeek) {
                //daysInMonthArray.add(null);
                numberOfTasks = noTasksForDate(LocalDate.of(nextMonth.getYear(),nextMonth.getMonth(),i-dayOfWeek+1-daysInMonth));
                numberOfTasksArray.add(numberOfTasks);
            }
            else{
                numberOfTasks = noTasksForDate(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i-dayOfWeek+1)); // something about red highlighting on actual date
                numberOfTasksArray.add(numberOfTasks); // Would can through the task list and look for all tasks with the current date in the list
            }
        }
        //Log.d("daysInMonthArray", String.valueOf(daysInMonthArray));
        return numberOfTasksArray;
    }
    public static ArrayList<Integer> tasksInWeekArray(LocalDate selectedDate) {
        ArrayList<Integer> taskNumber = new ArrayList<>();
        LocalDate current = mondayForDate(selectedDate);
        LocalDate endDate = current.plusWeeks(1);

        while(current.isBefore(endDate)){
            //For every task in list if task.date == current currentNumber++;taskNumber.add(currentNumber);
            taskNumber.add(noTasksForDate(current));
            current=current.plusDays(1);
        }

        return taskNumber;
    }


}
