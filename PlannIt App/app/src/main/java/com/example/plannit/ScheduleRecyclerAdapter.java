package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ScheduleRecyclerAdapter extends RecyclerView.Adapter<ScheduleRecyclerAdapter.MyViewHolder>{

    private final recyclerViewInterface RecyclerViewInterface;
    private final ArrayList<Schedule> scheduleList;

    public ScheduleRecyclerAdapter(recyclerViewInterface RecyclerViewInterface, ArrayList<Schedule> scheduleList) {
        this.RecyclerViewInterface = RecyclerViewInterface;
        this.scheduleList = scheduleList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView labelTextView;
        private TextView startTextView;
        private TextView startDateTextView;
        private TextView endTextView;
        private TextView endDateTextView;
        private TextView activeDaysTextView;


        // ViewHolder object which describes and provides access to all the views within each item row.
        public MyViewHolder(final View view, recyclerViewInterface recyclerViewInterface) {
            super(view);

            // Assigning the views to their respective variables
            labelTextView = view.findViewById(R.id.scheduleLabelTemplate);
            startTextView = view.findViewById(R.id.startText);
            startDateTextView = view.findViewById(R.id.startDateTemplate);
            endTextView = view.findViewById(R.id.endText);
            endDateTextView = view.findViewById(R.id.endDateTemplate);
            activeDaysTextView = view.findViewById(R.id.daysActiveTemplate);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();

                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }


        @Override
        public void onClick(View view) {

        }
    }
    // Involves inflating the layout of the XML and returning a new holder
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View scheduleView = inflater.inflate(R.layout.schedulerecyclertemplate,parent,false);

        // return the new look after adding something new
        return new MyViewHolder(scheduleView,RecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String scheduleLabelRecycler = scheduleList.get(position).getLabel();
        holder.labelTextView.setText(scheduleLabelRecycler);
        String scheduleStartDateRecycler = scheduleList.get(position).getStartDate();
        holder.startDateTextView.setText(scheduleStartDateRecycler);
        String scheduleEndDateRecycler = scheduleList.get(position).getEndDate();
        holder.endDateTextView.setText(scheduleEndDateRecycler);
        holder.startTextView.setText("Start:");
        holder.endTextView.setText("End:");

        String scheduleActiveDateRecycler = "";
        if (scheduleList.get(position).isMonday()) {
            scheduleActiveDateRecycler += "Monday  ";
        }
        if (scheduleList.get(position).isTuesday()) {
            scheduleActiveDateRecycler += "Tuesday  ";
        }
        if (scheduleList.get(position).isWednesday()) {
            scheduleActiveDateRecycler += "Wednesday  ";
        }
        if (scheduleList.get(position).isThursday()) {
            scheduleActiveDateRecycler += "Thursday  ";
        }
        if (scheduleList.get(position).isFriday()) {
            scheduleActiveDateRecycler += "Friday  ";
        }
        if (scheduleList.get(position).isSaturday()) {
            scheduleActiveDateRecycler += "Saturday  ";
        }
        if (scheduleList.get(position).isSunday()) {
            scheduleActiveDateRecycler += "Sunday  ";
        }
        holder.activeDaysTextView.setText(scheduleActiveDateRecycler);

    }

    // Returns the total amount of items in the notelist (The list which is used to create the RecyclerAdaptar)
    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

}
