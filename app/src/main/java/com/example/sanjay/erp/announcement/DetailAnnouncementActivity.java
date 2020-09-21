package com.example.sanjay.erp.announcement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.firestore.AnnouncementData;
import com.example.sanjay.erp.newChatScreen.help_activity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class DetailAnnouncementActivity extends AppCompatActivity {
    String donebyId;
    private String donebyIdName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_announcement);

        receiveData();
    }
    void receiveData(){
        Intent intent=getIntent();
        String aId=intent.getStringExtra("AID");

        extractDataFromDatabase(aId);
    }

    void extractDataFromDatabase(final String aId){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference=mFirestore.collection("announcement");
        Query mQuery = collectionReference.whereEqualTo("date",Long.valueOf(aId));

         mQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.e("DetailAnnouncement", "task  "+ task.getResult()+aId);
                 List<DocumentSnapshot> listlocal= Objects.requireNonNull(task.getResult()).getDocuments();
                Log.e("DetailAnnouncement", "list "+ listlocal);

                if (task.isSuccessful()) {


                     Log.e("DetailAnnouncement", "going to loop for loading data.."+task.getResult());
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.e("DetailAnnouncement", "inside loop loading data");
                        AnnouncementData value=  document.toObject(AnnouncementData.class);
                         /** *<>Read Media***** **/
                         Bundle bundle=new Bundle();
                         bundle.putStringArrayList("MEDIALIST", (ArrayList<String>) value.getContent());
                        ShowMedia filterResult = new ShowMedia();
                        filterResult.setArguments(bundle);
                        FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                       try{
                           fragmentTransaction1.replace(R.id.imageFragment, filterResult).commit();
                       }catch (Exception e){
                           e.printStackTrace();
                       }
                        /****</>Read Media*******/
                         try {
                            setMessage(value.getMessage());
                        }catch (Exception e){
                            e.printStackTrace();
                         }
                         setHeader(value.getHeader());

                        donebyId= value.getDonebyid();
                        donebyIdName=value.getDoneby();
                        setDoneby(donebyIdName);
                        findViewById(R.id.progressbar).setVisibility(View.GONE);
                        setDate(value.getDate());


                    }
                 }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             }
        });


    }

    private void setDoneby(String doneby) {

        TextView textView=findViewById(R.id.from);
        textView.setVisibility(View.VISIBLE);
        textView.setText(doneby);
     }

    private void setDate(long date) {
        TextView textView=findViewById(R.id.date);
        textView.setText(new SimpleDateFormat( "dd MMM hh:mm a",Locale.UK).format(new Date(date)));
        textView.setVisibility(View.VISIBLE);
    }

    private void setHeader(String header) {
        TextView textView=findViewById(R.id.announcementHeader);
        textView.setText(header);
    }

    private void setMessage(String message) {
        TextView textView=findViewById(R.id.announcementText);
        textView.setText(message);
    }

    public void fromOnClicked(View view) {
            Intent intent=new Intent(this,help_activity.class);
             intent.putExtra("UID",donebyId);
             intent.putExtra("STAFF",true);
             intent.putExtra("UserName",donebyIdName);
              intent.putExtra("img","");

            startActivity(intent);
    }

    public void onclickback(View view) {
        finish();
    }
}
