package il.co.afeka.com.memorygame;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText playerName = findViewById(R.id.playerName);
        final EditText playerAge = findViewById(R.id.playerAge);

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


}

