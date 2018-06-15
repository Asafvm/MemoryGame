package il.co.afeka.com.memorygame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Collections;
import java.util.List;

import il.co.afeka.com.memorygame.scoreboard.DatabaseProvider;
import il.co.afeka.com.memorygame.scoreboard.MapFragment;
import il.co.afeka.com.memorygame.scoreboard.ScoreTableFragment;
import il.co.afeka.com.memorygame.scoreboard.UserItem;
import il.co.afeka.com.memorygame.scoreboard.UserViewerAdapter;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FragmentManager mFragmentManager;
    private MapFragment mScoreMapFragment;
    private ScoreTableFragment mScoreTableFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText playerName = findViewById(R.id.playerName);
        final EditText playerAge = findViewById(R.id.playerAge);

        showTableFragment();
        //showMapFragment();


        Button mainBtn = findViewById(R.id.enterButton);
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

    private void showMapFragment() {



        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (mScoreMapFragment == null) {
            mScoreMapFragment = new MapFragment();
        }
        Fade fade = new Fade();
        fade.setDuration(400);
        mScoreMapFragment.setEnterTransition(fade);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.container, mScoreMapFragment).commit();

    }

    private void showTableFragment() {
        mFragmentManager = getSupportFragmentManager();
        FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();

        if (mScoreTableFragment == null) {
            mScoreTableFragment = new ScoreTableFragment();
        }
        Fade fade = new Fade();
        fade.setDuration(400);
        mScoreTableFragment.setEnterTransition(fade);
        mFragmentTransaction.addToBackStack(null);
        mFragmentTransaction.replace(R.id.container, mScoreTableFragment).commit();
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

    @Override
    public void onBackPressed() {

        if (mFragmentManager.getBackStackEntryCount() > 0) {
            mFragmentManager.popBackStackImmediate();

        } else {
            finish();
        }
    }

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
            if(mScoreTableFragment!=null){
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
        if(recyclerView!=null) {
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

}

