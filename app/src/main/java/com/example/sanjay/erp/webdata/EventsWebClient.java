package com.example.sanjay.erp.webdata;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
import android.widget.TextView;



public class EventsWebClient extends WebViewClient {
    private final DownloadManager downloadManager;
    private   Context context;
    private RelativeLayout progressBar;
    private TextView textView;
    private boolean isName=true;
    public EventsWebClient(RelativeLayout progressBar, TextView toolbar,DownloadManager downloadManager,Context context){
         this.progressBar=progressBar;
        this.textView=toolbar;
        this.context=context;
        this.downloadManager=downloadManager;
    }
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);

    }
    void downloadInitial(String url,String name){
        if (requestPermission((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE, 101)) {
             download(url,name);
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);

        }
        return hasPermission;
    }

     public void download(String url, String name) {
        Uri Download_Uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle(name);
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription("Downloading...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);
          //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);


        //Enqueue a new download and same the referenceId
        downloadManager.enqueue(request);
    }
    private String name(String link){
        if(link!=null){
            if (!link.isEmpty()){
                link=link.substring(0,link.indexOf("?"));
                link=link.substring(link.lastIndexOf("%2F")+3);
                return link;
            }
        }
        return "";
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.e("EventsWeb","Link started =>"+url);


        if (url.contains("alt=media")&&url.contains("firebasestorage")){
            Log.e("EventsWeb","Downloading link");

            downloadInitial(url,name(url));
            view.stopLoading();
            isName=false;
        }else{Log.e("EventsWeb","normal link");
            progressBar.setVisibility(View.VISIBLE);
            isName=true;
           // view.loadUrl(url);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
      if (isName)
          textView.setText(view.getTitle());
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }
}
