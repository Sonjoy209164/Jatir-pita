package com.example.myaudiobook.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.myaudiobook.HomepageNew;
import com.example.myaudiobook.R;
import com.example.myaudiobook.adapter.BookAdapter;
import com.example.myaudiobook.classes.AudioBook;
import com.example.myaudiobook.classes.PlayList;
import com.example.myaudiobook.classes.Utility;
import com.example.myaudiobook.databinding.FragmentPlaylistLayoutBinding;
import com.example.myaudiobook.listener.AudioBookListener;
import com.example.myaudiobook.listener.HomePlayerListener;
import com.example.myaudiobook.listener.PlaylistListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlaylistFragment extends Fragment implements PlaylistListener, HomePlayerListener {

    private BookAdapter bookAdapter = null;
    private FragmentPlaylistLayoutBinding binding = null;


    private Handler handler;
    private Runnable runnable;
    private boolean shouldRunSeekbar = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaylistLayoutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View mView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);

        setupRecyclerview();
        setPlayerArrowListener();
        setSeekBarListener();
    }


    private void setPlayerArrowListener(){

        binding.ivBackPlayer.setOnClickListener(view->{
            try{
                Activity activity = getActivity();
                if(activity instanceof HomepageNew){
                    ((HomepageNew)activity).takeToMainPage();
                }
            }catch (Exception ignored){}
        });

        binding.ivMinimizePlayer.setOnClickListener(view-> switchView(false));

        binding.ivPlayPrevious.setOnClickListener(view->{
            int position = bookAdapter.getPlayingPosition();
            if(position <= 0){
                Utility.showSafeToast(requireActivity(),"This is the first");
                return;
            }

            AudioBook book = bookAdapter.getBookAt(position-1);
            if(book == null || !book.isUrlValid()){
                Utility.showSafeToast(requireActivity(),"Coming soon");
                return;
            }

            bookAdapter.setPlayingPosition(position-1);
            processToPlay(book);

        });

        binding.ivPlayPause.setOnClickListener(view->{
            try{
                Activity activity = getActivity();
                if(activity instanceof HomepageNew){

                    HomepageNew homepageNew = ((HomepageNew)activity);
                    if(homepageNew.isPlaying()){
                        homepageNew.pause();
                        bookAdapter.setPlaying(false);
                        binding.ivPlayPause.setImageResource(R.drawable.baseline_play_circle_24);
                    }
                    else {
                        int progress = homepageNew.getProgress();
                        if(progress == 100) {
                            homepageNew.seekToTimePosition(0);
                            binding.seekBarPlayer.setProgress(0);
                            binding.tvPlayedPlayer.setText(homepageNew.getFormattedPlayedStatus());
                            homepageNew.play();
                            bookAdapter.setPlaying(true);
                            shouldRunSeekbar = true;
                            runSeekBar();
                            binding.ivPlayPause.setImageResource(R.drawable.baseline_pause_circle_24);
                        }
                        else{
                            homepageNew.play();
                            binding.ivPlayPause.setImageResource(R.drawable.baseline_pause_circle_24);
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        });

        binding.ivPlayNext.setOnClickListener(view->{
            int position = bookAdapter.getPlayingPosition();
            if(position >= bookAdapter.getItemCount()-1){
                Utility.showSafeToast(requireActivity(),"This is the last");
                return;
            }

            AudioBook book = bookAdapter.getBookAt(position+1);
            if(book == null || !book.isUrlValid()){
                Utility.showSafeToast(requireActivity(),"Coming soon");
                return;
            }

            bookAdapter.setPlayingPosition(position+1);
            processToPlay(book);
        });
    }


    private void setSeekBarListener(){

        binding.seekBarPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                try{
                    Activity activity = getActivity();
                    if(activity instanceof HomepageNew) {
                        ((HomepageNew)activity).seekToPosition(seekBar.getProgress());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }


    private void runSeekBar(){
        handler = new Handler();
        runnable = () -> {
            try{
                Activity activity = getActivity();
                if(activity instanceof HomepageNew){
                    HomepageNew homepageNew = ((HomepageNew)activity);
                    int progress = homepageNew.getProgress();
                    binding.seekBarPlayer.setProgress(progress);

                    binding.tvPlayedPlayer.setText(homepageNew.getFormattedPlayedStatus());
                    binding.tvTotalPlayer.setText(homepageNew.getFormattedTotalStatus());

                    if(shouldRunSeekbar) {
                        handler.postDelayed(runnable, 900);
                    }
                    if(progress == 100){
                        shouldRunSeekbar = false;
                        bookAdapter.setPlaying(false);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        };
        handler.postDelayed(runnable, 0);
    }


    private void setupRecyclerview(){
        bookAdapter = new BookAdapter(requireActivity(), new AudioBookListener() {
            @Override
            public void onPlayRequest(AudioBook audioBook) {
                processToPlay(audioBook);
            }

            @Override
            public void onMessageFound(String message) {
                Utility.showSafeToast(requireActivity(), message);
            }
        });
        binding.rvItemsInList.setAdapter(bookAdapter);
    }

    private void processToPlay(AudioBook book){
        try{
            Activity activity = getActivity();
            if(activity instanceof HomepageNew){
                HomepageNew homepageNew = ((HomepageNew) activity);
                homepageNew.playThisBook(book);
                binding.ivPlayPause.setImageResource(R.drawable.baseline_pause_circle_24);
                switchView(true);
                binding.tvBookNamePlayer.setText(book.getName());
                shouldRunSeekbar = true;
                runSeekBar();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fetchPlayList(String playListKey){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("audioBook");
        Query query = ref.orderByChild("playlistKey").equalTo(playListKey);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hidePb();

                List<AudioBook> books = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    AudioBook book = ds.getValue(AudioBook.class);
                    if(book == null) continue;

                    books.add(book);
                }
                showInRv(books);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.showSafeToast(requireActivity(),"Failed to load playlist");
            }
        });
    }

    private void switchView(boolean showPlayer){
        if(showPlayer){
            binding.clPlayer.setVisibility(View.VISIBLE);
            binding.clPlaylistViewer.setVisibility(View.INVISIBLE);
        }
        else{
            binding.clPlayer.setVisibility(View.INVISIBLE);
            binding.clPlaylistViewer.setVisibility(View.VISIBLE);
        }
    }

    private void hidePb(){
        if(binding == null) return;
        binding.pbPlaylist.setVisibility(View.GONE);
    }

    private void showInRv(@NonNull List<AudioBook> list){
        if(list.isEmpty()){
            binding.tvNoBookFound.setText(getString(R.string.no_audiobook_found));
            binding.tvNoBookFound.setVisibility(View.VISIBLE);
        }
        else{
            binding.tvNoBookFound.setVisibility(View.GONE);
        }
        bookAdapter.submitList(list);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if(activity instanceof HomepageNew){
            try{
                ((HomepageNew) activity).setPlayListListener(this);
                ((HomepageNew) activity).setHomePlayerListener(this);
            }catch (Exception ignored){}
        }
    }

    private void showInPage(PlayList playList){
        binding.tvPlayListName.setText(playList.getName());
        binding.tvPlayListNamePlayer.setText(playList.getName());

        binding.tvPlayListDetails.setText(playList.getDetails());
        Glide.with(this)
                .load(playList.getImageUrl())
                .placeholder(R.drawable.mujib_default_icon)
                .into(binding.ivPlaylistThumbnail)
                .onLoadFailed(ResourcesCompat.getDrawable(getResources(),R.drawable.mujib_default_icon,null));
    }

    @Override
    public void onItemClick(PlayList playList) {

        switchView(false);

        binding.clTop.setVisibility(View.VISIBLE);
        showInPage(playList);
        bookAdapter.submitList(new ArrayList<>());
        binding.pbPlaylist.setVisibility(View.VISIBLE);
        binding.tvNoBookFound.setVisibility(View.GONE);

        fetchPlayList(playList.getKey());
    }

    @Override
    public void onPaused() {
        binding.ivPlayPause.setImageResource(R.drawable.baseline_play_circle_24);
    }

    @Override
    public void onPlayed() {
        binding.ivPlayPause.setImageResource(R.drawable.baseline_pause_circle_24);
    }

    @Override
    public void switchPlayerView() {
        switchView(true);
    }

}
