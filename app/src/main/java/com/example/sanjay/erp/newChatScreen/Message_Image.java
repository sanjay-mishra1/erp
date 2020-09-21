package com.example.sanjay.erp.newChatScreen;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.util.prefs.Preferences;

public class  Message_Image extends Activity {
    private String Message;
    private String Img;
    ImageView imageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_display_image);
        Receive();
         imageView=findViewById(R.id.message_body_img);
        Glide.with(getApplicationContext()).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(Img).into(imageView);
        TextView textView=findViewById(R.id.text_message_name);
        textView.setText(Message);

    }
    void Receive(){
        try {
            Intent intent=getIntent();
            Img=intent.getStringExtra("Image");
            Message=intent.getStringExtra("Message");
        }catch (Exception e){e.printStackTrace();}

    }

    public void onclick_Download(View view) throws IOException {
             requestPermission(this);

    }



    public static final int REQUEST_WRITE_STORAGE = 112;

    private void requestPermission(Activity context)  {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            // You are allowed to write external storage:
         //   String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TheCanteen";
          //  File storageDir = new File(path,Img);

            save();
        }
    }
    void save()  {
        String name=System.currentTimeMillis()+".png";
        Uri Download_Uri = Uri.parse(Img);
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
        DownloadManager downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);
        Toast.makeText(this,"Saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Preferences.MAX_VALUE_LENGTH: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //  requestPermission(this);
                    save();
                    Toast.makeText(this, "The app was allowed to write to your storage!", Toast.LENGTH_LONG).show();
                    // Reload the activity with permission granted or use the features what required the permission
                } else {
                    Toast.makeText(this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    public void ONclickShare(View view)
    {   if(Constants.isUserBlocked=='f') {
        Intent intent = new Intent(this, image_zoomer.class);
        intent.putExtra("Image", (Img));
        intent.putExtra("From", "Message_Image");
        startActivity(intent);
        finish();
    }else Toast.makeText(this,"Follow user to send image",Toast.LENGTH_SHORT).show();
    }

    public void backtomessage(View view) {
        onBackPressed();
    }
}
