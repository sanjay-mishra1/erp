package com.example.sanjay.erp.newChatScreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;

import com.example.sanjay.erp.Setting.UserInfo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.example.sanjay.erp.newChatScreen.AllUser.searchKey;
import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class chat_app_Activity extends Fragment {
    RecyclerView recyclerView;
    private MyAdapter adapter;
     private boolean isSearchVisible=false;
    private SearchAdapter searchAdapter;
    private View searchProgress;
    private HashMap imblock;
    private HashMap block;
    boolean isBlockListLoaded=false;
    private View searchScreen;
    private ImageView searchIcon;
    private View tempView;

    public chat_app_Activity(){

}
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.chat_activity_layout,container,false);
         recyclerView=view.findViewById(R.id.recycler_Chat);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false);
         imblock=new HashMap();
        block=new HashMap();
        tempView=view. findViewById(R.id.progressbar);
       searchIcon= (ImageView) getActivity().findViewById(R.id.searchIcon);
         searchProgress=getActivity().findViewById(R.id.progressbar);
        mLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);
       searchScreen= getActivity().findViewById(R.id.searchScreen);
        view.findViewById(R.id.floatingbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAllUsers();
            }
        });
        check(view);
//        searchAdapter=new SearchAdapter(album_allorders.class,R.layout.chat_layout,mainactiv_allorders.FoodViewHolder.class,FirebaseDatabase.getInstance().
//                getReference("users"),searchKey,getContext(),
//                searchProgress,true,
//                new HashMap(),new HashMap(),false);
//        recyclerView.setAdapter(searchAdapter);
        if (!getContext().getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("TYPE","").equals("STAFF")) {
            loadSearchAdapter(view);
            loadSearchListener();
        }

        return  view;
    }

