package com.nisith.onlinelearning.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nisith.onlinelearning.Constant;
import com.nisith.onlinelearning.HomeActivity;
import com.nisith.onlinelearning.Model.User;
import com.nisith.onlinelearning.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class UserAccountFragment extends Fragment {

    private CircleImageView profileImageView;
    private EditText userNameEditText;
    private ImageView nameEditIcon, cameraImageView;
    private Button updateProfileButton;
    private byte[] profileImageByteArray;
    private CollectionReference userCollectionRef;
    //Firebase storage
    private StorageReference firebaseStorageRef;
    private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private boolean isImageViewClicked, isEditTextClicked;//if only image view is clicked then we only update image view or name
    public UserAccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_account, container, false);
        profileImageView = view.findViewById(R.id.profile_image_view);
        userNameEditText = view.findViewById(R.id.user_name_edit_text);
        profileImageView = view.findViewById(R.id.profile_image_view);
        nameEditIcon = view.findViewById(R.id.name_edit_icon);
        cameraImageView = view.findViewById(R.id.camera_image_view);
        updateProfileButton = view.findViewById(R.id.update_profile_button);
        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        updateProfileButton.setVisibility(View.GONE);//first invisible profile update button
        userNameEditText.setEnabled(false);
        profileImageView.setOnClickListener(new MyClickListener());
        cameraImageView.setOnClickListener(new MyClickListener());
        nameEditIcon.setOnClickListener(new MyClickListener());
        updateProfileButton.setOnClickListener(new MyClickListener());
        firebaseStorageRef = FirebaseStorage.getInstance().getReference().child("all_uploaded_photos").child("user_profile_images");
        fetchUserProfileData();

    }

    private class MyClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.name_edit_icon:
                    isEditTextClicked = true;
                    if (! userNameEditText.isEnabled()){
                        userNameEditText.setEnabled(true);
                        updateProfileButton.setVisibility(View.VISIBLE);
                    }
                    break;

                case R.id.camera_image_view:
                    isImageViewClicked = true;
                    openGallery();
                    break;

                case R.id.profile_image_view:
                    openGallery();
                    break;

                case R.id.update_profile_button:
                    if (userId != null){
                        String newName = userNameEditText.getText().toString().trim();
                        if (TextUtils.isEmpty(newName)){
                            userNameEditText.setError("Enter new Name");
                            return;
                        }
                        updateUserProfile(newName, userId);
                    }
            }
        }
    }


    private void fetchUserProfileData(){
        if (userId != null){
            DocumentReference userDocumentReference = FirebaseFirestore.getInstance().collection(Constant.USERS).document(userId);
            userDocumentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                       DocumentSnapshot documentSnapshot = task.getResult();
                       if (documentSnapshot != null){
                           User user = documentSnapshot.toObject(User.class);
                           if (user != null){
                               String userName = user.getUserName();
                               userNameEditText.setText(userName);
                               String profileImageUrl = user.getProfileImageUrl();
                               Log.d("ABCDE", profileImageUrl);
                               Picasso.get().load(profileImageUrl).fit().centerCrop().placeholder(R.drawable.default_user_icon)
                                       .into(profileImageView);

                           }
                       }
                    }else {
                        Toast.makeText(getContext(), "Unable to fetch your account data "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateUserProfile(String newName, String userId){
        if (isEditTextClicked && isImageViewClicked){
            //if user wants to update his/her profile name as well as image
            updateUserProfileName(userId, newName);
            updateUserProfileImage(userId);
            isEditTextClicked = false;
            isImageViewClicked = false;
        }else if (isEditTextClicked){
            //if user wants to update his/her profile name
            updateUserProfileName(userId, newName);
            isEditTextClicked = false;
        }else {
            //if user wants to update his/her profile image
            updateUserProfileImage(userId);
            isImageViewClicked = false;
        }
    }


    private void updateUserProfileName(String userId, String newName){
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constant.USERS).document(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("userName", newName);
        documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(getContext(), "Name update Successfully", Toast.LENGTH_SHORT).show();
                    userNameEditText.setEnabled(false);
                }else {
                    Toast.makeText(getContext(), "Name Not update "+ Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void updateUserProfileImage(final String userId){
        if (profileImageByteArray != null){
            firebaseStorageRef.child(userId+".jpg").putBytes(profileImageByteArray)
                   .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
                           task.addOnCompleteListener(new OnCompleteListener<Uri>() {
                               @Override
                               public void onComplete(@NonNull Task<Uri> task) {
                                   Uri imageUri = task.getResult();
                                   if (imageUri != null){
                                       final String newImageUrl = imageUri.toString();
                                       //new uploaded image url is not null
                                       Map<String, Object> map = new HashMap<>();
                                       map.put("profileImageUrl", newImageUrl);
                                       DocumentReference documentReference = FirebaseFirestore.getInstance().collection(Constant.USERS).document(userId);
                                       documentReference.update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                           @Override
                                           public void onComplete(@NonNull Task<Void> task) {
                                               if (task.isSuccessful()){
                                                   Toast.makeText(getContext(), "Image uploaded Successfully", Toast.LENGTH_SHORT).show();
                                                   Picasso.get().load(newImageUrl).fit().centerCrop().placeholder(R.drawable.default_user_icon)
                                                           .into(profileImageView);
                                               }else {
                                                   Toast.makeText(getContext(), "Image not uploaded ", Toast.LENGTH_SHORT).show();
                                               }
                                           }
                                       });

                                   }
                               }
                           });
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Image not uploaded "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openGallery(){
        //Open Crop Image Activity
        CropImage.activity()
                .setAspectRatio(1,1)
                .start(getContext(), this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK && result != null) {
                //Crop image uri
                Uri profileImageUri = result.getUri();
                Bitmap profileImageBitmap = getCompressImageBitmap(profileImageUri);
                if (profileImageBitmap != null) {
                    Picasso.get().load(profileImageUri).placeholder(R.drawable.default_user_icon).into(profileImageView);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    profileImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    profileImageByteArray = baos.toByteArray();
                    updateProfileButton.setVisibility(View.VISIBLE);
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private Bitmap getCompressImageBitmap(Uri imageUri){
        //this method compress the image and return as a bitmap format
        Bitmap imageBitmap = null;
        File imageFile = new File(Objects.requireNonNull(imageUri.getPath()));
        try {
            imageBitmap = new Compressor(getContext())
                    .setMaxWidth(260)
                    .setMaxHeight(260)
                    .setQuality(75)
                    .compressToBitmap(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBitmap;
    }




}