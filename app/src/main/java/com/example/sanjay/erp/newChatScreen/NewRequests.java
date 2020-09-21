package com.example.sanjay.erp.newChatScreen;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.example.sanjay.erp.newChatScreen.AllUser.searchKey;
import static com.example.sanjay.erp.newChatScreen.UsersListTabActivity.Totalcount;

public class NewRequests extends Fragment {
    View v;
    View progressBar;
    private Date date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.new_request, container, false);
        //searchKey="";
        if (Constants.uid.isEmpty()){
            Constants.uid=getContext(). getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("COLLEGEID",null);
        }
        RecyclerView recyclerView=v.findViewById(R.id.recycler_Chat);
        progressBar=  v.findViewById(R.id.progressbar);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, true);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SearchAdapter adapter = new SearchAdapter(album_allorders.class, R.layout.chat_layout, mainactiv_allorders.FoodViewHolder.class, FirebaseDatabase.getInstance().
                getReference("users/" + Constants.uid + "/PendingRequests").orderByChild("isReceived").equalTo(true), "request", getContext(),
                progressBar, 'u', v.findViewById(R.id.nothing_found),getActivity(),Constants.uid);
        recyclerView.setAdapter(adapter);
        searchBarListner(adapter);
        loadFirebaseForNotification();
          date=new Date();

        return v;
    }
    void searchBarListner(final SearchAdapter searchAdapter){
        EditText searchbar=getActivity(). findViewById(R.id.searchEdit);
        searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchKey=editable.toString();
                progressBar.setVisibility(View.VISIBLE);
                Totalcount=0;
                searchAdapter.notifyDataSetChanged();
                Log.e("AllUser ","Search key changed =>"+searchKey+" notifying to adapter");
            }
        });
    }
    String calculateDate(Date d){
        int daysDiff;
        Log.e("Calculating date","today=>"+date+"=>"+d);
        long diff=date.getTime()-d.getTime();
        long diffDays=diff/(1000*60*60*24);
        daysDiff=(int) diffDays;
        Log.e("Calculating date","final days are =>"+daysDiff);

        String message;
        if (daysDiff==0){
            message="Today";

        }else if(daysDiff<31)
             message=daysDiff+" d ago";
        else
            message=(daysDiff /7)+" w ago";
        return message;
    }

    private void loadFirebaseForNotification(){
        RecyclerView recyclerView=v.findViewById(R.id.notificationRecycler);
         LinearLayoutManager mLayoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, true);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        Query query=FirebaseDatabase.getInstance().getReference("users/"+Constants.uid).child("notification");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.notification_layout,
                mainactiv_allorders.FoodViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
             Glide.with(Objects.requireNonNull(getContext())).load(getResources().getDrawable(R.drawable.ic_info_black_24dp)).into((ImageView) viewHolder.mView.findViewById(R.id.img1));
                long time= Long.parseLong(getRef(position).getKey());
                Date date=new Date(time);
                Log.e("Actualdate",date.toString());
                DateFormat format=new SimpleDateFormat("dd/mm/yy",Locale.ENGLISH);
                String da=format.format(date);
                Log.e("Actual formated date",da);
                //Date d= new SimpleDateFormat("dd/mm/yy",Locale.UK).parse(new SimpleDateFormat( "dd/mm/yy",Locale.UK).format(new Date(Long.parseLong(getRef(position).getKey())) ));
                viewHolder.setTime1( calculateDate(date));

                viewHolder.setlast_message(model.getMessage(),0);
              }

            @Override
            public int getItemCount() {
                progressBar.setVisibility(View.GONE);
                return super.getItemCount();
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
}
