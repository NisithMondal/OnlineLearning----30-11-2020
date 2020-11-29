package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import android.accounts.OnAccountsUpdateListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nisith.onlinelearning.Fragments.CommentFragment;
import com.nisith.onlinelearning.Fragments.HomeFragment;
import com.nisith.onlinelearning.Fragments.UserAccountFragment;
import com.nisith.onlinelearning.Model.DrawerMenu;
import com.nisith.onlinelearning.Model.User;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, UserAccountFragment.OnUpdateUserAccountListener {



    public interface OnFragmentDataCommunicationListener{
        void onDataCommunication(String menuHeaderDocumentId, String menuItemDocumentId, String headingTitle);
    }

    private NavigationView navigationDrawerView;
    private CircleImageView navigationDrawerHeaderImageView;
    private DrawerLayout drawerLayout;
    private Toolbar appToolbar;
    private TextView toolbarTextView, navigationDrawerHeaderProfileNameTextView;
    private BottomNavigationView bottomNavigationView;
    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private CollectionReference rootCollectionRef;
    private String userId;
    private List<DrawerMenu> drawerMenuList;
    private String menuHeaderDocumentId, menuItemDocumentId, headingTitle = Constant.DEFAULT_LANGUAGE;
    private OnFragmentDataCommunicationListener onFragmentDataCommunicationListener;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
        addNavigationDrawer();
        drawerMenuList = new ArrayList<>();
        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        bottomNavigationView.setOnNavigationItemSelectedListener(new MyNavigationItemSelectedListener());
        //Set default fragment on starting
        Fragment fragment = new HomeFragment(menuHeaderDocumentId, menuItemDocumentId, headingTitle);
        onFragmentDataCommunicationListener = (OnFragmentDataCommunicationListener) fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment, "fragment").commit();
        navigationDrawerHeaderProfileNameTextView.setOnClickListener(new MyNavigationHeaderMenuItemClickListener());
        navigationDrawerHeaderImageView.setOnClickListener(new MyNavigationHeaderMenuItemClickListener());
    }


    private void initializeViews(){
        navigationDrawerView = findViewById(R.id.navigation_view);
        View headerView = navigationDrawerView.getHeaderView(0);
        navigationDrawerHeaderImageView = headerView.findViewById(R.id.profile_image_view);
        navigationDrawerHeaderProfileNameTextView = headerView.findViewById(R.id.profile_name_text_view);
        drawerLayout = findViewById(R.id.navigation_drawer);
        appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        setTitle("");
        toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.setText(Constant.APP_NAME);
        setSupportActionBar(appToolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }


    private class MyNavigationHeaderMenuItemClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            openUserAccountFragment();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }


    @Override
    public void onUpdateUserAccount() {
        //this is a callback method called when current user update his/her account.
        // So we have to update his data in Navigation drawer menu header also.
        fetchUserProfileData();
    }

    private void fetchUserProfileData(){
        //fetch current user name and profile image from fireStore
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
                                navigationDrawerHeaderProfileNameTextView.setText(userName);
                                String profileImageUrl = user.getProfileImageUrl();
                                Picasso.get().load(profileImageUrl).fit().centerCrop().placeholder(R.drawable.default_user_icon)
                                        .into(navigationDrawerHeaderImageView);

                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null){
            //User not register i.e. new user
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }else {
            userId = currentUser.getUid();
            rootCollectionRef = FirebaseFirestore.getInstance().collection(Constant.TOPICS);

            fetchUserProfileData();//set this data on Navigation drawer header menu
            addNavigationDrawerMenuItems();
        }

    }


    class MyNavigationItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment = null;
            switch (menuItem.getItemId()){
                case R.id.home:
                    toolbarTextView.setText(Constant.APP_NAME);
                    fragment = new HomeFragment(menuHeaderDocumentId, menuItemDocumentId, headingTitle);
                    onFragmentDataCommunicationListener = (OnFragmentDataCommunicationListener) fragment;
                    break;

                case R.id.add_comment:
                    toolbarTextView.setText("User Comments");
                    fragment = new CommentFragment(menuHeaderDocumentId, menuItemDocumentId);
                    onFragmentDataCommunicationListener = (OnFragmentDataCommunicationListener) fragment;
                    break;

                case R.id.account:
                    toolbarTextView.setText("User Account");
                    fragment = new UserAccountFragment(HomeActivity.this);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();
            return true;
        }
    }



    private void addNavigationDrawerMenuItems(){
        //fetch all menu items from fire store. First fetch all menu headers Title
        final Menu drawerMenu = navigationDrawerView.getMenu();
        drawerMenu.clear();
        drawerMenuList.clear();
        rootCollectionRef.orderBy(Constant.TITLE).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                    String menuHeaderTitle = (String) documentSnapshot.get(Constant.TITLE);
                    if (menuHeaderTitle!=null) {
                        String documentId = documentSnapshot.getId();
                        SubMenu subMenu = drawerMenu.addSubMenu(menuHeaderTitle);
                        fetchSubmenuItems(documentId,  subMenu);
                    }
                }
                addAnotherSubMenu();
            }
        });

    }

    private void addAnotherSubMenu(){
        Menu drawerMenu = navigationDrawerView.getMenu();
        SubMenu subMenu = drawerMenu.addSubMenu("Other Options");
        subMenu.add("logout");
        subMenu.add("Privacy Policy");
        subMenu.add("Admin User Login");
    }

    private void fetchSubmenuItems(final String menuHeaderId, final SubMenu subMenu){
        //fetch all the sub menu items
        rootCollectionRef.document(menuHeaderId).collection(Constant.LANGUAGE_COLLECTION).orderBy(Constant.TITLE).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            String subMenuItemTitle =  Objects.requireNonNull(documentSnapshot.get(Constant.TITLE)).toString();
                            drawerMenuList.add(new DrawerMenu(menuHeaderId, documentSnapshot.getId(), subMenuItemTitle));
                            subMenu.add(subMenuItemTitle);
                        }
                    }
                });
    }




    private void addNavigationDrawer(){
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,appToolbar,R.string.open_drawer,R.string.close_drawer){

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                //when nevagitation drawer is opened

            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //when nevagitation drawer is Closed
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationDrawerView.setNavigationItemSelectedListener(this);
//        navigationDrawerView.setCheckedItem(R.id.currency);
    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getTitle().toString()){
            case Constant.LOGOUT:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finishAffinity();
                break;
            case Constant.ADMIN_USER_LOGIN:
                startActivity(new Intent(HomeActivity.this, AdminOptionsActivity.class));
                break;
            case Constant.PRIVACY_POLICY:
                openPrivacyPolicyLink();
                break;
            default:
                String itemTitle = (String) menuItem.getTitle();
                    for (DrawerMenu drawerMenu : drawerMenuList){
                        if (drawerMenu.getMenuItemTitle().equals(itemTitle)){
                            if (onFragmentDataCommunicationListener != null) {
                                this.headingTitle = itemTitle;
                                menuHeaderDocumentId = drawerMenu.getMenuHeaderDocumentId();
                                menuItemDocumentId = drawerMenu.getMenuItemDocumentId();
                                onFragmentDataCommunicationListener.onDataCommunication(drawerMenu.getMenuHeaderDocumentId(), drawerMenu.getMenuItemDocumentId(), headingTitle);
                                break;
                            }else {
                                Toast.makeText(this, "else null "+itemTitle, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.user_account:
                openUserAccountFragment();
                break;

            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finishAffinity();
                break;

            case R.id.privacy_policy:
                openPrivacyPolicyLink();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }


    private void openUserAccountFragment(){
        toolbarTextView.setText("User Account");
        Fragment accountFragment = new UserAccountFragment(this);
        bottomNavigationView.setSelectedItemId(R.id.account);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, accountFragment).commit();
    }


    private void openPrivacyPolicyLink(){
        //If play store app is not installed in that device, then the following code open play store app in web browser.
        Intent playStoreIntent1 = new Intent();
        playStoreIntent1.setAction(Intent.ACTION_VIEW);
        playStoreIntent1.setData(Uri.parse("https://privacypolicyquestionsworld.blogspot.com/p/privacy-policies-questions-world.html"));
        try {
            startActivity(playStoreIntent1);
        }catch (Exception e){
            Toast.makeText(this, "Not Open. Something Went Wrong", Toast.LENGTH_SHORT).show();
        }
    }



}