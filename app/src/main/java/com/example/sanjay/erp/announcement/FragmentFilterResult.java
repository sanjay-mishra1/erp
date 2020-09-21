package com.example.sanjay.erp.announcement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.sanjay.erp.R;
import com.example.sanjay.erp.newChatScreen.album_allorders;
import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.sanjay.erp.announcement.make_announcement.announcementSet;
import static com.example.sanjay.erp.announcement.make_announcement.manuallyAnnouncementSet;

public class FragmentFilterResult extends Fragment {
  public   FragmentFilterResult(){

    }
    private FilterAdapter adapter;
    List<album_allorders> list;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    View view;
    String start;
   static boolean isNumber;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
          view=inflater.inflate(R.layout.chat_activity_layout,container,false);
        list= new ArrayList<>();

        progressBar=view.findViewById(R.id.progressbar);
        view.findViewById(R.id.floatingbutton).setVisibility(View.GONE);
        recyclerView=view.findViewById(R.id.recycler_Chat);
         LinearLayoutManager mLayoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
         assert this.getArguments() != null;
        if (!this.getArguments().getBoolean("ISMANUAL")){
            announcementSet.clear();
            loadData();
        }else{
            loadManualData();
        }
        return view;
    }

    private void loadManualData() {

          start=this.getArguments().getString("FIRSTLIMIT");
        TextView textView= Objects.requireNonNull(getActivity()).findViewById(R.id.manuallyTextview);
        list=new ArrayList<>(manuallyAnnouncementSet.values());
        adapter = new FilterAdapter(list, getContext(), start,
                start,
                textView, list.size()+1,false);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users");

     Query   query=databaseReference.
                    orderByChild("USERID").
                startAt(start).
                endAt(start+"\uf8ff");
        Log.e("FilterAnnouncement","inside manual sending to adapter to load start"+start+" list "+list);

                LoadAdapter(query, textView, "USERID",list,false);


    }

    void loadData(){

    TextView textView= Objects.requireNonNull(getActivity()).findViewById(R.id.totalFilteredData);
    textView.setText(R.string.searching);
        assert this.getArguments() != null;
        String search=this.getArguments().getString("SEARCHQUERY");
      start=this.getArguments().getString("FIRSTLIMIT");
    String end=this.getArguments().getString("LASTLIMIT");
    isNumber=this.getArguments().getBoolean("ISNUMBER",false);
    DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("users");
        Log.e("FilterAnnouncement","inside auto sending to adapter to load start"+start+" list "+list);

    try {
        Query  query;
        if (this.getArguments() != null) {
            if (!isNumber)
            {
                query=databaseReference.
                        orderByChild(Objects.requireNonNull(search)).
                        startAt(start.substring(0, Objects.requireNonNull(start).length()-3).trim()).
                        endAt(start.substring(0,start.length()-3).
                                trim()+"\uf8ff");
            }
            else{
                if (Objects.requireNonNull(end).isEmpty()){
                    query=databaseReference.
                            orderByChild(Objects.requireNonNull(search)).
                            startAt(start).
                            endAt(start+"\uf8ff");
                }else
                    query=databaseReference.
                            orderByChild("TYPE").
                            startAt("Student").
                            endAt("Student"+"\uf8ff");
            }

            if (Objects.requireNonNull(end).isEmpty()){

                LoadQuery(query,(search),start);

            }else {
                int totalRecord;
                if (search.equals("DEPARTMENT")||start.equals(end)){
                    totalRecord=0;
                }else {
                    totalRecord = getTotalLimit(start, end, textView);
                }

                if (totalRecord>=0){
                    adapter = new FilterAdapter(list, getContext(), start,
                            end,
                            textView, totalRecord,true);
                    recyclerView.setAdapter(adapter);
                    Log.e("FilterAnnouncement","sending to load adapter start modified "+
                            start);
                    LoadAdapter(query, textView, search,list,true);

                    textView.setText(R.string.searching);
                }else{
                    progressBar.setVisibility(View.GONE);
                    textView.setText(R.string.unable_to_fetch);
                    Log.e("FilterAnnouncement","Total Record less than 0"+totalRecord);
                }

            }
        }
    }catch (Exception e){
        Log.e("FilterAnnouncement","Error exception "+e.getMessage());
        e.printStackTrace();
        progressBar.setVisibility(View.GONE);
        textView.setText(R.string.unable_to_fetch);
    }
}
    private int getTotalLimit(String start,String end,TextView textView) {
        Log.e("FilterAnnouncement"," getTotalLimit() original start"+start+end);

        start= start.replaceAll("[^0-9]","");
       end= end.replaceAll("[^0-9]","");
       int result;
       try {
          result= (int) (Long.parseLong(end)-Long.parseLong(start));

         if (result<0){
             textView.setText(R.string.invalid_rollno);
             return -1;
         }
       }catch (Exception e) {
           progressBar.setVisibility(View.GONE);
           textView.setText(R.string.unable_to_fetch);
          e.printStackTrace();
           Log.e("FilterAnnouncement","Error exception getTotalLimit() start"+start+end+e.getMessage());
           return -1;
       }
        Log.e("FilterAnnouncement"," getTotalLimit() "+result);
        Log.e("FilterAnnouncement","TotalLimit "+result);
        return result+1;
    }

    void LoadQuery(Query databaseReference, String SearchQuery, String FirstLimit){
         progressBar.setVisibility(View.VISIBLE);
         TextView textView=getActivity().findViewById(R.id.totalFilteredData);
         LoadDatabase(databaseReference,textView);
    }

     void LoadAdapter(Query query, final TextView textView, final String searchKey, final List<album_allorders> list, final boolean isClear){

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                progressBar.setVisibility(View.GONE);
     if (isClear)
         list.clear();
                for (DataSnapshot dataSnapshot:dataSnapshot1.getChildren()) {


                    album_allorders value;
                    album_allorders data = new album_allorders();

                    try {
                        value = dataSnapshot.getValue(album_allorders.class);

                        Log.e("FilterFragment", "onDataChange record "
                                + dataSnapshot.getValue());
                        if (value != null) {
                            data.setUSERNAME(value.getUSERNAME());
                            data.setUSERIMAGE(value.getUSERIMAGE());

                          data.setSEARCHKEY(dataSnapshot.child(searchKey).getValue());
                             data.setUSERID(value.getUSERID());

                            list.add(data);
                        if (isClear)
                            announcementSet.add(value.getUSERID());
                        else{
                            manuallyAnnouncementSet.put(value.getUSERID(),data);
                        }
                        }
                    adapter.notifyDataSetChanged();
  }catch (DatabaseException e){
e.printStackTrace();
                    }
                    }
                    if(!dataSnapshot1.exists()){
                      if (!isClear) {
                          album_allorders data = new album_allorders();
                          data.setUSERNAME("Not in app record");
                          data.setUSERIMAGE("");

                          data.setSEARCHKEY(start);
                          data.setUSERID(start);
                          manuallyAnnouncementSet.put(start, data);

                          list.add(data);
                          adapter.notifyDataSetChanged();
                      }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            textView.setText(databaseError.getMessage());
            }

        });
    }
    void LoadDatabase(Query query, final TextView textView){
        Log.e("FilterFragment", "inside firebase recycler");

        final FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.chat_layout,
                mainactiv_allorders.FoodViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
//                view. findViewById(R.id.progressbar).setVisibility(View.GONE);
             try {
                 progressBar.setVisibility(View.GONE);

                 viewHolder.setImage(model.getUSERIMAGE(), getContext());
//                viewHolder.mView.findViewById(R.id.messagebody).setVisibility(View.GONE);
                 if (getArguments().getString("FIRSTLIMIT").equals("ROLLNO")) {
                     viewHolder.setId(model.getROLLNO());
                 } else viewHolder.setId(model.getUSERID());
                 viewHolder.setUserName(model.getUSERNAME());
                 announcementSet.add(model.getUSERID());
                 Log.e("FilterFragment", "Size of annSet " + announcementSet.size());
                 textView.setText(String.format(Locale.ENGLISH, "%d record found", getItemCount()));
             }catch (DatabaseException e){
                 e.printStackTrace();
             }

            }

            @Override
            public int getItemCount() {
                if (super.getItemCount()==0){
                textView.setText(R.string.unable_to_fetch);
                    progressBar.setVisibility(View.GONE);
//                recyclerView.setAdapter(null);
                }
                return super.getItemCount();

            }

        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
