package com.example.sanjay.erp.newChatScreen;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;

 import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.Query;


public class MyAdapter extends FirebaseRecyclerAdapter<album_allorders, mainactiv_allorders.FoodViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback{
    private   final int LAYOUT_DOC_REC =7 ;
    private   final int LAYOUT_ONE = 1;
    private   final int LAYOUT_TWO = 2;
    private   final int LAYOUT_THREE = 3;
    private   final int LAYOUT_FIVE = 5;
    private   final int LAYOUT_FOUR =4 ;
    private   final int LAYOUT_Doc =6 ;
    private   Context context;
    private   DownloadManager downloadManager;
    private String url,name;
    MyAdapter(Class<album_allorders> modelClass,
              int modelLayout,
              Class<mainactiv_allorders.FoodViewHolder> viewHolderClass,
              Query ref,DownloadManager downloadManager,Context context) {

         super(modelClass, modelLayout, viewHolderClass, ref  );
       this. downloadManager =downloadManager;
        this.context=context;
        Log.e("Firebase Messaging"," From adapter query is "+ref.orderByKey().limitToLast(1));
    }


    @NonNull
    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;

        if (viewType == LAYOUT_ONE) {

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent1, parent, false);
             return new  mainactiv_allorders.FoodViewHolder(view);

        } else if (viewType == LAYOUT_TWO){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent_img1, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_THREE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received1, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_FIVE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_layout, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }
        else if (viewType == LAYOUT_FOUR){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received_img1, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }else if (viewType == LAYOUT_Doc){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_sent_doc_layout, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }else if (viewType == LAYOUT_DOC_REC){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_doc_layout, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        } else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_empty, parent, false);
            return new mainactiv_allorders.FoodViewHolder(view);

        }
    }

    @Override
    public int getItemViewType(int position){

    album_allorders model = getItem(position);


try {
    if (model.getFrom().equals(Constants.uid+"_TEXT")){
        return LAYOUT_ONE;
    }else if (model.getFrom().equals(Constants.uid+"_DOC")){
        return LAYOUT_Doc;
    }else if (model.getFrom().contains("_DOC")){
        return LAYOUT_DOC_REC;
    }else if (model.getFrom().equals(Constants.uid+"_IMG")){
        return LAYOUT_TWO;
    }else if (model.getFrom().contains("_TEXT")){
        return LAYOUT_THREE;
    }else if (model.getFrom().contains("_IMG")){
        return LAYOUT_FOUR;
    }else  if (model.getFrom().contains("Date")){
        return LAYOUT_FIVE;
    }else   return 0;
 }catch (Exception ignored){}
        return position;

}

 /* */
   void checkDownload(String url,String name){
       if (requestPermission((Activity) context, Manifest.permission.WRITE_EXTERNAL_STORAGE, 102)) {
            download(url,name);
       }
       }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean requestPermission(Activity context, String permission, int value)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{permission},
                    value);

        }
        return hasPermission;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

                 if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    download(url,name);
        }


        }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void download(String url, String name) {
        Uri Download_Uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

        //Restrict the types of networks over which this download may proceed.
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        //Set whether this download may proceed over a roaming connection.
        request.setAllowedOverRoaming(false);
        //Set the title of this download, to be displayed in notifications (if enabled).
        request.setTitle(name);
        //Set a description of this download, to be displayed in notifications (if enabled)
        request.setDescription("Downloading");
        //Set the local destination for the downloaded file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir("/ERP", name);

        //Enqueue a new download and same the referenceId
        downloadManager.enqueue(request);
    }

    @Override
    protected void populateViewHolder(final mainactiv_allorders.FoodViewHolder viewHolder, final album_allorders model, final int position) {
        try { if(model.getFrom().contains("DOC")){
                    viewHolder.mView.findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.e("DownloadFile","Download requested");
                            checkDownload(name=model.getFood_Image(),url=model.getMessage());
                        }
                    });
              }else
            if (model.getFrom().contains("IMG")) {
                viewHolder.getImageView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(viewHolder.itemView.getContext(), Message_Image.class);
                        intent.putExtra("Image", model.getFood_Image());
                        intent.putExtra("Message", model.getMessage());
                        //  animation(position, viewHolder.itemView.getContext(),intent,viewHolder.itemView);
                        viewHolder.itemView.getContext().startActivity(intent);
                    }
                });
            }
        }catch (Exception ignored){
         }


        viewHolder.setMessage(model.getMessage());
        viewHolder.setMessage_Time(model.getTime());
        viewHolder.setImageMessage(viewHolder.itemView.getContext(),model.getFood_Image());
        viewHolder.setUserImage(model.getUser_Img());
        viewHolder.setDate2(model.getDate());
        viewHolder.setUsername(help_activity.UserName);
        try {
            viewHolder.setUserImage(help_activity. img);
        }  catch (Exception ignored){}
           try{

               viewHolder.setstatus(model.isSeen(),viewHolder.itemView.getContext());
           }catch (Exception ignored){}


    }



}
