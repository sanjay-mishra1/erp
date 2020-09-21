package com.example.sanjay.erp;

import android.util.Log;

import java.util.Stack;

public class Constants {
    public static String uname = "";
    public static String uid = "";
    public static String type = "";
    public static int fields = 0;
    public static boolean navEmpty = true;
    public static boolean isTitleLoaded = false;
    public static char isUserBlocked = 'n';

    public static java.util.Stack<String> Stack = new Stack<String>();


  public static String formatUserName(String name) {

      Log.e("UserName","inside formatUserName <"+name+">");

      String[] words;
        if (!name.isEmpty()) {
            words = name.split("\\s");
            StringBuilder nameBuilder = new StringBuilder();
            for (String w : words) {
                Log.e("UserName","inside formatUserName loop <"+nameBuilder+">");
              try {
                  nameBuilder.append(String.valueOf(w.charAt(0)).toUpperCase()).append(w.substring(1).toLowerCase()).append(" ");
              }catch (Exception e){e.printStackTrace();}
            }
            name = nameBuilder.toString();

        }
        return name;
    }

}