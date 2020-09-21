package com.example.sanjay.erp.newChatScreen;


import android.content.Context;
 import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
 import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
 import android.widget.ImageView;
 import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
 import com.bumptech.glide.request.RequestOptions;
 import com.example.sanjay.erp.R;
  import com.google.firebase.database.DatabaseReference;



public class mainactiv_allorders   {







    public static class FoodViewHolder extends RecyclerView.ViewHolder{
        public View mView;

        public FoodViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }



public void setlast_message(String last_message,long counter)throws NullPointerException {

  TextView  text1=(TextView) mView.findViewById(R.id.usermessage);
  if (counter>0){
      text1.setTextColor(mView.getResources().getColor(R.color.colorPrimary));
  }
    text1.setText(last_message);
      }
          void ExtractChatMessage(String chat,TextView message,TextView  name,TextView time) {
            String data[] = chat.split("```start```");
            for (int i = 0; i < data.length; i++) {
                System.out.println("new Sorted data is " + data[i]);

            }
            message.setText(data[0]);
            name.setText(data[1]);
            time.setText(data[2]);

        }
        public void setTime1(String time) {
            try {


                TextView food_name = (TextView) mView.findViewById(R.id.usertime);
                food_name.setText(time);
            }catch (Exception ignored){}
        }
        public TextView setUser_name(String user_name)throws NullPointerException{
            TextView text=(TextView) mView.findViewById(R.id.username);
            text.setText(user_name);
            return text;
        }

        void setMessage_counter(long message_counter) {
            TextView textView=mView.findViewById(R.id.Unread_messages);

            try {
                 {
                    if (message_counter>(0)) {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText (String.valueOf(message_counter));
                    }
                }
            }catch (Exception ignored){

            }
        }

        public ImageView setImage (String img, Context ctx ) {
            ImageView food_img=null;
    try {


             food_img = (ImageView) mView.findViewById(R.id.img1);
            TextView textView = mView.findViewById(R.id.imglink);

            if (img != null) {

                food_img.setVisibility(View.VISIBLE);
                textView.setText(img);
                Glide.with(ctx).applyDefaultRequestOptions(RequestOptions.circleCropTransform()).load(img).into(food_img);

            }
//            else textView.setText("");
        }catch (Exception ignored){

        }
            return food_img;
        }
        public void setUserName(String Name){
            try {
                TextView textView=mView.findViewById(R.id.username);
                textView.setText(Name);
            }catch (Exception ignored){}

        }



        public void setDate2(String time){
            try {
                TextView textView=mView.findViewById(R.id.date);
                 textView.setText(time);
            }catch (Exception ignored){}

        }
        public void setId(String id){
            try {
                TextView textView=mView.findViewById(R.id.usermessage);
                textView.setText(id);
            }catch (Exception ignored){}

        }
        public void setId(Object id){
            try {
                 TextView textView=mView.findViewById(R.id.usermessage);
                 textView.setText(id.toString());
            }catch (Exception ignored){}

        }
        public void setMessage(String Message){
            try {
                TextView textView=mView.findViewById(R.id.text_message_body);


                try {

                    if (!Message.equals(""))
                    {  textView.setText(Message);
                        textView.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    textView.setText("");
                }
            }catch (Exception ignored){}
        }
        public void setMessage_Time(String time){
            try {
                TextView textView=mView.findViewById(R.id.text_message_time);
                // time=time.substring(time.indexOf("||")+3,time.length());
                textView.setText(time);
            }catch (Exception ignored){}

        }
        public void setImageMessage(Context ctx,  String image) {
            try {
                ImageView food_image=(ImageView) mView.findViewById(R.id.message_body_img);
                //Picasso.with(ctx).load(image).into(food_image);
                // Picasso.get().load(image).into(food_image);
                Glide.with(ctx).load(image).into(food_image);

            }catch (Exception ignored){}

        }
        public void setUsername(String username) {
            try{
                TextView textView=mView.findViewById(R.id.text_message_name);
                textView.setText(username);
            }catch (Exception ignored){}
        }
        public void setUserImage(String img ) {
            try{
                if (img!=null)
                Glide.with(itemView.getContext()).load(img).apply(RequestOptions.circleCropTransform()).into((ImageView) mView.findViewById(R.id.image_message_profile));

            }catch (Exception e){
             try {


                 Glide.with(itemView.getContext()).load(R.drawable.user).into((ImageView) mView.findViewById(R.id.image_message_profile));
             }catch (Exception ignored){}
            }
        }

        public ImageView getImageView() {
            return mView.findViewById(R.id.message_body_img);
        }
        public void setstatus(boolean status,Context context) {
            if (!status){
                Glide.with(context).applyDefaultRequestOptions(RequestOptions.noAnimation()).applyDefaultRequestOptions(RequestOptions.skipMemoryCacheOf(true))
                        .load(R.drawable.sent).into((ImageView)
                        itemView.findViewById(R.id.status));
            }
        }


    }



    }




