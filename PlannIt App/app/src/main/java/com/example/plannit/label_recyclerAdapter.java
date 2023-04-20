package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class label_recyclerAdapter extends RecyclerView.Adapter<label_recyclerAdapter.MyViewHolder>{

    private ArrayList<String> labels;
    private Context context;

    public label_recyclerAdapter(Context context, ArrayList<String> labels) {
        this.context = context;
        this.labels = labels;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView content;
        ImageButton close;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            content = itemView.findViewById(R.id.lblContent);
            close = itemView.findViewById(R.id.lblDeleteButton);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.labelbubble, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.content.setText(labels.get(position));
        int position1 = holder.getAdapterPosition();
        holder.close.setOnClickListener(view -> removeItem(position1));
    }

    public void removeItem(int position) {
        try{
            labels.remove(position);
        }  catch (Exception e){
            labels.clear();
            e.printStackTrace();
        }
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }
}
