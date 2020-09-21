package com.example.sanjay.erp.filesClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.newChatScreen.album_allorders;
import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter
                <mainactiv_allorders.FoodViewHolder>{
    private Object img;
    private Context context;
    private byte fileType;
//    private ArrayList tempFile;
    private HashMap<FilesModel,String> files;
    String place;
    public FileAdapter(Context context, byte fileType, HashMap<FilesModel,String> files, Object img,String place) {
        this.context = context;
        this.fileType = fileType;
        this.files = files;
        this.img=img;
        this.place=place;
    }

    public FileAdapter(Context applicationContext, byte fileType, ArrayList<FilesModel> tempList) {
        this.context = applicationContext;
        this.fileType = fileType;
//        this.tempFile = tempList;

    }

    @NonNull
    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.image_layout_linear,parent,false);
        return new mainactiv_allorders.FoodViewHolder( view);
    }

    @Override
    public void onBindViewHolder(@NonNull mainactiv_allorders.FoodViewHolder holder, int position) {
        Log.e("MediaAnn","Inside onBindViewHolder "+files+" templist is ");
        String status;

      try {ArrayList<FilesModel> arrayList=new ArrayList<FilesModel>(files.keySet());
          FilesModel filesModel = arrayList.get(position);
          Log.e("MediaAnn","Inside onBindViewHolder "+files+" arraylist is  "+arrayList+" img is "+img);

           status = files.get(filesModel);
           String file=filesModel.getLink().toString().toLowerCase();
          if (!file.contains("jpg")&!file.contains("gif")&!file.contains("png")&!file.contains("jpeg")&!file.contains("mp4")) {
              Glide.with(context).applyDefaultRequestOptions
                      (RequestOptions.circleCropTransform()).load(icon(file)).into
                      ((ImageView)holder.mView.findViewById(R.id.imageId));

          }else {

              Glide.with(context).applyDefaultRequestOptions
                      (RequestOptions.circleCropTransform()).load(filesModel.getLink()).into
                      ((ImageView) holder.mView.findViewById(R.id.imageId));
          }
        switch (status)
        {
            case "false":
                Log.e("MediaAnn","Inside onBindViewHolder false "+files);
              new  UploadFiles(filesModel,context,filesModel.getLink(),
                      (ProgressBar) holder.mView.findViewById(R.id.progressbar),
                      (TextView) holder.mView.findViewById(R.id.uploadText),
                      (ImageButton) holder.mView.findViewById(R.id.uploadAction),
                      files,FileAdapter.this,position,(TextView) holder.mView.findViewById(R.id.uploadFileName),place);
                      break;
            default:
                Log.e("MediaAnn","Inside onBindViewHolder 'other' " +
                        files+" \nstatus"+status);

        }
      }catch (Exception e){
          Log.e("MediaAnn","Error "+files);
          e.printStackTrace();

      }
    }
    Drawable icon(String name){
        Drawable drawable;
        if (name.contains(".pdf")){
            drawable=context. getResources().getDrawable(R.drawable.pdf);
        }else if (name.contains(".xls")){
            drawable=context.getResources().getDrawable(R.drawable.xls);
        }else if (name.contains(".ppt")|name.contains(".pptx")){
            drawable=context.getResources().getDrawable(R.drawable.ppt);
        }else if (name.contains(".txt")){
            drawable=context.getResources().getDrawable(R.drawable.txt);
        }else if (name.contains(".doc")|name.contains(".docx")){
            drawable=context.getResources().getDrawable(R.drawable.doc);
        }
        else  drawable=context.getResources().getDrawable(R.drawable.otherdoc);
        return drawable;
    }

    @Override
    public int getItemCount() {
        int arr=0;
        try{
            if (files.size() != 0) {
                arr=files.size();
            }
        }catch (Exception ignored){

        }
        return arr;
    }
}
