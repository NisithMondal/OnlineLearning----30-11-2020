package com.nisith.onlinelearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ControlPanelActivity extends AppCompatActivity {
    private Toolbar appToolbar;
    private EditText menuHeaderEditText, topicEditText, questionEditText, answerEditText;
    private Button addMenuHeaderButton, addTopicButton, postAnswerButton;

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
    }

    private void initializeViews(){
        appToolbar = findViewById(R.id.app_toolbar);
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
    }

}