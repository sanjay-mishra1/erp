package com.example.sanjay.erp.webdata;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.database.store;
import com.example.sanjay.erp.webFrame.UIDetector;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;
import static com.example.sanjay.erp.Constants.fields;
import static com.example.sanjay.erp.Constants.uname;

public class web extends WebViewClient {
    private View extraFrag;
    private View mainFrag;
    private WebView webView;
    private String url;
    private Context context;
    private NavigationView navigationView;
     private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager;
    private boolean isStaff=false;
    private View searchView;

    public web(WebView webView, String url, Context context, NavigationView navigationView, boolean store, DrawerLayout drawerLayout, FragmentManager fragmentManager, View mainFrag, View extraFrag, boolean staff,View searchView) {
        this.webView = webView;
        this.url = url;
        this.mainFrag=mainFrag;
        this.extraFrag=extraFrag;
        this.fragmentManager=fragmentManager;
        this.drawerLayout=drawerLayout;
        this.navigationView=navigationView;
        this.context=context;
         this.searchView=searchView;
        this.isStaff=staff;
    }
    public web(WebView webView, String url, Context context, NavigationView navigationView, boolean store, DrawerLayout drawerLayout, FragmentManager fragmentManager, View mainFrag, View extraFrag,View searchView) {
        this.webView = webView;
        this.searchView=searchView;
        this.url = url;
        this.mainFrag=mainFrag;
        this.extraFrag=extraFrag;
        this.fragmentManager=fragmentManager;
        this.drawerLayout=drawerLayout;
        this.navigationView=navigationView;
        this.context=context;
      //  this.isStore=store;
       // this.isStaff=staff;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

        return super.shouldOverrideUrlLoading(view, request);
    }
/*"var data=document.getElementsByTagName(\"span\")[4].innerHTML;" +

                "data+=document.getElementsByTagName(\"img\")[1].src;"*/

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.e("ScreenUI","Page started to load "+url);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.e("ScreenUI","Page received error to load "+url);

    }

    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
        super.onReceivedHttpError(view, request, errorResponse);
        Log.e("ScreenUI","Page received Http error to load "+url);
