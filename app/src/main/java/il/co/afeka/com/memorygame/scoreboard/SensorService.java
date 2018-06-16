package il.co.afeka.com.memorygame.scoreboard;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class SensorService extends Service implements SensorEventListener {

    private final IBinder mBinder = new SensorBinder();
    private BindSensorListener mSensorListener;
    private SensorManager mSensorManager;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class SensorBinder extends Binder {

        SensorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SensorService.this;
        }

        void registerListener(BindSensorListener listener) {
            mSensorListener = listener;
        }
        void unRegisterListener() {
            mSensorListener =null;
        }

        void getSensorchange() {
            SensorService.this.getSensorChange();
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        mSensorManager.unregisterListener(SensorService.this);
        return super.onUnbind(intent);
    }

    public interface BindSensorListener {
        void didGetSensorChange(float axisZ) throws CloneNotSupportedException;
    }

    public void getSensorChange() {
        // Adding sensor
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_FASTEST);
    }
    public void onSensorChanged(SensorEvent event) {
        try {
            mSensorListener.didGetSensorChange(event.values[1]);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}
