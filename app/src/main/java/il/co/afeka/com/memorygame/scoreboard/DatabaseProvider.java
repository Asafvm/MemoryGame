package il.co.afeka.com.memorygame.scoreboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import il.co.afeka.com.memorygame.scoreboard.UserItem;

public class DatabaseProvider {
    public static final String BROADCAST_ACTION = "com.memorygame.SCORES";
    private final String TAG = "Database";
    private HashMap<String, UserItem> myInv = null;
    private boolean myFlag = false;
    private DatabaseReference myDatabaseRef;
    private Context context;

    public DatabaseProvider(Context context) {
        String USER_DB = "Users";
        myDatabaseRef = FirebaseDatabase.getInstance().getReference().child(USER_DB);
        this.context = context;
    }


    public List<UserItem> getMyInv() {
        if (myFlag) {
            return new ArrayList<>(myInv.values());
        }  //data is not available at the moment

        return null;
    }


    public void initializeDatabase() {
        myFlag = false;

        myDatabaseRef.addListenerForSingleValueEvent(myListener);
    }


    private ValueEventListener myListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
            myInv = getInv(dataSnapshot);
            if(myInv.values().size() >10)
                removeLowestScore();
            else {
                myFlag = true;
                Intent intent = new Intent(BROADCAST_ACTION);
                context.sendBroadcast(intent);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            myFlag = false;
        }
    };


    private HashMap<String, UserItem> getInv(DataSnapshot dataSnapshot) {
        HashMap<String, UserItem> tempValues = new HashMap<>();
        if (dataSnapshot.getValue() == null) {//root
            Log.e(TAG, "Cannot browse root folder");
        } else {
            if (dataSnapshot.getValue() == null) {//root
                Log.e(TAG, "Database " + dataSnapshot.getKey() + " does not exists");
            } else {  //Parts
                for (DataSnapshot ds : dataSnapshot.getChildren()) { //Serial numbers
                    UserItem temp = ds.getValue(UserItem.class);
                    if (temp != null)
                        tempValues.put(temp.getId(), temp);
                    else
                        Log.e(TAG, "Invalid item recieved on getInv()");
                }
            }
        }
        return tempValues;
    }


    public void updateRemote(UserItem item) {

        if (myInv.values().size() < 10) {
            myDatabaseRef.child(item.getId()).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    initializeDatabase();
                }
            });
        } else if (userHighscore(item)) {
            removeLowestScore();
            myDatabaseRef.child(item.getId()).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    initializeDatabase();
                }
            });
        }

        Log.d(TAG, "Finished updating");
    }


    private void removeLowestScore() {
        UserItem target = null;

        for (UserItem item : myInv.values()) {
            if (target == null)
                target = item;
            else {
                if (Integer.valueOf(target.getScore())>Integer.valueOf(item.getScore()))
                    target = item;
            }
        }
        final UserItem finalTarget1 = target;
        myDatabaseRef.child(finalTarget1.getId()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e(TAG,"Removed "+ finalTarget1.getName()+" - "+ finalTarget1.getScore());
                        initializeDatabase();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG,"Failed to remove "+ finalTarget1.getName()+" - "+ finalTarget1.getScore());
                    }
                });

    }

    private boolean userHighscore(UserItem item) {
        if (myFlag) {
            for (UserItem userItem : myInv.values()) {
                if (Integer.valueOf(userItem.getScore())>Integer.valueOf(item.getScore())) {
                    return true;        //greater then any other score
                }
            }
            return false;
        }  //should wait for database

        return false;
    }


}



