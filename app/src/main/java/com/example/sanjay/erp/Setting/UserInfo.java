package com.example.sanjay.erp.Setting;

 import android.content.Intent;
 import android.graphics.Color;
 import android.os.Bundle;
 import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
 import android.support.v7.widget.DefaultItemAnimator;
 import android.support.v7.widget.LinearLayoutManager;
 import android.support.v7.widget.RecyclerView;
 import android.text.Editable;
 import android.text.TextWatcher;
 import android.util.Log;
 import android.view.View;
 import android.view.ViewGroup;
 import android.view.ViewStub;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.LinearLayout;
 import android.widget.ProgressBar;
 import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
 import com.example.sanjay.erp.Constants;
 import com.example.sanjay.erp.R;
 import com.example.sanjay.erp.newChatScreen.SearchAdapter;
 import com.example.sanjay.erp.newChatScreen.album_allorders;
 import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;
 import com.google.firebase.database.FirebaseDatabase;

 import java.util.Locale;
 import java.util.Objects;

 import static com.example.sanjay.erp.newChatScreen.AllUser.searchKey;
 import static com.example.sanjay.erp.newChatScreen.UsersListTabActivity.Totalcount;

public class UserInfo extends AppCompatActivity {
    ImageView imageView;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (Constants.type.equalsIgnoreCase("STAFF")){
            //findViewById(R.id.userInfo).setVisibility(View.GONE);
            setTheme(R.style.popup_dialog1);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        imageView=findViewById(R.id.image_message_profile);
        extractUserDetails();
        loadFriends();

    }

    private void loadFriends() {
        LinearLayout layout=findViewById(R.id.mainLayout);
        RecyclerView recyclerView=new RecyclerView(this);
        recyclerView.setId(R.id.recycler_view);
        recyclerView.setLayoutParams
                (new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(recyclerView);
        EditText editText=findViewById(R.id.search);
        editText.setVisibility(View.VISIBLE);
//        editText.setId(R.id.searchEdit);
//        editText.setHint("Search friends...");
//        editText.setLayoutParams
//                (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT));
//        layout.addView(editText);
         recyclerView.setBackgroundColor(getResources().getColor(R.color.Black));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SearchAdapter searchAdapter=new SearchAdapter(album_allorders.class,R.layout.chat_layout,mainactiv_allorders.FoodViewHolder.class,
                FirebaseDatabase.getInstance().
                        getReference("users").child(uid).child("friends"),searchKey,this
                , findViewById(R.id.progressBar),'o', null,this,Constants.uid);

        recyclerView.setAdapter(searchAdapter);
        searchBarListner(searchAdapter,editText);
    }

    void searchBarListner(final SearchAdapter searchAdapter,EditText searchbar){

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
                //progressBar.setVisibility(View.VISIBLE);
                Totalcount=0;
                searchAdapter.notifyDataSetChanged();

             }
        });
    }


    void loadImageListner(final String imgId){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(UserInfo.this,ShowImg.class);
                intent.putExtra("IMGLINK",imgId);
                intent.putExtra("COLOR",getIntent().getLongExtra("COLOR", Color.BLACK));
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation
                                (UserInfo.this,findViewById(R.id.userimage) , "user_image");
                startActivity(intent, optionsCompat.toBundle());

            }
        });
    }
    void extractUserDetails(){
        Intent intent=getIntent();
        uid=intent.getStringExtra("COLLEGEID");
        String image=intent.getStringExtra("IMGLINK");
        if (image!=null&&!image.isEmpty()){
            try {
                loadImageListner(image);
                Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(image).into(imageView);
            }catch (Exception e){e.printStackTrace();}

        }
                if (intent.getBooleanExtra("STAFF",false)){
                    findViewById(R.id.userInfo).setVisibility(View.GONE);
                }else {
                    TextView textView = findViewById(R.id.rollnoEnter);
                    textView.setTextSize(14);
                    textView.setText(intent.getStringExtra("UNIVID"));
                    textView = findViewById(R.id.collegeEnter);
                    textView.setTextSize(14);
                    textView.setText(uid);
                    textView = findViewById(R.id.attendanceEnter);
                    textView.setTextSize(14);
                    try {
                        Double att = Double.valueOf(intent.getStringExtra("ATTENDANCE"));
                        Log.e("Attendance", "" + att);
                        if (att != null)
                            textView.setText(String.format("%s%%", String.format(Locale.ENGLISH, "%.2f", att)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }





    }

}
