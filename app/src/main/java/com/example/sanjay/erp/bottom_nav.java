package com.example.sanjay.erp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.sanjay.erp.Setting.SettingHome;
import com.example.sanjay.erp.announcement.ShowAnnouncement;
import com.example.sanjay.erp.newChatScreen.chat_app_Activity;
import com.example.sanjay.erp.webFrame.WebFrame;

import java.util.Objects;

import static com.example.sanjay.erp.Constants.Stack;

public class bottom_nav extends Fragment {
    private boolean isStaff=false;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction1 =getActivity(). getSupportFragmentManager().beginTransaction();
              FragmentTransaction fragmentTransaction2=getActivity().getSupportFragmentManager().beginTransaction();
            TextView textView=getActivity().findViewById(R.id.title);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setFragmentVisibilty();
                    getActivity().findViewById(R.id.searchScreen).setVisibility(View.GONE);
                    getActivity().findViewById(R.id.appbar).setVisibility(View.VISIBLE);
                    getActivity().findViewById(R.id.searchLinear).setVisibility(View.GONE);

                    if (getActivity().findViewById(R.id.fragmentExtra).getVisibility()==View.VISIBLE){
                        getActivity().findViewById(R.id.fragmentExtra).setVisibility(View.GONE);
                        textView.setText("Home");

                        getActivity().findViewById(R.id.fragment).setVisibility(View.VISIBLE);
                    }


                    return true;
                case R.id.navigation_announcement:
                    setFragmentVisibilty();
               try {                    getActivity().findViewById(R.id.searchLinear).setVisibility(View.GONE);
                   getActivity().findViewById(R.id.searchScreen).setVisibility(View.GONE);

                   getActivity().findViewById(R.id.appbar).setVisibility(View.VISIBLE);

                   ShowAnnouncement showAnnouncement = new ShowAnnouncement();
                    fragmentTransaction1.
                           replace(R.id.fragmentExtra, showAnnouncement).commit();

               }catch (Exception e){
                   e.printStackTrace();
               }
                    return true;
                case R.id.navigation_chat:
                    setFragmentVisibilty();
                    try {    if (!isStaff)
                        getActivity().findViewById(R.id.searchLinear).setVisibility(View.VISIBLE);
                            else textView.setText("Chat");
                        getActivity().findViewById(R.id.appbar).setVisibility(View.VISIBLE);

                        chat_app_Activity chat = new chat_app_Activity();
                   fragmentTransaction2.replace(R.id.fragmentExtra, chat).commit();
//                        fragmentTransaction1.addToBackStack("Extra");

                    }catch (Exception e){
                   e.printStackTrace();
               }
                    return true;
               case R.id.navigation_user:
                   getActivity().findViewById(R.id.appbar).setVisibility(View.GONE);
                   getActivity().findViewById(R.id.searchLinear).setVisibility(View.GONE);
                   getActivity().findViewById(R.id.searchScreen).setVisibility(View.GONE);
                   setFragmentVisibilty();
                   textView.setText("Settings");
                   SettingHome SettingHome = new SettingHome();
                   fragmentTransaction2.replace(R.id.fragmentExtra, SettingHome).commit();
                      return true;
            }
            return false;
        }
    };


    void setFragmentVisibilty(){
            if (getActivity().findViewById(R.id.fragmentExtra).getVisibility()!=View.VISIBLE){
                getActivity().findViewById(R.id.fragmentExtra).setVisibility(View.VISIBLE);
                getActivity().findViewById(R.id.fragment).setVisibility(View.GONE);
            }
        }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (Constants.type.isEmpty()){
            Constants.type=Objects.requireNonNull(getActivity()).getSharedPreferences("USERCREDENTIALS",Context.MODE_PRIVATE).getString("TYPE","");
        }
        if (Constants.type.equalsIgnoreCase("STAFF")){
            isStaff=true;
            view=inflater.inflate(R.layout.activity_bottom_nav_staff,container,false);
        }else
            view=inflater.inflate(R.layout.activity_bottom_nav,container,false);
        Objects.requireNonNull(getActivity()).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
       if (Objects.requireNonNull(this.getArguments()).getBoolean("LOADLINK")){
           Intent intent=getActivity().getIntent();
           Bundle bundle = new Bundle();
           Log.e("bottom_nav","URL=>"+intent.getStringExtra("URL"));
           bundle.putString("URL",intent.getStringExtra("URL"));
           WebFrame web = new WebFrame();
           web.setArguments(bundle);

           FragmentTransaction fragmentTransaction1 =getActivity(). getSupportFragmentManager().beginTransaction();
           fragmentTransaction1.replace(R.id.fragment, web);
           fragmentTransaction1.commit();
       }else{
           TextView textView=getActivity().findViewById(R.id.title);
           textView.setText(Constants.Stack.lastElement());
       }

        BottomNavigationView navigation =   view.findViewById(R.id.navigation);
         navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
       try {
           navigation.setSelectedItemId(Objects.requireNonNull(this.getArguments()).getInt("ACTIVATE"));
       }catch (Exception ignored){}
        return view;
    }



}
