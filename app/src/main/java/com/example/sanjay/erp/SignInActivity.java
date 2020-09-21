package com.example.sanjay.erp;
/*String autologin = "javascript:{" +
                    "document.getElementsByTagName(\"input\")[0].value='" + uname + "';" +
                    "document.getElementsByTagName(\"input\")[1].value='" + pass + "';" +
                    "var frms=document.getElementsByName('txtsubmit');" +
                    "frms[0].click();}";
            */
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;



public class SignInActivity extends Activity implements View.OnClickListener {

    TextInputEditText editTextEmail, editTextPassword;
    RelativeLayout progressBar;
    ProgressBar progressBar1;
    String uname, pass;
    SharedPreferences shared;
    WebView webView;
    int count = 1;
    String captcha = "";
    private boolean isEnterCred = false;
    private boolean isPageFinished = false;
    public final String MYPREFERENCES = "USERCREDENTIALS";
    private String url;
    boolean isStop = false;
    boolean isStaffLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_edited);
        Glide.with(getApplicationContext()).load(getResources().getDrawable(R.drawable.retry)).into((ImageButton) findViewById(R.id.retryCapButton));
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        progressBar = findViewById(R.id.progressbar);
        progressBar1 = findViewById(R.id.progress);
        webView = findViewById(R.id.webview);
        Intent intent = getIntent();
        TextView textView = findViewById(R.id.Login);
        ImageView imageView = findViewById(R.id.icon);
        shared = this.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);

        findViewById(R.id.buttonLogin).setOnClickListener(this);
        url = intent.getStringExtra("URL");
        if (url.contains("http://mit.thecollegeerp.com/academic/stlogin.php")) {
            Constants.type = "STUDENT";
            Glide.with(getApplicationContext()).load(R.drawable.studentnew).into(imageView);
            textView.setText(R.string.studentlogin);
            isStaffLoaded=true;
        } else {
            findViewById(R.id.three).setVisibility(View.VISIBLE);
            Constants.type = "STAFF";
            textView.setText(R.string.stafflogin);
            loadUrl(url);
        }


    }

    public int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    void typeLogin() {
        final String email = editTextEmail.getText().toString().trim();
        if (email.isEmpty()) {
            expand(progressBar, 300, dpToPx(124));
            editTextEmail.setError("Please enter a valid collegeid");
            editTextEmail.requestFocus();

        }

        userLogin(email.trim());


    }

    private void userLogin(String email) {
        final String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError("ID required");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            expand(progressBar, 300, dpToPx(124));
            editTextPassword.requestFocus();
            return;
        }
        if (Constants.type.equals("STAFF")) {
            EditText captcha = findViewById(R.id.capchaEdit);
            this.captcha = captcha.getText().toString().trim();
            if (this.captcha.isEmpty()) {
                captcha.setError("Captcha required");
                expand(progressBar, 300, dpToPx(124));
                captcha.requestFocus();
                return;
            }

        }


        collapse(progressBar, 300);
        uname = email;
        pass = password;
        Log.e("UserName", "Login u-" + uname + pass + " isFinished=>" + isPageFinished + " isCred=>" + isEnterCred + " isStop=>" + isStop);
        String loginLink=url;
        loginLink = this.url + "?txtUserName=" + uname + "&txtuserpass=" + pass + "&txt_captcha=" + captcha + "&txtsubmit=";
        loadUrl(loginLink);


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.buttonLogin:
                typeLogin();
                break;
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    void loadUrl(String url) {


        webView.setWebViewClient(new Browse(url));
        if (Build.VERSION.SDK_INT > 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        } else {
            CookieManager.getInstance().setAcceptCookie(true);
        }
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.setScrollBarStyle(View.SCROLL_AXIS_NONE);
        }
        webView.loadUrl(url);

    }

    public void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getHeight();
        progressBar1.setVisibility(View.GONE);

        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, dpToPx(124));
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                levitate(progressBar, 12, true, true);

            }
        });
        //findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public void collapse(final View v, int duration) {
        int prevHeight = v.getWidth();
        findViewById(R.id.buttonLogin).setVisibility(View.GONE);
        progressBar1.setVisibility(View.VISIBLE);
        final ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, dpToPx(45));
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().width = (int) animation.getAnimatedValue();
                v.requestLayout();
                animation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        //endAnimation(12, findViewById(R.id.progressbarRelative)  );
                        levitate(progressBar, 12, true, false);
                    }
                });
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();


    }

    void endAnimation(final float Y, final View movableView) {
        /* */
        final long yourDuration = 300;
        final TimeInterpolator yourInterpolator = new DecelerateInterpolator();
        movableView.animate().
                translationYBy(-Y).
                setDuration(yourDuration).
                setInterpolator(yourInterpolator).
                setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        {
                            findViewById(R.id.buttonLogin).setVisibility(View.VISIBLE);
                            // levitate(movableView, -Y, true,false);
                        }
                    }
                });

    }

    public void levitate(final View movableView, final float Y, boolean animated, final boolean end) {
        if (animated) {


            final long yourDuration = 300;
            final TimeInterpolator yourInterpolator = new DecelerateInterpolator();
            movableView.animate().
                    translationYBy(Y).
                    setDuration(yourDuration).
                    setInterpolator(yourInterpolator).
                    setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            if (!end) {
                                levitate(movableView, -Y, true, false);
                            } else {
                                endAnimation(Y, movableView);
                            }
                        }
                    });
        }

    }

    public void retryCapButtonClicked(View view) {
        loadUrl(url);
        view.setVisibility(View.GONE);
        findViewById(R.id.webProgress).setVisibility(View.VISIBLE);
    }

    void showError(StringBuilder message) {
        expand(progressBar, 300, dpToPx(124));

        AlertDialog.Builder alertDialog =
                new AlertDialog.Builder(this);
        alertDialog.setTitle("Login Failed");

        alertDialog.setMessage(message);


        alertDialog.setPositiveButton("OK", null);
        AlertDialog alertDialog1 = alertDialog.create();
        if (!isStop | !isDestroyed() | !isFinishing())
            alertDialog1.show();
    }

    private class Browse extends WebViewClient {
        String url;

        Browse(String url) {
            this.url = url;
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            isStop = true;
            isPageFinished = false;
            view.setVisibility(View.INVISIBLE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.e("LoginThread", "Error received " + error.getDescription());
            }
/*
if (Constants.type.equals("STAFF"))
loadUrl("file:///android_asset/errorStaff.html");
else{
loadUrl("file:///android_asset/errorSt.html");
}
*/
            findViewById(R.id.retryCapButton).setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            this.url = url;
            isStop = false;
            Log.e("URLs", "Page started " + url);
            Intent intent1 = getIntent();
            findViewById(R.id.retryCapButton).setVisibility(View.GONE);
            isPageFinished = false;
            if (!url.contains("login") && !url.contains("error")) {
                webView.stopLoading();
                finish();

                Log.e("Cookie", "1.From login url is" + url + CookieManager.getInstance().getCookie(url));

                Log.e("Cookie", "2.From login url is" + intent1.getStringExtra("URL") + CookieManager.getInstance().getCookie(url));
                SharedPreferences sharedPreferences = getSharedPreferences("USERCREDENTIALS", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("COLLEGEID", uname.toUpperCase());
                editor.putString("PASSWORD", pass);
                Constants.uid = uname.toUpperCase();
                editor.putString("LOGINURL", intent1.getStringExtra("URL"));
                editor.putString("HOMEURL", this.url);
                editor.putString("TYPE", Constants.type);

                webView.getSettings().setAppCacheEnabled(true);

                editor.apply();
                Intent intent = new Intent(SignInActivity.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("URL", url);
                intent.putExtra("STORE", true);
                startActivity(intent);
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (!isStop) {
                view.setVisibility(View.VISIBLE);
                isPageFinished = true;
            }
            Log.e("Page finished URL", url);
            if (!url.contains("error"))
                loadExtraScript(webView);
            if (isStaffLoaded) {




                if (isStop) {
                    count = 1;
                    isStop = false;
                    showError(new StringBuilder("Failed to connect to the server.\nCheck your internet connectivity or try again after sometime "));
                    isEnterCred = true;
                } else if (url.contains("login")) {
                    isEnterCred = false;
                    count = 1;
                    expand(progressBar, 300, dpToPx(124));
                    showError(new StringBuilder("Invalid id or password"));
                }

            }else isStaffLoaded=true;
        }





    private void loadExtraScript(WebView webView) {
        if (Constants.type.equals("STAFF")) {
            webView.evaluateJavascript("javascript:(function(){ " +

                    " document.getElementsByTagName('head')[0].parentNode.removeChild(document.getElementsByTagName('head')[0]);\n" +
                    "   document.getElementsByTagName('span')[0].parentNode.removeChild(document.getElementsByTagName('span')[0]);\n" +
                    "    document.getElementsByTagName('span')[0].parentNode.removeChild(document.getElementsByTagName('span')[0]);\n" +
                    "   document.getElementsByTagName('strong')[0].parentNode.removeChild(document.getElementsByTagName('strong')[0]);\n" +
                    "   var divs=document.getElementsByClassName('form-group');\n" +
                    "   for (var i = 0; i < divs.length-1; i++) {\n" +
                    "      divs[i].style='display:none';\n" +
                    "   }\n" +
                    "   document.getElementsByClassName('footer')[0].style='display:none';\n" +
                    "   document.getElementsByClassName('header')[0].style='display:none';\n" +
                    "   document.getElementById('txt_captcha').style='display:none';\n" +
                    " var ele=document.getElementsByTagName('img')[0];\n" +
                    "         ele.scrollIntoView(this);\n" +
                    "         var viewportH=Math.max(document.documentElement.clientHeight,window.innerHeight||0);\n" +
                    "         window.scrollBy(0,(ele.getBoundingClientRect().height-viewportH)/2);\n  " +
                    "return ('0');})();" +
                    "", new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    //    progressBar.setVisibility(View.GONE);
                    findViewById(R.id.webProgress).setVisibility(View.GONE);
                }

            });
        }
    }



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, request);
        }
    }

}
