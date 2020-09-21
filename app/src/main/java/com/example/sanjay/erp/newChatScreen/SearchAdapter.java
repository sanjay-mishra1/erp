package com.example.sanjay.erp.newChatScreen;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.Setting.UserInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import static com.example.sanjay.erp.newChatScreen.AllUser.searchKey;
import static com.example.sanjay.erp.newChatScreen.UsersListTabActivity.Totalcount;

public class SearchAdapter extends FirebaseRecyclerAdapter<album_allorders,mainactiv_allorders.FoodViewHolder> {
    private   final int LAYOUT_ONE = 1;
    private   final int LAYOUT_TWO = 2;
    private final DatabaseReference databaseReference;
    private Context context;
    private View progressBar;
    private char AllUsers;
    private HashMap imBlockedIn;
    private HashMap blockedUser;
    private String searchkeyOriginal;
    private boolean isBlockLoaded=false;
    private View nothing_found;
    private boolean isListLoaded=false;
    private FragmentActivity activity;
    private boolean isStaff=false;
    String myid;
    public SearchAdapter(Class<album_allorders> modelClass,
                         int modelLayout,
                         Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
                         Query ref, String searchKey, Context context, View progressBar, char isAllUsers, View nothing_found, FragmentActivity activity,String myid) {

        super(modelClass, modelLayout, viewHolderClass, ref );
        this.context=context;
        this.myid=myid;//getContext().getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("COLLEGEID",null);
        this.activity=activity;
        this.nothing_found=nothing_found;
        this.searchkeyOriginal=searchKey;
        this.AllUsers=isAllUsers;
        this.progressBar=progressBar;
        databaseReference= FirebaseDatabase.getInstance().getReference("users/" +
                myid + "/PendingRequests");
        imBlockedIn=new HashMap();
        blockedUser=new HashMap();
        isBlockLoaded=true;
        if (context.getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("TYPE","").equals("STAFF"))
            isStaff=true;

    }
    SearchAdapter(Class<album_allorders> modelClass,
                  int modelLayout,
                  Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
                  Query ref,String searchKey, Context context, View progressBar,char isAllUsers
            ,HashMap imBlockedIn,HashMap blockedusers,boolean isBlockLoaded,View noting_found,FragmentActivity activity,String myid) {

        super(modelClass, modelLayout, viewHolderClass, ref );
        this.context=context;
        this.myid=myid;
        this.activity=activity;
        this.isBlockLoaded=isBlockLoaded;
        this.AllUsers=isAllUsers;
        this.nothing_found=noting_found;
        this.progressBar=progressBar;
        databaseReference= FirebaseDatabase.getInstance().getReference("users/" +
                myid + "/PendingRequests");
        this.imBlockedIn=imBlockedIn;
        this.blockedUser=blockedusers;
    }




