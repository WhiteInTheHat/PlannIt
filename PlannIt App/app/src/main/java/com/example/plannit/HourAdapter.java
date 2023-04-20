package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HourAdapter extends ArrayAdapter<HourTask> {

    public HourAdapter(@NonNull Context context, List<HourTask> hourTasks) {
        super(context, 0, hourTasks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        HourTask task = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell,parent,false);
        }
        setHour(convertView, task.time);
        setEvents(convertView,task.tasks);

        return convertView;
    }

    private void setEvents(View convertView, ArrayList<Task> tasks) {
        ArrayList<Task> m0tasks = minuteTasks(tasks,0,15);
        ArrayList<Task> m15tasks = minuteTasks(tasks,15,15);
        ArrayList<Task> m30tasks = minuteTasks(tasks,30,15);
        ArrayList<Task> m45tasks = minuteTasks(tasks,45,15);

        TextView m0Event1 = convertView.findViewById(R.id.m0event1);
        TextView m0Event2 = convertView.findViewById(R.id.m0event2);
        TextView m0Event3 = convertView.findViewById(R.id.m0event3);
        if (m0tasks.size() == 0) {
            hideEvent(m0Event1);
            hideEvent(m0Event2);
            hideEvent(m0Event3);
        }
        else if (m0tasks.size() == 1) {
            setEvent(m0Event1, m0tasks.get(0));
            hideEvent(m0Event2);
            hideEvent(m0Event3);
        } else if (m0tasks.size() == 2) {
            setEvent(m0Event1, m0tasks.get(0));
            setEvent(m0Event2, m0tasks.get(1));
            hideEvent(m0Event3);
        } else if (m0tasks.size() == 3) {
            setEvent(m0Event1, m0tasks.get(0));
            setEvent(m0Event2, m0tasks.get(1));
            setEvent(m0Event3, m0tasks.get(2));
        } else {
            setEvent(m0Event1, m0tasks.get(0));
            setEvent(m0Event2, m0tasks.get(1));
            m0Event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(m0tasks.size() - 2);
            eventsNotShown += " More Events";
            m0Event3.setText(eventsNotShown);
        }

        TextView m15Event1 = convertView.findViewById(R.id.m15event1);
        TextView m15Event2 = convertView.findViewById(R.id.m15event2);
        TextView m15Event3 = convertView.findViewById(R.id.m15event3);
        if (m15tasks.size() == 0) {
            hideEvent(m15Event1);
            hideEvent(m15Event2);
            hideEvent(m15Event3);
        }
        else if (m15tasks.size() == 1) {
            setEvent(m15Event1, m15tasks.get(0));
            hideEvent(m15Event2);
            hideEvent(m15Event3);
        } else if (m15tasks.size() == 2) {
            setEvent(m15Event1, m15tasks.get(0));
            setEvent(m15Event2, m15tasks.get(1));
            hideEvent(m15Event3);
        } else if (m15tasks.size() == 3) {
            setEvent(m15Event1, m15tasks.get(0));
            setEvent(m15Event2, m15tasks.get(1));
            setEvent(m15Event3, m15tasks.get(2));
        } else {
            setEvent(m15Event1, m15tasks.get(0));
            setEvent(m15Event2, m15tasks.get(1));
            m15Event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(m15tasks.size() - 2);
            eventsNotShown += " More Events";
            m15Event3.setText(eventsNotShown);
        }

        TextView m30Event1 = convertView.findViewById(R.id.m30event1);
        TextView m30Event2 = convertView.findViewById(R.id.m30event2);
        TextView m30Event3 = convertView.findViewById(R.id.m30event3);
        if (m30tasks.size() == 0) {
            hideEvent(m30Event1);
            hideEvent(m30Event2);
            hideEvent(m30Event3);
        }
        else if (m30tasks.size() == 1) {
            setEvent(m30Event1, m30tasks.get(0));
            hideEvent(m30Event2);
            hideEvent(m30Event3);
        } else if (m30tasks.size() == 2) {
            setEvent(m30Event1, m30tasks.get(0));
            setEvent(m30Event2, m30tasks.get(1));
            hideEvent(m30Event3);
        } else if (m30tasks.size() == 3) {
            setEvent(m30Event1, m30tasks.get(0));
            setEvent(m30Event2, m30tasks.get(1));
            setEvent(m30Event3, m30tasks.get(2));
        } else {
            setEvent(m30Event1, m30tasks.get(0));
            setEvent(m30Event2, m30tasks.get(1));
            m30Event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(m30tasks.size() - 2);
            eventsNotShown += " More Events";
            m30Event3.setText(eventsNotShown);
        }

        TextView m45Event1 = convertView.findViewById(R.id.m45event1);
        TextView m45Event2 = convertView.findViewById(R.id.m45event2);
        TextView m45Event3 = convertView.findViewById(R.id.m45event3);
        if (m45tasks.size() == 0) {
            hideEvent(m45Event1);
            hideEvent(m45Event2);
            hideEvent(m45Event3);
        }
        else if (m45tasks.size() == 1) {
            setEvent(m45Event1, m45tasks.get(0));
            hideEvent(m45Event2);
            hideEvent(m45Event3);
        } else if (m45tasks.size() == 2) {
            setEvent(m45Event1, m45tasks.get(0));
            setEvent(m45Event2, m45tasks.get(1));
            hideEvent(m45Event3);
        } else if (m45tasks.size() == 3) {
            setEvent(m45Event1, m45tasks.get(0));
            setEvent(m45Event2, m45tasks.get(1));
            setEvent(m45Event3, m45tasks.get(2));
        } else {
            setEvent(m45Event1, m45tasks.get(0));
            setEvent(m45Event2, m45tasks.get(1));
            m45Event3.setVisibility(View.VISIBLE);
            String eventsNotShown = String.valueOf(m45tasks.size() - 2);
            eventsNotShown += " More Events";
            m45Event3.setText(eventsNotShown);
        }
    }

    private synchronized ArrayList<Task> minuteTasks(ArrayList<Task> tasks, int minute, int period) {
        ArrayList<Task> newTasks = new ArrayList<>();
        for (Task task : tasks) {
            int taskMinute;
            if (task.getDueTime().equals("Time:")){
                taskMinute = 0;
            }
            else {
                taskMinute = LocalTime.parse(task.getDueTime()).getMinute();
            }
            if (taskMinute >= minute && taskMinute < minute + period) {
                newTasks.add(task);
            }
        }
        return (newTasks);
    }



    private void setEvent(TextView textView, Task task) {
        textView.setText(task.getName()+" "+ String.valueOf(task.getDuration())+"h's");
        textView.setVisibility(View.VISIBLE);
    }

    private void hideEvent(TextView tv) {
        tv.setVisibility(View.INVISIBLE);
    }

    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        timeTV.setText(CalendarUtils.formattedTime(time));
    }
}
