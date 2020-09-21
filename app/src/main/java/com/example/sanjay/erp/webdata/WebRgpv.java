package com.example.sanjay.erp.webdata;

 import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
 import android.widget.RelativeLayout;
import android.widget.TextView;



public class WebRgpv extends WebViewClient {
    private String univid;
    private RelativeLayout progressBar;
    private TextView textView;
    public WebRgpv(String univid, RelativeLayout progressBar, TextView toolbar){
        this.univid=univid;
        this.progressBar=progressBar;
        this.textView=toolbar;
    }
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        Log.e("RGPV","Link started =>"+url);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        textView.setText(view.getTitle());
        android.os.Handler handler=new android.os.Handler();

        view.evaluateJavascript(""+
                "javascript:(function(){ " +
                        "document.getElementsByClassName('header-wrapper')[0].parentNode.removeChild(document.getElementsByClassName('header-wrapper')[0]);" +
                        "document.getElementsByClassName('footer-copyright')[0].parentNode.removeChild(document.getElementsByClassName('footer-copyright')[0]);" +
                        "document.getElementsByClassName('FieldText')[0].parentNode.removeChild(document.getElementsByClassName('FieldText')[0]);" +
                         "document.getElementsByTagName('td')[1].parentNode.removeChild(document.getElementsByTagName('td')[1]);" +
                        "document.aspnetForm.ctl00$ContentPlaceHolder1$txtrollno.value='"+univid+"';\n" +
                "    " +
                        "})();" ,null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);

            }
        },2000);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
        return super.shouldOverrideUrlLoading(view, request);
    }
}
