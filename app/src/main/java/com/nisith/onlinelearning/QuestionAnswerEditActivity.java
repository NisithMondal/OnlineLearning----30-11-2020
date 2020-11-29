package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
import android.widget.Toast;

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

import java.util.Arrays;

public class QuestionAnswerEditActivity extends AppCompatActivity implements QAEditRecyclerAdapter.OnItemClickListener, UpdateQuestionFragment.OnCloseWindowListener {

    private  Toolbar appToolbar;
    private RecyclerView recyclerView;
    private QAEditRecyclerAdapter adapter;
    private CollectionReference collectionReference;
    private String parentDocumentId, childDocumentId;
    private UpdateQuestionFragment updateQuestionFragment;
    private FrameLayout frameLayout;
    private String orderBy = Constant.ASCENDING_ORDER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_answer_edit);
        Intent intent = getIntent();
        parentDocumentId = intent.getStringExtra(Constant.PARENT_DOCUMENT_ID);
        childDocumentId = intent.getStringExtra(Constant.CHILD_DOCUMENT_ID);
        String title = intent.getStringExtra(Constant.TITLE);
        initializeViews(title);
        appToolbar.setNavigationIcon(R.drawable.ic_back_arrow_icon);
        appToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
                .collection(Constant.QUESTIONS_COLLECTION).orderBy(Constant.TIME_STAMP, Query.Direction.ASCENDING);
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
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint("Enter Questions");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null){
                    if (newText.trim().length()>0){
                        //If Some characters are present in search view
//                        char[] arr = newText.toCharArray();
//                        arr[0] = Character.toUpperCase(arr[0]) ;
                        adapter.updateOptions(getFireStoreRecyclerOptionsWithText(newText));
                    }else {
                        //when no characters in search view, then show all data i.e. all rows
                        adapter.updateOptions(getFireStoreRecyclerOptionsWithOutText());
                    }
                }

                return true;
            }
        });

        return true;
    }



    private FirestoreRecyclerOptions<QuestionAnswer> getFireStoreRecyclerOptionsWithText(String text){
        FirestoreRecyclerOptions<QuestionAnswer> recyclerOptions;
        Query query = collectionReference.document(parentDocumentId).collection(Constant.LANGUAGE_COLLECTION).document(childDocumentId)
                .collection(Constant.QUESTIONS_COLLECTION).orderBy("question").whereGreaterThanOrEqualTo("question", text).whereLessThanOrEqualTo("question", text+"\uf8ff");
            if (orderBy.equals(Constant.ASCENDING_ORDER)){
                query.orderBy(Constant.TIME_STAMP, Query.Direction.ASCENDING);
                recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                        .setQuery(query, QuestionAnswer.class)
                        .build();
            }else {
                query.orderBy(Constant.TIME_STAMP, Query.Direction.DESCENDING);
                recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                        .setQuery(query, QuestionAnswer.class)
                        .build();

        }

        return recyclerOptions;

    }


    private FirestoreRecyclerOptions<QuestionAnswer> getFireStoreRecyclerOptionsWithOutText(){
        FirestoreRecyclerOptions<QuestionAnswer> recyclerOptions;
        CollectionReference cf = collectionReference.document(parentDocumentId).collection(Constant.LANGUAGE_COLLECTION).document(childDocumentId)
                .collection(Constant.QUESTIONS_COLLECTION);
        if (orderBy.equals(Constant.ASCENDING_ORDER)){
            Query query = cf.orderBy(Constant.TIME_STAMP, Query.Direction.ASCENDING);
            recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                    .setQuery(query, QuestionAnswer.class)
                    .build();
        }else {
            Query query = cf.orderBy(Constant.TIME_STAMP, Query.Direction.DESCENDING);
            recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                    .setQuery(query, QuestionAnswer.class)
                    .build();

        }

        return recyclerOptions;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.write_question_answer_panel:
                startActivity(new Intent(this, ControlPanelActivity.class));
                break;

            case R.id.home:
                startActivity(new Intent(this, HomeActivity.class));
                finishAffinity();
                break;

            case R.id.sort:
                performSorting();
                break;



        }
        return true;
    }
    private void performSorting(){
        //sort the documents in ascending or decending orders
        CollectionReference cf = collectionReference.document(parentDocumentId).collection(Constant.LANGUAGE_COLLECTION).document(childDocumentId)
                .collection(Constant.QUESTIONS_COLLECTION);
        if (adapter != null){
            if (orderBy.equals(Constant.ASCENDING_ORDER)){
                //order the documents in descending order
                orderBy = Constant.DESCENDING_ORDER;
                Query query = cf.orderBy(Constant.TIME_STAMP, Query.Direction.DESCENDING);
                FirestoreRecyclerOptions<QuestionAnswer> recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                        .setQuery(query, QuestionAnswer.class)
                        .build();
                adapter.updateOptions(recyclerOptions);
                Toast.makeText(this, "Questions are sorted in descending order", Toast.LENGTH_SHORT).show();
            }else {
               //order the documents in ascending order
                orderBy = Constant.ASCENDING_ORDER;
                Query query = cf.orderBy(Constant.TIME_STAMP, Query.Direction.ASCENDING);
                FirestoreRecyclerOptions<QuestionAnswer> recyclerOptions = new FirestoreRecyclerOptions.Builder<QuestionAnswer>()
                        .setQuery(query, QuestionAnswer.class)
                        .build();
                adapter.updateOptions(recyclerOptions);
                Toast.makeText(this, "Questions are sorted in ascending order", Toast.LENGTH_SHORT).show();

            }

        }
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
                updateQuestionFragment.setData(question, answer, documentReference);
                showFragment();
                break;
        }
    }

    @Override
    public void onCloseWindow() {
        hideFragment();
    }
}