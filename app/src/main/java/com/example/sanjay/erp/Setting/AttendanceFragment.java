package com.example.sanjay.erp.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.Home;
import com.example.sanjay.erp.LoginFrontScreen;
import com.example.sanjay.erp.MainActivity;
import com.example.sanjay.erp.R;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.example.sanjay.erp.Constants.fields;
import static com.example.sanjay.erp.Constants.uname;
import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class AttendanceFragment extends Fragment {
    private RelativeLayout progressBar;
    private WebView webView;
     String name="";
    final boolean[] isFailed = {false};

    public AttendanceFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.webfragment,container,false);
        progressBar=v.findViewById(R.id.progressBar);
        webView=v.findViewById(R.id.webviewFrag);

        Log.e("WebFrame","Inside webframe"+this.getArguments().getString("URL"));
         loadUrl(getArguments().getString("URL"));

        return v;
    }


    @Override
    public void onStart() {
        super.onStart();

    }

    void loadUrl(String url){

        webView.setWebViewClient(new Browse(url));
        //webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);
        if (Build.VERSION.SDK_INT>21){
            webView.getSettings().setAllowFileAccessFromFileURLs(true);
            webView.getSettings().setAllowUniversalAccessFromFileURLs(true);


            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }else{
            CookieManager.getInstance().setAcceptCookie(true);
        }

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);
       // webView.loadDataWithBaseURL("file://assets/styleTable.html",);
       try{
           //loadData();
       }catch(Exception e){e.printStackTrace();}


    }
    void loadData() throws IOException {
        InputStream is=Objects.requireNonNull(getContext()).getAssets().open("editedstyleTable.html");
        int size=is.available();
        byte[] buffer=new byte[size];
        is.read(buffer);
        is.close();
        String str=new String(buffer);
        Log.e("AttendanceData",str);
    }

    @Override
    public void onStop() {
        super.onStop();
         Log.e("Login", "Stopped so stopping webView to load further");
        webView.stopLoading();
    }

    public class Browse extends WebViewClient {
        String url;
        Browse(String url){
            this.url=url;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            view.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.e("Attendance","error received "+error);
            webView.setVisibility(View.GONE);
            isFailed[0]=true;
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished( WebView view, String url) {
            super.onPageFinished(view, url);
            webView.evaluateJavascript("javascript:(function(){ " +

                    "var s=document.getElementsByTagName('a')[0].href;" +
                    "return (s);})();" +
                    "", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    Log.e("Webframe","Checking for login ....value is "+value);
                    if(value.toLowerCase().contains("login")){
                        isFailed[0] =true;
                        webView.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                    }else{
                        Log.e("attendance","Checking for login .... Session not expire ");
                    }

                }
            });
            if (!isFailed[0]) {
                Log.e("WebFrame", "Done Loading " + url);
                webView.evaluateJavascript("javascript:(function(){  var s = document.createElement('style');\n" +
                                "\n" +
                                "  s.innerHTML='body{display:block;min-height: auto;    max-height: fit-content;}'+" +
                                "  'table tr> *{display:block;}table tr{display:table-cell;}'+\n" +
                                "  '.table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td {padding: 8px;width: auto;'+\n" +
                                "  '  line-height: 1.428571429;'+\n" +
                                " '   vertical-align: top;'+\n" +
                                "'    border-top: 1px solid #ddd;'\n" +
                                "'}';\n" +
                                "  \n" +
                                "  document.getElementsByTagName('head')[0].appendChild(s);\n" +
                                "  \n" +
                                "\n" +
                                " var tbl = document.getElementsByTagName('body')[0];\n" +
                                "  tr = tbl.getElementsByTagName('tr');\n" +
                                " \n" +
                                "  var td = document.createElement('td');\n" +
                                "  var input = document.createElement('h2');\n" +
                                "   td.appendChild(input);\n" +
                                "  tr[1].appendChild(td);\n" +
                                "  td=   tr[2].getElementsByTagName('td');\n" +
                                "  var per=td[td.length-1].innerHTML;\n" +
                                "  document.getElementsByTagName('table')[1].style='display:none';\n" +
                                "  document.getElementsByTagName('header')[0].style='display:none';\n" +
                                " \n" +
                                "  document.getElementsByTagName('section')[2].style='display:none';\n" +
                                "  document.getElementsByTagName('section')[3].style='display:none';\n" +
                                "\n" +
                                "   td=document.getElementsByTagName('strong');\n" +
                                "tr[1].getElementsByTagName(\"td\")[0].innerHTML='<strong>Lectures</strong>';" +
                                "  for (var i = 0; i < td.length; i++) {\n" +
                                "   if(td[i].innerHTML.toLowerCase().includes('practical'))\n" +
                                "   { td[i].style='display:none';\n" +
                                "    break;}\n" +
                                "  }\n" +
                                "    return per;})();",


                        new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(final String value) {
                                // link[0] = value.split("\\s");
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        String percentage;
                                        try {
                                            percentage = value.substring(value.indexOf(">") + 1, value.indexOf("%") - 1).substring(2).trim();

                                            if (Constants.uid.isEmpty()) {
                                                Constants.uid = Objects.requireNonNull(getContext()).getSharedPreferences(MYPREFERENCES, MODE_PRIVATE).getString("COLLEGEID", null);
                                            }
                                            if (Constants.uid != null) {

                                                FirebaseDatabase.getInstance().getReference("users/" + Constants.uid).child("ATTENDANCE").
                                                        setValue(Double.valueOf(percentage));
                                                TextView textView = Objects.requireNonNull(getActivity()).findViewById(R.id.attendanceEnter);
                                                textView.setText(String.format("%s%%", percentage));

                                            }
                                            webView.setVisibility(View.VISIBLE);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        progressBar.setVisibility(View.GONE);
                                        webView.setVisibility(View.VISIBLE);

                                    }
                                }, 2000);
                            }
                        });

            }

        }

        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            view.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);

            return super.shouldOverrideUrlLoading(view, request);
        }
    }

}
