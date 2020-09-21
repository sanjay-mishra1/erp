package com.example.sanjay.erp.announcement;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.filesClasses.FileAdapter;
import com.example.sanjay.erp.filesClasses.FilesModel;
import com.example.sanjay.erp.firestore.AnnouncementData;
import com.example.sanjay.erp.database.store;
import com.example.sanjay.erp.newChatScreen.album_allorders;

import java.util.ArrayList;
import java.util.Calendar;
 import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class make_announcement extends AppCompatActivity {
    private   int CHOOSE_IMAGE = 101;
    private   int OPEN_CAMERA = 102;
    private   int CHOOSE_PDF = 103;
    private   int CHOOSE_DOC = 104;
    private   int CHOOSE_VIDEO = 105;
    RecyclerView recyclerView;
     String[] SearchGroup;
    String FirstLimit;
    String LastLimit;
    String Search;
     public static ArrayList<String> contentList;
    int index=0;
    LinkedHashMap<FilesModel,String> files;
    int ids[]={R.id.collegeLimit,R.id.rollLimit
            ,R.id.marksLimit,R.id.semLimit,R.id.departGroup
            ,R.id.attendanceLimit,  R.id.formFilterGroup,R.id.formNoLimit};
    int first_id[]={R.id.startInitials,R.id.rollLimit_startInitials
            ,R.id.marks_startInitials,R.id.sem_startInitials,1,
            R.id.attendance_startInitials,R.id.startInitials};
    int last_id[]={R.id.endInitials,R.id.rollLimit_endInitials
            ,R.id.marks_endInitials,R.id.sem_endInitials,1,
            R.id.attendance_endInitials,R.id.endInitials};
    protected static TreeSet<String> announcementSet;
     private boolean isNumber=false;
    private  FileAdapter fileAdapter;
    private Object img;
    public static LinkedHashMap<String,album_allorders> manuallyAnnouncementSet;
    BottomNavigationView.OnNavigationItemSelectedListener    mOnNavigationItemSelectedListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters);
        LoadName();
        img="";
        contentList=new ArrayList<>();
        files=new LinkedHashMap<>();
         contentRecycler();
        announcementSet=new TreeSet<>();
        manuallyAnnouncementSet=new LinkedHashMap<>();
        SearchGroup= getResources().getStringArray(R.array.filters);
        FilterGroup();
        BottomNavigationView navigation =   findViewById(R.id.navigation);
        loadFilterNav();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        RadioGroup radioGroup=findViewById(R.id.departGroup);
        final String DepartGroup[]=getResources().getStringArray(R.array.department);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FirstLimit=DepartGroup[group.indexOfChild(findViewById(checkedId))];
                Log.e("make_announcement","Department group firstlimit is<"+FirstLimit+">Index is "+index);
                LastLimit="";
            }
        });
        radioGroup=findViewById(R.id.formFilterGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                try{
                 int   index=group.indexOfChild(group.findViewById(checkedId));

                    Log.e("FilterGroup","form Group Index is "+index+"Search is "+SearchGroup[index]+" id is<"+checkedId);
                    if (index==0){
                        FirstLimit="YES";
                        collapse(findViewById(R.id.formNoLimit),300);
                    }else{
                        FirstLimit="NO";
                        expand(findViewById(R.id.formNoLimit),300,120);
                    }

                }catch (Exception ignored){

                }

             }
        });
    }
    void loadFilterNav(){
        mOnNavigationItemSelectedListener=new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_manually:
                        findViewById(R.id.manually_filter).setVisibility(View.VISIBLE);
                        findViewById(R.id.auto_filter).setVisibility(View.GONE);
                         break;
                    case R.id.navigation_auto:
                        findViewById(R.id.manually_filter).setVisibility(View.GONE);
                        findViewById(R.id.auto_filter).setVisibility(View.VISIBLE);
                         break;

                }
                return true;
            }
        };

    }

    void contentRecycler(){
         recyclerView=findViewById(R.id.recycler_view);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager( getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        fileAdapter=
                new FileAdapter(make_announcement.this, (byte) 1,files,img,"Announcement");

        Log.e("MediaAnn","Inside createNewFile "+files+" tempList "+" loading files initial");

        recyclerView.setAdapter(fileAdapter);

    }
    void createNewFile(Uri loc){
        FilesModel model=new FilesModel();
        model.setLink(loc);
        files.put(model,"false");

        Log.e("MediaAnn","Inside createNewFile "+files+" tempList ");
        fileAdapter.notifyItemInserted(files.size());
        fileAdapter.notifyDataSetChanged();

    }
    void LoadName(){
        if (!Constants.uname.isEmpty()){
          TextView textView=findViewById(R.id.username);
          textView.append(getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("USERNAME","STAFF"));
        }
    }
    public void filterOnclicked(View view) {

            FloatingActionButton filter = findViewById(R.id.filterTextviewid);
            FloatingActionButton Floatfilter = null;
            try {
                Floatfilter = findViewById(R.id.filterFloating);
            } catch (Exception ignored) {
            }

            if (findViewById(R.id.annoucemnt_filters).getVisibility() == View.VISIBLE) {
                if (extractLimitData()) {
                    findViewById(R.id.annoucemnt_filters).setVisibility(View.GONE);
                    findViewById(R.id.AnnouncementHome).setVisibility(View.VISIBLE);

                    try {
                        filter.setImageResource(R.drawable.ic_filter_list_black_24dp);
//                 filter.setCompoundDrawables(getResources(). getDrawable(R.drawable.ic_filter_list_black_24dp),null,null,null);
                    } catch (Exception ignored) {
                    }
                    try {
                        Floatfilter.setImageResource(R.drawable.ic_filter_list_black_24dp);
                        //   Floatfilter.setBackgroundDrawable(getResources(). getDrawable(R.drawable.ic_filter_list_black_24dp));
                    } catch (Exception ignored) {
                    }
                    loadFragment(true,"");
                }

            } else {
                try {
                    filter.setImageResource(R.drawable.cancel);

//              filter.setCompoundDrawables(getResources().getDrawable(R.drawable.cancel), null, null, null);
                } catch (Exception ignored) {
                }
                findViewById(R.id.annoucemnt_filters).setVisibility(View.VISIBLE);

                findViewById(R.id.AnnouncementHome).setVisibility(View.GONE);

                try {
                    Floatfilter.setImageResource(R.drawable.cancel);
                    //Floatfilter.setBackgroundDrawable(getResources(). getDrawable(R.drawable.cancel));
                } catch (Exception ignored) {
                }

            }



    }

    public void SendAnnouncementClicked(View view) {

         if (findViewById(R.id.annoucemnt_filters).getVisibility()==View.VISIBLE){
            filterOnclicked(view);
        }else{
            checkAnnoucementData();
        }
    }

    @Override
    public void onBackPressed() {

        if (findViewById(R.id.annoucemnt_filters).getVisibility()==View.VISIBLE) {
            //filterOnclicked(view);
            findViewById(R.id.AnnouncementHome).setVisibility(View.VISIBLE);
            findViewById(R.id.annoucemnt_filters).setVisibility(View.GONE);
        }else
         {super.onBackPressed();
         }
    }

    private void checkAnnoucementData() {

        if (announcementSet.isEmpty()&&manuallyAnnouncementSet.isEmpty()){
            Log.e("StoringAnnouncment","Set is empty");
            return;
        }
        /*if (FirstLimit==null|| FirstLimit.isEmpty()){
            Log.e("StoringAnnouncment","FirstLimit is empty");
            return;
        }*/
        TextInputEditText header=findViewById(R.id.announcementHeader);
        if (header.getText().toString().trim().isEmpty()){
            Log.e("StoringAnnouncment","header is empty");
            return;
        }
        EditText announcementText=findViewById(R.id.announcementText);
        if (announcementText.getText().toString().trim().isEmpty()){
            Log.e("StoringAnnouncment","announcement text is empty");
            return;
        }
        long time=(System.currentTimeMillis());
        List<String> userids = new ArrayList<>(announcementSet);
        if (!manuallyAnnouncementSet.isEmpty()){
            userids.addAll(manuallyAnnouncementSet.keySet());

        }
        if (Constants.uid.isEmpty()) {
            Constants.uid=getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID","NOT AVAILABLE");
        }
        userids.add(Constants.uid);
        AnnouncementData data= new AnnouncementData(getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("USERNAME","STAFF"),
                header.getText().toString().trim(),
                announcementText.getText().toString().trim(),
                userids,time,contentList ,getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID",""));
        loadFinalCheckDialog(data,time,
                header.getText().toString().trim(),announcementText.getText().toString().trim());

    }
    void loadFinalCheckDialog(final AnnouncementData data, final long time, String Header, String message){
        final AlertDialog.Builder alertDialog=
                new AlertDialog.Builder(make_announcement.this);
        alertDialog.setTitle("Announcement");
        alertDialog.setMessage("Are you sure you want to announce \n"+Header+"\n"+message
        +"\n Note\n* You cannot modify later in the future." +
                        "\n* You cannot delete this announcement." +
                        "\n* This announcement can be seen only by you and your selected participants."

        );
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new store(data,time,getApplicationContext());
                finish();
            }
        });
        alertDialog.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog1=  alertDialog.create();
        alertDialog1.show();

    }
    public void cancelAnnouncementClicked(View view) {
        //finish();
        onBackPressed();
    }
    boolean extractLimitData(){
        try {

            if (index!=4) {


                TextInputEditText Limit1 = findViewById(first_id[index]);
                TextInputEditText Limit2 = findViewById(last_id[index]);
                FirstLimit = Limit1.getText().toString().trim();
                LastLimit = Limit2.getText().toString().trim();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Log.e("FilterFragment","Inside announcement " +
                "Search <"+Search+">"+"First<"+FirstLimit+">"+"Last<"+LastLimit+">");

//        if (FirstLimit.isEmpty())
//        {   Limit1.setError("Enter start limit");
//
//            return false;
//        }
//        else
        return true;
    }
    private void FilterGroup(){
        final RadioGroup radioGroup=findViewById(R.id.filterGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
         try {   index=group.indexOfChild(group.findViewById(checkedId));
               index/=2;

             Log.e("FilterGroup","Index is "+index+"Search is "+SearchGroup[index]+" id is<"+checkedId);
             Search=SearchGroup[index];
             expand(findViewById(ids[index]),300,120);
         }catch (Exception e){
             e.printStackTrace();
             Toast.makeText(make_announcement.this,"Failed to load this option",Toast.LENGTH_SHORT).show();
         }

            }
        });
    }
  /*  void loadFragment(){
     try {
         if (Search!=null && !Search.isEmpty()){
             Bundle bundle = new Bundle();
             bundle.putString("SEARCHQUERY",Search);
             if (Search.equals("YEAR")){
                 loadSem();
                 isNumber=true;
             }
             if (index==5||index==2){
                 isNumber=true;

             }
             if (LastLimit.isEmpty()){
                 LastLimit=FirstLimit;
             }

             Log.e("make_announcement","Sending to fragment \nfirstlimit <"+FirstLimit.toUpperCase()+">\nLastLimit<"+LastLimit.toUpperCase()+">\n"+"index<"+index+">\nName<"+Search+">");
             bundle.putString("FIRSTLIMIT",FirstLimit.toUpperCase());
             bundle.putString("LASTLIMIT",LastLimit.toUpperCase());
             bundle.putBoolean("ISNUMBER",isNumber);
             FragmentFilterResult filterResult = new FragmentFilterResult();
             filterResult.setArguments(bundle);
             FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
             fragmentTransaction1.replace(R.id.frameLayout, filterResult).commit();
         }
     }catch (Exception ignored){
     }
    }
*/
    private void loadSem() {
        try {int year,yearsecond;
            if (LastLimit.isEmpty()) {
                year = giveYear(Integer.parseInt(FirstLimit));
                yearsecond = 0;//giveYear(Integer.parseInt("0"));
            }
            else {
                year = giveYear(Integer.parseInt(LastLimit));
                yearsecond = giveYear(Integer.parseInt(FirstLimit));
            }
            isNumber=true;
/*
switch (year) {
case 1:
case 2: year=0;break;
case 3:
case 4: year=1;break;
case 5:
case 6: year=2;break;
case 7:
case 8: year=3;break;

}
*/

            Calendar calendar=new GregorianCalendar();
           int presentyear= calendar.get(Calendar.YEAR);
            if (LastLimit.isEmpty()){
                if (presentyear%2==0){
                    FirstLimit= String.valueOf(presentyear-year);
                    LastLimit= "";
                }else{
                    FirstLimit= String.valueOf(presentyear-year-1);
                    LastLimit= "";
                }
            }else{
                if (presentyear%2==0){
                    FirstLimit= String.valueOf(presentyear-year);
                    LastLimit= String.valueOf(presentyear-yearsecond);
                }else{
                    FirstLimit= String.valueOf(presentyear-year-1);
                    LastLimit= String.valueOf(presentyear-yearsecond-1);
                }
            }

        }catch (Exception ignored){
        }
    }

     private int giveYear(int year){
         switch (year) {
             case 1:
             case 2: year=0;break;
             case 3:
             case 4: year=1;break;
             case 5:
             case 6: year=2;break;
             case 7:
             case 8: year=3;break;

         }
         return year;

     }
    public   void expand(final View v, int duration, int targetHeight) {

         int prevHeight = v.getHeight();
        collapse(v,duration);
         v.setVisibility(View.VISIBLE);
         ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
         valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
             @Override
             public void onAnimationUpdate(ValueAnimator animation) {
                 v.getLayoutParams().height = (int) animation.getAnimatedValue();
                 v.requestLayout();
             }
         });
         valueAnimator.setInterpolator(new DecelerateInterpolator());
         valueAnimator.setDuration(duration);
         valueAnimator.start();
     }

     public  void collapse(View v2, int duration) {

         View v;
         for (int id : ids) {
             if (v2 != findViewById(id)) {
                 v = findViewById(id);
                 int prevHeight = v.getHeight();


                 final ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, 0);
                 valueAnimator.setInterpolator(new DecelerateInterpolator());
                 final View finalV = v;
                 valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                     @Override
                     public void onAnimationUpdate(ValueAnimator animation) {
                         finalV.getLayoutParams().height = (int) animation.getAnimatedValue();
                         finalV.requestLayout();
                     }
                 });
                 valueAnimator.addListener(new Animator.AnimatorListener() {
                     @Override
                     public void onAnimationStart(Animator animator) {

                     }

                     @Override
                     public void onAnimationEnd(Animator animator) {
                         finalV.setVisibility(View.INVISIBLE);

                     }

                     @Override
                     public void onAnimationCancel(Animator animator) {

                     }

                     @Override
                     public void onAnimationRepeat(Animator animator) {

                     }
                 });

                 valueAnimator.setInterpolator(new DecelerateInterpolator());
                 valueAnimator.setDuration(duration);
                 valueAnimator.start();
             }
         }
     }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);
        } else {
            if (value==OPEN_CAMERA)
            {Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, OPEN_CAMERA);
            }else if (value==CHOOSE_IMAGE){
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
            }else if (value==CHOOSE_PDF){

                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), CHOOSE_PDF);
            }
            else if (value==CHOOSE_DOC){
                Intent intent = new Intent();
                intent.setType("application/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Document"), CHOOSE_DOC);
            }else   if (value==CHOOSE_VIDEO){
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), CHOOSE_VIDEO);
            }
         }
    }
    @Override
    public void onRequestPermissionsResult(int value, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(value, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (value == OPEN_CAMERA) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, OPEN_CAMERA);
            } else if (value == CHOOSE_IMAGE) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
            } else if (value == CHOOSE_PDF) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select PDF"), CHOOSE_PDF);
            } else if (value == CHOOSE_DOC) {
                Intent intent = new Intent();
                 intent.setType("application/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Document"), CHOOSE_DOC);
            } else if (value == CHOOSE_VIDEO) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), CHOOSE_VIDEO);
            }
        }

    }

    public void onActivityResult(int value, int resultCode, Intent data) {
        super.onActivityResult(value, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Log.e("Activity Result", "" + data.getData());
            if (value<103)
                {
                    img= (data.getData().toString());
            }else{
                img= (data.getData().toString());
            }
            createNewFile(data.getData());
            }
        }



    void loadFile(String type,int code){

        Log.e("MediaAnn","Inside loadFile "+type+code);
        requestPermission(this, type, code);

  /*      Intent intent = new Intent();
        intent.setType(type);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select"), CHOOSE_IMAGE);
*/
     }
    public void addImageOnClick(View view) {
        Log.e("MediaAnn","Image clicked ");
        loadFile(READ_EXTERNAL_STORAGE,CHOOSE_IMAGE);
    }

    public void addVidoOnClick(View view) {
        Log.e("MediaAnn","Video clicked ");
     try {
         img=getResources().getDrawable(R.drawable.ic_menu_slideshow).toString();
     }catch (Exception e){
         img="";
     }
        loadFile(READ_EXTERNAL_STORAGE,CHOOSE_VIDEO);

    }

    public void addDocumentOnClick(View view) {
        Log.e("MediaAnn","Doc clicked ");
try {
    img=getResources().getDrawable(R.drawable.document).toString();
}catch (Exception e){
    img="";
}
        loadFile(READ_EXTERNAL_STORAGE,CHOOSE_DOC);
    }


    public void addPdfOnClick(View view) {
        Log.e("MediaAnn","Pdf clicked ");
     try {
        // img=icon(".pdf");
     }catch (Exception e){
         img="";
     }
        loadFile(READ_EXTERNAL_STORAGE,CHOOSE_PDF);
    }
    public void addCameraOnClick(View view) {
        Log.e("MediaAnn","Camera clicked ");
        loadFile(Manifest.permission.CAMERA,OPEN_CAMERA);
    }

    public void addMaunuallyClicked(View view) {
        EditText editText=findViewById(R.id.manuallyEdit);
        Log.e("make_announcement","manual clicked ");

        if (!editText.getText().toString().trim().isEmpty()){
            FloatingActionButton filter = findViewById(R.id.filterTextviewid);
            FloatingActionButton Floatfilter = null;
            try {
                Floatfilter = findViewById(R.id.filterFloating);
            } catch (Exception ignored) {
            }
            findViewById(R.id.annoucemnt_filters).setVisibility(View.GONE);
            findViewById(R.id.AnnouncementHome).setVisibility(View.VISIBLE);

            try {
                filter.setImageResource(R.drawable.ic_filter_list_black_24dp);
             } catch (Exception ignored) {
            }
            try {
                Floatfilter.setImageResource(R.drawable.ic_filter_list_black_24dp);
                //   Floatfilter.setBackgroundDrawable(getResources(). getDrawable(R.drawable.ic_filter_list_black_24dp));
            } catch (Exception ignored) {
            }
            loadFragment(false,editText.getText().toString().trim());

        }

    }
    void loadFragment(boolean type,String id) {
        if (type) {
            try {

                if (Search != null && !Search.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putString("SEARCHQUERY", Search);
                    if (Search.equals("YEAR")) {
                        loadSem();
                        isNumber = true;
                    }
                    if (index == 5){
                        isNumber = true;
                        if (!FirstLimit.contains(".")){
                            if (!FirstLimit.isEmpty())
                                FirstLimit=FirstLimit+".00";
                        }else if (FirstLimit.length()<4)
                            FirstLimit=FirstLimit+"0";


                        if (!LastLimit.contains(".")){
                            if (!LastLimit.isEmpty())
                                LastLimit=LastLimit+".00";

                        } else if (LastLimit.length()<4)
                            LastLimit=LastLimit+"0";
                    }else
                    if ( index == 2|| index == 4) {
                        isNumber = true;
                        if (FirstLimit.length()<2){
                            if (!FirstLimit.isEmpty())
                            FirstLimit="0"+FirstLimit;
                        }
                        if (LastLimit.length()<2){
                            if (!LastLimit.isEmpty())
                            LastLimit="0"+LastLimit;
                        }
                       // Log.e("make_announcement","Last limit "+LastLimit+" is changed to empty");
                      //  LastLimit="";
                    }
                    if (LastLimit.isEmpty() &&index!=4) {
                        LastLimit = FirstLimit;
                    }

                    Log.e("make_announcement", "Sending to fragment \nfirstlimit <" + FirstLimit.toUpperCase() + ">\nLastLimit<" + LastLimit.toUpperCase() + ">\n" + "index<" + index + ">\nName<" + Search + ">");
                    bundle.putString("FIRSTLIMIT", FirstLimit.toUpperCase());
                    bundle.putString("LASTLIMIT", LastLimit.toUpperCase());
                    bundle.putBoolean("ISNUMBER", isNumber);
//                    bundle.putBoolean("ISNUMBER", isNumber);


                    FragmentFilterResult filterResult = new FragmentFilterResult();
                    filterResult.setArguments(bundle);
                    FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.frameLayout, filterResult).commit();
                }
            } catch (Exception ignored) {
            }
        }else {
         try {
             Bundle bundle = new Bundle();

             bundle.putString("FIRSTLIMIT", id.toUpperCase());
             bundle.putBoolean("ISMANUAL", true);
             FragmentFilterResult filterResult = new FragmentFilterResult();
             filterResult.setArguments(bundle);
             FragmentTransaction fragmentTransaction1 = getSupportFragmentManager().beginTransaction();
             fragmentTransaction1.replace(R.id.manuallyAddedFrame, filterResult).commit();
         }catch (Exception e){
             e.printStackTrace();
         }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