    @NonNull
    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType==1) {
            isListLoaded=true;
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_layout, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }else{

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_empty, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }


    }

    @Override
    public int getItemViewType(int position){

        album_allorders model = getItem(position);

        try {String name=model.getUSERNAME();
            if(name==null){
                name=getRef(position).getKey();
            }
            Log.e("Name is ",name);
            switch (CheckFilters(name,model.getUSERID())) {
                case 1:
                    Totalcount++;
                    return LAYOUT_ONE;
                case 2:

                    return LAYOUT_TWO;
            }
        }catch (Exception ignored){}
        return position;

    }

    @Override
    public void onBindViewHolder(mainactiv_allorders.FoodViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        viewHolder.setIsRecyclable(false);
    }

    private int CheckFilters(String username, String userid) {
        if (username==null){
            username="";
        }
        if (userid.toLowerCase().equals(myid.toLowerCase())||imBlockedIn.containsKey(userid))
        {
            Log.e("SearchAdapter","UCred=>"+username+userid+" searchKey=>"+searchKey+" same of constant.uid or user is blocked");
            return 2;
        }
        else
        if ( searchKey.isEmpty())
        {Log.e("SearchAdapter","UCred=>"+username+userid+" searchKey=>"+searchKey+" searhckey is empty");
            return 1;
        }
        else if(username.toLowerCase().contains(searchKey.toLowerCase())
                ||userid.toLowerCase().contains(searchKey.toLowerCase()))
        {

            Log.e("SearchAdapter","UCred=>"+username+userid+" searchKey=>"+searchKey+" searchkey contains ");
            return 1;
        }
        else {
            Log.e("SearchAdapter","UCred=>"+username+userid+" searchKey=>"+searchKey+" else not match anything");
            return 2;
        }
    }


    @Override
    protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
        if (getItemCount() < 1) {
            nothing_found.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else{
        try {
            Log.e("Inside firebase adapter", "Loading data and hiding firebase progressbar");
            progressBar.setVisibility(View.GONE);
            final Button button = viewHolder.mView.findViewById(R.id.followBt);
            final RelativeLayout relativeLayout = viewHolder.mView.findViewById(R.id.mainLayout);


                if (!isStaff)
                    button.setVisibility(View.VISIBLE);


                if (AllUsers == 'f') {

                    //relativeLayout=viewHolder.mView.findViewById(R.id.mainLayout);
                    if (!model.getTYPE().contains("STAFF")) {
                        if (isStaff) {//if my account is staff;

                            loadUserListener(viewHolder, true);
                            button.setVisibility(View.GONE);
                        }
                        relativeLayout.setVisibility(View.VISIBLE);
                        setFollowBtText(viewHolder, model.getUSERID(), button, relativeLayout);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadButtonChecker(button, (TextView) viewHolder.mView.findViewById(R.id.username), model.getUSERID(), "", relativeLayout);

                            }
                        });
                    } else {
                        button.setVisibility(View.GONE);
                        loadUserListener(viewHolder, true);
                    }

                    viewHolder.setImage(model.getUSERIMAGE(), context);
                    viewHolder.setId(model.getUSERID());
                    viewHolder.setUserName(model.getUSERNAME());
                } else if (AllUsers == 'u') {
                    if (searchkeyOriginal.contains("request")) {
                        {
                            enableAccept(button, model.getUSERID(), viewHolder.mView.findViewById(R.id.delete_request));
                            //button.setText("Accept");
                        }
                    } else {
                        button.setText("Unfollow");
                        loadUserListener(viewHolder, false);

                    }

                    loadUserCred(model.getUSERID(), viewHolder, model, button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView t = viewHolder.mView.findViewById(R.id.imglink);
                            loadButtonChecker(button, (TextView) viewHolder.mView.findViewById(R.id.username), model.getUSERID(), t.getText().toString(), relativeLayout);

                        }
                    });

                } else {
                    viewHolder.setImage("https://firebasestorage.googleapis.com/v0/b/malwainstituteoftechnologyerp.appspot.com/o/users%2F" +
                            model.getUSERID() +
                            ".jpg?alt=media&token=f9bc617f-2127-4b", context);
                    viewHolder.setId(model.getUSERID());
                    viewHolder.setUserName(model.getUSERNAME());
                    setFollowBtText(viewHolder, model.getUSERID(), button, relativeLayout);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //              TextView t = viewHolder.mView.findViewById(R.id.imglink);
                            loadButtonChecker(button, (TextView) viewHolder.mView.findViewById(R.id.username), model.getUSERID(), "https://firebasestorage.googleapis.com/v0/b/malwainstituteoftechnologyerp.appspot.com/o/users%2F" +
                                    model.getUSERID() +
                                    ".jpg?alt=media&token=f9bc617f-2127-4b", relativeLayout);
                        }
                    });
                }


            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    private void showDialog(final String message, final String userid, final String username, String imgLink, final Button button, final RelativeLayout relativeLayout){

        final View dialogView = View.inflate(getContext(), R.layout.user_profile_action_dialog, null);

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        dialog.setContentView(dialogView);
        if (imgLink==null)
            imgLink="https://firebasestorage.googleapis.com/v0/b/malwainstituteoftechnologyerp.appspot.com/o/users%2F"+
                    userid+
                    ".jpg?alt=media&token=f9bc617f-2127-4b";
        Glide.with(context).load(imgLink).apply(RequestOptions.circleCropTransform()).into((ImageView) dialogView.findViewById(R.id.userimage));
        TextView textView=dialogView.findViewById(R.id.username);
        textView.setText(username);
        textView=dialogView.findViewById(R.id.message);
        textView.setText(message);
        Log.e("showDialog","m=>"+message+"id=>"+userid+"uname=>"+username+"imglink=>"+imgLink);
        final Dialog finalDialog = dialog;
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {

                    finalDialog.dismiss();

                    return true;
                }

                return false;
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogView.findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDialog.dismiss();
                if (message.toLowerCase().contains("unfollow"))
                    unFollow(userid,button,relativeLayout);
                else  {
                    acceptRequest(userid,username);
                }
            }
        });
        dialogView.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalDialog.dismiss();
            }
        });
        dialog.show();


    }
    private void loadUserCred(final String userid, final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final Button button) {
        FirebaseDatabase.getInstance().getReference("users/" +
                userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ImageView imageView=viewHolder.setImage((String)dataSnapshot.child("USERIMAGE").getValue(), context);
                try {
                    if (button.getText().toString().toLowerCase().contains("unfollow")){
                        long color;
                        if (dataSnapshot.child("IMAGECOLOR").getValue()==null){
                            color= Color.BLACK;
                        }else
                            color= (long) dataSnapshot.child("IMAGECOLOR").getValue();
                        loadUserImgListener(imageView,Objects.requireNonNull(dataSnapshot.child("USERIMAGE").getValue()).toString(),
                                userid,
                                Objects.requireNonNull(dataSnapshot.child("ROLLNO").getValue()).toString(),
                                Objects.requireNonNull(dataSnapshot.child("ATTENDANCE").getValue()).toString(),
                                color
                        );
                    }
                }catch (Exception e){}
                viewHolder.setImage((String)dataSnapshot.child("USERIMAGE").getValue(), context);
                viewHolder.setId(userid);
                model.setUSERNAME((String)dataSnapshot.child("USERNAME").getValue());
                viewHolder.setUserName(model.getUSERNAME());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadButtonChecker(Button button,TextView textView, String userid,String img,RelativeLayout relativeLayout) {
        if (myid.isEmpty()){
            // myid=  getContext().getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("COLLEGEID",null);
        }
        String name=textView.getText().toString();
        Log.e("BtChecker","clicked=>"+button.getText()+"name=>"+name);
        try {


            switch (button.getText().toString().toLowerCase()) {
                case "follow":
                    sendFriendRequest(userid, name, button);
                    break;
                case "cancel":
                    cancelRequest(userid, button);
                    break;
                case "accept":
                    showDialog("Follow", userid, name, img, null,null);
                    break;
                case "unblock":
                    unblockUser(userid,button);
                    break;
                case "unfollow":
                    showDialog("Unfollow", userid, textView.getText().toString(), img, button,relativeLayout);
                    break;
                default:
                    Log.e("BtChecker", "does not match");
            }
        }catch (Exception e){
            Log.e("BtChecker", "Error "+e.getMessage());
            e.printStackTrace();
        }
    }

    private void unFollow(String userid, Button button,RelativeLayout  relativeLayout) {
        FirebaseDatabase.getInstance().getReference("users/" +
                myid + "/friends").child(userid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/friends").child(myid) .setValue(null);

        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/notification").child(String.valueOf(System.currentTimeMillis())).child("message")
                .setValue(context.getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("USERNAME","")
                        +" has unfollow you");
      if(AllUsers!='u')
        relativeLayout.setVisibility(View.GONE);
        // notifyAll();
        //notifyDataSetChanged();
         button.setText("Follow");
    }

    private void acceptRequest(String userid,String name) {
        databaseReference.child(userid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/PendingRequests").child(myid).setValue(null);

        FirebaseDatabase.getInstance().getReference("users/" +
                myid + "/friends").child(userid).child("USERID")
                .setValue(userid);
        FirebaseDatabase.getInstance().getReference("users/" +
                myid + "/friends").child(userid).child("USERNAME")
                .setValue(name);




        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/friends").child(myid).child("USERID")
                .setValue(myid);
        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/friends").child(myid).child("USERNAME")
                .setValue(context.getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("USERNAME",""));



        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/notification").child(String.valueOf(System.currentTimeMillis())).child("message")
                .setValue(context.getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("USERNAME","")+" has accepted your follow request");

    }

    private void cancelRequest(String userid, Button button) {
        Log.e("BtChecker", "cancelling request....");
        databaseReference.child(userid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/PendingRequests").child(myid).setValue(null);
        button.setText("Follow");
    }
    private void loadUserListener(final mainactiv_allorders.FoodViewHolder viewHolder, final boolean isStaff){
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, help_activity.class);
                TextView textView= viewHolder.mView.findViewById(R.id.usermessage);
                intent.putExtra("UID", sort(textView.getText().toString(), myid));
                TextView t = viewHolder.mView.findViewById(R.id.username);
                intent.putExtra("UserName", t.getText().toString().trim());
                t = viewHolder.mView.findViewById(R.id.imglink);
                intent.putExtra("img", t.getText().toString().trim());
                intent.putExtra("STAFF",isStaff);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
    private void sendFriendRequest(String userid,String name,Button button) {

        try {
            Log.e("FriendRequest","sent....Name"+name+" userid"+userid);
            //my account update
            FirebaseDatabase.getInstance().getReference("users/" + myid + "/PendingRequests/" + userid).child("USERID").setValue(userid);
            FirebaseDatabase.getInstance().getReference("users/" + myid + "/PendingRequests/" + userid).child("USERNAME").setValue(name);
            FirebaseDatabase.getInstance().getReference("users/" + myid + "/PendingRequests/" + userid).child("isReceived").setValue(false);

            //friend account update
            FirebaseDatabase.getInstance().getReference("users/" + userid + "/PendingRequests/" + myid).child("USERID").setValue(myid);
            FirebaseDatabase.getInstance().getReference("users/" + userid + "/PendingRequests/" + myid).child("USERNAME").setValue(context.getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("USERNAME",""));
            FirebaseDatabase.getInstance().getReference("users/" + userid + "/PendingRequests/" + myid).child("isReceived").setValue(true);
            button.setText("Cancel");
        }catch (Exception ignored){

        }
    }

    private void setFollowBtText(final mainactiv_allorders.FoodViewHolder viewHolder, final String userid, final Button followButton, final RelativeLayout mainLayout){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userid)){
                    if ((boolean)dataSnapshot.child(userid).child("isReceived").getValue()){
                        Log.e("check user","setting accept");
                        {   enableAccept(followButton,userid,viewHolder.mView.findViewById(R.id.delete_request));
                            viewHolder.itemView.setClickable(false);
                            viewHolder.itemView.setOnClickListener(null);
                        }
                    }else if(!(boolean)dataSnapshot.child(userid).child("isReceived").getValue()){

                        Log.e("check user","setting cancel");
                        viewHolder.itemView.setClickable(false);
                        viewHolder.itemView.setOnClickListener(null);
                        followButton.setText("Cancel");
                    }
                }else if (blockedUser.containsKey(userid)){Log.e("check user","setting unblock");
                    followButton.setText("Unblock");
                    viewHolder.itemView.setClickable(false);
                    viewHolder.itemView.setOnClickListener(null);
                }

                else{
                    FirebaseDatabase.getInstance().getReference("users/" +
                            myid + "/friends").child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                Log.e("check user","setting UnFollow");
                                followButton.setText("Unfollow");

                                loadUserListener(viewHolder, false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                mainLayout.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserImgListener(final ImageView imageView, final String userimage, final String userid, final String rollno, final String attendance,final long color) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),UserInfo.class);
                intent.putExtra("IMGLINK",userimage);
                intent.putExtra("COLLEGEID",userid);
                intent.putExtra("UNIVID",rollno);
                intent.putExtra("ATTENDANCE",attendance);
                intent.putExtra("COLOR",color);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation
                                (Objects.requireNonNull(activity),imageView , "user_image");
                activity. startActivity(intent, optionsCompat.toBundle());
            }
        });
    }

    private void enableAccept(Button followButton, final String userid, View deleteBt) {
        followButton.setText("Accept");
        if (!isStaff)
            deleteBt.setVisibility(View.VISIBLE);
        deleteBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteRequest(userid);
            }
        });
    }

    private void deleteRequest(String userid) {
        FirebaseDatabase.getInstance().getReference("users/" + myid + "/PendingRequests/" + userid).setValue(null);

        FirebaseDatabase.getInstance().getReference("users/" + userid + "/PendingRequests/" + myid).setValue(null);
    }

    private void unblockUser(String userid, Button button) {
        FirebaseDatabase.getInstance().getReference("users/" +
                myid).child("block").child("blocked").child(userid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                userid).child("block").child("blocked_by").child(myid).setValue(null);
//        button.setText("Follow");
    }

    private String sort(String input1, String input2){
        if (input1.compareTo(input2)>0)
            return input2+input1;
        else return input1+input2;

    }

    @Override
    public int getItemCount() {
        if (searchKey.isEmpty()&&AllUsers=='f')
        {  progressBar.setVisibility(View.GONE);
            Log.e("Item count","is 0 isempty=>"+searchKey.isEmpty()+"isalluser"+AllUsers);
            return 0;
        }
        else{Log.e("Item count","is > 1 OK Please wait loading data");
            if (isListLoaded)
                progressBar.setVisibility(View.GONE);
            try {
                nothing_found.setVisibility(View.GONE);
            }catch (Exception ignored){}
            // count=0;
            if (Totalcount<1){
                if(isListLoaded)
                {
                    try {
                        nothing_found.setVisibility(View.VISIBLE);
                    }catch (Exception ignored){}
                }

            }
//         if (super.getItemCount()<1){
//                    progressBar.setVisibility(View.GONE);
//                    nothing_found.setVisibility(View.VISIBLE);
//                }else{
//                    nothing_found.setVisibility(View.GONE);
//                }
            return super.getItemCount();
        }
    }

    public Context getContext() {
        return context;

    }
}
