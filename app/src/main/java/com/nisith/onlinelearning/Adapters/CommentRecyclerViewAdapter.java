package com.nisith.onlinelearning.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.Model.Comment;
import com.nisith.onlinelearning.Model.QuestionAnswer;
import com.nisith.onlinelearning.Model.User;
import com.nisith.onlinelearning.R;
import com.squareup.picasso.Picasso;

import java.util.ConcurrentModificationException;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentRecyclerViewAdapter extends FirestorePagingAdapter<Comment, CommentRecyclerViewAdapter.MyViewHolder> {

    public interface OnLoadingStateChangeListener{
        void onLoadingStateChange(String state);
    }

    private CollectionReference usersRootCollectionRef;
    private String currentUserId;
    private OnLoadingStateChangeListener loadingStateChangeListener;

    public CommentRecyclerViewAdapter(@NonNull FirestorePagingOptions<Comment> options, OnLoadingStateChangeListener loadingStateChangeListener) {
        super(options);
        usersRootCollectionRef = FirebaseFirestore.getInstance().collection(Constant.USERS);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        this.loadingStateChangeListener = loadingStateChangeListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_appearence_for_comments, parent, false);
        return new MyViewHolder(rootView);
    }


    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull Comment comment) {
        String commentMessage = comment.getCommentMessage();
        String date = comment.getDate();
        holder.commentMessageTextView.setText(commentMessage);
        holder.dateTextView.setText(date);
        String userId = comment.getUserId();
        if (userId != null){
            fetchUserDataFromServer(userId, holder);
        }

    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        super.onLoadingStateChanged(state);
        switch (state){
            case LOADING_INITIAL:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_START);
                break;
            case FINISHED:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_FINISH);
                break;
            case ERROR:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_ERROR);
                break;
//            case LOADED:
//                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_FINISH);
//                break;

            case LOADING_MORE:
                loadingStateChangeListener.onLoadingStateChange(Constant.DATA_LOADING_START);

        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        CircleImageView profileImageView;
        TextView profileNameTextView, commentMessageTextView, dateTextView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profile_image_view);
            profileNameTextView = itemView.findViewById(R.id.profile_name_text_view);
            commentMessageTextView = itemView.findViewById(R.id.comment_message_text_view);
            dateTextView = itemView.findViewById(R.id.date_text_view);
        }
    }


    private void fetchUserDataFromServer(final String userId, final MyViewHolder holder){
        usersRootCollectionRef.document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            User user = documentSnapshot.toObject(User.class);
                            if (user != null){
                                String userName = user.getUserName();
                                String profileImageUrl = user.getProfileImageUrl();
                                Picasso.get().load(profileImageUrl).fit().centerCrop().placeholder(R.drawable.default_user_icon)
                                        .into(holder.profileImageView);
                                if (currentUserId != null&& currentUserId.equals(userId)) {
                                    //means same user
                                    holder.profileNameTextView.setText("Your Comment");
                                }else {
                                    holder.profileNameTextView.setText(userName);
                                }

                            }
                        }
                    }
                });
    }

}
