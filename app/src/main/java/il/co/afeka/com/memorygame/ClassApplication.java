package il.co.afeka.com.memorygame;

import android.app.Application;

import il.co.afeka.com.memorygame.scoreboard.DatabaseProvider;


public class ClassApplication extends Application {
    private final String TAG = "ClassApplication";
    DatabaseProvider mDatabaseProvider;
    @Override
    public void onCreate() {
        super.onCreate();
        mDatabaseProvider = new DatabaseProvider(getApplicationContext());
        mDatabaseProvider.initializeDatabase();

    }

    public DatabaseProvider getDatabaseProvider(){
        return mDatabaseProvider;
    }



}
