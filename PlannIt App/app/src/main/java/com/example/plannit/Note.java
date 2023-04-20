package com.example.plannit;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * This is a note object. It contains the attributes that a note has and contains some the methods
 * the note will have. It has getters and setters for the attributes of the note and also has constructors for the
 * object.
 */
public class Note {

    // These are all the attributes that belong to a note object.
    private String noteTitle;
    private int noteId;
    private String noteContent;
    private boolean isNoteLocked;


    // Constructors
    public Note(String noteTitle, String noteContent, boolean isNoteLocked) {
        this.noteTitle = noteTitle;
        setId();
        this.noteContent = noteContent;
        this.isNoteLocked = isNoteLocked;
    }

    public Note(String noteTitle, int noteId, String noteContent, boolean isNoteLocked) {
        this.noteTitle = noteTitle;
        this.noteId = noteId;
        this.noteContent = noteContent;
        this.isNoteLocked = isNoteLocked;
    }

    // Getters + Setters

    public String getNoteName() {
        return noteTitle;
    }

    public void setNoteName(String noteTitle) {
        this.noteTitle = noteTitle;
    }

    public int getId() {
        return noteId;
    }

    public void setId() {
        this.noteId = idGenerator();
    }
    public boolean getIsNoteLocked() {
        return isNoteLocked;
    }

    public void setNoteLocked(boolean noteLocked) {
        isNoteLocked = noteLocked;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }


    // NOTE OPTIONS METHODS
    /**
     * This method is used to DELETE a specific note a user no longer wants.
     * @param context Takes in the context that is going to be used to display any Toast Messages
     */
    public void deleteNote(Context context) {

        removeNodeXML(context);
        Toast.makeText(context,noteTitle+".txt has been deleted",Toast.LENGTH_LONG).show();
    }

    /**
     * This method is used to RENAME the note to a new name.
     * @param newNoteTitle Takes in the new note title name
     * @param context Takes in the context that is going to be used to display any Toast Messages.
     */
    public void renameNote(Context context, String newNoteTitle){
        changeNodeNameXML(newNoteTitle,context);
    }

