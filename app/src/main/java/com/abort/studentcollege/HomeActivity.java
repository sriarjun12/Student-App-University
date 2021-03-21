package com.abort.studentcollege;

import android.os.Bundle;

import com.abort.studentcollege.EventBus.NotesClick;
import com.abort.studentcollege.EventBus.TimeTableClick;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class HomeActivity extends AppCompatActivity {
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
       navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
    @Subscribe(sticky=true,threadMode= ThreadMode.MAIN)
    public void onTimeTableCLick(TimeTableClick event){
        if(event.isSuccess())
        {

            navController.navigate(R.id.nav_time);


        }
    }
    @Subscribe(sticky=true,threadMode= ThreadMode.MAIN)
    public void onNotesclick(NotesClick event){
        if(event.isSuccess())
        {

            navController.navigate(R.id.nav_note);


        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().removeAllStickyEvents(); // Fix event bus always called after onActivityResult
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}