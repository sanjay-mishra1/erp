package com.example.sanjay.erp.ExtraClasses;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.Constants;
import com.example.sanjay.erp.R;
 import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;


public class issue extends AppCompatActivity {
    private   final int CHOOSE_IMAGE = 101;

    Uri uriProfileImage;
    ProgressBar progressBar;

     private long issueid;
        String from;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.issue);
        TextView textView=findViewById(R.id.title);
        textView.setText("Report Issue");
        issueid=System.currentTimeMillis();
        progressBar=findViewById(R.id.progressbar);
        loadUserData();
    }
    void loadUserData(){
    Intent intent=getIntent();
    from=intent.getStringExtra("FROM");
    TextView textView=findViewById(R.id.title);
    textView.setText(from);

    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSE_IMAGE);
    }
    private void uploadImageToFirebaseStorage() {
        //TextView textView=findViewById(R.id.foodname);
        StorageReference profileImageRef =
                //FirebaseStorage.getInstance().getReference("food/"+editText.toString().trim() + System.currentTimeMillis() + ".jpg");
                FirebaseStorage.getInstance().getReference(from+"/"+issueid );

        if (uriProfileImage != null) {
            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
//                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText( issue.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView=findViewById(R.id.userimage);
        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);
                 uploadImageToFirebaseStorage();
             } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void uploadImageClicked(View view) {
        showImageChooser();
    }


    public void SendonClicked(View view) {
        save();
    }

    private void save() {
        EditText issue=findViewById(R.id.issue);
        if (issue.getText().toString().trim().isEmpty()){

            issue.setError("Please share your "+from.toLowerCase());
            issue.requestFocus();
            return;
        }
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Messages/"+from.toLowerCase()+"/"+issueid);
        databaseReference.child("ISSUEID").setValue(String.valueOf(issueid));
      if (Constants.uid.isEmpty())
      {
          Constants.uid=getSharedPreferences(MYPREFERENCES,MODE_PRIVATE).getString("COLLEGEID","not found");
      }
          databaseReference.child("UID").setValue(Constants.uid);

        databaseReference.child("Date").setValue( DateFormat.getDateTimeInstance().format(new Date()));
        databaseReference.child("Message").setValue(issue.getText().toString().trim());

        finish();
    }

    public void onclickback(View view) {
        finish();
    }
}
