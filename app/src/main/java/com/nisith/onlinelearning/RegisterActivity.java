package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.Compressor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nisith.onlinelearning.Model.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private Toolbar appToolbar;
    private TextView toolbarTextView;
    private ProgressDialog progressDialog;
    private CircleImageView profileImageView;
    private EditText userNameEditText, emailEditText, passwordEditText;
    private Button createAccountButton;
    private TextView haveAnAccountTextView;
    //Firebase Auth
    private FirebaseAuth firebaseAuth;
//    private byte[] profileImageByteArray;
    private CollectionReference userCollectionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeViews();
        appToolbar.setNavigationIcon(R.drawable.ic_back_arrow_icon);
        appToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog = new ProgressDialog(this);
        createAccountButton.setOnClickListener(new MyClickListener());
        haveAnAccountTextView.setOnClickListener(new MyClickListener());
        profileImageView.setOnClickListener(new MyClickListener());
        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        userCollectionRef = FirebaseFirestore.getInstance().collection(Constant.USERS);
    }


    private void initializeViews(){
        appToolbar = findViewById(R.id.app_toolbar);
        setTitle("");
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.setText("User Login");
        profileImageView = findViewById(R.id.profile_image_view);
        userNameEditText = findViewById(R.id.user_name_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        createAccountButton = findViewById(R.id.create_account_button);
        haveAnAccountTextView = findViewById(R.id.already_have_account_text_view);
    }

    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.profile_image_view:
                    openGallery();
                    break;

                case R.id.create_account_button:
                    createAccountWithEmailAndPassword();
                    break;

                case R.id.already_have_account_text_view:
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    break;
            }

        }
    }

    private void openGallery(){
        //Open Crop Image Activity
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1,1)
                .start(this);
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK && result != null) {
//                //Crop image uri
//                Uri profileImageUri = result.getUri();
//                Bitmap profileImageBitmap = getCompressImageBitmap(profileImageUri);
//                if (profileImageBitmap != null) {
//                    Picasso.get().load(profileImageUri).placeholder(R.drawable.default_user_icon).into(profileImageView);
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                    profileImageByteArray = baos.toByteArray();
//                }
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


//    private Bitmap getCompressImageBitmap(Uri imageUri){
//        //this method compress the image and return as a bitmap format
//        Bitmap imageBitmap = null;
//        File imageFile = new File(Objects.requireNonNull(imageUri.getPath()));
//        try {
//            imageBitmap = new Compressor(this)
//                    .setMaxWidth(260)
//                    .setMaxHeight(260)
//                    .setQuality(75)
//                    .compressToBitmap(imageFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return imageBitmap;
//    }

    private void createAccountWithEmailAndPassword(){
        String email = emailEditText.getText().toString().trim();
        final String userName = userNameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email)){
            emailEditText.setError("Enter Email");
            emailEditText.requestFocus();
            return;
        }else if (TextUtils.isEmpty(password)){
            passwordEditText.setError("Enter Password");
            passwordEditText.requestFocus();
            return;
        }else if (TextUtils.isEmpty(userName)){
            userNameEditText.setError("Enter Password");
            userNameEditText.requestFocus();
            return;
        }
        progressDialog.setMessage("Account Creating. Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            //Account created Successfully. So store user information in Cloud FireStore
                            String currentUserId = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getUser()).getUid();
                            saveUserInformation(userName, currentUserId);

                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInformation(String userName, String currentUserId){
        User user = new User(userName, Constant.DEFAULT, getCurrentDateAndTime());
        userCollectionRef.document(currentUserId)
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    //User data store successfully
                    Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                    openHomeActivity();
                }else {
                    Toast.makeText(RegisterActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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


    private void openHomeActivity(){
        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
        finishAffinity();
    }

}