package il.co.afeka.com.memorygame;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {
    static int diff = 0;
    public static final String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextView tv = findViewById(R.id.helloText);
        final Bundle data = getIntent().getExtras();
        tv.setText("Hello "+data.getString("name")+"!\n Age: "+data.getInt("age")+"\nChoose difficulty below");


        final Spinner spinner = findViewById(R.id.menuSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.difficulty));
        //ArrayAdapter.createFromResource(this,,android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //String setting = adapterView.getItemAtPosition(i).toString();
                diff = i;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                Log.e(TAG, "Nothing selected?");

            }
        });

        Button enter = findViewById(R.id.playButton);
        enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(diff > 2){
                    Log.e(TAG, "Clicked when diff > 2");

                }else {
                    Intent intent = new Intent(getBaseContext(), GameActivity.class);

                    String set = "set";
                    intent.putExtra("player",data);
                    switch (diff){
                        case 0:
                            intent.putExtra("timer",30);
                            break;
                        case 1:
                            intent.putExtra("timer",45);
                            break;
                        case 2:
                            intent.putExtra("timer",60);
                            break;
                            default:
                                break;
                    }
                    intent.putExtra(set, diff);
                    startActivity(intent);
                }
            }
        });




    }







}
