package com.example.sanjay.erp.ExtraClasses;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.webFrame.WebFrame;


import java.util.Objects;

public class DashBoardFragment extends Fragment {
    private WebView webView;
    boolean isFirst=true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.erp_dashboard, container, false);
          webView=v.findViewById(R.id.eventsWebview);
        listners(v);
        return v;
    }

    private void listners(View v) {
        loadEvent();
                 Objects.requireNonNull(v).findViewById(R.id.rgpvButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url="http://result.rgpv.ac.in/Result/BErslt.aspx";
                FragmentManager FM = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
                WebFrame web = new WebFrame();
                Bundle bundle=new Bundle();
                bundle.putString("URL", url);

                web.setArguments(bundle);
                fragmentTransaction1.add(R.id.fragment, web).commit();
                Constants.Stack.push("RGPV");
                Constants.isTitleLoaded=true;
                fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);

                fragmentTransaction1.addToBackStack(url);
            }
        });

    }
     void loadEvent(){


        String eventLink="https://mitpp-583fb.firebaseapp.com/events.html";
         Log.e("LoadUrl", "normal URL=> " + eventLink);
         webView.setWebViewClient(new  Browse(eventLink));
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

             if (isFirst){
//                 view.loadUrl(url);
                 isFirst=false;
                 Log.e("on Dash","main event url->"+url);
              }else{Log.e("on Dash","new event url->"+view.getUrl());
                 view.stopLoading();
                 isFirst=true;
                 loadEvent();
                FragmentManager FM = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                 FragmentTransaction fragmentTransaction1 = FM.beginTransaction();
                 WebFrame web = new WebFrame();
                 Bundle bundle=new Bundle();
                 bundle.putString("URL", url);

                 web.setArguments(bundle);
                 fragmentTransaction1.add(R.id.fragment, web).commit();
                 Constants.Stack.push("Event");
                 Constants.isTitleLoaded=true;
                 fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);

                 fragmentTransaction1.addToBackStack(url);

              }
         }

         @Override
         public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
             Log.e("Override",url);
             return super.shouldOverrideUrlLoading(view, request);
         }

         @Override
         public void onPageFinished(WebView view, String url) {
             super.onPageFinished(view, url);
             Log.e("onfinishDash","page finish->"+url);

         }
     }
}
