package com.example.sanjay.erp.newChatScreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.example.sanjay.erp.newChatScreen.AllUser.searchKey;

public class notificationFragment extends Fragment {
    View v;
    View progressBar;
    private SearchAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         v = inflater.inflate(R.layout.activity_all_user, container, false);


         if (Constants.uid.isEmpty()){
            Constants.uid=getContext(). getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("COLLEGEID",null);
        }
        RecyclerView recyclerView=v.findViewById(R.id.recycler_Chat);
        progressBar=  v.findViewById(R.id.progressbar);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, true);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        loadFirebase(recyclerView);
         recyclerView.setAdapter(adapter);

        return v;
    }

    private void loadFirebase(RecyclerView recyclerView) {
        if(Constants.uid.isEmpty()){
            Constants.uid=Objects.requireNonNull(getContext()).getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("COLLEGEID",null);
        }
        DatabaseReference database=FirebaseDatabase.getInstance().getReference("users/"+Constants.uid+"/notifications");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> fbra=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.notification_layout,
                mainactiv_allorders.FoodViewHolder.class,
                database.orderByKey()
        ) {
            @Override
            protected void populateViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, album_allorders model, int position) {
//                viewHolder.mView.findViewById(R.id.username).setVisibility(View.GONE);
                TextView textView=viewHolder.mView.findViewById(R.id.usermessage);
                textView.setText(model.getMessage());
               try {
                   settime(Long.parseLong(this.getRef(position).getKey()),(TextView) viewHolder.mView.findViewById(R.id.usertime));
               }catch (Exception e){
                   e.printStackTrace();
               }
             }

            @Override
            public int getItemCount() {
                if (super.getItemCount()<=0){
                    progressBar.setVisibility(View.GONE);
                }
                return super.getItemCount();
            }
        };
        recyclerView.setAdapter(fbra);
    }
    void settime(long time,TextView textView) {
         textView.setText(new SimpleDateFormat( "dd MMM :mm a",Locale.UK).format(new Date(time)));
    }
    }
