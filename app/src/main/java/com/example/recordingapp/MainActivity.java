package com.example.recordingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    private Button startButton, stopButton;
    private MediaRecorder recorder;
    private File audiofile = null;
    private static final String TAG = "MediaRecording";

    public static final int RECORD_AUDIO = 0;
    private MediaRecorder myAudioRecorder;
    private String output = null;
    private Button start, stop, play;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String[] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.button1);
        stopButton = findViewById(R.id.button2);

        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
        start = (Button) findViewById(R.id.buttonStart);
        stop = (Button) findViewById(R.id.buttonStop);
        play = (Button) findViewById(R.id.buttonPlay);
        start.setOnClickListener(this);
        stop.setOnClickListener(this);
        play.setOnClickListener(this);
        stop.setEnabled(false);
        play.setEnabled(false);

        output = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording.3gp";

        Log.e( "onCreate: ",output );
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(output);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonStart:
                start(v);
                break;
            case R.id.buttonStop:
                stop(v);
                break;
            case R.id.buttonPlay:
                try {
                    play(v);
                } catch (IOException e) {
                    Log.i("IOException", "Error in play");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) MainActivity.super.finish();
        if (!permissionToWriteAccepted) MainActivity.super.finish();
    }

    public void start(View view) {
        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        start.setEnabled(false);
        stop.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_SHORT).show();
    }

    public void stop(View view) {
        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder = null;
        stop.setEnabled(false);
        play.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_SHORT).show();
    }

    public void play(View view) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException {
        MediaPlayer m = new MediaPlayer();
        m.setDataSource(output);
        m.prepare();
        m.start();
        start.setEnabled(true);
        stop.setEnabled(true);
        Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_SHORT).show();
    }
}