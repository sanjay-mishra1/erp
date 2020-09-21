package com.example.sanjay.erp.filesClasses;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.announcement.make_announcement;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

class UploadFiles {
    private final HashMap<FilesModel, String> files;
    private   TextView textViewFileName;
    private Uri fileLoc;
    private  String username;
     private ProgressBar progressBar;
    private TextView uploadDescription;
    private ImageButton cancelButoon;
    private Context context;
    private boolean isFailed=false;
    private String link;
    private boolean stopUploading=false;
    private FilesModel filesModel;
    private FileAdapter adapter;
    private int position;
    private String place;

    UploadFiles(FilesModel filesModel, Context context,
                Uri fileUrl, ProgressBar progressBar,
                TextView uploadDescription,
                ImageButton cancelButton,
                HashMap<FilesModel, String> files,
                FileAdapter adapter, int position,TextView textViewFileName,String place) {
        this.fileLoc = fileUrl;
        this.textViewFileName=textViewFileName;
         this.username = Constants.uid;
        this.context=context;
        this.position=position;
        this.files=files;
        this.adapter=adapter;
        this.filesModel=filesModel;
         this.progressBar = progressBar;
        this.cancelButoon=cancelButton;
        this.place=place;
        this.uploadDescription = uploadDescription;
        Glide.with(context).load(R.drawable.cancel).into(cancelButoon);
        uploadDescription.setText(R.string.uploading);
        progressBar.setVisibility(View.VISIBLE);
        storeFile();
    }
    private void alertForCancel(String message, final UploadTask uploadTask, final boolean i){
        final AlertDialog.Builder alertDialog=
                new AlertDialog.Builder(new ContextThemeWrapper(context,R.style.AlterDialogTheme));
        alertDialog.setMessage(message);
         alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                     action(uploadTask, i);
            }
        });
        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             }
        });
        AlertDialog alertDialog1=  alertDialog.create();
        alertDialog1.show();
    }
    private void action(UploadTask uploadTask, boolean i){
        if (!i){
            uploadDescription.setText(R.string.deleted);
            Glide.with(context).load(R.drawable.retry).into(cancelButoon);
            FirebaseStorage.getInstance()
                    .getReferenceFromUrl(String.valueOf(link)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e("UploadFiles", "File deleted...");
                    isFailed = true;
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            files.remove(filesModel);
                            make_announcement.contentList.remove(link);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyDataSetChanged();
                        }
                    },2000);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context,"Failed to delete the file...",Toast.LENGTH_SHORT).show();
                    Log.e("UploadFiles", "File failed to delete..."+e.getMessage());
                    e.printStackTrace();

                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(context,"Failed to delete the file...",Toast.LENGTH_SHORT).show();
                    Log.e("UploadFiles", "File cancelled to delete...");
                }
            });

        }else{
            stopUploading=true;
            uploadDescription.setText("Cancelling...");
           // Glide.with(context).load(R.drawable.retry).into(cancelButoon);
           // uploadDescription.setText("Cancelled");
           // progressBar.setVisibility(View.GONE);
            Log.e("UploadFiles", "File cancel requested...");
            uploadTask.cancel();
        }
    }
    private String name(String link){
        if(link!=null){
            if (!link.isEmpty()){
                if(link.contains("%3A"))
                    link=  link.substring(link.lastIndexOf("%3A")+3);
                if(link.contains("%2F"))
                    link=  link.substring(link.lastIndexOf("%2F")+3);
               try {
                   link=link.replace("%20","_");
               }catch (Exception e){
                   e.printStackTrace();
               }
                return link;
            }
        }
        return "";
    }
    private void storeFile(){
        String name=name(fileLoc.toString());
         textViewFileName.setText(name);
        final StorageReference mStorageRef = FirebaseStorage.
                getInstance().getReference().child(place+"/"+username+"/"+name);
          final UploadTask uploadTask = mStorageRef.putFile(fileLoc);

        cancelButoon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFailed) {
                    Glide.with(context).load(R.drawable.cancel).into(cancelButoon);
                    uploadDescription.setText(R.string.uploading);
                    progressBar.setVisibility(View.VISIBLE);
                    isFailed=false;
                    stopUploading=false;
                    storeFile();
                } else {
                    if (uploadTask.isInProgress()) {
                        alertForCancel(context.getString(R.string.cancel_upload), uploadTask,true);

                    } else {
                        Log.e("UploadFiles","File delete requested...");

                         alertForCancel(context.getString(R.string.delete_upload), uploadTask,false) ;
                            Log.e("UploadFiles", "File delete process alert said..."+true);



                    }
                }
            }
        });

        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful())
                    throw Objects.requireNonNull(task.getException());

                return mStorageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()){
                    try {
                        link= String.valueOf(task.getResult());
                       if (stopUploading){
                           action(uploadTask,false);
                       }else {
                           make_announcement.contentList.add(link);
                           Log.e("Firebase file upload", "contentList data are " + make_announcement.contentList);
                           uploadDescription.setText(R.string.uploaded);
                           progressBar.setVisibility(View.GONE);
                       }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }else{

                try {
                    Glide.with(context).load(R.drawable.retry).into(cancelButoon);

                    uploadDescription.setText(R.string.failed);
                    progressBar.setVisibility(View.GONE);
                    isFailed = true;
                }catch (Exception ignored){}
                }
            }
        });
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
               double progress=(100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                int   currentprogress=(int) progress;

               Log.e("Firebase file upload",fileLoc+"Progress is "+progress+" curre "+currentprogress);
               progressBar.setProgress(currentprogress);
            }
        });

    }

}
