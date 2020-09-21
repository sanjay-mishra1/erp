package com.example.sanjay.erp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.util.Objects;

public class LoginFrontScreen extends AppCompatActivity {
    private ImageView imageview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginfrontscreen);
        imageview=findViewById(R.id.imageView11);

    }

    public void studentclicked(View view) {
           Intent intent=new Intent(this,NewSignInActivity.class);
           intent.putExtra("URL", "http://mit.thecollegeerp.com/academic/stlogin.php");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation
                        (this,imageview , "malwaIcon");
        startActivity(intent, optionsCompat.toBundle());
           //   startActivity(intent);
    }

    public void staffclicked(View view) {
        Intent intent=new Intent(this,NewSignInActivity.class);
        intent.putExtra("URL", "http://mit.thecollegeerp.com/academic/facultylogin.php");

        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                .makeSceneTransitionAnimation
                        (this,imageview , "malwaIcon");
        startActivity(intent, optionsCompat.toBundle());
//        startActivity(intent);
    }
}
