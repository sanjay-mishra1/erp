package com.example.sanjay.erp.ExtraClasses;

import android.app.MediaRouteButton;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.LoginFrontScreen;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.webFrame.WebFrame;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.sanjay.erp.Constants.fields;
import static com.example.sanjay.erp.Constants.uname;

public class AssignmentFragment extends Fragment //implements Spinner.OnItemSelectedListener
{
    private View progressBar;
    private View errorScreen;
    private String eventLink;
    private WebView webView;
    // private Spinner spinner;
  //  private String[] subCode;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.webfragment, container, false);
       // subCode=new String[15];
        //spinner=v.findViewById(R.id.spinner);
        if (getActivity().findViewById(R.id.appbar).getVisibility()!=View.VISIBLE){
            getActivity().findViewById(R.id.appbar).setVisibility(View.VISIBLE);
        }
        TextView toolbar =getActivity(). findViewById(R.id.title);
        toolbar.setText("Assignment");
         progressBar=v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        errorScreen=v.findViewById(R.id.error_screen);
        v.findViewById(R.id.refreshButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorScreen.setVisibility(View.GONE);
                loadAssignment(v,eventLink);


            }
        });
        v.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
                SharedPreferences sharedPreferences=getContext().getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                fields = 0;
                uname = "";
                Constants.uid="";
                startActivity(new Intent( getContext(),LoginFrontScreen.class));


            }
        });
       eventLink= this.getArguments() != null ? this.getArguments().getString("URL") : "http://mit.thecollegeerp.com/academic/studentarea/assingement.php";
        loadAssignment(v,eventLink );
        return v;
    }
    void loadAssignment(View v,String eventLink){
          webView=v.findViewById(R.id.webviewFrag);

        Log.e("LoadUrl", "normal URL=> " + eventLink);
        webView.setWebViewClient(new Browse(eventLink));
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(eventLink);
    }



    private class Browse extends WebViewClient {
            String url;
            public Browse(String eventLink) {
                url=eventLink;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.e("onstartDash","page started->"+url);
                progressBar.setVisibility(View.VISIBLE);
                if (!url.equals(eventLink)&!url.contains("http://mit.thecollegeerp.com/academic/studentarea/assingement.php")){
                    webView.goBack();
                    FragmentManager FM = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
                    AssignmentFragment web = new AssignmentFragment();
                    Bundle bundle=new Bundle();
                    bundle.putString("URL", url);

                    web.setArguments(bundle);
                    fragmentTransaction1.add(R.id.fragment, web).commit();
                    Constants.Stack.push("Assignment");
                    Constants.isTitleLoaded=true;
                    fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);

                    fragmentTransaction1.addToBackStack(url);
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
            //if (!isCall)
                errorScreen.setVisibility(View.VISIBLE);
        }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.e("Override",url);
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                Log.e("onfinishDash", "page finish->" + url);

                view.evaluateJavascript("javascript:(function(){ " +

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
                                //if (endProgress)
                                {   Log.e("Browse","Hiding progressbar");

                                    progressBar.setVisibility(View.GONE);
                                }//else{
                                  //  Log.e("Browse","Not Hiding progressbar");
                                //}

                            }
                        }, 1700);

                    }
                });
                }

    }

    private void loadSpinner(String[] data) {

        //spinner.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, data));
        //spinner.setOnItemSelectedListener(this);
    }











    /*
    * if (url.contains("download_assignment")) {
                view.evaluateJavascript("javascript:(function(){" +
                        "" +
                        "var option=document.getElementsByTagName('option')" +
                        "var data='';" +
                        "var subname='';" +
                        "for (i=0;i<option.length;i++){" +
                        "   data=data+'~'+ option[i].value;" +
                        "   subname=subname+'~'+option[i].innerText;" +
                        "    }" +
                        " data=data+'`'+subname;" +
                        "return (data);})();", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String s) {
                        try {
                            String data[] = s.split("`");
                            subCode = data[1].split("~");
                            loadSpinner(data[0].split("~"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });*/
}
