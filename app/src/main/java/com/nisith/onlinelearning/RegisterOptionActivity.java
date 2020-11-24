package com.nisith.onlinelearning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterOptionActivity extends AppCompatActivity {
    private Toolbar appToolbar;
    private ProgressBar progressBar;
    private Button createAccountWithEmailButton, googleSignInButton;
    private TextView haveAnAccountTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_option);
        initializeViews();
        setSupportActionBar(appToolbar);
        appToolbar.setNavigationIcon(R.drawable.ic_back_arrow_icon);
        appToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        createAccountWithEmailButton.setOnClickListener(new MyClickListener());
        googleSignInButton.setOnClickListener(new MyClickListener());
        haveAnAccountTextView.setOnClickListener(new MyClickListener());
        progressBar.setVisibility(View.GONE);
    }

    private void initializeViews(){
        appToolbar = findViewById(R.id.app_toolbar);
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        setTitle("");
        toolbarTextView.setText("Options");
        progressBar = findViewById(R.id.progress_bar);
        createAccountWithEmailButton = findViewById(R.id.create_account_with_email_button);
        googleSignInButton = findViewById(R.id.google_signIn_button);
        haveAnAccountTextView = findViewById(R.id.already_have_account_text_view);

    }


    private class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.create_account_with_email_button:
                    startActivity(new Intent(RegisterOptionActivity.this, RegisterActivity.class));
                    break;

                case R.id.google_signIn_button:
                    Toast.makeText(RegisterOptionActivity.this, "google_signIn_button", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.already_have_account_text_view:
                    startActivity(new Intent(RegisterOptionActivity.this, LoginActivity.class));
            }

        }
    }

}