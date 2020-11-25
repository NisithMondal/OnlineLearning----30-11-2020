package com.nisith.onlinelearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nisith.onlinelearning.Adapters.MenuOptionsRecyclerAdapter;
import com.nisith.onlinelearning.Adapters.QAEditRecyclerAdapter;
import com.nisith.onlinelearning.Model.MenuItem;
import com.nisith.onlinelearning.Model.QuestionAnswer;

public class QuestionAnswerEditActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private QAEditRecyclerAdapter adapter;
    private CollectionReference collectionReference;
    private String parentDocumentId, childDocumentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer_edit);
        Intent intent = getIntent();
        parentDocumentId = intent.getStringExtra(Constant.PARENT_DOCUMENT_ID);
        childDocumentId = intent.getStringExtra(Constant.CHILD_DOCUMENT_ID);
        String title = intent.getStringExtra(Constant.TITLE);
        initializeViews(title);
        collectionReference = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        setupRecyclerViewWithAdapter();

        Log.d("ABCD", parentDocumentId);
        Log.d("ABCD", childDocumentId);
    }

    private void initializeViews(String title){
        Toolbar appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        setTitle("");
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.setText(title);
        recyclerView = findViewById(R.id.recycler_view);
    }


    private void setupRecyclerViewWithAdapter(){
        Query query = collectionReference.document(parentDocumentId).collection(Constant.LANGUAGE_COLLECTION).document(childDocumentId)
                .collection(Constant.QUESTIONS_COLLECTION).orderBy(Constant.TIME_STAMP);
        FirestoreRecyclerOptions<QuestionAnswer> recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                .setQuery(query, QuestionAnswer.class)
                .build();
        adapter = new QAEditRecyclerAdapter(recyclerOptions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null && parentDocumentId != null && childDocumentId != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null && parentDocumentId != null && childDocumentId != null){
            adapter.stopListening();
        }
    }


}