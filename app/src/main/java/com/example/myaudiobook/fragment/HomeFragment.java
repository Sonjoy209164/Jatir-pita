package com.example.myaudiobook.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myaudiobook.HomepageNew;
import com.example.myaudiobook.R;
import com.example.myaudiobook.adapter.PlayListAdapter;
import com.example.myaudiobook.classes.PlayList;
import com.example.myaudiobook.classes.Utility;
import com.example.myaudiobook.databinding.FragmentHomeLayoutBinding;
import com.example.myaudiobook.listener.MiniPlayerListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements MiniPlayerListener {

    private PlayListAdapter playListAdapter = null;
    private FragmentHomeLayoutBinding binding = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeLayoutBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View mView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(mView, savedInstanceState);


        setupRecyclerview();
        readPlayList();
        setMiniPlayerListener();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setMiniPlayerListener(){


        binding.seekBar.setOnTouchListener((view, motionEvent) -> true);

        binding.clMiniPlayer.setOnClickListener(view->{
            HomepageNew obj = getHomePage();
            if(obj == null) return;

            obj.switchToPlayerPage();
        });

        binding.ivBackward.setOnClickListener(view->{
            HomepageNew obj = getHomePage();
            if(obj == null) return;
            obj.addAndSeek(-10);
        });

        binding.ivPlayPause.setOnClickListener(view->{
            HomepageNew obj = getHomePage();
            if(obj == null) return;

            if(obj.isPlaying()){
                binding.ivPlayPause.setImageResource(R.drawable.baseline_play_circle_24);
                obj.pause();
            }
            else{
                binding.ivPlayPause.setImageResource(R.drawable.baseline_pause_circle_24);
                obj.play();
            }

        });


        binding.ivForward.setOnClickListener(view->{
            HomepageNew obj = getHomePage();
            if(obj == null) return;
            obj.addAndSeek(10);
        });
    }


    private HomepageNew getHomePage(){
        try{
            Activity activity = getActivity();
            if(activity instanceof HomepageNew){
                return ((HomepageNew) activity);
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if(activity instanceof HomepageNew){
            try{
                ((HomepageNew) activity).setMiniPlayerListener(this);
            }catch (Exception ignored){}
        }
    }

    private void setupRecyclerview(){
        playListAdapter = new PlayListAdapter(requireActivity(), playList -> {
            try{
                Activity activity = getActivity();
                if(activity instanceof  HomepageNew){
                    ((HomepageNew)activity).onItemClicked(playList);
                }
            }catch (Exception ignored){}
//            Intent intent = new Intent(requireActivity(), PlayerActivity.class);
//            intent.putExtra("playlist",playList);
//            startActivity(intent);
        });
        binding.rvList.setAdapter(playListAdapter);
    }

    private void readPlayList(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("playlist");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                hidePb();

                List<PlayList> list = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()){
                    PlayList playList = ds.getValue(PlayList.class);
                    if(playList == null) continue;

                    list.add(playList);
                }
                showInRv(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.showSafeToast(requireActivity(), "Failed to load playlist");
            }
        });
    }

    private void hidePb(){
        if(binding == null) return;
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showInRv(@NonNull List<PlayList> list){
        if(list.isEmpty()){
            Utility.showSafeToast(requireActivity(), "No playlist found");
        }
        playListAdapter.submitList(list);
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
    public void setBookName(String name) {
        binding.tvBookName.setText(name);
    }

    @Override
    public void updateTimeAndProgress(String formattedTime, int progress) {
        binding.tvPlayStatus.setText(formattedTime);
        binding.seekBar.setProgress(progress);
    }

    @Override
    public void showMiniPlayer() {
        binding.clMiniPlayer.setVisibility(View.VISIBLE);
        binding.ivPlayPause.setImageResource(R.drawable.baseline_pause_circle_24);
    }

    @Override
    public void hideMiniPlayer() {
        binding.clMiniPlayer.setVisibility(View.GONE);
    }

}
