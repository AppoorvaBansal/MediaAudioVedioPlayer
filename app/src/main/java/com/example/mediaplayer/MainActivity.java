package com.example.mediaplayer;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener{
ImageButton btnfrd,btnbck,btnplay,btnpause;
TextView txtsong,txtstarttime,txtendtime;
SeekBar seeksongprg;

MediaPlayer mp;
Handler h=new Handler();

static int otime=0,stime=0,etime=0,ftime=10000,btime=10000;

    VideoView vw;
    ArrayList<Integer> videolist = new ArrayList<>();
    ArrayList<Integer> songlist = new ArrayList<>();
    int currvideo = 0;
    int currsong=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vw = (VideoView)findViewById(R.id.videoView1);
        vw.setMediaController(new MediaController(this));
        vw.setOnCompletionListener(this);

        // video name should be in lower case alphabet.
        videolist.add(R.raw.hp);
        videolist.add(R.raw.faded);
        videolist.add(R.raw.hp);
        setVideo(videolist.get(0));

        songlist.add(R.raw.song);
        songlist.add(R.raw.song2);
        songlist.add(R.raw.song3);
        //setSong(songlist.get(0));

        btnbck=findViewById(R.id.bckbtn);
        btnfrd=findViewById(R.id.frdbtn);
        btnpause=findViewById(R.id.pausebtn);
        btnplay=findViewById(R.id.playbtn);

        txtsong=findViewById(R.id.txtsongname);
        txtsong.setText(txtsong.getText()+ "Despacito....");

        txtendtime=findViewById(R.id.endTime);
        txtstarttime=findViewById(R.id.startTime);

        seeksongprg=findViewById(R.id.seeksongpro);

      //  mp=MediaPlayer.create(this,R.raw.song);

        btnpause.setEnabled(false);
        seeksongprg.setClickable(false);

        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mp.start();
                if(stime>0)
                {
                    mp.start();
                }
                else {
                    setSong(songlist.get(currsong));
                }
                etime=mp.getDuration();
                if(otime==0)
                {
                    seeksongprg.setMax(etime);
                    otime=1;
                }

                txtendtime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(etime), TimeUnit.MILLISECONDS.toSeconds(etime)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(etime))));


                updateSong();

                btnpause.setEnabled(true);
                btnplay.setEnabled(false);
            }
        });

        btnpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();

                btnpause.setEnabled(false);
                btnplay.setEnabled(true);
            }
        });

    seeksongprg.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            if(fromUser)
            {
                mp.seekTo(progress); // song is moving
              seeksongprg.setProgress(progress); // seekbar
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    });

    btnfrd.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            otime=0;
            if(stime<etime)
            {
                mp.stop();
                ++currsong;
                if (currsong == songlist.size())
                    currsong = 0;
                setSong(songlist.get(currsong));
                etime=mp.getDuration();
                if(otime==0)
                {
                    seeksongprg.setMax(etime);
                    otime=1;
                }

                txtendtime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(etime), TimeUnit.MILLISECONDS.toSeconds(etime)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(etime))));

                updateSong();
                //stime=stime+ftime;
                //mp.seekTo(stime);
            }
            else
                mp.seekTo(etime);
        }
    });

btnbck.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if((stime-btime)>0)
        {
            stime=stime-btime;
            mp.seekTo(stime);
        }
        else{
            stime=1;
            mp.seekTo(stime);
        }


    }
});


    }

    private void setSong(Integer id) {
        String uriPath
                = "android.resource://"
                + getPackageName() + "/" + id;
        Uri uri = Uri.parse(uriPath);
        mp=MediaPlayer.create(this,uri);
        mp.start();

    }

    private void setVideo(Integer id) {
        String uriPath
                = "android.resource://"
                + getPackageName() + "/" + id;
        Uri uri = Uri.parse(uriPath);
        vw.setVideoURI(uri);
        vw.start();

    }

    private void updateSong() {

        stime=mp.getCurrentPosition();
        txtstarttime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(stime), TimeUnit.MILLISECONDS.toSeconds(stime)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(stime))));
        seeksongprg.setProgress(stime);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                updateSong();
            }
        },100);



    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        AlertDialog.Builder obj = new AlertDialog.Builder(this);
        obj.setTitle("Playback Finished!");
        obj.setIcon(R.mipmap.ic_launcher);
       MyListener m = new MyListener();
       obj.setPositiveButton("Replay",m);
       obj.setNegativeButton("Next", m);
        obj.setMessage("Want to replay or play next video?");
        obj.show();
    }

 class MyListener implements DialogInterface.OnClickListener  {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == -1) {
                vw.seekTo(0);
                vw.start();
            }
            else {
                ++currvideo;
                if (currvideo == videolist.size())
                    currvideo = 0;
                setVideo(videolist.get(currvideo));
            }
        }
    }
}
