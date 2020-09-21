package com.example.sanjay.erp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanjay.erp.Home.ExtractData;
import com.example.sanjay.erp.webFrame.WebFrame;
import com.example.sanjay.erp.webdata.web;

import java.util.Objects;

import static com.example.sanjay.erp.Constants.fields;
import static com.example.sanjay.erp.Constants.uname;

public class ReloginFragment extends WebViewClient{
    private   TextView messageTx;
    private   Button loginBt;
    private   View errorScreen;
    private String uid;
    private String pwd;
    private RelativeLayout progress;
    private String link;
    private int count=0;
    private String loginLink;
    private Context context;
    private FragmentActivity activity;
     public ReloginFragment(FragmentActivity activity, String uid, String pass, RelativeLayout progress, String loginLink, String url, View errorScreen, Button loginBt, TextView messageTx, Context context){
        this.uid=uid;
        this.progress=progress;
         this.pwd=pass;
        this.link=url;
        this.context=context;
        this.loginLink=loginLink;
        this.errorScreen=errorScreen;
        this.loginBt=loginBt;
        this.messageTx=messageTx;
        this.activity=activity;
    }
    private void loadMainView(WebView webView){

         Log.e("WebFrame","Inside loadextrascript ");
        String link="http://result.rgpv.ac.in/Result/BErslt.aspx";
        webView.evaluateJavascript("javascript:(function(){ " +
                // "var s='<a href=\'"+link+"\'><strong><font>RGPV Exam Result</font></strong></a>';" +
                "document.getElementsByClassName('LeftTable_Td_alter5')[0].innerHTML='" +
                "<a href=\""+link+"\">RGPV Exam Result</a>';"+
                "var obj=document.getElementById('NewsWindow').content;" +
                "  var html_string= \"content\";" +
                "  document.getElementById('NewsWindow').src = 'https://mitthecanteen.000webhostapp.com/events.html'; "+
                "return ('0');})();" +
                "", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);

                        try {
                            new ExtractData(false);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("URL","extract");
//                            WebFrame web = new WebFrame();
//                            web.setArguments(bundle);
//
//                            FragmentTransaction fragmentTransaction1 =getActivity(). getSupportFragmentManager().beginTransaction();
//                            fragmentTransaction1.replace(R.id.fragmentExtra, web);
//                            fragmentTransaction1.commit();
                        }catch (Exception e){
                            e.printStackTrace();
                            errorScreen.setVisibility(View.VISIBLE);
                            progress.setVisibility(View.GONE);
                        }
                    }
                },1600);

            }
        });
        if (link.contains("link"))
            loadExtraScript(webView);


    }
    private void loadExtraScript(WebView webView) {
        Log.e("WebFrame","Inside loadextrascript ");
        String link="http://result.rgpv.ac.in/Result/BErslt.aspx";
        webView.evaluateJavascript("javascript:(function(){ " +
                // "var s='<a href=\'"+link+"\'><strong><font>RGPV Exam Result</font></strong></a>';" +
                "document.getElementsByClassName('LeftTable_Td_alter5')[0].innerHTML='" +
                "<a href=\""+link+"\">RGPV Exam Result</a>';"+

                "return ('0');})();" +
                "", new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {


            }
        });
    }


    @Override
    public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        //progress.setVisibility(View.VISIBLE);
        Log.e("ReLoginFragment","Page started to load=>"+url);
       // if (!url.contains(loginLink)){
          //  view.loadUrl(link);
             //new ExtractData(false);
            //Log.e("ReLoginFragment","Login Successful");
//            Intent intent=new Intent(context,Home.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.putExtra("URL",url);
//            intent.putExtra("STORE",false);
//            context.startActivity(intent);
       //   }



    }

    @Override
    public void onPageFinished(final WebView view, final String url) {
        super.onPageFinished(view, url);
        Log.e("ReLoginFragment","Inside page finished  =>"+url+" count=>"+count);
        if(url.contains("login")){
            if (count<=5) {
                String autologin =
                        "document.LoginForm.txtUserName.value='" + uid + "';\n" +
                                "    document.LoginForm.txtuserpass.value='" + pwd + "';\n" +
                                "  try{\n" +
                                "     document.LoginForm.txt_captcha.value='" + "" + "';\n" +
                                "   } catch(err){\n" +

                                "   }" +
                                "var frms=document.getElementsByName('txtsubmit');" +
                                "frms[0].click();";

                view.evaluateJavascript("javascript:(function(){ " +
                        autologin +
                        "})();" +
                        "", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        // view.loadUrl(link);

                    }

                });
            count++;
            }else{
                  view.stopLoading();
                 progress.setVisibility(View.GONE);
                  errorScreen.setVisibility(View.VISIBLE);
                  messageTx.setText("Failed to access your account. Press try to refresh the page or try to Login again.");
                  loginBt.setVisibility(View.VISIBLE);

            }
        }else{
            //progress.setVisibility(View.GONE);
            loadMainView(view);
        }

    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        view.loadUrl(loginLink);

        return super.shouldOverrideUrlLoading(view, request);
    }
    public   class ExtractData {
        WebView webView;
        String url;
        NavigationView navigationView;
        boolean isStore;
        void run(){
            loadUrl(url);
        }

        ExtractData(String url,boolean isStore){
            webView=getActivity().findViewById(R.id.webview);
            this.url=url;
            this.navigationView=(NavigationView)getActivity(). findViewById(R.id.nav_view);
            this.isStore=isStore;
            webView.setWebViewClient(new web(
                    (WebView) (getActivity().findViewById(R.id.webview)),url,getActivity().getApplicationContext(),navigationView,isStore ,(DrawerLayout)getActivity(). findViewById(R.id.drawer_layout),getActivity().getSupportFragmentManager(),
                    (View )getActivity().findViewById(R.id.fragment),
                    (View)getActivity(). findViewById(R.id.fragmentExtra),true,getActivity(). findViewById(R.id.searchLinear)));
            loadUrl(url);
        }
        ExtractData(boolean isStore){//student data
            this.navigationView=(NavigationView)getActivity(). findViewById(R.id.nav_view);
            this.isStore=isStore;
            this.url="http://mit.thecollegeerp.com/academic/studentarea/myprofile.php";
            webView=getActivity().findViewById(R.id.webview);
            Log.e("ExtractDataThread","Inside constructor "+url);

            webView.setWebViewClient(new web(
                    (WebView) (webView),this.url,getActivity().getApplicationContext(),navigationView,isStore ,(DrawerLayout)getActivity(). findViewById(R.id.drawer_layout),getActivity().getSupportFragmentManager(),
                    (View )getActivity().findViewById(R.id.fragment),
                    (View)getActivity(). findViewById(R.id.fragmentExtra),getActivity(). findViewById(R.id.searchLinear)));
            loadUrl("http://mit.thecollegeerp.com/academic/studentarea/link.php");
            run();
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

    private FragmentActivity getActivity() {
         return activity;
    }

}