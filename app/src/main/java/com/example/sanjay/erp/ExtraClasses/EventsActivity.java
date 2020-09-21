package com.example.sanjay.erp.ExtraClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
 import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.sanjay.erp.R;
 import com.example.sanjay.erp.filesClasses.FileAdapter;
import com.example.sanjay.erp.filesClasses.FilesModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

 import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.example.sanjay.erp.announcement.make_announcement.contentList;

public class EventsActivity extends Fragment {
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener;
    private boolean isImgEvent = false;
    private final int CHOOSE_IMAGE = 101;
    private Object img;
    private FileAdapter fileAdapter;
    LinkedHashMap<FilesModel, String> files;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_make_events, container, false);
        files=new LinkedHashMap<>();
        contentList=new ArrayList<>();
        BottomNavigationView navigation = v.findViewById(R.id.navigation);
        loadNav(v);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        TextView textView=getActivity().findViewById(R.id.title);
        textView.setText("Event");
        contentRecycler(v);
        loadListeners(v);
        return v;
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission(Activity context, String permission, int value)  {
        Log.e("Permission","Inside permission");
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        Log.e("Permission","Inside permission =>"+hasPermission);
        if (!hasPermission) {
            requestPermissions(
                    new String[]{permission},
                    value);
            Log.e("Permission","sending to get permission ");
        } else {
            Intent intent = new Intent();
            intent.setType("image/* video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
        }

        }

    @Override
    public void onRequestPermissionsResult(int value, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(value, permissions, grantResults);
        Log.e("Permission","permission result  =>"+value+"=>pe=>"+permissions+"=>gr=>"+grantResults);

        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (value == CHOOSE_IMAGE) {
                Log.e("Permission","permission result   granted");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
            }else{
                Log.e("Permission","permission result   granted not choose image");
            }
        }else{
            Log.e("Permission","permission result  not granted  ");
        }
    }

    private void loadListeners(final View v) {
        v.findViewById(R.id.uploadImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImageChooser();
            }
        });
        v.findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    store(v);

            }
        });
    }

    void loadNav(final View v) {
        Log.e("nav"," loading nav");
        mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Log.e("nav"," clicked");
                switch (item.getItemId()) {
                    case R.id.img:
                        isImgEvent = true;
                        Log.e("nav","img clicked");
                        v.findViewById(R.id.linkOnly).setVisibility(View.GONE);
                        v.findViewById(R.id.imageEvent).setVisibility(View.VISIBLE);
                        break;
                    case R.id.link:
                        Log.e("nav","link clicked");
                        isImgEvent = false;
                        v.findViewById(R.id.linkOnly).setVisibility(View.VISIBLE);
                        v.findViewById(R.id.imageEvent).setVisibility(View.GONE);
                        break;

                }
                return true;
            }
        };

    }

    private void showImageChooser() {

        requestPermission(getActivity(),READ_EXTERNAL_STORAGE,CHOOSE_IMAGE);


    }
    void store(View v){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("events/"+System.currentTimeMillis());

        String data=checkData((TextInputEditText )v.findViewById(R.id.nameEdit));
        if (data!=null&& !data.isEmpty())
        {
            databaseReference.child("name").setValue(data);
        }
        else{ Log.e("EventsActivity","name is empty");
            return;
        }

        if (isImgEvent)
        {
            if (!contentList.isEmpty()){
                databaseReference.child("img").setValue(contentList);

            }else { Log.e("EventsActivity","img list is empty");
                return;
            }

            databaseReference.child("ISLINK").setValue(false);

        }
        else{
              data=checkData((TextInputEditText )v.findViewById(R.id.linkEdit));
            if (data!=null&!Objects.requireNonNull(data).isEmpty())
                databaseReference.child("link").setValue(data);
            else{ Log.e("EventsActivity","link is empty");
                return;
            }
            databaseReference.child("ISLINK").setValue(true);
        }

        Log.e("EventsActivity","Stored Successfully");
        contentList.clear();
        fileAdapter.notifyDataSetChanged();
        files.clear();
        loadDialog();
        TextInputEditText textInputEditText=v.findViewById(R.id.linkEdit);
        textInputEditText.setText("");
        textInputEditText=v.findViewById(R.id.nameEdit);
        textInputEditText.setText("");
    }

    private String checkData(TextInputEditText  edit) {
        if (!Objects.requireNonNull(edit.getText()).toString().trim().isEmpty()){
            return edit.getText().toString().trim();
        }

        return "";
    }
    void loadDialog(){
        final AlertDialog.Builder alertDialog=
                new AlertDialog.Builder(getContext());
       // alertDialog.setTitle("");
        alertDialog.setMessage("Event created successfully " );
        alertDialog.setPositiveButton("OK",null );

        AlertDialog alertDialog1=  alertDialog.create();
        alertDialog1.show();

    }

    void contentRecycler(View v) {
        RecyclerView recyclerView = v.findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        fileAdapter =
                new FileAdapter(getContext(), (byte) 1, files, img, "Events");

        Log.e("MediaAnn", "Inside createNewFile " + files + " tempList " + " loading files initial");

        recyclerView.setAdapter(fileAdapter);

    }

    void createNewFile(Uri loc) {
        FilesModel model = new FilesModel();
        model.setLink(loc);
        files.put(model, "false");

        Log.e("MediaAnn", "Inside createNewFile " + files + " tempList ");
        fileAdapter.notifyItemInserted(files.size());
        fileAdapter.notifyDataSetChanged();

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null ) {
//            Log.e("Data","reading =>"+data.getClipData().getItemCount());

            if (data.getClipData() != null) {
                for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                    Log.e("Data","index=>"+i+"=>"+data.getClipData().getItemAt(i).getUri().toString());
                    img = (data.getClipData().getItemAt(i).getUri().toString());
                    createNewFile(data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                img = data.getData().toString();
                createNewFile(data.getData());
            }else{
                Log.e("Data","all are null=>"+data.getClipData().getItemCount());

            }


        }
    }


}
