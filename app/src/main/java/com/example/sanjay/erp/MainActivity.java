package com.example.sanjay.erp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
 import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
 String loginurl1;
boolean load=true;
    private static String uname;
    private static String pass;
    int progresscount=0;
    private WebView webView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (uname==null){
            Intent intent=getIntent();
            uname=intent.getStringExtra("COLLEGEID");
            pass=intent.getStringExtra("PASSWORD");
            if (uname==null)
            startActivity(new Intent(this,LoginFrontScreen.class));
        }
        setContentView(R.layout.activity_main);
        Intent intent=getIntent();
          uname=intent.getStringExtra("COLLEGEID");
          pass=intent.getStringExtra("PASSWORD");
          loginurl1=intent.getStringExtra("LINK");
         loadUrl(loginurl1);
    }
    void loadUrl(String url){

         webView = findViewById(R.id.webview);

       final ProgressBar progressBar = findViewById(R.id.progressBar);
       webView.setWebViewClient(new Browse(url));
       webView.setWebChromeClient(new WebChromeClient() {
           @Override
           public void onProgressChanged(WebView view, int newProgress) {
               super.onProgressChanged(view, newProgress);
               progressBar.setProgress(newProgress);
           }
       });

       webView.getSettings().setLoadsImagesAutomatically(true);
       webView.getSettings().setJavaScriptEnabled(true);
       webView.getSettings().setDomStorageEnabled(true);
       webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
       webView.loadUrl(url);

}
    public void onclickback(View view) {

       onBackPressed();
    }
    void headerName(String Default,String link){
        String name="";
      try {
          if (link.equalsIgnoreCase("http://mit.thecollegeerp.com/academic/studentarea/link.php"))
              name="Home";
          else if (link.equalsIgnoreCase("http://mit.thecollegeerp.com/academic/studentarea/myprofile.php"))
          {
              Intent intent=new Intent(this,MainActivity.class);
              intent.putExtra("COLLEGEID", uname);
              intent.putExtra("PASSWORD", pass);
              finish();
              startActivity(intent);
          }
          else name=link.substring(link.indexOf("studentarea/")+12,link.indexOf(".php"));
      }catch (Exception e){
          name=Default;
      }
      if (name.contains("_")) {
        name=  name.replace("_"," ");
      }
      if (name.equalsIgnoreCase("ratefaculty")){
          loadUrl(loginurl1);
      }

      try {
          TextView textView = findViewById(R.id.toolbar);

          textView.setText(name);
      }catch (Exception e){}
    }

    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
        if (webView.canGoBack()){
            TextView textView=findViewById(R.id.toolbar);
            if (!textView.getText().toString().equalsIgnoreCase("home"))
            webView.goBack();
        }else
            finish();

    }

    private class Browse extends WebViewClient {
        String url;
        Browse(String url){
            this.url=url;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
          if (load)
              findViewById(R.id.progressBar).setVisibility(View.GONE);
            else  findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            headerName(view.getTitle(),url);
             findViewById(R.id.progressBar).setVisibility(View.GONE);
            view.getSettings().setDomStorageEnabled(true);
           view.getSettings().setJavaScriptEnabled(true);
            String autologin="javascript:{document.getElementsByTagName(\"input\")[0].value='" +uname+"';"+
                    "document.getElementsByTagName(\"input\")[1].value='"+pass+"';"+
                    "var frms=document.getElementsByName('txtsubmit');" +
                    "frms[0].click();}";
           ProgressBar progressBar= findViewById(R.id.bigprogress);
           progressBar.setProgress(progresscount+50);
           if (load) {

               loadUrl(autologin);
           }else{
//               findViewById(R.id.webview).setVisibility(View.VISIBLE);
//                findViewById(R.id.bigprogress).setVisibility(View.GONE);
               finish();
               SharedPreferences sharedPreferences=getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);
               SharedPreferences.Editor editor=sharedPreferences.edit();
//               editor.putString("COLLEGEID",uname);
//               editor.putString("PASSWORD",pass);
//               editor.apply();
                  Intent intent=new Intent(MainActivity.this,Home.class);

               intent.putExtra("URL",sharedPreferences.getString("URL",""));
               startActivity(intent);

           }

            load=false;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, request);
        }
    }
}
