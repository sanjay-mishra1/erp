package com.example.sanjay.erp.announcement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.firestore.AnnouncementData;
import com.example.sanjay.erp.firestore.FirestoreAdapter;
import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
 import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class ShowAnnouncement extends Fragment {
    FirestoreAdapter adapter;
    private List<AnnouncementData> list;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view=inflater.inflate(R.layout.layout_see_announcement,container,false);
        //loadUi(view);
        list=new ArrayList<>();
        TextView toolbar=getActivity().findViewById(R.id.title);
        toolbar .setText(getString(R.string.announcement));
        if (getContext().getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE).
                getString("TYPE","").equals("STAFF"))
            view.findViewById(R.id.floatingbutton).setVisibility(View.VISIBLE);
        view.findViewById(R.id.floatingbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeAnnouncmentClicked();
            }
        });

        final RecyclerView recyclerView =view. findViewById(R.id.recycler_Chat);
        LinearLayoutManager mLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter=new FirestoreAdapter(list,getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);
        loadFirstore(view,recyclerView);

        view.findViewById(R.id.retry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

                view.findViewById(R.id.no_announcement).setVisibility(View.GONE);

                loadFirstore(view,recyclerView);
            }
        });

        return view;
    }
    void extraActions(View view){
        if (view.findViewById(R.id.progressbar).getVisibility()==View.VISIBLE){
            view.findViewById(R.id.progressbar).setVisibility(View.GONE);
        }
    }
    void loadFirstore(final View view, final RecyclerView recyclerView){
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
       Query mQuery = mFirestore.collection("announcement")
                .orderBy("date", Query.Direction.ASCENDING);

        CollectionReference userids=mFirestore.collection("announcement");
        if (Constants.uid.isEmpty()){
            Constants.uid= Objects.requireNonNull(getContext()).getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE).getString("COLLEGEID","");
        }

        Log.e("Announcement", "Collegeid "+Constants.uid);

        userids.whereArrayContains("userid",Constants.uid)
//        userids.whereArrayContains("userid","CS16202061")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
           @Override
           public void onComplete(@NonNull Task<QuerySnapshot> task) {

               List<DocumentSnapshot> listlocal= Objects.requireNonNull(task.getResult()).getDocuments();

               for (int i = 0; i < listlocal.size(); i++) {
                    DocumentSnapshot document=listlocal.get(i);
                   if (document!=null){
        Log.e("Announcement", "Document..."+ Objects.requireNonNull(document).getData());

                       AnnouncementData value=  document.toObject(AnnouncementData.class);

                       AnnouncementData fire = new AnnouncementData();
                       String message;
                       try {
                           message = Objects.requireNonNull(value).getMessage();
                       }catch (Exception e){
                           message = "No message";
                       }
                       String head = null;
                       if (value != null) {
                           head = value.getHeader();
                       }else head = "No head";


                       fire.setMessage(message);
                       fire.setHeader(head);
                       fire.setDoneby(Objects.requireNonNull(value).getDoneby());
                       fire.setDonebyid(value.getDonebyid());
                       fire.setDate(value.getDate());
                      try {
                          if (!value.getContent().isEmpty()){
                              ArrayList<String> a= new ArrayList<String>();
                              a.add("not empty");
                              fire.setContent(a);
                          }
                      }catch (Exception e){
                          Log.e("Announcement", "Content error "+e.getMessage());

                      }
                     try {
                         list.add(fire);
                     }catch (Exception e){
                         Log.e("Announcement", "list error ");
                         e.printStackTrace();

                     }

                   }

                adapter.notifyDataSetChanged();
               }
               if (list.isEmpty()){
                   view.findViewById(R.id.no_announcement).setVisibility(View.VISIBLE);
               }else {
                   try {
                       final NestedScrollView scrollView = Objects.requireNonNull(getView()).findViewById(R.id.scrollview);

                       scrollView.postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               scrollView.fullScroll(View.FOCUS_DOWN);
                               extraActions(view);
                           }
                       }, 50);
                   }catch (Exception e){
                       extraActions(view);
                   }
               }

           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {

               view.findViewById(R.id.progress).setVisibility(View.GONE);
               view.findViewById(R.id.error_screen).setVisibility(View.GONE);
           }
       });


    }

      public void makeAnnouncmentClicked() {
        startActivity(new Intent(getContext(),make_announcement.class));
    }
}
