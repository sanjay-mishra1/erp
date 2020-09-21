package com.example.sanjay.erp.announcement;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.sanjay.erp.R;
import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

public class ShowMedia extends Fragment {
    RecyclerView recyclerView;
    ArrayList<String> mediaList;
    ArrayList<String> DocList;
    private int currPosition=1;
    private String check;
    private int back=0;
    private GestureDetector gestureDetector = null;
    DownloadManager downloadManager;
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.image_loader,container,false);
        recyclerView=view.findViewById(R.id.recycler_view);
     //   gestureDetector = new GestureDetector(new MyGestureDetector(view));
        receiveData();
          downloadManager =(DownloadManager) getContext(). getSystemService(Context.DOWNLOAD_SERVICE);

        // query = new DownloadManager();
         /*recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return gestureDetector.onTouchEvent(motionEvent);
            }


        });*/

        return view;
    }
    boolean checkFile(){
        DocList=new ArrayList<String>();
        String  List[]={"jpg","gif","png","jpeg","mp4"};
        ArrayList<String> temp=mediaList;
        Iterator<String> iter = mediaList.iterator();

     try {
         while (iter.hasNext()) {
             String file = iter.next();

             if (!file.contains("jpg")&!file.contains("gif")&!file.contains("png")&!file.contains("jpeg")&!file.contains("mp4")) {
                 Log.e("detailAnn", "File not contains");
                 iter.remove();
                 DocList.add(file);
             }else Log.e("detailAnn","File not contains  ");
         }
     }catch (Exception e){
         e.printStackTrace();
     }
        Log.e("detailAnn","After checking mediaList=>"+mediaList+"\nDocList=>"+DocList);
        /* for (String file : mediaList) {
             if (!file.contains("jpg")|!file.contains("gif")|!file.contains("png")|!file.contains("jpeg")|!file.contains("mp4")){
                 DocList.add(file);
                temp.remove(file);
             }
         }
         mediaList=temp;*/
        return true;
    }
    private void receiveData(){

        assert this.getArguments() != null;
          mediaList=this.getArguments().getStringArrayList("MEDIALIST");

          if (mediaList.isEmpty()){
              Objects.requireNonNull(getActivity()).findViewById(R.id.imageFragment).setVisibility(View.GONE);
          }else {
              checkFile();
              if (!mediaList.isEmpty()) {
                  LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
                  mLayoutManager.scrollToPosition(0);
                  recyclerView.setLayoutManager(mLayoutManager);

                  recyclerView.setItemAnimator(new DefaultItemAnimator());
                  recyclerView.setNestedScrollingEnabled(false);
                  recyclerView.setAdapter(new Adapter());
              }else{
                  Objects.requireNonNull(getActivity()).findViewById(R.id.imageFragment).setVisibility(View.GONE);
              }
              if (!DocList.isEmpty()) {
                  RecyclerView fileRecycler = Objects.requireNonNull(getActivity()).findViewById(R.id.Documents);
                  LinearLayoutManager mLayoutManager1 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, true);

                  fileRecycler.setLayoutManager(mLayoutManager1);

                  fileRecycler.setItemAnimator(new DefaultItemAnimator());
                  fileRecycler.setNestedScrollingEnabled(false);
                  fileRecycler.setAdapter(new FileAdapter());
              }
          }
          }
    String name(String link){
        if(link!=null){
            if (!link.isEmpty()){
                link=link.substring(0,link.indexOf("?"));
                Log.e("File Name","First Change =>"+link);
                link=link.substring(link.lastIndexOf("%2F")+3);
                return link;
            }
        }
        return "";
    }
    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        View view;
        MyGestureDetector(View view){
            this.view=view;
        }
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            if (check.equals("main"))
            {
                currPosition=0;
                check="not";
                back=1;
                CakeImage_popup();
            }
            return super.onSingleTapConfirmed(e);

        }

        private void CakeImage_popup() {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            if (e1.getX() < e2.getX()) {
                if (currPosition != 0) {
                    currPosition -= 1;
                     imagePointer(currPosition,view);
                }

            } else if (e1.getX() == e2.getX()) {
                //currPosition = getVisibleViews("right");
                currPosition += 1;
            } else {
                if (currPosition < mediaList.size() - 1) {
                    currPosition += 1;
                    //    changecolorof_ImagePointer(currPosition - 1, currPosition, "No");
                    imagePointer(currPosition,view);
                }

            }
            /*try {
                if (check.equals("main"))
                    recyclerView.smoothScrollToPosition(currPosition);
                else recyclerViewTop.smoothScrollToPosition(currPosition);
            } catch (Exception IllegalArgumentException) {
            }*/

            return true;
        }



        @Override
        public boolean onDoubleTap(MotionEvent e) {

            return super.onDoubleTap(e);
        }

    }
    void imagePointer(int position,View view){
       /* int idspointer[] = {R.id.pointer1_blue, R.id.pointer2_blue, R.id.pointer3_blue, R.id.pointer4_blue, R.id.pointer5_blue, R.id.pointer6_blue, R.id.pointer7_blue, R.id.pointer8_blue, R.id.pointer9_blue, R.id.pointer10_blue,R.id.pointer11_blue};
        try {         view. findViewById(idspointer[position+1]).setVisibility(View.GONE);
            Log.e("ImagePointer","Position+1 is "+(position+1));
           view. findViewById(idspointer[position]).setVisibility(View.VISIBLE);
            view.findViewById(idspointer[position-1]).setVisibility(View.GONE);

        } catch (Exception e){
            Log.e("ImagePointer","Error Position is "+(position)+" Error is ");

        }*/
    }
    public class Adapter extends RecyclerView.Adapter<mainactiv_allorders.FoodViewHolder> {

        @NonNull
        @Override
        public mainactiv_allorders.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.imageview,parent,false);
             return new mainactiv_allorders.FoodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull mainactiv_allorders.FoodViewHolder viewHolder, final int position) {
            final String doc= (String) mediaList.get(position);

           Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.noTransformation()).load(doc).into((ImageView) viewHolder.mView.findViewById(R.id.imageview));
           viewHolder.mView.findViewById(R.id.download).setOnClickListener(new View.OnClickListener() {
               @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
               @Override
               public void onClick(View v) {
                    download(doc, name(doc));
               }
           });



        }

        @Override
        public int getItemCount() {
            return mediaList.size();
        }
    }

    public class FileAdapter extends RecyclerView.Adapter<mainactiv_allorders.FoodViewHolder> {

        @NonNull
        @Override
        public mainactiv_allorders.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.image_layout_linear,parent,false);
            return new mainactiv_allorders.FoodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull mainactiv_allorders.FoodViewHolder viewHolder, final int position) {
            final String doc= (String) DocList.get(position);

            Glide.with(Objects.requireNonNull(getContext())).applyDefaultRequestOptions(RequestOptions.circleCropTransform())
                    .load(icon(doc)).into((ImageView) viewHolder.mView.findViewById(R.id.imageId));
            Glide.with(Objects.requireNonNull(getContext())).load(getResources().getDrawable(R.drawable.download)).into((ImageView) viewHolder.mView.findViewById(R.id.uploadAction));
            viewHolder.mView.findViewById(R.id.progressbar).setVisibility(View.GONE);
            TextView textView=viewHolder.mView.findViewById(R.id.uploadText);
            final String name=name(doc);
            textView.setText(name);
            viewHolder.mView.findViewById(R.id.uploadAction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    download(doc,name);
                }
            });


        }

            Drawable icon(String name){
                Drawable drawable;
                if (name.contains(".pdf")){
                       drawable=getResources().getDrawable(R.drawable.pdf);
                }else if (name.contains(".doc")){
                    drawable=getResources().getDrawable(R.drawable.doc);
                }else if (name.contains(".xls")){
                    drawable=getResources().getDrawable(R.drawable.xls);
                }else if (name.contains(".ppt")){
                    drawable=getResources().getDrawable(R.drawable.ppt);
                }else if (name.contains(".txt")){
                    drawable=getResources().getDrawable(R.drawable.txt);
                }else  drawable=getResources().getDrawable(R.drawable.otherdoc);
                return drawable;
            }
        @Override
        public int getItemCount() {
            return DocList.size();
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
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
        //Enqueue a new download and same the referenceId
        downloadManager.enqueue(request);
    }
    }
