package com.example.sanjay.erp.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.sanjay.erp.R;
import com.example.sanjay.erp.webFrame.WebFrame;

public class WebLoader extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_loader);
        Intent intent = getIntent();
        Log.e("webloader","url=>"+intent.getStringExtra("LINK"));
       try {

           Bundle bundle = new Bundle();
           bundle.putString("URL", intent.getStringExtra("LINK"));
           WebFrame web = new WebFrame();
           web.setArguments(bundle);
           FragmentTransaction fragmentTransaction1;
           fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
           fragmentTransaction1.replace(R.id.frameLayout, web);
           fragmentTransaction1.commit();
       }catch (Exception e){
           e.printStackTrace();
       }
       }
    public void onclickback(View view) {
        finish();
    }
}
