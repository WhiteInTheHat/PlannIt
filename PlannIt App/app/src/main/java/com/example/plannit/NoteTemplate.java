// TODO javadoc

package com.example.plannit;


import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class NoteTemplate extends AppCompatActivity {


    private String noteTitle;
    private int noteId;
    private EditText txtEditor;
    private EditText noteTitleView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_template);
        txtEditor = (EditText) findViewById(R.id.noteText);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            // This is what is displayed at the top of the screen
            noteTitle = extras.getString("Title");
            // This is used for knowing what note to modify
            noteId = extras.getInt("noteId");

            // This is setting the note title to
            noteTitleView = findViewById(R.id.noteTitleView);

            noteTitleView.setText(noteTitle);
        }

        this.setTitle(noteTitle);
        readNoteContent();

    }

    // TODO Javadoc
    @Override
    public void onBackPressed() {
        try {
            changeContent();
            finish();
        } catch (Throwable t) {
            Toast.makeText(this, "Exception: "+t.toString(), Toast.LENGTH_LONG).show();
        }

    }

    // TODO Javadoc
    private void readNoteContent() {

        try {
            Document doc = xmlUtils.loadNotesXML(this);
            NodeList nodeList = doc.getElementsByTagName("Note");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;
                    String noteIdNode = element.getElementsByTagName("NoteID").item(0).getTextContent();
                    String noteIdString = Integer.toString(noteId);
                    if (noteIdString.equals(noteIdNode)) {
                        String noteContent = element.getElementsByTagName("NoteContent").item(0).getTextContent();
                        txtEditor.setText(noteContent);
                    }
                }
            }


        } catch (Throwable t) {
            Toast.makeText(this, "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }


    }

    // TODO Javadoc
    private void changeContent(){
        try{
            Document doc = xmlUtils.loadNotesXML(this);
            NodeList listOfNotes = doc.getElementsByTagName("Note");

            for (int i = 0; i < listOfNotes.getLength(); i++) {
                Node note = listOfNotes.item(i);
                if (note.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) note;
                    String noteIdNode = eElement.getElementsByTagName("NoteID").item(0).getTextContent();
                    String noteIdString = Integer.toString(noteId);
                    if (noteIdString.equals(noteIdNode)) {
                        eElement.getElementsByTagName("NoteContent").item(0).setTextContent(txtEditor.getText().toString());
                        String noteTitleName = noteTitleView.getText().toString();
                        eElement.getElementsByTagName("NoteTitle").item(0).setTextContent(noteTitleName);
                    }

                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            OutputStreamWriter tasksXML = new OutputStreamWriter(openFileOutput("Notes.xml", 0));
            StreamResult result = new StreamResult(tasksXML);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(source, result);
            tasksXML.flush();
            tasksXML.close();
        } catch (IOException | TransformerException e) {
            Toast.makeText(this, "Exception: "+e.toString(), Toast.LENGTH_LONG).show();

        }
    }

}



