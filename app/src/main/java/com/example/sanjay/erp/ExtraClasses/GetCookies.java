package com.example.sanjay.erp.ExtraClasses;

import android.content.Context;
import android.util.Log;
import android.webkit.CookieSyncManager;


import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class GetCookies  {
private CookieManager cookieManager=null;
Context context;
public GetCookies(Context context){
//    cookieManager android.webkit.CookieManager.getInstance();
    this.context=context;
    cookieManager=new CookieManager();
    cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

    CookieHandler.setDefault(cookieManager);
}
    private List<HttpCookie> getCookies(){
    if (cookieManager==null){
        Log.e("Cookies","getcookies manager is null");

        return null;

    }else{
        Log.e("Cookies","getCookies manager is null");

        return  cookieManager.getCookieStore().getCookies();
//        return  cookieManager.get(context.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE).getString("LOGINURL",""),);
    }
    }
    private boolean isCookieManagerEmpty(){
    if (cookieManager==null){
        {        Log.e("Cookies","iscookiemanagerempty cookiemanager is null");

            return true;
        }
    }else return cookieManager.getCookieStore().getCookies().isEmpty();
    }
    public void getCookieValue(){
//    String cookieValue=new String();
    if (isCookieManagerEmpty()){
        for (HttpCookie eachCookie:Objects.requireNonNull(getCookies())){
//            eachCookie.setMaxAge(2592000);
            Log.e("Cookies",eachCookie.getName()+"=>"+eachCookie.getValue()+"<Age>=>"+eachCookie.getMaxAge());
        }
//        return cookieValue;
    }else{
        Log.e("Cookies","getCookieValue cookie not found");
    }
    }
    public void getcookies(){
        android.webkit.CookieManager cookieManager= android.webkit.CookieManager.getInstance();
        String cookies=cookieManager.getCookie(context.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE).getString("LOGINURL",""));
        String [] temp =cookies.split(";");
        HttpCookie cookie;


    }
    public void cookie(){
        android.webkit.CookieManager cookieManager= android.webkit.CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        CookieSyncManager webcookiesync=CookieSyncManager.createInstance(context);
        java.net.CookieStore cookieStore=((java.net.CookieManager)CookieHandler.getDefault()).getCookieStore();
        URI uri=null;
    try {
              uri=new URI(context.getSharedPreferences(MYPREFERENCES,Context.MODE_PRIVATE).getString("LOGINURL",""));
        Log.e("Cookie","1.From getcookie url is"+uri+ android.webkit.CookieManager.getInstance().getCookie(uri.toString()));

    } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        List<HttpCookie> list=cookieStore.get(uri);
        String url=Objects.requireNonNull(uri).toString();

        for (HttpCookie cookie:list){
            Log.e("Cookies",cookie.getName()+"=>"+cookie.getValue()+"<Age>=>"+cookie.getMaxAge());


            String setcookie=new StringBuilder(cookie.toString()).
                    append("; domain=").append(cookie.getDomain()).
                    append("; path=").append(cookie.getPath()).toString();
            cookieManager.setCookie(url,setcookie);
        }
    }
}
