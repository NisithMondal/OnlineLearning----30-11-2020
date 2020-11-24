package com.nisith.onlinelearning;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationDrawerView;
    private DrawerLayout drawerLayout;
    private Toolbar appToolbar;
    //Firebase Auth
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeViews();
        addNavigationDrawer();
        addNavigationDrawerMenuItems();
        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
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
    }

    private void addNavigationDrawerMenuItems(){
        List<CharSequence> name = new ArrayList();
        name.add("Java");
        name.add("Python");
        name.add("Android");
        name.add("Java Script");

        Menu drawerMenu = navigationDrawerView.getMenu();
        SubMenu subMenu = drawerMenu.addSubMenu(0,0,Menu.NONE,"Programming Languages");
        for (int i = 0; i < 4; i++) {

            subMenu.add(0, i, Menu.NONE,name.get(i));
            subMenu.setIcon(R.drawable.ic_android);
        }
        SubMenu subMenu1 = drawerMenu.addSubMenu("Mobile App Tutorial");
        subMenu1.add(1, 0, Menu.NONE,"Android");
        subMenu1.add(1, 1, Menu.NONE,"IOS");

        SubMenu subMenu3 = drawerMenu.addSubMenu("Other Options");

        subMenu3.add("Logout");
        subMenu3.add("Privacy Policy");
        subMenu3.add(Constant.ADMIN_USER_LOGIN);

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
                Toast.makeText(this, "PRIVACY_POLICY", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this, ""+menuItem.getGroupId(), Toast.LENGTH_SHORT).show();
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