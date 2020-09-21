package com.example.sanjay.erp.newChatScreen;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.sanjay.erp.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

 import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.sanjay.erp.Constants.*;
 import static com.example.sanjay.erp.newChatScreen.help_activity.Uid;
import static com.example.sanjay.erp.newChatScreen.help_activity.UserName;
import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

class StoreMessage   {
     private EditText editText;
    private String IMG;
    private String caption;
    private RelativeLayout progressbar;
    private Context context;
    StoreMessage(String Type, String uid, EditText editText, RelativeLayout progressBar, String message, Context context){
        this.editText=editText;
        this.progressbar=progressBar;
        this.context=context;
        this.IMG=message;
        TotalHelp(Type,uid);
    }
    StoreMessage(String Type, String uid, EditText editText, RelativeLayout progressBar, String message, String caption,Context context){
        this.editText=editText;
        this.context=context;
        this.progressbar=progressBar;
        this.IMG=message;
        this.caption=caption;
         TotalHelp(Type,uid);
    }

    private void TotalHelp(final String type, String uid) {
        progressbar.setVisibility(View.VISIBLE);
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("yyMMdd", Locale.US);
        final String key = date.format(c.getTime());
        date = new SimpleDateFormat("MMMM d, yyyy || h:mm a",Locale.US);
        final String messageTime = date.format(c.getTime());
          getDateStatus(setDate1(messageTime), FirebaseDatabase.getInstance().getReference().child("chat").
                child(uid + "/Keys" ),type,key,setTime(messageTime),uid);

     }
    private void send_notifications(final String Message, String Uid)
    {
        Log.e("Notifications","Inside save notifications");
        final DatabaseReference databaseReference=FirebaseDatabase.getInstance().
                getReference("users/"+Uid.replace(uid,"")+"/Notifications");
        Query query=databaseReference.orderByChild("Server_Time");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Log.e("Notifications", "not exist");
                    String key = "Message_" + System.currentTimeMillis();
                    databaseReference.child(key).child("From").setValue("Message");
                    databaseReference.child(key).child("key").setValue(key);
                    databaseReference.child(key).child("Message").setValue(Message + "\n\t Time " + System.currentTimeMillis());
                    databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                    databaseReference.child(key).child("Status").setValue("UNSEEN");
                }else{
                    Log.e("Notifications", "  exist");

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {
                            Log.e("Notifications", "Child " + dataSnapshot1.getChildrenCount());
                            if (dataSnapshot1.child("From").getValue().toString().trim().toUpperCase().contains("MESSAGE"))
                            { Log.e("Notifications", "inside get from "+dataSnapshot1.child("From").getValue());

                                if (dataSnapshot1.child("Status").getValue().toString().trim().toUpperCase().contains("UNSEEN")) {
                                    Log.e("Notifications", "message found");
                                    String key = (String) dataSnapshot1.child("key").getValue();
                                    databaseReference.child(key).child("From").setValue("Message");
                                    String old_message = (String) dataSnapshot1.child("Message").getValue();
                                    Log.e("Notifications", "Old message not null ");
                                    databaseReference.child(key).child("Message").setValue(old_message
                                            + "\n" + Message + "\n\t Time " + DateFormat.getDateTimeInstance().format(new Date()));
                                    databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                                    databaseReference.child(key).child("Status").setValue("UNSEEN");
                                    break;
                                } else {
                                    Log.e("Notifications", "not found");
                                    String key = "Message_" + System.currentTimeMillis();
                                    databaseReference.child(key).child("From").setValue("Message");
                                    databaseReference.child(key).child("key").setValue(key);
                                    databaseReference.child(key).child("Message").setValue(  Message + "\n\t Time " + DateFormat.getDateTimeInstance().format(new Date()));
                                    databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                                    databaseReference.child(key).child("Status").setValue("UNSEEN");
                                    break;
                                }
                            }else {
                                Log.e("Notifications", "not found");
                                String key = "Message_" + System.currentTimeMillis();
                                databaseReference.child(key).child("From").setValue("Message");
                                databaseReference.child(key).child("key").setValue(key);
                                databaseReference.child(key).child("Message").setValue(Message + "\n\t Time " +  DateFormat.getDateTimeInstance().format(new Date()));
                                databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                                databaseReference.child(key).child("Status").setValue("UNSEEN");
                            }
                        } catch (Exception e) {
                            Log.e("Notifications", "not found catch");
                            String key = "Message_" + System.currentTimeMillis();
                            databaseReference.child(key).child("From").setValue("Message");
                            databaseReference.child(key).child("key").setValue(key);
                            databaseReference.child(key).child("Message").setValue(Message + "\n\t Time " +  DateFormat.getDateTimeInstance().format(new Date()));
                            databaseReference.child(key).child("Server_Time").setValue(ServerValue.TIMESTAMP);
                            databaseReference.child(key).child("Status").setValue("UNSEEN");
                        }

                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Notifications"," error firebase "+databaseError.toString());

            }
        });

    }

    private void getDateStatus(final String date, DatabaseReference address, final String type, final String key, final String Time, final String uid)   {
        final boolean[] status = {false};

        final Query query=address.orderByValue().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChildren()) {


                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {               // album_allorders data1=dataSnapshot1 .getValue(album_allorders.class);

                            if (date.contains((String)dataSnapshot1.child("Date").getValue())) {
                                saveMessage(type, date, key, Time, true,uid);

                            } else {

                                saveMessage(type, date, key, Time, false,uid);

                            }
                        }

                    }else{
                        saveMessage(type,date,key,Time,false,uid);

                    }
                }catch (Exception e){

                    status[0]=false;}
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
      private void saveMessage(String type, String date, String key, String Time, boolean Status, String uid) {
        Log.e("Firebase Messaging","Inside save message  ");
      String Total= String.valueOf(System.currentTimeMillis());
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("chat").
                child(uid).child("messages").
                child(String.valueOf(Total));
        Log.e("Firebase Messaging","Save message credentials are "+"T <"+type+"> D <"+date+"> ke <"+key+"> Time<"+Time+">"
                +Status+" <uid"+uid);
         String message = editText.getText().toString().trim();
         if (Constants.uid.isEmpty()){
        Constants.uid=context.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE).getString("COLLEGEID",null);
         }
        if (message.trim().isEmpty()) {
            // Edit_message.setError("Please enter Something");
           // editText.requestFocus();
            if (type.contains("TEXT")) {
                Log.e("help", "  Error message is empty");
                Log.e("Firebase Messaging", "Inside save message  message is empty");
                progressbar.setVisibility(View.GONE);
                return;
            }else{
                message="";
            }
        }
        editText.setText("");
        if (!Status)
        {

            storenewKey(key,date, String.valueOf(Total),uid);
            database.child("From").setValue("Date");
            database.child("Date").setValue(date.toUpperCase());
            database = FirebaseDatabase.getInstance().getReference().child("chat").
                    child(uid ).child("messages").
                    child(String.valueOf(Total+1));
//   dont know         database.child("Date").setValue(date);

        }else{
            database.child("Date").setValue("");
        }
        Log.e("help", " Message Saved ");
        database.child("Message").setValue(message);
        database.child("Time").setValue(Time);

         if (type.contains("IMG"))
         { message="IMG "+caption;
             database.child("Food_Image").setValue(IMG);
             if (caption.trim().isEmpty())
                 database.child("Message").setValue("");
             else database.child("Message").setValue(caption);



         }else if (type.contains("DOC"))
          { message="DOC "+caption;
              database.child("Food_Image").setValue(IMG);
              if (caption.trim().isEmpty())
                  database.child("Message").setValue("");
              else database.child("Message").setValue(caption);



          }else{
             send_notifications(message,uid);
         }
         save_Extra_info(message,Time,uid);

          database.child("Seen").setValue(false);
        database.child("From").setValue(type);

     progressbar.setVisibility(View.GONE);

    }
    private void save_Extra_info(final String Message, final String Time, final String uid){

        final String[] name = {uname};

         FirebaseDatabase.getInstance().getReference("users/"+uid.replace(Constants.uid,"")+"/chat/"+Uid).addListenerForSingleValueEvent(new ValueEventListener() {
             @Override
             public void onDataChange(DataSnapshot dataSnapshot) {
                 long messagecounter;
                 try {messagecounter=(long) dataSnapshot.child("Last_message_counter").getValue();
                 }catch (Exception e){
                     messagecounter=1;
                 }
                 DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("users").
                         child(uid.replace(Constants.uid,"")).child("chat").child(uid);
                 for (int i=0;i<2;i++){
                     database.child("CHATID").setValue(uid);

                     database.child("User_last_Message").setValue(Message);
                     database.child("Time").setValue(Time);
                     database.child("Server_Time").setValue(ServerValue.TIMESTAMP);


                     database.child("Last_message_counter").setValue(messagecounter+1);

                     database = FirebaseDatabase.getInstance().getReference().child("users").
                             child(Constants.uid).child("chat").child(uid);
                     name[0] =UserName;
//                     if ((long)dataSnapshot.child("Last_message_counter").getValue()>=0)
                     messagecounter =-1;
                 }
             }

             @Override
             public void onCancelled(DatabaseError databaseError) {

             }
         });




    }
    private String setTime(String time){

        time=time.substring(time.indexOf("||")+3,time.length());
        return (time);


    }
    private void storenewKey(String key, String date, String Total, String uid){
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("chat").
                child(uid + "/Keys/"+key);
        databaseReference.child("Date").setValue(date);
        databaseReference.child("key").setValue(key);
        databaseReference.child("Total").setValue(Total);
    }
     private String setDate1(String time){

        try {
            time= time.substring(0,time.indexOf("||")-1 );
            return (String.format("  %s  ", time));
        }catch (Exception ignored){}
        return "";
    }

}
