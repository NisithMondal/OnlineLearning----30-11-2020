package com.nisith.onlinelearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nisith.onlinelearning.Adapters.MenuOptionsRecyclerAdapter;
import com.nisith.onlinelearning.Model.MenuItem;

public class MenuItemsActivity extends AppCompatActivity implements MenuOptionsRecyclerAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MenuOptionsRecyclerAdapter adapter;
    private CollectionReference collectionReference;
    private String documentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_items);
        Intent intent = getIntent();
        documentId = intent.getStringExtra(Constant.DOCUMENT_ID);
        String title = intent.getStringExtra(Constant.TITLE);
        initializeViews(title);
        collectionReference = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        setupRecyclerViewWithAdapter();

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
        Query query = collectionReference.document(documentId).collection(Constant.LANGUAGE_COLLECTION).orderBy(Constant.TITLE);
        FirestoreRecyclerOptions<MenuItem> recyclerOptions = new FirestoreRecyclerOptions.Builder<MenuItem>()
                .setQuery(query, MenuItem.class)
                .build();
        adapter = new MenuOptionsRecyclerAdapter(recyclerOptions, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null && documentId != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null && documentId != null){
            adapter.stopListening();
        }
    }

    @Override
    public void onItemClick(String title, DocumentReference documentReference) {
        Intent intent = new Intent(this, QuestionAnswerEditActivity.class);
        intent.putExtra(Constant.PARENT_DOCUMENT_ID, documentId);
        intent.putExtra(Constant.CHILD_DOCUMENT_ID, documentReference.getId());
        intent.putExtra(Constant.TITLE, title);
        Log.d("ABCD", documentId);
        Log.d("ABCD", documentReference.getId());
        startActivity(intent);
    }
}