package com.nisith.onlinelearning.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nisith.onlinelearning.Adapters.CommentRecyclerViewAdapter;
import com.nisith.onlinelearning.Adapters.HomeFragmentRecyclerViewAdapter;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.HomeActivity;
import com.nisith.onlinelearning.Model.Comment;
import com.nisith.onlinelearning.Model.QuestionAnswer;
import com.nisith.onlinelearning.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;


public class CommentFragment extends Fragment implements HomeActivity.OnFragmentDataCommunicationListener, CommentRecyclerViewAdapter.OnLoadingStateChangeListener {

    private TextView headingTextView;
    private RecyclerView commentRecyclerView;
    private EditText commentEditText;
    private ImageButton commitButton;
    private ProgressBar progressBar;
    private CollectionReference rootCollectionReference;
    private String currentUserId;
    private String menuHeaderDocumentId, menuItemDocumentId;
    private CommentRecyclerViewAdapter recyclerViewAdapter;
    private CollectionReference rootCollectionRef;


    public CommentFragment(String menuHeaderDocumentId, String menuItemDocumentId) {
        // Required empty public constructor
        this.menuHeaderDocumentId = menuHeaderDocumentId;
        this.menuItemDocumentId = menuItemDocumentId;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
//        headingTextView = view.findViewById(R.id.heading_text_view);
        commentRecyclerView = view.findViewById(R.id.comment_recycler_view);
        commentEditText = view.findViewById(R.id.comment_edit_text);
        commitButton = view.findViewById(R.id.commit_button);
        progressBar = view.findViewById(R.id.progress_bar);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        rootCollectionRef = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        progressBar.setVisibility(View.GONE);
        setupRecyclerviewWithAdapter();

        rootCollectionReference = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        commitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMessage = commentEditText.getText().toString().trim();
                if (TextUtils.isEmpty(commentMessage)){
                    Toast.makeText(getContext(), "Write Your Comment", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (menuHeaderDocumentId == null || menuItemDocumentId == null){
                    menuHeaderDocumentId = Constant.DEFAULT_MENU_HEADER_DOCUMENT_ID;
                    menuItemDocumentId = Constant.DEFAULT_MENU_ITEM_DOCUMENT_ID;
                }
                sendCommitMessageToFireStore(commentMessage);
            }
        });
    }



    private void setupRecyclerviewWithAdapter(){
        FirestorePagingOptions<Comment> firestorePagingOptions;
        if (menuHeaderDocumentId == null || menuItemDocumentId == null){
            firestorePagingOptions = createFireStorePagingOptions(Constant.DEFAULT_MENU_HEADER_DOCUMENT_ID, Constant.DEFAULT_MENU_ITEM_DOCUMENT_ID);
        }else {
            firestorePagingOptions = createFireStorePagingOptions(menuHeaderDocumentId, menuItemDocumentId);
        }

        recyclerViewAdapter = new CommentRecyclerViewAdapter(firestorePagingOptions, this);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        commentRecyclerView.setHasFixedSize(true);
        commentRecyclerView.setAdapter(recyclerViewAdapter);
    }


    private FirestorePagingOptions<Comment> createFireStorePagingOptions(String menuHeaderDocumentId, String menuItemDocumentId){
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(18)
                .setPageSize(Constant.pageSize)
                .build();
        Query query = rootCollectionRef.document(menuHeaderDocumentId).collection(Constant.LANGUAGE_COLLECTION)
                .document(menuItemDocumentId).collection(Constant.COMMENTS_COLLECTION).orderBy(Constant.TIME_STAMP, Query.Direction.DESCENDING);
        return new FirestorePagingOptions.Builder<Comment>()
                .setLifecycleOwner(this)
                .setQuery(query, config,Comment.class)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (recyclerViewAdapter != null){
            recyclerViewAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recyclerViewAdapter != null){
            recyclerViewAdapter.stopListening();
        }
    }




    private void sendCommitMessageToFireStore(String message){
        if (currentUserId != null){
            commentEditText.setText("");//clean comments edit text
            Comment comment = new Comment(message, currentUserId, getCurrentDateAndTime(), String.valueOf(System.currentTimeMillis()));
            rootCollectionReference.document(menuHeaderDocumentId).collection(Constant.LANGUAGE_COLLECTION).document(menuItemDocumentId)
                    .collection(Constant.COMMENTS_COLLECTION).document().set(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        if (recyclerViewAdapter != null){
                            recyclerViewAdapter.updateOptions(createFireStorePagingOptions(menuHeaderDocumentId, menuItemDocumentId));
                        }
                        Toast.makeText(getContext(), "Thanks for your feedback", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "Not commit. Something went wrong...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(getContext(), "Not commit. Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateAndTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = date.format(calendar.getTime());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        String currentTime = time.format(calendar.getTime());
        return currentDate + " " + currentTime;
    }

    @Override
    public void onDataCommunication(String menuHeaderDocumentId, String menuItemDocumentId, String headingTitle) {
        this.menuHeaderDocumentId = menuHeaderDocumentId;
        this.menuItemDocumentId = menuItemDocumentId;
        if (recyclerViewAdapter != null){
            recyclerViewAdapter.updateOptions(createFireStorePagingOptions(menuHeaderDocumentId, menuItemDocumentId));
        }
    }

    @Override
    public void onLoadingStateChange(String state) {
        if (state.equals(Constant.DATA_LOADING_START)){
            //when data loading is start
            progressBar.setVisibility(View.VISIBLE);
        }else {
            //when data loading is finish or some error occer in fetching data
            progressBar.setVisibility(View.GONE);
        }
    }
}