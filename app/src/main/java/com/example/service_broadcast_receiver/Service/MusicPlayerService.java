package com.example.service_broadcast_receiver.Service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import com.example.service_broadcast_receiver.Constants.Notifications;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import androidx.annotation.Nullable;

public class MusicPlayerService extends Service {

    private MusicPlayerApiImpl musicplayerapi = new MusicPlayerApiImpl();
    private List<String> history = null;
    private List<String> playList = null;
    private String actualSong = null;


    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        playList = new ArrayList<>();
        history = new ArrayList<>();
        fillplayList();
        playNext();
        startForeground(1,buildNotification());
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy(){
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicplayerapi;
    }

    public String playNext(){
        Random rn = new Random();
        String nextSong = playList.get((rn.nextInt(10)+0));
        history.add(nextSong);
        actualSong = nextSong;
        startForeground(1,buildNotification());
        Toast.makeText(this, "Now play " + nextSong, Toast.LENGTH_SHORT).show();
        return nextSong;
    }

    public List<String> queryHistory(){
        return history;
    }

    public void fillplayList(){
        playList.add("Mero - Wolke 10");
        playList.add("Pedro Capo - Calma");
        playList.add("Ava Max - Sweet But Psycho");
        playList.add("Post Malone - Wow");
        playList.add("Halsey - Without Me");
        playList.add("Capital Bra - Capital Bra je m'appelle");
        playList.add("P!nk - Walk Me Home");
        playList.add("Patent Ochsner - Für immer uf di");
        playList.add("Lukas Graham - Love Someone");
        playList.add("Azet & Zuna - Kamehameha");
    }

    private Notification buildNotification(){
        return new Notification.Builder(getApplicationContext(), Notifications.MUSICPLAYER_CHANNEL_ID)
                .setContentTitle("Marty Music Player")
                .setTicker("Marty Music Player")
                .setContentText("Aktueller Titel: " + actualSong)
                .setOngoing(true)
                .setSmallIcon(android.R.drawable.ic_media_play)
                .setWhen(System.currentTimeMillis())
                .build();
    }

    public class MusicPlayerApiImpl extends Binder implements MusicPlayerApi {

        @Override
        public String playNextItem() {
            return MusicPlayerService.this.playNext();
        }

        @Override
        public List<String> getHistory() {
            return MusicPlayerService.this.queryHistory();
        }
    }


}
