package com.example.plannit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;

public class NoteFragment extends Fragment implements recyclerViewInterface {

    private View view;
    private ArrayList<Note> noteList;
    private recyclerAdapter adapter;
    private Context context;
    private RecyclerView noteRecyclerView;

    /*
            ALL Variables being used for Popup Windows
     */
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText noteTitleEditText,newNoteTitle, notePassword;
    private Button confirmationButton, cancelationButton;


    /**
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return - This returns the fragment after being initialised
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_note,container,false);
        return view;

    }

    /**
     * This fills in the template created from onCreateView
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // Initialising Variables to be used through out the code.
        noteRecyclerView = getView().findViewById(R.id.noteRecyclerView);
        noteList = new ArrayList<Note>();
        context = getContext();

        // Carrying out methods that are required on setup
        xmlUtils.loadNotes(context,noteList); // Sets the noteList up
        initNoteRecyclerAdapter(); // Sets the Recycler Adapter up

    }


    /**
     * Upon resuming the note, it will update the recycler adapter to ensure it is the most up to date version
     */
    @Override
    public void onResume()
    {
        super.onResume();
        if (noteList != null) {
            updateAdapter();
        }
    }

    /**
     * This method will setup the recycler adapter that will be used to display the notes the user has.
     */
    private void initNoteRecyclerAdapter() {
        adapter = new recyclerAdapter(this, noteList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        noteRecyclerView.setLayoutManager(layoutManager);
        noteRecyclerView.setItemAnimator(new DefaultItemAnimator());
        noteRecyclerView.setAdapter(adapter);
    }

/*
        ALL METHODS WORKING WITH THE ADDITION OF A NOTE/FILE
 */


    /**
     * This method deals with the creation of a new note.
     * The user enters a note title, and using this information a new note is created.
     * This note is then added to the noteList and also the Notes.xml document
     */
    public void createNote(String noteTitle)  {

        // Checks to see if the entered text is empty
        if (noteTitle.isBlank()){
            Toast.makeText(getContext(),"This is not a valid title",Toast.LENGTH_LONG).show();
        }
        // Adds a new note
        else {
            // Creates a new note and adds it to the note list
            noteList.add(new Note(noteTitle,"",false));

            // Gets the Notes.xml file and deletes it.
            File dir = getContext().getFilesDir();
            File file = new File(dir, "Notes.xml");
            file.delete();
            // Creates a new Notes.xml file
            xmlUtils.loadNotesXML(context);
            // Writes into the new Notes.xml file all the notes. This is done to prevent duplicates of the note being created
            xmlUtils.writeNoteXML(context,noteList);
            // Refreshes the Recycler Adapter
            initNoteRecyclerAdapter();
        }




    }


/*
        METHODS DEALING WITH NOTE INTERACTIVITY
*/

    /**
     * This method will open the note that was clicked.
     * @param position - This returns the posistion of the item that was clicked in the array
     */
    @Override
    public void onItemClick(int position) {
        Note note = (noteList.get(position));
        if (note.getIsNoteLocked() == false) {
            // Creates an Intent, moving the user from activity to another.
            Intent intent = new Intent(getContext(), NoteTemplate.class);
            // Gets the note that was clicked and collects the data that is to be transferred into the new activity.
            intent.putExtra("Title",note.getNoteName());
            intent.putExtra("noteId",note.getId());
            // Starts the transferring
            startActivity(intent);
        }
        else {
            Toast.makeText(context,"Note is locked, please unlock it first!",Toast.LENGTH_LONG).show();
        }

    }


    /**
     * This code handles the drop down menu and what happens when a certain option is chosen
     * @param position The position in the recycler adapter that the note is in
     * @param menuItem This is the item that was clicked in the menu
     * @param note The specific note that was clicked
     */
    @Override
    public void onRvMenuItem(int position, MenuItem menuItem, Note note) {
        switch (menuItem.getItemId()) {
            case R.id.renameNote:
                renameDialog(note,context);
                break;
            case R.id.deleteNote:
                deletionConfirmationDialog(position,note);
                break;
            case R.id.encryptNote:
                encryptionDialog(note);
                break;
            case R.id.exportNote:
                exportConfirmationDialog(note);
                break;
        }
    }

/*
        DIALOG BOXES ASSOCIATED WITH NOTE OPTIONS
*/

    /**
     * This method deals with the delete note dialog box.
     * @param position The position in the recycler adapter
     * @param note The note that is to be deleted
     */
    private void deletionConfirmationDialog(int position, Note note){
        // Sets up the dialog box
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View confirmationDialogView = getLayoutInflater().inflate(R.layout.note_confirmation_popup,null);
        confirmationButton = confirmationDialogView.findViewById(R.id.confirmationButton);
        cancelationButton = confirmationDialogView.findViewById(R.id.cancelationButton);

        dialogBuilder.setView(confirmationDialogView);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Remove from the noteList
                noteList.remove(position);
                // Notify the adapter that an item has been removed
                adapter.notifyItemRemoved(position);
                // Delete the note from the XML Document
                note.deleteNote(context);
                // Closes the dialog box
                dialog.dismiss();
            }
        });

        cancelationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Closes the dialog box
                dialog.dismiss();
            }
        });
    }

    /**
     * This handles the dialog box that occurs when choosing the export a note
     * @param note - The note that is to be exported
     */
    private void exportConfirmationDialog(Note note){
        // Sets up the dialog box
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View confirmationDialogView = getLayoutInflater().inflate(R.layout.note_confirmation_popup,null);
        confirmationButton = confirmationDialogView.findViewById(R.id.confirmationButton);
        cancelationButton = confirmationDialogView.findViewById(R.id.cancelationButton);

        dialogBuilder.setView(confirmationDialogView);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.exportNote(context);
                dialog.dismiss();
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
     * This handles the dialog box that occurs when the user chooses the encrypt a note
     * @param note - The note to be encrypted
     */
    private void encryptionDialog(Note note){
        // Sets up the dialog box
        dialogBuilder = new AlertDialog.Builder(getContext());
        if (note.getIsNoteLocked()) {
            final View encryptionDialogView = getLayoutInflater().inflate(R.layout.note_decryption_popup,null);
            confirmationButton = encryptionDialogView.findViewById(R.id.confirmationButton);
            cancelationButton = encryptionDialogView.findViewById(R.id.cancelationButton);
            notePassword = encryptionDialogView.findViewById(R.id.addNoteTitle);
            dialogBuilder.setView(encryptionDialogView);
            dialog = dialogBuilder.create();
            dialog.show();
        }
        else {
            final View encryptionDialogView = getLayoutInflater().inflate(R.layout.note_encryption_popup,null);
            confirmationButton = encryptionDialogView.findViewById(R.id.confirmationButton);
            cancelationButton = encryptionDialogView.findViewById(R.id.cancelationButton);
            notePassword = encryptionDialogView.findViewById(R.id.addNoteTitle);
            dialogBuilder.setView(encryptionDialogView);
            dialog = dialogBuilder.create();
            dialog.show();
        }


        confirmationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String password = notePassword.getText().toString();
                Toast.makeText(context,password,Toast.LENGTH_LONG).show();
                // If the password has already been created
                if (isPasswordSaved() == true) {

                    if (comparePasswords(password) == true) {
                        if (note.getIsNoteLocked() == true) {
                            note.decryptNote(context,password);
                            Toast.makeText(context,"Decrypting the Note",Toast.LENGTH_LONG).show();
                            // Gets the Notes.xml file and deletes it.
                            File dir = getContext().getFilesDir();
                            File file = new File(dir, "Notes.xml");
                            file.delete();
                            // Creates a new Notes.xml file
                            xmlUtils.loadNotesXML(context);
                            // Writes into the new Notes.xml file all the notes. This is done to prevent duplicates of the note being created
                            xmlUtils.writeNoteXML(context,noteList);
                            dialog.dismiss();


                        }
                        else {
                            note.encryptNote(context,password);
                            Toast.makeText(context,"Encrypting the Note",Toast.LENGTH_LONG).show();


                            // Gets the Notes.xml file and deletes it.
                            File dir = getContext().getFilesDir();
                            File file = new File(dir, "Notes.xml");
                            file.delete();
                            // Creates a new Notes.xml file
                            xmlUtils.loadNotesXML(context);
                            // Writes into the new Notes.xml file all the notes. This is done to prevent duplicates of the note being created
                            xmlUtils.writeNoteXML(context,noteList);
                            dialog.dismiss();
                        }
                    }
                    else {
                        Toast.makeText(context,"That was the incorrect password",Toast.LENGTH_LONG).show();
                    }
                }
                // If the password does not exist, store password in shared preferences and encrypt note
                else {
                    savePassword(password);
                    note.encryptNote(context,password);
                    // Gets the Notes.xml file and deletes it.
                    File dir = getContext().getFilesDir();
                    File file = new File(dir, "Notes.xml");
                    file.delete();
                    // Creates a new Notes.xml file
                    xmlUtils.loadNotesXML(context);
                    // Writes into the new Notes.xml file all the notes. This is done to prevent duplicates of the note being created
                    xmlUtils.writeNoteXML(context,noteList);
                    dialog.dismiss();
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
     * This handles the dialog box that occurs when the user chooses the rename a note
     * @param note - The note to be renamed
     * @param context - The context where information is to be shown
     */
    private void renameDialog(Note note, Context context){
        // Sets up the dialog box
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View renameDialogView = getLayoutInflater().inflate(R.layout.note_rename_popup,null);
        confirmationButton = renameDialogView.findViewById(R.id.confirmationButton);
        cancelationButton = renameDialogView.findViewById(R.id.cancelationButton);
        newNoteTitle = renameDialogView.findViewById(R.id.addNoteTitle);


        dialogBuilder.setView(renameDialogView);
        dialog = dialogBuilder.create();
        dialog.show();

        confirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the noteTitle user wants to rename note to
                String renameNoteTitle = newNoteTitle.getText().toString();
                // Rename it
                note.renameNote(context,renameNoteTitle);
                // Update the Adapter that there has been a change to some of the contents
                updateAdapter();
                initNoteRecyclerAdapter();
                dialog.dismiss();
            }
        });

        cancelationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

/*
        HELPER FUNCTIONS
 */

    /**
     * This is used to store a password if it has never been created
     * @param password This is the password the user is setting to encrypt notes
     */
    private void savePassword(String password) {
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("PASSWORD", password);
        editor.apply();
    }

    /**
     * This is used to check whether a password has been set
     * @return
     */
    private boolean isPasswordSaved() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        return sharedPref.contains("PASSWORD");
    }

    /**
     * This function is used to verify if the password that is being used to decrypt/encrypt is equivalent to the one that has been saved
     * @param password - The inputted password
     * @return - A boolean value whether the password is a match or not
     */
    private boolean comparePasswords(String password) {
        SharedPreferences sharedPref = getContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE);
        String actualPassword = sharedPref.getString("PASSWORD","");
        if (actualPassword.equals(password)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Completely deletes the adapter and creates a new one with the most up-to date content
     */
    private void updateAdapter(){
        int size = noteList.size();
        noteList.clear();
        adapter.notifyItemRangeRemoved(0, size);
        xmlUtils.loadNotes(context,noteList);
    }

}
