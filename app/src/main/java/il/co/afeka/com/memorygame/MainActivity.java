package il.co.afeka.com.memorygame;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;

import il.co.afeka.com.memorygame.scoreboard.DatabaseProvider;
import il.co.afeka.com.memorygame.scoreboard.ScoreMapFragment;
import il.co.afeka.com.memorygame.scoreboard.ScoreTableFragment;
import il.co.afeka.com.memorygame.scoreboard.UserItem;
import il.co.afeka.com.memorygame.scoreboard.UserViewerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private android.support.v4.app.FragmentManager mFragmentManager;
    private ScoreMapFragment mScoreMapFragment;
    private ScoreTableFragment mScoreTableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText playerName = findViewById(R.id.playerName);
        final EditText playerAge = findViewById(R.id.playerAge);
        //default
        showTableFragment();
        ((Button) findViewById(R.id.switchButton)).setText("Show map");
        //showMapFragment();


        Button mainBtn = findViewById(R.id.enterButton);

        findViewById(R.id.switchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocationPermission();


            }
        });

        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!isValidName(playerName.getText().toString())) {
                    playerName.setBackgroundColor(Color.RED);
                } else if (!isValidAge(playerAge.getText().toString())) {
                    playerAge.setBackgroundColor(Color.RED);
                } else {
                    Intent intent = new Intent(getBaseContext(), MenuActivity.class);

                    intent.putExtra("name", playerName.getText().toString());
                    intent.putExtra("age", Integer.valueOf(playerAge.getText().toString()));
                    startActivity(intent);

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    switchView();

                } else {
                    // permission denied, boo!
                    Toast.makeText(this, "No location access", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void switchView() {
        if (mScoreTableFragment != null) {
            mScoreTableFragment = null;
            showMapFragment();
            ((Button) findViewById(R.id.switchButton)).setText("Show Table");

        } else {
            mScoreMapFragment = null;
            showTableFragment();
            ((Button) findViewById(R.id.switchButton)).setText("Show Map");

        }
    }

    private void showMapFragment() {


        mFragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (mScoreMapFragment == null) {
            mScoreMapFragment = new ScoreMapFragment();
        }
        //Fade fade = new Fade();
        //fade.setDuration(400);
        //mScoreMapFragment.setEnterTransition(fade);
        //mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.container, mScoreMapFragment).commit();

    }

    private void showTableFragment() {
        mFragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (mScoreTableFragment == null) {
            mScoreTableFragment = new ScoreTableFragment();
        }
        Fade fade = new Fade();
        fade.setDuration(400);
        mScoreTableFragment.setEnterTransition(fade);
        //mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.container, mScoreTableFragment).commit();
        updateTable();
    }

    private boolean isValidAge(String s) {
        try {
            Integer.valueOf(s);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidName(String name) {
        return !(name == null || name.equals(""));

    }
/*
    @Override
    public void onBackPressed() {

        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStackImmediate();

        } else {
            finish();
        }
    }*/

    private BroadcastReceiver ScoreBoardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context,intent.getStringExtra("path"),Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Database available!");
            updateTable();
        }
    };


    @Override
    protected void onResume() {
        registerReceiver(ScoreBoardReceiver,
                new IntentFilter(DatabaseProvider.BROADCAST_ACTION));
        if (mScoreTableFragment != null) {
            updateTable();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(ScoreBoardReceiver);
        super.onPause();
    }


    private void updateTable() {
        RecyclerView recyclerView = findViewById(R.id.recyclerScoresTable);
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            ClassApplication application = (ClassApplication) getApplication();
            DatabaseProvider provider = application.getDatabaseProvider();
            List<UserItem> values = provider.getMyInv();
            if (values != null) {
                Collections.sort(values);
                RecyclerView.Adapter<UserViewerAdapter.ViewHolder> adapter = new UserViewerAdapter(values, this);
                recyclerView.setAdapter(adapter);
            } else {
                //no data to show
            }
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            switchView();

        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }
}

