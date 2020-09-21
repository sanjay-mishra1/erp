package com.example.sanjay.erp.newChatScreen;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.example.sanjay.erp.newChatScreen.UsersListTabActivity.Totalcount;
import static com.example.sanjay.erp.newChatScreen.UsersListTabActivity.isListLoaded;
public class AllUser extends Fragment {
    public static String searchKey="";

    View v;
    View progressBar;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_all_user, container, false);
        searchKey="";
        if (Constants.uid.isEmpty()){
            Constants.uid=Objects.requireNonNull(getContext()). getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("COLLEGEID",null);
        }
          recyclerView=v.findViewById(R.id.recycler_Chat);
        progressBar=  v.findViewById(R.id.progressbar);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false);


        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        SearchAdapter adapter;
        if (getContext().getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE).getString("TYPE","").equals("STAFF")) {
            adapter = new SearchAdapter(album_allorders.class, R.layout.chat_layout, mainactiv_allorders.FoodViewHolder.class,
                    FirebaseDatabase.getInstance().
                            getReference("users"), searchKey, getContext(), progressBar, 'u', v.findViewById(R.id.nothing_found), getActivity(), Constants.uid);
            recyclerView.setAdapter(adapter);
            searchBarListner(adapter);
        }

        else {
            checkFriends();
        }

        return v;
    }
    void checkFriends(){
     final DatabaseReference databaseReference=   FirebaseDatabase.getInstance().getReference("users").child(Constants.uid).child("friends");

     databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount()>0){
                  //  databaseReference.addValueEventListener(null);
                    SearchAdapter adapter = new SearchAdapter(album_allorders.class, R.layout.chat_layout, mainactiv_allorders.FoodViewHolder.class,
                            FirebaseDatabase.getInstance().
                                    getReference("users").child(Constants.uid).child("friends"), searchKey, getContext()
                            , progressBar, 'u', v.findViewById(R.id.nothing_found), getActivity(), Constants.uid);
                    recyclerView.setAdapter(adapter);
                    searchBarListner(adapter);
                }else{
                    progressBar.setVisibility(View.GONE);
                    v.findViewById(R.id.nothing_found).setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

        void searchBarListner(final SearchAdapter searchAdapter){
        EditText searchbar=Objects.requireNonNull(getActivity()). findViewById(R.id.searchEdit);
         searchbar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
             searchKey=editable.toString();
             progressBar.setVisibility(View.VISIBLE);
             Totalcount=0;
              searchAdapter.notifyDataSetChanged();

            Log.e("AllUser ","Search key changed =>"+searchKey+" notifying to adapter");
            }
        });
    }


}
