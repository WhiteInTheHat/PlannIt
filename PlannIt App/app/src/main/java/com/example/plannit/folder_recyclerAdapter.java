package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class folder_recyclerAdapter extends RecyclerView.Adapter<folder_recyclerAdapter.MyViewHolder> {

    private ArrayList<String> folders;
    private taskRecyclerViewInterface recyclerViewInterface;

    public folder_recyclerAdapter(ArrayList<String> folders, taskRecyclerViewInterface recyclerViewInterface) {
        this.folders = folders;
        this.recyclerViewInterface  = recyclerViewInterface;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView folderName;

        public MyViewHolder(@NonNull View itemView, taskRecyclerViewInterface recyclerViewInterface) {

            super(itemView);
            folderName = itemView.findViewById(R.id.recyclerFolderName);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                recyclerViewInterface.onItemClick(pos);
            });
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.folder_layout, parent, false);
        return new MyViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.folderName.setText(folders.get(position));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItem(int position) {
        folders.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return folders.size();
    }


}