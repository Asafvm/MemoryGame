package il.co.afeka.com.memorygame;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Explode;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import il.co.afeka.com.memorygame.scoreboard.DatabaseProvider;
import il.co.afeka.com.memorygame.scoreboard.UserItem;

import static android.app.PendingIntent.getActivity;

public class GameActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 0;
    private static String savedTAG = "";
    private static int savedID = 0;
    private static boolean secondClick = false;
    private static boolean delay = false;
    private static final String TAG = "GameActivity";
    private static int score = 0;
    private static int timeRemain = 0;
    private int moves;
    private CountDownTimer cdtimer;
    private MediaPlayer mp;
    private SensorManager mSensorManager;
    private UserItem user;
    DatabaseProvider provider;
    private boolean tiltWarning = false;
    HashMap<Integer, String> foundList;
    private SensorService.SensorBinder mBinder;
    private SensorService mService;
    private boolean isBound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        ClassApplication application = (ClassApplication) getApplication();
        provider = application.getDatabaseProvider();
        foundList = new HashMap<Integer, String>();
        play(R.raw.theme);


        initVars();
        int timer;
        Bundle data = getIntent().getExtras();
        try {
            timer = data.getInt("timer");
            cdtimer = createTimer(timer);
            cdtimer.start();
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }
        switch (data.getInt("set")) {
            case 0:
                setContentView(R.layout.activity_game_easy);
                moves = 1;
                break;
            case 1:
                setContentView(R.layout.activity_game_normal);
                moves = 7;
                break;

            case 2:
                setContentView(R.layout.activity_game_hard);
                moves = 12;
                break;

            default:
                Log.e(TAG, "Something went wrong in GameActivity");
                break;

        }

        try {
            String pName = data.getBundle("player").getString("name");
            int pAge = data.getBundle("player").getInt("age");
            final TextView tv = findViewById(R.id.user);
            tv.setText(getString(R.string.nowPlaying) + pName + getString(R.string.age) + pAge);
            user = new UserItem("", "", "", "");
            user.setName(pName);
            user.setAge(String.valueOf(pAge));

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
        }


    }


    private void playEffect(int id) {

        MediaPlayer effect = MediaPlayer.create(getBaseContext(), id);
        effect.start();
    }

    private void play(int id) {
        if (mp != null)
            if (mp.isPlaying())
                mp.stop();
        mp = MediaPlayer.create(getBaseContext(), id);

        if (id == R.raw.theme) {
            mp.seekTo(20000);
            mp.setVolume(80, 80);
        }
        mp.start();
    }

    private CountDownTimer createTimer(final int timer) {
        return new CountDownTimer(timer * 1000, 1000) {
            int i = 1;

            @Override
            public void onTick(long millisUntilFinished) {
                TextView tvTimer = findViewById(R.id.timer);
                timeRemain = (int) (millisUntilFinished / 1000);
                tvTimer.setText(getString(R.string.timeremain) + timeRemain);

                if (tiltWarning) {
                    if (i % 3 == 0 && !foundList.isEmpty()) {
                        Iterator<Integer> iterator = foundList.keySet().iterator();

                        int cId = iterator.next();
                        String cTag = foundList.get(cId);
                        if (cTag.equals("1up")) {
                            findViewById(cId).setTag(cTag);
                            ((ImageButton) findViewById(cId)).setImageResource(R.drawable.pic_star);
                            foundList.remove(cId);
                            moves++;
                            decScore(moves * (timer - timeRemain));
                        } else {

                            while (iterator.hasNext()) {
                                int id = iterator.next();
                                if (foundList.get(id) == cTag) {
                                    findViewById(id).setTag(cTag);
                                    ((ImageButton) findViewById(id)).setImageResource(R.drawable.pic_star);
                                    findViewById(cId).setTag(cTag);
                                    ((ImageButton) findViewById(cId)).setImageResource(R.drawable.pic_star);
                                    foundList.remove(id);
                                    foundList.remove(cId);
                                    moves++;
                                    decScore(moves * (timer - timeRemain));
                                    break;
                                }
                            }
                        }


                    }
                    i++;
                } else
                    i = 0;
            }

            @Override
            public void onFinish() {
                //time's up
                animateButtons((ViewGroup) findViewById(android.R.id.content).getRootView(),false);
                play(R.raw.lose);
                popup(R.string.timesup);


                // Create the AlertDialog object and return it
            }
        };

    }


    private void popup(int messege) {
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        final TextView tvAlert = new TextView(this);
        alertBuilder.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        alertBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });

        tvAlert.setText(getString(messege));
        tvAlert.setTextSize(20);
        tvAlert.setGravity(Gravity.CENTER_HORIZONTAL);
        alertBuilder.setView(tvAlert);


        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
        Button button = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        //layoutParams.gravity = Gravity.CENTER;
        layoutParams.weight = 100;
        button.setLayoutParams(layoutParams);


    }


    private void initVars() {
        savedTAG = "";
        savedID = 0;
        secondClick = false;
        delay = false;
        score = 0;
        timeRemain = 0;
    }

    public void mainFunc(final View view) {
        //insert test for already marked tile
        String curTag = view.getTag().toString();
        int curID = view.getId();
        if (!delay) {
            if (curTag.equals("flipped")) {      //check if already flipped
                Toast.makeText(getBaseContext(), R.string.alreadyflipped, Toast.LENGTH_SHORT).show();
            } else if (curTag.equals("1up") && !secondClick) {  //hard mode only bonus
                Toast.makeText(getBaseContext(), R.string.bonus, Toast.LENGTH_SHORT).show();
                ImageButton img = findViewById(curID);
                img.setImageResource(R.drawable.pic_1up);
                view.setBackgroundColor(Color.YELLOW);
                addScore(250);
                moves--;
                foundList.put(img.getId(), img.getTag().toString());
                view.setTag("flipped");
                playEffect(R.raw.bonus1up);
            } else {
                //if()
                if (!secondClick) {                                 //flip first tile
                    savedID = curID;
                    savedTAG = curTag;
                    ImageButton img = findViewById(savedID);
                    img.setImageResource(getResources().getIdentifier("pic_" + savedTAG, "drawable", getPackageName()));
                    secondClick = !secondClick;

                } else {
                    if (view.getId() != savedID) {                                                     //test for same tile
                        final ImageButton img = findViewById(savedID);
                        final ImageButton img2 = (ImageButton) view;//findViewById(view.getId());
                        img2.setImageResource(getResources().getIdentifier("pic_" + curTag, "drawable", getPackageName()));
                        if (curTag.equals(savedTAG)) {      //test for same tag
                            Log.d(TAG, "Same Image");

                            foundList.put(img.getId(), img.getTag().toString());
                            foundList.put(img2.getId(), img2.getTag().toString());

                            img2.setTag("flipped");
                            img.setTag("flipped");
                            addScore(100);
                            playEffect(R.raw.coin);
                            findViewById(savedID).setBackgroundColor(Color.LTGRAY);
                            findViewById(view.getId()).setBackgroundColor(Color.LTGRAY);

                            if (moves > 0)
                                moves--;
                            else {
                                play(R.raw.win);
                                cdtimer.cancel();
                                user.setScore(String.valueOf(score));
                                Date date = new Date();
                                user.setId(String.valueOf(date.getTime()));
                                getUserLocation();
                                provider.updateRemote(user);
                                animateButtons((ViewGroup) findViewById(android.R.id.content).getRootView(),true);
                                popup(R.string.done);
                            }

                        } else {
                            new CountDownTimer(1000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                    findViewById(view.getId()).setBackgroundColor(Color.RED);
                                    delay = true;
                                }

                                @Override
                                public void onFinish() {
                                    img.setImageResource(R.drawable.pic_star);
                                    img2.setImageResource(R.drawable.pic_star);
                                    delay = false;
//                                findViewById(savedID).setBackgroundColor(Color.LTGRAY);
                                    findViewById(view.getId()).setBackgroundColor(Color.LTGRAY);
                                }
                            }.start();
                        }
                        savedID = 0;
                        savedTAG = "";
                        secondClick = !secondClick;
                    } else {
                        Toast.makeText(getBaseContext(), R.string.sametile, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void addScore(int s) {
        TextView tvScore = findViewById(R.id.score);
        score += s * timeRemain;
        tvScore.setText(String.valueOf(score));
    }

    private void decScore(int s) {
        TextView tvScore = findViewById(R.id.score);
        score -= s;
        tvScore.setText(String.valueOf(score));
    }

    @Override
    protected void onDestroy() {
        initVars();
        if (mp.isPlaying())
            mp.stop();
        cdtimer.cancel();
        super.onDestroy();
    }

    protected void onResume() {
        registerReceiver(sensorAlert,
                new IntentFilter(SensorService.ALERT_ACTION));

        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(sensorAlert);
        super.onPause();
    }

    public void getUserLocation() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setLocation();
        } else {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
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
                    setLocation();

                } else {
                    // permission denied, boo!
                    Toast.makeText(this, "No location access", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setLocation() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        user.setLng(location.getLongitude());
        user.setLat(location.getLatitude());
    }


    //((TextView)findViewById(R.id.warning)).setText(Arrays.toString(event.values));

    private BroadcastReceiver sensorAlert = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "Sensor alert!");
            if (intent.getExtras().getBoolean("alert")) {
                ((TextView) findViewById(R.id.warning)).setTextColor(Color.RED);
                ((TextView) findViewById(R.id.warning)).setText("Tilt Warning");
                tiltWarning = true;
            } else {
                ((TextView) findViewById(R.id.warning)).setTextColor(Color.GREEN);
                ((TextView) findViewById(R.id.warning)).setText("Safe");
                tiltWarning = false;
            }

        }
    };


    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, SensorService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        isBound = false;
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance

            mBinder = (SensorService.SensorBinder) service;
            mService = mBinder.getService();
            isBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBinder = null;
            mService = null;
            isBound = false;
        }
    };

    private Explode makeExplodeTransition() {
        Explode explode = new Explode();
        explode.setDuration(3000);
        explode.setInterpolator(new AnticipateOvershootInterpolator());
        return explode;
    }

    private void toggleVisibility(View... views) {
        // Loop through the views
        for (View v : views) {
            if (v.getVisibility() == View.VISIBLE) {
                v.setVisibility(View.INVISIBLE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }
    }

    private AutoTransition makeAutoTransition() {
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(3000);
        autoTransition.setInterpolator(new BounceInterpolator());
        return autoTransition;
    }

    public void animateButtons(ViewGroup layout, boolean win) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View view = layout.getChildAt(i);
            if (view instanceof ViewGroup)
                animateButtons((ViewGroup) view,win);
            else {
                if (view instanceof ImageButton) {
                    if (win)
                        TransitionManager.beginDelayedTransition(layout, makeAutoTransition());
                    else
                        TransitionManager.beginDelayedTransition(layout, makeExplodeTransition());
                    toggleVisibility(view);


                }
            }
        }
    }
}