    // TODO Personalise this code
    public void exportNote(Context context) {
        try {
            OutputStream outputStream;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                ContentValues contentValues = new ContentValues();

                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, noteTitle+".txt");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri extVolumeUri = MediaStore.Files.getContentUri("external");
                Uri fileUri = context.getContentResolver().insert(extVolumeUri, contentValues);

                try {
                    outputStream = context.getContentResolver().openOutputStream(fileUri);
                    byte[] bytes = noteContent.getBytes();
                    outputStream.write(bytes);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

            } else {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File file = new File(path, noteTitle+".txt");
                try {
                    outputStream = new FileOutputStream(file);
                    byte[] bytes = noteContent.getBytes();
                    outputStream.write(bytes);
                    outputStream.close();

                } catch (FileNotFoundException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        } catch (RuntimeException | IOException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This will perform PBE using the password provided and a fixed salt and iteration count
     * @param context This takes in the activity that will be used to display the toast
     * @param password This takes in the password to lock the note
     */
    public void encryptNote(Context context, String password) {

        try{
            // Initialisation of Basic Variables
            PBEKeySpec pbeKeySpec;
            PBEParameterSpec pbeParamSpec;
            SecretKeyFactory keyFac;

            // Salt
            byte[] salt = { (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c, (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99 };
            // Iteration count
            int count = 2048;
            // Text that we will be encrypting
            byte[]  plaintext = noteContent.getBytes();
            // Password to be used
            char[]  passwordCharArray = password.toCharArray();

            // Key Generation Process START //
            // Create PBE parameter set
            pbeParamSpec = new PBEParameterSpec(salt, count);
            //Create parameter for key generation
            pbeKeySpec = new PBEKeySpec(passwordCharArray);
            // Create instance of SecretKeyFactory for password-based encryption
            // using DES and MD5
            keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            // Generate a key
            Key pbeKey = keyFac.generateSecret(pbeKeySpec);
            // Create PBE Cipher
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
            // Key Generation Process END //
            // Encryption Process START //
            byte[]  ciphertext = pbeCipher.doFinal(plaintext);
            noteContent = toHex(ciphertext);
            isNoteLocked = true;
            // Encryption Process END //
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                 IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException |
                 BadPaddingException | InvalidKeyException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    /**
     * This will perform PBD using the password provided and a fixed salt and iteration count
     * @param context This takes in the activity that will be used to display the toast
     * @param password This takes in the password to unlock the note
     */
    public void decryptNote(Context context, String password) {

        try{
            PBEKeySpec pbeKeySpec;
            PBEParameterSpec pbeParamSpec;
            SecretKeyFactory keyFac;

            // Salt
            byte[] salt = { (byte)0xc7, (byte)0x73, (byte)0x21, (byte)0x8c, (byte)0x7e, (byte)0xc8, (byte)0xee, (byte)0x99 };
            // Iteration count
            int count = 2048;
            // Password to be used
            char[]  passwordCharArray = password.toCharArray();
            // CipherText
            // Fixed ciphertext
            String string = noteContent;
            byte[] ciphertext = new byte[string.length() / 2];
            for (int i = 0; i < ciphertext.length; i++) {
                int index = i * 2;

                // Using parseInt() method of Integer class
                int val = Integer.parseInt(string.substring(index, index + 2), 16);
                ciphertext[i] = (byte)val;
            }

            // Key Generation //

            // Create PBE parameter set
            pbeParamSpec = new PBEParameterSpec(salt, count);

            //Create parameter for key generation
            pbeKeySpec = new PBEKeySpec(passwordCharArray);

            // Create instance of SecretKeyFactory for password-based encryption
            // using DES and MD5
            keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");

            // Generate a key
            Key pbeKey = keyFac.generateSecret(pbeKeySpec);

            // Create PBE Cipher
            Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
            // decrypt the ciphertext
            byte[] plaintext = pbeCipher.doFinal(ciphertext);
            noteContent = new String (plaintext);
            isNoteLocked = false;

        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                 IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException |
                 BadPaddingException | InvalidKeyException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    // HELPER FUNCTIONS


    // Random Number Generator that will generate a random id for the note

    /**
     * This is a Random Number Generator that will generate a random id for a note created by the user
     * @return Return an ID for a note
     */
    private int idGenerator(){
        Random random = new Random();
        int maxValue = 2147483600;
        int noteId = random.nextInt(maxValue);
        return noteId;
    }

    /**
     * This will load the Notes.xml file
     * @param context The activity that is running the notes section
     * @return This will return a document that will be used to update the Notes.xml file
     */
    private Document loadXML(Context context)  {
        try {
            String xmlFile = "Notes.xml";
            File dir = context.getFilesDir();
            File XMLFILE = new File(dir, xmlFile);

            if (XMLFILE.isFile()) {
            } else {
                try {
                    OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput(xmlFile, 0));
                    String str = ("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n" +
                            "<Notes>\n" +
                            "</Notes>\n");
                    out.write(str);
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }

            }

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            return docBuilder.parse(XMLFILE);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    //TODO
    /**
     * This function takes in
     * @param newNoteTitle
     * @param context
     */
    private void changeNodeNameXML(String newNoteTitle, Context context)  {
        try {
            Document doc = loadXML(context);
            NodeList listOfNotes = doc.getElementsByTagName("Note");

            for (int i = 0; i < listOfNotes.getLength(); i++) {
                Node note = listOfNotes.item(i);
                if (note.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) note;
                    String noteTitleNode = eElement.getElementsByTagName("NoteTitle").item(0).getTextContent();
                    if (noteTitle.equals(noteTitleNode)) {
                        eElement.getElementsByTagName("NoteTitle").item(0).setTextContent(newNoteTitle);
                    }

                }
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
        } catch (IOException | TransformerException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * This will remove the note the user has selected from the XML document and as a result permanently delete it
     * @param context This stores the activity where the toast will be displayed on.
     */
    private void removeNodeXML(Context context)  {
        try {
            Document doc = loadXML(context);
            NodeList listOfNotes = doc.getElementsByTagName("Note");

            for (int i = 0; i < listOfNotes.getLength(); i++) {
                Node note = listOfNotes.item(i);
                if (note.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) note;
                    String noteIdNode = eElement.getElementsByTagName("NoteID").item(0).getTextContent();
                    String noteIdString = Integer.toString(noteId);
                    if (noteIdString.equals(noteIdNode)) {
                        note.getParentNode().removeChild(note);
                    }

                }
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
        } catch (IOException | TransformerException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Return the hex representation of length bytes of data.
     * @param data the bytes to be converted.
     * @param length the number of bytes in the data block to be converted.
     * @return a hex representation of length bytes of data.
     */
    public static String toHex(byte[] data, int length)
    {
        String digits = "0123456789abcdef";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i != length; i++)
        {
            int	value = data[i] & 0xff;
            stringBuffer.append(digits.charAt(value >> 4));
            stringBuffer.append(digits.charAt(value & 0xf));
        }
        return stringBuffer.toString();
    }

    /**
     * Return the passed in byte array as a hex string.
     *
     * @param data the bytes to be converted.
     * @return a hex representation of data.
     */
    public static String toHex(byte[] data)
    {
        return toHex(data, data.length);
    }




}
