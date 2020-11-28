package com.nisith.onlinelearning.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.Model.MenuItem;
import com.nisith.onlinelearning.R;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuOptionsRecyclerAdapter extends FirestoreRecyclerAdapter<MenuItem, MenuOptionsRecyclerAdapter.MyViewHolder> {

   public interface OnItemClickListener{
        void onItemClick(View view, String title, DocumentReference documentReference);
    }


    private OnItemClickListener itemClickListener;
   private String menuType; //i.e. item is a menu header options or menu subItems. I use this to set collection icon or document icon
   private Context context;

    public MenuOptionsRecyclerAdapter(@NonNull FirestoreRecyclerOptions<MenuItem> options, OnItemClickListener itemClickListener, Context context, String menuType) {
        super(options);
        this.itemClickListener = itemClickListener;
        this.context = context;
        this.menuType = menuType;
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
        if (menuType.equals(Constant.MENU_HEADER)){
            holder.fileTypeImageView.setImageResource(R.drawable.ic_collection);
        }else if (menuType.equals(Constant.MENU_ITEMS)){
            holder.fileTypeImageView.setImageResource(R.drawable.ic_document);
        }
    }




    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView titleTextView;
        Button saveButton;
        EditText updateEditText;
        ImageView editImageView, deleteImageView, fileTypeImageView;
        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.text_view);
            saveButton = itemView.findViewById(R.id.save_button);
            updateEditText = itemView.findViewById(R.id.update_edit_text);
            editImageView = itemView.findViewById(R.id.edit_image_view);
            deleteImageView = itemView.findViewById(R.id.delete_image_view);
            fileTypeImageView = itemView.findViewById(R.id.type_image_view);
            saveButton.setVisibility(View.GONE);
            updateEditText.setVisibility(View.GONE);


            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference documentReference = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                    String text = updateEditText.getText().toString();
                    if (!TextUtils.isEmpty(text)){
                        update(documentReference, text);
                        updateEditText.setVisibility(View.GONE);
                        saveButton.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(context, "enter text", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            editImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference documentReference = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                    itemClickListener.onItemClick(v, getItem(getAdapterPosition()).getTitle(),documentReference);
                    updateEditText.setText(titleTextView.getText());
                    if (updateEditText.getVisibility() == View.GONE){
                        //show edit text
                        updateEditText.setVisibility(View.VISIBLE);
                        saveButton.setVisibility(View.VISIBLE);

                    }else {
                        updateEditText.setVisibility(View.GONE);
                        saveButton.setVisibility(View.GONE);
                    }
                }
            });
            deleteImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference documentReference = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                    itemClickListener.onItemClick(v, getItem(getAdapterPosition()).getTitle(),documentReference);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DocumentReference documentReference = getSnapshots().getSnapshot(getAdapterPosition()).getReference();
                    itemClickListener.onItemClick(v, getItem(getAdapterPosition()).getTitle(),documentReference);
                }
            });
        }

        private void update(DocumentReference documentReference, String data){
            documentReference.update(Constant.TITLE, data)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateEditText.setVisibility(View.GONE);
                            saveButton.setVisibility(View.GONE);
                            if (task.isSuccessful()){
                                Toast.makeText(context, "Save Successfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(context, "Not save. Something went wrong...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }


    }

}
