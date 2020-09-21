package com.example.sanjay.erp.webFrame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.ExtraClasses.AssignmentFragment;
import com.example.sanjay.erp.ExtraClasses.DashBoardFragment;
import com.example.sanjay.erp.ExtraClasses.EventsActivity;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.bottom_nav;

import java.util.ArrayList;
 import java.util.TreeMap;

import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class UIDetector implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<String> name;
    private ArrayList<String> link;
    private NavigationView navigationView;
    private Menu m;
    private Context context;
    private DrawerLayout drawerLayout;
    private int decrementor = 0;
    private FragmentManager FM;
    View mainFrag;
    View extraFrag;
    View searchView;
     public UIDetector(ArrayList<String> name, ArrayList<String> link, NavigationView navigationView, Context context, DrawerLayout drawerLayout, FragmentManager FM, View mainFrag, View extraFrag, View searchView) {
        this.FM = FM;
        this.name = name;
        this.link = link;
        this.mainFrag = mainFrag;
        this.extraFrag = extraFrag;
        this.navigationView = navigationView;
        this.context = context;
        this.drawerLayout = drawerLayout;
        this.searchView=searchView;
 //            name.add("Dashboard");name.add("ACADEMIA");name.add("Announcement");name.add("Assignment");name.add("Course Content");
//            name.add("Lectures");
//        name.add("Rate Faculty");name.add(" Leave Application");name.add("MST Marks");name.add("Final Evaluation");name.add(" MY LIBRARY  ");name.add("OPAC-Book Search");
//        name.add("OPAC-Issue Details");name.add("Reservation Details");name.add("Book Suggestion");name.add("");name.add("EXTRA FEATURES ");name.add("");name.add(" My Files");
//        name.add("My Profile");name.add("Change Password");name.add(" Time Table");name.add(" MY LECTURE SCHEDULE");name.add("Libray Fine/Fee Detail");name.add(" My Attendance");name.add("My Total Attendance");
//        name.add("Form Forward");name.add("Documents");
//
//
    }

    public void CreateView() {

        m = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
        TreeMap treeMap = loadSubgroup();
        int privious = 0;
        ArrayList arrayList = new ArrayList<>(treeMap.keySet());
        Log.e("Menu", "Size is " + treeMap.size() + " Arraylist " + arrayList.size());
        for (int i = 0; i < treeMap.size(); i++) {
            //get first key

            if (privious == 0) {
                privious = (int) arrayList.get(i);
            }
            try {
                if ((((int) arrayList.get(i) - privious + 1) < 0)) {
                    withoutGroup((String) treeMap.get(arrayList.get(i)), privious + 1);
                }
                makeMenus((String) treeMap.get(arrayList.get(i)),
                        (String) treeMap.get(arrayList.get(i + 1))
                        , i);
            } catch (Exception e) {
                makeMenus((String) treeMap.get(arrayList.get(i)), i);
            }
            privious = (int) arrayList.get(i);
        }


    }

    private void withoutGroup(String group, int index) {
        Log.e("MenuConstruction", "GR->" + group + " nextMenu->" + "empty" + " id->" + index + " counter->" + index + " last->" + name.indexOf(index));
        m.add(1001 + index, index, 0, group).setIcon(loadIcons(name.get(name.indexOf(group) + decrementor)));
    }

    private void makeMenus(String groupmenu, int id) {
        SubMenu menu = m.addSubMenu(100 + id, 0, 0, groupmenu);
        int counter = name.indexOf(groupmenu) + 1;
        int last = name.size() - name.indexOf(groupmenu);
        Log.e("MenuConstruction", "GR->" + groupmenu + " nextMenu->" + "empty" + " id->" + id + " counter->" + counter + " last->" + last);
        if (last <= 1) {

            m.add(counter, counter, 0, name.get(counter + decrementor)).setIcon(loadIcons(name.get(counter + decrementor)));
            decrementor--;
            return;
        }
        for (int i = 0; i < last - 1; i++) {
            counter++;
            try {
                Log.e("Menu->" + groupmenu, name.get(counter + decrementor) + " id->" + counter + decrementor);
                menu.add(100 + id, counter + decrementor, 0, name.get(counter + decrementor).replace("\"", "")).setIcon(loadIcons(name.get(counter + decrementor)));

            } catch (Exception e) {
            }
        }
        /*if(groupmenu.toLowerCase().contains("dashboard")){
            menu.add(100 + id, counter+decrementor, 0, "Map").setIcon(loadIcons(name.get(counter + decrementor)));
        }*/
    }

    private int loadIcons(String name) {
        String word[] = name.split("\\s");
        StringBuilder nameBuilder = new StringBuilder();
        for (String e : word) {
            nameBuilder.append(e);
        }
        name = nameBuilder.toString().replaceAll("[-+.%#$!@*()?/\":;]", "").toLowerCase();
        Log.e("MenuConstruction", "name of Icon ->" + name);

        Resources resources = context.getResources();
        int resourseid;
        try {
            resourseid = resources.getIdentifier(name, "drawable", context.getPackageName());
            Log.e("MenuConstruction", "Found Icon " + resourseid);
        } catch (Exception e) {
            e.printStackTrace();
            resourseid = resources.getIdentifier(name + "issuedetails.xml", "drawable", context.getPackageName());
        }

        return resourseid;
    }

    private void makeMenus(String groupmenu, String nextMenu, int id) {
        int last = name.indexOf(nextMenu) - name.indexOf(groupmenu);
        int counter = name.indexOf(groupmenu) + 1;
        if (last <= 1) {

            m.add(counter, counter, 0, name.get(counter - 1)).setIcon(loadIcons(name.get(counter - 1 + decrementor)));
            decrementor--;
            return;
        }

        SubMenu menu = m.addSubMenu(100 + id, 0, 0, groupmenu);


        Log.e("MenuConstruction", "GR->" + groupmenu + " nextMenu->" + nextMenu + " id->" + id + " counter->" + counter + " last->" + last);
        for (int i = 0; i < last - 1; i++) {
            counter++;
            Log.e("Menu->" + groupmenu, name.get(counter + decrementor) + " id->" + counter + decrementor);
            menu.add(100 + id, counter + decrementor, 0, name.get(counter + decrementor)).setIcon(loadIcons(name.get(counter + decrementor)));
//            menu.setIcon(loadIcons(name.get(counter+decrementor)));
            // menu.getItem(0).setIcon(context.getResources().getDrawable(R.drawable.collegeid));
        }
    }

    void change(String check) {

        Log.e("Checking data", "<" + check + "> index is " + name.indexOf(check));
        try {
            if (check.trim().toLowerCase().contains("dashboard"))
                name.set(name.indexOf(check), "Home");
        } catch (Exception ignored) {
        }
        try {
            if (check.trim().toLowerCase().contains("opac-issue details"))
                name.set(name.indexOf(check), "Issue Details");
        } catch (Exception ignored) {
        }
        try {
            if (check.trim().toLowerCase().contains("opac-book search"))
                name.set(name.indexOf(check), "Book Search");
        } catch (Exception ignored) {
        }

        try {
            if (check.trim().toLowerCase().contains("libray fine/fee detail"))
                name.set(name.indexOf(check), "Library Fine/Fee Detail");
        } catch (Exception ignored) {
        }

    }

    private TreeMap loadSubgroup() {
        int j = name.indexOf("Rate Faculty");
        if (j >= 0) {
            name.remove(j);
            link.remove(j);
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(MYPREFERENCES, Context.MODE_PRIVATE);
        String urllink = sharedPreferences.getString("HOMEURL", "");
        @SuppressLint("UseSparseArrays") TreeMap<Integer, String> subgroup = new TreeMap<>();
        for (int i = 0; i < link.size(); i++) {
            change(name.get(i));

            if (link.get(i).contains("#") || link.get(i).contains(urllink)) {


                subgroup.put(i, name.get(i));

            }
        }
        Log.e("NavigationData", "groups are" + subgroup.toString());
        //{1=  Dashboard , 2=  ACADEMIA  , 11=  MY LIBRARY  , 16=  EXTRA FEATURES
//         subgroup.put(1,"Dashboard");
//         subgroup.put(2,"ACADEMIA");
//         subgroup.put(11,"MY LIBRARY");
//         subgroup.put(16,"EXTRA FEATURES");
        return subgroup;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        try {
            if (mainFrag.getVisibility() != View.VISIBLE) {
                if (searchView.getVisibility()==View.VISIBLE){
                    searchView.setVisibility(View.GONE);
                }
                mainFrag.setVisibility(View.VISIBLE);
                extraFrag.setVisibility(View.GONE);
                FragmentTransaction fragmentTransaction2 = FM.beginTransaction();
                Bundle bundle=new Bundle();
                bundle.putInt("ACTIVATE", 0);
                 bottom_nav bottom= new bottom_nav();
                 bottom.setArguments(bundle);
                fragmentTransaction2.replace(R.id.nav_frame,bottom );
                fragmentTransaction2.commit();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        drawerLayout.closeDrawer(GravityCompat.START);

try {
    Log.e("UIDetector","link is "+link.get(id));
    if (!Constants.Stack.lastElement().equals(name.get(id))) {
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction1 = FM.beginTransaction();

        if (link.get(id).equals("Events")) {
            EventsActivity web = new EventsActivity();
            bundle.putString("URL", link.get(id));
            web.setArguments(bundle);
            fragmentTransaction1.add(R.id.fragment, web).commit();

        }else if (link.get(id).equalsIgnoreCase("http://mit.thecollegeerp.com/academic/studentarea/myprofile.php#")) {
            DashBoardFragment dashBoardFragment = new DashBoardFragment();
            fragmentTransaction1.add(R.id.fragment, dashBoardFragment).commit();
        }else if(link.get(id).contains("assingement") ){
            AssignmentFragment assignFragment = new AssignmentFragment();
            fragmentTransaction1.add(R.id.fragment, assignFragment).commit();
        }
        else {
            WebFrame web = new WebFrame();
            if (link.get(id).contains("#")){
                bundle.putString("URL", link.get(id-1));
            }else bundle.putString("URL", link.get(id));

            web.setArguments(bundle);
            fragmentTransaction1.add(R.id.fragment, web).commit();

        }


        Constants.Stack.push(name.get(id));
        Constants.isTitleLoaded=true;
        fragmentTransaction1.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);

        fragmentTransaction1.addToBackStack(name.get(id));
    }
}catch (Exception e){e.printStackTrace();
    Constants.Stack.push("Home");
    onNavigationItemSelected(item);
}
            return false;

        }
    }
