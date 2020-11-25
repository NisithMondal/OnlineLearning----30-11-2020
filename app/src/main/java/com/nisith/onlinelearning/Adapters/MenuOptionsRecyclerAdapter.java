package com.nisith.onlinelearning.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentReference;
import com.nisith.onlinelearning.Model.MenuItem;
import com.nisith.onlinelearning.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuOptionsRecyclerAdapter extends FirestoreRecyclerAdapter<MenuItem, MenuOptionsRecyclerAdapter.MyViewHolder> {

   public interface OnItemClickListener{
        void onItemClick(String title, DocumentReference documentReference);
    }


    private OnItemClickListener itemClickListener;

    public MenuOptionsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MenuItem> options, OnItemClickListener itemClickListener) {
        super(options);
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_appearence_for_drawer_heading, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull MenuItem model) {
        String title = model.getTitle();
        holder.titleTextView.setText(title);
    }



    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference documentReference = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                    itemClickListener.onItemClick(getItem(getAdapterPosition()).getTitle(),documentReference);
                }
            });
        }
    }

}
