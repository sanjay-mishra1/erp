package com.example.sanjay.erp.newChatScreen;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
 import com.example.sanjay.erp.Home;
import com.example.sanjay.erp.LoginFrontScreen;
import com.example.sanjay.erp.R;

import com.example.sanjay.erp.Setting.UserInfo;
import com.example.sanjay.erp.announcement.make_announcement;
import com.example.sanjay.erp.firestore.FirestoreAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.content.Intent.ACTION_GET_CONTENT;
import static com.example.sanjay.erp.Constants.fields;
import static com.example.sanjay.erp.Constants.isUserBlocked;
import static com.example.sanjay.erp.Constants.uname;
import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

//m=>I've blocked the user
//d=> block list is not loaded
//o=> User has blocked me
//f=> User is  following me
//n=> User is not following me
public class help_activity extends AppCompatActivity {
    private   final int CHOOSE_DOC = 104;
    private   final int READ_EXTERNAL_STORAGE =102 ;
    private   final int OPEN_CAMERA =103 ;
      TextView Edit_message;
    boolean isActive;
    public static Uri uriProfileImage;
    boolean actionButtonStatus = false;
      private ImageButton actionButton;
      public static byte[] messageImage;
     RecyclerView recyclerView;
    public static String Caption;
    public static String Uid;
    public static String Canteen;
    public static String UserName;
    public static String img;
    public    long lastseen;

