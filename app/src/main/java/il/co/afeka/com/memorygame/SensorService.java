package il.co.afeka.com.memorygame;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;

public class SensorService extends Service {

    public static final String ALERT_ACTION = "sensor_alert";
    private final IBinder mBinder = new SensorBinder();
    private SensorManager mSensorManager;

    private final float[] mGravityReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(eventListener,mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),SensorManager.SENSOR_DELAY_GAME);
    }

    public class SensorBinder extends Binder {

        public SensorService getService() {
            // Return this instance of LocalService so clients can call public methods
            return SensorService.this;
        }
    }

    SensorEventListener eventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                System.arraycopy(event.values, 0, mGravityReading,
                        0, mGravityReading.length);

                if (event.values[1] < 5.5 || event.values[1] > 8.5) {
                    Intent intent = new Intent(ALERT_ACTION);
                    intent.putExtra("alert",true);
                    getApplicationContext().sendBroadcast(intent);

                } else {
                    Intent intent = new Intent(ALERT_ACTION);
                    intent.putExtra("alert",false);
                    getApplicationContext().sendBroadcast(intent);

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            mSensorManager.getRotationMatrix(mRotationMatrix, null,
                    mGravityReading, mMagnetometerReading);
        }
    };



    @Override
    public boolean onUnbind(Intent intent) {
        mSensorManager.unregisterListener(eventListener);
        return super.onUnbind(intent);
    }



}
