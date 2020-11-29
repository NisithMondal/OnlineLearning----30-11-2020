package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.nisith.onlinelearning.Model.QuestionAnswer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ControlPanelActivity extends AppCompatActivity {
    private Toolbar appToolbar;
    private ProgressBar progressBar;
    private EditText menuHeaderEditText, topicEditText, questionEditText, answerEditText;
    private Button addMenuHeaderButton, addTopicButton, postAnswerButton;
    private Spinner menuHeaderSpinner, topicSpinner;
    private TextView addMenuIcon, addTopicIcon, menuHeaderTextView, topicTextView;
    private ArrayAdapter<String> menuSpinnerAdapter, topicSpinnerAdapter;
    //Firebase FireStore
    private CollectionReference menuHeaderCollectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_panel);
        initializeViews();
        appToolbar.setNavigationIcon(R.drawable.ic_back_arrow_icon);
        appToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Views Visibility
        viewsVisibility(View.GONE);
        addMenuHeaderButton.setOnClickListener(new MyClickListener());
        addTopicButton.setOnClickListener(new MyClickListener());
        postAnswerButton.setOnClickListener(new MyClickListener());
        addTopicIcon.setOnClickListener(new MyClickListener());// when '+' icon is clicked
        addMenuIcon.setOnClickListener(new MyClickListener());// when '+' icon is clicked
        menuHeaderSpinner.setOnItemSelectedListener(new MySpinnerItemsClickListener());
        topicSpinner.setOnItemSelectedListener(new MySpinnerItemsClickListener());
        menuHeaderTextView.setText("No Option Selected");
        topicTextView.setText("No Option Selected");
        progressBar.setVisibility(View.GONE);
        //spinner adapter
        menuSpinnerAdapter = setDataOnMenuHeaderSpinner();
        topicSpinnerAdapter = setDataOnTopicSpinner();
        //Firebase FireStore
        menuHeaderCollectionRef = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        fetchMenuHeaderTitlesFromFireStore();




    }

    private void initializeViews(){
        appToolbar = findViewById(R.id.app_toolbar);
        progressBar = findViewById(R.id.progress_bar);
        setSupportActionBar(appToolbar);
        setTitle("");
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.setText("Admin Panel");
        TextView marqueTextView = findViewById(R.id.marque_text_view);
        marqueTextView.setSelected(true);
        menuHeaderEditText = findViewById(R.id.add_new_menu_header_et);
        topicEditText = findViewById(R.id.topic_edit_text);
        questionEditText = findViewById(R.id.question_edit_view);
        answerEditText = findViewById(R.id.answer_edit_view);
        addMenuHeaderButton = findViewById(R.id.add_menu_header_button);
        addTopicButton = findViewById(R.id.topic_add_button);
        postAnswerButton = findViewById(R.id.post_answer_button);
        menuHeaderSpinner = findViewById(R.id.menu_header_spinner);
        topicSpinner = findViewById(R.id.topic_spinner);
        addMenuIcon = findViewById(R.id.text_view2);
        addTopicIcon = findViewById(R.id.text_view3);
        menuHeaderTextView = findViewById(R.id.menu_header_text_view);
        topicTextView = findViewById(R.id.topic_text_view);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.admin_menu, menu);
        menu.findItem(R.id.write_question_answer_panel).setVisible(false);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.edit_question_answer_panel){
            startActivity(new Intent(this, MenuHeadingOptionsActivity.class));
        }else if (item.getItemId() == R.id.home){
            startActivity(new Intent(this, HomeActivity.class));
            finishAffinity();
        }
        return true;
    }

    private void viewsVisibility(int visibility){
        menuHeaderEditText.setVisibility(visibility);
        topicEditText.setVisibility(visibility);
        addTopicButton.setVisibility(visibility);
        addMenuHeaderButton.setVisibility(visibility);
    }


    private void fetchMenuHeaderTitlesFromFireStore(){
        menuHeaderCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String menuHeader = Objects.requireNonNull(documentSnapshot.get(Constant.TITLE)).toString();
                    menuSpinnerAdapter.add(menuHeader);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private ArrayAdapter<String> setDataOnMenuHeaderSpinner(){
        //set data in menuHeaderSpinner
        List<String> list =  new ArrayList<>();
        list.add("No Option Selected");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menuHeaderSpinner.setAdapter(spinnerAdapter);
        return spinnerAdapter;
    }

    private ArrayAdapter<String> setDataOnTopicSpinner(){
        //set data in topic spinner
        List<String> list =  new ArrayList<>();
        list.add("No Option Selected");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        topicSpinner.setAdapter(spinnerAdapter);
        return spinnerAdapter;
    }

    private class MyClickListener implements View.OnClickListener{
        @SuppressLint("UseCompatLoadingForDrawables")
        public void onClick(View view){
            switch (view.getId()){
                case R.id.add_menu_header_button:
                    //when top add button is clicked
                    if (TextUtils.isEmpty(menuHeaderEditText.getText().toString())){
                        menuHeaderEditText.setError("Enter Text");
                        menuHeaderEditText.requestFocus();
                    }else {
                        String temp = menuHeaderEditText.getText().toString();
                        menuSpinnerAdapter.remove(temp);
                        menuSpinnerAdapter.add(temp);
                    }
                    break;

                case R.id.topic_add_button:
                    if (TextUtils.isEmpty(topicEditText.getText().toString())){
                        topicEditText.setError("Enter Text");
                        topicEditText.requestFocus();
                    }else {
                        String temp = topicEditText.getText().toString();
                        topicSpinnerAdapter.remove(temp);
                        topicSpinnerAdapter.add(temp);
                    }
                    break;

                case R.id.post_answer_button:
                   postAnswer();
                    break;

                case R.id.text_view2:
                    if (menuHeaderEditText.getVisibility()==View.GONE){
                        menuHeaderEditText.setVisibility(View.VISIBLE);
                        addMenuHeaderButton.setVisibility(View.VISIBLE);
                        addMenuIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_negative), null);
                    }else {
                        menuHeaderEditText.setVisibility(View.GONE);
                        addMenuHeaderButton.setVisibility(View.GONE);
                        addMenuIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_add), null);
                    }
                    break;

                case R.id.text_view3:
                    if (topicEditText.getVisibility()==View.GONE){
                        topicEditText.setVisibility(View.VISIBLE);
                        addTopicButton.setVisibility(View.VISIBLE);
                        addTopicIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_negative), null);
                    }else {
                        topicEditText.setVisibility(View.GONE);
                        addTopicButton.setVisibility(View.GONE);
                        addTopicIcon.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, getDrawable(R.drawable.ic_add), null);
                    }



            }
        }
    }





    private class MySpinnerItemsClickListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()){
                case R.id.menu_header_spinner:
                    menuHeaderTextView.setText(parent.getItemAtPosition(position).toString());
                    fetchItemsForTopicSpinnerFromFireStore(parent.getItemAtPosition(position).toString());
                    break;
                case R.id.topic_spinner:
                    topicTextView.setText(parent.getItemAtPosition(position).toString());
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    private void fetchItemsForTopicSpinnerFromFireStore(String menuTitle){
        menuHeaderCollectionRef.document(menuTitle).collection(Constant.LANGUAGE_COLLECTION)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    if (documentSnapshot.exists()){
                        try {
                            String item = Objects.requireNonNull(documentSnapshot.get(Constant.TITLE)).toString();
                            topicSpinnerAdapter.remove(item);
                            topicSpinnerAdapter.add(item);
                        }catch (Exception e){

                        }


                    }
                }
            }
        });
    }

    private void postAnswer(){
        String menuHeaderSpinnerValue = menuHeaderTextView.getText().toString();
        final String topicSpinnerValue = topicTextView.getText().toString();
        String questionValue = questionEditText.getText().toString().trim();
        String answerValue = answerEditText.getText().toString().trim();
        if (menuHeaderSpinnerValue.equals("No Option Selected")){
            Toast.makeText(this, "Select Options", Toast.LENGTH_SHORT).show();
            return;
        }else if (topicSpinnerValue.equals("No Option Selected")){
            Toast.makeText(this, "Select Options", Toast.LENGTH_SHORT).show();
            return;
        }else if (TextUtils.isEmpty(questionValue)){
            questionEditText.setError("Enter Answer");
            questionEditText.requestFocus();
            return;
        }else if (TextUtils.isEmpty(answerValue)){
            answerEditText.setError("Enter Answer");
            answerEditText.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        long currentTimeMilli =  System.currentTimeMillis();
        QuestionAnswer questionAnswer = new QuestionAnswer(questionValue, answerValue, getCurrentDateAndTime()+currentTimeMilli);
        WriteBatch writeBatch = FirebaseFirestore.getInstance().batch();
        DocumentReference menuOptionsDocumentRef = menuHeaderCollectionRef.document(menuHeaderSpinnerValue);
        Map<String, Object> map = new HashMap<>();
        map.put(Constant.TITLE, menuHeaderSpinnerValue);
        writeBatch.set(menuOptionsDocumentRef, map);
        DocumentReference languageDocumentRef = menuHeaderCollectionRef.document(menuHeaderSpinnerValue).collection(Constant.LANGUAGE_COLLECTION)
                .document(topicSpinnerValue);
        Map<String, Object> map1 = new HashMap<>();
        map1.put(Constant.TITLE, topicSpinnerValue);
        writeBatch.set(languageDocumentRef, map1);
        DocumentReference questionDocumentRef = menuHeaderCollectionRef.document(menuHeaderSpinnerValue).collection(Constant.LANGUAGE_COLLECTION)
                .document(topicSpinnerValue).collection(Constant.QUESTIONS_COLLECTION).document();
        writeBatch.set(questionDocumentRef, questionAnswer);
        writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()){
                    Toast.makeText(ControlPanelActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(ControlPanelActivity.this, "Error "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private String getCurrentDateAndTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        String currentDate = date.format(calendar.getTime());
        SimpleDateFormat time = new SimpleDateFormat("hh:mm a");
        String currentTime = time.format(calendar.getTime());
        return currentDate + " " + currentTime;
    }

}