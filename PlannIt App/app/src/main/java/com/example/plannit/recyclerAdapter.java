package com.example.plannit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class recyclerAdapter extends RecyclerView.Adapter<recyclerAdapter.MyViewHolder> {

    // Variable declarations required for the RecyclerAdaptar Constructor
    private final recyclerViewInterface RecyclerViewInterface;
    // Stores a List of Notes. This is what is used to display the notes on the screen
    private final ArrayList<Note> noteList;

    /**
     * This is the constructor for the recyclerAdapter
     * The recyclerAdapter is used convert an object at a posistion into a list row item to be inserted
     * @param RecyclerViewInterface The interface that the RecyclerView will be using
     * @param noteList The list of notes that will store the notes to be displayed on the screen
     */
    public recyclerAdapter(recyclerViewInterface RecyclerViewInterface, ArrayList<Note> noteList) {
        this.RecyclerViewInterface = RecyclerViewInterface;
        this.noteList = noteList;
    }


    /**
     * MyViewHolder is a wrapper around a view that contains the layout and method of an item in the list
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        // In this section, I define all the Views that a list item contains so that I can later use them if needed
        private TextView noteTitle;
        private ImageButton settingsButton;

        // ViewHolder object which describes and provides access to all the views within each item row.
        public MyViewHolder(final View view, recyclerViewInterface recyclerViewInterface){
            super(view);

            // Assigning the views to their respective variables
            noteTitle = view.findViewById(R.id.noteTitleRecycler);
            settingsButton = view.findViewById(R.id.settingsButtonRecycler);
            settingsButton.setOnClickListener(this);

            // Creates the ability to click the list item to go to a new activity
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

            Note currentNote = noteList.get(getAdapterPosition());
            if (currentNote.getIsNoteLocked()) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.note_options_decrypt, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
            }
            else {
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.note_options, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(this);
                popupMenu.show();
            }

        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            int position = getAdapterPosition();
            RecyclerViewInterface.onRvMenuItem(position, menuItem, noteList.get(position));
            return false;
        }
    }


    // Involves inflating the layout of the XML and returning a new holder
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the layout
        View noteView = inflater.inflate(R.layout.note_recycler_layout,parent,false);

        // return the new look after adding something new
        return new MyViewHolder(noteView,RecyclerViewInterface);
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String noteTitleRecycler = noteList.get(position).getNoteName();
        holder.noteTitle.setText(noteTitleRecycler);
    }

    // Returns the total amount of items in the notelist (The list which is used to create the RecyclerAdaptar)
    @Override
    public int getItemCount() {
        return noteList.size();
    }



}
