package com.example.myaudiobook;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.myaudiobook.adapter.PagerAdapter;
import com.example.myaudiobook.classes.AudioBook;
import com.example.myaudiobook.classes.DataSaver;
import com.example.myaudiobook.classes.PlayList;
import com.example.myaudiobook.classes.Utility;
import com.example.myaudiobook.databinding.ActivityHomepageNewBinding;
import com.example.myaudiobook.fragment.HomeFragment;
import com.example.myaudiobook.fragment.InfoFragment;
import com.example.myaudiobook.fragment.PlaylistFragment;
import com.example.myaudiobook.listener.HomePlayerListener;
import com.example.myaudiobook.listener.MiniPlayerListener;
import com.example.myaudiobook.listener.PlaylistListener;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;

import java.util.ArrayList;
import java.util.List;

public class HomepageNew extends AppCompatActivity {

    private ActivityHomepageNewBinding binding = null;
    private int SELECTED_PAGE = 0;
    private boolean doubleBackToExitPressedOnce = false;

    private PlaylistListener playlistListener = null;
    private HomePlayerListener homePlayerListener = null;
    private MiniPlayerListener miniPlayerListener = null;

    private ExoPlayer player = null;
    private AudioBook currentBook = null;

    private Handler handler;
    private Runnable runnable;
    private boolean shouldRunSeekbar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomepageNewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        player = new ExoPlayer.Builder(this).build();
        startFsAdapter();
        setBnvListener();

    }

    public void changeColor() {
        try {
            getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            ActionBar bar = getSupportActionBar();
            if(bar != null)
                bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));
        }catch (Exception ignored){}

    }

    private void startFsAdapter(){
        List<Fragment> list = new ArrayList<>();
        list.add(new HomeFragment());
        list.add(new PlaylistFragment());
        list.add(new InfoFragment());

        PagerAdapter fSAdapter = new PagerAdapter(this,list);
        binding.viewPager.setOffscreenPageLimit(3);
        binding.viewPager.setAdapter(fSAdapter);
        binding.viewPager.setUserInputEnabled(false);//disabling viewpager horizontal swipe
    }

    public void playThisBook(AudioBook book){
        //player.release();
        this.currentBook = book;

        MediaItem mediaItem = new MediaItem.Builder()
                .setUri(book.getUrl())
                .build();

        player.setMediaItem(mediaItem);
        player.prepare();
        player.setPlayWhenReady(true);
        long savedTime = DataSaver.getInstance(this).getLastTime(currentBook.getKey());
        player.seekTo(savedTime);

        if(miniPlayerListener != null){
            miniPlayerListener.showMiniPlayer();
            miniPlayerListener.setBookName(currentBook.getName());
        }

        shouldRunSeekbar = true;
        runSeekBar();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(currentBook != null){
            DataSaver.getInstance(this).saveAudioTime(currentBook.getKey(), player.getCurrentPosition());
        }
    }

    public void play(){
        if(player == null) return;

        player.play();

        if(miniPlayerListener != null) miniPlayerListener.onPlayed();
        if(homePlayerListener != null) homePlayerListener.onPlayed();

        if(currentBook != null){
            DataSaver.getInstance(HomepageNew.this)
                    .saveAudioTime(currentBook.getKey(), player.getCurrentPosition());
        }
    }


    public void pause(){
        if(player == null) return;

        player.pause();

        if(miniPlayerListener != null) miniPlayerListener.onPaused();
        if(homePlayerListener != null) homePlayerListener.onPaused();
        if(currentBook != null){
            DataSaver.getInstance(HomepageNew.this)
                    .saveAudioTime(currentBook.getKey(), player.getCurrentPosition());
        }
    }

    public boolean isPlaying(){
        if(player == null) return false;

        return player.isPlaying();
    }

    public void addAndSeek(long offsetSecond){
        if(player == null) return;

        offsetSecond *= 1000;

        long cur = player.getCurrentPosition();
        cur += offsetSecond;

        if(cur < 0) cur = 0;
        else if(cur > player.getDuration()) cur = player.getDuration();

        player.seekTo(cur);
    }

    public void seekToTimePosition(long time){
        if(player == null) return;
        player.seekTo(time);
    }

    public void seekToPosition(int seekBarProgress){
        if(player == null) return;

        long cur = (int) (player.getDuration() / 100f * seekBarProgress);
        player.seekTo(cur);
    }

    public String getFormattedPlayedStatus(){
        return getFormattedTime(player.getCurrentPosition());
    }

    public String getFormattedTotalStatus(){
        return getFormattedTime(player.getDuration());
    }

    private String getFormattedTime(long timeInMs){
        if(timeInMs == C.TIME_UNSET) timeInMs = 0;

        long s = timeInMs/1000;

        long hh = s/3600; s %= 3600;

        long mm = s/60; s %= 60;

        StringBuilder builder = new StringBuilder();
        if(hh != 0) builder.append(hh).append(":");

        builder.append(mm<10 ? "0"+mm : mm).append(":").append(s<10 ? "0"+s : s);
        return builder.toString();
    }

    public int getProgress(){
        if(player == null) return 0;

        long cur = player.getCurrentPosition();
        long max = player.getDuration();
        return  (int)(100*cur / max);
    }

    public void onItemClicked(PlayList playList){

        if(playlistListener == null){
            Utility.showSafeToast(this,"Something went wrong");
            return;
        }

        if(player != null && player.isPlaying()){
            Utility.showSafeToast(this,"Close current playlist");
            return;
        }

        playlistListener.onItemClick(playList);
        binding.viewPager.setCurrentItem(1);
        binding.bnv.setSelectedItemId(R.id.navList);
        SELECTED_PAGE = 1;

    }

    public void setPlayListListener(PlaylistListener listener){
        this.playlistListener = listener;
    }
    public void setHomePlayerListener(HomePlayerListener listener){
        this.homePlayerListener = listener;
    }
    public void setMiniPlayerListener(MiniPlayerListener listener){this.miniPlayerListener = listener;}

    public void takeToMainPage(){
        binding.viewPager.setCurrentItem(0);
        binding.bnv.setSelectedItemId(R.id.navHome);
        SELECTED_PAGE = 0;
    }

    //

    private String getFormattedStatus(){
        String played = getFormattedTime(player.getCurrentPosition());
        String total = getFormattedTime(player.getDuration());

        return played+" / "+total;
    }


    private void runSeekBar(){
        handler = new Handler();
        runnable = () -> {
            try{
                int progress = getProgress();
                String time = getFormattedStatus();

                if(miniPlayerListener != null) miniPlayerListener.updateTimeAndProgress(time,progress);

                if(shouldRunSeekbar) {
                        handler.postDelayed(runnable, 900);
                }
                if(progress == 100){
                    shouldRunSeekbar = false;
                    if(miniPlayerListener != null){
                        Utility.showSafeToast(this,"AudioBook played completely");
                        if(currentBook != null)
                            DataSaver.getInstance(this).saveAudioTime(currentBook.getKey(), player.getCurrentPosition());
                        miniPlayerListener.hideMiniPlayer();
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }

        };
        handler.postDelayed(runnable, 0);
    }


    public void switchToPlayerPage(){
        binding.viewPager.setCurrentItem(1);
        SELECTED_PAGE = 1;
        binding.bnv.setSelectedItemId(R.id.navList);

        if(homePlayerListener != null) homePlayerListener.switchPlayerView();
    }

    private void setBnvListener(){

        binding.bnv.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.navHome){
                binding.viewPager.setCurrentItem(0);
                SELECTED_PAGE = 0;
                return true;
            }
            if(id == R.id.navList){
                binding.viewPager.setCurrentItem(1);
                SELECTED_PAGE = 1;
                return true;
            }
            if(id == R.id.navInfo){
                binding.viewPager.setCurrentItem(2);
                SELECTED_PAGE = 2;
                return true;
            }
            return false;
        });

    }


    @Override
    public void onBackPressed(){
        if (doubleBackToExitPressedOnce){
            if(player != null) player.release();
            finishAffinity();
        }
        else {
            if(SELECTED_PAGE != 0){
                binding.viewPager.setCurrentItem(0);
                binding.bnv.setSelectedItemId(R.id.navHome);
                SELECTED_PAGE = 0;
            }
            else {
                doubleBackToExitPressedOnce = true;
                Utility.showSafeToast(this,"Press again to exit");
                new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
            }
        }
    }

}