    private ImageView imageView;
    private boolean isStaff;
    private boolean myAccStaff=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_help_layout2);
        imageView=findViewById(R.id.mainimage);
        if(Constants.uid.isEmpty())
            Constants.uid=getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID",null);
        if (getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("TYPE",null).contains("STAFF")){
                findViewById(R.id.menu).setVisibility(View.GONE);
                myAccStaff=true;
        }
        receive();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("ddMM", Locale.ENGLISH);
        lastseen=Integer.parseInt(date.format(c.getTime()));

        FirebaseDatabase.getInstance().getReference().child("users")
                .child(Constants.uid).child("LASTSEEN")
                .setValue(lastseen);
        setLastSeen();
         change_status_to_seen();
        undoCounter();
        findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                help_activity.super.onBackPressed();
            }
        });
        TextView textView=findViewById(R.id.toolbar);
        textView.setText(UserName);
        recyclerView = findViewById(R.id.reyclerview_message_list);
        LinearLayoutManager horizontal = new LinearLayoutManager(help_activity.this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(horizontal);
         DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().
                child("chat").
                child(Uid).child("messages");
        com.example.sanjay.erp.newChatScreen.MyAdapter adapter;
        adapter = new com.example.sanjay.erp.newChatScreen.MyAdapter(album_allorders.class, R.layout.item_message_sent,
                mainactiv_allorders.FoodViewHolder.class, databaseReference,(DownloadManager)  getSystemService(Context.DOWNLOAD_SERVICE),this);
         horizontal.setStackFromEnd(true);
      ScrollPosition(databaseReference,recyclerView,adapter,horizontal);
        recyclerView.setAdapter(adapter);
        checkMessage(databaseReference);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Edit_message = findViewById(R.id.edittext_chatbox);
        actionButton = findViewById(R.id.button_send_message);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setActionButton();

            }
        });
        Edit_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkMessageBox(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        super.onStart();

    }

    private void loadUserImgListener( final String userimage, final String userid, final String rollno, final String attendance,final long color) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),UserInfo.class);
                intent.putExtra("IMGLINK",userimage);
                intent.putExtra("COLLEGEID",userid);
                intent.putExtra("UNIVID",rollno);
                intent.putExtra("ATTENDANCE",attendance);
                intent.putExtra("COLOR",color);
                intent.putExtra("STAFF",isStaff);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation
                                (help_activity.this,imageView , "user_image");
                  startActivity(intent, optionsCompat.toBundle());
            }
        });
    }

    public void setLastSeen() {

      { Log.e("Userid",Uid.replace(Constants.uid,""));
          FirebaseDatabase.getInstance().getReference().child("users").
                  child(Uid.replace(Constants.uid,"")).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(DataSnapshot dataSnapshot) {
                  long lastSeen=0;
                  String onlines;
                  try {

                     String last = (String) dataSnapshot.child("LASTSEEN").getValue();
                        if (last==null){
                            lastSeen=-1;
                        } else if ( !last.equalsIgnoreCase("online")){
                            lastSeen= Long.parseLong(last);
                            lastseen=lastSeen;
                        }
                      TextView textView = findViewById(R.id.lastseen);

                        setLastSeenText(lastSeen,textView);

                      Log.e("MessageStatus","USERLastSeen is "+lastSeen+"Online is ");
                      loadImageListener(dataSnapshot);

                  }catch (Exception e){

                      findViewById(R.id.lastseen).setVisibility(View.GONE);
                  }
              }

              @Override
              public void onCancelled(DatabaseError databaseError) {

              }
          });
      }


    }
    void loadImageListener(DataSnapshot dataSnapshot){
        long color;
        if (dataSnapshot.child("IMAGECOLOR").getValue()==null){
            color= Color.BLACK;
        }else
            color= (long) dataSnapshot.child("IMAGECOLOR").getValue();
        imageView.setOnClickListener(null);
        loadUserImgListener(Objects.requireNonNull(dataSnapshot.child("USERIMAGE").getValue()).toString(),
                Uid.replace(Constants.uid, ""),
                Objects.requireNonNull(dataSnapshot.child("ROLLNO").getValue()).toString(),
                Objects.requireNonNull(dataSnapshot.child("ATTENDANCE").getValue()).toString(),
                color
        );
    }
        void setLastSeenText(long text,TextView textView) {
           if(isUserBlocked=='f') {
               if (text == 0) {
                   textView.setVisibility(View.VISIBLE);
                   textView.setText(R.string.online);
               } else if (lastseen == text) {
                   SimpleDateFormat sfd;
                   sfd = new SimpleDateFormat("dd MMM", Locale.UK);
                   textView.setVisibility(View.VISIBLE);
                   textView.setText(String.format("last seen " + giveTextLastSeen(text, sfd.format(new Date(Long.parseLong(String.valueOf(text)))), textView) + " at %s", new SimpleDateFormat("HH:mm a", Locale.UK).format(new Date(Long.parseLong(String.valueOf(text))))));

               } else {
                   textView.setVisibility(View.GONE);
               }
           }
        }

    private String giveTextLastSeen(long text, String formateddate,TextView textView) {
        Date d;
        String message="";
        Date  date=new Date();

        try {
            d = new SimpleDateFormat("dd/mm/yy",Locale.UK).parse(new SimpleDateFormat( "dd/mm/yy",Locale.UK).format(new Date(text) ));


        int daysDiff;
            Log.e("Calculating date","today=>"+date+"=>"+d);
            long diff=date.getTime()-d.getTime();
            long diffDays=diff/(24*60*60*1000);
            daysDiff=(int) diffDays;
            Log.e("Calculating date","final days are =>"+daysDiff);

            if (daysDiff==1){
                message=daysDiff+"yesterday";
            }if (daysDiff==0)
                 message="Today";
            else message=formateddate;


        } catch (ParseException e) {
            textView.setVisibility(View.GONE);
            e.printStackTrace();
        }

            return message;


    }

    void receive(){
        try{
            Intent intent=getIntent();

            Uid= intent.getStringExtra("UID");
            isStaff= intent.getBooleanExtra("STAFF",false);
            Canteen= intent.getStringExtra("Address");
            UserName= intent.getStringExtra("UserName");
            img=intent.getStringExtra("img");
            isUserBlocked=intent.getCharExtra("ISUSERBLOCKED",'u');
            if (isStaff){
                findViewById(R.id.menu).setVisibility(View.GONE);
                findViewById(R.id.layout_chatbox1).setVisibility(View.VISIBLE);

            }
            if (img!=null){
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(img).into((ImageView) findViewById(R.id.mainimage));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("Receive","Uid "+Uid+" Username"+UserName);
    }





    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.e("Menu","Item clicked");
         if (id == R.id.block) {
            blockUser();
            return true;
        }else if (id==R.id.delete)
        {    deleteChat();
             return true;
        }else if (id==R.id.Unblock){
             unblockUser();
             return true;
         }else if (id==R.id.follow){
             followUser(Uid.replace(Constants.uid,""));
             return true;
         }else if (id==R.id.unfollow){
             findViewById(R.id.layout_chatbox1).setVisibility(View.GONE);
             unFollowUser(Uid.replace(Constants.uid,""));
             return true;
         }
        return   super.onOptionsItemSelected(item);
    }

    private void unFollowUser(String userid) {
        imageView.setOnClickListener(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                Constants.uid + "/friends").child(userid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/friends").child(Constants.uid) .setValue(null);

        FirebaseDatabase.getInstance().getReference("users/" +
                userid + "/notification").child(String.valueOf(System.currentTimeMillis())).child("message")
                .setValue( getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("USERNAME","")
                        +" has unfollow you");
        findViewById(R.id.layout_chatbox1).setVisibility(View.GONE);
    }


    private void followUser(String userid) {
        try {


            FirebaseDatabase.getInstance().getReference("users/" + Constants.uid + "/PendingRequests/" + userid).child("USERID").setValue(userid);
            FirebaseDatabase.getInstance().getReference("users/" + Constants.uid + "/PendingRequests/" + userid).child("isReceived").setValue(false);

            FirebaseDatabase.getInstance().getReference("users/" + userid + "/PendingRequests/" + Constants.uid).child("USERID").setValue(userid);
            FirebaseDatabase.getInstance().getReference("users/" + userid + "/PendingRequests/" + Constants.uid).child("isReceived").setValue(true);

        }catch (Exception ignored){

        }
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        isActive=true;
        onlineStatus("Online");
try {
    if (messageImage.length>0){
        Glide.with(getApplicationContext()).
                applyDefaultRequestOptions(RequestOptions.noTransformation()).
                load(uriProfileImage.getPath()).into((ImageView)
                findViewById(R.id.messageImage));
        reduceImage(messageImage,Caption);
    }
}catch (Exception ignored){}
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive=false;

        onlineStatus(String.valueOf(System.currentTimeMillis()));

    }
  public void checkUserId(){
        if (Constants.uid==null||Constants.uid.isEmpty()){
            Constants.uid=getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID",null);
        }
        if (!isStaff) {
            checkUserBlockStatus();
            findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openMenuClicked(view);

                }
            });
        }



    }


    private void checkUserBlockStatus() {
        if (!myAccStaff) {
            if (!isStaff) {
                final String originalid = Uid.replace(Constants.uid, "");
                Log.e("Menu", "Loading data from database");
                FirebaseDatabase.getInstance().getReference("users/" +
                        Constants.uid).child("block").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                        if (dataSnapshot.child("blocked_by").hasChild(originalid)) {
                            userIsBlocked();
                            isUserBlocked = 'o';


                        } else if (dataSnapshot.child("blocked").hasChild(originalid)) {
                            iveBlocked();
                            isUserBlocked = 'm';

                        }


                        Log.e("BLOCK_STATUS", "User check refreshed=>status=>" + isUserBlocked);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                FirebaseDatabase.getInstance().getReference("users/" +
                        Constants.uid).child("friends").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(originalid)) {

                            isUserBlocked = 'f';
                            findViewById(R.id.layout_chatbox1).setVisibility(View.VISIBLE);


                        } else {
                            imageView.setOnClickListener(null);
                            findViewById(R.id.layout_chatbox1).setVisibility(View.GONE);
                            findViewById(R.id.lastseen).setVisibility(View.GONE);
                            isUserBlocked = 'n';
                        }
                        Log.e("BLOCK_STATUS", "User check refreshed=>status=>" + isUserBlocked);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }else {
            findViewById(R.id.layout_chatbox1).setVisibility(View.VISIBLE);
        }
    }

    private void iveBlocked() {
        findViewById(R.id.layout_chatbox1).setVisibility(View.GONE);
        imageView.setOnClickListener(null);

    }

    private void userIsBlocked() {
        imageView.setOnClickListener(null);
        findViewById(R.id.layout_chatbox1).setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onlineStatus("Online");
        if (!isStaff)
        checkUserId();
        change_status_to_seen();
        isActive=true;
        //back = true;
        //undo_notification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onlineStatus(String.valueOf(System.currentTimeMillis()));
        isActive=false;
    }
    void onlineStatus(String time){
            FirebaseDatabase.getInstance().getReference().child("users").
                    child(Constants.uid).child("LASTSEEN")
                    .setValue(time);

    }
    @Override
    protected void onResume() {
        super.onResume();
        isActive=true;
        change_status_to_seen();
    }
    void checkMessage(DatabaseReference Address){
        Log.e("CheckMessage","Inside check message");
        Address.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()||!dataSnapshot.hasChildren()){
                    //check_messages(0);
                    Log.e("CheckMessage","Inside check message inside not found");
                    findViewById(R.id.no_messages).setVisibility(View.VISIBLE);

                }else
                    Log.e("CheckMessage","Inside check message found");
                findViewById(R.id.progressbar).setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.progressbar).setVisibility(View.GONE);
            }
        });

    }
    void checkMessageBox(String message) {
 //.matches(".*\\w.*"
        if (!message.trim().isEmpty()) {

             Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.ic_send_black_24dp).into(actionButton);
            actionButtonStatus = true;
            //actionButton.T
        }

        else {

            Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(R.drawable.add_color).into(actionButton);
            actionButtonStatus = false;
        }




    }



     void setActionButton() {
         if (actionButtonStatus) {
            //TotalHelp(type);
            new StoreMessage(Constants.uid+"_TEXT",Uid,(EditText) Edit_message,(RelativeLayout)findViewById(R.id.progressRelative),Edit_message.getText().toString().trim(),getApplicationContext());
        } else {
            CardView cardView = findViewById(R.id.card_view);
            //showDiag();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                if (cardView.getVisibility() == View.INVISIBLE) {

                    animation(true);
                } else {
                    animation(false);
                }
            } else {
                if (cardView.getVisibility() == View.INVISIBLE) {
                    findViewById(R.id.card_view).setVisibility(View.VISIBLE);
                    showZoomIn();
                    ExtraOptions();
                } else {
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                }
            }

        }

    }



    void showZoomIn() {
        Animation zoomIn =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.zoom_in);
        findViewById(R.id.relateGallery).startAnimation(zoomIn);
        findViewById(R.id.relateCamera).startAnimation(zoomIn);
        findViewById(R.id.relateOrder).startAnimation(zoomIn);
        ExtraOptions();

    }

    void ExtraOptions() {
        findViewById(R.id.gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animation(false);
                } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animation(false);
                } else findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
            }
        });
        findViewById(R.id.order).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  back = false;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                     animation(false);
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                    openDoc(true,"");
                } else {
                    findViewById(R.id.card_view).setVisibility(View.INVISIBLE);
                    openDoc(true,"");
                }
            }
        });

    }

    void storeToStorage(String caption) {
        int Image_size;
        String scheme = uriProfileImage.getScheme();
         if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            try {
                InputStream fileInputStream =
                        getApplicationContext().getContentResolver().openInputStream(uriProfileImage);
                if (fileInputStream != null) {
                     Image_size = fileInputStream.available() / 1024;
                    Log.e("help images", "Size is " + String.valueOf(Image_size));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (scheme.equals(ContentResolver.SCHEME_FILE)) {
            String path = uriProfileImage.getPath();
            File f;
            try {
                f = new File(path);
                Image_size = (int) f.length() / 1024;
                Log.e("help images", "Size is " + String.valueOf(Image_size));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        reduceImage(55,caption);

    }


    void reduceImage(int quality, String caption) {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            byte[] data = baos.toByteArray();
            Log.e("help image", " Image size is in bytes " + data.length);
            if ((data.length) / 1024 > 250) {
                Log.e("help image", " Image size is modified in bytes " + data.length);
                bmp = null;
                baos = null;
                reduceImage(quality - 15,caption);

            } else
                reduceImage(data,caption);

        } catch (Exception OutOfMemoryError) {
            Toast.makeText(this, "Failed to send image might be due to image size is more", Toast.LENGTH_SHORT).show();
            Log.e("help image", "Out of memory exception");
        }

    }


    void reduceImage(byte[] data, final String caption) {
        try {
            //final String fileName=name(data.toString());
                final StorageReference mStorageRef =
                        FirebaseStorage.getInstance().getReference("Chat_Images/"+
                                Uid+"/" + data.toString().substring(data.toString().lastIndexOf("/")+1));
                findViewById(R.id.imageCard).setVisibility(View.VISIBLE);
                final UploadTask uploadTask = mStorageRef.putBytes(data);
                final ProgressBar progressBar=findViewById(R.id.progressbarsmall);
                findViewById(R.id.cancel_message).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!uploadTask.isComplete()){
                            uploadTask.cancel();
                            findViewById(R.id.imageCard).setVisibility(View.GONE);
                        }
                    }
                });

                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                            throw Objects.requireNonNull(task.getException());

                        return mStorageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            try {
                                img= String.valueOf((task.getResult()));
                                findViewById(R.id.imageCard).setVisibility(View.GONE);
                                new StoreMessage(Constants.uid+"_IMG",
                                        Uid,(EditText) findViewById(R.id.edittext_chatbox),(RelativeLayout)findViewById(R.id.progressRelative),img,caption,getApplicationContext());

                                messageImage=null;
                                Log.e("Firebase file upload", "contentList data are " + make_announcement.contentList);


                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        int   currentprogress=(int) progress;

                        Log.e("Firebase file upload", "Progress is "+progress+" curre "+currentprogress);
                        progressBar.setProgress(currentprogress);
                    }
                });

           /* StorageReference profileImageRef =
                    FirebaseStorage.getInstance().getReference("Chat_Images/"+
                            Uid+"/" + System.currentTimeMillis() + ".jpg");
            findViewById(R.id.imageCard).setVisibility(View.VISIBLE);
            final UploadTask uploadTask = profileImageRef.putBytes(data);
            final ProgressBar progressBar=findViewById(R.id.progressbarsmall);
            findViewById(R.id.cancel_message).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!uploadTask.isComplete()){
                        uploadTask.cancel();
                        findViewById(R.id.imageCard).setVisibility(View.GONE);
                    }
                }
            });
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    int currentprogress=(int) progress;
                    Log.e("firebase messaging","Progress is "+progress+" curre "+currentprogress);
                    progressBar.setProgress(currentprogress);

                }
            });
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                      Intent intent=new Intent(help_activity.this,image_zoomer.class);
                      intent.putExtra("Image", uriProfileImage.toString());
                     intent.putExtra("From", "Camera_Image");
                     intent.putExtra("Caption", caption.toString());
                     messageImage=null;
                     findViewById(R.id.imageCard).setVisibility(View.GONE);
                     startActivity(intent);

                    Toast.makeText(help_activity.this, "Upload task completed", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    findViewById(R.id.imageCard).setVisibility(View.GONE);
                    messageImage=null;
                    Toast.makeText(help_activity.this, "Upload task failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });*/
        } catch (Exception OutOfMemoryError) {
            Log.e("help image", "Out of memory exception");
        }
    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void animation(boolean show) {
        final CardView cardView = findViewById(R.id.card_view);

        int height = cardView.getHeight();
        int width = cardView.getWidth();
        int endRadius = (int) Math.hypot(width, height);
        int cx = (int) (actionButton.getX() + (cardView.getWidth() / 2));
        int cy = (int) (actionButton.getY()) + cardView.getHeight() + 56;

        if (show) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(cardView, cx, cy, 0, endRadius);
            revealAnimator.setDuration(700);
            revealAnimator.start();
            cardView.setVisibility(View.VISIBLE);
            showZoomIn();
        } else {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(cardView, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    cardView.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }


 void ScrollPosition(RecyclerView recyclerView, long pos, RecyclerView.Adapter adapter){
        recyclerView.scrollToPosition(adapter.getItemCount()-1);
     Log.e("Scrolling","Adapter item count "+adapter.getItemCount());

 }
     void ScrollPosition(DatabaseReference Address, final RecyclerView recyclerView, final MyAdapter adapter, final LinearLayoutManager horizontal) {
         final long[] childrens = new long[1];
         FirebaseDatabase.getInstance().getReference().child("chat").
                 child(Uid).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                childrens[0] = dataSnapshot.getChildrenCount();
              //  undo_notification();
                //change_status_to_seen();
                 Log.e("scrolling", "Childs " + childrens[0]);
                 //check_messages(childrens[0]);
                //change_status_to_seen();
//                findViewById(R.id.no_messages).setVisibility(View.GONE);
//                ScrollPosition(recyclerView,dataSnapshot.getChildrenCount(),adapter);
             }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                undoCounter();
                Log.e("scrolling", "Childs String s is "+s);
            try {
                if (s.toLowerCase().contains("last"))
                {
                    try{
                        Log.e("MessageStatus", "Childs LASTSEEN "+
                                (long)dataSnapshot.getValue());
                    }catch (Exception e){

                    }try{
                    Log.e("MessageStatus", "Childs CanteenOnline "+
                            (String)dataSnapshot.child("CanteenOnline").getValue());
                }catch (Exception e){

                }
                   // setLastSeen( );
                }
            }catch (Exception ignored){

            }
                change_status_to_seen();
                findViewById(R.id.no_messages).setVisibility(View.GONE);
                ScrollPosition(recyclerView,dataSnapshot.getChildrenCount(),adapter);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
     }

    void undoCounter(){
        FirebaseDatabase.getInstance().getReference().child("users").
                child(Constants.uid).child("chat").child(Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    FirebaseDatabase.getInstance().getReference().child("users").
                            child(Constants.uid).child("chat").child(Uid).child("Last_message_counter").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        }
    private void change_status_to_seen() {
        if (isActive) {
            Log.e("MessageStatus","Activity   running active is "+isActive);

            FirebaseDatabase.getInstance().getReference().child("chat").
                    child(Uid ).child("messages")
                    .orderByChild("Seen").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("chat").
                            child(
                            Uid ).child("messages");
                    Log.e("MessageStatus","changing all to seen "+dataSnapshot.getChildrenCount());
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        try {

                            if (dataSnapshot1.child("From").getValue().toString().contains(Uid.replace(Constants.uid,""))) {
                                Log.e("scrolling", "change seen status completed");
                                databaseReference.child(dataSnapshot1.getKey()).child("Seen").setValue(true);


                            }
                        } catch (Exception ignored) {
                        }
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            Log.e("MessageStatus","Activity not running active is "+isActive);

        }
    }


    public   void openDoc(boolean show,String order) {

        if (show) {
            //orders_dialog();
            openDocument();
        } else {

            Edit_message.setText(String.format("%s %s ", Edit_message.getText().toString().trim(),order));
         }
    }

    private void openDocument() {
        /*Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Document"), CHOOSE_DOC);*/
        requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,CHOOSE_DOC);
    }

    private void openCamera() {

        requestPermission(this, Manifest.permission.CAMERA,OPEN_CAMERA);
        Log.e("Camera","Inside Open_camera permission");
    }

    private void showImageChooser() {

        requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE);

    }
     @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
     private void requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);
        } else {
            if (value==103)
            {Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent, OPEN_CAMERA);

            }else if (value==104)
            {   Intent intent = new Intent(ACTION_GET_CONTENT);
                intent.setType("application/*");

                startActivityForResult(Intent.createChooser(intent, "Select Document"), CHOOSE_DOC);

            }
          else  startActivity(new Intent(this, GallerySample.class));
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        startActivity(new Intent(this, GallerySample.class));



                 }
                break;
            }
            case OPEN_CAMERA:
                Log.e("Camera","Inside persmission result ");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Camera","Inside persmission result granted");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, OPEN_CAMERA);



                }
                break;
        }
    }
    void send_to_Image_viewer(Uri url){
        start_camera_dialog(url);
    }
    void start_camera_dialog(final Uri uri){

            Log.e("Camera","Inside start_camera_dialog");
            final View dialogView = View.inflate(help_activity.this, R.layout.image_viewer2, null);


          Dialog dialog = new Dialog(help_activity.this,R.style.Dialog);

        Window window=dialog.getWindow();
        if (window != null) {
            Log.e("Camera","Inside start_camera_dialog window is not null");
            window.setLayout( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        } else{
            dialog = new Dialog(help_activity.this);
              dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Log.e("Camera","Inside start_camera_dialog  window is null");
        }
        dialog.setContentView(dialogView);
        Glide.with(getApplicationContext()).load(uri).into((ImageView) dialogView.findViewById(R.id.imageId));

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

         dialogView.findViewById(R.id.send).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditText editText=dialogView. findViewById(R.id.ImageCaption);
                    Caption= editText.getText().toString().trim();
                    storeToStorage(Caption);
                    finalDialog.dismiss();
                 }
            });
     dialog.show();
    }
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         ImageView imageView=findViewById(R.id.messageImage);
         Log.e("help","Activity result open");
        if (requestCode == OPEN_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            Log.e("help", "activity result successful");
              Log.e("Camera", uriProfileImage.toString());
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            send_to_Image_viewer(uriProfileImage);

        } else if (requestCode == CHOOSE_DOC && resultCode == RESULT_OK && data != null && data.getData() != null){
            Glide.with(this).load(getResources().getDrawable(R.drawable.otherdoc)).into(imageView);
            uploadDoc(data.getData());
        }else{
            Log.e("help", "activity result unsuccessful reqcode" + requestCode + " resultcode " + requestCode
                    + " data "+data.getData());
        }
    }
    String name(String link){
        if(link!=null){
            if (!link.isEmpty()){
                if(link.contains("%3A"))
                  link=  link.substring(link.lastIndexOf("%3A")+3);
                if(link.contains("%2F"))
                    link=  link.substring(link.lastIndexOf("%2F")+3);
                link.replace("%20"," ");
                return link;
            }
        }
        return "";
    }

    private void uploadDoc(Uri data){
            try {Log.e("Uploading DOC","Name is=>"+data);
                final String fileName=name(data.toString());
                final StorageReference mStorageRef =
                    FirebaseStorage.getInstance().getReference("Chat_Images/"+
                            Uid+"/" + fileName);
                findViewById(R.id.imageCard).setVisibility(View.VISIBLE);
                final UploadTask uploadTask = mStorageRef.putFile(data);
                final ProgressBar progressBar=findViewById(R.id.progressbarsmall);
                findViewById(R.id.cancel_message).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!uploadTask.isComplete()){
                            uploadTask.cancel();
                            findViewById(R.id.imageCard).setVisibility(View.GONE);
                        }
                    }
                });

                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                            throw Objects.requireNonNull(task.getException());

                        return mStorageRef.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()){
                            try {
                                img= String.valueOf((task.getResult()));
                                findViewById(R.id.imageCard).setVisibility(View.GONE);
                                Log.e("Uploading DOC","Name is=>"+img);
                                new StoreMessage(Constants.uid+"_DOC",
                                        Uid,(EditText) findViewById(R.id.edittext_chatbox),(RelativeLayout)findViewById(R.id.progressRelative),img,fileName,getApplicationContext());

                                messageImage=null;


                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    }
                });
                uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        int   currentprogress=(int) progress;

                        Log.e("Firebase file upload", "Progress is "+progress+" curre "+currentprogress);
                        progressBar.setProgress(currentprogress);
                    }
                });

               /* */
            } catch (Exception OutOfMemoryError) {
                Log.e("help image", "Out of memory exception");
            }
        }
    @Override
    public void onBackPressed() {


            Intent intent=getIntent();

            if (intent.getStringExtra("From")!=null&& intent.getStringExtra("From").equals("Notification")) {
                finish();
                //startActivity(new Intent(this, fin));
             }
              else super.onBackPressed();
    }

    void blockUser(){
        String originalid=Uid.replace(Constants.uid,"");
         FirebaseDatabase.getInstance().getReference("users/" +
                Constants.uid).child("friends").child(originalid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                originalid).child("friends").child(Constants.uid).setValue(null);

        //nullify if in follow request
          FirebaseDatabase.getInstance().getReference("users/" +
                Constants.uid).child("PendingRequests").child(originalid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                originalid).child("PendingRequests").child(Constants.uid).setValue(null);




        FirebaseDatabase.getInstance().getReference("users/" +
                originalid).child("block").child("blocked_by").child(Constants.uid).setValue(getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("USERNAME",""));
        FirebaseDatabase.getInstance().getReference("users/" +
                Constants.uid).child("block").child("blocked").child(originalid).setValue(UserName);


        findViewById(R.id.layout_chatbox1).setVisibility(View.GONE);
        isUserBlocked='m';
        openMenuClicked(findViewById(R.id.menu));
    }
    private void unblockUser() {
        String userid=Uid.replace(Constants.uid,"");

        FirebaseDatabase.getInstance().getReference("users/" +
                Constants.uid).child("block").child("blocked").child(userid).setValue(null);
        FirebaseDatabase.getInstance().getReference("users/" +
                userid).child("block").child("blocked_by").child(Constants.uid).setValue(null);

         checkUserBlockStatus();
        isUserBlocked='d';
    }
    private void deleteChat() {

    }

    public void openMenuClicked(View view) {
        PopupMenu popupMenu=new PopupMenu(this,view);
        MenuInflater inflater=getMenuInflater();
        Log.e("Menu","Menu Clicked=>"+isUserBlocked);
        inflater.inflate(R.menu.chat_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                onOptionsItemSelected(menuItem);
                return false;
            }
        });
         switch (isUserBlocked) {
            case 'm':

                popupMenu.getMenu().findItem(R.id.Unblock).setVisible(true);
                break;
            case 'o':
                 popupMenu.getMenu().findItem(R.id.block).setVisible(true);
                break;
                case 'f':
                 popupMenu.getMenu().findItem(R.id.unfollow).setVisible(true);
                    popupMenu.getMenu().findItem(R.id.block).setVisible(true);
                 break;
            case 'n':
                imageView.setOnClickListener(null);
                popupMenu.getMenu().findItem(R.id.block).setVisible(true);
                popupMenu.getMenu().findItem(R.id.follow).setVisible(true);
                 break;

            default:
//                popupMenu.getMenu().findItem(R.id.Unblock).setVisible(false);
                break;
        }
        popupMenu.show();
    }
}

