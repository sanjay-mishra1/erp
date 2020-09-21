package com.example.sanjay.erp.announcement;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sanjay.erp.R;
import com.example.sanjay.erp.newChatScreen.album_allorders;
import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;

import java.util.List;
import java.util.Locale;

import static com.example.sanjay.erp.announcement.make_announcement.announcementSet;
import static com.example.sanjay.erp.announcement.make_announcement.manuallyAnnouncementSet;


public class FilterAdapter extends RecyclerView.Adapter<mainactiv_allorders.FoodViewHolder>{

    private static final int FIRST_VIEW = 1;
    private final boolean isAuto;
    private List<album_allorders> list;
    private Context context;
    private String startLimit;
    private String endLimit;
    private TextView filterResult;
    private int totalRecord;
     FilterAdapter(List<album_allorders> list, Context context, String startFilter, String endFilter, TextView filterResult, int totalFilter,boolean isAuto) {
        this.list = list;
        this.context = context;
        this.startLimit=startFilter;
        this.endLimit=endFilter;
        totalRecord=totalFilter;
        this.isAuto=isAuto;
        this.filterResult=filterResult;
     }
  private boolean filter(String startLimit, String endLimit, String value, String collegeid){
            if (value.compareTo(startLimit) < 0 || value.compareTo(endLimit) > 0) {
                Log.e("FilterAnnouncement",
                        "out of the range...start"+
                                startLimit+"end"+endLimit+"value"+value+"collegeid"+collegeid);
                 announcementSet.remove(collegeid);
                 return false;

            }else{
                Log.e("FilterAnnouncement",
                        "within the range...start"+
                                startLimit+"end"+endLimit+"value"+value+"position"+collegeid);

            }
      return true;
  }
    @NonNull
    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType==0){
            view = LayoutInflater.from(context).inflate(R.layout.item_message_empty,parent,false);
       }else{
            view = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false);
       }
        return new mainactiv_allorders.FoodViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        album_allorders model = list.get(position);
        if (isAuto){
        if (filter(startLimit,endLimit,model.getSEARCHKEY().toString(),model.getUSERID()))
        return FIRST_VIEW;
        else
            return 0;
        }else return FIRST_VIEW;
    }

    @Override
    public void onBindViewHolder(@NonNull mainactiv_allorders.FoodViewHolder viewHolder, final int position) {
try {
    final album_allorders model = list.get(position);

    Log.e("FilterAnnouncement", "sending data to adapter..." + list.get(position));
    viewHolder.setImage(model.getUSERIMAGE(), context);

    viewHolder.setId(model.getSEARCHKEY());
    viewHolder.setUserName(model.getUSERNAME());
    viewHolder.mView.findViewById(R.id.cancel_action).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (!isAuto)
               manuallyAnnouncementSet.remove(model.getUSERID());
          else announcementSet.remove(model.getUSERID());
            notifyItemRemoved(position);
            notifyDataSetChanged();
            list.remove(model);
        }
    });
}catch (Exception ignored){

}

    }

    @Override
    public int getItemCount() {

        int arr = 0;

        try {
            if (list.size() == 0) {

                if (!isAuto) {
                    filterResult.setVisibility(View.GONE);
                }
            } else {
                arr = list.size();
                filterResult.setVisibility(View.VISIBLE);

            }
            if (isAuto) {
                if (!FragmentFilterResult.isNumber) {
                    try {
                        filterResult.setText(String.format(Locale.ENGLISH, "%s %d out of %d Records",
                                context.getString(R.string.foundresult), announcementSet.size(), totalRecord));
                    } catch (Exception ignored) {
                    }

                } else {
                    try {


                        filterResult.setText(String.format(Locale.ENGLISH, "%s %d Records",
                                context.getString(R.string.foundresult), announcementSet.size()));
                    } catch (Exception ignored) {
                    }
                }

            }
            }catch(Exception ignored){
            }

        return arr;

    }


}