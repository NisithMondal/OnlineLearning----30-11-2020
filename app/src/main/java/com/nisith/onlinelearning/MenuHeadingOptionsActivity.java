package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.nisith.onlinelearning.Adapters.MenuOptionsRecyclerAdapter;
import com.nisith.onlinelearning.Model.MenuItem;

public class MenuHeadingOptionsActivity extends AppCompatActivity  implements MenuOptionsRecyclerAdapter.OnItemClickListener {

    private RecyclerView recyclerView;
    private MenuOptionsRecyclerAdapter adapter;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_heading_options);
        initializeViews();
        collectionReference = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        setupRecyclerViewWithAdapter();
    }


    private void initializeViews(){
        Toolbar appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        setTitle("");
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.setText("Edit");
        recyclerView = findViewById(R.id.recycler_view);
    }

    private void setupRecyclerViewWithAdapter(){
        Query query = collectionReference.orderBy(Constant.TITLE);
        FirestoreRecyclerOptions<MenuItem> recyclerOptions = new FirestoreRecyclerOptions.Builder<MenuItem>()
                .setQuery(query, MenuItem.class)
                .build();
        adapter = new MenuOptionsRecyclerAdapter(recyclerOptions, this, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null){
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null){
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
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.write_question_answer_panel){
            startActivity(new Intent(this, ControlPanelActivity.class));
        }
        return true;
    }


    @Override
    public void onItemClick(View view, String title, DocumentReference documentReference) {
        if (view.getId()==R.id.root_view) {
            Intent intent = new Intent(this, MenuItemsActivity.class);
            intent.putExtra(Constant.DOCUMENT_ID, documentReference.getId());
            intent.putExtra(Constant.TITLE, title);
            startActivity(intent);
        }

    }


}