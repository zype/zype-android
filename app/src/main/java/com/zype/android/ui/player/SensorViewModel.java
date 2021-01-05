package com.zype.android.ui.player;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.zype.android.utils.Logger;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

/**
 * Created by Evgeny Cherkasov on 20.07.2018
 */
public class SensorViewModel extends AndroidViewModel implements SensorEventListener {
    private MutableLiveData<Integer> orientationLiveData;

    private SensorManager sensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    public SensorViewModel(Application application) {
        super(application);
        sensorManager = (SensorManager) getApplication().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    protected void onCleared() {
        Logger.d("onCleared()");
        stopListeningOrientation();
        super.onCleared();
    }

    public MutableLiveData<Integer> getOrientation() {
        if (orientationLiveData == null) {
            orientationLiveData = new MutableLiveData<>();
        }

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);

        return orientationLiveData;
    }

    public void stopListeningOrientation() {
        sensorManager.unregisterListener(this);
    }

    private void updateOrientation() {
        SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);
        SensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        Logger.d("updateOrientation(): x=" + mOrientationAngles[0] + ", y=" + mOrientationAngles[1] + ", z=" + mOrientationAngles[2]);

        float y = mOrientationAngles[1];
        if (y > 0 && y < 0.78f) {
            orientationLiveData.setValue(Configuration.ORIENTATION_LANDSCAPE);
        }
        else if (y > -0.78f && y < 0) {
            orientationLiveData.setValue(Configuration.ORIENTATION_PORTRAIT);
        }
    }

    //
    // 'SensorEventListener' implementation
    //
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,0, mMagnetometerReading.length);
        }
        updateOrientation();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
