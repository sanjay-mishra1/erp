package com.example.sanjay.erp.newChatScreen;

import android.support.annotation.Keep;

/**
 * Created by sanjay on 12/03/2018.
 */
@Keep
public class album_allorders {
    private String User_Name;
    private String User_Img;
    private String Food_Image;
    private String Time;

    private String User_last_Message;
    private String Message;
      private String From;
    private String Date;
    private String key;
    private long Last_message_counter;
    private String Name;
    private String Online;
    private long LastSeen;
    private boolean Seen;
    private String CHATID;
    private String USERNAME;
    private String USERIMAGE;
    private String LASTMESSAGE;
    private String USERID;
    private String ROLLNO;
    private String TYPE;
    private Object SEARCHKEY;
    private long YEAR;
    private boolean isReceived;
///////////////present////////////////////////
    public String getCHATID() {
        return CHATID;
    }

    public String getTYPE() {
        return TYPE;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public Object getSEARCHKEY() {
        return SEARCHKEY;
    }

    public long getYEAR() {
        return YEAR;
    }

    public void setYEAR(long YEAR) {
        this.YEAR = YEAR;
    }

    public void setSEARCHKEY(Object SEARCHKEY) {
        this.SEARCHKEY =  SEARCHKEY;
    }

    public void setUser_Name(String user_Name) {
        User_Name = user_Name;
    }

    public void setUser_Img(String user_Img) {
        User_Img = user_Img;
    }

    public void setUser_last_Message(String user_last_Message) {
        User_last_Message = user_last_Message;
    }

    public String getFood_Image() {
        return Food_Image;
    }

    public void setFood_Image(String food_Image) {
        Food_Image = food_Image;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public void setMessage(String message) {
        Message = message;
    }





    public void setFrom(String from) {
        From = from;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLast_message_counter(long last_message_counter) {
        Last_message_counter = last_message_counter;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setOnline(String online) {
        Online = online;
    }

    public void setLastSeen(long lastSeen) {
        LastSeen = lastSeen;
    }

    public void setSeen(boolean seen) {
        Seen = seen;
    }

    public void setCHATID(String CHATID) {
        this.CHATID = CHATID;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public void setUSERIMAGE(String USERIMAGE) {
        this.USERIMAGE = USERIMAGE;
    }

    public void setLASTMESSAGE(String LASTMESSAGE) {
        this.LASTMESSAGE = LASTMESSAGE;
    }

    public void setUSERID(Object USERID) {
        this.USERID = (String) USERID;
    }

    public void setROLLNO(String ROLLNO) {
        this.ROLLNO = ROLLNO;
    }

    public String getLASTMESSAGE() {
        return LASTMESSAGE;
    }

    public String getUSERIMAGE() {
        return USERIMAGE;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public String getUSERID() {
        return USERID;
    }

    /////////////////////////////////
    public long getLastSeen() {
        return LastSeen;
    }

    public String getOnline() {
        return Online;
    }


    public String getName() {
        return Name;
    }

      public long getLast_message_counter() {
        return Last_message_counter;
    }


    public String getUser_Name() {
        return User_Name;
    }

    public String getUser_Img() {
        return User_Img;
    }

    public String getUser_last_Message() {
        return User_last_Message;
    }


    public album_allorders(){

    }

    public String getFrom() {
        return From;
    }

    public String getMessage() {
        return Message;
    }

    public String getDate() {
        return Date;
    }

    public String getKey() {
        return key;
    }


    public boolean isSeen() {
        return Seen;
    }

    public String getROLLNO() {
        return ROLLNO;
    }
}


