package com.example.sanjay.erp.newChatScreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ProgressBar;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.announcement.ShowMedia;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockedUsersActivity extends AppCompatActivity {
    FirebaseRecyclerAdapter fbra;
    private View progressBar;
    private ListAdapter listAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simplerecycle);
        load();
        db();
    }
    void load(){
          recyclerView=findViewById(R.id.recycler_view);
          progressBar = findViewById(R.id.progressbar);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, true);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
         recyclerView.setAdapter(listAdapter);



    }
    void db(){
        FirebaseDatabase.getInstance().getReference("users/"+Constants.uid+"/block/blocked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,String> data ;
                data= (HashMap<String, String>) dataSnapshot.getValue();
                Log.e("data", String.valueOf(data));

                ArrayList<String> user=new ArrayList<>();
                 listAdapter=new ListAdapter(user);
                recyclerView.setAdapter(listAdapter);
                assert data != null;
               try{
                   for(Map.Entry<String ,String> entry:data.entrySet()){
                       Log.e("block",entry.getKey());
                       user.add(entry.getKey()+","+entry.getValue());

                       listAdapter.notifyDataSetChanged();
                   }
                   progressBar.setVisibility(View.GONE);
               }catch (Exception e){
                   findViewById(R.id.progress).setVisibility(View.GONE);
                   findViewById(R.id.text).setVisibility(View.VISIBLE);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void gobackClicked(View view) {
        finish();
    }

    class ListAdapter extends RecyclerView.Adapter<mainactiv_allorders.FoodViewHolder>{
        ArrayList data;
         ListAdapter(ArrayList data){
            this.data=data;
         }
        @NonNull
        @Override
        public mainactiv_allorders.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
           View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_layout, viewGroup, false);

            return new mainactiv_allorders.FoodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final mainactiv_allorders.FoodViewHolder viewHolder, final int i) {
            Log.e("BLockAdap",""+data);
            final String user[]= ((String) data.get(i)).split(",");
            viewHolder.setImage("https://firebasestorage.googleapis.com/v0/b/malwainstituteoftechnologyerp.appspot.com/o/users%2F"+
                    user[0]+
            ".jpg?alt=media&token=f9bc617f-2127-4b",getApplicationContext());
            viewHolder.setUserName((String)  user[1]);
            viewHolder.setId( user[0]);
            Button button=viewHolder.mView.findViewById(R.id.followBt);
            button.setText("Unblock");
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    unblockUser((String)  user[0],viewHolder.getAdapterPosition());
                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
        private void unblockUser(String userid,int postion) {
            FirebaseDatabase.getInstance().getReference("users/" +
                    Constants.uid).child("block").child("blocked").child(userid).setValue(null);
            FirebaseDatabase.getInstance().getReference("users/" +
                    userid).child("block").child("blocked_by").child(Constants.uid).setValue(null);
            data.remove(postion);
            notifyItemRemoved(postion);

        }
    }

}


