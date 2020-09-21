package com.example.sanjay.erp.firestore;

 import android.support.annotation.Keep;

 import java.util.List;
@Keep
public class AnnouncementData {
    private String doneby;
    private String header;
    private String message;
    private List<String> userid;
   private long date;
    private List<String> content;
    private String donebyid;
public AnnouncementData(){

}
    public AnnouncementData(String doneby, String header, String message, List<String> userid, long date, List<String> content,String donebyid) {
        this.doneby = doneby;
        this.header = header;
        this.message = message;
        this.userid =  userid;
        this.donebyid=donebyid;
        this.date = date;
        this.content = content;
    }

    public void setUserid(List<String> userid) {
        this.userid = userid;
    }

    public String getDonebyid() {
        return donebyid;
    }

    public void setDonebyid(String donebyid) {
        this.donebyid = donebyid;
    }

//    public void setUserid(ArrayList userid) {
//        this.userid = userid;
//    }

    public String getDoneby() {
        return doneby;
    }

    public void setDoneby(String doneby) {
        this.doneby = doneby;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

        public List<String> getUserid() {
       return userid;
    }

//    public void setUserid(List userid) {
//        this.userid = (ArrayList) userid;
//    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }
}
