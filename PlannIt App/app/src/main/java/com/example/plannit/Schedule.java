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
import java.util.ArrayList;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class Schedule {

    // Schedules attributes
    private int scheduleID;
    private String label;
    private String startDate;
    private String endDate;
    private boolean isMonday;
    private boolean isTuesday;
    private boolean isWednesday;
    private boolean isThursday;
    private boolean isFriday;
    private boolean isSaturday;
    private boolean isSunday;
    private boolean isReoccurring;
    private String dateCreated;

    // Schedule Methods



    // Method that will extract the dates of a schedule, and the times
    public ArrayList<String> extractDateAndTimes() {

        ArrayList<String> output = new ArrayList<>();

        // First we store the startTime of the schedule in the first index
        output.add(startDate);
        output.add(endDate);
        // Then we go through every day and convert into text if true
        if (isMonday) {
            output.add("Monday");
        }
        if (isTuesday) {
            output.add("Tuesday");
        }
        if (isWednesday) {
            output.add("Wednesday");
        }
        if (isThursday) {
            output.add("Thursday");
        }
        if (isFriday) {
            output.add("Friday");
        }
        if (isSaturday) {
            output.add("Saturday");
        }
        if (isSunday) {
            output.add("Sunday");
        }

        return output;

    }




    private int generateID() {
        Random random = new Random();
        int maxValue = 2147483600;
        int scheduleID = random.nextInt(maxValue);
        return scheduleID;
    }


    public void removeNodeXML(Context context)  {
        try {
            Document doc = loadXML(context);
            NodeList listOfSchedules = doc.getElementsByTagName("Schedule");

            for (int i = 0; i < listOfSchedules.getLength(); i++) {
                Node schedule = listOfSchedules.item(i);
                if (schedule.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) schedule;
                    String scheduleIdNode = element.getElementsByTagName("ScheduleID").item(0).getTextContent();
                    String scheduleIdString = Integer.toString(scheduleID);
                    if (scheduleIdString.equals(scheduleIdNode)) {
                        schedule.getParentNode().removeChild(schedule);
                    }

                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            OutputStreamWriter tasksXML = new OutputStreamWriter(context.openFileOutput("Schedule.xml", 0));
            StreamResult result = new StreamResult(tasksXML);
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            transformer.transform(source, result);
            tasksXML.flush();
            tasksXML.close();
        } catch (IOException | TransformerException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    private Document loadXML(Context context) {
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




    // Constructors
    public Schedule(int scheduleID, String label, String startDate, String endDate, boolean isMonday, boolean isTuesday, boolean isWednesday, boolean isThursday, boolean isFriday, boolean isSaturday, boolean isSunday, boolean isReoccurring, String dateCreated) {
        this.scheduleID = scheduleID;
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isMonday = isMonday;
        this.isTuesday = isTuesday;
        this.isWednesday = isWednesday;
        this.isThursday = isThursday;
        this.isFriday = isFriday;
        this.isSaturday = isSaturday;
        this.isSunday = isSunday;
        this.isReoccurring = isReoccurring;
        this.dateCreated = dateCreated;
    }

    public Schedule(String label, String startDate, String endDate, boolean isMonday, boolean isTuesday, boolean isWednesday, boolean isThursday, boolean isFriday, boolean isSaturday, boolean isSunday, boolean isReoccurring, String dateCreated) {
        this.scheduleID = generateID();
        this.label = label;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isMonday = isMonday;
        this.isTuesday = isTuesday;
        this.isWednesday = isWednesday;
        this.isThursday = isThursday;
        this.isFriday = isFriday;
        this.isSaturday = isSaturday;
        this.isSunday = isSunday;
        this.isReoccurring = isReoccurring;
        this.dateCreated = dateCreated;
    }



    // Getters and Setters


    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getScheduleID() {
        return scheduleID;
    }

    public void setScheduleID(int scheduleID) {
        this.scheduleID = scheduleID;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isMonday() {
        return isMonday;
    }

    public void setMonday(boolean monday) {
        isMonday = monday;
    }

    public boolean isTuesday() {
        return isTuesday;
    }

    public void setTuesday(boolean tuesday) {
        isTuesday = tuesday;
    }

    public boolean isWednesday() {
        return isWednesday;
    }

    public void setWednesday(boolean wednesday) {
        isWednesday = wednesday;
    }

    public boolean isThursday() {
        return isThursday;
    }

    public void setThursday(boolean thursday) {
        isThursday = thursday;
    }

    public boolean isFriday() {
        return isFriday;
    }

    public void setFriday(boolean friday) {
        isFriday = friday;
    }

    public boolean isSaturday() {
        return isSaturday;
    }

    public void setSaturday(boolean saturday) {
        isSaturday = saturday;
    }

    public boolean isSunday() {
        return isSunday;
    }

    public void setSunday(boolean sunday) {
        isSunday = sunday;
    }

    public boolean isReoccurring() {
        return isReoccurring;
    }

    public void setReoccurring(boolean reoccurring) {
        isReoccurring = reoccurring;
    }
}
