package com.example.sanjay.erp.newChatScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

 import com.example.sanjay.erp.R;

public class imageViewer extends AppCompatActivity {
    WebView webView;
    String img;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);
        receive();
       // webView=findViewById(R.id.webview);
        loadImage();

    }
    void loadImage(){
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.loadUrl(img);
    }
    void receive(){
       try{
           Intent intent=getIntent();
           img=intent.getStringExtra("Image");
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


}
