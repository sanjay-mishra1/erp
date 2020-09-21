package com.example.sanjay.erp.database;

 import android.content.Context;
 import android.content.SharedPreferences;
 import android.graphics.Bitmap;
 import android.graphics.BitmapFactory;
 import android.net.Uri;
 import android.os.Bundle;
 import android.os.Environment;
 import android.os.StrictMode;
 import android.support.annotation.NonNull;
 import android.support.design.widget.NavigationView;
 import android.support.v4.app.FragmentManager;
 import android.support.v4.app.FragmentTransaction;
 import android.util.Log;
 import android.view.View;
 import android.webkit.WebView;
 import android.widget.ImageView;
 import android.widget.Toast;

 import com.bumptech.glide.Glide;
  import com.bumptech.glide.request.RequestOptions;
 import com.example.sanjay.erp.Constants;
  import com.example.sanjay.erp.R;
 import com.example.sanjay.erp.firestore.AnnouncementData;
 import com.example.sanjay.erp.webFrame.Nav_Header;
 import com.google.android.gms.tasks.Continuation;
 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

 import java.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
 import java.io.InputStream;
 import java.io.OutputStream;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;
 import java.net.URISyntaxException;
 import java.net.URL;
 import java.text.DecimalFormat;
 import java.util.HashMap;
import java.util.Map;
 import java.util.Objects;
 import java.util.Random;

 import static android.content.Context.MODE_PRIVATE;
 import static android.support.constraint.Constraints.TAG;
 import static com.example.sanjay.erp.splashActivity.MYPREFERENCES;

public class store  {


    public store( ){
    storeDataRealtime(Constants.uid,Constants.type);
}
    public store(String[] data, Context context, ImageView imageView){
        checkUser(data,context,imageView);

    }

    public store(String[] data, String img) {
        storeImage(data,img);
    }

