package com.example.sanjay.erp.webFrame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.ExtraClasses.DashBoardFragment;
import com.example.sanjay.erp.LoginFrontScreen;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.webdata.EventsWebClient;
import com.example.sanjay.erp.webdata.WebRgpv;
import com.example.sanjay.erp.webdata.web;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.example.sanjay.erp.Constants.Stack;
import static com.example.sanjay.erp.Constants.fields;
import static com.example.sanjay.erp.Constants.isTitleLoaded;
import static com.example.sanjay.erp.Constants.uname;
import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class WebFrame extends Fragment {
    private RelativeLayout progressBar;
    private WebView webView;
    private TextView toolbar;
    View errorScreen;
    String url;
     String name="";
    boolean isLoginUrlLoaded=false;
     private boolean isLogedin=false;
    private int count = 0;
    private SharedPreferences sharedPreferences;
    private boolean endProgress=true;
    private boolean isCreateNewFrag=true;
    private boolean isAssignmentLoaded=false;

    public WebFrame(){

}
    View v;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v=inflater.inflate(R.layout.webfragment,container,false);
    progressBar=v.findViewById(R.id.progressBar);
    webView=v.findViewById(R.id.webviewFrag);
    errorScreen=v.findViewById(R.id.error_screen);
      if (getActivity().findViewById(R.id.appbar).getVisibility()!=View.VISIBLE){
          getActivity().findViewById(R.id.appbar).setVisibility(View.VISIBLE);
      }
      try {
          toolbar =getActivity(). findViewById(R.id.title);
      }catch (Exception ignored){}
        Log.e("WebFrame","Inside webframe"+this.getArguments().getString("URL"));
        url=this.getArguments().getString("URL");
        sharedPreferences=Objects.requireNonNull(getContext()).getSharedPreferences(MYPREFERENCES,MODE_PRIVATE);


        loadUrl(url);
        v.findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorScreen.setVisibility(View.GONE);
                 loadUrl(url);
                 if (Constants.navEmpty){
                     Log.e("Webframe","nav is empty request generated");
                     new ExtractData(false).run();
                 }else{
                     Log.e("Webframe","nav is not empty");
                 }

            }
        });
        v.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                 fields = 0;
                uname = "";
                Constants.uid="";
                 startActivity(new Intent( getContext(),LoginFrontScreen.class));


            }
        });
    return v;
    }

    /*@Override
    public void onStart() {
        super.onStart();
        Log.e("WebFrame","Onstart name is "+name);
      if (!name.isEmpty())
          headerName(name,"");
    }*/

    @SuppressLint("SetJavaScriptEnabled")
    void loadUrl(String url){
         progressBar.setVisibility(View.VISIBLE);
         Log.e("loadUrl","URL=> "+url);
try {
    if (url.contains("rgpv")){
        Log.e("webframe","url contains rgpv "+url);
        webView.setWebViewClient(new WebRgpv(Objects.requireNonNull(getContext()).
                getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("UNIVID",""),progressBar, toolbar));


        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

    }
    else if(url.contains("login")){
        Log.e("webframe", "url contains login =>" + url);
        if (!isLogedin) {
            headerName("Home", "Home");

            url = sharedPreferences.getString("LOGINURL", url.trim());
            url = url.replace("\"", "");
            url=url+ "?txtUserName=" + sharedPreferences.getString("COLLEGEID", "") + "&txtuserpass=" + sharedPreferences.getString("PASSWORD", "") + "&txt_captcha=" + "&txtsubmit=";
            Log.e("webframe", "login started =>" + url);
            String autologinNew="javascript:{"+
                    "window.location.href='"+url+"' "+
                    "}";


            webView.setWebViewClient(new ReloginFragment(sharedPreferences.getString("COLLEGEID", ""),
                    sharedPreferences.getString("PASSWORD", ""), progressBar, this.url, url,
                    errorScreen, (Button) v.findViewById(R.id.login), (TextView) v.findViewById(R.id.errorMessage), getContext()));

            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.loadUrl(url);
        }else{
//            progressBar.setVisibility(View.GONE);
        }
    }else if (url.contains("showMedia.html")){
        Log.e("LoadUrl", " URL contains media=> " + url);
        webView.setWebViewClient(new EventsWebClient(progressBar, toolbar,(DownloadManager) Objects.requireNonNull(getContext()). getSystemService(Context.DOWNLOAD_SERVICE),getContext()));

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(url);

    }

    else
    {   Log.e("LoadUrl", "normal URL=> " + url);

         webView.setWebViewClient(new Browse(url));
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        //webView.loadDataWithBaseURL("","file:///android_asset/test.html","text/html","UTF-8","");
        webView.loadUrl(url);

    }
}catch (Exception e){
    e.printStackTrace();
    errorScreen.setVisibility(View.VISIBLE);

}

        if (Build.VERSION.SDK_INT>21){
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView,true);
        }else{
            CookieManager.getInstance().setAcceptCookie(true);
        }


          Log.e("webframe","Inside loadurl url is =>"+url);





    }

     void loadFragment(String url, boolean isReplace){
        Bundle bundle = new Bundle();
        bundle.putString("URL", url);
        WebFrame web = new WebFrame();
        web.setArguments(bundle);
      try {
          FragmentManager FM = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

          FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
          Log.e("webframe", "Starting new fragment");
          if (isReplace) {
              fragmentTransaction1.replace(R.id.fragment, web).commit();
          } else {
              fragmentTransaction1.add(R.id.fragment, web).commit();
              fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
              fragmentTransaction1.addToBackStack(url);
          }
      }catch (Exception e){
          e.printStackTrace();
      }
    }
    void setTitle(String title ){
        boolean isPush=true;
        title=title.trim();
        for (Object e:Stack){
            if (title.contains(e.toString().trim())){
                isPush=false;
                break;
            }
        }
        if (!Constants.isTitleLoaded) {
            if (isPush&&!title.contains("webpage")&!title.contains("Bus Tracker")&&!title.isEmpty()&&!title.contains("Link")) {
                if (title.contains("MALWA")) {
                    Constants.Stack.push("ERP");
                    Log.e("Home", "  contains MALWA title is   pushed=>" + title + " stack is =>" + Constants.Stack);
                } else {
                    Constants.Stack.push(title);
                    Log.e("Home", " not contains MALWA title is   pushed=>" + title + " stack is =>" + Constants.Stack);
                }
            }
        }else{
            Log.e("Home","title is not pushed=>"+title+" stack is =>"+Constants.Stack);
        }
        isTitleLoaded=false;
    }
    String headerName(String Default,String link){
       if (isVisible()) {
           String name = "";

       SharedPreferences    sharedPreferences =getContext().getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
           try {
               if (link.equalsIgnoreCase(sharedPreferences.getString("HOMEURL","")) || Default.toLowerCase().trim().contains("link"))
                   name = "Home";
               else if (link.equalsIgnoreCase(sharedPreferences.getString("LOGINURL",""))) {
//                   Intent intent = new Intent(getContext(), MainActivity.class);
//
//                   intent.putExtra("COLLEGEID", sharedPreferences.getString("COLLEGEID", null));
//                   intent.putExtra("LOGINURL",sharedPreferences.getString("LOGINURL",""));
//                   intent.putExtra("PASSWORD", sharedPreferences.getString("PASSWORD", null));
//                   getActivity().finish();
//                   startActivity(intent);
               } else
                   name = link.substring(link.indexOf("studentarea/") + 12, link.indexOf(".php"));
           } catch (Exception e) {
               name = Default;
           }
           if (name.contains("_")) {
               name = name.replace("_", " ");
           }
           if (name.equalsIgnoreCase("ratefaculty")) {
                getActivity().getSupportFragmentManager().popBackStack();
           }

           try {
               StringBuilder key = new StringBuilder((name.trim()));
               if (key.length() > 0) {
                   String[] words = key.toString().split("\\s");
                   key = new StringBuilder();
                   for (String w : words) {
                       key.append(String.valueOf(w.charAt(0)).toUpperCase()).append(w.substring(1).toLowerCase()).append(" ");
                       // Log.e("Searching","Data Original <"+w+"> modified <"+key+">");
                   }
               }

               name = key.toString();
               if (name.toLowerCase().contains("assingement")) {
                   name = name.replace("Assingement", "Assignments");
               }
               if (name.toLowerCase().contains("formates")) {
                   name = name.replace("Formates", "Formats");
               }
               this.name = name;
               toolbar.setText(name);
               //textView.setText(name);
           } catch (Exception ignored) {
           }
       }
       return name;
    }




    public class Browse extends WebViewClient {
        String url;
        boolean isCall=false;
        Browse(String url){
            this.url=url;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e("Onstart","url=>"+url);



            if (url.contains("tel:+")) {
                    call(url);
                    view.stopLoading();
                    isCall = true;



                }
                else if (!WebFrame.this.url.equals(url) && !url.contains("http://mit.thecollegeerp.com/academic/studentarea/changepass.php?mode=change")) {
                    Log.e("webframe", "Creating new fragment");
                    WebFrame.this.url = url;
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        if (isCreateNewFrag) {
                            webView.stopLoading();
                            loadFragment(url, false);
                        } else {
                            loadFragment(url, true);
                            isCreateNewFrag = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorScreen.setVisibility(View.VISIBLE);
                    }
                    isCall = false;

                    headerName(view.getTitle(), url);
                } else {
                    if (!isCreateNewFrag) {
                        loadFragment(url, true);
                        isCreateNewFrag = true;
                    }
                    WebFrame.this.url = url;
                    isCall = false;
                    progressBar.setVisibility(View.VISIBLE);
                    headerName(view.getTitle(), url);
                }
            }

        //deprecated
        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            Log.e("Error",description);
            if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
                errorScreen.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
             if (!isCall)
            errorScreen.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            super.onPageFinished(view, url);



            if (!isCall) {

                Log.e("Home", "Title is=>" + view.getTitle());
                setTitle(headerName(view.getTitle(), url));

                Log.e("WebFrame", "Done Loading " + url);
                webView.evaluateJavascript("javascript:(function(){ " +

                        "var s=document.getElementsByTagName('a')[0].href;" +
                        "return (s);})();" +
                        "", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Log.e("Webframe", "Checking for login ....value is " + value);
                        if (value.toLowerCase().contains("login")) {
                            endProgress = false;
                            Log.e("Webframe", "Checking for login .... Session expire relogin started");
                            loadUrl(value);
                        } else if (value.contains("mitindore")) {
                            Objects.requireNonNull(getActivity()).finish();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.clear();
                            editor.apply();
                            fields = 0;
                            uname = "";
                            Constants.uid = "";
                            startActivity(new Intent(getContext(), LoginFrontScreen.class));
                        } else {

                            Log.e("Webframe", "Checking for login .... Session not expire ");
                        }

                    }
                });
                webView.evaluateJavascript("javascript:(function(){ " +

                        "document.getElementsByTagName(\"header\")[0].style.display='none';" +
                        "document.getElementsByTagName(\"section\")[1].style.display='none';" +
                        "return ('0');})();" +
                        "", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (endProgress)
                                {   Log.e("Browse","Hiding progressbar");
                                  try {
                                      if (url.equals("http://mit.thecollegeerp.com/academic/studentarea/link.php")|url.equalsIgnoreCase("http://mit.thecollegeerp.com/academic/studentarea/myprofile.php#"))
                                      {   FragmentManager FM = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                                          FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
                                          DashBoardFragment dashBoardFragment = new DashBoardFragment();
                                          fragmentTransaction1.add(R.id.fragment, dashBoardFragment).commit();
                                      }
                                  }catch (Exception ignored){}
                                    progressBar.setVisibility(View.GONE);
                                }else{
                                    Log.e("Browse","Not Hiding progressbar");
                                }

                            }
                        }, 1700);

                    }
                });
                if (url.contains("link"))
                    loadExtraScript();
            }else{
                Log.e("iscall","it is true");

            }
        }


        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.e("Error","HTTP ERROR RECEIVED=>"+errorResponse.getReasonPhrase()+"\n"+request.getMethod());
            }
        }

        private void loadExtraScript() {
        Log.e("WebFrame","Inside loadextrascript ");
        String link="http://result.rgpv.ac.in/Result/BErslt.aspx";

             webView.evaluateJavascript("javascript:(function(){ " +
                    // "var s='<a href=\'"+link+"\'><strong><font>RGPV Exam Result</font></strong></a>';" +
                   // "var rgpvbutton=window.location.href = \""+link+"\";" +
                  //   "document.getElementsByClassName('LeftTable_Td_alter5')[0].innerHTML='" +
                    //"<a href=\""+link+"\">RGPV Exam Result</a>';

                     "document.getElementsByClassName('LeftTable_Td_alter5')[0].innerHTML=     " +
                     "      \"<form action='"+link+"' method='post' name='myformcustom'><input  value='RGPV Result' type='submit' style='background:none; border:none; color:#3c8dbc;' /><form> \";" +
                     "" +
                     "var obj=document.getElementById('NewsWindow').content;" +
                    "  var html_string= \"content\";" +
                    //"  document.getElementById('NewsWindow').src = 'https://mitpp-583fb.firebaseapp.com/events.html'; "+
                    "return ('0');})();" +
                    "", null);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.e("Overrideloading","url=>"+view.getUrl());
/*
            if(url.equals("http://mit.thecollegeerp.com/academic/studentarea/assingement.php")) {
                if (isAssignmentLoaded) {

                    Log.e("Assignment","stopped");
                    return false;

                } else isAssignmentLoaded = true;
            }*/

            if (view.getUrl().contains("tel:+")){
                Log.e("call","going inside call=>"+view.getUrl());
                return false;
            }else if    (view.getUrl().equals("http://result.rgpv.ac.in/Result/BErslt.aspx")){
                Log.e  ("Override","Link rgpv stopped loading");
                return false;
            }else
            {
                //view.loadUrl(url);
                return true;
            }

        }
    }
     private void call(String url) {
        if (isHaveCallPermision()) {


            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(url));
            startActivity(callIntent);
        }else{
            Toast.makeText(getContext(),"Not have call permission",Toast.LENGTH_SHORT).show();
        }
    }
    public  boolean isHaveCallPermision() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        1);
                return false;
            }else{

                return true;
            }
        }else{
            return true;
        }

        }

    public   class ExtractData extends Thread{
        WebView webView;
        String url;
        NavigationView navigationView;
        boolean isStore;
        FragmentActivity fragmentActivity=getActivity();
        public void run(){
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
                    (View)getActivity(). findViewById(R.id.fragmentExtra),true,fragmentActivity. findViewById(R.id.searchLinear)));
            loadUrl(url);
        }
        ExtractData(boolean isStore){//student data
            this.navigationView=(NavigationView)fragmentActivity. findViewById(R.id.nav_view);
            this.isStore=isStore;
            this.url="http://mit.thecollegeerp.com/academic/studentarea/myprofile.php";
            webView=fragmentActivity.findViewById(R.id.webview);
            Log.e("ExtractDataThread","Inside constructor "+url);

            webView.setWebViewClient(new web(
                    (WebView) (fragmentActivity.findViewById(R.id.webview)),url,fragmentActivity.getApplicationContext(),navigationView,isStore ,(DrawerLayout)fragmentActivity. findViewById(R.id.drawer_layout),fragmentActivity.getSupportFragmentManager(),
                    (View )fragmentActivity.findViewById(R.id.fragment),
                    (View)fragmentActivity. findViewById(R.id.fragmentExtra),(View)fragmentActivity. findViewById(R.id.searchLinear)));
//            run();
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

    public class ReloginFragment extends WebViewClient {
        private TextView messageTx;
        private Button loginBt;
        private View errorScreen;
        private String uid;
        private String pwd;
        private RelativeLayout progress;
        private String link;
        private String loginLink;
        private Context context;
        boolean isFinished=false;
        boolean isEnterCred=false;
        ReloginFragment(String uid, String pass, RelativeLayout progress, String loginLink, String url, View errorScreen, Button loginBt, TextView messageTx, Context context) {
            this.uid = uid;
            this.progress = progress;
            this.pwd = pass;
            this.link = url;
            this.context = context;
            this.loginLink = loginLink;
            this.errorScreen = errorScreen;
            this.loginBt = loginBt;
            this.messageTx = messageTx;
         }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            errorScreen.setVisibility(View.VISIBLE);
        }


        @Override
        public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.e("ReLoginFragment", "Page started to load=>" + url);
            if (!url.contains("login")&!url.contains("javascript")){
                Log.e("Relogin","url not contains login=>"+url);
                view.stopLoading();
                isEnterCred=false;
                loadUrl(url);
                isLogedin=true;
                isLoginUrlLoaded=true;
                isCreateNewFrag=false;
                try {
                    new ExtractData(false).run();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                isEnterCred=true;
                Log.e("Relogin","url   contains login=>"+url);
            }

        }

        @Override
        public void onPageFinished(final WebView view, final String url) {
            super.onPageFinished(view, url);
            Log.e("ReLoginFragment", "Inside page finished  =>" + url + " count=>" + count);
//            if (url.contains("login")) {
//                if (count <= 2) {
//                     Log.e("AutoLogin","Count=>"+count);
//
//                    String autologin = "javascript:{" +
//                            "document.getElementsByTagName(\"input\")[0].value='" + uid + "';" +
//                            "document.getElementsByTagName(\"input\")[1].value='" + pwd + "';" +
//
//                            "var frms=document.getElementsByName('txtsubmit');" +
//                            "frms[0].click();}";
//                    view.loadUrl(autologin);
//
//                    count++;
//                } else {
//                    view.stopLoading();
//                    progress.setVisibility(View.GONE);
//                    errorScreen.setVisibility(View.VISIBLE);
//                    messageTx.setText("Failed to access your account. Press try to refresh the page or Login again.");
//                    loginBt.setVisibility(View.VISIBLE);
//
//                }
//            }

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            if (!isLoginUrlLoaded)
                view.loadUrl(loginLink);

            return true;//super.shouldOverrideUrlLoading(view, request);
        }

     }
}
