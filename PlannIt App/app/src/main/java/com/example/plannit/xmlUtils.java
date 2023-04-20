package com.example.plannit;

import android.content.Context;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class xmlUtils {


    // XML UTIL FOR SCHEDULES

    /**
     * This will create a Document Object from the Schedule XML file stored on the users device
     * @param context This is the context the user is calling the function from
     * @return This will return the Document that the XML file is associated to
     */
    public static Document loadScheduleXML(Context context) {
        try {
            String xmlFile = "Schedule.xml";
            File dir = context.getFilesDir();
            File XMLFILE = new File(dir, xmlFile);


            if (XMLFILE.isFile()) {

            } else {
                try {
                    OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(xmlFile, 0));
                    String str = ("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n" +
                            "<Schedules>\n" +
                            "</Schedules>\n");
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

        } catch (ParserConfigurationException | SAXException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // An IOException has occured
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }


        return null;
    }

    /**
     * This will convert the Document code into Schedules
     * @param context - This is the context the user is calling the function from
     * @return - This will return an ArrayList of all schedules the user has created
     */
    public static ArrayList<Schedule> loadSchedules(Context context) {
        ArrayList<Schedule> allSchedules = new ArrayList<>();
        Document doc = loadScheduleXML(context);
        NodeList scheduleNodeList = doc.getElementsByTagName("Schedule");
        for (int i = 0; i < scheduleNodeList.getLength(); i++)
        {
            Node scheduleNode = scheduleNodeList.item(i);
            if (scheduleNode.getNodeType() == Node.ELEMENT_NODE)
            {
                Element element = (Element) scheduleNode;

                // Getting the note attributes
                int scheduleId = Integer.parseInt(element.getElementsByTagName("ScheduleID").item(0).getTextContent());
                String scheduleLabel = element.getElementsByTagName("Label").item(0).getTextContent();
                String scheduleStartTime = element.getElementsByTagName("StartTime").item(0).getTextContent();
                String scheduleEndTime = element.getElementsByTagName("EndTime").item(0).getTextContent();
                boolean isMonday = Boolean.parseBoolean(element.getElementsByTagName("IsMonday").item(0).getTextContent());
                boolean isTuesday = Boolean.parseBoolean(element.getElementsByTagName("IsTuesday").item(0).getTextContent());
                boolean isWednesday = Boolean.parseBoolean(element.getElementsByTagName("IsWednesday").item(0).getTextContent());
                boolean isThursday = Boolean.parseBoolean(element.getElementsByTagName("IsThursday").item(0).getTextContent());
                boolean isFriday = Boolean.parseBoolean(element.getElementsByTagName("IsFriday").item(0).getTextContent());
                boolean isSaturday = Boolean.parseBoolean(element.getElementsByTagName("IsSaturday").item(0).getTextContent());
                boolean isSunday = Boolean.parseBoolean(element.getElementsByTagName("IsSunday").item(0).getTextContent());
                boolean isReoccurring = Boolean.parseBoolean(element.getElementsByTagName("IsReoccurring").item(0).getTextContent());
                String dateCreated = element.getElementsByTagName("DateCreated").item(0).getTextContent();


                // Constructing the note
                Schedule schedule = new Schedule(scheduleId,scheduleLabel,scheduleStartTime,scheduleEndTime,isMonday,isTuesday,isWednesday,isThursday,isFriday,isSaturday,isSunday,isReoccurring,dateCreated);
                // Adding the note to the list
                allSchedules.add(schedule);
            }
        }
        return allSchedules;
    }

    /**
     * This function is used to update the Schedule XML if anything new is added or deleted or edited
     * @param context - This is the context the user is calling the function from
     * @param scheduleList - This is the new Schedule List that is needed to update the Schedule file
     */
    public static void writeScheduleXML(Context context, ArrayList<Schedule> scheduleList) {
        try {
            Document doc = loadScheduleXML(context);

            NodeList rootList = doc.getElementsByTagName("Schedules");
            Node root = rootList.item(0);

            // Goes through the entire noteList and updates the XML document to contain the new notes if any.
            for (Schedule schedule : scheduleList) {
                Element noteElement = doc.createElement("Schedule");
                root.appendChild(noteElement);

                Element scheduleIDElement = doc.createElement("ScheduleID");
                scheduleIDElement.appendChild(doc.createTextNode(Integer.toString(schedule.getScheduleID())));
                noteElement.appendChild(scheduleIDElement);

                Element scheduleLabelElement = doc.createElement("Label");
                scheduleLabelElement.appendChild(doc.createTextNode(schedule.getLabel()));
                noteElement.appendChild(scheduleLabelElement);

                Element scheduleStartTime = doc.createElement("StartTime");
                scheduleStartTime.appendChild(doc.createTextNode(schedule.getStartDate()));
                noteElement.appendChild(scheduleStartTime);

                Element scheduleEndTime = doc.createElement("EndTime");
                scheduleEndTime.appendChild(doc.createTextNode(schedule.getEndDate()));
                noteElement.appendChild(scheduleEndTime);

                Element isMondayElement = doc.createElement("IsMonday");
                isMondayElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isMonday())));
                noteElement.appendChild(isMondayElement);

                Element isTuesdayElement = doc.createElement("IsTuesday");
                isTuesdayElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isTuesday())));
                noteElement.appendChild(isTuesdayElement);

                Element isWednesdayElement = doc.createElement("IsWednesday");
                isWednesdayElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isWednesday())));
                noteElement.appendChild(isWednesdayElement);

                Element isThursdayElement = doc.createElement("IsThursday");
                isThursdayElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isThursday())));
                noteElement.appendChild(isThursdayElement);

                Element isFridayElement = doc.createElement("IsFriday");
                isFridayElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isFriday())));
                noteElement.appendChild(isFridayElement);

                Element isSaturdayElement = doc.createElement("IsSaturday");
                isSaturdayElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isSaturday())));
                noteElement.appendChild(isSaturdayElement);

                Element isSundayElement = doc.createElement("IsSunday");
                isSundayElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isSunday())));
                noteElement.appendChild(isSundayElement);

                Element isReoccurringElement = doc.createElement("IsReoccurring");
                isReoccurringElement.appendChild(doc.createTextNode(Boolean.toString(schedule.isReoccurring())));
                noteElement.appendChild(isReoccurringElement);

                Element dateCreated = doc.createElement("DateCreated");
                dateCreated.appendChild(doc.createTextNode(schedule.getDateCreated()));
                noteElement.appendChild(dateCreated);

            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            OutputStreamWriter scheduleXML = new OutputStreamWriter(context.openFileOutput("Schedule.xml", 0));
            StreamResult result = new StreamResult(scheduleXML);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(source, result);
            scheduleXML.flush();
            scheduleXML.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // XML UTIL FOR NOTES

    /**
     * Load notes will read the Notes.xml document that stores all the notes the user has created in a persistant way.
     * Upon reading the notes, it will then for each Note node, create a Note Object assigning its respective value and
     * putting it inside the noteList.
     */
    public static void loadNotes(Context context, ArrayList<Note> noteList) {
        Document doc = loadNotesXML(context);
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
                noteList.add(note);
            }
        }
    }

    /**
     * This will convert an XML file into a Document Object
     * @param context - This is the context the user is calling the function from
     * @return - This will return the Document associated with the Notes XML file
     */
    public static Document loadNotesXML(Context context) {
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

        } catch (ParserConfigurationException | SAXException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            // An IOException has occured
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }


        return null;
    }

    /**
     * This will update the Notes XML if anything is added, deleted or edited
     * @param context - This is the context the user is calling the function from
     * @param noteList - This is the noteList that is to be added to the XML
     */
    public static void writeNoteXML(Context context, ArrayList<Note> noteList) {
        try {
            Document doc = loadNotesXML(context);

            NodeList rootList = doc.getElementsByTagName("Notes");
            Node root = rootList.item(0);

            // Goes through the entire noteList and updates the XML document to contain the new notes if any.
            for (Note note : noteList) {
                Element noteElement = doc.createElement("Note");
                root.appendChild(noteElement);

                Element noteTitleElement = doc.createElement("NoteTitle");
                noteTitleElement.appendChild(doc.createTextNode(note.getNoteName()));
                noteElement.appendChild(noteTitleElement);

                Element noteIdElement = doc.createElement("NoteID");
                noteIdElement.appendChild(doc.createTextNode(Integer.toString(note.getId())));
                noteElement.appendChild(noteIdElement);

                Element noteContentElement = doc.createElement("NoteContent");
                noteContentElement.appendChild(doc.createTextNode(note.getNoteContent()));
                noteElement.appendChild(noteContentElement);

                Element isNoteLockedElement = doc.createElement("IsNoteLocked");
                isNoteLockedElement.appendChild(doc.createTextNode(Boolean.toString(note.getIsNoteLocked())));
                noteElement.appendChild(isNoteLockedElement);


            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            OutputStreamWriter tasksXML = new OutputStreamWriter(context.openFileOutput("Notes.xml", 0));
            StreamResult result = new StreamResult(tasksXML);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(source, result);
            tasksXML.flush();
            tasksXML.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // XML FOR TASKS

    /**
     * This will read the Task XML and convert it into a Document Object
     * @param context - This is the context the user is calling the function from
     * @return - This is the Document Object created from the Task XML
     */
    public static Document readTaskXML(Context context)  {
        try {
            String xmlFile = "tasks.xml";
            File dir = context.getFilesDir();
            File fileXmlFile = new File(dir, xmlFile);
            if (fileXmlFile.isFile()) {
            } else {
                OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(xmlFile, 0));
                String str = ("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                        "<tasks>\n" +
                        "</tasks>\n");
                out.write(str);
                out.flush();
                out.close();
            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            return docBuilder.parse(fileXmlFile);
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * This will convert the Document Tasks into Task Objects
     * @param doc - Takes in the Document that was generated from the Task XML
     * @return - This will output a list of Tasks that are stored inside the XML
     */
    public static ArrayList<Task> loadTasks(Document doc) {

        try {
            ArrayList<Task> newTaskArray = new ArrayList<>();
            NodeList rootList = doc.getElementsByTagName("task");

            int numTasks = rootList.getLength();

            for (int i =0; i<numTasks;i++) {

                Node node = rootList.item(i);
                Element element = (Element) node;

                String name = element.getElementsByTagName("name").item(0).getTextContent();

                NodeList labelNodes = element.getElementsByTagName("label");
                ArrayList<String> labels = new ArrayList<>();
                for (int j = 0; j < labelNodes.getLength(); j++) {
                    Node labelNode = labelNodes.item(j);
                    labels.add(labelNode.getTextContent());
                }

                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date dueDate = dateFormat.parse(element.getElementsByTagName("DueDate").item(0).getTextContent());

                int priority = Integer.parseInt(element.getElementsByTagName("priority").item(0).getTextContent());

                NodeList subtaskNodes = element.getElementsByTagName("subtask");
                ArrayList<Subtask> subtasks = new ArrayList<>();
                for (int j = 0; j < subtaskNodes.getLength(); j++) {
                    Node subtaskNode = subtaskNodes.item(j);
                    subtasks.add(new Subtask(subtaskNode.getTextContent()));
                }

                NodeList noteIDNodes = element.getElementsByTagName("noteID");
                ArrayList<Integer> noteIDs = new ArrayList<>();
                for (int j = 0; j < noteIDNodes.getLength(); j++) {
                    Node noteIDNode = noteIDNodes.item(j);
                    noteIDs.add(Integer.parseInt(noteIDNode.getTextContent()));
                }

                Double duration = Double.parseDouble(element.getElementsByTagName("duration").item(0).getTextContent());
                String DueTime = element.getElementsByTagName("DueTime").item(0).getTextContent();

                Integer splits;

                try {
                    splits = Integer.parseInt(element.getElementsByTagName("splits").item(0).getTextContent());
                } catch (Exception e) {
                    splits = -1;
                }
                boolean planniOn = Boolean.parseBoolean(element.getElementsByTagName("planniOn").item(0).getTextContent());

                Integer taskID = Integer.parseInt(element.getElementsByTagName("taskID").item(0).getTextContent());
                boolean planniTask = Boolean.parseBoolean(element.getElementsByTagName("planniTask").item(0).getTextContent());
                Integer parentID = Integer.parseInt(element.getElementsByTagName("parentID").item(0).getTextContent());

                Task task = new Task(name, labels, dueDate, priority, subtasks, noteIDs , taskID, duration, DueTime, splits, planniOn,planniTask,parentID);
                newTaskArray.add(task);
            }

            return newTaskArray;

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * This function is used to update a single task in the Task XML document
     * @param task - The task to be inserted into the XML document
     * @param context - This is the context the user is calling the function from
     */
    public static void singleWriteTaskXML(Task task, Context context) {
        try {
            Document doc = readTaskXML(context);
            NodeList rootList = doc.getElementsByTagName("tasks");
            Node root = rootList.item(0);
            Element taskElement = doc.createElement("task");
            root.appendChild(taskElement);
            Element nameElement =  doc.createElement("name");
            nameElement.appendChild(doc.createTextNode(task.getName()));
            taskElement.appendChild(nameElement);
            Element labelsElement =  doc.createElement("labels");
            for (String label: task.getLabels()) {
                Element labelElement = doc.createElement("label");
                labelElement.appendChild(doc.createTextNode(label));
                labelsElement.appendChild(labelElement);
            }
            taskElement.appendChild(labelsElement);

            Element dateElement = doc.createElement("DueDate");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String strDate = dateFormat.format(task.getDueDate());
            dateElement.appendChild(doc.createTextNode(strDate));
            taskElement.appendChild(dateElement);

            Element priorityElement = doc.createElement("priority");
            priorityElement.appendChild(doc.createTextNode(String.valueOf(task.getPriority())));
            taskElement.appendChild(priorityElement);

            Element subtasksElement =  doc.createElement("subtasks");
            for (Subtask subtask: task.getSubtasks()) {
                Element subtaskElement = doc.createElement("subtask");
                subtaskElement.appendChild(doc.createTextNode(subtask.getContent()));
                subtasksElement.appendChild(subtaskElement);
            }
            taskElement.appendChild(subtasksElement);

            Element notesElement =  doc.createElement("noteIDs");
            try {
                for (int note : task.getNotes()) {
                    Element noteElement = doc.createElement("noteID");
                    noteElement.appendChild(doc.createTextNode(String.valueOf(note)));
                    notesElement.appendChild(noteElement);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            taskElement.appendChild(notesElement);

            Element taskIDElement = doc.createElement("taskID");
            taskIDElement.appendChild(doc.createTextNode(String.valueOf(task.getID())));
            taskElement.appendChild(taskIDElement);

            System.out.println(task.getDuration());
            Element durationElement = doc.createElement("duration");
            durationElement.appendChild(doc.createTextNode(String.valueOf(task.getDuration())));
            taskElement.appendChild(durationElement);

            Element timeElement = doc.createElement("DueTime");
            timeElement.appendChild(doc.createTextNode(task.getDueTime()));
            taskElement.appendChild(timeElement);

            Element splitElement = doc.createElement("splits");
            splitElement.appendChild(doc.createTextNode(String.valueOf(task.getSplits())));
            taskElement.appendChild(splitElement);

            Element planniElement = doc.createElement("planniOn");
            planniElement.appendChild(doc.createTextNode(Boolean.toString(task.isPlanni())));
            taskElement.appendChild(planniElement);

            Element planniTaskElement = doc.createElement("planniTask");
            planniTaskElement.appendChild(doc.createTextNode(Boolean.toString(task.isPlanniTask())));
            taskElement.appendChild(planniTaskElement);

            Element parentIDElement = doc.createElement("parentID");
            parentIDElement.appendChild(doc.createTextNode(String.valueOf(task.getParentID())));
            taskElement.appendChild(parentIDElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            OutputStreamWriter tasksXML = new OutputStreamWriter(context.openFileOutput("tasks.xml", 0));
            StreamResult result = new StreamResult(tasksXML);
            transformer.setOutputProperty(OutputKeys.INDENT,"true");
            transformer.transform(source, result);
            tasksXML.flush();
            tasksXML.close();
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This function updates the entire Task XML file using the new task list
     * @param context - This is the context the user is calling the function from
     * @param allTasks - The list of tasks to be stored in XML
     */
    public static void replaceTasks(Context context, ArrayList<Task> allTasks) {
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element tasksElement = doc.createElement("tasks");
            doc.appendChild(tasksElement);
            for (Task task : allTasks) {
                Element taskElement = doc.createElement("task");
                tasksElement.appendChild(taskElement);

                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(task.getName()));
                taskElement.appendChild(nameElement);

                Element labelsElement = doc.createElement("labels");
                for (String label : task.getLabels()) {
                    Element labelElement = doc.createElement("label");
                    labelElement.appendChild(doc.createTextNode(label));
                    labelsElement.appendChild(labelElement);
                }
                taskElement.appendChild(labelsElement);

                Element dateElement = doc.createElement("DueDate");
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String strDate = dateFormat.format(task.getDueDate());
                dateElement.appendChild(doc.createTextNode(strDate));
                taskElement.appendChild(dateElement);

                Element priorityElement = doc.createElement("priority");
                priorityElement.appendChild(doc.createTextNode(String.valueOf(task.getPriority())));
                taskElement.appendChild(priorityElement);

                Element subtasksElement = doc.createElement("subtasks");
                for (Subtask subtask : task.getSubtasks()) {
                    Element subtaskElement = doc.createElement("subtask");
                    subtaskElement.appendChild(doc.createTextNode(subtask.getContent()));
                    subtasksElement.appendChild(subtaskElement);
                }
                taskElement.appendChild(subtasksElement);

                Element notesElement = doc.createElement("noteIDs");
                try {
                    for (int note : task.getNotes()) {
                        Element noteElement = doc.createElement("noteID");
                        noteElement.appendChild(doc.createTextNode(String.valueOf(note)));
                        notesElement.appendChild(noteElement);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                taskElement.appendChild(notesElement);

                Element taskIDElement = doc.createElement("taskID");
                taskIDElement.appendChild(doc.createTextNode(String.valueOf(task.getID())));
                taskElement.appendChild(taskIDElement);

                Element durationElement = doc.createElement("duration");
                durationElement.appendChild(doc.createTextNode(String.valueOf(task.getDuration())));
                taskElement.appendChild(durationElement);

                Element timeElement = doc.createElement("DueTime");
                timeElement.appendChild(doc.createTextNode(task.getDueTime()));
                taskElement.appendChild(timeElement);

                Element splitElement = doc.createElement("splits");
                splitElement.appendChild(doc.createTextNode(String.valueOf(task.getSplits())));
                taskElement.appendChild(splitElement);

                Element planniElement = doc.createElement("planniOn");
                planniElement.appendChild(doc.createTextNode(Boolean.toString(task.isPlanni())));
                taskElement.appendChild(planniElement);

                Element planniTaskElement = doc.createElement("planniTask");
                planniTaskElement.appendChild(doc.createTextNode(Boolean.toString(task.isPlanniTask())));
                taskElement.appendChild(planniTaskElement);

                Element parentIDElement = doc.createElement("parentID");
                parentIDElement.appendChild(doc.createTextNode(String.valueOf(task.getParentID())));
                taskElement.appendChild(parentIDElement);


            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            OutputStreamWriter tasksXML = new OutputStreamWriter(context.openFileOutput("tasks.xml", 0));
            StreamResult result = new StreamResult(tasksXML);
            transformer.setOutputProperty(OutputKeys.INDENT, "true");
            transformer.transform(source, result);
            tasksXML.flush();
            tasksXML.close();

        } catch (ParserConfigurationException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
