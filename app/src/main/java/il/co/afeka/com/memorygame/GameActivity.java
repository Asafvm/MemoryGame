package il.co.afeka.com.memorygame;

import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

import static android.app.PendingIntent.getActivity;

public class GameActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        play(R.raw.theme);


        initVars();
        int timer;
        Bundle data = getIntent().getExtras();
        try {
            timer = data.getInt("timer");
            cdtimer = createTimer(timer);
            cdtimer.start();
        }catch (NullPointerException e){
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
        }catch (NullPointerException e){
            Log.e(TAG,e.getMessage());
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

    private CountDownTimer createTimer(int timer) {
        return new CountDownTimer(timer * 1000, 1000) {


            @Override
            public void onTick(long millisUntilFinished) {
                TextView timer = findViewById(R.id.timer);
                timeRemain = (int) (millisUntilFinished / 1000);
                timer.setText(getString(R.string.timeremain) + timeRemain);
            }

            @Override
            public void onFinish() {
                //time's up
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
                moves -= 1;
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
                            img2.setTag("flipped");
                            img.setTag("flipped");
                            addScore(100);
                            playEffect(R.raw.coin);
                            findViewById(savedID).setBackgroundColor(Color.LTGRAY);
                            findViewById(view.getId()).setBackgroundColor(Color.LTGRAY);

                            if (moves > 0)
                                moves -= 1;
                            else {
                                play(R.raw.win);
                                cdtimer.cancel();
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

    @Override
    protected void onDestroy() {
        initVars();
        if (mp.isPlaying())
            mp.stop();
        super.onDestroy();
    }
}