package com.example.service_broadcast_receiver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.service_broadcast_receiver.Constants.Notifications;
import com.example.service_broadcast_receiver.Service.MusicPlayerApi;
import com.example.service_broadcast_receiver.Service.MusicPlayerService;

public class MainActivity extends AppCompatActivity {

    private int numberOfBroadcasts = 0;
    private MusicPlayerService.MusicPlayerApiImpl mService;
    private MusicPlayerConnection serviceConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();
    }


    //Button Listener
    public void onClickMusicStart(View Btn){
        if(!(isMyServiceRunning(MusicPlayerService.class))) {
            Intent intent = new Intent(this, MusicPlayerService.class);
            startService(intent);
        }else{
            Toast.makeText(this, "service is already running", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickMusicStop(View Btn){
        Intent intent = new Intent(this, MusicPlayerService.class);
        stopService(intent);
    }

    public void onClickPlayNext(View Btn){
        if(isServiceBoundToThisActivity()){
            this.mService.playNextItem();
        }
    }

    public void onClickCheckBoxMusicService(View checkbox){
        CheckBox checkBox = (CheckBox)findViewById(R.id.serviceCheckBox);
        if(isMyServiceRunning(MusicPlayerService.class)) {
            if (checkBox.isChecked()) {
                bindServiceToThisActivity();
            } else {
                unbindServiceFromThisActivity();
            }
        }else{
            checkBox.setChecked(false);
            Toast.makeText(this, "Service ist not started", Toast.LENGTH_LONG).show();
        }
    }

    public void onClickHistory(View Btn){
        if(isServiceBoundToThisActivity()) {
            AlertDialog dialog;
            CharSequence[] charItem = new CharSequence[mService.getHistory().size()];
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("got it...", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setTitle("Music Player History");
            int i = 0;
            for (String item : mService.getHistory()) {
                charItem[i] = item;
                i++;
            }
            builder.setItems(charItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            dialog = builder.create();
            dialog.show();
        }
    }

    public void onClickSendBroadcast(View Btn){
        Intent broadcast = new Intent("ACTION_MY_BROADCAST");
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
    }

    public void onCLickCheckBoxBroadCast(View checkbox){
        CheckBox checkBox = (CheckBox)findViewById(R.id.broadcastReg);
        if(checkBox.isChecked()){
            IntentFilter filter = new IntentFilter("ACTION_MY_BROADCAST");
            LocalBroadcastManager.getInstance(this).registerReceiver(broadCast,filter);
            String text = "Es sind aktuell: " + numberOfBroadcasts + " Broadcastnachrichten eingegangen!";
            TextView textView = (TextView)findViewById(R.id.broadcastText);
            textView.setText(text);
        }else{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadCast);
            this.numberOfBroadcasts = 0;
            TextView textView = (TextView)findViewById(R.id.broadcastText);
            textView.setText("Broadcastempfang deaktiviert!");
        }
    }



    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(Notifications.MUSICPLAYER_CHANNEL_ID, Notifications.MUSICPLAYER_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void bindServiceToThisActivity(){
        if(!isServiceBoundToThisActivity()){
            Intent intent = new Intent(this,MusicPlayerService.class);
            this.serviceConnection = new MusicPlayerConnection();
            bindService(intent,serviceConnection,BIND_AUTO_CREATE);
        }
    }

    private void unbindServiceFromThisActivity(){
        if(isServiceBoundToThisActivity()){
            unbindService(serviceConnection);
            serviceConnection = null;
        }
    }

    private boolean isServiceBoundToThisActivity(){
        if(serviceConnection == null){
            return false;
        }else {
            return true;
        }
    }


    private BroadcastReceiver broadCast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            numberOfBroadcasts++;
            TextView view = (TextView)findViewById(R.id.broadcastText);
            String text = "Es sind aktuell: " + numberOfBroadcasts + " Broadcastnachrichten eingegangen!";
            view.setText(text);
        }
    };

    public class MusicPlayerConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = (MusicPlayerService.MusicPlayerApiImpl) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }
    }


}
