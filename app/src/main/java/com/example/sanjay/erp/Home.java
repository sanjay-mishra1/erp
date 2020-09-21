package com.example.sanjay.erp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.webkit.CookieManager;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.ExtraClasses.issue;
import com.example.sanjay.erp.announcement.ShowAnnouncement;
import com.example.sanjay.erp.newChatScreen.chat_app_Activity;
import com.example.sanjay.erp.update.update;
import com.example.sanjay.erp.webFrame.Nav_Header;
import com.example.sanjay.erp.webdata.web;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.Objects;

import static com.example.sanjay.erp.Constants.Stack;
import static com.example.sanjay.erp.Constants.fields;
import static com.example.sanjay.erp.Constants.uname;
import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class Home extends AppCompatActivity  {
     private DrawerLayout drawer;
    TextView textView  ;
    View searchScreen ;
    View searchlinear ;
    FrameLayout frameLayoutExtra ;
    FrameLayout frameLayout ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_home);
        //WebView webView;
        //CS172021111=>482000


         checkUpadate();
          textView = findViewById(R.id.title);
          searchScreen=findViewById(R.id.searchScreen);
          searchlinear=findViewById(R.id.searchLinear);
          frameLayoutExtra=findViewById(R.id.fragmentExtra);
          frameLayout=findViewById(R.id.fragment);
        Constants.navEmpty=true;
        Constants.Stack.clear();
        Toolbar toolbar = findViewById(R.id.toolbar);
        profileImage();
        setSupportActionBar(toolbar);
         Stack.push("Home");
        Constants.isTitleLoaded=true;
         Log.e("RunningStates","Inside oncreate");
          drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Intent intent=getIntent();

        FragmentTransaction fragmentTransaction2;
        Bundle bundle=new Bundle();
        bundle.putBoolean("LOADLINK",true);
        bottom_nav bottom_nav= new bottom_nav();
        bottom_nav.setArguments(bundle);
         fragmentTransaction2= getSupportFragmentManager().beginTransaction();
        fragmentTransaction2.replace(R.id.nav_frame,bottom_nav);
        fragmentTransaction2.commit();
         if (Constants.type.isEmpty()){
            Constants.type= getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("TYPE","");
        }
        if(Constants.type.toLowerCase().equals("student"))
        {

            Log.e("Home","Type is  student");
            new ExtractData(intent.getBooleanExtra("STORE",false)).run();

        }else if (Constants.type.toLowerCase().equals("staff")){
             new ExtractData(intent.getStringExtra("URL"),
             intent.getBooleanExtra("STORE",false));
            Log.e("Home","Type is not student =>"+Constants.type);
        }

        //loadUserImage("http://mit.thecollegeerp.com/studentphoto/4868.jpg",webView);
     }

    void profileImage(){
         Log.e("CheckUser","inside home....checking............................");
        NavigationView navigationView=findViewById(R.id.nav_view);
        View header = navigationView.getHeaderView(0);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("PROFILEIMG",""))
        .into((ImageView) header.findViewById(R.id.userimage));
    }

    public void onBackPressed() {
        findViewById(R.id.appbar).setVisibility(View.VISIBLE);
        Log.e("Home","Stack is =>"+Constants.Stack);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {   drawer.closeDrawers();
         }else  {

            if (searchScreen.getVisibility()==View.VISIBLE){
                {Glide.with(Objects.requireNonNull(getApplicationContext())).load(getApplicationContext().getResources().getDrawable(R.drawable.ic_search_black_24dp)).into((ImageView) findViewById(R.id.searchIcon));
                    searchScreen.setVisibility(View.GONE);
                    Log.e("BACK","  hidding search screen...");
                }
            }else
            if (frameLayoutExtra.getVisibility() == View.VISIBLE) {
                Log.e("BACK","fragment extra is visible hidding...");

                frameLayoutExtra.setVisibility(View.GONE);
                searchlinear.setVisibility(View.GONE);
               frameLayout.setVisibility(View.VISIBLE);
                FragmentTransaction fragmentTransaction2;
                fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                bottom_nav bottom_nav = new bottom_nav();
                Bundle bundle = new Bundle();
                bundle.putInt("ACTIVATE", 0);
                bottom_nav.setArguments(bundle);
                fragmentTransaction2.replace(R.id.nav_frame, bottom_nav);
                fragmentTransaction2.commit();
            } else {
                Log.e("BACK","fragment  is visible popping stack...");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSupportFragmentManager().popBackStack();
                        try {
                            Stack.pop();
                            textView.setText(Stack.lastElement());
                        } catch (Exception ignored) {

                        }
                    }
                }, 100);
                if (Stack.isEmpty()||Stack.lastElement().equals("Home"))
                super.onBackPressed();

            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.Logout) {
            SharedPreferences sharedPreferences = this.getSharedPreferences("USERCREDENTIALS", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            finish();
            fields = 0;
            uname = "";
            startActivity(new Intent(this, LoginFrontScreen.class));
            return true;
        }else
        if (id==R.id.Report){
            Intent intent  =new Intent(this,issue.class);
            intent.putExtra("FROM","Issue");
            startActivity(intent);
            return true;
        }
        return   super.onOptionsItemSelected(item);
    }
    void sendForUpdate(DataSnapshot dataSnapshot){
        Intent intent=new Intent(Home.this,update.class);
        intent.putExtra("Link",(String)dataSnapshot.child("UPDATE_LINK").getValue());
        intent.putExtra("Force",(String)dataSnapshot.child("FORCE_UPDATE").getValue());
        startActivity(intent);
    }
    void checkUpadate(){
        Log.e("Update","Checking for update...");
        FirebaseDatabase.getInstance().getReference("extra/update").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              try {Log.e("Update","Loaded... "+dataSnapshot.child("APP_NEW_VERSION").getValue());
                  if ((Float)dataSnapshot.child("APP_NEW_VERSION").getValue()>Float.parseFloat(getString(R.string.app_version))){
                      sendForUpdate(dataSnapshot);
                  }
              }catch (ClassCastException e){

                  if ((long)dataSnapshot.child("APP_NEW_VERSION").getValue()>Float.parseFloat(getString(R.string.app_version))){
                      sendForUpdate(dataSnapshot);
                  }

                  Log.e("Update","Error..."+e.getMessage());
                  e.printStackTrace();
              }catch (Exception e){
                  e.printStackTrace();
                  Log.e("Update","Error..."+e.getMessage());
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



   public   class ExtractData extends Thread{
        WebView webView;
        String url;
        NavigationView navigationView;
        boolean isStore;
        public void run(){
            loadUrl(url);
        }

        ExtractData(String url,boolean isStore){
            webView=findViewById(R.id.webview);
            this.url=url;
            this.navigationView=(NavigationView) findViewById(R.id.nav_view);
            this.isStore=isStore;
             webView.setWebViewClient(new web(
                    (WebView) (findViewById(R.id.webview)),url,getApplicationContext(),navigationView,isStore ,(DrawerLayout) findViewById(R.id.drawer_layout),getSupportFragmentManager(),
                    (View )findViewById(R.id.fragment),
                    (View) findViewById(R.id.fragmentExtra),true,(View) findViewById(R.id.searchLinear)));
            loadUrl(url);
        }
        ExtractData(boolean isStore){//student data
              this.navigationView=(NavigationView) findViewById(R.id.nav_view);
            this.isStore=isStore;
            this.url="http://mit.thecollegeerp.com/academic/studentarea/myprofile.php";
            webView=findViewById(R.id.webview);



            Log.e("ExtractDataThread","Inside constructor "+url);
            webView.setWebViewClient(new web(
                    (WebView) (findViewById(R.id.webview)),url,getApplicationContext(),navigationView,isStore ,(DrawerLayout) findViewById(R.id.drawer_layout),getSupportFragmentManager(),
                    (View )findViewById(R.id.fragment),
                    (View) findViewById(R.id.fragmentExtra),findViewById(R.id.searchLinear)));
        }
                @SuppressLint("SetJavaScriptEnabled")
        void loadUrl(String url){



            Log.e("ExtractDataThread","Inside loadurl "+url);
             webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(url);



        }


    }





}
