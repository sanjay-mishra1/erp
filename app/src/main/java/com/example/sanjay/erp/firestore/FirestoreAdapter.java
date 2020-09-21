package com.example.sanjay.erp.firestore;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.announcement.DetailAnnouncementActivity;
import com.example.sanjay.erp.newChatScreen.help_activity;
import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;


public class FirestoreAdapter extends RecyclerView.Adapter<FirestoreAdapter.MyHolder>{

    private List<AnnouncementData> list;
    private Context context;

    public FirestoreAdapter(List<AnnouncementData> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.announcement_layout_new,parent,false);


        return new MyHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        AnnouncementData mylist = list.get(position);
        Log.e("Announcement", "sending data to adapter..."+list.get(position));
        holder.setUserName(mylist.getDoneby());
        holder.setHeader(mylist.getHeader());
        holder.setlast_message(mylist.getMessage());
        holder.settime(mylist.getDate());
        holder.setMyUserId();
        holder.username=mylist.getDoneby();
        holder.setMoreButton(mylist.getDate());
        loadUserCred(mylist.getDonebyid(),holder);
        holder.checkAttachment(mylist.getContent());
        if (!Constants.uid.isEmpty()&&!Constants.uid.toUpperCase().equals(mylist.getDonebyid().toUpperCase()))
        holder.setLinkToMessage(mylist.getDonebyid());

    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try{
            if(list.size()==0){

                arr = 0;


            }
            else{

                arr=list.size();
            }



        }catch (Exception e){



        }

        return arr;

    }
    private void loadUserCred(final String userid, final MyHolder viewHolder) {
        FirebaseDatabase.getInstance().getReference("users/" +
                userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                viewHolder.img=(String)dataSnapshot.child("USERIMAGE").getValue();
                viewHolder.setUserImage(context,viewHolder.img);
             }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class MyHolder extends RecyclerView.ViewHolder{
        TextView messageText,headText;
        private String username;
        private String img;


        MyHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.messagebody);
            headText= (TextView) itemView.findViewById(R.id.announcementHeader);


        }

        void setlast_message(String message) {
            messageText.setText(message);
        }
        void setUserName(final String message) {
            TextView textView=itemView.findViewById(R.id.username);
            textView.setText(message);
         //   textView=itemView.findViewById(R.id.collegeid);
           // textView.setText(Constants.uid);


        }

          void setHeader(String header) {
              headText.setText(header);

         }

         void settime(long time) {
            TextView textView=itemView.findViewById(R.id.time);
            textView.setText(new SimpleDateFormat( "dd MMM hh:mm a",Locale.UK).format(new Date(time)));
        }

          void setMyUserId() {
            TextView textView=itemView.
                    findViewById(R.id.collegeid);
            if (Constants.uid.isEmpty()){
              Constants.uid=  context.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE).getString("COLLEGEID","");
             }
             textView.setText(Constants.uid);

          }

          void setLinkToMessage(final String linkToMessage) {
              itemView.findViewById(R.id.image_message_profile).setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Intent intent=new Intent(context,help_activity.class);
                      intent.putExtra("UID",sort(linkToMessage,Constants.uid));
                      intent.putExtra("UserName",username);
                      intent.putExtra("img",img);

                      context.startActivity(intent);
                  }
              });
         }
        private String sort(String input1, String input2){
            if (input1.compareTo(input2)>0)
                return input2+input1;
            else return input1+input2;

        }
         private void setMoreButton(final long Aid) {
            itemView.findViewById(R.id.announcementMoreBt).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context,DetailAnnouncementActivity.class);
                    intent.putExtra("AID",String.valueOf(Aid));
                    context.startActivity(intent);
                }
            });
        }

        void setUserImage(Context context, String userimage) {
            try {
                ImageView img=(ImageView) itemView.findViewById(R.id.image_message_profile);
                Glide.with(context).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(userimage).into(img);

            }catch (Exception e){e.printStackTrace();}
        }

        void checkAttachment(List<String> content) {
            if (content==null){
                itemView.findViewById(R.id.attachFile).setVisibility(View.GONE);
            }
        }
    }

}