/*
if(count<=3){
if(isStaff){

}else{

}
}
*/
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        final String[] img = {""};
        webView.evaluateJavascript("javascript:(function(){ " +
                 "var oTable=document.getElementsByTagName('table')[0];\n" +
                "  var oCells = oTable.rows.item(0).cells;\n" +
                "  var  data=[document.getElementsByTagName('td')[9].innerHTML];  \n" +
                "    data+='`'+document.getElementsByTagName('td')[15].innerHTML;  \n" +

                "    data+='`'+document.getElementsByName('txtstudentname')[0].value;\n" +
                "    data+='`'+document.getElementsByName('txtdob')[0].value;\n" +
                "    data+='`'+document.getElementsByTagName('img')[3].src;  "+
                "return (data);})();" +
                "" +
                "" +
                "", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        try {
                            Log.e("UserName", "Inside Loop name is " + value);
                            String data[]=value.split("`");
                             img[0] = data[4];
                             View header = navigationView.getHeaderView(0);

                              if (!img[0].toLowerCase().contains("no_pic")) {
                                 Glide.with(context).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(img[0]).
                                        into((ImageView) header.findViewById(R.id.userimage));
                               }else img[0]="";

                            uname =  Constants.formatUserName(data[2]).trim();
                            setUserName(navigationView, img[0],data);
                        } catch (Exception e) {
                            Log.e("UserName", e.getMessage());
                            e.printStackTrace();
                        }
                        nav_creator();

                    }
                }
            );
}

    private void nav_creator(){
    final ArrayList<String> linkArray = new ArrayList<>();

    webView.evaluateJavascript("javascript:(function(){ " +

                    "var Sub_Parts= document.getElementsByClassName('sidebar-menu')[0].getElementsByTagName('a');" +
                    "var data;var link;" +
                    "for (var i = 0; i < Sub_Parts.length; i++) {" +

                    "link+=' '+Sub_Parts[i].href;" +
                    "}" +
                    "return (link);})();" +
                    "",


            new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // link[0] = value.split("\\s");
                    String[] links = value.split("\\s");
                    linkArray.addAll(Arrays.asList(links));

                }
            });

    {
        webView.evaluateJavascript("javascript:(function(){var Sub_Parts= document.getElementsByClassName('sidebar-menu')[0].getElementsByTagName('a');" +
                        "var data='';var link;" +
                        "for (var i = 0; i < Sub_Parts.length; i++) {" +
                        " data+='*'+Sub_Parts[i].textContent;" +

                        "}" +
                        "return (data);})();" +
                        "",


                new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value1) {
                        try {
                            // key[0] = new StringBuilder((value.trim()));

                            String[] name = value1.split("\\*");

                            Log.e("ScreenUI", "Names are " + Arrays.toString(name) + "value->" + value1);
                            ArrayList<String> nameArray = new ArrayList<>(Arrays.asList(name));
                            boolean isStaff=false;
                            if (context.getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("TYPE","").equals("STAFF"))
                                isStaff=true;
                            fields=linkArray.size();
                            for (int i = 0; i < nameArray.size(); i++) {
                                if (nameArray.get(i).toLowerCase().contains("dashboard")){
                                    nameArray.add(i+5,"Map");//"https://malwaerp.firebaseapp.com/"
                                    linkArray.add(i+5,"https://mitpp-583fb.firebaseapp.com/");
                                    if (isStaff) {
                                        nameArray.add(i + 5, "Events");
                                        linkArray.add(i + 5, "Events");
                                    }

                                }
                                Log.e("Data", i + nameArray.get(i) + "<" + linkArray.get(i) + ">");

                            }
                            Log.e("Data2", "nameArray=" + (nameArray.size()) + "LinkArray=" + linkArray.size());
                        try {
                            Constants.navEmpty = nameArray.isEmpty() || nameArray.size() < 2;


                         }catch (Exception e){
                            e.printStackTrace();
                             Constants.navEmpty=true;
                         }
                            Log.e("navempty","is_nav_loaded"+Constants.navEmpty);
                            UIDetector uiDetector = new UIDetector
                                    (nameArray, linkArray,navigationView, context,drawerLayout,fragmentManager,mainFrag,extraFrag,searchView);
                            uiDetector.CreateView();


                        } catch (Exception e) {
                            e.printStackTrace();
                         }

                    }
                });
    }
}
    private void setUserName(NavigationView navigationView,String img,String extData[]) {
        View header = navigationView.getHeaderView(0);
        TextView textView = header.findViewById(R.id.username);
        Log.e("RunningStates 'web'", "Inside setusername");
        if (!isStaff)
        textView.setText(uname);

        final TextView college = header.findViewById(R.id.collegeid);

        SharedPreferences sharedPreferences = context.getSharedPreferences("USERCREDENTIALS", MODE_PRIVATE);


        college.setText(sharedPreferences.getString("COLLEGEID", ""));
        if (!isStaff) {
            if (uname != null) {
                String data[] = new String[6];
                data[0] = sharedPreferences.getString("COLLEGEID", "");
                data[1] = uname;
                data[2] = sharedPreferences.getString("TYPE", Constants.type);
                data[3] = extData[0].substring(1, extData[0].indexOf(" ")).trim();//Univ id
                if(data[3]!=null){
                    SharedPreferences.Editor editor=sharedPreferences.edit();
                    editor.putString("UNIVID",data[3]);
                    editor.putString("USERNAME",data[1]);
                    editor.apply();
                }
                data[4] = extData[3];//dob
                if (extData[1].contains("II"))
                    data[5] = "2";
                else data[5] = "1";
                Log.e("UserCred", "" + Arrays.asList(data));
                //if (isStore) {
                  //  if (!img.isEmpty()) {
                   //        new store(data, img);
                   // } else {
                          new store(data,context,(ImageView) header.findViewById(R.id.userimage));
                   // }
             //   }
            }
        }else{
            Log.e("Username","isStaff is true "+isStaff);
            new store();
        }
    }


}