private void loadSearchListener() {
        Objects.requireNonNull(getActivity()).findViewById(R.id.searchEdit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Glide.with(Objects.requireNonNull(getContext())).load(getContext().getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp)).into(searchIcon);
                searchScreen.setVisibility(View.VISIBLE);
                isSearchVisible=true;
                Log.e("SearchEdit","Clicked");
            }
        });
     Objects.requireNonNull(getActivity()).findViewById(R.id.searchIcon).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isSearchVisible){
                //hide
               searchScreen.setVisibility(View.GONE);
                Glide.with(Objects.requireNonNull(getContext())).load(getContext().getResources().getDrawable(R.drawable.ic_search_black_24dp)).into(searchIcon);
                Log.e("SearchScreen","Button pressed "+"hiding "+isSearchVisible);
                isSearchVisible=false;
            }else{
                //add
                searchScreen.setVisibility(View.VISIBLE);
                Log.e("SearchScreen","Button pressed "+"showing "+isSearchVisible);
                isSearchVisible=true;
                Glide.with(Objects.requireNonNull(getContext())).load(getContext().getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp)).into(searchIcon);
              }
        }
    });
}

    private void loadSearchAdapter(View v) {
        RecyclerView recyclerView=Objects.requireNonNull(getActivity()).findViewById(R.id.searchRecycler);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager( getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(null);
        loadBlockList(v,recyclerView);
        searchBarListner(searchAdapter,v);
    }

    private void searchBarListner(final SearchAdapter searchAdapter, View v) {
        EditText searchbar=Objects.requireNonNull(getActivity()).findViewById(R.id.searchEdit);
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
                searchProgress.setVisibility(View.VISIBLE);
                if (searchScreen.getVisibility()!=View.VISIBLE)
                {   isSearchVisible=true;
                    Glide.with(Objects.requireNonNull(getContext())).load(getContext().getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp)).into(searchIcon);

                    searchScreen.setVisibility(View.VISIBLE);
                }
                try {
                    Log.e("AllUser ","Search key changed =>"+searchKey+" notifying to adapter");
                    searchAdapter.notifyDataSetChanged();
                }catch (Exception e){
                    Log.e("AllUser ","adapter is null Search key changed =>"+searchKey+" not notifying to adapter");
                }

            }
        });
    }

    private void loadBlockList(final View v, final RecyclerView recyclerView){
        FirebaseDatabase.getInstance().getReference("users/" +
                Constants.uid).child("block").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                isBlockListLoaded=true;

                     try {
                        Log.e("Blocked method","Blocked IN"+(HashMap) dataSnapshot.child("blocked_by").
                                getValue()+"\n"+(HashMap) dataSnapshot.child("blocked_users").getValue());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                     imblock=(HashMap) dataSnapshot.child("blocked_by").getValue();
                     if (imblock==null){
                         imblock=new HashMap();
                     }

                       block=(HashMap) dataSnapshot.child("blocked").getValue();

                if (block==null){
                    block=new HashMap();
                }
                Log.e("BLOCK_STATUS","Block List Refreshed=>"+block+"\n=>"+imblock);
                     searchAdapter=new SearchAdapter(album_allorders.class,R.layout.chat_layout,mainactiv_allorders.FoodViewHolder.class,FirebaseDatabase.getInstance().
                            getReference("users"),searchKey,getContext(),
                             searchProgress,'f',
                            imblock,block,true, v.findViewById(R.id.nothing_found),getActivity(),Constants.uid);
                     recyclerView.setAdapter(searchAdapter);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void check(final View view ){
        final DatabaseReference databaseReference=  FirebaseDatabase.getInstance().getReference().
                child("users").child(Constants.uid).child("chat");
         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ( !dataSnapshot.exists()){
                   view. findViewById(R.id.nothing_found).setVisibility(View.VISIBLE);
                    view.  findViewById(R.id.progressbar).setVisibility(View.GONE);
                    tempView=view. findViewById(R.id.nothing_found);
                } else
                    tempView=view. findViewById(R.id.progressbar);

                    ONStart(databaseReference,view);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
     protected void ONStart(final DatabaseReference databaseReference, final View view) {
         Query query=databaseReference.orderByChild("Server_Time");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.chat_layout,
                mainactiv_allorders.FoodViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
//             view.   findViewById(R.id.progressbar).setVisibility(View.GONE);
//                viewHolder.setImage(model.getUser_Img(),getApplicationContext());
                Log.e("Chat","Chat loaded");
                tempView.setVisibility(View.GONE);
                try {
                  viewHolder.setlast_message(model.getUser_last_Message(),model.getLast_message_counter());
                  viewHolder.setTime1(model.getTime());
              }catch (Exception e){}
               try {
                  Load_User_info(model.getCHATID().replace(Constants.uid,""), viewHolder.setImage(model.getUSERIMAGE(),getContext()),
                          viewHolder.setUser_name(model.getUSERNAME()),viewHolder.mView,model,viewHolder);
              }catch (Exception e){}

               try {
                   viewHolder.setMessage_counter(model.getLast_message_counter());
               }catch (DatabaseException e){
                   Log.e("Database Exception",e.toString());
               }



            }

        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }
    void loadUserListener(final View v, final boolean isstaff, final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model){
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),help_activity.class);
                intent.putExtra("UID",model.getCHATID());
                TextView t=viewHolder.mView.findViewById(R.id.username);
                intent.putExtra("UserName",t.getText().toString().trim());
                         intent.putExtra("STAFF",isstaff);

                if (block.isEmpty()&&imblock.isEmpty()){
                    if (isBlockListLoaded)
                        intent.putExtra("ISUSERBLOCKED",'n');
                    else intent.putExtra("ISUSERBLOCKED",'d');
                }else{
                    if (block.containsKey(model.getUSERID()))
                        intent.putExtra("ISUSERBLOCKED",'m');
                    else if (imblock.containsKey(model.getUSERID()))
                        intent.putExtra("ISUSERBLOCKED",'o');
                    else intent.putExtra("ISUSERBLOCKED",'n');
                }

                t=viewHolder.mView.findViewById(R.id.imglink);
                intent.putExtra("img",t.getText().toString().trim());

                startActivity(intent);
               try {
                   v.findViewById(R.id.Unread_messages).setVisibility(View.GONE);
                   TextView text = (TextView) v.findViewById(R.id.usermessage);
                   text.setTextColor(getContext().getResources().getColor(R.color.gray2));
               }catch (Exception e){
                   e.printStackTrace();
               }

            }
        });
    }
    protected void search(final DatabaseReference databaseReference, final View view,String searchKey) {
        Query query=databaseReference.orderByChild("Server_Time");
        FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder>(
                album_allorders.class,
                R.layout.chat_layout,
                mainactiv_allorders.FoodViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, int position) {
                view.   findViewById(R.id.progressbar).setVisibility(View.GONE);

                try {
                    viewHolder.setlast_message(model.getUser_last_Message(),model.getLast_message_counter());
                    viewHolder.setTime1(model.getTime());
                }catch (Exception e){}
                try {
                    Load_User_info(model.getCHATID().replace(Constants.uid,""), viewHolder.setImage(model.getUSERIMAGE(),getContext()),
                            viewHolder.setUser_name(model.getUSERNAME()),viewHolder.mView, model, viewHolder);
                }catch (Exception e){}

                try {
                    viewHolder.setMessage_counter(model.getLast_message_counter());
                }catch (DatabaseException e){
                    Log.e("Database Exception",e.toString());
                }


                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent=new Intent(getContext(),help_activity.class);
                        intent.putExtra("UID",model.getCHATID());
                        TextView t=viewHolder.mView.findViewById(R.id.username);
                        intent.putExtra("UserName",t.getText().toString().trim());
                        if (block.isEmpty()&&imblock.isEmpty()){
                            if (isBlockListLoaded)
                                intent.putExtra("ISUSERBLOCKED",'n');
                            else intent.putExtra("ISUSERBLOCKED",'d');
                        }else{
                            if (block.containsKey(model.getUSERID()))
                                intent.putExtra("ISUSERBLOCKED",'m');
                            else if (imblock.containsKey(model.getUSERID()))
                                intent.putExtra("ISUSERBLOCKED",'o');
                            else intent.putExtra("ISUSERBLOCKED",'n');
                        }

                        t=viewHolder.mView.findViewById(R.id.imglink);
                        intent.putExtra("img",t.getText().toString().trim());
                        t=viewHolder.mView.findViewById(R.id.Unread_messages);
                        databaseReference.child(model.getCHATID()).child("Last_message_counter").setValue(0);
                        startActivity(intent);
                    }
                });
            }

        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        checkUserId();
    }

    void checkUserId(){
        if (Constants.uid==null||Constants.uid.isEmpty()){
            Constants.uid=getContext(). getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID","");
        }
    }
    void Load_User_info(final String Uid, final ImageView imageview, final TextView User_name, final View view, final album_allorders model, final mainactiv_allorders.FoodViewHolder viewHolder){

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users"+"/"+Uid);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("ValueEventListener","activated=>"+Uid);
                User_name.setText((String) dataSnapshot.child("USERNAME").getValue());

              TextView textView=view.findViewById(R.id.imglink);
              if ( dataSnapshot.child("USERIMAGE").getValue()!=null)
              textView.setText((String) dataSnapshot.child("USERIMAGE").getValue());
              else textView.setText("");

              try {long color;
                    if (dataSnapshot.child("IMAGECOLOR").getValue()==null){
                        color= Color.BLACK;
                    }else
                        color= (long) dataSnapshot.child("IMAGECOLOR").getValue();
                    boolean isstaff=false;
                    if (Objects.requireNonNull(dataSnapshot.child("TYPE").getValue()).toString().toUpperCase().equals("STAFF")){
                        isstaff=true;
                    }
                  Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(dataSnapshot.child("USERIMAGE").getValue()).into(imageview);
                 if (dataSnapshot.child("friends/"+Constants.uid).exists()||isstaff) {
                     setImageListner(imageview, Objects.requireNonNull(dataSnapshot.child("USERIMAGE").getValue()).toString(),
                             Uid,
                             Objects.requireNonNull(dataSnapshot.child("ROLLNO").getValue()).toString(),
                             Objects.requireNonNull(dataSnapshot.child("ATTENDANCE").getValue()).toString(),
                             color,isstaff);

                 }
                 loadUserListener(view,isstaff,viewHolder,model);
              }catch (Exception ignored){}

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void setImageListner(final ImageView imageview, final String userimage, final String cid, final String univid, final String attendance, final long imagecolor, final boolean isstaff){

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isBlockListLoaded){
                     if (!block.containsKey(cid)&!imblock.containsKey(cid)){

                Intent intent=new Intent(getContext(),UserInfo.class);
                intent.putExtra("IMGLINK",userimage);
                intent.putExtra("COLLEGEID",cid);
                intent.putExtra("UNIVID",univid);
                intent.putExtra("STAFF",isstaff);
                intent.putExtra("ATTENDANCE",attendance);
                intent.putExtra("COLOR",imagecolor);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation
                                (Objects.requireNonNull(getActivity()),imageview , "user_image");
                startActivity(intent, optionsCompat.toBundle());
            }
                }
            }
        });
    }
    public void loadAllUsers() {
        startActivity(new Intent(getContext(),UsersListTabActivity.class));

    }
}
