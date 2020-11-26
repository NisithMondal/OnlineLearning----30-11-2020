package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nisith.onlinelearning.Adapters.HomeRecyclerViewAdapter;
import com.nisith.onlinelearning.Fragments.CommentFragment;
import com.nisith.onlinelearning.Fragments.HomeFragment;
import com.nisith.onlinelearning.Model.DrawerMenu;
import com.nisith.onlinelearning.Model.QuestionAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public interface OnFragmentDataCommunicationListener{
        void onDataCommunication(String menuHeaderDocumentId, String menuItemDocumentId);
    }

    private NavigationView navigationDrawerView;
    private DrawerLayout drawerLayout;
    private Toolbar appToolbar;
    private BottomNavigationView bottomNavigationView;
    //Firebase Auth
    private FirebaseAuth firebaseAuth;
    private CollectionReference rootCollectionRef;
    private List<DrawerMenu> drawerMenuList;
    private String menuHeaderDocumentId, menuItemDocumentId;
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
        rootCollectionRef = FirebaseFirestore.getInstance().collection(Constant.TOPICS);
        bottomNavigationView.setOnNavigationItemSelectedListener(new MyNavigationItemSelectedListener());
        //Set default fragment on starting
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new HomeFragment(menuHeaderDocumentId, menuItemDocumentId), "fragment").commit();
    }


    private void initializeViews(){
        navigationDrawerView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.navigation_drawer);
        appToolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(appToolbar);
        setTitle("");
        TextView toolbarTextView = findViewById(R.id.toolbar_text_view);
        toolbarTextView.setText("Online Learning");
        setSupportActionBar(appToolbar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null){
            //User not register i.e. new user
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        addNavigationDrawerMenuItems();

    }


    class MyNavigationItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment fragment = null;
            switch (menuItem.getItemId()){
                case R.id.home:
                    fragment = new HomeFragment(menuHeaderDocumentId, menuItemDocumentId);
                    onFragmentDataCommunicationListener = (OnFragmentDataCommunicationListener) fragment;
                    break;

                case R.id.add_comment:
                    fragment = new CommentFragment();
                    break;

                case R.id.account:

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
                break;
            case Constant.ADMIN_USER_LOGIN:
                startActivity(new Intent(HomeActivity.this, ControlPanelActivity.class));
                break;
            case Constant.PRIVACY_POLICY:
                startActivity(new Intent(this, MenuHeadingOptionsActivity.class));
                Toast.makeText(this, "PRIVACY_POLICY", Toast.LENGTH_SHORT).show();
                break;
            default:
                String itemTitle = (String) menuItem.getTitle();
                    for (DrawerMenu drawerMenu : drawerMenuList){
                        if (drawerMenu.getMenuItemTitle().equals(itemTitle)){
                            if (onFragmentDataCommunicationListener != null) {
                                menuHeaderDocumentId = drawerMenu.getMenuHeaderDocumentId();
                                menuItemDocumentId = drawerMenu.getMenuItemDocumentId();
                                onFragmentDataCommunicationListener.onDataCommunication(drawerMenu.getMenuHeaderDocumentId(), drawerMenu.getMenuItemDocumentId());
                                break;
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
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;

            case R.id.privacy_policy:
                Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show();
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

}