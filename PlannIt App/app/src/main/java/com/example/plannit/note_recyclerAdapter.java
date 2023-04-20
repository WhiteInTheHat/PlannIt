package com.example.plannit;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class note_recyclerAdapter extends RecyclerView.Adapter<note_recyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<Integer> chosenNotes;
    private Task task;
    private boolean isView;
    private ArrayList<Integer> theseNotes;

    public note_recyclerAdapter(Context context, Task task, boolean isView) {
        this.context = context;
        this.task = task;
        this.isView = isView;
        loadNotes();
        try {
            this.theseNotes = task.getNotes();
            this.chosenNotes = task.getNotes();
        } catch (Exception e) {
            this.theseNotes = new ArrayList<>();
            this.chosenNotes = new ArrayList<>();
        }

        ArrayList<Note> noteList = new ArrayList<>();
        if (isView){
            for (Note note: notes){
                for(int noteID: task.getNotes()) {
                    if (noteID == note.getId()){
                        noteList.add(note);
                    }
                }
            }
            notes=noteList;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.note_layout, parent, false);
        return new MyViewHolder(view, context, isView, notes);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.content.setText(note.getNoteName());

        if (isView){
            holder.checkBox.setVisibility(View.GONE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
        }

        if (theseNotes.contains(note.getId())) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            if (isChecked) {
                chosenNotes.add(note.getId());
                System.out.println(chosenNotes);
            } else {
                int index = chosenNotes.indexOf(note.getId());
                chosenNotes.remove(index);
                System.out.println(chosenNotes);
            }
        });

    }

    public void removeItem(int position) {
        notes.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView content;
        CheckBox checkBox;
        ImageView image;

        public MyViewHolder(@NonNull View itemView, Context context, boolean isView, ArrayList<Note> notes) {
            super(itemView);
            content = itemView.findViewById(R.id.noteContent);
            checkBox = itemView.findViewById(R.id.noteCheckbox);
            image = itemView.findViewById(R.id.noteImage);

            System.out.println("View: "+isView);

            itemView.setOnClickListener(view -> {
                int pos = getAdapterPosition();
                Note note = (notes.get(pos));
                if (!note.getIsNoteLocked()) {
                    // Creates an Intent, moving the user from activity to another.
                    Intent intent = new Intent(context, NoteTemplate.class);
                    // Gets the note that was clicked and collects the data that is to be transferred into the new activity.
                    intent.putExtra("Title", note.getNoteName());
                    intent.putExtra("noteId", note.getId());
                    // Starts the transferring
                    context.startActivity(intent);
                }   else {
                    Toast.makeText(context,"Note is locked, please unlock it first!",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public ArrayList<Note> getNotes(){

        return (noteIDstoNotes(this.chosenNotes));
    }

    private Document loadXML() {
        try {
            String xmlFile = "Notes.xml";
            File dir = context.getFilesDir();
            File XMLFILE = new File(dir, xmlFile);


            if (XMLFILE.isFile() == true) {

            } else {
                try {
                    OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(xmlFile, 0));
                    String str = ("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n" +
                            "<Notes>\n" +
                            "</Notes>\n");
                    out.write(str);
                    out.flush();
                    out.close();
                } catch (FileNotFoundException e) {
                    // Notes.XML does not exist
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // An IOException has occured
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }

            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(XMLFILE);
            return doc;

        } catch (ParserConfigurationException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // An IOException has occured
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        } catch (SAXException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }


        return null;
    }

    public ArrayList<Note> noteIDstoNotes(ArrayList<Integer> noteIDs) {
        ArrayList<Note> noteArrayList = new ArrayList<>();
        for (Integer noteID: noteIDs) {
            for (Note note: notes) {
                if (note.getId() == noteID) {
                    noteArrayList.add(note);
                }
            }
        }
        return noteArrayList;
    }

    private void loadNotes() {
        Document doc = loadXML();
        NodeList notenodeList = doc.getElementsByTagName("Note");
        for (int i = 0; i < notenodeList.getLength(); i++)
        {
            Node noteNode = notenodeList.item(i);
            if (noteNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) noteNode;

                // Getting the note attributes
                String noteTitle = element.getElementsByTagName("NoteTitle").item(0).getTextContent();
                int noteId = Integer.parseInt(element.getElementsByTagName("NoteID").item(0).getTextContent());
                String noteContent = element.getElementsByTagName("NoteContent").item(0).getTextContent();
                boolean isNoteLocked = Boolean.parseBoolean(element.getElementsByTagName("IsNoteLocked").item(0).getTextContent());
                // Constructing the note
                Note note = new Note(noteTitle, noteId,noteContent,isNoteLocked);
                // Adding the note to the list
                notes.add(note);
            }
        }
    }
}
