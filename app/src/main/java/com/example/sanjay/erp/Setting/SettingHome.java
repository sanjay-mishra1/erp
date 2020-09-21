package com.example.sanjay.erp.Setting;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.ExtraClasses.issue;
 import com.example.sanjay.erp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
 import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class SettingHome extends Fragment implements ActivityCompat.OnRequestPermissionsResultCallback{

    private   final int OPEN_CAMERA = 102;
    private   final int CHOOSE_IMAGE = 101;
    private ImageView imageView;
    private Uri uriProfileImage;
    ProgressBar progressBar;
    private SharedPreferences sharedPre;
    private long color;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_profile,container,false);
       try {
           if (Constants.uid.isEmpty()){
               Constants.uid=Objects.requireNonNull(getContext()).getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID",null);
           }
       }catch (Exception e){
           Constants.uid=Objects.requireNonNull(getContext()).getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID",null);
       }
        sharedPre=Objects.requireNonNull(getContext()). getSharedPreferences(MYPREFERENCES,MODE_PRIVATE);
        imageView=view.findViewById(R.id.image_message_profile);
        loadExtraDetails(view);
        extractUserDetails(view);
        progressBar=view.findViewById(R.id.progressBar);


        new loadAttendance().execute();
        loadListeners(view);
        return view;
    }


    void loadImageListner(final String imgId, final long imagecolor){
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(),ShowImg.class);
                intent.putExtra("IMGLINK",imgId);
                intent.putExtra("COLOR",imagecolor);
                 ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                        .makeSceneTransitionAnimation
                                (Objects.requireNonNull(getActivity()),imageView , "user_image");
                startActivity(intent, optionsCompat.toBundle());

            }
        });
    }
    int[] getColorFromImage(Bitmap bitmap){
     Palette palette=create(bitmap);
     Palette.Swatch vibrantcolor=palette.getLightVibrantSwatch();
        int color[]=new int[2];
     if (vibrantcolor!=null){

                color[0] =vibrantcolor.getRgb();

         View view=getView().findViewById(R.id.image_message_profile);
         view.setBackgroundColor(color[0]);

     }else{color[0]=Color.BLACK;
         Log.e("Color","It is null");
     }


     return color;
    }
    private Palette create(Bitmap bitmap){
        return Palette.from(bitmap).generate();
    }

    void loadExtraDetails(View view){
        if (!sharedPre.getString("PROFILEIMG","").equals("")){
            Glide.with(Objects.requireNonNull(getContext())).
                    applyDefaultRequestOptions(RequestOptions.noTransformation()).
                    load(sharedPre.getString("PROFILEIMG","")).
                    into(imageView);
            loadImageListner(sharedPre.getString("PROFILEIMG",""),sharedPre.getLong("COLOR",Color.BLACK));
        }

        TextView textView;

        textView=view.findViewById(R.id.last_login);
        textView.setText(R.string.today);
        textView=view.findViewById(R.id.rollnoEnter);
        textView.setText(sharedPre.getString("UNIVID",""));

        textView=view.findViewById(R.id.collegeEnter);
        textView.setText(sharedPre.getString("COLLEGEID",""));
    }
    void extractUserDetails(final View view){
        final String collegeid=sharedPre.getString("COLLEGEID","NOTFOUND");

        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("users/"+collegeid);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                view. findViewById(R.id.progressRelative).setVisibility(View.GONE);
                TextView textView=view.findViewById(R.id.rollnoEnter);
                textView.setText((String)dataSnapshot.child("ROLLNO").getValue());
                textView=view.findViewById(R.id.collegeEnter);
                textView.setText(collegeid);
                textView=view.findViewById(R.id.attendanceEnter);
               try {
                   Object att=Objects.requireNonNull(dataSnapshot).child("ATTENDANCE").getValue();
                    if (att!=null)
                   textView.setText(String.format(Locale.ENGLISH,"%.2f",att)+"%");
               } catch (Exception ignored){}
               String image=(String) dataSnapshot.child("USERIMAGE").getValue();
                if (image!=null&&!image.isEmpty()){
                   try {
                        if (dataSnapshot.child("IMAGECOLOR").getValue()==null){
                           color= Color.BLACK;
                       }else
                           color= (long) dataSnapshot.child("IMAGECOLOR").getValue();

//                       Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(image).into(imageView);
                       loadImageListner(image,color);
                   }catch (Exception e){e.printStackTrace();}

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
      void loadListeners(View view){
        view.findViewById(R.id.logoutButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutOnClicked();
            }
        });
        view.findViewById(R.id.changeImageFloat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserImage();
            }
        });
        view.findViewById(R.id.assignmentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            assignmentOnClicked();
            }
        });
        view.findViewById(R.id.editProfileButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            editProfileOnClicked();
            }
        });
        view.findViewById(R.id.changePasswordButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePasswordOnClicked();
            }
        });
        view.findViewById(R.id.feedbackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedbackOnClicked();
            }
        });
        view.findViewById(R.id.reportButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportProblemOnClicked();
            }
        });
        view.findViewById(R.id.attendanceButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AttendanceOnClicked();
            }
        });

    }

    private void changeUserImage() {
        start_image_dialog();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)

    void start_image_dialog( ){

        Log.e("Camera","Inside start_camera_dialog");
        View dialogView = View.inflate( getContext(), R.layout.settings_img_more_options, null);
        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()),R.style.Dialog1);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
         lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.BOTTOM;
        lp.windowAnimations = R.style.DialogAnimation;
        Objects.requireNonNull(dialog.getWindow()).setAttributes(lp);
        dialog.setCanceledOnTouchOutside(true);


        dialog.setContentView(dialogView);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();

                    return true;
                }

                return false;
            }
        });
        dialogView.findViewById(R.id.one).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE,CHOOSE_IMAGE);
            }
        });
        dialogView.findViewById(R.id.two).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                requestPermission(getActivity(), Manifest.permission.CAMERA,OPEN_CAMERA);
            }
        });
        dialogView.findViewById(R.id.three).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();


                if(!Constants.uid.isEmpty()) {
                    FirebaseDatabase.getInstance().getReference("users/"+Constants.uid+"/USERIMAGE")
                            .setValue("");
                    Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.noTransformation()).load("").into(imageView);
                    NavigationView navigationView =(NavigationView) getActivity().findViewById(R.id.nav_view);
                    View header=navigationView .getHeaderView(0);
                    Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load("").into((ImageView)header.findViewById(R.id.userimage));

                }
            }
        });


        dialog.show();
    }
    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select profile image"), CHOOSE_IMAGE);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);
        } else {
            if (value==102)
            {Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(intent, OPEN_CAMERA);

            }
            else  showImageChooser();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case CHOOSE_IMAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImageChooser();


                }
                break;
            }
            case OPEN_CAMERA:
                Log.e("Camera","Inside persmission result ");
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("Camera","Inside persmission result granted");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, OPEN_CAMERA);

                }
                break;
        }
    }



    private void uploadImageToFirebaseStorage(final Bitmap bitmap) {
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference("users/" + Constants.uid + ".jpg");

            final UploadTask uploadTask = profileImageRef.putFile(uriProfileImage);
              uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful())
                        throw Objects.requireNonNull(task.getException());

                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        try {
                            progressBar.setVisibility(View.GONE);
                           saveuserinformation(String.valueOf(task.getResult()),bitmap);
                            NavigationView navigationView =(NavigationView) getActivity().findViewById(R.id.nav_view);
                            View header=navigationView .getHeaderView(0);
                            Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(task.getResult()).into((ImageView)header.findViewById(R.id.userimage));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else{/* */
                    }
                }
            });

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    int   currentprogress=(int) progress;

                    Log.e("Firebase file upload",uriProfileImage+"Progress is "+progress+" curre "+currentprogress);
                    progressBar.setProgress(currentprogress);
                }
            });
        }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //requestCode == CHOOSE_IMAGE &&
        if ( resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uriProfileImage);
                Glide.with(getContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).asBitmap().load(bitmap).into(imageView);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void saveuserinformation(String url, Bitmap bitmap) {
        if (!(Constants.uid.isEmpty())) {
            FirebaseDatabase.getInstance().getReference("users/"+Constants.uid).child("USERIMAGE").setValue(url);
            int colors[]=getColorFromImage(bitmap);
            color=colors[0];
             loadImageListner(url,color);
            FirebaseDatabase.getInstance().getReference("users/"+Constants.uid).child("IMAGECOLOR").setValue(color);
             SharedPreferences.Editor edit=sharedPre.edit();
            edit.putString("PROFILEIMG",url);
            edit.putLong("COLOR",color);
            edit.apply();

        }
    }



    public void assignmentOnClicked() {
        Intent intent=new Intent(getContext(),WebLoader.class);
        intent.putExtra("LINK","http://mit.thecollegeerp.com/academic/studentarea/assingement.php");
        startActivity(intent);
    }

    public void editProfileOnClicked() {
        Intent intent=new Intent(getContext(),WebLoader.class);
        intent.putExtra("LINK","http://mit.thecollegeerp.com/academic/studentarea/myprofile.php");
        startActivity(intent);
    }

    public void changePasswordOnClicked() {
        Intent intent=new Intent(getContext(),WebLoader.class);
        intent.putExtra("LINK","http://mit.thecollegeerp.com/academic/studentarea/changepass.php");
        startActivity(intent);
    }

    public void feedbackOnClicked() {
        Intent intent  =new Intent(getActivity(),issue.class);
        intent.putExtra("FROM","Feedback");
        startActivity(intent);
     }


    public void reportProblemOnClicked() {
        Intent intent  =new Intent(getActivity(),issue.class);
        intent.putExtra("FROM","Issue");
        startActivity(intent);
    }

    public void logoutOnClicked() {
        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences("USERCREDENTIALS", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Objects.requireNonNull(getActivity()).finish();
     }

    public void AttendanceOnClicked() {
        Intent intent=new Intent(getContext(),WebLoader.class);
        intent.putExtra("LINK","http://mit.thecollegeerp.com/academic/studentarea/my_attendance.php");
        startActivity(intent);
    }
   class  loadAttendance extends AsyncTask<Void,Integer,Long>{




        @Override
        protected Long doInBackground(Void... voids) {
            Bundle bundle = new Bundle();
            bundle.putString("URL","http://mit.thecollegeerp.com/academic/studentarea/mylectureattendance.php");
            AttendanceFragment web = new AttendanceFragment();
            web.setArguments(bundle);

            FragmentTransaction fragmentTransaction1;

            fragmentTransaction1=Objects.requireNonNull(getActivity()). getSupportFragmentManager().beginTransaction();
            fragmentTransaction1.replace(R.id.attendanceFrame, web);
            fragmentTransaction1.commit();

            return null;
        }
    }
}