    private void checkUser(final String[] data, final Context context, final ImageView imageView) {
        final SharedPreferences sharedPreferences=context.getSharedPreferences("USERCREDENTIALS",MODE_PRIVATE);

         Glide.with(context).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).
                 load(sharedPreferences.getString("PROFILEIMG",""))
                .into(imageView);
        final DatabaseReference databaseReference =FirebaseDatabase.getInstance()
                .getReference("users/"+data[0]);
        Log.e("CheckUser","inside....checking............................");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()||dataSnapshot.hasChildren()||dataSnapshot.child("USERNAME").getValue()!=null){
                    databaseReference.child("LASTONLINE/"+System.currentTimeMillis());
                    Log.e("CheckUser","User found");
                    String img=(String) dataSnapshot.child("USERIMAGE").getValue();
                    if (img!=null)
                    {   Log.e("UserImage",img);
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putString("PROFILEIMG",img);
                        editor.apply();
                        Glide.with(context).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).
                                load(img)
                                .into(imageView);
                      }
                }else{int year=110000;
                    try {
                        year= Integer.parseInt(data[0].replaceAll("[^0-9]","").substring(0,2));
                    }catch (Exception e){e.printStackTrace();}
                    Log.e("CheckUser","User not found");

                    storeDataRealtime(data[1],data[0],data[2],2000+year,"",data);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }






     public store(AnnouncementData data, long date, Context context){
        storeData(data,date,context);
    }
        /**Into firestore**/
    private void storeData(AnnouncementData data, long date, final Context context) {
        // Add a new document with a generated ID
        FirebaseFirestore db=FirebaseFirestore.getInstance();
        db.collection("announcement")
                .document(String.valueOf(date))
                .set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context,"Announcement created successfully",Toast.LENGTH_SHORT).show();
              //  ((MultiDexApplication)context).finish();
                 Log.e("StoringAnnouncment","stored successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("StoringAnnouncment","stored successfully");
            }
        });

    }



    private void storeDataRealtime( String  id, String   type) {
        Map<String, Object> user = new HashMap<>();
         user.put("USERID", id);
        user.put("TYPE", type);

        storeToDatabase(user,new StringBuilder(id));

    }


    private void storeDataRealtime(String  username, String  id, String   type, int year, String image,String extData[]) {
         Map<String, Object> user = new HashMap<>();
        user.put("USERNAME", username);
        user.put("USERID", id);
        user.put("TYPE", type);
        user.put("USERIMAGE", image);
        user.put("YEAR", year);
        user.put("ROLLNO",extData[3]  );
        user.put("SECTION",extData[5]  );
        user.put("DOB",extData[4]  );
        storeToDatabase(user,new StringBuilder(id));

    }



    private void storeImage(final String data[], String image) {
        final StorageReference mStorageRef = FirebaseStorage.getInstance().getReference().child("Images/"+data[0]+".jpg");
       Uri file;
        UploadTask uploadTask=null;

        try {save(image);
//          file= Uri.fromFile(new File(String.valueOf(image)));
//          uploadTask = mStorageRef.putFile(file);

                uploadTask = mStorageRef.putFile(Uri.parse(storeImageInStorage(image)));

        }catch (Exception e){
               e.printStackTrace();
          }



       try {
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
                       int year=10000;
                       try {
                         year= Integer.parseInt(data[0].replaceAll("[^0-9]","").substring(0,2));

                       }catch (Exception e){
                           e.printStackTrace();
                       }
                       String link="";
                       try {
                          link= String.valueOf(task.getResult());
                       }catch (Exception e){}
                           storeDataRealtime(data[1],data[0],data[2],year,link,data);



                   }
               }
           });



       }catch (Exception e){
           e.printStackTrace();
       }



    }
    String storeImageInStorage(String src) throws IOException {
        URL url = new URL (src);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        InputStream input = url.openStream();
        File storagePath;
        try {
//The sdcard directory e.g. '/sdcard' can be used directly, or
//more safely abstracted with getExternalStorageDirectory()
             storagePath = Environment.getExternalStorageDirectory();
            OutputStream output = new FileOutputStream( "/ERP/profile.png");
            try {
                byte[] buffer = new byte[20000];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                    output.write(buffer, 0, bytesRead);
                }
            } finally {
                output.close();
            }
        } finally {
            input.close();
        }
        Log.e("storeImage","Inside store in storage "+ "/ERP/profile.png");
        return  "/ERP/profile.png";
    }
    void save(String src) throws IOException {
        FileOutputStream outStream = null;
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/ERP");
        dir.mkdirs();
        String fileName = String.format("%d.jpg", System.currentTimeMillis());
        File outFile = new File(dir, fileName);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        java.net.URL url = new java.net.URL(src);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
//         Bitmap bitmap=imageView.getDrawingCache(false);
        outStream = new FileOutputStream(outFile);
        try {
            myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        }catch (Exception ignored){}
        Log.e("storeImage","Image saved");
        outStream.flush();
        outStream.close();
     }

    private InputStream downloadImage(String src){

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                java.net.URL url = new java.net.URL(src);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return input;
            } catch (IOException e) {
                e.printStackTrace();
                return null;

        }
    }


    private void storeToDatabase(Map<String, Object> data, StringBuilder id){
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    db.child("users")
            .child(id.toString()).setValue(data).addOnSuccessListener(new OnSuccessListener<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            Log.d(TAG, "DocumentSnapshot added with ID: ");

        }
    }).addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception e) {
            Log.w(TAG, "Error adding document", e);

        }
    });

}
private void createDummyDb(){
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    Map<String,Object>user=new HashMap<String, Object>();
    String name[]={
            "Sanjay","Ram","Shyam","Mohan","Kapil",
            "Ramesh","Shikha","Gita","Rakesh",
            "Lakhan","Rahul"
    };
    String Surname[]={
            "Khanna","Sha","Khan","Sinha",
            "Mishra",
            "Bhatt",
            "Sharma",
            "kapoor",
            "Saha",
            "Singh",
            "jaiswal"
    };
    user.put("TYPE", "Student");
    user.put("USERIMAGE","" );

    for (int i=67;i<88;i++){
        Random random=new Random();
        double randomMarks=(double) random.nextInt(100);
         double randomAtte=0.99+(99.00)* random.nextDouble();
        DecimalFormat form=new DecimalFormat("0.00");

        user.put("ATTENDANCE",Double.valueOf( form.format( randomAtte)));
        user.put("MARKS",Double.valueOf(form.format( randomMarks))+0.0);
        user.put("USERNAME", name[random.nextInt(11)]+" "+Surname[random.nextInt(11)]);
        user.put("USERID", "CS162020"+i);

        user.put("YEAR", 2016);
        user.put("ROLLNO", "0821CS1610"+(i+1));

        db.child("users")
                .child("CS162020"+i).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot added with ID: ");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);

            }
        });
    }


}

}
