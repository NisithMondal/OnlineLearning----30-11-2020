package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nisith.onlinelearning.Adapters.MenuOptionsRecyclerAdapter;
import com.nisith.onlinelearning.Adapters.QAEditRecyclerAdapter;
import com.nisith.onlinelearning.AlertDialogs.DeleteDocumentAlertDialog;
import com.nisith.onlinelearning.Fragments.UpdateQuestionFragment;
import com.nisith.onlinelearning.Model.QuestionAnswer;

public class QuestionAnswerEditActivity extends AppCompatActivity implements QAEditRecyclerAdapter.OnItemClickListener, UpdateQuestionFragment.OnCloseWindowListener {

    private  Toolbar appToolbar;
    private RecyclerView recyclerView;
    private QAEditRecyclerAdapter adapter;
    private CollectionReference collectionReference;
    private String parentDocumentId, childDocumentId;
    private UpdateQuestionFragment updateQuestionFragment;
    private FrameLayout frameLayout;

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
        createFragment();
        frameLayout.setVisibility(View.GONE);

    }

    private void initializeViews(String title){
        appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        setTitle("");
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.setText(title);
        recyclerView = findViewById(R.id.recycler_view);
        frameLayout = findViewById(R.id.frame_layout);
    }


    private void setupRecyclerViewWithAdapter(){
        Query query = collectionReference.document(parentDocumentId).collection(Constant.LANGUAGE_COLLECTION).document(childDocumentId)
                .collection(Constant.QUESTIONS_COLLECTION).orderBy(Constant.TIME_STAMP);
        FirestoreRecyclerOptions<QuestionAnswer> recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                .setQuery(query, QuestionAnswer.class)
                .build();
        adapter = new QAEditRecyclerAdapter(recyclerOptions, this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu, menu);
        menu.findItem(R.id.edit_question_answer_panel).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.write_question_answer_panel){
            startActivity(new Intent(this, ControlPanelActivity.class));
        }
        return true;
    }


    private void createFragment(){
        updateQuestionFragment = new UpdateQuestionFragment(this);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frame_layout, updateQuestionFragment, "fragment").commit();
    }
    private void showFragment(){
        frameLayout.setVisibility(View.VISIBLE);
        appToolbar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    private void hideFragment(){
        frameLayout.setVisibility(View.GONE);
        appToolbar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    @Override
    public void onBackPressed() {
        if (frameLayout.getVisibility() == View.VISIBLE)
            hideFragment();
        else
            super.onBackPressed();
    }

    @Override
    public void onItemClick(View view, String question, String answer, DocumentReference documentReference) {
        switch (view.getId()){
            case R.id.delete_question_button:
                DeleteDocumentAlertDialog dialog = new DeleteDocumentAlertDialog(documentReference, this);
                dialog.show(getSupportFragmentManager(), "online learning");
                break;
            case R.id.update_question_button:
                updateQuestionFragment.setData(question, answer, documentReference);
                showFragment();
                break;
            case R.id.root_view:

                break;
        }
    }

    @Override
    public void onCloseWindow() {
        hideFragment();
    }
}