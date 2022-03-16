package com.example.ujchatapp.Activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ujchatapp.Fragment.ChatFragment;
import com.example.ujchatapp.Fragment.ContactFragment;
import com.example.ujchatapp.Fragment.GroupsFragment;
import com.example.ujchatapp.Fragment.ProfileFragment;
import com.example.ujchatapp.R;
import com.example.ujchatapp.Utils.Util;
import com.google.firebase.auth.FirebaseAuth;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;


public class DashBoard extends AppCompatActivity {

    private ChipNavigationBar navigationBar;
    private Fragment fragment = null;
    private Util util;

    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        util = new Util();

        navigationBar = findViewById(R.id.navigationChip);
        
        if (savedInstanceState == null) {
            navigationBar.setItemSelected(R.id.chat, true);
            getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, new ChatFragment()).commit();
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    //Do anything here which needs to be done after signout is complete
                    finish();
                }
            }
        };

        navigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                switch (i) {
                    case R.id.chat:
                        fragment = new ChatFragment();
                        break;
                    case R.id.contacts:
                        fragment = new ContactFragment();
                        break;
                    case R.id.profile:
                        fragment = new ProfileFragment();
                        break;
                    case R.id.groups:
                        fragment = new GroupsFragment();
                        break;
                }

                if (fragment != null)
                    getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = new MenuInflater(this);
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.my_media:
                break;
            case R.id.logout:
                //Init and attach
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.addAuthStateListener(authStateListener);

//Call signOut()
                firebaseAuth.signOut();

                break;
        }

        return true;

    }

    @Override
    protected void onResume() {
        util.updateOnlineStatus("online");
        super.onResume();
    }

    @Override
    protected void onPause() {
        util.updateOnlineStatus(String.valueOf(System.currentTimeMillis()));
        super.onPause();
    }
}
