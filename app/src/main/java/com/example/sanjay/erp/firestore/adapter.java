package com.example.sanjay.erp.firestore;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sanjay.erp.R;
import com.example.sanjay.erp.newChatScreen.mainactiv_allorders;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import static android.support.v7.widget.RecyclerView.*;

/* */

public   class adapter
        <VH extends ViewHolder>
        extends Adapter<mainactiv_allorders.FoodViewHolder>
        implements EventListener<QuerySnapshot>
{
    private List<QueryDocumentSnapshot> mSnapshots;

    // ...
    public adapter(Context context, List<QueryDocumentSnapshot> mSnapshots){
    super();
    this.mSnapshots=mSnapshots;
        Log.e("Announcement", "constructor adapter... size "+mSnapshots.size()+" main <"+this.mSnapshots.size());
    }


    @Override
    public int getItemCount() {
        return 0;
    }


    @NonNull
    @Override
    public mainactiv_allorders.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent
               .getContext()).inflate(R.layout.chat_layout, parent, false);
        return new  mainactiv_allorders.FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull mainactiv_allorders.FoodViewHolder holder, int position) {
     try {
//         MainPage mainPage = doc.getDocument().toObject(MainPage.class).witId(doc_id);

         AnnouncementData data = (AnnouncementData)
                 mSnapshots.get(position).getData();
         Log.e("Announcement", "sending data to adapter..."+data);
         //holder.setUserName(data.getHEADER());
         holder.setUserName("Head");
         holder.setlast_message("Message", 0);
     }catch (Exception e){
         e.printStackTrace();
     }
    }


    private void onDocumentAdded(DocumentChange change) {
        mSnapshots.add(change.getNewIndex(), change.getDocument());
        Log.e("Announcement", String.valueOf(change.getDocument().getData()));
        notifyItemInserted(change.getNewIndex());
    }

    private void onDocumentModified(DocumentChange change) {
        if (change.getOldIndex() == change.getNewIndex()) {
            // Item changed but remained in same position
            mSnapshots.set(change.getOldIndex(), change.getDocument());
            notifyItemChanged(change.getOldIndex());
        } else {
            // Item changed and changed position
            mSnapshots.remove(change.getOldIndex());
            mSnapshots.add(change.getNewIndex(), change.getDocument());
            notifyItemMoved(change.getOldIndex(), change.getNewIndex());
        }
    }

    private void onDocumentRemoved(DocumentChange change) {
        mSnapshots.remove(change.getOldIndex());
        notifyItemRemoved(change.getOldIndex());
    }
    @Override
    public void onEvent(QuerySnapshot documentSnapshots,
                        FirebaseFirestoreException e) {

        // ...
        Log.e("Announcement", "Inside event adapter...");
        // Dispatch the event
        for (DocumentChange change : documentSnapshots.getDocumentChanges()) {
            // Snapshot of the changed document
            DocumentSnapshot snapshot = change.getDocument();
            Log.e("Announcement", String.valueOf(change.getDocument().getData()));
            switch (change.getType()) {
                case ADDED:
                    onDocumentAdded(change);
                    break;
                case MODIFIED:
                    onDocumentModified(change);
                    break;
                case REMOVED:
                    onDocumentRemoved(change);
                    break;
            }
        }

//        onDataChanged();
    }
//    public void startListening() {
//        if (mQuery != null && mRegistration == null) {
//            mRegistration = mQuery.addSnapshotListener(this);
//        }

    class holder extends RecyclerView.ViewHolder{
        public holder (View itemview){
            super(itemview);
        }
    }
    }
